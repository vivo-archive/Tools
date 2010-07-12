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
import java.util.HashMap;

public class ButtonForm {
    private String action = "";
    private String label  = "no label specified";
    private String cssClass = null;
    private HashMap<String,String> params = null;
    
    public ButtonForm() {
        action = ""; // submits to same page
        cssClass = null;
        label = "no label specified";
        params = null;
    }
    
    public ButtonForm(String actionStr, String classStr, String labelStr, HashMap<String,String> paramMap) {
        action = actionStr;
        cssClass = classStr; // can be null
        label = labelStr;
        params = paramMap;
    }
    
    public String getAction(){
        return action;
    }
    public void setAction(String s){
        action = s;
    }
    
    public String getLabel(){
        return label;
    }
    public void setLabel(String s){
        label = s;
    }
    
    public String getCssClass(){
        if (cssClass==null){
            return "";
        }
        return "class=\""+cssClass+"\"";
    }
    public void setCssClass(String s){
        cssClass=s;
    }
    
    public HashMap<String,String> getParams(){
        return params;
    }
    public void setParams(HashMap<String,String> p){
        params = p;
    }
    public void addParam(String key, String value){
        if (params==null){
            params = new HashMap<String,String>();
        }
        params.put(key, value);
    }
}
