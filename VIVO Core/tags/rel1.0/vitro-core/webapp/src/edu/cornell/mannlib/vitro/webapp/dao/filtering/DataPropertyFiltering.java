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

import java.util.LinkedList;
import java.util.List;

import net.sf.jga.algorithms.Filter;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;

public class DataPropertyFiltering extends DataProperty {
    private VitroFilters filters;
    private DataProperty innerDataProperty;
    
    public DataPropertyFiltering(DataProperty innerDataProperty, VitroFilters filters){
        this.innerDataProperty = innerDataProperty;
        this.filters = filters;
    }
    
    /**
     * Need to filter DataPropertyStatements and return DataPropertyStatements
     * wrapped in DataPropertyStatementsFiltering. 
     */
    @Override
    public List<DataPropertyStatement> getDataPropertyStatements() {        
        List<DataPropertyStatement> propStmts =  innerDataProperty.getDataPropertyStatements();
        if( propStmts == null ) return null;
        
        List<DataPropertyStatement> filteredStmts = new LinkedList<DataPropertyStatement>();
        Filter.filter(propStmts, filters.getDataPropertyStatementFilter(), filteredStmts);
        
        List<DataPropertyStatement> wrappedStmts = new LinkedList<DataPropertyStatement>();
        for( DataPropertyStatement stmt : filteredStmts){
            wrappedStmts.add( new DataPropertyStatementFiltering(stmt, filters) );
        }
        return wrappedStmts;        
    }
    
           
    /* the rest of the methods are delegated with no filtering */
    @Override
    public int compareTo(DataProperty op) {
        return innerDataProperty.compareTo(op);
    }

    @Override
    public boolean equals(Object obj) {
        return innerDataProperty.equals(obj);
    }

    @Override
    public String getDescription() {
        return innerDataProperty.getDescription();
    }

    @Override
    public int getDisplayLimit() {
        return innerDataProperty.getDisplayLimit();
    }

    @Override
    public int getDisplayTier() {
        return innerDataProperty.getDisplayTier();
    }

    @Override
    public String getDomainClassURI() {
        return innerDataProperty.getDomainClassURI();
    }

    @Override
    public String getPublicName() {
        return innerDataProperty.getPublicName();
    }

    @Override
    public String getEditLabel() {
        return innerDataProperty.getEditLabel();
    }

    @Override
    public String getExample() {
        return innerDataProperty.getExample();
    }

    @Override
    public String getGroupURI() {
        return innerDataProperty.getGroupURI();
    }

    @Override
    public RoleLevel getHiddenFromDisplayBelowRoleLevel() {
        return innerDataProperty.getHiddenFromDisplayBelowRoleLevel();
    }
    
    @Override
    public RoleLevel getProhibitedFromUpdateBelowRoleLevel() {
        return innerDataProperty.getProhibitedFromUpdateBelowRoleLevel();
    }

    @Override
    public String getLocalName() {
        return innerDataProperty.getLocalName();
    }
    
    @Override
    public String getLocalNameWithPrefix() {
        return innerDataProperty.getLocalNameWithPrefix();
    }
    
    @Override
    public String getPickListName() {
        return innerDataProperty.getPickListName();
    }

    @Override
    public String getNamespace() {
        return innerDataProperty.getNamespace();
    }

    @Override
    public String getPublicDescription() {
        return innerDataProperty.getPublicDescription();
    }

    @Override
    public String getURI() {
        return innerDataProperty.getURI();
    }

    @Override
    public int hashCode() {
        return innerDataProperty.hashCode();
    }

    @Override
    public boolean isAnonymous() {
        return innerDataProperty.isAnonymous();
    }

    @Override
    public boolean isSubjectSide() {
        return innerDataProperty.isSubjectSide();
    }

    @Override
    public void setDescription(String description) {
        innerDataProperty.setDescription(description);
    }

    @Override
    public void setPublicName(String publicName) {
        innerDataProperty.setPublicName(publicName);
    }

    @Override
    public void setDomainClassURI(String domainClassURI) {
        innerDataProperty.setDomainClassURI(domainClassURI);
    }

    @Override
    public void setEditLabel(String label) {
        innerDataProperty.setEditLabel(label);
    }

    @Override
    public void setExample(String example) {
        innerDataProperty.setExample(example);
    }

    @Override
    public void setGroupURI(String in) {
        innerDataProperty.setGroupURI(in);
    }

    @Override
    public void setHiddenFromDisplayBelowRoleLevel(RoleLevel eR) {
        innerDataProperty.setHiddenFromDisplayBelowRoleLevel(eR);
    }
    
    @Override
    public void setHiddenFromDisplayBelowRoleLevelUsingRoleUri(String roleUri) {
        innerDataProperty.setHiddenFromDisplayBelowRoleLevel(BaseResourceBean.RoleLevel.getRoleByUri(roleUri));
    }

    @Override
    public void setProhibitedFromUpdateBelowRoleLevel(RoleLevel eR) {
        innerDataProperty.setProhibitedFromUpdateBelowRoleLevel(eR);
    }
    
    @Override
    public void setProhibitedFromUpdateBelowRoleLevelUsingRoleUri(String roleUri) {
        innerDataProperty.setProhibitedFromUpdateBelowRoleLevel(BaseResourceBean.RoleLevel.getRoleByUri(roleUri));
    }

    @Override
    public void setLocalName(String localName) {
        innerDataProperty.setLocalName(localName);
    }

    @Override
    public void setLocalNameWithPrefix(String localNameWithPrefix) {
        innerDataProperty.setLocalNameWithPrefix(localNameWithPrefix);
    }
    
    @Override
    public void setPickListName(String pickListName) {
        innerDataProperty.setPickListName(pickListName);
    }

    @Override
    public void setNamespace(String namespace) {
        innerDataProperty.setNamespace(namespace);
    }

    @Override
    public void setDataPropertyStatements(
            List<DataPropertyStatement> objectPropertyStatements) {
        innerDataProperty
                .setDataPropertyStatements(objectPropertyStatements);
    }

    @Override
    public void setPublicDescription(String s) {
        innerDataProperty.setPublicDescription(s);
    }

    @Override
    public void setDisplayLimit(int displayLimit) {
        innerDataProperty.setDisplayLimit(displayLimit);
    }

    @Override
    public void setDisplayTier(int displayTier) {
        innerDataProperty.setDisplayTier(displayTier);
    }

    @Override
    public void setURI(String URI) {
        innerDataProperty.setURI(URI);
    }

    @Override
    public String toString() {
        return innerDataProperty.toString();
    }
}
