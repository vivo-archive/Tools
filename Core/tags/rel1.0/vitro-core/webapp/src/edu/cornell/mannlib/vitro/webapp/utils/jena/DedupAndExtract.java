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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;

public class DedupAndExtract {

	/**
	 * Returns a model where redundant individuals that are sameAs one another are smushed
	 * using URIs in preferred namespaces where possible.
	 * @param model
	 * @param preferredIndividualNamespace
	 * @return
	 */
	public Model dedupAndExtract( Model model, String preferredNamespace ) {
		
		Model extractsModel = ModelFactory.createDefaultModel();
		
		HashMap<String, String> rewriteURIUsing = new HashMap<String, String>();
		
		Iterator haveSameAsIt = model.listSubjectsWithProperty(OWL.sameAs);
		while (haveSameAsIt.hasNext()) {
			String preferredURI = null;
			Resource hasSameAs = (Resource) haveSameAsIt.next();
			List<Statement> sameAsList = hasSameAs.listProperties(OWL.sameAs).toList();
			if (sameAsList.size()>1) { // if sameAs something other than the same URI (we assume reasoning model)
				List<String> sameAsURIs = new LinkedList<String>();
				Iterator sameAsStmtIt = sameAsList.iterator();
				for (int i=0; i<sameAsList.size(); i++) {
					Statement sameAsStmt = (Statement) sameAsStmtIt.next();
					if (!sameAsStmt.getObject().isResource()) {
						throw new RuntimeException( sameAsStmt.getResource().getURI() + " is sameAs() a literal!" );
					}
					Resource sameAsRes = (Resource) sameAsStmt.getObject();
					if (!sameAsRes.isAnon()) {
						sameAsURIs.add(sameAsRes.getURI());
						if (preferredNamespace != null & preferredNamespace.equals(sameAsRes.getNameSpace())) {
							preferredURI = sameAsRes.getURI();
						}
					}
					if (preferredURI == null) {
						preferredURI = sameAsURIs.get(0);
					}
					for (String s : sameAsURIs) {
						rewriteURIUsing.put(s,preferredURI);
					}
				}
			}
		}
		
		StmtIterator modelStmtIt = model.listStatements();
		while (modelStmtIt.hasNext()) {
			Statement origStmt = modelStmtIt.nextStatement();
			Resource newSubj = null;
			RDFNode newObj = null;
			if (!origStmt.getSubject().isAnon()) { 
				String rewriteURI = rewriteURIUsing.get(origStmt.getSubject().getURI());
				if (rewriteURI != null) {
					newSubj = extractsModel.getResource(rewriteURI);
				}
			}
			if (origStmt.getObject().isResource() && !origStmt.getResource().isAnon()) {
				String rewriteURI = rewriteURIUsing.get(((Resource) origStmt.getObject()).getURI());
				if (rewriteURI != null) {
					newObj = extractsModel.getResource(rewriteURI);
				}
			}
			if (newSubj == null) {
				newSubj = origStmt.getSubject();
			}
			if (newObj == null) {
				newObj = origStmt.getObject();
			}
			extractsModel.add(newSubj, origStmt.getPredicate(), newObj);
		}
		
		return extractsModel;
		
	}
	
}