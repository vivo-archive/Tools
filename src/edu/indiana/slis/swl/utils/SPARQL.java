package edu.indiana.slis.swl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class SPARQL {
	
	public static String namespace = null; // namespaces string for the sparql query
	public static HashMap<String, String> namespaces = null; // namespaces hashmap for substituting the uri with namespace
	public static HashMap<String, String> hidden = null; //
	
	public String path = "/../namespaces.properties";
	
	//initialize the namespace, namespaces and hidden
	public void init() {
		InputStream is = this.getClass().getResourceAsStream(this.path);
		Properties prop = new Properties();
		this.namespace = "";
		this.namespaces = new HashMap<String, String>();
		this.hidden = new HashMap<String, String>();
		
		// add rdf:type to hidden hash map
		hidden.put("rdf:type", "rdf:type");
		
		// load namespaces from namespaces.properties
		try {
			prop.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set keys = prop.keySet();
		
		if (keys.size() < 0){
			System.out.println("No element is found in namespaces.properties.");
		}
		else{
			Iterator keyit = keys.iterator();
			while (keyit.hasNext()){
				String prefix = (String) keyit.next();
				String uri = prop.getProperty(prefix);
				this.namespace += "PREFIX " + prefix + ": <" + uri + ">\n";
				this.namespaces.put(uri, prefix);
			}
		}
	}
	
	// compose the query given criterions
	public static String composeQuery(String select, String where){
		String query = "";
		query = SPARQL.namespace +
			"SELECT \n" + 
			select +
			"\nWHERE {\n" +
			where +
			"\n}\n";
		return query;
	}
	
	// the query that gets the properties
	public static String getPropertyQuery(String subject){
		String query = "";
		
		query = SPARQL.namespace +
			"SELECT distinct ?pre" +
			"\nWHERE {\n" +
			"?one rdf:type " + subject + " .\n" + 
			"?one ?pre ?obj .\n" +
			"}\n";
		return query;
	}
	
	// the query that gets the classes
	public static String getClazzQuery(){
		String query = "";
		query = SPARQL.namespace +
			"SELECT distinct ?obj" +
			"\nWHERE {\n" +
			"?sub rdf:type ?obj .\n" + 
			"}\n";
		return query;
	}
	
	// the query that get object
	
	public static String getObjectQuery(String subject, String predicate){
		String query = "";
		
		query = SPARQL.namespace +
			"SELECT distinct ?objclazz" +
			"\nWHERE {\n" +
			"?sub rdf:type " + subject + " . \n" + 
			"?sub " + predicate + " ?obj .\n" +
			"FILTER isURI(?obj) \n" +
			"?obj rdf:type ?objclazz" +
			"}\n";
		return query;
	}
	
	/*
	public static String getObjectQuery(String subject, String predicate){
		String query = "";
		
		query = SPARQL.namespace +
			"SELECT distinct ?obj" +
			"\nWHERE {\n" +
			"?sub rdf:type " + subject + " . \n" + 
			"?sub " + predicate + " ?obj .\n" +
			"FILTER isURI(?obj) \n" +
			"}\n";
		return query;
	}
	/*
	public static String getObjectQuery(String subject, String predicate){
		String query = "";
		
		query = SPARQL.namespace +
			"SELECT distinct ?obj" +
			"\nWHERE {\n" +
			"?sub rdf:type " + subject + " . \n" + 
			"?sub " + predicate + " ?obj .\n" +
			"}\n";
		return query;
	}
	*/
	
	//
	public static String checkPropertyQuery(String instance){
		String query = "";
		
		query = SPARQL.namespace +
			"SELECT distinct ?clazz" +
			"\nWHERE {\n" +
			"<" + instance + "> rdf:type ?clazz .\n" +
			"}\n";
		return query;
	}
	
	// compose the xml result
	public static String composeXML(List result){
		
		String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		response += "<options>";
		Iterator it = result.iterator();
		
		while(it.hasNext()){
			
			String item = (String) it.next();
			response += "<option>" + item + "</option>";
			
		}
		
		response += "</options>";
		
		return response;
	}
	
	// compose the html result
	public static String composeHTMLResult(List result){
		String response = "<table border='1' cellspacing='0' cellpadding='0'>";
		boolean header = true;
		Iterator it = result.iterator();
		while(it.hasNext()){
			HashMap hm = (HashMap) it.next();
			Set key = hm.keySet();
			Iterator keyit = key.iterator();
			String head = "<tr>";
			String record = "<tr>";
			while(keyit.hasNext()){
				
				String item = (String) keyit.next();
				if (header){
					head += "<th>" + item + "</th>";
				}
				String obj = hm.get(item).toString();
				record += "<td>" + SPARQL.getValue(obj) + "</td>";
			}
			if (header){
				head += "</tr>";
				response += head;
				header = false;
			}
			record += "</tr>";
			response += record;
		}
		response += "</table>";
		return response;
	}
	
	// get the literal value of the string
	public static String getValue(String value){
		int index = value.indexOf("\"@");
		int length = value.length();
		if (index > 0){
			value = value.substring(1, length-4);
		}
		else{
			index = value.lastIndexOf('\"');
			if (index > 0){
				value = value.substring(1, value.lastIndexOf('\"'));
			}
		}
		return value;
	}
	
	// get the uri with prefix
	public static String getUriWithPrefix(String uri){
		String preuri = "";
		Iterator keyit = SPARQL.namespaces.keySet().iterator();
		while (keyit.hasNext()){
			String namespace = (String) keyit.next();
			if (uri.startsWith(namespace)){
				String prefix = (String) SPARQL.namespaces.get(namespace);
				preuri = uri.replace(namespace, prefix + ":");
				return preuri;
			}
		}
		return preuri;
	}
}
