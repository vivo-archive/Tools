package org.vivoweb.ingest.transfer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vivoweb.ingest.util.JenaConnect;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public class Transfer {
	
	private static Log log = LogFactory.getLog(Transfer.class);
	
	/**
	 * @param in Model to read from
	 * @param out Model to write to
	 */
	public static void transfer(Model in, Model out) {
		out.add(in);
	}
	
	/**
	 * @param args command line arguments
	 */
	public static void main(String... args) {
		if(args.length != 2) {
			IllegalArgumentException e = new IllegalArgumentException("Transfer requires 2 arguments, both being Jena Model Configuration Files");
			log.error(e.getMessage(),e);
			throw e;
		}
		try {
			JenaConnect in = JenaConnect.parseConfig(args[0]);
			JenaConnect out = JenaConnect.parseConfig(args[0]);
			transfer(in.getJenaModel(),out.getJenaModel());
		} catch(Exception e) {
			// TODO Real Error Handling?
			log.error(e.getMessage(),e);
		}
	}
}
