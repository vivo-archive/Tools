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
import java.util.Iterator;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public class FTPTextFileRecordHandler extends RecordHandler {
	
	/**
	 * 
	 */
	public FTPTextFileRecordHandler() {
		
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#addRecord(org.vivoweb.ingest.util.Record)
	 */
	@Override
	public void addRecord(Record rec) throws IOException {
		// TODO Auto-generated method stub
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#delRecord(java.lang.String)
	 */
	@Override
	public void delRecord(String recID) throws IOException {
		// TODO Auto-generated method stub
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#getRecord(java.lang.String)
	 */
	@Override
	public Record getRecord(String recID) throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#getRecordData(java.lang.String)
	 */
	@Override
	public String getRecordData(String recID) throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Record> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
