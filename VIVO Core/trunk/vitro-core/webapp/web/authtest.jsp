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

<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils" %>
<html>

<head>
<title>Authroization Test</title>
<link rel="stylesheet" type="text/css" href="css/edit.css">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body>

<table width="90%" cellpadding="1" cellspacing="1" border="0" align="center">

<tr><td colspan="4" height="1"><img src="site_icons/transparent.gif" width="100%" height="1" border="0"></td></tr>

    <%

    out.println("here");
java.util.Map params = request.getParameterMap();
java.util.Iterator keys = params.keySet().iterator();

%> <tr><td><b>params</b></td></tr> <%
while (keys.hasNext()){
    String name = (String) keys.next();
    String val = (String) params.get(name);
    out.println("<tr><td>"+ name + "</td><td>"+ val +"</td></tr>");
}
%> <tr><td><b>headers</b></td></tr> <%
    java.util.Enumeration hnames = request.getHeaderNames();
while( hnames.hasMoreElements() ){
    String name = (String) hnames.nextElement();
    String val  = request.getHeader(name);
    out.println("<tr><td>"+ name + "</td><td>"+ val +"</td></tr>");
}%>
</table>

<%= MiscWebUtils.getReqInfo(request) %>

</body>
</html>
