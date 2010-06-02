package edu.cornell.mannlib.vitro.webapp.search.lucene;

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

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.TokenStream;

public class VitroAnalyzer extends Analyzer {
    Analyzer keywordAnalyzer;
    Analyzer stemmingAnalyzer;
    Analyzer nonStemmingAnalyzer;
    
    public VitroAnalyzer(){
        keywordAnalyzer = new KeywordAnalyzer();
        stemmingAnalyzer = new HtmlLowerStopStemAnalyzer();
        nonStemmingAnalyzer = new HtmlLowerStopAnalyzer();
    }
    
    @Override
    public TokenStream tokenStream(String field, Reader reader) {
        if( Entity2LuceneDoc.term.ALLTEXT.equals(field) ||
            Entity2LuceneDoc.term.NAME.equals(field) )
            return stemmingAnalyzer.tokenStream(field, reader);
        else if( Entity2LuceneDoc.term.ALLTEXTUNSTEMMED.equals(field) )
            return nonStemmingAnalyzer.tokenStream(field, reader);
        else{
            return keywordAnalyzer.tokenStream(field, reader);
        }
    }

}