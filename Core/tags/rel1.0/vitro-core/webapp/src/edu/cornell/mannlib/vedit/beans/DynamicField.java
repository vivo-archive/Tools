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

package edu.cornell.mannlib.vedit.beans;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class DynamicField {

    private String name = null;

    private String table = null;

    private int maxCardinality = 1;
    private int minCardinality = -1;
    private int visible = -1;

    private List<DynamicFieldRow> rowList = null;
    private DynamicFieldRow rowTemplate = null;

    private HashMap metadata = new HashMap();

    private Boolean deleteable = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getMaxCardinality() {
        return maxCardinality;
    }

    public void setMaxCardinality (int maxCardinality) {
        this.maxCardinality = maxCardinality;
    }

    public int getMinCardinality () {
        return minCardinality;
    }

    public void setMinCardinality(int minCardinality) {
        this.minCardinality = minCardinality;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public boolean getDeleteable() {
        return deleteable;
    }

    public void setDeleteable(boolean deleteable) {
        this.deleteable = deleteable;
    }

    public HashMap getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap metadata) {
        this.metadata = metadata;
    }

    public List<DynamicFieldRow> getRowList() {
        return rowList;
    }

    public void setRowList (List<DynamicFieldRow> rowList) {
        this.rowList = rowList;
    }

    public DynamicFieldRow getRowTemplate() {
        return rowTemplate;
    }

    public void setRowTemplate(DynamicFieldRow dfr) {
        rowTemplate = dfr;
    }

}
