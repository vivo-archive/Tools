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

package edu.cornell.mannlib.vitro.webapp.controller.edit.listing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vedit.beans.ButtonForm;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyStatementDao;

public class ObjectPropertyStatementListingController extends
		BaseEditController {
	
   public void doGet(HttpServletRequest request, HttpServletResponse response) {
        VitroRequest vrequest = new VitroRequest(request);
        Portal portal = vrequest.getPortal();

        if(!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request, response);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        boolean assertedStatementsOnly = false;
     
        String assertParam = request.getParameter("assertedStmts");
        if (assertParam!=null && assertParam.equalsIgnoreCase("true")) {
            assertedStatementsOnly = true;
        }
                
        boolean showVClasses = false;
        String displayParam = request.getParameter("showVClasses");
        if (displayParam!=null && displayParam.equalsIgnoreCase("true")) {
            showVClasses = true;  // this will trigger a limitation to asserted vclasses, since we can't easily display all vclasses for an individual
        }
        
        int startAt=1;
        String startAtParam = request.getParameter("startAt");
        if (startAtParam!=null && startAtParam.trim().length()>0) {
            try {
                startAt = Integer.parseInt(startAtParam);
                if (startAt<=0) {
                    startAt = 1;
                }
            } catch(NumberFormatException ex) {
                throw new Error("Cannot interpret "+startAtParam+" as a number");
            }
        }
        
        int endAt=50;
        String endAtParam = request.getParameter("endAt");
        if (endAtParam!=null && endAtParam.trim().length()>0) {
            try {
                endAt = Integer.parseInt(endAtParam);
                if (endAt<=0) {
                    endAt=1;
                }
                if (endAt<startAt) {
                    int temp = startAt;
                    startAt = endAt;
                    endAt = temp;
                }
            } catch(NumberFormatException ex) {
                throw new Error("Cannot interpret "+endAtParam+" as a number");
            }
        }

        
        ArrayList<String> results = new ArrayList();
        
        request.setAttribute("results",results);
        
        results.add("XX");
        results.add("subject");
        if (showVClasses) results.add("type");
        results.add("property");
        results.add("object");
        if (showVClasses) results.add("type");
        
        ObjectPropertyStatementDao opsDao = null;
        if (assertedStatementsOnly){ // get only asserted, not inferred, object property statements
            opsDao = getAssertionsWebappDaoFactory().getObjectPropertyStatementDao();
        } else {
            opsDao = getWebappDaoFactory().getObjectPropertyStatementDao();
        }

        // get all object properties -- no concept of asserted vs. inferred object properties
        ObjectPropertyDao opDao = getWebappDaoFactory().getObjectPropertyDao();
        
        IndividualDao iDao = null;
        if (showVClasses) {
            iDao = getAssertionsWebappDaoFactory().getIndividualDao();
        } else {
            iDao = getWebappDaoFactory().getIndividualDao();
        }
        
        String propURIStr = request.getParameter("propertyURI");
        
        ObjectProperty op = opDao.getObjectPropertyByURI(propURIStr);        
        
        int count = 0;


        for (Iterator<ObjectPropertyStatement> i = opsDao.getObjectPropertyStatements(op,startAt,endAt).iterator(); i.hasNext();) {
        	count++;
        	ObjectPropertyStatement ops = i.next();
        	Individual subj = iDao.getIndividualByURI(ops.getSubjectURI());
        	Individual obj = iDao.getIndividualByURI(ops.getObjectURI());
        	results.add("XX");
        	results.add(ListingControllerWebUtils.formatIndividualLink(subj,portal));
        	if (showVClasses) {
				try {
					results.add(ListingControllerWebUtils.formatVClassLinks(subj.getVClasses(true),portal));
				} catch (Exception e) {
					results.add("?");
				}
			}
        	results.add(op.getDomainPublic());
        	results.add(ListingControllerWebUtils.formatIndividualLink(obj,portal));
            if (showVClasses) {
				try {
					results.add(ListingControllerWebUtils.formatVClassLinks(obj.getVClasses(true),portal));
				} catch (Exception e) {
					results.add("?");
				}
			}
        }
        
        if (count == 0) {
        	results.add("XX");
        	results.add("No statements found for property \""+op.getLocalNameWithPrefix()+"\"");
        	results.add("");
        	results.add("");
        	if (showVClasses) {
        	    results.add("");
        	    results.add("");
        	}
        }
        
        if (showVClasses){
            request.setAttribute("columncount",new Integer(6));
        } else {
            request.setAttribute("columncount",new Integer(4));
        }
        request.setAttribute("suppressquery","true");
        request.setAttribute("title","Object Property Statements");
        request.setAttribute("portalBean",portal);
        request.setAttribute("bodyJsp", Controllers.HORIZONTAL_JSP);
        request.setAttribute("home", portal.getPortalId());
        // new way of adding more than one button
        List <ButtonForm> buttons = new ArrayList<ButtonForm>();
        HashMap<String,String> newPropParams=new HashMap<String,String>();
        newPropParams.put("controller", "Property");
        newPropParams.put("home", String.valueOf(portal.getPortalId()));
        ButtonForm newPropButton = new ButtonForm(Controllers.RETRY_URL,"buttonForm","Add new object property",newPropParams);
        buttons.add(newPropButton);
        HashMap<String,String> rootPropParams=new HashMap<String,String>();
        rootPropParams.put("iffRoot", "true");
        rootPropParams.put("home", String.valueOf(portal.getPortalId()));
        ButtonForm rootPropButton = new ButtonForm("showObjectPropertyHierarchy","buttonForm","root properties",rootPropParams);
        buttons.add(rootPropButton);
        request.setAttribute("topButtons", buttons);
        /*
        request.setAttribute("horizontalJspAddButtonUrl", Controllers.RETRY_URL);
        request.setAttribute("horizontalJspAddButtonText", "Add new object property");
        request.setAttribute("horizontalJspAddButtonControllerParam", "Property");
        */
        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        try {
            rd.forward(request,response);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
   }
      
   public void doPost(HttpServletRequest request, HttpServletResponse response) {
	   // don't post to this controller
   }
	
}
