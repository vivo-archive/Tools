package edu.cornell.indexbuilder

import akka.actor.Actor._
import akka.actor.Actor
import akka.actor.ActorRef
import akka.routing.Routing._
import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.util.FileManager
import com.weiglewilczek.slf4s.Logging
import edu.cornell.indexbuilder.VitroVersion._
import edu.cornell.indexbuilder.http.Http
import edu.cornell.indexbuilder.http.Http
import edu.cornell.indexbuilder.indexing._
import edu.cornell.indexbuilder.discovery._
import org.apache.solr.client.solrj.SolrServer
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set
import scala.collection.JavaConversions

/*
 * This represents a basic process to run a site index.
 * Individuals discovered on the site specified by siteUrl
 * will be indexed to the service indicated by solrUrl.
 *
 * siteUrl - URL of site to indes ex "http://vivo.exampe.edu" No / at end
 * solrUrl - URL of solr sevice ex "http://solr.example.edu:8080/solr"
 * classUris - List of URIs of classes to index from siteUrl
 * siteVersion - Should be either "1.2" or "1.3" to indicate
 * which version of the vivo software is running.
 */
class CatalystDiscoverAndIndex(
  siteUrl:String, 
  siteName:String, 
  solrUrl:String, 
  classes:List[String], 
  rdfFile:String) 
extends DiscoverAndIndex( siteUrl, siteName, solrUrl ) 
with Logging {

  override def configSelectorGenerator( siteUrl:String ):SelectorGenerator = {
    new SelectorGeneratorForVivo(siteUrl)
  }

 
  override def configDiscoveryWorker():ActorRef ={
    var in = FileManager.get().open( rdfFile );
    if (in == null) {
      throw new IllegalArgumentException(
        "File: " + rdfFile + " not found");
    }
        
    Actor.actorOf(new RdfFileDiscovery( in ,"noDir"))
  }

  //Returns a function that checks if a uri should be skipped.
  override def configSkipUris( selGen:SelectorGenerator ): String=>Boolean = {
    //return function
    (uri:String)=>{
      ( uri == null ) ||
      ( !uri.startsWith( siteUrl ) ) 
    }
  }  


  override def configHttp():Http={
    //setup an Http that will try to convert text/html to rdf/xml
    //because harvard returns text/html for rdf/xml calls
    new Http(10){
  override val HEADER_TO_JENASTR =
    Map( "text/n3" -> "N3",
         "text/rdf+n3" -> "N3",
         "application/rdf+xml" -> "RDF/XML",
         "text/turtle" -> "TURTLE",
        "text/html" -> "RDF/XML" )
    }

  }
}

