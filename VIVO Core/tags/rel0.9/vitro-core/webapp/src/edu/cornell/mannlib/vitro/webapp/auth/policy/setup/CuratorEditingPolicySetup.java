package edu.cornell.mannlib.vitro.webapp.auth.policy.setup;

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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.CuratorEditingIdentifierFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundleFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.ServletIdentifierBundleFactory;
import edu.cornell.mannlib.vitro.webapp.auth.policy.CuratorEditingPolicy;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.VisitingPolicyIface;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

/**
 * Sets up RoleBasedPolicy and IdentifierBundleFactory.  
 * This will cause the vitro native login to add Identifiers that can
 * be used by the Auth system and the in-line editing.
 * 
 * To use this add it as a listener to the web.xml. 
 * 
 * See RoleBasedPolicy.java
 * 
 * @author bdc34
 *
 */
public class CuratorEditingPolicySetup  implements ServletContextListener  {
    private static final Log log = LogFactory.getLog(CuratorEditingPolicySetup.class.getName());
   
    public void contextInitialized(ServletContextEvent sce) {
        try{
            log.debug("Setting up CuratorEditingPolicy");
            
            //need to make a policy and add it to the ServeltContext
            OntModel model = (OntModel)sce.getServletContext().getAttribute("jenaOntModel");
            CuratorEditingPolicy cep = makeCuratorEditPolicyFromModel(model);
            ServletPolicyList.addPolicy(sce.getServletContext(), cep);
            
            //need to put an IdentifierFactory for CuratorEditingIds into the ServletContext
            IdentifierBundleFactory ibfToAdd = new CuratorEditingIdentifierFactory();
            ServletIdentifierBundleFactory.addIdentifierBundleFactory(sce.getServletContext(), ibfToAdd); 
            
            log.debug( "Finished setting up CuratorEditingPolicy: " + cep );            
        }catch(Exception e){
            log.error("could not run CuratorEditingPolicySetup: " + e);
            e.printStackTrace();
        }
    }
    
    public void contextDestroyed(ServletContextEvent sce) { /*nothing*/  }
    
    public static CuratorEditingPolicy makeCuratorEditPolicyFromModel( Model model ){
        CuratorEditingPolicy pol = null;
        if( model == null )
            pol = new CuratorEditingPolicy(null,null,null,null);
        else{
            Set<String> prohibitedProps = new HashSet<String>();
            //ResIterator it = model.listSubjectsWithProperty( model.createProperty( VitroVocabulary.PROPERTY_CURATOREDITPROHIBITEDANNOT ) );
            // need to iterate through one level higher than CURATOR (the higher of current 2 targeted levels) plus all higher levels
            for (BaseResourceBean.RoleLevel e : EnumSet.range(BaseResourceBean.RoleLevel.DB_ADMIN,BaseResourceBean.RoleLevel.NOBODY)) {
                ResIterator it = model.listSubjectsWithProperty( model.createProperty( VitroVocabulary.PROHIBITED_FROM_UPDATE_BELOW_ROLE_LEVEL_ANNOT),ResourceFactory.createResource(e.getURI()));
                while( it.hasNext() ){
                    Resource resource = it.nextResource();
                    if( resource != null && resource.getURI() != null ) {
                        log.debug("adding \""+resource.getURI()+"\" to properties prohibited from inline curator editing ("+e.getLabel()+")"); 
                        prohibitedProps.add( resource.getURI() );
                    }
                }
            }
            pol = new CuratorEditingPolicy(prohibitedProps,null,null,null);
        }               
        return pol;
    }
        
    
    public static void replaceCuratorEditing( ServletContext sc, Model model ){
        ServletPolicyList.replacePolicy(sc, makeCuratorEditPolicyFromModel(model));
    }
}