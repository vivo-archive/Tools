package edu.cornell.indexbuilder

import akka.actor.Actor._
import akka.actor.Actor
import akka.actor.ActorRef
import akka.routing.Routing._
import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.ontology.OntModel
import com.weiglewilczek.slf4s.Logging
import edu.cornell.indexbuilder.VitroVersion._
import edu.cornell.indexbuilder.http.Http
import edu.cornell.indexbuilder.indexing._
import edu.cornell.indexbuilder.discovery._
import org.apache.solr.client.solrj.SolrServer
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set
import scala.collection.JavaConversions

/*
 * This represents a basic process to run a site index.
 * Individuals discovered on the site specified by siteUrl
 * will be indexed to the service indicated by solrUrl.
 *
 * siteUrl - URL of site to indes ex "http://vivo.exampe.edu" No / at end
 * solrUrl - URL of solr sevice ex "http://solr.example.edu:8080/solr"
 * classUris - List of URIs of classes to index from siteUrl
 * siteVersion - Should be either "1.2" or "1.3" to indicate
 * which version of the vivo software is running.
 */
abstract class DiscoverAndIndex(
  siteUrl:String,
  siteName:String,
  solrUrl:String
) extends Object with Logging {

  // concrete method that combines the configured parts
  def run (){
    //setup http client
    val http = configHttp()
    
    //setup connection to solr server
    val solrServer = SolrIndexWorker.makeSolrServer( solrUrl )
    
    //setup URI discovery
    val uriDiscoveryWorker = configDiscoveryWorker(  )
  
    val selectorGen = new SelectorGeneratorForVivo(siteUrl);

    val skip = configSkipUris( selectorGen );

    //Setup and start a master server to coordinate the work
    val master = configMaster(
        siteUrl, 
        siteName,
        uriDiscoveryWorker, 
        solrServer, 
        selectorGen,
        skip,
        http
      )

    master.start()
    master ! DiscoverUrisForSite( siteUrl )
    logger.debug("master created, started, and discovery for site requested")
  }


  //returns an object that configures the OntModelSelector for a site
  def configSelectorGenerator( siteUrl:String ):SelectorGenerator 

  //returns a configured discovery worker
  def configDiscoveryWorker():ActorRef 

  //Returns a function that checks if a uri should be skipped.
  def configSkipUris( selectorGen:SelectorGenerator ):String => Boolean

  //returns a configured Master worker,usually this doesn't need to 
  // be overridden
  def configMaster( 
    siteUrl:String, 
    siteName:String, 
    discoveryWorker:ActorRef, 
    solrServer:SolrServer, 
    selectorGen:SelectorGenerator, 
    skipUris:String=>Boolean, 
    http:Http):ActorRef = {

    Actor.actorOf( 
      new MasterWorker( 
        siteUrl, 
        siteName,
        discoveryWorker, 
        solrServer, 
        selectorGen,
        configSkipUris( selectorGen ),
        http
      ))
  }

  // return a configured Http object
  // usually this doesn't need to be overrridden
  def configHttp():Http={
    val maxConn = 10
    new Http(maxConn)
  }
  
}

