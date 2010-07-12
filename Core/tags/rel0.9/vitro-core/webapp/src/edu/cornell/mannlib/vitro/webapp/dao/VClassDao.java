package edu.cornell.mannlib.vitro.webapp.dao;

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


import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;

import java.util.List;

public interface VClassDao {

    List<VClass> getRootClasses();

    List<VClass> getOntologyRootClasses(String ontologyURI);

    List<VClass> getAllVclasses();

    List<String> getDisjointWithClassURIs(String vclassURI);
    
    void addSuperclass(VClass subclass, VClass superclass);
    
    void addSuperclass(String classURI, String superclassURI);
    
    void removeSuperclass(VClass vclass, VClass superclass);
    
    void removeSuperclass(String classURI, String superclassURI);
    
    void addSubclass(VClass vclass, VClass subclass);
    
    void addSubclass(String classURI, String subclassURI);
    
    void removeSubclass(VClass vclass, VClass subclass);
    
    void removeSubclass(String classURI, String subclassURI);
    
    void addDisjointWithClass(String classURI, String disjointCLassURI);
    
    void removeDisjointWithClass(String classURI, String disjointClassURI);
    
    List<String> getEquivalentClassURIs(String classURI);
    
    void addEquivalentClass(String classURI, String equivalentClassURI);
    
    void removeEquivalentClass(String classURI, String equivalentClassURI);
    
    List <String> getSubClassURIs(String classURI);

    List <String> getAllSubClassURIs(String classURI);

    List <String> getSuperClassURIs(String classURI);

    List <String> getAllSuperClassURIs(String classURI);

    VClass getVClassByURI(String URI);

    void insertNewVClass(VClass cls ) throws InsertException;

    void updateVClass(VClass cls);

    void deleteVClass(String URI);

    void deleteVClass(VClass cls);

    List <VClass> getVClassesForProperty(String propertyURI, boolean domainSide);
    
    List <VClass> getVClassesForProperty(String vclassURI, String propertyURI);

    void addVClassesToGroup(VClassGroup group);

    @SuppressWarnings("unchecked")
    void addVClassesToGroup(VClassGroup group, boolean includeUninstantiatedClasses);/* (non-Javadoc)
    * @see edu.cornell.mannlib.vitro.webapp.dao.db.VClassDao#addVClassesToGroups(java.util.List)
    */

    void addVClassesToGroup(VClassGroup group, boolean includeUninstantiatedClasses, boolean getIndividualCount); /*
    * @see edu.cornell.mannlib.vitro.webapp.dao.db.VClassDao#addVClassesToGroups(java.util.List)
    */
    
    /*
    void addVClassesToGroup(VClassGroup group, boolean includeUninstantiatedClasses, boolean getIndividualCount, RoleLevel userVisibilityRoleLevel, RoleLevel userUpdateRoleLevel ); /*
    * @see edu.cornell.mannlib.vitro.webapp.dao.db.VClassDao#addVClassesToGroups(java.util.List)
    */
    
    void addVClassesToGroups(List <VClassGroup> groups );
    
    /**
     * @param vc1
     * @param vc2
     * @return true if subClassOf(vc1, vc2)
     */
    boolean isSubClassOf(VClass vc1, VClass vc2);
    
    /**
     * @param vc1
     * @param vc2
     * @return true if subClassOf(vc1, vc2)
     */
    boolean isSubClassOf(String vclassURI1, String vclassURI2);
    
    
    /**
     * Returns the top concept for the current modeling language (e.g. owl:Thing)
     */
    VClass getTopConcept();
    
    /**
     * Returns the bottom concept for the current modeling language (e.g. owl:Nothing)
     */
    VClass getBottomConcept();
}