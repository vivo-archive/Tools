package edu.cornell.indexbuilder.configurations
import edu.cornell.indexbuilder.DiscoverAndIndex

import edu.cornell.indexbuilder._
import akka.actor.{Actor, PoisonPill}
import Actor._
import akka.routing.{Routing, CyclicIterator}
import Routing._
import akka.event.EventHandler
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
      new DiscoverAndIndex(
        siteUrl,siteName,
        solrUrl,
        classUris, 
        VitroVersion.r1dot2)

    process.run()
  }
}

object RollinsTestConfigLibrarian {
  def main(args : Array[String]) : Unit = {

    val classUris = List( """http://vivoweb.org/ontology/core#Librarian"""  )

    val process = 
      new DiscoverAndIndex(
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
      new DiscoverAndIndex(
        siteUrl,siteName,
        solrUrl,
        classUris, 
        VitroVersion.r1dot2)

    process.run()
  }
}

