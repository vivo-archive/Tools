#!/bin/bash

# Set working directory
set -e

DIR=$(cd "$(dirname "$0")"; pwd)
cd $DIR
cd ..

HARVESTER_TASK=mods

if [ -f scripts/env ]; then
  . scripts/env
else
  exit 1
fi

#variables for model arguments
HCONFIG="config/jenaModels/h2.xml"
INPUT="-i $HCONFIG -IdbUrl=jdbc:h2:XMLVault/h2MODS/all/store -ImodelName=MODS"
OUTPUT="-o $HCONFIG -OdbUrl=jdbc:h2:XMLVault/h2MODS/all/store -OmodelName=MODS"
VIVO="-v $VIVOCONFIG"
SCORE="-s $HCONFIG -SdbUrl=jdbc:h2:XMLVault/h2MODS/score/store -SmodelName=MODS"
MATCHOUTPUT="-o $HCONFIG -OdbUrl=jdbc:h2:XMLVault/h2MODS/match/store -OmodelName=MODS"
MATCHINPUT="-i $HCONFIG -IdbUrl=jdbc:h2:XMLVault/h2MODS/match/store -ImodelName=MODS"





#clear old fetches
rm -rf XMLVault/h2MODS/XML

# Execute Fetch for MODS
$MODSFetch -X config/tasks/MODSFetch.xml

# backup fetch
date=`date +%Y-%m-%d_%T`
tar -czpf backups/.$date.tar.gz XMLVault/h2MODS/XML
rm -rf backups/mods.xml.latest.tar.gz
ln -s mods.xml.$date.tar.gz backups/mods.xml.latest.tar.gz
# uncomment to restore previous fetch
#tar -xzpf backups/mods.xml.latest.tar.gz XMLVault/h2MODS/XML







# clear old translates
rm -rf XMLVault/h2MODS/RDF

# Execute Translate using the MODSToVIVO.xsl file
$XSLTranslator -i config/recordHandlers/MODS-XML.xml -x config/datamaps/MODSToVivo.xsl -o config/recordHandlers/MODS-RDF.xml

# backup translate
date=`date +%Y-%m-%d_%T`
tar -czpf backups/mods.rdf.$date.tar.gz XMLVault/h2MODS/RDF
rm -rf backups/mods.rdf.latest.tar.gz
ln -s mods.rdf.$date.tar.gz backups/mods.rdf.latest.tar.gz
# uncomment to restore previous translate
#tar -xzpf backups/mods.rdf.latest.tar.gz XMLVault/h2MODS/RDF

# Clear old H2 models
rm -rf XMLVault/h2MODS/all
rm -rf XMLVault/h2MODS/temp

# Execute Transfer to import from record handler into local temp model
$Transfer $OUTPUT -h config/recordHandlers/MODS-RDF.xml

# backup H2 translate Models
date=`date +%Y-%m-%d_%T`
tar -czpf backups/mods.all.$date.tar.gz XMLVault/h2MODS/all
rm -rf backups/mods.all.latest.tar.gz
ln -s ps.all.$date.tar.gz backups/mods.all.latest.tar.gz
# uncomment to restore previous H2 translate models
#tar -xzpf backups/mods.all.latest.tar.gz XMLVault/h2MODS/all

# clear old score models
rm -rf XMLVault/h2MODS/score

# Execute Score to disambiguate data in "scoring" JENA model
# Execute match to match and link data into "vivo" JENA model
LEVDIFF="org.vivoweb.harvester.score.algorithm.NormalizedLevenshteinDifference"
WORKEMAIL="-AwEmail=$LEVDIFF -FwEmail=http://vivoweb.org/ontology/core#workEmail -WwEmail=0.5 -PwEmail=http://vivoweb.org/ontology/score#workEmail"
#FNAME="-AfName=$LEVDIFF -FfName=http://xmlns.com/foaf/0.1/firstName -WfName=0.3 -PfName=http://vivoweb.org/ontology/score#foreName"
LNAME="-AlName=$LEVDIFF -FlName=http://xmlns.com/foaf/0.1/lastName -WlName=0.5 -PlName=http://xmlns.com/foaf/0.1/lastName"
#MNAME="-AmName=$LEVDIFF -FmName=http://vivoweb.org/ontology/core#middleName -WmName=0.2 -PmName=http://vivoweb.org/ontology/score#middleName"
mkdir XMLVault/h2MODS/temp/
TEMP="-t XMLVault/h2MODS/temp/"

