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

import edu.cornell.mannlib.vedit.validator.*;
import java.util.HashSet;
import java.util.Iterator;

public class EnumValuesValidator implements Validator {

    private HashSet legalValues = new HashSet();

    public ValidationObject validate(Object obj){
        ValidationObject vo = new ValidationObject();
        if (legalValues.contains((String)obj)){
            vo.setValid(true);
        } else {
            vo.setValid(false);
            if (legalValues.size()<7){
                String msgString = "Please enter one of ";
                Iterator valuesIt = legalValues.iterator();
                while (valuesIt.hasNext()) {
                    String legalValue = (String) valuesIt.next();
                    msgString += "'"+legalValue+"'";
                    if (valuesIt.hasNext())
                        msgString += ", ";
                    else
                        msgString += ".";
                }
                vo.setMessage(msgString);
            }
            else {
                vo.setMessage("Please enter a legal value.");
            }
        }
        vo.setValidatedObject(obj);
        return vo;
    }

    public EnumValuesValidator (String[] legalValues){
        for (int i=0; i<legalValues.length; i++)
            this.legalValues.add(legalValues[i]);
    }
}
