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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;

public class ListingControllerWebUtils {
	
	private static final Log log = LogFactory.getLog(ListingControllerWebUtils.class.getName());

	public static synchronized String formatIndividualLink(Individual ind, Portal portal) {
		try {
			System.out.println(ind.getURI());
			String nameStr = (ind.getName() != null) ? ind.getName() : ind.getURI();
    		return "<a href=\"entityEdit?uri="+URLEncoder.encode(ind.getURI(),"UTF-8")+"&amp;home="+portal.getPortalId()+"\">"+nameStr+"</a>";
		} catch (NullPointerException npe) {
			return "?";
    	} catch (UnsupportedEncodingException e) {
    		return ind.getName();
    	}
	}
	
	public static synchronized String formatVClassLinks(List<VClass> vList, Portal portal) {
	    String linksStr="";
	    if (vList!=null) {
	        int count=0;
	        for (Object obj : vList) {
	        	try {
	                if (count>0) linksStr += " | ";
		            VClass vclass = (VClass) obj;
		            try {
		                linksStr += "<a href=\"vclassEdit?uri="+URLEncoder.encode(vclass.getURI(),"UTF-8")+"&amp;home="+portal.getPortalId()+"\">"+vclass.getName()+"</a>";
		            } catch (UnsupportedEncodingException e) {
		                linksStr += vclass.getName();
		            }
	                ++count;
	        	} catch (Exception e) {
	        		if (obj == null) {
	        			log.error(ListingControllerWebUtils.class.getName()+" could not format null VClass");	        		
	        		}
	        	}
	        }
	    }
	    return linksStr;
	}
	
}
