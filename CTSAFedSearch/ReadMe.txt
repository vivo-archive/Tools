Copyright (c) 2010, University of Florida
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of the University of Florida nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 

-------------

The "singlefs.php" replaces the "fs.xml" with "singlefs.php?return=desc"


Lines that should be changed.

In ??_fs.php:
    lines 26,27,28 are the school identifiers
    line 31 is the description of the population

In aggregatedsearch.php:
    lines 63-147 = Wrapper page before results
    lines 245-305 = Wrapper page before results
    line 27 location of fsearchsites.xml
    
aggsearch.php is geared towards being able to get its own wrapper.
In aggsearch.php:
    line 29 page to get the wrapper from
    line 32 div to place the results into ('#contents' is id='contents' , '.contents' is class='contents')
    line 35 location of fsearchsites.xml



The aggregatedsearch.php uses fsearchsites.xml to get the location of the "??_fs.php" or the "fs.xml" file.
The folder "fs" has the current fs.xml files as seen from another box.
The versions of the fs.xml with the php files are set to work from "localhost".
the ??_fs.php file finds itself and returns the description.

fs.php is geared to reurn the vivo site results in the desired xml.
The ??_fs.xml sites point to the fs.php with the search data.

the flow of information can be shown as :

Old process:aggregatedsearch.php --calls-->fsearchsites.xml --to find--> ??_fs.xml --to call--> fs.php?(proper values) --to get--> search data --displayed--> aggregated search page.

New process :aggregatedsearch.php --calls-->fsearchsites.xml --to find--> ??_fs.php?return=desc --to call-->??_fs.php?return=xml&querytext=TERM --to get-->search data --displayed-->

the other network sites will bet told where to find the ??_fs.xml which then calls fs.php to get them the xml they are looking for.