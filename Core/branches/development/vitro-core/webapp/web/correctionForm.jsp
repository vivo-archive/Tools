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

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep"%>
    
<c:set var='themeDir'><c:out value='${portalBean.themeDir}' /></c:set>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>  <!-- from formPrefix.jsp -->
	<link rel="stylesheet" type="text/css" href="<c:url value="/${themeDir}css/screen.css"/>" media="screen"/>
    <title>VIVO Correction Form</title>
</head>
<body class="formsEdit">
<div id="wrap">
<jsp:include page="/${themeDir}jsp/identity.jsp" flush="true"/>
<div id="contentwrap">
<jsp:include page="/${themeDir}jsp/menu.jsp" flush="true"/>
<!-- end of formPrefix.jsp -->

<div id="content" class="staticPageBackground">

<div class="feedbackForm">

<h2>VIVO Correction and Update Form</h2>
<p/>
<p>
		Please submit corrections and/or updates on the form below.
</p>
<p>
		Staff on duty during regular business hours will contact you as soon as possible
		to confirm or clarify your requested changes.
</p>

<hr/>

		<form name = "contact_form" action="sendmail" method="post" onsubmit="return ValidateForm('contact_form');">
		<input type="hidden" name="home" value="${portalBean.portalId}"/>
		<input type="hidden" name="RequiredFields" value="webuseremail,webusername,comments"/>
		<input type="hidden" name="RequiredFieldsNames" value="Email address,Name,Comments"/>
		<input type="hidden" name="EmailFields" value="webuseremail"/>
		<input type="hidden" name="EmailFieldsNames" value="emailaddress"/>
		<input type="hidden" name="DeliveryType" value="correction"/>
		
		<p class="normal">My email address (e.g., userid<b>@institution.edu</b>) is:</p>
		<input style="width:25%;" type="text" name="webuseremail" maxlength="255"/><br/><br/>
		<p class="normal">My full name is:</p>
		<input style="width:33%;" type="text" name="webusername" maxlength="255"/><br/><br/>

		<p class="normal">
		    <i>			
			Please also optionally include any suggestions for additional content (people, departments, courses, research services, etc.)
			that you would like to see represented in VIVO.
			</i>
	    </p>
		<h3>Enter your corrections, questions, or suggestions in the box below.</h3>

		<textarea name="s34gfd88p9x1" rows="10" cols="90"></textarea>
		<p/>
		<input type="submit" value="Send Mail" class="yellowbutton"/>
		<input type="reset" value="Clear Form" class="plainbutton"/>

		<h3>Thank you!</h3>
		</form>

</div><!--feedbackForm-->

</div><!--staticPageBackground-->

<c:set var='themeDir'><c:out value='${portalBean.themeDir}'/></c:set>
</div> <!-- contentwrap -->
     <jsp:include page="/${themeDir}jsp/footer.jsp" flush="true"/>
</div><!-- wrap -->
</body>
</html>
