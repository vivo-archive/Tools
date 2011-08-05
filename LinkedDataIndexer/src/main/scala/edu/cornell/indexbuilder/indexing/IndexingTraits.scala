package edu.cornell.indexbuilder.indexing

import com.hp.hpl.jena.rdf.model.Model
import edu.cornell.mannlib.vitro.webapp.beans.Individual
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector

/**
 * Interface for an object that generates an OntModelSelector
 */
trait SelectorGenerator {
   def generateSelector( model: Model ): OntModelSelector  
}
