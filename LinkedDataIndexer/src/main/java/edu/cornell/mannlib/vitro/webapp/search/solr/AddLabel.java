/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.solr;

import org.apache.solr.common.SolrInputDocument;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.search.solr.MultiSiteIndToDoc.multiSiteTerm;

public class AddLabel implements DocumentModifier {    

    @Override
    public void modifyDocument(Individual ind, SolrInputDocument doc, StringBuffer arg2) throws SkipIndividualException {
        String value = "";
        String label = ind.getRdfsLabel();
        if (label != null) {
            value = label;
        } else {
            value = ind.getLocalName();
        }            
        
        doc.addField(multiSiteTerm.name,value);
        doc.addField(multiSiteTerm.nameStemmed, value); 
        
    }

    @Override
    public void shutdown() {
        /*nothing*/        
    }
}
