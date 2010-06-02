package edu.cornell.mannlib.vitro.webapp.web;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.dao.PortalDao;

public class PortalWebUtil {
    private static final Log log = LogFactory.getLog(PortalWebUtil.class.getName());

    /**
     * This is only used by head.jsp
     * @param pB
     * @param appBean
     * @param portalDao
     */
    public static void populateSearchOptions(Portal pB, ApplicationBean appBean, PortalDao portalDao) {
        if( pB == null || portalDao == null || appBean==null)
            log.error("populateNavigationChoices needs a non null Portal, PortalDao, ApplicationBean and HttpServletRequest");

        String optionStr="";

        if ((appBean.getMaxSharedPortalId()-appBean.getMinSharedPortalId())>1 && ((pB.getPortalId()>=appBean.getMinSharedPortalId() && pB.getPortalId()<=appBean.getMaxSharedPortalId()) || pB.getPortalId()==appBean.getSharedPortalFlagNumeric())) { // CALS Research portals
            HashMap portalOptionHash  = new HashMap();
            int optionCount=0;
            for (int i=appBean.getMinSharedPortalId(); i<=appBean.getMaxSharedPortalId(); i++) {
                ++optionCount;
                if (i==pB.getPortalId()) {
                    portalOptionHash.put(new Integer(0),"<option value='"+pB.getPortalId()+"'>"+pB.getShortHand()+"</option>");
                } else if (pB.getPortalId()==appBean.getSharedPortalFlagNumeric()) {
                    Portal otherPortalBean= portalDao.getPortal(i); // will not change current portal id
                    // note that we are not using the portal Id as a hash, but the display rank, to sort the options -- the portalId is the value
                    if (otherPortalBean != null) {
                    	portalOptionHash.put(new Integer(otherPortalBean.getDisplayRank()),"<option value='"+i+"'>"+otherPortalBean.getShortHand()+"</option>");
                    }
                }
            }
            if (pB.getPortalId()==appBean.getSharedPortalFlagNumeric()) {
                portalOptionHash.put(new Integer(0),"<option value='"+pB.getPortalId()+"'>"+pB.getAppName()+"</option>");
            } else {
                Portal otherPortalBean= portalDao.getPortal(appBean.getSharedPortalFlagNumeric()); // will not change current portal id
                if (otherPortalBean != null) {
                	portalOptionHash.put(new Integer(otherPortalBean.getDisplayRank()),"<option value='"+appBean.getSharedPortalFlagNumeric()+"'>"+otherPortalBean.getAppName()+"</option>");
                }
            }
            ++optionCount;
            Iterator keyIt = portalOptionHash.keySet().iterator();
            LinkedList<Integer> keyList = new LinkedList<Integer>();
            while (keyIt.hasNext()) {
                Integer key = (Integer) keyIt.next();
                keyList.add(key);
            }
            Collections.sort(keyList);
            for (int j : keyList) {
                Object obj=null;
                if ((obj=portalOptionHash.get(new Integer(j)))!=null) {
                    if (obj instanceof String) {
                        optionStr += (String)obj;
                    }
                }
            }
        } else { // single portal
            optionStr="<option value='"+pB.getPortalId()+"'>"+pB.getShortHand()+"</option>";
        }
        if (!optionStr.equals("")) {
            pB.setSearchOptions(optionStr);
        }
    }


