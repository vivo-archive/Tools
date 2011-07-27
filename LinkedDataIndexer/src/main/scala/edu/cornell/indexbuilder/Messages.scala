package edu.cornell.indexbuilder

import edu.cornell.indexbuilder._
import org.apache.solr.common.SolrInputDocument
import com.hp.hpl.jena.rdf.model.Model
import org.apache.http.client.ResponseHandler
import org.apache.http.HttpResponse

/**
 * These are the messages used by the indexing system.
 */

trait IndexBuilderMessage 

/*
 * Ask for a site to be indexed.
 * Asynchronous. Worker will send out DiscoverUrisForClass messages.
 * siteBaseUrl should not end with a /
 */
case class GetUrlsToIndexForSite ( siteBaseUrl : String) extends IndexBuilderMessage

/*
 * Ask for a site to have its individuals of vclass indexed.
 * Asynchronous. Worker will send out SiteIndex messages.
 */
case class DiscoverUrisForClass( siteBaseUrl:String, classUri:String) extends IndexBuilderMessage

/**
 * Indicate that the URI discovery phase is complete for the site.
 */
case class DiscoveryComplete( siteBaseUrl:String ) extends IndexBuilderMessage

/**
 * Requests that URIs be indexed for a site.
 * siteBaseUrl should not end with a /
 */
case class IndexUris ( 
  siteBaseUrl : String, 
  uris: List[String], 
  classUri:String, 
  page:Int, 
  totalPages:Int) 
  extends IndexBuilderMessage


/**
 * Indicates that the RDF should be retreived for the URI
 */
case class GetRdf (uri:String) extends IndexBuilderMessage
case class GotRdf (uri:String, model:Model) extends IndexBuilderMessage 

case class RdfToDoc(suteUrl: String, uri:String, model:Model) extends IndexBuilderMessage

case class GotDoc (sitUrl: String, uri: String, doc: SolrInputDocument) extends IndexBuilderMessage
case class IndexDoc (siteUrl: String, uri: String, doc: SolrInputDocument) extends IndexBuilderMessage

/**
 * Indicates that a document has been added to the solr index
 */
case class IndexedDoc( siteUrl:String, uri:String) extends IndexBuilderMessage


/* *********** HTTP Messages **************** */

/**
 * Requests that a URL be retreived.
 * Asynchronous, handler must do work with result
 */
case class HttpGetAndProcess( url:String, handler: ResponseHandler[Unit] ) extends IndexBuilderMessage

/**
 * Requests that a URI be retreived.
 * Asynchronous, sends reply of type ???.
 */
case class HttpLinkedDataGet ( uri:String, handler: (String,Model)=>Unit ) extends IndexBuilderMessage

/**
 * Syncrhonous HTTP GET, replys with HttpResponse.  Note that syncrhonous calls to a
 * HttpClient or HttpWorker that is also taking asynchronous calls will not be handled
 * fairly. The asynchronous calls can hog all the resources and starve the syncrhonous calls.
 */
//case class HttpGet( url:String  ) extends IndexBuilderMessage

case class HttpLinkedDataGetSync( uri: String ) extends IndexBuilderMessage 
