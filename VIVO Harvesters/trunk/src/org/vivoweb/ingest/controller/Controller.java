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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vivoweb.ingest.harvest.PubmedSOAPFetch;
import org.vivoweb.ingest.io.JenaIO;
import org.vivoweb.ingest.score.Score;
import org.vivoweb.ingest.translate.XSLTranslator;


/*****************************************************************************************
 * VIVO Harvester Controller
 * 
 * @author Dale Scheppler dscheppler@ichp.ufl.edu
 * @author Chris Haines cah@ichp.ufl.edu
 * @author Stephen V. Williams swilliams@ichp.ufl.edu
 * @author Nicholas Skaggs nskaggs@ctrip.ufl.edu
 * ***************************************************************************************/
public class Controller {
	private static Log log = LogFactory.getLog(Controller.class);
	private File dataFile;
	private Map<String, String> runModes;
	private Map<String, String> transParams;
	private Map<String, String> scoreStore;
	private Map<String, String> vivoStore;
	private List<OutputStream> fetchOutputs;
	private InputStream transInput;
	private List<OutputStream> transOutputs;
	private InputStream scoreInput;
	private Map<String, String> config;
	
	/**
	 * Constructor
	 * 
	 * @param configFile
	 *           stores configuration data
	 * @param runDataFile
	 *           stores data about last run times, etc
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @author chaines
	 */
	public Controller(File configFile, File runDataFile) throws IllegalArgumentException, FileNotFoundException, IOException {
		// set data file so we can write back later
		this.dataFile = runDataFile;
		// parse and store data from config file
		this.config = this.readConfigFile(configFile);
		// parse and store data from data file
		this.config.putAll(this.readConfigFile(this.dataFile));
		
		// check to make sure we have all needed parameters
		this.checkParams();
		
		// get the list of operations we need to perform
		this.runModes = this.parseParamList("runMode");
		
		// determine what to do with the output from the fetch operation
		this.fetchOutputs = this.handleOutputGroupParse("fetchOutput");
		
		// parse the parameters for translation
		this.transParams = this.parseParamList("transParam");
		// determing the input for translation
		this.transInput = this.handleInputParse("transInput");
		// determine what to  do with the output from the translation operation
		this.transOutputs = this.handleOutputGroupParse("transOutput");
		
		// parse the parameters for scoring
		this.scoreStore = this.parseParamList("scoreStore");
		// parse connection data for vivo store
		this.vivoStore = this.parseParamList("vivoStore");
		// parse connection data for scoring store
		this.scoreInput = this.handleInputParse("scoreInput");		
	}
	
	/**
	 * 
	 * @param key key in config to be parsed
	 * @return Map generated from these parameters
	 * @throws IllegalArgumentException
	 * @author chaines
	 */
	private Map<String, String> parseParamList(String key) throws IllegalArgumentException {
		String[] paramArray = this.config.remove(key).split(",");
		Map<String, String> map = new HashMap<String, String>();
		for(String param : paramArray) {
			String[] temp = this.splitParams(param, ";");
			map.put(temp[0], temp[1]);
		}
		return map;
	}
	
	/**
	 * @param key
	 * @return List<OutputStream>
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 * @author chaines
	 */
	private List<OutputStream> handleOutputGroupParse(String key) throws IllegalArgumentException, FileNotFoundException {
		List<OutputStream> outs = new LinkedList<OutputStream>();
		String[] outputs = this.config.remove(key).split(",");
		for(String output : outputs) {
			outs.add(this.handleOutputParse(output));
		}
		return outs;
	}
	
	/**
	 * @param key
	 * @return InputStream
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 * @author chaines
	 */
	private InputStream handleInputParse(String key) throws IllegalArgumentException, FileNotFoundException {
		String[] temp = this.splitParams(this.config.remove(key), ";");
		if(temp[0].equalsIgnoreCase("file")) {
			return new FileInputStream(temp[1]);
		} else if(temp[0].equalsIgnoreCase("out")) {
			// TODO chaines: output from another operation... streaming
			throw new IllegalArgumentException("WHY YOU TESTIN MY NOT READY CODE!?");
		} else {
			throw new IllegalArgumentException("Unknown Input Format");
		}
	}
	
	/**
	 * @param output
	 * @return OutputStream
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 * @author chaines
	 */
	private OutputStream handleOutputParse(String output) throws IllegalArgumentException, FileNotFoundException {
		String[] temp = this.splitParams(output, ";");
		if(temp[0].equalsIgnoreCase("file")) {
			return new FileOutputStream(temp[1]);
		} else if(temp[0].equalsIgnoreCase("in")) {
			// TODO chaines: input for another operation... streaming
			throw new IllegalArgumentException("WHY YOU TESTIN MY NOT READY CODE!?");
		} else {
			throw new IllegalArgumentException("Unknown Output Format");
		}
	}
	
