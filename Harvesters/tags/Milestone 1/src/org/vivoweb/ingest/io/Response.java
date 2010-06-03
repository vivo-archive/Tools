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
