package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

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
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.beans.IndividualImpl;

public class RdfLiteralHashTest {

    final String TEST_VALUE ="this is a test literal string";
    final String TEST_DATA_PROP_URI ="http://this.is.a.test.uri.com/1999/02/blec-ns#test2332";
    final String TEST_INDIVIDUAL_URI ="http://this.is.a.testUri.com/1999/02/bleck-ns#INDIVIDUAL787878";
    final String TEST_DATA_TYPE_URI ="http://this.is.a.uri.com/TEST/DATA/TYPE#e8";
    final String TEST_LANG = "ENG";

    @Test
    public void testEdBackground(){
        String value = "[CELE97] Waldemar Celes and Jonathan Corson-Rikert. &quot;Act: An Easy-to-use and Dynamically Extensible 3D Graphics Library&quot; in Proceedings, Brazilian Symposium on Computer Graphics and Image Processing, Campos do Jordao, SP -Brazil, October, 1997.";
        String propUri = "http://vivo.library.cornell.edu/ns/0.1#publications";
        String subject = "http://vivo.library.cornell.edu/ns/0.1#individual22972";
        String datatypeUri= null;
        String language = null;

        DataPropertyStatement stmt = new DataPropertyStatementImpl();
        stmt.setIndividualURI(subject);
        stmt.setData(value);
        stmt.setDatapropURI(propUri);
        stmt.setDatatypeURI(datatypeUri);
        stmt.setLanguage(language);

        int hash = RdfLiteralHash.makeRdfLiteralHash( stmt);
        Assert.assertTrue(hash != 0);
        Assert.assertEquals(1646037091 , hash);
    }

    @Test
    public void testMakeRdfLiteralHash() {
        DataPropertyStatement stmt = new DataPropertyStatementImpl();

        stmt.setData(TEST_VALUE);
        stmt.setDatapropURI(TEST_DATA_PROP_URI);
        stmt.setIndividualURI(TEST_INDIVIDUAL_URI);
        int hash = RdfLiteralHash.makeRdfLiteralHash(stmt);
        Assert.assertTrue(hash != 0);

        stmt = new DataPropertyStatementImpl();
        stmt.setData(TEST_VALUE);
        stmt.setDatapropURI(TEST_DATA_PROP_URI);
        stmt.setIndividualURI(TEST_INDIVIDUAL_URI);
        stmt.setDatatypeURI(TEST_DATA_TYPE_URI);
        hash = RdfLiteralHash.makeRdfLiteralHash(stmt);
        Assert.assertTrue(hash != 0);

        stmt = new DataPropertyStatementImpl();
        stmt.setData(TEST_VALUE);
        stmt.setDatapropURI(TEST_DATA_PROP_URI);
        stmt.setIndividualURI(TEST_INDIVIDUAL_URI);
        stmt.setLanguage(TEST_LANG);
        hash = RdfLiteralHash.makeRdfLiteralHash(stmt);
        Assert.assertTrue(hash != 0);
    }

    @Test
    public void testDoesStmtMatchHash() {
        DataPropertyStatement stmtA = new DataPropertyStatementImpl();
        DataPropertyStatement stmtB = new DataPropertyStatementImpl();
        int expectedHash =  0;


        stmtA.setData(TEST_VALUE);
        stmtA.setDatapropURI(TEST_DATA_PROP_URI);
        stmtA.setIndividualURI(TEST_INDIVIDUAL_URI);
        expectedHash = RdfLiteralHash.makeRdfLiteralHash(stmtA);
        stmtB.setData(TEST_VALUE);
        stmtB.setDatapropURI(TEST_DATA_PROP_URI);
        stmtB.setIndividualURI(TEST_INDIVIDUAL_URI);
        Assert.assertTrue(expectedHash == RdfLiteralHash.makeRdfLiteralHash(stmtB) );
        Assert.assertTrue( RdfLiteralHash.doesStmtMatchHash(stmtB, expectedHash));


        stmtA = new DataPropertyStatementImpl();
        stmtA.setData(TEST_VALUE);
        stmtA.setDatapropURI(TEST_DATA_PROP_URI);
        stmtA.setIndividualURI(TEST_INDIVIDUAL_URI);
        stmtA.setDatatypeURI(TEST_DATA_TYPE_URI);
        expectedHash = RdfLiteralHash.makeRdfLiteralHash(stmtA);
        stmtB = new DataPropertyStatementImpl();
        stmtB.setData(TEST_VALUE);
        stmtB.setDatapropURI(TEST_DATA_PROP_URI);
        stmtB.setIndividualURI(TEST_INDIVIDUAL_URI);
        stmtB.setDatatypeURI(TEST_DATA_TYPE_URI);
        Assert.assertTrue( expectedHash == RdfLiteralHash.makeRdfLiteralHash(stmtB) );
        Assert.assertTrue( RdfLiteralHash.doesStmtMatchHash(stmtB, expectedHash));

        stmtA = new DataPropertyStatementImpl();
        stmtA.setData(TEST_VALUE);
        stmtA.setDatapropURI(TEST_DATA_PROP_URI);
        stmtA.setIndividualURI(TEST_INDIVIDUAL_URI);
        stmtA.setLanguage(TEST_LANG);
        expectedHash = RdfLiteralHash.makeRdfLiteralHash(stmtA);
        stmtB = new DataPropertyStatementImpl();
        stmtB.setData(TEST_VALUE);
        stmtB.setDatapropURI(TEST_DATA_PROP_URI);
        stmtB.setIndividualURI(TEST_INDIVIDUAL_URI);
        stmtB.setLanguage(TEST_LANG);
        Assert.assertTrue( expectedHash == RdfLiteralHash.makeRdfLiteralHash(stmtB) );
        Assert.assertTrue( RdfLiteralHash.doesStmtMatchHash(stmtB, expectedHash));

        Assert.assertTrue( ! RdfLiteralHash.doesStmtMatchHash(null, expectedHash) );
    }

