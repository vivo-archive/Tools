SPARQL
  |
  |-- lib                                                       # libraries
  |
  |-- src                                                       # java source files
  |    |
  |    |-- edu
  |         |
  |         |-- indiana
  |                |
  |                |-- slis
  |                     |
  |                     |-- swl
  |                          |
  |                          |-- service
  |                          |      |
  |                          |      |-- SparqlQueryService.java # contains basic functions of the builder, communicates with SesameService.java
  |                          |
  |                          |-- servlet
  |                          |      |
  |                          |      |-- GetClazz.java           # read classes from the database
  |                          |      |
  |                          |      |-- GetObject.java          # read object classes of given subject and predicate from the database
  |                          |      |
  |                          |      |-- GetProperty.java        # read predicates  of given subject from the database
  |                          |      |
  |                          |      |-- GetResult.java          # execute the composed sparql query and retrieve the data
  |                          |
  |                          |-- sesame
  |                          |      |-- SesameService.java      # communicates with sesame api
  |                          |      |-- DataIngest.java         # demo for ingesting rdf data into database
  |                          |
  |                          |-- utils
  |                                 |
  |                                 |-- SPARQL.java             # provides construction function for query and result file
  |
  |-- vivo.rdf.xml                                              # data source files
  |
  |-- WebRoot                                                   # web application directory
         |
         |-- index.jsp                                          # index of the web application
         |
         |-- META-INF
         |
         |-- scripts                                            # javascripts
         |
         |-- WEB-INF
                |
                |-- classes
                |
                |-- db                                          # database directory
                |
                |-- lib
                |
                |-- namespaces.properties                       # lists the namespaces
                |
                |-- web.xml