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
$VivoHeader ='<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" lang="en-US"> 
<head profile="http://gmpg.org/xfn/11"> 
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> 
 
 <meta name="robots" content="index,follow" /> 
 
 <link rel="canonical" href="http://dev-clinicaltrials.ctsi.ufl.edu/" /> 
 
 <link rel="stylesheet" type="text/css" href="http://www.ctsi.ufl.edu/wp-content/themes/thematic/style.css" /> 
 
 <link rel="alternate" type="application/rss+xml" title="CTSI RSS Feed" href="http://www.ctsi.ufl.edu/feed/" />  <link rel="pingback" href="http://www.ctsi.ufl.edu/xmlrpc.php" /> 
 
 
<!-- page title --> 
<title>Clinical and Translational Science Institute - Federated Search</title> 
 
 
 
<script type="text/javascript" src="http://www.ctsi.ufl.edu/wp-content/themes/thematic/library/scripts/tabber.js"></script> 
<link rel="stylesheet" type="text/css" href="http://www.ctsi.ufl.edu/wp-content/themes/thematic/library/styles/tabber.css"> 
 
 <!-- begin content slider --> 
  <link rel="stylesheet" href="http://www.ctsi.ufl.edu/wp-content/themes/thematic/slider/coda-slider.css" type="text/css" media="screen" title="no title" charset="utf-8" /> 
  <script src="http://www.ctsi.ufl.edu/wp-content/themes/thematic/slider/jquery.scrollto.js" type="text/javascript"></script> 
  <script src="http://www.ctsi.ufl.edu/wp-content/themes/thematic/slider/jquery.localscroll.js" type="text/javascript" charset="utf-8"></script> 
  <script src="http://www.ctsi.ufl.edu/wp-content/themes/thematic/slider/jquery.serialscroll.js" type="text/javascript" charset="utf-8"></script> 
  <script src="http://www.ctsi.ufl.edu/wp-content/themes/thematic/slider/coda.slider.js" type="text/javascript" charset="utf-8"></script> 
 <!-- end content slider --> 
 
</head> 
 
<body class="home page page-id-65 page-parent page-template page-template-template-page-home-php linux chrome ch5-0"> 
 
<div id="wrapper" class="hfeed">   
 
<!-- Header -->
<div id="header"> 
  <div id="branding"> 
    <div id="branding-left"> 
      <a href="http://www.ctsi.ufl.edu"><img src="http://www.ctsi.ufl.edu/wp-content/themes/thematic/library/images/header-logo-uf-48.png" title="CTSI"></a> 
    <div id="titles"> 
      <a href="http://www.ctsi.ufl.edu"><h1>Clinical and Translational Science Institute</h1><h2>UNIVERSITY <em>of</em> FLORIDA</h2></a>
    </div> 
  </div> 
 
  <div id="branding-right"> 
</div> 
<div class="spacer"></div> 
</div> 
 
<div id="shadow-top">...</div> 
<div id="access"> 
  <center><font color="#FFFFFF">
   <br/>
   <h1>Federated Search</h1>
   </center></font>
</div>
<div id="shadow-bottom">...</div> 
</div><!-- #header--> 
 
<!-- Sets Page Width + Light Grey -->
<div id="main">';

//footer from vivo
$VivoFooter = '
</div> <!-- #main --> 
    
 
<!-- Site Footer -->
<div id="footer"> 
<div id="footercontainer"> 
  <div id="siteinfo"> 
    <div id="siteinfo-left"> 
      <a href="http://www.ctsi.ufl.edu/wp-admin">CTSI Portal Login</a>  ::  Updated February 3, 2011  ::  <a href="mailto:info@ctsi.ufl.edu">Contact Webmaster</a>  ::  &#169; 2011 <a href="http://www.ufl.edu">University of Florida</a><br /> 
      <a href="/disclaimer-and-permitted-use/">Disclaimer & Permitted Use</a>  ::  <a href="http://www.ufl.edu/disability">Disability Services</a>  ::  <a href="https://security.health.ufl.edu/">Security Policies</a>  ::  <a href="http://privacy.ufl.edu/privacystatement.html">Privacy Policy</a>  ::  <a href="http://privacy.ufl.edu/">Privacy Office</a><br /> 
      This page uses <a href="http://www.google.com/analytics/">Google Analytics</a> (<a href="http://www.google.com/privacy.html">Google Privacy Policy</a>)<br /> 
      The UF CTSI is supported in part by NIH awards UL1 RR029890, KL2 RR029888 and TL1 RR029889
<br/>
<a href="http://www.w3.org/RDF/" title="RDF Resource Description
Framework"> <img border="0" src="http://www.w3.org/RDF/icons/rdf_w3c_icon.48"
alt="RDF Resource Description Framework Icon"/></a>
    </div> 
    <div id="siteinfo-right"> 
      <a href="http://www.ufl.edu">
      <img src="http://www.ctsi.ufl.edu/wp-content/themes/thematic/library/images/footer_logo.jpg" title="UF" /></a> 
    </div> 
  </div><!-- #siteinfo --> 
</div><!-- #footer --> 
</div><!-- #wrapper .hfeed -->  
 
</body> 
</html>';

echo $VivoHeader;
echo '<style type="text/css">
    div#topBanner { text-align: center; padding-bottom: 10px; } 

    div#bodyLeft { margin-left: 100px; width: 275px; vertical-align:text-top; } 

    div#bodyCenter { margin-left: 400px; vertical-align:middle; width: 100px; position: absolute; } 

    div#bodyRight { position: absolute;  margin-left: 500px; width: 240px; vertical-align:text-top; }

    div#singleResult{ height:200px; font-size:0.7em;}
</style>';


echo ' <form name="input" action= "' . $_SERVER["PHP_SELF"] . '" method="get">';
echo ' Search Term: <input type="text" name="querytext" value="' . $_GET["querytext"] . '" />';
echo ' <input type="submit" value="Search" /><br/>';
echo '</form>';

echo "<br />";
echo "Results for \"" . urldecode($term) . "\".";
echo "<br /> <br />\n";

for($col = 2;$col > -1; $col--){// it goes through a column at a time.
	switch($col){
		case 0 :
			echo "<div id='bodyLeft'>\n";
			break;
		case 1 :
			echo "<div id='bodyCenter'>\n";
			break;
		case 2 :
			echo "<div id='bodyRight'>\n";
			break;
	}
	echo "<ul class=\"searchhits\">\n";

	//for each site getting Partner, Page, Count,Poptype, Previewsite, Searchresult
	for($inc = 0;$inc < count($Page);$inc++)
	if($Partner[$inc] != ""){
		echo "<div id='singleResult'>\n";
		if($col != 2){
			echo "<br /><br />\n";//iframe compensation
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
				 echo "<div style=\"border:1px solid;\"><iframe src='" .
				 trim($Previewsite[$inc]) .
					"' width='200' height='150'>\n iframes not supported\n</iframe></div>\n";
				}

				break;
		}
		echo "</div>\n";//single result


	}

	echo "</ul>\n";
	if(false){
		echo "<br><a href =\"./fs.xml\" > Site description XML </a>";
		echo "<br><a href =\"http://". $_SERVER['SERVER_NAME'] . $_SERVER['PHP_SELF'] . "?return=xml&amp;querytext=" . $term . "\" > These results in XML </a>";
		echo "<br><a href =\"http://". $_SERVER['SERVER_NAME'] . $_SERVER['PHP_SELF'] . "?return=prev&amp;querytext=" . $term . "\" > Preview page of these results </a>";
	}
	echo "</div>\n";//left right or center


}



echo $VivoFooter;

?>

