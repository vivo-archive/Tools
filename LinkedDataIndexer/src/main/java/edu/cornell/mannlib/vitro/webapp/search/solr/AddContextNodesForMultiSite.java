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
        
        SolrInputField field = doc.getField( MultiSiteIndToDoc.multiSiteTerm.alltext );
        if( field == null ){
            doc.addField(MultiSiteIndToDoc.multiSiteTerm.alltext , 
                    objectProperties + " " + 
                    runQuery(individual, multiValuedQueryForInformationResource));          
        }else{
            field.addValue(objectProperties + " " + 
                    runQuery(individual, multiValuedQueryForInformationResource), 
                    field.getBoost());
        }                                  
    }

}
