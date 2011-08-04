package edu.cornell.mannlib.vitro.webapp.search.solr;


import org.apache.solr.common.SolrInputDocument;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;

public class AddSourceInstitution implements DocumentModifier {

    private final String siteURL;
    private final String siteName;    
    
    private String fieldForSiteURL =  MultiSiteIndToDoc.multiSiteTerm.siteURL;
    private String fieldForSiteName = MultiSiteIndToDoc.multiSiteTerm.siteName;
    
    public AddSourceInstitution(String siteURL, String siteName){
        this.siteURL = siteURL;
        this.siteName = siteName;
    }
    
    @Override
    public void modifyDocument(Individual individual, SolrInputDocument doc,
            StringBuffer addUri) throws SkipIndividualException {       
        doc.addField(fieldForSiteURL, siteURL);        
        doc.addField(fieldForSiteName, siteName);
    }

    @Override
    public void shutdown() {

    }


}
