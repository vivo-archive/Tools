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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import javax.net.ssl.SSLSocketFactory;

public class SocketClient implements Runnable {
	
	private InetAddress host;
	private int connectionPort = -1;
	private int streamPort = -1;
	private Operation op;
	private int debugLevel;
	
	public SocketClient(String hostPath, int connPort, int dataStreamPort, Operation operation) throws IllegalArgumentException,
			UnknownHostException {
		this.setHost(hostPath);
		this.setConnectionPort(connPort);
		this.setStreamPort(dataStreamPort);
		this.setOperation(operation);
		this.setDebugLevel(3);
	}
	
	public String getHost() {
		return this.host.getHostName();
	}
	
	public void setHost(String newHost) throws UnknownHostException {
		this.host = InetAddress.getByName(newHost);
	}
	
	public int getConnectionPort() {
		return this.connectionPort;
	}
	
	public void setConnectionPort(int newConnPort) throws IllegalArgumentException {
		if(this.streamPort != -1 && newConnPort == this.streamPort) {
			throw new IllegalArgumentException("Connection port must not be the same as streaming port: " + newConnPort);
		}
		if(newConnPort < 10000 || newConnPort > 99999) {
			throw new IllegalArgumentException("Connection port must be in the range 10000-99999: " + newConnPort);
		}
		this.connectionPort = newConnPort;
	}
	
	public int getStreamPort() {
		return this.streamPort;
	}
	
	public void setStreamPort(int newStreamPort) {
		if(this.connectionPort != -1 && newStreamPort == this.connectionPort) {
			throw new IllegalArgumentException("Streaming port must not be the same as connection port: " + newStreamPort);
		}
		if(newStreamPort < 10000 || newStreamPort > 99999) {
			throw new IllegalArgumentException("Streaming port must be in the range 10000-99999: " + newStreamPort);
		}
		this.streamPort = newStreamPort;
	}
	
	public void setOperation(Operation newOp) {
		this.op = newOp;
	}
	
	public Operation getOperation() {
		return this.op;
	}
	
	public int getDebugLevel() {
		return this.debugLevel;
	}
	
	public void setDebugLevel(int debugLev) {
		this.debugLevel = (debugLev < -1)
				? -1
				: ((debugLev > 3)
						? 3
						: debugLev);
	}
	
	public void execute(List<OutputStream> outs) throws UnknownHostException, IOException, ClassNotFoundException, IllegalStateException,
			IllegalArgumentException {
		if(this.getOperation().getTask() == Task.OpenStream) {
			// error?: should never create a new socket client for this?
		} else {
			// make operation/response connection
			Socket connection = new Socket(this.getHost(), this.getConnectionPort());
			// make oos
			ObjectOutputStream oos = this.getOOS(connection);
			// make ois
			ObjectInputStream ois = this.getOIS(connection);
			// open stream connection
			Socket streamConn;
			if((streamConn = this.openStream(oos, ois)) != null) {
				// write op and get response
				Response resp = this.writeOp(oos, ois, this.op);
				// handle response
				if(resp.getFeedback() == Feedback.Success) {
					System.out.println("" + resp);
				} else if(resp.getFeedback() == Feedback.Processing) {
					InputStream in = streamConn.getInputStream();
					byte[] buffer = new byte[1024];
					while((in.read(buffer)) != 0){
						for(OutputStream out : outs) {
							out.write(buffer);
						}
						buffer = new byte[1024];
					}
					
				} else {
					System.out.println("Error: " + resp);
				}
				streamConn.close();
			} else {
				debug(2, "execute", "stream connection failed");
			}
		}
	}
	
	private ObjectOutputStream getOOS(Socket connection) throws IOException {
		return new ObjectOutputStream(new BufferedOutputStream(connection.getOutputStream()));
	}
	
	private ObjectInputStream getOIS(Socket connection) throws IOException {
		return new ObjectInputStream(new BufferedInputStream(connection.getInputStream()));
	}
	
	private Response writeOp(ObjectOutputStream oos, ObjectInputStream ois, Operation sendOp) throws IOException,
			ClassNotFoundException {
		// push operation into oos
		oos.writeObject(sendOp);
		oos.flush();
		// wait for non-'processing' feedback response
		Response resp = null;
		boolean loop = true;
		while(loop) {
			// get response
			resp = (Response)ois.readObject();
			loop = (resp != null);
//			// if not 'processing', exit
//			loop = (resp != null && resp.getFeedback() == Feedback.Processing);
		}
		return resp;
	}
	
	private Socket openStream(ObjectOutputStream oos, ObjectInputStream ois) throws UnknownHostException, IOException,
			ClassNotFoundException, IllegalStateException, IllegalArgumentException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("port", "" + this.getStreamPort());
		Operation openStreamOp = new Operation(Task.OpenStream, params);
		Response resp = this.writeOp(oos, ois, openStreamOp);
		if(resp.getFeedback() == Feedback.Success) {
			Socket streamConn = new Socket(this.getHost(), this.getStreamPort());
			return streamConn;
		}
    debug(2, "openStream", "" + resp);
    return null;
	}
	
	public void run() {
		debug(2, "run", "start");
		debug(2, "run", "server is " + this.getHost() + " on connection port " + this.getConnectionPort()
				+ "streaming data on port " + this.getStreamPort());
		try {
			debug(2, "run>try", "start");
			debug(2, "run>try", "make connection");
			// Socket connection = SSLSocketFactory.getDefault().createSocket(host, port);
			Socket connection = new Socket(this.getHost(), this.getConnectionPort());
			debug(2, "run>try", "open connection oos");
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(connection.getOutputStream()));
			debug(0, "", "Operation: " + this.op);
			debug(2, "run>try", "write op to connection");
			oos.writeObject(this.op);
			debug(2, "run>try", "flush connection oos");
			oos.flush();
			debug(2, "run>try", "open connection ois");
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(connection.getInputStream()));
			debug(2, "run>try", "make datastream connection");
			Socket streamConn = new Socket(this.getHost(), this.getStreamPort());
			streamConn.getInputStream();
			streamConn.getOutputStream();
			boolean loop = true;
			while(loop) {
				debug(2, "run>try>loop", "start");
				Response resp = null;
				boolean subloop = true;
				while(subloop) {
					debug(2, "run>try>loop>subloop", "start");
					debug(2, "run>try>loop>subloop", "read connection ois response");
					resp = (Response)ois.readObject();
					if(resp != null) {
						debug(0, "", "Response: " + resp);
						subloop = false;
						loop = (resp.getFeedback() == Feedback.Processing);
					} else {
						debug(2, "run>try>loop>subloop", "null response");
					}
					debug(2, "run>try>loop>subloop", "end");
				}
				debug(2, "run>try>loop", "end");
			}
			debug(2, "run>try", "close all");
			connection.close();
			debug(2, "run>try", "end");
		} catch(Exception g) {
			System.out.println("Exception: " + g);
		}
		debug(2, "run", "end");
	}
	
	private void debug(int level, String method, String text) {
		if(this.debugLevel >= level) {
			if(level == 0) {
				System.out.println(method + text);
			} else {
				System.out.println("DEBUG(" + method + "): " + text);
			}
		}
	}
}