$Score $VIVO $INPUT $TEMP $SCORE $WORKEMAIL $LNAME
$Match $INPUT $SCORE $MATCHOUTPUT -t 0.9 -r
#$Score $VIVO $INPUT $TEMP $SCORE $FNAME $LNAME $MNAME
#$Match $INPUT $SCORE -t 0.8 -r
 
# back H2 score models
date=`date +%Y-%m-%d_%T`
tar -czpf backups/mods.scored.$date.tar.gz XMLVault/h2MODS/score
rm -rf backups/mods.scored.latest.tar.gz
ln -s ps.scored.$date.tar.gz backups/mods.scored.latest.tar.gz
# uncomment to restore previous H2 score models
#tar -xzpf backups/mods.scored.latest.tar.gz XMLVault/h2MODS/score

#remove score statements
$Qualify $MATCHINPUT -n http://vivoweb.org/ontology/score -p

# Execute ChangeNamespace to get into current namespace
$ChangeNamespace $VIVO $MATCHINPUT -n $NAMESPACE -o http://vivoweb.org/harvest/modsPub/
$ChangeNamespace $VIVO $MATCHINPUT -n $NAMESPACE -o http://vivoweb.org/harvest/modsAuthorship/
$ChangeNamespace $VIVO $MATCHINPUT -n $NAMESPACE -o http://vivoweb.org/harvest/modsAuthor/
$ChangeNamespace $VIVO $MATCHINPUT -n $NAMESPACE -o http://vivoweb.org/harvest/modsJournal/

# Backup pretransfer vivo database, symlink latest to latest.sql
date=`date +%Y-%m-%d_%T`
mysqldump -h $SERVER -u $USERNAME -p$PASSWORD $DBNAME > backups/$DBNAME.mods.pretransfer.$date.sql
rm -rf backups/$DBNAME.mods.pretransfer.latest.sql
ln -s $DBNAME.mods.pretransfer.$date.sql backups/$DBNAME.mods.pretransfer.latest.sql

#Update VIVO, using previous model as comparison. On first run, previous model won't exist resulting in all statements being passed to VIVO
VIVOMODELNAME="modelName=http://vivoweb.org/ingest/mods"
INMODELNAME="modelName=MODS"
INURL="dbUrl=jdbc:h2:XMLVault/h2MODS/match/store"
ADDFILE="XMLVault/update_Additions.rdf.xml"
SUBFILE="XMLVault/update_Subtractions.rdf.xml"
  
# Find Subtractions
$Diff -m $VIVOCONFIG -M$VIVOMODELNAME -s $HCONFIG -S$INURL -S$INMODELNAME -d $SUBFILE
# Find Additions
$Diff -m $HCONFIG -M$INURL -M$INMODELNAME -s $VIVOCONFIG -S$VIVOMODELNAME -d $ADDFILE
# Apply Subtractions to Previous model
$Transfer -o $VIVOCONFIG -O$VIVOMODELNAME -r $SUBFILE -m
# Apply Additions to Previous model
$Transfer -o $VIVOCONFIG -O$VIVOMODELNAME -r $ADDFILE
# Apply Subtractions to VIVO
$Transfer -o $VIVOCONFIG -r $SUBFILE -m
# Apply Additions to VIVO
$Transfer -o $VIVOCONFIG -r $ADDFILE

# Backup posttransfer vivo database, symlink latest to latest.sql
date=`date +%Y-%m-%d_%T`
mysqldump -h $SERVER -u $USERNAME -p$PASSWORD $DBNAME > backups/$DBNAME.mods.posttransfer.$date.sql
rm -rf backups/$DBNAME.mods.posttransfer.latest.sql
ln -s $DBNAME.mods.posttransfer.$date.sql backups/$DBNAME.mods.posttransfer.latest.sql

#Restart Tomcat
#Tomcat must be restarted in order for the harvested data to appear in VIVO
echo $HARVESTER_TASK ' completed successfully'
/etc/init.d/tomcat stop
/etc/init.d/apache2 reload
/etc/init.d/tomcat start
