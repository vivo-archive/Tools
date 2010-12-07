<?php 
/*******************************************************************************
Copyright (c) 2010, Dale Scheppler
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of the author nor the names of any contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
function getURI($URL)
{
	$URL = str_replace("http://", "", $URL);
	$URL = str_replace("https://", "", $URL);
	$URLData = explode("/", $URL);
	$URLLength = sizeof($URLData);
	$URLLength = $URLLength - 1;
	return $URLData[$URLLength];
	
}
function getSite($URL)
{
	$URL = str_replace("http://", "", $URL);
	$URL = str_replace("https://", "", $URL);
	$URLData = explode("/", $URL);
	return $URLData[0];
}
function echoDiv($id, $string)
{
	if (strlen($string) > 0)
	{
	echo "<p class=\"".$id."\">".$string."</p>\n";
	}
}
function echoImageDiv($id, $string, $name)
{
	if (strlen($string) > 0)
	{
	echo "<img class=\"vivoImage\" src=\"".$string."\" alt=\"".$name."\">\n";	
	}
}
function echoLinkDiv($id, $string)
{
	if (strlen($string) > 0)
	{
	echo "<a class=\"".$id."\" href=\"".$string."\">".$string."</a>\n";
	}
}
function echoEmailDiv($id, $string)
{
	if (strlen($string) > 0)
	{
	echo "<a class=\"".$id."\" href=\"mailto:".$string."\">".$string."</a>\n";
	}
	
}
function isURLValid($URL)
{
	$pageContents = @file_get_contents($URL) or print_r("");
	if(strlen($pageContents) < 1)
	{
		return False;
	}
	else
	{
		return True;
	}
}
?>
<?php
function getVIVOPersonData($search){
	include("arc2/ARC2.php");
	$identifier = getURI($search);
	$personURI = $search; //What's the VIVO URI for this person?
	$site = getSite($search);
	$personRDF = "http://".$site."/individual/".$identifier."/".$identifier.".rdf"; //Where is the raw RDF?
	$personSubject = "http://".$site."/individual/".$identifier;
	$parser = ARC2::getRDFParser();
	$parser->parse($personRDF);
	$index = $parser->getSimpleIndex();

	//Get the person's name, this should be checking for foaf:firstName, but it doesn't exist anywhere I've seen.
	//First, let's try for an active directory name. This only exists on the UF VIVO ontology extension
	$personName = $index[$personSubject]["http://vivo.ufl.edu/ontology/vivo-ufl/activeDirName"][0];
	//If we don't find it..
	if (strlen($personName) < 1)
	{
		//Fallback to rdfs:label
		$personName = $index[$personSubject]["http://www.w3.org/2000/01/rdf-schema#label"][0];
	}
	//End getting person's name
	
	//Get person's title
	//Try the default location
	$position = $index[$personSubject]["http://vivoweb.org/ontology/core#preferredTitle"][0];
	//We're going to set the initial position URL here so we can use it later
	$positionURL = $index[$personSubject]["http://vivoweb.org/ontology/core#personInPosition"][0];
	$positionIdentifier = getURI($positionURL);
	$positionRDF = "http://".$site."/individual/".$positionIdentifier."/".$positionIdentifier.".rdf";
	$positionSubject = "http://".$site."/individual/".$positionIdentifier;
	//If we don't find it there, try in the position
	if(strlen($position) < 1)
	{
//		$positionURL = $index[$personSubject]["http://vivoweb.org/ontology/core#personInPosition"][0];
		if(isURLValid($positionURL))
		{
			$positionParser = ARC2::getRDFParser();
			$positionParser->parse($positionRDF);
			$positionIndex = $positionParser->getSimpleIndex();
			//Let's check for an HR job title first
			$position = $positionIndex[$positionSubject]["http://vivoweb.org/ontology/core#hrJobTitle"][0];
			//If we don't find one
			if(strlen($position) < 1)
			{
				//Fallback to the label
				$position = $positionIndex[$positionSubject]["http://www.w3.org/2000/01/rdf-schema#label"][0];
			}
		}
		else
		{
			//If the first position link is bad, try the second one.
			$positionURL = $index[$personSubject]["http://vivoweb.org/ontology/core#personInPosition"][1];
			$positionIdentifier = getURI($positionURL);
			$positionRDF = "http://".$site."/individual/".$positionIdentifier."/".$positionIdentifier.".rdf";
			$positionSubject = "http://".$site."/individual/".$positionIdentifier;
			$positionParser = ARC2::getRDFParser();
			$positionParser->parse($positionRDF);
			$positionIndex = $positionParser->getSimpleIndex();
			//Let's check for an HR job title first
			$position = $positionIndex[$positionSubject]["http://vivoweb.org/ontology/core#hrJobTitle"][0];
			//If we don't find one
			if(strlen($position) < 1)
			{
				//Fallback to the label
				$position = $positionIndex[$positionSubject]["http://www.w3.org/2000/01/rdf-schema#label"][0];
			}
		}
	}
	//End getting title
	
	//Begin getting Email
	$personEmail = $index[$personSubject]["http://vivoweb.org/ontology/core#workEmail"][0];
	//End getting email
	//Begin getting phone
	$personPhone = $index[$personSubject]["http://vivoweb.org/ontology/core#workPhone"][0];
	//End Getting phone
	//Begin getting Fax
	$personFax = $index[$personSubject]["http://vivoweb.org/ontology/core#workFax"][0];
	//End Getting Fax
	//Begin getting department
	$positionParser = ARC2::getRDFParser();
	$positionParser->parse($positionRDF);
	$positionIndex = $positionParser->getSimpleIndex();
	$departmentURL = $positionIndex[$positionSubject]["http://vivoweb.org/ontology/core#positionInOrganization"][0];
	$departmentIdentifier = getURI($departmentURL);
	$departmentRDF = "http://".$site."/individual/".$departmentIdentifier."/".$departmentIdentifier.".rdf";
	$departmentSubject = "http://".$site."/individual/".$departmentIdentifier;
	$departmentParser = ARC2::getRDFParser();
	$departmentParser->parse($departmentRDF);
	$departmentIndex = $departmentParser->getSimpleIndex();
	$department = $departmentIndex[$departmentSubject]["http://www.w3.org/2000/01/rdf-schema#label"][0];
	//End getting department
	
	//Begin getting image
	
	//End getting image
	$imageURL = $index[$personSubject]["http://vitro.mannlib.cornell.edu/ns/vitro/public#mainImage"][0];
	$imageIdentifier = getURI($imageURL);
	$imageRDF = "http://".$site."/individual/".$imageIdentifier."/".$imageIdentifier.".rdf";
	$imageSubject = "http://".$site."/individual/".$imageIdentifier;
	$imageParser = ARC2::getRDFParser();
	$imageParser->parse($imageRDF);
	$imageIndex = $imageParser->getSimpleIndex();
	$filename = $imageIndex[$imageSubject]["http://vitro.mannlib.cornell.edu/ns/vitro/public#filename"][0];
	$downloadLocation = $imageIndex[$imageSubject]["http://vitro.mannlib.cornell.edu/ns/vitro/public#downloadLocation"][0];
	$fullsizeURI = getURI($downloadLocation);
	$fullsizeURL = "http://".$site."/file/".$fullsizeURI."/".$filename;
	//End getting image
	
	
	

$vivoName = strip_tags($personName);
$vivoTitle = strip_tags($position);
$vivoEmail = strip_tags($personEmail);
$vivoLink = strip_tags($personURI);
$vivoPhone = strip_tags($personPhone);
$vivoFax = strip_tags($personFax);
$vivoDepartment = strip_tags($department);
$vivoImage = strip_tags($fullsizeURL);

//Now let's output the data.
echo "<div id=\"vivoPerson\">\n";
echoImageDiv("vivoImage", $vivoImage, $vivoName);
echoDiv("vivoName", $vivoName); //Done
echoDiv("vivoTitle", $vivoTitle); //Done
echoDiv("vivoDepartment", $vivoDepartment);
echoDiv("vivoPhone", $vivoPhone); //Done
echoDiv("vivoFax", $vivoFax); //Done
echoEmailDiv("vivoEmail", $vivoEmail); //Done
echoLinkDiv("vivoLink", $vivoLink); //Done
echo "</div>";

//TODO: Write a method to clear up some of that repetitive code
}
?>