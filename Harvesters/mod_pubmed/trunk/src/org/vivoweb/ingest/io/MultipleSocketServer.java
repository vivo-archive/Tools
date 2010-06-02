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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vivoweb.ingest.harvest.Harvestor;
import org.vivoweb.ingest.harvest.PubmedSOAPFetch;

// import javax.net.ssl.SSLServerSocketFactory;

public class MultipleSocketServer implements Runnable {
	private static Log log = LogFactory.getLog(MultipleSocketServer.class);
	private Socket connection;
	private int ID;
	private Socket streamConn = null;
	
	public static void main(String[] args) {
		log.trace("main: start");
		// log.trace("main: am init");
		// AccountManipulator.init();
		int connectionPort = 14048;
		int count = 0;
		try {
			log.trace("main>try: start");
			log.trace("main>try: open socket");
			// ServerSocket socket = SSLServerSocketFactory.getDefault().createServerSocket(port);
			ServerSocket socket = new ServerSocket(connectionPort);
			while(true) {
				log.trace("main>try>loop: start");
				log.trace("main>try>loop: accept");
				Socket connection = socket.accept();
				log.trace("main>try>loop: new mss");
				Runnable runnable = new MultipleSocketServer(connection, ++count);
				log.trace("main>try>loop: new thread");
				Thread thread = new Thread(runnable);
				log.trace("main>try>loop: thread start");
				thread.start();
				log.trace("main>try>loop: end");
			}
			// log.trace("main>try: end");
		} catch(Exception e) {}
		log.trace("main: end");
	}
	
	private MultipleSocketServer(Socket s, int i) {
		log.trace("const: start");
		this.connection = s;
		log.trace("const: set ID = " + i);
		this.ID = i;
		log.trace("const: end");
	}
	
	public void run() {
		log.trace("run: start");
		try {
			log.trace("run>try: start");
			ObjectInputStream ois = this.getOIS();
			boolean loop = true;
			while(loop) {
				log.trace("run>try>loop: start");
				log.trace("run>try>loop: read");
				Operation op = (Operation)ois.readObject();
				if(op != null) {
					loop = this.handleOperation(op);
				}
				log.trace("run>try>loop: end");
			}
			log.trace("run>try: end");
		} catch(Exception e) {
			System.out.println(e);
		} finally {
			log.trace("run>finally: start");
			try {
				log.trace("run>finally>try: start");
				this.connection.close();
				log.trace("run>finally>try: end");
			} catch(Exception e) {
				log.trace("run>finaly>catch: connection invalid");
			}
			log.trace("run>finally: end");
		}
		log.trace("run: end");
	}
	
	private ObjectOutputStream getOOS() throws IOException {
		log.trace("getOOS: open bos");
		BufferedOutputStream bos = new BufferedOutputStream(this.connection.getOutputStream());
		log.trace("getOOS: open oos");
		return new ObjectOutputStream(bos);
	}
	
	private ObjectInputStream getOIS() throws IOException {
		log.trace("getOIS: open bis");
		BufferedInputStream bis = new BufferedInputStream(this.connection.getInputStream());
		log.trace("getOIS: open ois");
		return new ObjectInputStream(bis);
	}
	
	private boolean handleOperation(Operation op) throws IOException, IllegalArgumentException {
		boolean loop = true;
		ObjectOutputStream oos = this.getOOS();
		log.debug("handleOperation: "+op);
		Task task = op.getTask();
		if(task == Task.Fetch) {
			if(this.checkParam(op, "email", oos) && this.checkParam(op, "tool", oos)) {
				Map<String, String> params = op.getParameters();
				Harvestor harvest = new PubmedSOAPFetch(params.get("email"), params.get("tool"), oos);
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
			log.info("CLIENT" + this.ID + ": Shutdown Command Recieved: Shutting Down!");
			loop = false;
		} else {
			log.debug("handleOperation>ifelse>else: Unknown Task!");
			log.trace("handleOperation>ifelse>else: create hashmap");
			HashMap<String, Object> data = new HashMap<String, Object>();
			log.trace("handleOperation>ifelse>else: put task");
			data.put("task", task);
			log.trace("handleOperation>ifelse>else: write response");
			oos.writeObject(new Response(Feedback.UnknownTask, data));
			log.trace("handleOperation>ifelse>else: flush");
			oos.flush();
		}
		return loop;
	}
	
	private boolean checkParam(Operation op, String paramName, ObjectOutputStream oos) throws IOException {
		if(op.getParameters().containsKey(paramName)) {
			return true;
		}
    log.info("checkParam: Bad Parameters - Missing '" + paramName + "'!");
    log.trace("checkParam: create hashmap");
    HashMap<String, Object> data = new HashMap<String, Object>();
    log.trace("checkParam: put params");
    data.put("paramName", paramName);
    data.put("task", op.getTask());
    data.put("params", op.getParameters());
    log.trace("checkParam: write response");
    oos.writeObject(new Response(Feedback.BadFetchParameters, data));
    log.trace("checkParam: flush");
    oos.flush();
    return false;
	}
}
