<%--
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
--%>

<%@ page import="com.hp.hpl.jena.rdf.model.Model" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.VClass" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.ModelSelector" %>
<%@ page import="com.hp.hpl.jena.ontology.OntModel"%>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>
<%!
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.admin.mayEditAs.jsp");
    public static String RANGE_CLASS = "http://xmlns.com/foaf/0.1/Person";    
    public static String PREDICATE = VitroVocabulary.MAY_EDIT_AS;
%>
<%
    String subjectUri = (String)request.getAttribute("subjectUri");    

    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();
    
    VClass rangeClass = wdf.getVClassDao().getVClassByURI( RANGE_CLASS );
    if( rangeClass == null ) log.debug("Cannot find class for range for property."
            + " Looking for " + RANGE_CLASS);
    request.setAttribute("rangeClassUriJson", MiscWebUtils.escape(RANGE_CLASS));
    
    request.setAttribute("predicateUriJson", MiscWebUtils.escape(PREDICATE));
    
    request.setAttribute("objectUriJson" , MiscWebUtils.escape((String)request.getAttribute("objectUri")));    
%>

<v:jsonset var="n3ForEdit"  >
    ?subject ?predicate ?objectVar.
</v:jsonset>

<c:set var="editjson" scope="request">
  {
    "formUrl"                   : "${formUrl}",
    "editKey"                   : "${editKey}",
    "urlPatternToReturnTo"      : "/userEdit",

    "subject"      : [ "subject", "${subjectUriJson}" ] ,
    "predicate"    : [ "predicate", "${predicateUriJson}" ],
    "object"       : [ "objectVar" ,  "${objectUriJson}" , "URI"],

    "n3required"                : [ "${n3ForEdit}" ],
    "n3optional"                : [ ],
    "newResources"              : { },

    "urisInScope"               : { },
    "literalsInScope"           : { },

    "urisOnForm"                : ["objectVar"],
    "literalsOnForm"            : [ ],
    "filesOnForm"               : [ ],

    "sparqlForLiterals"         : { },
    "sparqlForUris"             : { },

    "sparqlForExistingLiterals" : { },
    "sparqlForExistingUris"     : { },
    "fields"                    : { "objectVar" : {
                                       "newResource"      : "false",
                                       "queryForExisting" : { },
                                       "validators"       : [ ],
                                       "optionsType"      : "INDIVIDUALS_VIA_VCLASS",
                                       "subjectUri"       : "${subjectUriJson}",
                                       "subjectClassUri"  : "",
                                       "predicateUri"     : "",
                                       "objectClassUri"   : "${rangeClassUriJson}",
                                       "rangeDatatypeUri" : "",
                                       "rangeLang"        : "",
                                       "literalOptions"   : [ ] ,
                                       "assertions"       : ["${n3ForEdit}"]
                                     }
                                  }
  }
</c:set>

<%  /* now put edit configuration Json object into session */
    EditConfiguration editConfig = new EditConfiguration((String)request.getAttribute("editjson"));
    EditConfiguration.putConfigInSession(editConfig, session);
    String formTitle   ="";
    String submitLabel ="";
    Model model = (Model)application.getAttribute("jenaOntModel");
    editConfig.setWriteModelSelector( new ModelSelector(){
    	public OntModel getModel(HttpServletRequest request, ServletContext context){
    		return (OntModel)context.getAttribute("userAccountsOntModel");
    	}
    });
    if( request.getAttribute("object") != null ){//this block is for an edit of an existing object property statement
        editConfig.prepareForObjPropUpdate( model );
        formTitle   = "Change person associated with this user account";
        submitLabel = "Save Change";
    } else {
        editConfig.prepareForNonUpdate( model );     
        formTitle   = "Select person to associate with this user account";
        submitLabel = "Create Association";        
    }
%>
<jsp:include page="${preForm}"/>

<h2><%=formTitle%></h2>
<form class="editForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" method="post">      
    <v:input type="select" id="objectVar" size="80" />
    <v:input type="submit" id="submit" value="<%=submitLabel%>" cancel="${param.subjectUri}"/>    
</form>

<c:if test="${!empty param.objectUri}" >
    <form class="deleteForm" action="<c:url value="/edit/n3Delete.jsp"/>" method="post">       
        <label for="delete"><h3>Or remove this association?</h3></label>
        <input type="hidden" name="subjectUri"   value="${param.subjectUri}"/>
        <input type="hidden" name="predicateUri" value="${param.predicateUri}"/>
        <input type="hidden" name="objectVar"    value="${param.objectUri}"/>    
        <input type="hidden" name="editform"    value="edit/admin/mayEditAs.jsp"/>
        <v:input type="submit" id="delete" value="Remove" cancel="" />
    </form>
</c:if>

<jsp:include page="${postForm}"/>
