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

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%@ page import="edu.cornell.mannlib.vedit.beans.LoginFormBean" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.Controllers" %>

<%
    if(session == null || !LoginFormBean.loggedIn(request, LoginFormBean.CURATOR)) {
        %><c:redirect url="<%= Controllers.LOGIN %>" /><%
    }
  
    String conceptIdStr = request.getParameter("conceptId");
    if (conceptIdStr != null) {
    	
    	String describeQueryStr = 
    		"PREFIX afn: <http://jena.hpl.hp.com/ARQ/function#> \n\n" +
    		"DESCRIBE ?bnode \n" +
    	    "WHERE { \n" +
    		"    FILTER(afn:bnode(?bnode) = \"" + conceptIdStr + "\")\n" +
    	    "}";
    	    
    	OntModel ontModel = (OntModel) getServletContext().getAttribute("baseOntModel");
    	Model conceptDescription = ModelFactory.createDefaultModel();
    	try {
    		ontModel.enterCriticalSection(Lock.READ);
    		Query describeQuery = QueryFactory.create(describeQueryStr, Syntax.syntaxARQ);
    		QueryExecution qe = QueryExecutionFactory.create(describeQuery, ontModel);
    		qe.execDescribe(conceptDescription);
    		
    		conceptDescription.add(ontModel.listStatements((Resource) null, (Property) null, ontModel.createResource(new AnonId(conceptIdStr))));
    		
    	} finally {
    		ontModel.leaveCriticalSection();
    	}
    	
    	
    	
    	List<String> actionStrList = (request.getParameterValues("action") != null)
    	   ? Arrays.asList(request.getParameterValues("action"))
    	   : new ArrayList<String>();
    	if (actionStrList.contains("remove")) {
    		try {
    			ontModel.enterCriticalSection(Lock.WRITE);
    			ontModel.remove(conceptDescription);
    		} finally {
    			ontModel.leaveCriticalSection();
    		}
    	}
    	if (actionStrList.contains("describe")) {
    		conceptDescription.write(response.getOutputStream(), "TTL");
    		return;
    	}
    	
    }

%>


<%@page import="com.hp.hpl.jena.ontology.OntModel"%>
<%@page import="com.hp.hpl.jena.shared.Lock"%>
<%@page import="com.hp.hpl.jena.query.Syntax"%>
<%@page import="com.hp.hpl.jena.query.Query"%>
<%@page import="com.hp.hpl.jena.query.QueryFactory"%>
<%@page import="com.hp.hpl.jena.query.QueryExecutionFactory"%>
<%@page import="com.hp.hpl.jena.rdf.model.ModelFactory"%>
<%@page import="com.hp.hpl.jena.rdf.model.Model"%>
<%@page import="com.hp.hpl.jena.query.QueryExecution"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.hp.hpl.jena.rdf.model.Resource"%>
<%@page import="com.hp.hpl.jena.rdf.model.Property"%>
<%@page import="com.hp.hpl.jena.rdf.model.AnonId"%><html>
<head>
    <title>Anonymous Concept Repair Tools</title>
</head>
<body>
    <h1>Concept Repair</h1>
    <form action="" method="post">
        <p>Concept bnode id: <input name="conceptId"/></p>
        <p><input type="checkbox" name="action" value="describe"/> describe concept</p>
        <p><input type="checkbox" name="action" value="remove"/> remove concept</p> 
        <p><input type="submit" value="Perform action"/></p>
    </form>
</body></html>    
