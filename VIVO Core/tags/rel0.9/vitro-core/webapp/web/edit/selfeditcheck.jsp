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

<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditSubmission" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.Field" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="java.io.StringReader" %>
<%@ page import="java.util.*" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.SelfEditingIdentifierFactory.NetId"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.SelfEditingIdentifierFactory"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.SelfEditingIdentifierFactory.SelfEditing"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.ServletIdentifierBundleFactory"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.ArrayIdentifierBundle"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle"%>
<%@page import="java.io.IOException"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundleFactory"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.policy.SelfEditingPolicy"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AddObjectPropStmt"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>


<h1>SelfEditing Sanity Check</h1>

<h3>Is there a Factory that will create self editing identifiers?</h3>
<%
    ServletIdentifierBundleFactory sibf = ServletIdentifierBundleFactory.getIdentifierBundleFactory(application);
String found = "Self editing identifier factory found.";
for( IdentifierBundleFactory ibf : sibf ){
   if( ibf instanceof SelfEditingIdentifierFactory ){
       found = "Found a self editing identifier factory.";
       break;
   }
}
%>
<%= found %>


<h3>Is there a self editing policy in the context?</h3>
<% 
ServletPolicyList spl = ServletPolicyList.getPolicies(application);
SelfEditingPolicy sePolicy = null;
ListIterator it = spl.listIterator();
found = "Could not find a SelfEditingPolicy";
while(it.hasNext()){
    PolicyIface p = (PolicyIface)it.next();
    if( p instanceof SelfEditingPolicy ){
        found = "Found a SelfEditingPolicy";
        sePolicy = (SelfEditingPolicy)p;
    }
}
%>
<%= found %>

<h3>Do you have a REMOTE_USER header from CUWebAuth?</h3>

<% String user = request.getHeader("REMOTE_USER"); 
if( user != null && user.length() > 0){
    %> Found a remote user of <%= user %>. <%
}else{
    %> Could not find a remote user.  Maybe you are not logged into CUWebAutn? <%
}
 %>
 <h3>Check if we can get a SelfEditingIdentifer for <%= user %></h3>
 <%
 SelfEditingIdentifierFactory.SelfEditing selfEditingId = null;
 IdentifierBundle ib  = null;
if( user != null && user.length() > 0){
  ib = sibf.getIdentifierBundle(request,session,application);
  if( ib != null ) {
       for( Object obj : ib){
           if( obj instanceof SelfEditingIdentifierFactory.SelfEditing )
               selfEditingId = (SelfEditingIdentifierFactory.SelfEditing) obj;
       }
       if( selfEditingId != null )
           found = "found a SelfEditingId " + selfEditingId.getValue();
       else
           found = "Cound not find a SelfEditingId";
  }else{
      found = "Could not get any identififers";
  }  
%>
  <%= found %>
<%}else{%> 
   Cannot check becaue user is <%= user %>.
<%} %>   


<h3>Is that SelfEditingIdentifer blacklisted?</h3>
<% if( user == null || user.length() == 0 ){ %>
 No REMOTE_USER to check 
<% }else if( selfEditingId == null ){ %>
     no SelfEditingId to check   
<% }else if( selfEditingId.getBlacklisted() != null){%>
    SelfEditingId blacklisted because of <%= selfEditingId.getBlacklisted() %>
<% } else {%>
    SelfEditingId is not blacklisted.
<% } %>

<h3>Can an object property be edited with this SelfEditingId and Policy?</h3>
<% if( user == null || selfEditingId == null ){ %>
No
<% }else{ 
    AddObjectPropStmt whatToAuth = new AddObjectPropStmt(
           selfEditingId.getValue(),"http://mannlib.cornell.edu/fine#prp999" ,"http://mannlib.cornell.edu/fine#prp999");                
    PolicyDecision pdecison = sePolicy.isAuthorized(ib, whatToAuth);         
%> The policy decision was <%= pdecison %> 
  
<% } %>