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

package edu.cornell.mannlib.vitro.webapp.search.indexing;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.search.IndexingException;
import edu.cornell.mannlib.vitro.webapp.search.docbuilder.Obj2DocIface;

/**
 * IntexerIface is for objects that will be used by the IndexBuilder.  The
 * IndexBuilder will manage getting lists of object to index and then use
 * an object that implements IndexerIface to stuff the backend index.
 *
 * An example is LuceneIndexer which is set up and associated with a
 * IndexBuilder in LuceneSetup.
 *
 * @author bdc34
 *
 */
public interface IndexerIface {

    public void addObj2Doc(Obj2DocIface o2d);
    public List getObj2DocList();

    /**
     * Check if indexing is currently running in a different thread.
     * @return
     */
    public boolean isIndexing();

    /**
     * Index a document.  This should do an update of the
     * document in the index of the semantics of the index require it.
     *
     * @param doc
     * @param newDoc - if true, just insert doc, if false attempt to update.
     * @throws IndexingException
     */
    public void index(Object doc, boolean newDoc)throws IndexingException;


    /**
     * Remove a document from the index.
     * @param obj
     * @throws IndexingException
     */
    public void removeFromIndex(Object obj ) throws IndexingException;

    /**
     * Removes all documents from the index.
     * @throws IndexingException
     */
    public void clearIndex()throws IndexingException;

    public void startIndexing() throws IndexingException;
    public void endIndexing();

    public long getModified();
}
