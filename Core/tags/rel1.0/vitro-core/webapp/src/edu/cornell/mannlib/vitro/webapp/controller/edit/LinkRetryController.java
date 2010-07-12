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
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Link;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.LinksDao;
import edu.cornell.mannlib.vitro.webapp.dao.LinktypeDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

public class LinkRetryController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(LinkRetryController.class.getName());

    public void doPost (HttpServletRequest req, HttpServletResponse response) {
    	
    	VitroRequest request = new VitroRequest(req);
        if (!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request,response);
        } catch (Exception e) {
            log.error("LinkRetryController encountered exception calling super.doGet()");
        }

        //create an EditProcessObject for this and put it in the session
        EditProcessObject epo = super.createEpo(request);
        request.setAttribute("epo",epo);

        /*for testing*/
        Link testMask = new Link();
        epo.setBeanClass(Link.class);
        epo.setBeanMask(testMask);

        String action = "insert";

        WebappDaoFactory wadf = (getAssertionsWebappDaoFactory() != null) ? getAssertionsWebappDaoFactory() : getWebappDaoFactory();
        
        LinksDao lDao = wadf.getLinksDao();
        epo.setDataAccessObject(lDao);
        LinktypeDao ltDao = wadf.getLinktypeDao();
        IndividualDao eDao = wadf.getIndividualDao();

        Link linkForEditing = null;
        if (!epo.getUseRecycledBean()){
            String linkUri = request.getParameter("uri");
            if (linkUri != null) {
                try {
                    linkForEditing = lDao.getLinkByURI(linkUri);
                    action = "update";
                } catch (NullPointerException e) {
                    log.error("Need to implement 'record not found' error message.");
                }
            } else {
                linkForEditing = new Link();
                if (request.getParameter("entityUri") != null) {
                    try {
                        linkForEditing.setEntityURI(request.getParameter("entityUri"));
                    } catch (NumberFormatException e) {
                        // oh well; it was worth a shot
                    }
                }
            }
            epo.setOriginalBean(linkForEditing);
        } else {
            linkForEditing = (Link) epo.getNewBean();
            action = "update";
            log.error("using newBean");
        }

        //set up any listeners

        //set portal flag to current portal
        Portal currPortal = (Portal) request.getAttribute("portalBean");
        int currPortalId = 1;
        if (currPortal != null) {
            currPortalId = currPortal.getPortalId();
        }

        //set the getMethod so we can retrieve a new bean after we've inserted it
        try {
            Class[] args = new Class[1];
            args[0] = int.class;
            epo.setGetMethod(lDao.getClass().getDeclaredMethod("getLinkById",args));
        } catch (NoSuchMethodException e) {
            log.error("LinkRetryController could not find the namespaceById method in the facade");
        }


        FormObject foo = new FormObject();
        HashMap OptionMap = new HashMap();
        List entityList = new LinkedList();
        if (linkForEditing.getEntityURI() != null) {
            Individual theEnt = eDao.getIndividualByURI(linkForEditing.getEntityURI());
            entityList.add(new Option(theEnt.getURI(),theEnt.getName(),true));
        } else {
            entityList.add(new Option ("", "Error: the entity must be specified", true));
        }
        OptionMap.put("EntityId",entityList);
        List linkTypeOptList = FormUtils.makeOptionListFromBeans(ltDao.getAllLinktypes(), "URI", "Type", linkForEditing.getTypeURI(),"");
        linkTypeOptList.add(0,new Option("","unspecified"));
        OptionMap.put("TypeURI", linkTypeOptList);
        foo.setOptionLists(OptionMap);
        foo.setErrorMap(epo.getErrMsgMap());

        epo.setFormObject(foo);

        String html = FormUtils.htmlFormFromBean(linkForEditing,action,foo,epo.getBadValueMap());

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("formHtml",html);
        request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
        request.setAttribute("formJsp","/templates/edit/specific/links_retry.jsp");
        request.setAttribute("scripts","/templates/edit/formBasic.js");
        request.setAttribute("title","Hyperlink Editing Form");
        request.setAttribute("_action",action);
        request.setAttribute("unqualifiedClassName","Link");
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

}
