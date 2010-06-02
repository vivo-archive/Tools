package edu.cornell.mannlib.vitro.webapp.auth.identifier;

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
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import edu.cornell.mannlib.vedit.beans.LoginFormBean;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

/**
 * Check to see if the User is logged in, find Individuals that the User mayEditAs,
 * and and those Individuals as identifiers.
 *  
 * @author bdc34
 *
 */
public class UserToIndIdentifierFactory implements IdentifierBundleFactory {

    public IdentifierBundle getIdentifierBundle(
            ServletRequest request,
            HttpSession session, 
            ServletContext context) {        
        if( session != null ){
            // is the request logged in as a User?
            LoginFormBean loginBean = (LoginFormBean) session.getAttribute("loginHandler");
            if( loginBean != null && "authenticated".equals(loginBean.getLoginStatus() )){
                String userURI = loginBean.getUserURI();

                WebappDaoFactory wdf = (WebappDaoFactory)context.getAttribute("webappDaoFactory");
                
                // get Individuals that the User mayEditAs
                List<String> mayEditAsUris = 
                    wdf.getUserDao().getIndividualsUserMayEditAs(userURI);

                // make self editing Identifiers for those Individuals
                IdentifierBundle idb = new ArrayIdentifierBundle();
                idb.add( new UserIdentifier(userURI,mayEditAsUris) );
                
                //Also make a self-editing identifier.
                //There is not need for SelfEditingIdentifierFactory because SelfEditing
                //identifiers are created here.              
                for( String personUri : mayEditAsUris){
                    if( personUri != null ){
                        Individual person = wdf.getIndividualDao().getIndividualByURI(personUri);
                        if( person != null ){
                            idb.add( new SelfEditingIdentifierFactory.SelfEditing(person,null) );
                        }
                    }
                }
                return idb;            
            }
        }
        return null;
    }
    
    public static List<String> getIndividualsForUser(IdentifierBundle ids) {
        if( ids == null )
            return Collections.EMPTY_LIST;
        
        //find the user id
        List<String> uris = new ArrayList<String>();
        for( Identifier id : ids ){
            if( id instanceof UserIdentifier){
                uris.addAll( ((UserIdentifier)id).getMayEditAsURIs() );
            }
        }
        return uris;
    }
            
    public class UserIdentifier implements Identifier {
        private final String userURI;
        private final List<String> mayEditAsURIs;
        public UserIdentifier(String userURI, List<String> mayEditAsURIs) {
            super();
            this.userURI = userURI;
            this.mayEditAsURIs = Collections.unmodifiableList(mayEditAsURIs);
        }
        public String getUserURI() {
            return userURI;
        }
        public List<String> getMayEditAsURIs() {
            return mayEditAsURIs;
        }        
    }
}
