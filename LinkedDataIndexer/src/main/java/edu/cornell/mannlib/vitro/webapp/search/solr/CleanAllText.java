/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.solr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.search.solr.MultiSiteIndToDoc.multiSiteTerm;

/** Strip odd XML, HTML tags etc out of alltext field */
public class CleanAllText implements DocumentModifier {
    public static final Log log = LogFactory.getLog( CleanAllText.class);
            
    @Override
    public void modifyDocument(
            Individual ind, 
            SolrInputDocument doc,
            StringBuffer arg2) 
    throws SkipIndividualException {
        List<String> cleanValues = new ArrayList<String>();
        SolrInputField alltext = doc.getField( multiSiteTerm.alltext);
        if( alltext != null ){
            for( Object obj : alltext.getValues()){
                if( obj instanceof String){
                    cleanValues.add( clean( (String)obj) );                    
                }                                    
            }
            doc.removeField( multiSiteTerm.alltext );
            for( String cleaned : cleanValues){
                doc.addField(multiSiteTerm.alltext,cleaned);
            }
        }
                               
        doc.addField( multiSiteTerm.alltext, alltext);        
    }
    
    protected String clean(String in){
        String stripped = null;
        try {
            stripped = Jsoup.clean(in, Whitelist.none());                       
        } catch(Exception e) {
            log.debug("Could not strip HTML during indexing. " , e);
        }   
        if( stripped == null)
            return in;
        else
            return in;
    }
    
    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }

}
