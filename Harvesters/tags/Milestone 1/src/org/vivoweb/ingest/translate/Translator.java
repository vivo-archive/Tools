/*******************************************************************************
 * Copyright (c) 2010 Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams
 * 
 * This file is part of VIVO.
 * 
 * VIVO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VIVO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VIVO.  If not, see <http://www.gnu.org/licenses/gpl-3.0.txt>.
 * 
 * Contributors:
 *     Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams - initial implementation
 ******************************************************************************/
package org.vivoweb.ingest.translate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
//import java.io.StringWriter;
import java.util.Calendar;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/*****************************************************************************************
 * VIVO Data Translator
 * @author Stephen V. Williams swilliams@ichp.ufl.edu
 * TODO MILESTONE2: create constructor for accepting a parameter list
 * TODO MILESTONE2: create constructor for accepting a parameter array
 * TODO MILESTONE1: test for error handling, failing completely and gently
 * ***************************************************************************************/
public class Translator {
	
	/**
	 * Source type may become depreciated depending on construction of additional translators
	 */
	private String sourceType;
	
	/**
	 * The translation file is the map that will reconstruct our input stream's document into 
	 * the appropriate format
	 */
	private File translationFile;
	
	/**
	 * in stream is the stream containing the file (xml) that we are going to translate
	 */
	private InputStream inStream;
	
	/**
	 * out stream is the stream that the controller will be handling and were we will dump the translation
	 */
	private OutputStream outStream;
	
	
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
	public Translator(String sType, File transFile, InputStream iStream, OutputStream oStream){
		this.setSourceType(sType);
		this.setTranslationFile(transFile);
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
	 * Set translation file
	 * sets the translationFile
	 * 
	 * @param transFile valid type of translation file is xslt
	 */
	public void setTranslationFile(File transFile){
		this.translationFile = transFile;
	}
	
	public void setTranslationFile(String filename){
		this.translationFile = new File(filename);
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
				(this.translationFile !=null && this.translationFile.isFile()) && this.inStream != null && this.outStream != null) {
		
			System.out.println("Translation: Start");	
			
			xmlTranslate(this.inStream, this.translationFile, this.outStream);
			
			System.out.println("Translation: End");
		}
		else {
			throw new IllegalArgumentException("Unable to translate, system not configured");
		}		
	}
	
   /***
   * 
   * @param xmlData ObjectInputStream of the xml that is about to be translated
   * @param xsltFile File for the definition that should be translated
   * @param oStream The stream to which the data should be written
   */
  private void xmlTranslate(InputStream xmlData, File xsltFile, OutputStream oStream) {
    StreamResult outputResult = new StreamResult(oStream);
    
    try {
      // JAXP reads data using the Source interface
          Source xmlSource = new StreamSource(xmlData);
          Source xsltSource = new StreamSource(xsltFile);
  
          System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
          
          // the factory pattern supports different XSLT processors
          TransformerFactory transFact = TransformerFactory.newInstance();
          Transformer trans = transFact.newTransformer(xsltSource);
  
          // this outputs to oStream
          trans.transform(xmlSource, outputResult);
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
	
//	/***
//	 * 
//	 * @param xmlData ObjectInputStream of the xml that is about to be translated
//	 * @param xsltFile File for the definition that should be translated
//	 * @return StringWriter of the transformation
//	 */
//	private StringWriter xmlTranslate(InputStream xmlData, File xsltFile) {
//		StringWriter outputWriter = new StringWriter();
//		StreamResult outputResult = new StreamResult(outputWriter);
//		
//		try {
//			// JAXP reads data using the Source interface
//	        Source xmlSource = new StreamSource(xmlData);
//	        Source xsltSource = new StreamSource(xsltFile);
//	
//	        // the factory pattern supports different XSLT processors
//	        TransformerFactory transFact = TransformerFactory.newInstance();
//	        Transformer trans = transFact.newTransformer(xsltSource);
//	
//	        // this outputs to sysout but further implementations should push to jena then to datastore
//	        trans.transform(xmlSource, outputResult);
//		}
//		catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		
//		return outputWriter;
//	}
	
	/**
	 * @author Dale Scheppler - dscheppler@ichp.ufl.edu
	 * @param strWrite - String to write to logfile, automatically appends date and time.
	 */
	@SuppressWarnings("unused")
	private void writeToLog(String strWrite)
	{
		File fileLogFile = new File("logs/Log.txt");
		FileWriter fwOutFile;
		try
		{
			Calendar gcToday = Calendar.getInstance();
			int intYear = gcToday.get(Calendar.YEAR);
			int intMonth = gcToday.get(Calendar.MONTH) + 1;
			int intDay = gcToday.get(Calendar.DATE);
			int intHour = gcToday.get(Calendar.HOUR_OF_DAY);
			int intMinute = gcToday.get(Calendar.MINUTE);
			int intSecond = gcToday.get(Calendar.SECOND);
			int intMillisecond = gcToday.get(Calendar.MILLISECOND);
			fwOutFile = new FileWriter(fileLogFile, true);
			PrintWriter out = new PrintWriter(fwOutFile);
			out.println("Translate:" + intYear + "/" + intMonth + "/" + intDay + "/" + intHour + ":" + intMinute + ":" + intSecond + "." + intMillisecond + "-" + strWrite);
			out.close();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
}