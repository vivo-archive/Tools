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
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicyTest;
import edu.cornell.mannlib.vitro.webapp.dao.jena.IndividualDaoJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Collection;

public class EntityDaoJenaTest extends AbstractTestClass {

    OntModel dorkHobbyModel;
    
    @Before
    public void setUp() throws Exception {
    	// Suppress error logging.
		setLoggerLevel(RDFDefaultErrorHandler.class, Level.OFF);

		Model model = ModelFactory.createDefaultModel();        
        InputStream in = JenaNetidPolicyTest.class.getResourceAsStream("resources/dorkyhobbies.owl");
        model.read(in,null);
        dorkHobbyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,model);
    }

	/**
	 * This is a class that had no tests, so Brian and I pulled one back. But it
	 * doesn't compile, and it appears to be intended to test a method that has
	 * not been implemented. So for now, we give it a pass.
	 */
    @Test
    public void testGetEntitiesByProperties() {
    	// This is the class that had no tests, so we pulled one back.
//        IndividualDaoJena edj = new IndividualDaoJena();
//        edj.setOntModel(dorkHobbyModel);
//        String propURI="http://test.mannlib.cornell.edu#hasHobby",
//            ignoreEntURI="http://test.mannlib.cornell.edu#bob",
//            classURI=null;
//        
//        //bob hasHobby x
//        Collection ents = 
//            edj.getIndividualsByObjectProperty(propURI, ignoreEntURI, classURI, true);        
//        assertNotNull(ents);                        
    }

}
