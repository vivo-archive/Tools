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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertyGroup extends BaseResourceBean implements Comparable<PropertyGroup> {
    
    private static final Log log = LogFactory.getLog(PropertyGroup.class.getName());
    
	private int displayRank;
	private String name;
	private List<Property> propertyList;
	private int statementCount;
	private String publicDescription;
	
	public int getDisplayRank() {
		return this.displayRank;
	}
	
	public void setDisplayRank(int in) {
		this.displayRank = in;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String in) {
		this.name = in;
	}
	
	public List<Property> getPropertyList() {
		return this.propertyList;
	}
	
	public void setPropertyList(List<Property> in) {
		this.propertyList = in;
	}
	
	public int getStatementCount() {
	    return statementCount;
	}
	
	public void setStatementCount(int count) {
	    statementCount = count;
	}
	
	public String getPublicDescription() {
	    return publicDescription;
	}
	
	public void setPublicDescription(String s) {
	    publicDescription = s;
	}
			


    /**
     * Sorts PropertyGroup objects by group rank, then alphanumeric.
     * @author bdc34 modified by jc55, bjl23
     */
    public int compareTo(PropertyGroup o2) {
    	Collator collator = Collator.getInstance();
        if (o2 == null) {
            log.error("object NULL in DisplayComparator()");
            return 0;
        }
        int diff = (this.getDisplayRank() - o2.getDisplayRank());
        if (diff == 0 ) {
            return collator.compare(this.getName(),o2.getName());
        }
        return diff;
    }
	
}
