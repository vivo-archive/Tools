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

package edu.cornell.mannlib.vitro.webapp.servlet.setup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;

import edu.cornell.mannlib.vitro.webapp.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VitroJenaModelMaker;

public class VitroJenaModelMakerSetup implements ServletContextListener {
	private static final Logger LOG = Logger
			.getLogger(VitroJenaModelMakerSetup.class);

	protected final static String DB_TYPE = "MySQL";
	
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void contextInitialized(ServletContextEvent arg0) {
		try {		
			String jdbcUrl = ConfigurationProperties.getProperty("VitroConnection.DataSource.url")
					+ "?useUnicode=yes&characterEncoding=utf8";
			String username = ConfigurationProperties.getProperty("VitroConnection.DataSource.username");
			String password = ConfigurationProperties.getProperty("VitroConnection.DataSource.password");
				
			DBConnection dbConn = new DBConnection(jdbcUrl, username, password, DB_TYPE);
			ModelMaker mMaker = ModelFactory.createModelRDBMaker(dbConn);
			VitroJenaModelMaker vjmm = new VitroJenaModelMaker(mMaker);
			arg0.getServletContext().setAttribute("vitroJenaModelMaker", vjmm);
			LOG.debug("VitroJenaModelMaker set up");
		} catch (Throwable t) {
			LOG.error("Unable to set up default VitroJenaModelMaker", t);
		}

	}

}
