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

<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditSubmission" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.Controllers" %>
<%@page import="org.apache.commons.logging.Log"%>
<%@page import="org.apache.commons.logging.LogFactory"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jstl/functions" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/string-1.1" prefix="str" %>


<%
    /* Clear any cruft from session. */
    String redirectTo = null;
    String urlPattern = null;
    if( session != null ) {
        EditConfiguration editConfig = EditConfiguration.getConfigFromSession(session,request);
        //In order to support back button resubmissions, don't remove the editConfig from session.
        //EditConfiguration.clearEditConfigurationInSession(session, editConfig);
        
        EditSubmission editSub = EditSubmission.getEditSubmissionFromSession(session,editConfig);        
        EditSubmission.clearEditSubmissionInSession(session, editSub);

        if( editConfig != null && editConfig.getEntityToReturnTo() != null ){
            String predicateUri = editConfig.getPredicateUri();
            log.debug("Return to property after submitting form: " + predicateUri);
            %>
            <c:set var="predicateUri" value="<%=predicateUri%>" />
            <c:set var="localName" value="${fn:substringAfter(predicateUri, '#')}" />
            <%  
                        
            if( editConfig.getEntityToReturnTo().startsWith("?") ){
                redirectTo = (String)request.getAttribute("entityToReturnTo");
            }else{            
                redirectTo = editConfig.getEntityToReturnTo();
            }
              
        }
        if( editConfig != null && editConfig.getUrlPatternToReturnTo() != null){
            urlPattern = editConfig.getUrlPatternToReturnTo();
        }
    }

    if( redirectTo != null ){
        request.setAttribute("redirectTo",redirectTo);    %>

        <%-- <c:redirect url="/entity">
            <c:param name="uri" value="${redirectTo}" />
            <c:param name="property" value="${localName}" />
        </c:redirect> --%>

      <%  if( urlPattern != null && urlPattern.endsWith("entity")){  %>
		      <%-- Here we're building the redirect URL to include an (unencoded) fragment identifier such as: #propertyName  --%>               
		      <c:url context="/" var="encodedUrl" value="/entity">
			     <c:param name="uri" value="${redirectTo}" />
              </c:url>
		      <c:redirect url="${encodedUrl}${'#'}${localName}" />
       <% } else {
              request.setAttribute("urlPattern",urlPattern);%>
              <c:url context="/" var="encodedUrl" value="${urlPattern}">
                 <c:param name="uri" value="${redirectTo}" />
              </c:url>
              <c:redirect url="${encodedUrl}${'#'}${localName}" />                    
		<%} %>
    <% } else { %>
        <c:redirect url="<%= Controllers.LOGIN %>" />
    <% } %>


<%!
Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.edit.postEditCleanUp.jsp");
%>



