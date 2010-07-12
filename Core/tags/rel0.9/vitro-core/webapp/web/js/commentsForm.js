<!-- /*
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
*/ -->

<script language="JavaScript" type="text/javascript">
<!--
function ValidateForm(formName) {
	var x = 0; // counts form elements - used as array index
	var y = 0; // counts required fields - used as array index
	errors = false;
	var errorList;

	if (document.forms[formName].RequiredFields) {
		errorList = 'Please fill out the following required fields:\n';
		// build array of required fields
		reqStr = document.forms[formName].RequiredFields.value;
		requiredFields = reqStr.split(',');
		// build array holding the names of required fields as
		// displayed in error box
		if (document.forms[formName].RequiredFieldsNames) {
			reqNameStr = document.forms[formName].RequiredFieldsNames.value;
		} else {
			reqNameStr = document.forms[formName].RequiredFields.value;
		}
		requiredNames = reqNameStr.split(',');
		// Loop through form elements, checking for required fields
		while ((x < document.forms[formName].elements.length)) {
			if (document.forms[formName].elements[x].name == requiredFields[y]) {
				if (document.forms[formName].elements[x].value == '') {
					errorList += requiredNames[y] + '\n';
					errors = true;
				}
				y++;
			}
			x++;
		}
		if (errors) {
			alert(errorList);
			return false;
		}
	x = 0;
	y = 0;
	}

	// Check for Email formatting
	if (document.forms[formName].EmailFields) {
		errorList = 'Please format your e-mail address as: \"userid@institution.edu\" or enter another complete email address';
		// build array of required fields
		emailStr = document.forms[formName].EmailFields.value;
		emailFields = emailStr.split(',');
		// build array holding the names of required fields as
		// displayed in error box
		if (document.forms[formName].EmailFieldsNames) {
			emailNameStr = document.forms[formName].EmailFieldsNames.value;
		} else {
			emailNameStr = document.forms[formName].EmailFields.value;
		}
		emailNames = emailNameStr.split(',');
		// Loop through form elements, checking for required fields
		while ((x < document.forms[formName].elements.length)) {
			if (document.forms[formName].elements[x].name == emailFields[y]) {
				if ((document.forms[formName].elements[x].value.indexOf('@') < 1)
					|| (document.forms[formName].elements[x].value.lastIndexOf('.') < document.forms[formName].elements[x].value.indexOf('@')+1)) {
					errors = true;
				}
				y++;
			}
			x++;
		}
		if (errors) {
			alert(errorList);
			return false;
		}
	x = 0;
	y = 0;
	}

	return true;
}
 -->
</script>