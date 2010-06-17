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

import java.io.IOException;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public abstract class RecordHandler implements Iterable<Record> {
	
	/**
	 * @param rec
	 * @throws IOException 
	 */
	public abstract void addRecord(Record rec) throws IOException;
	
	/**
	 * @param recID
	 * @param recData
	 * @throws IOException 
	 */
	public void addRecord(String recID, String recData) throws IOException {
		addRecord(new Record(recID, recData));
	}
	
	/**
	 * @param recID
	 * @return
	 * @throws IllegalArgumentException 
	 * @throws IOException 
	 */
	public Record getRecord(String recID) throws IllegalArgumentException, IOException {
		return new Record(recID, getRecordData(recID));
	}
	
	/**
	 * @param recID
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public abstract String getRecordData(String recID) throws IllegalArgumentException, IOException;
	
	/**
	 * @param recID
	 */
	public abstract void delRecord(String recID) throws IOException;

}
