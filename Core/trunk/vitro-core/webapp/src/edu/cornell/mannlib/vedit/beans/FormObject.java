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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import edu.cornell.mannlib.vedit.beans.Option;

public class FormObject implements Serializable {

    private HashMap values = new HashMap();
    private HashMap optionLists = new HashMap();
    private HashMap checkboxLists = new HashMap();
    private HashMap errorMap = new HashMap();
    private List dynamicFields = null;

    public HashMap getValues(){
        return values;
    }

    public void setValues(HashMap values){
        this.values = values;
    }

    public String valueByName(String name){
        return (String) values.get(name);
    }

    public HashMap getOptionLists() {
        return optionLists;
    }

    public void setOptionLists(HashMap optionLists) {
        this.optionLists = optionLists;
    }

    public List optionListByName(String key){
        return (List) optionLists.get(key);
    }

    public HashMap getCheckboxLists(){
        return checkboxLists;
    }

    public HashMap getErrorMap(){
        return errorMap;
    }

    public void setErrorMap(HashMap errorMap){
        this.errorMap = errorMap;
    }

    public List getDynamicFields() {
        return dynamicFields;
    }

    public void setDynamicFields(List dynamicFields){
        this.dynamicFields = dynamicFields;
    }

}
