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

public class StartYearBeforeEndYear implements N3Validator {
	private String startFieldName;
	private String endFieldName;
	
	public StartYearBeforeEndYear(String startFieldName, String endFieldName){
		this.startFieldName = startFieldName;
		this.endFieldName = endFieldName;
	}
	public Map<String, String> validate(EditConfiguration editConfig,
			EditSubmission editSub) {
		Map<String, Literal> existingLiterals = editConfig.getLiteralsInScope();
		Literal existingStartYear = existingLiterals.get(startFieldName);
		Literal existingEndYear = existingLiterals.get(endFieldName);

		Map<String, Literal> literalsFromForm = editSub.getLiteralsFromForm();
		Literal formStartYear = literalsFromForm.get(startFieldName);
		Literal formEndYear = literalsFromForm.get(endFieldName);

		Map<String, String> errors = new HashMap<String, String>();

		if (formStartYear != null && formEndYear != null) {
			errors.putAll(checkDateLiterals(formStartYear, formEndYear));
		} else if (formStartYear != null && existingEndYear != null) {
			errors.putAll(checkDateLiterals(formStartYear, existingEndYear));
		} else if (existingStartYear != null && formEndYear != null) {
			errors.putAll(checkDateLiterals(existingStartYear, formEndYear));
		} else if (existingStartYear != null && existingEndYear != null) {
			errors
					.putAll(checkDateLiterals(existingStartYear,
							existingEndYear));
		}

		if (errors.size() != 0)
			return errors;
		else
			return null;
	}

	private Map<String, String> checkDateLiterals(Literal startLit,
			Literal endLit) {
		Map<String, String> errors = new HashMap<String, String>();
		try {
			int start = Integer.parseInt(startLit.getLexicalForm());
			int end = Integer.parseInt(endLit.getLexicalForm());
			if (end < start) {
				errors.put(startFieldName, "Start year must be before end year");
				errors.put(endFieldName, "End year must be after start year");
			}
		} catch (NumberFormatException nfe) {

		}
		return errors;
	}

}
