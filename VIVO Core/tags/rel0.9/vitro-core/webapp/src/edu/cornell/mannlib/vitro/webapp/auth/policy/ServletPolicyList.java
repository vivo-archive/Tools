package edu.cornell.mannlib.vitro.webapp.auth.policy;

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

import java.util.ListIterator;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.VisitingPolicyIface;

/**
 * This is a PolicyList that can do isAuthorized and stashes a singleton
 * in the ServletContext.
 * 
 * The intent of this class is to allow a single point for policies
 * in a ServletContext.  example:
 * <code>
 * Authorization canIDoIt = ServletPolicyList.getPolicies( getServletContext() ).isAuthorized( IdBundle, action );
 * </code>
 * 
 * @author bdc34
 *
 */
public class ServletPolicyList extends PolicyList {
    protected static String POLICY_LIST = "policy_list";
    private static final Log log = LogFactory.getLog(ServletPolicyList.class.getName());

    /**
     * This is for general public use to get a list of policies for the ServletContext.
     * @param sc
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ServletPolicyList getPolicies(ServletContext sc){
        ServletPolicyList list  = null;
        try{
            list = (ServletPolicyList)sc.getAttribute(POLICY_LIST);
        }catch(ClassCastException cce){
            log.error(POLICY_LIST +" server context attribute was not of type List<PolicyIface>");
        }
        if( list == null ){
            list = new ServletPolicyList();
            sc.setAttribute(POLICY_LIST, list);
        }
        return list;
    }

    public static void addPolicy(ServletContext sc, PolicyIface policy){
        ServletPolicyList policies = getPolicies(sc);
        if( !policies.contains(policy) ){
            policies.add(policy);
            log.info("Added policy: " + policy.toString());
        }else{
            log.info("Ignored attempt to add redundent policy.");
        }
    }
    
    /** 
     * This adds the policy to the front of the list but it may be moved further down
     * the list by other policies that are later added using this method.
     */
    public static void addPolicyAtFront(ServletContext sc, PolicyIface policy){
        ServletPolicyList policies = getPolicies(sc);
        if( !policies.contains(policy) ){
            policies.add(0,policy);
            log.info("Added policy at front of ServletPolicyList: " + policy.toString());
        }else{
            log.info("Ignored attempt to add redundent policy.");
        }
    }
    
    /** 
     * Replace first instance of policy found in policy list.  If no instance
     * is found in list add at end of the list.
     * 
     * @param sc
     * @param policy
     */
    public static void replacePolicy(ServletContext sc, PolicyIface policy){
        if( sc == null ) 
            throw new IllegalArgumentException( "replacePolicy() needs a non-null ServletContext");
        if( policy == null )
            return;
        Class clzz = policy.getClass();
        
        ServletPolicyList spl = ServletPolicyList.getPolicies(sc);
        ListIterator<PolicyIface> it = spl.listIterator();
        boolean replaced = false;
        while(it.hasNext()){
            VisitingPolicyIface p = (VisitingPolicyIface)it.next();            
            if( clzz.isAssignableFrom(p.getClass()) ){
                it.set( policy );
                replaced = true;
            }
        }    
        if( ! replaced ){
            ServletPolicyList.addPolicy(sc, policy);
        }    
    }
}
