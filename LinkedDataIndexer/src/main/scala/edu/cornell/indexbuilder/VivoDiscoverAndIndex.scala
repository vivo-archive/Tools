package edu.cornell.indexbuilder

import akka.actor.Actor._
import akka.actor.Actor
import akka.actor.ActorRef
import akka.routing.Routing._
import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.ontology.OntModel
import com.weiglewilczek.slf4s.Logging
import edu.cornell.indexbuilder.VitroVersion._
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
class VivoDiscoverAndIndex(
  siteUrl:String,
  siteName:String,
  solrUrl:String,
  classUris:List[String],
  siteVersion:VitroVersion )
extends DiscoverAndIndex(siteUrl,siteName,solrUrl)
with Logging {


  override def configSelectorGenerator( siteUrl:String ):SelectorGenerator = {
    new SelectorGeneratorForVivo( siteUrl )
  }
 
  override def configDiscoveryWorker():ActorRef ={
   val actionName =
    if(r1dot2 == siteVersion ){
      logger.debug("server expected to be 1.2 or earlier VIVO version")
      VivoUriDiscoveryWorker.rel12actionName
    }else{
      logger.debug("server expected to be 1.3 or later VIVO version")
      VivoUriDiscoveryWorker.rel13actionName 
    }

    logger.debug("discoery worker setup with classUris: " + classUris)
    Actor.actorOf(new VivoUriDiscoveryWorker(classUris, actionName, "working dir is not in use"))  
  }


  override def configSkipUris( selector:SelectorGenerator ): String=>Boolean = {
    selector match {
      case sel:SelectorGeneratorForVivo => {
        
        val uriSet = new HashSet[String]() 
        val iter = SelectorGeneratorForVivo.sharedFullModel.listIndividuals()
        while( iter.hasNext() ){
          uriSet.add( iter.next().getURI())
        }    

        //return function
        (uri:String)=>{
          ( uri == null ) ||
          ( !uri.startsWith( siteUrl ) ) ||
          ( uriSet.contains( uri ))
        }
        
      }
      case _ => {
        logger.error("VivoDiscoverAndIndex must be configured with a SelectorGeneratorForVivo")
        //return a function that skipps all uris when misconfigured
        _=>true
      }
    }
  }  
  
}

