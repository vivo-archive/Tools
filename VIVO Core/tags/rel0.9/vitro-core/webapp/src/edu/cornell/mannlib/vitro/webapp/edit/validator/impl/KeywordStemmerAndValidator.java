package edu.cornell.mannlib.vitro.webapp.edit.validator.impl;

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

import java.util.Iterator;
import java.util.List;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.validator.ValidationObject;
import edu.cornell.mannlib.vedit.validator.Validator;
import edu.cornell.mannlib.vitro.webapp.beans.Keyword;
import edu.cornell.mannlib.vitro.webapp.dao.KeywordDao;
import edu.cornell.mannlib.vitro.webapp.edit.listener.impl.KeywordPreProcessor;
import edu.cornell.mannlib.vitro.webapp.utils.Stemmer;

public class KeywordStemmerAndValidator implements Validator {

    private int MAX_LENGTH = 32;

    private EditProcessObject epo = null;
    private KeywordDao kDao = null;

    public KeywordStemmerAndValidator(EditProcessObject in, KeywordDao keywordDao) {
        this.epo = in;
        this.kDao = keywordDao;
    }

    public ValidationObject validate (Object obj) throws IllegalArgumentException {

        ValidationObject vo = new ValidationObject();

        if (obj==null || (obj instanceof String && ((String)obj).length()>0)) {
            String keywordString = (String) obj;
            String stemmedString = Stemmer.StemString(keywordString, MAX_LENGTH);
            epo.getPreProcessorList().add(new KeywordPreProcessor(stemmedString));
            List<Keyword> matches = kDao.getKeywordsByStem(stemmedString);
            if (matches.size() > 0) {
                vo.setValid(false);
                String suggestionsStr = new String();
                Iterator<Keyword> matchesIt = matches.iterator();
                boolean first = true;
                while (matchesIt.hasNext()) {
                    Keyword k = matchesIt.next();
                    if (!matchesIt.hasNext() && !first) {
                        suggestionsStr += "or ";
                    }
                    suggestionsStr += "'"+k.getTerm();
                    if (matchesIt.hasNext()) {
                        suggestionsStr += ",' ";
                    } else {
                        suggestionsStr += "'";
                    }
                    first = false;
                }
                vo.setMessage("Keyword identified as redundant.  Please modify term or consider canceling and instead linking individual to existing keyword "+suggestionsStr);
            } else {
                vo.setValid(true);
            }
        } else {
            vo.setValid(false);
            vo.setMessage("Please enter a keyword.");
        }
        vo.setValidatedObject(obj);

        return vo;

    }

}