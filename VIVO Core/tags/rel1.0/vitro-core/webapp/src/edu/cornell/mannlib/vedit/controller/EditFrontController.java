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

package edu.cornell.mannlib.vedit.controller;

import edu.cornell.mannlib.vitro.webapp.controller.VitroHttpServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

/**
 * This controller exists only so we can request different edit form controllers without having to have entries in web.xml for each.
 * @author bjl23
 *
 */
public class EditFrontController extends VitroHttpServlet {
    private static final Log log = LogFactory.getLog(EditFrontController.class.getName());
    private static final String CONTROLLER_PKG = "edu.cornell.mannlib.vitro.webapp.controller.edit";

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	try {
    		
	        String controllerName = request.getParameter("controller")+"RetryController";
	        if (controllerName==null || controllerName.length()==0) {
	            log.error("doPost() found no controller parameter");
	        }
	        Class controller = null;
	        Object controllerInstance = null;
	        try {
	            controller = Class.forName(CONTROLLER_PKG+"."+controllerName);
	            try {
	                controllerInstance = controller.getConstructor((Class[]) null).newInstance((Object[]) null);
	                ((HttpServlet)controllerInstance).init(getServletConfig());
	            } catch (Exception e) {
	                log.error("doPost() could not instantiate specific controller "+controllerName);
	            }
	        } catch (ClassNotFoundException e){
	            log.error("doPost() could not find controller "+CONTROLLER_PKG+"."+controllerName);
	        }
	        Class[] args = new Class[2];
	        args[0] = HttpServletRequest.class;
	        args[1] = HttpServletResponse.class;
	        try {
	            Method meth = controller.getDeclaredMethod("doGet",args);
	            Object[] methArgs = new Object[2];
	            methArgs[0] = request;
	            methArgs[1] = response;
	            try {
	                meth.invoke(controllerInstance,methArgs);
	            } catch (IllegalAccessException e) {
	                log.error("doPost() encountered IllegalAccessException on invoking "+controllerName);
	            } catch (InvocationTargetException e) {
	                log.error("doPost() encountered InvocationTargetException on invoking "+controllerName);
	                log.debug(e.getTargetException().getMessage());
	                e.printStackTrace();
	            }
	
	        } catch (NoSuchMethodException e){
	            log.error("could not find doPost() method in "+controllerName);
	        }
    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request,response);
    }

}
