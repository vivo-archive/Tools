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

<%@ page import="com.hp.hpl.jena.ontology.Individual" %>
<%@ page import="com.hp.hpl.jena.ontology.OntModel" %>
<%@ page import="com.hp.hpl.jena.rdf.model.ModelMaker" %>
<%@ page import="com.hp.hpl.jena.shared.Lock" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%

    ModelMaker maker = (ModelMaker) request.getSession().getAttribute("vitroJenaModelMaker");
    if (maker == null) {
        maker = (ModelMaker) getServletContext().getAttribute("vitroJenaModelMaker");
    }

%>

    
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%><h2>Execute SPARQL CONSTRUCT Query</h2>

    <p><a href="ingest">Ingest Home</a></p>

    <c:if test="${requestScope.constructedStmtCount != null}">
        <h3 class="notice">${requestScope.constructedStmtCount} statements CONSTRUCTed</h3>
    </c:if>

    <c:if test="${errorMsg != null}"> 
        <h3 class="error">${requestScope.errorMsg}</h3>
    </c:if>

    <c:if test="${requestScope.validationMessage != null}">
        <h3 class="notice">${requestScope.validationMessage}</h3>
    </c:if>

    <form action="ingest" method="post">
        <input type="hidden" name="action" value="executeSparql"/>

    <h3>SPARQL Query
   
        <select name="savedQuery">
             <option value="">select saved query</option>
        <%
              OntModel jenaOntModel = (OntModel) getServletContext().getAttribute("jenaOntModel");
              jenaOntModel.enterCriticalSection(Lock.READ);
              try {
                  List savedQueries = (List) request.getAttribute("savedQueries");
	          for (Iterator it = savedQueries.iterator(); it.hasNext();)  {
	              Individual savedQuery = (Individual) it.next();
                      String queryURI = savedQuery.getURI();
                      String queryLabel = savedQuery.getLabel(null);
                      %> <option value="<%=queryURI%>"><%=queryLabel%></option> <%
                  }
              } finally {
                  jenaOntModel.leaveCriticalSection();
	      }
        %>
        </select>
  
        <textarea rows="16" cols="40" name="sparqlQueryStr"><c:choose>
    <c:when test="${param.sparqlQueryStr != null}">
        ${param.sparqlQueryStr}
    </c:when>
    <c:otherwise>
PREFIX rdf:   &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt;
PREFIX rdfs:  &lt;http://www.w3.org/2000/01/rdf-schema#&gt;
PREFIX owl:   &lt;http://www.w3.org/2002/07/owl#&gt;
PREFIX xsd:   &lt;http://www.w3.org/2001/XMLSchema#&gt;
PREFIX vitro: &lt;http://vitro.mannlib.cornell.edu/ns/vitro/0.7#&gt;
</c:otherwise>
</c:choose>


</textarea>

    <h3>Select Source Models</h3>

    <ul>
<%
	List<String> sourceModelNameList = new ArrayList<String>();
	String[] sourceModelParamVals = request.getParameterValues("sourceModelName");
	if (sourceModelParamVals != null) {
	    sourceModelNameList.addAll(Arrays.asList(sourceModelParamVals));
	}
%>

		  <li><input type="checkbox" name="sourceModelName" value="vitro:jenaOntModel"
                    <%
                        if (sourceModelNameList.contains("vitro:jenaOntModel")) {
                        	%>checked="checked"<%
                        }
                    %>
               />webapp model</li>
		  <li><input type="checkbox" name="sourceModelName" value="vitro:baseOntModel"
                    <%
                        if (sourceModelNameList.contains("vitro:baseOntModel")) {
                            %>checked="checked"<%
                        }
                    %>
               />webapp assertions</li>
<%
    for (Iterator it = maker.listModels(); it.hasNext(); ) {
	String modelName = (String) it.next();
        %> <li> <input type="checkbox" name="sourceModelName" value="<%=modelName%>"
                    <%
                        if (sourceModelNameList.contains(modelName)) {
                        	%> checked="checked" <%
                        }
                    %>
                /><%=modelName%></li>
        <%    
    }
%>
    </ul>

    <h3>Select Destination Model</h3>

    <select name="destinationModelName">
           <option value="vitro:jenaOntModel"
              <% if ("vitro:jenaOntModel".equals(request.getParameter("destinationModelName"))) {
            	  %> selected="selected" <%
            	 }
              %>
            />webapp model</option>
           <option value="vitro:baseOntModel"
              <% if ("vitro:baseOntModel".equals(request.getParameter("destinationModelName"))) {
                  %> selected="selected" <%
                 }
              %>
            />webapp assertions</option>
<%
    for (Iterator it = maker.listModels(); it.hasNext(); ) {
    String modelName = (String) it.next();
        %> <option value="<%=modelName%>" 
                <%
                 if (modelName.equals(request.getParameter("destinationModelName"))) {
                     %> selected="selected" <%
                 }
                %>
           /><%=modelName%></option>
        <%    
    }
%>   
    </select>

    <c:choose>
	    <c:when test="${paramValues['reasoning'] != null}">
	        <c:forEach var="paramValue" items="${paramValues['reasoning']}">
	            <c:if test="${paramValue eq 'pellet'}">
	                <p><input type="checkbox" name="reasoning" value="pellet" checked="checked"/> include pellet reasoning </p>
	            </c:if>
	        </c:forEach>
	    </c:when>
        <c:otherwise>
                    <p><input type="checkbox" name="reasoning" value="pellet" /> include Pellet OWL-DL reasoning </p>
        </c:otherwise>
    </c:choose>

    <input type="submit" value="Execute CONSTRUCT"/>
           