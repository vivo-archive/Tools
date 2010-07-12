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

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.util.iterator.ClosableIterator;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.IndividualUpdateEvent;

public class ObjectPropertyStatementDaoJena extends JenaBaseDao implements ObjectPropertyStatementDao {

    public ObjectPropertyStatementDaoJena(WebappDaoFactoryJena wadf) {
        super(wadf);
    }

    public void deleteObjectPropertyStatement(ObjectPropertyStatement objPropertyStmt) {
    	deleteObjectPropertyStatement(objPropertyStmt, getOntModelSelector().getABoxModel());
    }

    public void deleteObjectPropertyStatement(ObjectPropertyStatement objPropertyStmt, OntModel ontModel) {
        ontModel.enterCriticalSection(Lock.WRITE);
        getOntModel().getBaseModel().notifyEvent(new IndividualUpdateEvent(getWebappDaoFactory().getUserURI(),true,objPropertyStmt.getSubjectURI()));
        try {
            Resource s = ontModel.getResource(objPropertyStmt.getSubjectURI());
            com.hp.hpl.jena.rdf.model.Property p = ontModel.getProperty(objPropertyStmt.getPropertyURI());
            Resource o = ontModel.getResource(objPropertyStmt.getObjectURI());
            if ((s != null) && (p != null) && (o != null)) {
                ontModel.remove(s,p,o);
            }
            List<Statement> dependentResources = DependentResourceDeleteJena.getDependentResourceDeleteList(o, ontModel);
            ontModel.remove(dependentResources);
        } finally {
        	getOntModel().getBaseModel().notifyEvent(new IndividualUpdateEvent(getWebappDaoFactory().getUserURI(),false,objPropertyStmt.getSubjectURI()));
            ontModel.leaveCriticalSection();
        }
    }

    public Individual fillExistingObjectPropertyStatements(Individual entity) {
        if (entity.getURI() == null)
            return entity;
        else {
        	Map<String, ObjectProperty> uriToObjectProperty = new HashMap<String,ObjectProperty>();
        	
        	ObjectPropertyDaoJena opDaoJena = new ObjectPropertyDaoJena(getWebappDaoFactory());
            Resource ind = getOntModel().getResource(entity.getURI());
            List<ObjectPropertyStatement> objPropertyStmtList = new ArrayList<ObjectPropertyStatement>();
            ClosableIterator propIt = ind.listProperties();
            try {
                while (propIt.hasNext()) {
                    Statement st = (Statement) propIt.next();
                    if (st.getObject().isResource() && !(NONUSER_NAMESPACES.contains(st.getPredicate().getNameSpace()))) {
                        try {
                            ObjectPropertyStatement objPropertyStmt = new ObjectPropertyStatementImpl();
                            objPropertyStmt.setSubjectURI(entity.getURI());
                            objPropertyStmt.setSubject(entity);
                            try {
                                objPropertyStmt.setObjectURI(((Resource)st.getObject()).getURI());
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                            objPropertyStmt.setPropertyURI(st.getPredicate().getURI());
                            try {
                                Property prop = st.getPredicate();
                                if( uriToObjectProperty.containsKey(prop.getURI())){
                                	objPropertyStmt.setProperty(uriToObjectProperty.get(prop.getURI()));
                                }else{
                                	ObjectProperty p = opDaoJena.propertyFromOntProperty(getOntModel().getObjectProperty(prop.getURI()));
                                	if( p != null ){
                                		uriToObjectProperty.put(prop.getURI(), p);
                                		objPropertyStmt.setProperty(uriToObjectProperty.get(prop.getURI()));
                                	}else{
                                		//if ObjectProperty not found in ontology, skip it
                                		continue;
                                	}
                                }                                
                            } catch (Throwable g) {
                                //do not add statement to list
                            	log.debug("exception while trying to get object property for statement list, statement skipped.", g);
                            	continue;                                                                
                            }
                            if (objPropertyStmt.getObjectURI() != null) {
                                Individual objInd = getWebappDaoFactory().getIndividualDao().getIndividualByURI(objPropertyStmt.getObjectURI());
                                objPropertyStmt.setObject(objInd);
                            }

                            //add object property statement to list for Individual
                            if ((objPropertyStmt.getSubjectURI() != null) && (objPropertyStmt.getPropertyURI() != null) && (objPropertyStmt.getObject() != null)){
                                objPropertyStmtList.add(objPropertyStmt);                           
                            } 
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            } finally {
                propIt.close();
            }
            entity.setObjectPropertyStatements(objPropertyStmtList);
            return entity;
        }
    }
    
    private int NO_LIMIT = -1;
    
    public List<ObjectPropertyStatement> getObjectPropertyStatements (ObjectProperty objectProperty) {
    	return getObjectPropertyStatements(objectProperty, NO_LIMIT, NO_LIMIT);
    }
    
    public List<ObjectPropertyStatement> getObjectPropertyStatements (ObjectProperty objectProperty, int startIndex, int endIndex) {
    	getOntModel().enterCriticalSection(Lock.READ);
    	List<ObjectPropertyStatement> opss = new ArrayList<ObjectPropertyStatement>();
    	try {
    		Property prop = ResourceFactory.createProperty(objectProperty.getURI());
    		ClosableIterator opsIt = getOntModel().listStatements(null,prop,(Resource)null);
    		try {
    			int count = 0;
    			while ( (opsIt.hasNext()) && ((endIndex<0) || (count<endIndex)) ) {
    				Statement stmt = (Statement) opsIt.next();
    				if (stmt.getObject().isResource()) {
	    				++count;
	    				if (startIndex<0 || startIndex<=count) {
	    					Resource objRes = (Resource) stmt.getObject();
	    					if (!objRes.isAnon()) {
			    				ObjectPropertyStatement ops = new ObjectPropertyStatementImpl();
			    				ops.setSubjectURI(stmt.getSubject().getURI());
			    				ops.setPropertyURI(objectProperty.getURI());		
			    				ops.setObjectURI(objRes.getURI());
			    				opss.add(ops);
	    					}
	    				}
    				}
    			}
    		} finally {
    			opsIt.close();
    		}
    	} finally {
    		getOntModel().leaveCriticalSection()
;    	}
    	return opss;
    }

    public int insertNewObjectPropertyStatement(ObjectPropertyStatement objPropertyStmt) {
    	return insertNewObjectPropertyStatement(objPropertyStmt, getOntModelSelector().getABoxModel());
    }

    public int insertNewObjectPropertyStatement(ObjectPropertyStatement objPropertyStmt, OntModel ontModel) {
        ontModel.enterCriticalSection(Lock.WRITE);
        getOntModel().getBaseModel().notifyEvent(new IndividualUpdateEvent(getWebappDaoFactory().getUserURI(),true,objPropertyStmt.getSubjectURI()));
        try {
            Resource s = ontModel.getResource(objPropertyStmt.getSubjectURI());
            com.hp.hpl.jena.rdf.model.Property p = ontModel.getProperty(objPropertyStmt.getPropertyURI());
            Resource o = ontModel.getResource(objPropertyStmt.getObjectURI());
            if ((s != null) && (p != null) && (o != null)) {
                ontModel.add(s,p,o);
            }
        } finally {
        	getOntModel().getBaseModel().notifyEvent(new IndividualUpdateEvent(getWebappDaoFactory().getUserURI(),false,objPropertyStmt.getSubjectURI()));
            ontModel.leaveCriticalSection();
        }
        return 0;
    }

}
