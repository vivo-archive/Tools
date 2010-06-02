package edu.cornell.mannlib.vitro.webapp.dao.jena.pellet;

/*
Copyright (c) 2010, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import org.mindswap.pellet.jena.PelletReasonerFactory;

public class ReasonerConfiguration {
	
	public Set<ObjectPropertyStatementPattern> inferenceDrivingPatternAllowSet;
	public Set<ObjectPropertyStatementPattern> inferenceDrivingPatternDenySet;
	public Set<ObjectPropertyStatementPattern> inferenceReceivingPatternAllowSet;
	
	private boolean queryForAllObjectProperties = false;
	private boolean incrementalReasoningEnabled = true;
	
	// These are some hamfisted stopgap measures until I add better support for dataproperty reasoning
	private boolean reasonOnAllDatatypePropertyStatements = false;
	private boolean queryForAllDatatypeProperties = false;
	
	private OntModelSpec ontModelSpec = PelletReasonerFactory.THE_SPEC;

	/**
	 * The default reasoner configuration is designed to provide acceptable performance on larger knowledge bases.
	 * It will classify and realize, and add inferred disjointWith statements.
	 * It ignores domain and range "axioms," on the assumption that they are not truly axioms but editing constraints.
	 * It also ignores "owl:inverseOf."  
	 */
	public static ReasonerConfiguration DEFAULT;
	
	/**
	 * This configuration will ask Pellet for "all" inferences (calls listStatements(null,null,null)).
	 * Usually suitable only for smaller ontologies.
	 */
	public static ReasonerConfiguration COMPLETE;
	
	/**
	 * This configuration will ask Pellet for the default inferences, plus all statements where the predicate
	 * is a property in the user's ontology(ies).
	 * Can lead to drastic performance improvements, depending on the ontology.
	 */
	public static ReasonerConfiguration PSEUDOCOMPLETE;
	
	/**
	 * This configuration will ask Pellet for the default inferences, plus all statements where the predicate
	 * is an object property in the user's ontology(ies).
	 */
	public static ReasonerConfiguration PSEUDOCOMPLETE_IGNORE_DATAPROPERTIES;
		
	static {	
	
		
		//ask the reasoner only to classify, realize, and infer disjointWith statements (based on a somewhat incomplete information)
		DEFAULT = new ReasonerConfiguration();
		HashSet<ObjectPropertyStatementPattern> defaultInferenceDrivingPatternAllowSet = new HashSet<ObjectPropertyStatementPattern>();
		defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,RDF.type,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,RDFS.subClassOf,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,RDFS.subPropertyOf,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.equivalentClass,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.unionOf,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.intersectionOf,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.complementOf,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.onProperty,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.someValuesFrom,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.allValuesFrom,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.hasValue,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.minCardinality,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.maxCardinality,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.cardinality,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,RDF.first,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,RDF.rest,null));
        defaultInferenceDrivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.disjointWith,null));
        DEFAULT.setInferenceDrivingPatternAllowSet(defaultInferenceDrivingPatternAllowSet);
        Set<ObjectPropertyStatementPattern> defaultInferenceReceivingPatternAllowSet = new HashSet<ObjectPropertyStatementPattern>();
        defaultInferenceReceivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,RDF.type,null));
        defaultInferenceReceivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,RDFS.subClassOf,null));
        defaultInferenceReceivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.equivalentClass,null));
        defaultInferenceReceivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.disjointWith,null));
        defaultInferenceReceivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null,OWL.inverseOf,null));
        DEFAULT.setInferenceReceivingPatternAllowSet(defaultInferenceReceivingPatternAllowSet);
        DEFAULT.setQueryForAllObjectProperties(false);

		PSEUDOCOMPLETE = new ReasonerConfiguration();
		PSEUDOCOMPLETE.setQueryForAllObjectProperties(true); 
		PSEUDOCOMPLETE.setReasonOnAllDatatypePropertyStatements(true);
		PSEUDOCOMPLETE.setQueryForAllDatatypeProperties(true);
        PSEUDOCOMPLETE.setInferenceReceivingPatternAllowSet(defaultInferenceReceivingPatternAllowSet);

		PSEUDOCOMPLETE_IGNORE_DATAPROPERTIES = new ReasonerConfiguration();
		PSEUDOCOMPLETE_IGNORE_DATAPROPERTIES.setQueryForAllObjectProperties(true); 
		PSEUDOCOMPLETE_IGNORE_DATAPROPERTIES.setReasonOnAllDatatypePropertyStatements(false);
		PSEUDOCOMPLETE_IGNORE_DATAPROPERTIES.setQueryForAllDatatypeProperties(false);
        PSEUDOCOMPLETE_IGNORE_DATAPROPERTIES.setInferenceReceivingPatternAllowSet(defaultInferenceReceivingPatternAllowSet);
        
		// ask the reasoner for "all" inferred statements
		// change from earlier version because Pellet seemed to stop including sameAs/differentFrom with listStatements()
		COMPLETE = new ReasonerConfiguration();
		COMPLETE.setQueryForAllObjectProperties(true); 
		COMPLETE.setReasonOnAllDatatypePropertyStatements(true); 
		COMPLETE.setQueryForAllDatatypeProperties(true); 
		Set<ObjectPropertyStatementPattern> completeInferenceReceivingPatternAllowSet = new HashSet<ObjectPropertyStatementPattern>(); 
		completeInferenceReceivingPatternAllowSet.addAll(defaultInferenceReceivingPatternAllowSet);
		completeInferenceReceivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null, OWL.sameAs, null));
		// getting NPEs inside Pellet with differentFrom on 2.0.0-rc7 
        //completeInferenceReceivingPatternAllowSet.add(ObjectPropertyStatementPatternFactory.getPattern(null, OWL.differentFrom, null));
        COMPLETE.setInferenceReceivingPatternAllowSet(completeInferenceReceivingPatternAllowSet);
        
	}
	
	public Set<ObjectPropertyStatementPattern> getInferenceDrivingPatternAllowSet() {
		return this.inferenceDrivingPatternAllowSet;
	}
	
	public void setInferenceDrivingPatternAllowSet(Set<ObjectPropertyStatementPattern> patternSet) {
		this.inferenceDrivingPatternAllowSet = patternSet;
	}
	
	public Set<ObjectPropertyStatementPattern> getInferenceDrivingPatternDenySet() {
		return this.inferenceDrivingPatternDenySet;
	}
	
	public void setInferenceDrivingPatternDenySet(Set<ObjectPropertyStatementPattern> patternSet) {
		this.inferenceDrivingPatternDenySet = patternSet;
	}
	
	public Set<ObjectPropertyStatementPattern> getInferenceReceivingPatternAllowSet() {
		return this.inferenceReceivingPatternAllowSet;
	}
	
	public void setInferenceReceivingPatternAllowSet(Set<ObjectPropertyStatementPattern> patternSet) {
		this.inferenceReceivingPatternAllowSet = patternSet;
	}
	
	public boolean getQueryForAllObjectProperties() {
		return queryForAllObjectProperties;
	}

	public void setQueryForAllObjectProperties(boolean boole) {
		this.queryForAllObjectProperties = boole;
	}
	
	public boolean getReasonOnAllDatatypePropertyStatements() {
		return this.reasonOnAllDatatypePropertyStatements;
	}
	
	public void setReasonOnAllDatatypePropertyStatements(boolean boole) {
		this.reasonOnAllDatatypePropertyStatements = boole;
	}
	
	public boolean getQueryForAllDatatypeProperties() {
		return this.queryForAllDatatypeProperties;
	}
	
	public void setQueryForAllDatatypeProperties(boolean boole) {
		this.queryForAllDatatypeProperties = boole;
	}

	public OntModelSpec getOntModelSpec() {
		return this.ontModelSpec;
	}

	public void setOntModelSpec(OntModelSpec spec) {
		this.ontModelSpec = spec;
	}
	
	public boolean isIncrementalReasoningEnabled() {
		return this.incrementalReasoningEnabled;
	}
	
	public void setIncrementalReasongingEnabled(boolean value) {
		this.incrementalReasoningEnabled = value;
	}
	
}

