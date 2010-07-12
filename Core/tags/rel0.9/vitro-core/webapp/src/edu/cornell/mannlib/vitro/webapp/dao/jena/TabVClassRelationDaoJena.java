package edu.cornell.mannlib.vitro.webapp.dao.jena;

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

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;
import edu.cornell.mannlib.vitro.webapp.beans.TabVClassRelation;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.TabVClassRelationDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class TabVClassRelationDaoJena extends JenaBaseDao implements TabVClassRelationDao {

    public TabVClassRelationDaoJena(WebappDaoFactoryJena wadf) {
        super(wadf);
    }

    @Override
    protected OntModel getOntModel() {
    	return getOntModelSelector().getApplicationMetadataModel();
    }
    
    public void deleteTabVClassRelation( TabVClassRelation t2t ) {
    	deleteTabs2Types(t2t,getOntModel());
    }

    public void deleteTabs2Types( TabVClassRelation t2t, OntModel ontModel )
    {
        ontModel.enterCriticalSection(Lock.WRITE);
        try {
            Property typeLinkedToTab = ontModel.getProperty(VitroVocabulary.TAB_AUTOLINKEDTOTAB);
            Resource type = ontModel.getResource(t2t.getVClassURI());
            Resource tab = ontModel.getResource(DEFAULT_NAMESPACE+"tab"+t2t.getTabId());
            if ((typeLinkedToTab != null) && (tab != null) && (type != null)) {
                ontModel.removeAll(type, typeLinkedToTab, tab);
            } else {
                log.error("No good - something was null");
            }
        } finally {
            ontModel.leaveCriticalSection();
        }
    }

    public int insertTabVClassRelation( TabVClassRelation t2t ) {
    	insertNewTabs2Types(t2t,getOntModel());
        return 0;
    }

    public void insertNewTabs2Types( TabVClassRelation t2t, OntModel ontModel )
    {
        ontModel.enterCriticalSection(Lock.WRITE);
        try {
            Property typeLinkedToTab = ontModel.getProperty(VitroVocabulary.TAB_AUTOLINKEDTOTAB);
            Resource type = ontModel.getResource(t2t.getVClassURI());
            Resource tab = ontModel.getResource(DEFAULT_NAMESPACE+"tab"+t2t.getTabId());
            if ((typeLinkedToTab != null) && (tab != null) && (type != null)) {
                ontModel.add(type, typeLinkedToTab, tab);
            }
        } finally {
            ontModel.leaveCriticalSection();
        }
    }


}
