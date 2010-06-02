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

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.flags.FlagException;
import edu.cornell.mannlib.vitro.webapp.web.jsptags.InputElementFormattingTag;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles requests for entity information.
 * The methods for sorting Property/ObjectPropertyStatement Lists are here also.
 * @author bdc34
 *
 */
public class EntityListController extends VitroHttpServlet {

    long startTime = -1;
    
    private static final Log log = LogFactory.getLog(EntityListController.class.getName());

    /**
     * This generates a list of entities and then sends that
     * off to a jsp to be displayed.
     *
     * Expected parameters:
     *
     * Expected Attributes:
     * entity - set to entity to display properties for.
     *
     * @author bdc34
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res )
    throws IOException, ServletException {
        startTime = System.currentTimeMillis(); // TODO: remove
        try {
            super.doGet(req, res);
            VitroRequest vreq = new VitroRequest(req);
            Object obj = req.getAttribute("vclass");
            VClass vclass=null;
            if( obj == null ) { // look for vitroclass id parameter
                String vitroClassIdStr=req.getParameter("vclassId");
                if (vitroClassIdStr!=null && !vitroClassIdStr.equals("")) {
                    try {
                            //TODO have to change this so vclass's group and entity count are populated
                            vclass = vreq.getWebappDaoFactory().getVClassDao().getVClassByURI(vitroClassIdStr);
                            if (vclass == null) {
                                log.error("Couldn't retrieve vclass "+vitroClassIdStr);
                                res.sendRedirect(Controllers.BROWSE_CONTROLLER+"?"+req.getQueryString());
                            }
                        } catch (Exception ex) {
                            throw new HelpException("EntityListController: request parameter 'vclassId' must be a URI string");
                    }
                }
            } else if (obj instanceof VClass) {
                vclass = (VClass)obj;
            } else {
                throw new HelpException("EntityListController: attribute 'vclass' must be of type "
                        + VClass.class.getName() );
            }
            if (vclass!=null){
                doVClass(req, res, vclass);
            }
        } catch (HelpException help){
            doHelp(res);
        } catch (Throwable e) {
            req.setAttribute("javax.servlet.jsp.jspException",e);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp");
            rd.forward(req, res);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException,IOException {
        doGet(request, response);
    }

    /**
     * Perform the actions needed for the entity list jsp.
     * @param request
     * @param res
     * @param vclass
     * @throws ServletException
     * @throws IOException
     */
    private void doVClass(HttpServletRequest request, HttpServletResponse res, VClass vclass)
    throws ServletException, IOException, FlagException {
        VitroRequest vreq = new VitroRequest(request);
        
        //get list of entities,
        List<Individual> entities = vreq.getWebappDaoFactory().getIndividualDao().getIndividualsByVClass(vclass);
        request.setAttribute("entities",entities);

        if (entities == null) {
            log.error("entities list is null");
        }

        VClassGroup classGroup=vclass.getGroup();
        if (classGroup==null) {
            request.setAttribute("title",vclass.getName()/* + "&nbsp;("+vclass.getEntityCount()+")"*/);
        } else {
            request.setAttribute("title",classGroup.getPublicName());
            request.setAttribute("subTitle",vclass.getName()/* + "&nbsp;("+vclass.getEntityCount()+")"*/);
        }
        request.setAttribute("bodyJsp",Controllers.ENTITY_LIST_JSP);

        //here you could have a search css
        //String css = "<style type='text/css' media='screen'>@import '"+thePortal.getThemeDir()+"css/search.css';</style>";
        //request.setAttribute("css",css);

        //////////////////////////////////////////////////////////////////////
        //FINALLY: send off to the BASIC_JSP to get turned into html
        //

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);

        // use this for more direct debugging: RequestDispatcher rd = request.getRequestDispatcher(Controllers.ENTITY_LIST_JSP);
        res.setContentType("text/html; charset=UTF-8");
        request.setAttribute("pageTime", System.currentTimeMillis()-startTime);
        rd.include(request,res);
    }


    private void doHelp(HttpServletResponse res)
    throws IOException, ServletException {
        ServletOutputStream out = res.getOutputStream();
        res.setContentType("text/html; charset=UTF-8");
        out.println("<html><body><h2>Quick Notes on using EntityList:</h2>");
        out.println("<p>request.attributes 'entities' must be set by servlet before calling."
                +" It must be a List of Entity objects </p>");
        out.println("</body></html>");
    }

    private class HelpException extends Throwable{
        public HelpException(String string) {
            super(string);
        }
    }
}
