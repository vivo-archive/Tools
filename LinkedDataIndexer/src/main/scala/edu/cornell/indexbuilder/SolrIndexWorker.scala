package edu.cornell.indexbuilder

import akka.actor.Actor
import akka.event.EventHandler
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.common.SolrDocument
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.SolrException
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer

/**
 * Worker to add a document to a solr index.
 */
class SolrIndexWorker( solrServer: SolrServer ) extends Actor {
  var indexedCount = 0
  val COMMIT_SIZE = 1000

  checkServer(solrServer)
  
  def receive = {    
    case IndexDoc( siteUrl, uri, doc ) => {
      //add document to solr index.
      val addResp = solrServer.add( doc )
      indexedCount = indexedCount + 1
      EventHandler.debug(this, "added doc for " + uri + " resp " + addResp )

      if( indexedCount % COMMIT_SIZE == 0 ){
        val commitResp = solrServer.commit()
        EventHandler.debug(this, "commit for " + uri + " resp " + commitResp)
      }

      self reply IndexedDoc( siteUrl, uri )
    }
    
    case _ => 
      EventHandler.debug(this, "SolrIndexWorker received mystery message");
  }

  def checkServer(solrServer:SolrServer) = {
    try{
      val resp = solrServer.ping()
      EventHandler.debug(this, "Ping SolrServer: " + resp )
    } catch {
      case solrEx:SolrException => {
        EventHandler.debug(this, "Could not ping SolrServer, code: " + solrEx.code() + " " + solrEx.getMessage() )
        System.exit(1)
      }
      case e => {
        EventHandler.debug(this, "Could not ping SolrServer: " + e )
        System.exit(1)
      }
    }
  }
}

/** Companion object for SolrIndexWorker */
object SolrIndexWorker {

  /**
   * Makes a solr server object for the url.
   */
  def makeSolrServer( url:String ):SolrServer = {
    val  server  = new CommonsHttpSolrServer( url )
    server.setDefaultMaxConnectionsPerHost(10);
    server.setMaxTotalConnections(10);
    //server.setMaxRetries(2);
    server
  }
}
