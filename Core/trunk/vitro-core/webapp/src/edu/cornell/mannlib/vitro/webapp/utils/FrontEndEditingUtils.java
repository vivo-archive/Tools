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

package edu.cornell.mannlib.vitro.webapp.utils;

import java.util.Arrays;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

public class FrontEndEditingUtils {
 
    // NB List includes only those properties currently editable from the front end.
    private static final List<String> VITRO_NS_DATA_PROPS = Arrays.asList(VitroVocabulary.BLURB,                                                                         
                                                                        VitroVocabulary.CITATION, 
                                                                        VitroVocabulary.DESCRIPTION, 
                                                                        VitroVocabulary.IMAGETHUMB, 
                                                                        VitroVocabulary.LABEL, 
                                                                        VitroVocabulary.MONIKER
                                                                        // VitroVocabulary.RDF_TYPE,
                                                                        // VitroVocabulary.TIMEKEY
                                                                        );
    
    // NB List includes only those properties currently editable from the front end.   
    private static final List<String> VITRO_NS_OBJECT_PROPS = Arrays.asList(VitroVocabulary.ADDITIONAL_LINK,
                                                                           VitroVocabulary.PRIMARY_LINK
                                                                           );
            

    public static String getVitroNsPropDatatypeUri(String propName) {
        //Resource datatype = propName == TIMEKEY ? XSD.dateTime : XSD.xstring;
        //return datatype.getURI();
        return XSD.xstring.getURI();
    }

//  public static final Map<String, String> VITRO_NS_PROPERTIES = new HashMap<String, String>() {
//  {
//      put(BLURB, XSD.xstring.getURI());
//      put(CITATION, XSD.xstring.getURI());
//      put(DESCRIPTION, XSD.xstring.getURI());
//      put(LABEL, XSD.xstring.getURI());
//      put(LINK_ANCHOR, XSD.xstring.getURI());
//      put(MONIKER, XSD.xstring.getURI());
//      put(PRIMARY_LINK, XSD.xstring.getURI()); 
//      put(RDF_TYPE, XSD.xstring.getURI());
//      put(TIMEKEY, XSD.dateTime.getURI());            
//  }
//};

    public static boolean isVitroNsDataProp(String propertyUri) {
        return VITRO_NS_DATA_PROPS.contains(propertyUri);
    }
    
    public static boolean isVitroNsObjProp(String propertyUri) {
        return VITRO_NS_OBJECT_PROPS.contains(propertyUri);
    }
    
    public static String getVitroNsObjDisplayName(String predicateUri, Individual object, Model model) {
        
        String displayName = null;
                
        // These are the only Vitro namespace object properties that are editable on the front end at this point.
        if (StringUtils.equalsOneOf(predicateUri, VitroVocabulary.PRIMARY_LINK, VitroVocabulary.ADDITIONAL_LINK)) {            
            String linkAnchor = getLiteralValue(model, object, VitroVocabulary.LINK_ANCHOR);
            String linkUrl = getLiteralValue(model, object, VitroVocabulary.LINK_URL);            
            displayName = "<a class='externalLink' href='" + linkUrl + "'>" + linkAnchor + "</a>";                       
        }
        
        return displayName;

    }
    
    private static String getLiteralValue(Model model, Individual ind, String predicateUri) {

        String value = null;
        StmtIterator stmts = model.listStatements(model.createResource(ind.getURI()),  
                                                  model.getProperty(predicateUri),
                                                  (RDFNode)null);
        while (stmts.hasNext()) {
            Statement stmt = stmts.nextStatement();
            RDFNode node = stmt.getObject();
            if (node.isLiteral()) {
                Literal lit = (Literal) node.as(Literal.class);
                value = lit.getLexicalForm();
            }
        }
        
        return value;
            
    }
   
}
