/*******************************************************************************
Copyright (c) 2010, Dale Scheppler
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of the author nor the names of any contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/


Filename: getVIVOPersonData.php
Purpose: Returns data from a person entity in VIVO that can then be formatted to fit a page style.
Version: 1.4

Usage:

This one is pretty straightforward. Include the file and pass it a string however you want. The string should be the link to a VIVO person entity. For example, if the URI you wish to grab is "https://vivo.ufl.edu/display/n25562", then you would pass a string of "https://vivo.ufl.edu/display/n25562".

Example code follows:


<?php 
include 'getVIVOPersonData.php';
//Example of getting the data from the URL, such as http://www.yoursite.com/foo.php?search=n25562
getVIVOPersonData($search = $_GET["search"]);
//Example of getting the data from another page, say from a text field.
getVIVOPersonData($search = $_POST["searchValue"]);
//And you can just pass it a string of the URI.
getVIVOPersonData("https://vivo.ufl.edu/display/n25562");
?>

Example Output:

<div id="vivoPerson">
<img class="vivoImage" src="http://vivo.ufl.edu/file/n34850/_main_image_491-NUCATS-STS-042310.jpg" alt="Conlon, Mike">
<p class="vivoName">Conlon, Mike</p>
<p class="vivoTitle">Associate Director and Chief Operating Officer</p>
<p class="vivoDepartment">Health Outcomes and Policy</p>
<p class="vivoPhone">3522738872</p>
<p class="vivoFax">3522147882</p>
<a class="vivoEmail" href="mailto:mconlon@ufl.edu">mconlon@ufl.edu</a>
<a class="vivoLink" href="https://vivo.ufl.edu/display/n25562">https://vivo.ufl.edu/display/n25562</a>
</div>

Known Issues:

If the VIVO implementation is lagging, this can take a few seconds to complete.
Differences in ontology can cause results to vary.