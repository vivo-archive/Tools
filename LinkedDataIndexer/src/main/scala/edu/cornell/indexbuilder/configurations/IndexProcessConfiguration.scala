package edu.cornell.indexbuilder

import edu.cornell.indexbuilder._
import akka.actor.{Actor, PoisonPill}
import Actor._
import akka.routing.{Routing, CyclicIterator}
import Routing._
import akka.event.EventHandler
import org.apache.solr.client.solrj.SolrServer

/*
 * This represents a basic configuration to run a site index.
 * Individuals discovered on the site specified by siteUrl
 * will be indexed to the service indicated by solrUrl.
 *
 * siteUrl - URL of site to indes ex "http://vivo.exampe.edu"
 * solrUrl - URL of solr sevice ex "http://solr.example.edu:8080/solr"
 * classUris - List of URIs of classes to index from siteUrl
 * siteVersion - Should be either "1.2" or "1.3" to indicate
 * which version of the vivo software is running.
 */
class IndexProcessConfiguration(
  siteUrl:String,
  solrUrl:String,
  classUris:List[String],
  siteVersion:String
){

  def run (){
    val actionName =
      if("1.2".equals( siteVersion ))
        VivoUriDiscoveryWorker.rel12actionName
      else
        VivoUriDiscoveryWorker.rel13actionName 

    //setup connection to solr server
    val solrServer = SolrIndexWorker.makeSolrServer( solrUrl )

    //setup URI discovery
    val uriDiscoveryWorker = 
      Actor.actorOf(new VivoUriDiscoveryWorker(classUris, actionName))

    val selectorGen = new SelectorGeneratorForVivo(siteUrl);

    //Setup and start a master server to coordinate the work
    val master = Actor.actorOf( 
      new MasterWorker( 
        siteUrl, 
        uriDiscoveryWorker, 
        solrServer, 
        selectorGen))
    master.start()
    master ! GetUrlsToIndexForSite( siteUrl )
  }

}

