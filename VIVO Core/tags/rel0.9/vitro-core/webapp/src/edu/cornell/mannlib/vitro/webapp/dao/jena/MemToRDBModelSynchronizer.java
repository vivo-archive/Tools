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

import java.sql.SQLException;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.listeners.StatementListener;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * This listener will open and close RDB models as it performs edits to avoid
 * wasting DB connections
 * @author bjl23
 *
 */
public class MemToRDBModelSynchronizer extends StatementListener {

	private static long IDLE_MILLIS = 2000; // how long to let a model site idle after an edit has been performed
	
	RDBGraphGenerator generator;
	Model model;
	boolean editInProgress;
	boolean cleanupThreadActive;
	long lastEditTimeMillis;
	
	public MemToRDBModelSynchronizer(RDBGraphGenerator generator) {
		this.generator = generator;
	}
	
	private Model getModel() {
		if ( model != null && !model.isClosed() ) {
			return model;
		} else {
			Graph g = generator.generateGraph();
			model = ModelFactory.createModelForGraph(g);
			return model;
		}
	}
	
	@Override
	public void addedStatement(Statement stmt) {
		this.editInProgress = true;
		try {
			getModel().add(stmt);
		} finally {
			lastEditTimeMillis = System.currentTimeMillis();
			this.editInProgress = false;
			if (!cleanupThreadActive) {
				(new Thread(new Cleanup(this))).start();
			}
		}
	}
	
	@Override
	public void removedStatement(Statement stmt) {
		this.editInProgress = true;
		try {
			getModel().remove(stmt);
		} finally {
			lastEditTimeMillis = System.currentTimeMillis();
			this.editInProgress = false;
			if (!cleanupThreadActive) {
				(new Thread(new Cleanup(this))).start();
			}
		}
	}
	
	private class Cleanup implements Runnable {
		
		private MemToRDBModelSynchronizer s;
				
		public Cleanup(MemToRDBModelSynchronizer s) {
			this.s = s;
		}
		
		public void run() {
			s.cleanupThreadActive = true;
			while( (s.editInProgress) || (System.currentTimeMillis() - s.lastEditTimeMillis < IDLE_MILLIS ) ) {
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException("Interrupted cleanup thread in " + this.getClass().getName(), e);
				}
			}
			if (s.model != null) {
				s.model.close();
				s.model = null;
			} else {
				throw new RuntimeException(this.getClass().getName()+"Model already null");
			}
			java.sql.Connection c = generator.getConnection();
			try {
				if ( (c != null) && (!c.isClosed()) ) {
					c.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			s.cleanupThreadActive = false;
		}
		
	}
	
}
