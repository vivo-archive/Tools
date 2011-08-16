package edu.cornell.indexbuilder.indexing

import akka.actor.Actor
import com.hp.hpl.jena.ontology.OntDocumentManager
import com.hp.hpl.jena.ontology.{OntModel, OntModelSpec}
import com.hp.hpl.jena.rdf.model.{Model, ModelFactory}
import com.hp.hpl.jena.util.FileManager
import com.hp.hpl.jena.util.LocationMapper
import com.weiglewilczek.slf4s.Logging
import edu.cornell.indexbuilder.http.CouldNotGetData
import edu.cornell.mannlib.vitro.webapp.beans.Individual
import edu.cornell.mannlib.vitro.webapp.dao.jena.{OntModelSelector, WebappDaoFactoryJena}
import edu.cornell.mannlib.vitro.webapp.search.beans.IndividualProhibitedFromSearchImpl
import edu.cornell.mannlib.vitro.webapp.search.beans.ProhibitedFromSearch
import edu.cornell.mannlib.vitro.webapp.search.solr.SourceInstitution
import edu.cornell.mannlib.vitro.webapp.search.solr.SourceInstitution
import edu.cornell.mannlib.vitro.webapp.search.solr.ThumbnailForMultiSite
import edu.cornell.mannlib.vitro.webapp.search.solr.{DocumentModifier, MultiSiteIndToDoc, AddContextNodesForMultiSite,AddSourceInstitution,AddTitle}
import java.io.ByteArrayOutputStream
import org.apache.solr.common.SolrInputDocument
import SolrDocWorker._
import scala.collection.JavaConversions._

/**
 * This class should take a RdfToDoc message and send GotDoc back
 * with the SolrInputDocument for the model.
 *
  * selctor is a SelectorGenerator that can handle adding any additional
  * statements that are needed for the model.  Ex. tbox or degrees.
  *
  * siteName is the human readable name of this site.
 */

/* TODO: we may need local extensions. ex. There may be a local class in a classgroup. */

class SolrDocWorker( selector : SelectorGenerator , siteName : String) 
extends Actor with Logging  {

  //make jena not process imports
  OntDocumentManager.getInstance().setProcessImports(false)
  
  //make jena not do LocationMapping
  OntDocumentManager.getInstance().setFileManager(new FileManager(new LocationMapper()))

  def receive = {    
    
    case RdfToDoc( siteUrl, uri, model ) => {
      logger.trace( "RDF for %s %s".format(uri, SolrDocWorker.modelToString( model ) ))

      if( model.isEmpty() ){
        self reply CouldNotGetData( siteUrl, uri , "SolrDocWorker got an empty model")
      }else{

        //make a wdf that has both the tbox and the RDF data
        val ontModels = selector.generateSelector(model) 
        val wdf = new WebappDaoFactoryJena( ontModels )

        val maybeInd:Option[Individual] = getIndividual(uri,wdf)
        maybeInd match{
          case Some( ind ) => {
            val doc = makeIndToDoc( ontModels, siteUrl, siteName).translate(ind)
            logger.trace( "Doc for " + uri + " " + doc)
            self reply GotDoc( siteUrl, uri, doc )
          }
          case None => {            
            self reply CouldNotGetData( siteUrl, uri , "SolrDocWorker could not get Individual from Model")
          }        
        }
      }
    }

    case msg => logger.warn("received odd message: " + msg)
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


/* *************** Companion Object for SolrDocWorker *************** */

object SolrDocWorker {

  val classesToSkipForDisplay = Set(
    "http://xmlns.com/foaf/0.1/Agent",
    "http://xmlns.com/foaf/0.1/Person",
    "http://xmlns.com/foaf/0.1/Organization",
    "http://vivoweb.org/ontology/core#InformationResource",
    "http://purl.org/ontology/bibo/Document",
    "http://vivoweb.org/ontology/core#Agreement",
    "http://purl.org/NET/c4dm/event.owl#Event"
  )

  val classgroupToLabel = Map(
    "http://vivoweb.org/ontology#vitroClassGrouppublications" -> "research",
    "http://vivoweb.org/ontology#vitroClassGroupactivities" -> "activities",
    "http://vivoweb.org/ontology#vitroClassGrouporganizations" -> "organizations",
    "http://vivoweb.org/ontology#vitroClassGroupcourses" -> "courses",
    "http://vivoweb.org/ontology#vitroClassGroupequipment" -> "equipment",
    "http://vivoweb.org/ontology#vitroClassGroupevents" -> "events",
    "http://vivoweb.org/ontology#vitroClassGrouplocations" -> "locations",
    "http://vivoweb.org/ontology#vitroClassGrouppeople" -> "people"  )    

  def getIndividual( uri:String, wdf:WebappDaoFactoryJena ):Option[Individual]={
    val ind = wdf.getIndividualDao().getIndividualByURI(uri)
    if( ind == null )
       None
    else
      Some(ind)
  }
  
  def makeIndToDoc( oms : OntModelSelector , siteUrl:String, siteName:String ):MultiSiteIndToDoc = {
    val docModifiers = new java.util.ArrayList[DocumentModifier]()
    
    docModifiers.add( new AddContextNodesForMultiSite( oms.getFullModel() ))
    docModifiers.add( new AddSourceInstitution( siteUrl, siteName) ) 
    docModifiers.add( new AddTitle( oms.getFullModel() ))
    docModifiers.add( new ThumbnailForMultiSite( oms.getFullModel()))

    new MultiSiteIndToDoc( 
      new ProhibitedFromSearch("",oms.getTBoxModel() ),
      new IndividualProhibitedFromSearchImpl( oms.getFullModel() ),
      null,//core class uris
      classgroupToLabel,//classgroup uris to labels
      docModifiers,
      SolrDocWorker.classesToSkipForDisplay
    )
  }
  
  def modelToString(model: Model):String = {
    val out = new ByteArrayOutputStream()
    model.write(out, "N3-PP")
    out.toString("UTF-8")
  }

}
