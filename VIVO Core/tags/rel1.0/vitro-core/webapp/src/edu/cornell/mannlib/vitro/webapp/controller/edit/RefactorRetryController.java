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
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class RefactorRetryController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(RefactorRetryController.class.getName());

	private void doRenameResource(VitroRequest request, HttpServletResponse response, EditProcessObject epo) {
		epo.setAttribute("modeStr","renameResource");
		epo.setAttribute("_action","insert");
		epo.setAttribute("oldURI",request.getParameter("oldURI"));
		request.setAttribute("editAction","refactorOp");
		request.setAttribute("title","Rename Resource");
		request.setAttribute("formJsp", "/templates/edit/specific/renameResource_retry.jsp");
		request.setAttribute("_cancelButtonDisabled","disabled");
		request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
        request.setAttribute("scripts","/templates/edit/formBasic.js");
	}
	
	private void doMovePropertyStatements(VitroRequest request, HttpServletResponse response, EditProcessObject epo) {
		epo.setAttribute("modeStr","movePropertyStatements");
		String propertyURI = request.getParameter("propertyURI");
		epo.setAttribute("propertyURI",propertyURI);
		epo.setAttribute("propertyType",request.getParameter("propertyType"));
		FormObject foo = new FormObject();
		epo.setFormObject(foo);
		HashMap<String,List<Option>> optMap = new HashMap<String,List<Option>>();
		foo.setOptionLists(optMap);
		List<Option> subjectClassOpts = FormUtils.makeOptionListFromBeans(getWebappDaoFactory().getVClassDao().getAllVclasses(),"URI","Name", null, null);
		subjectClassOpts.add(0,new Option("","? wildcard",true));
		optMap.put("SubjectClassURI", subjectClassOpts);
		optMap.put("ObjectClassURI", subjectClassOpts);
		
		List newPropertyOpts;
		if (epo.getAttribute("propertyType").equals("ObjectProperty"))  {
			List<ObjectProperty> opList = getWebappDaoFactory().getObjectPropertyDao().getAllObjectProperties();
			Collections.sort(opList);
			newPropertyOpts = FormUtils.makeOptionListFromBeans(opList,"URI","LocalNameWithPrefix", null, null);
		} else {
			List<DataProperty> dpList = getWebappDaoFactory().getDataPropertyDao().getAllDataProperties();
			Collections.sort(dpList);
			newPropertyOpts = FormUtils.makeOptionListFromBeans(dpList,"URI","Name", null, null);
		}
		newPropertyOpts.add(new Option("","(move to trash)"));
		optMap.put("NewPropertyURI", newPropertyOpts);				
		
		request.setAttribute("editAction","refactorOp");
		request.setAttribute("title", "Move Property Statements");
		request.setAttribute("formJsp", "/templates/edit/specific/movePropertyStatements_retry.jsp");
		request.setAttribute("_cancelButtonDisabled","disabled");
		request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
        request.setAttribute("scripts","/templates/edit/formBasic.js");
	}
	
	public void doMoveInstances(VitroRequest request, HttpServletResponse response, EditProcessObject epo) {
		epo.setAttribute("modeStr","moveInstances");
		String propertyURI = request.getParameter("VClassURI");
		epo.setAttribute("VClassURI",propertyURI);
		FormObject foo = new FormObject();
		epo.setFormObject(foo);
		HashMap<String,List<Option>> optMap = new HashMap<String,List<Option>>();
		foo.setOptionLists(optMap);
		List<Option> newClassURIopts = FormUtils.makeOptionListFromBeans(getWebappDaoFactory().getVClassDao().getAllVclasses(),"URI","LocalNameWithPrefix", null, null);
		newClassURIopts.add(new Option ("","move to trash"));
		optMap.put("NewVClassURI", newClassURIopts);
		request.setAttribute("editAction","refactorOp");
		request.setAttribute("title", "Move Class Instances");
		request.setAttribute("formJsp", "/templates/edit/specific/moveInstances_retry.jsp");
		request.setAttribute("_cancelButtonDisabled","disabled");
		request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
        request.setAttribute("scripts","/templates/edit/formBasic.js");
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		
	    if(!checkLoginStatus(request,response))
	        return;
	
	    try {
	        super.doGet(request,response);
	    } catch (Exception e) {
	        log.error(this.getClass().getName()+" encountered exception calling super.doGet()");
	    }
	
	    //create an EditProcessObject for this and put it in the session
	    EditProcessObject epo = super.createEpo(request);
	    
	    VitroRequest vreq = new VitroRequest(request);
	    
	    String modeStr = request.getParameter("mode");
	    
	    if (modeStr != null) {
	    	if (modeStr.equals("renameResource")) {
	    		doRenameResource(vreq, response, epo);
	    	} else if (modeStr.equals("movePropertyStatements")) {
	    		doMovePropertyStatements(vreq, response, epo);
	    	} else if (modeStr.equals("moveInstances")) {
	    		doMoveInstances(vreq, response, epo);
	    	}
	    }

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        setRequestAttributes(request,epo);

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error(this.getClass().getName()+" could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

	    
	}
    
}
