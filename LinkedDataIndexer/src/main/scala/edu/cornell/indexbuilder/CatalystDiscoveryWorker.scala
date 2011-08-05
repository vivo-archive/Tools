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
 * This is a URI discvoery worker for catalyst systems.  It will attempt
 * to gather information from a catalyst web site about the URIs of
 * individuals that could be retreived via linked data requests and
 * added to the index.
 *
 * The example of a catalyst site that we have is from Harvard:
 * http://connects.catalyst.harvard.edu/
 *
 * An empty search will return a result set with all the people.
 *
 * Process:
 * 1) run search for all people
 * 2) using that first result page, make a list of page URLs to be requested
 * 3) retrieve each search result page and get URIs off of those
 * 
 */
class CatalystDiscoveryWorker (classUris:List[String], action:String, workDirectory:String )
extends UriDiscoveryWorker {
  

  def receive = {

    case GetUrlsToIndexForSite( siteBaseUrl ) => {
      EventHandler.info(this,"GetUrlsToIndexForSite " + siteBaseUrl)      
      
      if( hasSavedState() ){
        restartFromState( self )
      }else{
        //starting with no saved state
        setupInitialState(  )        
        whatsLeft()
        
      }
    }

    case DiscoverUrisForClass( siteBaseUrl, classUri ) => {
      EventHandler.info(this,"DiscoverUrisForClass " + classUri)
      val url = reqForClassUri( siteBaseUrl, classUri )
      val initReqHandler =  getInitialRequestHandler(siteBaseUrl, classUri, self ) 

      // Send a message to HttpWorker to do the request and then it will call initReqHandler
      HttpWorker.httpWorkRouter ! HttpGetAndProcess( url, initReqHandler)
    }

    case _ => 
         EventHandler.error(this,"got a mystery message")
  }


  def getPageURLs( siteBaseUrl:String ):List[String] {
    List[String].empty()
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
           EventHandler.error(this,"got null for response.getEntity()")
           
         }else{           
           try{
             val uris=parseIndividualsByVClassForURIs( EntityUtils.toString(entity) )
             //send out work for all the URIs           
             val msg = IndexUris( siteBaseUrl, uris.toList  )
             
             urisDiscoveredForPage( pageUrl )
             saveUrisToState( classUri, pageUrl, msg )
             
             MasterWorker.getMaster() ! msg
           }catch{
             case e: Exception => {
               EventHandler.error(e,this,"Could not get page for %s".format(pageUrl))
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
         EventHandler.warning(this, 
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
    // EventHandler.debug(this,"classes left to get pages for: %s ".format(
    //   isPageDiscoveryCompleteForClassUri.foldLeft(""){ case (a,(k,v)) => if( v ) "" else ", "+k}))
    // EventHandler.debug(this,"pages left to get URIs for: %s ".format(
    //   isUriDiscoveryCompleteForPageUrl.foldLeft(""){ case (a,(k,v)) => if( v ) "" else ", "+k}))
    //EventHandler.debug(this,"classes left to get pages for: %s ".format( isPageDiscoveryCompleteForClassUri ) )
    //EventHandler.debug(this,"pages left to get URIs for: %s ".format( isUriDiscoveryCompleteForPageUrl ))
  }
}

object VivoUriDiscoveryWorker{
  val rel12actionName = "getLuceneIndividualsByVClass=1";
  val rel13actionName = "getSolrIndividualsByVClass=1";
}
