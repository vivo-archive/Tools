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
	<tr class="editformcell">
		<td valign="top" colspan="2">
			<b>Keyword</b><br />
				<input type="text" name="Term" value="<form:value name="Term"/>" size="60" maxlength="255" />
				<p><font size="2" color="red"><form:error name="Term"/></font></p>
		</td>
	</tr>
	<tr class="editformcell">
		<td valign="top" colspan="1">
			<b>Origin</b> <i>(source of 1st use of this keyword)</i><br/>
				<select name="Origin" >
					<form:option name="Origin"/>
				</select>
				<font size="2" color="red"><form:error name="Origin"/></font>
			</td>
			<td valign="top"><sup>*</sup>If [new origin] is selected, optionally enter a <b>new origin</b> here:<br/>
				<input type="text" name="Origin" size="30" maxlength="80" /><br>
		</td>
	</tr>
	<tr class="editformcell">
		<td valign="bottom" colspan="3">
			<b>Comment</b> <i>limited to ~255 characters</i><br />
				<textarea name="Comments" ROWS="3" COLS="80" wrap="physical"><form:value name="Comments"/></textarea>
				<font size="2" color="red"><form:error name="Comments"/></font>
		</td>
	</tr>
