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

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.TabMenu" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Portal" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.BreadCrumbsUtil" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.Controllers" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jsp:useBean id="loginHandler" class="edu.cornell.mannlib.vedit.beans.LoginFormBean" scope="session" />

<%
    /***********************************************
     Make the Tab menu div, nothing else.

     bdc34 2006-01-03 created
     **********************************************/
     
    HttpSession currentSession = request.getSession();
    String currentSessionIdStr = currentSession.getId();
    int securityLevel = -1;
    String loginName = null;
    if (loginHandler.testSessionLevel(request) > -1) {
       securityLevel = Integer.parseInt(loginHandler.getLoginRole());
       loginName = loginHandler.getLoginName();
    }
     
    final Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.web.menu.jsp");

    VitroRequest vreq = new VitroRequest(request);
    Portal portal = vreq.getPortal();

    int portalId = -1;
    if (portal==null) {
        log.error("Attribute 'portalBean' missing or null; portalId defaulted to 1");
        portalId=1;
    } else {
        portalId=portal.getPortalId();
    }
    String fixedTabStr=(fixedTabStr=request.getParameter("fixed"))==null?null:fixedTabStr.equals("")?null:fixedTabStr;
%>
<c:set var="currentPortal" value="<%=portal.getPortalId()%>"/>
<!-- ************** START menu.jsp **** -->

<div id="menu">
	<div id="primaryAndOther">
    	<%=TabMenu.getPrimaryTabMenu(vreq)%>
        <ul id="otherMenu">
           
        <%-- A user is logged in --%>
        <% if (securityLevel > 0) { %>

            <c:url var="logoutHref" value="<%= Controllers.LOGOUT_JSP %>">
                <c:param name="home" value="${currentPortal}" />
                <c:param name="loginSubmitMode" value="Log Out" /> 
            </c:url>
  
            <c:url var="siteAdminHref" value="<%= Controllers.SITE_ADMIN %>">
                <c:param name="home" value="${currentPortal}" />
            </c:url>
 
            <li class="border">
                Logged in as <strong><%= loginName %></strong> (<a href="${logoutHref}">Log out</a>)     
            </li>
      
            <li class="border"><a href="${siteAdminHref}" >Site Admin</a></li>
       
        <%-- A user is not logged in --%>
        <% } else { %>
  
            <c:url var="loginHref" value="<%= Controllers.LOGIN %>">
                <c:param name="home" value="${currentPortal}"/>
                <c:param name="login" value="block"/>
            </c:url>
    
            <li class="border"><a title="log in to manage this site" href="${loginHref}">Log in</a></li>
        <% } 
                      
           if ("browse".equalsIgnoreCase(fixedTabStr)) {%>
                <li class="activeTab"><a href="<c:url value="/browsecontroller"/>" title="list all contents by type">Index</a></li>
<%          } else {%>
                <li><a href="<c:url value="/browsecontroller"><c:param name="home" value="${portalBean.portalId}"/></c:url>" title="list all contents by type">Index</a></li>
<%          }

            if ("about".equalsIgnoreCase(fixedTabStr)) {%>
				<c:url var="aboutHref" value="<%= Controllers.ABOUT %>">
					<c:param name="home" value="${currentPortal}"/>
					<c:param name="login" value="none"/>
				</c:url>
				<c:set var="aboutHref">
					<c:out value="${aboutHref}" escapeXml="true"/>
				</c:set>
                <li><a class="activeTab" href="${aboutHref}" title="more about this web site">About</a></li>
<%          } else {%>
				<c:url var="aboutHref" value="<%= Controllers.ABOUT %>">
					<c:param name="home" value="${currentPortal}"/>
					<c:param name="login" value="none"/>
				</c:url>
 				<c:set var="aboutHref">
					<c:out value="${aboutHref}" escapeXml="true"/>
				</c:set>
                <li><a href="${aboutHref}" title="more about this web site">About</a></li>
<%          }                                                    

            if ("comments".equalsIgnoreCase(fixedTabStr)) { %>
                <li class="activeTab"><a href="<c:url value="/comments"><c:param name="home" value="${portalBean.portalId }"/></c:url>">Contact Us</a></li>
<%          } else {%>
                <li><a href="<c:url value="/comments"><c:param name="home" value="${portalBean.portalId }"/></c:url>">Contact Us</a></li>
<%          }%>
		</ul>
	</div><!--END 'primaryAndOther'-->
<%	if( fixedTabStr == null ) {%>
		<div id="secondaryTabMenu">
     		<%=TabMenu.getSecondaryTabMenu(vreq)%> 
  		</div><!--END 'secondaryTabMenu'-->
<%	}%>
</div><!-- END 'menu' -->
<div id="breadcrumbs"><%=BreadCrumbsUtil.getBreadCrumbsDiv(request)%></div>
<!-- ************************ END menu.jsp ************************ -->
