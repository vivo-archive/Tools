# VIVO Tools

Several tools have been developed for working with VIVO data, 
principally to facilitate data ingest and updating or to streamline re-use of data 
from VIVO in other applications. 
Most of these tools are available in the [VIVO repository](https://github.com/vivo-project/VIVO).

These tools are in various stages of maturity -- please note any README files or 
other indicators by the authors.

## Tools developed and shared to date include:

* [Extensions](https://sourceforge.net/projects/vivo/files/Utilities/) to 
[Google Refine](http://refine.google.com/) to support reconciliation of data with VIVO 
(e.g., for author alignment prior to publications ingest) and transformation of source data 
from spreadsheets, XML, or JSON into VIVO-compatible RDF (Eliza Chan). 
[Further documentation](https://sourceforge.net/apps/mediawiki/vivo/index.php?title=Extending_Google_Refine_for_VIVO) 
describes how to add the Google Refine extensions and activate this additional functionality in VIVO.

* SPARQL Query Builder provides an interactive user interface to generate SPARQL queries against the VIVO ontology (Yuyin Sun).

* Linked Data Index Builder is an alpha tool used to create the [VIVO multi-site search](http://vivosearch.org/) (Brian Caruso)

* Refworks Java Translator (Stephen Williams)

## Connectors from VIVO to other applications:

* CTSAFedSearch is a federated search tool that accesses VIVO and other CTSA researcher 
profile applications across the country (James Pence)

* PHPVIVO is an example code of using VIVO's linked data to pull a staff directory from VIVO 
for a department website (James Pence)

* VIVOtoWordPress, an alpha connector from VIVO to WordPress (Dale Scheppler)

* [OrgCrawler](https://sourceforge.net/projects/vivo/files/Utilities/), 
a tool written in [R](http://www.r-project.org/) to query a VIVO instance and generate 
a graphical view of the organizational hierarchy under a root organization URI (Alex Rockwell).

## Notable additional projects maintained outside of the VIVO Sourceforge site:

* [Semantic web services](http://semanticservice.svn.sourceforge.net/) supporting connection 
to any SPARQL endpoint and transformation of query results into XML and JSON 
for consumption on websites not yet capable of issuing or processing the results of 
SPARQL queries directly (John Fereira).

* [VIVO search Drupal module](https://github.com/milesworthington/vivosolr) is built on top 
of the [Drupal model](http://drupal.org/project/Solr) for the 
[Apache Solr Framework](http://lucene.apache.org/solr/) and provides integration with a 
[VIVO search index](http://vivosearch.org/) built using the Linked Data Index Builder 
described above (Miles Worthington)

* [Drupal RDFimporter module](http://drupal.org/sandbox/milesw/1085078), a general purpose tool 
used to create sites such as http://impact.cals.cornell.edu from a VIVO instance (Miles Worthington)

### Please contact us if you wish to nominate additional tools for listing here.
