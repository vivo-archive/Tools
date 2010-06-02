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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.Tab;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.TabDao;

public class TabHierarchyOperationController extends BaseEditController {


    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse response) {

    	VitroRequest request = new VitroRequest(req);
    	String defaultLandingPage = getDefaultLandingPage(request);
        TabDao tDao = getWebappDaoFactory().getTabDao();

        if (request.getParameter("_cancel") == null) {
        
	        String action = request.getParameter("primaryAction");
	        if (action == null) {
	            if (request.getParameter("_insert") != null) {
	                action = "_insert";
	            }
	        }
	        
	        String[] childIdStr = request.getParameterValues("ChildId");
	        String[] parentIdStr = request.getParameterValues("ParentId");
	        
	        if (action.equals("_remove") && childIdStr != null && parentIdStr != null) {
	        	if (childIdStr.length>1 || (childIdStr.length==1 && parentIdStr.length==1)) {
	        		try {
	        			int parentId = Integer.decode(parentIdStr[0]);
	        			Tab parent = tDao.getTab(parentId);
		        		for (int childIndex=0; childIndex<childIdStr.length; childIndex++) {
		        			try {
		        				int childId = Integer.decode(childIdStr[childIndex]);
		        				Tab child = tDao.getTab(childId);
		        				tDao.removeParentTab(child, parent);
		        			} catch (NumberFormatException nfe) {}
		        		}
	        		} catch (NumberFormatException nfe) {}
	        	} else {
	        		try {
	        			int childId = Integer.decode(childIdStr[0]);
	        			Tab child = tDao.getTab(childId);
		        		for (int parentIndex=0; parentIndex<parentIdStr.length; parentIndex++) {
		        			try {
		        				int parentId = Integer.decode(parentIdStr[parentIndex]);
		        				Tab parent = tDao.getTab(parentId);
		        				tDao.removeParentTab(child, parent);
		        			} catch (NumberFormatException nfe) {}
		        		}
	        		} catch (NumberFormatException nfe) {}
	        	}
	        } else if (action.equals("_insert")) {
	        	try {
	        		int childId = Integer.decode(childIdStr[0]);
	                int parentId = Integer.decode(parentIdStr[0]);
	                Tab child = tDao.getTab(childId);
	                Tab parent = tDao.getTab(parentId);
	                tDao.addParentTab(child, parent);
	        	} catch (NumberFormatException nfe) {}
	        }
        
        }

        //if no page forwarder was set, just go back to referring page:
        //the referer stuff all will be changed so as not to rely on the HTTP header
        EditProcessObject epo = super.createEpo(request);
        String referer = epo.getReferer();
        if (referer == null) {
            try {
                response.sendRedirect(defaultLandingPage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                response.sendRedirect(referer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
