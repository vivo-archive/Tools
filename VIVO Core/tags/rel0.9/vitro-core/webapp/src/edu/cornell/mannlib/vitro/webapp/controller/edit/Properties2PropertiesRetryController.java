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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vedit.util.FormUtils;
import edu.cornell.mannlib.vitro.webapp.beans.Classes2Classes;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.Classes2ClassesDao;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;

public class Properties2PropertiesRetryController extends BaseEditController {

	private static final Log log = LogFactory.getLog(Properties2PropertiesRetryController.class.getName());

    public void doGet (HttpServletRequest req, HttpServletResponse response) {
    	VitroRequest request = new VitroRequest(req);
        if(!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request,response);
        } catch (Exception e) {
            log.error(this.getClass().getName()+" encountered exception calling super.doGet()");
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

        ObjectPropertyDao opDao = getWebappDaoFactory().getObjectPropertyDao();
        DataPropertyDao dpDao = getWebappDaoFactory().getDataPropertyDao();
        epo.setDataAccessObject(opDao);
        
        List propList = ("data".equals(request.getParameter("propertyType"))) 
    	? dpDao.getAllDataProperties()
    	: opDao.getAllObjectProperties();
        
    	Collections.sort(propList);
    	
        String superpropertyURIstr = request.getParameter("SuperpropertyURI");
        String subpropertyURIstr = request.getParameter("SubpropertyURI");
        
        HashMap hash = new HashMap();
    	hash.put("SuperpropertyURI", FormUtils.makeOptionListFromBeans(propList,"URI","LocalNameWithPrefix",superpropertyURIstr,null));
        hash.put("SubpropertyURI", FormUtils.makeOptionListFromBeans(propList,"URI","LocalNameWithPrefix",subpropertyURIstr,null));
        
        FormObject foo = new FormObject();
        foo.setOptionLists(hash);

        epo.setFormObject(foo);
        
        request.setAttribute("operation","add");
        
        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
        request.setAttribute("scripts","/templates/edit/formBasic.js");
        String modeStr = request.getParameter("opMode");
        if (modeStr != null && ( modeStr.equals("superproperty") || modeStr.equals("subproperty") || modeStr.equals("equivalentProperty") ) ) {
        	request.setAttribute("editAction","props2PropsOp");
        	request.setAttribute("formJsp","/templates/edit/specific/properties2properties_retry.jsp");
        	request.setAttribute("title", (modeStr.equals("superproperty") ? "Add Superproperty" : modeStr.equals("equivalentProperty") ? "Add Equivalent Property" : "Add Subproperty") );
        } 
        request.setAttribute("opMode", modeStr);
        
        request.setAttribute("_action",action);
        setRequestAttributes(request,epo);

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error(this.getClass().getName() + " could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }
	
}
