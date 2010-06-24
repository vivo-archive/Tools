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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.util.ModelLoader;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public class JenaConnect {
	
	private static Log log = LogFactory.getLog(JenaConnect.class);
	private Model jenaModel;
	
	/**
	 * @param dbUrl
	 * @param dbUser
	 * @param dbPass
	 * @param dbType
	 * @param dbClass
	 */
	public JenaConnect(String dbUrl, String dbUser, String dbPass, String dbType, String dbClass) {
		try {
			this.setJenaModel(this.createModel(dbUrl, dbUser, dbPass, dbType, dbClass));
		} catch(InstantiationException e) {
			log.error(e.getMessage(), e);
		} catch(IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch(ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * @param dbUrl
	 * @param dbUser
	 * @param dbPass
	 * @param modelName
	 * @param dbType
	 * @param dbClass
	 */
	public JenaConnect(String dbUrl, String dbUser, String dbPass, String modelName, String dbType, String dbClass) {
		try {
			this.setJenaModel(this.loadModel(dbUrl, dbUser, dbPass, modelName, dbType, dbClass));
		} catch(InstantiationException e) {
			log.error(e.getMessage(), e);
		} catch(IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch(ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * @param in
	 */
	public JenaConnect(InputStream in) {
		this.setJenaModel(ModelFactory.createDefaultModel());
		this.loadRDF(in);
	}
	
	/**
	 * @param inFilePath
	 * @throws FileNotFoundException
	 */
	public JenaConnect(String inFilePath) throws FileNotFoundException {
		this(new FileInputStream(inFilePath));
	}
	
	/**
	 * @return
	 */
	public Model getJenaModel() {
		return this.jenaModel;
	}

	private void setJenaModel(Model jena) {
		this.jenaModel = jena;
	}

	/**
	 * @param dbUrl
	 * @param dbUser
	 * @param dbPass
	 * @param dbType
	 * @param dbClass
	 * @return Model
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private Model createModel(String dbUrl, String dbUser, String dbPass, String dbType, String dbClass)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return initModel(initDB(dbUrl, dbUser, dbPass, dbType, dbClass)).createDefaultModel();
	}
	
	/**
	 * @param dbUrl
	 * @param dbUser
	 * @param dbPass
	 * @param modelName
	 * @param dbType
	 * @param dbClass
	 * @return Model
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private Model loadModel(String dbUrl, String dbUser, String dbPass, String modelName, String dbType, String dbClass)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return ModelLoader.connectToDB(dbUrl, dbUser, dbPass, modelName, dbType, dbClass);
	}
	
	/**
	 * @param dbUrl
	 * @param dbUser
	 * @param dbPass
	 * @param dbType
	 * @param dbClass
	 * @return IDBConnection
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private IDBConnection initDB(String dbUrl, String dbUser, String dbPass, String dbType, String dbClass)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName(dbClass).newInstance();
		return new DBConnection(dbUrl, dbUser, dbPass, dbType);
	}
	
	/**
	 * @param dbcon
	 * @return ModelMaker
	 */
	private ModelMaker initModel(IDBConnection dbcon) {
		return ModelFactory.createModelRDBMaker(dbcon);
	}
	
	/**
	 * @param in
	 */
	public void loadRDF(InputStream in) {
		this.getJenaModel().read(in, null);
		log.info("JenaIO: RDF Data was loaded");
	}
	
	/**
	 * @param out
	 */
	public void exportRDF(OutputStream out) {
		RDFWriter fasterWriter = this.jenaModel.getWriter("RDF/XML");
		fasterWriter.setProperty("allowBadURIs", "true");
		fasterWriter.setProperty("relativeURIs", "");
		fasterWriter.write(this.jenaModel, out, "");
		log.info("JenaIO: RDF/XML Data was exported");
	}
	
}
