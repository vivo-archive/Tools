package edu.cornell.mannlib.vitro.webapp.search.beans;

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

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.search.SearchException;

import edu.cornell.mannlib.vitro.webapp.search.beans.VitroHighlighter;
import edu.cornell.mannlib.vitro.webapp.search.beans.VitroQuery;
import edu.cornell.mannlib.vitro.webapp.search.beans.VitroQueryFactory;

/**
 * This is used by the SearchController.  Any search back end should
 * implement this.
 *
 * Currently we use LuceneSearcher. It gets setup by LuceneSetup which
 * is specified to run as a listener in the application's web.xml.
 *
 * @author bdc34
 *
 */
public interface Searcher {

    public VitroQueryFactory getQueryFactory();

    /**
     * return a list of object that are related to the query.
     * The objects should be of type Entity or Tab, if not they
     * will be ignored.
     * @param query
     * @return
     * @throws SearchException
     */
    public List search( VitroQuery query ) throws SearchException;

    /**
     * The searcher may need to be used when making a highlighter.
     * In Lucene the highlighter needs access to the index.
     * @param q
     * @return
     */
    public abstract VitroHighlighter getHighlighter(VitroQuery q);

    /**
     * Used to close the searcher if the index that it was using gets
     * deleted.
     */
    public void close();
}
