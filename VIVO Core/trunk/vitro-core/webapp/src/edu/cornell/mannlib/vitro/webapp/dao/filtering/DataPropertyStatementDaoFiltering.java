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

import edu.cornell.mannlib.vitro.webapp.beans.*;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;
import net.sf.jga.fn.UnaryFunctor;

import java.util.Collection;
import java.util.List;

class DataPropertyStatementDaoFiltering extends BaseFiltering implements DataPropertyStatementDao{
    final DataPropertyStatementDao innerDataPropertyStatementDao;    
    final VitroFilters filters;

    public DataPropertyStatementDaoFiltering(
            DataPropertyStatementDao dataPropertyStatementDao,
            VitroFilters filters) {
        super();
        this.innerDataPropertyStatementDao = dataPropertyStatementDao;
        this.filters = filters;
    }


    public void deleteDataPropertyStatement(DataPropertyStatement dataPropertyStatement) {
        innerDataPropertyStatementDao.deleteDataPropertyStatement(dataPropertyStatement);
    }


    public Individual fillExistingDataPropertyStatementsForIndividual(
            Individual individual/*,
            boolean allowAnyNameSpace*/) {
        if( individual == null ) return null;
        Individual ind = innerDataPropertyStatementDao.fillExistingDataPropertyStatementsForIndividual(individual/*,allowAnyNameSpace*/);
        if( ind == null ) return null;

        List<DataPropertyStatement> dprops = ind.getDataPropertyStatements();
        if( dprops != null ){
            ind.setDataPropertyStatements(filter(dprops, filters.getDataPropertyStatementFilter()));
        }
        return ind;
    }

    public void deleteDataPropertyStatementsForIndividualByDataProperty(Individual individual, DataProperty dataProperty) {
        innerDataPropertyStatementDao.deleteDataPropertyStatementsForIndividualByDataProperty(individual, dataProperty);
    }

    public void deleteDataPropertyStatementsForIndividualByDataProperty(String individualURI, String dataPropertyURI) {
        innerDataPropertyStatementDao.deleteDataPropertyStatementsForIndividualByDataProperty(individualURI, dataPropertyURI);
    }

    public Collection<DataPropertyStatement> getDataPropertyStatementsForIndividualByDataPropertyURI(
            Individual entity, String datapropURI) {
        Collection<DataPropertyStatement> col =
            innerDataPropertyStatementDao
                .getDataPropertyStatementsForIndividualByDataPropertyURI(entity, datapropURI);
        if( col != null ){
            return filter(col,filters.getDataPropertyStatementFilter());
        }else{
            return null;
        }
    }

    @Deprecated
    public List getExistingQualifiers(String dataPropertyURI) {
        return innerDataPropertyStatementDao.getExistingQualifiers(dataPropertyURI);
    }
    
    public List<DataPropertyStatement> getDataPropertyStatements(DataProperty dataProperty) {
    	List<DataPropertyStatement> dps = innerDataPropertyStatementDao.getDataPropertyStatements(dataProperty);
    	if (dps != null) {
    		return filter(dps,filters.getDataPropertyStatementFilter());
    	} else {
    		return dps;
    	}
    }
    
    public List<DataPropertyStatement> getDataPropertyStatements(DataProperty dataProperty, int startIndex, int endIndex) {
    	List<DataPropertyStatement> dps = innerDataPropertyStatementDao.getDataPropertyStatements(dataProperty, startIndex, endIndex);
    	if (dps != null) {
    		return filter(dps,filters.getDataPropertyStatementFilter());
    	} else {
    		return dps;
    	}
    }

    public int insertNewDataPropertyStatement(
            DataPropertyStatement dataPropertyStatement) {
        return innerDataPropertyStatementDao.insertNewDataPropertyStatement(dataPropertyStatement);
    }


}