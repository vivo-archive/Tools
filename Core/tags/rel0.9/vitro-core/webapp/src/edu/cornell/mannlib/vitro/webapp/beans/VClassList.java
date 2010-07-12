package edu.cornell.mannlib.vitro.webapp.beans;

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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * simple class that extends vclass to include a list of entites that are
 * members of that class.
 *
 * @author bdc34
 *
 */
public class VClassList extends VClass {

    List <Individual>entities = null;

    public VClassList( VClass vc, List<Individual> ents){
        this.entities = ents;
        this.setURI(vc.getURI());
        this.setNamespace(vc.getNamespace());
        this.setLocalName(vc.getLocalName());
        this.setDisplayLimit(vc.getDisplayLimit() );
        this.setDisplayRank(vc.getDisplayRank() );
        this.setName(vc.getName());
    }
    public List<Individual> getEntities() {
        return entities;
    }

    public void setEntities(List<Individual> entities) {
        this.entities = entities;
    }

    public void sort(){
        Collections.sort(getEntities(), getCompare() );
    }

    public int getSize(){
        if( entities != null )
            return entities.size();
        else
            return 0;
    }
    /**
     * override this if you want a different sorting.
     * @return
     */
    public Comparator<Individual> getCompare(){
        return new Comparator<Individual>(){
            public int compare(Individual o1, Individual o2) {
                if( o1 == null && o2 == null) return 0;
                if( o1 == null ) return 1;
                if( o2 == null ) return -1;

                if( o1.getName() == null && o2.getName() == null) return 0;
                if( o1.getName() == null) return 1;
                if( o2.getName() == null) return -1;
                return o1.getName().compareToIgnoreCase( o2.getName());


            }
        };
    }
}
