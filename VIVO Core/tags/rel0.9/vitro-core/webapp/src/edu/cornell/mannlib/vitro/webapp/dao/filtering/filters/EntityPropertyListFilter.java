package edu.cornell.mannlib.vitro.webapp.dao.filtering.filters;

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
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jga.fn.UnaryFunctor;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

public class EntityPropertyListFilter extends UnaryFunctor<List<Property>, List<Property>> {

	private static final com.hp.hpl.jena.rdf.model.Property MASKS_PROPERTY = ResourceFactory.createProperty(VitroVocabulary.MASKS_PROPERTY);
	private Map<String,Collection<String>> propertyMaskMap;
	
	public EntityPropertyListFilter(OntModel ontModel) {
		propertyMaskMap = new HashMap<String,Collection<String>>();
		ontModel.enterCriticalSection(Lock.READ);
		try {
			StmtIterator maskStmtIt = ontModel.listStatements((Resource) null, MASKS_PROPERTY, (RDFNode) null );
			while (maskStmtIt.hasNext()) {
				Statement maskStmt = maskStmtIt.nextStatement();
				if ( !maskStmt.getSubject().isAnon() && maskStmt.getObject().isResource() && !((Resource) maskStmt.getObject()).isAnon()) {
					String maskedPropertyURI = ((Resource) maskStmt.getObject()).getURI();
					String maskingPropertyURI = maskStmt.getSubject().getURI();
					Collection<String> collectionOfMaskers = propertyMaskMap.get(maskedPropertyURI);
					if (collectionOfMaskers == null) {
						collectionOfMaskers = new LinkedList<String>();
					}
					if (!collectionOfMaskers.contains(maskingPropertyURI)) {
						collectionOfMaskers.add(maskingPropertyURI);
					}
					propertyMaskMap.put(maskedPropertyURI, collectionOfMaskers);
				}
			}
		} finally {
			ontModel.leaveCriticalSection();
		}
	}
		
	@Override
	public List<Property> fn(List<Property> propertyList) {
		List<Property> filteredList = new ArrayList<Property>();
		HashMap<String, Property> urisToProps = new HashMap<String, Property>();
		for (Property p: propertyList) {
			urisToProps.put(p.getURI(), p);
		}
		for (Property p : propertyList) {
			Collection<String> maskingPropertyURIs = propertyMaskMap.get(p.getURI());
			if (maskingPropertyURIs == null) {
				filteredList.add(p);
			} else {
				Property maskingProp = null;
				for (String maskingURI : maskingPropertyURIs) {
					if (urisToProps.keySet().contains(maskingURI)) {
						maskingProp = urisToProps.get(maskingURI);
						break;
					}
				}
				// BUT: don't mask a prop if it "has" (used in) statements and its masker does not
				boolean propHasStatements = false;
				boolean maskerHasStatements = false;
				if (maskingProp != null) {
					if (p instanceof ObjectProperty) {
						List<ObjectPropertyStatement> stmtList = ((ObjectProperty) p).getObjectPropertyStatements();
						propHasStatements = (stmtList != null) && (stmtList.size() > 0);
					} else if (p instanceof DataProperty) {
						List<DataPropertyStatement> stmtList = ((DataProperty) p).getDataPropertyStatements(); 
						propHasStatements = (stmtList != null) && (stmtList.size() > 0);
					}
					if (maskingProp instanceof ObjectProperty) {
						List<ObjectPropertyStatement> stmtList = ((ObjectProperty) maskingProp).getObjectPropertyStatements();
						maskerHasStatements = (stmtList != null) && (stmtList.size() > 0);
					} else if (maskingProp instanceof DataProperty) {
						List<DataPropertyStatement> stmtList = ((DataProperty) maskingProp).getDataPropertyStatements(); 
						maskerHasStatements = (stmtList != null) && (stmtList.size() > 0);
					}
				}
				if ( (maskingProp == null) || (propHasStatements & !maskerHasStatements) ) {
					filteredList.add(p);
				}
			}
		}
		return filteredList;
	}
	
}
