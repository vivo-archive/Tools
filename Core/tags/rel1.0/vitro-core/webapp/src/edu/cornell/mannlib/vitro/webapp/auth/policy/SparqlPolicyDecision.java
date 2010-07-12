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

package edu.cornell.mannlib.vitro.webapp.auth.policy;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;

import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;

/**
 * Extends the BasicPolicyDecision with additional debugging information about the
 * sparql queries that were run to create the decision.
 * 
 * @author bdc34
 *
 */
public class SparqlPolicyDecision extends BasicPolicyDecision {
    Query query = null;
    QueryExecution qexec = null;

    public SparqlPolicyDecision(Authorization authorized, String message) {
        super(authorized, message);
    }

    public QueryExecution getQexec() {
        return qexec;
    }

    public void setQexec(QueryExecution qexec) {
        this.qexec = qexec;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    @Override
    public String getDebuggingInfo() {
        String msg = "";
        if( super.getDebuggingInfo() != null && super.getDebuggingInfo().length() > 0)
            msg = super.getDebuggingInfo() + '\n';

        if( query != null )
            msg= msg + "query: \n" + query.toString() + '\n';
         else
            msg = msg + " query was null \n";

        if( qexec != null )
            msg = msg + "query exec: \n" + qexec.toString();
        else
            msg = msg + " query exec was null \n";

        return msg;
    }


}
