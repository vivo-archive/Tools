package edu.cornell.mannlib.vitro.indexbuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.shared.Lock;

/**
 * This is a utility that will examin a Jena Model and return the URIs for
 * additional linked data requests that are needed to properly build the
 * solr document.
 */
 /*
  * for each object property in the model:
  *   is it special?
  *   yes
  *    add to two-hop-list
  *   no
  *    do we have the object's label?
  *    yes
  *     continue
  *    no
  *     add to one-hop-list
  *
  */
  
public class UrisForDataExpansion  {
    public Set<String> twoHopPredicates;
    public Set<String> predicatesToSkip;
    public Set<String> resouceNsToSkip;

    public UrisForDataExpansion(Set<String> twoHopPredicates, 
                                Set<String> predicatesToSkip, 
                                Set<String> resouceNsToSkip){
        this.twoHopPredicates = twoHopPredicates;
        this.predicatesToSkip = predicatesToSkip;
        this.resouceNsToSkip = resouceNsToSkip;
    }
    
    public Map<String,List<String>> getUris(String uri, Model model) {
        List<String> twoHopUris = new LinkedList<String>();
        List<String> oneHopUris = new LinkedList<String>();
        Map<String,List<String>> rmap = new HashMap<String,List<String>>();
        rmap.put("oneHop",oneHopUris);
        rmap.put("twoHop",twoHopUris);

        model.enterCriticalSection(Lock.READ);
        try{
            Resource ind = model.getResource(uri);

            if( skip( ind ) )
                return rmap;

            StmtIterator stmts = model.listStatements( ind , (Property)null, (RDFNode)null);
            while( stmts.hasNext()){
                Statement stmt = stmts.nextStatement();
                String predicateURI = stmt.getPredicate().getURI();
                //can only HTTP GET uri resources, no blank nodes
                if( stmt.getObject().isURIResource() && !predicatesToSkip.contains( predicateURI )) {                
                    Resource obj = stmt.getObject().as(Resource.class);
                    if( skip( obj ) ) 
                        continue;
                    if( twoHopPredicates.contains( predicateURI )){                    
                        twoHopUris.add( obj.getURI() );
                    }else{
                        //figure out if it has a rdfs:label
                        if( !skip( obj ) && ! obj.hasProperty( RDFS.label ) )
                            oneHopUris.add( obj.getURI());
                    }
                }            
            }
        }finally{
            model.leaveCriticalSection();
        }

        return rmap;            
    }
    
    public List<String> getSingleHopUris(String uri, Model model){
        List<String> oneHopUris = new LinkedList<String>();
        
        model.enterCriticalSection(Lock.READ);
        try{
            Resource ind = model.getResource(uri);

            if( skip(ind) )
                return oneHopUris;

            StmtIterator stmts = model.listStatements( ind , (Property)null, (RDFNode)null);
            while( stmts.hasNext()){
                Statement stmt = stmts.nextStatement();
                String predicateURI = stmt.getPredicate().getURI();
                //can only HTTP GET uri resources, no blank nodes
                if( stmt.getObject().isURIResource() && !predicatesToSkip.contains(predicateURI) ) {
                    Resource obj = stmt.getObject().as(Resource.class);
                    if( skip( obj ) ) 
                        continue;
                    //figure out if it has a rdfs:label
                    if( ! obj.hasProperty( RDFS.label ) ){
                        oneHopUris.add( obj.getURI());    
                    }            
                }
            }
        }finally{
            model.leaveCriticalSection();
        }

        return oneHopUris;
    }

    /**
     * Skip individuals that are in the skip namespace.
     */
    public boolean skip(Resource ind ){
        //don't index blank nodes, we cannot HTTP GET them.
        if( ! ind.isURIResource() )
            return true;

        for( String ns : resouceNsToSkip ) {
            if( ind.getURI().startsWith( ns ) ) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> getVivoTwoHopPredicates(){
        String core = "http://vivoweb.org/ontology/core#";
        String acti ="http://vivoweb.org/ontology/activity-insight#";
        String bibo = "http://purl.org/ontology/bibo/";
        
        String[] twoHopPredicates = {
            core+"hasRole",
            acti+"hasPartnerRole",
            core+"hasClinicalRole",
            core+"hasPresenterRole",
            core+"hasServiceProviderRole",
            core+"hasLeaderRole",
            core+"hasMemberRole",
            core+"hasResearcherRole",
            core+"hasInvestigatorRole",
            core+"hasCo-PrincipalInvestigatorRole",
            core+"hasPrincipalInvestigatorRole",
            core+"hasOutreachProviderRole",
            core+"hasTeacherRole",
            
            core+"educationalTraining",
            core+"organizationForTraining",
            
            core+"authorInAuthorship",
            core+"informationResourceInAuthorship",
            
            bibo+"editor",
            core+"editorOf"
        };
        return new HashSet<String>( Arrays.asList(twoHopPredicates) );
    }

    
    public static Set<String> getDefaultSkippedResourceNS(){
        String[] skip = {
            //skip things in this namespace since they are not
            //served by cornell's vivo site.
            "http://vivo.cornell.edu/resource/",
            //skip things in this namespace since they are
            //not served as linked data.
            "http://vivoweb.org/ontology/"
        };
        return new HashSet<String>( Arrays.asList(skip) );
    }    

    public static Set<String> getDefaultSkippedPredicates(){
        String[] skip = {
            RDF.type.getURI()
        };
        return new HashSet<String>( Arrays.asList(skip) );
    }

}
