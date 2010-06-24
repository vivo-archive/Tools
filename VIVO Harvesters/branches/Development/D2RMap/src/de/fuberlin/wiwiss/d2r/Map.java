package de.fuberlin.wiwiss.d2r;

import java.util.Vector;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.sql.*;
import com.hp.hpl.jena.rdf.model.*;

import de.fuberlin.wiwiss.d2r.exception.D2RException;

import org.apache.log4j.Logger;

/**
 * D2R Map Class. A Map class is created for every d2r:ClassMap element in the mapping file.
 * The Map class contains a Vector with all Bridges and an HashMap with all instances.
 * <BR><BR>History: 
 * <BR>07-21-2004   : Error handling changed to Log4J.
 * <BR>09-25-2003   : Changed for Jena2.
 * <BR>01-15-2003   : Initial version of this class.
 * @author Chris Bizer chris@bizer.de
 * @version V0.3
 */
public class Map {
  private HashMap instances;
  private Vector bridges;
  private String uriPattern;
  private String uriColumn;
  private String sql;
  private String id;
  private Vector groupBy;

  /** log4j logger used for this class */
  private static Logger log = Logger.getLogger(Map.class);

  protected Map() {
    instances = new HashMap();
    bridges = new Vector();
    groupBy = new Vector();
  }

  /**
   * Generates all instances for this map.
   * @param  odbc ODBC database connection.
   * @param  processor Reference to an D2R processor instance.
   */
  protected void generateInstances(D2rProcessor processor) throws D2RException {

    try {

      //get a connection from the processor
      Connection con = processor.getConnection();

      //generate instances using the Connection
      this.generateInstances(processor, con);

      //close the connection
      con.close();
    }
    catch (SQLException ex) {

      //an error occurred while closing the connection
      throw new D2RException("Could not close JDBC Connection.", ex);
    }
  }

  /**
   * Generates all instances for this map.
   * @param  odbc ODBC database connection.
   * @param  processor Reference to an D2R processor instance.
   */
  protected void generateInstances(D2rProcessor processor,
                                   Connection con) throws D2RException {

    if (log.isDebugEnabled()) {

      log.debug("Generating instances for D2rProcessor: " + processor);
    }

    //get model from processor
    Model model = processor.getModel();
    String query = this.sql.trim();

    // Add ORDER BY to query
    String ucQuery = query.toUpperCase();
    if (ucQuery.indexOf("ORDER BY") == -1) {
      if (query.indexOf(";") != -1)
        query = query.substring(0, query.indexOf(";"));
      query += " ORDER BY ";
      for (Iterator it = this.groupBy.iterator(); it.hasNext(); ) {
        query += (String) it.next() + ", ";
      }
      query = query.substring(0, query.length() - 2);
      query += ";";
    }
    else {
      throw new D2RException("SQL statement should not contain ORDER BY: " +
                             query);
    }
    try {

      // Create and execute SQL statement
      java.sql.Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      int numCols = rs.getMetaData().getColumnCount();
      boolean more = rs.next();
      HashMap lastTuple = new HashMap();
      // loop over the recordset and create new instance if groupBy values differ.
      while (more) {
        // cache tuple data
        HashMap currentTuple = new HashMap(numCols);
        for (int i = 1; i <= numCols; i++) {
          currentTuple.put(rs.getMetaData().getColumnName(i), rs.getString(i));
        }
        // check if new instance
        boolean newInstance = false;
        for (Iterator it = this.groupBy.iterator(); it.hasNext(); ) {
          String fieldName = D2rUtil.getFieldName( (String) it.next()).trim();
          if (!currentTuple.get(fieldName).equals(lastTuple.get(fieldName))) {
            newInstance = true;
            break;
          }
        }
        if (newInstance == true) {
          Instance inst = null;
          // define URI and generate instance
          if (this.uriPattern != null) {
            String uri = D2rUtil.parsePattern(this.uriPattern, D2R.DELIMINATOR,
                                              currentTuple);
            uri = processor.getNormalizedURI(uri);
            inst = new Instance(uri, model);
          }
          else if (this.uriColumn != null) {
            String uri = (String) currentTuple.get(D2rUtil.getFieldName(this.
                uriColumn));
            if (uri == null)
              throw new D2RException(
                  "(CreateInstances) No NULL value in the URI colunm '" +
                  D2rUtil.getFieldName(this.uriColumn) + "' allowed.");
            uri = processor.getNormalizedURI(uri);
            inst = new Instance(uri, model);
          }
          else {
            // generate blank node instance
            inst = new Instance(model);
          }
          // set instance id
          String instID = "";
          for (Iterator it = this.groupBy.iterator(); it.hasNext(); ) {
            instID +=
                currentTuple.get(D2rUtil.getFieldName( (String) it.next()).trim());
          }
          if (inst != null && instID != "") {
            inst.setInstanceID(instID);
            instances.put(instID, inst);
          }
          else {

            log.warn("Warning: (CreateInstances) Couldn't create " +
                     "instance " + instID + " in map " + this.getId() +
                     ".");
          }
        }
        // Fetch the next result set row
        more = rs.next();
        lastTuple = currentTuple;
      }

      // close result set and statement
      rs.close();
      stmt.close();
    }
    catch (SQLException ex) {
      String message = "SQL Exception caught: ";
      while (ex != null) {
        message += " SQLState: " + ex.getSQLState();
        message += "Message:  " + ex.getMessage();
        message += "Vendor:   " + ex.getErrorCode();
        ex = ex.getNextException();
      }
      throw new D2RException(message);
    }
    catch (D2RException ex) {
      throw ex;
    }
    catch (java.lang.Throwable ex) {
      // Got some other type of exception.  Dump it.
      throw new D2RException("Error: " + ex.toString(), ex);
    }
  }

