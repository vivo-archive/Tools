package edu.cornell.indexbuilder

import akka.actor._
import akka.actor.{Actor, ActorRef}
import akka.actor.Actor._
import akka.dispatch.Dispatchers
import akka.routing.CyclicIterator
import akka.routing.Routing._
import akka.config.Supervision._



import akka.actor.{Actor, ActorRef}

import akka.config.Supervision.{OneForOneStrategy,Permanent}
import Actor._
import akka.event.EventHandler


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
import org.joda.time.Duration
import org.joda.time.format.PeriodFormatterBuilder
import scala.tools.nsc.interpreter.ILoop
import scala.tools.nsc.interpreter.NamedParam
import sun.misc.Signal
import sun.misc.SignalHandler

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
  
  val handler = new EvalOnAlarmHandler( this )

  self.lifeCycle = Permanent
  self.faultHandler = OneForOneStrategy(List(classOf[Exception]),5,1000)

  //Workers for use by the master:
  val rdfWorker = makeRdfLinkedDataWorker( http, skipUrl )
  self.link(rdfWorker)

  val solrDocWorker = makeSolrDocWorker( selectorGen, siteName ) 
  self.link(solrDocWorker)

  val solrIndexWorker = makeSolrIndexWorker( solrServer )
  self.link(solrIndexWorker)

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
      logger.error("could not get data for "+uri+" because " +msg)
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

    case MaximumNumberOfRestartsWithinTimeRangeReached(_,_,_,_) =>
      logger.error("max errors received")

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
      val errors = urisWithErrors.size
      if( (indexedCount+errors) % 10 == 0 ){        
        
        val etime = System.currentTimeMillis - startTime

        val etimeStr = ctimeFormater.print(new Duration( etime ).toPeriod())

        val timePerUri = 
          if( indexedCount != 0 ) 
            etime / (indexedCount+errors)
          else 0 

        val ctime = ctimeFormater.print(new Duration(getUncompletedUris().length * timePerUri).toPeriod())
        
        logger.info("uris indexed: "+indexedCount+" errors: " + errors + " elapsed time: "+etimeStr+" "+
                    "time per uri: "+timePerUri+" msec, time to completion: "+ ctime )
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

  def makeRdfLinkedDataWorker( http:Http, skipUrl:String => Boolean  ):ActorRef={
    val numberOfWorkers = 300
    var workers = List[ActorRef]()
    for( i <- 0 until numberOfWorkers ) {
      val rdfWorker = Actor.actorOf( new RdfLinkedDataWorker(http, skipUrl) )
      self.link( rdfWorker )
      rdfWorker.start()
      workers = rdfWorker :: workers 
    }
    loadBalancerActor( new CyclicIterator[ActorRef](workers) )
  }

  def makeSolrIndexWorker( solrServer:SolrServer  ):ActorRef={
    val numberOfWorkers = 200
    var workers = List[ActorRef]()
    for( i <- 0 until numberOfWorkers ) {
      val solrIndexWorker = Actor.actorOf( new SolrIndexWorker( solrServer ) )
      //solrIndexWorker.dispatcher = MasterWorker.dispatcher
      solrIndexWorker.lifeCycle = Permanent
      self.link( solrIndexWorker )
      solrIndexWorker.start()
      workers = solrIndexWorker :: workers 
    }
    loadBalancerActor( new CyclicIterator[ActorRef](workers) )
  }

  def makeSolrDocWorker( selectorGen:SelectorGenerator, siteName:String):ActorRef={
    val numberOfWorkers = 200
    var workers = List[ActorRef]()
    for( i <- 0 until numberOfWorkers ) {
      val worker = Actor.actorOf( new SolrDocWorker( selectorGen, siteName ) )  
      //worker.dispatcher = MasterWorker.dispatcher
      worker.lifeCycle = Permanent
      self.startLink( worker )
      workers = worker :: workers 
    }
    loadBalancerActor( new CyclicIterator[ActorRef](workers) )
  }


}

object MasterWorker {

  val dispatcher = Dispatchers.newExecutorBasedEventDrivenWorkStealingDispatcher("pool")
                   .setCorePoolSize(100)
                   .setMaxPoolSize(100)
                   .build

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

 val ctimeFormater = new PeriodFormatterBuilder()
  .printZeroAlways()
  .appendHours()
  .appendSuffix(" hour"," hours")
  .appendSeparator(" and ")
  .appendMinutes()
  .appendSuffix(" minute"," minutes")
  .toFormatter()
}

/*
 * Signal handler to fall into a eval loop
 * when the alarm signal is sent to process.
 */
class EvalOnAlarmHandler( master:MasterWorker ) extends SignalHandler {
  val signalName = "USR2"

  //save the old handler for chaining 
  val oldHandler = Signal.handle(new Signal(signalName),this)

  def handle(sig:Signal):Unit={
    try{
      System.out.println("HANDLED by OUR CODE!")
      ILoop.break( List( NamedParam("master",master) ) )
      //if( oldHandler != null ){
      //  oldHandler.handle(sig);
     // }
    } catch {
      case e => System.out.println("Signal handler failed, reason"+e)
    }
  }




// uriDiscoveryWorker :: rdfWorker :: solrDocWorker :: solrIndexWorker )
}
