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

import edu.cornell.mannlib.vitro.webapp.auth.policy.JenaNetidPolicy.ContextSetup;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.*;

/**
 * This tag will build an option list using the subjectUri
 * and the predicateUri.
 *
 * User: bdc34
 * Date: Jan 4, 2008
 * Time: 12:16:29 PM
 */
public class OptionsForPropertyTag extends TagSupport {
	
	private static final Log log = LogFactory.getLog(OptionsForPropertyTag.class.getName());
    private String subjectUri, predicateUri, selectedUri;

    public String getSubjectUri() {
        return subjectUri;
    }
    public void setSubjectUri(String subjectUri) {
        this.subjectUri = subjectUri;
    }

    public String getPredicateUri() {
        return predicateUri;
    }
    public void setPredicateUri(String predicateUri) {
        this.predicateUri = predicateUri;
    }

    public String getSelectedUri() {
        return selectedUri;
    }
    public void setSelectedUri(String selectedUri) {
        this.selectedUri = selectedUri;
    }

    public int doStartTag() {
        try {
            VitroRequest vreq = new VitroRequest( (HttpServletRequest) pageContext.getRequest() );
            WebappDaoFactory wdf = vreq.getWebappDaoFactory();
            if( wdf == null ) throw new Exception("could not get WebappDaoFactory from request.");

            Individual subject = wdf.getIndividualDao().getIndividualByURI(getSubjectUri());
            if( subject == null ) throw new Exception("could not get individual for subject uri " + getSubjectUri());

            ObjectProperty objProp = wdf.getObjectPropertyDao().getObjectPropertyByURI(getPredicateUri());
            if( objProp == null ) throw new Exception ("could not get object property for predicate " + getPredicateUri());

            List <VClass> vclasses = new ArrayList<VClass>();
            vclasses = wdf.getVClassDao().getVClassesForProperty(getPredicateUri(), true);

            HashMap<String,Individual> indMap = new HashMap<String,Individual>();
            for ( VClass vclass :  vclasses){
                for( Individual ind : wdf.getIndividualDao().getIndividualsByVClassURI(vclass.getURI(),-1,-1))
                  if( !indMap.containsKey(ind.getURI()))
                    indMap.put(ind.getURI(),ind);
            }

            List<Individual> individuals = new ArrayList(indMap.values());

            List<ObjectPropertyStatement> stmts = subject.getObjectPropertyStatements();
            if( stmts == null ) throw new Exception("object properties for subject were null");

            individuals = removeIndividualsAlreadyInRange(individuals,stmts);
            Collections.sort(individuals,new  compareEnts());

            JspWriter out = pageContext.getOut();

            int optionsCount=0;
            for( Individual ind : individuals ){
                String uri = ind.getURI()  ;
                if( uri != null ){
                    out.print("<option value=\"" + StringEscapeUtils.escapeHtml( uri ) + '"');
                    if( uri.equals(getSelectedUri()))
                        out.print(" selected=\"selected\"");
                    out.print('>');
                    out.print(StringEscapeUtils.escapeHtml( ind.getName() ));
                    out.println("</option>");
                    ++optionsCount;
                }

            }
            log.trace("added "+optionsCount+" options for object property \""+getPredicateUri()+"\" in OptionsForPropertyTag.doStartTag()");
        } catch (Exception ex) {
            throw new Error("Error in doStartTag: " + ex.getMessage());
        }
        return SKIP_BODY;
    }

    public int doEndTag(){
	  return EVAL_PAGE;
	}

    private List<Individual> removeIndividualsAlreadyInRange(List<Individual> indiviuals,
                                                             List<ObjectPropertyStatement> stmts){
        log.trace("starting to check for duplicate range individuals in OptionsForPropertyTag.removeIndividualsAlreadyInRange() ...");
        HashSet<String>  range = new HashSet<String>();

        for(ObjectPropertyStatement ops : stmts){
            if( ops.getPropertyURI().equals(getPredicateUri()))
                range.add( ops.getObjectURI() );
        }

        int removeCount=0;
        ListIterator<Individual> it = indiviuals.listIterator();
        while(it.hasNext()){
            Individual ind = it.next();
            if( range.contains( ind.getURI() ) ) {
                it.remove();
                ++removeCount;
            }
        }
        log.trace("removed "+removeCount+" duplicate range individuals");
        return indiviuals;
    }


    private class compareEnts  implements Comparator<Individual>{
        public int compare(Individual o1, Individual o2) {
            return ((o1.getName()==null)?"":o1.getName()).compareToIgnoreCase((o2.getName()==null)?"": o2.getName());
        }
    }
}
