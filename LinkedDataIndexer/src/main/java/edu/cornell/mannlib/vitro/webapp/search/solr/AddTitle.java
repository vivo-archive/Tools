/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.solr;

import org.apache.solr.common.SolrInputDocument;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

/**
 * Gets the title for a person, right now it uses moniker since 
 * all the sites are still 1.2
 *
 */
public class AddTitle implements DocumentModifier {    
    Model model;
    Property moniker = ResourceFactory.createProperty( VitroVocabulary.MONIKER );
    
    public AddTitle(Model model){
        this.model = model;
       
    }
    
    @Override
    public void modifyDocument(Individual ind, SolrInputDocument doc, StringBuffer arg2) throws SkipIndividualException {
        if( ind == null || model == null || doc == null)
            return;
                
        StmtIterator stmts = model.listStatements(ResourceFactory.createResource( ind.getURI() ), moniker, (RDFNode)null);
        try{
            while(stmts.hasNext()){
                Statement stmt = stmts.next();
                if( stmt != null && stmt.getObject().isLiteral() ){
                    Literal lit = stmt.getObject().asLiteral();                    
                    if( lit != null){
                        doc.addField(MultiSiteIndToDoc.multiSiteTerm.moniker, lit.getValue());
                        break;
                    }
                }
            }
        }finally{
            stmts.close();
        }
        
                
    }

    @Override
    public void shutdown() {
        /*nothing*/        
    }
}
