package edu.cornell.mannlib.vitro.webapp.search.solr;


import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;

public class AddSourceInstitution extends SourceInstitution implements DocumentModifier {

	public AddSourceInstitution(String siteURL, String siteName) {
		super(siteURL, siteName);
	}
	
    @Override
    public void modifyDocument(Individual individual, SolrInputDocument doc, StringBuffer addUri) {                              
        
		doc.addField(MultiSiteIndToDoc.multiSiteTerm.siteURL, siteURL);
		doc.addField(MultiSiteIndToDoc.multiSiteTerm.siteName, siteName);                                  
    }

}
