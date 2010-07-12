package edu.cornell.mannlib.vitro.webapp.auth.identifier;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import edu.cornell.mannlib.vedit.beans.LoginFormBean;
import edu.cornell.mannlib.vitro.webapp.auth.policy.RoleBasedPolicy;
import edu.cornell.mannlib.vitro.webapp.auth.policy.RoleBasedPolicy.AuthRole;

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

public class CuratorEditingIdentifierFactory implements IdentifierBundleFactory{
    
    public IdentifierBundle getIdentifierBundle(ServletRequest request,
            HttpSession session, ServletContext context) {
        IdentifierBundle ib = new ArrayIdentifierBundle();
        ib.add( RoleBasedPolicy.AuthRole.ANYBODY);
        
        if( session != null ){
            LoginFormBean f = (LoginFormBean) session.getAttribute( "loginHandler" );
            try{
                if( f != null && Integer.parseInt( f.getLoginRole() ) >=  LoginFormBean.CURATOR){
                    ib.add(new CuratorEditingId(f.getLoginRole(),f.getUserURI()));
                    ib.add(AuthRole.CURATOR);
                }
            }catch(NumberFormatException th){}            
        }

        return ib;        
    }

    public static class CuratorEditingId extends RoleIdentifier {
        final String role;
        final String uri;
        
        public CuratorEditingId( String role, String uri) {
            this.role = role;
            this.uri = uri;
        }
        
        public String getRole() { return role; }
        
        public String getUri(){ return uri; }
        
        public String toString(){ return uri; }
    }
}
