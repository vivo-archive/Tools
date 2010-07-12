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

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.beans.TabIndividualRelation;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.TabIndividualRelationDao;

public class TabIndividualRelationOperationController extends
        BaseEditController {
	
	private static final Log log = LogFactory.getLog(TabIndividualRelationOperationController.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse response) {
    	
    	VitroRequest request = new VitroRequest(req);
    	String defaultLandingPage = getDefaultLandingPage(request);
    	
        if(!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request,response);
        } catch (Exception e) {
            log.error(this.getClass().getName()+" encountered exception calling super.doGet()");
        }

        HashMap epoHash = null;
        EditProcessObject epo = null;
        try {
            epoHash = (HashMap) request.getSession().getAttribute("epoHash");
            epo = (EditProcessObject) epoHash.get(request.getParameter("_epoKey"));
        } catch (NullPointerException e) {
            //session or edit process expired
            try {
                response.sendRedirect(defaultLandingPage);
            } catch (IOException f) {
                e.printStackTrace();
            }
            return;
        }

        if (epo == null) {
            log.error("null epo");
            try {
                response.sendRedirect(defaultLandingPage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        TabIndividualRelationDao dao = getWebappDaoFactory().getTabs2EntsDao();

        try {
            if (request.getParameter("operation").equals("remove")) {
                String[] tirURIstrs = request.getParameterValues("TirURI");
                if ((tirURIstrs != null) && (tirURIstrs.length > 0)) {
                        for (int i=0; i<tirURIstrs.length; i++) {
                            TabIndividualRelation tir = dao.getTabIndividualRelationByURI(tirURIstrs[i]);
                            dao.deleteTabIndividualRelation(tir);
                        }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        //if no page forwarder was set, just go back to referring page:
        //the referer stuff all will be changed so as not to rely on the HTTP header
        String referer = request.getHeader("REFERER");
        if (referer == null) {
            try {
                response.sendRedirect(defaultLandingPage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                response.sendRedirect(referer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        doGet(request, response);
    }

}
