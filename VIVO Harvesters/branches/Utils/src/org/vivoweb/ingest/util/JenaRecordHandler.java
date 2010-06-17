/*******************************************************************************
 * Copyright (c) 2010 Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the new BSD license
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/bsd-license.html
 * 
 * Contributors:
 *     Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams - initial API and implementation
 ******************************************************************************/
package org.vivoweb.ingest.util;

import java.io.IOException;
import java.util.Iterator;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public class JenaRecordHandler extends RecordHandler {
	
	/**
	 * 
	 */
	private Model model;
	/**
	 * 
	 */
	private String recordType;
	/**
	 * 
	 */
	private String idFieldType;
	/**
	 * 
	 */
	private String dataFieldType;
	
	/**
	 * @param connType 
	 * @param jdbcDriverClass 
	 * @param host 
	 * @param username 
	 * @param password 
	 * @param dbType 
	 * @param modelName 
	 * @param recordType 
	 * @param idFieldType 
	 * @param dataFieldType 
	 * 
	 */
	public JenaRecordHandler(String connType, String jdbcDriverClass, String host, String username, String password, String dbType, String modelName, String recordType, String idFieldType, String dataFieldType) {
		// TODO Auto-generated constructor stub
		setModel(new JenaConnect(host, username, password, modelName, dbType, jdbcDriverClass).getJenaModel());
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#addRecord(org.vivoweb.ingest.util.Record)
	 */
	@Override
	public void addRecord(Record rec) throws IOException {
		// TODO Auto-generated method stub
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#delRecord(java.lang.String)
	 */
	@Override
	public void delRecord(String recID) throws IOException {
		// TODO Auto-generated method stub
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#getRecordData(java.lang.String)
	 */
	@Override
	public String getRecordData(String recID) throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Record> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param recordType the recordType to set
	 */
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	/**
	 * @return the recordType
	 */
	public String getRecordType() {
		return this.recordType;
	}

	/**
	 * @param idFieldType the idFieldType to set
	 */
	public void setIdFieldType(String idFieldType) {
		this.idFieldType = idFieldType;
	}

	/**
	 * @return the idFieldType
	 */
	public String getIdFieldType() {
		return this.idFieldType;
	}

	/**
	 * @param dataFieldType the dataFieldType to set
	 */
	public void setDataFieldType(String dataFieldType) {
		this.dataFieldType = dataFieldType;
	}

	/**
	 * @return the dataFieldType
	 */
	public String getDataFieldType() {
		return this.dataFieldType;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return this.model;
	}
	
}
