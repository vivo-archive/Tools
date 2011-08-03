package edu.cornell.mannlib.vitro.webapp.search.solr;

import org.apache.solr.common.SolrInputDocument;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;

public class AddParameters extends CalculateParameters implements
		DocumentModifier {
	@Override
	public void modifyDocument(Individual individual, SolrInputDocument doc, StringBuffer addUri) {
		 // calculate beta value.  
        float beta = calculateBeta(individual.getURI());
        doc.addField(MultiSiteIndToDoc.multiSiteTerm.BETA, beta);
        doc.setDocumentBoost(beta);   
	}
}
