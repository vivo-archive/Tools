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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public class TextFileRecordHandler extends RecordHandler {

	private static Log log = LogFactory.getLog(TextFileRecordHandler.class);
	private String directory;
	
	/**
	 * Constructor
	 * @param fileDir 
	 * @throws IOException 
	 * 
	 */
	public TextFileRecordHandler(String fileDir) throws IOException {
		this.directory = fileDir;
		File dir = new File(getDirectory());
		if(!dir.exists()) {
			log.info("Directory '"+fileDir+"' Does Not Exist, attempting to create");
			if(dir.mkdir()) {
				log.info("Directory was successfully created");
			} else {
				throw new IOException("Directory '"+fileDir+"' Does Not Exist and failed to be created");
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#addRecord(org.vivoweb.ingest.util.Record)
	 */
	@Override
	public void addRecord(Record rec) throws IOException {
		File f = new File(getPath(rec.getID()));
		if(f.exists()) {
			throw new IOException("Failed to add record "+rec.getID()+" because file"+getPath(rec.getID())+"already exists.");
		}
		if(!f.createNewFile() || !f.canWrite()) {
			throw new IOException("Insufficient file system privileges to add record "+rec.getID()+" to file"+getPath(rec.getID()));
		}
		FileWriter fw = new FileWriter(f);
		fw.append(rec.getData());
		fw.close();
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#delRecord(java.lang.String)
	 */
	@Override
	public void delRecord(String recID) throws IOException {
		File f = new File(getPath(recID));
		if(!f.exists()) {
			log.warn("Attempted to delete record "+recID+", but file"+getPath(recID)+"did not exist.");
		} else if(!f.canWrite()) {
			throw new IOException("Insufficient file system privileges to delete record "+recID+" from file"+getPath(recID));
		} else if(!f.delete()) {
			throw new IOException("Failed to delete record "+recID+" from file"+getPath(recID));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#getRecordData(java.lang.String)
	 */
	@Override
	public String getRecordData(String recID) throws IllegalArgumentException, IOException {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(getPath(recID)));
			String line;
			while((line = br.readLine()) != null){
				sb.append(line);
				sb.append("\n");
			}
			br.close();
		} catch(FileNotFoundException e) {
			log.error("Record File Not Found For "+recID);
			throw new IllegalArgumentException("Record File Not Found For "+recID);
		} catch(IOException e) {
			log.error("Error Reading Contents of file '"+getPath(recID)+"'");
			throw e;
		}
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	@SuppressWarnings("synthetic-access")
	public Iterator<Record> iterator() {
		return new TextFileRecordIterator();
	}
	
	private class TextFileRecordIterator implements Iterator<Record> {
		Iterator<File> fileIter;
		
		private TextFileRecordIterator() {
			LinkedList<File> fileListing = new LinkedList<File>();
			for(File filename : new File(getDirectory()).listFiles()) {
				if(!filename.isHidden()) {
					fileListing.add(filename);
					System.out.println("file: "+filename+"\n");
				}
			}
			this.fileIter = fileListing.iterator();
		}
		
		public boolean hasNext() {
			return this.fileIter.hasNext();
		}
		
		public Record next() {
			try {
				Record result = getRecord(this.fileIter.next().getName());
				return result;
			} catch(IOException e) {
				throw new NoSuchElementException(e.getMessage());
			}
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * @return
	 */
	protected String getDirectory() {
		return this.directory;
	}
	
	private String getPath(String recID) {
		return getDirectory()+"/"+recID;
	}
	
}
