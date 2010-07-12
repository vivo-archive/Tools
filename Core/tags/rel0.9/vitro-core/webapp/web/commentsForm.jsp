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

<div id="content" class="staticPageBackground">

<div class="feedbackForm">

<h2>${siteName} Feedback and Comments Form</h2>

<p>
	<c:choose>
		<c:when test='${portalType eq "CALSResearch"}'>
 			Thank you for your interest in the Cornell University College of Agriculture and Life Sciences Research Portal.
 		</c:when>
 		<c:when test='${portalType eq "VIVO"}'>
 			Thank you for your interest in VIVO. 		
 		</c:when>
 		<c:otherwise>
 			Thank you for your interest in the ${siteName} portal.
 		</c:otherwise>
 	</c:choose>
<p>

		<c:if test='${portalType != "clone"}'>
 			<p>If you are looking for information on:</p>
			<ul>
				<li>Undergraduate admissions: contact <a href="http://admissions.cornell.edu/">http://admissions.cornell.edu/</a></li>
 				<li>Undergraduate financial aid: contact <a href="http://finaid.cornell.edu/">http://finaid.cornell.edu/</a></li>
 				<li>Graduate admissions and information: contact <a href="http://www.gradschool.cornell.edu/">http://www.gradschool.cornell.edu/</a></li>
 				<li>International Students and Scholars Office: contact <a href="http://www.isso.cornell.edu/">http://www.isso.cornell.edu/</a></li>
				<li>Faculty, staff and student directory: contact <a href="http://www.cornell.edu/search?tab=people">http://www.cornell.edu/search/?tab=people</a></li>
				<li>General information about Cornell University: <a href="http://www.cornell.edu/">http://www.cornell.edu/</a></li>
			</ul>
		</c:if>


<p>
		If you have a question regarding the content of this portal, please submit the form below.
		<c:if test='${(siteName eq "CALSResearch" || siteName eq "CALSImpact")}'>
			The reference librarians at Albert R. Mann Library will be in touch with you soon.
		</c:if>
</p>

<hr/>

		<form name = "contact_form" action="sendmail" method="post" onsubmit="return ValidateForm('contact_form');">
		<input type="hidden" name="home" value="${portalId}"/>
		<input type="hidden" name="RequiredFields" value="webuseremail,webusername,comments"/>
		<input type="hidden" name="RequiredFieldsNames" value="Email address,Name,Comments"/>
		<input type="hidden" name="EmailFields" value="webuseremail"/>
		<input type="hidden" name="EmailFieldsNames" value="emailaddress"/>
		<input type="hidden" name="DeliveryType" value="comment"/>
		
		<p class="normal">My email address is (e.g., userid<b>@institution.edu</b>):</p>
		<input style="width:25%;" type="text" name="webuseremail" maxlength="255"/><br/><br/>
		<p class="normal">My full name is:</p>
		<input style="width:33%;" type="text" name="webusername" maxlength="255"/><br/><br/>

		<p class="normal"><i>${siteName} is a service that depends on regular updates and feedback.
			Please help us out by providing any necessary corrections and suggestions for additional content (people, departments, courses, research services, etc.)
			that you would like to see represented.</i></p>
		<h3>Enter your comments, questions, or suggestions in the box below.</h3>

		<textarea name="s34gfd88p9x1" rows="10" cols="90"></textarea>
		<div>
  		<input type="submit" value="Send Mail" class="yellowbutton"/>
  		<input type="reset" value="Clear Form" class="plainbutton"/>
		</div

		<p style="font-weight: bold; margin-top: 1em">Thank you!</p>
		</form>

</div><!--feedbackForm-->

</div><!--content, staticPageBackground-->
