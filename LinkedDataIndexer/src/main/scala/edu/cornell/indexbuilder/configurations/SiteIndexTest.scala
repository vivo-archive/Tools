package edu.cornell.indexbuilder

/*
 * This is a test of configurations to index
 * from the partener sites to a test server.
 */
object SiteIndexTest {
  val classUris = List( 
    """http://vivoweb.org/ontology/core#Postdoc""",
    """http://vivoweb.org/ontology/core#Librarian"""
  )
  
  val solrUrl = "http://rolins.mannlib.cornell.edu:8080/devIndexUnstable"    
}


object TestIndexCornell {
  val siteUrl = "http://vivo.cornell.edu"
  val siteVivoVersion="1.2"

  def main(args : Array[String]) : Unit = {
    val process = new IndexProcessConfiguration(
      siteUrl,
      SiteIndexTest.solrUrl,
      SiteIndexTest.classUris,
      siteVivoVersion
    )
    process.run()
  }
}

object TestIndexIndiana {
  val siteUrl = "http://vivo.iu.edu"
  val siteVivoVersion="1.2"

  def main(args : Array[String]) : Unit = {
    val process = new IndexProcessConfiguration(
      siteUrl,
      SiteIndexTest.solrUrl,
      SiteIndexTest.classUris,
      siteVivoVersion
    )
    process.run()
  }
}

object TestIndexPonce {
  val siteUrl = "http://vivo.psm.edu/"
  val siteVivoVersion="1.2"

  def main(args : Array[String]) : Unit = {
    val process = new IndexProcessConfiguration(
      siteUrl,
      SiteIndexTest.solrUrl,
      SiteIndexTest.classUris,
      siteVivoVersion
    )
    process.run()
  }
}

object TestIndexScripps {
  val siteUrl = "http://vivo.scripps.edu/"
  val siteVivoVersion="1.2"

  def main(args : Array[String]) : Unit = {
    val process = new IndexProcessConfiguration(
      siteUrl,
      SiteIndexTest.solrUrl,
      SiteIndexTest.classUris,
      siteVivoVersion
    )
    process.run()
  }
}

object TestIndexUFL {
  val siteUrl = "http://vivo.ufl.edu/"
  val siteVivoVersion="1.2"

  def main(args : Array[String]) : Unit = {
    val process = new IndexProcessConfiguration(
      siteUrl,
      SiteIndexTest.solrUrl,
      SiteIndexTest.classUris,
      siteVivoVersion
    )
    process.run()
  }
}

object TestIndexWustl {
  val siteUrl = "http://vivo.wustl.edu/"
  val siteVivoVersion="1.2"

  def main(args : Array[String]) : Unit = {
    val process = new IndexProcessConfiguration(
      siteUrl,
      SiteIndexTest.solrUrl,
      SiteIndexTest.classUris,
      siteVivoVersion
    )
    process.run()
  }
}

object TestIndexWeillMed {
  val siteUrl = "http://vivo.med.cornell.edu/"
  val siteVivoVersion="1.2"

  def main(args : Array[String]) : Unit = {
    val process = new IndexProcessConfiguration(
      siteUrl,
      SiteIndexTest.solrUrl,
      SiteIndexTest.classUris,
      siteVivoVersion
    )
    process.run()
  }
}
