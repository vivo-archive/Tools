package edu.cornell.indexbuilder
import akka.actor.ActorRef

import akka.actor.{Actor, PoisonPill}
import Actor._
import akka.routing.{Routing, CyclicIterator}
import Routing._
import akka.event.EventHandler
import org.apache.http.client.ResponseHandler
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import edu.cornell.mannlib.vitro.indexbuilder.ParseDataServiceJson._
import scala.collection.mutable.Map

/**
 * How to get the list of classes?
 *   get browse page and extract list of classes from href attributes
 *   get the vivo ontology and run a query?
 *   or maybe try getting members of owl:Thing?
 * 
 * How to get the individuals for a given class?
 *   It would be nice to use IndividualListRdfController but that is broken, fixed in vivo release 1.2.1.
 *   So instead, try to use json /dataservice controller to get owl:Thing and then figure out the page URLs and request those.
 *   ex. http://vivo.cornell.edu/dataservice?getSolrIndividualsByVClass=1&vclassId=http%3A%2F%2Fvivo.library.cornell.edu%2Fns%2F0.1%23CornellAffiliatedPerson"
 * 
 * What to do about the URIs?
 *   There will be a lot of them, 100,000 or so per a site.
 *   The whole process should be interuptable and restartable.
 *   They should get queued up in files or someting.
 *
 * Problem 1:
 *   There is a bug in vivo that causes lucene searches where
 *   if you try to get a document that is the 30001th item in a
 *   result set you get an exception.
 * Workaround for problem 1:
 *   Do URI discovery for a list of vivo classes with the hope that
 *   all the classes will have fewer than 30000 members.
 * 
 * create working directory
 * get first /dataservice request
 * figure out all the requests to /dataserivce that will be needed
 * make them and put each in a file
 * 
 */
