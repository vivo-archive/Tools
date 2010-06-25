/*******************************************************************************
 * Copyright (c) 2010 Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the new BSD license
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/bsd-license.html
 * Contributors:
 * Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams - initial API and implementation
 ******************************************************************************/
package org.vivoweb.ingest.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 */
public class JenaRecordHandler extends RecordHandler {
	
	protected Model model;
	protected String nameSpace;
	protected Property recType;
	protected Property idType;
	protected Property dataType;
	protected Property isA;
	
	/**
	 * Default Constructor
	 */
	public JenaRecordHandler() {
		
	}
	
	/**
	 * Constructor (w/ Named Model)
	 * @param jdbcDriverClass jdbc driver class
	 * @param connType type of jdbc connection
	 * @param host host to conenct to
	 * @param port port to connect on
	 * @param dbName name of the database
	 * @param modelName name of the model
	 * @param username username to use
	 * @param password password to use
	 * @param dbType ex:"MySQL"
	 */
	public JenaRecordHandler(String jdbcDriverClass, String connType, String host, String port, String dbName,
			String username, String password, String dbType, String modelName) {
		this.model = new JenaConnect("jdbc:" + connType + "://" + host + ":" + port + "/" + dbName, username, password,
				modelName, dbType, jdbcDriverClass).getJenaModel();
		initVars();
	}
	
	/**
	 * Constructor (w/o Named Model)
	 * @param jdbcDriverClass jdbc driver class
	 * @param connType type of jdbc connection
	 * @param host host to conenct to
	 * @param port port to connect on
	 * @param dbName name of the database
	 * @param username username to use
	 * @param password password to use
	 * @param dbType ex:"MySQL"
	 */
	public JenaRecordHandler(String jdbcDriverClass, String connType, String host, String port, String dbName,
			String username, String password, String dbType) {
		this.model = new JenaConnect("jdbc:" + connType + "://" + host + ":" + port + "/" + dbName, username, password,
				dbType, jdbcDriverClass).getJenaModel();
		initVars();
	}
	
	/**
	 * Constructor (w/ Model Config File)
	 * @param configFile the model config file
	 * @throws IOException error connecting
	 * @throws SAXException xml parse error
	 * @throws ParserConfigurationException xml parse error
	 */
	public JenaRecordHandler(String configFile) throws ParserConfigurationException, SAXException, IOException {
		this.model = JenaConnect.parseConfig(configFile).getJenaModel();
		initVars();
	}
	
	private void initVars() {
		this.nameSpace = "http://ingest.vivoweb.org/util/jenarecordhandler#";
		this.recType = this.model.createProperty(this.nameSpace, "record");
		this.idType = this.model.createProperty(this.nameSpace, "idField");
		this.dataType = this.model.createProperty(this.nameSpace, "dataField");
		this.isA = this.model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#","type");
	}
	
	@Override
	public void addRecord(Record rec) throws IOException {
		Resource record = this.model.createResource();
		this.model.add(this.model.createStatement(record, this.isA, this.recType));
		this.model.add(this.model.createStatement(record, this.idType, rec.getID()));
		this.model.add(this.model.createStatement(record, this.dataType, rec.getData()));
	}
	
	@Override
	public void delRecord(String recID) throws IOException {
		// create query string
		String sQuery = ""
				+ "PREFIX ns: <"+this.nameSpace+"> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "DELETE { "
				+ "  ?record ?p ?v "
				+ "} "
				+ "WHERE { "
				+ "  ?record rdf:type ns:"+this.recType.getLocalName()+" . "
				+ "  ?record ns:"+this.idType.getLocalName()+" \""+recID+"\" . "
				+ "  ?record ?p ?v . "
				+ "}";
		UpdateRequest ur = UpdateFactory.create(sQuery);
		UpdateAction.execute(ur, this.model);
	}
	
	@Override
	public String getRecordData(String recID) throws IllegalArgumentException, IOException {
		// create query string
		String sQuery = ""
				+ "PREFIX ns: <"+this.nameSpace+"> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "Select ?dataField "
				+ "WHERE { "
				+ "  ?record rdf:type ns:"+this.recType.getLocalName()+" . "
				+ "  ?record ns:"+this.idType.getLocalName()+" \""+recID+"\" . "
				+ "  ?record ns:"+this.dataType.getLocalName()+" ?dataField . "
				+ "}";
		
		// create query
		Query query = QueryFactory.create(sQuery);
		
		// execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, this.model);
		ResultSet resultSet = qe.execSelect();
		
		// read first result
		String data = null;
		if(resultSet.hasNext()) {
			QuerySolution result = resultSet.next();
			data = result.getLiteral(resultSet.getResultVars().get(0)).getString();
		}
		return data;
	}
	
	@Override
	public Iterator<Record> iterator() {
		return new JenaRecordIterator();
	}
	
	private class JenaRecordIterator implements Iterator<Record> {
		
		private ResultSet resultSet;
		
		protected JenaRecordIterator() {
		// create query string
			String sQuery = ""
					+ "PREFIX ns: <"+JenaRecordHandler.this.nameSpace+"> "
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
					+ "Select ?idField "
					+ "WHERE { "
					+ "  ?record rdf:type ns:"+JenaRecordHandler.this.recType.getLocalName()+" . "
					+ "  ?record ns:"+JenaRecordHandler.this.idType.getLocalName()+" ?idField . "
					+ "  ?record ns:"+JenaRecordHandler.this.dataType.getLocalName()+" ?dataField . "
					+ "}";
			
			// create query
			Query query = QueryFactory.create(sQuery);
			
			// execute the query and obtain results
			QueryExecution qe = QueryExecutionFactory.create(query, JenaRecordHandler.this.model);
			this.resultSet = qe.execSelect();
		}
		
		public boolean hasNext() {
			return this.resultSet.hasNext();
		}
		
		public Record next() {
			try {
				QuerySolution querySol = this.resultSet.next();
				List<String> resultVars = this.resultSet.getResultVars();
				String var = resultVars.get(0);
				Literal lit = querySol.getLiteral(var);
				String id = lit.getString();
				Record result = getRecord(id);
				return result;
			} catch(IOException e) {
				throw new NoSuchElementException(e.getMessage());
			}
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public void setParams(Map<String, String> params) throws IllegalArgumentException, IOException {
		String jenaConfig = getParam(params,"jenaConfig",false);
		if(jenaConfig != null) {
			try {
				this.model = JenaConnect.parseConfig(jenaConfig).getJenaModel();
			} catch(ParserConfigurationException e) {
				throw new IllegalArgumentException(e);
			} catch(SAXException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			String jdbcDriverClass = getParam(params,"jdbcDriverClass",true);
			String connType = getParam(params,"connType",true);
			String host = getParam(params,"host",true);
			String port = getParam(params,"port",true);
			String dbName = getParam(params,"dbName",true);
			String username = getParam(params,"username",true);
			String password = getParam(params,"password",true);
			String dbType = getParam(params,"dbType",true);
			String modelName = getParam(params,"modelName",false);
			if(modelName != null) {
				this.model = new JenaConnect("jdbc:" + connType + "://" + host + ":" + port + "/" + dbName, username, password,
						modelName, dbType, jdbcDriverClass).getJenaModel();
			} else {
				this.model = new JenaConnect("jdbc:" + connType + "://" + host + ":" + port + "/" + dbName, username, password,
						dbType, jdbcDriverClass).getJenaModel();
			}
		}
		initVars();
	}
	
}
