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

package edu.cornell.mannlib.vitro.webapp.auth.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundleFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.ServletIdentifierBundleFactory;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.RequestPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList;

/**
 * Setup an IdentifierBundle and PolicyList for the request and put it in the request scope.
 *
 * It expects to get the IdentifierBundleFactory from ServletIdentifierBundleFactory and
 * PolicyList from ServletPolicyList;
 *
 * @author bdc34
 *
 */
public class AuthSetupForRequest implements Filter {
    ServletContext context;

    public void init(FilterConfig filterConfig) throws ServletException {
        context = filterConfig.getServletContext();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //get a factory that will convert Requests into Identifiers
        IdentifierBundleFactory idbf = ServletIdentifierBundleFactory.getIdentifierBundleFactory(context);

        //try to get the session
        HttpSession session = null;
        if( servletRequest instanceof HttpServletRequest)
            session = ((HttpServletRequest)servletRequest).getSession(false);

        //get Identifiers and stick in Request scope
        try{
            if( idbf != null ){
                IdentifierBundle ib = idbf.getIdentifierBundle(servletRequest, session, context);
                servletRequest.setAttribute(IDENTIFIER_BUNDLE,  ib);
            }
        }catch(RuntimeException rx){
            log.warn("could not get Identifier Bundle",rx);
        }

        //get the policies that are in effect for the context and add to Request Scope
        PolicyList plist = ServletPolicyList.getPolicies(context);        
        servletRequest.setAttribute(RequestPolicyList.POLICY_LIST , plist);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() { }
    
    private static final Log log = LogFactory.getLog(AuthSetupForRequest.class);
    private static final String IDENTIFIER_BUNDLE = "IdentifierBundle";
    
    /* ************ static utility methods ********************* */
    public static IdentifierBundle getIdentifierBundle(HttpServletRequest req){
        if( req != null )                
            return (IdentifierBundle)req.getAttribute(IDENTIFIER_BUNDLE);            
       else
            return null;                                      
    }
    
    public static PolicyList getPolicyList( HttpServletRequest req){
        if( req != null ){
            HttpSession sess = req.getSession(false);
            if( sess != null ){     
                return (PolicyList)sess.getAttribute(RequestPolicyList.POLICY_LIST);
            }else{
                return null;            
            }
        }else{
            return null;            
        }
    }    
}