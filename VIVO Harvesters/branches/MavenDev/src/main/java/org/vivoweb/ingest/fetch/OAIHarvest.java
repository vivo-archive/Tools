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
package org.vivoweb.ingest.fetch;
import java.io.FileOutputStream;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import ORG.oclc.oai.harvester2.app.RawWrite;

/**
 * Class for harvesting from OAI Data Sources
 * @author Dale Scheppler
 *
 */
public class OAIHarvest {

	/**
	 * A listing of the paramaters that need to be in the configuration file for
	 * the OAI harvest to function properly.
	 * Changed to protected on suggestion of UCDetect.
	 * @author Dale Scheppler
	 */
	protected static final String[] arrRequiredParamaters = {"address", "startDate", "endDate", "filename"};
	/**
	 * The main function currently does nothing. This code is not meant to be called from the command line. Use Fetch OAI instead.
	 * @author Dale Scheppler
	 * @param args - Unused
	 * @throws Exception Unused
	 */
	public static void main(String[] args) throws Exception {
//		FileOutputStream out; // declare a file output object
//		out = new FileOutputStream("UFDC.xml");
//		RawWrite.run("http://www.uflib.ufl.edu/ufdc/", "2005-01-01", "2010-06-15", "oai_dc", "", out);
//		out.close();
		System.out.println("This module is not meant to be run from the command line, use \"fetch OAI\" instead.");

	}
	/**
	 * Calls the RawWrite function of the OAI Harvester example code. Writes to a file output stream.<br>
	 * Some repositories are configured incorrectly and this process will not work. For those a custom<br>
	 * method will need to be written.
	 * @author Dale Scheppler
	 * @param strAddress - The website address of the repository, without http://
	 * @param strStartDate - The date at which to begin fetching records, format and time resolution depends on repository.
	 * @param strEndDate - The date at which to stop fetching records, format and time resolution depends on repository.
	 * @param fosOutStream - The file output stream to write to.
	 * @throws Exception General exception catch if no other exceptions caught.
	 * @throws SAXException Thrown if there is an error in SAX.
	 * @throws TransformerException Thrown if there is an error during XML transform.
	 * @throws NoSuchFieldException Thrown if one of the fields queried does not exist.
	 */
	public static void execute(String strAddress, String strStartDate, String strEndDate, FileOutputStream fosOutStream) throws Exception, SAXException, TransformerException, NoSuchFieldException
	{
		//This code was marked as may cause compile errors by UCDetector.
		//Change visibility of method "OAIHarvest.execute" to Protected.
		//FIXME This code was marked as may cause compile errors by UCDetector.
		RawWrite.run("http://" + strAddress, strStartDate, strEndDate, "oai_dc", "", fosOutStream);
	}

}
