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

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vedit.util.FormUtils;
import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.beans.Classes2Classes;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.Classes2ClassesDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;

public class Classes2ClassesRetryController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(Classes2ClassesRetryController.class.getName());

    public void doPost (HttpServletRequest req, HttpServletResponse response) {
    	VitroRequest request = new VitroRequest(req);
        if(!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request,response);
        } catch (Exception e) {
            log.error("Classes2ClassesRetryController encountered exception calling super.doGet()");
        }

        //create an EditProcessObject for this and put it in the session
        EditProcessObject epo = super.createEpo(request);

        String action = null;
        if (epo.getAction() == null) {
            action = "insert";
            epo.setAction("insert");
        } else {
            action = epo.getAction();
        }

        Classes2ClassesDao c2cDao = getWebappDaoFactory().getClasses2ClassesDao();
        VClassDao vcDao = getWebappDaoFactory().getVClassDao();
        epo.setDataAccessObject(c2cDao);
        Classes2Classes objectForEditing = new Classes2Classes();

        String superclassURIstr = request.getParameter("SuperclassURI");
        String subclassURIstr = request.getParameter("SubclassURI");
        if (superclassURIstr != null && superclassURIstr.length()>0)
            objectForEditing.setSuperclassURI(superclassURIstr);
        if (subclassURIstr != null && subclassURIstr.length()>0)
            objectForEditing.setSubclassURI(subclassURIstr);

        epo.setOriginalBean(objectForEditing);

        populateBeanFromParams(objectForEditing, request);

        HashMap hash = new HashMap();
        hash.put("SuperclassURI", FormUtils.makeOptionListFromBeans(vcDao.getAllVclasses(),"URI","LocalNameWithPrefix",objectForEditing.getSuperclassURI(),null));
        hash.put("SubclassURI", FormUtils.makeOptionListFromBeans(vcDao.getAllVclasses(),"URI","LocalNameWithPrefix",objectForEditing.getSubclassURI(),null));

        FormObject foo = new FormObject();
        foo.setOptionLists(hash);

        epo.setFormObject(foo);

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
        request.setAttribute("scripts","/templates/edit/formBasic.js");
        String modeStr = request.getParameter("opMode");
        if (modeStr != null && modeStr.equals("disjointWith")) {
        	request.setAttribute("editAction","classes2ClassesOp");
        	request.setAttribute("formJsp","/templates/edit/specific/disjointClasses_retry.jsp");
        	request.setAttribute("title","Disjointness Axiom Editing Form");
        } else if (modeStr != null && modeStr.equals("equivalentClass")){
        	request.setAttribute("editAction","classes2ClassesOp");
        	request.setAttribute("formJsp","/templates/edit/specific/equivalentClasses_retry.jsp");
        	request.setAttribute("title","Equivalent Class Editing Form");
        } else {
        	request.setAttribute("formJsp","/templates/edit/specific/class2classes_retry.jsp");
        	request.setAttribute("title","Super/Subclass Editing Form");
        }
        request.setAttribute("_action",action);
        request.setAttribute("unqualifiedClassName","Classes2Classes");
        setRequestAttributes(request,epo);

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("Classes2ClassesRetryController could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }

    public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }

}
