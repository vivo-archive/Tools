/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.solr;

import org.apache.solr.common.SolrInputDocument;

import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;

public class ThumbnailForMultiSite extends ThumbnailImageURL {

    
    public ThumbnailForMultiSite(Model model) {
        super(model);
    }

    @Override
    public void modifyDocument(Individual individual, SolrInputDocument doc,
            StringBuffer addUri) throws SkipIndividualException {
        
        //add a field for storing the location of thumbnail for the individual.
        doc.addField(
                MultiSiteIndToDoc.multiSiteTerm.thumbnail_URL, 
                runQueryForThumbnailLocation(individual));
    
    }
}
