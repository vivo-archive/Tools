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

package edu.cornell.mannlib.vitro.webapp.servlet.setup;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ClosableIterator;

/**
 * WARNING: This configuration may not reconnect to MySQL after connection timeouts.
 * @author bjl23
 *
 */
public class JenaPersistentDBOnlyDataSourceSetup extends JenaDataSourceSetupBase implements ServletContextListener {
	
	private static final Log log = LogFactory.getLog(JenaPersistentDataSourceSetup.class.getName());
	
	public void contextInitialized(ServletContextEvent sce) {
		OntModel memModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC);
		OntModel dbModel = null;
        try {
            Model dbPlainModel = makeDBModelFromConfigurationProperties(JENA_DB_MODEL, DB_ONT_MODEL_SPEC);
            dbModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC,dbPlainModel);
            boolean isEmpty = true;
            ClosableIterator stmtIt = dbModel.listStatements();
            try {
                if (stmtIt.hasNext()) {
                    isEmpty = false;
                }
            } finally {
                stmtIt.close();
            }

            if (isEmpty) {
                long startTime = System.currentTimeMillis();
                System.out.println("Reading ontology files into database");
                ServletContext ctx = sce.getServletContext();
                readOntologyFilesInPathSet(USERPATH, ctx, dbModel);
                readOntologyFilesInPathSet(AUTHPATH, ctx, dbModel);
                readOntologyFilesInPathSet(SYSTEMPATH, ctx, dbModel);
                System.out.println((System.currentTimeMillis()-startTime)/1000+" seconds to populate DB");
            }

            //readOntologyFilesInPathSet(sce.getServletContext().getResourcePaths(AUTHPATH), sce, dbModel);
            //readOntologyFilesInPathSet(sce.getServletContext().getResourcePaths(SYSTEMPATH), sce, dbModel);

            memModel = dbModel;
            
        } catch (Throwable t) {
			System.out.println("**** ERROR *****");
            System.out.println("Vitro unable to open Jena database model.");
			System.out.println("Check that the configuration properties file has been created in WEB-INF/classes, ");
			System.out.println("and that the database connection parameters are accurate. ");
			System.out.println("****************");
        }

		try {
	        if (dbModel==null) {
	            System.out.println("Reading ontology files");
	            ServletContext ctx = sce.getServletContext();
	            readOntologyFilesInPathSet(USERPATH, ctx, dbModel);
	            readOntologyFilesInPathSet(AUTHPATH, ctx, dbModel);
	            readOntologyFilesInPathSet(SYSTEMPATH, ctx, dbModel);
	        }
		} catch (Exception f) {
			log.error(f);
		}
        
        
        // default inference graph
        try {
        	Model infDbPlainModel = makeDBModelFromConfigurationProperties(JENA_INF_MODEL, DB_ONT_MODEL_SPEC);
        	OntModel infDbModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC,infDbPlainModel);
        	sce.getServletContext().setAttribute("inferenceOntModel",infDbModel);
        } catch (Throwable e) {
        	log.error(e);
        }
           
        sce.getServletContext().setAttribute("jenaOntModel", memModel);
        sce.getServletContext().setAttribute("persistentOntModel", dbModel);
        
        // BJL23: This is a funky hack until I completely rework how the models get set up in a more sane fashion
        sce.getServletContext().setAttribute("useModelSynchronizers", "false");
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
        //Close the database connection
        //try {
        //    ((IDBConnection)sce.getServletContext().getAttribute("jenaConnection")).close();
        //} catch (Exception e) {
        //    //log.debug("could not close the JDBC connection.");
        //}
	}
	
}
