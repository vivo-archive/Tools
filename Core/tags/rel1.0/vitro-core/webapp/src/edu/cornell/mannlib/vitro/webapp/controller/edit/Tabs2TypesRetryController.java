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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vedit.util.FormUtils;
import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.beans.Tab;
import edu.cornell.mannlib.vitro.webapp.beans.TabVClassRelation;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.TabDao;
import edu.cornell.mannlib.vitro.webapp.dao.TabVClassRelationDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassGroupDao;

public class Tabs2TypesRetryController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(Tabs2TypesRetryController.class.getName());

    public void doPost (HttpServletRequest req, HttpServletResponse response) {

    	VitroRequest request = new VitroRequest(req);
    	
        if(!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request,response);
        } catch (Exception e) {
            log.error("Tabs2TypesRetryController encountered exception calling super.doGet()");
        }

        //create an EditProcessObject for this and put it in the session
        EditProcessObject epo = super.createEpo(request);

        //set portal flag to current portal
        Portal currPortal = (Portal) request.getAttribute("portalBean");
        int currPortalId = 1;
        if (currPortal != null) {
            currPortalId = currPortal.getPortalId();
        }

        String action = "insert";

        TabVClassRelationDao t2tDao = getWebappDaoFactory().getTabs2TypesDao();
        epo.setDataAccessObject(t2tDao);
        TabDao tDao = getWebappDaoFactory().getTabDao();
        VClassGroupDao cgDao = getWebappDaoFactory().getVClassGroupDao();

        TabVClassRelation objectForEditing = null;

        if (request.getParameter("id") != null) {
            int id = Integer.parseInt(request.getParameter("id"));
            if (id > 0) {
                try {
                    // objectForEditing = t2tDao.getTabs2TypesById(id);
                    action = "update";
                } catch (NullPointerException e) {
                    log.error("Need to implement 'record not found' error message.");
                }
            }
        } else {
            objectForEditing = new TabVClassRelation();
            String vClassIdStr = request.getParameter("vClassId");
            if (vClassIdStr != null) {
                try {
                    objectForEditing.setVClassURI(vClassIdStr);
                } catch (NumberFormatException e) {
                    // not really an integer.
                }
            }
            String tabIdStr = request.getParameter("tabId");
            if (tabIdStr != null) {
                try {
                    objectForEditing.setTabId(Integer.decode(tabIdStr));
                } catch (NumberFormatException e) {
                    // not really an integer.
                }
            }
        }

        epo.setOriginalBean(objectForEditing);

        // populateBeanFromParams(objectForEditing, request);

        //set up any listeners
        //List changeListenerList = new LinkedList();
        //epo.setChangeListenerList(changeListenerList);

        //set the getMethod so we can retrieve a new bean after we've inserted it
//      try {
//          Class[] args = new Class[1];
//          args[0] = int.class;
//          epo.setGetMethod(t2tDao.getClass().getDeclaredMethod("getTabs2TypesById",args));
//      } catch (NoSuchMethodException e) {
//          log.error("Tabs2TypesRetryController could not find the getTabs2TypesById method in the facade");
//      }

        HashMap optionMap = new HashMap();
        List<Tab> tabList = tDao.getAllAutolinkableTabs(currPortalId);
        Collections.sort(tabList);
        optionMap.put("TabId", FormUtils.makeOptionListFromBeans(tabList, "TabId", "Title", request.getParameter("TabId"), null));
        List classGroups = cgDao.getPublicGroupsWithVClasses(true); // order by displayRank
        Iterator classGroupIt = classGroups.iterator();
        ListOrderedMap optGroupMap = new ListOrderedMap();
        while (classGroupIt.hasNext()) {
            VClassGroup group = (VClassGroup)classGroupIt.next();
            List classes = group.getVitroClassList();
            if (classes.size() > 0)
            optGroupMap.put(group.getPublicName(),FormUtils.makeOptionListFromBeans(classes,"URI","Name",objectForEditing.getVClassURI(),null,false));
        }
        optionMap.put("VClassId", optGroupMap);
        FormObject foo = new FormObject();
        foo.setOptionLists(optionMap);

        epo.setFormObject(foo);

        String html = FormUtils.htmlFormFromBean(objectForEditing,action,foo);

        Portal portal = (new VitroRequest(request)).getPortal();
        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("formHtml",html);
        request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
        request.setAttribute("formJsp","/templates/edit/specific/tabs2types_retry.jsp");
        request.setAttribute("scripts","/templates/edit/formBasic.js");
        request.setAttribute("title","Tab-VClass Autolink Editing Form");
        request.setAttribute("_action",action);
        request.setAttribute("unqualifiedClassName","Tabs2Types");
        setRequestAttributes(request,epo);

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("Tabs2TypesRetryController could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }

    public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }

}
