package edu.cornell.mannlib.vitro.webapp.auth.requestedAction;

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

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.VisitingPolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestActionConstants;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;

public class DropDataPropStmt implements RequestedAction {

    final DataPropertyStatement dataPropStmt;
    
    public DropDataPropStmt(DataPropertyStatement dps){
        this.dataPropStmt = dps;
    }
    
    public PolicyDecision accept(VisitingPolicyIface policy, IdentifierBundle whoToAuth) {
        return policy.visit(whoToAuth,this);
    }

    //TODO: rename this method to something like getUriOfSubject    
    public String uriOfSubject(){ return dataPropStmt.getIndividualURI(); }
    
    //TODO: rename this method to something like getUriOfPredicate
    public String uriOfPredicate(){ return dataPropStmt.getDatapropURI(); }
    
    public String data(){ return dataPropStmt.getData(); }
    public String lang(){ return dataPropStmt.getLanguage(); }
    public String datatype(){return dataPropStmt.getDatatypeURI(); }
    
    
    public String getURI() {
        return RequestActionConstants.actionNamespace + this.getClass().getName();
    }

    /*
     * TODO: needs to be fixed to work with lang/datatype literals
     */
    
    /*
    
    protected String resourceUri;
    protected String dataPropUri;
    protected String value;

    //TODO: needs to be fixed to work with lang/datatype literals
    public DropDataPropStmt(String resourceUri, String dataPropUri, String value) {
        super();
        this.resourceUri = resourceUri;
        this.dataPropUri = dataPropUri;
        this.value = value;
    }

    public String getDataPropUri() {
        return dataPropUri;
    }

    public void setDataPropUri(String dataPropUri) {
        this.dataPropUri = dataPropUri;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }
//TODO: needs to be fixed to work with lang/datatype literals
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getURI() {
        return RequestActionConstants.actionNamespace + this.getClass().getName();
    }
    public PolicyDecision accept(PolicyIface policy, IdentifierBundle ids){
        return policy.visit(ids,this);
    } */
}
