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

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelAuditor;

public class ModelAuditorSetup extends JenaDataSourceSetupBase implements ServletContextListener  {

	private static final Log log = LogFactory.getLog(ModelAuditorSetup.class.getName());
	
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	
	public void contextInitialized(ServletContextEvent sce) {
        try {
        	// the events don't seem to filter down to the writable model of a dynamic union.  Bummer.  For now we have the ugly workaround of registering twice and recording some duplication.
        	OntModel ontModel = (OntModel) sce.getServletContext().getAttribute("baseOntModel");
        	Model baseModel = ontModel.getBaseModel();
        	OntModel dynamicUnionModel = (OntModel) sce.getServletContext().getAttribute("jenaOntModel");
            log.debug("Setting model auditor database...");
            OntModel auditModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC,makeDBModelFromConfigurationProperties(JENA_AUDIT_MODEL, DB_ONT_MODEL_SPEC));
            sce.getServletContext().setAttribute("jenaAuditModel", auditModel);
            ModelAuditor ma = new ModelAuditor(auditModel,ontModel);
            baseModel.register(ma);
            dynamicUnionModel.getBaseModel().register(ma);
            log.debug("Successful.");
        } catch (Exception e) {
            log.error("Unable to use audit model "+JENA_AUDIT_MODEL);
        }
	}

}
