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

public interface ObjectPropertyStatement {

    public String toString();

    public String getDescription();

    public void setDescription(String description);

    public boolean isSubjectOriented();

    public void setSubjectOriented(boolean subjectOriented);

    public String getSubjectURI();

    public void setSubjectURI(String subjectURI);

    public String getQualifier();

    public void setQualifier(String qualifier);

    public String getObjectURI();

    public void setObjectURI(String objectURI);

    public Individual getSubject();

    public void setSubject(Individual subject);

    public ObjectProperty getProperty();

    public void setProperty(ObjectProperty property);

    public Individual getObject();

    public void setObject(Individual object);

    public String getPropertyURI();

    public void setPropertyURI(String URI);

    public void setSunrise(Date date);

    public Date getSunrise();

    public void setSunset(Date date);

    public Date getSunset();

    public PropertyInstance toPropertyInstance();

}