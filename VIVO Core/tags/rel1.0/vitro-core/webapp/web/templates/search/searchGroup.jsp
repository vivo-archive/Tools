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

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%/* this odd thing points to something in web.xml */ %>
<%@ page errorPage="/error.jsp"%>
<%	/***********************************************
		 Display a single search result group
		 
		 request.attributes:
		 a List with objects with the named "entities" 
		 a ClassGroup object named "classgroup"
		 
		 request.parameters:
		 None yet.
		 
		  Consider sticking < % = MiscWebUtils.getReqInfo(request) % > in the html output
		  for debugging info.		  		 		 		
        **********************************************/		                      
		if (request.getAttribute("entities") == null){
        	String e="searchGroup.jsp expects that request attribute 'entities' be set to the Entity object to display.";
    	    throw new JspException(e);
        }
        if (request.getAttribute("classgroup") == null){
        	String e="searchGroup.jsp expects that request attribute 'classgroup' be set to the Entity object to display.";
    	    throw new JspException(e);
        }
%>
<c:set var='imageDir' value='images' />
<c:set var='entities' value='${requestScope.entities}'/><%/* just moving this into page scope for easy use */ %>
<c:set var='classgroup' value='${requestScope.classgroup}'/>

<c:set var='portal' value='${requestScope.portal}'/>
<c:set var='portalBean' value='${requestScope.portalBean}'/>

		<div class='contents entity entity${entity.id}'>
				<h1><c:out value="${entity.name}"/></h1>
				<c:out value="${entity.moniker}" default="moniker?"/>		
				<c:if test="${!empty entity.anchor}">
					<a href='<c:url value="${entity.url}"/>'>${entity.anchor}</a>
				</c:if>
				<c:forEach items="${entity.linksList}" var='link'>
					| <a href='<c:url value="${link.url}"/>'${link.anchor}</a>
				</c:forEach>
				<c:if test="${!empty entity.imageThumb}">
				<div class="thumbnail">
					<c:if test="${!empty entity.imageFile}"><a target="_new" href="<c:url value='${imageDir}/${entity.imageFile}'/>"></c:if>
					<img src="<c:url value='${imageDir}/${entity.imageThumb}'/>" title="click to view larger image in new window" width="150">
					<c:if test="${!empty entity.imageFile}"></a></c:if>
				</div>
				</c:if>
				<c:import url="${entityPropsListJsp}" /><%/* here we import the properties for the entity */ %>
				<div class='description'>
				<c:out value="${entity.description}" escapeXml ='false'/>
				</div>
				<jsp:include page="entityAdmin.jsp"/>
		</div>
