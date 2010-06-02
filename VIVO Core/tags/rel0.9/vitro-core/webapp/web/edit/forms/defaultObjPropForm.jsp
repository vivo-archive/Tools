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
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>
<%!
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.defaultObjPropForm.jsp");
%>
<%
    Individual subject = (Individual)request.getAttribute("subject");
    ObjectProperty prop = (ObjectProperty)request.getAttribute("predicate");

    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();
    if( prop.getRangeVClassURI() == null ) {
    	log.debug("Property has null for its range class URI");
    	// If property has no explicit range, we will use e.g. owl:Thing.
    	// Typically an allValuesFrom restriction will come into play later.
    	VClass top = wdf.getVClassDao().getTopConcept();
    	prop.setRangeVClassURI(top.getURI());
    	log.debug("Using "+prop.getRangeVClassURI());
    }

    VClass rangeClass = wdf.getVClassDao().getVClassByURI( prop.getRangeVClassURI());
    if( rangeClass == null ) log.debug("Cannot find class for range for property.  Looking for " + prop.getRangeVClassURI() );    
%>


<%@page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.SelectListGenerator"%>
<%@page import="java.util.Map"%><v:jsonset var="queryForInverse" >
    PREFIX owl:  <http://www.w3.org/2002/07/owl#>
    SELECT ?inverse_property
    WHERE {
        ?inverse_property owl:inverseOf ?predicate
    }
</v:jsonset>

<v:jsonset var="n3ForEdit"  >
    ?subject ?predicate ?objectVar.
</v:jsonset>

<v:jsonset var="n3Inverse" >
    ?objectVar ?inverseProp ?subject.
</v:jsonset>

<c:set var="editjson" scope="request">
  {
    "formUrl"                   : "${formUrl}",
    "editKey"                   : "${editKey}",
    "urlPatternToReturnTo"      : "/entity",

    "subject"      : [ "subject", "${subjectUriJson}" ] ,
    "predicate"    : [ "predicate", "${predicateUriJson}" ],
    "object"       : [ "objectVar" ,  "${objectUriJson}" , "URI"],

    "n3required"                : [ "${n3ForEdit}" ],
    "n3optional"                : [ "${n3Inverse}" ],
    "newResources"              : { },

    "urisInScope"               : { },
    "literalsInScope"           : { },

    "urisOnForm"                : ["objectVar"],
    "literalsOnForm"            : [ ],
    "filesOnForm"               : [ ],

    "sparqlForLiterals"         : { },
    "sparqlForUris"             : {"inverseProp" : "${queryForInverse}" },

    "sparqlForExistingLiterals" : { },
    "sparqlForExistingUris"     : { },
    "fields"                    : { "objectVar" : {
                                       "newResource"      : "false",
                                       "queryForExisting" : { },
                                       "validators"       : [ "nonempty" ],
                                       "optionsType"      : "INDIVIDUALS_VIA_OBJECT_PROPERTY",
                                       "subjectUri"       : "${subjectUriJson}",
                                       "subjectClassUri"  : "",
                                       "predicateUri"     : "${predicateUriJson}",
                                       "objectClassUri"   : "",
                                       "rangeDatatypeUri" : "",
                                       "rangeLang"        : "",
                                       "literalOptions"   : [ ] ,
                                       "assertions"       : ["${n3ForEdit}", "${n3Inverse}"]
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
    if( request.getAttribute("object") != null ){//this block is for an edit of an existing object property statement
        editConfig.prepareForObjPropUpdate( model );
        formTitle   = "Change entry for: <em>"+prop.getDomainPublic()+"</em>";
        submitLabel = "save change";
    } else {
        editConfig.prepareForNonUpdate( model );
        if ( prop.getOfferCreateNewOption() ) {
            log.debug("property set to offer \"create new\" option; custom form: ["+prop.getCustomEntryForm()+"]");
            formTitle   = "Select an existing "+rangeClass.getName()+" for "+subject.getName();
            submitLabel = "select existing";
        } else {
            formTitle   = "Add an entry to: <em>"+prop.getDomainPublic()+"</em>";
            submitLabel = "save entry";
        }
    }
    
    if( prop.getSelectFromExisting() ){
    	Map<String,String> rangeOptions = SelectListGenerator.getOptions(editConfig, "objectVar" , wdf);    	
    	if( rangeOptions != null && rangeOptions.size() > 0 )
    		request.setAttribute("rangeOptionsExist", true);
    	else 
    		request.setAttribute("rangeOptionsExist",false);
    }
%>
<jsp:include page="${preForm}"/>

<h2><%=formTitle%></h2>

<c:if test="${requestScope.predicate.selectFromExisting == true }">
  <c:if test="${requestScope.rangeOptionsExist == true }">
  	<form class="editForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >    
	    <c:if test="${!empty predicate.publicDescription}">
	    	<p>${predicate.publicDescription}</p>
	    </c:if>
	    <v:input type="select" id="objectVar" size="80" />
	    <div style="margin-top: 1em">
	        <v:input type="submit" id="submit" value="<%=submitLabel%>" cancel="${param.subjectUri}"/>
	    </div>    
    </form>
  </c:if>
  <c:if test="${requestScope.rangeOptionsExist == false }">
    <p>There are no entries in the system to select from.</p>
  </c:if>
</c:if>

<c:if test="${requestScope.predicate.offerCreateNewOption == true}">
 	<c:if test="${requestScope.rangeOptionsExist == true }">
    	<p>If you don't find the appropriate entry on the selection list:</p>
  	</c:if>
  	<c:if test="${requestScope.rangeOptionsExist == false }">
  		<p style="margin-top: 5em">Please create a new entry.</p>  		    
  	</c:if>	
	<c:url var="createNewUrl" value="/edit/editRequestDispatch.jsp"/>
	<form class="editForm" action="${createNewUrl}">        
        <input type="hidden" value="${param.subjectUri}" name="subjectUri"/>
        <input type="hidden" value="${param.predicateUri}" name="predicateUri"/>
        <input type="hidden" value="${param.objectUri}" name="objectUri"/>        
		<input type="hidden" value="create" name="cmd"/>        
		<v:input type="typesForCreateNew" id="typeOfNew" />
        <v:input type="submit" id="submit" value="add a new item to this list"/>
	</form>                            
</c:if>

<c:if test="${(requestScope.predicate.offerCreateNewOption == false) && (requestScope.predicate.selectFromExisting == false)}">
 <p>This property is currently configured to prohibit editing. </p>
</c:if>

<c:if test="${ (!empty param.objectUri) && (empty param.deleteProhibited) }" >
    <form class="deleteForm" action="editRequestDispatch.jsp" method="get">       
	 	<label for="delete"><h3>Delete this entry?</h3></label>
        <input type="hidden" name="subjectUri"   value="${param.subjectUri}"/>
        <input type="hidden" name="predicateUri" value="${param.predicateUri}"/>
        <input type="hidden" name="objectUri"    value="${param.objectUri}"/>    
        <input type="hidden" name="cmd"          value="delete"/>
        <c:if test="${(requestScope.predicate.offerCreateNewOption == false) && (requestScope.predicate.selectFromExisting == false)}">
            <v:input type="submit" id="delete" value="Delete" cancel="cancel" />           
        </c:if>
        <c:if test="${(requestScope.predicate.offerCreateNewOption == true) || (requestScope.predicate.selectFromExisting == true)}">
            <v:input type="submit" id="delete" value="Delete" cancel="" />
        </c:if>
    </form>
</c:if>

<jsp:include page="${postForm}"/>
