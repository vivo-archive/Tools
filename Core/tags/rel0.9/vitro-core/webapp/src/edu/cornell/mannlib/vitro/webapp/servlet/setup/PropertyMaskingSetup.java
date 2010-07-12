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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.jga.fn.UnaryFunctor;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.EntityPropertyListFilter;
import edu.cornell.mannlib.vitro.webapp.dao.jena.JenaBaseDao;

public class PropertyMaskingSetup implements ServletContextListener {

	private final static String ENTITY_PROPERTY_LIST_FILTER_ATTR_NAME = "entityPropertyListFilter";

	public void contextInitialized(ServletContextEvent sce) {
		OntModel jenaOntModel = (OntModel) sce.getServletContext().getAttribute(JenaBaseDao.JENA_ONT_MODEL_ATTRIBUTE_NAME);		
        sce.getServletContext().setAttribute(ENTITY_PROPERTY_LIST_FILTER_ATTR_NAME, new EntityPropertyListFilter(jenaOntModel));
	}
	
	public static UnaryFunctor<List<Property>,List<Property>> getEntityPropertyListFilter(ServletContext ctx) {
		return (UnaryFunctor<List<Property>,List<Property>>) ctx.getAttribute(ENTITY_PROPERTY_LIST_FILTER_ATTR_NAME);
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		// nothing to worry about
	}

}
