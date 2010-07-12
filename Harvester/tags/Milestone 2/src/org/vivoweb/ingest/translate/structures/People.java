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
package org.vivoweb.ingest.translate.structures;


/**
 * People
 * 
 * Generic Class for Translation of People into the VIVO format
 * 
 * @author swilliams
 *
 */
public class People {

	//Name Parts
	private String name;
	private String firstName;
	private String lastName;
	private String middleName;
	
	
	public People() {
		
	}
	
	public void setName(String n) {
		this.name = n;
	}
	
	public void setFirstname(String fn) {
		this.firstName = fn;
	}
	
	public void setLastName(String ln) {
		this.lastName = ln;
	}
	
	public void setMiddleName(String mn) {
		this.middleName = mn;
	}
	
	public String[] getVIVORDF(String node, String type) {
		
		StringBuilder sb = new StringBuilder();
		
		String[] returnArray = {"NodeName", sb.toString()};
		
		
		return(returnArray); //TODO fix this it should return the name of the value created.
	}
	
	
	
	
	
	
	
}
