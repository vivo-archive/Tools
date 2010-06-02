package edu.cornell.mannlib.vitro.webapp.dao.jena;

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

import java.sql.SQLException;
import java.sql.Connection;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.GraphRDB;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.db.ModelRDB;
import com.hp.hpl.jena.graph.Graph;



public class RDBGraphGenerator implements GraphGenerator {

	private static final Log log = LogFactory.getLog(RDBGraphGenerator.class.getName());
	
    private BasicDataSource ds = null;
    private Connection connection = null;
    private String dbTypeStr = null;
    private String graphID = null;

    public RDBGraphGenerator(BasicDataSource bds, String dbTypeStr, String graphID) {
        this.ds = bds;
        this.dbTypeStr = dbTypeStr;
        this.graphID = graphID;
    }

    public Graph generateGraph() {
    	log.info(this.getClass().getName()+" regenerateGraph()");
        try {
        	if (log.isDebugEnabled()) {
        		log.debug(ds.getNumActive() + " active SQL connections");
        		log.debug(ds.getNumIdle() + " idle SQL connections");
        	}
        	if ( ( this.connection == null ) || ( this.connection.isClosed() ) ) {
        		this.connection = ds.getConnection();
        	}
            IDBConnection idbConn = new DBConnection(this.connection, dbTypeStr);
            Graph requestedProperties = null;
            boolean modelExists = idbConn.containsModel(graphID);
            if (!modelExists) {
            	requestedProperties = ModelRDB.getDefaultModelProperties(idbConn).getGraph();
            }
            Graph graphRDB = new GraphRDB(idbConn, graphID, requestedProperties, GraphRDB.OPTIMIZE_ALL_REIFICATIONS_AND_HIDE_NOTHING, !modelExists);
            return graphRDB;
        } catch (SQLException e) {
        	log.error(e);
        	throw new RuntimeException("SQLException: unable to regenerate graph", e);
        }
    }
    
    public Connection getConnection() {
    	return connection;
    }

}


