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
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.VClass" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditSubmission" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page errorPage="/error.jsp"%>

<c:if test="${sessionScope.loginHandler != null &&
             sessionScope.loginHandler.loginStatus == 'authenticated' &&
             sessionScope.loginHandler.loginRole >= sessionScope.loginHandler.editor}">
    <c:set var="showCuratorEdits" value="true"/>
</c:if>
<c:set var='entity' value='${requestScope.entity}'/><%/* just moving this into page scope for easy use */ %>
<c:set var='dashboardPropsListJsp' value='/dashboardPropList'/>
<c:set var='portal' value='${currentPortalId}'/>
<c:set var='portalBean' value='${currentPortal}'/>
<c:set var='imageDir' value='images' />
<div id="dashboard"<c:if test="${showCuratorEdits}"> class="loggedIn"</c:if>>
    <c:if test="${!empty entity.imageThumb}">
        <c:if test="${!empty entity.imageFile}">
            <c:url var="imageUrl" value="${imageDir}/${entity.imageFile}" />
            <a class="image" href="${imageUrl}">
        </c:if>
        <c:url var="imageSrc" value='${imageDir}/${entity.imageThumb}'/>
        <img class="headshot" src="<c:out value="${imageSrc}"/>" title="click to view larger image in new window" alt="" width="150"/>
        <c:if test="${!empty entity.imageFile}"></a></c:if>
        <c:if test="${!empty entity.citation}"><div class="citation">${entity.citation}</div></c:if>
    </c:if>
       
    <ul class="profileLinks">
        <c:if test="${!empty entity.anchor}">
            <c:choose>
                <c:when test="${!empty entity.url}">
                    <c:url var="entityUrl" value="${entity.url}" />
                    <li><a class="externalLink" href="<c:out value="${entityUrl}"/>">${entity.anchor}</a></li>
                </c:when>
                <c:otherwise><li>${entity.anchor}</li></c:otherwise>
            </c:choose>
        </c:if>
       
        <c:if test="${!empty entity.linksList }">
            <c:forEach items="${entity.linksList}" var='link'>
                <c:url var="linkUrl" value="${link.url}" />
                <li><a class="externalLink" href="<c:out value="${linkUrl}"/>">${link.anchor}</a></li>
            </c:forEach>
        </c:if>
    </ul>
    <c:if test="${showCuratorEdits}">
        <c:import url="${dashboardPropsListJsp}">
        	<%-- unless a value is provided, properties not assigned to a group will not appear on the dashboard --%>
        	<c:param name="unassignedPropsGroupName" value=""/>
        </c:import>
    </c:if>
</div>