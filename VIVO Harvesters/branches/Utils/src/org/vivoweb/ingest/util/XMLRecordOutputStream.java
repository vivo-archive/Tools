package org.vivoweb.ingest.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author cah
 *
 */
public class XMLRecordOutputStream extends OutputStream {
	private ByteArrayOutputStream tempBuf;
	private ByteArrayOutputStream recBuf;
	private ByteArrayOutputStream buf;
	private RecordHandler rh;
	private boolean inRecord;
	private byte[] openTag;
	private byte[] closeTag;
	
	/**
	 * Constructor
	 * @param tagToSplitOn 
	 * @param recordHandler 
	 */
	public XMLRecordOutputStream(String tagToSplitOn, RecordHandler recordHandler) {
		this.tempBuf = new ByteArrayOutputStream();
		this.recBuf = new ByteArrayOutputStream();
		this.buf = this.tempBuf;
		this.rh = recordHandler;
		this.inRecord = false;
		this.closeTag = ("</"+tagToSplitOn+">").getBytes();
		this.openTag = ("<"+tagToSplitOn).getBytes();
	}

	@Override
	public void write(int arg0) throws IOException {
		this.buf.write(arg0);
		byte[] a = this.buf.toByteArray();
		if(!inRecord() && compareByteArrays(a,this.openTag)) {
			
			this.buf = this.recBuf;
		}else if(inRecord() && compareByteArrays(a, this.closeTag)) {
			
			this.buf = this.tempBuf;
		}
	}
	
	private boolean inRecord() {
		return (this.buf == this.recBuf);
	}
	
	private boolean compareByteArrays(byte[] a, byte[] b) {
		if(a.length < b.length) {
			return false;
		}
		int o = a.length-b.length;
		for(int i = 0; i < b.length; i++) {
			if(a[o+i] != b[i]) {
				return false;
			}
		}
		return true;
	}
	
}
