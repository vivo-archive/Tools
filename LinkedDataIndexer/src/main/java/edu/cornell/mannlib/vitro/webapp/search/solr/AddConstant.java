/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.solr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.search.solr.MultiSiteIndToDoc.multiSiteTerm;

/**
 * THIS CLASS CAN BE REMOVED!!!
 */
public class AddConstant implements DocumentModifier {

    public static final Log log = LogFactory.getLog( AddConstant.class);

    final String fieldName;
    final String value;

    public AddConstant(String fieldName, String value){
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public void modifyDocument(Individual ind, SolrInputDocument doc,
            StringBuffer arg2) throws SkipIndividualException {        
        doc.addField(fieldName, value);
    }
    
    @Override
    public void shutdown() {
        //nothing to do on shutdown
    }

}
