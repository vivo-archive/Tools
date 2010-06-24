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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.vivoweb.ingest.translate.Translator;

public class XSLTranslator extends Translator{
		
	/**
	 * The translation file is the map that will reconstruct our input stream's document into 
	 * the appropriate format
	 */
	private File translationFile;
	
	/***
	 * Empty constructor
	 */
	public XSLTranslator(){
		
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
	public XSLTranslator(String sType, File transFile, InputStream iStream, OutputStream oStream){
		this.setSourceType(sType);
		this.setTranslationFile(transFile);
		this.setInStream(iStream);
		this.setOutStream(oStream);
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
	 * This function will utilize the translation file to transform the outputstream
	 * @throws IllegalArgumentException when the system is not properly configured
	 */
	public void execute() throws IllegalArgumentException {
		
		//checking for valid input parameters
		if ((this.translationFile !=null && this.translationFile.isFile()) && this.inStream != null && this.outStream != null) {
		
			log.info("Translation: Start");
			
			xmlTranslate(this.inStream, this.translationFile, this.outStream);
			
			log.info("Translation: End");
		}
		else {
			log.error("Translation unable to start: Not all Parameters Set" );
			log.error("Translation File: " + this.translationFile.toString());
			log.error("Translation File truth: " + this.translationFile.isFile());
			log.error("Translation Stream: " + this.inStream.toString());
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
	    	log.error("Translation Error",e);
	    }
	  }
	  
//		/***
//		 * 
//		 * @param xmlData ObjectInputStream of the xml that is about to be translated
//		 * @param xsltFile File for the definition that should be translated
//		 * @return StringWriter of the transformation
//		 */
//		private StringWriter xmlTranslate(InputStream xmlData, File xsltFile) {
//			StringWriter outputWriter = new StringWriter();
//			StreamResult outputResult = new StreamResult(outputWriter);
//			
//			try {
//				// JAXP reads data using the Source interface
//		        Source xmlSource = new StreamSource(xmlData);
//		        Source xsltSource = new StreamSource(xsltFile);
	//	
//		        // the factory pattern supports different XSLT processors
//		        TransformerFactory transFact = TransformerFactory.newInstance();
//		        Transformer trans = transFact.newTransformer(xsltSource);
	//	
//		        // this outputs to sysout but further implementations should push to jena then to datastore
//		        trans.transform(xmlSource, outputResult);
//			}
//			catch (Exception e) {
//				log.error("",e);
//			}
//			
//			return outputWriter;
//		}

	  /**
	   * Currently the main method accepts two methods of execution, file translation and record handler translation
	   * 
	   * @param functionSwitch possible entries include -f for file and -rh for record handler
	   * @param translationFile
	   * @param fileToTranslate  
	   * @param inRecordHandler
	   * @param outRecordHandler
	   */
	  public static void main(String[] args) {
		 if (args.length != 3) {
			  log.error("Invalid Arguments: XSLTranslate requires 3. They system was supplied " + args.length);
			  throw new IllegalArgumentException();
		 }
		 else {
			if (args[0].equals("-f")) {
				try {
					//set the in/out and translation var
					XSLTranslator xslTrans = new XSLTranslator();
					xslTrans.setInStream(new FileInputStream(new File(args[1])));
					xslTrans.setTranslationFile(new File(args[2]));
					xslTrans.setOutStream(System.out);
					
					//execute the program
					xslTrans.execute();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					log.error(e.getStackTrace().toString());
				}				
			}
			else if (args[0].equals("-rh")) {
				//TODO add pulling in the config portions
				//TODO add creating the record handlers
				//TODO get from the in record and translate
				//TODO send output to out handler
			}
			else {
				log.error("Invalid Arguments: Translate option " + args[0] + " not handled.");
				throw new IllegalArgumentException();
			}
		 }	
		  
	  }

}
