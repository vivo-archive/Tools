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

package edu.cornell.mannlib.vitro.webapp.web.jsptags;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.djpowell.sparqltag.SelectTag;
import net.djpowell.sparqltag.SparqlTag;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.utils.IterableAdaptor;

/**
 * sparql:listselect - wraps a SPARQL select query, and copies the resultset into
 * a variable.
 *
 *      var         resultset  (list of rows in the "rows" property)
 *      model       model
 *
 * based on SelectTag.java from David Powell.
 */

public class ListSparqlTag extends SelectTag {

/**
 * 
 * The query get executed, the result set copied into a Collection and
 * the queryExecution gets closed.
 */    
    public void doTag() throws JspException {
        trc.debug("CollectionSparqlTag.doTag()");

        SparqlTag container = ((SparqlTag)SimpleTagSupport.findAncestorWithClass(this, SparqlTag.class));
        if (container == null) {
            throw new RuntimeException("Ancestor must be sparql container");
        }
        //we copy the resultset into a collection so dispose can be called immediately
        //container.addInnerTag((SelectTag)this);
        
        Query query = parseQuery();
        QueryExecution qex = QueryExecutionFactory.create(query, model, qparams);
        trc.debug("query executed");
        
        ResultSet results;
        model.enterCriticalSection(Lock.READ);
        try {
            results = qex.execSelect();
            List<Map<String,RDFNode>> resultList = new LinkedList<Map<String,RDFNode>>();

            for( QuerySolution qs : IterableAdaptor.adapt( (Iterator<QuerySolution>)results )  ){
                trc.debug("found solution");
                HashMap<String,RDFNode> map1 = new HashMap<String,RDFNode>();
                for( String name : IterableAdaptor.adapt((Iterator<String>)qs.varNames())){
                    RDFNode value = qs.get(name);
                    if( trc.isDebugEnabled() ){trc.debug(name + ": " + value.toString() );}
                    map1.put(name, value);
                }
                resultList.add(map1);
            }    
            trc.debug("setting " + var + " to a list of size " + resultList.size() );
            getJspContext().setAttribute(var, resultList);
        } finally {
            model.leaveCriticalSection();
            synchronized (this) {
                if (qex != null) {
                    qex.close();
                }
                model = null;
                var = null;
                qparams = null;
                qex = null;
            }  
        }            
    }

    private static final Logger trc = Logger.getLogger(ListSparqlTag.class);
}
