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

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.User" %>

<script type="text/javascript" src="js/jquery.js"></script> 
<script language="JavaScript" type="text/javascript" src="js/toggle.js"></script>
<script language="JavaScript" type="text/javascript" src="js/md5.js"></script>
<script language="JavaScript" type="text/javascript">
<!-- Hide from browsers without JavaScript support

function isValidLogin( theForm ) {
    if ( isEmpty( theForm.loginName.value)) {
        theForm.loginName.focus();
        return false;
    }
    if ( isEmptyOrWrongLength( theForm.loginPassword.value)) {
        theForm.loginPassword.focus();
        return false;
    }

    //alert("theForm.loginPassword.value=" + theForm.loginPassword.value );
    theForm.loginPassword.value = calcMD5( theForm.loginPassword.value );
    //alert("theForm.loginPassword.value=" + theForm.loginPassword.value );
    return true;
}

function isEmpty( aStr ) {
    if ( aStr.length == 0 ) {
        alert("Please enter a username to log in");
        return true;
    }
    return false;
}

function isEmptyOrWrongLength( aStr ) {
    if ( aStr.length == 0 ) {
        alert("Please enter a password to log in");
        return true;
    } else if ( aStr.length < <%=User.MIN_PASSWORD_LENGTH%> || aStr.length > <%=User.MAX_PASSWORD_LENGTH%>) {
        alert("Please enter a password between 6 and 12 characters long");
        return true;
    }
    return false;
}

//Give initial focus to the password or username field 
$(document).ready(function(){
  if ($("em.passwordError").length > 0 && $("em.usernameError").length == 0) {
    $("input#password").focus();
  } else {
    $("input#username").focus();
  }
});
-->
</script>

<script language="JavaScript" type="text/javascript">
	initDHTMLAPI();
</script>

