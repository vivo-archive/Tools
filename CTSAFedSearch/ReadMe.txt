Copyright (c) 2010, University of Florida
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of the University of Florida nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 

-------------

The project hasn't been set up, so the buildpath etc. is probably wrong.

The aggregatedsearch.php uses fsearchsites.xml to get the location of the ??_fs.xml.
The folder "fs" has the current fs.xml files as seen from another box.
The versions of the fs.xml with the php files are set to work from "localhost".

fs.php is geared to reurn the vivo site results in the desired xml.
The ??_fs.xml sites point to the fs.php with the search data.

the flow of information can be showen as :

aggregatedsearch.php --calls-->fsearchsites.xml --to find--> ??_fs.xml --to call--> fs.php?(proper values) --to get--> search data --displayed--> aggregated search page.

the other network sites will bet told where to find the ??_fs.xml which then calls fs.php to get them the xml they are looking for.