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
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.InsertException;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;

class DataPropertyDaoFiltering extends BaseFiltering implements DataPropertyDao{
    final DataPropertyDao innerDataPropertyDao;
    final VitroFilters filters;

    public DataPropertyDaoFiltering(DataPropertyDao dataPropertyDao,
            VitroFilters filters) {
        super();
        this.innerDataPropertyDao = dataPropertyDao;
        this.filters = filters;
    }

    public void deleteDataProperty(DataProperty dataProperty) {
        innerDataPropertyDao.deleteDataProperty(dataProperty);
    }


    public void deleteDataProperty(String dataPropertyURI) {
        innerDataPropertyDao.deleteDataProperty(dataPropertyURI);
    }

    public boolean annotateDataPropertyAsExternalIdentifier(String dataPropertyURI) {
        return innerDataPropertyDao.annotateDataPropertyAsExternalIdentifier(dataPropertyURI);
    }


    public void fillDataPropertiesForIndividual(Individual individual) {
        innerDataPropertyDao.fillDataPropertiesForIndividual(individual);
        List<DataProperty> props = individual.getDataPropertyList();
        if(props != null && props.size() > 0){
            individual.setDatatypePropertyList( filter(props,filters.getDataPropertyFilter()) );
        }
    }


    public List getAllDataProperties() {
        return filter(innerDataPropertyDao.getAllDataProperties(), filters.getDataPropertyFilter());
    }

    public List getAllExternalIdDataProperties() {
        return filter(innerDataPropertyDao.getAllDataProperties(), filters.getDataPropertyFilter());
    }


    public List<DataProperty> getDataPropertiesForVClass(String classURI) {
        return filter(innerDataPropertyDao.getDataPropertiesForVClass(classURI),
                filters.getDataPropertyFilter());
    }

    public Collection<DataProperty> getAllPossibleDatapropsForIndividual(String individualURI) {
        return filter(innerDataPropertyDao.getAllPossibleDatapropsForIndividual(individualURI),
                filters.getDataPropertyFilter());
    }
    
    public String getRequiredDatatypeURI(Individual individual, DataProperty dataProperty) {
    	return innerDataPropertyDao.getRequiredDatatypeURI(individual, dataProperty);
    }
    
    public DataProperty getDataPropertyByURI(String dataPropertyURI) {
        DataProperty prop = innerDataPropertyDao.getDataPropertyByURI(dataPropertyURI);
        if( prop != null ){
            Boolean acceptable = filters.getDataPropertyFilter().fn(prop);
            if( acceptable == Boolean.TRUE )
                return prop;
            else
                return null;
        }
        return null;
    }


    public String insertDataProperty(DataProperty dataProperty) throws InsertException {
        return innerDataPropertyDao.insertDataProperty(dataProperty);
    }


    public void updateDataProperty(DataProperty dataProperty) {
        innerDataPropertyDao.updateDataProperty(dataProperty);
    }
    
    public void addSuperproperty(ObjectProperty property, ObjectProperty superproperty) {
    	innerDataPropertyDao.addSuperproperty(property, superproperty);
    }
    
    public void addSuperproperty(String propertyURI, String superpropertyURI) {
    	innerDataPropertyDao.addSuperproperty(propertyURI, superpropertyURI);
    }
    
    public void removeSuperproperty(ObjectProperty property, ObjectProperty superproperty) {
    	innerDataPropertyDao.removeSuperproperty(property, superproperty);
    }
    
    public void removeSuperproperty(String propertyURI, String superpropertyURI) {
    	innerDataPropertyDao.removeSuperproperty(propertyURI, superpropertyURI);
    }
    
    public void addSubproperty(ObjectProperty property, ObjectProperty subproperty) {
    	innerDataPropertyDao.addSubproperty(property, subproperty);
    }
    
    public void addSubproperty(String propertyURI, String subpropertyURI) {
    	innerDataPropertyDao.addSubproperty(propertyURI, subpropertyURI);
    }
    
    public void removeSubproperty(ObjectProperty property, ObjectProperty subproperty) {
    	innerDataPropertyDao.removeSubproperty(property, subproperty);
    }
    
    public void removeSubproperty(String propertyURI, String subpropertyURI) {
    	innerDataPropertyDao.removeSubproperty(propertyURI, subpropertyURI);
    }
    
    public List <String> getSubPropertyURIs(String propertyURI) {
    	return innerDataPropertyDao.getSubPropertyURIs(propertyURI);
    }

    public List <String> getAllSubPropertyURIs(String propertyURI) {
    	return innerDataPropertyDao.getAllSubPropertyURIs(propertyURI);
    }

    public List <String> getSuperPropertyURIs(String propertyURI) {
    	return innerDataPropertyDao.getSuperPropertyURIs(propertyURI);
    }

    public List <String> getAllSuperPropertyURIs(String propertyURI) {
    	return innerDataPropertyDao.getAllSuperPropertyURIs(propertyURI);
    }
    
    public List<DataProperty> getRootDataProperties() {
    	return innerDataPropertyDao.getRootDataProperties();
    }

	public void addSubproperty(Property property, Property subproperty) {
		innerDataPropertyDao.addSubproperty(property, subproperty);
	}

	public void addSuperproperty(Property property, Property superproperty) {
		innerDataPropertyDao.addSuperproperty(property, superproperty);	
	}

	public void removeSubproperty(Property property, Property subproperty) {
		innerDataPropertyDao.removeSubproperty(property, subproperty);
	}

	public void removeSuperproperty(Property property, Property superproperty) {
		innerDataPropertyDao.removeSuperproperty(property, superproperty);
	}

	public void addEquivalentProperty(String propertyURI,
			String equivalentPropertyURI) {
		innerDataPropertyDao.addEquivalentProperty(propertyURI, equivalentPropertyURI);
	}

	public void addEquivalentProperty(Property property,
			Property equivalentProperty) {
		innerDataPropertyDao.addEquivalentProperty(property, equivalentProperty);
	}

	public List<String> getEquivalentPropertyURIs(String propertyURI) {
		return innerDataPropertyDao.getEquivalentPropertyURIs(propertyURI);
	}

	public void removeEquivalentProperty(String propertyURI,
			String equivalentPropertyURI) {
		innerDataPropertyDao.removeEquivalentProperty(propertyURI, equivalentPropertyURI);
	}

	public void removeEquivalentProperty(Property property,
			Property equivalentProperty) {
		innerDataPropertyDao.removeEquivalentProperty(property, equivalentProperty);
	}

}