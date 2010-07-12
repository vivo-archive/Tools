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

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.Lock;

/**
 * An OntModelSelector that does not support model-per-ontology separation
 * @author bjl23
 *
 */
public class SimpleOntModelSelector implements OntModelSelector {

	private OntModel fullModel;
	private OntModel aboxModel;
	private OntModel applicationMetadataModel;
	private OntModel tboxModel;
	private OntModel userAccountsModel;
	
	private OntModelSpec DEFAULT_ONT_MODEL_SPEC = OntModelSpec.OWL_MEM;
	
	/**
	 * Construct an OntModelSelector with a bunch of empty models
	 */
	public SimpleOntModelSelector() {
		aboxModel = ModelFactory.createOntologyModel(DEFAULT_ONT_MODEL_SPEC);
		tboxModel = ModelFactory.createOntologyModel(DEFAULT_ONT_MODEL_SPEC);
		applicationMetadataModel = ModelFactory.createOntologyModel(DEFAULT_ONT_MODEL_SPEC);
		userAccountsModel = ModelFactory.createOntologyModel(DEFAULT_ONT_MODEL_SPEC);
		fullModel = ModelFactory.createOntologyModel(DEFAULT_ONT_MODEL_SPEC);
		fullModel.addSubModel(aboxModel);
		fullModel.addSubModel(tboxModel);
		fullModel.addSubModel(applicationMetadataModel);
	}
	
	/**
	 * Construct An OntModel selector that works with a single union OntModel
	 * Only for temporary backwards compatibility.
	 */	
	public SimpleOntModelSelector(OntModel ontModel) { 
		this.fullModel = ontModel;
		this.aboxModel = ontModel;
		this.applicationMetadataModel = ontModel;
		this.tboxModel = ontModel;
		this.userAccountsModel = ontModel;
	}
	
	public void setABoxModel(OntModel m) {
		fullModel.enterCriticalSection(Lock.WRITE);
		try {
			fullModel.removeSubModel(aboxModel);
			this.aboxModel = m;
			fullModel.addSubModel(aboxModel);
		} finally {
			fullModel.leaveCriticalSection();
		}
	}
	
	public void setApplicationMetadataModel(OntModel m) {
		fullModel.enterCriticalSection(Lock.WRITE);
		try {
			fullModel.removeSubModel(applicationMetadataModel);
			this.applicationMetadataModel = m;
			fullModel.addSubModel(applicationMetadataModel);
		} finally {
			fullModel.leaveCriticalSection();
		}
	}
	
	public void setTBoxModel(OntModel m) {
		fullModel.enterCriticalSection(Lock.WRITE);
		try {
			fullModel.removeSubModel(tboxModel);
			this.tboxModel = m;
			fullModel.addSubModel(tboxModel);
		} finally {
			fullModel.leaveCriticalSection();
		}
	}
	
	public void setFullModel(OntModel m) {
		m.addSubModel(tboxModel);
		m.addSubModel(aboxModel);
		m.addSubModel(applicationMetadataModel);
		this.fullModel = m;
	}
	
	public OntModel getABoxModel() {
		return aboxModel;
	}
	
	public OntModel getApplicationMetadataModel() {
		return applicationMetadataModel;
	}

	public OntModel getFullModel() {
		return fullModel;
	}

	public OntModel getTBoxModel() {
		return tboxModel;
	}

	public OntModel getTBoxModel(String ontologyURI) {
		return tboxModel;
	}

	public OntModel getUserAccountsModel() {
		return userAccountsModel;
	}
	
	public void setUserAccountsModel(OntModel userAccountsModel) {
		this.userAccountsModel = userAccountsModel;
	}

}
