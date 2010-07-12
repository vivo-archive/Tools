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

<%

    ModelMaker maker = (ModelMaker) request.getSession().getAttribute("vitroJenaModelMaker");
    if (maker == null) {
        maker = (ModelMaker) getServletContext().getAttribute("vitroJenaModelMaker");
    }

%>

    <p><a href="ingest">Ingest Home</a></p>

    <h2>Convert CSV to RDF</h2>

    <form action="ingest" method="get"i>
        <input type="hidden" name="action" value="csv2rdf"/>

	<p><input type="radio" name="separatorChar" value="comma" checked="checked"/> comma separated 
	<input type="radio" name="separatorChar" value="tab"/> tab separated </p>

    <input type="text" style="width:80%;" name="csvUrl"/>
    <p>CSV file URL (e.g. "file:///")</p>

    <input type="text" name="namespace"/>
    <p>Namespace in which to generate resources</p>

	<input type="text" name="tboxNamespace"/>
    <p>Namespace in which to generate class and properties</p>

<!-- 
 <input type="checkbox" name="discardTbox"/> do not add TBox or RBox to result model
-->

    <input type="text" name="typeName"/>
    <p>Class Name for Resources</p>

    <select name="destinationModelName">
        <option value="vitro:baseOntModel">webapp assertions</option>
<%
    for (Iterator it = maker.listModels(); it.hasNext(); ) {
	String modelName = (String) it.next();
        %> <option value="<%=modelName%>"><%=modelName%></option>
        <%    
    }
%>    <option value="">(none)</option>
    </select>
    <p>Destination Model</p>

   <select name="tboxDestinationModelName">
        <option value="vitro:baseOntModel">webapp assertions</option>
<%
    for (Iterator it = maker.listModels(); it.hasNext(); ) {
	String modelName = (String) it.next();
        %> <option value="<%=modelName%>"><%=modelName%></option>
        <%    
    }
%>    <option value="">(none)</option>
    </select>
    <p>Destination Model for TBox</p>

    <input type="submit" value="Convert CSV"/>
