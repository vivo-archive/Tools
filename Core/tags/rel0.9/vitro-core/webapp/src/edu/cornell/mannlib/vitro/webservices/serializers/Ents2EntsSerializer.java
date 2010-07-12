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

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * This is used to by the axis webservices. see vitro/webservices/wsdd/VitroWs.wsdd
 */
public class Ents2EntsSerializer implements Serializer {
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
        if (!(value instanceof ObjectPropertyStatement))
            throw new IOException("Can't serialize a " + value.getClass().getName() + " with a Ents2EntsSerializer.");
        ObjectPropertyStatement e2e = (ObjectPropertyStatement)value;
        context.startElement(name, attributes);

        //context.serialize(new QName("", ENTS2ENTSID_MBER), null, e2e.getEnts2entsId());
        //context.serialize(new QName("", DOMAINID_MBER), null, e2e.getDomainId());
        //context.serialize(new QName("", DOMAIN_MBER), null, e2e.getDomain());
        context.serialize(new QName("", RANGEID_MBER), null, e2e.getObjectURI());

        //we don't want to serialize any further.
        Individual object = e2e.getObject();
        object.setObjectPropertyStatements(null);        
        context.serialize(new QName("", RANGE_MBER), null, object);

        //context.serialize(new QName("", PROPERTYID_MBER), null, e2e.getPropertyId());
        //context.serialize(new QName("", PROPERTY_MBER), null, e2e.getProperty());
        context.serialize(new QName("", QUALIFIER_MBER), null, e2e.getQualifier());

        context.endElement();
    }

    public String getMechanismType() { return Constants.AXIS_SAX; }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }

//     /**
//      * 1) reflects props and ents2ents domainside,
//      * 2) sorts
//      * 3) removed domain Entity object from all ents2ents
//      *
//      */
//     private final void prepareForSerialization(final Entity ent){
//         if( ent == null || ent.getPropertyList() == null) return;

//         ent.sortForDisplay();
//         ent.forceAllPropertiesDomainSide();

//         Iterator it = ent.getPropertyList().iterator();
//         while(it.hasNext()){
//             Property prop = (Property)it.next();
//             prepareProperty(prop);
//         }
//     }

//     /**
//      * calls prepareEnts2Ents on each ents2ents of Property
//      */
//     private final void prepareProperty(final Property prop){
//         if( prop == null || prop.getEnts2Ents() == null ) return;

//         Iterator it = prop.getEnts2Ents().iterator();
//         while(it.hasNext()){
//             prepareEnts2Ents((Ents2Ents)it.next());
//         }
//     }

//     private final void prepareEnts2Ents(final Ents2Ents e2e ){
//         if( e2e != null){
//             e2e.setDomain(null);
//             e2e.setProperty(null);
//         }
//     }
}
