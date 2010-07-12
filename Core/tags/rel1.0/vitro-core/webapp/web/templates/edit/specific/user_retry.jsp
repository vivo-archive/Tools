<?xml version="1.0" encoding="UTF-8"?>

<!--
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
-->

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jstl/core"  xmlns:form="http://vitro.mannlib.cornell.edu/edit/tags" version="2.0">

	<tr class="editformcell">
		<td valign="bottom" colspan="2">
			<b>User Name*</b><br/>
			<input type="text" name="Username" value="${formValue['Username']}" size="60" maxlength="120" />
			<span class="warning"><form:error name="Username"/></span>
		</td>
	</tr>
	<tr class="editformcell">
		<td valign="bottom" colspan="1">
			<b>First Name</b><br/>
                        <input type="text" name="FirstName" value="${formValue['FirstName']}" size="30" maxlength="120"/>
			<span class="warning"><form:error name="FirstName"/></span>
		</td>
                <td valign="bottom" colspan="1">
                        <b>Last Name</b><br/>
                        <input type="text" name="LastName" value="${formValue['LastName']}" size="30" maxlength="120"/>
                        <span class="warning"><form:error name="LastName"/></span>
                </td>
	</tr>
	<tr class="editformcell">
                <td valign="bottom" colspan="2">
                        <b>Role</b><br/>
                        <select name="RoleURI">
                            <form:option name="Role"/>
                        </select>
                        <span class="warning"><form:error name="Role"/></span>
                </td>
	</tr>
        <c:if test="${empty user.md5password}">
          <tr class="editformcell">
              <td valign="bottom" colspan="2">
                      <b>Password (6-12 characters)</b><br/>
                      <input type="password" name="Md5password" value="${formValue['Md5password']}" size="64" maxlength="128"/>
                      <span class="warning"><form:error name="Md5password"/></span>
              </td>
          </tr>
          <tr class="editformcell">
              <td valign="bottom" colspan="2">
                      <b>Confirm password</b><br/>
                      <input type="password" name="passwordConfirmation" value="" size="64" maxlength="128"/>
              </td>
          </tr>
        </c:if>
</jsp:root>
