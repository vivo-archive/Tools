package edu.cornell.mannlib.vitro.webapp.web.jsptags;

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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.sf.jga.fn.UnaryFunctor;

/**
 * A tag to applying some string processing from the Request scope to selective
 * parts of the output.  Intended for search highlighting.
 */

public class StringProcessorTag extends BodyTagSupport {
    
    public void setPageContext(PageContext pageContext){
        this.pageContext = pageContext;
    }
    
    @Override
    public int doStartTag(){
        Object obj =  pageContext.getRequest().getAttribute(STRING_PROCESSOR) ;
        if( obj == null || !(obj instanceof UnaryFunctor) )                   
            return EVAL_BODY_INCLUDE;
        else
            return EVAL_BODY_BUFFERED;        
    }
    
    @Override
    public int doAfterBody() throws JspException{
        Object obj =  pageContext.getRequest().getAttribute(STRING_PROCESSOR) ;
        if( obj == null || !(obj instanceof UnaryFunctor) )               
            return EVAL_PAGE;
        
        UnaryFunctor<String,String> functor = (UnaryFunctor<String,String>)obj;        
        BodyContent bc = getBodyContent();
        JspWriter out = getPreviousOut();
        try{
            out.write(functor.fn(bc.getString()));
        }catch(IOException ex){} //ignore
        return SKIP_BODY;
    }

    public static void putStringProcessorInRequest(HttpServletRequest request, UnaryFunctor<String,String>processor){
        if( request==null || processor==null) return;
        
        Object obj =  request.getAttribute(STRING_PROCESSOR) ;
        if( obj == null )               
            request.setAttribute(STRING_PROCESSOR, processor);
        else{            
            UnaryFunctor<String,String> functor = (UnaryFunctor<String,String>)obj;
            request.setAttribute(STRING_PROCESSOR,processor.compose(functor));
        }
    }
    
   public static String STRING_PROCESSOR = "StringProcessor";
}
