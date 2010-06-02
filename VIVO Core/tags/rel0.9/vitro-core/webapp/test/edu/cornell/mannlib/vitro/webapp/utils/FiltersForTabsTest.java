package edu.cornell.mannlib.vitro.webapp.utils;

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

import net.sf.jga.fn.UnaryFunctor;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.IndividualImpl;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.beans.Tab;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.FiltersForTabs;

/**
 * User: bdc34
 * Date: Dec 13, 2007
 * Time: 2:47:03 PM
 */
public class FiltersForTabsTest {

    @Before
    public void setup(){
    }

    @Test
    public void testTabCollegeFiltering(){
        String flags [] =  {"CALS","BOAK","FUNGORK","MACAWI"};
        UnaryFunctor<Individual,Boolean> testFn =
                FiltersForTabs.getCollegeFilter(flags);
        Assert.assertTrue ( testFn!=null);

        IndividualImpl ind = new IndividualImpl();
        Assert.assertTrue(testFn.fn(ind) == false);

        ind.setFlag2Set("BOAK");
        Assert.assertTrue( testFn.fn(ind) == true);

        ind.setFlag2Set("");
       Assert.assertTrue(testFn.fn(ind) == false);

        ind.setFlag2Set("CALS,BOAK,FUNGORK");
        Assert.assertTrue(testFn.fn(ind) == true); 

        ind.setFlag2Set("FINKLY,HAPTO,FOOTOP");
        Assert.assertTrue(testFn.fn(ind) == false);

        ind.setFlag2Set("FINKLY,HAPTO,FOOTOP,CALS");
        Assert.assertTrue(testFn.fn(ind) == true);
    }

    @Test
    public  void testCollegeFilterCreation(){
        Tab tab = new Tab();
        tab.setFlag2Set("CALS,BOAK,FUNGORK");
        tab.setPortalId(7);

        UnaryFunctor<Individual,Boolean> testFn =
                FiltersForTabs.getFilterForTab(tab, new Portal());
        Assert.assertTrue ( testFn!=null);


        IndividualImpl ind = new IndividualImpl();
        Assert.assertFalse(  testFn.fn( ind) );

        ind.setFlag2Set("CALS");
        ind.setFlag1Numeric((int)FlagMathUtils.portalId2Numeric( tab.getPortalId() ));

        DateTime dt = new DateTime();
        ind.setSunrise(dt.minusDays(1000).toDate());
        ind.setSunset(dt.plusDays(1000).toDate());
        Assert.assertTrue( testFn.fn( ind));

        tab.setFlag2Mode("OMIT");        
        testFn = FiltersForTabs.getFilterForTab(tab, new Portal());

        Assert.assertFalse( testFn.fn(ind));

    }
}
