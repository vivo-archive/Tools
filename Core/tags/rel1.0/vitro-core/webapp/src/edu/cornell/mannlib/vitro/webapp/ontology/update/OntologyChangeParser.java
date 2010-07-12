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

import edu.cornell.mannlib.vitro.webapp.ontology.update.AtomicOntologyChange;
import edu.cornell.mannlib.vitro.webapp.ontology.update.AtomicOntologyChange.AtomicChangeType;

import org.skife.csv.CSVReader;
import org.skife.csv.SimpleReader;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * Performs parsing on Prompt output and provides change object list.
 * 
 * @author ass92
 *
 */

public class OntologyChangeParser {

	private OntologyChangeLogger logger;
	
	public OntologyChangeParser(OntologyChangeLogger logger) {
		this.logger = logger;
	}
		
	/**
	 * @param args
	 * @throws IOException 
	 */
	
	@SuppressWarnings({ "unchecked", "null", "static-access" })
	public ArrayList<AtomicOntologyChange> parseFile(String diffPath) throws IOException{
		
		AtomicOntologyChange changeObj;
		ArrayList<AtomicOntologyChange> changeObjects = new ArrayList<AtomicOntologyChange>();
		int countColumns = 0;
		String URI = null;
		String rename = null;
		String sourceURI = null;
		String destinationURI = null;
		StringTokenizer stArr = null; 
		FileInputStream in = new FileInputStream(new File(diffPath));
		CSVReader readFile = new SimpleReader();
		readFile.setSeperator('\t');
		
		List<String[]> rows = readFile.parse(in);
		
		for(int rowNum = 0; rowNum < rows.size(); rowNum++){
			
			String[] cols = rows.get(rowNum);
			if (cols.length != 5) {
				logger.logError("Invalid PromptDiff data at row " + (rowNum + 1) 
					   + ". Expected 5 columns; found " + cols.length );
			} else {
		
				changeObj = new AtomicOntologyChange();
				if (cols[0] != null && cols[0].length() > 0) {
					changeObj.setSourceURI(cols[0]);
				}
				if (cols[1] != null && cols[1].length() > 0) {
					changeObj.setDestinationURI(cols[1]);
				}
				if ("Yes".equals(cols[2])) {
					changeObj.setAtomicChangeType(AtomicChangeType.RENAME);
				} else if ("Delete".equals(cols[3])) {
					changeObj.setAtomicChangeType(AtomicChangeType.DELETE); 
				} else if ("Add".equals(cols[3])) {
					changeObj.setAtomicChangeType(AtomicChangeType.ADD);
				} else {
					logger.logError("Invalid rename or change type data: '" +
							cols[2] + " " + cols[3] + "'");
				}
				changeObjects.add(changeObj);
					
			}
			
		}
		
		if (changeObjects.size() == 0) {
			logger.log("did not find any changes in PromptDiff output file.");
		}
		return changeObjects;
	}

}
