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

package edu.cornell.mannlib.vitro.webapp.ontology.update;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class SimpleOntologyChangeLogger implements OntologyChangeLogger {

	private Writer logWriter;
	private Writer errorWriter;
	
	private boolean errorsWritten = false;
	
	public SimpleOntologyChangeLogger( String logPath, 
									   String errorPath ) {
		File logFile = new File(logPath);
		File errorFile = new File(errorPath);
		try {
			logWriter = new BufferedWriter(new FileWriter(logFile));
			errorWriter = new BufferedWriter(new FileWriter(errorFile));
		} catch (IOException ioe) {
			throw new RuntimeException ("Unable to open ontology change log " +
										"files for writing", ioe);
		}
	}
					
	public void log(String logMessage) throws IOException {
		
		Exception e = new Exception();
		StackTraceElement[] elements = e.getStackTrace();
		String className = ((StackTraceElement)elements[1]).getClassName();
		className = className.substring(className.lastIndexOf('.') + 1 );
		String methodName = ((StackTraceElement)elements[1]).getMethodName();
		
		logWriter.write(className + "." + methodName +  ":  " + logMessage + "\n\n");
		logWriter.flush();
	}

	public void logError(String errorMessage) throws IOException {

		errorsWritten = true;
		Exception e = new Exception();
		StackTraceElement[] elements = e.getStackTrace();
		String className = ((StackTraceElement)elements[1]).getClassName();
		className = className.substring(className.lastIndexOf('.') + 1 );
		String methodName = ((StackTraceElement)elements[1]).getMethodName();
		int lineNumber = ((StackTraceElement)elements[1]).getLineNumber(); 

		errorWriter.write(className + "." + methodName +  " line " + lineNumber + ":  " + errorMessage + "\n");
		errorWriter.flush();
	}
	
	public void closeLogs() throws IOException {
		logWriter.flush();
		logWriter.close();
		errorWriter.flush();
		errorWriter.close();
	}
	
	public boolean errorsWritten() {
		return errorsWritten;
	}
}
