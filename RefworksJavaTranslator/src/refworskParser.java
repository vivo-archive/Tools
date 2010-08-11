import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.lang.StringBuilder;

/**
 * 
 * dcelem:  	 http://purl.org/dc/elements/1.1/
 * 
 * RT=Reference Type
 * SR=Source Type (field is either Print(0) or  Electronic(1) )
 * ID=Reference Identifier
 * A1=Primary Authors																		core:authorNameAsListed core:AuthorRank rdfs:label
 * T1=Primary Title																			dcelem:Title rdfs:label
 * JF=Periodical Full
 * JO=Periodical Abbrev
 * YR=Publication Year
 * FD=Publication Data, Free Form
 * VO=Volume
 * IS=Issue
 * SP=Start Page
 * OP=Other Pages
 * K1=Keyword																				core:freetextKeyword
 * AB=Abstract
 * NO=Notes
 * A2=Secondary Authors (Editors)
 * T2=Secondary Title
 * ED=Edition
 * PB=Publisher
 * PP=Place of Publication
 * A3=Tertiary Authors
 * A4=Quaternary Authors
 * A5=Quinary Authors
 * T3=Tertiary Title
 * SN=ISSN/ISBN
 * AV=Availability
 * AD=Author Address
 * AN=Accession Number
 * LA=Language
 * CL=Classification
 * SF=Subfile/Database
 * OT=Original Foreign Title
 * LK=Links
 * DO=Digital Object Identifier
 * CN=Call Number
 * DB=Database
 * DS=Data Source
 * IP=Identifying Phrase
 * RD=Retrieved Date
 * ST=Shortened Title
 * U1=User 1
 * U2=User 2
 * U3=User 3
 * U4=User 4
 * U5=User 5
 * U6=User 6
 * U7=User 7
 * U8=User 8
 * U9=User 9
 * U10=User 10
 * U11=User 11
 * U12=User 12
 * U13=User 13
 * U14=User 14
 * U15=User 15
 * UL=URL
 * SL=Sponsoring Library
 * LL=Sponsoring Library Location
 * CR=Cited References
 * WT=Website Title
 * A6=Website editors
 * WV=Website version
 * WP=Date of Electronic Publication
 * OL=Output Language (see codes for specific languages below)
 * PMID=PMID
 * PMCID=PMCID
 * 
 * 
 * @author swilliams@ctrip.ufl.edu 
 *
 */
