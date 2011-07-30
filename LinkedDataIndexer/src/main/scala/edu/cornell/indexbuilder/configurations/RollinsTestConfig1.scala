package edu.cornell.indexbuilder

import edu.cornell.indexbuilder._
import akka.actor.{Actor, PoisonPill}
import Actor._
import akka.routing.{Routing, CyclicIterator}
import Routing._
import akka.event.EventHandler
import org.apache.solr.client.solrj.SolrServer

object RollinsConfig{
  val siteUrl = "http://vivo.cornell.edu"    

  //parameters for solr index
  val solrHost = "rollins.mannlib.cornell.edu"
  val solrPort = "8080"
  val solrContext = "devIndexUnstable"
  val solrUrl = "http://" + solrHost + ":" + solrPort+ "/" + solrContext 
  val solrServer = SolrIndexWorker.makeSolrServer( solrUrl )

  //setup OntModelSelector generator
  val selectorGen = new SelectorGeneratorForVivo(siteUrl)

}

object RollinsTestConfig1 {
  def main(args : Array[String]) : Unit = {

    //setup URI discovery 
    val classUris = List( """http://vivoweb.org/ontology/core#Postdoc"""  )
    val uriDiscoveryWorker = Actor.actorOf(new VivoUriDiscoveryWorker(classUris,VivoUriDiscoveryWorker.rel12actionName))

    //Setup and start a master server to coordinate the work
    val master = Actor.actorOf( 
      new MasterWorker( RollinsConfig.siteUrl, uriDiscoveryWorker, RollinsConfig.solrServer, RollinsConfig.selectorGen ) ) 

    master.start()    
    master ! GetUrlsToIndexForSite( RollinsConfig.siteUrl )     
  }
}

object RollinsTestConfig2 {
  def main(args : Array[String]) : Unit = {

    //setup URI discovery 
    val classUris = List( 
      """http://vivo.library.cornell.edu/ns/0.1#CornellAffiliatedPerson""",
      """http://vivoweb.org/ontology/core#FacultyMember""",
      """http://vivoweb.org/ontology/core#NonFacultyAcademic""",
      
      """http://xmlns.com/foaf/0.1/Organization""",
      """http://vivo.library.cornell.edu/ns/0.1#OrganizedEndeavor"""
    )
    val uriDiscoveryWorker = Actor.actorOf(new VivoUriDiscoveryWorker(classUris,VivoUriDiscoveryWorker.rel12actionName))

    //Setup and start a master server to coordinate the work
    val master = Actor.actorOf( 
      new MasterWorker( RollinsConfig.siteUrl, uriDiscoveryWorker, RollinsConfig.solrServer, RollinsConfig.selectorGen ) ) 

    master.start()    
    master ! GetUrlsToIndexForSite( RollinsConfig.siteUrl )     
  }
}
