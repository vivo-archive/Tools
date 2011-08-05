package edu.cornell.indexbuilder.indexing

import org.apache.solr.common.SolrInputDocument
import com.hp.hpl.jena.rdf.model.Model
import org.apache.http.client.ResponseHandler
import org.apache.http.HttpResponse

/**
 * These are the messages used by the indexing system.
 */

trait IndexBuilderMessage 

/**  Requests that URIs be indexed for a site.  siteBaseUrl should not end with a /  */
case class IndexUris ( siteBaseUrl : String, uris: List[String]) extends IndexBuilderMessage

/** Request for RDF to be retreived for the URI */
case class GetRdf (uri:String) extends IndexBuilderMessage

/** Reply with RDF for a URI */
case class GotRdf (uri:String, model:Model) extends IndexBuilderMessage 

/** Request to convert the RDF to a Solr document */
case class RdfToDoc(suteUrl: String, uri:String, model:Model) extends IndexBuilderMessage
/** Reply with Solr document for URI */
case class GotDoc (sitUrl: String, uri: String, doc: SolrInputDocument) extends IndexBuilderMessage

/** Request to have a Solr document indexed on a Solr server */
case class IndexDoc (siteUrl: String, uri: String, doc: SolrInputDocument) extends IndexBuilderMessage

/** Reply that indicates that a document has been added to the solr index  */
case class IndexedDoc( siteUrl:String, uri:String) extends IndexBuilderMessage

/**  Request that changes to an index be commited to a solr index */
case class Commit () extends IndexBuilderMessage
