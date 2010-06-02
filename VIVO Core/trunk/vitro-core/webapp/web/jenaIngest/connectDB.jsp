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

    <h2>Connect to Jena Database</h2>

    <form action="ingest" method="post">
        <input type="hidden" name="action" value="connectDB"/>

    
	<input type="text" style="width:80%;" name="jdbcUrl" value="jdbc:mysql://localhost/"/>
    <p>JDBC URL</p>
 
    <input type="text" name="username"/>
    <p>username</p>

    <input type="password" name="password"/>
    <p>password</p>


		<input id="tripleStoreRDB" name="tripleStore" type="radio" checked="checked" value="RDB"/>
			<label for="tripleStoreRDB">Jena RDB</label>
		<input id="tripleStoreSDB" name="tripleStore" type="radio" value="SDB"/>
			<label for="tripleStoreRDB">Jena SDB (hash layout)</label>

    
        <select name="dbType">
            <option value="MySQL">MySQL</option>
        </select>
    <p>database type</p>

    <input type="submit" value="Connect Database"/>
