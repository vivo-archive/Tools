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

<%@ page language="java"%>
<%@ page import="java.util.Calendar" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.*"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Portal" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%/* this odd thing points to something in web.xml */ %>

<jsp:useBean id="loginHandler" class="edu.cornell.mannlib.vedit.beans.LoginFormBean" scope="session" />
<%
    /**
     * @version 1.00
     * @author Jon Corson-Rikert
     * UPDATES:
     * 2006-01-04   bdc   removed <head> and <body> tags and moved from <table> to <div>
     * 2005-07-07   JCR   included LoginFormBean so can substitute filterbrowse for portalbrowse for authorized users
     */

    final Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.web.themes.default.footer.jsp");

    VitroRequest vreq = new VitroRequest(request);

    Portal portal = vreq.getPortal();
    if (portal==null) {
    	log.error("portal from vreq.getPortal() null in themes/default/footer.jsp");
    }
    HttpSession currentSession = request.getSession();

    boolean authorized = false;
    if (loginHandler.getLoginStatus().equals("authenticated")) /* test if session is still valid */
        if (currentSession.getId().equals(loginHandler.getSessionId()))
            if (request.getRemoteAddr().equals(
                    loginHandler.getLoginRemoteAddr()))
                authorized = true;
%>
<c:set var="currentYear" value="<%=  Calendar.getInstance().get(Calendar.YEAR) %>" />
<div class='footer'><div class='footerLinks'>
	<% String rootBreadCrumb = BreadCrumbsUtil.getRootBreadCrumb(vreq,"",portal); 
	   if (rootBreadCrumb != null && rootBreadCrumb.length()>0) { %>
	    <%=rootBreadCrumb%> | 
	<% } %>
      <a href="<%=(authorized?"browsecontroller":"browsecontroller")%>?home=<%=portal.getPortalId()%>">Index</a>
    | <a href="comments?home=<%=portal.getPortalId()%>">Contact Us</a>
    <c:if test="${sessionScope.loginHandler.loginStatus == 'authenticated' && sessionScope.loginHandler.loginRole > 3 }">
        | admin [
	    <a href="http://validator.w3.org/check?uri=referer">validate xhtml</a>
	    <a href="http://jigsaw.w3.org/css-validator/check/referer">validate css</a>
	    ]
    </c:if>
    </div>
    <% if (portal.getCopyrightAnchor() != null && portal.getCopyrightAnchor().length()>0) { %> 
	    <div class='copyright'>
		    &copy;${currentYear}&nbsp;
			<% if (portal.getCopyrightURL() != null && portal.getCopyrightURL().length()>0) { %>
				<a href="<%=portal.getCopyrightURL()%>">
			<% } %>
			<%=portal.getCopyrightAnchor()%>
			<% if (portal.getCopyrightURL() != null && portal.getCopyrightURL().length()>0) { %>
				</a>.
			<% } %>
	    </div>
	    <div class='copyright'>
		    All Rights Reserved. <a href="termsOfUse?home=<%=portal.getPortalId()%>">Terms of Use</a>
	    </div>
	<% } %> 
</div>
