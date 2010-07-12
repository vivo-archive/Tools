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
import java.util.LinkedList;
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
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;

public class ExternalIdRetryController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(ExternalIdRetryController.class.getName());

    public void doPost (HttpServletRequest request, HttpServletResponse response) {
    	
    	

        if (!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request,response);
        } catch (Exception e) {
            log.error("ExternalIdRetryController encountered exception calling super.doGet()");
        }

        VitroRequest vreq = new VitroRequest(request);

        //create an EditProcessObject for this and put it in the session
        EditProcessObject epo = super.createEpo(request);

        String action = "insert";

        DataPropertyDao dpDao = getWebappDaoFactory().getDataPropertyDao();
        DataPropertyStatementDao edDao = getWebappDaoFactory().getDataPropertyStatementDao();
        epo.setDataAccessObject(edDao);
        epo.setBeanClass(DataPropertyStatement.class);

        IndividualDao eDao = getWebappDaoFactory().getIndividualDao();

        DataPropertyStatement eidForEditing = null;
        if (!epo.getUseRecycledBean()){
            eidForEditing = new DataPropertyStatementImpl();
            populateBeanFromParams(eidForEditing,vreq);
            if (vreq.getParameter(MULTIPLEXED_PARAMETER_NAME) != null) {
                action = "update";
            }
            epo.setOriginalBean(eidForEditing);
        } else {
            eidForEditing = (DataPropertyStatement) epo.getNewBean();
        }

        //make a simple mask for the class's id
        Object[] simpleMaskPair = new Object[2];
        simpleMaskPair[0]="Id";
        //simpleMaskPair[1]=Integer.valueOf(eidForEditing.getId());
        epo.getSimpleMask().add(simpleMaskPair);

        //set up any listeners

        //set portal flag to current portal
        Portal currPortal = (Portal) request.getAttribute("portalBean");
        int currPortalId = 1;
        if (currPortal != null) {
            currPortalId = currPortal.getPortalId();
        }

        FormObject foo = new FormObject();
        HashMap OptionMap = new HashMap();
        List entityList = new LinkedList();
        if (eidForEditing.getIndividualURI() != null) {
            Individual individual = eDao.getIndividualByURI(eidForEditing.getIndividualURI());
            entityList.add(new Option(individual.getURI(),individual.getName(),true));
        } else {
            entityList.add(new Option ("-1", "Error: the entity must be specified", true));
        }
        OptionMap.put("IndividualURI",entityList);
        // TOOD change following DAO call to getAllExternalIdDataProperties once implemented
        List allExternalIdDataProperties = dpDao.getAllExternalIdDataProperties();
        Collections.sort(allExternalIdDataProperties);
        OptionMap.put("DatapropURI", FormUtils.makeOptionListFromBeans(allExternalIdDataProperties, "URI", "PublicName", eidForEditing.getDatapropURI(),""));
        foo.setOptionLists(OptionMap);
        foo.setErrorMap(epo.getErrMsgMap());

        epo.setFormObject(foo);

        String html = FormUtils.htmlFormFromBean(eidForEditing,action,foo,epo.getBadValueMap());

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("formHtml",html);
        request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
        request.setAttribute("formJsp","/templates/edit/specific/externalIds_retry.jsp");
        request.setAttribute("scripts","/templates/edit/formBasic.js");
        request.setAttribute("title","External Id Editing Form");
        request.setAttribute("_action",action);
        request.setAttribute("unqualifiedClassName","External Id");
        setRequestAttributes(request,epo);

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("ExternalIdRetryController could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }

    public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }

}
