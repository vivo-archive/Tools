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

<%@ page import="edu.cornell.mannlib.vedit.beans.LoginFormBean" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.Controllers" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.FakeSelfEditingIdentifierFactory" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%
    if(session == null || !LoginFormBean.loggedIn(request, LoginFormBean.CURATOR)) {
        %><c:redirect url="<%= Controllers.LOGIN %>" /><%
    }

    if(  request.getParameter("force") != null ){        
        VitroRequestPrep.forceToSelfEditing(request);
        String netid = request.getParameter("netid");
        FakeSelfEditingIdentifierFactory.clearFakeIdInSession( session );
        FakeSelfEditingIdentifierFactory.putFakeIdInSession( netid , session );%>
        <c:redirect url="/entity">
            <c:param name="netid" value="<%=netid%>" />
        </c:redirect>
<%    }
    if( request.getParameter("stopfaking") != null){
        VitroRequestPrep.forceOutOfSelfEditing(request);
        FakeSelfEditingIdentifierFactory.clearFakeIdInSession( session );
    }    
    String netid = (String)session.getAttribute(FakeSelfEditingIdentifierFactory.FAKE_SELF_EDIT_NETID);
    String msg = "You have not configured a netid for testing self-editing. ";
    if( netid != null ) {
        msg = "You have are testing self-editing as '" + netid + "'.";%>
        <c:redirect url="/entity">
        	<c:param name="netid" value="<%=netid%>"/>
       	</c:redirect>
<%  } else {
        netid = "";
    }
        
%>


<html>
<title>Test Self-Edit</title>
<body>
<h2>Configure Self-Edit Testing</h2>
<p><%=msg %></p>
<form action="<c:url value="fakeselfedit.jsp"/>" >
    <input type="text" name="netid" value="<%= netid %>"/>
    <input type="hidden" name="force" value="1"/>
    <input type="submit" value="use a netid for testing"/>
</form>

<p/>

<form action="<c:url value="fakeselfedit.jsp"/>" >
    <input type="hidden" name="stopfaking" value="1"/>
    <input type="submit" value="stop usng netid for testing"/>
</form>

</body>
</html>