  /**
   * Generates properties for all instances of this map.
   * @param  odbc ODBC database connection.
   * @param  processor Reference to an D2R processor instance.
   */
  protected void generatePropertiesForAllInstances(D2rProcessor processor)
      throws D2RException {

    try {

      //get a connection from the processor
      Connection con = processor.getConnection();

      //generate properties using the Connection
      this.generatePropertiesForAllInstances(processor, con);

      //close the connection
      con.close();
    }
    catch (SQLException ex) {

      //an error occurred while closing the connection
      throw new D2RException("Could not close JDBC Connection.", ex);
    }
  }

  /**
   * Generates properties for all instances of this map.
   * @param  odbc ODBC database connection.
   * @param  processor Reference to an D2R processor instance.
   */
  protected void generatePropertiesForAllInstances(D2rProcessor processor,
      Connection con) throws D2RException {
    Model model = processor.getModel();
    String query = this.sql;
    try {

      java.sql.Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      int numCols = rs.getMetaData().getColumnCount();
      // create properties for all instances
      boolean more = rs.next();
      while (more) {
        // cache tuple data
        HashMap tuple = new HashMap(numCols);
        for (int i = 1; i <= numCols; i++)
          tuple.put(rs.getMetaData().getColumnName(i), rs.getString(i));
          // get instance id
        String instID = "";
        for (Iterator it = this.groupBy.iterator(); it.hasNext(); ) {
          instID += tuple.get(D2rUtil.getFieldName( (String) it.next()).trim());
        }
        //get instance
        Instance inst = (Instance)this.getInstanceById(instID);
        if (inst == null) {

          log.warn("Warning: (CreateProperties) Didn't find instance " +
                   instID + " in map " + this.getId() + ".");
        }
        else {
          // add properties
          for (Iterator propIt = this.bridges.iterator(); propIt.hasNext(); ) {
            Bridge bridge = (Bridge) propIt.next();
            // generate property
            Property prop = bridge.getProperty(processor);
            // generate value
            if (bridge instanceof DataPropertyBridge) {
              // Generate property value
              String value = null;
              if (bridge.getColumn() != null) {
                // Column
                String fieldName = D2rUtil.getFieldName(bridge.getColumn()).
                    trim();
                if (! (tuple.get(D2rUtil.getFieldName(bridge.getColumn())) == null)) {
                  value = (String) tuple.get(D2rUtil.getFieldName(bridge.
                      getColumn()));
                  // translate value
                  if (bridge.getTranslation() != null) {
                    HashMap tables = processor.getTranslationTables();
                    // Warnung wenn Table nicht gefunden!!!!
                    HashMap table = (HashMap) tables.get(bridge.getTranslation());
                    value = (String) table.get(value);
                  }
                }
              }
              else if (bridge.getPattern() != null) {
                // pattern
                value = D2rUtil.parsePattern(bridge.getPattern(),
                                             D2R.DELIMINATOR, tuple);
              }
              else {
                value = bridge.getValue();
              }
              if (value != null) {
                Literal literal;
                if (bridge.getXmlLang() != null) {
                  literal = model.createLiteral(value, bridge.getXmlLang());
                }
                else {
                  literal = model.createLiteral(value);
                }
                inst.addProperty(prop, literal);
              }
            }
            // object property bridge
            if (bridge instanceof ObjectPropertyBridge) {
              ObjectPropertyBridge objectBridge = (ObjectPropertyBridge) bridge;
              Resource referredResource = null;
              if (objectBridge.getReferredClass() != null) {
                // get referred map
                Map referredMap = processor.getMapById(objectBridge.
                    getReferredClass());
                if (referredMap != null) {
                  // get referred instance
                  instID = "";
                  for (Iterator it = objectBridge.getReferredGroupBy().iterator();
                       it.hasNext(); ) {
                    instID +=
                        tuple.get(D2rUtil.getFieldName( (String) it.next()).trim());
                  }
                  Instance referredInstance = referredMap.getInstanceById(
                      instID);
                  if (referredInstance != null) {
                    referredResource = referredInstance.getInstanceResource();
                  }
                  else {

                    log.warn("Warning: (CreateProperties) Reference to instance " +
                             objectBridge.getReferredClass() + " " + instID +
                             " not found in map " + this.getId() + ".");
                  }
                }
                else {

                  log.warn("Warning: (CreateProperties) Couldn't find referred " +
                           "map " + objectBridge.getReferredClass() +
                           " in map " + this.getId() + ".");
                }
              }
              else if (objectBridge.getPattern() != null) {
                // pattern
                String value = D2rUtil.parsePattern(bridge.getPattern(),
                    D2R.DELIMINATOR, tuple);
                value = processor.getNormalizedURI(value);
                referredResource = model.getResource(value);
              }
              else if (objectBridge.getColumn() != null) {
                // column
                String fieldName = D2rUtil.getFieldName(bridge.getColumn()).
                    trim();
                if (! (tuple.get(D2rUtil.getFieldName(bridge.getColumn())) == null)) {
                  String value = (String) tuple.get(D2rUtil.getFieldName(bridge.
                      getColumn()));
                  if (objectBridge.getTranslation() != null) {
                    HashMap tables = processor.getTranslationTables();
                    HashMap table = (HashMap) tables.get(objectBridge.
                        getTranslation());
                    if (table != null) {
                      String translation = (String) table.get(value);
                      // if not found in table and there is a pattern -> use pattern
                      if (translation == null && objectBridge.getPattern() != null) {
                        // alternative pattern
                        translation = D2rUtil.parsePattern(bridge.getPattern(),
                            D2R.DELIMINATOR, tuple);
                      }
                      value = translation;
                    }
                    else {

                      log.warn("Warning: (CreateProperties) " +
                               "Couldn't find translation table " +
                               objectBridge.getTranslation() +
                               " in map " + this.getId() + ".");
                    }
                  }
                  value = processor.getNormalizedURI(value);
                  referredResource = model.getResource(value);
                }
              }
              else if (objectBridge.getValue() != null) {
                // fixed value
                String value = objectBridge.getValue();
                value = processor.getNormalizedURI(value);
                referredResource = model.getResource(value);
              }
              if (referredResource != null && prop != null) {
                // add object property property
                inst.addProperty(prop, referredResource);
              }
            }
          }
        }
        more = rs.next();
      }
      // Close result set and statement
      rs.close();
      stmt.close();
    }
    catch (SQLException ex) {
      String message = "SQL Exception caught: ";
      while (ex != null) {
        message += " SQLState: " + ex.getSQLState();
        message += "Message:  " + ex.getMessage();
        message += "Vendor:   " + ex.getErrorCode();
        ex = ex.getNextException();
      }
      throw new D2RException(message);
    }
    catch (java.lang.Throwable ex) {
      // Got some other type of exception.  Dump it.
      throw new D2RException(ex.getMessage());
    }
  }