	/**
	 * Checks if all configuration parameters are set
	 * 
	 * TODO define configuration parameters needed
	 * @throws IllegalArgumentException
	 * @author chaines
	 */
	private void checkParams() throws IllegalArgumentException {
		String[] params = {"runMode", "lastRunYear", "lastRunMonth", "lastRunDay", "toolEmail", "toolLocation",
				"fetchOutput", "transInput", "transOutput", "transType", "transParam", "scoreInput",
				"scoreStore", "vivoStore"};
		for(String param : params) {
			this.checkParam(param, this.config);
		}
	}
	
	/**
	 * @param param
	 * @param map
	 * @throws IllegalArgumentException
	 * @author chaines
	 */
	private void checkParam(String param, Map<String, String> map) throws IllegalArgumentException {
		if( !map.containsKey(param)) {
			throw new IllegalArgumentException("Missing Parameter: " + param);
		}
	}
	
	/**
	 * Reads configuration data in from file and returns a map
	 * 
	 * @param f 
	 * @return Map<String,String> of data read or null if failed to read
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @author chaines
	 */
	private Map<String, String> readConfigFile(File f) throws IllegalArgumentException, FileNotFoundException,
			IOException {
		// initialize variables
		Map<String, String> data = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(f));
		// read each line
		String thisLine;
		while((thisLine = br.readLine()) != null) {
			if( !thisLine.startsWith("#")) {
				// remove newline character and make pair of field:value into length2 array
				String[] temp = this.splitParams(thisLine, ":");
				// add to map
				data.put(temp[0], temp[1]);
			}
		}
		return data;
	}
	
	private String[] splitParams(String paramList, String delimiter) {
		String[] temp = paramList.split(delimiter, 2);
		if(temp.length != 2) {
			throw new IllegalArgumentException("parameters are corrupted at '"+paramList+"'");
		}
		temp[0] = temp[0].trim();
		temp[1] = temp[1].trim();
		return temp;
	}
	
