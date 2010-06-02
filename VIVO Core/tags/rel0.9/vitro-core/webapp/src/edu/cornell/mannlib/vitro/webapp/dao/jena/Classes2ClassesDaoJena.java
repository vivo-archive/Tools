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

import java.util.HashSet;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.webapp.beans.Classes2Classes;
import edu.cornell.mannlib.vitro.webapp.dao.Classes2ClassesDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.EditEvent;

/**
 *
 */
public class Classes2ClassesDaoJena extends JenaBaseDao implements Classes2ClassesDao {
	
    public Classes2ClassesDaoJena(WebappDaoFactoryJena wadf) {
        super(wadf);
    }

    public void deleteClasses2Classes( Classes2Classes c2c ) {
    	deleteClasses2Classes(c2c, getOntModelSelector().getTBoxModel());
    }

    public void deleteClasses2Classes( Classes2Classes c2c, OntModel ontModel )
    {
        ontModel.enterCriticalSection(Lock.WRITE);
        getOntModel().getBaseModel().notifyEvent(new EditEvent(getWebappDaoFactory().getUserURI(),true));
        try {
            OntResource subclass = getOntClass(ontModel,c2c.getSubclassURI());
            OntResource superclass = getOntClass(ontModel,c2c.getSuperclassURI());
            if ((subclass != null) && (superclass != null)) {
                ontModel.removeAll(subclass, RDFS.subClassOf, superclass);
            }
            if (subclass.isAnon()) {
            	smartRemove(subclass, getOntModel());
            }
            if (superclass.isAnon()) {
            	smartRemove(superclass, getOntModel());
            }
        } finally {
        	getOntModel().getBaseModel().notifyEvent(new EditEvent(getWebappDaoFactory().getUserURI(),false));
            ontModel.leaveCriticalSection();
        }
    }

    public void insertNewClasses2Classes( Classes2Classes c2c ) {
    	insertNewClasses2Classes(c2c, getOntModelSelector().getTBoxModel());
    }

    public void insertNewClasses2Classes( Classes2Classes c2c, OntModel ontModel )
    {
        ontModel.enterCriticalSection(Lock.WRITE);
        getOntModel().getBaseModel().notifyEvent(new EditEvent(getWebappDaoFactory().getUserURI(),true));
        try {
            Resource subclass = ontModel.getResource(c2c.getSubclassURI());
            Resource superclass = ontModel.getResource(c2c.getSuperclassURI());
            if ((subclass != null) && (superclass != null)) {
                ontModel.add(subclass, RDFS.subClassOf, superclass);
            }
        } finally {
        	getOntModel().getBaseModel().notifyEvent(new EditEvent(getWebappDaoFactory().getUserURI(),false));
            ontModel.leaveCriticalSection();
        }
    }

}
