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

<h3>Search Tips</h3>		
<ul>
<li>Use short, single terms unless your searches are returning too many results</li>
<li>When you enter more than one term, the search will look for records containing <strong>all</strong> of them unless you add the word "OR" between your terms.</li>
<li>"NOT" can help limit searches -- e.g., <i>climate NOT change</i></li>
<li>Except for boolean operators, searches are <strong>not</strong> case-sensitive, so "Geneva" and "geneva" are equivalent</li>
<li>Enclose a phrase in quotes (") to search for the whole phrase, not individual words (e.g., "protein folding") -- <i>both leading and ending quotes are required</i></li>
<li>Phrase searches may be combined with boolean operators: <i>"climate change" OR "global warming"</i></li>
<li>The search uses <strong>stemming</strong> by default so that close word variations will also be found (e.g., "sequence" also matches "sequences" and "sequencing").
    Use the wildcard <strong>*</strong> character to match wider variation (e.g., <strong>nano*</strong> to match both
    <i>nanotechnology</i> and <i>nanofabrication</i>), but note that searching uses <i>stemmed</i>, or shortened, versions of words,
    so "cogniti*" finds nothing while "cognit*" finds both <i>cognitive</i> and <i>cognition</i></li>
<li>If you're not sure of the spelling, put a <strong>~</strong> at the end -- e.g., <i>cabage~</i> finds <i>cabbage</i>,
    <i>steven~</i> finds <i>Stephen</i> and <i>Stefan</i> (as well as a few unwanted extra words)</li>
<li>To match a person or unit's primary entry, enter <i>name: Smith</i> or <i>name: Biology</i></li>
</ul>