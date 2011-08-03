package edu.cornell.mannlib.vitro.webapp.search.solr;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;

public class AddNameBoost extends NameBoost implements DocumentModifier{
	
    String[] fieldsToBoost = {MultiSiteIndToDoc.multiSiteTerm.name, MultiSiteIndToDoc.multiSiteTerm.nameStemmed};
    
    static final float NAME_BOOST = (float) 1.2;
    
    @Override
    public void modifyDocument(Individual individual, SolrInputDocument doc,
            StringBuffer addUri) {
        
        for( String fieldName : fieldsToBoost){
            SolrInputField field = doc.getField(fieldName);
            if( field != null )
                field.setBoost(field.getBoost() * NAME_BOOST);            
        }        
    }

}
