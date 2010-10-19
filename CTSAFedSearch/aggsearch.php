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
    //echo "\nhad an error at line " . $errline . " - " . $errstr . "\n";
	//throw new Exception("boom");
}
set_error_handler("exception_error_handler");



$term = $_GET["querytext"];//Getting the term for the search
$term = urlencode(trim($term));

//----------- page to get the wrapper.
$wrapper = file_get_html('http://vivo.ufl.edu');

//------------ div to place the results in.
$content = $wrapper->find('#contents',0); // '#contents' is id='contents' , '.contents' is class='contents'

//------------- location of fsearchsites
$xmlDoc = simplexml_load_file("./fsearchsites.xml");
$partsite = $xmlDoc->xpath('description-site-URL');
for($i = 0; $i < count($partsite); $i++){
	$descriptsite[$i] = trim( (string) ($partsite[$i]) );
	//echo "descriptsite(" . $i . ") = \"" . $descriptsite[$i] . "\" \n";
}
//for each site getting Partner, Page, Count,Poptype, Previewsite, Searchresult
for($i = 0; $i < count($descriptsite); $i++){
	$xmlDoc = simplexml_load_file($descriptsite[$i]);
	if($xmlDoc !=null){
	$name = $xmlDoc->xpath('name');
	$Partner[$i] = trim( (string) ($name[0]) );
	$icon = $xmlDoc->xpath('icon');
	$Logo[$i] = trim( (string) ($icon[0]) );
	$site = $xmlDoc->xpath('aggregate-query');
	$Page[$i] = trim( (string) ($site[0]) ) . $term;
	//echo "page(" . $i . ") = \"" . $Page[$i] . "\" \n\n";
	$pageDoc = simplexml_load_file($Page[$i]);
	}
	if($pageDoc != null){
	$cnt = $pageDoc->xpath('count');
	$Count[$i] = ( (integer) ($cnt[0]) );
	$pop = $pageDoc->xpath('population-type');
	$Poptype[$i] = trim( (string) ($pop[0]) );
	$prvew = $pageDoc->xpath('preview-URL');
	$Previewsite[$i] = trim( (string) ($prvew[0]) );
	$result = $pageDoc->xpath('search-results-URL');
	$Searchresult[$i] = trim( (string) ($result[0]) );
	}else{
	$Count[$i] = 0;
	$Previewsite[$i] = "";
	$Searchresult[$i] = "";
	}

	//echo "\"" . $Partner[$i] . "\" \"" . $Page[$i] . "\" \"" . $Count[$i] . "\" \n \"" . $Previewsite[$i] . "\" \"" . $Searchresult[$i] . "\" \n";
}



$result = "";

$result = $result . '<style type="text/css">
    div#topBanner { text-align: center; padding-bottom: 10px; } 

    div#bodyLeft { width: 275px; } 

    div#bodyCenter { margin-left: 300px; width: 100px; position: absolute; } 

    div#bodyRight { position: absolute;  margin-left: 400px; width: 300px; }

    div#singleResult{ height:150px; vertical-align:middle;}
</style>';

    
$result = $result . ' <form name="input" action= "' . $_SERVER["PHP_SELF"] . '" method="get">';
$result = $result . ' Search Term: <input type="text" name="querytext" value="' . $_GET["querytext"] . '" />';
$result = $result . ' <input type="submit" value="Search" /><br/>';
$result = $result . '</form>';

	$result = $result . "<br />";
	$result = $result . "Results for \"" . urldecode($term) . "\".";
	$result = $result . "<br /> <br />\n";

$get= (get_browser(null,true));
$browser = strtolower($get['browser']);

for($col = 2;$col > -1; $col--){
	switch($col){
		case 0 :
			$result = $result . "<div id='bodyLeft'>\n";
		break;
		case 1 :
			$result = $result . "<div id='bodyCenter'>\n";
		break;
		case 2 :
			$result = $result . "<div id='bodyRight'>\n";
		break;
	}
	$result = $result . "<div class=\"searchhits\">\n";

//for each site getting Partner, Page, Count,Poptype, Previewsite, Searchresult
	for($inc = 0;$inc < count($Page);$inc++)
	{
   		//$result = $result . "<li>\n";
		$result = $result . "<div id='singleResult'>\n";
		if($col != 2){
			$result = $result . "<br /><br />\n";//iframe compensation
		}
		switch($col){
			case 0 :
   				//if($Count[$inc] == 0)
				//{
   				//echo  $Partner[$inc] . "\n";
				//}else
				{
   				$result = $result . "<a  href='" . $Searchresult[$inc]. "'>" . $Partner[$inc] . "</a>\n";
				}
				$result = $result . "<br/> " .$Poptype[$inc] . "  \n";
			break;
			case 1 :
   				if($Count[$inc] == 0)
				{
				$result = $result . $Count[$inc] . " Pe" . (($Count[$inc] == 1)?"rson":"ople") . ".\n";
				}else
				{
				$result = $result . "<a  href='" . $Searchresult[$inc]. "'>" . 
					$Count[$inc] . " Pe" . (($Count[$inc] == 1)?"rson":"ople") . ".</a>  \n";
				}
			break;
			case 2 :
				if($Count[$inc] != 0 && $browser == 'safari')
				{
					if($Logo[$inc] != ""){
						$result = $result ."<img src='" . $Logo[$inc] . "' />"; 
					}
				}else
				{
				 $result = $result . "<iframe src='" .
					 trim($Previewsite[$inc]) . 
					"' width='200' height='150'>\n iframes not supported\n</iframe>\n";
				}

			break;
		}
		$result = $result . "</div>\n";//single result
   		//$result = $result . "</li>\n";


	}

	$result = $result . "</div> <!--search results -->\n";
	if(false){
		$result = $result . "<br><a href =\"./fs.xml\" > Site description XML </a>";
		$result = $result . "<br><a href =\"http://". $_SERVER['SERVER_NAME'] . $_SERVER['PHP_SELF'] . "?return=xml&amp;querytext=" . $term . "\" > These results in XML </a>";
		$result = $result . "<br><a href =\"http://". $_SERVER['SERVER_NAME'] . $_SERVER['PHP_SELF'] . "?return=prev&amp;querytext=" . $term . "\" > Preview page of these results </a>";
	}
	$result = $result . "</div>\n";//left right or center

		
}
$content->innertext = $result;

echo $wrapper->save();

?>

