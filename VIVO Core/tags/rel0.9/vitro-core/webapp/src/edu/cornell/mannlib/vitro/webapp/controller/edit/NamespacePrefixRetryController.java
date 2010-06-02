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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;
import edu.cornell.mannlib.vedit.beans.Option;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;

public class NamespacePrefixRetryController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(NamespacePrefixRetryController.class.getName());
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
	    if(!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request,response);
        } catch (Exception e) {
            log.error("Classes2ClassesRetryController encountered exception calling super.doGet()");
        }

        //create an EditProcessObject for this and put it in the session
        EditProcessObject epo = super.createEpo(request);
        
        if (request.getParameter("prefix") != null) {
        	epo.setAction("update");
        	request.setAttribute("_action","update");
        } else {
        	epo.setAction("insert");
        	request.setAttribute("_action","insert");
        }
				
	       RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
	       request.setAttribute("editAction","namespacePrefixOp");
	        request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
	        request.setAttribute("scripts","/templates/edit/formBasic.js");
        	request.setAttribute("formJsp","/templates/edit/specific/namespacePrefix_retry.jsp");
        	request.setAttribute("title","Edit Namespace Prefix Mapping");
	        setRequestAttributes(request,epo);

	        try {
	            rd.forward(request, response);
	        } catch (Exception e) {
	            log.error(this.getClass().getName()+" could not forward to view.");
	            log.error(e.getMessage());
	            log.error(e.getStackTrace());
	        }

	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		// shouldn't be posting to this controller
	}

}
