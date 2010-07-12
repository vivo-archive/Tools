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

<%
int DEFAULT_PORTAL_ID=1;
String portalIdStr=(portalIdStr=(String)request.getAttribute("home"))==null ?
	((portalIdStr=request.getParameter("home"))==null?String.valueOf(DEFAULT_PORTAL_ID):portalIdStr):portalIdStr;
%>

<div class="editingForm"/>

<jsp:include page="/templates/edit/fetch/vertical.jsp"/>

<hr/>
<p/>
<div align="center">
<table class="form-background" border="0" cellpadding="2" cellspacing="2">
<tr valign="bottom" align="center">
	<td>
		<form action="fetch" method="get">
			<input type="submit" class="form-button" value="display this class group's record"/>
			<input type="hidden" name="home" value="<%=portalIdStr%>" />
			<input type="hidden" name="queryspec" value="private_classgroup"/>
			<input type="hidden" name="header" value="titleonly"/>
			<input type="hidden" name="linkwhere" value="classgroups.id=<%=request.getAttribute("firstvalue")%>"/>
		</form>
		<form action="fetch" method="get">
			<input type="submit" class="form-button" value="see all class groups"/>
			<input type="hidden" name="home" value="<%=portalIdStr%>" />
			<input type="hidden" name="queryspec" value="private_classgroups"/>
			<input type="hidden" name="header" value="titleonly"/>
		</form>
	</td>
	<td valign="bottom" align="center">
		<form action="editForm" method="get">
			<input name="id" type = "hidden" value="<%=request.getAttribute("firstvalue")%>" />
			<input type="submit" class="form-button" value="edit class group <%=request.getAttribute("firstvalue")%>"/>
			<input type="hidden" name="home" value="<%=portalIdStr%>" />
			<input type="hidden" name="controller" value="Classgroup"/>
		</form>
	</td>
	<td valign="bottom">
		<form action="editForm" method="get">
			<input type="hidden" name="home" value="<%=portalIdStr%>" />
			<input type="hidden" name="controller" value="Classgroup"/>
			<input type="submit" class="form-button" value="add new class group"/>
		</form>
		<form action="editForm" method="get">
			<input type="hidden" name="home" value="<%=portalIdStr%>" />
			<input type="hidden" name="controller" value="Vclass"/>
			<input type="hidden" name="GroupId" value="<%=request.getAttribute("firstvalue")%>"/>
			<input type="submit" class="form-button" value="add new class to this group"/>
		</form>
	</td>
</tr>
<tr><td colspan="3"><hr/></td></tr>
</table>
</div>
</div>
