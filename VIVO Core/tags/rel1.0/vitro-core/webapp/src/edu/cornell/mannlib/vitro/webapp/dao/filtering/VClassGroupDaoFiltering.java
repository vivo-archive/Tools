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

package edu.cornell.mannlib.vitro.webapp.dao.filtering;

import edu.cornell.mannlib.vitro.webapp.beans.*;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassGroupDao;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;
import net.sf.jga.fn.UnaryFunctor;

import java.util.*;

public class VClassGroupDaoFiltering extends BaseFiltering implements VClassGroupDao {

    private final VClassGroupDao innerDao;
    private final WebappDaoFactoryFiltering filteredDaos;
    private final VitroFilters filters;

    public VClassGroupDaoFiltering(VClassGroupDao classGroupDao,
            WebappDaoFactoryFiltering webappDaoFactoryFiltering,
            VitroFilters filters) {
        
        this.innerDao = classGroupDao;
        this.filteredDaos = webappDaoFactoryFiltering;
        this.filters =  filters;
    }


    public void deleteVClassGroup(VClassGroup vcg) {
        innerDao.deleteVClassGroup(vcg);
    }


    public LinkedHashMap getClassGroupMap() {
        LinkedHashMap lhm = innerDao.getClassGroupMap();
        Set keys = lhm.keySet();
        for( Object key : keys){
            VClassGroup vcg = (VClassGroup)lhm.get(key);
            if( vcg == null || !filters.getVClassGroupFilter().fn(vcg) ){
                lhm.remove(key);
            }
        }
        return lhm;
    }

    public VClassGroup getGroupByURI(String uri) {
        VClassGroup vg = innerDao.getGroupByURI(uri);
        if( vg != null && filters.getVClassGroupFilter().fn(vg))
            return vg;
        else
            return null;
    }

    public List getPublicGroupsWithVClasses() {
        return this.getPublicGroupsWithVClasses(false);
    }

    public List getPublicGroupsWithVClasses(boolean displayOrder) {
        return this.getPublicGroupsWithVClasses(displayOrder,true);
    }
    public List getPublicGroupsWithVClasses(boolean displayOrder, boolean includeUninstantiatedClasses) {
        return this.getPublicGroupsWithVClasses(displayOrder,includeUninstantiatedClasses,true);
    }
    /** filter both vclassgroups and their vclasses */
    public List getPublicGroupsWithVClasses(boolean displayOrder, boolean includeUninstantiatedClasses,
            boolean getIndividualCount) {

        LinkedHashMap groupMap = this.getClassGroupMap();
        List<VClassGroup> groups = new ArrayList(groupMap.values());

        VClassDao vclassDao = filteredDaos.getVClassDao();
        for( VClassGroup vg : groups){
            vclassDao.addVClassesToGroup(vg, includeUninstantiatedClasses, getIndividualCount);
        }
        if( !includeUninstantiatedClasses ){
            ListIterator<VClassGroup> it = groups.listIterator();
            while(it.hasNext()){
                if( it.next().size() == 0)
                    it.remove();
            }
        }
        return groups;
    }


    public int insertNewVClassGroup(VClassGroup vcg) {
        return innerDao.insertNewVClassGroup(vcg);
    }


    public int removeUnpopulatedGroups(List groups) {
        return innerDao.removeUnpopulatedGroups(groups);
    }


    public void sortGroupList(List groupList) {
        innerDao.sortGroupList(groupList);
    }


    public void updateVClassGroup(VClassGroup vcg) {
        innerDao.updateVClassGroup(vcg);
    }


}