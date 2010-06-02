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

import net.sf.jga.algorithms.Filter;
import net.sf.jga.fn.UnaryFunctor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BaseFiltering {

//    public Collection<DataProperty> filter(Collection<DataProperty> cin,
//                                            UnaryFunctor<DataProperty,Boolean> test){
//        ArrayList<DataProperty> cout = new ArrayList<DataProperty>();
//        Filter.filter(cin,test,cout);
//        return cout;
//    }

    public <T> Collection<T> filter(Collection<? extends T> cin,
                                                 UnaryFunctor<T,Boolean> test){
        if( cin == null ) return new ArrayList<T>(0);
        ArrayList<T> cout = new ArrayList<T>();
        Filter.filter(cin,test,cout);
        return cout;
    }


    public <T> List<T> filter(List<? extends T> cin,
                               UnaryFunctor<T,Boolean> test){
        if( cin == null ) return new ArrayList<T>(0);

        ArrayList<T> cout = new ArrayList<T>();
        Filter.filter(cin,test,cout);
        return cout;
    }
}
