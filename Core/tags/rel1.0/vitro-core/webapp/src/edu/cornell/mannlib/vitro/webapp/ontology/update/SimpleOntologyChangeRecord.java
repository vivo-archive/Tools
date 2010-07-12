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

package edu.cornell.mannlib.vitro.webapp.ontology.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SimpleOntologyChangeRecord implements OntologyChangeRecord {

	private final static Log log = 
			LogFactory.getLog(SimpleOntologyChangeRecord.class);
	
	private final static String RDF_SYNTAX = "N3";
	
	private Model additionsModel = ModelFactory.createDefaultModel();
	private Model retractionsModel = ModelFactory.createDefaultModel();
	private File additionsFile;
	private File retractionsFile;
	
	public SimpleOntologyChangeRecord(
			String additionsFile, String retractionsFile) {
		this.additionsFile = new File(additionsFile);
		try {
			FileWriter test = new FileWriter(additionsFile);
		} catch (IOException ioe) {
				throw new RuntimeException(this.getClass().getName() + 
					" unable to create required file at " + additionsFile);
		}	
		this.retractionsFile = new File(retractionsFile);
		try { 
			FileWriter test = new FileWriter(retractionsFile);
		} catch (IOException ioe) {
			throw new RuntimeException(this.getClass().getName() + 
					" unable to create required file at " + retractionsFile);			
		}
	}
	
	public void recordAdditions(Model incrementalAdditions) {
		additionsModel.add(incrementalAdditions);
	
	}

	public void recordRetractions(Model incrementalRetractions) {
		retractionsModel.add(incrementalRetractions);
	}
	
	private void write(Model model, File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			model.write(fos, RDF_SYNTAX);
		} catch (FileNotFoundException fnfe) {
			log.error(this.getClass().getName() + 
					  " unable to write to RDF file", fnfe);
		}
	}
	
	public void writeChanges() {
		if (additionsModel.size() > 0) {
			write(additionsModel, additionsFile);
		}
		if (retractionsModel.size() > 0) {
			write(retractionsModel, retractionsFile);
		}
	}

}
