package edu.cornell.indexbuilder

import akka.actor.Actor
import akka.event.EventHandler
 
import java.io.ByteArrayOutputStream

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.graph.compose.Union
import com.hp.hpl.jena.sparql.core.DatasetImpl

import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena
import edu.cornell.mannlib.vitro.webapp.search.solr.IndividualToSolrDocument
import edu.cornell.mannlib.vitro.webapp.search.solr.ContextNodeFields
import edu.cornell.mannlib.vitro.webapp.search.solr.DocumentModifier
import edu.cornell.mannlib.vitro.webapp.search.beans.IndividualProhibitedFromSearch
import edu.cornell.mannlib.vitro.webapp.search.beans.IndividualProhibitedFromSearchImpl

import edu.cornell.mannlib.vitro.webapp.search.beans.ProhibitedFromSearch
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector

/**
 * This class should take a RdfToDoc message and send GotDoc back
 * with the SolrInputDocument for the model.
 */

/*
 * TODO: we may need local extensions. ex. There may be a local class in a classgroup.
 * TODO: Need way to configure the creation of a WebappDaoFactory
 */
class SolrDocWorker( selector : SelectorGenerator ) extends Actor {
  
  def receive = {    
    
    case RdfToDoc( siteUrl, uri, model ) => {
      EventHandler.debug(this, "RDF for " + uri + " " + SolrDocWorker.modelToString( model ) )
      val doc = individualToDocument( siteUrl, uri, model)
      EventHandler.debug(this, "Doc for " + uri + " " + doc)
      self reply GotDoc( siteUrl, uri, doc )
    }

    case _ => println("got odd message") 
  }
  
  def individualToDocument( siteUrl:String, uri:String, model:Model )={
    //make a wdf that has both the tbox and the RDF data
    val ontModelSelector = selector.generateSelector(model) 
    val wdf = new WebappDaoFactoryJena( ontModelSelector )

    // create the object that builds the doc from the RDF
    makeIndToDoc( ontModelSelector ).translate( wdf.getIndividualDao().getIndividualByURI(uri) )
  }
  
  def makeIndToDoc( oms : OntModelSelector  ) = {
    val docModifiers = new java.util.ArrayList[DocumentModifier](1)
    docModifiers.add( new ContextNodeFields(new DatasetImpl(oms.getFullModel())) )
    //docModifiers.add( new ContextNodeFields(oms.getFullModel()) ) 

    new IndividualToSolrDocument( 
      new ProhibitedFromSearch("",oms.getTBoxModel() ),
      new IndividualProhibitedFromSearchImpl( oms.getFullModel() ),
      docModifiers
    )
  }
}

object SolrDocWorker {

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
