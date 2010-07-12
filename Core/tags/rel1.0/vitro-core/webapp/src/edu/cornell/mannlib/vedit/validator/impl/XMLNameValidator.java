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

package edu.cornell.mannlib.vedit.validator.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cornell.mannlib.vedit.validator.Validator;
import edu.cornell.mannlib.vedit.validator.ValidationObject;

public class XMLNameValidator implements Validator {

    private final static String ERR_MSG = "Must start with a letter or '_' and use only letters, digits, '.', '-' or '_'. No spaces allowed.";

    Pattern pat = null;
    boolean permitEmpty = false;

    public XMLNameValidator() {
        pat = Pattern.compile("[A-Za-z_][A-Za-z0-9_\\-\\.]*");
    }

    public XMLNameValidator(boolean permitEmpty) {
	this();
	this.permitEmpty = permitEmpty;
    }

    public ValidationObject validate (Object obj) throws IllegalArgumentException {
        ValidationObject vo = new ValidationObject();
        String theString = null;

        try {
            theString = (String) obj;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Expected instance of String");
        }

	if (permitEmpty && (theString == null || "".equals(theString))) {
	    vo.setValid(true);
	} else {
            Matcher mat = pat.matcher(theString);
            if (mat.matches()){
                vo.setValid(true);
            } else {
                vo.setValid(false);
                vo.setMessage(ERR_MSG);
            }
        }

        vo.setValidatedObject(obj);
        return vo;
    }

}
