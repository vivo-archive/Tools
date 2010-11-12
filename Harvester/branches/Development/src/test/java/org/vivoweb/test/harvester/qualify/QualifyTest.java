/*******************************************************************************
 * Copyright (c) 2010 Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams. All rights reserved.
 * This program and the accompanying materials are made available under the terms of the new BSD license which
 * accompanies this distribution, and is available at http://www.opensource.org/licenses/bsd-license.html Contributors:
 * Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams - initial API and implementation
 ******************************************************************************/
package org.vivoweb.test.harvester.qualify;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivoweb.harvester.qualify.Qualify;
import org.vivoweb.harvester.util.InitLog;
import org.vivoweb.harvester.util.repo.JenaConnect;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 * @author Stephen Williams (swilliams@ctrip.ufl.edu)
 */
public class QualifyTest extends TestCase {
	/**
	 * SLF4J Logger
	 */
	private static Logger log = LoggerFactory.getLogger(QualifyTest.class);
	/** */
	private static final String dbClass = "org.h2.Driver";
	/** */
	private static final String dbType = "HSQLDB";
	/** */
	private static final String dbUrl = "jdbc:h2:mem:TestSPARQLQualifyModel;MODE=HSQLDB";
	/** */
	private static final String dbUser = "sa";
	/** */
	private static final String dbPass = "";
	/** */
	private JenaConnect jena;
	/** */
	private Property label;
	
	@Override
	public void setUp() throws Exception {
		InitLog.initLogger();
		this.jena = new JenaConnect(dbUrl, dbUser, dbPass, dbType, dbClass);
		this.label = this.jena.getJenaModel().createProperty("http://www.w3.org/2000/01/rdf-schema#label");
	}
	
	@Override
	public void tearDown() throws Exception {
		this.jena.close();
		this.jena = null;
		System.gc();
	}
	
	/**
	 * Test method for {@link org.vivoweb.harvester.qualify.Qualify#main(java.lang.String[]) main(String... args)} using
	 * regex replace.
	 */
	public void testRegexReplace() {
		try {
			Resource res1 = this.jena.getJenaModel().createResource("http://ingest.vivoweb.org/testSPARQLQualify/item#1");
			Resource res2 = this.jena.getJenaModel().createResource("http://ingest.vivoweb.org/testSPARQLQualify/item#2");
			Resource res3 = this.jena.getJenaModel().createResource("http://ingest.vivoweb.org/testSPARQLQualify/item#3");
			this.jena.getJenaModel().add(res1, this.label, "IATRR");
			this.jena.getJenaModel().add(res2, this.label, "wooIATRRblah");
			this.jena.getJenaModel().add(res3, this.label, "I A T R R");
			String expectedValue = "I Am Testing Regex Replace";
			// call qualify
			new Qualify(this.jena, this.label.getURI(), ".*?IATRR.*?", expectedValue, null, true).executeTask();
			assertEquals(expectedValue, this.jena.getJenaModel().getProperty(res1, this.label).getString());
			assertEquals(expectedValue, this.jena.getJenaModel().getProperty(res2, this.label).getString());
			assertFalse(this.jena.getJenaModel().getProperty(res3, this.label).getString().equals(expectedValue));
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test method for {@link org.vivoweb.harvester.qualify.Qualify#main(java.lang.String[]) main(String... args)} using
	 * string replace.
	 */
	public void testStringReplace() {
		try {
			Resource res1 = this.jena.getJenaModel().createResource("http://ingest.vivoweb.org/testSPARQLQualify/item#1");
			Resource res2 = this.jena.getJenaModel().createResource("http://ingest.vivoweb.org/testSPARQLQualify/item#2");
			Resource res3 = this.jena.getJenaModel().createResource("http://ingest.vivoweb.org/testSPARQLQualify/item#3");
			this.jena.getJenaModel().add(res1, this.label, "IATTR");
			this.jena.getJenaModel().add(res2, this.label, "wooIATTRblah");
			this.jena.getJenaModel().add(res3, this.label, "I A T T R");
			String expectedValue = "I Am Testing Test Replace";
			// call qualify
			new Qualify(this.jena, this.label.getURI(), "IATTR", expectedValue, null, false).executeTask();
			assertEquals(expectedValue, this.jena.getJenaModel().getProperty(res1, this.label).getString());
			assertFalse(this.jena.getJenaModel().getProperty(res2, this.label).getString().equals(expectedValue));
			assertFalse(this.jena.getJenaModel().getProperty(res3, this.label).getString().equals(expectedValue));
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
}