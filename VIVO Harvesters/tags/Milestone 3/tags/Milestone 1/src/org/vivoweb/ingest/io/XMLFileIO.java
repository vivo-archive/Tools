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
package org.vivoweb.ingest.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class XMLFileIO implements InputSystem, OutputSystem {
	
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
	
	@Override
	public InputStream load() {
		return this.inStream;
	}
	
	public void store(OutputStream os) throws IOException {
		PipedInputStream pin = new PipedInputStream();
		PipedOutputStream pout = new PipedOutputStream(pin);
		PrintStream out = new PrintStream(pout);
		BufferedReader in = new BufferedReader(new InputStreamReader(pin));
		System.out.println("Writing to output stream...");
		out.println("Hello World!");
		out.flush();
		System.out.println("Text written: " + in.readLine());
	}

	@Override
	public OutputStream getOutputStream(InputStream is) throws IOException {
//		PipedInputStream pin = new PipedInputStream();
//		PipedOutputStream pout = new PipedOutputStream(pin);
		
		return null;
	}
}
