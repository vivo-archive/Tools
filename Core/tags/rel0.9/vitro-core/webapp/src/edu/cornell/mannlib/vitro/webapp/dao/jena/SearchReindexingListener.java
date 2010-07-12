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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.IndividualCreationEvent;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.IndividualDeletionEvent;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.IndividualEditEvent;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.IndividualUpdateEvent;
import edu.cornell.mannlib.vitro.webapp.search.indexing.IndexBuilder;


public class SearchReindexingListener implements ModelChangedListener {
	
	private static final Log log = LogFactory.getLog(SearchReindexingListener.class.getName());	
	
	private OntModel ontModel;
	private ServletContext servletContext;
	
	protected DateFormat xsdDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	private boolean dirty = false;
	private boolean indexing = false;

	public SearchReindexingListener(OntModel ontModel, ServletContext sc) {
		this.ontModel = ontModel;
		this.servletContext = sc;
	}	
	
	private class Reindexer implements Runnable {
		public void run() {
			while(dirty) {
				dirty = false;
				IndexBuilder builder = (IndexBuilder) servletContext.getAttribute(IndexBuilder.class.getName());
				indexing = true;
				try {
		        builder.run();
				} finally {
					indexing = false;
				}
			}
		}
	}
	
	public void addedStatement(Statement arg0) {

	}

	
	public void addedStatements(Statement[] arg0) {
		// TODO Auto-generated method stub

	}

	
	public void addedStatements(List arg0) {
		// TODO Auto-generated method stub

	}

	
	public void addedStatements(StmtIterator arg0) {
		// TODO Auto-generated method stub

	}

	
	public void addedStatements(Model arg0) {
		// TODO Auto-generated method stub

	}

	
	public void notifyEvent(Model arg0, Object arg1) {
		if ((arg1 instanceof IndividualCreationEvent) || (arg1 instanceof IndividualUpdateEvent)) {
			IndividualEditEvent ee = (IndividualEditEvent) arg1;
			if (!ee.getBegin()) {
				dirty=true;
				if (!indexing) {
					new Thread(new Reindexer()).start();
				}
			}
		} else if (arg1 instanceof IndividualDeletionEvent) {
			IndividualEditEvent ee = (IndividualEditEvent) arg1;
	        IndexBuilder builder = (IndexBuilder) servletContext.getAttribute(IndexBuilder.class.getName());
	        if (builder != null) {
	        	builder.entityDeleted(ee.getIndividualURI());
	        } else {
	        	log.warn("Unable to remove individual from search index: no attribute " + IndexBuilder.class.getName() + " in servlet context");
	        }
		}
	}

	
	public void removedStatement(Statement arg0) {
		// TODO Auto-generated method stub

	}

	
	public void removedStatements(Statement[] arg0) {
		// TODO Auto-generated method stub

	}

	
	public void removedStatements(List arg0) {
		// TODO Auto-generated method stub

	}

	
	public void removedStatements(StmtIterator arg0) {
		// TODO Auto-generated method stub

	}

	
	public void removedStatements(Model arg0) {
		// TODO Auto-generated method stub

	}

}
