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

import java.util.Collection;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;

/**
 * Created by IntelliJ IDEA.
 * User: bdc34
 * Date: Apr 18, 2007
 * Time: 7:03:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DataPropertyStatementDao {

    void deleteDataPropertyStatement(DataPropertyStatement dataPropertyStmt);

    @Deprecated
    List getExistingQualifiers(String dataPropertyURI);
    
    List<DataPropertyStatement> getDataPropertyStatements(DataProperty dataProperty);
    
    List<DataPropertyStatement> getDataPropertyStatements(DataProperty dataProperty, int startIndex, int endIndex);

    @SuppressWarnings("unchecked")
    Collection<DataPropertyStatement> getDataPropertyStatementsForIndividualByDataPropertyURI(Individual entity,String datapropURI);

    Individual fillExistingDataPropertyStatementsForIndividual(Individual individual/*, boolean allowAnyNameSpace*/);

    void deleteDataPropertyStatementsForIndividualByDataProperty(String individualURI, String dataPropertyURI);

    void deleteDataPropertyStatementsForIndividualByDataProperty(Individual individual, DataProperty dataProperty);

    int insertNewDataPropertyStatement(DataPropertyStatement dataPropertyStatement );

}