  protected Vector getBridges() {
    return bridges;
  }

  protected void addBridge(Bridge bridge) {
    this.bridges.add(bridge);
  }

  protected String getUriPattern() {
    return uriPattern;
  }

  protected void setUriPattern(String uriPattern) {
    this.uriPattern = uriPattern;
  }

  protected String getUriColumn() {
    return uriColumn;
  }

  protected void setUriColumn(String uriColumn) {
    this.uriColumn = uriColumn;
  }

  protected String getSql() {
    return sql;
  }

  protected void setSql(String sql) {
    this.sql = sql;
  }

  protected Vector getGroupBy() {
    return groupBy;
  }

  protected void setGroupBy(Vector groupBy) {
    this.groupBy = groupBy;
  }

  protected String getId() {
    return this.id;
  }

  protected void setId(String id) {
    this.id = id;
  }

  /**
   * Adds GroupBy fields to the map.
   * @param  fields String containing all GroupBy fields separated be ','.
   */
  protected void addGroupByFields(String fields) {
    StringTokenizer tokenizer = new StringTokenizer(fields, ",");
    while (tokenizer.hasMoreTokens())
      this.groupBy.add(tokenizer.nextToken().trim());
  }

  /**
   * Return the instance with the specified ID.
   * @parm Instance ID. Instances are identified by the values of the d2r:groupBy fields.
   * return Instance with the specified ID.
   */
  protected Instance getInstanceById(String id) {
    return (Instance)this.instances.get(id);
  }
}
