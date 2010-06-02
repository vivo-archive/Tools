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

package edu.cornell.mannlib.vitro.webapp.edit;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;/**
 * bdc34: I needed to have a representation of a rdf literal for
 * editing.  Jena seems to have a Model associated with the literals and
 * has deprecated the creation of simple Literals as if they were data
 * structures.  So this code was writen.
 *
 * THESE MAY NOT BE USED AS LITERALS WITH THE JENA LIBRARY
 */
public class EditLiteral implements Literal {

    String value = null;
    String datatype =null;
    String lang =null;

    public EditLiteral(String value, String datatype, String lang){
        //both datatype and lang set is not suppored in jena2
//        if( lang != null && datatype != null)
//            throw new IllegalArgumentException("a literal cannot have a lang and a datatype");

        this.value= value;
        this.datatype = datatype;
        this.lang = lang;
    }

     public Object getValue() {
        return value;
    }

    public String getDatatypeURI() {
        return datatype;
    }

    public String getLexicalForm() {
//        if( lang != null && lang.length() > 0)
//            return '"' + value + "\"@" + lang;
//        else if ( datatype != null && datatype.length() > 0 )
//            return '"' + value + "\"^^<" + datatype + '>';
//        else
//            return '"' + value + '"';
        return value;
    }

    public boolean isLiteral() {
        return true;
    }

    public String getLanguage() {
        return lang;
    }


    public String getString(){
        return value;
    }

    public String toString(){
        return "value: "+value+"\ndatatype: "+datatype+"\nlanguage: "+lang;
    }

    public RDFDatatype getDatatype() {
        throw new UnsupportedOperationException();
    }

    public boolean getBoolean() {
        throw new UnsupportedOperationException();
    }

    public byte getByte() {
        throw new UnsupportedOperationException();
    }

    public short getShort() {
        throw new UnsupportedOperationException();
    }

    public int getInt() {
        throw new UnsupportedOperationException();
    }

    public long getLong() {
        throw new UnsupportedOperationException();
    }

    public char getChar() {
        throw new UnsupportedOperationException();
    }

    public float getFloat() {
        throw new UnsupportedOperationException();
    }

    public double getDouble() {
        throw new UnsupportedOperationException();
    }

//    @Deprecated
//    public Object getObject(ObjectF objectF) {
//        throw new UnsupportedOperationException();
//    }


    @Deprecated
    public boolean getWellFormed() {
        throw new UnsupportedOperationException();
    }

    public boolean isWellFormedXML() {
        throw new UnsupportedOperationException();
    }

    public boolean sameValueAs(Literal literal) {
        return equalLiterals( this, literal);
    }

    public boolean isAnon() {
        throw new UnsupportedOperationException();
    }



    public boolean isURIResource() {
        throw new UnsupportedOperationException();
    }

    public boolean isResource() {
        throw new UnsupportedOperationException();
    }

    public RDFNode inModel(Model model) {
        throw new UnsupportedOperationException();
    }

    public Object visitWith(RDFVisitor rdfVisitor) {
        throw new UnsupportedOperationException();
    }

    public Node asNode() {
        throw new UnsupportedOperationException();
    }


    public static  boolean equalLiterals( Literal a, Literal b){
        if( a == null && b == null )
            return true; //?
        if((a == null && b != null) || ( b == null && a != null))
            return false;

        if( ( a.getDatatypeURI() != null && b.getDatatypeURI() == null )
            || ( a.getDatatypeURI() == null && b.getDatatypeURI() != null ))
            return false;

        //in Jena2, typed literals with languages are not supported, ignore lang
        if( a.getDatatypeURI() != null && b.getDatatypeURI() != null ){
            if( ! a.getDatatypeURI().equals( b.getDatatypeURI() ) ){
                return false;
            }else{
                return compareValues( a, b );
            }
        }

        if( a.getLanguage() == null && b.getLanguage() == null ){
            return compareValues( a, b );
        }

        if(( a.getLanguage() == null && b.getLanguage() != null ) ||
            (a.getLanguage() != null && b.getLanguage() == null ) )
            return false;

        if( a.getLanguage() != null && b.getLanguage() != null &&
            a.getLanguage().equals( b.getLanguage() ) ){
            return compareValues( a, b );
        }else{
            return false;
        }
    }

    private static boolean compareValues( Literal a, Literal b){
        if( a.getValue() == null && b.getValue() == null )
            return true; //?
        else  if( a.getValue() == null && b.getValue() != null
                  || a.getValue() != null && b.getValue() == null )
            return false;
        else
            return a.getValue().equals( b.getValue() ) ;
    }

    @Deprecated
	public Object getObject(ObjectF arg0) {
		throw new UnsupportedOperationException();
	}

	public <T extends RDFNode> T as(Class<T> arg0) {
        throw new UnsupportedOperationException();
	}

	public <T extends RDFNode> boolean canAs(Class<T> arg0) {
        throw new UnsupportedOperationException();
	}
}
