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
package org.vivoweb.ingest.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class XMLFileIO {
	
	private FileInputStream inStream;
	private FileOutputStream outStream;
	
	public XMLFileIO(String filepath) throws FileNotFoundException {
		this.inStream = new FileInputStream(filepath);
		this.outStream = new FileOutputStream(filepath);
	}
	
	private XMLStreamReader getReader(InputStream is) throws XMLStreamException, FactoryConfigurationError {
		return XMLInputFactory.newInstance().createXMLStreamReader(is);
	}
	
	private XMLStreamWriter getWriter(OutputStream os) throws XMLStreamException, FactoryConfigurationError {
		return XMLOutputFactory.newInstance().createXMLStreamWriter(os);
	}
	
	public XMLStreamReader getReader() throws XMLStreamException, FactoryConfigurationError {
		return getReader(this.inStream);
	}
	
	public XMLStreamWriter getWriter() throws XMLStreamException, FactoryConfigurationError {
		return getWriter(this.outStream);
	}
}
