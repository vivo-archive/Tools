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

package edu.cornell.mannlib.vitro.webapp.controller.edit.utils;

import java.util.LinkedList;
import java.util.List;

import edu.cornell.mannlib.vedit.beans.Option;
import edu.cornell.mannlib.vitro.webapp.beans.ResourceBean;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean;
import edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RoleLevelOptionsSetup {
    private static final Log log = LogFactory.getLog(RoleLevelOptionsSetup.class.getName());
    
    public static List<Option> getDisplayOptionsList(ResourceBean b) {
        List<Option> hiddenFromDisplayList = new LinkedList<Option>();
        try {
            BaseResourceBean.RoleLevel currentLevel = b.getHiddenFromDisplayBelowRoleLevel();
            BaseResourceBean.RoleLevel roles[] = BaseResourceBean.RoleLevel.values();
            boolean someLevelSet=false;
            Option publicOption = null;
            for (BaseResourceBean.RoleLevel level : roles) {
                Option option = new Option (level.getURI(),level.getLabel(),false);
                if (level==BaseResourceBean.RoleLevel.PUBLIC) {
                    publicOption = option;
                }
                if (level==currentLevel) {
                    option.setSelected(true);
                    someLevelSet=true;
                }
                hiddenFromDisplayList.add(option);
            }
            if (!someLevelSet) {
                publicOption.setSelected(true);
            }
        } catch (Exception ex) {
            log.error("cannot create HiddenFromDisplayBelowRoleLevel options");
        }
        return hiddenFromDisplayList;
    }
    
    public static List<Option> getUpdateOptionsList(ResourceBean b) {
        // if make changes here, make sure to change SelfEditingPolicySetup and CuratorEditingPolicySetup
        List<Option> prohibitedFromUpdateList = new LinkedList<Option>();
        try {
            BaseResourceBean.RoleLevel currentLevel = b.getProhibitedFromUpdateBelowRoleLevel();
            BaseResourceBean.RoleLevel roles[] = BaseResourceBean.RoleLevel.values();
            boolean someLevelSet=false;
            Option publicOption = null;
            for (BaseResourceBean.RoleLevel level : roles) {
                Option option = new Option (level.getURI(),level.getLabel(),false);
                if (level==BaseResourceBean.RoleLevel.PUBLIC) {
                    publicOption = option;
                }
                if (level==currentLevel) {
                    option.setSelected(true);
                    someLevelSet=true;
                }
                prohibitedFromUpdateList.add(option);
            }
            if (!someLevelSet) {
                publicOption.setSelected(true);
            }
        } catch (Exception ex) {
            log.error("cannot create ProhibitedFromUpdateBelowRoleLevel options");
        }
        return prohibitedFromUpdateList;
    }
}
