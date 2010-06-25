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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public class RecordHandlerParser extends DefaultHandler {
	
	private RecordHandler rh;
	private Map<String,String> params;
	private String type;
	private String tempVal;
	private String tempParamName;
	
	private RecordHandlerParser() {
		this.params = new HashMap<String,String>();
		this.tempVal = "";
		this.type = "Unset!";
	}
	
	/**
	 * Parses a configuration file describing a RecordHandler
	 * @param filename the name of the file to parse
	 * @return the RecordHandler described by the config file
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static RecordHandler parseRecordHandlerConfig(String filename) throws ParserConfigurationException, SAXException, IOException {
		return new RecordHandlerParser().parseConfig(filename);
	}
	
	private RecordHandler parseConfig(String filename) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance(); // get a factory
		SAXParser sp = spf.newSAXParser(); // get a new instance of parser
		sp.parse(filename, this); // parse the file and also register this class for call backs
		return this.rh;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		this.tempVal = "";
		this.tempParamName = "";
		if(qName.equalsIgnoreCase("RecordHandler")) {
			this.type = attributes.getValue("type");
		} else if(qName.equalsIgnoreCase("Param")) {
			this.tempParamName = attributes.getValue("name");
		} else {
			throw new SAXException("Unknown Tag: "+qName);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		this.tempVal = new String(ch, start, length);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equalsIgnoreCase("RecordHandler")) {
			try {
				Class<?> className = Class.forName(this.type);
				Object tempRH = className.newInstance();
				if(!(tempRH instanceof RecordHandler)) {
					throw new SAXException("Class must extend RecordHandler");
				}
				this.rh = (RecordHandler)tempRH;
				this.rh.setParams(this.params);
			} catch(ClassNotFoundException e) {
				throw new SAXException("Unknown Class: "+this.type,e);
			} catch(SecurityException e) {
				throw new SAXException(e.getMessage(),e);
			} catch(IllegalArgumentException e) {
				throw new SAXException(e.getMessage(),e);
			} catch(InstantiationException e) {
				throw new SAXException(e.getMessage(),e);
			} catch(IllegalAccessException e) {
				throw new SAXException(e.getMessage(),e);
			} catch(IOException e) {
				throw new SAXException(e.getMessage(),e);
			}
		} else if(qName.equalsIgnoreCase("Param")) {
			this.params.put(this.tempParamName, this.tempVal);
		} else {
			throw new SAXException("Unknown Tag: "+qName);
		}
	}
}