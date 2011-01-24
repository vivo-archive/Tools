Copyright (c) 2010, University of Florida
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of the University of Florida nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 

  Contributors: James Pence
-------------

The CTSAFederated search is a search ideally performed across several many servers simultaneously.

The "aggregatedsearch.php" pulls displays the XML from the associated search sites. The "fs.php" code is designed to navigate the DOM of the VIVO searchsite and get the required data and provide it in the prescribed XML.

For this script to work all the parts are expected to be in the same directory.
the parts:
   ??_fs.php              // ?? is the abbreviation of the instituion
   at least one of (aggregatedsearch.php,aggregatedsearchVer2.php,aggsearch.php)
   fsearchsites.xml       //altered to point to each remote sites' ??_fs.php
   simple_html_dom.php    //library used to do the html DOM parsing.

The "??_fs.php" replaces the "fs.xml" with "??_fs.php?return=desc"

Variables that should be changed.

In ??_fs.php:
   variables $school ,$name, and $icon are the school identifiers
   variables $popdesc is the description of the population

In aggregatedsearch.php: (Ver2 is set to use the Icons in preference to the IFRAMES.)
   variable $VivoHeader = Wrapper page HTML before results
   variable $VivoFooter = Wrapper page HTML after results
   variable $siteList   = Location of fsearchsites.xml
    
aggsearch.php is geared towards being able to get its own wrapper.
Remember that any root reference (href="/blah.blah/stuff") will only work if they are on the same web server.
In aggsearch.php:
   variable $wrapper  = the HTML from the the page with the wrapper
   variable $content  = div to place the results into ('#contents' is id='contents' , '.contents' is class='contents')
   variable $siteList = location of fsearchsites.xml


The aggregatedsearch.php uses fsearchsites.xml to get the location of the "??_fs.php" or the "fs.xml" file.
The folder "fs" has the current fs.xml files as seen from another box.
The versions of the fs.xml with the php files are set to work from "localhost".
the ??_fs.php file finds itself and returns the description.

fs.php is geared to reurn the vivo site results in the desired xml.
The ??_fs.xml sites point to the fs.php with the search data.

the flow of information can be shown as :

xml process:aggregatedsearch.php --calls-->fsearchsites.xml --to find--> ??_fs.xml --to call--> fs.php?(proper values) --to get--> search data --displayed--> aggregated search page.

php process :aggregatedsearch.php --calls-->fsearchsites.xml --to find--> ??_fs.php?return=desc --to call-->??_fs.php?return=xml&querytext=TERM --to get-->search data --displayed--> aggregated search page.

The other network sites will bet told where to find the ??_fs.php which gives the description to call ??_fs.php to get the xml of the search results.
