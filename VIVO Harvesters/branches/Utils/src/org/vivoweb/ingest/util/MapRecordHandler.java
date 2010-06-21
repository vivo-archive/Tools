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
