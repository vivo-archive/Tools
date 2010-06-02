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

import org.dom4j.Element;
import org.dom4j.io.HTMLWriter;

import edu.cornell.mannlib.vitro.beans.Entity;
import edu.cornell.mannlib.vitro.webapp.dao.VitroFacade;
import edu.cornell.mannlib.vitro.webservices.xml.convert.EntityXml;

public class EntityXmlTest extends TestCase {
	
    public void testEntityProperties(){
    	String resultXml = null;
    	VitroFacade facade = new VitroFacade();
    	Entity ent = facade.entityById(1);
    	ent = facade.fillExistingEnts2Ents(ent);    	
//        assertTrue(ent.getDomainEnts2ents() != null && 
//        		ent.getDomainEnts2ents().size() > 0);
//        assertTrue(ent.getRangeEnts2ents() != null && 
//        		ent.getRangeEnts2ents().size() > 0);
        Element elem = EntityXml.toXmlElem(ent,EntityXml.FULL);
        assertTrue( elem != null );
        try{
        	resultXml = HTMLWriter.prettyPrintXHTML( elem.asXML() );
        }catch(Exception ex){
        	fail("exception: " + ex.toString());
        }
        assertTrue(resultXml != null && resultXml.length() > 0); 
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run( EntityXmlTest.class );
    }

}
//
//<entity>
//  <id>1</id>
//  <name>Albert R. Mann Library</name>
//  <moniker>library</moniker>
//  <url>
//    <a href="http://www.mannlib.cornell.edu">Mann Library web page</a>
//  </url>
//  <domainProperties>
//    <Ents2Ents ents2entsId="8949" etypes2RelationsId="562" rangeId="9036" domainId="1">
//      <domain>
//        <entity>
//          <id>1</id>
//          <name>Albert R. Mann Library</name>
//        </entity>
//      </domain>
//      <range>
//        <entity>
//          <id>9036</id>
//          <name>PRESERVING THE HISTORY OF UNITED STATES AGRICULTURE AND RURAL LIFE: STATE AND LOCAL LITERATURE, 1820-1945, PHASE IV</name>
//        </entity>
//      </range>
//      <property>
//        <Property id="562" parentId="-1" minCardinality="-1" maxCardinality="-1" domainEtypeId="1" rangeEtypeId="117">
//          <domainPublic>administers sponsored project</domainPublic>
//          <rangePublic>administered by</rangePublic>
//          <domainEtype>
//            <VClass id="1">
//              <typename>CUL unit library</typename>
//            </VClass>
//          </domainEtype>
//          <rangeEtype>
//            <VClass id="117">
//              <typename>research grant</typename>
//            </VClass>
//          </rangeEtype>
//        </Property>
//      </property>
//    </Ents2Ents>
//    <Ents2Ents ents2entsId="9866" etypes2RelationsId="562" rangeId="9390" domainId="1">
//      <domain>
//        <entity>
//          <id>1</id>
//          <name>Albert R. Mann Library</name>
//        </entity>
//      </domain>
//      <range>
//        <entity>
//          <id>9390</id>
//          <name>AGORA: ACCESS TO GLOBAL ONLINE RESEARCH IN AGRICULTURE</name>
//        </entity>
//      </range>
//      <property>
//        <Property id="562" parentId="-1" minCardinality="-1" maxCardinality="-1" domainEtypeId="1" rangeEtypeId="117">
//          <domainPublic>administers sponsored project</domainPublic>
//          <rangePublic>administered by</rangePublic>
//          <domainEtype>
//            <VClass id="1">
//              <typename>CUL unit library</typename>
//            </VClass>
//          </domainEtype>
//          <rangeEtype>
//            <VClass id="117">
//              <typename>research grant</typename>
//            </VClass>
//          </rangeEtype>
//        </Property>
//      </property>
//    </Ents2Ents>
//    <Ents2Ents ents2entsId="10078" etypes2RelationsId="562" rangeId="9461" domainId="1">
//      <domain>
//        <entity>
//          <id>1</id>
//          <name>Albert R. Mann Library</name>
//        </entity>
//      </domain>
//      <range>
//        <entity>
//          <id>9461</id>
//          <name>COORDINATED COLLECTION DEVELOPMENT AID</name>
//        </entity>
//      </range>
//      <property>
//        <Property id="562" parentId="-1" minCardinality="-1" maxCardinality="-1" domainEtypeId="1" rangeEtypeId="117">
//          <domainPublic>administers sponsored project</domainPublic>
//          <rangePublic>administered by</rangePublic>
//          <domainEtype>
//            <VClass id="1">
//              <typename>CUL unit library</typename>
//            </VClass>
//          </domainEtype>
//          <rangeEtype>
//            <VClass id="117">
//              <typename>research grant</typename>
//            </VClass>
//          </rangeEtype>
//        </Property>
//      </property>
//    </Ents2Ents>
//    <Ents2Ents ents2entsId="10187" etypes2RelationsId="562" rangeId="9500" domainId="1">
//      <domain>
//        <entity>
//          <id>1</id>
//          <name>Albert R. Mann Library</name>
//        </entity>
//      </domain>
//      <range>
//        <entity>
//          <id>9500</id>
//          <name>PRESERVING THE HISTORY OF UNITED STATES AGRICULTURE AND RURAL LIFE: STATE AND LOCAL LITERATURE, 1820-1945: PHASE V, 2004-2006</name>
//        </entity>
//      </range>
//      <property>
//        <Property id="562" parentId="-1" minCardinality="-1" maxCardinality="-1" domainEtypeId="1" rangeEtypeId="117">
//          <domainPublic>administers sponsored project</domainPublic>
//          <rangePublic>administered by</rangePublic>
//          <domainEtype>
//            <VClass id="1">
//              <typename>CUL unit library</typename>
//            </VClass>
//          </domainEtype>
//          <rangeEtype>
//            <VClass id="117">
//              <typename>research grant</typename>
//            </VClass>
//          </rangeEtype>
//        </Property>
//      </property>
//    </Ents2Ents>
//    <Ents2Ents ents2entsId="10369" etypes2RelationsId="562" rangeId="9561" domainId="1">
//      <domain>
//        <entity>
//          <id>1</id>
//          <name>Albert R. Mann Library</name>
//        </entity>
//      </domain>
//      <range>
//        <entity>
//          <id>9561</id>
//          <name>TEEAL(THE ESSENTIAL ELECTRONIC AGRICULTURAL LIBRARY)</name>
//        </entity>
//      </range>
//      <property>
//        <Property id="562" parentId="-1" minCardinality="-1" maxCardinality="-1" domainEtypeId="1" rangeEtypeId="117">
//          <domainPublic>administers sponsored project</domainPublic>
//          <rangePublic>administered by</rangePublic>
//          <domainEtype>
//            <VClass id="1">
//              <typename>CUL unit library</typename>
//            </VClass>
//          </domainEtype>
//          <rangeEtype>
//            <VClass id="117">
//              <typename>research grant</typename>
//            </VClass>
//          </rangeEtype>
//        </Property>
//      </property>
//    </Ents2Ents>
//    <Ents2Ents ents2entsId="11014" etypes2RelationsId="562" rangeId="9774" domainId="1">
//      <domain>
//        <entity>
//          <id>1</id>
//          <name>Albert R. Mann Library</name>
//        </entity>
//      </domain>
//      <range>
//        <entity>
//          <id>9774</id>
//          <name>PLANNING INFORMATION INFRASTRUCTURE THROUGH A NEW LIBRARY-RESEARCH PARTNERSHIP</name>
//        </entity>
//      </range>
//      <property>
//        <Property id="562" parentId="-1" minCardinality="-1" maxCardinality="-1" domainEtypeId="1" rangeEtypeId="117">
//          <domainPublic>administers sponsored project</domainPublic>
//          <rangePublic>administered by</rangePublic>
//          <domainEtype>
//            <VClass id="1">
//              <typename>CUL unit library</typename>
//            </VClass>
//          </domainEtype>
//          <rangeEtype>
//            <VClass id="117">
//              <typename>research grant</typename>
//            </VClass>
//          </rangeEtype>
//        </Property>
//      </property>
//    </Ents2Ents>
//    <Ents2Ents ents2entsId="11174" etypes2RelationsId="562" rangeId="9833" domainId="1">
//      <domain>
//        <entity>
//          <id>1</id>
//          <name>Albert R. Mann Library</name>
//        </entity>
//      </domain>
//      <range>
//        <entity>
//          <id>9833</id>
//          <name>DISSEMINATION AND PRESERVAITON OF DIGITAL AGRICULTURAL ECONOMIC DATA ON THE INTERNET</name>
//        </entity>
//      </range>
//      <property>
//        <Property id="562" parentId="-1" minCardinality="-1" maxCardinality="-1" domainEtypeId="1" rangeEtypeId="117">
//          <domainPublic>administers sponsored project</domainPublic>
//          <rangePublic>administered by</rangePublic>
//          <domainEtype>
//            <VClass id="1">
//              <typename>CUL unit library</typename>
//            </VClass>
//          </domainEtype>
//          <rangeEtype>
//            <VClass id="117">
//              <typename>research grant</typename>
//            </VClass>
//          </rangeEtype>
//        </Property>
//      </property>
//    </Ents2Ents>
//    <Ents2Ents ents2entsId="11212" etypes2RelationsId="562" rangeId="9848" domainId="1">
//      <domain>
//        <entity>
//          <id>1</id>
//          <name>Albert R. Mann Library</name>
//        </entity>
//      </domain>
//      <range>
//        <entity>
//          <id>9848</id>
//          <name>BUILDING A SERVICE ORIENTED ARCHITECTURE FOR AGNIC</name>
//        </entity>
//      </range>
//      <property>
//        <Property id="562" parentId="-1" minCardinality="-1" maxCardinality="-1" domainEtypeId="1" rangeEtypeId="117">
//          <domainPublic>administers sponsored project</domainPublic>
//          <rangePublic>administered by</rangePublic>
//          <domainEtype>
//            <VClass id="1">
//              <typename>CUL unit library</typename>
//            </VClass>
//          </domainEtype>
//          <rangeEtype>
//            <VClass id="117">
//              <typename>research grant</typename>
//            </VClass>
//          </rangeEtype>
//        </Property>
//      </property>
//    </Ents2Ents>
//    <Ents2Ents ents2entsId="11245" etypes2RelationsId="562" rangeId="9859" domainId="1">
//      <domain>
//        <entity>
//          <id>1</id>
//          <name>Albert R. Mann Library</name>
//        </entity>
//      </domain>
//      <range>
//        <entity>
//          <id>9859</id>
//          <name>THE ESSENTIAL ELECTRONIC AGRICULTURAL LIBRARY</name>
//        </entity>
//      </range>
//      <property>
//        <Property id="562" parentId="-1" minCardinality="-1" maxCardinality="-1" domainEtypeId="1" rangeEtypeId="117">
//          <domainPublic>administers sponsored project</domainPublic>
//          <rangePublic>administered by</rangePublic>
//          <domainEtype>
//            <VClass id="1">
//              <typename>CUL unit library</typename>
//            </VClass>
//          </domainEtype>
//          <rangeEtype>
//            <VClass id="117">
//              <typename>research grant</typename>
//            </VClass>
//          </rangeEtype>
//        </Property>
//      </property>
//    </Ents2Ents>
//    <Ents2Ents ents2entsId="15023" etypes2RelationsId="562" rangeId="11579" domainId="1">
//      <domain>
//        <entity>
//          <id>1</id>
//          <name>Albert R. Mann Library</name>
//        </entity>
//      </domain>
//      <range>
//        <entity>
//          <id>11579</id>
//          <name>COORDINATED COLLECTION DEVELOPMENT AID</name>
//        </entity>
//      </range>
//      <property>
//        <Property id="562" parentId="-1" minCardinality="-1" maxCardinality="-1" domainEtypeId="1" rangeEtypeId="117">
//          <domainPublic>administers sponsored project</domainPublic>
//          <rangePublic>administered by</rangePublic>
//          <domainEtype>
//            <VClass ...