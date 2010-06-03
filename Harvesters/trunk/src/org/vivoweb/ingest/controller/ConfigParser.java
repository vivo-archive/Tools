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
package org.vivoweb.ingest.controller;

import java.io.IOException;
import java.util.LinkedList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author cah
 *
 */
public class ConfigParser extends DefaultHandler {
	
	// TODO cah: make this not Object, but something that makes sense
	private LinkedList<HarvestTask> harvestTasks;
	private HarvestTask tempTask;
	@SuppressWarnings("unused")
	private String tempVal;
	
	private ConfigParser() {
		this.harvestTasks = new LinkedList<HarvestTask>();
	}
	
	/**
	 * @return
	 */
	public LinkedList<HarvestTask> getConfig() {
		return new ConfigParser().parseConfig();
	}
	
	private LinkedList<HarvestTask> parseConfig() {
		SAXParserFactory spf = SAXParserFactory.newInstance(); // get a factory
		try {
			SAXParser sp = spf.newSAXParser(); // get a new instance of parser
			sp.parse("config/ingestConfig.xml", this); // parse the file and also register this class for call backs
		} catch(SAXException se) {
			se.printStackTrace();
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch(IOException ie) {
			ie.printStackTrace();
		}
		return harvestTasks;
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	/**
	 * @param uri 
	 * @param localName 
	 * @param qName 
	 * @param attributes 
	 * @throws SAXException 
	 * 
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//		reset tempVal = "";
//		if(qName.equalsIgnoreCase("Employee")) {
//			// create a new instance of employee
//			tempEmp = new Employee();
//			tempEmp.setType(attributes.getValue("type"));
//		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	/**
	 * @param ch 
	 * @param start 
	 * @param length 
	 * @throws SAXException 
	 * 
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	/**
	 * @param uri 
	 * @param localName 
	 * @param qName 
	 * @throws SAXException 
	 * 
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equalsIgnoreCase("Task")) {
			harvestTasks.add(tempTask);
		} else if(qName.equalsIgnoreCase("Schedule")) {
			
		}
	}
}
