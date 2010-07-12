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

import org.dom4j.Element;

import edu.cornell.mannlib.vitro.beans.Ents2Ents;
import edu.cornell.mannlib.vitro.webservices.xml.convert.Ents2EntsXml;
import junit.framework.TestCase;

public class Ents2EntsTest extends TestCase {
    
	public void testNull(){
		Ents2EntsXml xmler = new Ents2EntsXml();
		assertEquals(xmler.toXmlElem(null), null);
	}
	
	public Ents2Ents makeObj(){
		Ents2Ents obj = new Ents2Ents();
		obj.setEnts2entsId(12);
		obj.setDomainId(223);
		obj.setRangeId(2255);
		obj.setPropertyId(605);
		obj.setQualifier("This is the super(){}[]';\":.,><");		
		return obj;
	}

	public void testAttributes(){
		Ents2Ents ent = makeObj();
		Ents2EntsXml xmler = new Ents2EntsXml();
		Element elem = xmler.toXmlElem(ent);
		assertEquals(elem.valueOf("@ents2entsId"),
				Integer.toString(ent.getEnts2entsId()));
		assertEquals(elem.valueOf("@domainId"),
				Integer.toString(ent.getDomainId()));
		assertEquals(elem.valueOf("@rangeId"),
				Integer.toString(ent.getRangeId()));
		assertEquals(elem.valueOf("@etypes2RelationsId"),
				Integer.toString(ent.getPropertyId()));
		assertEquals(elem.valueOf("@qualifier"),ent.getQualifier());		
	}
	
    public static void main(String[] args) {
        junit.textui.TestRunner.run( Ents2EntsTest.class );
    }

}
