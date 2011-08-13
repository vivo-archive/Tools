package edu.cornell.mannlib.vitro.webapp.search.solr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.search.IndexingException;
import edu.cornell.mannlib.vitro.webapp.search.beans.IndividualProhibitedFromSearch;
import edu.cornell.mannlib.vitro.webapp.search.beans.ProhibitedFromSearch;

public class MultiSiteIndToDoc extends IndividualToSolrDocument {
    
    private static final CleanAllText cleaner = new CleanAllText();
    
    public MultiSiteIndToDoc( ProhibitedFromSearch pfs, IndividualProhibitedFromSearch ipfs, Set<String>coreClassURIs, Map<String,String>classGroupURIToLabel, List<DocumentModifier> dms ) {
        super(pfs,ipfs,dms);                
        
        List<DocumentModifier> tmp = new ArrayList<DocumentModifier>(dms);
        tmp.add( new AddAllText() );
        tmp.add( new AddName() );
        tmp.add( new AddObjectPropertyText());
        tmp.add( new AddClassesForMultiSite(pfs,classGroupURIToLabel, coreClassURIs ));
        //tmp.add(new AddParameters());
        //tmp.add(new AddNameBoost());
        this.documentModifiers = tmp;
    }    
    
    /*
     * Currently the schemal.xml for the multi site index has these fields:

<field name="siteName" type="string" indexed="true" stored="true" required="true"/>
<field name="siteURL" type="string" indexed="true" stored="true" required="true"/>
<field name="alltext" type="vivo" indexed="true" stored="true"/>
<field name="alltextStemmed" type="vivo-stemmed" indexed="true" stored="false" omitTermFreqAndPositions="true"/>
<field name="name" type="vivo" indexed="true" stored="true"/>
<field name="nameStemmed" type="vivo-stemmed" indexed="true" stored="false" omitTermFreqAndPositions="true"/>
<field name="moniker" type="string" indexed="true" stored="true"/>
<field name="URI" type="string" indexed="true" stored="true" required="true"/>
<field name="type" type="string" indexed="true" stored="true" multiValued="true"/>
<field name="type_label" type="string" indexed="true" stored="true" multiValued="true"/>
<field name="classgroup" type="string" indexed="true" stored="true" multiValued="true"/>

     * copy fields have been omitted from this list.
     */
    

    public SolrInputDocument translate(Individual ind) throws IndexingException{
        try{                                            
            log.debug("translating " + ind.getURI());
            checkForSkipBasedOnNS( ind );
                                    
            SolrInputDocument doc = new SolrInputDocument();                       
            doc.addField(term.URI, ind.getURI());                                                                             
               
            StringBuffer addUri = new StringBuffer();                        
            runAdditionalDocModifers(ind,doc,addUri);
            
            cleaner.modifyDocument(ind,doc,addUri);
            return doc;
        }catch(SkipIndividualException ex){
            //indicates that this individual should not be indexed by returning null
            log.debug(ex);
            return null;
        }catch(Throwable th){
            System.out.println(th);
            th.printStackTrace();
            //Odd exceptions from jena get thrown on shutdown
            if( log != null )
                log.debug(th);
            return null;
        }
    }
       
    @Override
    protected void runAdditionalDocModifers( Individual ind, SolrInputDocument doc, StringBuffer addUri ) 
    throws SkipIndividualException{        
        if( documentModifiers != null && !documentModifiers.isEmpty()){            
            for(DocumentModifier modifier: documentModifiers){        
                modifier.modifyDocument(ind, doc, addUri);                                
            }
        }
    }
       
    public static class multiSiteTerm {
        public static final String URI = "URI";
        public static final String site_name = "site_name";
        public static final String site_URL = "site_URL";
        public static final String name = "name";
        public static final String title = "title";
        public static final String alltext = "alltext";
        public static final String thumbnail_URL = "thumbnail_URL";
        public static final String class_URI = "class";
        public static final String class_label = "class_label";
        public static final String core_class = "core_class";
        public static final String core_class_label = "core_class_label";
        public static final String most_specific_class = "most_specific_class";
        public static final String most_specific_class_label = "most_specific_class_label";
        public static final String classgroup = "classgroup";
        public static final String classgroup_label = "classgroup_label";                                            
    }
      
    
    /**
     * Attempt to append to a single value field
     */
    public static void addToField( SolrInputDocument doc, String fieldName, String value){
        SolrInputField f = doc.getField(fieldName);
        if( f == null )
            doc.addField( fieldName, value);
        else{
            Object existing = f.getValue();
            if( existing instanceof String)
                f.setValue(value + (String)existing, f.getBoost());
            else
                throw new IllegalArgumentException(" addToField only handles fields with String values");            
        }                
    }
    
}
