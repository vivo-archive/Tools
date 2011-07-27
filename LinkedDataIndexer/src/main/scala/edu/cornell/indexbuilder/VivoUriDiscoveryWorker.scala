package edu.cornell.indexbuilder

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
class VivoUriDiscoveryWorker (classUris:List[String])extends UriDiscoveryWorker {

  /**
   * A map of classUri to a list of IndexUris messages
   * for that class */
  val classToUrisMap = Map.empty[String, List[IndexUris]]

  /**
   * A map of classUris to the total expected number of pages
   * that should be generated for the class. */
  val classToPageTotal = Map.empty[String, Int]



  def receive = {

    case GetUrlsToIndexForSite( siteBaseUrl ) => {
      EventHandler.info(this,"GetUrlsToIndexForSite " + siteBaseUrl)      
      //send messages to self to index for each classUri
      for( classUri <- classUris )
        self ! DiscoverUrisForClass(siteBaseUrl,classUri)
    }

    case DiscoverUrisForClass( siteBaseUrl, classUri ) => {
      EventHandler.info(this,"DiscoverUrisForClass " + classUri)
      val url = reqForClassUri( siteBaseUrl, classUri )
      val reqHandler =  getInitialRequestHandler(siteBaseUrl,classUri) 
      // send a message to http worker to do the request and then have
      // it handled by the reqHandler.
      HttpWorker.httpWorkRouter ! HttpGetAndProcess( url, reqHandler)
    }

    case _ => 
         EventHandler.error(this,"got a mystery message")
  }


  /*
   * Handle a request for the initial list of page URLs to go after for
   * a vivo site.  This will send out additional messages to the
   * http worker to make requests for the pages. 
   */
  def getInitialRequestHandler( siteBaseUrl:String, classUri:String ) : ResponseHandler[Unit] = {
    val uriDiscoveryWorker:VivoUriDiscoveryWorker = this

    return new ResponseHandler[Unit]() {
       def handleResponse(  response : HttpResponse ) : Unit = {
         val entity = response.getEntity()
         if( entity == null ){
           EventHandler.error(this,"got null for response.getEntity()")
           return
         }

         //parse result to get urls for all the pages        
         val pageUrls = 
           parseInitialIndividualsByVClassForURLs( EntityUtils.toString(entity) )
             .map( url => siteBaseUrl + url )
         
         //record the total pages for this class in the discovery object
         uriDiscoveryWorker.addClassPageTotal(classUri, pageUrls.length)
         EventHandler.debug(this,"pages to index:" +  pageUrls.length )

         //create a partially applied function that still needs a page
         val responseHandler = 
           getIndexPageHandler(siteBaseUrl,classUri,pageUrls.length,uriDiscoveryWorker)_

         //send out work to prcess each index page
         val http = HttpWorker.httpWorkRouter 
         for( i <- 1 to pageUrls.length )
            http ! HttpGetAndProcess(pageUrls(i-1), responseHandler(i) )
         
         return 
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
    pageCount:Int, 
    uriDiscoveryWorker:VivoUriDiscoveryWorker )
    (page:Int)  //for partial function application
    :ResponseHandler[Unit] = {

    return new ResponseHandler[Unit]() {
       def handleResponse( response : HttpResponse ) : Unit = {    

         //parse the JSON to get URIs for individuals for this page of results
         val entity = response.getEntity()
         if( entity == null ){
           EventHandler.error(this,"got null for response.getEntity()")
           return
         }
        
         val uris=parseIndividualsByVClassForURIs( EntityUtils.toString(entity) )

         //send out work all the URIs
         val master = MasterWorker.getMaster()

         val msg = IndexUris(siteBaseUrl, uris.toList, classUri, page, pageCount)
         //record this set of URIs for the class with the discovery object
         uriDiscoveryWorker.addToUrisMap(classUri,msg)
         master ! msg
         if( uriDiscoveryWorker.discoveryComplete() ){
           master ! DiscoveryComplete( siteBaseUrl )
         }         
      }
    }
  } 

  /** Make an initial HTTP request URL for a classUri */
  def reqForClassUri( siteBaseUrl:String, classUri:String):String={
    return siteBaseUrl + 
      "/dataservice" +
      "?getSolrIndividualsByVClass=1&"+
      "vclassId=" + java.net.URLEncoder.encode( classUri, "UTF-8" )
  }

  def addClassPageTotal(classUri:String,pageTotal:Int){
    synchronized{
      classToPageTotal(classUri)=pageTotal
      classToUrisMap(classUri)=List[IndexUris]()
    }    
  }

  def addToUrisMap(classUri:String,indexUrisMsg:IndexUris){
    synchronized{
      classToUrisMap(classUri) = indexUrisMsg :: classToUrisMap(classUri)      
    }
  }
 

  def discoveryComplete():Boolean = {
    synchronized{
      classToUrisMap.keys.forall( discoveryCompleteForClass )
    }
  } 

  def discoveryCompleteForClass(classUri:String):Boolean = {
    synchronized{
      classToUrisMap(classUri).length == classToPageTotal(classUri)
    }
  }
}

