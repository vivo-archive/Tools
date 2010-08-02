package edu.indiana.slis.swl.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.indiana.slis.swl.sesame.SesameService;
import edu.indiana.slis.swl.utils.SPARQL;

public class SparqlQueryService {
	
	
	private static SesameService sesame;
	
	
	/*
	 * Initialized the SparqlQueryService
	 */
	
	public SparqlQueryService(String db){
		if(this.sesame == null){
			this.sesame = new SesameService(false, db);
		}
		if (SPARQL.namespace == null) {
			SPARQL sparql = new SPARQL();
			sparql.init();
		}
	}
	
	/*
	 * Get the result of given query
	 */
	
	public List getResult(String query){
		List result = this.sesame.runSPARQL(query);
		return result;
	}
	
	/*
	 * Get the result of given query and item
	 */
	
	public List getItemResult(String query, String item){
		List itemResult = new ArrayList();
		List result = this.getResult(query);
		Iterator it = result.iterator();
		
		while(it.hasNext()){
			HashMap hm = (HashMap)it.next();
			String obj = hm.get(item).toString();
			String clazz = SPARQL.getUriWithPrefix(obj);
			if (!clazz.equals("")){
				itemResult.add(clazz);
			}
		}
		return itemResult;
	}
	
	
	/*
	 * Get all the classes of the data
	 */
	
	public List getClazz(){
		List clazzes = new ArrayList();
		
		String query = SPARQL.getClazzQuery();
		
		List result = this.getResult(query);
		
		Iterator it = result.iterator();
		
		while(it.hasNext()){
			HashMap hm = (HashMap)it.next();
			String obj = hm.get("obj").toString();
			String clazz = SPARQL.getUriWithPrefix(obj);
			if (!clazz.equals("")){
				clazzes.add(clazz);
			}
		}
		return clazzes;
	}
	
	/*
	 * Get all the properties of given class
	 */
	
	public List getProperty(String subject){
		
		List properties = new ArrayList();
		
		String query = SPARQL.getPropertyQuery(subject);
		List result = this.getResult(query);
		
		Iterator it = result.iterator();
		
		while(it.hasNext()){
			HashMap hm = (HashMap)it.next();
			String obj = hm.get("pre").toString();
			String property = SPARQL.getUriWithPrefix(obj);
			if (!property.equals("")){
				if (!SPARQL.hidden.containsKey(property)){
					properties.add(property);
				}
			}
		}
		return properties;
	}
	
	/*
	 * Get the objects of given subject and predicate
	 */
	public List getObject(String subject, String predicate){
		
		List objects = new ArrayList();
			
		String query = SPARQL.getObjectQuery(subject, predicate);
		List result = this.getResult(query);
		
		Iterator it = result.iterator();
		while(it.hasNext()){
			HashMap hm = (HashMap)it.next();
			String obj = hm.get("objclazz").toString();
			String clazz = SPARQL.getUriWithPrefix(obj);
			if (!clazz.equals("")){
				objects.add(clazz);
			}
		}
		return objects;
	}

}
