/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.solr;

import org.apache.solr.common.SolrInputDocument;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;

/**
 *  Adds a field for storing the location of thumbnail image for the individual. 
 */
public class AddThumbnail implements DocumentModifier {     
  
  @Override
  public void modifyDocument(Individual individual, SolrInputDocument doc,
          StringBuffer addUri) throws SkipIndividualException {

      String url = individual.getThumbUrl();
      if( url != null && !"".equals(url)){
          doc.addField(VitroSearchTermNames.THUMBNAIL_URL, url);
      }            
  }
  
  @Override
  public void shutdown() {
        // TODO Auto-generated method stub
  }

}
