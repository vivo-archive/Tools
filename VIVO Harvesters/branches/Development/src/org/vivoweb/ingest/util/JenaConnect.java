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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
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
	 * Config File Based Factory
	 * @param configFile
	 * @return JenaConnect instance
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static JenaConnect parseConfig(FileObject configFile) throws ParserConfigurationException, SAXException, IOException {
		return new JenaConnectConfigParser().parseConfig(configFile.getContent().getInputStream());
	}
	
	/**
	 * Config File Based Factory
	 * @param configFile
	 * @return 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static JenaConnect parseConfig(File configFile) throws ParserConfigurationException, SAXException, IOException {
		return parseConfig(VFS.getManager().resolveFile(new File("."), configFile.getAbsolutePath()));
	}
	
	/**
	 * @param configFileName
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static JenaConnect parseConfig(String configFileName) throws ParserConfigurationException, SAXException, IOException {
		return parseConfig(VFS.getManager().resolveFile(new File("."), configFileName));
	}
	
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
		log.info("RDF Data was loaded");
	}
	
	/**
	 * @param out
	 */
	public void exportRDF(OutputStream out) {
		RDFWriter fasterWriter = this.jenaModel.getWriter("RDF/XML");
		fasterWriter.setProperty("allowBadURIs", "true");
		fasterWriter.setProperty("relativeURIs", "");
		fasterWriter.write(this.jenaModel, out, "");
		log.info("RDF/XML Data was exported");
	}
	
	/**
	 * Adds all records in a RecordHandler to the model
	 * @param rh the RecordHandler to pull records from
	 */
	public void importRDF(RecordHandler rh) {
		for(Record r : rh) {
			this.getJenaModel().read(r.getData());
		}
	}
	
	private static class JenaConnectConfigParser extends DefaultHandler {
		private JenaConnect jc;
		private Map<String,String> params;
		private String tempVal;
		private String tempParamName;
		
		protected JenaConnectConfigParser() {
			this.params = new HashMap<String,String>();
			this.tempVal = "";
			this.tempParamName = "";
		}
		
		protected JenaConnect parseConfig(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
			SAXParserFactory spf = SAXParserFactory.newInstance(); // get a factory
			SAXParser sp = spf.newSAXParser(); // get a new instance of parser
			JenaConnectConfigParser p = new JenaConnectConfigParser();
			sp.parse(inputStream, p); // parse the file and also register this class for call backs
			return p.jc;
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			this.tempVal = "";
			this.tempParamName = "";
			if(qName.equalsIgnoreCase("Param")) {
				this.tempParamName = attributes.getValue("name");
			} else if(!qName.equalsIgnoreCase("Model")) {
				throw new SAXException("Unknown Tag: "+qName);
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			this.tempVal = new String(ch, start, length);
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if(qName.equalsIgnoreCase("Model")) {
				try {
					if(this.params.containsKey("modelName")) {
						this.jc = new JenaConnect(this.params.get("dbUrl"), this.params.get("dbUser"), this.params.get("dbPass"), this.params.get("modelName"), this.params.get("dbType"), this.params.get("dbClass"));
					} else {
						this.jc = new JenaConnect(this.params.get("dbUrl"), this.params.get("dbUser"), this.params.get("dbPass"), this.params.get("dbType"), this.params.get("dbClass"));
					}
				} catch(SecurityException e) {
					throw new SAXException(e.getMessage(),e);
				} catch(IllegalArgumentException e) {
					throw new SAXException(e.getMessage(),e);
				}
			} else if(qName.equalsIgnoreCase("Param")) {
				this.params.put(this.tempParamName, this.tempVal);
			} else {
				throw new SAXException("Unknown Tag: "+qName);
			}
		}
	}
	
}
