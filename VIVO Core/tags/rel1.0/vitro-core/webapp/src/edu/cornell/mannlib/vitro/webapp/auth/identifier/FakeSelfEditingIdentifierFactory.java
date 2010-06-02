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

package edu.cornell.mannlib.vitro.webapp.auth.identifier;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.SelfEditingIdentifierFactory.NetId;
import edu.cornell.mannlib.vitro.webapp.auth.policy.RoleBasedPolicy;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

/** 
 * Attempts to simulate the action of NetIdIdentifierFactory.java using the
 * request attribute FAKE_SELF_EDIT_NETID.
 */
public class FakeSelfEditingIdentifierFactory implements IdentifierBundleFactory{    
    
    public static final String FAKE_SELF_EDIT_NETID = "fakeSelfEditingNetid";     
    
    public IdentifierBundle getIdentifierBundle(ServletRequest request,
            HttpSession session, ServletContext context) {
        WebappDaoFactory wdf = ((WebappDaoFactory)context.getAttribute("webappDaoFactory"));
        
        IdentifierBundle ib = new ArrayIdentifierBundle();
        ib.add( RoleBasedPolicy.AuthRole.ANYBODY);
                
        String netid = null;
        if( session != null )
            netid = (String)session.getAttribute(FAKE_SELF_EDIT_NETID );
        
        if( netid != null ){
            NetId netIdToken = new NetId(netid);
            ib.add(netIdToken);
            
            String uri = wdf.getIndividualDao().getIndividualURIFromNetId( netid );
            if( uri != null ){        
                Individual ind = wdf.getIndividualDao().getIndividualByURI(uri);
                if( ind != null ){        
                    String causeOfBlacklist = SelfEditingIdentifierFactory.checkForBlacklisted(ind, context);
                    if( causeOfBlacklist == SelfEditingIdentifierFactory.NOT_BLACKLISTED )
                        ib.add( new SelfEditingIdentifierFactory.SelfEditing( ind, SelfEditingIdentifierFactory.NOT_BLACKLISTED ) );
                    else
                        ib.add( new SelfEditingIdentifierFactory.SelfEditing( ind, causeOfBlacklist ) );
                }
            }
        }
        return ib;        
    }

    public static void putFakeIdInSession(String netid, HttpSession session){        
        session.setAttribute(FAKE_SELF_EDIT_NETID , netid);
    }
    
    public static void clearFakeIdInSession( HttpSession session){        
        session.removeAttribute(FAKE_SELF_EDIT_NETID);
    }
}
