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

package edu.cornell.mannlib.vitro.webapp.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.tidy.Tidy;

public class MakeTidy {
	private static final Logger log = Logger.getLogger(MakeTidy.class);
	private static PrintWriter outFile = new PrintWriter(new LoggingWriter(log,
			Level.INFO));

	public String process(String value) {
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
	    
		tidy.setErrout(outFile);
		tidy.setShowErrors(Integer.MAX_VALUE);
		outFile.println("\nInput:\n" + value + "\n");

		StringWriter sw = new StringWriter();
		/* Node rootNode = */tidy.parse(new StringReader(value), sw);
		String outputStr = sw.toString();
		log.debug("\nTidied Output:\n" + outputStr + "\n");
		return outputStr;
	}

	private static class LoggingWriter extends Writer {
		private final Logger logger;
		private final Level level;
		private String buffer;

		LoggingWriter(Logger logger, Level level) {
			this.logger = logger;
			this.level = level;
			this.buffer = "";
		}

		/**
		 * Append the new stuff to the buffer, and write any complete lines to
		 * the log.
		 */
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			buffer += new String(cbuf, off, len);
			dumpLines();
		}

		/**
		 * If the buffer isn't empty, clean it out by completing the line and
		 * dumping it to the log.
		 */
		@Override
		public void close() throws IOException {
			if (buffer.length() > 0) {
				buffer += "\n";
				dumpLines();
			}
		}

		/**
		 * We don't want to log a partial line, so {@link #flush()} does
		 * nothing.
		 */
		@Override
		public void flush() throws IOException {
		}

		/**
		 * If there are any complete lines in the buffer, write them to the log
		 * and remove them from the buffer.
		 */
		private void dumpLines() {
			while (true) {
				int lineEnd = buffer.indexOf("\n");
				if (lineEnd == -1) {
					return;
				} else {
					logger.log(level, buffer.substring(0, lineEnd).trim());
					buffer = buffer.substring(lineEnd + 1);
				}
			}
		}
	}
}
