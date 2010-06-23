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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public class SFTPTextFileRecordHandler extends RecordHandler {

	protected static Log log = LogFactory.getLog(FTPTextFileRecordHandler.class);
	protected FTPClient ftp;
	
	/**
	 * @param hostAddr 
	 * @param hostPort 
	 * @param connUN 
	 * @param connPW 
	 * @param fileDir 
	 * @throws IOException 
	 * @throws SocketException 
	 * @throws NumberFormatException 
	 * 
	 */
	public SFTPTextFileRecordHandler(String hostAddr, String hostPort, String connUN, String connPW, String fileDir) throws NumberFormatException, SocketException, IOException {
		this.ftp = new FTPClient();
		this.ftp.connect(hostAddr, Integer.valueOf(hostPort).intValue());
		this.ftp.login(connUN, connPW);
		this.ftp.changeWorkingDirectory(fileDir);
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#addRecord(org.vivoweb.ingest.util.Record)
	 */
	@Override
	public void addRecord(Record rec) throws IOException {
		OutputStream os = this.ftp.storeFileStream(rec.getID());
		if(os == null) {
			log.error("Unable to store file on remote server");
			throw new IOException("Unable to store file on remote server");
		}
		os.write(rec.getData().getBytes());
		os.flush();
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#delRecord(java.lang.String)
	 */
	@Override
	public void delRecord(String recID) throws IOException {
		if(!this.ftp.deleteFile(recID)) {
			log.error("Unable to delete file from remote server");
			throw new IOException("Unable to delete file from remote server");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#getRecordData(java.lang.String)
	 */
	@Override
	public String getRecordData(String recID) throws IllegalArgumentException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if(!this.ftp.retrieveFile(recID, baos)) {
			log.error("Unable to retrieve file from remote server - does file exist?");
			throw new IllegalArgumentException("Unable to retrieve file from remote server - does file exist?");
		}
		baos.flush();
		return baos.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Record> iterator() {
		return new FTPTextFileRecordIterator();
	}
	
	private class FTPTextFileRecordIterator implements Iterator<Record> {
		Iterator<FTPFile> fileIter;
		
		protected FTPTextFileRecordIterator() {
			LinkedList<FTPFile> fileListing = new LinkedList<FTPFile>();
			try {
				for(FTPFile filename : SFTPTextFileRecordHandler.this.ftp.listFiles()) {
					if(filename.isFile()) {
						fileListing.add(filename);
//						System.out.println("file: "+filename+"\n");
					}
				}
			} catch(IOException e) {
				log.error("Unable to get File Listing",e);
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
	
}