//	/**
//	 * Writes the runData to the file
//	 * 
//	 * @param newRunDate
//	 * @throws IOException
//	 * @author dscheppler
//	 */
//	private void updateLastRunDate(Calendar newRunDate) throws IOException {
//		FileWriter fwOutFile = new FileWriter(this.dataFile);
//		PrintWriter out = new PrintWriter(fwOutFile);
//		out.println("lastRunMonth:" + (newRunDate.get(Calendar.MONTH) + 1));
//		out.println("lastRunDay:" + newRunDate.get(Calendar.DAY_OF_MONTH));
//		out.println("lastRunYear:" + newRunDate.get(Calendar.YEAR));
//		out.close();
//		fwOutFile.close();
//	}
	
	/**
	 * Performs the fetch specified in the configuration file
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 * @author dscheppler
	 * @author chaines
	 */
	private void Fetch() throws NumberFormatException, IOException {
		// FIXME chaines: hard coding to use only first output, but this needs to fixed to write to the the PIS-POS system
		PubmedSOAPFetch f = new PubmedSOAPFetch(this.config.get("toolEmail"), this.config.get("toolLocation"), this.fetchOutputs.get(0));
		//int numToFetch = f.getHighestRecordNumber(); //TODO Make this handle the 100,000 record limit correctly
		//FIXME As example we are only grabbing 1000 pubmed records -- numToFetch to be in config
		int numToFetch = 20;
		//FIXME numPerBatch needs to be in config
		int numPerBatch = 20;
		//FIXME querystring needs to be in config
		//String[] env = f.ESearchEnv(f.fetchAll(), numToFetch);
		String[] env = f.ESearchEnv(f.queryByAffiliation("ufl.edu"), numToFetch);
		String webEnv = env[0];
		String queryKey = env[1];
		String idListLength = env[2];
		log.trace("webEnv: "+webEnv);
		log.trace("queryKey: "+queryKey);
		log.info("Fetching "+idListLength+" records.");
		f.beginXML();
		for(int idLow = 1; idLow <= Integer.parseInt(idListLength); idLow+=numPerBatch) {
			if (idLow+numPerBatch > Integer.parseInt(idListLength)) {
				log.info("Fetching Records "+idLow+" to "+Integer.parseInt(idListLength)+".");
				f.fetchPubMedEnv(webEnv, queryKey, ""+idLow, ""+Integer.parseInt(idListLength));
			} else {
				log.info("Fetching Records "+idLow+" to "+(idLow+numPerBatch-1)+".");
				f.fetchPubMedEnv(webEnv, queryKey, ""+idLow, ""+numPerBatch);
			}
		}
		f.endXML();
	}
	
	/**
	 * Performs the translation specified in the configuration file
	 * 
	 * @throws IllegalArgumentException
	 * @author swilliams
	 * @author chaines
	 */
	private void Translate() throws IllegalArgumentException {
		// TODO this will eventually be replaced with initialization of the correct subtype of translate... if that is the
		// cont'd: path we follow
		if( !this.config.get("transType").toLowerCase().equals("xml")) {
			throw new IllegalArgumentException("Translate currently only works with an xml translation");
		}
		
		// FIXME swilliams: Make Translate work with the transParams map (take it as and argument) and do this checking
		// cont'd: itself... more generic and allows for non-xml type translations in the future if we so choose
		if( !this.transParams.containsKey("xsltFile")) {
			throw new IllegalArgumentException("XML translation requires the translation parameter 'xsltFile' to exist!");
		}
		String xsltFilePath = this.transParams.get("xsltFile");
		File xsltFile = new File(xsltFilePath);
		
		// FIXME chaines: hard coding to use only first output, but this needs to fixed to write to the the PIS-POS system
		new XSLTranslator("xml", xsltFile, this.transInput, this.transOutputs.get(0)).execute();
	}
	
	/**
	 * Performs the scoring of the data
	 * 
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 * @author chaines
	 * @author nskaggs
	 */
	private void Score() throws IllegalArgumentException, FileNotFoundException {
		this.checkParam("connPath", this.scoreStore);
		this.checkParam("userName", this.scoreStore);
		this.checkParam("passWord", this.scoreStore);
		this.checkParam("dbType", this.scoreStore);
		this.checkParam("dbClass", this.scoreStore);
		this.checkParam("connPath", this.vivoStore);
		this.checkParam("userName", this.vivoStore);
		this.checkParam("passWord", this.vivoStore);
		this.checkParam("dbType", this.vivoStore);
		this.checkParam("dbClass", this.vivoStore);
		this.checkParam("modelName", this.vivoStore);
		
		// TODO Remove this when this is no longer the case
		if( !this.scoreStore.get("dbType").equals("MySQL") || !this.vivoStore.get("dbType").equals("MySQL")) {
			throw new IllegalArgumentException("Scoring only works with MySQL Databases at the moment!");
		}
		
		JenaIO vivo = new JenaIO(this.vivoStore.get("connPath"), this.vivoStore.get("userName"), this.vivoStore
				.get("passWord"), this.vivoStore.get("modelName"), this.vivoStore.get("dbType"), this.vivoStore.get("dbClass"));
		JenaIO scoreInput = new JenaIO(this.scoreInput);
		JenaIO scoreOutput = new JenaIO(this.scoreStore.get("connPath"), this.scoreStore.get("userName"), this.scoreStore
				.get("passWord"), this.scoreStore.get("dbType"), this.scoreStore.get("dbClass"));
		new Score(vivo.getJenaModel(), scoreInput.getJenaModel(), scoreOutput.getJenaModel()).execute();
	}
	
	/**
	 * Performs the qualification of the data
	 */
	private void Qualify() {
	// TODO fill this in when Qualify is a valid process
	}
	
	/**
	 * Executes the entire process from beginning to end
	 * 
	 * FIXME scheduling needs to be implemented
	 * TODO client/server interaction with IO layer
	 * @author chaines
	 */
	public void execute() {
		if(this.runModes.containsKey("fetch")) {
			try {
				this.Fetch();
			} catch(NumberFormatException e) {
				System.out.println("Last Run Data File Corrupted!");
				log.error("Last run data file corrupted, exiting.",e);
//				e.printStackTrace();
			} catch(IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(this.runModes.containsKey("translate")) {
			try {
				this.Translate();
			} catch(IllegalArgumentException e) {
				log.error("",e);
//				e.printStackTrace();
			}
		}
		
		if(this.runModes.containsKey("score")) {
			try {
				this.Score();
			} catch(IllegalArgumentException e) {
				log.error("",e);
//				e.printStackTrace();
			} catch(FileNotFoundException e) {
				log.error("",e);
//				e.printStackTrace();
			}
		}
		
		if(this.runModes.containsKey("qualify")) {
			this.Qualify();
		}
	}
	
	/**
	 * The main controller for the VIVO harvesters. Pretty barebones, look at the imports for what is
	 * going on.
	 * 
	 * @author chaines
	 * @author dscheppler
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Controller c = new Controller(new File("config/ingest.conf"), new File("config/rundata.dat"));
			c.execute();
		} catch(Exception e) {
			// TODO Better Exception Handling
			log.error("",e);
//			e.printStackTrace();
		}
	}
}
