package edu.cornell.indexbuilder.indexing

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.shared.Lock
import com.weiglewilczek.slf4s.Logging
import edu.cornell.indexbuilder.http.CouldNotGetData
import edu.cornell.mannlib.vitro.indexbuilder.UrisForDataExpansion
import java.io.ByteArrayOutputStream
import org.apache.http.HttpResponse

import edu.cornell.indexbuilder._
import scala.actors.Future
import scala.actors.Futures
import scala.actors.Futures._
import edu.cornell.indexbuilder.http.Http

import scala.collection.mutable.HashSet
import scala.collection.JavaConversions._
import akka.actor.Actor
import Actor._


/**
 * Actor that gets RDF for URIs.
 * TODO: what to do about expanding the RDF to handle context nodes?
 * 
 * TODO: what to do about external URIs such as WCMC in vivo.cornell.edu?
 * 
 * ex.
 *   @prefix vitro: "http://vitro.mannlib.cornell.edu/ns/vitro/public#" .
 *   person vitro:mainImage img .
     img vitro:thumbnailImage thumbImg .
     thumbImg vitro:downloadLocation locationUrl .
 */
class RdfLinkedDataWorker ( http:Http, skipUri: String=>Boolean ) 
extends Actor with Logging {  

  val expand = 
    new UrisForDataExpansion( 
      UrisForDataExpansion.getVivoTwoHopPredicates(), 
      UrisForDataExpansion.getDefaultSkippedPredicates(),
      UrisForDataExpansion.getDefaultSkippedResourceNS()
    )
  
  def receive = {

    case GetRdf( siteUrl, uri ) => {
      if(  innerSkipUri( uri ) ){
        logger.debug("Not getting RDF for " + uri )
        self reply CouldNotGetData( siteUrl, uri , "Url is to be skipped")
      }

      logger.debug("Attempting to get RDF for URI " + uri )    
      val res = http.getLinkedData(uri) 

      res match {      
        case Some(model:Model) => {
          logger.debug("got rdf for URI " + uri )
          logger.trace("RDF for " +uri+ ": " + modelToString(model))

          val expandedModel = expandData( uri, model )
          MasterWorker.getMaster() ! GotRdf(siteUrl, uri, expandedModel)
        }
        case None =>{
          logger.debug("Could not get RDF for URI " + uri )    
          self reply CouldNotGetData( siteUrl, uri, 
           "Could not get RDF for URI %s timeout or other HTTP problem".format(uri)  )
        }
      }
    }

    case e => {
      logger.error("received unhandled message: " + e)
    }

  }


  def expandData( uri: String, model: Model): Model = {
    //get list of additional URIs to get linked data for
    val map = expand.getUris( uri, model );    
    var oneHop = asScalaBuffer(map.get("oneHop"))
    var twoHop = asScalaBuffer(map.get("twoHop"))
    
    logger.trace("oneHop expansion list for URI %s: %s".format(uri,oneHop))
    logger.trace("twoHop expansion list for URI %s: %s".format(uri,twoHop))

    oneHop = oneHop.filterNot( innerSkipUri )
    twoHop = twoHop.filterNot( innerSkipUri )

    val modelExp = singleHopExpansion(oneHop ++ twoHop, model)    
    logger.trace("model for %s after one hop expansion: %s"
                 .format( uri, SolrDocWorker.modelToString(modelExp)))
    val modelExp2 = secondHopExpansion( uri, twoHop, modelExp)
    logger.trace("model for %s after two hop expansion: %s"
                 .format( uri, SolrDocWorker.modelToString(modelExp)))
    modelExp2
  }

  def singleHopExpansion( urisToExpand:Seq[String], model:Model):Model = {
    //make a list of results for those URI to their RDF
    val expModels: Seq[(String,Option[Model])] = 
      urisToExpand.map( (uri) => (uri, http.getLinkedData(uri) ) ) 

    // add up all the models that are not None
    // it may be more efficient to do this as a union
    val f = (acc:Model,opt:(String,Option[Model]) ) => opt match { 
      case (_,Some(m)) => acc.add(m) 
      case (uri,None) => {
        logger.debug("No RDF found for "+uri+" during expansion")
        acc
      }
    }

    expModels.foldLeft(model)(f)
  }

  def secondHopExpansion( uri:String, twoHop:Seq[String],model:Model):Model = {
    //each of the two hop URIs need to be expanded one more hop
    val urisFor2ndHop = twoHop.flatMap( getUrisFor2ndExpand(model) )
      .distinct.filterNot( innerSkipUri )
    logger.trace("twoHop 2nd expansion list for URI %s: %s".format(uri,urisFor2ndHop))

    singleHopExpansion( urisFor2ndHop, model)
  }

  def getUrisFor2ndExpand(model:Model)(uri2ndHop:String):Seq[String] = {
    asScalaBuffer( expand.getSingleHopUris( uri2ndHop, model) )    
  }

  //take two models and add them together
  def addModels (acc: Model, i: Model) :Model = {
    acc.enterCriticalSection(Lock.WRITE)
    try{
      acc.add(i)
    }finally{
      acc.leaveCriticalSection()
    }
    acc
  }

  def doneExpanding( urisToExpand:HashSet[String], urisExpanded:HashSet[String] ):Boolean = {
    urisExpanded.synchronized{
      return urisExpanded.size == urisToExpand.size
    }      
  }

  def modelToString(model: Model):String = {
    val out = new ByteArrayOutputStream()
    model.write(out, "N3-PP")
    out.toString("UTF-8")
  } 

  def innerSkipUri( uri:String):Boolean={
    skipUri(uri) || notHTTP(uri)  || hashNamespace(uri) || hasDoubleSlash(uri)
  }

  def notHTTP( uri:String):Boolean ={
    //skip the uri if it isn't http protocol
    ( uri == null ) ||
    ( !uri.startsWith("http:") )
  }

  def hashNamespace(uri:String):Boolean={
    (uri != null )&&(uri.contains("#"))
  }

  /* return true if there is a double slash that insn't part of http:// */
  def hasDoubleSlash(uri:String):Boolean={
    (uri.substring(7).contains("//") ) 
  }

  //callback method for restart handling 
  override def preRestart(reason: Throwable){
    logger.trace("Restaring after shutdown because of " + reason)
  }

  //callback method for restart handling 
  override def postRestart(reason: Throwable){
    logger.trace("Restaring after shutdown because of " + reason)
  }
}

