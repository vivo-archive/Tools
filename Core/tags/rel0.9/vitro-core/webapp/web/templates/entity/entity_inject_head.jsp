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

<%  /***********************************************
    This file is used to inject <link> and <script> elements
    into the head element of the generated source of a VITRO
    page that is being displayed by the entity controller.
    
    In other words, anything like domain.com/entity?...
    will have the lines specified here added in the <head>
    of the page.
    
    This is a great way to specify JavaScript or CSS files
    at the entity display level as opposed to globally.
    
    Example:
    <link rel="stylesheet" type="text/css" href="/css/entity.css"/>" media="screen"/>
    <script type="text/javascript" src="/js/jquery.js"></script>
****************************************************/ %>

<c:url var="jquery" value="/js/jquery.js"/>
<c:url var="getURLParam" value="/js/jquery_plugins/getURLParam.js"/>
<c:url var="colorAnimations" value="/js/jquery_plugins/colorAnimations.js"/>
<c:url var="propertyGroupSwitcher" value="/js/propertyGroupSwitcher.js"/>
<c:url var="vitroControls" value="/js/controls.js"/>
<c:url var="jqueryForm" value="/js/jquery_plugins/jquery.form.js"/>
<c:url var="tinyMCE" value="/js/tiny_mce/tiny_mce.js"/>

<script type="text/javascript" src="${jquery}"></script>
<script type="text/javascript" src="${getURLParam}"></script>
<script type="text/javascript" src="${colorAnimations}"></script>
<script type="text/javascript" src="${propertyGroupSwitcher}"></script>
<script type="text/javascript" src="${jqueryForm}"></script>
<script type="text/javascript" src="${tinyMCE}"></script>
<script type="text/javascript" src="${vitroControls}"></script>
