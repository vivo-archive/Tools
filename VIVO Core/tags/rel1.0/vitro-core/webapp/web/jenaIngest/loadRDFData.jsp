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

    <h2>Load RDF Data</h2>

    <form style="margin-bottom:2ex;" action="ingest" method="post">
    	<input type="hidden" name="modelName" value="<%=request.getParameter("modelName")%>"/>
	<input type="hidden" name="action" value="loadRDFData"/>
	<p>RDF document URI: <input type="text" size="32" name="docLoc"/></p>
	<p>Or local filesystem path: <input type="text" size="32" name="filePath"/>
	(If a directory is supplied, will attempt to load all files therein)</p>
	<p>    
        <select name="language">
        	<option value="RDF/XML">RDF/XML</option>
        	<option value="N3">N3</option>
        	<option	value="N-TRIPLE">N-Triples</option>
            <option value="TTL">Turtle</option>
        </select>
		<input type="submit" name="submit" value="Load Data"/>
	</p>
	
    </form>
