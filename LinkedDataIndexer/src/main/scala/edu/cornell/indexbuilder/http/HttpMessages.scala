package edu.cornell.indexbuilder.http

import org.apache.solr.common.SolrInputDocument
import com.hp.hpl.jena.rdf.model.Model
import org.apache.http.client.ResponseHandler
import org.apache.http.HttpResponse

/**
 * These are the messages used by the indexing system.
 */

trait HttpMessages



/* *********** HTTP Messages **************** */
/**
 * Requests that a URL be retreived.
 * Asynchronous, handler must do work with result
 */
case class HttpGetAndProcess( url:String, handler: ResponseHandler[Unit] ) extends HttpMessages

/**
 * Requests that a URI be retreived.
 * Asynchronous, sends reply of type ???.
 */
case class HttpLinkedDataGet ( uri:String, handler: (String,Model)=>Unit ) extends HttpMessages

/**
 * Syncrhonous HTTP GET, replys with HttpResponse.  Note that syncrhonous calls to a
 * HttpClient or HttpWorker that is also taking asynchronous calls will not be handled
 * fairly. The asynchronous calls can hog all the resources and starve the syncrhonous calls.
 */
//case class HttpGet( url:String  ) extends HttpMessages

case class HttpLinkedDataGetSync( uri: String ) extends HttpMessages 

/**
 * Indicate that a URI could not be retreived from a siteUrl.
 */
case class CouldNotGetData( siteUrl:String, uri:String, message:String ) extends HttpMessages
