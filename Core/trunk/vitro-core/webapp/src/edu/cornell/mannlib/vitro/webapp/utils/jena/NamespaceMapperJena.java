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

package edu.cornell.mannlib.vitro.webapp.utils.jena;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.listeners.StatementListener;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.util.iterator.ClosableIterator;

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.utils.NamespaceMapper;

public class NamespaceMapperJena extends StatementListener implements
		NamespaceMapper {
	
	private HashMap<String,String> prefixToNamespaceMap;
	private HashMap<String,List<String>> namespaceToPrefixMap;
	
	private HashSet<String> knownNamespaces;
	
	private OntModel metadataModel;
	private OntModel dataModel;
	
	private boolean pipeOpen = true;
	
	private String namespaceForNamespaceObjects;

	private HashSet<String> propertyURIsToListenFor;
	
	public NamespaceMapperJena(OntModel dataModel, OntModel metadataModel, String namespaceForNamespaceObjects) {
		prefixToNamespaceMap = new HashMap<String,String>();
		namespaceToPrefixMap = new HashMap<String,List<String>>();
		
		knownNamespaces = new HashSet<String>();
		
		this.dataModel = dataModel;
		dataModel.getBaseModel().register(this);
		this.metadataModel = metadataModel;
		this.namespaceForNamespaceObjects = namespaceForNamespaceObjects; 
		
		propertyURIsToListenFor = new HashSet<String>();
		propertyURIsToListenFor.add(VitroVocabulary.NAMESPACE_HASPREFIXMAPPING);
		propertyURIsToListenFor.add(VitroVocabulary.NAMESPACE_ISCURRENTPREFIXMAPPING);
		propertyURIsToListenFor.add(VitroVocabulary.NAMESPACE_PREFIX);
		
		rebuildNamespaceCache();
		
	}
	
	private void rebuildNamespaceCache() {
		HashMap<String,String> tempPrefixToNamespaceMap = new HashMap<String,String>();
		HashMap<String,List<String>> tempNamespaceToPrefixMap = new HashMap<String,List<String>>();
		metadataModel.enterCriticalSection(Lock.READ);
		try {	
			// Iterate through all the namespace objects
			ClosableIterator closeIt = metadataModel.listIndividuals(metadataModel.getResource(VitroVocabulary.NAMESPACE));
			try {
				for (Iterator namespaceIt = closeIt; namespaceIt.hasNext();) {
					Individual namespaceInd = (Individual) namespaceIt.next();
					String namespaceURI = null;
					RDFNode node = namespaceInd.getPropertyValue(metadataModel.getProperty(VitroVocabulary.NAMESPACE_NAMESPACEURI));
					// Get the namespace URI
					if ( (node != null) && node.isLiteral()) {
						namespaceURI = ((Literal)node).getLexicalForm();
						knownNamespaces.add(namespaceURI);
					}
					// Iterate through all the prefix mappings for each namespace
					List<String> prefixList = new LinkedList<String>();
					ClosableIterator closeIt2 = namespaceInd.listPropertyValues(metadataModel.getProperty(VitroVocabulary.NAMESPACE_HASPREFIXMAPPING));
					try {
						for (Iterator namespacePrefixMappingIt = closeIt2; namespacePrefixMappingIt.hasNext();) {
							RDFNode value = (RDFNode) namespacePrefixMappingIt.next();
							if (value.canAs(Individual.class)) {
								Individual namespacePrefixMappingInd = (Individual) value.as(Individual.class);
								RDFNode prefixValue = namespacePrefixMappingInd.getPropertyValue(metadataModel.getProperty(VitroVocabulary.NAMESPACE_PREFIX));
								if ( (prefixValue != null) && (prefixValue.isLiteral()) ) {
									String prefix = ((Literal)prefixValue).getLexicalForm();
									boolean isCurrent = true;
									RDFNode isCurrentValue = namespacePrefixMappingInd.getPropertyValue(metadataModel.getProperty(VitroVocabulary.NAMESPACE_ISCURRENTPREFIXMAPPING));
									if ( (isCurrentValue != null) && (isCurrentValue.isLiteral()) ) {
										isCurrent = ((Literal)isCurrentValue).getBoolean();
									} 
									// if it's the current prefix, we want to put it at the head of the list
									if (isCurrent) {
										prefixList.add(0, prefix);
									} else {
										prefixList.add(prefix);
									}
									tempPrefixToNamespaceMap.put(prefix, namespaceURI);
								}
							}
						}
					} finally {
						closeIt2.close();
					}
					tempNamespaceToPrefixMap.put(namespaceURI, prefixList);
				}
			} finally {
				closeIt.close();
			}
		} finally {
			metadataModel.leaveCriticalSection();
		}
		namespaceToPrefixMap = tempNamespaceToPrefixMap;
		prefixToNamespaceMap = tempPrefixToNamespaceMap;
	}
	
	private void makeNewNamespaces(Statement s) {
		List<String> namespacesToCheck = new LinkedList<String>();
		Resource subj = s.getSubject();
		if ( (!subj.isAnon()) && (subj.getNameSpace() != null) ) {
			namespacesToCheck.add(subj.getNameSpace());
		}
		Property pred = s.getPredicate();
		if (pred.getNameSpace() != null) {
			namespacesToCheck.add(pred.getNameSpace());		
		}
		if ( s.getObject().isResource() ) {
			if ( ((Resource)s.getObject()).getNameSpace() != null ) {
				namespacesToCheck.add( ((Resource)s.getObject()).getNameSpace() );
			}
		}
		processPossibleNewNamespaces(namespacesToCheck);
	}
	
	private void processPossibleNewNamespaces(List<String> namespaceList) {
		Set<String> newNamespaces = new HashSet<String>();
		for (String namespace : namespaceList) {
			metadataModel.enterCriticalSection(Lock.READ);
			try {
				if (!knownNamespaces.contains(namespace)) {
					newNamespaces.add(namespace);
				}
			} finally {
				metadataModel.leaveCriticalSection();
			}
		}
		if (newNamespaces.size()>0) {
			List<String> newNamespaceList = new LinkedList<String>();
			newNamespaceList.addAll(newNamespaces);
			createNewNamespaceObjects(newNamespaceList);
		}
	}
	
	private void createNewNamespaceObjects(List<String> newNamespaces) {
		for (String newNamespace : newNamespaces) {
			metadataModel.enterCriticalSection(Lock.WRITE);
			try {
				// make a new namespace object
				int id = 1;
				while (metadataModel.getIndividual(namespaceForNamespaceObjects+"ns"+id) != null) {
					id++;
				}
				Individual nsInd = metadataModel.createIndividual(namespaceForNamespaceObjects+"ns"+id,metadataModel.getResource(VitroVocabulary.NAMESPACE));
				nsInd.addProperty(metadataModel.getProperty(VitroVocabulary.NAMESPACE_NAMESPACEURI), newNamespace);
				knownNamespaces.add(newNamespace);
			} finally {
				metadataModel.leaveCriticalSection();
			}
		}
	}
	
	@Override
	public void addedStatement(Statement s) {
		if (!pipeOpen) return;
		// avoid stack overflow errors due to listening to our own changes
		pipeOpen = false;
		makeNewNamespaces(s);
		String predURI = s.getPredicate().getURI();
		if (propertyURIsToListenFor.contains(predURI)) {
			rebuildNamespaceCache();
		}
		pipeOpen = true;
	}

	@Override
	public void removedStatement(Statement s) {
		if (!pipeOpen) return;
		pipeOpen = false;
		String predURI = s.getPredicate().getURI();
		if (propertyURIsToListenFor.contains(predURI)) {
			rebuildNamespaceCache();
		}
		pipeOpen = true;
	}

	public String getNamespaceForPrefix(String prefix) {
		return prefixToNamespaceMap.get(prefix);
	}

	public String getPrefixForNamespace(String namespace) {
		List<String> prefixList = namespaceToPrefixMap.get(namespace);
		return (prefixList == null || prefixList.size()==0) ? null : prefixList.get(0);
	}

	public List<String> getPrefixesForNamespace(String namespace) {
		return namespaceToPrefixMap.get(namespace);
	}
	
}
