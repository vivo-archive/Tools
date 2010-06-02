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

package edu.cornell.mannlib.vitro.webservices.xml.convert.test;

import junit.framework.TestCase;
import edu.cornell.mannlib.vitro.webservices.xml.convert.*;
import edu.cornell.mannlib.vitro.beans.*;

public class PropertyXmlTest extends TestCase {
    public void testXml1(){
        String expected =
            "<Property id=\"12\" parentId=\"1\" minCardinality=\"0\" "+
            "maxCardinality=\"23\" domainEtypeId=\"2323\" "+
            "rangeEtypeId=\"2443\"/>";
        
        Property prop = new Property();
        prop.setId(12);
        prop.setParentId(1);
        prop.setMinCardinality(0);
        prop.setMaxCardinality(23);
//        prop.setDomainEtypeId(2323);
//        prop.setRangeEtypeId(2443);
        PropertyXml xmler = new PropertyXml();        
        assertEquals(expected,xmler.toXmlStr(prop));
    }
    
    public void testXml2(){
        String expected =
            "<Property id=\"12\" parentId=\"1\" minCardinality=\"0\" "+
            "maxCardinality=\"23\" domainEtypeId=\"2323\" "+
            "rangeEtypeId=\"2443\"/>";
        
        Property prop = new Property();
        prop.setId(12);
        prop.setParentId(1);
        prop.setMinCardinality(0);
        prop.setMaxCardinality(23);
//        prop.setDomainEtypeId(2323);
//        prop.setRangeEtypeId(2443);
        PropertyXml xmler = new PropertyXml();        
        assertEquals(expected,xmler.toXmlStr(prop));
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run( PropertyXmlTest.class );
    }

}
