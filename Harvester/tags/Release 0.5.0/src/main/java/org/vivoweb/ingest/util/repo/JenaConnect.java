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
package org.vivoweb.ingest.util.repo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.SQLException;
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

/**
 * Connection Helper for Jena Models
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 */
public class JenaConnect {
	
	/**
	 * Log4J Logger
	 */
	private static Log log = LogFactory.getLog(JenaConnect.class);
	/**
	 * Model we are connecting to
	 */
	private Model jenaModel;
	/**
	 * The jdbc connection
	 */
	private IDBConnection conn;
	
	/**
	 * Constructor (Memory Default Model)
	 */
	public JenaConnect() {
		this.setJenaModel(ModelFactory.createMemModelMaker().createDefaultModel());
	}
	
	/**
	 * Constructor (Memory Named Model)
	 * @param modelName the model name to use
	 */
	public JenaConnect(String modelName) {
		this.setJenaModel(ModelFactory.createMemModelMaker().createModel(modelName));
	}
	
	/**
	 * Constructor (DB Default Model)
	 * @param dbUrl jdbc connection url
	 * @param dbUser username to use
	 * @param dbPass password to use
	 * @param dbType database type ex:"MySQL"
	 * @param dbClass jdbc driver class
	 */
	public JenaConnect(String dbUrl, String dbUser, String dbPass, String dbType, String dbClass) {
		try {
			this.setJenaModel(initModel(initDB(dbUrl, dbUser, dbPass, dbType, dbClass)).createDefaultModel());
		} catch(InstantiationException e) {
			log.error(e.getMessage(), e);
		} catch(IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch(ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Constructor (DB Named Model)
	 * @param dbUrl jdbc connection url
	 * @param dbUser username to use
	 * @param dbPass password to use
	 * @param dbType database type ex:"MySQL"
	 * @param dbClass jdbc driver class
	 * @param modelName the model to connect to
	 */
	public JenaConnect(String dbUrl, String dbUser, String dbPass, String dbType, String dbClass, String modelName) {
		try {
			this.setJenaModel(initModel(initDB(dbUrl, dbUser, dbPass, dbType, dbClass)).openModel(modelName, false));
		} catch(InstantiationException e) {
			log.error(e.getMessage(), e);
		} catch(IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch(ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Constructor (connects to the same jena triple store as another jena connect, but uses a different named model)
	 * @param old the other jenaconnect
	 * @param modelName the model name to use
	 * @throws IOException unable to secure db connection
	 */
	public JenaConnect(JenaConnect old, String modelName) throws IOException {
		if(old.conn != null) {
			try {
				this.setJenaModel(initModel(new DBConnection(old.conn.getConnection(), old.conn.getDatabaseType())).openModel(modelName, false));
			} catch(SQLException e) {
				throw new IOException(e);
			}
		} else {
			this.setJenaModel(ModelFactory.createMemModelMaker().createModel(modelName));
		}
	}
	
	/**
	 * Constructor (Load rdf from input stream)
	 * @param in input stream to load rdf from
	 */
	public JenaConnect(InputStream in) {
		this();
		this.loadRDF(in);
	}
	
	/**
	 * Config File Based Factory
	 * @param configFile the vfs config file descriptor
	 * @return JenaConnect instance
	 * @throws IOException error connecting
	 * @throws SAXException xml parse error
	 * @throws ParserConfigurationException xml parse error
	 */
	public static JenaConnect parseConfig(FileObject configFile) throws ParserConfigurationException, SAXException, IOException {
		return build(new JenaConnectConfigParser().parseConfig(configFile.getContent().getInputStream()));
	}
	
	/**
	 * Config File Based Factory
	 * @param configFile the config file descriptor
	 * @return JenaConnect instance
	 * @throws IOException error connecting
	 * @throws SAXException xml parse error
	 * @throws ParserConfigurationException xml parse error
	 */
	public static JenaConnect parseConfig(File configFile) throws ParserConfigurationException, SAXException, IOException {
		return parseConfig(VFS.getManager().resolveFile(new File("."), configFile.getAbsolutePath()));
	}
	
	/**
	 * Config File Based Factory
	 * @param configFileName the config file path
	 * @return JenaConnect instance
	 * @throws ParserConfigurationException error connecting
	 * @throws SAXException xml parse error
	 * @throws IOException xml parse error
	 */
	public static JenaConnect parseConfig(String configFileName) throws ParserConfigurationException, SAXException, IOException {
		return parseConfig(VFS.getManager().resolveFile(new File("."), configFileName));
	}
	
	/**
	 * Build a JenaConnect based on the given parameter set
	 * @param params the parameter set
	 * @return the JenaConnect
	 */
	private static JenaConnect build(Map<String,String> params) {
		if(!params.containsKey("type")){
			throw new IllegalArgumentException("must define type!");
		}
		JenaConnect jc;
		if(params.get("type").equalsIgnoreCase("memory")) {
			if(params.containsKey("modelName")) {
				jc = new JenaConnect(params.get("modelName"));
			} else {
				jc = new JenaConnect();
			}
		} else if(params.get("type").equalsIgnoreCase("db")) {
			if(params.containsKey("modelName")) {
				jc = new JenaConnect(params.get("dbUrl"), params.get("dbUser"), params.get("dbPass"), params.get("dbType"), params.get("dbClass"), params.get("modelName"));
			} else {
				jc = new JenaConnect(params.get("dbUrl"), params.get("dbUser"), params.get("dbPass"), params.get("dbType"), params.get("dbClass"));
			}
		} else {
			throw new IllegalArgumentException("unknown type: "+params.get("type"));
		}
		return jc;
	}
	
	/**
	 * Load in RDF
	 * @param in input stream to read rdf from
	 */
	public void loadRDF(InputStream in) {
		getJenaModel().read(in, null);
		log.info("RDF Data was loaded");
	}
	
	/**
	 * Export all RDF
	 * @param out output stream to write rdf to
	 */
	public void exportRDF(OutputStream out) {
		RDFWriter fasterWriter = this.jenaModel.getWriter("RDF/XML");
		fasterWriter.setProperty("showXmlDeclaration","true");
		fasterWriter.setProperty("allowBadURIs", "true");
		fasterWriter.setProperty("relativeURIs", "");
		OutputStreamWriter osw = new OutputStreamWriter(out, Charset.availableCharsets().get("UTF-8"));
		fasterWriter.write(this.jenaModel, osw, "");
		log.info("RDF/XML Data was exported");
	}
	
	/**
	 * Adds all records in a RecordHandler to the model
	 * @param rh the RecordHandler to pull records from
	 */
	public void importRDF(RecordHandler rh) {
		for(Record r : rh) {
			ByteArrayInputStream bais = new  ByteArrayInputStream(r.getData().getBytes());
			this.getJenaModel().read(bais,null);
		}
	}
	
	/**
	 * Closes the model and the jdbc connection
	 */
	public void close() {
		this.jenaModel.close();
		try {
			this.conn.close();
		} catch(Exception e) {
			//ignore
		}
	}
	
	/**
	 * Accessor for Jena Model
	 * @return the Jena Model
	 */
	public Model getJenaModel() {
		return this.jenaModel;
	}
	
	/**
	 * Setter
	 * @param jena the new model
	 */
	private void setJenaModel(Model jena) {
		this.jenaModel = jena;
	}
	
	/**
	 * Get ModelMaker for a database connection
	 * @param dbConn the database connection
	 * @return the ModelMaker
	 */
	private ModelMaker initModel(IDBConnection dbConn) {
		this.conn = dbConn;
		return ModelFactory.createModelRDBMaker(dbConn);
	}
	
	/**
	 * Setup database connection
	 * @param dbUrl url of server
	 * @param dbUser username to connect with
	 * @param dbPass password to connect with
	 * @param dbType database type
	 * @param dbClass jdbc connection class
	 * @return the database connection
	 * @throws InstantiationException could not instantiate
	 * @throws IllegalAccessException not authorized
	 * @throws ClassNotFoundException no such class
	 */
	private IDBConnection initDB(String dbUrl, String dbUser, String dbPass, String dbType, String dbClass) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName(dbClass).newInstance();
		return new DBConnection(dbUrl, dbUser, dbPass, dbType);
	}
	
	/**
	 * Config parser for Jena Models
	 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
	 */
	private static class JenaConnectConfigParser extends DefaultHandler {
		/**
		 * Param list from the config file
		 */
		private Map<String,String> params;
		/**
		 * temporary storage for cdata
		 */
		private String tempVal;
		/**
		 * temporary storage for param name
		 */
		private String tempParamName;
		
		/**
		 * Default Constructor
		 */
		protected JenaConnectConfigParser() {
			this.params = new HashMap<String,String>();
			this.tempVal = "";
			this.tempParamName = "";
		}
		
		/**
		 * Build a JenaConnect using the input stream data
		 * @param inputStream stream to read config from
		 * @return the JenaConnect described by the stream
		 * @throws ParserConfigurationException parser incorrectly configured
		 * @throws SAXException xml error
		 * @throws IOException error reading stream
		 */
		protected Map<String, String> parseConfig(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
			SAXParserFactory spf = SAXParserFactory.newInstance(); // get a factory
			SAXParser sp = spf.newSAXParser(); // get a new instance of parser
			sp.parse(inputStream, this); // parse the file and also register this class for call backs
			return this.params;
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
				this.params.put("type", "db");
			} else if(qName.equalsIgnoreCase("Param")) {
				this.params.put(this.tempParamName, this.tempVal);
			} else {
				throw new SAXException("Unknown Tag: "+qName);
			}
		}
	}
}