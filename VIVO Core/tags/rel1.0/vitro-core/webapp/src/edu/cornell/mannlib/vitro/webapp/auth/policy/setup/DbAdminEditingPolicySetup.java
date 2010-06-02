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

package edu.cornell.mannlib.vitro.webapp.auth.policy.setup;

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

import edu.cornell.mannlib.vitro.webapp.auth.identifier.DbAdminEditingIdentifierFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundleFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.ServletIdentifierBundleFactory;
import edu.cornell.mannlib.vitro.webapp.auth.policy.DbAdminEditingPolicy;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
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
public class DbAdminEditingPolicySetup  implements ServletContextListener  {
    private static final Log log = LogFactory.getLog(DbAdminEditingPolicySetup.class.getName());
   
    public void contextInitialized(ServletContextEvent sce) {
        try{
            log.debug("Setting up DbAdminEditingPolicy");
            
            //need to make a policy and add it to the ServeltContext
            OntModel model = (OntModel)sce.getServletContext().getAttribute("jenaOntModel");
            DbAdminEditingPolicy cep = makeDbAdminEditPolicyFromModel(model);
            ServletPolicyList.addPolicy(sce.getServletContext(), cep);
            
            //need to put an IdentifierFactory for DbAdminEditingIds into the ServletContext
            IdentifierBundleFactory ibfToAdd = new DbAdminEditingIdentifierFactory();
            ServletIdentifierBundleFactory.addIdentifierBundleFactory(sce.getServletContext(), ibfToAdd); 
            
            log.debug( "Finished setting up DbAdminEditingPolicy: " + cep );            
        }catch(Exception e){
            log.error("could not run DbAdminEditingPolicySetup: " + e);
            e.printStackTrace();
        }
    }
    
    public void contextDestroyed(ServletContextEvent sce) { /*nothing*/  }
    
    public static DbAdminEditingPolicy makeDbAdminEditPolicyFromModel( Model model ){
        DbAdminEditingPolicy pol = null;
        if( model == null )
            pol = new DbAdminEditingPolicy(null,null,null,null);
        else{
            Set<String> prohibitedProps = new HashSet<String>();
            // no need to iterate through any level higher than DB_ADMIN
            //for (BaseResourceBean.RoleLevel e : EnumSet.range(BaseResourceBean.RoleLevel.NOBODY,BaseResourceBean.RoleLevel.NOBODY)) {
                BaseResourceBean.RoleLevel e = BaseResourceBean.RoleLevel.NOBODY;
                ResIterator it = model.listSubjectsWithProperty( model.createProperty( VitroVocabulary.PROHIBITED_FROM_UPDATE_BELOW_ROLE_LEVEL_ANNOT),ResourceFactory.createResource(e.getURI()));
                while( it.hasNext() ){
                    Resource resource = it.nextResource();
                    if( resource != null && resource.getURI() != null ) {
                        log.debug("adding \""+resource.getURI()+"\" to properties prohibited from dbAdmin editing ("+e.getLabel()+")"); 
                        prohibitedProps.add( resource.getURI() );
                    }
                }
            //}
            pol = new DbAdminEditingPolicy(prohibitedProps,null,null,null);
        }               
        return pol;
    }
            
    public static void replaceDbAdminEditing( ServletContext sc, Model model ){
        ServletPolicyList.replacePolicy(sc, makeDbAdminEditPolicyFromModel(model));
    }    
}