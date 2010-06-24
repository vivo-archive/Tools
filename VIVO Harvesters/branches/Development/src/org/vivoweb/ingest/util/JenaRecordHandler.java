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
import java.util.NoSuchElementException;
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
	 * @param jdbcDriverClass
	 * @param connType
	 * @param host
	 * @param port
	 * @param modelName
	 * @param username
	 * @param password
	 * @param dbType
	 * @param recordType
	 * @param idFieldType
	 * @param dataFieldType
	 */
	public JenaRecordHandler(String jdbcDriverClass, String connType, String host, String port, String dbName,
			String username, String password, String dbType, String modelName, String recordType, String idFieldType,
			String dataFieldType) {
		this.model = new JenaConnect("jdbc:" + connType + "://" + host + ":" + port + "/" + dbName, username, password,
				modelName, dbType, jdbcDriverClass).getJenaModel();
		this.nameSpace = "http://"+host+"/"+dbType+"/"+dbName+"/"+modelName+"#";
		this.recType = this.model.createProperty(this.nameSpace, recordType);
		this.idType = this.model.createProperty(this.nameSpace, idFieldType);
		this.dataType = this.model.createProperty(this.nameSpace, dataFieldType);
		this.isA = this.model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#","type");
	}
	
	/**
	 * @param jdbcDriverClass
	 * @param connType
	 * @param host
	 * @param port
	 * @param dbName
	 * @param username
	 * @param password
	 * @param dbType
	 * @param recordType
	 * @param idFieldType
	 * @param dataFieldType
	 */
	public JenaRecordHandler(String jdbcDriverClass, String connType, String host, String port, String dbName,
			String username, String password, String dbType, String recordType, String idFieldType, String dataFieldType) {
		this.model = new JenaConnect("jdbc:" + connType + "://" + host + ":" + port + "/" + dbName, username, password,
				dbType, jdbcDriverClass).getJenaModel();
		this.nameSpace = "http://"+host+"/"+dbType+"/"+dbName+"/defaultModel"+"#";
		this.recType = this.model.createProperty(this.nameSpace, recordType);
		this.idType = this.model.createProperty(this.nameSpace, idFieldType);
		this.dataType = this.model.createProperty(this.nameSpace, dataFieldType);
		this.isA = this.model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#","type");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#addRecord(org.vivoweb.ingest.util.Record)
	 */
	@Override
	public void addRecord(Record rec) throws IOException {
		Resource record = this.model.createResource();
		this.model.add(this.model.createStatement(record, this.isA, this.recType));
		this.model.add(this.model.createStatement(record, this.idType, rec.getID()));
		this.model.add(this.model.createStatement(record, this.dataType, rec.getData()));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#delRecord(java.lang.String)
	 */
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
	
	/*
	 * (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#getRecordData(java.lang.String)
	 */
	@Override
	public String getRecordData(String recID) throws IllegalArgumentException, IOException {
		// create query string
		String sQuery = ""
				+ "PREFIX ns: <"+this.nameSpace+"> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "Select ?dataField "
				+ "WHERE { "
				+ "  ?record rdf:type ns:"+this.recType.getLocalName()+" . "
//				+ "  ?record ns:"+this.idType.getLocalName()+" ?idField . "
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
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
	
}
