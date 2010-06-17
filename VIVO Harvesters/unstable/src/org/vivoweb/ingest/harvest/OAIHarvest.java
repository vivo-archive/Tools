package org.vivoweb.ingest.harvest;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;


import ORG.oclc.oai.harvester2.app.RawWrite;


public class OAIHarvest {

	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
//		FileOutputStream out; // declare a file output object
//		out = new FileOutputStream("UFDC.xml");
//		RawWrite.run("http://www.uflib.ufl.edu/ufdc/", "2005-01-01", "2010-06-15", "oai_dc", "", out);
//		out.close();

	}
	public static void execute(String strAddress, String strStartDate, String strEndDate, FileOutputStream fosOutStream) throws Exception, Exception, SAXException, TransformerException, NoSuchFieldException
	{
		RawWrite.run("http://" + strAddress, strStartDate, strEndDate, "oai_dc", "", fosOutStream);
	}

}
