package edu.cornell.indexbuilder.configurations
import com.hp.hpl.jena.util.FileManager
import edu.cornell.indexbuilder.CatalystDiscoverAndIndex
import edu.cornell.indexbuilder.CatalystDiscoverAndIndex
import edu.cornell.indexbuilder.CatalystDiscoverAndIndex

import edu.cornell.indexbuilder.VivoDiscoverAndIndex
import edu.cornell.indexbuilder.VitroVersion
import edu.cornell.indexbuilder.configurations.SiteIndexTest._
import edu.cornell.indexbuilder.discovery.RdfFileDiscovery

/*
 * This is a test of configurations to index
 * from Harvard's Catalyst Profiles system.
 */
object HarvardIndex {

  var classUris = List( 
    """http://vivoweb.org/ontology/core#Postdoc""",
    """http://vivoweb.org/ontology/core#FacultyMember""",
    """http://vivoweb.org/ontology/core#Librarian"""
  ) 

  var uriToName = SiteIndexTest.uriToName
  var solrUrl = SiteIndexTest.solrUrl
}

object TestIndexHarvard{
  val siteUrl = "http://connects.catalyst.harvard.edu"
  val siteName = uriToName( siteUrl )
 
  def main(args : Array[String]) : Unit = {
    var inputFileName = "/home/bdc34/workspace/LinkedDataIndexer/src/main/resources/HarvardFacultyMembers.rdf"

    // // use the FileManager to find the input file
    // var in = FileManager.get().open( inputFileName );
    // if (in == null) {
    //   throw new IllegalArgumentException(
    //     "File: " + inputFileName + " not found");
    // }

    // var uris = RdfFileDiscovery.readUris( in )
    // println("some uris: " + uris.take(10))
   
    var process = new CatalystDiscoverAndIndex( 
      siteUrl, siteName, solrUrl, Nil, inputFileName)
    process.run
  }
}
