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
import org.dom4j.io.HTMLWriter;

import junit.framework.TestCase;
import edu.cornell.mannlib.vitro.beans.*;
import edu.cornell.mannlib.vitro.dao.db.*;
import edu.cornell.mannlib.vitro.webservices.xml.convert.TabXml;

public class TabXmlTest extends TestCase {
    
    private void doTabXmlTest(int tabId){
//        Tab tab = TabDao.getTab(tabId, 1,"now()",10,60,0,null,null,null);
//        Element elem = TabXml.toXmlElem(tab, 10);
//        assertTrue( elem != null );
//        String resultXml = null;
//        try{
//            resultXml = HTMLWriter.prettyPrintXHTML( elem.asXML() );
//        }catch(Exception ex){
//            fail("exception: " + ex.toString());
//        }
//        assertTrue(resultXml != null && resultXml.length() > 0); 
        
    }
    
    public void testTabs(){
        doTabXmlTest(63); 
        doTabXmlTest(18);
        doTabXmlTest(19);
        //bad xhtml doTabXmlTest(23);
        doTabXmlTest(25);
        doTabXmlTest(35);
        doTabXmlTest(64);
        doTabXmlTest(42);        
    }
    
    public static void main(String[] args){
        junit.textui.TestRunner.run( TabXmlTest.class );
    }
}
