package edu.cornell.mannlib.vitro.webapp.auth.policy;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;

/**
 * This is a List of Policy Objects that implements PolciyIface.  The intent
 * is to make it easy to query a list of policies for a PolicyDecision.  
 * 
 *  The Policy objects in the PolicyList are queried for authorization in order
 *  and return the first AUTHORIZED or UNAUTHROIZED decision.  INCONCLUSIVE
 *  or null decisions will be ignored and the next policy on the list will
 *  be queried. 
 *  
 *   
 * @author bdc34
 *
 */
public class PolicyList extends ArrayList<PolicyIface> implements PolicyIface{
    private static final Log log = LogFactory.getLog(PolicyList.class.getName());

    public PolicyList(){
        super();
    }

    public PolicyDecision isAuthorized(IdentifierBundle whoToAuth, RequestedAction whatToAuth) {
        PolicyDecision pd = null;
        for(PolicyIface policy : this){ 
            try{
                pd = policy.isAuthorized(whoToAuth, whatToAuth);
                if( pd != null ){
                    if(  pd.getAuthorized() == Authorization.AUTHORIZED )
                        break;
                    if( pd.getAuthorized() == Authorization.UNAUTHORIZED )
                        break;
//                    if( pd.getAuthorized() == Authorization.INCONCLUSIVE )
//                        continue;
                } else{
                    log.debug("policy " + policy.toString() + " returned a null PolicyDecision");
                }

            }catch(Throwable th){
                log.error("ignoring exception in policy " + policy.toString(), th );
            }
        }
        return pd;
    }

}
