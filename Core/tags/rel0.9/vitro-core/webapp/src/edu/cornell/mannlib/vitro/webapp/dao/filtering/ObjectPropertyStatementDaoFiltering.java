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

import java.util.ArrayList;
import java.util.List;

import net.sf.jga.algorithms.Filter;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;

class ObjectPropertyStatementDaoFiltering extends BaseFiltering implements ObjectPropertyStatementDao{
    final ObjectPropertyStatementDao innerObjectPropertyStatementDao;
    final VitroFilters filters;


    public ObjectPropertyStatementDaoFiltering(
            ObjectPropertyStatementDao objectPropertyStatementDao,
            VitroFilters filters) {
        super();
        this.innerObjectPropertyStatementDao = objectPropertyStatementDao;
        this.filters = filters;
    }

    
    public void deleteObjectPropertyStatement(ObjectPropertyStatement objPropertyStmt) {
        innerObjectPropertyStatementDao.deleteObjectPropertyStatement(objPropertyStmt);
    }


    protected static List<ObjectPropertyStatement> filterAndWrapList(List<ObjectPropertyStatement> list, VitroFilters filters){        
        if( ( list ) != null ){                        
            
            ArrayList<ObjectPropertyStatement> ctemp = new ArrayList<ObjectPropertyStatement>();
            Filter.filter(list,filters.getObjectPropertyStatementFilter(),ctemp);
                        
            List<ObjectPropertyStatement> cout= new ArrayList<ObjectPropertyStatement>(list.size());
            for( ObjectPropertyStatement stmt: ctemp){
                cout.add( new ObjectPropertyStatementFiltering(stmt,filters) );
            }
            return cout;
        }else{
            return null;
        }
    }
    
    public Individual fillExistingObjectPropertyStatements(Individual entity) {
        Individual ind = innerObjectPropertyStatementDao.fillExistingObjectPropertyStatements(entity);
        if( ind == null ) 
            return null;
        else{    
            ind.setObjectPropertyStatements( filterAndWrapList( ind.getObjectPropertyStatements(), filters) );       
            return ind;
        }
    }

    public List<ObjectPropertyStatement> getObjectPropertyStatements(ObjectProperty objectProperty) {
    	return filterAndWrapList( innerObjectPropertyStatementDao.getObjectPropertyStatements(objectProperty), filters );
    }
    
    public List<ObjectPropertyStatement> getObjectPropertyStatements(ObjectProperty objectProperty, int startIndex, int endIndex) {
    	return filterAndWrapList( innerObjectPropertyStatementDao.getObjectPropertyStatements(objectProperty, startIndex, endIndex) ,filters);    	
    }
    
    public int insertNewObjectPropertyStatement(ObjectPropertyStatement objPropertyStmt) {
        return innerObjectPropertyStatementDao.insertNewObjectPropertyStatement(objPropertyStmt);
    }

}