package edu.cornell.mannlib.vitro.indexbuilder;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This is a class intended to parse URIs from
 * catalyst search page.
 */
public class CatalystPageToURIs{

  /**
   * This parses a HTML page from the Harvard Catalyst system
   * into a list of URIs to subsequently request via HTTP as 
   * linked data.
   */   
    public synchronized static Collection<String> parseForURIs( String text ) {
    	
    	final String prefix = "http://connects.catalyst.harvard.edu/profiles/profile/person/";
    	
    	Document doc = Jsoup.parse(text);
    	Set<String> listOfIDS = new HashSet<String>();
    	
    	Elements rows = doc.select("body div div div table tr td div div table tr td div div table tr");
    	for(Element e : rows){
    		if(e.hasClass("oddRow") || e.hasClass("evenRow")){
    			String id = e.attr("onclick");
    			listOfIDS.add(prefix + extractIdentifier(id));
    		}
    	}
    	
    	return listOfIDS;
    }
    
    /**
     * Extract the identifier from the onclick attribute value.
     * @param id
     * @return
     */
	private static String extractIdentifier(String id) {
		
		char[] arr = id.toCharArray();
		StringBuffer sb = new StringBuffer();
		
		for(char c : arr){
			if(c >= 48 && c <= 57){
				sb.append(c);
			}
		}
		return sb.toString();
	}
}

