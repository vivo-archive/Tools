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
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public class MapRecordHandler extends RecordHandler {
	
	HashMap<String,String> map;
	
	/**
	 * Constructor
	 */
	public MapRecordHandler() {
		this.map = new HashMap<String,String>();
	}
	
	@Override
	public void addRecord(Record rec) throws IOException {
		this.map.put(rec.getID(), rec.getData());
	}
	
	@Override
	public void delRecord(String recID) throws IOException {
		this.map.remove(recID);
	}
	
	@Override
	public String getRecordData(String recID) throws IllegalArgumentException, IOException {
		return this.map.get(recID);
	}
	
	@Override
	public Iterator<Record> iterator() {
		return new MapRecordIterator();
	}
	
	private class MapRecordIterator implements Iterator<Record> {
		private Iterator<String> keyIter;
		
		protected MapRecordIterator() {
			this.keyIter = MapRecordHandler.this.map.keySet().iterator();
		}
		
		public boolean hasNext() {
			return this.keyIter.hasNext();
		}
		
		public Record next() {
			String key = this.keyIter.next();
			String data = MapRecordHandler.this.map.get(key);
			return new Record(key,data);
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
}
