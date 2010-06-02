package edu.cornell.mannlib.vitro.webapp.beans;

import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;

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

/**
 * User: bdc34
 * Date: Oct 18, 2007
 * Time: 3:41:23 PM
 */
public interface ResourceBean {
    String getURI();
    
    boolean isAnonymous();

    void setURI(String URI);

    String getNamespace();

    void setNamespace(String namespace);

    String getLocalName();

    void setLocalName(String localName);
    
    public RoleLevel getHiddenFromDisplayBelowRoleLevel() ;
    
    public void setHiddenFromDisplayBelowRoleLevel(RoleLevel eR) ;
    
    public void setHiddenFromDisplayBelowRoleLevelUsingRoleUri(String roleUri) ;
    /*
    public RoleLevel getProhibitedFromCreateBelowRoleLevel() ;
    
    public void setProhibitedFromCreateBelowRoleLevel(RoleLevel eR) ;
    
    public void setProhibitedFromCreateBelowRoleLevelUsingRoleUri(String roleUri) ;
    */
    public RoleLevel getProhibitedFromUpdateBelowRoleLevel() ;
    
    public void setProhibitedFromUpdateBelowRoleLevel(RoleLevel eR) ;
    
    public void setProhibitedFromUpdateBelowRoleLevelUsingRoleUri(String roleUri) ;
    /*
    public RoleLevel getProhibitedFromDeleteBelowRoleLevel() ;
    
    public void setProhibitedFromDeleteBelowRoleLevel(RoleLevel eR) ;
    
    public void setProhibitedFromDeleteBelowRoleLevelUsingRoleUri(String roleUri) ;
    */
}
