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

package edu.cornell.mannlib.vitro.webapp.search.lucene;

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

public class LuceneHighlighter extends VitroHighlighter{
    /* See VitroHighlighter for prefix tag and postfix tag */

    Highlighter nonFragHighlighter = null;
    Highlighter fragHighlighter = null;

    Analyzer analyzer = null;

    /**
     * Makes a VitroHighlighter that uses lucene highlighters.
     * PreTag and PostTag are from VitroHighlighter.
     *
     * @param query - the query to highlight for.
     * @param a - the Analyzer that was used in the query.
     */
    public LuceneHighlighter(Query query, Analyzer a){
        QueryScorer scorer = new QueryScorer( query );
        /* See VitroHighlighter for prefix tag and postfix tag */
        Formatter formatter =
            new SimpleHTMLFormatter(preTag,postTag);
        this.analyzer = a;
        this.fragHighlighter = new Highlighter(formatter, scorer);

        //here we make a highlighter that doesn't fragment
        this.nonFragHighlighter = new Highlighter( formatter, scorer);
        this.nonFragHighlighter.setTextFragmenter(new NullFragmenter());
    }

    
    private Pattern htmlOrNot = Pattern.compile("(<[^>]*>)|([^<]*)");
    private int HTML_PATTERN_INDEX = 1;
    private int TEXT_PATTERN_INDEX = 2;
    /**
     * Highlights in a string. No Fragmenting. Attempts to avoid some HTML.
     * @param in
     * @return
     */
    public String highlight( String in){
        Matcher matcher =  htmlOrNot.matcher(in);
        StringBuilder output = new StringBuilder();
        
        boolean found = matcher.find();
        if( ! found )
            return in;
        
        while( found ){
            String foundHtmlElement = matcher.group( HTML_PATTERN_INDEX );
            if( foundHtmlElement != null ){
                output.append( foundHtmlElement );
            }else{
                String foundTextNode = matcher.group( TEXT_PATTERN_INDEX );
                String hi = foundTextNode;
                try {
                    hi = nonFragHighlighter.getBestFragment(analyzer,"contents",foundTextNode);                    
                } catch (IOException e) {
                    return in;
                }
                if( hi != null )
                    output.append( hi );
                else
                    output.append( foundTextNode );
            }
            found = matcher.find();
        }
        return output.toString();        
    }
    
    
            
    
    
    protected boolean WITH_ELLIPSIS = true;
    protected String ellipsis = "...";
    public String getHighlightFragments(String in ) {

        if(WITH_ELLIPSIS ){
            if( in != null && in.trim().length() > 0){
                String b = doHighlight( in ,fragHighlighter);
                if( b != null && b.trim().length() > 0 )
                    return ellipsis + " " + b + " " + ellipsis;
                else
                    return "";
            } else {
                return "";
            }
        } else {
            return doHighlight(  in , fragHighlighter);
        }
    }

    private String doHighlight(String in, Highlighter hi ) {
        String result = in;

        if(in != null ){


            TokenStream tokenStream =
                analyzer.tokenStream("contents", new StringReader(in));
            //       Get 3 best fragments and seperate with a "..."
            try {
                result = hi.getBestFragments(tokenStream, in , 3, "...");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }
    
    private final int maxDocCharsToAnalyze = 4000;
    Log log = LogFactory.getLog(LuceneHighlighter.class);
}