    /**
     * Make the navigation choices that will be used with the portal urlPrefixes.
     * This is only used by head.jsp
     * @param pB
     * @param request
     */
    @SuppressWarnings("unchecked")
    public static void populateNavigationChoices(Portal pB, HttpServletRequest request, ApplicationBean appBean, PortalDao portalDao) {
        if( pB == null || request == null || portalDao == null || appBean==null)
            log.error("populateNavigationChoices needs a non null Portal, PortalDao, ApplicationBean and HttpServletRequest");

        String choiceStr="";
        //we need to make a absolute URL for these to move from portal to portal, this is clearly a bad idea.
        StringBuilder url = new StringBuilder("http://");
        url.append(request.getLocalName());
        if( request.getLocalPort() != 80)
            url.append(':').append(request.getLocalPort());

        url.append(request.getContextPath());
        url.append('/');

        log.debug("attempting to use abs URL: " + url);

        if ((appBean.getMaxSharedPortalId()-appBean.getMinSharedPortalId())>1
                && ((pB.getPortalId()>=appBean.getMinSharedPortalId()
                        && pB.getPortalId()<=appBean.getMaxSharedPortalId())
                        || pB.getPortalId()==appBean.getSharedPortalFlagNumeric())) { // CALS Research portals
            HashMap portalOptionHash  = new HashMap();
            int optionCount=0;
            for (int i=appBean.getMinSharedPortalId(); i<=appBean.getMaxSharedPortalId(); i++) {
                ++optionCount;
                if (i==pB.getPortalId()) {
                    portalOptionHash.put(new Integer(pB.getDisplayRank()),"<td class='inlineSelected'>"+pB.getAppName()+"</td>");
                } else {
                    Portal otherPortalBean= portalDao.getPortal(i);// will not change current portal id
                    if (otherPortalBean != null) {
                    	portalOptionHash.put(new Integer(otherPortalBean.getDisplayRank()),
                            "<td class='unselected'><a href='"+url+otherPortalBean.getUrlprefix()
                            +'/'+"index.jsp'>"+otherPortalBean.getAppName()+"</a></td>");
                    }
                }
            }
            if (pB.getPortalId()==appBean.getSharedPortalFlagNumeric()) {
                portalOptionHash.put(new Integer(pB.getDisplayRank()),
                        "<td class='inlineSelected'>"+pB.getAppName()+"</td>");
            } else {
                Portal otherPortalBean= portalDao.getPortal(appBean.getSharedPortalFlagNumeric()); // will not change current portal id
                if (otherPortalBean != null) {
                portalOptionHash.put(new Integer(otherPortalBean.getDisplayRank()),
                        "<td class='unselected'><a href='"+url+otherPortalBean.getUrlprefix()
                        +'/'+"index.jsp'>"+otherPortalBean.getAppName()+"</a></td>");
                }
            }
            ++optionCount;
            Iterator keyIt = portalOptionHash.keySet().iterator();
            LinkedList<Integer> keyList = new LinkedList<Integer>();
            while (keyIt.hasNext()) {
                Integer key = (Integer) keyIt.next();
                keyList.add(key);
            }
            Collections.sort(keyList);
            for (int j : keyList) {
                Object obj=null;
                if ((obj=portalOptionHash.get(new Integer(j)))!=null) {
                    if (obj instanceof String) {
                        choiceStr += (String)obj;
                    }
                }
            }
        } else { // single portal
            choiceStr="<td class='inlineSelected'>"+pB.getAppName()+"</td>";
        }
        if (!choiceStr.equals("")) {
            pB.setNavigationChoices(choiceStr);
        }
    }

//    public static Collection<NavChoice> getNavigationChoices(Portal portal, PortalDao portalDao){
//        if( portal == null  || portalDao == null)
//            log.error("getNavigationChoices needs a non null Portal and PortalDao ");
//
//        ArrayList<NavChoice> list = null;
//        ApplicationBean appBean=ApplicationBean.getAppBean(null);
//        if ((appBean.getMaxSharedPortalId()-appBean.getMinSharedPortalId())>1
//                && ((portal.getPortalId()>=appBean.getMinSharedPortalId()
//                        && portal.getPortalId()<=appBean.getMaxSharedPortalId())
//                        || portal.getPortalId()==appBean.getSharedPortalFlagNumeric())) { // CALS Research portals
//
//            HashMap<Integer,NavChoice>  portalOptionHash = new HashMap<Integer,NavChoice>();
//            int optionCount=0;
//
//            for (int i=appBean.getMinSharedPortalId(); i<=appBean.getMaxSharedPortalId(); i++) {
//                ++optionCount;
//                if (i == portal.getPortalId()) {
//                    portalOptionHash.put(new Integer(portal.getDisplayRank()), new NavChoice(portal,true));
//                } else {
//                    Portal otherPortalBean= portalDao.getPortal(i);// will not change current portal id
//                    portalOptionHash.put(new Integer(otherPortalBean.getDisplayRank()),new NavChoice(otherPortalBean));
//                }
//            }
//
//            if (portal.getPortalId()==appBean.getSharedPortalFlagNumeric()) {
//                portalOptionHash.put(new Integer(portal.getDisplayRank()), (new NavChoice(portal,true)));
//            } else {
//                Portal otherPortalBean= portalDao.getPortal(appBean.getSharedPortalFlagNumeric()); // will not change current portal id
//                portalOptionHash.put(new Integer(otherPortalBean.getDisplayRank()),new NavChoice(otherPortalBean));
//            }
//            ++optionCount;
//
//            list = new ArrayList<NavChoice>(optionCount);
//            for (int j=0; j<=optionCount; j++) {
//                NavChoice obj=null;
//                if ((obj=portalOptionHash.get(new Integer(j)))!=null) {
//                    list.add(obj);
//                }
//            }
//        } else { // single portal
//            list = new ArrayList<NavChoice>(1);
//            list.add( new NavChoice(portal));
//        }
//        return list;
//    }
//
//    public static class NavChoice{
//        String appName;
//        String urlPrefix;
//        boolean selected=false;
//        public NavChoice(String appName, String urlPrefix, boolean selected) {
//            super();
//            this.appName = appName;
//            this.urlPrefix = urlPrefix;
//            this.selected = selected;
//        }
//        public NavChoice(Portal portal ) {
//            appName = portal.getAppName();
//            urlPrefix = portal.getUrlprefix();
//        }
//        public NavChoice(Portal portal ,boolean selected) {
//            this(portal);
//            this.selected = selected;
//
//        }
//    }
}
