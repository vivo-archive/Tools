package edu.cornell.mannlib.vitro.webapp.servlet.setup;

/*
Copyright (c) 2010, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.webapp.dao.jena.JenaBaseDao;

public class AttachSubmodels implements ServletContextListener {

	private static String PATH = "/WEB-INF/submodels";
	
	private static final Log log = LogFactory.getLog( AttachSubmodels.class );
	
	public void contextInitialized( ServletContextEvent sce ) {
		try {
			int attachmentCount = 0;
			OntModel baseModel = (OntModel) sce.getServletContext().getAttribute( JenaBaseDao.ASSERTIONS_ONT_MODEL_ATTRIBUTE_NAME );
			Set<String> pathSet = sce.getServletContext().getResourcePaths( PATH );
			if (pathSet == null) { 
				return;
			}
			for ( String p : pathSet ) {
				File file = new File( sce.getServletContext().getRealPath( p ) );
				try {
					FileInputStream fis = new FileInputStream( file );
					try {
						Model m = ModelFactory.createDefaultModel(); 
						if ( p.endsWith(".n3") || p.endsWith(".N3") || p.endsWith(".ttl") || p.endsWith(".TTL") ) {
							m.read( fis, null, "N3" );
						} else if ( p.endsWith(".owl") || p.endsWith(".OWL") || p.endsWith(".rdf") || p.endsWith(".RDF") || p.endsWith(".xml") || p.endsWith(".XML") ) {
							m.read( fis, null, "RDF/XML" );
						} else {
							log.warn("Ignoring submodel file " + p + " because the file extension is unrecognized.");
						}
						if ( !m.isEmpty() ) {
							baseModel.addSubModel( m );
						}
						attachmentCount++;
						log.info("Attached submodel from file " + p);
					} catch (Exception ioe) {
						fis.close();
						log.error("Unable to attach submodel from file " + p, ioe);
						System.out.println("Unable to attach submodel from file " + p);
						ioe.printStackTrace();
					}
				} catch (FileNotFoundException fnfe) {
					log.warn(p + " not found. Unable to attach as submodel" + 
							((fnfe.getLocalizedMessage() != null) ? 
							fnfe.getLocalizedMessage() : "") );
				}
			}
			System.out.println("Attached " + attachmentCount + " file" + ((attachmentCount == 1) ? "" : "s") + " as submodels");
		} catch (ClassCastException cce) {
			String errMsg = "Unable to cast servlet context attribute " + JenaBaseDao.ASSERTIONS_ONT_MODEL_ATTRIBUTE_NAME + " to " + OntModel.class.getName();
			// Logging this ourselves because Tomcat's tends not to log exceptions thrown in context listeners.
			log.error( errMsg );
			throw new ClassCastException( errMsg );
		} catch (Throwable t) {
			System.out.println("Throwable in listener " + this.getClass().getName());
			log.error(t);
			t.printStackTrace();
		}
		
	}
	
	public void contextDestroyed( ServletContextEvent sce ) {
		// nothing to worry about
	}
	
}
