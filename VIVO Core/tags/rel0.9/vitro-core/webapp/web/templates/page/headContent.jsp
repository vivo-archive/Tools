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

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="javax.servlet.ServletException" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Portal"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean"%>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%! 
public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.templates.page.headContent.jsp");
%>
<%
  VitroRequest vreq = new VitroRequest(request);  
  Portal portal = vreq.getPortal();
  
  String themeDir = portal != null ? portal.getThemeDir() : Portal.DEFAULT_THEME_DIR_FROM_CONTEXT;
  themeDir = request.getContextPath() + '/' + themeDir; 
  
%>
<!-- headContent.jsp -->
<link rel="stylesheet" type="text/css" href="<%=themeDir%>css/screen.css" media="screen"/>
<link rel="stylesheet" type="text/css" href="<%=themeDir%>css/print.css" media="print"/>

<%-- <c:url var="jqueryPath" value="/js/jquery.js"/>
<script type="text/javascript" src="${jqueryPath}"></script> --%>

<link rel="stylesheet" type="text/css" href="<%=themeDir%>css/edit.css"/>
<title><c:out value="${requestScope.title}"/></title>
<%
// nac26 080424: the following line should only be uncommented for PHILLIPS (vivo.cornell.edu) to ensure we're only tracking stats on the live site
// <script type="text/javascript" src="http://vivostats.mannlib.cornell.edu/?js"></script>
%>
<c:if test="${!empty scripts}"><jsp:include page="${scripts}"/></c:if>

<%-- 
mw542 021009: Brian C said this was ignoring the catch tags throwing exceptions. we should find a better way to include css/js anyway

<c:set var="customJsp"><c:out value="${requestScope.bodyJsp}" default="/debug.jsp"/></c:set>
<c:set var="customHeadJsp">
    <c:if test="${fn:substringAfter(customJsp,'.jsp') == ''}">${fn:substringBefore(customJsp,'.jsp')}${"Head.jsp"}</c:if>
</c:set>
<c:if test="${customJsp != '/debug.jsp' && customHeadJsp != ''}">
    <c:catch var="fileCheck">
        <c:import url="${customHeadJsp}"/>
    </c:catch>
</c:if> 
--%>

<!-- end headContent.jsp -->
     