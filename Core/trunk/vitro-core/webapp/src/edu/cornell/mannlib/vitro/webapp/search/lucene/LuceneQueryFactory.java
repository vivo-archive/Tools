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

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.analysis.Analyzer;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.flags.PortalFlag;
import edu.cornell.mannlib.vitro.webapp.search.SearchException;
import edu.cornell.mannlib.vitro.webapp.search.beans.VitroQuery;
import edu.cornell.mannlib.vitro.webapp.search.beans.VitroQueryFactory;

public class LuceneQueryFactory implements VitroQueryFactory {

    public static final int MAX_QUERY_LENGTH = 500;
    private String indexDir;

    public LuceneQueryFactory(Analyzer analyzer, String indexDir ){
        this.analyzer = analyzer;
        this.indexDir = indexDir;
    }

    private Analyzer analyzer = null;

    public VitroQuery getQuery(VitroRequest request, PortalFlag portalState) throws SearchException {
        //there should be a better way to integrate this with LuceneQuery
        //here we check that the request has the parameters that we need to
        //make the query.  If it does not then we return null.    	
        String txt = request.getParameter(VitroQuery.QUERY_PARAMETER_NAME);
        if( txt == null || txt.length() == 0 )
            return null;
        if( txt.length() > MAX_QUERY_LENGTH )
            throw new SearchException("The search was too long. The maximum " +
            		"query length is " + MAX_QUERY_LENGTH );
        LuceneQuery query = new LuceneQuery(request, portalState, analyzer, indexDir);
        return query;
    }

    public Analyzer getAnalyzer(){
        return analyzer;
    }
}
