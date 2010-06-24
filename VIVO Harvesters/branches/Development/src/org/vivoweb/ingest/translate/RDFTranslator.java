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

/**
 * RDF Tranlator
 * This class translates RDF from its current schema into 
 * the VIVO ontology.  It uses RDFworkflows to manipulate the
 * data
 * 
 * TODO:  Identify additional parameters required for translation
 * TODO:  Identify the method of invoking an RDF workflow in Jena
 * TODO:  Create a sample RDF workflow
 * 
 * @author Stephen V. Williams swilliams@ctrip.ufl.edu
 *
 */
public class RDFTranslator extends Translator{
	
	public void execute(){
		
	}
	
	/**
	 * The main method is for execution through the command line.  
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		if (args.length < 1){
			log.error("Invalid Arguments: RDFTranslate requires at least 1 arguments.  The system was supplied with " + args.length);
			throw new IllegalArgumentException();
		}
		else {
			
		}
	}

}
