package edu.cornell.indexbuilder.http


import com.hp.hpl.jena.rdf.model.{Model, ModelFactory}
import com.weiglewilczek.slf4s.Logging

import org.apache.http._
import org.apache.http.client._
import org.apache.http.client.methods._
import org.apache.http.client.ResponseHandler
import org.apache.http.impl.client._
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils

/**
 * A class to use apache commons HttpClient. This is an
 * attempt to move away from a actor based HttpClient.
 * 
 * Redirects are handled by HttpClient.
 * 
 * TODO: cases that need to be handled
 * connection refused
 * timeouts
 */

class Http( connections:Int ) extends Object with Logging {

  /* Setup Apache HttpClient */
  val cm = new ThreadSafeClientConnManager()
  cm.setMaxTotal(connections)
  cm.setDefaultMaxPerRoute(connections)
  val httpClient: HttpClient = new DefaultHttpClient(cm)
  
  def getAndProcess[R](url:String , process: Function1[HttpResponse,R]):R = {
    logger.trace("getAndProcess %s".format(url))
    val response = httpClient.execute( new HttpGet(url) )    
    try{
      process.apply( response )
    }catch {
      case e => throw new Exception("could not get and process request for " + url + " " + e.getMessage() )      
    }finally{
      close( response )
    }
  } 
  
  def getLinkedDataAndProcess[R]( uri:String, process: Function2[HttpResponse,Model,R] ):R = {
    logger.trace("getLinkedDataAndProcess %s".format(uri))
    val get = new HttpGet(uri)
    get.setHeader("Accept", RDF_ACCEPT_HEADER)
    var resp = httpClient.execute( get )
    try{
      process( resp, responseToModel(uri,resp))    
    }catch {
      case e => throw new Exception("could not get and process LD request for " + uri + " " + e.getMessage() )      
    }finally{
      close( resp )
    }
  }

  def getLinkedData( uri:String ):Option[Model] = {
    logger.trace("getLinkedData %s".format(uri))
    val get = new HttpGet(uri)
    get.setHeader("Accept", RDF_ACCEPT_HEADER)
    var resp = httpClient.execute( get )
    try{
      Some(responseToModel(uri,resp))
    }catch {
      case e =>{
        logger.debug("could not get LD request for " + uri + " " + e.getMessage() )
        None
      }
    }finally{
      close( resp )
    }
  }


 def responseToModel(uri: String, response: HttpResponse): Model = {
   logger.trace( "got response %s for %s".format(response, uri))

   if( response != null && 
      response.getStatusLine() != null &&
      response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        
        throw new Exception("could not get HTTP for " + uri + 
                            " status: " + response.getStatusLine() )
      } 
   
   val m = ModelFactory.createDefaultModel()
   val entity = response.getEntity();
   if (entity == null) {
     throw new Exception("could not get HttpEntity for " + uri + 
                         " status: " + response.getStatusLine() )
   }else{
     //Now we really try to get the content
     val instream = entity.getContent()
     try {
       m.read(instream, "", getRDFType( entity ))
       instream.close();
     } catch {
       case e => 
         throw new Exception("Could not parse RDF for " + uri +
                             " status: " + response.getStatusLine() + " "
                             + entity.getContentType() + " "+ e.getMessage() )
     }
   }
   return m
  }

  def getRDFType( entity:HttpEntity):String = {
    if( entity != null && 
        entity.getContentType() != null && 
        entity.getContentType().getValue() != null )
      {
        //strip charset=UTF-8 part, it gets ignored right now
        var contentType = entity.getContentType().getValue()
        if( contentType.contains(';') ){
          contentType = contentType.substring(0,contentType.indexOf(';'))
        }

        HEADER_TO_JENASTR.get( contentType ) match {
          case Some(jenaFormat) => jenaFormat
          case None => {
            throw new Exception("Could not identifiy content type \""+entity.getContentType().getValue()+"\"")
          }
        }

      }else{
        throw new Exception("Could not identifiy content type \""+entity.getContentType().getValue()+"\"")
      }
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

  def close(  response:HttpResponse ) {
    try{
      if( response != null){
        val entity = response.getEntity()
        if( entity != null ){
          EntityUtils.consume(entity)
        }
      }
    }catch{
      case e => logger.error("could not close entity",e)
    }
  }

}

