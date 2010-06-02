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

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Portal" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.jena.pellet.PelletListener"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.Controllers" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.auth.policy.RoleBasedPolicy" %>

<%@ page errorPage="/error.jsp"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%/* this odd thing points to something in web.xml */ %>
<%@ taglib prefix="form" uri="http://vitro.mannlib.cornell.edu/edit/tags" %>

<jsp:useBean id="loginHandler" class="edu.cornell.mannlib.vedit.beans.LoginFormBean" scope="session" />

<%
    Portal portal = (Portal) request.getAttribute("portalBean");
    final String DEFAULT_SEARCH_METHOD = "fulltext"; /* options are fulltext/termlike */     
    
%>

<div id="content">

    <div class="tab">
        <h2>Site Administration</h2>
    </div>
    
	<div id="adminDashboard">
	
	    <%@ include file="loginForm.jsp" %>
	    
	    <%@ include file="dataInput.jsp" %>
	    
	    <%@ include file="siteConfiguration.jsp" %>
	
	    <%@ include file="ontologyEditor.jsp" %>
        
        <%@ include file="advancedDataTools.jsp" %> 
        
        <%@ include file="customReports.jsp" %> 
        <%-- 
        <%@ include file="sessionPreferences.jsp" %>      
        --%>
    </div> <!--  end adminDashboard -->
    
</div> <!-- end content -->


