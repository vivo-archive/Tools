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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;
import edu.cornell.mannlib.vedit.beans.LoginFormBean;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vedit.util.FormUtils;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class SiteAdminController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(SiteAdminController.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            super.doGet(request,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        VitroRequest vreq = new VitroRequest(request);

        EditProcessObject epo = super.createEpo(request);
        FormObject foo = new FormObject();
        HashMap optionMap = new HashMap();
        
        List classGroups = vreq.getWebappDaoFactory().getVClassGroupDao().getPublicGroupsWithVClasses(true,true,false); // order by displayRank, include uninstantiated classes, don't get the counts of individuals
        
        Iterator classGroupIt = classGroups.iterator();
        ListOrderedMap optGroupMap = new ListOrderedMap();
        while (classGroupIt.hasNext()) {
            VClassGroup group = (VClassGroup)classGroupIt.next();
            List classes = group.getVitroClassList();
            optGroupMap.put(group.getPublicName(),FormUtils.makeOptionListFromBeans(classes,"URI","PickListName",null,null,false));
        }
        optionMap.put("VClassId", optGroupMap);
        foo.setOptionLists(optionMap);
        epo.setFormObject(foo);

        if ( (200 <= getWebappDaoFactory().getLanguageProfile()) && (getWebappDaoFactory().getLanguageProfile() < 300) ) {
            request.setAttribute("languageModeStr", "OWL Mode" );        	
        } else if ( 100 == getWebappDaoFactory().getLanguageProfile() ) {
            request.setAttribute("languageModeStr", "RDF Schema Mode" );        	
        } 
        
        
        LoginFormBean loginHandler = (LoginFormBean)request.getSession().getAttribute("loginHandler");
        if( loginHandler != null ){
            String status = loginHandler.getLoginStatus();
            if ( "authenticated".equals(status) ) {
                int securityLevel = Integer.parseInt( loginHandler.getLoginRole() );
                if(securityLevel >= loginHandler.CURATOR ){
                    String verbose = request.getParameter("verbose");
                    if( "true".equals(verbose)) {
                        request.getSession().setAttribute(VERBOSE, Boolean.TRUE);
                    } else if( "false".equals(verbose)) {
                        request.getSession().setAttribute(VERBOSE, Boolean.FALSE);
                    }
                }
            }
        }
        
        request.setAttribute("singlePortal",new Boolean(vreq.getWebappDaoFactory().getPortalDao().isSinglePortal()));
        
        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);        
        request.setAttribute("bodyJsp","/siteAdmin/main.jsp");
        request.setAttribute("scripts","/siteAdmin/scripts.jsp");
        request.setAttribute("title",((Portal)request.getAttribute("portalBean")).getAppName() + " Site Administration");
        request.setAttribute("epoKey",epo.getKey());
        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("SiteAdminController could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        doGet(request,response);
    }

    public static final String VERBOSE = "verbosePropertyListing";
}
