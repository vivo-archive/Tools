package edu.cornell.mannlib.vitro.webapp.dao.jena;

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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.GraphMaker;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.ModelReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Wraps a model maker and returns Models from the servlet context when 
 * certain model URIs are requested
 * @author bjl23
 *
 */
public class VitroJenaSpecialModelMaker implements ModelMaker {

	private static final Log log = LogFactory.getLog(VitroJenaSpecialModelMaker.class.getName());
	
	private ModelMaker innerModelMaker;
	private HttpServletRequest request;
	
	public VitroJenaSpecialModelMaker(ModelMaker mm,  HttpServletRequest request) {
		this.innerModelMaker = mm;
		this.request = request;
	}
	
	public void close() {
		innerModelMaker.close();
	}

	public Model createModel(String arg0) {
		return innerModelMaker.createModel(arg0);
	}

	public Model createModel(String arg0, boolean arg1) {
		Model specialModel = getSpecialModel(arg0);
		return (specialModel != null) ? specialModel : innerModelMaker.createModel(arg0,arg1);
	}

	public GraphMaker getGraphMaker() {
		return innerModelMaker.getGraphMaker();
	}

	public boolean hasModel(String arg0) {
		return ( (getSpecialModel(arg0) != null) || innerModelMaker.hasModel(arg0) );
	}

	/**
	 * Won't list the special models
	 */
	public ExtendedIterator listModels() {
		return innerModelMaker.listModels(); 
	}

	public Model openModel(String arg0, boolean arg1) {
		Model specialModel = getSpecialModel(arg0);
		return (specialModel != null) ? specialModel : innerModelMaker.openModel(arg0,arg1);
	}

	public void removeModel(String arg0) {
		innerModelMaker.removeModel(arg0);
	}

	public Model createModelOver(String arg0) {
		Model specialModel = getSpecialModel(arg0);
		return (specialModel != null) ? specialModel : createModelOver(arg0);
	}

	public Model createDefaultModel() {
		return innerModelMaker.createDefaultModel();
	}

	public Model createFreshModel() {
		return innerModelMaker.createFreshModel();
	}

	public Model openModel(String arg0) {
		Model specialModel = getSpecialModel(arg0);
		return (specialModel != null) ? specialModel : innerModelMaker.openModel(arg0);
	}

	public Model openModelIfPresent(String arg0) {
		Model specialModel = getSpecialModel(arg0);
		return (specialModel != null) ? specialModel : innerModelMaker.openModelIfPresent(arg0);
	}

	public Model getModel(String arg0) {
		Model specialModel = getSpecialModel(arg0);
		return (specialModel != null) ? specialModel : innerModelMaker.getModel(arg0);
	}

	public Model getModel(String arg0, ModelReader arg1) {
		Model specialModel = getSpecialModel(arg0);
		return (specialModel != null) ? specialModel : innerModelMaker.getModel(arg0,arg1);
	}
	
	/**
	 * This will trap for strings like "vitro:jenaOntModel" and return the
	 * appropriate in-memory model used by the current webapp context.
	 * To use this functionality, the VitroJenaModelMaker must be constructed 
	 * with a VitroRequest parameter
	 */
	private Model getSpecialModel(String modelName) {
		if (request != null) {
			if ("vitro:jenaOntModel".equals(modelName)) {
				Object sessionOntModel = request.getSession().getAttribute("jenaOntModel");
				if (sessionOntModel != null && sessionOntModel instanceof OntModel) {
					log.debug("Returning jenaOntModel from session");
					return (OntModel) sessionOntModel;
				} else {
					log.debug("Returning jenaOntModel from context");
					return (OntModel) request.getSession().getServletContext().getAttribute("jenaOntModel");
				}
			} else if ("vitro:baseOntModel".equals(modelName)) {
				Object sessionOntModel = request.getSession().getAttribute("baseOntModel");
				if (sessionOntModel != null && sessionOntModel instanceof OntModel) {
					return (OntModel) sessionOntModel;
				} else {
					return (OntModel) request.getSession().getServletContext().getAttribute("baseOntModel");
				}
			} else if ("vitro:inferenceOntModel".equals(modelName)) {
				Object sessionOntModel = request.getSession().getAttribute("inferenceOntModel");
				if (sessionOntModel != null && sessionOntModel instanceof OntModel) {
					return (OntModel) sessionOntModel;
				} else {
					return (OntModel) request.getSession().getServletContext().getAttribute("inferenceOntModel");
				}
			} else {
				return null;
			}
		}
		return null;
	}

}
