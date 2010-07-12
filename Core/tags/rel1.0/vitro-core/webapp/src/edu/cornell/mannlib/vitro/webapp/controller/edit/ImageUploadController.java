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

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;

import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;

public class ImageUploadController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(ImageUploadController.class.getName());

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		if (!checkLoginStatus(request,response,(String)request.getAttribute("fetchURI")))
			return;
		
		try {
			super.doGet(request,response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String individualURIStr=
		    (individualURIStr=(String)request.getAttribute("entityUri"))==null ? 
		           ((individualURIStr=request.getParameter("entityUri"))==null ? null:individualURIStr) : individualURIStr;

		if (individualURIStr != null)
			request.setAttribute("individual", getWebappDaoFactory().getIndividualDao().getIndividualByURI(individualURIStr));
		
		EditProcessObject epo = super.createEpo(request);
		FormObject foo = new FormObject();
		HashMap optionMap = new HashMap();
		foo.setOptionLists(optionMap);
		epo.setFormObject(foo);
						
		RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
		request.setAttribute("bodyJsp","/templates/edit/specific/uploadimages_body.jsp");
		request.setAttribute("scripts","/templates/edit/specific/uploadimages_head.jsp");
        request.setAttribute("title","Upload Image");
        request.setAttribute("bodyAttr","onLoad=\"initDHTMLAPI();initGroupDisplay('thumbnailExtra','block');\"");
		request.setAttribute("epoKey",epo.getKey());
		try {
			rd.forward(request, response);
		} catch (Exception e) {
			log.error("ImageUploadController could not forward to view.");
			log.error(e.getMessage());
			log.error(e.getStackTrace());
		}    
	}		
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request,response);
	}
	
}
