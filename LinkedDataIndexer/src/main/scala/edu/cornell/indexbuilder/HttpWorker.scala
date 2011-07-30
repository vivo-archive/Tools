package edu.cornell.indexbuilder

import scala.io.Source
import org.apache.http._
import org.apache.http.client.methods._
import org.apache.http.client._
import org.apache.http.impl.client._
import org.apache.http.util.EntityUtils
import org.apache.http.HttpResponse
import org.apache.http.conn.scheme.SchemeRegistry
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager
import org.apache.http.client.ResponseHandler
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory

import akka.actor.Actor
import akka.actor.UntypedActor
import akka.event.EventHandler
import akka.dispatch.Dispatchers
import akka.routing.Routing
import akka.routing._

/* A class to use commons-HttpClient.
 * Redirects are handled by HttpClient.
 * 
 * TODO: cases that need to be handled
 * connection refused
 * timeouts
 */

/* companion object for HttpWorker */
object HttpWorker {
  //maximum http connections for this worker
  val MAX_CONNECTIONS = 10

  //number of actors to startup
  val ACTOR_COUNT = 10

  /* Setup Apache HttpClient */
  val cm = new ThreadSafeClientConnManager()
  cm.setMaxTotal(MAX_CONNECTIONS)
  cm.setDefaultMaxPerRoute(MAX_CONNECTIONS)
  val httpClient: HttpClient = new DefaultHttpClient(cm)

  val workerList = Vector.fill(ACTOR_COUNT)(Actor.actorOf[HttpWorker].start())
  val httpWorkRouter = Routing.loadBalancerActor(new CyclicIterator(workerList))
}

class HttpWorker() extends Actor {

  def receive = {
    case "test" => EventHandler.info(this, "received test")
    
    // // Synchronus GET doesn't get fair scheduling from HttpClient
    // // when there is HttpResponseHandler work in its queue.    
    // case HttpGet(url) => {
    //   EventHandler.debug(this, "doing get " + url)
    //   val response = HttpWorker.httpClient.execute(new HttpGet(url))
    //   self reply response
    // }
    
    case HttpGetAndProcess(url, handler) => {
      EventHandler.debug(this, "doing HttpGetAndProcess " + url)
      HttpWorker.httpClient.execute(new HttpGet(url), handler)
    }
  
    case HttpLinkedDataGet(uri, callback) => {
      EventHandler.debug(this, "Linked Data GET " + uri)
      
      val get = new HttpGet(uri)
      get.setHeader("Accept", RDF_ACCEPT_HEADER)
      
      //need a ResponseHandler that runs the call back for HttpClient
      val handler = new ResponseHandler[Unit]() {
        def handleResponse(response: HttpResponse): Unit = {
          callback(uri, responseToModel(uri, response))
        }
      }
      val response = HttpWorker.httpClient.execute(get, handler)
    }
  

    case HttpLinkedDataGetSync( uri ) => {
      EventHandler.debug(this, "Linked Data GET Sync " + uri)
      
      val get = new HttpGet(uri)
      get.setHeader("Accept", RDF_ACCEPT_HEADER)

      val replyTo = self.channel
      val handler = new ResponseHandler[Unit]() {
        def handleResponse(response: HttpResponse): Unit = {
           replyTo ! responseToModel(uri, response)
        }
      }
      HttpWorker.httpClient.execute(get, handler)
    }  
  }

 def responseToModel(uri: String, response: HttpResponse): Model = {
   EventHandler.debug(this, "got response %s for %s".format(response, uri))
   val m = ModelFactory.createDefaultModel();
   if( response != null && 
       response.getStatusLine() != null &&
       response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
          EventHandler.warning(this,"could not get HTTP for " + uri + 
                             " status: " + response.getStatusLine())
       } else {
         val entity = response.getEntity();
         if (entity == null) {
           EventHandler.error(this,"could not get HttpEntity for " + uri + 
                              " status: " + response.getStatusLine())
         }else{
           //Now we really try to get the content
           val instream = entity.getContent()
           try {
             m.read(instream, "", getRDFType( entity ))
             instream.close();
           } catch {
             case e => {
               EventHandler.error(e,this, "could not parse RDF for " + uri +
                                  " status: " + response.getStatusLine() + " "
                                  + entity.getContentType() )
             }
           }
         }
       }
   return m
  }

  def getRDFType( entity:HttpEntity):String = {
    if( entity != null && 
        entity.getContentType() != null && 
        entity.getContentType().getValue() != null )
      {
         val contentType = entity.getContentType().getValue()
         if( contentType.contains("text/n3") || contentType.contains("text/rdf+n3")) 
           return "N3"
         else if( contentType.contains("text/rdf+n3") ) 
           return "RDF/XML"
         else if( contentType.contains("text/turtle")) 
           return "TURTLE"
       }

    return "UNKNOWN"       
  }

  val RDF_ACCEPT_HEADER = 
    "text/n3, text/rdf+n3, application/rdf+xml;q=0.9, text/turtle;q=0.8"

  val HEADER_TO_JENASTR =
    Map( "text/n3" -> "N3",
         "text/rdf+n3" -> "N3",
         "application/rdf+xml" -> "RDF/XML",
         "text/turtle" -> "TURTLE")

//Getting string from InputStream
//EventHandler.debug(this, scala.io.Source.fromInputStream(instream).getLines().mkString("\n") )
}

