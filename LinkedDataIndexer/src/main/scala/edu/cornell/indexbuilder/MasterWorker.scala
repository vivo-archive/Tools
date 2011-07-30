package edu.cornell.indexbuilder

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor._
import akka.actor.Actor._
import akka.event.EventHandler
import scalax.io.Resource
import org.apache.solr.client.solrj.SolrServer

/**
 * Handle the main coordination of messages to build the index.
 */

class MasterWorker(
  siteUrl: String, 
  uriDiscoveryWorker: ActorRef,  
  solrServer: SolrServer, 
  selectorGen: SelectorGenerator ) 
extends Actor {

  //Workers for use by the master:
  val rdfWorker = Actor.actorOf( new RdfLinkedDataWorker() )
  val solrDocWorker = Actor.actorOf( new SolrDocWorker( selectorGen ) )
  val solrIndexWorker = Actor.actorOf( new SolrIndexWorker(solrServer) )

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

  var errors = List[IndexBuilderMessage]()

  var startTime = 0L

  override def preStart () = {
    startTime = System.currentTimeMillis
  }

  def receive = {

    case GetUrlsToIndexForSite( siteUrl ) =>{
      startAllMyWorkers()
      uriDiscoveryWorker ! GetUrlsToIndexForSite( siteUrl )
    }

    case IndexUris(baseSiteUrl, uris, classUri, page, totalPages) => {
      EventHandler.debug(this, "Received URIs to index: " + uris.length)
      addToUrisToIndex( uris )
    }

    case DiscoveryComplete(baseSiteUrl) => {
      EventHandler.info(this, "Discovery complete for site " 
                         + baseSiteUrl + " starting linked data reqeusts.")
      getUncompletedUris().foreach( uri => rdfWorker ! GetRdf( uri ) )
    }

    // make solr documents for the RDF model    
     case GotRdf( uri, model ) =>{
       solrDocWorker ! RdfToDoc( siteUrl, uri, model ) 
     }

    // index the SolrDoc
    case GotDoc( siteUrl, uri, doc) =>{      
      solrIndexWorker ! IndexDoc (siteUrl,uri,doc)
    }

    // solr document was indexed so remove it from the list of URIs to index
    case IndexedDoc( siteUrl, uri ) =>{
      //record that uri was indexed
      addToCompletedUris( uri )

      if( allUrisIndexed ){
        solrIndexWorker !! Commit //wait for commit to finish
        endMessage()
        Actor.registry.shutdownAll()
      }else{
        incrementalMessage()
      }
    }

    case CouldNotGetData(siteUrl,uri,msg) => {
      synchronized{
        urisWithErrors +=  uri
        errors =   CouldNotGetData(siteUrl,uri,msg) :: errors
      }
    }

    case _ => println("SiteIndexWoker got some mystery message")
  }


  def getUncompletedUris():Seq[String] = {
    synchronized{
      urisToIndex.diff( urisCompleted )
    }
  }

  def allUrisIndexed():Boolean = {
    synchronized{
      urisToIndex.length == (urisCompleted.length + urisWithErrors.size) 
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
      if( urisCompleted.length % 100 == 0 ){
        val etime = System.currentTimeMillis - startTime

        val timePerUri = if( urisCompleted.length != 0 ) 
          etime / urisCompleted.length
                         else 0 

        val ctime = getUncompletedUris().length * timePerUri

        EventHandler.info(this,
           "uris indexed: %d elapsed time: %d msec time per uri: %d msec time to completion: %d msec"
           .format( urisCompleted.length, etime, timePerUri, ctime) )
      }
    }
  }
  
  def endMessage() = {
    val etime = System.currentTimeMillis - startTime
    val timePerUri = etime / urisCompleted.length
    EventHandler.info(this,
        "uris indexed: %d elapsed time: %d msec time per uri: %d msec"
        .format( urisCompleted.length, etime, timePerUri) )
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
      EventHandler.error(this,"Could not get MasterWorker, not doing anything.")
    }
    masters(0)
    
  }
}
