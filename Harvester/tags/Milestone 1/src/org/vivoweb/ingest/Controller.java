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
package org.vivoweb.ingest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.vivoweb.ingest.harvest.pubmed.Fetch;
import org.vivoweb.ingest.io.JenaIO;
import org.vivoweb.ingest.score.Score;
import org.vivoweb.ingest.translate.Translator;


/*****************************************************************************************
 * VIVO Harvester Controller
 * 
 * @author Dale Scheppler dscheppler@ichp.ufl.edu
 * @author Chris Haines cah@ichp.ufl.edu
 * @author Stephen V. Williams swilliams@ichp.ufl.edu
 * @author Nicholas Skaggs nskaggs@ctrip.ufl.edu
 * ***************************************************************************************/
public class Controller {
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
//	private InputStream sanitizeInput;
//	private OutputStream sanitizeOutput;
	private boolean debug = true;
	
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
	 * @param key
	 * @return
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
	 * @TODO define configuration parameters needed
	 * @throws IllegalArgumentException
	 * @author chaines
	 */
	private void checkParams() throws IllegalArgumentException {
		String[] params = {"runMode", "lastRunYear", "lastRunMonth", "lastRunDay", "toolEmail", "toolLocation",
				"debugLevel", "fetchOutput", "transInput", "transOutput", "transType", "transParam", "scoreInput",
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
		new Fetch(this.config.get("toolEmail"), this.config.get("toolLocation"), Integer.parseInt(this.config.get("debugLevel")), this.fetchOutputs.get(0)).execute();
//		f.execute();
//		f.fetchByAffiliation("ufl.edu", 20);
		//this.updateLastRunDate(f.fetchAllFromLastFetch(Integer.parseInt(this.config.get("lastRunMonth")), Integer
		//		.parseInt(this.config.get("lastRunDay")), Integer.parseInt(this.config.get("lastRunYear"))));
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
		new Translator("xml", xsltFile, this.transInput, this.transOutputs.get(0)).execute();
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
	 * Sanitize Method
	 * Adds the dtd and xml code to the top of the xml file and removes the extranious
	 * xml namespace attributes.  This function is slated for deprecation on mileston 2
	 * 
	 * @throws IOException 
	 * @FIXME clear out this method and either correct the xsl finicky ness
	 * cont'd: or move the cleaning parts of this method to fetch
	 * @author swilliams
	 */
	private void sanitizeXML() throws IOException{
	
		//xml write functions, take in a stream pass it to a writer
		FileOutputStream xmlOut = new FileOutputStream(this.config.get("sanitOutput").split(";")[1]);
		OutputStreamWriter xmlWriter = new OutputStreamWriter(xmlOut);
		
		//xml read functions, take in a stream pass it to a reader
		FileReader xmlIn = new FileReader(this.config.get("sanitInput").split(";")[1]);
		BufferedReader xmlReader = new BufferedReader(xmlIn);
		
		//Header lines for XML files from pubmed
		xmlWriter.write("<?xml version=\"1.0\"?>");
		xmlWriter.write("<!DOCTYPE PubmedArticleSet PUBLIC \"-//NLM//DTD PubMedArticle, 1st January 2010//EN\" \"http://www.ncbi.nlm.nih.gov/corehtml/query/DTD/pubmed_100101.dtd\">");

		//Loop through the file and remove the namespaces
		String s;
		while((s = xmlReader.readLine()) != null){
			//System Messages
			if(debug) {
				System.out.println("XML File Length - Pre Sanitize: " + s.length());
				System.out.println("XML File Length - Post Sanitze: " + s.replace(" xmlns=\"\"", "").replace(" xmlns=\"http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed\"", "").length());
			}
			
			xmlWriter.write(s.replace(" xmlns=\"\"", "").replace(" xmlns=\"http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed\"", ""));
		}
		
		//file close statements.  Warning, not closing the file will leave incomplete xml files and break the translate method
		xmlWriter.close();
		xmlOut.close();
		xmlReader.close();
		xmlIn.close();
	}
	
	/**
	 * Executes the entire process from beginning to end
	 * 
	 * @FIXME scheduling needs to be implemented
	 * @TODO client/server interaction with IO layer
	 * @author chaines
	 */
	public void execute() {
		if(this.runModes.containsKey("fetch")) {
			try {
				this.Fetch();
			} catch(NumberFormatException e) {
				System.out.println("Last Run Data File Corrupted!");
				writeToLog("Last run data file corrupted, exiting.");
				e.printStackTrace();
			} catch(IOException e) {
				// // // TODO dscheppler: I forget why we are throwing IOExceptions in fetch... so that should be explained here
				// // TODO Because it writes to logfiles.
				// TODO handle the IOExceptions in the writeToLog method (don't let that bubble out here as it should not stop the functioning of Fetch)
				e.printStackTrace();
			}
		}
		
		//TODO svw: increase error handling - add logging
		if(this.runModes.containsKey("sanitize")) {
			try {
				this.sanitizeXML();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(this.runModes.containsKey("translate")) {
			try {
				this.Translate();
			} catch(IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		
		if(this.runModes.containsKey("score")) {
			try {
				this.Score();
			} catch(IllegalArgumentException e) {
				e.printStackTrace();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		if(this.runModes.containsKey("qualify")) {
			this.Qualify();
		}
	}
	
	/**
	 * Writes to a log file
	 * @param strWrite
	 * @author dscheppler
	 */
	private void writeToLog(String strWrite)
	{
		File fileLogFile = new File("logs/Log.txt");
		FileWriter fwOutFile;
		try
		{
			Calendar gcToday = Calendar.getInstance();
			int intYear = gcToday.get(Calendar.YEAR);
			int intMonth = gcToday.get(Calendar.MONTH) + 1;
			int intDay = gcToday.get(Calendar.DATE);
			int intHour = gcToday.get(Calendar.HOUR_OF_DAY);
			int intMinute = gcToday.get(Calendar.MINUTE);
			int intSecond = gcToday.get(Calendar.SECOND);
			int intMillisecond = gcToday.get(Calendar.MILLISECOND);
			fwOutFile = new FileWriter(fileLogFile, true);
			PrintWriter out = new PrintWriter(fwOutFile);
			out.println("Controller:" + intYear + "/" + intMonth + "/" + intDay + "/" + intHour + ":" + intMinute + ":" + intSecond + "." + intMillisecond + "-" + strWrite);
			out.close();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
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
			Controller c = new Controller(new File("conf/ingest.conf"), new File("conf/rundata.dat"));
			c.execute();
		} catch(Exception e) {
			// TODO Better Exception Handling
			e.printStackTrace();
		}
	}
}