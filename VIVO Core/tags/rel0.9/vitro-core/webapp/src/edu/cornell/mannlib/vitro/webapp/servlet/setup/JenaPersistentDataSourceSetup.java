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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ClosableIterator;

import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelSynchronizer;

public class JenaPersistentDataSourceSetup extends JenaDataSourceSetupBase implements ServletContextListener {
	
	private static final Log log = LogFactory.getLog(JenaPersistentDataSourceSetup.class.getName());
	
	public void contextInitialized(ServletContextEvent sce) {
		OntModel memModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC);
		Model dbModel = null;
		
		boolean firstStartup = false;
		
        try {
            dbModel = makeDBModelFromPropertiesFile(sce.getServletContext().getRealPath(CONNECTION_PROP_LOCATION), JENA_DB_MODEL, DB_ONT_MODEL_SPEC);
            
            ClosableIterator stmtIt = dbModel.listStatements();
            try {
                if (!stmtIt.hasNext()) {
                    firstStartup = true;
                }
            } finally {
                stmtIt.close();
            }

            if (firstStartup) {
                long startTime = System.currentTimeMillis();
                System.out.println("Reading ontology files into database");
                ServletContext ctx = sce.getServletContext();
                readOntologyFilesInPathSet(USERPATH, ctx, dbModel);
                readOntologyFilesInPathSet(SYSTEMPATH, ctx, dbModel);
                System.out.println((System.currentTimeMillis()-startTime)/1000+" seconds to populate DB");
            }

            System.out.println("Populating in-memory Jena model from persistent DB model");

            long startTime = System.currentTimeMillis();
            memModel.add(dbModel);
            System.out.println((System.currentTimeMillis()-startTime)/1000+" seconds to synchronize models");
            
        	memModel.getBaseModel().register(new ModelSynchronizer(dbModel));
            
        } catch (OutOfMemoryError ome) {
            System.out.println("**** ERROR *****");
            System.out.println("Insufficient memory to load database contents for vitro.");
            System.out.println("Refer to servlet container documentation about increasing heap space.");
            System.out.println("****************");
        } catch (Throwable t) {
			System.out.println("**** ERROR *****");
            System.out.println("Vitro unable to open Jena database model.");
			System.out.println("Check that connection.properties has been created WEB-INF/classes, ");
			System.out.println("and that the database connection parameters are accurate. ");
			System.out.println("****************");
        }        
        
        // default inference graph
        try {
        	Model infDbModel = makeDBModelFromPropertiesFile(sce.getServletContext().getRealPath(CONNECTION_PROP_LOCATION), JENA_INF_MODEL, DB_ONT_MODEL_SPEC);
        	OntModel infModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC);
        	if (infDbModel != null) {
        		long startTime = System.currentTimeMillis();
        		System.out.println("Copying cached inferences into memory");
        		infModel.add(infDbModel);
        		System.out.println((System.currentTimeMillis()-startTime)/1000+" seconds to load inferences");
        	}
        	infModel.getBaseModel().register(new ModelSynchronizer(infDbModel));
        	sce.getServletContext().setAttribute("inferenceOntModel",infModel);
        } catch (Throwable e) {
        	log.error("Unable to load inference cache from DB", e);
        }
        
        // user accounts Model
        try {
        	Model userAccountsDbModel = makeDBModelFromPropertiesFile(sce.getServletContext().getRealPath(CONNECTION_PROP_LOCATION), JENA_USER_ACCOUNTS_MODEL, DB_ONT_MODEL_SPEC);
        	if (firstStartup) {
        		readOntologyFilesInPathSet(AUTHPATH, sce.getServletContext(), userAccountsDbModel);
        	}
        	OntModel userAccountsModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC);
        	userAccountsModel.add(userAccountsDbModel);
        	userAccountsModel.getBaseModel().register(new ModelSynchronizer(userAccountsDbModel));
        	sce.getServletContext().setAttribute("userAccountsOntModel", userAccountsModel);
        } catch (Throwable t) {
        	log.error("Unable to load user accounts model from DB", t);
        }
             
        sce.getServletContext().setAttribute("jenaOntModel", memModel);
       
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
	}
	
}
