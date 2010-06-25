/*******************************************************************************
 * Copyright (c) 2010 Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the new BSD license
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/bsd-license.html
 * 
 * Contributors:
 *     Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams - initial API and implementation
 *     Christoper Barnes, Narayan Raum - scoring ideas and algorithim
 *     Yang Li - pairwise scoring algorithm
 ******************************************************************************/
package org.vivoweb.ingest.score;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vivoweb.ingest.util.JenaConnect;
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
		private Model tempModel;
		
		final static public void main(String[] args) {
			//pass models from command line
			//TODO proper args handler
			
			//args[0];
			//args[1];
			//debug
			//Hardcode parms for now
			
			//debug
			
			//load up models
			
//			JenaConnect vivo = new JenaConnect();
//			JenaConnect scoreInput = new JenaConnect();
//			JenaConnect scoreOutput = new JenaConnect();
			
//			new Score(vivo.getJenaModel(),scoreInput.getJenaModel(), scoreOutput.getJenaModel()).execute();
	    }
		
		public Score (Model vivo, Model scoreInput, Model scoreOutput) {
			this.vivo = vivo;
			this.scoreInput = scoreInput;
			this.scoreOutput = scoreOutput;
		}
		
		public void execute() {		
			 	log.info("Scoring: Start");
			 	
			 	ResultSet scoreInputResult;
			 	ResultSet matchResult;
			 	
			 	//DEBUG
				 	//TODO howto pass this in via config
				 	String matchAttribute = "email";
				 	String matchQuery = "PREFIX score: <http://vivoweb.org/ontology/score#> " +
			    						"SELECT ?x ?email " +
			    						"WHERE { ?x score:workEmail ?email}";
				 	String coreAttribute = "core:workEmail";
				 	String authorAttribute = "author";
			 	//DEBUG
			 	
				//Attempt Matching

			 	//Exact Matches
			 	//TODO finish implementation of exact matching loop
			 	//for each matchAttribute
			 		scoreInputResult = executeQuery(scoreInput, matchQuery);
			 		exactMatch(vivo,scoreInput,matchAttribute,coreAttribute,scoreInputResult);
			    //end for
			 		
		 		//DEBUG
				 	//TODO howto pass this in via config
				 	matchAttribute = "author";
				 	matchQuery = "PREFIX score: <http://vivoweb.org/ontology/score#> " +
			    	       		 "SELECT ?x ?author " +
			    				 "WHERE { ?x score:author ?author}";
				 	coreAttribute = "core:author";
			 	//DEBUG
				
				//Pairwise Matches
				//TODO finish implementation of pairwise matching loop
			 	//for each matchAttribute
			 		scoreInputResult = executeQuery(scoreInput, matchQuery);
			 		pairwiseScore(vivo,scoreInput,matchAttribute,coreAttribute,scoreInputResult);	
			 	//end for
			 		
				//Close and done
				scoreInput.close();
		    	scoreOutput.close();
		    	vivo.close();
		    	log.info("Scoring: End");
		}
		
		/**
		 * executeQuery
		 * Executes a sparql query against a JENA model and returns a result set
		 * 
		 * @param  Model model a model containing statements
		 * @param  String queryString the query to execute against the model
		 * @return queryExec the executed query result set
		 */
		 private static ResultSet executeQuery(Model model, String queryString) {
		    	Query query = QueryFactory.create(queryString);
		    	QueryExecution queryExec = QueryExecutionFactory.create(query, model);
		    	
		    	return queryExec.execSelect();
			}
		 
		 
		/**
		 * commitResultSet
		 * Commits resultset to a vivo model and a score model
		 * 
		 * @param  Model vivo a model containing vivo statements
		 * @param  Model score a model containing vivo + scoring statements
		 * @param  ResultSet storeResult the result to be stored
		 * @param  Resource paperResource
		 * @param  RDFNode matchNode
		 * @param  RDFNode paperNode
		 */
		 private static void commitResultSet(Model vivo, Model score, ResultSet storeResult, Resource paperResource, RDFNode matchNode, RDFNode paperNode) {
				RDFNode authorNode;
				QuerySolution vivoSolution;
				StmtIterator paperStmts;
				Resource authorship;

                Property linkedAuthorOf = ResourceFactory.createProperty("http://vivoweb.org/ontology/core#linkedAuthor");
                Property authorshipForPerson = ResourceFactory.createProperty("http://vivoweb.org/ontology/core#authorInAuthorship");
                
                Property authorshipForPaper = ResourceFactory.createProperty("http://vivoweb.org/ontology/core#informationResourceInAuthorship");
                Property paperOf = ResourceFactory.createProperty("http://vivoweb.org/ontology/core#linkedInformationResource");
                
                
                Resource flag1 = vivo.getResource("http://vitro.mannlib.cornell.edu/ns/vitro/0.7#Flag1Value1Thing");
                Property rdfType = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
                Property rdfLabel = ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
				
                Statement stmt;
			 
				//loop thru vivo matches
	 	    	while (storeResult.hasNext()) {
	 	    		vivoSolution = storeResult.nextSolution();
	 	    		
	 	    		//Grab person URI
	                 authorNode = vivoSolution.get("x");
	                 log.info("Found " + matchNode.toString() + " for person " + authorNode.toString() + " in VIVO");
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
	                 	score.add(stmt);
	                 }
	                 
	               //link author to paper
                    log.trace("Link paper " + paperNode.toString() + " to person " + authorNode.toString() + " in VIVO");
                    authorship = ResourceFactory.createResource(paperNode.toString() + "/vivoAuthorship/1");
                    
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
	             }	 
		 }
		 
		 
		/**
		* pairwiseScore
		* Executes a pair scoring method, utilizing the matchAttribute. This attribute is expected to 
		* return 2 to n results from the given query. This "pair" will then be utilized as a matching scheme 
		* to construct a sub dataset. This dataset can be scored and stored as a match 
		* 
		* 
		* @param  Model matched a model containing statements describing known authors
		* @param  Model scoreInput a model containing statements to be disambiguated
		* @param  String matchAttribute an attribute to perform the exact match
	    * @param  String coreAttribute an attribute to perform the exact match against from core ontology
		* @param  ResultSet matchResult contains a resultset of the matchAttribute
		* @return Model scoreInput
		*/
		
		private static Model pairwiseScore(Model matched, Model scoreInput, String matchAttribute, String coreAttribute, ResultSet matchResult) {			
		 	//iterate thru scoringInput pairs against matched pairs
		 	//TODO support partial scoring, multiples matches against several pairs
		 	//if pairs match, store publication to matched author in Model
			//TODO return scoreInput minus the scored statements
			
			String scoreMatch;
			String queryString;
			Resource paperResource;
			RDFNode matchNode;
			RDFNode paperNode;
			ResultSet vivoResult;
			QuerySolution scoreSolution;

		 	//create pairs of *attribute* from matched
	    	log.trace("Creating pairs of " + matchAttribute + " from input");
	    	
	    	//look for exact match in vivo
	    	while (matchResult.hasNext()) {
	    		scoreSolution = matchResult.nextSolution();
                matchNode = scoreSolution.get(matchAttribute);
                
                scoreMatch = matchNode.toString();
                
                log.trace("\nChecking for " + scoreMatch + " in VIVO");
    			
                //Select all matching attributes from vivo store
    			queryString =
					"PREFIX core: <http://vivoweb.org/ontology/core#> " +
					"SELECT ?x " +
					"WHERE { ?x " + coreAttribute + "\"" +  scoreMatch + "\"}";
    			
    			//TODO how to combine result sets? not possible in JENA
//    			vivoResult = executeQuery(vivo, queryString);
//    			commitResultSet(vivo,scoreInput,vivoResult,paperResource,paperNode,matchNode);
            }	    			 
	    	
	    	//TODO return scoreInput minus the scored statements			
			return scoreInput;
		 }
		 
		 
		 /**
		 * exactMatch
		 * Executes an exact matching algorithm for author disambiguation
		 * 
		 * 
		 * @param  Model vivo a model containing statements describing authors
		 * @param  Model scoreInput a model containing statements to be disambiguated
		 * @param  String matchAttribute an attribute to perform the exact match
		 * @param  String coreAttribute an attribute to perform the exact match against from core ontology
		 * @param  ResultSet matchResult contains a resultset of the matchAttribute
		 * @return scoreInput
		 */
		 private static Model exactMatch(Model vivo, Model scoreInput, String matchAttribute, String coreAttribute, ResultSet matchResult) {
				String scoreMatch;
				String queryString;
				Resource paperResource;
				RDFNode matchNode;
				RDFNode paperNode;
				ResultSet vivoResult;
				QuerySolution scoreSolution;

                
		    	log.trace("Looping thru " + matchAttribute + " from input");
		    	
		    	//look for exact match in vivo
		    	while (matchResult.hasNext()) {
		    		scoreSolution = matchResult.nextSolution();
	                matchNode = scoreSolution.get(matchAttribute);
	                //TODO paperNode must currently be 'x'; howto abstract?
	                paperNode = scoreSolution.get("x");
	                //TODO paperResource must currently be 'x'; howto abstract?
	                paperResource = scoreSolution.getResource("x");
	                
	                scoreMatch = matchNode.toString();
	                
	                log.trace("\nChecking for " + scoreMatch + " from " + paperNode.toString() + " in VIVO");
	    			
	                //Select all matching attributes from vivo store
	    			queryString =
						"PREFIX core: <http://vivoweb.org/ontology/core#> " +
						"SELECT ?x " +
						"WHERE { ?x " + coreAttribute + "\"" +  scoreMatch + "\"}";
	    			
	    			//TODO how to combine result sets? not possible in JENA
	    			vivoResult = executeQuery(vivo, queryString);
	    			commitResultSet(vivo,scoreInput,vivoResult,paperResource,paperNode,matchNode);
	            }	    			 
		    	
		    	//TODO return scoreInput minus the scored statements
		    	return scoreInput;
		 }
	}
