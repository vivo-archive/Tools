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

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Tab" %>
<%@ page import="java.util.Collection,java.util.Iterator" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.TabWebUtil" %>
<%@ page errorPage="/error.jsp"%>
<%
	int CutOffDepth = 3;
		/***********************************************
		 Tab Content is to display the sub tabs of type primaryTabContent
		 
		 Primary Content Tabs will have:
		 tab heading,
		 tab description
		 request.attributes
		 "leadingTab" the tab that is at the top of the display hierarchy
		  		  
		 "tabDepth" String that is the depth of the tab in the display
		 leadingTab = 1, child of leadingTab = 2, etc.
		 Here tabDepth does not default, it must be set
		 
		 bdc34 2006-01-12 created		 
        **********************************************/
        
    Tab leadingTab =(Tab) request.getAttribute("leadingTab");       
    if(leadingTab== null ) {
	        String e="tabContent expects that request attribute 'leadingTab' be set";
    	    throw new JspException(e);
     }    
             
    String tabId = request.getParameter("tabId");
    if( tabId == null ){
     	String e="tabContent expects that request parameter 'tabId' be set";
   	    throw new JspException(e);
    }
        
	Tab tab = null;
   	tab = TabWebUtil.findStashedTab(tabId,request);
   	if( tab == null ){
        String e="tabContent expects that request attribute 'leadingTab' will have the tab with tabId as a sub tab";
   	    throw new JspException(e);       	
     }
        
   String obj= request.getParameter("tabDepth");
    int depth = 1; //depth 1 represents primary tab level, 2 is secondary, etc.        
    if( obj == null ){
      	String e="tabContent expects that request parameter 'tabDepth' be set";
   	    throw new JspException(e);
    }
    depth = Integer.parseInt((String)obj);                    
	int childDepth = depth + 1;
    	
    Collection children = tab.filterChildrenForContentTabs();
    if( depth < CutOffDepth && children!= null ){
		Iterator childIter=children.iterator();                
		boolean hasChildren = childIter.hasNext();
		int columnSize = children.size();
		if(   hasChildren ){ %>
		  
			<div id='tabContent'><!-- tabContent.jsp -->
			<table><tr>
		<% } 	
		while( childIter.hasNext() ) {
			Tab contentTab = (Tab)childIter.next();
			    TabWebUtil.stashTabsInRequest(contentTab, request);	 		%>	
			    
			    <% if (columnSize==2) {%>
			      <td class="span-12">
			    <% } %>
			    
			    <% else if (columnSize==3) {%>
			      <td class="span-8">
          <% } %>
          
			    <% else if (columnSize==4) {%>
			      <td class="span-6">
			    <% } %>
			    
			    <% else {%>
			      <td>
			    <% } %>
			    
						<jsp:include page='tabBasic.jsp'>
							<jsp:param name='tabDepth' value='<%=childDepth%>' />
						 	<jsp:param name='tabId' value='<%=contentTab.getTabId()%>' />
						 	<jsp:param name='noContent' value='true'/>
						 	<jsp:param name='noSubtabs' value='true'/>
						</jsp:include>
						</td>					
		<% }	
		if( hasChildren ){ %>		
			</tr></table>
			</div><!--  end tabContent div-->
		<%}%>
<% } %>
