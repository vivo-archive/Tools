<?php
/*Copyright (c) 2010, University of Florida
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of the University of Florida nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
*/


ini_set('memory_limit', '64M');//Memory is intensly used on large searches.
include_once('./simple_html_dom.php');//The DOM library is used to 

function exception_error_handler($errno, $errstr, $errfile, $errline ) {
    //echo "had an error at line " . $errline . " - " . $errstr . "\n";
	//throw new Exception("boom");
}
set_error_handler("exception_error_handler");
$filetype = $_GET["return"];//Getting the type for the search
$term = $_GET["querytext"];//Getting the term for the search

//-----------School related
$school = array('vivo.iu.edu');// web of vivo school
$name = array('Indiana University');//name of vivo school
$icon = array('http://www.vivoweb.org/files/logos/partner_institutions/iu_logo.jpg');//logo of school
//description of the population searched
$popdesc = "Participants in the VIVO National Network include institutions with local installations of VIVO or those with research discovery and profiling applications that can provide semantic web-compliant data.";

$num = 0;// $_GET["school"];
$term = urlencode($term);
//$school = array('vivo.ufl.edu', 'vivo.cornell.edu', 'vivo.iu.edu', 'vivo.wustl.edu', 'vivo.psm.edu', 'vivo.med.cornell.edu', 'vivo.scripps.edu');//The list of school sites

//if($num >= count($school) ){$num = 0;}
//$name = array('University of Florida', 'Cornell University', 'Indiana University', 'Washington University in St. Louis', 'Ponce School of Medicine', 'Weill Cornell Medical College', 'The Scripps Research Institute');
if($filetype == "desc")

{//type = xml
	header ("Content-type: text/xml");
	echo"<?xml version='1.0'?>\n";
	{
	   echo "<site-description>\n";
	   echo "\t<name>" . $name[$num] . "</name>\n";
	   echo "\t<icon>" . $icon[$num] . "</icon>\n";
	   echo "\t<aggregate-query>" . 
		"http://". $_SERVER['SERVER_NAME'] . $_SERVER['PHP_SELF'] . "?return=xml&amp;querytext=" . 
		"</aggregate-query>\n";
	   echo "</site-description>\n";
	}
}else if($filetype == "xml"){
   $page = "http://" . $school[$num] . "/search?classgroup=http%3A%2F%2Fvivoweb.org%2Fontology%23vitroClassGrouppeople&querytext=" . $term;//Creating URLs
   try{	
   $html = file_get_html($page . "&startIndex=10000&hitsPerPage=1");//start index makes for less memory use
   
	$more = $html->find('.moreHits',0);
	while($more != null)
	{
		unset($html);
   		$html = file_get_html("http://" . $school[$num] . $more->href);
		$more = $html->find('.moreHits',0);
	}
	
	$es = $html->find('.searchpages',0);
	$number = 0;
	if($es != null){
		$number = count($es->children());//The count is the number of the searchpages listed
	}
	
   }
   catch(Exception $e){
      $number = 0;
   }
   $inc++;
   unset($html);

	header ("Content-type: text/xml");
	echo"<?xml version='1.0'?>\n";
	   echo "<aggregation-result>\n";
	   echo "<count>" . $number . "</count>\n"; 
	   
//-----Description of the popultion
	   echo  "<population-type>". $popdesc ."</population-type>" ;
	    
	   echo "<preview-URL>" . "http://" . $school[$num] . "/search?classgroup=http%3A%2F%2Fvivoweb.org%2Fontology%23vitroClassGrouppeople&amp;querytext=" . $term . "</preview-URL>\n";
	   echo "<search-results-URL>" ."http://" . $school[$num] . "/search?classgroup=http%3A%2F%2Fvivoweb.org%2Fontology%23vitroClassGrouppeople&amp;querytext=" . $term . "\n\t</search-results-URL>\n";
	   echo "</aggregation-result>\n";
	
}else 

?>

