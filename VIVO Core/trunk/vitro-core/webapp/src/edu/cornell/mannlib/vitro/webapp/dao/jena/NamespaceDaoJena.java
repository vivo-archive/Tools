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

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import edu.cornell.mannlib.vitro.webapp.beans.Namespace;
import edu.cornell.mannlib.vitro.webapp.dao.NamespaceDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

import java.util.HashSet;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;

public class NamespaceDaoJena extends JenaBaseDao implements NamespaceDao {

    public NamespaceDaoJena(WebappDaoFactoryJena wadf) {
        super(wadf);
    }

    public void deleteNamespace(int namespaceId) {
        // should be deprecated
    }

    public void deleteNamespace(Namespace namespace) {
        // may not need/want to implement this
    }

    public List<Namespace> getAllNamespaces() {
        // TODO Auto-generated method stub
        return null;
    }

    public Namespace getNamespaceById(int namespaceId) {
        // should be deprecated
        return null;
    }

    public int insertNewNamespace(Namespace namespace) {
        // may not need/want to implement this
        return 0;
    }

    public void updateNamespace(Namespace ont) {
        // may not need/want to implement this
    }

}
