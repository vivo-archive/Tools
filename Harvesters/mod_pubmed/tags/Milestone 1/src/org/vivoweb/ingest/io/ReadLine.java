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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadLine {
	private static int debugEnabled = 3;
	
	public String read(String preamble) {
		debug(2, "readline", "start");
		System.out.println(preamble);
		debug(2, "readline", "open br");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		try {
			debug(2, "readline>try", "start");
			debug(2, "readline>try", "read");
			while( !br.ready()) {
				// System.out.println("br notready");
			}
			input = br.readLine();
			debug(2, "readline>try", "close");
			// br.close();
			debug(2, "readline>try", "end");
		} catch(IOException ioe) {
			System.out.println("\nIO error trying to read your input! " + ioe);
		}
		debug(2, "readline", "end");
		return input;
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
