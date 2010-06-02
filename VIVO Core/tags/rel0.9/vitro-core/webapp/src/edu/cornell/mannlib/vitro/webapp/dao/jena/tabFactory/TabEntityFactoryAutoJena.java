package edu.cornell.mannlib.vitro.webapp.dao.jena.tabFactory;

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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.sf.jga.algorithms.Filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Tab;
import edu.cornell.mannlib.vitro.webapp.dao.TabEntityFactory;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.IndividualJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;

public class TabEntityFactoryAutoJena extends TabEntityFactoryJena implements TabEntityFactory {
    List<Individual> _relatedEnts;
    private static final Log log = LogFactory.getLog(TabEntityFactoryAutoJena.class.getName());
    
    public TabEntityFactoryAutoJena(Tab tab, int auth_level, ApplicationBean appBean, WebappDaoFactoryJena wadf) {
        super(tab, auth_level, appBean, wadf);
    }

    @SuppressWarnings("unchecked")
    public List getRelatedEntites(String alpha) {
        if( _relatedEnts == null )
            _relatedEnts =  getAllRelatedEnts();

        if( alpha == null || "all".equals(alpha) )
            return _relatedEnts;

        List<Individual> out = new LinkedList<Individual>();
        Filter.filter(_relatedEnts, new FirstLetterFilter( alpha ), out);
        return out;
    }

    private List getAllRelatedEnts(){
        LinkedList<Individual> ents = new LinkedList<Individual>();
        getOntModel().enterCriticalSection(Lock.READ);
        try{
            com.hp.hpl.jena.ontology.Individual tabInd = getTabIndividual(tab);
            if( tabInd == null ) return Collections.EMPTY_LIST;

            if( TAB_AUTOLINKEDTOTAB == null ){
                log.error("could not find annotation property " + VitroVocabulary.TAB_AUTOLINKEDTOTAB);
                return Collections.EMPTY_LIST;
            }

            //get the classes that are linked to this tab
            ClosableIterator classIt = getOntModel().listStatements(null, TAB_AUTOLINKEDTOTAB, tabInd);

            try{
                while(classIt.hasNext()){
                    Statement linkedToTab = (Statement)classIt.next();
                    OntClass linkedClass = getOntModel().getOntClass( linkedToTab.getSubject().getURI() );

                    ClosableIterator entIt = getOntModel().listStatements(null, RDF.type, linkedClass);
                    try{
                        while(entIt.hasNext()){
                            Statement entIsOfClass = (Statement)entIt.next();
                            if( entIsOfClass.getSubject().canAs(com.hp.hpl.jena.ontology.Individual.class) ) {
                            	com.hp.hpl.jena.ontology.Individual ind = (com.hp.hpl.jena.ontology.Individual) entIsOfClass.getSubject().as(com.hp.hpl.jena.ontology.Individual.class);
                                Individual ent = new IndividualJena(ind, (WebappDaoFactoryJena)webappDaoFactory);
                                ents.add(ent);
                            }
                        }
                    }finally{
                        entIt.close();
                    }
                }
            }finally{
                classIt.close();
            }
        }finally{
            getOntModel().leaveCriticalSection();
        }

        removeDuplicates( ents );        
        return ents;
    }
}
