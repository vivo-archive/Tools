package edu.cornell.mannlib.vitro.webapp.controller;

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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;

/**
 * Controller for Terms of Use page
 * @author bjl23
 */
public class TermsOfUseController  extends VitroHttpServlet{
    private static final long serialVersionUID = 1L;

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public void doGet( HttpServletRequest request, HttpServletResponse response )
    throws IOException, ServletException {
        try {
            super.doGet(request,response);
            VitroRequest vreq = new VitroRequest(request);

            ApplicationBean appBean=vreq.getAppBean();
            Portal portalBean=vreq.getPortal();

            request.setAttribute("rootBreadCrumbAnchorOrAppName", portalBean.getRootBreadCrumbAnchor()==null || portalBean.getRootBreadCrumbAnchor().equals("")?portalBean.getAppName():portalBean.getRootBreadCrumbAnchor());
            request.setAttribute("copyrightAnchor", portalBean.getCopyrightAnchor());
            //request.setAttribute("rootLogotypeTitle", appBean.getRootLogotypeTitle()); THIS IS NOT POPULATED
            request.setAttribute("appName", portalBean.getAppName());

            request.setAttribute("title", portalBean.getAppName()+" Terms of Use");
            request.setAttribute("bodyJsp", "/usageTerms.jsp");// <<< this is where the body gets set
            request.setAttribute("portalBean",portalBean);

            RequestDispatcher rd =
                request.getRequestDispatcher(Controllers.BASIC_JSP);
            rd.forward(request, response);

        } catch (Throwable e) {
            // This is how we use an error.jsp
            //it expects javax.servlet.jsp.jspException to be set to the
            //exception so that it can display the info out of that.
            request.setAttribute("javax.servlet.jsp.jspException", e);
            RequestDispatcher rd = request.getRequestDispatcher("/error.jsp");
            rd.include(request, response);
        }
    }
}
