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

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import edu.cornell.mannlib.vitro.webapp.search.beans.VitroHighlighter;
import edu.cornell.mannlib.vitro.webapp.utils.Html2Text;

/**
 * This is a highlighter and fragmenter for use with PagedSearchController. 
 */
public class SimpleLuceneHighlighter extends VitroHighlighter{    
    Highlighter fragHighlighter = null;
    Analyzer analyzer = null;
    
    public SimpleLuceneHighlighter(Query query, Analyzer a){
        QueryScorer scorer = new QueryScorer( query ,Entity2LuceneDoc.term.ALLTEXT);

        Formatter formatter =
            new SimpleHTMLFormatter(preTag,postTag);
        this.analyzer = a;
        this.fragHighlighter = new Highlighter(formatter, scorer);
    }
   
    @Override
    public String highlight( String in){
        //not really implemented.
        return in;
    }
    
    @Override
    public String getHighlightFragments(String in ) {
        Html2Text h2t = new Html2Text();
        try{
            h2t.parse(in);
        }catch(IOException ioe){
            log.debug("could not strip html from string",ioe);
        }
        String txt = h2t.getText();

        if( txt != null && txt.trim().length() > 0){
            String b = doHighlight( txt ,fragHighlighter);
            if( b != null && b.trim().length() > 0 )
                return "..." + " " + b + " " + "...";
            else
                return "";
        } else {
            return "";
        }
    }

    private String doHighlight(String in, Highlighter hi ) {
        String result = in;
        if(in != null ){
            TokenStream tokenStream =
                analyzer.tokenStream(Entity2LuceneDoc.term.ALLTEXT, new StringReader(in));
            try {
                //Get 3 best fragments and seperate with a "..."
                result = hi.getBestFragments(tokenStream, in , 3, "...");
            } catch (IOException e) {
                log.debug("could not highlight",e);
            }
        }
        return result;
    }
    
    private static Log log = LogFactory.getLog(SimpleLuceneHighlighter.class);
}
