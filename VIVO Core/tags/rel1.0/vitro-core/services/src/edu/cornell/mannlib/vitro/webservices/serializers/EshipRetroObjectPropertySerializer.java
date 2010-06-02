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

package edu.cornell.mannlib.vitro.webservices.serializers;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.List;


/**
 * This is used to by the axis webservices. see vitro/webservices/wsdd/VitroWs.wsdd
 */
public class EshipRetroObjectPropertySerializer implements Serializer {
    public static final QName myTypeQName = new QName("typeNS", "VitroEntity");

    public static final String ENTS2ENTSID_MBER = "ents2entsId";
    public static final String DOMAINID_MBER = "domainId";
    public static final String DOMAIN_MBER = "domain";
    public static final String RANGEID_MBER = "rangeId";
    public static final String RANGE_MBER = "range";
    public static final String PROPERTYID_MBER = "propertyId";
    public static final String PROPERTY_MBER = "property";
    public static final String QUALIFIER_MBER = "qualifier";
    public static final String DOMAINORIENTED_MBER = "domainOriented";


    /**
     * Serialize an element named name, with the indicated attributes
     * and value.
     * @param name is the element name
     * @param attributes are the attributes...serialize is free to add more.
     * @param value is the value
     * @param context is the SerializationContext
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (!(value instanceof ObjectProperty ))
            throw new IOException("Can't serialize a " + value.getClass().getName() + " with a EshipRetroObjectPropertySerializer.");
        ObjectProperty e2e = (ObjectProperty)value;
        
        //jc55 trouble with RoleLevel objects
        e2e.setProhibitedFromUpdateBelowRoleLevel(null);
        e2e.setHiddenFromDisplayBelowRoleLevel(null);
        
        context.startElement(name, attributes);

        //context.serialize(new QName("", ENTS2ENTSID_MBER), null, e2e.getEnts2entsId());
        //context.serialize(new QName("", DOMAINID_MBER), null, e2e.getDomainId());
        //context.serialize(new QName("", DOMAIN_MBER), null, e2e.getDomain());
        context.serialize(new QName("", "URI"         ), null, e2e.getURI());
        context.serialize(new QName("", "domainPublic"), null, e2e.getDomainPublic());
        context.serialize(new QName("", "domainSide"  ), null, e2e.getDomainSide());
                
        List stmts = e2e.getObjectPropertyStatements();        
        context.serialize(new QName("", "ents2Ents"       ), null, stmts);

        context.endElement();
    }

    public String getMechanismType() { return Constants.AXIS_SAX; }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }

}
