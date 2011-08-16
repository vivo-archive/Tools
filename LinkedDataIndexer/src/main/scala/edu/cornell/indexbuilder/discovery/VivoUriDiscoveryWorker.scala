package edu.cornell.indexbuilder.discovery

import akka.actor.ActorRef

import akka.actor.{Actor, PoisonPill}
import Actor._
import akka.routing.{Routing, CyclicIterator}
import Routing._
import com.weiglewilczek.slf4s.Logging
import org.apache.http.client.ResponseHandler
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import edu.cornell.mannlib.vitro.indexbuilder.ParseDataServiceJson._
import scala.collection.mutable.Map

import edu.cornell.indexbuilder.http.HttpWorker
import edu.cornell.indexbuilder.http.HttpWorker._
import edu.cornell.indexbuilder.http.HttpMessages
import edu.cornell.indexbuilder.MasterWorker
import edu.cornell.indexbuilder.indexing._
import edu.cornell.indexbuilder.discovery._
import edu.cornell.indexbuilder._
import edu.cornell.indexbuilder.http._


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
class VivoUriDiscoveryWorker (
  classUris:List[String], 
  action:String, 
  workDirectory:String )

extends Actor with Logging {
  
  /**
   * A map of classUri to if page discovery is complete
   * for that class.  As the page list is retreived for a
   * class it will be set to true in this map. */
  val isPageDiscoveryCompleteForClassUri = Map.empty[String, Boolean]

  /**
   * A map of pageUrls and if they are complete. When a
   * pageUrl has had its URIs added to the list of URIs to
   * index, it will be set to true in this map.
   */
  val isUriDiscoveryCompleteForPageUrl = Map.empty[String, Boolean]

  def receive = {

    case DiscoverUrisForSite( siteBaseUrl ) => {
      logger.info("GetUrlsToIndexForSite " + siteBaseUrl)      
      
      if( hasSavedState() ){
        restartFromState( self )
      }else{
        //starting with no saved state
        setupInitialState(  )
        
        whatsLeft()

        //send messages to self to index for each classUri
        for( classUri <- classUris )
          self ! DiscoverUrisForClass(siteBaseUrl,classUri)
      }
    }

    case DiscoverUrisForClass( siteBaseUrl, classUri ) => {
      logger.info("DiscoverUrisForClass " + classUri)
      val url = reqForClassUri( siteBaseUrl, classUri )
      val initReqHandler =  getInitialRequestHandler(siteBaseUrl, classUri, self ) 

      // Send a message to HttpWorker to do the request and then it will call initReqHandler

      HttpWorker.httpWorkRouter ! HttpGetAndProcess( url, initReqHandler )
    }

    case DiscoverUrisForClassPage(siteBaseUrl,classUri,pageUrl) => {
      //respHandler will be run by HttpWorker 
      val respHandler = 
           getIndexPageHandler(siteBaseUrl, classUri, pageUrl, this )

      //send out work to prcess index page for URIs
      HttpWorker.httpWorkRouter ! HttpGetAndProcess(pageUrl, respHandler)
    }

    case e => 
         logger.error("got a mystery message: " + e)
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
           logger.error("got null for response.getEntity()")
           return
         }

         //parse result to get urls for all the pages        
         val pageMsgs = 
           parseInitialIndividualsByVClassForURLs( EntityUtils.toString(entity), action )
           .map( url => DiscoverUrisForClassPage(siteBaseUrl,classUri,siteBaseUrl+url))
         
         logger.debug("Adding %d pages to index for class %s".format(pageMsgs.length,classUri) )

         //save the page message to the file system state
         pageMsgs.foreach( savePageToState )
         allPagesDiscoveredForClass(classUri)
         
         whatsLeft()

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
           logger.error("got null for response.getEntity()")
           
         }else{           
           try{
             val uris=parseIndividualsByVClassForURIs( EntityUtils.toString(entity) )
             //send out work for all the URIs           
             val msg =  IndexUris( siteBaseUrl, uris.toList  )
             
             urisDiscoveredForPage( pageUrl )
             saveUrisToState( classUri, pageUrl, msg )
             
             MasterWorker.getMaster() ! msg
           }catch{
             case e: Exception => {
               logger.error("Could not get page for %s".format(pageUrl),e)
               errorDuringDiscoveryForPage( pageUrl )
             }
           }

           //if discovery is done, send out message to Master
           if( isDiscoveryComplete() )
             MasterWorker.getMaster() ! DiscoveryComplete( siteBaseUrl )
           else
             whatsLeft()           
         }    
      }
    }
  }

  /** Make an initial HTTP request URL for a classUri */
  def reqForClassUri( siteBaseUrl:String, classUri:String):String={
    return siteBaseUrl + "/dataservice" +
      "?" + action + "&"+
      "vclassId=" + java.net.URLEncoder.encode( classUri, "UTF-8" )
  }

  def setupInitialState():Unit={
    //Add all classes as not yet having their page discovery completed
    for( classUri <- classUris ){
      isPageDiscoveryCompleteForClassUri += classUri -> false
    }
  }

  def allPagesDiscoveredForClass(classUri:String) : Unit = {
    isPageDiscoveryCompleteForClassUri += classUri -> true
  }

  def savePageToState( pageMsg:DiscoverUrisForClassPage ) :Unit = {    
    //record that there is a new pageURL that need to be indexed
    // and that it is not yet indexed
    synchronized{
      if( isUriDiscoveryCompleteForPageUrl.contains( pageMsg.pageUrl))
         logger.warn( 
           "Not adding pageUrl to isUriDiscoveryCompleteForPageUrl"+
           " becasue it is already in the map")
      else
        isUriDiscoveryCompleteForPageUrl += pageMsg.pageUrl -> false
    }  
  } 

  /**
   * Call this method when the URIs for a page
   * have been retreived and added to the queue.
   */
  def urisDiscoveredForPage( pageUrl:String){
    //record that the page is completed
    synchronized{
      isUriDiscoveryCompleteForPageUrl += pageUrl -> true 
    }                           
  }

  /**
   * Call this method when there has been an error
   * while getting the URIs for a page
   */
  def errorDuringDiscoveryForPage( pageUrl:String ){
    //TODO: should record that there was an error of some sort.
    synchronized{
      isUriDiscoveryCompleteForPageUrl += pageUrl -> true 
    }                           
  }

  def isDiscoveryComplete():Boolean = {
    synchronized{
      isDiscoveryCompleteForClasses && isDiscoveryCompleteForPages 
    }
  } 
 
  def isDiscoveryCompleteForClasses():Boolean ={
      isPageDiscoveryCompleteForClassUri.values.forall( _ == true ) 
  }

  def isDiscoveryCompleteForPages():Boolean={
      isUriDiscoveryCompleteForPageUrl.values.forall( _ == true )
  }

  def saveUrisToState( classUri:String, pageUrl:String, uriMsg:IndexUris):Unit ={
    //TODO: save state
  }

  def hasSavedState():Boolean={
    //TODO: saving state is not implemented
    false
  }

  def restartFromState( actor:ActorRef):Unit={
    //TODO: go through the state object and send out any messages that are needed
  }

  def whatsLeft():Unit={    
    // logger.debug("classes left to get pages for: %s ".format(
    //   isPageDiscoveryCompleteForClassUri.foldLeft(""){ case (a,(k,v)) => if( v ) "" else ", "+k}))
    // logger.debug("pages left to get URIs for: %s ".format(
    //   isUriDiscoveryCompleteForPageUrl.foldLeft(""){ case (a,(k,v)) => if( v ) "" else ", "+k}))
    //logger.debug("classes left to get pages for: %s ".format( isPageDiscoveryCompleteForClassUri ) )
    //logger.debug("pages left to get URIs for: %s ".format( isUriDiscoveryCompleteForPageUrl ))
  }
}

object VivoUriDiscoveryWorker{
  val rel12actionName = "getLuceneIndividualsByVClass=1";
  val rel13actionName = "getSolrIndividualsByVClass=1";
}
