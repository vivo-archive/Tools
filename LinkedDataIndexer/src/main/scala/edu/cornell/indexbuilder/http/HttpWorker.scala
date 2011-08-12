package edu.cornell.indexbuilder.http


import akka.actor.{Actor, UntypedActor}
import akka.routing._

import com.hp.hpl.jena.rdf.model.{Model, ModelFactory}
import com.weiglewilczek.slf4s.Logging
import java.io.ByteArrayOutputStream

import org.apache.http._
import org.apache.http.client._
import org.apache.http.client.methods._
import org.apache.http.client.ResponseHandler
import org.apache.http.impl.client._
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager
import org.apache.http.HttpResponse

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

class HttpWorker() extends Actor with Logging{

  def receive = {
    case "test" => logger.info( "received test")
    
    // // Synchronus GET doesn't get fair scheduling from HttpClient
    // // when there is HttpResponseHandler work in its queue.    
    // case HttpGet(url) => {
    //   logger.debug( "doing get " + url)
    //   val response = HttpWorker.httpClient.execute(new HttpGet(url))
    //   self reply response
    // }
    
    case HttpGetAndProcess(url, handler) => {
      logger.debug( "doing HttpGetAndProcess " + url)
      HttpWorker.httpClient.execute(new HttpGet(url), handler)
    }
  
    case HttpLinkedDataGet(uri, callback) => {
      logger.debug( "Linked Data GET " + uri)
      
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
      logger.debug( "Linked Data GET Sync " + uri)
      
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
   logger.debug( "got response %s for %s".format(response, uri))
   val m = ModelFactory.createDefaultModel();

   if( response != null && 
       response.getStatusLine() != null &&
       response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
          logger.warn("could not get HTTP for " + uri + 
                             " status: " + response.getStatusLine())
       } else {
         val entity = response.getEntity();
         if (entity == null) {
           logger.error("could not get HttpEntity for " + uri + 
                              " status: " + response.getStatusLine())
         }else{
           //Now we really try to get the content
           logger.debug("attempting to convert HTTP entity to RDF")
           val instream = entity.getContent()           
           try {
             m.read(instream, "", getRDFType( entity ))
             logger.debug("rdf for %s \n%s".format(uri, modelToString(m)))
             instream.close();
           } catch {
             case e => {
               logger.error( "could not parse RDF for " + uri +
                                  " status: " + response.getStatusLine() + " "
                                  + entity.getContentType(),
                            e)
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
//logger.debug( scala.io.Source.fromInputStream(instream).getLines().mkString("\n") )

  def modelToString(model: Model):String = {
    val out = new ByteArrayOutputStream()
    model.write(out, "N3-PP")
    out.toString("UTF-8")
  }
}

