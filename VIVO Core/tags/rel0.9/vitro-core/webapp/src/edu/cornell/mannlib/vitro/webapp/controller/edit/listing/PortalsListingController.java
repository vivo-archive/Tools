package edu.cornell.mannlib.vitro.webapp.controller.edit.listing;

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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.PortalDao;

public class PortalsListingController extends BaseEditController {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        VitroRequest vrequest = new VitroRequest(request);
        Portal portal = vrequest.getPortal();

        if(!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request, response);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        PortalDao dao = getWebappDaoFactory().getPortalDao();

        Collection portals = dao.getAllPortals();

        ArrayList results = new ArrayList();
        results.add("XX");
        results.add("ID number");
        results.add("Portal");
        results.add("");
        

        if (portals != null) {
            Iterator portalIt = portals.iterator();
            while (portalIt.hasNext()) {
                Portal p = (Portal) portalIt.next();
                results.add("XX");
                results.add(Integer.toString(p.getPortalId()));
                if (p.getAppName() != null)
                    try {
                        String pName = (p.getAppName()==null || p.getAppName().length()==0) ? Integer.toString(p.getPortalId()) : p.getAppName();
                        results.add("<a href=\"./editForm?id="+p.getPortalId()+"&amp;controller=Portal&amp;home="+portal.getPortalId()+"\">"+pName+"</a>");
                    } catch (Exception e) {
                        results.add(p.getAppName());
                    }
                else
                    results.add("");
                StringBuffer portalPath = (new StringBuffer()).append(request.getContextPath());
                if (p.getUrlprefix() != null) {
                	portalPath.append("/"+p.getUrlprefix());
                } else {
                	portalPath.append("/?home=").append(p.getPortalId());
                }
                results.add("<a href=\""+portalPath.toString()+"\">visit this portal</a>");
            }
            request.setAttribute("results",results);
        }

        request.setAttribute("columncount",new Integer(4));
        request.setAttribute("suppressquery","true");
        request.setAttribute("title","Portals");
        request.setAttribute("portalBean",portal);
        request.setAttribute("bodyJsp", Controllers.HORIZONTAL_JSP);
        request.setAttribute("horizontalJspAddButtonUrl", Controllers.RETRY_URL);
        request.setAttribute("horizontalJspAddButtonText", "Add new portal");
        request.setAttribute("horizontalJspAddButtonControllerParam", "Portal");
        request.setAttribute("home", portal.getPortalId());
        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        try {
            rd.forward(request,response);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
	
}
