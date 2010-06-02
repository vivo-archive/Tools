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

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%/* this odd thing points to something in web.xml */ %>
<!-- %@ page errorPage="/error.jsp"% -->
<%	/***********************************************
		 Used to display a search form.
		 
		 request.attributes:
		 		 
		 request.parameters:
		 None yet.
		 
		  Consider sticking < % = MiscWebUtils.getReqInfo(request) % > in the html output
		  for debugging info.
		  		 		 		
    **********************************************/
%>
<c:set var='portal' value='${requestScope.portal}'/>
<c:set var='portalBean' value='${requestScope.portalBean}'/>

<c:set var='themeDir' >
	<c:out value='${portal.themeDir}'/>
</c:set>
<div class='contents searchForm'>

<div class="advancedSearchForm">
<form name="filterForm" method="post" action="search">
<h3>Search</h3>
<input class="top_padded" style="width:97%;" name="querytext" value="" type="text"/>
<!-- supplanted by including OR, NOT etc. with search terms
<input name="inclusion" value="all" checked="checked" type="radio"/> all terms entered
   <input name="inclusion" value="any" type="radio"/> any terms entered
</p>
-->

<p><input class="form-button" value=" Search" type="submit"/></p>
</form>		
</div><!--advancedSearchForm-->	
<div class='searchTips'>
<jsp:include page="searchTips.jsp"/>
</div>					

</div>

