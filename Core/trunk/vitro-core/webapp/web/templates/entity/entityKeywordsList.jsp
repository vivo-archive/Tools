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

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://vitro.mannlib.cornell.edu/vitro/tags/PropertyEditLink" prefix="edLnk" %>
<%@ page import="edu.cornell.mannlib.vedit.beans.LoginFormBean" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.KeywordProperty" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.KeywordDao" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Keyword" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.KeywordIndividualRelation" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.KeywordIndividualRelationDao" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.Controllers" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://vitro.mannlib.cornell.edu/vitro/tags/StringProcessorTag" prefix="p" %>

<%! 
public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.templates.entity.entityKeywordsList.jsp");
%>

<%
if (!LoginFormBean.loggedIn(request, LoginFormBean.CURATOR)) {%>
    <c:redirect url="<%= Controllers.LOGIN %>" />
<%
}
Individual ent = (Individual)request.getAttribute("entity");
if (ent==null) {
    log.error("No incoming entity in entityKeywordsList.jsp");
}
VitroRequest vreq = new VitroRequest(request);
KeywordProperty kProp = new KeywordProperty("has keyword","keywords",0,null);
WebappDaoFactory wdf = vreq.getWebappDaoFactory();
KeywordIndividualRelationDao kirDao = wdf.getKeys2EntsDao();
KeywordDao kDao = wdf.getKeywordDao();
List<KeywordIndividualRelation> kirs = kirDao.getKeywordIndividualRelationsByIndividualURI(ent.getURI());
if (kirs != null) {
    int keyCount=0;
    Iterator kirIt = kirs.iterator();
    while (kirIt.hasNext()) {
        KeywordIndividualRelation kir = (KeywordIndividualRelation) kirIt.next();
        if (kir.getKeyId() > 0) {
            Keyword k = kDao.getKeywordById(kir.getKeyId());
            if (k != null) {
                ++keyCount;
				if (keyCount==1) {%>
					<h3 class="propertyName">Keywords</h3>
                    <div class="datatypePropertyValue">
<%				} else { %>
					<c:out value=", "/>
<%				}%>
                <c:out value="<%=k.getTerm()%>"/>
<% 			}
        }
    }%>
    </div><%
}
%>

