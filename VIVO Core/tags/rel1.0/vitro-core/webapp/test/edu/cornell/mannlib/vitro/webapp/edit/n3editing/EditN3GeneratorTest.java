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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.EditLiteral;

public class EditN3GeneratorTest {

    EditN3Generator en3g ;
    
    @Before
    public void setUp() throws Exception {
        en3g = new EditN3Generator((EditConfiguration) null);
    }

    @Test
    public void testSubInLiterals() {
        String var = "TestVar";
        String target = "Fake n3 ?TestVar .";
        Literal literal = null;
        
        EditN3Generator en3g = new EditN3Generator((EditConfiguration) null);
        String result = en3g.subInLiterals(var, literal, target);
        Assert.assertNotNull( result );               
    }
    
    
    @Test
    public void testSubInLiteralsWithGroupReference() {
        String var = "TestVar";
        String target = "Fake n3 ?TestVar .";
        Literal literal = new EditLiteral("should not a regex group --> ?2 <--  blblkj (lskdfj) " ,null,null);
        
        EditN3Generator en3g = new EditN3Generator((EditConfiguration) null);
        String result = en3g.subInLiterals(var, literal, target);
        Assert.assertNotNull( result );    
        Assert.assertEquals("Fake n3 \"should not a regex group --> ?2 <--  blblkj (lskdfj) \" ." , result);
    }
    
    @Test
    public void testConflictingVarNames(){
      Map<String,String> varToExisting= new HashMap<String,String>();      
      varToExisting.put("bob", "http://uri.edu#BobTheElder");
      varToExisting.put("bobJr", "http://uri.edu#BobTheSon");
             
      String target =   "SELECT ?cat WHERE{ ?bobJr <http://uri.edu#hasCat> ?cat }" ;
      List<String> targets = new ArrayList<String>();
      targets.add(target);            
      
      List<String> out = en3g.subInUris(varToExisting, targets);
      Assert.assertNotNull(out);
      Assert.assertNotNull( out.get(0) );      
      String expected = "SELECT ?cat WHERE{ <http://uri.edu#BobTheSon>  <http://uri.edu#hasCat> ?cat }";
      Assert.assertEquals(expected, out.get(0) );
      
      //force a non match on a initial-partial var name
      varToExisting= new HashMap<String,String>();      
      varToExisting.put("bob", "http://uri.edu#BobTheElder");      
             
      target =   "SELECT ?cat WHERE{ ?bobJr <http://uri.edu#hasCat> ?cat }" ;
      targets = new ArrayList<String>();
      targets.add(target);            
      
      out = en3g.subInUris(varToExisting, targets);
      Assert.assertNotNull(out);
      Assert.assertNotNull( out.get(0) );      
      expected = target;
      Assert.assertEquals(expected, out.get(0) );
      
    }

}
