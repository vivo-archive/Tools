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

package edu.cornell.mannlib.vitro.webapp.search.docbuilder;

import edu.cornell.mannlib.vitro.webapp.search.IndexingException;

/**
 * The Obj2Doc is an object that can translate something into
 * an object that the indexer can use.
 *
 * @author bdc34
 *
 */
public interface Obj2DocIface {
    /**
     * Tests to see if this Obj2Doc an translate this object.
     * @param obj
     * @return
     */
    public boolean canTranslate(Object obj);

    /**
     * Returns an object that the indexer can use.
     * @param obj
     * @return
     * @throws IndexingException 
     */
    public Object translate(Object obj) throws IndexingException;

    /**
     * Returns a vitro object from a search result/hit.
     *
     */
    public Object unTranslate(Object result);

    /**
     * Test to see if this can untranslate a search result/hit.
     */
    public boolean canUnTranslate(Object result);

    /**
     * Gets the id used by the index for this obj.
     * @param obj
     * @return
     */
    public Object getIndexId(Object obj);
}