class VivoUriDiscoveryWorker (classUris:List[String], action:String, workDirectory:String )
extends UriDiscoveryWorker {

  /**
   * A map of classUri to a list of IndexUris messages
   * for that class */
  val classToUrisMap = Map.empty[String, List[IndexUris]]

  /**
   * A map of classUris to the total expected number of pages
   * that should be generated for the class. */
  val classToPageTotal = Map.empty[String, Int]

  /* URIs of classes that have not yet had pages retrieved for them */
  val classQueue = List.empty[String]

  /* URLs of pages that have not yet been retrieved */
  val pageQueue = List.empty[String]

  /* URIs of individuals to index */
  val indexQueue = List.empty[String]

  @Override
  def prestart() = {
    //set up class queue 
    
    //set up page queue
    
    //setup URI queue
  }

  def receive = {

    case GetUrlsToIndexForSite( siteBaseUrl ) => {
      EventHandler.info(this,"GetUrlsToIndexForSite " + siteBaseUrl)      
      
      if( hasSavedState() ){
        restartFromState( self )
      }else{
        //starting with no saved state
        setupInitialState(  )
        
        //send messages to self to index for each classUri
        for( classUri <- classUris )
          self ! DiscoverUrisForClass(siteBaseUrl,classUri)
      }
    }

    case DiscoverUrisForClass( siteBaseUrl, classUri ) => {
      EventHandler.info(this,"DiscoverUrisForClass " + classUri)
      val url = reqForClassUri( siteBaseUrl, classUri )
      val initReqHandler =  getInitialRequestHandler(siteBaseUrl, classUri, self ) 

      // Send a message to HttpWorker to do the request and then it will call initReqHandler
      HttpWorker.httpWorkRouter ! HttpGetAndProcess( url, initReqHandler)
    }

    case DiscoverUrisForClassPage(siteBaseUrl,classUri,pageUrl) => {
      //respHandler will be run by HttpWorker 
      val respHandler = 
           getIndexPageHandler(siteBaseUrl, classUri, pageUrl, this )

      //send out work to prcess index page for URIs
      HttpWorker.httpWorkRouter ! HttpGetAndProcess(pageUrl, respHandler)
    }

    case _ => 
         EventHandler.error(this,"got a mystery message")
  }


  /*
   * Handle a request for the initial list of page URLs to go after for
   * a vivo site.  This will send out additional messages to the
   * http worker to make requests for the pages. 
   */
  def getInitialRequestHandler( siteBaseUrl:String, classUri:String , actor:ActorRef ) : ResponseHandler[Unit] = {
    val uriDiscoveryWorker:VivoUriDiscoveryWorker = this

    return new ResponseHandler[Unit]() {
       def handleResponse(  response : HttpResponse ) : Unit = {
         val entity = response.getEntity()
         if( entity == null ){
           EventHandler.error(this,"got null for response.getEntity()")
           return
         }

         //parse result to get urls for all the pages        
         val pageMsgs = 
           parseInitialIndividualsByVClassForURLs( EntityUtils.toString(entity), action )
           .map( url => DiscoverUrisForClassPage(siteBaseUrl,classUri,siteBaseUrl+url))
         
         EventHandler.debug(this,"Adding %d pages to index for class %s".format(pageMsgs.length,classUri) )

         //save the page message to the file system state
         pageMsgs.foreach( uriDiscoveryWorker.savePageToState )
         uriDiscoveryWorker.pagesDiscoveredForClass(classUri)

         //send out messages to get the pages converted to URIs to index
         pageMsgs.foreach( actor ! _ )
      }
    }
  } 

 /*
  * Handle a request for a single page of URIs from the JSON data service
  * of a vivo system. Parse the JSON into URIs, save them to the discovery worker
  * and send a message to the master worker.
  */
  def getIndexPageHandler(
    siteBaseUrl:String, 
    classUri:String, 
    pageUrl:String,
    uriDiscoveryWorker:VivoUriDiscoveryWorker ) : ResponseHandler[Unit] = {

    return new ResponseHandler[Unit]() {
       def handleResponse( response : HttpResponse ) : Unit = {    
         //TODO: add better error handling 

         //parse the JSON to get URIs for individuals for this page of results
         val entity = response.getEntity()
         if( entity == null ){
           EventHandler.error(this,"got null for response.getEntity()")
           
         }else{           
           val uris=parseIndividualsByVClassForURIs( EntityUtils.toString(entity) )

           //send out work for all the URIs           
           val msg = IndexUris(siteBaseUrl, uris.toList  )
           
           saveUrisToState( pageUrl, msg )

           val master = MasterWorker.getMaster()
           master ! msg

           //if discovery is done, send out message to Master
           if( uriDiscoveryWorker.isDiscoveryComplete() ){
             master ! DiscoveryComplete( siteBaseUrl )
           }     
         }    
      }
    }
  }

  /** Make an initial HTTP request URL for a classUri */
  def reqForClassUri( siteBaseUrl:String, classUri:String):String={
    return siteBaseUrl + 
      "/dataservice" +
      "?" + action + "&"+
      "vclassId=" + java.net.URLEncoder.encode( classUri, "UTF-8" )
  }

  def setupInitialState():Unit={
  }

  def hasSavedState():Boolean={
    false
  }

  def restartFromState( actor:ActorRef):Unit={
    //go through the state object and send out any messages that are needed
  }

  def isDiscoveryComplete():Boolean = {
    synchronized{
      classToUrisMap.keys.forall( isDiscoveryCompleteForClass )
    }
  } 

  def isDiscoveryCompleteForClass(classUri:String):Boolean = {
    synchronized{
      classToUrisMap(classUri).length == classToPageTotal(classUri)
    }
  }

  def pagesDiscoveredForClass( classUri:String ){
    EventHandler.error(this,"pagesDiscoveredForClass not implemented!!!")
  }
  
  def savePageToState( pageMsg:DiscoverUrisForClassPage ) :Unit = {
    EventHandler.error(this,"savePageToState not implemented!!!")
  }

  def saveUrisToState( pageUrl:String, uriMsg:IndexUris):Unit ={
    //remove pageUrl from todo list
    urisDiscoveredForPage( pageUrl )
    //add uriMsg to todo list
    EventHandler.error(this,"saveUrisToState not implemented!!!")
  }

  def urisDiscoveredForPage( pageUri:String){
    EventHandler.error(this,"urisDiscoveredForPage not implemented!!!")
  }

}

object VivoUriDiscoveryWorker{
  val rel12actionName = "getLuceneIndividualsByVClass=1";
  val rel13actionName = "getSolrIndividualsByVClass=1";
}
