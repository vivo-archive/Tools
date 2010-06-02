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
package org.vivoweb.ingest.harvest.pubmed;

import gov.nih.nlm.ncbi.www.soap.eutils.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.ADBException;
import org.vivoweb.ingest.harvest.Harvestor;

/**
 * Fetch
 * @author Stephen V. Williams swilliams@ichp.ufl.edu
 * @author Dale R. Scheppler dscheppler@ichp.ufl.edu
 * @author Christopher Haines cah@ichp.ufl.edu
 */
public class Fetch implements Harvestor
{
	private String strEmailAddress;
	private String strToolLocation;
	private OutputStream osOutputStream;
	public int intDebugLevel;

	
	/***
	 * Constructor
	 * 
	 * 
	 * @param strEmail
	 *            - Email address of the tool
	 * 
	 * @param strToolLoc
	 *            - Location of the tool (Eg: UF or Cornell or Pensyltucky U.
	 * @param intDebugLev
	 *            - Debug level, 0 = minimal, 1 = Java errors, 2 = stacktraces.
	 * @param osOutStream
	 *            - The output stream for the method.
	 */
	public Fetch(String strEmail, String strToolLoc, int intDebugLev, OutputStream osOutStream)
	{
		this.strEmailAddress = strEmail; // NIH Will email this person if there is a problem
		this.strToolLocation = strToolLoc; // This provides further information to NIH
		this.intDebugLevel = intDebugLev; // The amount of information to throw in an error
		this.osOutputStream = osOutStream; // The stream we're getting from the controller.		
	}
	
