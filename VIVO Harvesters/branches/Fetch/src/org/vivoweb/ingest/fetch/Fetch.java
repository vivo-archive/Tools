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
/**
 * @author Dale Scheppler Dscheppler@ctrip.ufl.edu
 *
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class Fetch 
{
	private static Log log = LogFactory.getLog(Fetch.class);

	public static void main(String[] args)
	{
		log.debug("Initializing Fetch.");
		if(args.length == 1)
		{
		for(String s: args)
			{
			if(s.equals("OAI"))
			{
				try
				{
				System.out.println("Trying to read file.");
				OAIFetch(readConfig("config/OAIHarvestConfig.txt"));
				}
				catch(IllegalArgumentException e)
				{
					log.fatal("", e);
				}
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
	public static void OAIFetch(HashMap<String, String> hmConfigMap )
	{
		log.debug("Initializing OAI Fetch.");
		checkConfig(hmConfigMap, OAIHarvest.arrRequiredParamaters);		
		
		try {
			OAIHarvest.execute(hmConfigMap.get("address"), hmConfigMap.get("startDate"), hmConfigMap.get("endDate"), new FileOutputStream(hmConfigMap.get("filename")));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error during OAI Fetch: ", e);
		}
		log.debug("OAI Fetch Complete.");
	}
	public static void PubMedFetch()
	{
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("temp.txt");
			PubmedSOAPFetch bob = new PubmedSOAPFetch("dscheppler@ichp.ufl.edu", "somewhere", fos);
			bob.fetchPubMedEnv(bob.ESearchEnv(bob.queryByAffiliation("ufl.edu"), 1000));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static void NIHFetch()
	{
		System.out.println("We would be running NIH Fetch here");
	}
	public static void RDBFetch()
	{
		System.out.println("We would be running RDB Fetch here");
	}

	public static HashMap<String, String> readConfig(String strFilename) throws IllegalArgumentException {
		HashMap<String, String> hmConfigMap = new HashMap<String, String>();
		try {
			FileInputStream fisConfigFile = new FileInputStream(strFilename);
			BufferedReader brConfigFile = new BufferedReader(
					new InputStreamReader(fisConfigFile));
			String strLine;
			while ((strLine = brConfigFile.readLine()) != null) {
				String[] strParams = strLine.split(":",2);
				if(strParams.length != 2){
					throw new IllegalArgumentException("Invalid configuration file format. Entries must be key:value.");
				}
//				System.out.println(strLine.split(":")[1]);
				hmConfigMap.put(strParams[0], strParams[1]);
				
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			log.fatal("File Not Found: " + strFilename, e1);
		} catch (IOException e) {
			e.printStackTrace();
			log.fatal("IO Exception reading configuration file: "+strFilename, e);
		}
		if(hmConfigMap.isEmpty())
		{
			throw new IllegalArgumentException("Failed to read configuration file. File does not exist or is blank.");
		}
		return hmConfigMap;

	}
	private Fetch()
	{
		System.out.println("This space intentionally left blank.");
	}
	private static void checkConfig(HashMap<String, String> hmConfigMap, String[] arrParameters)
	{
		for(String Param:arrParameters)
		{
			if(!hmConfigMap.containsKey(Param))
			{
				throw new IllegalArgumentException ("Missing parameter \"" + Param + "\" in configuration file");
			}
		}
	}
}
