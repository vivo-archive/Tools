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
package org.vivoweb.ingest.harvest;
/**
 * @author Dale Scheppler Dscheppler@ctrip.ufl.edu
 *
 */
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vivoweb.ingest.harvest.OAIHarvest;
public class Fetch 
{
	private static Log log = LogFactory.getLog(PubmedSOAPFetch.class);

	public static void main(String[] args) 
	{
		log.debug("Initializing Fetch.");
		if(args.length == 1)
		{
		for(String s: args)
			{
			if(s.equals("OAI"))
			{
				OAIFetch();
			}
			else if(s.equals("NIH"))
			{
				NIHFetch();
			}
			else if(s.equals("PubMed"))
			{
				PubMedFetch();
			}
			else if(s.equals("RDB"))
			{
				RDBFetch();
			}
			else
			{
				log.fatal("Fetch initialized with inappropriate argument.");
				System.out.println("Invalid Argument. Valid arguments are NIH, OAI, PubMed, or RDB.");
			}
			}
		}
		else
		{
			log.fatal("Fetch attempted to run with incorrect number of arguments.");
			System.out.println("Incorrect Number of Arguments, valid arguments are OAI, NIH, PubMed, or RDB.");
		}
		log.debug("Fetch Complete.");

	}
	public static void OAIFetch()
	{
		log.debug("Initializing OAI Fetch.");
//		try {
//			FileInputStream fisConfigFile = new FileInputStream("/config/OAIHarvestConfig.txt");
//			DataInputStream disConfigFile = new DataInputStream(fisConfigFile);
//			BufferedReader brConfigFile = new BufferedReader(new InputStreamReader(fisConfigFile));
//			String strLine;
//			while ((strLine = brConfigFile.readLine()) != null);
//			System.out.println(strLine);
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//			log.fatal("File Not Found: /config/OAI/config.txt", e1);
//		} catch (IOException e) {
//			e.printStackTrace();
//			log.fatal("IO Exception in OAI Fetch." , e);
//		}

		
		try {
			String strAddress = "www.uflib.ufl.edu/ufdc/";
			String strStartDate = "2005-01-01";
			String strEndDate = "2010-01-01";
			FileOutputStream fosOutStream; // declare a file output object
			fosOutStream = new FileOutputStream("UFDC.xml");
			OAIHarvest.execute(strAddress, strStartDate, strEndDate, fosOutStream);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error during OAI Fetch: ", e);
		}
		log.debug("OAI Fetch Complete.");
	}
	public static void PubMedFetch()
	{
		System.out.println("We would be running PubMed Fetch here");
	}
	public static void NIHFetch()
	{
		System.out.println("We would be running NIH Fetch here");
	}
	public static void RDBFetch()
	{
		System.out.println("We would be running RDB Fetch here");
	}
	public Fetch()
	{
		System.out.println("Fetch() ran for some reason.");
	}
}
