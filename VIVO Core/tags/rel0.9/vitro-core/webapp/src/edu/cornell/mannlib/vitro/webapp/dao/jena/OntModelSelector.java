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

import com.hp.hpl.jena.ontology.OntModel;

/**
 * Interface for retrieving RDF (Ont)models containing certain types of data.
 * So named because there was already a ModelSelector in the n3editing package.
 * @author bjl23
 */
public interface OntModelSelector {

	/**
	 * @return OntModel containing all RDF statements available to the application
	 */
	public OntModel getFullModel();
		
	/**
	 * @return OntModel containing Portals, Tabs, etc. but not Users
	 */
	public OntModel getApplicationMetadataModel();
	
	/**
	 * @return OntModel containing Users
	 */
	public OntModel getUserAccountsModel();
	
	/**
	 * @return OntModel containing all ABox assertions
	 */
	public OntModel getABoxModel();
	
	/**
	 * @return OntModel containing all TBox axioms
	 */
	public OntModel getTBoxModel();

	/**
	 * @param ontologyURI
	 * @return OntModel containing TBox axioms for the specified ontology
	 */
	public OntModel getTBoxModel(String ontologyURI);
	
}
