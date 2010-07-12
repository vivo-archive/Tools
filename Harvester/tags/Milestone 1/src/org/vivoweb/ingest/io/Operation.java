/*******************************************************************************
 * Copyright (c) 2010 Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams
 * 
 * This file is part of VIVO.
 * 
 * VIVO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VIVO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VIVO.  If not, see <http://www.gnu.org/licenses/gpl-3.0.txt>.
 * 
 * Contributors:
 *     Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams - initial implementation
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
