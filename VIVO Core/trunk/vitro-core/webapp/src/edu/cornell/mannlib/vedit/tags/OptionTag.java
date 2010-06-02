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

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.collections.OrderedMapIterator;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import edu.cornell.mannlib.vedit.beans.Option;
import edu.cornell.mannlib.vedit.tags.EditTag;
import org.apache.commons.lang.StringEscapeUtils;

public class OptionTag extends EditTag {
    private String name = null;

    public void setName( String name ) {
        this.name = name;
    }

    private void outputOptionsMarkup(List optList, JspWriter out) throws IOException {
        Iterator it = optList.iterator();
        while (it.hasNext()){
            Option opt = (Option) it.next();
            if (opt.getValue() == null)
                opt.setValue("");
            if (opt.getBody() == null)
                opt.setBody("");
            out.print("<option value=\""+StringEscapeUtils.escapeHtml(opt.getValue())+"\"");
            if (opt.getSelected())
                out.print(" selected=\"selected\"");
            out.print(">");
            out.print(StringEscapeUtils.escapeHtml(opt.getBody()));
            out.print("</option>\n");
        }
    }

    public int doEndTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();

            List optList = null;
            ListOrderedMap optGroups = null;

            try {
                optList = (List) getFormObject().getOptionLists().get(name);
                outputOptionsMarkup(optList,out);
            } catch (ClassCastException e){
                // maybe it's a ListOrderedMap of optgroups
                optGroups = (ListOrderedMap) getFormObject().getOptionLists().get(name);
                OrderedMapIterator ogKey = optGroups.orderedMapIterator();
                while (ogKey.hasNext()) {
                    String optGroupName = (String) ogKey.next();
                    out.println("<optgroup label=\""+StringEscapeUtils.escapeHtml(optGroupName)+"\">");
                    outputOptionsMarkup((List)optGroups.get(optGroupName),out);
                    out.println("</optgroup>");
                }
            } catch (NullPointerException npe) {
                System.out.println("OptionTag could not find option list for "+name);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            throw new JspException(ex.getMessage());
        }
        return SKIP_BODY; // EVAL_PAGE; did colnames only //EVAL_PAGE in connection pooled version;
    }
}
