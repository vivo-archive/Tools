/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.solr;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.solr.common.SolrInputDocument;

import com.hp.hpl.jena.vocabulary.OWL;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.search.beans.ProhibitedFromSearch;
import edu.cornell.mannlib.vitro.webapp.search.solr.MultiSiteIndToDoc.multiSiteTerm;

public class AddClassesForMultiSite implements DocumentModifier {

    private ProhibitedFromSearch classesProhibitedFromSearch;
    private Map<String, String> classgroupURIToLabel;
    private Set<String> coreClassURIs;
    private Set<String> classesToSkipForDisplay;

    public AddClassesForMultiSite( 
            ProhibitedFromSearch cps, 
            Map<String,String>classgroupURIToLabel,
            Set<String> coreClassURIs ,
            Set<String> classesToSkipForDisplay ){
        
        this.classesProhibitedFromSearch = cps;
        
        if( classgroupURIToLabel != null)
            this.classgroupURIToLabel = classgroupURIToLabel;
        else
            this.classgroupURIToLabel = Collections.emptyMap();
        
        if( coreClassURIs != null )
            this.coreClassURIs = coreClassURIs;
        else
            this.coreClassURIs = Collections.emptySet();
        
        if( classesToSkipForDisplay != null)
            this.classesToSkipForDisplay = classesToSkipForDisplay;
        else
            this.classesToSkipForDisplay = Collections.emptySet();
    }
    
    @Override
    public void modifyDocument(Individual ind, SolrInputDocument doc,
            StringBuffer arg2) throws SkipIndividualException {
        Set<String> classGroupUris = new TreeSet<String>();
        
        List<VClass> vclasses = ind.getVClasses(false);               
        for(VClass clz : vclasses){
            
            if(clz.getURI() == null){
                continue;
            
            }else if(OWL.Thing.getURI().equals(clz.getURI())){
                //index individuals of type owl:Thing, just don't add owl:Thing as the type field in the index
                continue;
            
            } else if(clz.getURI().startsWith(OWL.NS)){
                throw new SkipIndividualException("not indexing " + ind.getURI() + " because of type " + clz.getURI() );    
            
            } else if(classesProhibitedFromSearch.isClassProhibitedFromSearch(clz.getURI())){
                // do not index individuals of type Role, AdvisingRelationShip, Authorship, etc.(see search.n3 for more information)
                 throw new SkipIndividualException("not indexing " + ind.getURI() + " because of prohibited type " + clz.getURI() );

            }else if( classesToSkipForDisplay.contains( clz.getURI() )){
                //skipped classes still contribute their classgroup
                if( clz.getGroupURI() != null ){
                    classGroupUris.add( clz.getGroupURI());                    
                }
                     
            } else {
                boolean isCore = coreClassURIs.contains(clz.getURI());
                
                //Add class URI
                doc.addField(multiSiteTerm.class_URI, clz.getURI());
                
                //Add class public name
                if(clz.getName() != null && !"".equals(clz.getName()) ){                    
                    doc.addField(multiSiteTerm.class_label , clz.getName() );                   
                }
                
                if( isCore ){
                    doc.addField(multiSiteTerm.core_class_label,clz.getName());
                    doc.addField(multiSiteTerm.core_class,clz.getURI());                    
                }
                
                //Add ClassGroup URI 
                if(clz.getGroupURI() != null){
                    classGroupUris.add( clz.getGroupURI() );                    
                }               
            }        
        }
        
        for(String cgUri: classGroupUris){            
            doc.addField(multiSiteTerm.classgroup, cgUri);
            if( classgroupURIToLabel.containsKey(cgUri))
                doc.addField(multiSiteTerm.classgroup_label,classgroupURIToLabel.get(cgUri) );
        }
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }

}
