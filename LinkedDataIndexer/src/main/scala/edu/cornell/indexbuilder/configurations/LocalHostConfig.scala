package edu.cornell.indexbuilder.configurations
import edu.cornell.indexbuilder.IndexProcess

/*
 * A configuration that indexes some classes from
 * a 1.3 VIVO running on localhost:8080
 * to a solr at http://localhost:8080/multiSiteIndex .
 */
object LocalHost {

  def main(args : Array[String]) : Unit = {

    val classUris = List( 
      """http://vivoweb.org/ontology/core#NonFacultyAcademic""",
      """http://vivoweb.org/ontology/core#Postdoc""",
      """http://vivoweb.org/ontology/core#Librarian""",
      """http://vivoweb.org/ontology/core#FacultyMember"""
    )

    val process = new IndexProcess( 
      "http://localhost:8080/vivo",
      "http://localhost:8080/multiSiteIndex",
      classUris,
      "1.3"
    )

    process.run()
  }
}
