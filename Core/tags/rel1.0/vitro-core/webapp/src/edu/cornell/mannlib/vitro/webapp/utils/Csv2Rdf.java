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

package edu.cornell.mannlib.vitro.webapp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.skife.csv.CSVReader;
import org.skife.csv.SimpleReader;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Csv2Rdf {

	private String namespace;
	private String tboxNamespace;
	private String typeName;
	private String individualNameBase;
	private String propertyNameBase;
	private char separatorChar;
    private char[] quoteChars;
	
    public Csv2Rdf(char[] quoteChars, String namespace, String tboxNamespace, String typeName) {
    	this.separatorChar = ',';
		this.quoteChars = quoteChars;
		this.namespace = namespace;
		this.tboxNamespace = tboxNamespace;
		this.typeName = typeName;
		this.individualNameBase = typeName.toLowerCase();
		this.propertyNameBase = individualNameBase+"_";
	}
    
	public Csv2Rdf(char separatorChar, char[] quoteChars, String namespace, String tboxNamespace, String typeName) {
		this.separatorChar = separatorChar;
		this.quoteChars = quoteChars;
		this.namespace = namespace;
		this.tboxNamespace = tboxNamespace;
		this.typeName = typeName;
		this.individualNameBase = typeName.toLowerCase();
		this.propertyNameBase = individualNameBase+"_";
	}
	
	public Model[] convertToRdf(InputStream fis) throws IOException {
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		OntModel tboxOntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        ontModel.addSubModel(tboxOntModel);
        OntClass theClass = tboxOntModel.createClass(tboxNamespace+typeName);

		CSVReader cReader = new SimpleReader();
		cReader.setSeperator(separatorChar);
		cReader.setQuoteCharacters(quoteChars);	
		
		List<String[]> fileRows = cReader.parse(fis);
		
        String[] columnHeaders = fileRows.get(0);

        DatatypeProperty[] dpArray = new DatatypeProperty[columnHeaders.length];

        for (int i=0; i<columnHeaders.length; i++) {
            dpArray[i] = tboxOntModel.createDatatypeProperty(tboxNamespace+propertyNameBase+columnHeaders[i].replaceAll("\\W",""));
        }

        for (int row=1; row<fileRows.size(); row++) {
	        Individual ind = ontModel.createIndividual(namespace+individualNameBase+row,theClass);
	        String[] cols = fileRows.get(row);
	        for (int col=0; col<cols.length; col++) {
				String value = cols[col].trim();
	            if (value.length()>0) {
	                ind.addProperty(dpArray[col], value); // no longer using: , XSDDatatype.XSDstring);
	                // TODO: specification of datatypes for columns
	            }
	        }
        }
        
        ontModel.removeSubModel(tboxOntModel);
		
		Model[] resultModels = new Model[2];
		resultModels[0] = ontModel;
		resultModels[1] = tboxOntModel;
		return resultModels;
		
	}
	
}
