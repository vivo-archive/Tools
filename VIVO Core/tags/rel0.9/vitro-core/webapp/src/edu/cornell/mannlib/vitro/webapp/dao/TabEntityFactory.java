package edu.cornell.mannlib.vitro.webapp.dao;

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

import edu.cornell.mannlib.vitro.webapp.beans.Individual;

import java.util.List;
/**
 * This class defines a minimum that we can expect a
 * TabEntityFactory to have for methods.
 *
 * @author bdc34
 *
 */
public interface TabEntityFactory {

    /**
     * Expected to return a list of entities to be displayed on the tab.
     * The standard meaning of the alpha parameters is that the tab
     * should only include entities that have names that start with that
     * letter.
     *
     * @param alpha
     * @return
     */
    public List <Individual> getRelatedEntites(String alpha);

    /**
     * Gets a count of entities that would be displayed on the tab.
     * @return
     */
    public int getRelatedEntityCount();

    /**
     * Returns a list of letters for which there are entities.
     * Each string should have a single letter.
     *
     * @return
     */
    public List <String> getLettersOfEnts();

}