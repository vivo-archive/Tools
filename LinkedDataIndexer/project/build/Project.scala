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

    // slf4j implementation 
    val logback_core = "ch.qos.logback" % "logback-core" % "0.9.29"
    val logback_classic = "ch.qos.logback" % "logback-classic" % "0.9.29" 

    val slf4s = "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.6"  
}
