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

package edu.cornell.mannlib.vitro.webapp.controller.edit;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;
import edu.cornell.mannlib.vedit.beans.Option;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vedit.util.FormUtils;
import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.beans.Tab;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.TabDao;

public class Tabs2TabsRetryController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(Tabs2TabsRetryController.class.getName());

    public void doPost (HttpServletRequest req, HttpServletResponse response) {

    	VitroRequest request = new VitroRequest(req);
    	
        if(!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request,response);
        } catch (Exception e) {
            log.error("Tabs2TabsRetryController encountered exception calling super.doGet()");
        }

        VitroRequest vreq = new VitroRequest(request);
        Portal portal = vreq.getPortal();

        //create an EditProcessObject for this and put it in the session
        EditProcessObject epo = super.createEpo(request);
        FormObject foo = new FormObject();
        epo.setFormObject(foo);

        String action = "insert";

        TabDao tDao = getWebappDaoFactory().getTabDao();

        Tab child = null;
        Tab parent = null;

        if (request.getParameter("ChildId") != null) {
            try {
                int childId = Integer.decode(request.getParameter("ChildId"));
                child = tDao.getTab(childId);
            } catch (NumberFormatException e) {}
        }

        if (request.getParameter("ParentId") != null) {
            try {
                int parentId = Integer.decode(request.getParameter("ParentId"));
                parent = tDao.getTab(parentId);
            } catch (NumberFormatException e) {}
        }

        HashMap hash = new HashMap();
        foo.setOptionLists(hash);
        if (parent != null ) {
            hash.put("ChildId", FormUtils.makeOptionListFromBeans(tDao.getTabsForPortalByTabtypes(portal.getPortalId(),false, parent.getTabtypeId()),"TabId","Title",null,null));
            List parentList = new LinkedList();
            parentList.add(new Option(Integer.toString(parent.getTabId()),parent.getTitle(),true));
            hash.put("ParentId", parentList);
        } else if (child != null){
            hash.put("ParentId", FormUtils.makeOptionListFromBeans(tDao.getTabsForPortalByTabtypes(portal.getPortalId(),true,child.getTabtypeId()),"TabId","Title",null,null));
            List childList = new LinkedList();
            childList.add(new Option(Integer.toString(child.getTabId()),child.getTitle(),true));
            hash.put("ChildId", childList);
        }

        epo.setFormObject(foo);

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);

        request.setAttribute("epoKey",epo.getKey());
        request.setAttribute("epo",epo);
        request.setAttribute("bodyJsp","/templates/edit/specific/tabs2tabs_retry.jsp");
        request.setAttribute("scripts","/templates/edit/formBasic.js");
        request.setAttribute("title","Super/Subtab Editing Form");
        request.setAttribute("_action",action);
        setRequestAttributes(request,epo);

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("Tabs2TabsRetryController could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }

    public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }

}
