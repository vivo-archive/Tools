<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<h2>Load RDF Data</h2>

<form style="margin-bottom: 2ex;" action="uploadRDF" method="POST" enctype="multipart/form-data">
<input type="hidden" name="modelName"
	value="<%=request.getParameter("modelName")%>" /> <input type="hidden"
	name="action" value="loadRDFData" />
<p>RDF document URI: <input type="text" size="32" name="docLoc" /></p>
<p>Or upload a file from your computer:</p>
<p><input type="file" name="filePath" /></p>

<select name="language">
	<option value="RDF/XML">RDF/XML</option>
	<option value="N3">N3</option>
	<option value="N-TRIPLE">N-Triples</option>
	<option value="TTL">Turtle</option>
</select>
<br></br>
<br></br>
<p><input type="submit" name="submit" value="Load Data" /></p>
</form>