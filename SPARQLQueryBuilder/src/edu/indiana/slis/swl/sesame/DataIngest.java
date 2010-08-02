package edu.indiana.slis.swl.sesame;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class DataIngest {

	public static void main(String[] args) {
		SesameService g = new SesameService(false, "WebRoot/WEB-INF/db"); 
        g.addFile("vivo.rdf.xml", SesameService.RDFXML);
	}
}