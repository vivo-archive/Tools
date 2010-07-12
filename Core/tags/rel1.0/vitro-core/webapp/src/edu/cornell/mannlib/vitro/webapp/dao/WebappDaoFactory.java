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

package edu.cornell.mannlib.vitro.webapp.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WebappDaoFactory {

	/**
	 * Retrieves a map containing arbitrary key-value pairs describing this WebappDaoFactory
	 */
	public Map<String,String> getProperties();

	/**
	 * Checks a URI String for two things: well-formedness and uniqueness in the model.
	 * Ill-formed strings or those matching URIs already in use will cause an error message to be returned.
	 * @return error message String if invalid; otherwise null
	 */
	public String checkURI(String uriStr);
	
	/**
	 * Checks a URI String for two things: well-formedness and, optionally, uniqueness in the model.
	 * Ill-formed strings or those matching URIs already in use will cause an error message to be returned.
	 * @return error message String if invalid; otherwise null
	 */
	public String checkURI(String uriStr, boolean checkUniqueness);
	
	public int getLanguageProfile();
	
    public String getDefaultNamespace();
    
    public Set<String> getNonuserNamespaces();
    
    public String[] getPreferredLanguages();
    
    /**
     * BJL23 2008-05-20: Putting this here for lack of a more logical place.  We need to build better support for the RDFS vocabulary into our API.
     * Returns a list of the simple lexical form strings of the rdfs:comment values for a resource; empty list if none found.
     */
    public List<String> getCommentsForResource(String resourceURI);

    /**
     * Copy this DAO factory to a new object associated with the specified user URI, or return the same factory if a user-aware version cannot be used.
     * @param userURI
     * @return
     */
    public WebappDaoFactory getUserAwareDaoFactory(String userURI);

    /**
     * Return URI of user associated with this WebappDaoFactory, or null if not applicable.
     * @return
     */
    public String getUserURI();

    /* ======================== DAOs for ontology (TBox) manipulation ======================== */

    /**
     * returns a Data Access Object for working with class subsumption axioms in the model
     */
    public Classes2ClassesDao getClasses2ClassesDao();

    /**
     * returns a Data Access Object for working with DataProperties in the model
     */
    public DataPropertyDao getDataPropertyDao();

    /**
     * returns a Data Access Object for working with Datatypes
     */
    public DatatypeDao getDatatypeDao();

    /**
     * returns a Data Access Object for working with ObjectProperties in the model
     */
    public ObjectPropertyDao getObjectPropertyDao();

    /**
     * returns a Data Access Object for working with Ontologies in the model
     */
    public OntologyDao getOntologyDao();

    /**
     * returns a Data Access Object for working with ontology class objects in the model
     */
    public VClassDao getVClassDao();


    /* ======================== DAOs for ABox manipulation ======================== */

    /**
     * returns a Data Access Object for working with DatatypePropertyStatements in the model
     */
    public DataPropertyStatementDao getDataPropertyStatementDao();

    /**
     * returns a Data Access Object for working with Individuals in the model
     */
    public IndividualDao getIndividualDao();

    /**
     * returns a Data Access Object for working with ObjectPropertyStatements in the model
     */
    public ObjectPropertyStatementDao getObjectPropertyStatementDao();


    /* ======================== DAOs for other objects ======================== */

    public ApplicationDao getApplicationDao();
    public PortalDao getPortalDao();

    public TabDao getTabDao();
    public TabIndividualRelationDao getTabs2EntsDao();
    public TabVClassRelationDao getTabs2TypesDao();

    public KeywordIndividualRelationDao getKeys2EntsDao();
    public KeywordDao getKeywordDao();

    public LinksDao getLinksDao();
    public LinktypeDao getLinktypeDao();

    public FlagDao getFlagDao();

    public UserDao getUserDao();

    public VClassGroupDao getVClassGroupDao();

    public PropertyGroupDao getPropertyGroupDao();
    
    public NamespaceDao getNamespaceDao();
    
    public PropertyInstanceDao getPropertyInstanceDao();    
}
