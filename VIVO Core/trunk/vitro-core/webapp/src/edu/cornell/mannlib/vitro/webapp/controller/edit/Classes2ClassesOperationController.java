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
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.beans.Classes2Classes;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.Classes2ClassesDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;

public class Classes2ClassesOperationController extends BaseEditController {

    private static final Log log = LogFactory.getLog(Classes2ClassesOperationController.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse response) {
    	VitroRequest request = new VitroRequest(req);
        String defaultLandingPage = getDefaultLandingPage(request);
        
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
                e.printStackTrace();
            }
            return;
        }

        Classes2ClassesDao dao = getWebappDaoFactory().getClasses2ClassesDao();
        VClassDao vcDao = getWebappDaoFactory().getVClassDao();
        
        String modeStr = request.getParameter("opMode");
        modeStr = (modeStr == null) ? "" : modeStr;
        
        if (request.getParameter("_cancel") == null) {
        
	        try {
	            if (request.getParameter("operation").equals("remove")) {
	                String[] subclassURIstrs = request.getParameterValues("SubclassURI");
	                if ((subclassURIstrs != null) && (subclassURIstrs.length > 1)) {
	                    String superclassURIstr = request.getParameter("SuperclassURI");
	                    if (superclassURIstr != null) {
	                        for (int i=0; i<subclassURIstrs.length; i++) {
	                        	if (modeStr.equals("disjointWith")) {
	                        		vcDao.removeDisjointWithClass(superclassURIstr, subclassURIstrs[i]);
	                        	} else if (modeStr.equals("equivalentClass")) {
	                        		vcDao.removeEquivalentClass(superclassURIstr, subclassURIstrs[i]);
	                        	} else {
		                            Classes2Classes c2c = new Classes2Classes();
		                            c2c.setSubclassURI(subclassURIstrs[i]);
		                            c2c.setSuperclassURI(superclassURIstr);
		                            dao.deleteClasses2Classes(c2c);
	                        	}
	                        }
	                    }
	                } else {
	                    String subclassURIstr = subclassURIstrs[0];
	                    String[] superclassURIstrs = request.getParameterValues("SuperclassURI");
	                    if (superclassURIstrs != null) {
	                        for (int i=0; i<superclassURIstrs.length; i++) {
	                        	if (modeStr.equals("disjointWith")) {
	                        		vcDao.removeDisjointWithClass(superclassURIstrs[i],subclassURIstr);
		                        } else if (modeStr.equals("equivalentClass")) {
		                        	vcDao.removeEquivalentClass(subclassURIstr,superclassURIstrs[i]);
		                        } else {
		                            Classes2Classes c2c = new Classes2Classes();
		                            c2c.setSuperclassURI(superclassURIstrs[i]);
		                            c2c.setSubclassURI(subclassURIstr);
		                            dao.deleteClasses2Classes(c2c);
	                        	}
	                        }
	                    }
	                }
	            } else if (request.getParameter("operation").equals("add")) {
	            	if (modeStr.equals("disjointWith")) {
	            		vcDao.addDisjointWithClass(request.getParameter("SuperclassURI"), request.getParameter("SubclassURI"));
	            	} else if (modeStr.equals("equivalentClass")) {
	            		vcDao.addEquivalentClass(request.getParameter("SuperclassURI"), request.getParameter("SubclassURI"));
	            	} else {
		            	Classes2Classes c2c = new Classes2Classes();
		                c2c.setSuperclassURI(request.getParameter("SuperclassURI"));
		                c2c.setSubclassURI(request.getParameter("SubclassURI"));
		                dao.insertNewClasses2Classes(c2c);
	            	}
	            }
	        } catch (Exception e) {
	            //e.printStackTrace();
	        }
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

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        doGet(request, response);
    }

}
