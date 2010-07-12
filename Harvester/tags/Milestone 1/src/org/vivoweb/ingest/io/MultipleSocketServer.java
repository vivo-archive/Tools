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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.vivoweb.ingest.harvest.Harvestor;
import org.vivoweb.ingest.harvest.pubmed.Fetch;

// import javax.net.ssl.SSLServerSocketFactory;

public class MultipleSocketServer implements Runnable {
	
	private Socket connection;
	private int ID;
	private Socket streamConn = null;
	
	private static final int debugEnabled = 3;
	
	public static void main(String[] args) {
		debug(2, "main", "start");
		// debug(2,"main", "am init");
		// AccountManipulator.init();
		int connectionPort = 14048;
		int count = 0;
		try {
			debug(2, "main>try", "start");
			debug(2, "main>try", "open socket");
			// ServerSocket socket = SSLServerSocketFactory.getDefault().createServerSocket(port);
			ServerSocket socket = new ServerSocket(connectionPort);
			while(true) {
				debug(2, "main>try>loop", "start");
				debug(2, "main>try>loop", "accept");
				Socket connection = socket.accept();
				debug(2, "main>try>loop", "new mss");
				Runnable runnable = new MultipleSocketServer(connection, ++count);
				debug(2, "main>try>loop", "new thread");
				Thread thread = new Thread(runnable);
				debug(2, "main>try>loop", "thread start");
				thread.start();
				debug(2, "main>try>loop", "end");
			}
			// debug(2,"main>try", "end");
		} catch(Exception e) {}
		debug(2, "main", "end");
	}
	
	private MultipleSocketServer(Socket s, int i) {
		debug(2, "const", "start");
		this.connection = s;
		debug(2, "const", "set ID = " + i);
		this.ID = i;
		debug(2, "const", "end");
	}
	
	public void run() {
		debug(2, "run", "start");
		try {
			debug(2, "run>try", "start");
			ObjectInputStream ois = this.getOIS();
			boolean loop = true;
			while(loop) {
				debug(2, "run>try>loop", "start");
				debug(2, "run>try>loop", "read");
				Operation op = (Operation)ois.readObject();
				if(op != null) {
					loop = this.handleOperation(op);
				}
				debug(2, "run>try>loop", "end");
			}
			debug(2, "run>try", "end");
		} catch(Exception e) {
			System.out.println(e);
		} finally {
			debug(2, "run>finally", "start");
			try {
				debug(2, "run>finally>try", "start");
				this.connection.close();
				debug(2, "run>finally>try", "end");
			} catch(Exception e) {
				debug(2, "run>finaly>catch", "connection invalid");
			}
			debug(2, "run>finally", "end");
		}
		debug(2, "run", "end");
	}
	
	private ObjectOutputStream getOOS() throws IOException {
		debug(2, "getOOS", "open bos");
		BufferedOutputStream bos = new BufferedOutputStream(this.connection.getOutputStream());
		debug(2, "getOOS", "open oos");
		return new ObjectOutputStream(bos);
	}
	
	private ObjectInputStream getOIS() throws IOException {
		debug(2, "getOIS", "open bis");
		BufferedInputStream bis = new BufferedInputStream(this.connection.getInputStream());
		debug(2, "getOIS", "open ois");
		return new ObjectInputStream(bis);
	}
	
	private boolean handleOperation(Operation op) throws IOException, IllegalArgumentException {
		boolean loop = true;
		ObjectOutputStream oos = this.getOOS();
		debug(1, "handleOperation", op.toString());
		Task task = op.getTask();
		if(task == Task.Fetch) {
			if(this.checkParam(op, "email", oos) && this.checkParam(op, "tool", oos) && this.checkParam(op, "debugLevel", oos)) {
				Map<String, String> params = op.getParameters();
				Harvestor harvest = new Fetch(params.get("email"), params.get("tool"), Integer.parseInt(params.get("debugLevel")), oos);
				harvest.toString();
				
//				PipedInputStream pin = new PipedInputStream();
//				PrintStream out = new PrintStream(new PipedOutputStream(pin));
//				BufferedReader in = new BufferedReader(new InputStreamReader(pin));
//				harvest.execute(out);
//				out.flush();
//				String s;
//				while((s = in.readLine()) != null) {
//					System.out.println(s);
//				}
			} else {
				throw new IllegalArgumentException("parameters missing!");
			}
		} else if(task == Task.Translate) {
			// TODO Fill In This
		} else if(task == Task.Score) {
			// TODO Fill In This
		} else if(task == Task.Qualify) {
			// TODO Fill In This
		} else if(task == Task.OpenStream) {
			if(this.checkParam(op, "port", oos)) {
				String streamPortString = op.getParameters().get("port");
				int streamPort = Integer.parseInt(streamPortString);
				ServerSocket streamSocket = new ServerSocket(streamPort);
				oos.writeObject(new Response(Feedback.Success, null));
				oos.flush();
				this.streamConn = streamSocket.accept();
				this.streamConn.getOutputStream();
				this.streamConn.getInputStream();
			} else {
				throw new IllegalArgumentException("parameters missing!");
			}
		} else if(task == Task.Shutdown) {
			debug(0, "CLIENT" + this.ID + ": ", "Shutdown Command Recieved: Shutting Down!");
			loop = false;
		} else {
			debug(1, "handleOperation>ifelse>else", "Unknown Task!");
			debug(2, "handleOperation>ifelse>else", "create hashmap");
			HashMap<String, Object> data = new HashMap<String, Object>();
			debug(2, "handleOperation>ifelse>else", "put task");
			data.put("task", task);
			debug(2, "handleOperation>ifelse>else", "write response");
			oos.writeObject(new Response(Feedback.UnknownTask, data));
			debug(2, "handleOperation>ifelse>else", "flush");
			oos.flush();
		}
		return loop;
	}
	
	private boolean checkParam(Operation op, String paramName, ObjectOutputStream oos) throws IOException {
		if(op.getParameters().containsKey(paramName)) {
			return true;
		}
    debug(0, "checkParam", "Bad Parameters: Missing '" + paramName + "'!");
    debug(2, "checkParam", "create hashmap");
    HashMap<String, Object> data = new HashMap<String, Object>();
    debug(2, "checkParam", "put params");
    data.put("paramName", paramName);
    data.put("task", op.getTask());
    data.put("params", op.getParameters());
    debug(2, "checkParam", "write response");
    oos.writeObject(new Response(Feedback.BadFetchParameters, data));
    debug(2, "checkParam", "flush");
    oos.flush();
    return false;
	}
	
	private static void debug(int level, String method, String text) {
		if(debugEnabled >= level) {
			if(level == 0) {
				System.out.println(method + text);
			} else {
				System.out.println("DEBUG(" + method + "): " + text);
			}
		}
	}
}
