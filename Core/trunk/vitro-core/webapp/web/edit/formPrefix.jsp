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

<%@ page import="java.util.List" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<c:set var='themeDir'><c:out value='${portalBean.themeDir}'/></c:set>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>  <!-- formPrefix.jsp -->
<%  
	String useTinyMCE = (useTinyMCE=request.getParameter("useTinyMCE")) != null && !(useTinyMCE.equals("")) ? useTinyMCE : "false";
	if (useTinyMCE.equalsIgnoreCase("true")) {
		String height = (height=request.getParameter("height")) != null && !(height.equals("")) ? height : "200";
		String width  = (width=request.getParameter("width")) != null && !(width.equals("")) ? width : "75%";
		String defaultButtons="bold,italic,underline,separator,link,bullist,numlist,separator,sub,sup,charmap,separator,undo,redo,separator,code";
		String buttons = (buttons=request.getParameter("buttons")) != null && !(buttons.equals("")) ? buttons : defaultButtons;
		String tbLocation = (tbLocation=request.getParameter("toolbarLocation")) != null && !(tbLocation.equals("")) ? tbLocation : "top";
%>
		<script language="javascript" type="text/javascript" src="../js/tiny_mce/tiny_mce.js"></script>
	    <script language="javascript" type="text/javascript">
		tinyMCE.init({
			theme : "advanced",
			mode : "textareas",
			theme_advanced_buttons1 : "<%=buttons%>",
			theme_advanced_buttons2 : "",
			theme_advanced_buttons3 : "",
			theme_advanced_toolbar_location : "<%=tbLocation%>",
			theme_advanced_toolbar_align : "left",
			theme_advanced_statusbar_location : "bottom",
			theme_advanced_path : false,
			theme_advanced_resizing : true,
			height : "<%=height%>",
			width  : "<%=width%>",
			valid_elements : "a[href|name|title],br,p,i,em,cite,strong/b,u,sub,sup,ul,ol,li",
			fix_list_elements : true,			
			fix_nesting : true,
			cleanup_on_startup : true,
		    gecko_spellcheck : true,
		    forced_root_block: false
            //forced_root_block : 'p',
		    // plugins: "paste",
		    // theme_advanced_buttons1_add : "pastetext,pasteword,selectall",
		    // paste_create_paragraphs: false,
		    // paste_create_linebreaks: false,
		    // paste_use_dialog : true,
		    // paste_auto_cleanup_on_paste: true,
		    // paste_convert_headers_to_strong : true
			// save_callback : "customSave",
			// content_css : "example_advanced.css",
			// extended_valid_elements : "a[href|target|name]",
			// plugins : "table",
			// theme_advanced_buttons3_add_before : "tablecontrols,separator",
			// invalid_elements : "li",
			// theme_advanced_styles : "Header 1=header1;Header 2=header2;Header 3=header3;Table Row=tableRow1", // Theme specific setting CSS classes
		});
	    </script>
<%	} %>

    <script language="javascript" type="text/javascript" src="../js/extensions/String.js"></script>
	<script language="javascript" type="text/javascript" src="../js/jquery.js"></script>
	<script language="javascript" type="text/javascript" src="../js/jquery_plugins/jquery.bgiframe.pack.js"></script>
	<script language="javascript" type="text/javascript" src="../js/jquery_plugins/thickbox/thickbox-compressed.js"></script>
	<!-- <script language="javascript" type="text/javascript" src="../js/jquery_plugins/ui.datepicker.js"></script>	-->
	<script language="javascript" type="text/javascript" src="../js/jquery_plugins/jquery-autocomplete/jquery.autocomplete.pack.js"></script>
	
<%	String useAutoComplete = (useAutoComplete=request.getParameter("useAutoComplete")) != null && !(useAutoComplete.equals("")) ? useAutoComplete : "false";
	if (useAutoComplete.equalsIgnoreCase("true")) { %>
	    <link rel="stylesheet" type="text/css" href="../js/jquery_plugins/jquery-autocomplete/jquery.autocomplete.css"/>	
<%	} %>


    <c:forEach var="jsFile" items="${customJs}">
        <script type="text/javascript" src="${jsFile}"></script>
    </c:forEach>     
    
    <link rel="stylesheet" type="text/css" href="../js/jquery_plugins/thickbox/thickbox.css"/>	
	<link rel="stylesheet" type="text/css" href="<c:url value="/${themeDir}css/screen.css"/>" media="screen"/>	
	<link rel="stylesheet" type="text/css" href="<c:url value="/${themeDir}css/formedit.css" />" media="screen"/>

    <c:forEach var="cssFile" items="${customCss}">
        <link rel="stylesheet" type="text/css" href="${cssFile}" media="screen"/>
    </c:forEach>
     
    <title>Edit</title>
</head>
<body class="formsEdit">
<div id="wrap" class="container">
    <jsp:include page="/${themeDir}jsp/identity.jsp" flush="true"/>
    <jsp:include page="/${themeDir}jsp/menu.jsp" flush="true"/>
    <div id="contentwrap">
			<div id="content" class="form">
        <!-- end of formPrefix.jsp -->
