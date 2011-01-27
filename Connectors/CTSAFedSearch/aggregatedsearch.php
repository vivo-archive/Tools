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
$VivoHeader ='<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">  
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en"><head>
  <!-- headContent.jsp -->

<link rel="stylesheet" type="text/css" href="http://vivo.ufl.edu/themes/vivo-basic/css/screen.css" media="screen"/>
<link rel="stylesheet" type="text/css" href="http://vivo.ufl.edu/themes/vivo-basic/css/print.css" media="print"/>

<link rel="stylesheet" type="text/css" href="http://vivo.ufl.edu/themes/vivo-basic/css/edit.css"/>

<script language="javascript" src="http://vivo.ufl.edu/js/jquery.js"></script>
<script language="javascript" src="http://vivo.ufl.edu/js/feedWriters.js"></script>

<title>VIVO</title>

<!-- end headContent.jsp -->
  </head>
<body >
<div id="wrap" class="container">
  <div id="header">
    <div id="institution">
  <div class="replace" id="logo">
    <a href="http://www.uflib.ufl.edu/">George A. Smathers Libraries</a>
  </div>

  <form id="search" action="http://vivo.ufl.edu/search" method="get">
    <input type="hidden" name="site" value="qa.vivo.ufl.edu">
    <label for="searchbox">
      <input name="querytext" id="searchbox" type="text" size="20" value="Search VIVO" alt="Search" onfocus="select();">
    </label>
    <input id="image" type="submit" name="Submit" value="Go">
  </form>
</div>
<div id="identity">
  <h1><a title="Home" href="http://vivo.ufl.edu/">VIVO</a></h1>

</div>
<div id="navAndSearch" class="block">
    
<div id="primaryAndOther">
    <ul id="primary">
            <li>
                <a href="http://vivo.ufl.edu/index.jsp?primary=1"  class="activeTab" >
                    Home
                </a>
            </li>          
            <li>
                <a href="http://vivo.ufl.edu/index.jsp?primary=983349090" >

                    People
                </a>
            </li>          
            <li>
                <a href="http://vivo.ufl.edu/index.jsp?primary=2084405211" >
                    Academic Units
                </a>
            </li>          
            <li>
                <a href="http://vivo.ufl.edu/index.jsp?primary=283864618" >
                    Research
                </a>

            </li>          
            <li>
                <a href="http://vivo.ufl.edu/index.jsp?primary=681167054" >
                    Events & Seminars
                </a>
            </li>          
            <li>
                <a href="http://vivo.ufl.edu/browse" >
                    Index
                </a>

            </li>          
    </ul>
</div></div> <!--  end navAndSearch -->

<div id="breadcrumbs" class="small"><div class="breadCrumbs"> <a href="http://vivo.ufl.edu/index.jsp?primary=1">VIVO</a> </div></div>

</div><!--header-->
    <hr class="hidden" />
    <div id="contentwrap" class="withSidebar">

        <div id="content">
        <div id="contents">';

//footer from vivo
$VivoFooter = '
<br>
    </div> <!-- contents -->
    </div><!-- content -->        <div id="sidebar">
          <h2>Log into VIVO</h2>
          <p>
            <a href="http://vivo.ufl.edu/siteAdmin">
              <img src="http://vivo.ufl.edu/themes/vivo-basic/images/login.jpg" width="186" height="43" alt="Login">
            </a>
          </p>

          <h2>Latest from VIVO</h2>
          <ul id="latestVIVOFeed">
            <li>Please enable javascript for best viewing experience.</li>
          </ul>
          <p>
            <a href="http://vivoweb.org/blog" class="more">More from the VIVO blog</a>
          </p>

        </div> <!-- sidebar -->
</div> <!-- contentwrap -->
    
<div id="footer">
  <div class="footerLinks">
		<ul class="otherNav">
        <li><a href="http://vivo.ufl.edu/about" title="more about this web site">About VIVO</a></li>
        <li><a href="http://vivo.ufl.edu/contact" title="feedback form">Contact Us</a></li>

		<li><a href="http://privacy.ufl.edu/privacystatement.html">Privacy Policy</a></li>
		<li class="last"><a href="http://www.ufl.edu/">University of Florida</a></li>
        </ul>
		<div id="uflogo"><a href="http://www.ufl.edu/"><img src="http://vivo.ufl.edu/themes/vivo-basic/images/UF_white.png" width="196" height="35" alt="University of Florida"></a></div>
    </div>
    <div class="copyright">
		    &copy;2010&nbsp;

			VIVO Project</div>
	    <div class="copyright">
		    All Rights Reserved. <a href="http://vivo.ufl.edu/termsOfUse?home=1">Terms of Use</a>
	    </div>
	</div>
</div>

<script type="text/javascript">  var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www."); document.write(unescape("%3Cscript src=\'" + gaJsHost + "google-analytics.com/ga.js\' type=\'text/javascript\'%3E%3C/script%3E")); 
</script>  

<script type="text/javascript">  
try { 
var pageTracker = _gat._getTracker("UA-12495269-1");
pageTracker._setDomainName("vivo.ufl.edu");
pageTracker._setAllowLinker(true);
pageTracker._trackPageview(); 
var rollupTracker = _gat._getTracker("UA-12531954-1");
rollupTracker._setDomainName("none"); 
rollupTracker._setAllowLinker(true);
rollupTracker._trackPageview(location.host+location.pathname);  
} 
catch(err) {}  
</script>
</div> <!-- wrap -->
</body>
</html>
';

echo $VivoHeader;
echo '<style type="text/css">
    div#topBanner { text-align: center; padding-bottom: 10px; } 

    div#bodyLeft { width: 275px; } 

    div#bodyCenter { margin-left: 300px; width: 100px; position: absolute; } 

    div#bodyRight { position: absolute;  margin-left: 400px; width: 300px; }

    div#singleResult{ height:150px; vertical-align:middle;}
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
   		echo "<li>\n";
		echo "<div id='singleResult'>\n";
		if($col != 2){
			echo "<br /><br />\n";//iframe compensation
		}
		switch($col){
			case 0 :
   				if($Count[$inc] == 0)
				{
   				echo  $Partner[$inc] . "\n";
				}else{
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
				if($Count[$inc] != 0 && $browser == 'safari')
				{
					if($Logo[$inc] != ""){
						echo "<img src='" . $Logo[$inc] . "' />"; 
					}
				}else
				{
				 echo "<iframe src='" .
					 trim($Previewsite[$inc]) . 
					"' width='200' height='150'>\n iframes not supported\n</iframe>\n";
				}

			break;
		}
		echo "</div>\n";//single result
   		echo "</li>\n";


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

