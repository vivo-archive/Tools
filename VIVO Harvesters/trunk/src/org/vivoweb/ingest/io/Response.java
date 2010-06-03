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

public class Response implements Serializable {
	
	private static final long serialVersionUID = -4696786343365828290L;
	private Feedback feedback;
	private Map<String, Object> data;
	
	public Response(Feedback sendFeedback, Map<String, Object> sendData) {
		this.setFeedback(sendFeedback);
		this.setData(sendData);
	}
	
	public void setFeedback(Feedback sendFeedback) {
		this.feedback = sendFeedback;
	}
	
	public Feedback getFeedback() {
		return this.feedback;
	}
	
	public void setData(Map<String, Object> newData) {
		this.data = newData;
	}
	
	public Map<String, Object> getData() {
		return this.data;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getFeedback());
		sb.append(" -");
		for(String key : this.getData().keySet()) {
			sb.append(" ");
			sb.append(key);
			sb.append(":'");
			sb.append(this.getData().get(key));
			sb.append("'");
		}
		return sb.toString();
	}
	
}
