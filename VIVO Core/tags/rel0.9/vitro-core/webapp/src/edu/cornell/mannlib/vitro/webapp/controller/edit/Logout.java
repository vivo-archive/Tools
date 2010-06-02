package edu.cornell.mannlib.vitro.webapp.controller.edit;

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
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.sql.*;
import java.util.*;

import edu.cornell.mannlib.vedit.beans.LoginFormBean;

import edu.cornell.mannlib.vitro.webapp.beans.User;
import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.UserDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.LogoutEvent;
import edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep;

public class Logout extends HttpServlet /*implements SingleThreadModel*/ {
	
	private static final Log log = LogFactory.getLog(Logout.class.getName());

    public void doPost( HttpServletRequest request, HttpServletResponse response ) {
        try {
        	
        	VitroRequest vreq = new VitroRequest(request);
        	HttpSession session = vreq.getSession();
        	if (session != null) {
	        	UserDao userDao = ((WebappDaoFactory)session.getServletContext().getAttribute("webappDaoFactory")).getUserDao();
	            LoginFormBean f = (LoginFormBean) session.getAttribute( "loginHandler" );
	            if (f != null) {
		            User user = userDao.getUserByUsername(f.getLoginName());
		            if (user == null) {
		            	log.error("Unable to retrieve user " + f.getLoginName() + " from model");
		            } else {
		            	Authenticate.sendLoginNotifyEvent( new LogoutEvent( user.getURI() ), getServletContext(), session );
		            }
	            }
	            session.invalidate();
        	}
            response.sendRedirect("./");
            
            /*
            LoginFormBean f = (LoginFormBean) session.getAttribute( "loginHandler" );
            //reset the login bean properties
            f.setLoginStatus( "logged out" );
            f.setLoginRole( "1" );
            // f.setLoginName( "" ); leave so users can see who they last logged in as
            f.setLoginPassword( "" );
            f.setErrorMsg( "loginPassword", "" ); // remove any error messages
            f.setErrorMsg( "loginUsername", "" ); // remove any error messages
            f.setEmailAddress( "reset" );
            f.setSessionId( "" );
            
            // VitroRequestPrep.forceOutOfCuratorEditing(request);
            //CuratorEditingUriFactory.clearFakeIdInSession( session );    
			*/
            
        } catch (Exception ex) {
            log.error( ex.getMessage() );
            ex.printStackTrace();
        }
    }
    
    public void doGet( HttpServletRequest request, HttpServletResponse response ) {
    	doPost(request, response);
    }
}

