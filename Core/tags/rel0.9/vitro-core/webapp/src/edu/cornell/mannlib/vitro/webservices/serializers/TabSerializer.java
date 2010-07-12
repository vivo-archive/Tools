package edu.cornell.mannlib.vitro.webservices.serializers;

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

import java.io.IOException;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Tab;

public class TabSerializer implements Serializer{

    //@Override
    public void serialize(QName name,  Attributes attributes,
            Object value, SerializationContext context) 
    throws IOException {
        if (!(value instanceof Tab))
            throw new IOException("Can't serialize a " + value.getClass().getName() 
                    + " with a TabSerializer.");
        Tab tab = (Tab)value;
        
        context.startElement(name, attributes);
        
        context.serialize(new QName("", "id"), null, tab.getTabId() );
        context.serialize(new QName("",  "title"), null, tab.getTitle());
        context.serialize(new QName("",  "description"), null, tab.getDescription());                
        context.serialize(new QName("", "moreTag"), null, tab.getMoreTag());
        
        
//        context.serialize(new QName("",  VCLASS_MBER), null, ent.getVClass());
//        context.serialize(new QName("",  URL_MBER), null, ent.getUrl());
//        context.serialize(new QName("",  DESCRIPTION_MBER), null, ent.getDescription());
//        context.serialize(new QName("",  SUNRISE_MBER), null, ent.getSunrise());
//        context.serialize(new QName("",  SUNSET_MBER), null, ent.getSunset());
//        context.serialize(new QName("",  TIMEKEY_MBER), null, ent.getTimekey());
//        context.serialize(new QName("",  IMAGEFILE_MBER), null, ent.getImageFile());
//        context.serialize(new QName("",  ANCHOR_MBER), null, ent.getAnchor());
//        context.serialize(new QName("",  BLURB_MBER), null, ent.getBlurb());
//        context.serialize(new QName("",  IMAGETHUMB_MBER), null, ent.getImageThumb());
//        context.serialize(new QName("",  CITATION_MBER), null, ent.getCitation());
//        context.serialize(new QName("",  STATUS_MBER), null, ent.getStatus());
 
        Collection<Individual> ents = tab.getRelatedEntities();        
        if( ents != null ){
            for(Individual ent : ents ){
                ent.setHiddenFromDisplayBelowRoleLevel(null);
                ent.setProhibitedFromUpdateBelowRoleLevel(null);               
            }
        }
                
        context.serialize(new QName("",  "relatedEntities"), null, ents);                

        context.endElement();
        
    }

    //@Override
    public Element writeSchema(Class arg0, Types arg1) throws Exception {
        return null;
    }

    //@Override
    public String getMechanismType() { return Constants.AXIS_SAX;   }

}
