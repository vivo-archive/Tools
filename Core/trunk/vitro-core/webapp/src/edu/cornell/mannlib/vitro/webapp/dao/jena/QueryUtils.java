/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/** 
 * Utilities for executing queries and working with query results. 
 * 
 */

public class QueryUtils {

    protected static Map<String,Object> querySolutionToObjectValueMap( QuerySolution soln){
        Map<String,Object> map = new HashMap<String,Object>();
        Iterator<String> varNames = soln.varNames();
        while(varNames.hasNext()){
            String varName = varNames.next();
            map.put(varName, nodeToObject( soln.get(varName)));
        }
        return map;
    }
 
    protected static Map<String,String> querySolutionToStringValueMap( QuerySolution soln ){
        Map<String,String> map = new HashMap<String,String>();
        Iterator<String> varNames = soln.varNames();
        while(varNames.hasNext()){
            String varName = varNames.next();
            map.put(varName, nodeToString( soln.get(varName)));
        }
        return map;
    }
    
    protected static Object nodeToObject( RDFNode node ){
        if( node == null ){
            return "";
        }else if( node.isLiteral() ){
            Literal literal = node.asLiteral();
            return literal.getValue();
        }else if( node.isURIResource() ){
            Resource resource = node.asResource();
            return resource.getURI();
        }else if( node.isAnon() ){  
            Resource resource = node.asResource();
            return resource.getId().getLabelString(); //get b-node id
        }else{
            return "";
        }
    }

    protected static String nodeToString( RDFNode node ){
        if( node == null ){
            return "";
        }else if( node.isLiteral() ){
            Literal literal = node.asLiteral();
            return literal.getLexicalForm();
        }else if( node.isURIResource() ){
            Resource resource = node.asResource();
            return resource.getURI();
        }else if( node.isAnon() ){  
            Resource resource = node.asResource();
            return resource.getId().getLabelString(); //get b-node id
        }else{
            return "";
        }
    }
}