	/**
	 * 
	 */
	public void fetchAll()
	{
		int intStartRecord = 1;
		int intStopRecord = getHighestRecordNumber();
		writeToLog("Beginning fetch of records " + intStartRecord + " to " + intStopRecord + ".");
		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (int id = intStartRecord; id <= intStopRecord; id++)
		{
			idList.add(new Integer(id));
		}
		try
		{
			throttle(idList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void fetchByAffiliation(String strAffiliation, int intMaxRecords)
	{
		writeToLog("Searching for all records affiliated with " + strAffiliation);
		throttle(ESearch(strAffiliation + "[ad]", intMaxRecords));
	}

	/**
	 * Performs an ESearch against PubMed database using a search term
	 * 
	 * @param term
	 *            - The search term
	 * @param maxNumRecords
	 *            - Maximum number of records to pull, set currently by
	 *            Fetch.throttle.
	 * @return List<Integer> of ids found in the search result
	 * @author chaines
	 */
	private List<Integer> ESearch(String term, Integer maxNumRecords)
	{
		// define the list to hold our ids
		ArrayList<Integer> idList = new ArrayList<Integer>();
		try
		{
			// create service connection
			EUtilsServiceStub service = new EUtilsServiceStub();
			// create a new search
			EUtilsServiceStub.ESearchRequest req = new EUtilsServiceStub.ESearchRequest();
			// set search to pubmed database
			req.setDb("pubmed");
			// set search term
			req.setTerm(term);
			// set max number of records to return from search
			req.setRetMax(maxNumRecords.toString());
			// run the search and get result set
			EUtilsServiceStub.ESearchResult res = service.run_eSearch(req);
			writeToLog("Fetching a total of " + res.getIdList().getId().length + " records.");
			// for each id in the list of ids in the search results
			for (String id : res.getIdList().getId())
			{
				try
				{
					// put it in our List
					idList.add(new Integer(id));
				}
				// just in case there is a non-number in the ID list (should not happen)
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
			writeToLog(idList.size()+" records found");
		}
		catch (AxisFault f)
		{
			writeToLog("Failed to initialize service connection");
			f.printStackTrace();
		}
		catch (RemoteException e)
		{
			writeToLog("Failed to run the search");
			e.printStackTrace();
		}
		// return the list of ids
		return idList;
	}
	
	/**
	 * Performs an ESearch against PubMed database using a search term
	 * 
	 * @param term
	 *            - The search term
	 * @param maxNumRecords
	 *            - Maximum number of records to pull, set currently by
	 *            Fetch.throttle.
	 * @return String[] = {WebEnv, QueryKey}
	 * @author chaines
	 */
	@SuppressWarnings("unused")
	private String[] ESearchEnv(String term, Integer maxNumRecords)
	{
		// define the array to hold our Environment data
		String[] env = new String[2];
		try
		{
			// create service connection
			EUtilsServiceStub service = new EUtilsServiceStub();
			// create a new search
			EUtilsServiceStub.ESearchRequest req = new EUtilsServiceStub.ESearchRequest();
			// set search to pubmed database
			req.setDb("pubmed");
			// set search term
			req.setTerm(term);
			// set max number of records to return from search
			req.setRetMax(maxNumRecords.toString());
			// run the search and get result set
			EUtilsServiceStub.ESearchResult res = service.run_eSearch(req);
			writeToLog("Fetching a total of " + res.getIdList().getId().length + " records.");
			// for each id in the list of ids in the search results
			env[0] = req.getWebEnv();
			env[1] = req.getQueryKey();
		}
		catch (AxisFault f)
		{
			writeToLog("Failed to initialize service connection");
			f.printStackTrace();
		}
		catch (RemoteException e)
		{
			writeToLog("Failed to run the search");
			e.printStackTrace();
		}
		return env;
	}
	
	/**
	 * Performs a PubMed Fetch using a previously defined esearch environment and querykey
	 * @param env =  = {WebEnv, QueryKey}
	 */
	@SuppressWarnings("unused")
	private void fetchPubMedEnv(String[] env) throws IllegalArgumentException {
		if(env.length != 2) {
			throw new IllegalArgumentException("Invalid WebEnv and QueryKey");
		}
		fetchPubMedEnv(env[0], env[1]);
	}
	
	/**
	 * Performs a PubMed Fetch using a previously defined esearch environment and querykey
	 * @param WebEnv
	 * @param QueryKey
	 */
	private void fetchPubMedEnv(String WebEnv, String QueryKey) {
		try {
			EFetchPubmedServiceStub service = new EFetchPubmedServiceStub();
			EFetchPubmedServiceStub.EFetchRequest req = new EFetchPubmedServiceStub.EFetchRequest();
			req.setQuery_key(QueryKey);
			req.setWebEnv(WebEnv);
			req.setEmail(this.strEmailAddress);
			req.setTool(this.strToolLocation);
			EFetchPubmedServiceStub.EFetchResult res = service.run_eFetch(req);
			new StAXOMBuilder(res.getPullParser(null)).getDocumentElement().serializeAndConsume(this.osOutputStream);
		} catch(AxisFault f) {
			writeToLog("Could not create service connection");
			f.printStackTrace();
		} catch(RemoteException e) {
			writeToLog("Could not run search");
			e.printStackTrace();
		} catch(ADBException e) {
			writeToLog("Could not get xml data from result set");
			e.printStackTrace();
		} catch(XMLStreamException e) {
			writeToLog("Could not write xml data from result set");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method takes in a range of PMIDs and returns MedLine XML to the main
	 * method as an outputstream.
	 * 
	 * @param ids
	 *            Range of PMID you want to pull, in list form
	 * @param outputStream
	 *            The outputstream for passing it back to the main method.
	 * @param strEmailAddress
	 *            Email address of the technical contact for this VIVO
	 *            installation
	 * @param strToolLocation
	 *            Location of this VIVO installation, EG: UF or Cornell or
	 *            Pensyltucky U.
	 */
	private void fetchPubMed(List<Integer> ids) {
		try {
			EFetchPubmedServiceStub service = new EFetchPubmedServiceStub();
			EFetchPubmedServiceStub.EFetchRequest req = new EFetchPubmedServiceStub.EFetchRequest();
			StringBuilder strPMID = new StringBuilder();
			for(int id = 0; id < ids.size(); id++ ) {
				if(id != 0) {
					strPMID.append(",");
				}
				strPMID.append(ids.get(id));
			}
			req.setId(strPMID.toString());
			writeToLog("Fetching records: " + strPMID.toString() + ".");
			req.setEmail(this.strEmailAddress);
			req.setTool(this.strToolLocation);
			EFetchPubmedServiceStub.EFetchResult res = service.run_eFetch(req);
			new StAXOMBuilder(res.getPullParser(null)).getDocumentElement().serializeAndConsume(this.osOutputStream);
		} catch(AxisFault f) {
			writeToLog("Could not create service connection");
			f.printStackTrace();
		} catch(RemoteException e) {
			writeToLog("Could not run search");
			e.printStackTrace();
		} catch(ADBException e) {
			writeToLog("Could not get xml data from result set");
			e.printStackTrace();
		} catch(XMLStreamException e) {
			writeToLog("Could not write xml data from result set");
			e.printStackTrace();
		} catch(NullPointerException e) {
			// FIXME We really need to figure out why it is throwing these, but it seems to work fine if we just ignore them
		}
	}
	
	/**
	 * Ensures that fetch procedures only request a number of times under the PubMed cap.
	 * 10000 between the hours of 9pmEST and 5amEST, 100 otherwise
	 * 
	 * @param ids a List<Integer> of ids to be fetched
	 * @author chaines
	 */
	private void throttle(List<Integer> ids)
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("EST"));
		int intHour = cal.get(Calendar.HOUR_OF_DAY);
		int intRetNum;
		if (intHour > 20 || intHour < 6)
		{
			intRetNum = 10000;
		}
		else
		{
			intRetNum = 100;
		}
		writeToLog("Throttle is allowing fetch to get " + intRetNum + " records this batch.");
		// our list to fetch is more than one batch
		if (ids.size() > intRetNum)
		{
			// create List to hold ids to fetch this batch
			ArrayList<Integer> idList = new ArrayList<Integer>();
			int x = intRetNum;
			while (x != 0)
			{
				//shift the top intRetNum records from ids to idList
				idList.add(ids.remove(0));
				x--;
			}
			System.out.println(idList);
			// fetch this batch
			fetchPubMed(idList);
			try
			{
				// pause for 1/3 second
				Thread.sleep(333);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			// run throttle again with the remaining ids to be fetched
			throttle(ids);
		}
		else
		{
			fetchPubMed(ids);
			writeToLog("Throttle is allowing fetch to get "+ ids.size() + " records this batch.");
		}
	}
	
	/**
	 * 
	 * @param intStartRecord
	 * @param intStopRecord
	 */
	public void fetchAllByRange(int intStartRecord, int intStopRecord)
	{
		int start = intStartRecord;
		int stop = intStopRecord;
		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (int id = start; id <= stop; id++)
		{
			idList.add(new Integer(id));
		}
		try
		{
			throttle(idList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		writeToLog("Fetching records that fall within " + intStartRecord+ " and " + intStopRecord);
	}
	
	/**
	 * 
	 * @param intStartMonth
	 * @param intStartDay
	 * @param intStartYear
	 * @param intStopMonth
	 * @param intStopDay
	 * @param intStopYear
	 */
	public void fetchAllByDate(int intStartMonth, int intStartDay, int intStartYear, int intStopMonth, int intStopDay, int intStopYear)
	{
		writeToLog("Fetching all records between "+intStartYear+"/"+intStartMonth+"/"+intStartDay+ " and " +intStopYear+"/"+intStopMonth+"/"+intStopDay+ ".");
		throttle(ESearch("\""+intStartYear+"/"+intStartMonth+"/"+intStartDay+"\""+"[PDAT] : \""+intStopYear+"/"+intStopMonth+"/"+intStopDay+"\"[PDAT]", 200000));
//		fetchPubMedEnv(ESearchEnv("\""+intStartYear+"/"+intStartMonth+"/"+intStartDay+"\""+"[PDAT] : \""+intStopYear+"/"+intStopMonth+"/"+intStopDay+"\"[PDAT]", 200000));		
	}
	
	/**
	 * 
	 * @param intLastRunMonth
	 * @param intLastRunDay
	 * @param intLastRunYear
	 * @return
	 */
	public Calendar fetchAllFromLastFetch(int intLastRunMonth, int intLastRunDay, int intLastRunYear)
	{
		Calendar gcToday = Calendar.getInstance();
		int intYear = gcToday.get(Calendar.YEAR);
		int intMonth = gcToday.get(Calendar.MONTH) + 1;
		int intDay = gcToday.get(Calendar.DATE);
		writeToLog("Fetching all records created between " + intLastRunYear+"/"+intLastRunMonth+"/"+intLastRunDay + " and now.");
		//FetchController fc = new FetchController(null);
		//fc.updateFetchLastRunDate(intMonth, intDay, intYear);
		throttle(ESearch("\""+intLastRunYear+"/"+intLastRunMonth+"/"+intLastRunDay+"\""+"[PDAT] : \""+(intYear + 5)+"/"+intMonth+"/"+intDay+"\"[PDAT]", 200000));
//		fetchPubMedEnv(ESearchEnv("\""+intLastRunYear+"/"+intLastRunMonth+"/"+intLastRunDay+"\""+"[PDAT] : \""+(intYear + 5)+"/"+intMonth+"/"+intDay+"\"[PDAT]", 200000));
		return gcToday;
	}
	
	/**
	 * This function simply checks to see what is the highest PubMed article PMID at the time it is called.
	 * The pubmed website might have 2-5 more records past what this one pulls
	 * But this function pulls them up to what they have indexed.
	 * So it's as good a "Highest number" as we're going to get.
	 * 
	 * @return Returns an integer of the highest PMID at the time it is run
	 * @author dscheppler
	 */
	public int getHighestRecordNumber()
	{
		Calendar gcToday = Calendar.getInstance();
		int intYear = gcToday.get(Calendar.YEAR);
		int intMonth = gcToday.get(Calendar.MONTH);
		int intDay = gcToday.get(Calendar.DATE);
		List<Integer> lstResult = ESearch("\""+intYear+"/"+intMonth+"/"+intDay+"\""+"[PDAT] : \""+(intYear + 5)+"/"+12+"/"+31+"\"[PDAT]", 1);
		return lstResult.get(0);
	}
	
	/**
	 * @param strWrite - String to write to logfile, automatically appends date and time.
	 * @author dscheppler
	 */
	private void writeToLog(String strWrite)
	{
		System.out.println(strWrite);
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
			out.println("Fetch:" + intYear + "/" + intMonth + "/" + intDay + "/" + intHour + ":" + intMinute + ":" + intSecond + "." + intMillisecond + "-" + strWrite);
			out.close();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Executes the fetch
	 * 
	 * @FIXME eventually Fetch should be initialized with parameters such that it know which of the fetches to run and all
	 * -- that needs to be called is execute()
	 */
	public void execute()
	{
		writeToLog("Fetch Begin");
		this.fetchByAffiliation("ufl.edu", 20);
		writeToLog("Fetch End");
		// TODO throttling should be done as part of the queries maybe? the current throttle does not work with the idea of
		// -- WebEnv/QueryKey fetching... Will need to research how that will work
	}
	
}