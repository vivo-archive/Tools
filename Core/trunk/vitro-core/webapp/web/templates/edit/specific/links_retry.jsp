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

<%@ taglib prefix="form" uri="http://vitro.mannlib.cornell.edu/edit/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

	<tr class="editformcell">
		<td valign="top" colspan="3">
			<b>Individual Name</b><br/>
				<select name="EntityId" >
					<form:option name="EntityId"/></option>
 				</select>
				<font size="2" color="red"><form:error name="EntityId"/></font>
		</td>
	</tr>
  <c:if test="${!empty epo.formObject.optionLists['TypeURI']}">
	<tr class='editformcell'>
		<td valign="top" colspan="3">
			<b>Link Type</b><br>
				<select name="TypeURI">
					<form:option name="TypeURI"/>
				</select>
				<br><font size="2" color="red"><form:error name="TypeURI"/></font>
		</td>
	</tr>
  </c:if>
	<tr class="editformcell">
		<td valign="bottom" colspan="3">
			<b>URL</b> itself (http://...)<br/>
				<input type="text" name="Url" style="width:32em;" value="<form:value name="Url"/>"/>
				<font size="2" color="red"><form:error name="Url"/></font>
		</td>
	</tr>
	<tr class="editformcell">
		<td valign="bottom" colspan="2">
			<b>anchor text for above URL</b><br/>
				<input type="text" name="Anchor" value="<form:value name="Anchor"/>" style="width:24em;" maxlength="255" />
				<font size="2" color="red"><form:error name="Anchor"/></font>
		</td>
	</tr>

