package edu.cornell.mannlib.vitro.webapp.dwr;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.InsertException;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

/**
   This is a class to support Direct Web Remoting(DWR) in
   relation to vitro entities.  It exposes
   classes that can be called from javascript from browsers.
*/
public class EntityDWR {
    IndividualDao entityWADao;

    public EntityDWR(){
        WebContext ctx = WebContextFactory.get();
        ServletContext sc= ctx.getServletContext();
        entityWADao = ((WebappDaoFactory)sc.getAttribute("webappDaoFactory")).getIndividualDao();
    }

    /**
     *  Insets a new entity into the Vitro system.
     *  @returns < 1 if failed, entityId if success.
     */
    public String insertNewEntity(Individual ent ){
    	try {
    		return entityWADao.insertNewIndividual(ent);
    	} catch (InsertException e) {
    		e.printStackTrace();
    		return null;
    	}
    }

    /**
     ********************************************************
     * Gets an Entity object for a given entities.id.
     * @param entityId
     * @return
     */
    public Individual entityByURI(String entityURI){
        WebContext ctx = WebContextFactory.get();
        HttpServletRequest req = ctx.getHttpServletRequest();
        VitroRequest vreq = new VitroRequest(req);
        
        Individual ind = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(entityURI);
        return ind;
    }

    /**
       Gets entities that can be in a propertyId relation with classId but
       excludes entity with id of ignoredEntId.  If classId < 0 then it the
       query is not limited by vclass.
       @param propertyId - which relation to get entities for column properties.id
       @param ignoredEntId - the entity with this id is excluded from the
       result collection.  Set to < 0 if you don't want to ignore any entities.
       @param classId - if > 0 then only ents with this vClassId or which are subclasses
       of this are included in results.
       @param domainSide - if true, return RANGE entities of relation, if false return
        DOMAIN entities of relation.
       @return - Entities that can satisify the property.  Only the name and id of the
        object will be filled out.
    */
    @Deprecated
    public Collection getEntitiesByProperties(String propertyURI,
                                              String ignoredEntURI,
                                              String classURI,
                                              boolean domainSide ){
    	return new LinkedList();
//        WebContext ctx = WebContextFactory.get();
//        HttpServletRequest req = ctx.getHttpServletRequest();
//        VitroRequest vreq = new VitroRequest(req);
//        IndividualDao entityWADao = vreq.getWebappDaoFactory().getIndividualDao();
//        
//        return entityWADao.getIndividualsByProperty(
//                propertyURI,ignoredEntURI,classURI,domainSide);
    }

    /**
     *  Gets the monikers for a vclass.
     */
    public List monikers( String vClassURI ){
        WebContext ctx = WebContextFactory.get();
        HttpServletRequest req = ctx.getHttpServletRequest();
        VitroRequest vreq = new VitroRequest(req);
        IndividualDao entityWADao = vreq.getWebappDaoFactory().getIndividualDao();
                
        return entityWADao.monikers( vClassURI );
    }

    /**
     * Gets all of the entities given the vclass.
     * This returns a collection of EntityWebapp objects.
     */
    public Collection getEntitiesByVClass(String vclassURI){
        VClass vc = new VClass(vclassURI);
        WebContext ctx = WebContextFactory.get();
        HttpServletRequest req = ctx.getHttpServletRequest();
        VitroRequest vreq = new VitroRequest(req);
        IndividualDao entityWADao = vreq.getWebappDaoFactory().getIndividualDao();
        
        return entityWADao.getIndividualsByVClass( vc );
    }
}
