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

package edu.cornell.mannlib.vitro.webapp.controller.edit.listing;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyGroup;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyGroupDao;

public class PropertyGroupsListingController extends BaseEditController {

   public void doGet(HttpServletRequest request, HttpServletResponse response) {

        if(!checkLoginStatus(request,response))
            return;

        try {
            super.doGet(request, response);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        Portal portal = (new VitroRequest(request)).getPortal();

        //need to figure out how to structure the results object to put the classes underneath

        PropertyGroupDao dao = getWebappDaoFactory().getPropertyGroupDao();

        List groups = dao.getPublicGroups(true);

        ArrayList results = new ArrayList();
        results.add("XX");
        results.add("Group");
        results.add("Public description");
        results.add("display rank");
        results.add("XX");

        if (groups != null) {
            Collections.sort(groups, new PropertyGroupDisplayComparator());
            Iterator<PropertyGroup> groupsIt = groups.iterator();
            while (groupsIt.hasNext()) {
                PropertyGroup pg = groupsIt.next();
                results.add("XX");
                if (pg.getName() != null) {
                    try {
                        results.add("<a href=\"./editForm?uri="+URLEncoder.encode(pg.getURI(),"UTF-8")+"&amp;home="+portal.getPortalId()+"&amp;controller=PropertyGroup\">"+pg.getName()+"</a>");
                    } catch (Exception e) {
                        results.add(pg.getName());
                    }
                } else {
                    results.add("");
                }
                results.add(pg.getPublicDescription()==null ? "unspecified" : pg.getPublicDescription());
                results.add(Integer.valueOf(pg.getDisplayRank()).toString());
                results.add("XX");
                List<Property> classList = pg.getPropertyList();
                if (classList != null && classList.size()>0) {
                    results.add("+");
                    results.add("XX");
                    results.add("Property");
                    results.add("example");
                    results.add("description");
                    results.add("@@entities");
                    Iterator<Property> propIt = classList.iterator();
                    while (propIt.hasNext()) {
                    	results.add("XX");
                        Property p = propIt.next();
                        if (p instanceof ObjectProperty) {
                        	ObjectProperty op = (ObjectProperty) p;
	                        if (op.getLocalNameWithPrefix() != null && op.getURI() != null) {
	                            try {
	                                results.add("<a href=\"propertyEdit?uri="+URLEncoder.encode(op.getURI(),"UTF-8")+"\">"+op.getLocalNameWithPrefix()+"</a>");
	                            } catch (Exception e) {
	                                results.add(op.getLocalNameWithPrefix());
	                            }
	                        } else {
	                            results.add("");
	                        }
	                        String exampleStr = (op.getExample() == null) ? "" : op.getExample();
	                        results.add(exampleStr);
	                        String descriptionStr = (op.getDescription() == null) ? "" : op.getDescription();
	                        results.add(descriptionStr);
                        } else {
                          	DataProperty dp = (DataProperty) p;
	                        if (dp.getName() != null && dp.getURI() != null) {
	                            try {
	                                results.add("<a href=\"datapropEdit?uri="+URLEncoder.encode(dp.getURI(),"UTF-8")+"\">"+dp.getName()+"</a>");
	                            } catch (Exception e) {
	                                results.add(dp.getName());
	                            }
	                        } else {
	                            results.add("");
	                        }
	                        String exampleStr = (dp.getExample() == null) ? "" : dp.getExample();
	                        results.add(exampleStr);
	                        String descriptionStr = (dp.getDescription() == null) ? "" : dp.getDescription();
	                        results.add(descriptionStr);
                        }
                        if (propIt.hasNext())
                            results.add("@@entities");
                    }
                }
            }
            request.setAttribute("results",results);
        }

        request.setAttribute("columncount",new Integer(5));
        request.setAttribute("suppressquery","true");
        request.setAttribute("title","Property Groups");
        request.setAttribute("portalBean",portal);
        request.setAttribute("bodyJsp", Controllers.HORIZONTAL_JSP);
        request.setAttribute("horizontalJspAddButtonUrl", Controllers.RETRY_URL);
        request.setAttribute("horizontalJspAddButtonText", "Add new property group");
        request.setAttribute("horizontalJspAddButtonControllerParam", "PropertyGroup");
        request.setAttribute("home", portal.getPortalId());
        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        try {
            rd.forward(request,response);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
   
    private class PropertyGroupDisplayComparator implements Comparator {
        public int compare (Object o1, Object o2) {
            try {
                int diff = ((PropertyGroup)o1).getDisplayRank() - ((PropertyGroup)o2).getDisplayRank();
                if (diff==0) {
                    return ((PropertyGroup)o1).getName().compareToIgnoreCase(((PropertyGroup)o2).getName());
                }
                return diff;
            } catch (Exception e) {
                return 1;
            }
        }
    }

}
