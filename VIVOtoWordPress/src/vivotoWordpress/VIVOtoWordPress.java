/*******************************************************************************
Copyright (c) 2010, Dale Scheppler
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of the author nor the names of any contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package vivotoWordpress;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VIVOtoWordPress {
	
	//Uuuuuugly cooooode.
	
	public static void main (String[] args)
	{
		
		//Lets get those variables created
		List<String> lstPositions = new ArrayList<String>();
		List<String> lstPeople = new ArrayList<String>();
		
		
		FileWriter fstream;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter("wordpress.2010-10-08.xml");
			out = new BufferedWriter(fstream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//First, get the positions links from the department RDF
		lstPositions = new ArrayList<String>(getPositions(args[0]));
		//Then, let's roll through those positions and get the people
		try {
			lstPeople = new ArrayList<String>(getPeople(lstPositions));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
//		System.out.println("Position URIs: "+ lstPositions);
//		System.out.println("Person URIs: "+ lstPeople);
		
		try {
			writeData(lstPeople, out);
			
			writeFooter(out);
			out.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
//	public static List<String> getDataFromPeople (List<String> peoInput)
//	{
//		List<String> lstInfo = new ArrayList<String>();
//		
//		
//	}
	
	public static void writeData(List<String> datInput, BufferedWriter out) throws IOException
	{

		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		out.write("<rss version=\"2.0\"\n");
		out.write("xmlns:excerpt=\"http://wordpress.org/export/1.0/excerpt/\"\n");
		out.write("xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"\n");
		out.write("xmlns:wfw=\"http://wellformedweb.org/CommentAPI/\"\n");
		out.write("xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n");
		out.write("xmlns:wp=\"http://wordpress.org/export/1.0/\"\n");
		out.write(">\n");
		out.write("<channel>\n");
		out.write("<title>Wordpress VIVO Integration Test</title>\n");
		out.write("<link>http://vivotest.ctrip.ufl.edu/wordpressvivo</link>\n");
		out.write("<description>Just another WordPress site</description>\n");
		out.write("<pubDate></pubDate>\n");
		out.write("<generator>http://wordpress.org/?v=3.0.1</generator>\n");
		out.write("<language>en</language>\n");
		out.write("<wp:wxr_version>1.0</wp:wxr_version>\n");
		out.write("<wp:base_site_url>http://vivotest.ctrip.ufl.edu/wordpressvivo</wp:base_site_url>\n");
		out.write("<wp:base_blog_url>http://vivotest.ctrip.ufl.edu/wordpressvivo</wp:base_blog_url>\n");
		out.write("<wp:category><wp:category_nicename>uncategorized</wp:category_nicename><wp:category_parent></wp:category_parent><wp:cat_name><![CDATA[Uncategorized]]></wp:cat_name></wp:category>\n");
		out.write("<generator>http://wordpress.org/?v=3.0.1</generator>\n");
		out.write("<item>\n");
		out.write("<title>Department Members</title>\n");
		out.write("<link>http://vivotest.ctrip.ufl.edu/wordpressvivo/?page_id=5</link>\n");
		out.write("<pubDate>Fri, 08 Oct 2010 06:54:42 +0000</pubDate>\n");
		out.write("<dc:creator><![CDATA[Leonassan]]></dc:creator>\n");
		out.write("<guid isPermaLink=\"false\">http://vivotest.ctrip.ufl.edu/wordpressvivo/?page_id=5</guid>\n");
		out.write("<description></description>\n");
		out.write("<content:encoded><![CDATA[An auto-generated list of people in this department.]]></content:encoded>\n");
		out.write("<excerpt:encoded><![CDATA[]]></excerpt:encoded>\n");
		out.write("<wp:post_id>5</wp:post_id>\n");
		out.write("<wp:post_date>2010-10-08 06:54:42</wp:post_date>\n");
		out.write("<wp:post_date_gmt>2010-10-08 06:54:42</wp:post_date_gmt>\n");
		out.write("<wp:comment_status>closed</wp:comment_status>\n");
		out.write("<wp:ping_status>closed</wp:ping_status>\n");
		out.write("<wp:post_name>department-members</wp:post_name>\n");
		out.write("<wp:status>publish</wp:status>\n");
		out.write("<wp:post_parent>0</wp:post_parent>\n");
		out.write("<wp:menu_order>0</wp:menu_order>\n");
		out.write("<wp:post_type>page</wp:post_type>\n");
		out.write("<wp:post_password></wp:post_password>\n");
		out.write("<wp:is_sticky>0</wp:is_sticky>\n");
		out.write("<wp:postmeta>\n");
		out.write("<wp:meta_key>_edit_last</wp:meta_key>\n");
		out.write("<wp:meta_value><![CDATA[2]]></wp:meta_value>\n");
		out.write("</wp:postmeta>\n");
		out.write("<wp:postmeta>\n");
		out.write("<wp:meta_key>_edit_lock</wp:meta_key>\n");
		out.write("<wp:meta_value><![CDATA[1286520882]]></wp:meta_value>\n");
		out.write("</wp:postmeta>\n");
		out.write("<wp:postmeta>\n");
		out.write("<wp:meta_key>_wp_page_template</wp:meta_key>\n");
		out.write("<wp:meta_value><![CDATA[default]]></wp:meta_value>\n");
		out.write("</wp:postmeta>\n");
		out.write("</item>\n");

		//End Header
		
		BufferedReader datReader = null;
		String datLine = null;
		URL datURI = null;
		Integer postID = 1000;
		
		
		
		for (Iterator<String> itData = datInput.iterator(); itData.hasNext();)
		{
			String nameLine = null;
			String emailLine = null;
			String posLine = null;
			String posOut = null;
			
			String s =itData.next();
			datURI = generateVIVOURL(s);
			try{
				datReader = read(datURI);
			}
			catch (Exception e){
				e.printStackTrace();
			}
			//begin item entry

			try
			{
				datLine = datReader.readLine();
				while (datLine != null) {
					//System.out.println(datLine);
					if(datLine.contains("    <rdfs:label>")){
					nameLine = datLine.replace("    <rdfs:label>","");
					nameLine = nameLine.replace("</rdfs:label>", "");
					//System.out.println("Name: " + nameLine);					
					}

					if(datLine.contains("    <j.2:workEmail>")){
						emailLine = datLine.replace("    <j.2:workEmail>", "");
						emailLine = emailLine.replace("</j.2:workEmail>", "");
						//System.out.println("Email Address: " + emailLine);
					}
					if(datLine.contains("    <j.2:personInPosition rdf:resource=\"http://vivo.ufl.edu/individual/")) {
						posLine = datLine.replace("    <j.2:personInPosition rdf:resource=\"http://vivo.ufl.edu/individual/", "");
						posLine = posLine.replace("\"/>", "");
						URL posURI = generateVIVOURL(posLine);
						BufferedReader posReader = null;
						posReader = read(posURI);
						String posLine2 = posReader.readLine();
						while (posLine2 != null) {
							//System.out.println(posLine);
							if(posLine2.contains("    <j.0:preferredTitle>")){
								posOut = posLine2.replace("    <j.0:preferredTitle>", "");
								posOut = posOut.replace("</j.0:preferredTitle>", "");
								//System.out.println("Position Title: " + posOut);
								
							}
														
							posLine2 = posReader.readLine();
							
						}
						
					}
					
					datLine = datReader.readLine();
					postID = postID + 1;
				}
				if(nameLine == null)
				{
					System.out.println("We have a null name!");
				}
				writeItem(nameLine, emailLine, posOut, postID, out);
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}

		
	}
	
	public static void writeItem(String strName, String strEmail, String strPosition, Integer postID, BufferedWriter out) throws IOException{
		//System.out.println("Using name: " + strName + " and Email: " + strEmail + " and title: " + strPosition);
		if(strName == null || strPosition == null)
		{
			
		}
		else

		{
		out.write("<item>\n");
		out.write("<title>"+strName+"</title>\n");
		out.write("<link>http://vivotest.ctrip.ufl.edu/wordpressvivo/?page_id="+postID);
		out.write("<pubDate>Fri, 08 Oct 2010 06:55:47 +0000</pubDate>\n");
		out.write("<dc:creator><![CDATA[vivoautopost]]></dc:creator>\n");
		out.write("<guid isPermaLink=\"false\">http://vivotest.ctrip.ufl.edu/wordpressvivo/?page_id="+postID+"</guid>\n");
		out.write("<description></description>\n");
		out.write("<content:encoded><![CDATA[Name: " + strName + "\n\nPosition Title:" + strPosition + "\n\nEmail Address: " + strEmail + "]]></content:encoded>\n");
		out.write("<excerpt:encoded><![CDATA[]]></excerpt:encoded>\n");
		out.write("<wp:post_id>"+postID+"</wp:post_id>\n");
		out.write("<wp:post_date>2010-10-08 06:55:47</wp:post_date>\n");
		out.write("<wp:post_date_gmt>2010-10-08 06:55:47</wp:post_date_gmt>\n");
		out.write("<wp:comment_status>closed</wp:comment_status>\n");
		out.write("<wp:ping_status>closed</wp:ping_status>\n");
		out.write("<wp:post_name>"+strName+"</wp:post_name>\n");
		out.write("<wp:status>publish</wp:status>\n");
		out.write("<wp:post_parent>5</wp:post_parent>\n");
		out.write("<wp:menu_order>0</wp:menu_order>\n");
		out.write("<wp:post_type>page</wp:post_type>\n");
		out.write("<wp:post_password></wp:post_password>\n");
		out.write("<wp:is_sticky>0</wp:is_sticky>\n");
		out.write("<wp:postmeta>\n");
		out.write("<wp:meta_key>_edit_last</wp:meta_key>\n");
		out.write("<wp:meta_value><![CDATA[2]]></wp:meta_value>\n");
		out.write("</wp:postmeta>\n");
		out.write("<wp:postmeta>\n");
		out.write("<wp:meta_key>_edit_lock</wp:meta_key>\n");
		out.write("<wp:meta_value><![CDATA[1286522043]]></wp:meta_value>\n");
		out.write("</wp:postmeta>\n");
		out.write("<wp:postmeta>\n");
		out.write("<wp:meta_key>_wp_page_template</wp:meta_key>\n");
		out.write("<wp:meta_value><![CDATA[default]]></wp:meta_value>\n");
		out.write("</wp:postmeta>\n");
		out.write("</item>\n");
		}
	}
	
	public static void writeFooter(BufferedWriter out) throws Exception{
		out.write("</channel>\n");
		out.write("</rss>\n");
	}
	
	public static List<String> getPeople(List<String> posInput) throws MalformedURLException
	{
		List<String> lstPeople = new ArrayList<String>();
		BufferedReader peoReader = null;
		String peoLine = null;
		URL peoURI = null;
		
		for (Iterator<String> itPeople = posInput.iterator(); itPeople.hasNext();)
		{
			String s = itPeople.next();
			peoURI = generateVIVOURL(s);
			try {
				peoReader = read(peoURI);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				peoLine = peoReader.readLine();
				while (peoLine != null) {
					//System.out.println(peoLine);
					if(getPeopleFromPositions(peoLine) != null)
					{
						//Get the position URIs for department members
						lstPeople.add(getPeopleFromPositions(peoLine));
						//System.out.println(getPeopleFromPositions(peoLine));
					}
					
					peoLine = peoReader.readLine();
					
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lstPeople;
		
	}
	
	public static String getPeopleFromPositions(String inputString)
	{
		if (inputString.startsWith("    <j.0:positionForPerson rdf:resource=")) {
			String procString = inputString.replace("    <j.0:positionForPerson rdf:resource=\"http://vivo.ufl.edu/individual/", "");
			procString = procString.replace("\"/>", "");
			//System.out.println(procString);
			return procString;
		}
		else {
			return null;
		}
	}
	
	
	
	
	public static List<String> getPositions(String deptName)
	{
		//Let's initialize some variables.
		URL deptURI = null;
		BufferedReader deptReader = null;
		String line = null;
		List<String> lstPositions = new ArrayList<String>();
				
		
		//All sorts of debug code in here. Clean it up later.
//		System.out.println("Begin Process.");
//		System.out.println("Attempting to read department data of " + deptName);
		try
		{
			deptURI = generateVIVOURL(deptName);
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
//		System.out.println("Generated URI of: " + deptURI.toString());
		
		//Begin reading in that document
		
		try {
			deptReader = read(deptURI);
		}
		catch (Exception e)	{
			e.printStackTrace();
		}
		
		try {
			line = deptReader.readLine();
			while (line != null) {
				//System.out.println(line);
				if(getPositionsFromDept(line) != null)
				{
					//Get the position URIs for department members
					lstPositions.add(getPositionsFromDept(line));
					
				}
				
				line = deptReader.readLine();
				
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return lstPositions;
		
		
	}
	
	
	public static URL generateVIVOURL(String inputURI) throws MalformedURLException
	{
			URL genURL = new URL("http://vivo.ufl.edu/individual/"+inputURI+"/"+inputURI+".rdf");
			return genURL;
		
	}
	
	public static BufferedReader read(URL deptURI) throws Exception{
		return new BufferedReader(
			new InputStreamReader(deptURI.openStream()));}

	public static String getPositionsFromDept(String inputString){
		if (inputString.startsWith("    <j.1:organizationForPosition")) {
			String procString = inputString.replace("    <j.1:organizationForPosition rdf:resource=\"http://vivo.ufl.edu/individual/", "");
			procString = procString.replace("\"/>", "");
			return procString;
		}
		else {
			return null;
		}
			
	}
}


