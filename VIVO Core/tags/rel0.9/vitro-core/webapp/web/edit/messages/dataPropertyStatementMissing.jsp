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

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page errorPage="/error.jsp"%>

<c:set var="errorMsg">
The text you are trying to edit cannot be found.
</c:set>

<jsp:include page="/edit/formPrefix.jsp"/>
<div id="content" class="full">
    <div align="center">${errorMsg}</div>


<%
VitroRequest vreq = new VitroRequest(request);
if( vreq.getParameter("subjectUri") != null ){ 
    Individual individual = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(vreq.getParameter("subjectUri"));
    String name = "the individual you were trying to edit.";
    if( individual != null && individual.getName() != null ){ 
          name = individual.getName() + ".";
     } %>    
     <c:url value="/entity" var="entityPage">
       <c:param name="uri"><%=vreq.getParameter("subjectUri")%></c:param>
     </c:url>  
     <div align="center">                
      <button type="button" 
       onclick="javascript:document.location.href='${entityPage}'">
       Return to <%=name%></button>
     </div>
<%}else{ %>
    <c:url value="/" var="siteRoot"/>   
    <div align="center">                
      <button type="button" 
        onclick="javascript:document.location.href='${siteRoot}'">
        Return to main site</button>
    </div>
<%} %>
 
<jsp:include page="/edit/formSuffix.jsp"/>