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


<div class="contents">

<h1>Import OWL/RDFS Ontologies</h1>

<span class="warning">Note:  This is not yet fully stable.  Avoid running on production servers.</span>

<form name="owlFromUri" method="post" action="importOwl">
<p>Import OWL/RDF XML ontology from URI:</p>

<input name="owlUri" size="60%"/>
<input type="submit" value="Import"/>

<p>Value to use for property domains/ranges that are not specified or cannot be interpreted:
<select name="nulldomrange">
	<option value="none"><em>none</em></option>
	<option value="owl:Thing" selected="selected">owl:Thing</option>
</select></p>

<p>Format <select name="isN3">
	<option value="false">RDF/XML</option>
	<option value="true">N3</option>
</select></p>
<!-- <input type="checkbox" disabled="disabled" name="omitInd" />
import classes and properties only (no individuals) -->
</form>

</div><!-- contents -->


</div>

