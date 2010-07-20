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

import java.io.IOException;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.vivoweb.ingest.util.Task;
import org.xml.sax.SAXException;

/**
 * Handles the bibtex format and translates it into the VIVO ontology.
 * @author Stephen V. Williams swilliams@ctrip.ufl.edu
 */
public class BibtexTranslator extends Task{
	
	public static BibtexTranslator getInstance(Map<String,String> params) throws ParserConfigurationException, SAXException, IOException {
		//TODO Stephen: Identify parameters required for translation
		return null;
	}

	@Override
	public void executeTask() throws NumberFormatException {
		//TODO Stephen: Stub out parsing method for reading in the Bibtex Records
	}
}