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

import gov.nih.nlm.ncbi.www.soap.eutils.*;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.EFetchResult;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.PubmedArticleSet_type0;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.utils.writer.MTOMAwareXMLSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Fetch
 * @author Stephen V. Williams swilliams@ichp.ufl.edu
 * @author Dale R. Scheppler dscheppler@ichp.ufl.edu
 * @author Christopher Haines cah@ichp.ufl.edu
 */
public class PubmedSOAPFetch implements Harvestor
{
	private static Log log = LogFactory.getLog(PubmedSOAPFetch.class);
	private String strEmailAddress;
	private String strToolLocation;
	private OutputStreamWriter xmlWriter;
	
	/***
	 * Constructor
	 * 
	 * 
	 * @param strEmail
	 *            - Email address of the tool
	 * 
	 * @param strToolLoc
	 *            - Location of the tool (Eg: UF or Cornell or Pensyltucky U.
	 * @param osOutStream
	 *            - The output stream for the method.
	 */
	public PubmedSOAPFetch(String strEmail, String strToolLoc, OutputStream osOutStream)
	{
		this.strEmailAddress = strEmail; // NIH Will email this person if there is a problem
		this.strToolLocation = strToolLoc; // This provides further information to NIH
		try {
			// Writer to the stream we're getting from the controller.
			this.xmlWriter = new OutputStreamWriter(osOutStream, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			log.error("",e);
		}
	}
	
	/**
	 * Returns Query to Fetch All Records
	 * @return 
	 */
	public String fetchAll()
	{
		return "1:8000[dp]";
	}
	
	/**
	 * TODO
	 * @param strAffiliation
	 * @return 
	 */
	public String queryByAffiliation(String strAffiliation)
	{
		return strAffiliation+"[ad]";
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
	public List<Integer> ESearch(String term, Integer maxNumRecords)
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
			log.trace("Fetching a total of " + res.getIdList().getId().length + " records.");
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
			log.trace(idList.size()+" records found");
		}
		catch (AxisFault f)
		{
			log.error("Failed to initialize service connection");
			f.printStackTrace();
		}
		catch (RemoteException e)
		{
			log.error("Failed to run the search");
			e.printStackTrace();
		}
		// return the list of ids
		return idList;
	}
	
	/**
	 * TODO
	 * @param term
	 * @param maxNumRecords
	 * @return
	 */
	public String[] ESearchEnv(String term, Integer maxNumRecords) {
		return ESearchEnv(term, maxNumRecords, 0);
	}
	
