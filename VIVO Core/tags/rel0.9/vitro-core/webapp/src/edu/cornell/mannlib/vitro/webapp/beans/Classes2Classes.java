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

/**
 *
 * a class representing a direct subsumption relationship
 * between two ontology classes
 *
 */
public class Classes2Classes {

    private String superclassURI = null;
    private String superclassNamespace = null;
    private String superclassLocalName = null;

    private String subclassURI = null;
    private String subclassNamespace = null;
    private String subclassLocalName = null;

    public String getSuperclassURI(){ return superclassURI;}
    public void setSuperclassURI(String in){ superclassURI=in;}

    public String getSuperclassNamespace(){ return superclassNamespace; }
    public void setSuperclassNamespace(){ this.superclassNamespace=superclassNamespace;}

    public String getSuperclassLocalName(){ return superclassLocalName; }
    public void setSuperclassLocalName(){ this.superclassLocalName=superclassLocalName;}

    public String getSubclassURI(){ return subclassURI;}
    public void setSubclassURI(String in){ subclassURI=in;}

    public String getSubclassNamespace(){ return subclassNamespace; }
    public void setSubclassNamespace(){ this.subclassNamespace=subclassNamespace;}

    public String getSubclassLocalName(){ return subclassLocalName; }
    public void setSubclassLocalName(){ this.subclassLocalName=subclassLocalName;}

}