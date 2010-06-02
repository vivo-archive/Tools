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

package edu.cornell.mannlib.vitro.webapp.beans;

import java.util.Date;

/**
 * a class representing an particular instance of a data property
 *
 */
public class DataPropertyStatementImpl implements VitroTimeWindowedResource, DataPropertyStatement
{
    private String individualURI = null;
    private String data = null;
    private String datapropURI = null;
    private String datatypeURI = null;
    private String language = null;

    private Date sunrise = null;
    private Date sunset = null;
    //private String qualifier = null;

    public DataPropertyStatementImpl(){
    }

    public DataPropertyStatementImpl(Individual individual){
        if( individual != null ){
            this.individualURI = individual.getURI();
        }
    }
    
    public DataPropertyStatementImpl(String individualUri, String propertyUri, String data){
        individualURI = individualUri;
        datapropURI = propertyUri;
        this.data = data;
    }

    public String getIndividualURI() {
        return individualURI;
    }

    public void setIndividualURI(String individualURI) {
        this.individualURI = individualURI;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDatapropURI() {
        return datapropURI;
    }

    public void setDatapropURI(String datapropURI) {
        this.datapropURI = datapropURI;
    }

    public String getDatatypeURI() {
        return datatypeURI;
    }

    public void setDatatypeURI(String datatypeURI) {
        this.datatypeURI = datatypeURI;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getSunrise() {
        return sunrise;
    }

    public void setSunrise(Date sunrise) {
        this.sunrise = sunrise;
    }

    public Date getSunset() {
        return sunset;
    }

    public void setSunset(Date sunset) {
        this.sunset = sunset;
    }
    /*
    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }
    */
    public String getString(){
        String out = "instance of dataprop: " + datapropURI;

        if( data == null )
            out = out + " data is null";
        else if( data.length() > 20 )
            out = out + " data (truncated): '" + data.substring(0,19) + "'...";
        else
            out = out + " data: '" + data ;
        return out;
    }
}
