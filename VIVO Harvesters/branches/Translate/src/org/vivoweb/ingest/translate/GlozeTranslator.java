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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

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
 *
 */
public class GlozeTranslator extends Translator{

	/**
	 * 
	 */
	//protected String incomingXMLStr;
	protected File incomingXMLFile;
	protected File incomingSchema;
	protected URI uriBase;

	/*public void setIncomingXMLStr(String xmlStr){
		this.incomingXMLStr = xmlStr;
	}*/
	
	public void setIncomingXMLFile(File xmlFile){
		this.incomingXMLFile = xmlFile;
	}
	
	public void setIncomingSchema(File schema){
		this.incomingSchema = schema;
	}
	
	public void setURIBase(String base){
		try {
			this.uriBase = new URI(base);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			log.error(e.getStackTrace().toString());
		}
	}
	
	/**
	 * 
	 */
	public GlozeTranslator() {
	}
	
	/*public void translateString(){
	}*/
	
	public void translateFile(){
		Gloze gl = new Gloze();
		
		Model outputModel = ModelFactory.createDefaultModel();
		
		try {
			gl.xml_to_rdf(incomingXMLFile, new File("test"), this.uriBase , outputModel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		outputModel.write(System.out);
	}
	
	/**
	 * 
	 */
	public void execute() {
		/*if(incomingXMLStr != null){
			translateString();
		}
		
		else if(incomingXMLFile != null) {*/
			translateFile();
		//}
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length <= 1 || args.length >= 4) {
			  log.error("Invalid Arguments: GlozeTranslate requires at least 2 arguments.  The system was supplied with " + args.length);
			  throw new IllegalArgumentException();
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
				//TODO create a record handler from the third argument
				//TODO talk to chris about the parameters needed for Gloze to use record handler
				//TODO create a record handler from the fourth argument
				glTrans.execute();
			}
			else {
				log.error("Invalid Arguments: Translate option " + args[0] + " not handled.");
				throw new IllegalArgumentException();
			}		
		}
	}
}
