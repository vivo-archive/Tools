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


<%@page import="edu.cornell.mannlib.vitro.webapp.utils.jena.SesameSyncUtils"%>
<%@page import="com.hp.hpl.jena.rdf.model.ModelFactory"%>
<%@page import="com.hp.hpl.jena.shared.Lock"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.dao.jena.JenaModelUtils"%>
<%@page import="com.hp.hpl.jena.rdf.model.Model"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.dao.jena.JenaBaseDao"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.util.Properties"%>
<%@page import="edu.cornell.mannlib.vedit.beans.LoginFormBean"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.Controllers" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>


<%!

    final String SESAME_PROPS_PATH = "/WEB-INF/classes/sesame.sync.properties" ;
    final String SESAME_SERVER = "vitro.sesame.server" ;
    final String SESAME_REPOSITORY = "vitro.sesame.repository" ;
    final String SESAME_CONTEXT = "vitro.sesame.context" ;
    
    final String USER_SPARQL_QUERY =
    	"PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
        "PREFIX vitro: <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#> \n\n" +
        "DESCRIBE ?user WHERE { \n " +
        "    ?user rdf:type vitro:User \n" +
        "}";

%>

<%

	if(session == null || !LoginFormBean.loggedIn(request, LoginFormBean.DBA)) {
	    %>
        <c:redirect url="<%= Controllers.LOGIN %>" />
        <%
	}

    long startTime = System.currentTimeMillis();

    Properties sesameProperties = new Properties();
    InputStream propStream = getServletContext().getResourceAsStream(SESAME_PROPS_PATH);
    if (propStream == null) {
    	response.setStatus(500, "Sesame properties not found at " + SESAME_PROPS_PATH);
    	return;
    }
    sesameProperties.load(propStream);
    String sesameLocation = sesameProperties.getProperty(SESAME_SERVER);
    if (sesameLocation == null) {
    	response.setStatus(500, "Missing property " + SESAME_SERVER);
    }
    String sesameRepository = sesameProperties.getProperty(SESAME_REPOSITORY);
    if (sesameRepository == null) {
    	response.setStatus(500, "Missing property " + SESAME_REPOSITORY);
    }
    String contextId = sesameProperties.getProperty(SESAME_CONTEXT);
    
    Model fullModel = (Model) getServletContext().getAttribute(JenaBaseDao.JENA_ONT_MODEL_ATTRIBUTE_NAME);
    // Copy the model to avoid locking the main model during sync.  Assumes enough memory.
    Model copyModel = ModelFactory.createDefaultModel();
    fullModel.enterCriticalSection(Lock.READ);
    try {
    	copyModel.add(fullModel); 
    } finally {
    	fullModel.leaveCriticalSection();
    }
    
    Model userDataToRetract = ModelFactory.createDefaultModel();
    Query userDataQuery = QueryFactory.create(USER_SPARQL_QUERY);
    QueryExecution qe = QueryExecutionFactory.create(userDataQuery, copyModel);
    qe.execDescribe(userDataToRetract);
    copyModel.remove(userDataToRetract);
    
    System.out.println("Not sharing " + userDataToRetract.size() + " statements of user data");
    
    System.out.println("Using Sesame server at " + sesameLocation);
    System.out.println("Using Sesame repository at " + sesameRepository);
    System.out.println("Using context " + contextId);
    
    try {
        (new SesameSyncUtils()).writeModelToSesameContext(copyModel, sesameLocation, sesameRepository, contextId);
    } catch (Throwable t) {
    	t.printStackTrace();
    	throw new Error(t);
    }
    
    System.out.println((System.currentTimeMillis() - startTime) + " ms to sync");

%>


<%@page import="com.hp.hpl.jena.rdf.model.StmtIterator"%>
<%@page import="com.hp.hpl.jena.rdf.model.Statement"%>
<%@page import="com.hp.hpl.jena.query.Query"%>
<%@page import="com.hp.hpl.jena.query.QueryFactory"%>
<%@page import="com.hp.hpl.jena.query.QueryExecution"%>
<%@page import="com.hp.hpl.jena.query.QueryExecutionFactory"%><html>
    <head>
        <title>Sync successful</title>
    </head>
</html>
