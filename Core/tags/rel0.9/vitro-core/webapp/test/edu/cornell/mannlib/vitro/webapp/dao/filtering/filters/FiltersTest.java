package edu.cornell.mannlib.vitro.webapp.dao.filtering.filters;

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

import java.util.Date;

import net.sf.jga.fn.UnaryFunctor;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.IndividualImpl;
import edu.cornell.mannlib.vitro.webapp.beans.Tab;


public class FiltersTest {

    Boolean ACCEPTED = Boolean.TRUE;
    Boolean REJECTED = Boolean.FALSE;
    
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testTimeFiltersForFutureEvents(){
        Tab tab = new Tab();
        tab.setDayLimit( 10 );
        UnaryFunctor<Individual,Boolean> filter =
                FiltersForTabs.getTimeFilter(tab, new DateTime());

        Individual ind = new IndividualImpl();
        DateTime timekey;

        // Allow a slight fudge factor for the time it takes the tests to run.
        DateTime now = new DateTime().plusSeconds(1);

        for(int i=1; i < 100 ; i++){
            timekey = now.minusDays(i);
            ind.setTimekey( timekey.toDate() );
            Assert.assertTrue("minus " + i + " days should Reject", 
            		filter.fn( ind ) == REJECTED);
        }

        for(int i=0; i< 10 ; i++){
            timekey = now.plusDays(i);
            ind.setTimekey( timekey.toDate() );
            Assert.assertTrue("plus " + i + " days should Accept", 
            		filter.fn( ind ) == ACCEPTED);
        }

        timekey = now.plusDays( 10 );
        ind.setTimekey( timekey.toDate() );
        Assert.assertTrue("plus 10 days should Reject",
        		filter.fn( ind ) == REJECTED);

        for(int i=10; i < 1000 ; i++){
            timekey = now.plusDays(i);
            ind.setTimekey( timekey.toDate() );
            Assert.assertTrue("plus " + i + " days should Reject", 
            		filter.fn( ind ) == REJECTED);
        }
    }

    @Test
    public void testTimeFiltersForPastReleases(){
        Tab tab = new Tab();
        tab.setDayLimit( -10 );
        UnaryFunctor<Individual,Boolean> filter =
                FiltersForTabs.getTimeFilter(tab, new DateTime());

        Individual ind = new IndividualImpl();
        DateTime sunrise;
        
        // Allow a slight fudge factor for the time it takes the tests to run.
        DateTime now = new DateTime().plusSeconds(1);
        
        for(int i=1; i < 1000 ; i++){
            sunrise = now.plusDays(i);
            ind.setSunrise( sunrise.toDate() );
            Assert.assertTrue("plus " + i + " days should Reject", 
            		filter.fn( ind ) == REJECTED);
        }

        ind.setSunrise(  now.minusMinutes(20).toDate() );
        Assert.assertTrue("minus 20 minutes should Accept", 
        		filter.fn( ind ) == ACCEPTED);

        for(int i=1; i <= 10 ; i++){
            sunrise = now.minusDays(i);
            ind.setSunrise( sunrise.toDate() );
            Assert.assertTrue("minus " + i + " days should Accept", 
            		filter.fn( ind ) == ACCEPTED);
        }

        for(int i=11; i < 100 ; i++){
            sunrise = now.minusDays(i);
            ind.setSunrise( sunrise.toDate() );
            Assert.assertTrue("minus " + i + " days should Reject", 
            		filter.fn( ind ) == REJECTED);
        }
    }

    @Test
    public void testMarkowitzCase(){
    	DateTime now = new DateTime().withTime(0, 0, 0, 0);
    	Date sunrise = now.minusDays(1).toDate();
    	Date timeKey = now.plusDays(2).toDate();
    	
        Tab tab = new Tab();
        tab.setDayLimit( -10 );
        UnaryFunctor<Individual,Boolean> filter =
                FiltersForTabs.getTimeFilter(tab, new DateTime());

        Individual ind = new IndividualImpl();
        ind.setSunrise( sunrise );
        ind.setTimekey( timeKey );

        Assert.assertTrue("Should accept with day limit -10",
        		filter.fn( ind ) == ACCEPTED);

        tab.setDayLimit( 10 );
        filter = FiltersForTabs.getTimeFilter(tab, new DateTime());

        Assert.assertTrue("Should accept with day limit +10", 
        		filter.fn( ind ) == ACCEPTED );
    }


}
