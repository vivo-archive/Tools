package edu.cornell.indexbuilder

import akka.actor.Actor
import akka.event.EventHandler
 
import java.io.ByteArrayOutputStream

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.graph.compose.Union

import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena
import edu.cornell.mannlib.vitro.webapp.search.solr.IndividualToSolrDocument
import edu.cornell.mannlib.vitro.webapp.search.beans.IndividualProhibitedFromSearch
import edu.cornell.mannlib.vitro.webapp.search.beans.IndividualProhibitedFromSearchImpl
import edu.cornell.mannlib.vitro.webapp.search.beans.ProhibitedFromSearch
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector
import edu.cornell.mannlib.vitro.webapp.dao.jena.SimpleOntModelSelector

/**
 * Creates an OntModelSelector that is used for a WebappDaoFactory.
 * This setups the ontologies and RDF instance data that are needed
 * for VIVO such as the VIVO core tbox, the geopolitical instances etc.
 */
class SelectorGeneratorForVivo( siteBaseUrl : String ) extends SelectorGenerator {

  def generateSelector(model:Model):OntModelSelector = {
    new SimpleOntModelSelector( makeCombinedModel( model ) )
  }

  def makeCombinedModel( linkedData: Model  ) = {
    val combinedModels = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM )
    combinedModels.addSubModel( SelectorGeneratorForVivo.sharedFullModel )
    combinedModels.addSubModel( linkedData )
    combinedModels
  }  
}


//Companion object for ModelToIndividualVivo
object SelectorGeneratorForVivo{

  val sharedFullModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)
  
  //set up the ontology for vivo core
  sharedFullModel.add( loadCoreOntology() )
  
  //setup application metadata
  sharedFullModel.add( loadApplicationMetadata() )

  //setup shared abox data
  sharedFullModel.add( loadSharedAbox() )

  
  /**
   * Load the vivo core ontology, it is needed for the tbox.
   */
  def loadCoreOntology() = {
    val ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM )
    ontModel.read("http://vivoweb.org/ontology/core")
    ontModel
  }

  def loadApplicationMetadata() = {
    val appModel = loadModelFromRDFXML( "/classgroups.rdf")
    val annotations = loadModelFromRDFXML( "/vivo-core-1.3-annotations.rdf")
    val geoAnnotations = loadModelFromRDFXML( "/geopolitical-1.1-annotations.rdf")    
    appModel.add( annotations)
    appModel.add( geoAnnotations)

    appModel.add( loadModelFromRDFXML("/academicDegree.rdf"))
    appModel.add( loadModelFromRDFXML("/documentStatus.rdf"))
    appModel.add( loadModelFromRDFXML("/geopolitical-1.1-annotations.rdf"))
    appModel.add( loadModelFromRDFXML("/geopolitical-1.1-individual-labels.rdf"))

    //maybe some of these files are not app data 
    appModel.add( loadModelFromRDFXML("/scires-1.3.owl"))
    appModel.add( loadModelFromRDFXML("/vitro-0.7.owl"))
    appModel.add( loadModelFromRDFXML("/vivo-core-1.3-annotations.rdf"))
    appModel.add( loadModelFromRDFXML("/vivo-core-1.3.owl"))
    appModel.add( loadModelFromRDFXML("/vivocore.owl"))
    appModel.add( loadModelFromRDFXML("/dateTimeValuePrecision.rdf"))

    appModel
  }

  def loadSharedAbox() = {
    loadModelFromRDFXML("/geopolitical-1.1-individual-labels.rdf")    
  }

  def loadModelFromRDFXML( name : String ): OntModel = {
    //getClass().getResourceAsStream( "/com/company/app/dao/sql/SqlQueryFile.sql" )));
    val in = getClass().getResourceAsStream( name );
    val ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM )
    ontModel.read(in, "")
    ontModel
  }

  //utility method to format a model as N3
  def modelToString(model: Model):String = {
    val out = new ByteArrayOutputStream()
    model.write(out, "N3-PP")
    out.toString("UTF-8")
  }
}



