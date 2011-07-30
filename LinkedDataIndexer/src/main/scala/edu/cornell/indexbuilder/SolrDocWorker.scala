package edu.cornell.indexbuilder

import akka.actor.Actor
import akka.event.EventHandler
import com.hp.hpl.jena.ontology.{OntModel, OntModelSpec}
import com.hp.hpl.jena.rdf.model.{Model, ModelFactory}
import edu.cornell.mannlib.vitro.webapp.beans.Individual
import edu.cornell.mannlib.vitro.webapp.dao.jena.{OntModelSelector, WebappDaoFactoryJena}
import edu.cornell.mannlib.vitro.webapp.search.beans.IndividualProhibitedFromSearchImpl
import edu.cornell.mannlib.vitro.webapp.search.beans.ProhibitedFromSearch
import edu.cornell.mannlib.vitro.webapp.search.solr.SourceInstitution
import edu.cornell.mannlib.vitro.webapp.search.solr.{DocumentModifier, MultiSiteIndToDoc, AddContextNodesForMultiSite}
import java.io.ByteArrayOutputStream
import org.apache.solr.common.SolrInputDocument
import SolrDocWorker._

/**
 * This class should take a RdfToDoc message and send GotDoc back
 * with the SolrInputDocument for the model.
 */

/* TODO: we may need local extensions. ex. There may be a local class in a classgroup. */

class SolrDocWorker( selector : SelectorGenerator ) extends Actor  {
  
  def receive = {    
    
    case RdfToDoc( siteUrl, uri, model ) => {
      //EventHandler.debug(this, "RDF for %s %s".format(uri, SolrDocWorker.modelToString( model ) ))
      if( model.isEmpty() ){
        self reply CouldNotGetData( siteUrl, uri , "SolrDocWorker got an empty model")
      }else{

        //make a wdf that has both the tbox and the RDF data
        val ontModels = selector.generateSelector(model) 
        val wdf = new WebappDaoFactoryJena( ontModels )

        val maybeInd:Option[Individual] = getIndividual(uri,wdf)
        maybeInd match{
          case Some( ind ) => {
            val doc = makeIndToDoc( ontModels, siteUrl).translate(ind)
            EventHandler.debug(this, "Doc for " + uri + " " + doc)
            self reply GotDoc( siteUrl, uri, doc )
          }
          case None => {            
            self reply CouldNotGetData( siteUrl, uri , "SolrDocWorker could not get Individual from Model")
          }        
        }
      }
    }

    case _ => println("got odd message") 
  }
  
}

object SolrDocWorker {

  def getIndividual( uri:String, wdf:WebappDaoFactoryJena ):Option[Individual]={
    val ind = wdf.getIndividualDao().getIndividualByURI(uri)
    if( ind == null )
       None
    else
      Some(ind)
  }
  
  def makeIndToDoc( oms : OntModelSelector , siteUrl:String ):MultiSiteIndToDoc = {
    val docModifiers = new java.util.ArrayList[DocumentModifier](2)

    //docModifiers.add( new ContextNodeFields(oms.getFullModel())) 
    docModifiers.add( new AddContextNodesForMultiSite( oms.getFullModel() ))
    docModifiers.add( new SourceInstitution( siteUrl ) )

    new MultiSiteIndToDoc( 
      new ProhibitedFromSearch("",oms.getTBoxModel() ),
      new IndividualProhibitedFromSearchImpl( oms.getFullModel() ),
      docModifiers
    )
  }

  //set up the ontology for vivo core
  val tbox:OntModel = loadOntology()
  
  /**
   * Load the vivo core ontology, it is needed for the tbox.
   */
  def loadOntology() = {
    //TODO: make a way to config this
    val ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM )
    ontModel.read("http://vivoweb.org/ontology/core")
    ontModel
  }

  def modelToString(model: Model):String = {
    val out = new ByteArrayOutputStream()
    model.write(out, "N3-PP")
    out.toString("UTF-8")
  }
}
