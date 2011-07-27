import sbt._

class LinkedDataIndexBuilderProject(info: ProjectInfo) 
extends DefaultProject(info) 
with AkkaProject{

    //for some sbt dependency help, 
    //see http://scalateral.blogspot.com/2010/11/day-3-sbt-dependencies-and-scala.html  

    //scala-io from scalax
    val ioCore = "com.github.scala-incubator.io" %% "core" % "0.1.1"
    val ioFile = "com.github.scala-incubator.io" %% "file" % "0.1.1"
 
    val scalatest = "org.scalatest" %% "scalatest" % "1.4.1"
    
  

    //This is how to import java jars for solrj and its dependencies
    val solrJ = "org.apache.solr" % "solr-solrj" % "3.1.0"

    //adding this to deal with static linking of slf4j?
    val slf4j = "org.slf4j" % "slf4j-simple" % "1.5.6"
}
