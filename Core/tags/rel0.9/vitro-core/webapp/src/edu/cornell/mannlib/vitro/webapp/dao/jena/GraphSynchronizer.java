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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.graph.GraphListener;
import com.hp.hpl.jena.graph.Triple;

import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;

public class GraphSynchronizer implements GraphListener {

	private static final Log log = LogFactory.getLog(GraphSynchronizer.class.getName());
	
	private Graph g;
	
	public GraphSynchronizer (Graph synchronizee) {
		g = synchronizee;
	}
	
	public void notifyAddArray(Graph arg0, Triple[] arg1) {
		g.getBulkUpdateHandler().add(arg1);
	}

	
	public void notifyAddGraph(Graph arg0, Graph arg1) {
		g.getBulkUpdateHandler().add(arg1);

	}

	
	public void notifyAddIterator(Graph arg0, Iterator arg1) {
		g.getBulkUpdateHandler().add(arg1);
	}

	
	public void notifyAddList(Graph arg0, List arg1) {
		g.getBulkUpdateHandler().add(arg1);
	}

	public void notifyAddTriple(Graph arg0, Triple arg1) {
		g.add(arg1);
	}

	
	public void notifyDeleteArray(Graph arg0, Triple[] arg1) {
		g.getBulkUpdateHandler().delete(arg1);
	}

	
	public void notifyDeleteGraph(Graph arg0, Graph arg1) {
		g.getBulkUpdateHandler().delete(arg1);
	}

	public void notifyDeleteIterator(Graph arg0, Iterator arg1) {
		g.getBulkUpdateHandler().delete(arg1);
	}

	public void notifyDeleteList(Graph arg0, List arg1) {
		g.getBulkUpdateHandler().delete(arg1);
	}
	
	public void notifyDeleteTriple(Graph arg0, Triple arg1) {
		g.delete(arg1);
		log.trace("Delete triple");
		if (arg1.getObject().isLiteral()) {
			log.trace(arg1.getObject().getLiteralLexicalForm());
		} else if (arg1.getObject().isVariable()) {
			log.trace("Triple object is variable");
		} else if (arg1.getObject().isURI()) {
			log.trace(arg1.getObject().getURI());
		} else if (arg1.getObject().isBlank()) {
			log.trace("Triple object is blank");
		} else if (arg1.getObject().isConcrete()) {
			log.trace("Triple object is concrete");
		} 
		log.trace(arg1.getObject().toString());
	}
	
	public void notifyEvent(Graph arg0, Object arg1) {
		// ontModel.removeAll(s,p,o) doesn't trigger a notifyDeleteTriple() !?!
		// So, I'm doing this silly thing to make sure we see the deletion.
		// What else is lurking around the corner?
		if (arg1 instanceof GraphEvents) {
			GraphEvents ge = ((GraphEvents) arg1);
			Object content = ge.getContent();
			if (ge.getTitle().equals("remove") && content instanceof Triple) {
				notifyDeleteTriple( arg0, (Triple) content );
			}
		}
	}

}
