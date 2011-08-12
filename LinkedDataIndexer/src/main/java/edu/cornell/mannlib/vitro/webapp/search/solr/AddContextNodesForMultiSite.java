package edu.cornell.mannlib.vitro.webapp.search.solr;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;

import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;

public class AddContextNodesForMultiSite extends ContextNodeFields implements DocumentModifier{

    public AddContextNodesForMultiSite(Model model) {
        super(model);
    }
    
    @Override
    public void modifyDocument(Individual individual, SolrInputDocument doc, StringBuffer addUri) {                              
        
        StringBuffer objectProperties = singleThreadExecute( individual, multiValuedQueriesForAgent);
        
        MultiSiteIndToDoc.addToField(
                doc,
                MultiSiteIndToDoc.multiSiteTerm.alltext,                
                objectProperties + " " + 
                runQuery(individual, multiValuedQueryForInformationResource));                                            
    }

}
