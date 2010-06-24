package de.fuberlin.wiwiss.d2r;

import java.util.Vector;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 * D2R bridge for ObjectProperties (References to other instances).
 * <BR>History: 01-15-2003   : Initial version of this class.
 * <BR>History: 09-25-2003   : Changed for Jena2.
 * @author Chris Bizer chris@bizer.de
 * @version V0.2
 */
public class ObjectPropertyBridge
    extends Bridge {
  private String referredClass;
  private Vector referredGroupBy;

  /** log4j logger used for this class */
  private static Logger log = Logger.getLogger(D2rUtil.class);

  protected ObjectPropertyBridge() {
    this.referredGroupBy = new Vector();
  }

  protected String getReferredClass() {
    return referredClass;
  }

  protected void setReferredClass(String referredClass) {
    this.referredClass = referredClass;
  }

  protected Vector getReferredGroupBy() {
    return referredGroupBy;
  }

  /**
   * Parses a string containing the GroupBy fields and adds them to the bridge.
   * @param  fiels String with GroupBy fields separated by ','.
   */
  protected void setReferredGroupBy(String fields) {
    StringTokenizer tokenizer = new StringTokenizer(fields, ",");
    while (tokenizer.hasMoreTokens())
      this.referredGroupBy.add(tokenizer.nextToken().trim());
  }
}