    @Test
    public void testGetDataPropertyStmtByHash() {
        DataPropertyStatement stmtA = new DataPropertyStatementImpl();
        IndividualImpl ind = new IndividualImpl();
        List<DataPropertyStatement> stmts = new ArrayList<DataPropertyStatement>();

        int expectedHash =  0;

        //test to see if the same subURI, predURI and Value can be distinguished by LANG/datatype
        stmtA.setData(TEST_VALUE);
        stmtA.setDatapropURI(TEST_DATA_PROP_URI);
        stmtA.setIndividualURI(TEST_INDIVIDUAL_URI);
        stmts.add(stmtA);
        int expectedHashForSimpleStmt = RdfLiteralHash.makeRdfLiteralHash(stmtA);

        stmtA = new DataPropertyStatementImpl();
        stmtA.setData(TEST_VALUE );
        stmtA.setDatapropURI(TEST_DATA_PROP_URI);
        stmtA.setIndividualURI(TEST_INDIVIDUAL_URI);
        stmtA.setDatatypeURI(TEST_DATA_TYPE_URI);
        int expectedHashForDatatypeStmt = RdfLiteralHash.makeRdfLiteralHash(stmtA);
        stmts.add(stmtA);

        stmtA = new DataPropertyStatementImpl();
        stmtA.setData(TEST_VALUE );
        stmtA.setDatapropURI(TEST_DATA_PROP_URI);
        stmtA.setIndividualURI(TEST_INDIVIDUAL_URI);
        stmtA.setLanguage(TEST_LANG);
        int expectedHashForLangStmt = RdfLiteralHash.makeRdfLiteralHash(stmtA);
        stmts.add(stmtA);

        ind.setDataPropertyStatements(stmts);

        DataPropertyStatement stmt = RdfLiteralHash.getDataPropertyStmtByHash(ind, expectedHashForLangStmt);
        Assert.assertNotNull(stmt);
        Assert.assertEquals(TEST_DATA_PROP_URI, stmt.getDatapropURI() );
        Assert.assertEquals(TEST_INDIVIDUAL_URI, stmt.getIndividualURI() );
        Assert.assertEquals(TEST_LANG, stmt.getLanguage() );
        Assert.assertEquals(TEST_VALUE, stmt.getData() );
        Assert.assertNull(stmt.getDatatypeURI());

        stmt = RdfLiteralHash.getDataPropertyStmtByHash(ind, expectedHashForSimpleStmt);
        Assert.assertNotNull(stmt);
        Assert.assertEquals(TEST_DATA_PROP_URI, stmt.getDatapropURI() );
        Assert.assertEquals(TEST_INDIVIDUAL_URI, stmt.getIndividualURI() );
        Assert.assertEquals(TEST_VALUE, stmt.getData() );
        Assert.assertNull(stmt.getDatatypeURI());
        Assert.assertNull(stmt.getLanguage());

        stmt = RdfLiteralHash.getDataPropertyStmtByHash(ind, expectedHashForDatatypeStmt);
        Assert.assertNotNull(stmt);
        Assert.assertEquals(TEST_DATA_PROP_URI, stmt.getDatapropURI() );
        Assert.assertEquals(TEST_INDIVIDUAL_URI, stmt.getIndividualURI() );
        Assert.assertEquals(TEST_VALUE, stmt.getData() );
        Assert.assertEquals(TEST_DATA_TYPE_URI, stmt.getDatatypeURI() );
        Assert.assertNull(stmt.getLanguage());


        stmt = RdfLiteralHash.getDataPropertyStmtByHash(ind, 111111);
        Assert.assertNull(stmt);

    }

}
