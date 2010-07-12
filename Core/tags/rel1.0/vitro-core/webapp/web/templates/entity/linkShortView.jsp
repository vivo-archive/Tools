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
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Link" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%! 
public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.templates.entity.linkShortView.jsp");
%>

<%-- this comes in as an attribute called "individual" but the object has to be created as a Link using the Link dao getLinkByURI() method in order to render correctly
     when the link uri may sometimes be of XSD type anyURI and sometimes of XSD type string --%>
<%                             
Individual ind = (Individual)request.getAttribute("individual");
WebappDaoFactory wdf = (WebappDaoFactory)getServletContext().getAttribute("webappDaoFactory");
if (wdf!=null) {
    Link link = wdf.getLinksDao().getLinkByURI(ind.getURI());                                       
    if (link!=null) {
        request.setAttribute("linkIndividual", link);
    } else {
        log.error("Cannot create linkIndividual from individual "+ind.getUrl());
    }
} else {
    log.error("Cannot create WebappDaoFactory in linkShortView.jsp");
}%>


<c:choose>
	<c:when test="${!empty linkIndividual}">
        <c:choose>
            <c:when test="${!empty linkIndividual.anchor}">
                <c:choose>
                    <c:when test="${!empty linkIndividual.url}">
                        <c:url var="link" value="${linkIndividual.url}" />
                        <a class="externalLink" href="<c:out value="${link}"/>">${linkIndividual.anchor}</a>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${linkIndividual.anchor}"/>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <c:out value="link anchor is not populated"/>
            </c:otherwise>
        </c:choose>
	</c:when>
	<c:otherwise>
		<c:out value="link individual not found ..."/>
	</c:otherwise>
</c:choose>