public class refworskParser {
	
	
	public static void parseTranslate(String fileName, String uri){
		try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(fileName));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());


            NodeList listofReferences = doc.getElementsByTagName("reference");
            int totalRef = listofReferences.getLength();
            System.out.println("Total no of references : " + totalRef);
            
            StringBuilder referenceN3 = new StringBuilder();
            referenceN3.append(uri + "\n");
            
            for(int i=0; i<totalRef ; i++){

            	Node refNode = listofReferences.item(i);
            	if(refNode.getNodeType() == Node.ELEMENT_NODE){

                    Element refElement = (Element)refNode;
                    referenceN3.append("core:authorInAuthorship [ ");
                    referenceN3.append("a core:Authorship;\n");
                    referenceN3.append("core:linkedInformationResource [ ");										//FIXME
                    
                    //Reference Type
                    NodeList typeList = refElement.getElementsByTagName("rt");
                    String mainType = typeList.item(0).getChildNodes().item(0).getNodeValue().trim();				//to preform special class properties	                    
//                    for (int t=0; t<typeList.getLength(); t++)
//                    {
//                    	Element typeElement = (Element)typeList.item(t);
//                    	System.out.println(typeElement.getChildNodes().item(0).getNodeValue().trim());
//                    }

                    /*
                     * This section details the values that are different depending on type but use the same key values
                     */
                    if (mainType.equals("Journal Article")) {						//bibo:Article (don't know if its academic)
                    	referenceN3.append(" a bibo:Article;\n");
                    	
                    	//look for the periodical
	                    NodeList journalList = refElement.getElementsByTagName("jf");
	                    NodeList issnList = refElement.getElementsByTagName("sn");
	                                 
	                    
	                    if (journalList.getLength() == issnList.getLength())
	                    {		                    
		                    for (int j=0; j<journalList.getLength(); j++){
		                    	Element journalElement = (Element)journalList.item(j);
		                    	Element issnElement = (Element)issnList.item(j);
		                    	String outputStr = "core:featuredIn [ ";												//FIXME
		                    	outputStr += "rdfs:label \"" + journalElement.getChildNodes().item(0).getNodeValue().trim() + "\";\n";
		                    	outputStr += "bibo:issn \"" + issnElement.getChildNodes().item(0).getNodeValue().trim() + "\" ];\n";
		                    	referenceN3.append(outputStr);
		                    }
	                    }
	                    else
	                    {
	                    	System.out.println("Error in Parsing Journal");
	                    }
	                    
	                  //Reference Authors (a1)
	                    NodeList issueList = refElement.getElementsByTagName("is");		      
	                    for (int is=0; is<issueList.getLength(); is++)
	                    {
	                    	Element issueElement = (Element)issueList.item(is);
	                    	String outputStr = "rdfs:label \"" + issueElement.getChildNodes().item(0).getNodeValue().trim() + "\";\n";	
                    		referenceN3.append(outputStr);
	                    }
	                    

	                    NodeList volumeList = refElement.getElementsByTagName("vo");	      
	                    for (int v=0; v<issueList.getLength(); v++)
	                    {
	                    	Element volumeElement = (Element)volumeList.item(v);
	                    	String outputStr = "rdfs:label \"" + volumeElement.getChildNodes().item(0).getNodeValue().trim() + "\";\n";	
                    		referenceN3.append(outputStr);
	                    }
	                    
	                    
                    }
                    else if (mainType.equals("Journal, Electronic")) {				//bibo:Article
                    	referenceN3.append(" a bibo:Article;\n");
                    }
                    else if (mainType.equals("Magazine Article")) {					//bibo:Article
                    	referenceN3.append(" a bibo:Article;\n");
                    }
                    else if (mainType.equals("Newspaper Article")) {				//bibo:Article
                    	referenceN3.append(" a bibo:Article;\n");
                    }
                    else if (mainType.equals("Book, Section")) {					//bibo:BookSection
                    	referenceN3.append(" a bibo:BookSection;\n");
                    }
                    else if (mainType.equals("Book, Edited")) {						//bibo:EditedBook
                    	referenceN3.append(" a bibo:EditedBook;\n");	
                    }
                    else if (mainType.equals("Book, Whole")) {						//bibo:Book
                    	referenceN3.append(" a bibo:Book;\n");
                    }
                    else if (mainType.equals("Conference Proceedings")) {			//bibo:Article
                    	referenceN3.append(" a bibo:Article;\n");
                    }
                    else if (mainType.equals("Dissertation/Thesis")){				//bibo:Thesis
                    	referenceN3.append(" a bibo:Thesis;\n");
                    }
                    else if (mainType.equals("Dissertation/Thesis, Unpublished")){	//bibo:Thesis
                    	referenceN3.append(" a bibo:Thesis;\n");
                    }
                    else if (mainType.equals("Bills/Resolutions")){					//bibo:Bill
                    	referenceN3.append(" a bibo:Bill;\n");
                    }	                    
                    else if (mainType.equals("Case/Court Decisions")){				//bibo:LegalCaseDocument
                    	referenceN3.append(" a bibo:LegalCaseDocument;\n");
                    }
                    else if (mainType.equals("Law/Statues")){						//bibo:Statute
                    	referenceN3.append(" a bibo:Statute;\n");
                    }
                    else if (mainType.equals("Patent")){							//bibo:Patent
                    	referenceN3.append(" a bibo:Patent;\n");
                    }	                    
                    else {														//bibo:Document
                    	referenceN3.append(" a bibo:Document;\n");
                    }
                    
                    
                    //Reference Authors (a1)
                    NodeList titleList = refElement.getElementsByTagName("t1");
                    for (int t=0; t<titleList.getLength(); t++)
                    {
                    	Element titleElement = (Element)titleList.item(t);
                    	String title = titleElement.getChildNodes().item(0).getNodeValue().trim();
                    	String outputStr = "dcelem:title \"" + title + "\";\n";
                    	outputStr += "rdfs:label \"" + title + "\";\n";	
                		referenceN3.append(outputStr);
                    }
               		 
                    //Reference Authors (a1)
                    NodeList authorList = refElement.getElementsByTagName("a1");
                    for (int a=0; a<authorList.getLength(); a++)
                    {
                    	Element authorElement = (Element)authorList.item(a);
                    	String authorName = authorElement.getChildNodes().item(0).getNodeValue().trim();
                    	String outputStr = "core:informationResourceInAuthorship [ ";
                    	outputStr += "a core:Authorship;\n" ;
                    	outputStr += "core:authorNameAsListed \"" + authorName + "\";\n";
                    	outputStr += "core:authorRank " + String.valueOf(a+1) + ";\n";
                    	outputStr += "rdfs:label \"" + authorName + "\" ];\n";
                    	referenceN3.append(outputStr);
                    }
                    
                    //Reference Authors (a1)
                    NodeList editorList = refElement.getElementsByTagName("a1");
                    for (int a=0; a<editorList.getLength(); a++)
                    {
                    	Element authorElement = (Element)authorList.item(a);
                    	String authorName = authorElement.getChildNodes().item(0).getNodeValue().trim();
                    	String outputStr = "core:informationResourceInAuthorship [ ";
                    	outputStr += "a core:Authorship;\n" ;
                    	outputStr += "core:authorNameAsListed \"" + authorName + "\";\n";
                    	outputStr += "core:authorRank " + String.valueOf(a+1) + ";\n";
                    	outputStr += "rdfs:label \"" + authorName + "\" ];\n";
                    	referenceN3.append(outputStr);
                    }
                    
                    //Keywords(k1) - core:freetextKeyword
                    NodeList keywordList = refElement.getElementsByTagName("k1");
                    for (int a=0; a<keywordList.getLength(); a++)
                    {
                    	Element keywordElement = (Element)keywordList.item(a);
                    	String outputStr = "core:freetextKeyword \"" + keywordElement.getChildNodes().item(0).getNodeValue().trim() + "\";\n";
                    	referenceN3.append(outputStr);
                    }
                    
                    //Abstract(AB) - bibo:abstract
                    NodeList abstractList = refElement.getElementsByTagName("ab");
                    for (int ab=0; ab<abstractList.getLength(); ab++)
                    {
                    	Element abstractElement = (Element)abstractList.item(ab);
                    	String outputStr = "bibo:abstract \"" + abstractElement.getChildNodes().item(0).getNodeValue().trim() + "\";\n";
                    	referenceN3.append(outputStr);
                    }
                    
                    //Issue(IS) - bibo:number
//                    NodeList keywordList = refElement.getElementsByTagName("k1");
//                    for (int a=0; a<keywordList.getLength(); a++)
//                    {
//                    	Element keywordElement = (Element)keywordList.item(a);
//                    	String outputStr = "core:freetextKeyword \"" + keywordElement.getChildNodes().item(0).getNodeValue().trim() + "\";\n";
//                    	referenceN3.append(outputStr);
//                    }
                    
                    //Volume(VO) - bibo:volume
//                    NodeList keywordList = refElement.getElementsByTagName("k1");
//                    for (int a=0; a<keywordList.getLength(); a++)
//                    {
//                    	Element keywordElement = (Element)keywordList.item(a);
//                    	String outputStr = "core:freetextKeyword \"" + keywordElement.getChildNodes().item(0).getNodeValue().trim() + "\";\n";
//                    	referenceN3.append(outputStr);
//                    }
                    
                    

	            	referenceN3.append(" ] ] .\n");
                }//end of if clause


            }//end of for loop with s var

            System.out.println(referenceN3.toString());
            
            //File out
            BufferedWriter outBW = new BufferedWriter(new FileWriter(fileName.replace(".xml", ".rdf")));
            outBW.write(referenceN3.toString());
            outBW.close();
            
            
        }catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
        t.printStackTrace ();
        }
        //System.exit (0);	

	}
	
	public static void main(String[] args) {
		
		if (args.length == 2 && args[1].endsWith(".xml")){
			String fileName = args[1];
			String uri = args[0];
			
			parseTranslate(fileName, uri);
		}
		else if(args.length == 1){
			
			 try{
			    // Open the file that is the first 
			    // command line parameter
			    FileInputStream fstream = new FileInputStream(args[0]);
			    // Get the object of DataInputStream
			    DataInputStream in = new DataInputStream(fstream);
			    BufferedReader br = new BufferedReader(new InputStreamReader(in));
			    String strLine;
			    //Read File Line By Line
			    while ((strLine = br.readLine()) != null)   {
			      // Print the content on the console
			      System.out.println (strLine);
			      
			      String[] splitStr = null;
			      
			      splitStr = strLine.split(";");
			      String fileName = splitStr[1];
			      String uri = splitStr[0];
			      
			      parseTranslate(fileName,uri);
			    }
			    //Close the input stream
			    in.close();
			    }catch (Exception e){//Catch exception if any
			      System.err.println("Error: " + e.getMessage());
			    }
				 
		}
		else {
			System.out.println("Invalid Arguments");
		}			
						
			
		
	}
}
