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

<div class="editingForm">

<jsp:include page="/templates/edit/fetch/vertical.jsp"/>

<%@ taglib uri="/WEB-INF/tlds/database.tld" prefix="database"%>
<%
int DEFAULT_PORTAL_ID=1;
String portalIdStr=(portalIdStr=(String)request.getAttribute("home"))==null ?
	((portalIdStr=request.getParameter("home"))==null?String.valueOf(DEFAULT_PORTAL_ID):portalIdStr):portalIdStr;
%>
<hr/>
<p/>
<div align="center">
<table class="form-background" border="0" cellpadding="2" cellspacing="2">
<tr valign="bottom" align="center">
	<td>
		<form action="fetch" method="post">
			<input type="submit" class="form-button" value="display this keyword"/>
			<input type="hidden" name="home" value="<%=portalIdStr%>" />
			<input type="hidden" name="queryspec" value="public_keyterm"/>
			<input type="hidden" name="linkwhere" value="keyterms.id=<%=request.getAttribute("firstvalue")%>" />
		</form><br />
		<form action="fetch" method="post">
			<input type="submit" class="form-button" value="all keywords"/>
			<input type="hidden" name="home" value="<%=portalIdStr%>" />
			<input type="hidden" name="queryspec" value="public_keyterms"/>
			<input type="hidden" name="header" value="titleonly"/>
		</form>
	</td>
	<td valign="bottom" align="center">
		<form action="editForm" method="get">
			<input name="id" type = "hidden" value="<%=request.getParameter("firstvalue")%>" />
			<input type="submit" class="form-button" value="edit keyword <%=request.getAttribute("firstvalue")%>"/>
			<input type="hidden" name="home" value="<%=portalIdStr%>" />
			<input type="hidden" name="controller" value="Keyword"/>
		</form>
	</td>
	<td valign="bottom">
		<form action="editForm" method="get">
			<input type="hidden" name="home" value="<%=portalIdStr%>" />
			<input type="hidden" name="controller" value="Keyword"/>
			<input type="submit" class="form-button" value="add new keyword"/>
		</form>
	</td>
</tr>
<tr><td colspan="3"><hr/></td></tr>
<!-- -------------------------------------------- entities  ------------------------------------------->
<tr valign="bottom" align="center">
    <td>
        <form action="fetch" method="post">
            <input type="submit" class="form-button" value="all related entities"/>
            <input type="hidden" name="home" value="<%=portalIdStr%>" />
            <input type="hidden" name="queryspec" value="public_keyterm_entities"/>
            <input type="hidden" name="header" value="titleonly"/>
            <input type="hidden" name="linkwhere" value="keys2ents.keyId=<%=request.getParameter("firstvalue")%>"/>
        </form><br />
    </td>
    <td valign="bottom" align="center">
        <database:query id="ifEntities" scope="page">
            SELECT count(*) FROM keys2ents,entities WHERE keys2ents.entId=entities.id AND keys2ents.keyId=<%=request.getAttribute("firstvalue")%>
        </database:query>
        <database:rows query="ifEntities">
            <database:columns query="ifEntities" id="entityCount">
                <% if (Integer.parseInt(entityCount)>0) { %>
                    <form action="editForm" method="get">
                        <database:query id="keys2ents" scope="page">
                            SELECT keys2ents.id,name FROM keys2ents,entities
                            WHERE keyId=<%=request.getAttribute("firstvalue")%>
                            AND keys2ents.entId=entities.id
                            ORDER BY entities.name
                        </database:query>
                        <select name="id" class="form-item">
                        <database:rows query="keys2ents">
                            <database:select_columns query="keys2ents" id="selectId" selectValue="selectName">
                                <option value="<%= selectId %>"><%= selectName %></option>
                            </database:select_columns>
                        </database:rows>
                        <database:release query="keys2ents"/>
                        </select><br>
                        <input type="submit" class="form-button" value="edit entity link"/>
                        <input type="hidden" name="home" value="<%=portalIdStr%>" />
                        <input type="hidden" name="controller" value="Keys2Ents"/>
                    </form>
            <% } %>
            </database:columns>
        </database:rows>
        <database:release query="ifEntities"/>
    </td>
    <td>
        <form action="editForm" method="get">
			<database:query id="vclasses" scope="page">
				SELECT DISTINCT vclass.id,vclass.name FROM vclass,entities
				WHERE entities.vClassId=vclass.id ORDER BY vclass.name
			</database:query>
			<select name="vclassId" class="form-item" >
				<option selected="selected" value="0">select entity class ...</option>
				<database:rows query="vclasses">
					<database:select_columns query="vclasses" id="selectId" selectValue="selectName">
						<option value="<%= selectId %>"><%= selectName %></option>
					</database:select_columns>
				</database:rows>
				<database:release query="vclasses"/>
			</select>
			<br />
            <input type="hidden" name="home" value="<%=portalIdStr%>" />
            <input type="hidden" name="keyId" value="<%=request.getParameter("firstvalue")%>">
            <input type="hidden" name="controller" value="Keys2Ents"/>
            <input type="submit" class="form-button" value="link to existing entity"/>
        </form>
    </td>
</tr>
</table>
</div>
</div>
