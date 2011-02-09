<?php
/*Copyright (c) 2010, University of Florida
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the University of Florida nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 Contributors: James Pence
 *
 */


ini_set('memory_limit', '64M');//Memory is intensly used on large searches.
include_once('./simple_html_dom.php');//The DOM library is used to

function exception_error_handler($errno, $errstr, $errfile, $errline ) {//placed to silence errors (ie unresponsive pages)
	//echo "\nhad an error at line " . $errline . " - " . $errstr . "\n";
	//throw new Exception("boom");
}
set_error_handler("exception_error_handler");
$filetype = $_GET["return"];//Getting the type for the search
$term = $_GET["querytext"];//Getting the term for the search
if($term == ""){
	$term = "informatics";
}
$term = urlencode(trim($term));
//----------- location of fsearchsites
$siteList ="./fsearchsites.xml";
$xmlDoc = simplexml_load_file($siteList);
$partsite = $xmlDoc->xpath('description-site-URL');
for($i = 0; $i <= count($partsite); $i++){
	$descriptsite[$i] = trim( (string) ($partsite[$i]) );
	//echo "descriptsite(" . $i . ") = \"" . $descriptsite[$i] . "\" \n";
}
//for each site getting Partner, Page, Count,Poptype, Previewsite, Searchresult
for($i = 0; $i < count($descriptsite); $i++){
	$xmlDoc = simplexml_load_file($descriptsite[$i]);//opening a single site description
	if($xmlDoc !=null){
		$name = $xmlDoc->xpath('name');
		$Partner[$i] = trim( (string) ($name[0]) );

		$icon = $xmlDoc->xpath('icon');
		$Logo[$i] = trim( (string) ($icon[0]) );
		$site = $xmlDoc->xpath('aggregate-query');
		$Page[$i] = trim( (string) ($site[0]) ) . $term;
		//echo "page(" . $i . ") = \"" . $Page[$i] . "\" \n\n";
		$pageDoc = simplexml_load_file($Page[$i]);
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
	}else{
		$Partner[$i] = "";
		$Logo[$i] = "";
		$Page[$i] = "";
		$Count[$i] = 0;
		$Previewsite[$i] = "";
		$Searchresult[$i] = "";
	}

	//echo "\"" . $Partner[$i] . "\" \"" . $Page[$i] . "\" \"" . $Count[$i] . "\" \n \"" . $Previewsite[$i] . "\" \"" . $Searchresult[$i] . "\" \n";
}

//header from vivo
$VivoHeader ='<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Federated Search</title>
<link rel="icon" type="image/x-icon" href="https://phonebook.ufl.edu/favicon.ico" />
<link rel="shortcut icon" type="image/x-icon" href="https://phonebook.ufl.edu/favicon.ico" />
<link rel="stylesheet" type="text/css" media="screen" href="https://phonebook.ufl.edu/static/basic.css" />
<link rel="stylesheet" type="text/css" media="print" href="https://phonebook.ufl.edu/static/print.css" />
<style type="text/css" media="screen">
  @import "https://phonebook.ufl.edu/static/common.css";
  @import "https://phonebook.ufl.edu/static/pri.css";
</style>
</head>
<body>
<div id="header"> 
  <h2 class="replace" id="logo"><span style="padding-left:2em">Federated Search</span></h2>

  <h2 class="replace" id="unit"><a href="http://www.ufl.edu/">University of Florida<span></span></a></h2>
</div><!-- #header -->
<div id="mainContainer"> 

  <div id="content">  ';

//footer from vivo
$VivoFooter = '</div><!-- #content -->

</div><!-- #mainContainer -->
<div id="footer">
  <h3>Frequently Used Sites</h3>
  <ul>

    <li><a href="http://news.ufl.edu/">News</a></li>
    <li><a href="http://calendar.ufl.edu/">Calendar</a></li>
    <li><a href="https://phonebook.ufl.edu/">Phonebook</a></li>
    <li><a href="https://my.ufl.edu/ps/signon.html">MyUFL</a></li>
    <li><a href="http://www.isis.ufl.edu/">ISIS</a></li>
    <li><a href="http://www.ufl.edu/websites/">Web Site Listing</a></li>

    <li><a href="http://campusmap.ufl.edu/">Campus Map</a></li>
    <li><a href="http://webmail.ufl.edu/">WebMail</a></li>
    <li><a href="http://www.questions.ufl.edu/">Ask UF</a></li>
  </ul>
  <p>&copy; <a href="http://www.ufl.edu/" id="footerHome" title="University of Florida">University of Florida<span></span></a>, Gainesville, FL 32611; (352) 392-3261.</p>
  <h3>General Site Information</h3>

  <ul>
    <li><a href="https://phonebook.ufl.edu/about/">About This Site</a></li>
    <li><a href="http://www.ufl.edu/disability/">Disability Services</a></li>
    <li><a href="http://www.webadmin.ufl.edu/policies/internet_privacy/">Privacy Policy</a></li>
    <li><a href="http://search.ufl.edu/">Search</a></li>
  </ul>
  <p>This page uses <a href="http://www.google.com/analytics/">Google Analytics</a> (<a href="http://www.google.com/intl/en_ALL/privacypolicy.html">Google Privacy Policy</a>)</p>

