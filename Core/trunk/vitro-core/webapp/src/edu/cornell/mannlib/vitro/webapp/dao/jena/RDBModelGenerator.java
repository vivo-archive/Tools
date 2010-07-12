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

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import org.apache.commons.dbcp.BasicDataSource;

import java.sql.SQLException;

public class RDBModelGenerator implements ModelGenerator {

    private BasicDataSource ds = null;
    private String dbTypeStr = null;
    private String modelNameStr = null;
    private OntModelSpec ontModelSpec = null;

    public RDBModelGenerator(BasicDataSource bds, String dbTypeStr, String modelNameStr, OntModelSpec ontModelSpec) {
        this.ds = bds;
        this.dbTypeStr = dbTypeStr;
        this.modelNameStr = modelNameStr;
        this.ontModelSpec = ontModelSpec;
    }

    public OntModel generateModel() {
        try {
            IDBConnection conn = new DBConnection(ds.getConnection(), dbTypeStr);
            ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
            Model model = maker.openModel(modelNameStr);
            OntModel oModel = ModelFactory.createOntologyModel(ontModelSpec, model);
            oModel.prepare();
            return oModel;
        } catch (SQLException e) {
            return null;
        }
    }

}
