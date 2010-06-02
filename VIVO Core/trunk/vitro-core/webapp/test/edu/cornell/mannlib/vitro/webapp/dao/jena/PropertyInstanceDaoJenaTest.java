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

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.io.StringReader;

import junit.framework.Assert;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;


public class PropertyInstanceDaoJenaTest {
	String isDependentRelation =
		" <"+VitroVocabulary.PROPERTY_STUBOBJECTPROPERTYANNOT+"> \"true\"^^xsd:boolean .\n" ;
	
	String nosePropIsDependentRel = 
	"<"+VitroVocabulary.PROPERTY_STUBOBJECTPROPERTYANNOT+"> rdf:type owl:AnnotationProperty .\n" +
    " ex:hasNose " + isDependentRelation;
	
    String prefixesN3 = 
        "@prefix vitro: <" + VitroVocabulary.vitroURI + "> . \n" +
        "@prefix xsd: <" + XSD.getURI() + "> . \n " +
        "@prefix ex: <http://example.com/> . \n" +            
        "@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n"+
        "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . \n"+
        "@prefix owl:  <http://www.w3.org/2002/07/owl#> . \n";

	
    void printModels(Model expected, Model result){
    	System.out.println("Expected:");
    	expected.write(System.out);
    	System.out.println("Result:");
    	result.write(System.out);    
    }
    
 
    @org.junit.Test
    public void testStmtNonForceDelete() {
        String n3 = 
            prefixesN3 +
            " ex:bob ex:hasNose ex:nose1 .   \n" +            
        	" ex:nose1 ex:hasHair ex:hair23. \n" +
        	" ex:hair23 ex:hasHairCount \"23\". " ;
        
        String expected = 
            prefixesN3 +
        	" ex:nose1 ex:hasHair ex:hair23. \n" +
        	" ex:hair23 ex:hasHairCount \"23\". " ;
        
        Model model = (ModelFactory.createDefaultModel()).read(new StringReader(n3), "", "N3");
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.add(model.listStatements());        
        WebappDaoFactory wdf = new WebappDaoFactoryJena(ontModel);
        wdf.getPropertyInstanceDao().deleteObjectPropertyStatement("http://example.com/bob", "http://example.com/hasNose", "http://example.com/nose1");       
                        
        Model expectedModel = (ModelFactory.createDefaultModel()).read(new StringReader(expected), "", "N3");
        wipeOutModTime(ontModel);
        //Model resultModel = ModelFactory.createDefaultModel().add(ontModel.listStatements());
        
        boolean same = expectedModel.isIsomorphicWith( ontModel.getBaseModel() );
        if( ! same ) printModels( expectedModel, ontModel.getBaseModel());
        Assert.assertTrue( same );
    }
    
    
    @org.junit.Test
    public void testStmtSimpleForceDelete() {
        String n3= 
            prefixesN3 + 
            nosePropIsDependentRel +
            "ex:hasHair " + isDependentRelation + 
            " ex:bob ex:hasNose ex:nose1 .   \n" +
        	" ex:nose1 ex:hasHair ex:hair23. \n" +
        	" ex:hair23 ex:hasHairCount \"23\". " ;        
        String expected = 
            prefixesN3 +
            nosePropIsDependentRel +
            "ex:hasHair " + isDependentRelation ;                                          
        
        Model model = (ModelFactory.createDefaultModel()).read(new StringReader(n3), "", "N3");
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.add(model.listStatements());        
        WebappDaoFactory wdf = new WebappDaoFactoryJena(ontModel);
        wdf.getPropertyInstanceDao().deleteObjectPropertyStatement("http://example.com/bob", "http://example.com/hasNose", "http://example.com/nose1");       
                        
        Model expectedModel = (ModelFactory.createDefaultModel()).read(new StringReader(expected), "", "N3");
        wipeOutModTime(ontModel);
        //Model resultModel = ModelFactory.createDefaultModel().add(ontModel.listStatements());
        
        boolean same = expectedModel.isIsomorphicWith( ontModel.getBaseModel() );
        if( ! same ) printModels( expectedModel, ontModel.getBaseModel());
        Assert.assertTrue( same );
    }
    
    @org.junit.Test
    public void testStmtForceDeleteWithLiterals() {
        String n3 = 
            prefixesN3 +
            nosePropIsDependentRel +
            "ex:hasHair " + isDependentRelation +
            " ex:bob ex:a \"Bob\".   \n" +
            " ex:bob ex:hasNose ex:nose1 .   \n" +            
            " ex:nose1 ex:a \"this is a literal\". \n" +
            " ex:nose1 ex:b \"2343\" . \n" +
        	" ex:nose1 ex:hasHair ex:hair23. \n" +
        	" ex:hair23 ex:hasHairCount \"23\". " ;
        
        String expected = 
            prefixesN3 +
            nosePropIsDependentRel +
            "ex:hasHair " + isDependentRelation +
            " ex:bob ex:a \"Bob\".   \n"  ;
        
        Model model = (ModelFactory.createDefaultModel()).read(new StringReader(n3), "", "N3");
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.add(model.listStatements());        
        WebappDaoFactory wdf = new WebappDaoFactoryJena(ontModel);
        wdf.getPropertyInstanceDao().deleteObjectPropertyStatement("http://example.com/bob", "http://example.com/hasNose", "http://example.com/nose1");       
                        
        Model expectedModel = (ModelFactory.createDefaultModel()).read(new StringReader(expected), "", "N3");
        wipeOutModTime(ontModel);
        //Model resultModel = ModelFactory.createDefaultModel().add(ontModel.listStatements());
        
        boolean same = expectedModel.isIsomorphicWith( ontModel.getBaseModel() );
        if( ! same ) printModels( expectedModel, ontModel.getBaseModel());
        Assert.assertTrue( same );
    }

    void wipeOutModTime(Model model){
		model.removeAll(null, model.createProperty(VitroVocabulary.MODTIME), null);
	}
}
