package edu.cornell.mannlib.vitro.webapp.flags;

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

import edu.cornell.mannlib.vedit.beans.LoginFormBean;
import edu.cornell.mannlib.vitro.webapp.flags.AuthFlag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by IntelliJ IDEA.
 * User: bdc34
 * Date: Apr 5, 2007
 * Time: 11:12:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class RequestToAuthFlag {
    public static AuthFlag makeAuthFlag(HttpServletRequest request){
        AuthFlag authFlag = new AuthFlag();
        authFlag.setUserSecurityLevel(0);

        HttpSession currentSession = request.getSession();
        if( currentSession == null )
            return authFlag;

        LoginFormBean f = (LoginFormBean) currentSession.getAttribute( "loginHandler" );
        if (f!=null) {
            if (f.getLoginStatus().equals("authenticated")) { // test if session is still valid
                if (currentSession.getId().equals(f.getSessionId())) {
                    if (request.getRemoteAddr().equals(f.getLoginRemoteAddr())) {
                        authFlag.setUserSecurityLevel(Integer.parseInt(f.getLoginRole()));
                    }
                }
            }
        }

        return authFlag;
    }
}
