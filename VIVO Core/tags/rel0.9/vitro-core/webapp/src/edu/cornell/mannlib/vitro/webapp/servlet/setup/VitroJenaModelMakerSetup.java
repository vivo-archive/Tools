package edu.cornell.mannlib.vitro.webapp.servlet.setup;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;

import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VitroJenaModelMaker;

public class VitroJenaModelMakerSetup implements ServletContextListener {

	protected final static String CONNECTION_PROP_LOCATION = "/WEB-INF/classes/connection.properties";
	protected final static String DB_TYPE = "MySQL";
	
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void contextInitialized(ServletContextEvent arg0) {
		
		/* Sometimes this class fails for no apparent reason and stops
		the context from loading.  Trying to eliminate all uncaught errors. */
		Log log = null;
		
		try {	
			log = LogFactory.getLog(VitroJenaModelMakerSetup.class.getName());
		} catch (Throwable t) {
			System.out.println("Unable to use error log for "+this.getClass().getName());
		}
		
		try {		
			String filename = arg0.getServletContext().getRealPath(CONNECTION_PROP_LOCATION);
		    if (filename == null || filename.length() <= 0) {
		    	if (log != null) {
		           log.error(
		        		   "Error setting up VitroModelMaker:\n" +
		                   "To establish the DB model you MUST set the "
		                   + "filename to the location of a "
		                   + "connection.properties file with the database connection parameters.");
		    	}
  	        }
	
	       File propF = new File(filename );
	       InputStream is = null;
	       try {
	           is = new FileInputStream(propF);
	       } catch (FileNotFoundException e) {
	    	   String msg = "Error setting up VitroJenaModelMaker:\nCould not load file "+filename;
	    	   if (log != null) {
	    		   log.error(msg);
	    	   } else {
	    		   System.out.println(msg);
	    	   }
	       }

	       Properties dbProps = new Properties();
	       try {
	           dbProps.load(is);
	           String dns = dbProps.getProperty("Vitro.defaultNamespace");
	           String dbDriverClassname = dbProps.getProperty("VitroConnection.DataSource.driver");
	           String jdbcUrl = dbProps.getProperty("VitroConnection.DataSource.url") + "?useUnicode=yes&characterEncoding=utf8";
	           String username = dbProps.getProperty("VitroConnection.DataSource.username");
	           String password = dbProps.getProperty("VitroConnection.DataSource.password"); 	   
	           DBConnection dbConn = new DBConnection(jdbcUrl,username,password,DB_TYPE);
	           ModelMaker mMaker = ModelFactory.createModelRDBMaker(dbConn);
	           VitroJenaModelMaker vjmm = new VitroJenaModelMaker(mMaker);
	           arg0.getServletContext().setAttribute("vitroJenaModelMaker",vjmm);
	           String msg = "VitroJenaModelMaker set up";
	           if (log != null) {
	        	   log.debug(msg);
	           } else {
	        	   System.out.println(msg);
	           }
	       } catch (IOException e) {
	    	   String msg = "Error setting up VitroJenaModelMaker:\nCould not load properties from file " + filename + '\n'
               + e.getMessage();
	    	   if (log != null) { 
	    		   log.error(msg); 
	    	   } else { 
	    		   System.out.println(msg);
	    	   }
	       } 
	       
		} catch (Throwable t) {
			String msg = "Unable to set up default VitroJenaModelMaker";
			if (log != null) {
				log.error(msg);
				log.error(t);
			} else {
				System.out.println(msg);
			}
		}

	}

}
