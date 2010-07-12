package edu.cornell.mannlib.vitro.webapp.beans;

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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: bdc34
 * Date: Oct 18, 2007
 * Time: 3:08:33 PM
 */
public interface Individual extends ResourceBean, VitroTimeWindowedResource, Comparable<Individual> {
    String getName();
    void setName(String in);

    String getVClassURI();
    void setVClassURI(String in);

    //Date getSunrise();
    void setSunrise(Date in);

    //Date getSunset();
    void setSunset(Date in);

    Date getTimekey();
    void setTimekey(Date in);

    Timestamp getModTime();
    void setModTime(Timestamp in);

    List<ObjectProperty> getObjectPropertyList();
    void setPropertyList(List<ObjectProperty> propertyList);

    Map<String,ObjectProperty> getObjectPropertyMap();
    void setObjectPropertyMap(Map<String,ObjectProperty> propertyMap);
    
    List<DataProperty> getDataPropertyList();
    void setDatatypePropertyList(List<DataProperty> datatypePropertyList);

    Map<String,DataProperty> getDataPropertyMap();
    void setDataPropertyMap(Map<String,DataProperty> propertyMap);
    
    void setDataPropertyStatements(List<DataPropertyStatement> list);
    List<DataPropertyStatement> getDataPropertyStatements();

    VClass getVClass();
    void setVClass(VClass class1);
    
    List<VClass> getVClasses();
    
    List<VClass> getVClasses(boolean direct);
    void setVClasses(List<VClass> vClassList, boolean direct);

    void setObjectPropertyStatements(List<ObjectPropertyStatement> list);
    List<ObjectPropertyStatement> getObjectPropertyStatements();

    List<DataPropertyStatement> getExternalIds();
    void setExternalIds(List<DataPropertyStatement> externalIds);

    String getMoniker();
    void setMoniker(String in);

    String getDescription();
    void setDescription(String in);

    String getAnchor();
    void setAnchor(String in);

    String getBlurb();
    void setBlurb(String in);

    String getCitation();
    void setCitation(String in);

    int getStatusId();
    void setStatusId(int in);

    String getStatus();
    void setStatus(String s);

    String getImageFile();
    void setImageFile(String imageFile);

    String getImageThumb();
    void setImageThumb(String imageThumb);

    String getUrl();
    void setUrl(String url);

    List<Link> getLinksList();
    void setLinksList(List<Link> linksList);
    
    Link getPrimaryLink();
    void setPrimaryLink(Link link);

    String getFlag1Set();
    void setFlag1Set(String in);
    int getFlag1Numeric();
    void setFlag1Numeric(int i);

    /* Consider the flagBitMask as a mask to & with flags.
    if flagBitMask bit zero is set then return true if
    the individual is in portal 2,
    if flagBitMask bit 1 is set then return true if
    the individual is in portal 4
    etc.
     */
    boolean doesFlag1Match(int flagBitMask);

    String getFlag2Set();
    void setFlag2Set(String in);
    int getFlag2Numeric();
    void setFlag2Numeric(int i);

    String getFlag3Set();
    void setFlag3Set(String in);
    int getFlag3Numeric();
    void setFlag3Numeric(int i);

    List<String> getKeywords();
    void setKeywords(List<String> keywords);
    String getKeywordString();
    
    List<Keyword> getKeywordObjects();
    void setKeywordObjects(List<Keyword> keywords);

    void sortForDisplay();

    JSONObject toJSON() throws JSONException;

    Object getField(String fieldName) throws NoSuchMethodException;
    
    Float getSearchBoost();
    void setSearchBoost( Float boost );
}
