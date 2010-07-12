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

<%@ page	import="java.util.*,edu.cornell.mannlib.vitro.webapp.beans.Individual"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%><%/* this odd thing points to something in web.xml */ %>
<%@ page errorPage="/error.jsp"%>
<%	/***********************************************
		 Display a List of Entities in the most basic fashion.
		 
		 request.attributes:
		 a List of Entity objects with the name "entities" 
		 portal id as "portal"
		 
		 request.parameters:
		 "rows"  is number of rows in gallery table
		 "columns" is number of columns in gallery table
		 
		  Consider sticking < % = MiscWebUtils.getReqInfo(request) % > in the html output for debugging info.
		  		 
		 bdc34 2006-01-27 created		 
        **********************************************/		                      
		if (request.getAttribute("entities") == null){
        	String e="entityListForTabs.jsp expects that request attribute 'entities' be set to a List of Entity objects.";
    	    throw new JspException(e);
        }         
%>
<c:set var='entities' value='${requestScope.entities}' /><%/* just moving this into page scope for easy use */ %>
<c:set var='portal' value='${requestScope.portal}' />
<c:set var='count' value='0'/>

<c:set var='rows'>
	<c:out value="${requestScope.rows}" default="3"/>
</c:set>
<c:set var='columns'>
 	<c:out value="${requestScope.columns}" default="8"/>
</c:set>

<c:set var='IMG_DIR' value='images/' />
<c:set var='IMG_WIDTH' value='100'/>

<table class='tabEntities entityListForGalleryTab'>
	<c:forEach var='row' begin="1" end="${rows}" step="1">
	<tr>
	  	<c:forEach var='col' begin="1" end="${columns}" step="1">
	  		<c:set var='ent' value='${entities[count]}'/>
			<c:set var='count' value='${count + 1}'/>
	  		<c:if test="${ not empty ent and not empty ent.imageThumb}">
			<td>
				<c:url var="entityHref" value="/entity">
					<c:param name="home" value="${portal.portalId}"/>
					<c:param name="uri" value="${ent.URI}"/>
				</c:url>
				<a class="image" href="<c:out value="${entityHref}"/>" >
					<c:url var="imageSrc" value="${IMG_DIR}${ent.imageThumb}"/>
					<img width="${IMG_WIDTH}" src="<c:out value="${imageSrc}"/>" title="${ent.name}" alt="${ent.name}" />
				</a>
			</td>
			</c:if>
		</c:forEach>
	</tr>
	</c:forEach>
</table>


