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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<table cellpadding="1" cellspacing="1" border="0" width="100%">
<c:choose>
	<c:when test="${!empty processError}">
		<tr>
			<td align="center" colspan="7">
				<font color="red">Results from processing input file:</font><br/>
				${processError}
				<c:if test="${!empty outputLink}">
					<br/><font color="red">Link to check uploaded image:</font><br/>
					<${outputLink}>
					<c:if test="${processErrorUpdated}">
						<p>
						<form action="entity" method="get">
							<input type="hidden" name="home" value="${portalBean.portalId}"/>
							<input type="submit" class="form-button" value="view updated record"/>
							<input type="hidden" name="entityUri" value="${individual.URI}"/>
						</form>
						</p>
					</c:if>
				</c:if>
			</td>
		</tr>
	</c:when>
</c:choose>
<tr><td align="center" colspan="7">
		<form name="uploadForm" action="uploadImages" method="post" ENCTYPE="multipart/form-data">
			<input type="hidden" name="home" value="${portalBean.portalId}"/>
			<input type="hidden" name="submitter" value="${loginName}"/>
			<input type="hidden" name="destination" value="images" />
			<input type="hidden" name="contentType" value="image/jpeg/gif/pjpeg" />
    	<table width="100%" cellpadding="4" cellspacing="2" border="1">
			<tr>
				<td bgcolor="#C8D8F8"  valign="middle" colspan="1" align="right">
					<b>Individual</b>
				</td>
				<td bgcolor="#C8D8F8" valign="middle" align="left" colspan="2">
					<c:choose>
						<c:when test="${individual != null}">
							<select name="entityUri" style="width:95%;">
								<option value="${individual.URI}">${individual.name}</option>
							</select>
						</c:when>
						<c:otherwise>
							<p>No individuals match the incoming parameters or no individual was specified in the request</p>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			 <%-- RY Disabling large image upload
			<tr>
			    
				<td bgcolor="#C8D8F8"  valign="middle" colspan="1" align="right">
					<b>Select Image Type</b><br/>
				</td>

				<td colspan="2" bgcolor="#C8D8F8" align="left">
					<input type="radio" name="type" value="thumb" checked="checked" onclick="refreshModeValue();" /> thumbnail (150px x 150px only)
					<input type="radio" name="type" value="larger" onclick="refreshModeValue();" /> optional larger image
				</td>
				
			</tr>
			--%>
			<tr>
				<td bgcolor="#C8D8F8"  valign="middle" colspan="1" align="right">
					<b>Select Image File</b>
					<c:if test="${!empty inputLink}">
						<br/>${inputLink}
					</c:if>
				</td>
				<td colspan="2" bgcolor="#C8D8F8" align="left">
				    <span class="comment">Please note: images are displayed at approximately 150x150px. Smaller images may not display optimally.</span><br />
					<input type="file" size="55" name="file1"/>
				</td>
			</tr>
			<tr>
				<td bgcolor="#C8D8F8"  valign="middle" colspan="1" align="right">
					<b>Optional remote image link</b>
				</td>
				<td colspan="2" bgcolor="#C8D8F8" align="left">
					<div id="thumbnailExtra" class="dropdownExtra">
						<%--  (instead of uploading larger image -- use only when uploading thumbnail)<br/> --%>
						<input type="text" size="55" name="remoteURL" value="http://"/>
					</div>
				</td>
			</tr>
			<tr>
				<td bgcolor="#C8D8F8"  valign="middle" colspan="1" align="right">
					<b>Destination Directory</b><br/>
				</td>
				<td colspan="2" bgcolor="#C8D8F8" align="left">
					<input type="radio" name="destination" value="buildings" /> buildings<br/>
					<input type="radio" name="destination" value="events" /> events<br/>
					<input type="radio" name="destination" value="logos" /> logos<br/>
					<input type="radio" name="destination" value="people" checked="checked" /> people<br/>
					<input type="radio" name="destination" value="projects" /> projects<br/>
					<input type="radio" name="destination" value="science" /> science<br/>
					<input type="radio" name="destination" value="other" /> other<br/>
				</td>
			</tr>
			<tr>
				<td bgcolor="#C8D8F8"  valign="middle" colspan="1" align="right">
					<b>Select Processing Mode</b><br/>
				</td>
				<td colspan="2" bgcolor="#C8D8F8" align="left">
					<input type="radio" name="mode" value="upload"/> upload image
					<input type="radio" name="mode" value="replace" checked="checked"/> upload and replace any existing image or URL
				</td>
			</tr>
			<tr>
				<td colspan="3" bgcolor="#C8D8F8" align="center">
				    <input type="hidden" name="type" value="thumb" /> <%-- RY Disabling large image upload --%>
					<p>
					<input type="submit" name="submitMode" class="yellowbutton" value="Upload Selected Image"/>
					<input type="reset" name="reset" class="plainbutton" value="Reset" onclick="document.refreshForm.submit();"/>
					</p>
				</td>
			</tr>
	</table>
	</form>
	</td>
</tr>
<tr><td colspan="7">
	</td>
</tr>
</table>
<form action="uploadimages.jsp" name="refreshForm" >
	<input type="hidden" name="entityUri" value="${individual.URI}" />
</form>
