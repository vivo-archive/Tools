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

package edu.cornell.mannlib.vitro.webapp.ontology.update;

import com.hp.hpl.jena.ontology.OntModel;
/**
 * 
 * We need to document what is in source and destinationURI for each
 * change type.
 *
 */
public class AtomicOntologyChange {

	private String sourceURI;	
	private String destinationURI;
	private AtomicChangeType atomicChangeType;

	public AtomicOntologyChange() {

    }

	public AtomicOntologyChange(String sourceURI,
	                            String destinationURI,
	                            AtomicChangeType atomicChangeType) {
		
		this.sourceURI = sourceURI;
		this.destinationURI = destinationURI;
		this.atomicChangeType = atomicChangeType;
    }

	
	/**
	 * Contains the URI of a class or property in the previous version of
	 * the ontology, or null if a new class or property was introduced
	 * in the current version of the ontology.
	 * @return
	 */
	public String getSourceURI() {
		return this.sourceURI;
	}
	
	public void setSourceURI(String sourceURI) {
		this.sourceURI = sourceURI;
	}
	
	/**
	 * Contains the URI of a class or property in the current version of 
	 * the ontology, or null if a class or property was removed from the
	 * previous version of the ontology.
	 * @return
	 */
	public String getDestinationURI() {
		return this.destinationURI;
	}
	
	public void setDestinationURI(String destinationURI) {
		this.destinationURI = destinationURI;
	}
	
	public AtomicChangeType getAtomicChangeType() {
		return atomicChangeType;
	}

	public void setAtomicChangeType(AtomicChangeType atomicChangeType) {
		this.atomicChangeType = atomicChangeType;
	}
	
	public enum AtomicChangeType {
		ADD, DELETE, RENAME
	}
	
}
