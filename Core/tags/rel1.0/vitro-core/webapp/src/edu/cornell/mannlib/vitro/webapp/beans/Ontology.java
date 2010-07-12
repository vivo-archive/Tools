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

package edu.cornell.mannlib.vitro.webapp.beans;

import java.text.Collator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A representation of an OWL ontology resource
 * [kwg8-08/01/07] unified get*, set* methods with style in VClass.java
 */
public class Ontology implements Comparable<Ontology>
{
	
	private static final Log log = LogFactory.getLog(Ontology.class.getName());
	
    private int myID = -1;
    public int getId()          { return myID; }
    public void setId( int id ) { myID = id; }

    private String myName = null;
    public String getName()             { return myName; }
    public void setName( String name )  { myName = name; }

    private String myType = null;
    public String getType()             { return myType; }
    public void setType( String type )  { myType = type; }
    
    private String myPrefix = null;
    public String getPrefix()               { return myPrefix; }
    public void setPrefix( String prefix )  { myPrefix = prefix; }

    private int myNamespaceID;
    public int getNamespaceId()           { return myNamespaceID; }
    public void setNamespaceId( int nid ) { myNamespaceID = nid; }

    private String myURI;
    public String getURI()              { return myURI; }
    public void setURI( String URI )    { myURI = URI; }

    private List myVClassesList = null;
    public List getVClassesList()              { return myVClassesList; }
    public void setVClassesList( List vcl )    { myVClassesList = vcl; }

    private List myPropsList = null;
    public List getPropsList()           { return myPropsList; }
    public void setPropsList( List pl )  { myPropsList = pl; }

    private List myEntitiesList = null;
    public List getEntsList()                  { return myEntitiesList; }
    public void setEntsList( List entsList )   { myEntitiesList = entsList; }
    
    public int compareTo(Ontology o2) {
    	Collator collator = Collator.getInstance();
        if (o2 == null) {
            log.error("Ontology NULL in DisplayComparator()");
            return 0;
        }
        return collator.compare(this.getName(), o2.getName());
    }
    
}