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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mindswap.pellet.PelletOptions;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.vocabulary.OWL;

import edu.cornell.mannlib.vitro.webapp.dao.jena.pellet.PelletListener;
import edu.cornell.mannlib.vitro.webapp.dao.jena.pellet.ReasonerConfiguration;

public class RDFSReasonerSetup implements ServletContextListener {
	
	private static final Log log = LogFactory.getLog(PelletReasonerSetupComplete.class.getName());
	
	/**
	 * This ContextListener uses the infrastructure designed for Pellet, but switches the OntModelSpec
	 * to use Jena's RDFS reasoner.  Pellet itself is not used, despite the current names of some of the
	 * classes involved.
	 */
	public void contextInitialized(ServletContextEvent sce) {
		
		try {	
			
			OntModel memoryModel = (OntModel) sce.getServletContext().getAttribute("jenaOntModel");
			OntModel baseModel = (OntModel) sce.getServletContext().getAttribute("baseOntModel");
			OntModel inferenceModel = (OntModel) sce.getServletContext().getAttribute("inferenceOntModel");
	        
	        ReasonerConfiguration configuration = ReasonerConfiguration.COMPLETE;
	        configuration.setOntModelSpec(OntModelSpec.RDFS_MEM_RDFS_INF);
	        PelletListener pelletListener = new PelletListener(memoryModel,baseModel,inferenceModel,configuration);
	        sce.getServletContext().setAttribute("pelletListener",pelletListener);
	        sce.getServletContext().setAttribute("pelletOntModel", pelletListener.getPelletModel());
	        
	        log.debug("RDFS reasoner connected");
	     
			} catch (Throwable t) {
				t.printStackTrace();
			}
	}
	
	public void contextDestroyed(ServletContextEvent arg0) {
		// 
	}

}
