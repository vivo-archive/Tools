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

package edu.cornell.mannlib.vedit.tags;

import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspWriter;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;
import org.apache.commons.lang.StringEscapeUtils;

public class EditTag extends TagSupport {
    private String name = null;

    public void setName( String name ) {
        this.name = name;
    }

    public int doEndTag() throws JspException {
        return SKIP_BODY;
    }

    public EditProcessObject getEpo() {
        EditProcessObject epo = null;
        String epoKey = null;
        String epoKeyAttr = (String) pageContext.getRequest().getAttribute("epoKey");
        if (epoKeyAttr != null) {
            epoKey = epoKeyAttr;
        }
        else {
            String epoKeyParam = (String) pageContext.getRequest().getParameter("epoKey");
            if (epoKeyParam != null) {
                epoKey = epoKeyParam;
            }
        }
        HashMap epoHash = (HashMap) pageContext.getSession().getAttribute("epoHash");
        try {
            epo = (EditProcessObject) epoHash.get(epoKey);
        } catch (NullPointerException npe) {
            System.out.println("Null epoHash in edu.cornell.mannlib.vitro.edu.tags.utils.TagUtils.getEpo()");
        }
        return epo;
    }

    public FormObject getFormObject() {
        FormObject foo=null;
        try {
            foo=getEpo().getFormObject();
        } catch (NullPointerException npe) {
            System.out.println("Null epo in edu.cornell.mannlib.vitro.edit.tags.utils.TagUtils.getFormObject()");
        }
        return foo;
    }
}
