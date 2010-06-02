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
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vitro.webapp.auth.policy.RoleBasedPolicy.AuthRole;
import edu.cornell.mannlib.vitro.webapp.auth.policy.setup.SelfEditingPolicySetup;
import edu.cornell.mannlib.vitro.webapp.beans.User;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

/**
 * Checks to see if the Individual associated with a SelfEditingIdentifier
 * has Admin, Curator or Editor rights.  This ignores black listing.
 * 
 * This should be added to the IdentifierFactory list after the
 * SelfEditingIdentiferFactory.
 * 
 * SelfEditing2RoleIdentifierSetup can be used in web.xml to add this class
 * to the IdentifierFactory list of a servlet context.
 * 
 * @author bdc34
 *
 */
public class SelfEditing2RoleIdentifierFactory implements
        IdentifierBundleFactory {

    public IdentifierBundle getIdentifierBundle(ServletRequest request,
            HttpSession session, ServletContext context) {               
        IdentifierBundle whoToAuth = ServletIdentifierBundleFactory.getExistingIdBundle(request);
        if( whoToAuth != null ){            
            WebappDaoFactory wdf = (WebappDaoFactory)context.getAttribute("webappDaoFactory");
                if( wdf == null ) 
                    return whoToAuth;
                SelfEditingIdentifierFactory.SelfEditing selfEditing = 
                SelfEditingIdentifierFactory.getSelfEditingIdentifier(whoToAuth);
            if( selfEditing != null ){
                User user = wdf.getUserDao().getUserByURI(selfEditing.getIndividual().getURI());
                if( user != null){
                    String role = user.getRoleURI();
                    if("role/:50".equals(role)){
                        whoToAuth.add( AuthRole.DBA );
                    }
                    if("role/:4".equals(role)){
                        whoToAuth.add( AuthRole.CURATOR);
                    }
                    if("role/:3".equals(role)){
                        whoToAuth.add( AuthRole.EDITOR);
                    }
                    if("role/:2".equals(role)){
                        whoToAuth.add( AuthRole.USER );
                    }                                                            
                }               
            }            
        }
        return whoToAuth;
    }   
}