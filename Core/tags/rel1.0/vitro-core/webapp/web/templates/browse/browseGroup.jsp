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

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.VClassGroup,edu.cornell.mannlib.vitro.webapp.beans.VClass" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%/* this odd thing points to something in web.xml */ %>
<%@ page errorPage="/error.jsp"%>

<%@page import="com.hp.hpl.jena.vocabulary.OWL"%><jsp:useBean id="loginHandler" class="edu.cornell.mannlib.vedit.beans.LoginFormBean" scope="session" />
<%  /***********************************************
         Display Browse Results (the "Index" menu command)

         request.attributes:
         a List with the name "classgroups"
         collatedGroupNames
         additionalParameterStr

         request.parameters:
         None yet.

          Consider sticking < % = MiscWebUtils.getReqInfo(request) % > in the html output
          for debugging info.

        **********************************************/
if (request.getAttribute("classgroups") == null){
    String e="browsesGroup.jsp expects that request attribute 'classgroups' be set to a List of classgroups to display.";
    throw new JspException(e);
}
if (request.getAttribute("portalState") == null){
    String e="browsesGroup.jsp expects that request attribute 'portalState' be set to a portal state [PortalFlag] object.";
    throw new JspException(e);
}

String additionalParameterStr = ""; //we expect this to already be encoded as a url.
if(request.getAttribute("passthru") != null){
    additionalParameterStr = (String)request.getAttribute("passthru");
}

if( request.getAttribute("classgroupsIsEmpty") != null && ((Boolean)request.getAttribute("classgroupsIsEmpty")) == true){
	%>

	<div id="content" class="siteMap">
	<p>There not yet any items in the system.</p>
	</div> <!-- content -->
<% } else { %>		
<div id="content" class="siteMap">
    <form name="filterForm" action="browsecontroller" method="post" class="padded" >
    <jsp:include page="portalFlagChoices.jsp" flush="true" >
        <jsp:param name="action" value="browse" />
    </jsp:include>
    </form>
    <%
        Collection classgroupList = (Collection) request.getAttribute("classgroups");
        if (classgroupList != null) {
            Iterator groupIter = classgroupList.iterator();
            Object groupObj = null;
            while (groupIter.hasNext()) {
                groupObj = groupIter.next();
                if (groupObj != null && groupObj instanceof VClassGroup) {
                    VClassGroup theGroup = (VClassGroup) groupObj; %>
                <h2><%=theGroup.getPublicName()%></h2>
    <%          if (theGroup.getVitroClassList()!=null && theGroup.getVitroClassList().size()>0) {%>
                    <ul>
    <%              Iterator classIter=theGroup.getVitroClassList().iterator();
                    Object classObj=null;
                    while (classIter.hasNext()) {
                        classObj = classIter.next();
                        if (classObj!=null && classObj instanceof VClass) {
                            VClass theClass=(VClass)classObj;
                            //filter out owl:Thing
                            if( theClass.getName() == null || OWL.Thing.getURI().equals(theClass.getURI()))
                            	continue;
                           	String linkStr=response.encodeURL("entitylist");
               				if (theClass.getURI() == null)
                   				theClass.setURI("null://null");
                           	String queryStr="?vclassId="+URLEncoder.encode(theClass.getURI(),"UTF-8")+additionalParameterStr; %>
                           	<li><a href="<%=linkStr+queryStr%>"><%=theClass.getName()%></a> (<%=theClass.getEntityCount()%>)</li>
    <%                  }
                    }%>
                    </ul>
    <%          } else {%>
                    <ul><li>no entities</li></ul>
    <%          }
            }
        }
    }%>
</div> <!-- content -->
<% } %> 