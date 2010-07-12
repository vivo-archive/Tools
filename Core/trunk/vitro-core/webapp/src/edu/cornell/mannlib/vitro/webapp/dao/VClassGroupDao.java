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

package edu.cornell.mannlib.vitro.webapp.dao;

import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;

import java.util.LinkedHashMap;
import java.util.List;

public interface VClassGroupDao {

    public abstract VClassGroup getGroupByURI(String uri);

    /**
     * Gets all of the ClassGroups as a map ordered by displayRank.
     * VClassGroup.getPublicName() -> VClassGroup
     *
     * @return
     */
    public abstract LinkedHashMap getClassGroupMap();

    /**
     * Return a list of VClassGroups with their associated VClasses
     * @return List
     */
    public abstract List getPublicGroupsWithVClasses();

    /**
     * Return a list of VClassGroups with their associated VClasses
     * @param displayOrder
     * @return List
     */
    public abstract List getPublicGroupsWithVClasses(boolean displayOrder);

    /**
     * Return a list of VClassGroups with their associated VClasses
     * @param displayOrder
     * @param includeUninstantiatedClasses
     * @return List
     */
    public abstract List getPublicGroupsWithVClasses(boolean displayOrder, boolean includeUninstantiatedClasses);

    /**
     * Return a list of VClassGroups with their associated VClasses
     * @param displayOrder
     * @param includeUninstantiatedClasses
     * @param getIndividualCount
     * @return List
     */
    public abstract List getPublicGroupsWithVClasses(boolean displayOrder, boolean includeUninstantiatedClasses, boolean getIndividualCount);


    public abstract void sortGroupList(List groupList);

    public abstract int removeUnpopulatedGroups(List /* of VClassGroup*/groups);

    int insertNewVClassGroup(VClassGroup vcg);

    void updateVClassGroup(VClassGroup vcg);

    void deleteVClassGroup(VClassGroup vcg);
}