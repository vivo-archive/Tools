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
package org.vivoweb.ingest.qualify;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public class SPARQLQualify extends Qualify {
	private static Log log = LogFactory.getLog(SPARQLQualify.class);
	
	/**
	 * Constructor
	 * @param model the JENA model to run qualifications on
	 */
	public SPARQLQualify(Model model) {
		setModel(model);
	}
	
	@Override
	public void replace(String dataType, String matchValue, String newValue, boolean regex) {
		if(regex) {
			regexReplace(dataType, matchValue, newValue);
		} else {
			strReplace("?uri", dataType, matchValue, newValue);
		}
	}
	
	private void strReplace(String uri, String dataType, String oldValue, String newValue) {
		// create query string
		String sQuery = ""
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "DELETE { "+uri+" "+dataType+" ?value } "
				+ "INSERT { "+uri+" "+dataType+" \""+newValue+"\" } "
				+ "WHERE { "+uri+" "+dataType+" \""+oldValue+"\" }";
		
		// run update
		UpdateRequest ur = UpdateFactory.create(sQuery);
		UpdateAction.execute(ur, getModel());
	}

	private void regexReplace(String dataType, String regexMatch, String newValue) {
	// create query string
		String sQuery = ""
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "Select ?record ?dataField "
				+ "WHERE { "
				+ "  ?record "+dataType+" ?dataField . "
				+ "}";
		
		// create query
		Query query = QueryFactory.create(sQuery);
		
		// execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, getModel());
		ResultSet resultSet = qe.execSelect();
		
		// read first result
		String data = null;
		if(resultSet.hasNext()) {
			QuerySolution result = resultSet.next();
			data = result.getLiteral(resultSet.getResultVars().get(1)).getString();
			if(data.matches(regexMatch)) {
				String newData = data.replaceAll(regexMatch, newValue);
				if(!newData.equals(data)) {
					String record = result.getLiteral(resultSet.getResultVars().get(0)).getString();
					strReplace(record, dataType, data, newData);
				}
			}
		}
	}
	
	/**
	 * @param args commandline arguments
	 */
	public static void main(String... args) {
		
	}

}
