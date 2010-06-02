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
import java.util.Date;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyInstance;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;

public class DataPropertyStatementFiltering implements DataPropertyStatement {
    final DataPropertyStatement innerStmt;
    final VitroFilters filters;
    
    public DataPropertyStatementFiltering( DataPropertyStatement stmt, VitroFilters filters){
        this.innerStmt = stmt;
        this.filters = filters;
    }
    
    /***** methods that return wrapped objects *****/
    /*
    public String getIndividual() {
        return new IndividualFiltering(innerStmt.getIndividual(),filters);
    } */

    /* ******** */

    public String toString() {
        return innerStmt.toString();
    }

    public String getIndividualURI() {
        return innerStmt.getIndividualURI();
    }

    public String getLanguage() {
        return innerStmt.getLanguage();
    }

    public String getData() {
        return innerStmt.getData();
    }
    
    public String getDatatypeURI() {
        return innerStmt.getDatatypeURI();
    }

    public String getDatapropURI() {
        return innerStmt.getDatapropURI();
    }

    public Date getSunrise() {
        return innerStmt.getSunrise();
    }

    public Date getSunset() {
        return innerStmt.getSunset();
    }
    
    public String getString() {
        return innerStmt.getString();
    }
    
    public void setIndividualURI(String individualURI) {
        innerStmt.setIndividualURI(individualURI);
    }
    
    public void setData(String data) {
        innerStmt.setData(data);
    }

    public void setLanguage(String language) {
        innerStmt.setLanguage(language);
    }

    public void setDatatypeURI(String URI) {
        innerStmt.setDatatypeURI(URI);
    }

    public void setDatapropURI(String datapropURI) {
        innerStmt.setDatapropURI(datapropURI);
    }

    public void setSunrise(Date date) {
        innerStmt.setSunrise(date);
    }

    public void setSunset(Date date) {
        innerStmt.setSunset(date);
    }
    
}
