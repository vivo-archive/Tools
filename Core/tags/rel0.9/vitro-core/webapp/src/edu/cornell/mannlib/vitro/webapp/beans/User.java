package edu.cornell.mannlib.vitro.webapp.beans;

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

import java.text.Collator;
import java.util.Date;

public class User implements Comparable {
    
    public final static int MIN_PASSWORD_LENGTH = 5;
    public final static int MAX_PASSWORD_LENGTH = 99;

    private String URI = null;
    private String namespace = null;
    private String localName = null;
    private String username = null;
    private String oldPassword = null;
    private String md5password = null;
    private Date modTime = null;
    private Date firstTime = null;
    private int loginCount = 0;
    private String roleURI = null;
    private String lastName = null;
    private String firstName = null;

    public String getURI() {
        return URI;
    }
    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getNamespace() {
        return namespace;
    }
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getLocalName() {
        return localName;
    }
    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getMd5password() {
        return md5password;
    }
    public void setMd5password(String md5password) {
        this.md5password = md5password;
    }

    public Date getModTime() {
        return modTime;
    }
    public void setModTime(Date modTime) {
        this.modTime = modTime;
    }

    public Date getFirstTime() {
        return firstTime;
    }
    public void setFirstTime(Date firstTime) {
        this.firstTime = firstTime;
    }

    public int getLoginCount() {
        return loginCount;
    }
    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public String getRoleURI() {
        return roleURI;
    }
    public void setRoleURI(String roleURI) {
        this.roleURI = roleURI;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int compareTo(Object o) {
        Collator collator = Collator.getInstance();
        return collator.compare(this.getUsername(),((User)o).getUsername());
    }

}
