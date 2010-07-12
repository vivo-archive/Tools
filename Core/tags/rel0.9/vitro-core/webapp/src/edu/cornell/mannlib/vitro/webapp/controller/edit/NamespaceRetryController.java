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

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vedit.forwarder.PageForwarder;
import edu.cornell.mannlib.vedit.util.FormUtils;
import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.beans.Namespace;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.NamespaceDao;

public class NamespaceRetryController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(NamespaceRetryController.class.getName());

    public void doPost (HttpServletRequest req, HttpServletResponse response) {
    	
    	VitroRequest request = new VitroRequest(req);
        if (!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request,response);
        } catch (Exception e) {
            log.error("NamespaceRetryController encountered exception calling super.doGet()");
            e.printStackTrace();
        }

        //create an EditProcessObject for this and put it in the session
        EditProcessObject epo = super.createEpo(request);

        /*for testing*/
        Namespace testMask = new Namespace();
        epo.setBeanClass(Namespace.class);
        epo.setBeanMask(testMask);

        String action = "insert";
        if (getWebappDaoFactory() == null)
            log.error("null CoreDaoFactory");
        NamespaceDao namespaceDao = getWebappDaoFactory().getNamespaceDao();
        //VitroFacade facade = getFacade();
        //epo.setFacade(facade);

        Namespace namespaceForEditing = null;
        if (!epo.getUseRecycledBean()){
            if (request.getParameter("id") != null) {
                int id = Integer.parseInt(request.getParameter("id"));
                if (id > 0) {
                    try {
                        namespaceForEditing = namespaceDao.getNamespaceById(id);
                        action = "update";
                    } catch (NullPointerException e) {
                        log.error("Need to implement 'record not found' error message.");
                    }
                }
            } else {
                namespaceForEditing = new Namespace();
            }
            epo.setOriginalBean(namespaceForEditing);
        } else {
            namespaceForEditing = (Namespace) epo.getNewBean();
            action = "update";
            log.error("using newBean");
        }

        //make a simple mask for the class's id
        Object[] simpleMaskPair = new Object[2];
        simpleMaskPair[0]="Id";
        simpleMaskPair[1]=Integer.valueOf(namespaceForEditing.getId());
        epo.getSimpleMask().add(simpleMaskPair);

        //set up any listeners

        //set portal flag to current portal
        Portal currPortal = (Portal) request.getAttribute("portalBean");
        int currPortalId = 1;
        if (currPortal != null) {
            currPortalId = currPortal.getPortalId();
        }
        //make a postinsert pageforwarder that will send us to a new class's fetch screen
        epo.setPostInsertPageForwarder(new NamespaceInsertPageForwarder(currPortalId));
        //make a postdelete pageforwarder that will send us to the list of classes

        //set the getMethod so we can retrieve a new bean after we've inserted it
        try {
            Class[] args = new Class[1];
            args[0] = int.class;
            epo.setGetMethod(namespaceDao.getClass().getDeclaredMethod("getNamespaceById",args));
        } catch (NoSuchMethodException e) {
            log.error("NamespaceRetryController could not find the namespaceById method in the facade");
        }


        FormObject foo = new FormObject();

        foo.setErrorMap(epo.getErrMsgMap());

        epo.setFormObject(foo);

        String html = FormUtils.htmlFormFromBean(namespaceForEditing,action,foo,epo.getBadValueMap());

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("formHtml",html);
        request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
        request.setAttribute("formJsp","/templates/edit/specific/namespace_retry.jsp");
        request.setAttribute("scripts","/templates/edit/formBasic.js");
        request.setAttribute("title","Namespace Editing Form");
        request.setAttribute("_action",action);
        request.setAttribute("unqualifiedClassName","Namespace");
        setRequestAttributes(request,epo);

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("NamespaceRetryController could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }

    public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }

    class NamespaceInsertPageForwarder implements PageForwarder {

        private int portalId = 1;

        public NamespaceInsertPageForwarder(int currPortalId) {
            portalId = currPortalId;
        }

        public void doForward(HttpServletRequest request, HttpServletResponse response, EditProcessObject epo){
            String newNamespaceUrl = "fetch?home="+portalId+"&queryspec=private_namespacev&postGenLimit=-1&linkwhere=namespaces.id=";
            Namespace ns = (Namespace) epo.getNewBean();
            newNamespaceUrl += ns.getId();
            try {
                response.sendRedirect(newNamespaceUrl);
            } catch (IOException ioe) {
                log.error("NamespaceInsertPageForwarder could not send redirect.");
            }
        }
    }

}
