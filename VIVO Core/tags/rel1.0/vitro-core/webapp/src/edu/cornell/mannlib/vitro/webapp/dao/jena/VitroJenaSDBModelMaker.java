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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.graph.GraphMaker;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.ModelReader;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

public class VitroJenaSDBModelMaker implements ModelMaker {

	// TODO: need to rethink the inheritance/interfaces here
	
	private Store store = null;
	
	public static final String METADATA_MODEL_URI = "http://vitro.mannlib.cornell.edu/ns/vitro/sdb/metadata";
	public static final String HAS_NAMED_MODEL_URI = "http://vitro.mannlib.cornell.edu/ns/vitro/sdb/hasNamedModel";
	
	private Model metadataModel;
	private Resource sdbResource; // a resource representing the SDB database 
	
	public VitroJenaSDBModelMaker(Store store) {
		this.store = store;
		try {
			Model meta = getModel(METADATA_MODEL_URI);
			// Test query to see if the database has been initialized
			meta.listStatements(null, RDF.type, OWL.Nothing); 
		} catch (Exception e) {
			// initialize the store
			store.getTableFormatter().create();
        	store.getTableFormatter().truncate();
		}
		
		this.metadataModel = getModel(METADATA_MODEL_URI);
		
		if (metadataModel.size()==0) {
			// set up the model name metadata to avoid expensive calls to listNames()
			Resource sdbRes = metadataModel.createResource(); 
			this.sdbResource = sdbRes;
			Iterator nameIt = SDBFactory.connectDataset(store).listNames();
			while (nameIt.hasNext()) {
				String name = (String) nameIt.next();
				metadataModel.add(sdbResource,metadataModel.getProperty(HAS_NAMED_MODEL_URI),name);
			}
		} else {
			StmtIterator stmtIt = metadataModel.listStatements((Resource)null, metadataModel.getProperty(HAS_NAMED_MODEL_URI),(RDFNode)null);
			if (stmtIt.hasNext()) {
				Statement stmt = stmtIt.nextStatement();
				sdbResource = stmt.getSubject();
			}
			stmtIt.close();
		}
	}
	
	public void close() {
		store.close();
	}

	public Model createModel(String arg0) {
		Model model = SDBFactory.connectNamedModel(store, arg0);
		metadataModel.add(sdbResource,metadataModel.getProperty(HAS_NAMED_MODEL_URI),arg0);
		return model;
	}

	public Model createModel(String arg0, boolean arg1) {
		// TODO Figure out if we can offer a "create if not found" option using SDB
		return createModel(arg0);
	}

	public GraphMaker getGraphMaker() {
		throw new UnsupportedOperationException("GraphMaker not supported by "+this.getClass().getName());
	}

	public boolean hasModel(String arg0) {
		StmtIterator stmtIt = metadataModel.listStatements(sdbResource,metadataModel.getProperty(HAS_NAMED_MODEL_URI),arg0);
		try {
			return stmtIt.hasNext();
		} finally {
			if (stmtIt != null) {
				stmtIt.close();
			}
		}
	}

	public ExtendedIterator listModels() {
		List<String> nameList = new LinkedList<String>();
		Iterator nameIt = metadataModel.listObjectsOfProperty(metadataModel.getProperty(HAS_NAMED_MODEL_URI));
		while (nameIt.hasNext()) {
			RDFNode obj = (RDFNode) nameIt.next();
			if (obj.isLiteral()) {
				nameList.add( ((Literal)obj).getLexicalForm() );
			}
		}
		return WrappedIterator.create(nameList.iterator());
	}

	public Model openModel(String arg0, boolean arg1) {
		return SDBFactory.connectNamedModel(store,arg0);
	}

	public void removeModel(String arg0) {
		Model m = getModel(arg0);
		m.removeAll(null,null,null);
		metadataModel.remove(sdbResource,metadataModel.getProperty(HAS_NAMED_MODEL_URI),metadataModel.createLiteral(arg0));
	}

	public Model addDescription(Model arg0, Resource arg1) {
		throw new UnsupportedOperationException("addDescription not supported by "+this.getClass().getName());
	}

	public Model createModelOver(String arg0) {
		throw new UnsupportedOperationException("createModelOver not supported by "+this.getClass().getName());
	}

	public Model getDescription() {
		throw new UnsupportedOperationException("createModelOver not supported by "+this.getClass().getName());
	}

	public Model getDescription(Resource arg0) {
		throw new UnsupportedOperationException("getDescription not supported by "+this.getClass().getName());
	}

	public Model openModel() {
		return SDBFactory.connectDefaultModel(store);
	}

	public Model createDefaultModel() {
		return SDBFactory.connectDefaultModel(store);
	}

	public Model createFreshModel() {
		throw new UnsupportedOperationException("createFreshModel not supported by "+this.getClass().getName());
	}

	/**
	 * @deprecated
	 */
	public Model createModel() {
		return SDBFactory.connectDefaultModel(store);
	}

	/**
	 * @deprecated
	 */
	public Model getModel() {
		return SDBFactory.connectDefaultModel(store);
	}

	public Model openModel(String arg0) {
		return SDBFactory.connectDefaultModel(store);
	}

	public Model openModelIfPresent(String arg0) {
		return (this.hasModel(arg0)) ? SDBFactory.connectNamedModel(store,arg0) : null;
	}

	public Model getModel(String arg0) {
		return SDBFactory.connectNamedModel(store, arg0);
	}

	public Model getModel(String arg0, ModelReader arg1) {
		throw new UnsupportedOperationException("getModel(String,ModelReader) not supported by "+this.getClass().getName());
	}

}