</div><!-- #footer -->
<script type="text/javascript" src="https://assets.webadmin.ufl.edu/js/common/1.0.1/common.min.js"></script>
<script type="text/javascript" src="https://assets.webadmin.ufl.edu/js/search/1.0.1/search.min.js"></script>
<script type="text/javascript">
  // Set default for subpages
  Query.setDefaultValue(\'phonebook\', \'Search UF People\');
</script>
<script src="https://ssl.google-analytics.com/ga.js" type="text/javascript"></script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-3703196-32");
pageTracker._trackPageview();
} catch(err) {}
</script>
<script type="text/javascript" src="https://assets.webadmin.ufl.edu/js/ga-outbound-tracking/1.0.0/ga-outbound-tracking.min.js"></script>
</body>
</html>';

echo $VivoHeader;
echo '
<style type="text/css">
    div.topBanner { text-align: center; padding-bottom: 10px; } 

    div.bodyLeft { width: 275px; vertical-align:text-top; text-align: left; } 

    div.bodyCenter { vertical-align:middle; width: 100px; } 

    div.bodyRight { width: 240px; vertical-align:text-top; }

    div.singleResult{ height:180px; font-size:1em;}
    
</style>';


echo ' <form name="input" action= "' . $_SERVER["PHP_SELF"] . '" method="get">';
echo ' Search Term: <input type="text" name="querytext" value="' . $_GET["querytext"] . '" />';
echo ' <input type="submit" value="Search" /><br/>';
echo '</form>';

echo "<br />";
echo "Results for \"" . urldecode($term) . "\".";
echo "<br /> <br />\n";

echo "<div style='width:750px;margin-left:auto; margin-right:auto;'>\n";
echo "<table border = '0'>";
//for each site getting Partner, Page, Count,Poptype, Previewsite, Searchresult
for($inc = 0;$inc < count($Page);$inc++){
	echo "<tr>\n";

	for($col = 0;$col < 3; $col++){// it goes through a column at a time.
		echo "<td>";
		switch($col){
			case 0 :
				echo "<div class='bodyLeft'>\n";
				break;
			case 1 :
				echo "<div class='bodyCenter'>\n";
				break;
			case 2 :
				echo "<div class='bodyRight'>\n";
				break;
		}
		echo "<ul class=\"searchhits\">\n";

		if($Partner[$inc] != ""){
			echo "<div class='singleResult'>\n";
			if($col != 2){
				echo "<br />\n";//iframe compensation
			}
			switch($col){
				case 0 :
					//if($Count[$inc] == 0)
					//{
					//echo  $Partner[$inc] . "\n";
						//}else
						{
							echo "<a  href='" . $Searchresult[$inc]. "'>" . $Partner[$inc] . "</a>\n";
						}
						echo "<br/> " .$Poptype[$inc] . "  \n";
						break;
				case 1 :
					if($Count[$inc] == 0)
					{
						echo $Count[$inc] . " Pe" . (($Count[$inc] == 1)?"rson":"ople") . "\n";
					}else{
						echo "<a  href='" . $Searchresult[$inc]. "'>" .
						$Count[$inc] . " Pe" . (($Count[$inc] == 1)?"rson":"ople") . "</a>  \n";
					}
					break;
				case 2 :
					if($Count[$inc] != 0)
					{
						echo '<script type="text/javascript">
						if(!navigator.userAgent.match("Safari")){
						';//show IFRAMES?
						// if IFRAMES become a problem comment them out here.
						 echo "document.write(\"<iframe src='" .
							 trim($Previewsite[$inc]) . 
							"' width='200' height='150'> iframes not supported</iframe>\");";
						
						echo '}
						else
						{';
						echo "document.write(\"";
						if($Logo[$inc] != "")
						{
							list($width, $height, $type, $attr) = getimagesize($Logo[$inc]);
							if( ($width * 0.75) > $height)
							{
								echo "<a href='" .trim($Previewsite[$inc]) ."'><img src='" . $Logo[$inc] . "' width='200' /></a>";
							}else{
								echo "<a href='" .trim($Previewsite[$inc]) ."'><img src='" . $Logo[$inc] . "' height='150' /></a>";
							}
						}
						echo "\");";
						echo '}</script>';
					}

					break;
			}
			echo "</div> <!-- singleResult -->\n";//single result


		}

		echo "</ul>\n";
		if(false){
			echo "<br><a href =\"./fs.xml\" > Site description XML </a>";
			echo "<br><a href =\"http://". $_SERVER['SERVER_NAME'] . $_SERVER['PHP_SELF'] . "?return=xml&amp;querytext=" . $term . "\" > These results in XML </a>";
			echo "<br><a href =\"http://". $_SERVER['SERVER_NAME'] . $_SERVER['PHP_SELF'] . "?return=prev&amp;querytext=" . $term . "\" > Preview page of these results </a>";
		}
		echo "</div>\n";//left right or center
		echo "</td>\n";

	}
}
	echo "</table>\n";


	echo $VivoFooter;

	?>
