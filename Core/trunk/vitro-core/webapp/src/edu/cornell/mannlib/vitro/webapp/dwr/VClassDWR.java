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

package edu.cornell.mannlib.vitro.webapp.dwr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class VClassDWR {    

    public VClassDWR(){        
    }

    /**
     * Returns all vclasses that the given vclas can have as the other side of
     * of the given property.
     *
     * Gets vclasses for the vclass select drop down on the ent_edit.jsp dynamic
     * add proprties form.
     *
     *
     * @param vclassId - vclass we want to make a property for
     * @param propertyId - property we want to use
     * @param filterOutUninstanciated - if true filter out any vclasses with zero instances.
     * @return a list of VClass objects, one for each vclass that could be in the
     * relation indicated by the parameters.
     */
    public Collection <VClass> getVClasses(String vclassURI, String propertyURI, boolean filterOutUninstanciated){
        List <VClass> vclasses = new ArrayList <VClass>();
        
        WebContext ctx = WebContextFactory.get();
        HttpServletRequest req = ctx.getHttpServletRequest();
        VitroRequest vreq = new VitroRequest(req);
        
        vclasses = vreq.getWebappDaoFactory().getVClassDao().getVClassesForProperty(vclassURI, propertyURI);
//        if( vclasses != null && filterOutUninstanciated ){
//            ListIterator<VClass> it = vclasses.listIterator();
//            while(it.hasNext()){
//                VClass clazz = it.next();
//                if( clazz.getEntityCount() == 0 )
//                    it.remove();
//            }
//        }
        return vclasses;
    }

}
