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

<html>
<%@ page contentType="text/html; charset=utf-8" %>
<%
/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
%>

<%@ include file="i18nLib.jsp" %>

<%
    // initialize a private HttpServletRequest
    setRequest(request);

    // set a resouce base
    setResouceBase("i18n");
%>

<head>
 <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
 <title>Apache-Axis</title>
</head>

<body bgcolor="#FFFFFF">

<h1 align="center">Apache-AXIS</h1>

<%= getLocaleChoice() %>

<%
  out.print(getMessage("welcomeMessage")+"<p/>");
  out.print(getMessage("operationType"));
%>

<ul>

  <li>
    <%
      out.print("<a href=\""+ getMessage("validationURL") +"\">");
      out.print(getMessage("validation") +"</a> - ");
      out.print(getMessage("validationFootnote00") +"<br>");
      out.print("<i>"+ getMessage("validationFootnote01") +"</i>");
    %>
  </li>

  <li>
    <%
      out.print("<a href=\""+ getMessage("serviceListURL") +"\">");
      out.print(getMessage("serviceList") +"</a> - ");
      out.print(getMessage("serviceListFootnote"));
    %>
  </li>

  <li>
    <%
      out.print("<a href=\""+ getMessage("callAnEndpointURL") +"\">");
      out.print(getMessage("callAnEndpoint") +"</a> - ");
      out.print(getMessage("callAnEndpointFootnote00") +" ");
      out.print(getMessage("callAnEndpointFootnote01"));
    %>
  </li>

  <li>
    <%
      out.print("<a href=\""+ getMessage("visitURL") +"\">");
      out.print(getMessage("visit") +"</a> - ");
      out.print(getMessage("visitFootnote"));
    %>
  </li>

  <li>
    <%
      out.print("<a href=\""+ getMessage("adminURL") +"\">");
      out.print(getMessage("admin") +"</a> - ");
      out.print(getMessage("adminFootnote"));
    %>
  </li>

  <li>
    <%
      out.print("<a href=\""+ getMessage("soapMonitorURL") +"\">");
      out.print(getMessage("soapMonitor") +"</a> - ");
      out.print(getMessage("soapMonitorFootnote"));
    %>
  </li>

</ul>

<%
  out.print(getMessage("sideNote") +"<p/>");
%>

<%
  out.print("<h3>"+ getMessage("validatingAxis") +"</h3>");

  out.print(getMessage("validationNote00") +"<p/>");
  out.print(getMessage("validationNote01"));
%>
</body>
</html>
