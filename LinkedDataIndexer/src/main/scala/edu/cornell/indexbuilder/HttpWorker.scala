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

/* simple class to use commons-HttpClient 
 *
 * TODO: cases that need to be handled
 * connection refused
 * content negotiation
 * redirects
 * timeouts
 * 
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
      
      //TODO: content negotiation
      val get = new HttpGet(uri)
      get.setHeader("Accept", "text/n3")
      
      //need a ResponseHandler that runs the call back for HttpClient
      val handler = new ResponseHandler[Unit]() {
        def handleResponse(response: HttpResponse): Unit = {
          callback(uri, responseToModel(uri, response))
        }
      }
      val response = HttpWorker.httpClient.execute(get, handler)
    }
  

    case HttpLinkedDataGetSync(uri ) => {
      EventHandler.debug(this, "Linked Data GET Sync " + uri)
      
      //TODO: content negotiation
      val get = new HttpGet(uri)
      get.setHeader("Accept", "text/n3")

      val replyTo = self.channel
      val handler = new ResponseHandler[Unit]() {
        def handleResponse(response: HttpResponse): Unit = {
           replyTo ! responseToModel(uri, response)
        }
      }

      HttpWorker.httpClient.execute(get, handler)
    }
  
//    case _ => EventHandler.info(this, "received unknown message")
  }

 def responseToModel(uri: String, response: HttpResponse): Model = {
    EventHandler.debug(this, "got response " + response + " for " + uri)
    val entity = response.getEntity();
    val m = ModelFactory.createDefaultModel();
    if (entity != null) {
      val instream = entity.getContent()
      try {
        //TODO: do content negotiation
        m.read(instream, "", "N3")
      } catch {
        case e => {
          EventHandler.error(e,this, "could not parse RDF for " + uri + " status: " + response.getStatusLine() + " " + entity.getContentType() )
          EventHandler.debug(this, scala.io.Source.fromInputStream(instream).getLines().mkString("\n") )
        }          
      } finally {
        instream.close();
      }
    } else {
      EventHandler.error(this, "could not get HttpEntity for " + uri + " status: " + response.getStatusLine())
    }
    return m
  }


}

