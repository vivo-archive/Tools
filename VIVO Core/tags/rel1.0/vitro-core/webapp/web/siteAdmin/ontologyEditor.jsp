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

<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.jena.pellet.PelletListener"%>

<% if (securityLevel >= loginHandler.CURATOR) { %>
    
        <div class="pageBodyGroup">
    
            <h3>Ontology Editor</h3>
<%                         
            Object plObj = getServletContext().getAttribute("pelletListener");
            if ( (plObj != null) && (plObj instanceof PelletListener) ) {
                PelletListener pelletListener = (PelletListener) plObj;
                if (!pelletListener.isConsistent()) {
%>
	                <p class="notice">
	                    INCONSISTENT ONTOLOGY: reasoning halted.
	                </p>
	                <p class="notice">
	                Cause: <%=pelletListener.getExplanation()%>
	                </p>
<%              } else if (pelletListener.isInErrorState()) { %>
					<p class="notice">
					    An error occurred during reasoning; 
                        reasoning has been halted.
                        See error log for details.
					</p>
	   
<%                }
            }
%>       
            <ul>
                <li><a href="listOntologies?home=<%=portal.getPortalId()%>">Ontology list</a></li>
            </ul>
        
            <h4>Class Management</h4>
            <ul>
                <li><a href="showClassHierarchy?home=<%=portal.getPortalId()%>">Class hierarchy</a></li> 
                <li><a href="listGroups?home=<%=portal.getPortalId()%>">Class groups</a></li>
            </ul>
        
            <h4>Property Management</h4>
            <ul>
                <li><a href="showObjectPropertyHierarchy?home=${portalBean.portalId}&amp;iffRoot=true">Object property hierarchy</a></li>
                <li><a href="showDataPropertyHierarchy?home=<%=portal.getPortalId()%>">Data property hierarchy</a></li>      
                <li><a href="listPropertyGroups?home=<%=portal.getPortalId()%>">Property groups</a></li>
            </ul>

            <c:set var="verbosePropertyListing" value="${verbosePropertyListing == true ? true : false}" />
            <form id="verbosePropertyForm" action="${Controllers.SITE_ADMIN}#verbosePropertyForm" method="get">
                <input type="hidden" name="verbose" value="${!verbosePropertyListing}" />
                <span>Verbose property display for this session is <b>${verbosePropertyListing == true ? 'on' : 'off'}</b>.</span>
                <input type="submit" value="Turn ${verbosePropertyListing == true ? 'off' : 'on'}" />
            </form>   

    </div>               

<%  } %>