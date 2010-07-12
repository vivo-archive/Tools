package edu.cornell.mannlib.vitro.webapp.utils;

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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.tidy.Node;
import org.w3c.tidy.Tidy;

public class MakeTidy {
    private static final Log log = LogFactory.getLog(MakeTidy.class.getName());
    private static PrintWriter outFile = null;
    
    public MakeTidy() {
        try {
            outFile = new PrintWriter( new FileWriter( "tidyErrorOutput.log"));
            System.out.println("logging errors to tidy.error.output in Tomcat logs directory\n");
        } catch (IOException ex) {
            log.error("cannot open Tidy error output file");
        }
    }

	public String process (String value) {
	    Tidy tidy = new Tidy(); // obtain a new Tidy instance
	    
	    // set desired config options using tidy setters: see http://jtidy.sourceforge.net/apidocs/index.html
	    tidy.setAsciiChars(true);                // convert quotes and dashes to nearest ASCII character
	    tidy.setDropEmptyParas(true);            // discard empty p elements
        tidy.setDropFontTags(true);              // discard presentation tags
	    tidy.setDropProprietaryAttributes(true); // discard proprietary attributes
        tidy.setForceOutput(true);               // output document even if errors were found
	    tidy.setLogicalEmphasis(true);           // replace i by em and b by strong
	    tidy.setMakeBare(true);                  // remove Microsoft cruft
	    tidy.setMakeClean(true);                 // remove presentational clutter
        tidy.setPrintBodyOnly(true);             // output BODY content only
        tidy.setShowWarnings(true);              // show warnings
        tidy.setTidyMark(true);                  // add meta element indicating tidied doc
        tidy.setTrimEmptyElements(true);         // trim empty elements
	    tidy.setWord2000(true);                  // draconian cleaning for Word 2000
        tidy.setXHTML(true);                     // output extensible HTML
	    
        if (outFile != null /* && (log.isDebugEnabled() */) {
            tidy.setErrout(outFile);
            tidy.setShowErrors(Integer.MAX_VALUE);
            outFile.println("\nInput:\n"+value+"\n");
        }
        
	    StringWriter sw = new StringWriter();
      /*Node rootNode = */tidy. parse(new StringReader(value),sw);
        String outputStr = sw.toString();
        if (outFile != null /*&& log.isDebugEnabled()*/) {
            outFile.println("\nTidied Output:\n"+outputStr+"\n");
        }
        outFile.flush();
        return outputStr;        
	}
}
