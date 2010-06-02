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
import com.hp.hpl.jena.rdf.listeners.StatementListener;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * This ModelChangedListener will accumulate all statements added to
 * or removed from the audited model in an additions model and a 
 * retractions model.  Note that this class attempts to be clever.
 * If a statement is retracted that is already in the additions model,
 * the statement will be removed from the additions model and not added
 * to the retractions model.
 * in this object.
 * @author bjl23
 *
 */
public class CumulativeDeltaModeler extends StatementListener {

	private Model additionsModel;
	private Model retractionsModel;
	
	public CumulativeDeltaModeler() {
		this.additionsModel = ModelFactory.createDefaultModel();
		this.retractionsModel = ModelFactory.createDefaultModel();
	}
	
	public CumulativeDeltaModeler(Model model) {
		this();
		model.register(this);
	}
	
	public CumulativeDeltaModeler(OntModel ontModel) {
		this();
		ontModel.getBaseModel().register(this);
	}
	
	/**
	 * Return a model containing all statements added to the attached model
	 * @return additionsModel
	 */
	public Model getAdditions() {
		return additionsModel;
	}
	
	/**
	 * Return a model containing all statements retracted from the attached model
	 * @return retractionsModel
	 */
	public Model getRetractions() {
		return retractionsModel;
	}
	
	@Override
	public void addedStatement(Statement s) {
		if (retractionsModel.contains(s)) {
			retractionsModel.remove(s);
		} else {
			additionsModel.add(s);
		}
	}
	
	@Override
	public void removedStatement(Statement s) {
		if (additionsModel.contains(s)) {
			additionsModel.remove(s);
		} else {
			retractionsModel.add(s);
		}
	}
	
}
