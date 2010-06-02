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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Literal;

public class PersonHasPositionValidator implements N3Validator {
    
    private static String DUPLICATE_ERROR = "Must choose an existing organization or create a new one, not both.";
    private static String MISSING_ORG_ERROR = "Must either choose an existing organization or create a new one.";
    private static String MISSING_ORG_TYPE_ERROR = "Must select a type for the new organization.";
    private static String MISSING_ORG_NAME_ERROR = "Must select a name for the new organization.";
	 
	public Map<String,String> validate(EditConfiguration editConfig, EditSubmission editSub){
//		Map<String,String> existingUris = editConfig.getUrisInScope();
//		Map<String,Literal> existingLiterals = editConfig.getLiteralsInScope();					 
		Map<String,String> urisFromForm = editSub.getUrisFromForm();
		Map<String,Literal> literalsFromForm = editSub.getLiteralsFromForm();
		
		Literal newOrgName = literalsFromForm.get("newOrgName");
		if( newOrgName.getLexicalForm() != null && "".equals(newOrgName.getLexicalForm()) )
			newOrgName = null;
		String newOrgType = urisFromForm.get("newOrgType");
		if( "".equals(newOrgType ) )
			newOrgType = null;
		String organizationUri = urisFromForm.get("organizationUri");
		if( "".equals(organizationUri))
			organizationUri = null;
		
//		System.out.println("newOrgName " + newOrgName);
//		System.out.println("newOrgType " + newOrgType);
//		System.out.println("organizationUri " + organizationUri);
		
		Map<String,String> errors = new HashMap<String,String>();		
		if( organizationUri != null && (newOrgName != null || newOrgType != null)  ){
			errors.put("newOrgName", DUPLICATE_ERROR);	
			errors.put("organizationUri", DUPLICATE_ERROR);
		} else if ( organizationUri == null && newOrgName == null && newOrgType == null) {
            errors.put("newOrgName", MISSING_ORG_ERROR);   
            errors.put("organizationUri", MISSING_ORG_ERROR);		    		    
		}else if( organizationUri == null && newOrgName != null && newOrgType == null) {
			errors.put("newOrgType", MISSING_ORG_TYPE_ERROR);			
		}else if( organizationUri == null && newOrgName == null && newOrgType != null) {
			errors.put("newOrgName", MISSING_ORG_NAME_ERROR);			
		}
		
		if( errors.size() != 0 )
			return errors;
		else 
			return null;
   }
}