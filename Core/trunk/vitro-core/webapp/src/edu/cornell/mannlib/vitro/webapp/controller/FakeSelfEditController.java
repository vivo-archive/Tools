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

package edu.cornell.mannlib.vitro.webapp.controller;

import java.util.Enumeration;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vedit.beans.LoginFormBean;
//import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.FakeSelfEditingIdentifierFactory;

public class FakeSelfEditController extends VitroHttpServlet {
	
	private static final Log log = LogFactory.getLog(FakeSelfEditController.class.getName());
	
    public void doGet( HttpServletRequest request, HttpServletResponse response )
    	throws IOException, ServletException {
        
//        if (!checkLoginStatus(request, response)) {
//            return;
//        }
        
        String redirectUrl = null;       
        String netid = null;
        String msg = null;
        
        try {
            super.doGet(request,response);
            VitroRequest vreq = new VitroRequest(request);
            HttpSession session = request.getSession(true);
            
            Object obj = vreq.getSession().getAttribute("loginHandler");        
            LoginFormBean loginHandler = null;
            if( obj != null && obj instanceof LoginFormBean )
                loginHandler = ((LoginFormBean)obj);
            
            // Not logged in to site admin
            if ( loginHandler == null ||
                 ! "authenticated".equalsIgnoreCase(loginHandler.getLoginStatus()) ||
                 Integer.parseInt(loginHandler.getLoginRole()) <= LoginFormBean.CURATOR ) {       
      
                session.setAttribute("postLoginRequest",
                        vreq.getRequestURI()); //+( vreq.getQueryString()!=null?('?' + vreq.getQueryString()):"" ));
                // Redirect to site admin login page
                redirectUrl=request.getContextPath() + Controllers.LOGIN + "?login=block";
            }
            
            // Logged in to site admin
            else {
                // Handle form submission
                // Form to use netid submitted 
                if(  vreq.getParameter("force") != null ){        
                    VitroRequestPrep.forceToSelfEditing(request);
                    netid = request.getParameter("netid");
                    FakeSelfEditingIdentifierFactory.clearFakeIdInSession( session );
                    FakeSelfEditingIdentifierFactory.putFakeIdInSession( netid , session );
                    // Redirect to user's entity page
                    redirectUrl = request.getContextPath() + Controllers.ENTITY + "?netid=" + netid;
                }
            
                // Form to stop using netid submitted
                else if ( request.getParameter("stopfaking") != null) {
                    VitroRequestPrep.forceOutOfSelfEditing(request);
                    FakeSelfEditingIdentifierFactory.clearFakeIdInSession( session );  
                    // Redirect to home page
                    redirectUrl = request.getContextPath() + "/"; 
                }
            }
            
            if (redirectUrl != null) {
                response.sendRedirect(redirectUrl);
                return;
            }
            
            // On page, form not yet submitted
            // Check if already logged in from previous form submission
            netid = (String)session.getAttribute(FakeSelfEditingIdentifierFactory.FAKE_SELF_EDIT_NETID);
            
            // Already logged in from a previous request
            if ( netid != null ) {
                msg = "You are testing self-editing as '" + netid + "'.";                   
            }
            // Not logged in
            else {
                msg = "You have not configured a netid to test self-editing.";
            }                  
            
            request.setAttribute("msg", msg); 
            
            request.setAttribute("title", "Self-Edit Test");
            request.setAttribute("bodyJsp", "/admin/fakeselfedit.jsp");
            
            RequestDispatcher rd =
                request.getRequestDispatcher(Controllers.BASIC_JSP);
            rd.forward(request, response);

        } catch (Throwable e) {
            log.error("FakeSelfEditController could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    	doGet(request, response);
    }


}
