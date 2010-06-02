package edu.cornell.mannlib.vitro.webapp.controller.edit.listing;

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

import java.net.URLEncoder;
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
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

public class IndividualsListingController extends BaseEditController {

    private static final int MAX_INDIVIDUALS = 50;

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

        //need to figure out how to structure the results object to put the classes underneath

        String assertedOnlyStr = request.getParameter("assertedOnly");
        
        WebappDaoFactory wadf = null;
        
        if (assertedOnlyStr != null && assertedOnlyStr.equalsIgnoreCase("true")) {
        	wadf = getAssertionsWebappDaoFactory();
        }
        if (wadf == null) {
        	wadf = getWebappDaoFactory();
        }
        
        IndividualDao dao = wadf.getIndividualDao();
        VClassDao vcDao = wadf.getVClassDao();

        String vclassURI = request.getParameter("VClassURI");
        VClass vc = vcDao.getVClassByURI(vclassURI);
        
        List inds = dao.getIndividualsByVClassURI(vclassURI,1,MAX_INDIVIDUALS);

        ArrayList results = new ArrayList();
        results.add("XX");
        results.add("Individual");
        results.add("class");
        results.add("moniker");

        if (inds != null && inds.size()>0) {
            Iterator indsIt = inds.iterator();
            while (indsIt.hasNext()) {
                Individual ind = (Individual) indsIt.next();

                results.add("XX");

                if (ind.getName() != null) {
                    try {
                        String individualName = (ind.getName()==null || ind.getName().length()==0) ? ind.getURI() : ind.getName();
                        results.add("<a href=\"./entityEdit?uri="+URLEncoder.encode(ind.getURI(),"UTF-8")+"&amp;home="+portal.getPortalId()+"\">"+individualName+"</a>");
                    } catch (Exception e) {
                        results.add(ind.getName());
                    }
                } else {
                    results.add("");
                }


                if (vc != null) {
                    try {
                        String vclassName = (vc.getName()==null || vc.getName().length()==0) ? vc.getURI() : vc.getName();
                        results.add("<a href=\"./vclassEdit?uri="+URLEncoder.encode(vc.getURI(),"UTF-8")+"&amp;home="+portal.getPortalId()+"\">"+vclassName+"</a>");
                    } catch (Exception e) {
                        results.add(vc.getName());
                    }
                } else {
                    results.add(vclassURI);
                }

                String monikerStr = (ind.getMoniker() != null) ? ind.getMoniker() : "unspecified";
                results.add(monikerStr);

            }
        } else {
            results.add("XX");
            results.add("No individuals to display");
        }

        request.setAttribute("results",results);

        request.setAttribute("columncount",new Integer(4));
        request.setAttribute("suppressquery","true");
        request.setAttribute("title", "Individuals in Class "+ ( (vc != null) ? vc.getName() : vclassURI ) );
        request.setAttribute("portalBean",portal);
        request.setAttribute("bodyJsp", Controllers.HORIZONTAL_JSP);
//        request.setAttribute("horizontalJspAddButtonUrl", Controllers.RETRY_URL);
//        request.setAttribute("horizontalJspAddButtonText", "Add new individual");
//        request.setAttribute("horizontalJspAddButtonControllerParam", "Individual");
        request.setAttribute("home", portal.getPortalId());
        
        // new individual button
        List <ButtonForm> buttons = new ArrayList<ButtonForm>();
        HashMap<String,String> newIndividualParams=new HashMap<String,String>();
        newIndividualParams.put("home", String.valueOf(portal.getPortalId()));
        newIndividualParams.put("VClassURI",vclassURI);    
        newIndividualParams.put("controller","Entity");
        ButtonForm newIndividualButton = new ButtonForm(Controllers.RETRY_URL,"buttonForm","Add instance",newIndividualParams);
        buttons.add(newIndividualButton);
        request.setAttribute("topButtons", buttons);
          
        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        try {
            rd.forward(request,response);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        doGet(request,response);
    }

}
