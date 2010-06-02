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

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;

public interface ObjectPropertyDao extends PropertyDao {

    public abstract List<ObjectProperty> getAllObjectProperties();

    public ObjectProperty getObjectPropertyByURI(String objectPropertyURI);

    public List <ObjectProperty> getObjectPropertiesForObjectPropertyStatements(List /*of ObjectPropertyStatement */ objectPropertyStatements);

    public List<String> getSuperPropertyURIs(String objectPropertyURI);

    public List<String> getSubPropertyURIs(String objectPropertyURI);

    public List<ObjectPropertyStatement> getStatementsUsingObjectProperty(ObjectProperty op);

    public void fillObjectPropertiesForIndividual(Individual individual);

    public int insertObjectProperty(ObjectProperty objectProperty ) throws InsertException;

    public void updateObjectProperty(ObjectProperty objectProperty );

    public void deleteObjectProperty(String objectPropertyURI);

    public void deleteObjectProperty(ObjectProperty objectProperty);
    
    public boolean skipEditForm(String predicateURI);
                                             

//    List /*of ObjectProperty */ getObjectPropertiesForObjectPropertyStatements(List /*of ObjectPropertyStatement */ objectPropertyStatements);
//
//    void fillObjectPropertiesForIndividual(IndividualWebapp individual);
//
//    int insertObjectProperty(ObjectProperty objectPropertyWebapp);
//
//    void updateObjectProperty(ObjectProperty objectPropertyWebapp);
//
//    void deleteObjectProperty(ObjectProperty objectPropertyWebapp);

//    ObjectProperty getObjectPropertyByURI(String objectPropertyURI);

//    List /* of ObjectProperty */ getAllObjectProperties();

    List <ObjectProperty> getRootObjectProperties();
}
