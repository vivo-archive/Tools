package org.vivoweb.ingest.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vivoweb.ingest.harvest.PubmedSOAPFetch;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public class XMLRecordOutputStream extends OutputStream {
	private ByteArrayOutputStream buf;
	private RecordHandler rh;
	private byte[] closeTag;
	private Pattern idRegex;
	private String header;
	private String footer;
	
	/**
	 * Constructor
	 * @param tagToSplitOn 
	 * @param headerInfo 
	 * @param footerInfo 
	 * @param idLocationRegex 
	 * @param recordHandler 
	 */
	public XMLRecordOutputStream(String tagToSplitOn, String headerInfo, String footerInfo, String idLocationRegex, RecordHandler recordHandler) {
		this.buf = new ByteArrayOutputStream();
		this.rh = recordHandler;
		this.idRegex = Pattern.compile(idLocationRegex);
		this.closeTag = ("</"+tagToSplitOn+">").getBytes();
		this.header = headerInfo;
		this.footer = footerInfo;
	}

	@Override
	public void write(int arg0) throws IOException {
		this.buf.write(arg0);
		byte[] a = this.buf.toByteArray();
		if(compareByteArrays(a, this.closeTag)) {
			String record = new String(a);
			Matcher m = this.idRegex.matcher(record);
			m.find();
			String id = m.group(1);
			this.rh.addRecord(id.trim(), this.header+record.trim()+this.footer);
			this.buf.reset();
		}
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
	
	/**
	 * @return
	 */
	public RecordHandler getRecordHandler() {
		return this.rh;
	}
	
	/**
	 * @param args
	 */
	public static void main(String... args) {
		XMLRecordOutputStream os = new XMLRecordOutputStream("PubmedArticle", "<?xml version=\"1.0\"?>\n<!DOCTYPE PubmedArticleSet PUBLIC \"-//NLM//DTD PubMedArticle, 1st January 2010//EN\" \"http://www.ncbi.nlm.nih.gov/corehtml/query/DTD/pubmed_100101.dtd\">\n<PubmedArticleSet>\n", "\n</PubmedArticleSet>", ".*?<PMID>(.*?)</PMID>.*?", new MapRecordHandler());
		PubmedSOAPFetch f = new PubmedSOAPFetch("hainesc@ctrip.ufl.edu", "University of Florid", os);
		f.fetchPubMedEnv(f.ESearchEnv(f.queryByAffiliation("ufl.edu"), new Integer(20)));
		for(Record r : os.getRecordHandler()) {
			System.out.println("========================================================");
			System.out.println(r.getID());
			System.out.println("--------------------------------------------------------");
			System.out.println(r.getData());
			System.out.println("========================================================\n");
		}
	}
	
}
