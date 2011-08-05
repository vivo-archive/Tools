package edu.cornell.indexbuilder.discovery

import akka.actor.Actor._
import akka.actor.Actor
import akka.actor.ActorRef
import akka.routing.Routing._
import com.weiglewilczek.slf4s.Logging
import edu.cornell.indexbuilder.http.{HttpGetAndProcess, HttpWorker}
import edu.cornell.indexbuilder.MasterWorker
import edu.cornell.mannlib.vitro.indexbuilder.ParseDataServiceJson._
import edu.cornell.mannlib.vitro.indexbuilder.CatalystPageToURIs
import org.apache.http.client.ResponseHandler
import org.apache.http.util.EntityUtils
import org.apache.http.HttpResponse
import scala.collection.JavaConversions._

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
extends Actor with Logging {
  
  // Share a single CatalystPageToURIs object 
  val pageParser = new CatalystPageToURIs()

  def receive = {

    case DiscoverUrisForSite( siteBaseUrl ) => {
      logger.info("GetUrlsToIndexForSite " + siteBaseUrl)
    }

    case DiscoverUrisForPage( siteBaseUrl, pageUrl) => {
      logger.info("DiscoverUrisForPage " + pageUrl)
    }

    case _ => 
         logger.error("got a mystery message")
  }

  def getUrisForPage( siteUrl:String, pageUrl:String ){
    // Send a message to HttpWorker to do the request and then it will call initReqHandler
    val handler = getIndexPageHandler(siteUrl,  pageUrl, this ) 
    HttpWorker.httpWorkRouter !! HttpGetAndProcess( pageUrl, handler )
    //val urisForPage = pageParser.parseForURIs("test")
    logger.error("getUrisForPage() is not implemented")
  }

  /*
   * Handle a request for the initial list of page URLs to go after for
   * a vivo site.  This will send out additional messages to the
   * http worker to make requests for the pages. 
   */
  def getInitialRequestHandler( siteBaseUrl:String, classUri:String , actor:ActorRef ) : ResponseHandler[Unit] = {

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
         //pageMsgs.foreach( savePageToState )
         //allPagesDiscoveredForClass(classUri)
         
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
    pageUrl:String,
    uriDiscoveryWorker:CatalystDiscoveryWorker ) : ResponseHandler[Unit] = {

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
             //val msg = IndexUris( siteBaseUrl, uris.toList  )
             val msg = URIsDiscovered( siteBaseUrl, uris.toList  )
             
             //urisDiscoveredForPage( pageUrl )
             //saveUrisToState( classUri, pageUrl, msg )
             
             MasterWorker.getMaster() ! msg
           }catch{
             case e: Exception => {
               logger.error("Could not get page for %s".format(pageUrl), e)
               //errorDuringDiscoveryForPage( pageUrl )
             }
           }

           //if discovery is done, send out message to Master
           // if( isDiscoveryComplete() )
           //   MasterWorker.getMaster() ! DiscoveryComplete( siteBaseUrl )
           // else
           //   whatsLeft()           
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


  def whatsLeft():Unit={    
    // logger.debug(this,"classes left to get pages for: %s ".format(
    //   isPageDiscoveryCompleteForClassUri.foldLeft(""){ case (a,(k,v)) => if( v ) "" else ", "+k}))
    // logger.debug(this,"pages left to get URIs for: %s ".format(
    //   isUriDiscoveryCompleteForPageUrl.foldLeft(""){ case (a,(k,v)) => if( v ) "" else ", "+k}))
    //logger.debug(this,"classes left to get pages for: %s ".format( isPageDiscoveryCompleteForClassUri ) )
    //logger.debug(this,"pages left to get URIs for: %s ".format( isUriDiscoveryCompleteForPageUrl ))
  }
}

