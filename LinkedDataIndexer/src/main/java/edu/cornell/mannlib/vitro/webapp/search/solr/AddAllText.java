/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.solr;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;

import com.hp.hpl.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.search.solr.MultiSiteIndToDoc.multiSiteTerm;

public class AddAllText implements DocumentModifier {
    public static final Log log = LogFactory.getLog( AddAllText.class);
            
    @Override
    public void modifyDocument(Individual ind, SolrInputDocument doc,
            StringBuffer arg2) throws SkipIndividualException {

        String t=null;
        StringBuffer allTextValue = new StringBuffer();

        //collecting data property statements
        List<DataPropertyStatement> dataPropertyStatements = ind.getDataPropertyStatements();
        if (dataPropertyStatements != null) {
            Iterator<DataPropertyStatement> dataPropertyStmtIter = dataPropertyStatements.iterator();
            while (dataPropertyStmtIter.hasNext()) {
                DataPropertyStatement dataPropertyStmt =  dataPropertyStmtIter.next();
                
                // we don't want label to be added to alltext
                if( RDFS.label.getURI().equals( dataPropertyStmt.getDatapropURI())){ 
                    continue;
                }
                
                allTextValue.append(" ");
                allTextValue.append(((t=dataPropertyStmt.getData()) == null)?"":t);
            }
        }                         
        
        try {
            String stripped = Jsoup.parse(allTextValue.toString()).text();
            allTextValue.setLength(0);
            allTextValue.append(stripped);
        } catch(Exception e) {
            log.debug("Could not strip HTML during search indexing. " , e);
        }
                
        String alltext = allTextValue.toString();
        
        doc.addField(multiSiteTerm.alltext, alltext);
        doc.addField(multiSiteTerm.alltextStemmed, alltext);        
    }
    
    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }

}
