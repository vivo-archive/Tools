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

<%@page import="edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.Controllers" %>
<%@page import="edu.cornell.mannlib.vedit.beans.LoginFormBean"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%

if (session == null || !LoginFormBean.loggedIn(request, LoginFormBean.DBA)) {
    %><c:redirect url="<%= Controllers.LOGIN %>" /><%
}


if( request.getParameter("uri") != null ){
    %> <c:redirect url="/entity"><c:param name="uri" value="${param.uri}"/></c:redirect> <%
    return;
}

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>  <!-- gotoIndividual.jsp -->           
    <link rel="stylesheet" type="text/css" href="<c:url value="/${themeDir}css/screen.css"/>" media="screen"/>  
    <link rel="stylesheet" type="text/css" href="<c:url value="/${themeDir}css/formedit.css" />" media="screen"/>
    
    <title>Enter a URI</title>
</head>
<body class="formsEdit">
<div id="wrap">

<% /* BJL23 put this is in a catch block because it seems to fail ungracefully for 
      some clones */ %>
<c:catch>
    <jsp:include page="/${themeDir}jsp/identity.jsp" flush="true"/>
    <div id="contentwrap">
        <jsp:include page="/${themeDir}jsp/menu.jsp" flush="true"/>
        <!-- end of formPrefix.jsp -->
</c:catch>

<form>
<input name="uri" type="text" size="200" />
<input type="submit" value="Lookup Individual for URI"/>
</form>
