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
package org.vivoweb.ingest.score;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;

/***
 *  VIVO Score
 *  @author Nicholas Skaggs nskaggs@ichp.ufl.edu
 */
public class Score {
	
	private static Log log = LogFactory.getLog(Score.class);
	
		private Model vivo;
		private Model scoreInput;
		private Model scoreOutput;
		
		public Score (Model vivo, Model scoreInput, Model scoreOutput) {
			this.vivo = vivo;
			this.scoreInput = scoreInput;
			this.scoreOutput = scoreOutput;
		}
		
		public void execute() {		
			 	log.info("Scoring: Start");
				//Attempt Matching
				
				//Exact Matches
				matchEmail(vivo,scoreInput,scoreOutput);
				
				//Partial Matches
				
				//Close and done
				//TODO change 
				scoreInput.close();
		    	scoreOutput.close();
		    	vivo.close();
		    	log.info("Scoring: End");
		}
		
		/**
		 * executeQuery
		 * Executes a sparql query against a JENA model and returns a result set
		 * 
		 * @param  model a model containing statements
		 * @param  queryString the query to execute against the model
		 * @return queryExec the executed query result set
		 */
		 private static ResultSet executeQuery(Model model, String queryString) {
		    	Query query = QueryFactory.create(queryString);
		    	QueryExecution queryExec = QueryExecutionFactory.create(query, model);
		    	
		    	return queryExec.execSelect();
			}
		 
		 /**
		 * matchEmail
		 * Executes an email matching algorithm for author disambiguation
		 * Returns nothing
		 * 
		 * 
		 * @param  vivo a model containing statements describing authors
		 * @param  scoreInput a model containing statements to be disambiguated
		 * @param  scoreOutput a model used to output the disambiguated statements(can be empty)
		 */
		 
		 //TODO refactor to returning a Model of matched statements, and don't require a scoreOutput model
		 private static void matchEmail(Model vivo, Model scoreInput, Model scoreOutput) {
				String scoreEmail;
				String queryString;
				RDFNode emailNode;
				RDFNode paperNode;
				RDFNode authorNode;
	            ResultSet scoreInputResult;
				ResultSet vivoResult;
				QuerySolution scoreSolution;
				QuerySolution vivoSolution;
				StmtIterator paperStmts;
				Resource authorship;
				Resource paperResource;
                Property linkedAuthorOf = ResourceFactory.createProperty("http://vivoweb.org/ontology/core#linkedAuthor");
                Property authorshipForPerson = ResourceFactory.createProperty("http://vivoweb.org/ontology/core#authorInAuthorship");
                
                Property authorshipForPaper = ResourceFactory.createProperty("http://vivoweb.org/ontology/core#informationResourceInAuthorship");
                Property paperOf = ResourceFactory.createProperty("http://vivoweb.org/ontology/core#linkedInformationResource");
                
                
                Resource flag1 = vivo.getResource("http://vitro.mannlib.cornell.edu/ns/vitro/0.7#Flag1Value1Thing");
                Property rdfType = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
                Property rdfLabel = ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
				
                Statement stmt;
                
                //Issue select for email addresses from score_input store
		    	log.trace("Grabbing all email addresses from input");
		    	
		    	queryString =
		    		"PREFIX score: <http://vivoweb.org/ontology/score#> " +
		    		"SELECT ?x ?email " +
		    		"WHERE { ?x score:workEmail ?email}";

		    	scoreInputResult = executeQuery(scoreInput, queryString);
		    	
		    	//look for email address in vivo
		    	while (scoreInputResult.hasNext()) {
		    		scoreSolution = scoreInputResult.nextSolution();
	                emailNode = scoreSolution.get("email");
	                paperNode = scoreSolution.get("x");
	                paperResource = scoreSolution.getResource("x");
	                
	                scoreEmail = emailNode.toString();
	                
	                log.trace("\nChecking for " + scoreEmail + " from " + paperNode.toString() + " in VIVO");
	    			
	                //Select all matching email addresses from vivo store
	    			queryString =
						"PREFIX core: <http://vivoweb.org/ontology/core#> " +
						"SELECT ?x " +
						"WHERE { ?x core:workEmail \"" +  scoreEmail + "\" }";
	    			
	    			vivoResult = executeQuery(vivo, queryString);

	    	    	//look for email address in VIVO
	    	    	while (vivoResult.hasNext()) {
	    	    		vivoSolution = vivoResult.nextSolution();
	    	    		
	    	    		//Grab person URI  		    	
	                    authorNode = vivoSolution.get("x");
	                    log.info("Found " + scoreEmail + " for person " + authorNode.toString() + " in VIVO");
	                    log.info("Adding paper " + paperNode.toString() + " to VIVO");

	                    
	                    //loop through and add statements
	                    paperStmts = paperResource.listProperties();
	                    
	                    //insert paper statements into vivo
	                    //TODO refactor this to a recursive method
	                    while (paperStmts.hasNext()) {
	                    	stmt = paperStmts.nextStatement();
	                    	log.trace("Paper Statement " + stmt.toString());

	                    	//write to vivo, minus scoring info
	                    	if (!stmt.getPredicate().toString().contains("/score")) {
	                    		vivo.add(stmt);
	                    		                    	
		                    	if (stmt.getObject().isResource()) {
		                    		StmtIterator objectIterator = ((Resource)stmt.getObject()).listProperties();
		                    		
		                    		while(objectIterator.hasNext()){
		                    			stmt = objectIterator.nextStatement();
		                    			log.trace("Object Statement " + stmt.toString());
		               
		                    			if(!stmt.getPredicate().toString().contains("/score")) {
		                    				vivo.add(stmt);
		                    			}
		                    		}
		                    	}
	                    	}
	                    	
	    	    			//write out to score output
	                    	//TODO add score of 100
	                    	scoreOutput.add(stmt);
	                    }
	                    
	                    //link author to paper
	                    log.trace("Link paper " + paperNode.toString() + " to person " + authorNode.toString() + " in VIVO");
	                    //authorship = ResourceFactory.createResource("http://vivoweb.org/ontology/pubMed/#");
	                    authorship = ResourceFactory.createResource(paperNode.toString() + "/vivoAuthorship/1");
	                    //System.out.println(authorship.getNameSpace());
	                    
	                    vivo.add(authorship,linkedAuthorOf,authorNode);
	                    log.trace("Link Statement [" + authorship.toString() + ", " + linkedAuthorOf.toString() + ", " + authorNode.toString() + "]");
	                    vivo.add((Resource)authorNode,authorshipForPerson,authorship);
	                    log.trace("Link Statement [" + authorNode.toString() + ", " + authorshipForPerson.toString() + ", " + authorship.toString() + "]");
	                    vivo.add(authorship,paperOf,paperNode);
	                    log.trace("Link Statement [" + authorship.toString() + ", " + paperOf.toString() + ", " + paperNode.toString() + "]");
	                    vivo.add((Resource)paperNode,authorshipForPaper,authorship);
	                    log.trace("Link Statement [" + paperNode.toString() + ", " + authorshipForPaper.toString() + ", " + authorship.toString() + "]");
	                    vivo.add(authorship,rdfType,flag1);
	                    log.trace("Link Statement [" + authorship.toString() + ", " + rdfType.toString() + ", " + flag1.toString() + "]");
	                    vivo.add(authorship,rdfLabel,"Authorship for Paper");
	                    log.trace("Link Statement [" + authorship.toString() + ", " + rdfLabel.toString() + ", " + "Authorship for Paper]");

		    			//take results and store in VIVO
		    			vivo.commit();
		    			scoreOutput.commit();
	                }
	            }	    			 
		 }
	}
