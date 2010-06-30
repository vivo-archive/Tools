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
package org.vivoweb.ingest.translate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.vivoweb.ingest.util.Record;
import org.vivoweb.ingest.util.RecordHandler;

import com.hp.gloze.Gloze;
import com.hp.hpl.jena.rdf.model.*;

/**
 * Gloze Tranlator
 * This class translates XML into its own natrual RDF ontology
 * using the gloze library.  Translation into the VIVO ontology
 * is completed using the RDF Translator.
 * 
 * TODO:  Identify additional parameters required for translation
 * TODO:  Identify methods to invoke in the gloze library
 * 
 * @author Stephen V. Williams swilliams@ctrip.ufl.edu
 */
public class GlozeTranslator extends Translator{

	/**
	 * 
	 */
	//FIXME remove this and use the incoming stream if possible
	protected File incomingXMLFile;				
	protected File incomingSchema;
	protected URI uriBase;

	/**
	 * @param xmlFile the file to translate
	 */
	public void setIncomingXMLFile(File xmlFile){
		this.incomingXMLFile = xmlFile;
	}
	
	/**
	 * 
	 * @param schema the schema that gloze can use, but doesn't need to translate the xml
	 */
	public void setIncomingSchema(File schema){
		this.incomingSchema = schema;
	}
	
	/**
	 * 
	 * @param base the base uri to apply to all relative entities
	 */
	public void setURIBase(String base){
		try {
			this.uriBase = new URI(base);
		} catch (URISyntaxException e) {
			log.error("",e);
		}
	}
	
	/**
	 * 
	 */
	public GlozeTranslator() {
	}
	
	
	public void translateFile(){
		Gloze gl = new Gloze();
		
		Model outputModel = ModelFactory.createDefaultModel();
		
		try {
			//TODO move this to use the instream instead of this incomingXML File
			gl.xml_to_rdf(incomingXMLFile, new File("test"), this.uriBase , outputModel);
		} catch (Exception e) {
			log.error("",e);
		}
		
		outputModel.write(this.outStream);
	}
	
	/**
	 * 
	 */
	public void execute() {
		if (this.uriBase != null && this.incomingXMLFile != null ){	
			log.info("Translation: Start");
			
			translateFile();
			
			log.info("Translation: End");
		}
		else {
			log.error("Invalid Arguments: Gloze Translation requires a URIBase and XMLFile");
			throw new IllegalArgumentException();
		}
	}
	
	
	public void parseArgsExecute(String[] args){
		if (args.length < 2 || args.length > 4) {
			  log.error("Invalid Arguments: GlozeTranslate requires at least 2 arguments.  The system was supplied with " + args.length);
		}
		else {
			GlozeTranslator glTrans = new GlozeTranslator();
			
			if (args[0].equals("-f")) {			
				glTrans.setURIBase(args[1]);
				glTrans.setIncomingXMLFile(new File(args[2]));
				if (args.length == 4) {					
					glTrans.setIncomingSchema(new File(args[3]));
				}
				
				glTrans.execute();
			}
			else if (args[0].equals("-rh")) {
				try {
				glTrans.setURIBase(args[1]);
				
				//pull in the translation xsl
				if (!args[2].equals("") && args[2] != null){
					glTrans.setIncomingSchema(new File(args[2]));
				}
				
				//create record handlers
				RecordHandler inStore = RecordHandler.parseConfig(args[1]);
				RecordHandler outStore = RecordHandler.parseConfig(args[3]);
				
				//create a output stream for writing to the out store
				ByteArrayOutputStream buff = new ByteArrayOutputStream();
				
				// get from the in record and translate
				for(Record r : inStore){
					glTrans.setInStream(new ByteArrayInputStream(r.getData().getBytes()));
					glTrans.setOutStream(buff);
					glTrans.execute();
					buff.flush();
					outStore.addRecord(r.getID(),buff.toString());
					buff.reset();
				}
				
				buff.close();
				}
				catch (Exception e) {
					log.error("",e);
				}
			}
			else {
				log.error("Invalid Arguments: Translate option " + args[0] + " not handled.");
				//throw new IllegalArgumentException();
			}		
		}
	}
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GlozeTranslator glTrans = new GlozeTranslator();
		glTrans.parseArgsExecute(args);		
	}
}
