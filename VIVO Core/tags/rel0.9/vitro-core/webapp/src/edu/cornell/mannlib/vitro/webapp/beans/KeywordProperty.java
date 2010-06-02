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

public class KeywordProperty extends Property implements Comparable<KeywordProperty> {
    public final static int DEFAULT_KEYWORDS_DISPLAY_RANK = 99;
    
    private String displayLabel = null;
    private int    displayRank  = 0;
    
    public KeywordProperty(String displayText,String editText,int rank, String groupUri) {
        super();
        this.setDisplayLabel(displayText);
        this.setEditLabel(editText);
        this.setDisplayRank(rank);
        this.setGroupURI(groupUri);
        this.setLocalName("keywords");
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
    public void setDisplayLabel(String label) {
        displayLabel = label;
    }
    
    public int getDisplayRank() {
        return displayRank;
    }
    public void setDisplayRank(int rank) {
        displayRank = rank;
    }
    
    /**
     * Sorts alphabetically by non-public name
     */
    public int compareTo (KeywordProperty kp) {
        Collator collator = Collator.getInstance();
        return collator.compare(this.getDisplayLabel(),(kp).getDisplayLabel());
    }

}
