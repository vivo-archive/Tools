package edu.cornell.mannlib.vitro.webapp.controller;

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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

public class VitroHttpServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected static DateFormat publicDateFormat = new SimpleDateFormat("M/dd/yyyy");

    private static final Log log = LogFactory.getLog(VitroHttpServlet.class.getName());
    
    private WebappDaoFactory myWebappDaoFactory = null;
    private WebappDaoFactory myAssertionsWebappDaoFactory = null;
    private WebappDaoFactory myDeductionsWebappDaoFactory = null;

    /**
     * Setup the auth flag, portal flag and portal bean objects.
     * Put them in the request attributes.
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
                          throws ServletException, IOException
    {
    	if (request.getSession() != null) {
	    	Object webappDaoFactoryAttr = request.getSession().getAttribute("webappDaoFactory");
	    	if (webappDaoFactoryAttr != null && webappDaoFactoryAttr instanceof WebappDaoFactory) {
	    		myWebappDaoFactory = (WebappDaoFactory) webappDaoFactoryAttr;
	    	}
	    	webappDaoFactoryAttr = request.getSession().getAttribute("assertionsWebappDaoFactory");
	    	if (webappDaoFactoryAttr != null && webappDaoFactoryAttr instanceof WebappDaoFactory) {
	    		myAssertionsWebappDaoFactory = (WebappDaoFactory) webappDaoFactoryAttr;
	    	}
	    	webappDaoFactoryAttr = request.getSession().getAttribute("deductionsWebappDaoFactory");
	    	if (webappDaoFactoryAttr != null && webappDaoFactoryAttr instanceof WebappDaoFactory) {
	    		myDeductionsWebappDaoFactory = (WebappDaoFactory) webappDaoFactoryAttr;
	    	}
    	}
    	
        //check to see if VitroRequestPrep filter was run
        if( request.getAttribute("appBean") == null ||
            request.getAttribute("webappDaoFactory") == null ){
            log.warn("request scope was not prepared by VitroRequestPrep");
        }
    }

    /**
     * doPost does the same thing as the doGet method
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
                           throws ServletException, IOException
    {
        doGet( request,response );
    }


    /** gets WebappDaoFactory with no filtering */
    public WebappDaoFactory getWebappDaoFactory(){
    	return (myWebappDaoFactory != null) ? myWebappDaoFactory :
        (WebappDaoFactory) getServletContext().getAttribute("webappDaoFactory");
    }
    
    /** gets assertions-only WebappDaoFactory with no filtering */
    public WebappDaoFactory getAssertionsWebappDaoFactory() {
    	return (myAssertionsWebappDaoFactory != null) ? myAssertionsWebappDaoFactory :
    	(WebappDaoFactory) getServletContext().getAttribute("assertionsWebappDaoFactory");
    }
    
    /** gets inferences-only WebappDaoFactory with no filtering */
    public WebappDaoFactory getDeductionsWebappDaoFactory() {
    	return (myDeductionsWebappDaoFactory != null) ? myDeductionsWebappDaoFactory :
    	(WebappDaoFactory) getServletContext().getAttribute("deductionsWebappDaoFactory");
    }

}
