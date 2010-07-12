package edu.cornell.mannlib.vitro.webapp.dao;

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

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.beans.Tab;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: bdc34
 * Date: Apr 18, 2007
 * Time: 11:18:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TabDao {

    void addParentTab(Tab tab, Tab parent);

    void addParentTab(String tabURI, String parentURI);

    void removeParentTab(Tab tab, Tab parent);

    void removeParentTab(String tabURI, String parentURI);

    List<Tab> getParentTabs(Tab tab);

    List<Tab> getParentTabs(String tabURI);

    List<Tab> getChildTabs(Tab tab);

    List<Tab> getChildTabs(String tabURI);

//  void addAutolinkedVClass(Tab tab, VClass vclass);
//
//  void addAutolinkedVClass(String tabURI, String vclassURI);
//
//  void removeAutolinkedVClass(Tab tab, VClass vclass);
//
//  void removeAutolinkedVClass(String tabURI, String vclassURI);
//
//  List /* of Tab */ getAutolinkedVClasses(Tab tab);
//
//  List /* of Tab */ getAutolinkedVClasses(String tabURI);



    Tab getTab(int tab_id, int auth_level, ApplicationBean app);

    Tab getTab(int tab_id, int auth_level, ApplicationBean app, int depth );

    int insertTab(Tab tab);

    void updateTab(Tab tab);

    void deleteTab(Tab tab);

    Tab getTab(int tab_id);

    List <String> getTabAutoLinkedVClassURIs(int tab_id);

    List <String> getTabManuallyLinkedEntityURIs(int tab_id);

    Tab getTabByName(String tabName);

    List getPrimaryTabs(int portalId );

    List getSecondaryTabs(int primaryTabId);

    List getTabsForPortal(int portalId);

    List getTabsForPortalByTabtypes(int portalId, boolean direction, int tabtypeId);

    int cloneTab(int tabId) throws Exception;

    String getNameForTabId(int tabId);

    List getTabHierarcy(int tabId, int rootTab);

    int getRootTabId(int portalId);

    List<Tab> getAllAutolinkableTabs(int portalId);

    List<Tab> getAllManuallyLinkableTabs(int portalId);


}
