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

<!-- this is a page set up to be parsed by the dynamicFields tag -->
<!-- the datapropsNN, $$, $foo, and @x symbols are special notations understood by this tag and are not part of JSP -->

<!-- @pre -->

<li style="padding-top:0.8em;padding-bottom:0.5em;" id="datapropsNNsuper">
<strong>$fieldName</strong><a id="datapropsNNaddLink" style="margin-left:0.5em;font-style:italic;font-weight:bold;" href="#" onClick="addLine(this, 'dataprops');return false;">add</a>

<span id="datapropsNNgenTaName" style="display:none;">$genTaName</span>

<ul id="datapropsNNul">

<li style="display:none;">This is a dummy li to make sure the ul has at least one child.</li>

<!-- @template -->

<li id="datapropsNN" style="margin-left:0em;margin-top:0.4em;margin-bottom:0.4em;">
    <div id="datapropsNNcontent">
        <span id="datapropsNNcontentValue">$$</span>
        <a id="datapropsNNeditLink" href="#" style="margin-left:0.8em;font-style:italic" onClick="convertLiToTextarea(this, 'dataprops');return false;">edit</a>
        <a id="datapropsNNdeleteLink" href="#" style="margin-left:0.8em;font-style:italic" onClick="deleteLine(this, 'dataprops');return false;">remove</a>
	<a id="datapropsNNundeleteLink" href="#" style="display:none;margin-left:0.8em;font-style:italic" onClick="undeleteLine(this, 'dataprops');return false;">restore</a>
    </div>
    <div id="datapropsNNta" style="display:none;">
        <div style="padding:0;margin:0;">
            <textarea style="margin:0;padding:0;width:95%;height:16ex;" id="datapropsNNtata" name="$taName">$$</textarea>
        </div>
        <div style="padding:0;margin:0;">
            <input id="datapropsNNokLink" style="margin:0;padding:0;" type="button" onClick="backToLi(this);return false;" value='OK'/>
            <input id="datapropsNNcancelLink" style="margin:0;padding:0;" type="button" onClick="cancelBackToLi(this);return false;" value='cancel'/>
            <input id="datapropsNNosLink" style="margin-left:5em;padding:0;" type="button" onClick="backToLi(this);submitPage();return false;" value='OK & save all changes'/>
        </div>
    </div>
</li>

<!-- @post -->

</ul>
</li>

