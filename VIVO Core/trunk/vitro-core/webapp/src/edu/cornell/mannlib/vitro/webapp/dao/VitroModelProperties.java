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

package edu.cornell.mannlib.vitro.webapp.dao;

import java.util.HashSet;
import java.util.Set;

public class VitroModelProperties {

	public static final int RDF_ABOX_ONLY = 50;
	public static final int RDFS = 100;
	public static final int OWL_FULL = 200;
	public static final int OWL_DL = 202;
	public static final int OWL_LITE = 204;
	
	private int languageProfile;
	private String[] preferredLanguages;
	private String defaultNamespace;
	private Set<String> nonUserNamespaces;
	
	public static boolean isOWL(int languageProfile) {
		return ((OWL_FULL <= languageProfile && OWL_LITE >= languageProfile));
	}
	
	public static boolean isRDFS(int languageProfile) {
		return (languageProfile==100);
	}
	
	public VitroModelProperties() {
		languageProfile = 200;
		preferredLanguages = new String[1];
		preferredLanguages[0] = null;
		defaultNamespace = "http://vitro.mannlib.cornell.edu/ns/default#";
		nonUserNamespaces = new HashSet<String>();
		nonUserNamespaces.add(VitroVocabulary.vitroURI);
	}
	
	public int getLanguageProfile() {
		return this.languageProfile;
	}
	
	public void setLanguageProfile(int languageProfile) {
		this.languageProfile = languageProfile;
	}
	
	public String[] getPreferredLanguages() {
		return this.preferredLanguages;
	}
	
	public void setPreferredLanguages(String[] pl) {
		this.preferredLanguages = pl;
	}
	
	public String getDefaultNamespace() {
		return defaultNamespace;
	}
	
	public void setDefaultNamespace(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}
	
	public Set<String> getNonUserNamespaces() {
		return this.nonUserNamespaces;
	}
	
	public void setNonUserNamespaces(Set<String> nonUserNamespaces) {
		this.nonUserNamespaces = nonUserNamespaces;
	}
	
}
