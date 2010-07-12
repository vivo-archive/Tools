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
package org.vivoweb.ingest.io;

public enum Feedback {
	
	Processing("Operation In Progress"),
	Success("Operation Completed Successfully"),
	UnknownTask("Failure: Unknown Task Encountered!"),
	BadFetchParameters("Failure: Parameters Missing For Fetch!"),
	Error3("Failure: XXX3 Happened");
	
	private String prettyName;
	
	private Feedback(String niceName) {
		this.prettyName = niceName;
	}
	
	@Override
	public String toString() {
		return this.prettyName;
	}
}
