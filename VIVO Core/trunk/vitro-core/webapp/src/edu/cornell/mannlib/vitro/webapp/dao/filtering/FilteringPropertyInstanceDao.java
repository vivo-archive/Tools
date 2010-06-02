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

package edu.cornell.mannlib.vitro.webapp.dao.filtering;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.jga.algorithms.Filter;
import net.sf.jga.fn.UnaryFunctor;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyInstance;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyInstanceIface;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyInstanceDao;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;

public class FilteringPropertyInstanceDao implements PropertyInstanceDao {
    private final PropertyInstanceDao innerPropertyInstanceDao;
    private final VitroFilters filters;
    private final UnaryFunctor<PropertyInstance,Boolean> propertyInstanceFilter;
    private final IndividualDao individualDao;
    private final ObjectPropertyDao objectPropDao;
    
    
    public FilteringPropertyInstanceDao(
            final PropertyInstanceDao propertyInstanceDao, 
            final ObjectPropertyDao objectPropDao,
            final IndividualDao individualDao,
            final VitroFilters filters) {
        if( propertyInstanceDao == null ) 
            throw new IllegalArgumentException("Must pass a non null PropertyInstanceDao to constructor");
        if( filters == null )
            throw new IllegalArgumentException("Must pass a non-null VitroFilters to constructor");
        
        this.innerPropertyInstanceDao = propertyInstanceDao;
        this.filters = filters;
        this.individualDao = individualDao;
        this.objectPropDao = objectPropDao;
        
        this.propertyInstanceFilter = new UnaryFunctor<PropertyInstance,Boolean>(){
            @Override
            public Boolean fn(PropertyInstance inst) {
                if( inst == null ) return false;
                
                //this shouldn't happen
                if( inst.getSubjectEntURI()== null && inst.getPropertyURI() == null &&
                        inst.getRangeClassURI() == null )
                    return false;
                
                //in some classes, like PropertyDWR.java, a PropertyInstance with nulls
                //in the subjectUri and objectUri represent an ObjectProperty, not
                //an ObjectPropertyStatement.                
                if( inst.getSubjectEntURI() == null && inst.getObjectEntURI() == null
                        && inst.getPropertyURI() != null ){
                    //is it a property we can show?
                    ObjectProperty op = objectPropDao.getObjectPropertyByURI(inst.getPropertyURI());
                    if( op == null )
                        return false;
                    else
                        return filters.getObjectPropertyFilter().fn(op);                    
                }
                
                
                //Filter based on subject, property and object.  This could be changed
                //to filter on the subject's and object's class.
                Individual sub = individualDao.getIndividualByURI(inst.getSubjectEntURI());
                if( filters.getIndividualFilter().fn(sub) == false )
                    return false;
                Individual obj = individualDao.getIndividualByURI(inst.getObjectEntURI());
                if( filters.getIndividualFilter().fn(obj) == false)
                    return false;
                ObjectProperty prop = objectPropDao.getObjectPropertyByURI(inst.getPropertyURI());
                if( filters.getObjectPropertyFilter().fn(prop) == false)
                    return false;
                else 
                    return true;                                
            }            
        };
    }
     
    /* ******************** filtered methods ********************* */    
    public Collection<PropertyInstance> getAllPossiblePropInstForIndividual(
            String individualURI) {
        Collection<PropertyInstance> innerInst = innerPropertyInstanceDao.getAllPossiblePropInstForIndividual(individualURI);
        Collection<PropertyInstance> out = new LinkedList<PropertyInstance>();
        Filter.filter(innerInst, propertyInstanceFilter , out);                
        return out;        
    }

    public Collection<PropertyInstance> getAllPropInstByVClass(String classURI) {
        Collection<PropertyInstance> innerInst = innerPropertyInstanceDao.getAllPropInstByVClass(classURI);
        Collection<PropertyInstance> out = new LinkedList<PropertyInstance>();
        Filter.filter(innerInst, propertyInstanceFilter , out);                
        return out;
    }

    public Collection<PropertyInstance> getExistingProperties(String entityURI,
            String propertyURI) {
        Collection<PropertyInstance> innerInst = innerPropertyInstanceDao.getExistingProperties(entityURI, propertyURI);
        Collection<PropertyInstance> out = new LinkedList<PropertyInstance>();
        Filter.filter(innerInst, propertyInstanceFilter , out);                
        return out;
    }

    public PropertyInstance getProperty(String subjectURI, String predicateURI,
            String objectURI) {
        PropertyInstance propInst = innerPropertyInstanceDao.getProperty(subjectURI, predicateURI, objectURI);
        if( propertyInstanceFilter.fn(propInst) )
            return propInst;
        else
            return null;
    }

    
    /* ************* unfiltered methods that might need to be filtered ****** */
    public Iterator getAllOfThisTypeIterator() {
        return innerPropertyInstanceDao.getAllOfThisTypeIterator();
    }
    
    /* **************** unfiltered methods ***************** */    
    public void deleteObjectPropertyStatement(String subjectURI,
            String propertyURI, String objectURI) {
        innerPropertyInstanceDao.deleteObjectPropertyStatement(subjectURI, propertyURI, objectURI);
    }

    public void deletePropertyInstance(PropertyInstance prop) {
        innerPropertyInstanceDao.deletePropertyInstance(prop);
    }

    public int insertProp(PropertyInstanceIface prop) {
        return innerPropertyInstanceDao.insertProp(prop);
    }

    public void insertPropertyInstance(PropertyInstance prop) {
        innerPropertyInstanceDao.insertPropertyInstance(prop);

    }

    
}
