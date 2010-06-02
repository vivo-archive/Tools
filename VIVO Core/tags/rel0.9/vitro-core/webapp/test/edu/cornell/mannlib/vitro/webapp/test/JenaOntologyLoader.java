package edu.cornell.mannlib.vitro.webapp.test;

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

import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * this is a class to load owl files for testing.
 * @author bdc34
 *
 */
public class JenaOntologyLoader {
    public OntModel ontModel = null;
    
    /**
     * This should load the system with classes, data properties and
     * object properties that the vitro systems needs.
     *  
     * @return
     * @throws Exception 
     */
    OntModel loadSystemAndUsers() throws Exception{        
        Model model = ModelFactory.createDefaultModel();        
        for( String ont : systemOnts){                    
            InputStream in = this.getClass().getResourceAsStream(ont);
            model.read(in,null);
            in.close();
        }        
        ontModel = ModelFactory.createOntologyModel(ONT_MODEL_SPEC,model);        
        ontModel.prepare();    
        return ontModel;
    }

    
    /**
     * Loads a owl file into the ontModel.  Looks for files on classpath.
     * example: loadSpecialVivoModel("/testontologies/smallVivo-20070809.owl") 
     * 
     * @param junk
     * @return
     * @throws IOException
     */
    OntModel loadSpecialVivoModel(String junk) throws IOException{
        InputStream in = this.getClass().getResourceAsStream(junk);
        Model model = ModelFactory.createDefaultModel();
        model.read(in,null);
        in.close();
            
        ontModel.add(model);         
        ontModel.prepare();    
        return ontModel;                
    }
    
    static String systemOnts[] ={    
        "/testontologies/vitro1.owl",
        "/testontologies/vivo-users.owl" };
    
    static String testOnt[] ={
        "/testontologies/smallVivo-20070809.owl" };
    
    static OntModelSpec ONT_MODEL_SPEC = OntModelSpec.OWL_DL_MEM; // no additional entailment reasoning

}
