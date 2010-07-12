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

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%><%/* this odd thing points to something in web.xml */ %>
<%@ page errorPage="/error.jsp"%>
<%	/***********************************************
		 Display a List of Entities in the most basic fashion.
		 
		 request.attributes:
		 a List of Entity objects with the name "entities" 
		 portal id as "portal"
		 
		 request.parameters:
		 None yet.
		 
		  Consider sticking < % = MiscWebUtils.getReqInfo(request) % > in the html output for debugging info.
		  		 
		 bdc34 2006-01-27 created		 
        **********************************************/		                      
		if (request.getAttribute("entities") == null){
        	String e="entityList.jsp expects that request attribute 'entities' be set to a List of Entity objects.";
    	    throw new JspException(e);
        }         
%>
<c:set var='entities' value='${requestScope.entities}' /><%/* just moving this into page scope for easy use */ %>
<c:set var='portal' value='${requestScope.portal}' />
<c:set var='title' value='${requestScope.title}' />
<c:set var='subTitle' value='${requestScope.subTitle}' />

<div id="content">
<div class="contents">

<div class="entityList">
<h2>${title}</h2>
<c:if test="${!empty subTitle}"><h4>${subTitle}"</h4></c:if>

<% /* <p>${pageTime} milliseconds</p> */ %>

<ul>
	<c:forEach items='${entities}' var='ent'>
		<li>
        <c:forEach items="${ent.VClasses}" var="type">
            <c:if test="${!empty type.customSearchView}">
                <c:set var="altRenderJsp" value="${type.customSearchView}"/>
            </c:if>
        </c:forEach>
        <c:url var="entHref" value="/entity">
		    <c:param name="home" value="${portal.portalId}"/>
			<c:param name="uri" value="${ent.URI}"/>
		</c:url>
		<a href='<c:out value="${entHref}"/>'><p:process><c:out value="${ent.name}"/></p:process></a> 
	    <c:choose>
		    <c:when test="${!empty ent.moniker}">
			    | <p:process><c:out value="${ent.moniker}"/></p:process>
		    </c:when>
		    <c:otherwise>
			    <c:out value="${ent.VClass.name}"/>
		    </c:otherwise> 
	    </c:choose>
	    <c:choose>
            <c:when test="${!empty altRenderJsp}">
                <c:set scope="request" var="individual" value="${ent}"/>
	  		    <c:if test="${!empty ent.url}">
	  			    <%-- decide here whether to render a link on the included page by setting individualUrl in the request scope --%>
	  			    <c:set scope="request" var="individualURL" value="${ent.url}"/>
	  			</c:if>
                <c:catch var="e">
  			       <jsp:include page="/templates/search/${altRenderJsp}" flush="true"/>
                    <c:remove var="altRenderJsp"/>
                </c:catch>
	  		    <c:if test="${e != null}">
                    <!-- unable to include ${altRenderJsp} -->
                </c:if>
            </c:when>
            <c:otherwise>
	  	        <c:if test="${!empty ent.anchor}"> |
	  		        <c:choose>
	  			        <c:when test="${!empty ent.url}">
	  				        <c:url var="entUrl" value="${ent.url}" />
	  				        <a class="externalLink" href='<c:out value="${entUrl}"/>'><c:out value="${ent.anchor}"/></a>
	  			        </c:when>
	  			        <c:otherwise>
	  				        <i><c:out value="${ent.anchor}"/></i>
	  			        </c:otherwise>
	  		        </c:choose>
	  	        </c:if> 
		        <c:forEach items='${ent.linksList}' var="entLink">
		            | <c:url var="entLinkUrl" value="${entLink.url}" />
		            <a class="externalLink" href="<c:out value='${entLinkUrl}'/>"><c:out value="${entLink.anchor}"/></a>
		        </c:forEach>
            </c:otherwise>
        </c:choose>
        </li>
	</c:forEach>
</ul>
</div> <!--end entityList-->

<% /* add supplementary text provided by controllers such as "CoAuthorServlet" */ %>
<c:if test="${!empty requestScope.suppText}">
	${requestScope.suppText}
</c:if>

</div> <!--end contents-->
</div><!-- end content -->