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

/**
 * This class should take a RdfToDoc message and send GotDoc back
 * with the SolrInputDocument for the model.
 */

/* TODO: we may need local extensions. ex. There may be a local class in a classgroup. */

class SolrDocWorker( selector : SelectorGenerator ) extends Actor  {
  
  def receive = {    
    
    case RdfToDoc( siteUrl, uri, model ) => {
      //EventHandler.debug(this, "RDF for " + uri + " " + SolrDocWorker.modelToString( model ) )
      if( ! model.isEmpty() ){
        val doc = SolrDocWorker.individualToDocument( siteUrl, uri, model, selector)
        EventHandler.debug(this, "Doc for " + uri + " " + doc)
        self reply GotDoc( siteUrl, uri, doc )
      }else{
        EventHandler.warning(this,"Could not get data for %s from %s".format(uri,siteUrl))        
        self reply CouldNotGetData( siteUrl, uri , "SolrDocWorker got an empty model")
      }
    }

    case _ => println("got odd message") 
  }
  
}

object SolrDocWorker {
  def individualToDocument( siteUrl:String, uri:String, model:Model, selector:SelectorGenerator ):SolrInputDocument={
    //make a wdf that has both the tbox and the RDF data
    val ontModelSelector = selector.generateSelector(model) 
    val wdf = new WebappDaoFactoryJena( ontModelSelector )

    // create the object that builds the doc from the RDF
    val ind = wdf.getIndividualDao().getIndividualByURI(uri)
    if( ind != null )
      makeIndToDoc( ontModelSelector, siteUrl ).translate( ind)    
  }
  
  def makeIndToDoc( oms : OntModelSelector , siteUrl:String ) = {
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
