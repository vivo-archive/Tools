/*******************************************************************************
 * Copyright (c) 2010 Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams
 * 
 * This file is part of VIVO.
 * 
 * VIVO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VIVO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VIVO.  If not, see <http://www.gnu.org/licenses/gpl-3.0.txt>.
 * 
 * Contributors:
 *     Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams - initial implementation
 ******************************************************************************/
package org.vivoweb.ingest.score;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

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
	
		private Model vivo;
		private Model scoreInput;
		private Model scoreOutput;
		
		public Score (Model vivo, Model scoreInput, Model scoreOutput) {
			this.vivo = vivo;
			this.scoreInput = scoreInput;
			this.scoreOutput = scoreOutput;
		}
		
		 public void execute() {		
			 	System.out.println("Scoring: Start");
				//Attempt Matching
				
				//Exact Matches
				matchEmail(vivo,scoreInput,scoreOutput);
				
				//Partial Matches
				
			 	scoreInput.close();
		    	scoreOutput.close();
		    	vivo.close();
		    	System.out.println("Scoring: End");
		}
		
		/*
		 * executeQuery
		 */
		 private static ResultSet executeQuery(Model model, String queryString) {
		    	Query query = QueryFactory.create(queryString);
		    	QueryExecution queryExec = QueryExecutionFactory.create(query, model);
		    	
		    	return queryExec.execSelect();
			}
			/**
			 * @author Dale Scheppler - dscheppler@ichp.ufl.edu
			 * @param strWrite - String to write to logfile, automatically appends date and time.
			 */
			@SuppressWarnings("unused")
			private void writeToLog(String strWrite)
			{
				File fileLogFile = new File("logs/Log.txt");
				FileWriter fwOutFile;
				try
				{
					Calendar gcToday = Calendar.getInstance();
					int intYear = gcToday.get(Calendar.YEAR);
					int intMonth = gcToday.get(Calendar.MONTH) + 1;
					int intDay = gcToday.get(Calendar.DATE);
					int intHour = gcToday.get(Calendar.HOUR_OF_DAY);
					int intMinute = gcToday.get(Calendar.MINUTE);
					int intSecond = gcToday.get(Calendar.SECOND);
					int intMillisecond = gcToday.get(Calendar.MILLISECOND);
					fwOutFile = new FileWriter(fileLogFile, true);
					PrintWriter out = new PrintWriter(fwOutFile);
					out.println("Score:" + intYear + "/" + intMonth + "/" + intDay + "/" + intHour + ":" + intMinute + ":" + intSecond + "." + intMillisecond + "-" + strWrite);
					out.close();
					
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
			}
		 
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
				Property authorOf = ResourceFactory.createProperty("http://vivoweb.org/ontology/core#authorInAuthorship");
                Property linkedAuthorOf = ResourceFactory.createProperty("http://vivoweb.org/ontology/core#linkedAuthor");
                Property paperOf = ResourceFactory.createProperty("http://vivoweb.org/ontology/core#linkedInformationResource");
                Resource flag1 = vivo.getResource("http://vitro.mannlib.cornell.edu/ns/vitro/0.7#Flag1Value1Thing");
                Property rdfType = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
                Property rdfLabel = ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
				
                Statement stmt;
                
                //Issue select for email addresses from score_input store
		    	System.out.println("Grabbing all email addresses from input");
		    	
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
	                
	                System.out.println("\nChecking for " + scoreEmail + " from " + paperNode.toString() + " in VIVO");
	    			
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
	                    System.out.println("Found " + scoreEmail + " for person " + uriNode.toString() + " in VIVO");
	                    System.out.println("Adding paper " + paperNode.toString() + " to VIVO");

	                    
	                    //loop through and add statements
	                    paperStmts = paperResource.listProperties();
	                    
	                    //insert paper statements into vivo
	                    while (paperStmts.hasNext()) {
	                    	stmt = paperStmts.nextStatement();
	                    	System.out.println("Paper Statement " + stmt.toString());

	                    	//write to vivo, minus scoring info
	                    	if (!stmt.getPredicate().toString().contains("/score")) {
	                    		vivo.add(stmt);
	                    	}
	                    	
	    	    			//write out to score output
	                    	//TODO add score of 100
	                    	scoreOutput.add(stmt);
	                    }
	                    
	                    //link author to paper
	                    System.out.println("Link paper " + paperNode.toString() + " to person " + uriNode.toString() + " in VIVO");
	                    //authorship = ResourceFactory.createResource("http://vivoweb.org/ontology/pubMed/#");
	                    authorship = ResourceFactory.createResource(paperNode.toString() + "/vivoAuthorship/1");
	                    //System.out.println(authorship.getNameSpace());
	                    
	                    vivo.add(authorship,linkedAuthorOf,uriNode);
	                    vivo.add(authorNode,linkedAuthorOf,authorship);
	                    vivo.add(authorship,paperOf,paperNode);
	                    vivo.add(authorship,rdfType,flag1);
	                    vivo.add(authorship,rdfLabel,"Authorship for Paper");
	                    
	                    
	                    
		    			//take results and store in VIVO
		    			vivo.commit();
		    			scoreOutput.commit();
	                }
	            }	    			 
		 }
	}
