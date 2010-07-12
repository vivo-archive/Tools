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

<%@ page import="com.hp.hpl.jena.rdf.model.*" %>
<%@ page import="com.hp.hpl.jena.ontology.OntModel" %>
<%@ page import="com.hp.hpl.jena.shared.Lock" %>
<%@ page import="com.thoughtworks.xstream.XStream" %>
<%@ page import="com.thoughtworks.xstream.io.xml.DomDriver" %>
<%@ page import="edu.cornell.mannlib.vedit.beans.LoginFormBean" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.EditLiteral" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditN3Generator" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditSubmission" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.Field" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="java.io.StringReader" %>
<%@ page import="java.util.*" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.NetIdIdentifierFactory.NetId"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.NetIdIdentifierFactory"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.SelfEditingUriFactory.SelfEditing"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.SelfEditingUriFactory"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.ArrayIdentifierBundle"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle"%>
<%@page import="java.io.IOException"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>


<http>
Testing getIndividualURIFromNetId()
<% 
  String[] netids = {"bdc34", "jc55", "bjl24" , "mhd6" , "tpb2" };
  for( String netid : netids){
      %><h2>Checking <%=netid %></h2><%
    checkNetId( netid, out, request, (WebappDaoFactory)application.getAttribute("webappDaoFactory"));   
  }  
%>
</http>


<%! 

final  String CUWEBAUTH_REMOTE_USER_HEADER = "REMOTE_USER"; 


private void checkNetId( String inNetId, JspWriter out, HttpServletRequest request, WebappDaoFactory wdf ) throws IOException{ 

    if( inNetId != null
            && inNetId.length() > 0
            && inNetId.length() < 100 ){
    
        NetIdIdentifierFactory.NetId netid = new NetId(inNetId);                        
        SelfEditingUriFactory.SelfEditing selfE = null;
                
        IdentifierBundle idb = new ArrayIdentifierBundle();
        idb.add(netid);
        //out.println("added NetId object to IdentifierBundle from CUWEBAUTH header");            
        
        //VitroRequest vreq = new VitroRequest((HttpServletRequest)request);
        String uri = wdf.getIndividualDao().getIndividualURIFromNetId(inNetId);
    
       
        if( uri != null){
                    Individual ind = wdf.getIndividualDao().getIndividualByURI(uri);
                    if( ind != null ){
                        selfE = new SelfEditing( ind, null );                
                        idb.add(  selfE );
                        out.println("found a URI and an Individual for " + inNetId +  " URI: " + ind.getURI());
                    }else{
                            out.println("found a URI for the netid " + inNetId + " but could not build Individual");
                    }
        }else{
            out.println("could not find a Individual with the neditd of " + inNetId );
        }
            //putNetIdInSession(session, selfE, netid);            
        
    }else{
        out.println("no remote user value found or value was longer than 100 chars.");    
    }
}
%>