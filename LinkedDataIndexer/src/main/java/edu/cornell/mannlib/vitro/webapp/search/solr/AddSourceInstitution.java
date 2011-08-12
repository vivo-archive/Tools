package edu.cornell.mannlib.vitro.webapp.search.solr;


import org.apache.solr.common.SolrInputDocument;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;

public class AddSourceInstitution implements DocumentModifier {
    private final String siteURL;
    private final String siteName;    
    
    public AddSourceInstitution(String siteURL, String siteName){
        this.siteURL = siteURL;
        this.siteName = siteName;
    }
    
    @Override
    public void modifyDocument(Individual individual, SolrInputDocument doc,
            StringBuffer addUri) 
    throws SkipIndividualException {
        
        MultiSiteIndToDoc.addToField(
                doc, MultiSiteIndToDoc.multiSiteTerm.site_URL, siteURL);        
        MultiSiteIndToDoc.addToField(
                doc, MultiSiteIndToDoc.multiSiteTerm.site_name, siteName);
        
    }

    @Override
    public void shutdown() {

    }


}
