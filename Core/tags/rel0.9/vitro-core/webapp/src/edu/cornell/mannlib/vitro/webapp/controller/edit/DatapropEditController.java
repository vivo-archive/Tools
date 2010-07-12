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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyGroup;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyGroupDao;

public class DatapropEditController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(DatapropEditController.class.getName());

    public void doPost (HttpServletRequest request, HttpServletResponse response) {
        final int NUM_COLS=15;

        if (!checkLoginStatus(request,response,(String)request.getAttribute("fetchURI")))
            return;

        try {
            super.doGet(request,response);
        } catch (Exception e) {
            log.error("DatapropEditController caught exception calling doGet()");
        }

        String datapropURI = request.getParameter("uri");

        DataPropertyDao dpDao = getWebappDaoFactory().getDataPropertyDao();
        DataProperty dp = dpDao.getDataPropertyByURI(datapropURI);
        PropertyGroupDao pgDao = getWebappDaoFactory().getPropertyGroupDao();

        ArrayList results = new ArrayList();
        results.add("Datatype Property");
        results.add("ontology");
        results.add("display name");
        results.add("domain");
        results.add("range datatype");
        results.add("group");
        results.add("display tier");
        results.add("display limit");
        results.add("example");
        results.add("description");
        results.add("public description");
        results.add("display level");
        results.add("update level");
        results.add("custom entry form");
        results.add("URI");

        
        Portal portal = (new VitroRequest(request)).getPortal();
        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);

        results.add(dp.getLocalNameWithPrefix());
        String ontologyName = null;
        if (dp.getNamespace() != null) {
            Ontology ont = getWebappDaoFactory().getOntologyDao().getOntologyByURI(dp.getNamespace());
            if ( (ont != null) && (ont.getName() != null) ) {
                ontologyName = ont.getName();
            }
        }
        results.add(ontologyName==null ? "(not identified)" : ontologyName);
        results.add(dp.getPublicName() == null ? "(no public name)" : dp.getPublicName());

        // we support parents now, but not the simple getParent() style method
        //String parentPropertyStr = "<i>(datatype properties are not yet modeled in a property hierarchy)</i>"; // TODO - need multiple inheritance
        //results.add(parentPropertyStr);
        
        // TODO - need unionOf/intersectionOf-style domains for domain class
        String domainStr="";
        try {
            domainStr = (dp.getDomainClassURI() == null) ? "" : "<a href=\"vclassEdit?uri="+URLEncoder.encode(dp.getDomainClassURI(),"UTF-8")+"&amp;home="+portal.getPortalId()+"\">"+dp.getDomainClassURI()+"</a>";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        results.add(domainStr);
        
        String rangeStr = (dp.getRangeDatatypeURI() == null) ? "<i>untyped</i> (rdfs:Literal)" : dp.getRangeDatatypeURI(); // TODO
        results.add(rangeStr);
        if (dp.getGroupURI() != null) {
            PropertyGroup pGroup = pgDao.getGroupByURI(dp.getGroupURI());
            if (pGroup != null) {
            	results.add(pGroup.getName());
            } else {
            	results.add(dp.getGroupURI());
            }
        } else {
            results.add("unspecified");
        }
        results.add(String.valueOf(dp.getDisplayTier()));
        results.add(String.valueOf(dp.getDisplayLimit()));
        String exampleStr = (dp.getExample() == null) ? "" : dp.getExample();
        results.add(exampleStr);
        String descriptionStr = (dp.getDescription() == null) ? "" : dp.getDescription();
        results.add(descriptionStr);
        String publicDescriptionStr = (dp.getPublicDescription() == null) ? "" : dp.getPublicDescription();
        results.add(publicDescriptionStr);        
        results.add(dp.getHiddenFromDisplayBelowRoleLevel()  == null ? "unspecified" : dp.getHiddenFromDisplayBelowRoleLevel().getLabel());
        results.add(dp.getProhibitedFromUpdateBelowRoleLevel() == null ? "unspecified" : dp.getProhibitedFromUpdateBelowRoleLevel().getLabel());
        results.add(dp.getCustomEntryForm() == null ? "unspecified" : dp.getCustomEntryForm());
        results.add(dp.getURI() == null ? "" : dp.getURI());
        request.setAttribute("results",results);
        request.setAttribute("columncount",NUM_COLS);
        request.setAttribute("suppressquery","true");

        boolean FORCE_NEW = true;
        
        EditProcessObject epo = super.createEpo(request, FORCE_NEW);
        FormObject foo = new FormObject();
        HashMap OptionMap = new HashMap();
        // add the options
        foo.setOptionLists(OptionMap);
        epo.setFormObject(foo);

        DataPropertyDao assertionsDpDao = (getAssertionsWebappDaoFactory() != null) 
            ? getAssertionsWebappDaoFactory().getDataPropertyDao()
            : getWebappDaoFactory().getDataPropertyDao();
        
        List superURIs = assertionsDpDao.getSuperPropertyURIs(dp.getURI());
        List superProperties = new ArrayList();
        Iterator superURIit = superURIs.iterator();
        while (superURIit.hasNext()) {
            String superURI = (String) superURIit.next();
            if (superURI != null) {
                DataProperty superProperty = assertionsDpDao.getDataPropertyByURI(superURI);
                if (superProperty != null) {
                    superProperties.add(superProperty);
                }
            }
        }
        request.setAttribute("superproperties",superProperties);

        List subURIs = assertionsDpDao.getSubPropertyURIs(dp.getURI());
        List subProperties = new ArrayList();
        Iterator subURIit = subURIs.iterator();
        while (subURIit.hasNext()) {
            String subURI = (String) subURIit.next();
            DataProperty subProperty = dpDao.getDataPropertyByURI(subURI);
            if (subProperty != null) {
                subProperties.add(subProperty);
            }
        }
        request.setAttribute("subproperties",subProperties);
        
        List eqURIs = assertionsDpDao.getEquivalentPropertyURIs(dp.getURI());
        List eqProperties = new ArrayList();
        Iterator eqURIit = eqURIs.iterator();
        while (eqURIit.hasNext()) {
            String eqURI = (String) eqURIit.next();
            DataProperty eqProperty = dpDao.getDataPropertyByURI(eqURI);
            if (eqProperty != null) {
                eqProperties.add(eqProperty);
            }
        }
        request.setAttribute("equivalentProperties", eqProperties);
        
        request.setAttribute("epoKey",epo.getKey());
        request.setAttribute("datatypeProperty", dp);
        request.setAttribute("bodyJsp","/templates/edit/specific/dataprops_edit.jsp");
        request.setAttribute("portalBean",portal);
        request.setAttribute("title","Datatype Property Control Panel");
        request.setAttribute("css", "<link rel=\"stylesheet\" type=\"text/css\" href=\""+portal.getThemeDir()+"css/edit.css\"/>");

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("DatapropEditController could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }

    public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request,response);
    }

}