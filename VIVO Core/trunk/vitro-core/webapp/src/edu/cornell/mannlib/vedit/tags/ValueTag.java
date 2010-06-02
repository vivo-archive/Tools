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
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspWriter;
import edu.cornell.mannlib.vedit.beans.FormObject;
import org.apache.commons.lang.StringEscapeUtils;
import edu.cornell.mannlib.vedit.tags.EditTag;

public class ValueTag extends EditTag {
    private String name = null;

    public void setName( String name ) {
        this.name = name;
    }

    public int doEndTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();

            HashMap values = null;
            try {
                // FormObject foo = (FormObject) pageContext.getSession().getAttribute("FormObject");
                // FormObject foo = TagUtils.getFormObject(pageContext);
                FormObject foo = getFormObject();
                values = foo.getValues();
            } catch (Exception e){
                System.out.println("Could not get the form object from which to build an option list");
            }

            if (values != null){
                String value = (String) values.get(name);
                if (value != null)
                    out.print(StringEscapeUtils.escapeHtml(value));
            } else {
                System.out.println("ValueTag unable to get HashMap of form values");
            }

        } catch(Exception ex) {
            throw new JspException(ex.getMessage());
        }
        return SKIP_BODY;
    }
}
