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
		<td valign="bottom" colspan="3">
			<b>Ontology name</b><br/>
				<input type="text" name="Name" value="<form:value name="Name"/>" size="40" maxlength="120" />
				<font size="2" color="red"><form:error name="Name"/></font>
		</td>
	</tr>
	<tr class="editformcell">
		<td valign="bottom" colspan="3">
            <b>Namespace URI</b><br/>
             <c:choose>
               <c:when test="${_action eq 'update'}">
                    <i>Change via the "change URI" button on previous screen</i><br/>
                    <input disabled="disabled" type="text" name="URI" value="<form:value name="URI"/>" size="50" maxlength="240" />
                </c:when>
                <c:otherwise>
                    <input type="text" name="URI" value="<form:value name="URI"/>" size="50" maxlength="240" />
                </c:otherwise>
              </c:choose>
		</td>
	</tr>
	<tr class="editformcell">
		<td valign="bottom" colspan="3">
			<b>Namespace prefix</b><br/>
				<input type="text" name="Prefix" value="<form:value name="Prefix"/>" size="8" maxlength="25" />
				<font size="2" color="red"><form:error name="Prefix"/></font>
		</td>
	</tr>

