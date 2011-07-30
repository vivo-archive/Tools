package edu.cornell.indexbuilder

import edu.cornell.indexbuilder._
import akka.actor.{Actor, PoisonPill}
import Actor._
import akka.routing.{Routing, CyclicIterator}
import Routing._
import akka.event.EventHandler
import org.apache.solr.client.solrj.SolrServer

object UserTestingConfig{
  val siteToIndex = "http://usertesting.mannlib.cornell.edu/Rel-1.3"    

  //parameters for solr index
  val solrHost = "rollins.mannlib.cornell.edu"
  val solrPort = "8080"
  val solrContext = "devIndexUnstable"
  val solrUrl = "http://" + solrHost + ":" + solrPort+ "/" + solrContext 
  val solrServer = SolrIndexWorker.makeSolrServer( solrUrl )

  //setup OntModelSelector generator
  val selectorGen = new SelectorGeneratorForVivo( siteToIndex )

}

object UserTestingConfig1 {
  def main(args : Array[String]) : Unit = {

    //setup URI discovery 
    val classUris = List( """http://vivoweb.org/ontology/core#FacultyMember"""  )
    val uriDiscoveryWorker = Actor.actorOf(new VivoUriDiscoveryWorker(classUris,VivoUriDiscoveryWorker.rel13actionName))

    //Setup and start a master server to coordinate the work
    val master = Actor.actorOf( 
      new MasterWorker( UserTestingConfig.siteToIndex , uriDiscoveryWorker, UserTestingConfig.solrServer, UserTestingConfig.selectorGen ) ) 

    master.start()    
    master ! GetUrlsToIndexForSite( UserTestingConfig.siteToIndex )     
  }
}

