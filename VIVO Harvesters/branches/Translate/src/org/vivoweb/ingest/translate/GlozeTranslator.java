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
		
		/*if (args.length >= 1) {
			  log.error("Invalid Arguments: GlozeTranslate requires 1");
			  throw new IllegalArgumentException();
		}
		else {*/
			GlozeTranslator glTrans = new GlozeTranslator();
			
			//process arguments takes 1 string or 1-2 files
			/*if (args[1].endsWith("</xml>")) {
				glTrans.setIncomingXMLStr(args[1]);
			}
			else {*/
				glTrans.setIncomingXMLFile(new File(args[1]));
				if (args.length >= 2) {					
					glTrans.setIncomingSchema(new File(args[2]));
				}
			//}
			try {
				glTrans.uriBase = new URI("http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed");
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
									
			glTrans.execute();
		//}
	}
}
