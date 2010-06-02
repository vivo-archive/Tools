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

<%@ page import="java.lang.Integer" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.TabWebUtil" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Tab" %>
<%@ page errorPage="/error.jsp"%>
<%
    int CutOffDepth = 3; //tab depth at which subtabs stop being shown
    int TabDescCutoffDepth = 3;//depth at which descriptions of subtabs stop being shown
    /***********************************************
     Display a set of subtabs for a Tab as a <UL></UL>
     Based on tabSub.
     This does not attempt to display any subtabs, entities, etc.

     request.attributes
     "leadingTab" the tab that is at the top of the display hierarchy

     request.parameters
     "tabId" id of the tab to do subtabs for

     "tabDepth" String that is the depth of the tab in the display
     leadingTab = 1, child of leadingTab = 2, etc.
     Here tabDepth does not default, it must be set

     bdc34 2006-01-18 created
     **********************************************/
    Tab leadingTab = (Tab) request.getAttribute("leadingTab");
    if (leadingTab == null) {
        String e = "tabSub expects that request attribute 'leadingTab' be set";
        throw new JspException(e);
    }

    String tabId = request.getParameter("tabId");
    if (tabId == null) {
        String e = "tabSub expects that request parameter 'tabId' be set";
        throw new JspException(e);
    }

    Tab tab = null;
    tab = TabWebUtil.findStashedTab(tabId, request);
    if (tab == null) {
        String e = "tabSub expects that request attribute 'leadingTab' will have the tab with tabId as a sub tab";
        throw new JspException(e);
    }

    String obj = request.getParameter("tabDepth");
    int depth = 1; //depth 1 represents primary tab level, 2 is secondary, etc.
    if (obj == null) {
        String e = "tabSub expects that request parameter 'tabDepth' be set";
        throw new JspException(e);
    }
    depth = Integer.parseInt((String) obj);
    int childDepth = depth + 1;

    String noDesc = (childDepth >= TabDescCutoffDepth) ? "true" : "false";
    //noDesc = "true";

    Collection children = tab.filterChildrenForSubtabs();
    if (depth < CutOffDepth && children != null) {
        Iterator childIter = children.iterator();
        boolean hasChildren = childIter.hasNext();
        if (hasChildren) { %>
			<div class='subtabs'><!-- tabSub.jsp -->
			<ul class='tabSubAsList'>
		<% } 	
		while( childIter.hasNext() ) {
			Tab subtab = (Tab)childIter.next();
			    TabWebUtil.stashTabsInRequest(subtab,request);	 		%>			
						<li>
						<jsp:include page='tabBasic.jsp'>
							<jsp:param name='tabDepth' value='<%=childDepth%>' />
						 	<jsp:param name='tabId' value='<%=subtab.getTabId()%>' />
							<jsp:param name='noDesc' value='<%=noDesc%>'/>
							<jsp:param name='yesBody' value='false'/>
						 	<jsp:param name='noEntities' value='true'/>
						 	<jsp:param name='noContent' value='true'/>						 	
						 	<jsp:param name='noSubtabs' value='true'/>						 							 	
						</jsp:include>
						</li>					
		<% }	
		if( hasChildren ){ %>		
			</ul>
			</div><!--  end subtabs div-->
		<%}%>
<% } %>
