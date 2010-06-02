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

<div class="staticPageBackground">

<h2> Export to RDF </h2>

<form action="" method="get">

<ul>
    <li style="list-style-type:none;"><input type="radio" name="subgraph" checked="checked" value="full"/> Export entire RDF model (including application metadata)</li>
    <li style="list-style-type:none;"><input type="radio" name="subgraph" value="tbox"/> Export ontology/ontologies (TBox)</li>
    <li style="list-style-type:none;"><input type="radio" name="subgraph" value="abox"/> Export instance data (ABox)</li>
</ul>

<hr/>

<ul>
    <li style="list-style-type:none;"><input type="radio" name="assertedOrInferred" checked="checked" value="asserted"/> Export only asserted statements </li>
    <li style="list-style-type:none;"><input type="radio" name="assertedOrInferred" value="inferred"/> Export only inferred statements </li>
    <li style="list-style-type:none;"><input type="radio" name="assertedOrInferred" value="full"/> Export asserted and inferred statements together </li>
</ul>

<h3>Select format</h3>
<select name="format">
    <option value="RDF/XML">RDF/XML</option>
    <option value="RDF/XML-ABBREV">RDF/XML abbrev.</option>
    <option value="N3">N3</option>
    <option value="N-TRIPLES">N-Triples</option>
    <option value="TURTLE">Turtle</option>
</select>

<input type="submit" name="submit" value="Export"/>

</form>

</div>