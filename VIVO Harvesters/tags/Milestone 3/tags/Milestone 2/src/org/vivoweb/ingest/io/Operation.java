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
package org.vivoweb.ingest.io;

import java.io.Serializable;
import java.util.Map;

public class Operation implements Serializable {
	
	private static final long serialVersionUID = -6220354331849779748L;
	private Task task;
	private Map<String, String> parameters;
	
	public Operation(Task doTask, Map<String, String> params) throws IllegalStateException, IllegalArgumentException {
		this.setTask(doTask);
		this.setParameters(params);
	}
	
	private void setTask(Task newTask) {
		this.task = newTask;
	}
	
	public Task getTask() {
		return this.task;
	}
	
	private void setParameters(Map<String, String> params) throws IllegalStateException, IllegalArgumentException {
		if(this.task == null) {
			throw new IllegalStateException("Cannot initialize parameters for a null operation task!");
		}
		for(String s : params.keySet()) {
			if(s == "") {
				throw new IllegalArgumentException("Cannot initialize unnamed parameter");
			}
		}
		this.parameters = params;
	}
	
	public Map<String, String> getParameters() {
		return this.parameters;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getTask());
		sb.append(" -");
		for(String key : this.getParameters().keySet()) {
			sb.append(" ");
			sb.append(key);
			sb.append(":'");
			sb.append(this.getParameters().get(key));
			sb.append("'");
		}
		return sb.toString();
	}
	
}
