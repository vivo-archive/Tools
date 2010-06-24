package de.fuberlin.wiwiss.d2r;

import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * Some utility methods used in the mapping process.
 * <BR>History: 01-15-2003   : Initial version of this class.
 * <BR>History: 09-25-2003   : Changed for Jena2.
 * @author Chris Bizer chris@bizer.de
 * @version V0.2
 */
public class D2rUtil {

  /** log4j logger used for this class */
  private static Logger log = Logger.getLogger(D2rUtil.class);

  protected static String getNamespacePrefix(String qName) {
    int len = qName.length();
    for (int i = 0; i < len; i++) {
      if (qName.charAt(i) == ':') {
        return qName.substring(0, i);
      }
    }
    return "NoPrefixFound";
  }

  protected static String getLocalName(String qName) {
    int len = qName.length();
    for (int i = 0; i < len; i++) {
      if (qName.charAt(i) == ':') {
        return qName.substring(i + 1);
      }
    }
    return "NoLocalnameFound";
  }

  protected static String getFieldName(String fName) {
    int len = fName.length();
    for (int i = 0; i < len; i++) {
      if (fName.charAt(i) == '.') {
        return fName.substring(i + 1);
      }
    }
    return fName;
  }

  /**
   * Parses an D2R pattern. Translates the placeholders in an D2R pattern with values from the database.
   * @param  pattern Pattern to be translated.
   * @param  delininator Deliminator to identifiy placeholders (Standard: @@)
   * @param  tuple Hashmap with values used for replacement.
   * @return String with placeholders replaced.
   */
  protected static String parsePattern(String pattern, String deliminator,
                                       HashMap tuple) {
    String result = "";
    int startPosition = 0;
    int endPosition = 0;
    try {
      if (pattern.indexOf("@@") == -1)return pattern;
      while (startPosition < pattern.length() &&
             pattern.indexOf(deliminator, startPosition) != -1) {
        endPosition = startPosition;
        startPosition = pattern.indexOf(deliminator, startPosition);
        // get Text
        if (endPosition < startPosition)
          result += pattern.substring(endPosition, startPosition).trim();
        startPosition = startPosition + deliminator.length();
        endPosition = pattern.indexOf(deliminator, startPosition);
        // get field
        String fieldname = D2rUtil.getFieldName(pattern.substring(startPosition,
            endPosition).trim());
        result += tuple.get(fieldname);
        startPosition = endPosition + deliminator.length();
      }
      if (endPosition + deliminator.length() < pattern.length())
        result += pattern.substring(startPosition, pattern.length()).trim();
      return result;
    }
    catch (java.lang.Throwable ex) {
      log.error("Warning: There was a problem while parsing the pattern" +
                pattern + ".", ex);
      return result;
    }
  }
}
