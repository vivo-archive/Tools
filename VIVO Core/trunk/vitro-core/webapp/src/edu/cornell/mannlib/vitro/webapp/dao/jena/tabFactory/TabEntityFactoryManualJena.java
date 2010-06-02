/*
Copyright (c) 2010, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.dao.jena.tabFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jga.algorithms.Filter;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.util.iterator.ClosableIterator;

import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Tab;
import edu.cornell.mannlib.vitro.webapp.dao.TabEntityFactory;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.IndividualJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;

public class TabEntityFactoryManualJena extends TabEntityFactoryJena implements TabEntityFactory {
    List<Individual> _allents = null;
    
    private static final Log log = LogFactory.getLog(TabEntityFactoryManualJena.class.getName());

    public TabEntityFactoryManualJena(Tab tab, int auth_level, ApplicationBean appBean, WebappDaoFactoryJena wadf) {
        super(tab, auth_level, appBean, wadf);
    }

    public List getRelatedEntites(String alpha) {
        com.hp.hpl.jena.ontology.Individual tabInd = getTabIndividual(tab);
        if( tabInd == null ) return Collections.EMPTY_LIST;

        if( _allents == null )
            _allents = getAllRelatedEnts();

        if( alpha == null || "all".equals(alpha) )
            return _allents;

        List<Individual> out = new LinkedList<Individual>();
        Filter.filter(_allents, new FirstLetterFilter( alpha ), out);
        return out;
    }

    private List<Individual> getAllRelatedEnts(){
    	getOntModel().enterCriticalSection(Lock.READ);
    	try {
	        com.hp.hpl.jena.ontology.Individual tabInd = getTabIndividual(tab);
	        if( tabInd == null ) return Collections.emptyList();
	
	        List<Individual> ents = new LinkedList<Individual>();
	        if( TAB_INDIVIDUALRELATION == null )
	            log.debug(" TAB_INDIVIDUALRELATION is " + TAB_INDIVIDUALRELATION );
	        if( TAB_INDIVIDUALRELATION_INVOLVESTAB == null )
	            log.debug(" TAB_INDIVIDUALRELATION_INVOLVESTAB is " + TAB_INDIVIDUALRELATION_INVOLVESTAB );
	        if( TAB_INDIVIDUALRELATION_INVOLVESINDIVIDUAL == null )
	            log.debug(" TAB_INDIVIDUALRELATION_INVOLVESINDIVIDUAL is " + TAB_INDIVIDUALRELATION_INVOLVESINDIVIDUAL );
	
	        ClosableIterator stmtIt = getOntModel().listStatements(null , TAB_INDIVIDUALRELATION_INVOLVESTAB, tabInd);
	        try{
	            while(stmtIt.hasNext() ){
	                Statement relationStmt = (Statement)stmtIt.next();
	                Resource relation =  relationStmt.getSubject();
	                ClosableIterator manualLinkStmtIt = getOntModel().listStatements(relation, TAB_INDIVIDUALRELATION_INVOLVESINDIVIDUAL, (Resource)null);
	                try{
	                    while(manualLinkStmtIt.hasNext()){
	                        Statement stmt = (Statement)manualLinkStmtIt.next();
	                        if( stmt.getObject().canAs(com.hp.hpl.jena.ontology.Individual.class) ) {
	                        	com.hp.hpl.jena.ontology.Individual ind = (com.hp.hpl.jena.ontology.Individual) stmt.getObject().as(com.hp.hpl.jena.ontology.Individual.class);                        
	                            Individual relatedInd = new IndividualJena(ind, (WebappDaoFactoryJena)webappDaoFactory);
	                            ents.add(relatedInd);
	                        }
	                    }
	                }finally{
	                    manualLinkStmtIt.close();
	                }
	            }
	        }finally{
	            stmtIt.close();
	        }
	        removeDuplicates(ents);
	        return ents;
    	} finally {
    		getOntModel().leaveCriticalSection();
    	}
    }
}
