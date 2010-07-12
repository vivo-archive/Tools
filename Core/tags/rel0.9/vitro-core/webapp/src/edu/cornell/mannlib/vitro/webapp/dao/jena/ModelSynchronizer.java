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

import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Simple change listener to keep a model (the 'synchronizee') in synch with the model with which it is registered.
 * @author bjl23
 *
 */
public class ModelSynchronizer implements ModelChangedListener {

	private Model m;
	
	public ModelSynchronizer (Model synchronizee) {
		this.m = synchronizee;
	}
		
	public void addedStatement(Statement arg0) {
		m.add(arg0);
	}

	public void addedStatements(Statement[] arg0) {
		m.add(arg0);
	}

	
	public void addedStatements(List arg0) {
		m.add(arg0);
	}

	
	public void addedStatements(StmtIterator arg0) {
		m.add(arg0);
	}

	
	public void addedStatements(Model arg0) {
		m.add(arg0);
	}

	
	public void notifyEvent(Model arg0, Object arg1) {
		if ( arg1 instanceof CloseEvent ) {
			m.close();
		}
	}

	
	public void removedStatement(Statement arg0) {
		m.remove(arg0);
	}

	
	public void removedStatements(Statement[] arg0) {
		m.remove(arg0);
	}
	
	public void removedStatements(List arg0) {
		m.remove(arg0);
	}

	
	public void removedStatements(StmtIterator arg0) {
		m.remove(arg0);
	}

	
	public void removedStatements(Model arg0) {
		m.remove(arg0);
	}
	
}
