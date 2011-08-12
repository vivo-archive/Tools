/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.solr;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.search.solr.MultiSiteIndToDoc.multiSiteTerm;

public class AddObjectPropertyText implements DocumentModifier {
    public static final Log log = LogFactory.getLog(AddObjectPropertyText.class);
    
    @Override
    public void modifyDocument(Individual ind, SolrInputDocument doc,
            StringBuffer arg2) throws SkipIndividualException {
        
        StringBuffer objectNames = new StringBuffer();
        
        List<ObjectPropertyStatement> objectPropertyStatements = ind.getObjectPropertyStatements();
        if (objectPropertyStatements != null) {
            Iterator<ObjectPropertyStatement> objectPropertyStmtIter = objectPropertyStatements.iterator();
            while (objectPropertyStmtIter.hasNext()) {
                ObjectPropertyStatement objectPropertyStmt = objectPropertyStmtIter.next();
                if( "http://www.w3.org/2002/07/owl#differentFrom".equals(objectPropertyStmt.getPropertyURI()) ){
                    continue;
                }
                try {                    
                    String t=null;
                    objectNames.append(((t=objectPropertyStmt.getObject().getRdfsLabel()) == null)?"":t);
                    objectNames.append(' ');
                } catch (Exception e) { 
                    log.error("could not get object property statment for " + ind.getURI() ,e );
                }
            }
            
                        
        }        
        String value = objectNames.toString();
        doc.addField( multiSiteTerm.alltext, value);        
    }

    @Override
    public void shutdown() {
       /* nothing */
    }

}
