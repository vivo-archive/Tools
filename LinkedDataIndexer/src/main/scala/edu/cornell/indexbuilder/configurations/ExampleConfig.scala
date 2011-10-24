package edu.cornell.indexbuilder.configurations

import edu.cornell.indexbuilder.VitroVersion
import edu.cornell.indexbuilder.VivoDiscoverAndIndex
import edu.cornell.indexbuilder.configurations.ExampleConfig._

object ExampleConfig{
  val siteName = "Weill Cornell Medical College"
  val siteUrl = "http://vivo.med.cornell.edu"    
  val siteVivoVersion = VitroVersion.r1dot2

  val solrUrl = "http://exapleSolrServer.example.edu:8080/solr"
  val classUris = List( """http://vivoweb.org/ontology/core#Postdoc""" )
}

class ExampleConfig {
  def main(args : Array[String]) : Unit = {
    val process = 
      new VivoDiscoverAndIndex(
        siteUrl,
        siteName,
        solrUrl,
        classUris, 
        siteVivoVersion
      )
    process.run()
  }
}



