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

<% if (securityLevel >= loginHandler.CURATOR) { %>
    <div class="pageBodyGroup">
    
        <h3>Site Configuration</h3>
    
        <ul>
            <c:if test="${requestScope.singlePortal == true }">
                <li><a href="editForm?home=<%=portal.getPortalId()%>&amp;controller=Portal&amp;id=<%=portal.getPortalId()%>">Site information</a></li>
            </c:if>      
            <c:if test="${requestScope.singlePortal == false }">
                <li><a href="editForm?home=<%=portal.getPortalId()%>&amp;controller=Portal&amp;id=<%=portal.getPortalId()%>">Current portal information</a></li>
                <li><a href="listPortals?home=<%=portal.getPortalId()%>">List all portals</a></li>
            </c:if>  
        
            <li><a href="listTabs?home=<%=portal.getPortalId()%>">Tab management</a></li>
            
<%          if (securityLevel >= loginHandler.DBA) { %>
                <li><a href="listUsers?home=<%=portal.getPortalId()%>">User accounts</a></li>    
<%          } %>      
        </ul>
    </div>
<% } %>  
  
  
               
