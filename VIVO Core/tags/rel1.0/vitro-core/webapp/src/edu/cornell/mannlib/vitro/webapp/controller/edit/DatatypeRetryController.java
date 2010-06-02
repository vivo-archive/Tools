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
import edu.cornell.mannlib.vitro.webapp.beans.Datatype;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DatatypeDao;


public class DatatypeRetryController extends BaseEditController {

	private static final Log log = LogFactory.getLog(DatatypeRetryController.class.getName());
	
    public void doPost (HttpServletRequest req, HttpServletResponse response) {
    	
    	VitroRequest request = new VitroRequest(req);
        if (!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request,response);
        } catch (Exception e) {
            log.error("DatatypeRetryController encountered exception calling super.doGet()");
        }

        //create an EditProcessObject for this and put it in the session
        EditProcessObject epo = super.createEpo(request);

        DatatypeDao dDao = getWebappDaoFactory().getDatatypeDao();
        epo.setDataAccessObject(dDao);
        Datatype objectForEditing = null;
        String action = "";

        if (request.getParameter("id") != null) {
            int id = Integer.parseInt(request.getParameter("id"));

            if (id > 0) {
                try {
                    log.debug("Trying to retrieve datatype "+id);
                    objectForEditing = dDao.getDatatypeById(id);
                    action = "update";
                } catch (NullPointerException e) {
                    log.error("Need to implement 'record not found' error message.");
                }
            }
        } else {
            action = "insert";
            objectForEditing = new Datatype();
        }

        epo.setOriginalBean(objectForEditing);

        //put this in the parent class?
        //make a simple mask for the class's id
        Object[] simpleMaskPair = new Object[2];
        simpleMaskPair[0]="Id";
        simpleMaskPair[1]=Integer.valueOf(objectForEditing.getId());
        epo.getSimpleMask().add(simpleMaskPair);

        //set the getMethod so we can retrieve a new bean after we've inserted it
        try {
            Class[] args = new Class[1];
            args[0] = int.class;
            epo.setGetMethod(dDao.getClass().getDeclaredMethod("getDatatypeById",args));
        } catch (NoSuchMethodException e) {
            log.error("EntityRetryController could not find the entityById method in the facade");
        }

        epo.setPostInsertPageForwarder(new DatatypeInsertPageForwarder());
        epo.setPostDeletePageForwarder(new DatatypeDeletePageForwarder());

        FormObject foo = new FormObject();
        epo.setFormObject(foo);
        String html = FormUtils.htmlFormFromBean(objectForEditing,action,foo);
        //for now, this is also making the value hash - need to separate this out


        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("formHtml",html);
        request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
        request.setAttribute("scripts","/templates/edit/formBasic.js");
        request.setAttribute("formJsp","/templates/edit/specific/datatype_retry.jsp");
        request.setAttribute("title","Datatype Editing Form");
        request.setAttribute("_action",action);
        request.setAttribute("unqualifiedClassName","Datatype");
        setRequestAttributes(request,epo);

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("VclassRetryContro" +
                    "ller could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }

    public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }

    class DatatypeInsertPageForwarder implements PageForwarder {
        public void doForward(HttpServletRequest request, HttpServletResponse response, EditProcessObject epo){
            int portalId = (new VitroRequest(request)).getPortal().getPortalId();
            String newDtpUrl = "fetch?home="+portalId+"&queryspec=private_datatypev&postGenLimit=-1&linkwhere=datatypes.id=";
            Datatype dtp = (Datatype) epo.getNewBean();
            newDtpUrl += dtp.getId();
            try {
                response.sendRedirect(newDtpUrl);
            } catch (IOException ioe) {
                log.error("DatatypeInsertPageForwarder could not send redirect.");
            }
        }
    }

    class DatatypeDeletePageForwarder implements PageForwarder {
        public void doForward(HttpServletRequest request, HttpServletResponse response, EditProcessObject epo){
            int portalId = (new VitroRequest(request)).getPortal().getPortalId();
            String newDtpUrl = "fetch?home="+portalId+"&queryspec=private_datatypes";
            Datatype dtp = (Datatype) epo.getNewBean();
            try {
                response.sendRedirect(newDtpUrl);
            } catch (IOException ioe) {
                log.error("DatatypeInsertPageForwarder could not send redirect.");
            }
        }
    }

}
