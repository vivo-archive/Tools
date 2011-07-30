package edu.cornell.mannlib.vitro.webapp.search.solr;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.search.IndexingException;
import edu.cornell.mannlib.vitro.webapp.search.beans.IndividualProhibitedFromSearch;
import edu.cornell.mannlib.vitro.webapp.search.beans.ProhibitedFromSearch;

public class MultiSiteIndToDoc extends IndividualToSolrDocument {
    
    public MultiSiteIndToDoc( ProhibitedFromSearch pfs, IndividualProhibitedFromSearch ipfs, List<DocumentModifier> dms ) {
        super(pfs,ipfs,dms);
        
        List<DocumentModifier> tmp = new ArrayList<DocumentModifier>(dms);
        tmp.add( new AddAllText() );
        tmp.add( new AddLabel() );
        tmp.add( new AddObjectPropertyText());
        tmp.add( new AddClassesForMultiSite(pfs));
        tmp.add( new AddThumbnail() );
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
        public static String type_label="type_label";        
        public static String name="name";
        public static String alltext="alltext";
        public static String alltextStemmed="alltextStemmed";
        public static String nameStemmed = "nameStemmed";                
    }
        
    
}
