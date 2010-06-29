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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*****************************************************************************************
 * VIVO Data Translator
 * @author Stephen V. Williams swilliams@ichp.ufl.edu
 * TODO MILESTONE2: create constructor for accepting a parameter list
 * TODO MILESTONE2: create constructor for accepting a parameter array
 * TODO MILESTONE1: test for error handling, failing completely and gently
 * ***************************************************************************************/
public class Translator {
	//This code was marked as may cause compile errors by UCDetector.
	//Change visibility of class to Default
	//FIXME This code was marked as may cause compile errors by UCDetector.
	
	protected static Log log = LogFactory.getLog(Translator.class);
	
	/**
	 * Source type may become depreciated depending on construction of additional translators
	 */
	protected String sourceType;
	//This code was marked as may cause compile errors by UCDetector.
	//Change visibility of Field Translator.sourceType to Private
	//FIXME This code was marked as may cause compile errors by UCDetector.
	
	/**
	 * in stream is the stream containing the file (xml) that we are going to translate
	 */
	protected InputStream inStream;
	
	/**
	 * out stream is the stream that the controller will be handling and were we will dump the translation
	 */
	protected OutputStream outStream;
	
	
	/***
	 * Empty constructor
	 */
	public Translator(){
		
	}
		
	/***
	 * Initial constructor for the translate method, it is not required to use this constructor
	 * but it is suggested, since not passing one of the variables would result in a error being thrown
	 * 
	 * @param sType Type of the file to be translated (for this exercise its always XML)
	 * @param transFile The file that contains the mapping for translation
	 * @param iStream the incoming stream that the file is passed into
	 * @param oStream the outgoing stream that the translation is passed to
	 */
	public Translator(String sType, InputStream iStream, OutputStream oStream){
		//This code was marked as never used by UCDetector.
		//FIXME Determine if this code is necessary.
		this.setSourceType(sType);
		this.setInStream(iStream);
		this.setOutStream(oStream);
	}
		
	/***
	 * Set Source Type
	 * sets the source type
	 * 
	 * @param sType String value of the source type, valid terms are XML
	 */
	public void setSourceType(String sType)
	{
		this.sourceType = sType;
	}
	
	
	/***
	 * Sets the input stream 
	 * 
	 * @param iStream the input stream
	 */
	public void setInStream(InputStream iStream) {
		this.inStream = iStream;
	}
	
	 /***
   * Sets the output stream 
   * 
   * @param oStream the output stream
   */
	public void setOutStream(OutputStream oStream) {
	  this.outStream = oStream;
	}
		
	
	/***
	 * This function will utilize the translation file to transform the outputstream
	 * @throws IllegalArgumentException when the system is not properly configured
	 */
	public void execute() throws IllegalArgumentException {
		
		//checking for valid input parameters
		if ((this.sourceType != null && !this.sourceType.equals("")) && 
				(this.inStream != null && this.outStream != null)) {
		
			log.trace((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(Calendar.getInstance()) + " - Translation: Start");
			System.out.println("Translation: Start");
			
			System.out.println("This is the stub for all translation classes, please instatiate one of them to run an actual translation");
			
			log.trace((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(Calendar.getInstance()) + " - Translation: End");
			System.out.println("Translation: End");
		}
		else {
			log.error((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(Calendar.getInstance()) + " - Translation unable to start: Not all Parameters Set" );
			throw new IllegalArgumentException("Unable to translate, system not configured");
		}		
	}
	
}
