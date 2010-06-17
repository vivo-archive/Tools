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

public enum Task {
	
	Fetch("Initiate Fetch"),
	OpenStream("Open A Stream Connection"),
	Translate("Perform Translation"),
	Score("Score Authors"),
	Qualify("Perform Qualification"),
	Shutdown("Shutdown Process");
	
	private String prettyName;
	
	private Task(String niceName) {
		this.prettyName = niceName;
	}
	
	@Override
	public String toString() {
		return this.prettyName;
	}
}
