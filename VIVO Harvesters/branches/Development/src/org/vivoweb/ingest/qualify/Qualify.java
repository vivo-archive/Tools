/*******************************************************************************
 * Copyright (c) 2010 Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the new BSD license
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/bsd-license.html
 * 
 * Contributors:
 *     Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams - initial API and implementation
 ******************************************************************************/
package org.vivoweb.ingest.qualify;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public abstract class Qualify {
	private static Log log = LogFactory.getLog(Qualify.class);
	
	private Model model;
	
	/**
	 * @param model the model to set
	 */
	protected void setModel(Model model) {
		this.model = model;
	}
	
	/**
	 * @return the model
	 */
	protected Model getModel() {
		return this.model;
	}
	
	/**
	 * @param dataType
	 * @param oldValue
	 * @param newValue
	 * @param regex 
	 */
	public abstract void replace(String dataType, String matchValue, String newValue, boolean regex);
	
	/**
	 * @param args commandline arguments
	 */
	public static void main(String[] args) {
		
	}
	
}
