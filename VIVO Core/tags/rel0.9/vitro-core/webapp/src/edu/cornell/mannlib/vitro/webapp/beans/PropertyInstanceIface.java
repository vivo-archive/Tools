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

import java.util.Date;

public interface PropertyInstanceIface {
//needed for PropertyInstance
//object property statements
    public abstract String getPropertyURI();
    public abstract String getObjectEntURI();
    public abstract String getSubjectEntURI();
    public abstract String getQualifier();
    public abstract Date getSunrise();
    public abstract Date getSunset();

//  entities
    public abstract String getSubjectName();
    public abstract String getObjectName();

//needed for Any Property
//properties
    public abstract String getPropertyName();
    public abstract String getDomainPublic();
    public abstract String getRangePublic();

//classs
    public abstract String getDomainClassName();
    public abstract String getRangeClassName();
    public abstract String getDomainQuickEditJsp();
    public abstract String getRangeQuickEditJsp();

//classs2relations
    public abstract String getRangeClassURI();
    public abstract String getDomainClassURI();

    public abstract boolean getSubjectSide();

/******************* setters ************************/

    public abstract void setPropertyURI(String in);

    public abstract void setSubjectName(String in);
    public abstract void setObjectName(String in);
    public abstract void setRangeClassURI(String in);
    public abstract void setDomainClassURI(String in);
    public abstract void setDomainClassName(String in);
    public abstract void setRangeClassName(String in);

    public abstract void setSubjectEntURI(String in);
    public abstract void setObjectEntURI(String in);

    public abstract void setPropertyName(String in);

    public abstract void setDomainPublic(String in);

    public abstract void setRangePublic(String in);

    public abstract void setQualifier(String in);

    public abstract void setSunrise(Date in);

    public abstract void setSunset(Date in);

    public abstract void setSubjectSide(boolean in);

    public abstract void setDomainQuickEditJsp(String in);

    public abstract void setRangeQuickEditJsp(String in);

}
