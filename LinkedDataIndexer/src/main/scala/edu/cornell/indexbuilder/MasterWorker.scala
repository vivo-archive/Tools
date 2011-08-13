package edu.cornell.indexbuilder

import akka.actor._
import akka.actor.{Actor, ActorRef}
import akka.actor.Actor._
import com.hp.hpl.jena.ontology.OntDocumentManager
import com.hp.hpl.jena.util.FileManager
import com.hp.hpl.jena.util.LocationMapper
import com.weiglewilczek.slf4s.Logging
import edu.cornell.indexbuilder.discovery._
import edu.cornell.indexbuilder.http._
import edu.cornell.indexbuilder.indexing.CouldNotIndexDoc
import edu.cornell.indexbuilder.indexing._
import org.apache.solr.client.solrj.SolrServer
import MasterWorker._

/**
 * Handle the main coordination of messages to build the index.
 */

class MasterWorker(
  siteUrl: String, 
  siteName: String,
  uriDiscoveryWorker: ActorRef,  
  solrServer: SolrServer, 
  selectorGen: SelectorGenerator,
  skipUrl: String => Boolean,
  http: Http ) 
extends Actor with Logging {

  setupJenaStatic

  //Workers for use by the master:
  val rdfWorker = Actor.actorOf( new RdfLinkedDataWorker(http, skipUrl) )
  val solrDocWorker = Actor.actorOf( new SolrDocWorker( selectorGen, siteName ) )
  val solrIndexWorker = Actor.actorOf( new SolrIndexWorker( solrServer ) )

  /**
   * List of uris to index messages.
   */
  var urisToIndex = List[String]()

  /**
   * List of uris completed.
   */
  var urisCompleted = List[String]()

  /**
   * Map of uris that had problems.
   */
  var urisWithErrors = Set[String]()

  var errors = Set[Object]()

  var startTime = 0L

  override def preStart () = {
    startTime = System.currentTimeMillis
  }

  def receive = {

    case DiscoverUrisForSite( siteUrl ) =>{
      logger.debug("Starting discovery for site " + siteUrl)
      startAllMyWorkers()
      uriDiscoveryWorker ! DiscoverUrisForSite( siteUrl )
    }

    case IndexUris(baseSiteUrl, uris ) => {
      logger.debug( "Received URIs to index: " + uris.length)
      addToUrisToIndex( uris )
    }

    case DiscoveryComplete(baseSiteUrl) => {
      val delta = (System.currentTimeMillis - startTime )/ 1000
      val urisToIndex = getUncompletedUris()
      logger.info( "Discovery complete after "+delta+" sec; "+
                    urisToIndex.size+" uris to index. Starting linked" +
                   " data reqeusts." )
      urisToIndex.foreach( uri => rdfWorker ! GetRdf( baseSiteUrl, uri ) )
    }

    // make solr documents for the RDF model    
     case GotRdf( siteUrl, uri, model ) =>{
       logger.debug("got rdf for uri " + uri)
       solrDocWorker ! RdfToDoc( siteUrl, uri, model ) 
     }

    // index the SolrDoc
    case GotDoc( siteUrl, uri, doc) =>{      
       logger.debug("got solr doc for uri " + uri)
      solrIndexWorker ! IndexDoc (siteUrl,uri,doc)
    }

    // solr document was indexed so remove it from the list of URIs to index
    case IndexedDoc( siteUrl, uri ) =>{
      logger.debug(uri + " has been indexed")

      //record that uri was indexed
      addToCompletedUris( uri )

      checkIfCompleted
    }

    case CouldNotGetData(siteUrl,uri,msg) => {
      logger.error("could not get data for "+uri+" because msg")
      synchronized{
        urisWithErrors +=  uri
        errors +=   CouldNotGetData(siteUrl,uri,msg) 
      }
      checkIfCompleted
    }

    case CouldNotIndexDoc(siteUrl,uri) => {
      synchronized{
        urisWithErrors += uri
        errors += CouldNotIndexDoc( siteUrl ,uri)
      }
      checkIfCompleted
    }

    case _ => println("SiteIndexWoker got some mystery message")
  }


  def checkIfCompleted() = {
   if( allUrisIndexed ){
     logger.info("All URIs have been indexed. Commiting")
     solrIndexWorker ! Commit
     endMessage()
     Actor.registry.shutdownAll()
   }else{
     incrementalMessage()
   }
  }

  def getUncompletedUris():Seq[String] = {
    synchronized{
      urisToIndex.diff( urisCompleted ++ urisWithErrors )
    }
  }

  def allUrisIndexed():Boolean = {
    synchronized{
      urisToIndex.length <=  numberOfProcessedUris
    }
  }

  def numberOfProcessedUris():Int = {
    synchronized{
      (urisCompleted.length + urisWithErrors.size) 
    }
  }

  def addToUrisToIndex(uris:List[String]) = {
    synchronized{
      urisToIndex = urisToIndex ::: uris 
    }
  }

  def addToCompletedUris(uri:String) = {
    synchronized{
      urisCompleted = uri :: urisCompleted
    }
  }

  def incrementalMessage(){
    synchronized{
      val indexedCount = numberOfProcessedUris
      if( indexedCount % 10 == 0 ){
        
        val etime = System.currentTimeMillis - startTime

        val timePerUri = 
          if( indexedCount != 0 ) 
            etime / indexedCount
          else 0 

        val ctime = getUncompletedUris().length * timePerUri

        logger.info("uris indexed: "+indexedCount+" elapsed time: "+etime+" msec "+
                    "time per uri: "+timePerUri+"msec time to completion: "+ctime+" msec")
      }
    }
  }
  
  def endMessage() = {
    val etime = System.currentTimeMillis - startTime
    val timePerUri = etime / urisCompleted.length
    logger.info(
        "uris indexed: "+urisCompleted.length+
        " elapsed time: "+ etime + " msec "+
        "time per uri: " + timePerUri + " msec")
    printErrors
    logger.info("Done")
  }

  def printErrors()={
    logger.info("Errors: ")
    errors.foreach( (e)=>logger.info(e.toString()) )
  }

  def startAllMyWorkers() = {
    uriDiscoveryWorker.start()
    rdfWorker.start()
    solrDocWorker.start()
    solrIndexWorker.start()
  }  
}

object MasterWorker {

  def getMaster() : ActorRef = {
    val masters = Actor.registry.actorsFor[ MasterWorker ]
    if ( masters.size == 0 ) {
      throw new IllegalStateException("There is no MasterWorker in Actor.registry.")
    }
    masters(0)
    
  }

 def setupJenaStatic = {
  //make jena not process imports
  OntDocumentManager.getInstance().setProcessImports(false)  
  //make jena not do LocationMapping
  OntDocumentManager.getInstance().setFileManager(new FileManager(new LocationMapper()))
 }
}
