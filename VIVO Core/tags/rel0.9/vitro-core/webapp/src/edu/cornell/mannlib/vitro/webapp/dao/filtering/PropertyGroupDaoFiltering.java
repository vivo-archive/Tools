package edu.cornell.mannlib.vitro.webapp.dao.filtering;

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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.sf.jga.algorithms.Filter;

import com.hp.hpl.jena.ontology.DatatypeProperty;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyGroup;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyGroupDao;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;

public class PropertyGroupDaoFiltering implements PropertyGroupDao {

    final PropertyGroupDao innerDao;
    final WebappDaoFactoryFiltering filteredDaos;
    final VitroFilters filters;

	public PropertyGroupDaoFiltering(PropertyGroupDao propertyGroupDao,
            WebappDaoFactoryFiltering webappDaoFactoryFiltering,
            VitroFilters filters) {	    
	    this.innerDao = propertyGroupDao;
        this.filteredDaos = webappDaoFactoryFiltering;
        this.filters = filters;	    
    }

    public void deletePropertyGroup(PropertyGroup group) {
		innerDao.deletePropertyGroup(group);
	}

	
    public PropertyGroup getGroupByURI(String uri) {
        PropertyGroup grp  = innerDao.getGroupByURI(uri);
         wrapPropertyGroup( grp );
         return grp;
    }
   
	private void  wrapPropertyGroup( PropertyGroup grp ){
	    if( grp == null ) return ;        
        List<Property> props =  grp.getPropertyList();
        if( props == null ||  props.size() == 0 ) 
            return ;
        
        List<Property> filteredProps = new LinkedList<Property>();
        for( Property prop : props ){
    	    if( prop != null ){
	           	if( prop instanceof ObjectProperty ){
                	if( filters.getObjectPropertyFilter().fn( (ObjectProperty)prop ) ){
        	           	filteredProps.add( new ObjectPropertyFiltering((ObjectProperty)prop,filters));
            	    }
	            }else if( prop instanceof ObjectPropertyFiltering ){                
                	//log.debug("property instanceof ObjectPropertyFiltering == true but property instanceof ObjectProperty == false");
	                if( filters.getObjectPropertyFilter().fn( (ObjectProperty)prop ) ){
    	                filteredProps.add( new ObjectPropertyFiltering((ObjectProperty)prop,filters));
        	        }
            	}else if( prop instanceof DatatypeProperty ){
                	if( filters.getDataPropertyFilter().fn((DataProperty)prop)){
                    	filteredProps.add( prop );
	                }
    	        }
            }
        }
        
        grp.setPropertyList(filteredProps); //side effect 
        return ;	    
	}
	
	public List<PropertyGroup> getPublicGroups(boolean withProperties) {
		List<PropertyGroup> groups =  innerDao.getPublicGroups(withProperties);
		for( PropertyGroup grp : groups ){
		    wrapPropertyGroup(grp);
		}
		return groups;
	}
	
	public PropertyGroup createTempPropertyGroup(String name, int rank) {
	    return innerDao.createTempPropertyGroup(name, rank);
	}
	
	public String insertNewPropertyGroup(PropertyGroup group) {
		return innerDao.insertNewPropertyGroup(group);
	}

	
	public int removeUnpopulatedGroups(List<PropertyGroup> groups) {
		return innerDao.removeUnpopulatedGroups(groups);
	}

	public void updatePropertyGroup(PropertyGroup group) {
		innerDao.updatePropertyGroup(group);
	}

}
