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

import edu.cornell.mannlib.vedit.beans.LoginFormBean;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.search.beans.VitroQuery;
import edu.cornell.mannlib.vitro.webapp.search.beans.VitroQueryWrapper;
import edu.cornell.mannlib.vitro.webapp.web.EntityWebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * for testing styles and JSPs
 *
 * @author bdc34
 *
 */
public class TestController extends VitroHttpServlet {
    private static final Log log = LogFactory.getLog(EntityController.class.getName());

    /**
     *
     * @author bdc34
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res )
    throws IOException, ServletException {
        try {
            super.doGet(req, res);

            VitroRequest vreq = new VitroRequest(req);

            Portal portal = vreq.getPortal();
            vreq.setAttribute("portal",String.valueOf(portal));

            String body = vreq.getParameter("bodyJsp");
            if( body != null )
                vreq.setAttribute("bodyJsp" , body);
            else
                vreq.setAttribute("bodyJsp", Controllers.EMPTY);


            //set title before we do the highlighting so we don't get markup in it.
            vreq.setAttribute("title","Test Page");

            String css = "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\""
                + portal.getThemeDir() + "css/entity.css\"/>\n"
                + "<script language='JavaScript' type='text/javascript' src='js/toggle.js'></script>";
            vreq.setAttribute("css",css);

            RequestDispatcher rd = vreq.getRequestDispatcher(Controllers.BASIC_JSP);
            rd.forward(req,res);
        } catch (Throwable e) {
            log.error(e);
            req.setAttribute("javax.servlet.jsp.jspException",e);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp");
            rd.forward(req, res);
        }
    }



    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException,IOException {
        doGet(request, response);
    }

}