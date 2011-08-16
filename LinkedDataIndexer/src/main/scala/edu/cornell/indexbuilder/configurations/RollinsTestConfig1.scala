package edu.cornell.indexbuilder.configurations
import akka.actor.ActorRef
import edu.cornell.indexbuilder.DiscoverAndIndex
import edu.cornell.indexbuilder.VivoDiscoverAndIndex

import edu.cornell.indexbuilder._
import akka.actor.{Actor, PoisonPill}
import Actor._
import akka.routing.{Routing, CyclicIterator}
import Routing._
import akka.event.EventHandler
import edu.cornell.indexbuilder.discovery.FaultTestDiscoveryWorker
import edu.cornell.indexbuilder.discovery.VivoUriDiscoveryWorker
import edu.cornell.indexbuilder.http.Http
import edu.cornell.indexbuilder.indexing.SelectorGenerator
import edu.cornell.indexbuilder.indexing.SolrIndexWorker
import org.apache.solr.client.solrj.SolrServer
import edu.cornell.indexbuilder.configurations.RollinsConfig._

object RollinsConfig{
  val siteUrl = "http://vivo.cornell.edu"    
  val siteName = "Cornell University"

  val solrUrl = "http://rollins.mannlib.cornell.edu:8080/devIndexUnstable/core3"
  val solrServer = SolrIndexWorker.makeSolrServer( solrUrl )
}

object RollinsTestConfigPostDoc {
  def main(args : Array[String]) : Unit = {

    val classUris = List( """http://vivoweb.org/ontology/core#Postdoc"""  )

    val process = 
      new VivoDiscoverAndIndex(
        siteUrl,siteName,
        solrUrl,
        classUris, 
        VitroVersion.r1dot2)

    process.run()
  }
}

object RollinsTestFaultDiscovery {
  def main(args : Array[String]) : Unit = {

    val classUris = List( """http://vivoweb.org/ontology/core#Librarian"""  )

    val process = 
      new VivoDiscoverAndIndex(
        siteUrl,siteName,
        solrUrl,
        classUris, 
        VitroVersion.r1dot2){
        
        override def configMaster( 
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
            ){
              override def makeRdfLinkedDataWorker( http:Http, skipUrl:String => Boolean ):ActorRef = {
                val numberOfWorkers = 10
                var workers = List[ActorRef]()
                for( i <- 0 until numberOfWorkers ) {
                  val rdfWorker = Actor.actorOf( new FaultTestDiscoveryWorker() )
                  self.link(rdfWorker)
                  rdfWorker.start()
                  workers = rdfWorker :: workers 
                }
                loadBalancerActor( new CyclicIterator[ActorRef](workers) )
              }
            }
          )
        }          
      }
      

    process.run()
  }
}

object RollinsTestConfigLibrarian {
  def main(args : Array[String]) : Unit = {

    val classUris = List( """http://vivoweb.org/ontology/core#Librarian"""  )

    val process = 
      new VivoDiscoverAndIndex(
        siteUrl,siteName,
        solrUrl,
        classUris, 
        VitroVersion.r1dot2)

    process.run()
  }
}

object RollinsTestConfig2 {
  def main(args : Array[String]) : Unit = {

    val classUris = List( 
      """http://vivo.library.cornell.edu/ns/0.1#CornellAffiliatedPerson""",
      """http://vivoweb.org/ontology/core#FacultyMember""",
      """http://vivoweb.org/ontology/core#NonFacultyAcademic""",
      
      """http://xmlns.com/foaf/0.1/Organization""",
      """http://vivo.library.cornell.edu/ns/0.1#OrganizedEndeavor"""
    )

    val process = 
      new VivoDiscoverAndIndex(
        siteUrl,siteName,
        solrUrl,
        classUris, 
        VitroVersion.r1dot2)

    process.run()
  }


}

