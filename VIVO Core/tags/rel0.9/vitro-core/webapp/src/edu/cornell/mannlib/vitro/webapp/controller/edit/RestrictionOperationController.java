package edu.cornell.mannlib.vitro.webapp.controller.edit;

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

import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.EditEvent;

public class RestrictionOperationController extends BaseEditController {

	private static final Log log = LogFactory.getLog(RestrictionOperationController.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse response) {
		
		VitroRequest request = new VitroRequest(req);
		String defaultLandingPage = getDefaultLandingPage(request);
		
	    if (!checkLoginStatus(request,response))
	        return;
	    
	    try {
	        super.doGet(request,response);
	    } catch (Exception e) {
	        log.error(this.getClass().getName()+" encountered exception calling super.doGet()");
	    }
	    
	    try {
	    	
	    	OntModel ontModel = getOntModel(request, getServletContext());
		    
            HashMap epoHash = null;
            EditProcessObject epo = null;
            try {
                epoHash = (HashMap) request.getSession().getAttribute("epoHash");
                epo = (EditProcessObject) epoHash.get(request.getParameter("_epoKey"));
            } catch (NullPointerException e) {
                //session or edit process expired
                response.sendRedirect(defaultLandingPage);
                return;
            }

            if (epo == null) {
                response.sendRedirect(defaultLandingPage);
                return;
            }
            
            // TODO: I need to de-spaghettify this and remap this controller, now that I know what I'm doing 
                
            if ( (request.getParameter("_cancel") == null ) ) {            
            
	            ontModel.enterCriticalSection(Lock.WRITE);
	            try {
            		
            		ontModel.getBaseModel().notifyEvent(new EditEvent(getWebappDaoFactory().getUserURI(),true));
            		
	            	if ( request.getParameter("_action") != null && request.getParameter("_action").equals("delete") ) {
	            		
	            		String restId = request.getParameter("restrictionId");
	            		
	            		if (restId != null) {
	            			
	            			OntClass restrictedClass = ontModel.getOntClass( request.getParameter( "classUri" ) );
	            			
	            			OntClass rest = null;
	            			
	            			for ( Iterator i = restrictedClass.listEquivalentClasses(); i.hasNext(); ) {
	            				OntClass equivClass = (OntClass) i.next();
	            				if (equivClass.isAnon() && equivClass.getId().toString().equals(restId)) {
	            					rest = equivClass;
	            				}
	            			}
	            			
	            			if ( rest == null ) { 
	            				for ( Iterator i = restrictedClass.listSuperClasses(); i.hasNext(); ) {
	                				OntClass  superClass = (OntClass) i.next();
	                				if (superClass.isAnon() && superClass.getId().toString().equals(restId)) {
	                					rest = superClass;
	                				}
	                			}	
	            			}
	            			
	            			if ( rest != null ) {
	            				rest.remove();
	            			}
	            			
	            		}
	            		
	            	} else {
	            	
		            	OntProperty onProperty = ontModel.getOntProperty( (String) request.getParameter("onProperty") );
		            	
		            	String conditionTypeStr = request.getParameter("conditionType");
		            	
		            	String restrictionTypeStr = (String) epo.getAttribute("restrictionType");
		            	Restriction rest = null;
		            	
		            	OntClass ontClass = ontModel.getOntClass( (String) epo.getAttribute("VClassURI") );
		            	
		            	String roleFillerURIStr = request.getParameter("ValueClass");
		            	Resource roleFiller = null;
		            	if (roleFillerURIStr != null) {
		            		roleFiller = ontModel.getResource(roleFillerURIStr);
		            	}           	
		            	
		            	int cardinality = -1;
		            	String cardinalityStr = request.getParameter("cardinality");
		            	if (cardinalityStr != null) {
		            		cardinality = Integer.decode(cardinalityStr); 
		            	}
		            	
		            	if (restrictionTypeStr.equals("allValuesFrom")) {
		            		rest = ontModel.createAllValuesFromRestriction(null,onProperty,roleFiller);
		            	} else if (restrictionTypeStr.equals("someValuesFrom")) {
		            		rest = ontModel.createSomeValuesFromRestriction(null,onProperty,roleFiller);
		            	} else if (restrictionTypeStr.equals("hasValue")) {
		            		String valueURI = request.getParameter("ValueIndividual");
		            		if (valueURI != null) {
			            		Resource valueRes = ontModel.getResource(valueURI);
			            		if (valueRes != null) {
			            			rest = ontModel.createHasValueRestriction(null, onProperty, valueRes);
			            		}
		            		} else {
		            			String valueLexicalForm = request.getParameter("ValueLexicalForm");
		            			if (valueLexicalForm != null) {
		            				String valueDatatype = request.getParameter("ValueDatatype");
		            				Literal value = null;
		            				if (valueDatatype != null && valueDatatype.length() > 0) {
		            					RDFDatatype dtype = null;
		            					try {
		            						dtype = TypeMapper.getInstance().getSafeTypeByName(valueDatatype);
		            					} catch (Exception e) {
		            				    	log.warn ("Unable to get safe type " + valueDatatype + " using TypeMapper");
		            				    }
		            					if (dtype != null) {
		            						value = ontModel.createTypedLiteral(valueLexicalForm, dtype);
		            					} else {
		            						value = ontModel.createLiteral(valueLexicalForm);
		            					}
		            				} else {
		            					value = ontModel.createLiteral(valueLexicalForm);
		            				}
		            				rest = ontModel.createHasValueRestriction(null, onProperty, value);
		            			}
		            		}
		            	} else if (restrictionTypeStr.equals("minCardinality")) {
		            		rest = ontModel.createMinCardinalityRestriction(null,onProperty,cardinality);
		            	} else if (restrictionTypeStr.equals("maxCardinality")) {
		            		rest = ontModel.createMaxCardinalityRestriction(null,onProperty,cardinality);
		            	} else if (restrictionTypeStr.equals("cardinality")) {
		            		rest = ontModel.createCardinalityRestriction(null,onProperty,cardinality);
		            	}
		            	
		            	if (conditionTypeStr.equals("necessary")) {
		            		ontClass.addSuperClass(rest);
		            	} else if (conditionTypeStr.equals("necessaryAndSufficient")) {
		            		ontClass.addEquivalentClass(rest);
		            	}
	            	
	            	}
	            	
	            } finally {
	            	ontModel.getBaseModel().notifyEvent(new EditEvent(getWebappDaoFactory().getUserURI(),false));
	            	ontModel.leaveCriticalSection();
	            }
            
            }
            
            //if no page forwarder was set, just go back to referring page:
            String referer = epo.getReferer();
            if (referer == null) {
                response.sendRedirect(defaultLandingPage);
            } else {
                response.sendRedirect(referer);
            }
		    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	try {
	    		response.sendRedirect(defaultLandingPage);
	    		return;
	    	} catch (Exception f) {
	    		e.printStackTrace();
	    	}
	    }
    
	}

}
