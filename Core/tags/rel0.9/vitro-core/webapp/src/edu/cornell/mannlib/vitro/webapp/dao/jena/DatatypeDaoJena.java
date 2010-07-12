package edu.cornell.mannlib.vitro.webapp.dao.jena;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vitro.webapp.beans.Datatype;
import edu.cornell.mannlib.vitro.webapp.dao.DatatypeDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

public class DatatypeDaoJena extends JenaBaseDao implements DatatypeDao {

    private List<Datatype> allDatatypes;
    private HashMap<String,Datatype> allDatatypesMap;
    
    public DatatypeDaoJena(WebappDaoFactoryJena wadf) {
        super(wadf);
    }
    
    public void deleteDatatype(Datatype dtp) {
    	throw new UnsupportedOperationException();
    }

    public void deleteDatatype(int id) {
        throw new UnsupportedOperationException();
    }

    public List getAllDatatypes() {
    	if (allDatatypes == null) {
    		init();
    	}
        return allDatatypes;

//      TypeMapper tm = new TypeMapper();
//      XSDDatatype.loadXSDSimpleTypes(tm);
//      Iterator datatypes = tm.listTypes();
//      while (datatypes.hasNext()) {
//          RDFDatatype datatype = (RDFDatatype) datatypes.next();
//          Datatype dtp = new Datatype();
//          dtp.setName(datatype.getURI());
//          dtp.setUri(datatype.getURI());
//          allDatatypes.add(datatype);
//      }
//      if (allDatatypes.size()<1)
//          return null;
//      else
//          return allDatatypes;

    }

    public Datatype getDatatypeById(int id) {
        throw new UnsupportedOperationException();
    }

    public Datatype getDatatypeByURI(String uri) {
    	if (allDatatypesMap == null) {
    		init();
    	}
        return allDatatypesMap.get(uri);
    }

    public int getDatatypeIdByURI(String uri) {
        if (allDatatypesMap == null) {
        	init();
        }
        return allDatatypesMap.get(uri).getId();
    }

    public void updateDatatype(Datatype dtp) {
        throw new UnsupportedOperationException();
    }
    
    private synchronized void init() {
    	
       List<Datatype> allDatatypes = new ArrayList<Datatype>();
       HashMap<String,Datatype> allDatatypesMap = new HashMap<String,Datatype>();
    	
       int index = 0;
           
       Datatype stringD = new Datatype();
       stringD.setUri(XSD+"string");
       stringD.setName("string");
       stringD.setId(index++);
       allDatatypes.add(stringD);
       allDatatypesMap.put(stringD.getUri(), stringD);
       
       Datatype intD = new Datatype();
       intD.setUri(XSD+"int");
       intD.setName("integer between -2147483648 and 2147483647 (int)");
       intD.setId(index++);
       allDatatypes.add(intD);
       allDatatypesMap.put(XSD+"int", intD);
       
       Datatype integerD = new Datatype();
       integerD.setUri(XSD+"integer");
       integerD.setName("integer");
       integerD.setId(index++);
       allDatatypes.add(integerD);
       allDatatypesMap.put(integerD.getUri(), integerD);
       
       Datatype dateTimeD = new Datatype();
       dateTimeD.setUri(XSD+"dateTime");
       dateTimeD.setName("date and time (YYYY-MM-DDThh:mm:ss)");
       dateTimeD.setId(index++);
       allDatatypes.add(dateTimeD);
       allDatatypesMap.put(XSD+"dateTime", dateTimeD);
       
       Datatype dateD = new Datatype();
       dateD.setUri(XSD+"date");
       dateD.setName("date (YYYY-MM-DD)");
       dateD.setId(index++);
       allDatatypes.add(dateD);
       allDatatypesMap.put(dateD.getUri(), dateD);
       
       Datatype timeD = new Datatype();
       timeD.setUri(XSD+"time");
       timeD.setName("time (hh:mm:ss)");
       timeD.setId(index++);
       allDatatypes.add(timeD);
       allDatatypesMap.put(timeD.getUri(), timeD);
       
       Datatype gYearD = new Datatype();
       gYearD.setUri(XSD+"gYear");
       gYearD.setName("year (YYYY)");
       gYearD.setId(index++);
       allDatatypes.add(gYearD);
       allDatatypesMap.put(gYearD.getUri(), gYearD);
       
       Datatype gMonthD = new Datatype();
       gMonthD.setUri(XSD+"gMonth");
       gMonthD.setName("month (MM)");
       gMonthD.setId(index++);
       allDatatypes.add(gMonthD);
       allDatatypesMap.put(gMonthD.getUri(), gYearD);
       
       Datatype gYearMonthD = new Datatype();
       gYearMonthD.setUri(XSD+"gYearMonth");
       gYearMonthD.setName("year and month (YYYY-MM)");
       gYearMonthD.setId(index++);
       allDatatypes.add(gYearMonthD);
       allDatatypesMap.put(gYearMonthD.getUri(), gYearMonthD);
                
       Datatype anyURID = new Datatype();
       anyURID.setUri(XSD+"anyURI");
       anyURID.setName("URI/URL");
       anyURID.setId(index++);
       allDatatypes.add(anyURID);
       allDatatypesMap.put(XSD+"anyURI", anyURID);
       
       Datatype booleanD = new Datatype();
       booleanD.setUri(XSD+"boolean");
       booleanD.setName("boolean (true/false)");
       booleanD.setId(index++);
       allDatatypes.add(booleanD);
       allDatatypesMap.put(booleanD.getUri(), booleanD);
       
       this.allDatatypes = allDatatypes;
       this.allDatatypesMap = allDatatypesMap;
           
   }

}
