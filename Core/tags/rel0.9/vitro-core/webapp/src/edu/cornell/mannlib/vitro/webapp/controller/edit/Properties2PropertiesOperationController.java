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
import java.util.HashMap;

import javax.management.RuntimeErrorException;
import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.Classes2Classes;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.Classes2ClassesDao;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;

public class Properties2PropertiesOperationController extends
		BaseEditController {
	
	private static final Log log = LogFactory.getLog(Properties2PropertiesOperationController.class.getName());

	private static final boolean ADD = false;
	private static final boolean REMOVE = true;
	
    public void doPost(HttpServletRequest req, HttpServletResponse response) {
    	
    	String defaultLandingPage = getDefaultLandingPage(req);
    	
    	try {
    		
			VitroRequest request = new VitroRequest(req);
		    if(!checkLoginStatus(request,response))
		        return;
		
		    try {
		        super.doGet(request,response);
		    } catch (Exception e) {
		        log.error(this.getClass().getName()+" encountered exception calling super.doGet()");
		    }
		    
		    HashMap epoHash = null;
		    EditProcessObject epo = null;
		    try {
		        epoHash = (HashMap) request.getSession().getAttribute("epoHash");
		        epo = (EditProcessObject) epoHash.get(request.getParameter("_epoKey"));
		    } catch (NullPointerException e) {
		        //session or edit process expired
		        try {
		            response.sendRedirect(defaultLandingPage);
		        } catch (IOException f) {
		            e.printStackTrace();
		        }
		        return;
		    }
		
		    if (epo == null) {
		        log.debug("null epo");
		        try {
		            response.sendRedirect(defaultLandingPage);
		        } catch (IOException e) {
		            log.error(e);
		        }
		        return;
		    }
		    
		    // get parameters from request
		   	        
		    String modeStr = request.getParameter("opMode");
		    modeStr = (modeStr == null) ? "" : modeStr;
		    String operationStr = request.getParameter("operation");
		    boolean operation = false;
		    if ("add".equals(operationStr)) {
		    	operation = ADD;
		    } else if ("remove".equals(operationStr)) {
		    	operation = REMOVE;
		    } else {
		    	throw new IllegalArgumentException(
		    	    "request parameter 'operation' must have value 'add' or 'remove'");
		    }
		    	    
		    if (request.getParameter("_cancel") == null) {
		    	doEdit(modeStr, operation, request);
		    }
		    	     
		    //if no page forwarder was set, just go back to referring page:
		    //the referer stuff all will be changed so as not to rely on the HTTP header
		    
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

	    } catch (RuntimeException e) {
	        log.error("Unable to perform edit operation: ", e);
	        throw e;
	    } catch (Error err) {
	    	log.error("Unable to perform edit operation: ", err);
	    	throw err;
	    }
        
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
    	
    	String defaultLandingPage = getDefaultLandingPage(request);
    	
    	try {
            response.sendRedirect(defaultLandingPage);
        } catch (IOException e) {
            log.error("Unable to redirect to default landing page", e);
        }
    }
    
    private void doEdit(String modeStr, boolean operation, VitroRequest request) {
    	PropertyDao opDao = getWebappDaoFactory().getObjectPropertyDao();
   	 
        if (operation == REMOVE) {
            String[] subpropertyURIstrs = request.getParameterValues("SubpropertyURI");
            if ((subpropertyURIstrs != null) && (subpropertyURIstrs.length > 1)) {
                String superpropertyURIstr = request.getParameter("SuperpropertyURI");
                if (superpropertyURIstr != null) {
                    for (int i=0; i<subpropertyURIstrs.length; i++) {
                    	if (modeStr.equals("equivalentProperty")) {
                    		opDao.removeEquivalentProperty(superpropertyURIstr, subpropertyURIstrs[i]);
                    	} else {
                    		opDao.removeSuperproperty(subpropertyURIstrs[i], superpropertyURIstr);
                    	}
                    }
                }
            } else {
                String subpropertyURIstr = subpropertyURIstrs[0];
                String[] superpropertyURIstrs = request.getParameterValues("SuperpropertyURI");
                if (superpropertyURIstrs != null) {
                    for (int i=0; i<superpropertyURIstrs.length; i++) {
                    	if (modeStr.equals("equivalentProperty")) {
                        	opDao.removeEquivalentProperty(subpropertyURIstr,superpropertyURIstrs[i]);
                        } else {
                        	opDao.removeSuperproperty(subpropertyURIstr,superpropertyURIstrs[i]);
                    	}
                    }
                }
            }
        } else if (operation == ADD) {
        	if (modeStr.equals("equivalentProperty")) {
        		opDao.addEquivalentProperty(request.getParameter("SuperpropertyURI"), request.getParameter("SubpropertyURI"));
        	} else {
        		opDao.addSuperproperty(request.getParameter("SubpropertyURI"), request.getParameter("SuperpropertyURI"));
        	}
        }
    
    }
	
}
