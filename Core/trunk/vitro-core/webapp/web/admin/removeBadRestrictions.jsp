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


<%
    if(session == null || !LoginFormBean.loggedIn(request, LoginFormBean.CURATOR)) {
        %><c:redirect url="/siteAdmin"></c:redirect><%
    }
  
    if (request.getParameter("execute") != null) {
    	OntModel ontModel = (OntModel) getServletContext().getAttribute(JenaBaseDao.ASSERTIONS_ONT_MODEL_ATTRIBUTE_NAME);
    	int results = doRemoval(ontModel);
    	request.setAttribute("removalCount", results);
    }

%>

<%!
    private int doRemoval(OntModel ontModel) {
	    int removedStmts = 0;
	    List<String> bnodeIds = new ArrayList<String>();
    	ontModel.enterCriticalSection(Lock.READ);
    	try {
    		Iterator<Restriction> restIt = ontModel.listRestrictions();
    		while(restIt.hasNext()) {
    			Restriction rest = restIt.next();
    			if (rest.isAnon()) {
    			    boolean bad = false;
    			    bad |= (rest.getPropertyValue(OWL.onProperty) == null);
    			    bad |= ( !(
    			    		   (rest.getPropertyValue(OWL.someValuesFrom) != null) ||
    			    		   (rest.getPropertyValue(OWL.allValuesFrom) != null)  ||
    			    	       (rest.getPropertyValue(OWL.hasValue) != null)  ||
    			    	       (rest.getPropertyValue(OWL.cardinality) != null)  ||
    			    	       (rest.getPropertyValue(OWL.minCardinality) != null)  ||
    			    	       (rest.getPropertyValue(OWL.maxCardinality) != null)
    			    	      )
    			           );
    			    if (bad) {
    			    	bnodeIds.add(rest.getId().toString());
    			    }
    			}
    		}
    	} finally {
    		ontModel.leaveCriticalSection();
    	}
    	for (String id : bnodeIds) {
    		Model toRemove = describeBnode(id);
    		ontModel.enterCriticalSection(Lock.WRITE);
    		try {
    			ontModel.remove(toRemove);
    		} finally {
    			ontModel.leaveCriticalSection();
    		}
    		removedStmts += toRemove.size();
    	}
    	return removedStmts;
    }
    
    private Model describeBnode(String bnodeId) {
        String describeQueryStr = 
            "PREFIX afn: <http://jena.hpl.hp.com/ARQ/function#> \n\n" +
            "DESCRIBE ?bnode \n" +
            "WHERE { \n" +
            "    FILTER(afn:bnode(?bnode) = \"" + bnodeId + "\")\n" +
            "}";
            
        OntModel ontModel = (OntModel) getServletContext().getAttribute("baseOntModel");
        Model conceptDescription = ModelFactory.createDefaultModel();
        try {
            ontModel.enterCriticalSection(Lock.READ);
            Query describeQuery = QueryFactory.create(describeQueryStr, Syntax.syntaxARQ);
            QueryExecution qe = QueryExecutionFactory.create(describeQuery, ontModel);
            qe.execDescribe(conceptDescription);
            
            conceptDescription.add(ontModel.listStatements((Resource) null, (Property) null, ontModel.createResource(new AnonId(bnodeId))));
            return conceptDescription;
        } finally {
            ontModel.leaveCriticalSection();
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
<%@page import="com.hp.hpl.jena.rdf.model.AnonId"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.dao.jena.JenaBaseDao"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.hp.hpl.jena.ontology.Restriction"%>
<%@page import="com.hp.hpl.jena.vocabulary.OWL"%><html>
<head>
    <title>Remove Bad Restrictions</title>
</head>
<body>
    <c:if test="${!empty requestScope.removalCount}">
        <p>${removalCount} statements removed</p>
    </c:if>

    <h1>Remove Bad Restrictions</h1>
    <form action="" method="post">
        <p><input name="execute" type="submit" value="Remove now"/></p>
    </form>
</body></html>    
