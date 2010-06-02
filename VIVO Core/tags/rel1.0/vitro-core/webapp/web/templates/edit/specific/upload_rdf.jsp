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


<div class="staticPageBackground">

<form action="uploadRDF" method="post"  enctype="multipart/form-data" >

<c:if test="${!empty param.errMsg}">       
    <p><strong class="warning">${errMsg}</strong></p>
</c:if>

    <p>Enter Web-accessible URL of RDF document:</p>
    <p><input name="rdfUrl" type="text" style="width:67%;" value="<c:out value='${param.rdfUrl}'/>"/></p>
    
    <p>Or upload a file from your computer: </p>
    <p><input type="file" name="rdfStream"/> </p>
    
    <p><input type="radio" name="mode" value="add" checked="checked"/>add RDF <input type="radio" name="mode" value="remove"/>remove RDF</p>
    <select name="language">
        	<option value="RDF/XML">RDF/XML</option>
        	<option value="N3">N3</option>
        	<option	value="N-TRIPLE">N-Triples</option>
            <option value="TTL">Turtle</option>
    </select>
    <p><input type="checkbox" name="makeClassgroups" value="true"/> create classgroups automatically</p>
            
    <c:if test="${requestScope.singlePortal == true}">
      <input type="hidden" name="checkIndividualsIntoPortal" value="current"/>
    </c:if>
    
    <c:if test="${requestScope.singlePortal == false }">
      <p>
      <input type="radio" name="checkIndividualsIntoPortal" value="current"/> make individuals visible in current portal
      <input type="radio" name="checkIndividualsIntoPortal" value="all"/> make individuals visible in all portals
      </p>
    </c:if>

    <p><input type="submit" name="submit" value="submit"/></p>     
</form>

</div>
