package de.fuberlin.wiwiss.d2r;

import com.hp.hpl.jena.rdf.model.* ;
import org.apache.log4j.Logger;


/**
 * Abstract class representing an D2R bridge. Extended by the subclasses ObjectPropertyBridge and DatapropertyBridge.
 * <BR><BR>History: 
 * <BR>07-21-2004   : Error handling changed to Log4J.
 * <BR>09-25-2003   : Changed for Jena2.
 * <BR>01-15-2003   : Initial version of this class.
 * @author Chris Bizer chris@bizer.de
 * @version V0.2
 */
abstract public class Bridge {
    private String translation;
    private String datatype;
    private String pattern;
    private String xmlLang;
    private String column;
    private String property;
    private String value;

    /** log4j logger used for this class */
    private static Logger log = Logger.getLogger(D2rUtil.class);

    protected String getTranslation() { return translation; }

    protected void setTranslation(String translation) { this.translation = translation; }

    protected String getDatatype() { return datatype; }

    protected void setDatatype(String datatype) { this.datatype = datatype; }

    protected String getPattern() { return pattern; }

    protected void setPattern(String pattern) { this.pattern = pattern; }

    protected String getXmlLang() { return xmlLang; }

    protected void setXmlLang(String xmlLang) { this.xmlLang = xmlLang; }

    protected String getColumn() { return column; }

    protected void setColumn(String column) { this.column = column; }

    protected Property getProperty(D2rProcessor processor) {
        Property prop = null;
        try {
            String propURI = processor.getNormalizedURI(this.getProperty());
            prop = processor.getModel().getProperty(propURI);
        } catch (java.lang.Throwable ex) {
          log.warn("Warning: (getProperty) Property object for property " +
                   this.getProperty() + " not found in model.", ex);
        }
        return prop;
    }

    protected String getProperty() { return property; }

    protected void setProperty(String property) { this.property = property; }

    protected String getValue() { return value; }

    protected void setValue(String value) { this.value = value; }
}
