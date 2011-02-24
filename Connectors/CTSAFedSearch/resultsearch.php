<?php
/*Copyright (c) 2011, University of Florida
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the University of Florida nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 Contributors: James Pence
 */

echo '<?xml version="1.0" encoding="utf-8"?>
';
$descriptsite = urldecode(str_replace("_-percent-_","%",$_GET["descsite"]) );
$term = $_GET["term"];
$iframes = $_GET["iframes"];

	//echo "descsite = \"" . $descriptsite . "\" \n\n";
	$xmlDoc = simplexml_load_file($descriptsite);//opening a single site description
	if($xmlDoc !=null){
		$name = $xmlDoc->xpath('name');
		$Partner = trim( (string) ($name[0]) );

		$icon = $xmlDoc->xpath('icon');
		$Logo = trim( (string) ($icon[0]) );
		$site = $xmlDoc->xpath('aggregate-query');
		$Page = trim( (string) ($site[0]) ) . $term;
		//echo "page = \"" . $Page . "\" \n\n";
		$pageDoc = simplexml_load_file($Page);
		if($pageDoc != null){
			$cnt = $pageDoc->xpath('count');
			$Count = ( (integer) ($cnt[0]) );
			$pop = $pageDoc->xpath('population-type');
			$Poptype = trim( (string) ($pop[0]) );
			$prvew = $pageDoc->xpath('preview-URL');
			$Previewsite = trim( (string) ($prvew[0]) );
			$result = $pageDoc->xpath('search-results-URL');
			$Searchresult = trim( (string) ($result[0]) );
		}else{
			echo "
			<!-- " ;
			echo $Page;
			echo " result page ";
			echo $i;
			echo " returned null -->
			";
			$Partner = "";
			$Logo = "";
			$Page = "";
			$Count = 0;
			$Previewsite = "";
			$Searchresult = "";
		}
	}else{
		echo "
		<!--" ;
		echo $descriptsite;
		echo " description site ";
		echo $i;
		echo " returned null -->
		";
		$Partner = "";
		$Logo = "";
		$Page = "";
		$Count = 0;
		$Previewsite = "";
		$Searchresult = "";
	}

	if($Partner != ""){
		//echo "<tr>\n";
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
		echo "<div class=\"searchhits\">\n";

		if($Partner != ""){
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
						echo "<a style='font-size:1.5em'  href='" . $Searchresult. "'>" . $Partner . "</a>\n";
					}
					echo "<br/> " .$Poptype . "  \n";
					break;
				case 1 :
					if($Count == 0)
					{
						echo $Count . " Pe" . (($Count == 1)?"rson":"ople") . "\n";
					}else{
						echo "<a style='font-size:1.5em' href='" . $Searchresult. "'>" .
						$Count . " Pe" . (($Count == 1)?"rson":"ople") . "</a>  \n";
					}
					break;
				case 2 :
					if($Count != 0)
					{
						if($iframes){
						 echo "<iframe src='" . trim($Previewsite) . 
							"' width='200' height='150'> iframes not supported</iframe>";
						}else{
						if($Logo != "")
							{
								list($width, $height, $type, $attr) = getimagesize($Logo[$inc]);
								if( ($width * 0.75) > $height)
								{
									echo "<a href='" .trim($Previewsite) ."'><img src='" . $Logo . "' width='200' /></a>";
								}else{
									echo "<a href='" .trim($Previewsite) ."'><img src='" . $Logo . "' height='150' /></a>";
								}
							}
						}
					}

					break;
			}
			echo "</div> <!-- singleResult -->\n";//single result


		}

		echo "</div> <!-- searchhits -->
		";
		echo "</div>\n";//left right or center
		echo "</td>\n";
	}
	//echo "</tr>";
	}
?>
