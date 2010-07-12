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

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelSynchronizer;

/**
 * This is the beginning of a more sane and flexible model management system,
 * especially necessary for DataStaR.
 * Don't use it yet; it's going to change.
 * (That part is still insane, I know.)
 * @author bjl23
 */
public class AssembleModelsSetup implements ServletContextListener {

	private static final Log log = LogFactory.getLog(AssembleModelsSetup.class);
	
	private List<Model> assembledModels = new LinkedList<Model>();
	
	private String ASSEMBLERS_DIR_PATH = "/WEB-INF/assemblers/";
	private Resource ASSEMBLER_OBJECT = ResourceFactory.createProperty("http://jena.hpl.hp.com/2005/11/Assembler#Object");
	private String SYNTAX = "N3";
	
	public void contextInitialized(ServletContextEvent sce) {
		OntModel jenaOntModel = null;
		try {
			jenaOntModel = (OntModel) sce.getServletContext().getAttribute("baseOntModel");
		} catch (Exception e) {
			log.error("No baseOntModel found to which to attach assembled models");
			return;
		}
		// read assemblers out of assemblers directory
		Set pathSet = sce.getServletContext().getResourcePaths(ASSEMBLERS_DIR_PATH);
		for (String path : (Set<String>)pathSet) {
			InputStream assemblerInputStream = sce.getServletContext().getResourceAsStream(path);
			Model assemblerModel = ModelFactory.createDefaultModel();
			try {
				assemblerModel.read(assemblerInputStream, null, SYNTAX);
				ExtendedIterator assemblerIt = assemblerModel.listResourcesWithProperty(RDF.type,ASSEMBLER_OBJECT);
				while (assemblerIt.hasNext()) {
					Resource assemblerObj = (Resource) assemblerIt.next();
					Model assembledModel = Assembler.general.openModel(assemblerObj);
					/* special stuff here */
					Model memModel = ModelFactory.createDefaultModel();
					memModel.add(assembledModel);
					memModel.register(new ModelSynchronizer(assembledModel));
					/* end special stuff */
					if (assembledModel != null) {
						jenaOntModel.addSubModel(memModel);
					}
				}
				if (assemblerIt != null) {
					assemblerIt.close();
				}
			} catch (Exception e) {
				log.error("Unable to use assembler at "+path);
			}
		}
		System.out.println("ContextListener AssembleModelsSetup done");
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		for (Model model : assembledModels) {
			if (model != null) {
				model.close();
			}
		}
	}

}
