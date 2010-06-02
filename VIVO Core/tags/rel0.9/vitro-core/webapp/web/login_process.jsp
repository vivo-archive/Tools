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

<%@ page isThreadSafe="false" %>
<%@ page import="java.util.*" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.Controllers" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%  final int DEFAULT_PORTAL_ID=1;
    String portalIdStr=(portalIdStr=(String)request.getAttribute("home"))==null ?
        ((portalIdStr=request.getParameter("home"))==null?String.valueOf(DEFAULT_PORTAL_ID):portalIdStr):portalIdStr;
        //int incomingPortalId=Integer.parseInt(portalIdStr);    
%>

<jsp:useBean id="loginHandler" class="edu.cornell.mannlib.vedit.beans.LoginFormBean" scope="session">
    <jsp:setProperty name="loginHandler" property="*"/>
</jsp:useBean>

<c:url var="siteAdminUrl" value="<%= Controllers.SITE_ADMIN %>" />

<% 

    String submitModeStr = request.getParameter("loginSubmitMode");
    if ( submitModeStr == null ) {
        submitModeStr = "unknown";
    } 
    
    if ( submitModeStr.equalsIgnoreCase("Log Out")) { %>
        <jsp:forward page="/logout" >
            <jsp:param name="home" value="<%= portalIdStr %>" />
        </jsp:forward>
        
<%  } else if ( submitModeStr.equalsIgnoreCase("Log In")) {
        String loginNameStr = request.getParameter("loginName");
        String loginPasswordStr = request.getParameter("loginPassword"); %>
        <jsp:setProperty name="loginHandler" property="loginName" value="<%= loginNameStr %>" />
        <jsp:setProperty name="loginHandler" property="loginPassword" value="<%= loginPasswordStr %>" />
        <jsp:setProperty name="loginHandler" property="loginRemoteAddr" value="<%= request.getRemoteAddr() %>" />
        
<%      if ( loginHandler.validateLoginForm() ) { %>
            <jsp:forward page="/authenticate" >
                <jsp:param name="home" value="<%= portalIdStr %>" />
            </jsp:forward>
<%      } else {
            String redirectURL = "${siteAdminUrl}?home=" + portalIdStr + "&amp;login=block";
            response.sendRedirect(redirectURL);
       }
    }
%>