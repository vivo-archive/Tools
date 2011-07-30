package edu.cornell.indexbuilder

import edu.cornell.indexbuilder._
import akka.actor.{Actor, PoisonPill}
import Actor._
import akka.routing.{Routing, CyclicIterator}
import Routing._
import akka.event.EventHandler
import org.apache.solr.client.solrj.SolrServer

object BuildIndex {
  def main(args : Array[String]) : Unit = {

    //url of VIVO site to index
    val siteUrl = "http://vivo.cornell.edu"    

    //parameters for solr index
    val solrHost = "localhost"
    val solrPort = "8080"
    val solrContext = "devIndex"
    //If we needed a custom configuration of the SolrServer we could do that
    val solrServer = SolrIndexWorker.makeSolrServer( "http://" + solrHost + ":" + solrPort+ "/" + solrContext )

    //setup OntModelSelector generator
    val selectorGen = new SelectorGeneratorForVivo(siteUrl)


      val classUris = List( 
//        """http://vivoweb.org/ontology/core#NonFacultyAcademic"""
        """http://vivoweb.org/ontology/core#Postdoc"""
//        """http://vivoweb.org/ontology/core#Librarian"""
//,          """http://vivoweb.org/ontology/core#FacultyMember"""
      )


    val uriDiscoveryWorker = Actor.actorOf(
        new VivoUriDiscoveryWorker(classUris, VivoUriDiscoveryWorker.rel12actionName))

    //Setup and start a master server to coordinate the work
    val master = Actor.actorOf( 
      new MasterWorker( siteUrl, uriDiscoveryWorker, solrServer, selectorGen ) ) 

    master.start()    
    master ! GetUrlsToIndexForSite( siteUrl )     
  }
  
}