	/**
	 * Performs an ESearch against PubMed database using a search term
	 * 
	 * @param term
	 *            - The search term
	 * @param maxNumRecords
	 *            - Maximum number of records to pull, set currently by
	 *            Fetch.throttle.
	 * @param retStart TODO
	 * @return String[] = {WebEnv, QueryKey, idListLength}
	 * @author chaines
	 */
	public String[] ESearchEnv(String term, Integer maxNumRecords, Integer retStart)
	{
		// define the array to hold our Environment data
		String[] env = new String[3];
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
			// set number to start at
			req.setRetStart(retStart.toString());
			// save this search so we can use the returned set
			req.setUsehistory("y");
			// run the search and get result set
			EUtilsServiceStub.ESearchResult res = service.run_eSearch(req);
			log.trace("Query resulted in a total of " + res.getIdList().getId().length + " records.");
			// save the environment data
			env[0] = res.getWebEnv();
			env[1] = res.getQueryKey();
			env[2] = ""+res.getIdList().getId().length;
		}
		catch (RemoteException e)
		{
			log.error("Failed to run the search",e);
		}
		return env;
	}
	
	/**
	 * Performs a PubMed Fetch using a previously defined esearch environment and querykey
	 * @param env =  = {WebEnv, QueryKey, idListLength}
	 * @throws IllegalArgumentException 
	 */
	public void fetchPubMedEnv(String[] env) throws IllegalArgumentException {
		if(env.length != 3) {
			throw new IllegalArgumentException("Invalid WebEnv, QueryKey, and idListLength");
		}
		fetchPubMedEnv(env[0], env[1], "0", env[2]);
	}
	
	/**
	 * Performs a PubMed Fetch using a previously defined esearch environment and querykey
	 * @param env = {WebEnv, QueryKey, [idListLength]}
	 * @param start = String of record number to start at 
	 * @param numRecords = String of number of records to pull
	 * @throws IllegalArgumentException 
	 */
	public void fetchPubMedEnv(String[] env, String start, String numRecords) throws IllegalArgumentException {
		if(!(env.length == 2 || env.length == 3)) {
			throw new IllegalArgumentException("Invalid WebEnv and QueryKey");
		}
		fetchPubMedEnv(env[0], env[1], start, numRecords);
	}
	
	/**
	 * Performs a PubMed Fetch using a previously defined esearch environment and querykey
	 * @param WebEnv
	 * @param QueryKey
	 * @param intStart 
	 * @param maxRecords 
	 */
	public void fetchPubMedEnv(String WebEnv, String QueryKey, String intStart, String maxRecords) {
		EFetchPubmedServiceStub.EFetchRequest req = new EFetchPubmedServiceStub.EFetchRequest();
		req.setQuery_key(QueryKey);
		req.setWebEnv(WebEnv);
		req.setEmail(this.strEmailAddress);
		req.setTool(this.strToolLocation);
		req.setRetstart(intStart);
		req.setRetmax(maxRecords);
		log.trace("Fetching records from search");
		try {
			serializeFetchRequest(req);
		}catch(RemoteException e) {
			log.error("Could not run search",e);
		}
	}
	
	/**
	 * This method takes in a range of PMIDs and returns Query string to get all the ids
	 * 
	 * @param ids
	 *            Range of PMID you want to pull, in list form
	 * @return 
	 */
	public String queryPubMedIDs(List<Integer> ids) {
		StringBuilder strPMID = new StringBuilder();
		for(int id = 0; id < ids.size(); id++ ) {
			if(id != 0) {
				strPMID.append(",");
			}
			strPMID.append(ids.get(id));
		}
		return strPMID.toString()+"[uid]";
	}
	
	private void serializeFetchRequest(EFetchPubmedServiceStub.EFetchRequest req) throws RemoteException {
		ByteArrayOutputStream buffer=new ByteArrayOutputStream();
		EFetchPubmedServiceStub service = new EFetchPubmedServiceStub();
		EFetchResult result = service.run_eFetch(req);
		PubmedArticleSet_type0 articleSet = result.getPubmedArticleSet();
		XMLStreamWriter writer;
		try {
			writer = XMLOutputFactory.newInstance().createXMLStreamWriter(buffer);
			MTOMAwareXMLSerializer serial = new MTOMAwareXMLSerializer(writer);
			log.trace("Writing to output");
			articleSet.serialize(new QName("RemoveMe"), null, serial);
			serial.flush();
			log.trace("Writing complete");
//			log.trace("buffer size: "+buffer.size());
			String iString = buffer.toString("UTF-8");
			sanitizeXML(iString);
		} catch(XMLStreamException e) {
			log.error("Unable to write to output",e);
		} catch(UnsupportedEncodingException e) {
			log.error("Cannot get xml from buffer",e);
		}
	}
	
	/**
	 * This method takes in a range of PMIDs and returns MedLine XML to the main
	 * method as an outputstream.
	 * 
	 * @param id
	 *            PMID you want to pull
	 * @return 
	 */
	public String queryPubMedID(int id) {
		return id+"[uid]";
	}
	
	/**
	 * 
	 * @param intStartRecord
	 * @param intStopRecord
	 * @return 
	 */
	public String queryByRange(int intStartRecord, int intStopRecord)
	{
		return intStartRecord+":"+intStopRecord+"[uid]";
	}
	
	/**
	 * TODO
	 * @param intStartMonth
	 * @param intStartDay
	 * @param intStartYear
	 * @param intStopMonth
	 * @param intStopDay
	 * @param intStopYear
	 * @return 
	 */
	public String queryAllByDateRange(int intStartMonth, int intStartDay, int intStartYear, int intStopMonth, int intStopDay, int intStopYear)
	{
		return intStartYear+"/"+intStartMonth+"/"+intStartDay+"[PDAT]:"+intStopYear+"/"+intStopMonth+"/"+intStopDay+"[PDAT]";		
	}
	
	/**
	 * 
	 * @param intLastRunMonth
	 * @param intLastRunDay
	 * @param intLastRunYear 
	 * @return String query to fetch all from given date
	 */
	public String queryAllFromLastFetch(int intLastRunMonth, int intLastRunDay, int intLastRunYear)
	{
		return intLastRunYear+"/"+intLastRunMonth+"/"+intLastRunDay+"[PDAT]:8000[PDAT]";
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
//		List<Integer> lstResult = ESearchEnv("\""+intYear+"/"+intMonth+"/"+intDay+"\""+"[PDAT] : \""+(intYear + 5)+"/"+12+"/"+31+"\"[PDAT]", 1);
		List<Integer> lstResult = ESearch("\""+intYear+"/"+intMonth+"/"+intDay+"\""+"[PDAT] : \""+(intYear + 5)+"/"+12+"/"+31+"\"[PDAT]", 1);
		return lstResult.get(0);
	}
	
	/**
	 * Sanitize Method
	 * Adds the dtd and xml code to the top of the xml file and removes the extranious
	 * xml namespace attributes.  This function is slated for deprecation on mileston 2
	 * 
	 * @param s 
	 * @throws IOException
	 * @author cah
	 * @author swilliams
	 */
	private void sanitizeXML(String s) {
		log.trace("Sanitizing Output");
		
		//System Messages
//		log.trace("=================================\n=======================================================================\n=======================================================================\n=======================================================================");
//		log.trace(s);
//		log.trace("+++++++++++++++++++++++++++++++++\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		String newS = s.replaceAll(" xmlns=\".*?\"", "").replaceAll("</?RemoveMe>", "").replaceAll("</PubmedArticle>.*?<PubmedArticle", "</PubmedArticle>\n<PubmedArticle");
		log.trace("XML File Length - Pre Sanitize: " + s.length());
		log.trace("XML File Length - Post Sanitze: " + newS.length());
		try {
			this.xmlWriter.write(newS);
			//file close statements.  Warning, not closing the file will leave incomplete xml files and break the translate method
			this.xmlWriter.write("\n");
			this.xmlWriter.flush();
		} catch(IOException e) {
			log.error("Unable to write XML to file.",e);
		}
//		log.trace(newS);
//		log.trace("---------------------------------\n-----------------------------------------------------------------------\n-----------------------------------------------------------------------\n-----------------------------------------------------------------------");
		log.trace("Sanitization Complete");
	}
	
	/**
	 * @throws IOException
	 */
	public void beginXML() throws IOException {
		this.xmlWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		this.xmlWriter.write("<!DOCTYPE PubmedArticleSet PUBLIC \"-//NLM//DTD PubMedArticle, 1st January 2010//EN\" \"http://www.ncbi.nlm.nih.gov/corehtml/query/DTD/pubmed_100101.dtd\">\n");
		this.xmlWriter.write("<PubmedArticleSet>\n");
		this.xmlWriter.flush();
	}
	
	/**
	 * @throws IOException
	 */
	public void endXML() throws IOException {
		this.xmlWriter.flush();
		this.xmlWriter.write("</PubmedArticleSet>");
		this.xmlWriter.flush();
		this.xmlWriter.close();
	}
	
	/**
	 * Executes the fetch
	 * 
	 * FIXME eventually Fetch should be initialized with parameters such that it know which of the fetches to run and all
	 * -- that needs to be called is execute()
	 */
	public void execute()
	{
		log.info("Fetch Begin");
		//xml write functions, take in a stream pass it to a writer
		//Header lines for XML files from pubmed
		try {
			beginXML();
			this.fetchAll();
			endXML();
		} catch(IOException e) {
			log.error("",e);
		}
//		this.fetchByAffiliation("ufl.edu", 20);
		log.info("Fetch End");
		// TODO throttling should be done as part of the queries maybe? the current throttle does not work with the idea of
		// -- WebEnv/QueryKey fetching... Will need to research how that will work
	}
	
}