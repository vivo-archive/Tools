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

package edu.cornell.mannlib.vitro.webapp.dao.filtering.filters;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.IndividualImpl;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyInstance;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;

public class HiddenFromEditRoleFilterTest {

        
    OntModel testModel;
    WebappDaoFactory wdf;
    
    @Before
    public void setUp() throws Exception {
        Model model = ModelFactory.createDefaultModel();        
        InputStream in = HiddenFromDisplayBelowRoleLevelFilter.class.getResourceAsStream("./filtertesting.rdf");
        model.read(in,null);
        testModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,model);
        wdf = new WebappDaoFactoryJena(testModel,"http://example.org/test/1.0/",null,null);        
        
    }
    
    @Test
    public void testCuratorLevelRoleFilter(){
        HiddenFromDisplayBelowRoleLevelFilter filter =  new HiddenFromDisplayBelowRoleLevelFilter(RoleLevel.CURATOR, wdf);    
        Assert.assertNotNull(filter);
        
        Individual ind = new IndividualImpl();                
        
        List<VClass> vcs = new ArrayList<VClass>();
        VClass vc = new VClass() ;        
        vc.setHiddenFromDisplayBelowRoleLevel(RoleLevel.PUBLIC);        
        vcs.add( vc );
        ind.setVClasses(vcs, true);
        vc = new VClass() ;
        vc.setHiddenFromDisplayBelowRoleLevel(RoleLevel.PUBLIC);        
        vcs.add( vc );
        ind.setVClasses(vcs, true);
                
        Assert.assertTrue( filter.individualFilter.fn(ind) );
        
        ind.setHiddenFromDisplayBelowRoleLevel(RoleLevel.PUBLIC);
        Assert.assertTrue( filter.individualFilter.fn(ind) );
        
        ind.setHiddenFromDisplayBelowRoleLevel(RoleLevel.DB_ADMIN);
        Assert.assertFalse( filter.individualFilter.fn(ind) );

        
        //classes can force an individual to be hidden
        ind.setHiddenFromDisplayBelowRoleLevel(RoleLevel.PUBLIC);
        vc.setHiddenFromDisplayBelowRoleLevel(RoleLevel.DB_ADMIN);
//        Assert.assertFalse( filter.individualFilter.fn(ind) );        
        
        Assert.assertFalse( filter.classFilter.fn( vc ) );
        vc.setHiddenFromDisplayBelowRoleLevel(RoleLevel.CURATOR);
        Assert.assertTrue( filter.classFilter.fn( vc ) );
        
        DataProperty dp = new DataProperty();
        Assert.assertTrue( filter.dataPropertyFilter.fn(dp) );
        dp.setHiddenFromDisplayBelowRoleLevel(RoleLevel.PUBLIC);
        Assert.assertTrue( filter.dataPropertyFilter.fn(dp) );
        dp.setHiddenFromDisplayBelowRoleLevel(RoleLevel.DB_ADMIN);
        Assert.assertFalse( filter.dataPropertyFilter.fn(dp) );        
        
    }
}
