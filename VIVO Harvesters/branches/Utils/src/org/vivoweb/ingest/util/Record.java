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
package org.vivoweb.ingest.util;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public class Record {
	/**
	 * 
	 */
	private String id;
	/**
	 * 
	 */
	private String data;
	
	/**
	 * @param recID
	 * @param recData
	 */
	public Record(String recID, String recData) {
		setID(recID);
		setData(recData);
	}
	
	/**
	 * @param id
	 */
	public void setID(String id) {
		this.id = id;
	}
	
	/**
	 * @return
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * @param data
	 */
	public void setData(String data) {
		this.data = data;
	}
	
	/**
	 * @return
	 */
	public String getData() {
		return data;
	}
	
}
