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

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Tab" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page errorPage="/error.jsp"%>
<% /***********************************************
 Displays the little group of things at the bottom of the page
 for administrators and editors.

 request.attributes:
 an Tab object with the name "leadingTab"


 request.parameters:
 None, should only work with requestScope attributes for security reasons.

 Consider sticking < % = MiscWebUtils.getReqInfo(request) % > in the html output
 for debugging info.

 bdc34 2006-01-22 created
 **********************************************/

    Tab leadingTab = (Tab) request.getAttribute("leadingTab");
    if (leadingTab == null) {
        String e = "tabAdmin.jsp expects that request attribute 'leadingTab' be set to a TabBean object";
        throw new JspException(e);
    }
%>

<c:if test="${sessionScope.loginHandler.loginStatus == 'authenticated' && sessionScope.loginHandler.loginRole > 3 }">
   	<c:set var='tab' value='${requestScope.leadingTab}'/><%/* just moving this into page scope for easy use */ %>
   	<c:set var='portal' value='${requestScope.portalBean.portalId}'/>
	<div class='admin bottom'>  	
		<c:url var="editHref" value="tabEdit">
			<c:param name="home" value="${currentPortalId}"/>
			<c:param name="controller" value="Tab"/>
			<c:param name="id" value="${tab.tabId}"/>	
		</c:url>
		<c:set var="editHref">
			<c:out value="${editHref}" escapeXml="true"/>
		</c:set>
    	<a href="${editHref}">edit tab: <em>${tab.title}</em></a> 
    	<% /* | <a href='<c:url value="cloneEntity?home=${portal}&tabId=${tab.tabId}"/>'> <i>clone tab</i> ${tab.title}</a> */ %>      
  </div>
</c:if>
