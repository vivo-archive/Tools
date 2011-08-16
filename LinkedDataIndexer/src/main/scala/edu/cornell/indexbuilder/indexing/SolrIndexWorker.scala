package edu.cornell.indexbuilder.indexing

import akka.actor.Actor
import java.io.IOException
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.common.SolrDocument
import org.apache.solr.common.SolrInputDocument
import com.weiglewilczek.slf4s.Logging
import org.apache.solr.common.SolrException
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer

/**
 * Worker to add a document to a solr index.
 */
class SolrIndexWorker( solrServer: SolrServer ) extends Actor with Logging{
  var indexedCount = 0
  val COMMIT_SIZE = 1000

  checkServer(solrServer)
  
  def receive = {    

    // Add document to solr index.
    case IndexDoc( siteUrl, uri, doc ) => {
      logger.debug("received message to IndexDoc for " + uri)
      try{
        val addResp = solrServer.add( doc )
        indexedCount = indexedCount + 1
        logger.debug( "added doc for " + uri + " resp " + addResp )
        self reply IndexedDoc( siteUrl, uri )
      }catch{
        case ex => {
          logger.error("Could not index document for "+uri+" " + ex)
          self reply CouldNotIndexDoc(siteUrl,uri)
        }
      }

      if( indexedCount % COMMIT_SIZE == 0 ){
        val commitResp = solrServer.commit()
        logger.debug( "commit resp " + commitResp)
      }


    }
    
    case Commit => {
      val commitResp = solrServer.commit()
      logger.debug( "commit resp " + commitResp)
      self reply "done"
    }

    case msg => 
      logger.debug( "SolrIndexWorker received mystery message " + msg);
  }

  def checkServer(solrServer:SolrServer) = {
    try{
      val resp = solrServer.ping()
      logger.debug( "Ping SolrServer: " + resp )
    } catch {
      case solrEx:SolrException => {
        logger.error( "Could not ping SolrServer, code: " + solrEx.code() + " " + solrEx.getMessage() )
        System.exit(1)
      }
      case e => {
        logger.error( "Could not ping SolrServer: " + e )
        System.exit(1)
      }
    }
  }

  //callback method for restart handling 
  override def preRestart(reason: Throwable){
    logger.trace("Restaring after shutdown because of " + reason)
  }

  //callback method for restart handling 
  override def postRestart(reason: Throwable){
    logger.trace("Restaring after shutdown because of " + reason)
  }
}


/* *************** Companion object for SolrIndexWorker ***************** */

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
