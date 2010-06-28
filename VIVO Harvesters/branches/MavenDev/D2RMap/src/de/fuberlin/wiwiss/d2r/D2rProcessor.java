package de.fuberlin.wiwiss.d2r;

import java.util.*;
import java.io.*;
import java.sql.SQLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.*;
import com.hp.hpl.jena.rdf.model.*;
import org.apache.log4j.Logger;
import java.net.URL;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.*;
import java.sql.*;
import de.fuberlin.wiwiss.d2r.factory.ModelFactory;
import de.fuberlin.wiwiss.d2r.exception.D2RException;
import de.fuberlin.wiwiss.d2r.exception.FactoryException;
import de.fuberlin.wiwiss.d2r.factory.DriverFactory;

/**
 * D2R processor exports data from a RDBMS into an RDF model using a D2R MAP.
 * D2R MAP is a declarative, XML-based language to describe mappings between the relational
 * database model and the graph-based RDF data model. The resulting model can be serialized as RDF, N3, N-TRIPLES or exported
 * directly as Jena model. The processors is compliant with all relational databases offering JDBC or ODBC access.
 * The processor can be used in a servlet environment to dynamically publish XHTML pages
 * containing RDF, as a database connector in applications working with Jena models or as a command line tool.
 * The D2R Map language specification and usage examples are found at
 * http://www.wiwiss.fu-berlin.de/suhl/bizer/d2rmap/D2Rmap.htm.
 *
 * <BR><BR>History: 
 * <BR>07-21-2004   : Process map methods added.
 * <BR>07-21-2004   : Connection and driver accessors added. 
 * <BR>07-21-2004   : Error handling changed to Log4J.
 * <BR>09-25-2003   : Changed for Jena2.
 * <BR>01-15-2003   : Initial version of this class.
 *
 * @author Chris Bizer chris@bizer.de
 * @version V0.3
 */
public class D2rProcessor {
  private String saveAs;
  private String outputFormat;
  private String odbc;
  private String jdbc;
  private String jdbcDriver;
  private String databaseUsername;
  private String databasePassword;
  private String prepend;
  private String postpend;
  private Vector maps;
  private HashMap translationTables;
  private HashMap namespaces;
  private Model model;
  private boolean mapLoaded;

  /** log4j logger used for this class */
  private static Logger log = Logger.getLogger(D2rProcessor.class);

  /** JDBC Connection used to retrieve data */
  private Connection connection = null;

  /** Classpath of JDBC Driver (JAR) used to establish the connection */
  private URL driverClasspath = null;


  public D2rProcessor() {
    this.initialize();
  }

  private void initialize() {
    this.maps = new Vector();
    this.namespaces = new HashMap();
    this.namespaces.put(D2R.RDFNSPREFIX, D2R.RDFNS);
    this.translationTables = new HashMap();
    //this.model = new ModelMem();
    this.mapLoaded = false;
    this.outputFormat = D2R.STANDARD_OUTPUT_FORMAT;
    this.saveAs = "StandardOut";
  }

  /**
   * Command line interface. Parameters:<BR> <UL><LI>-map:filename  : path/filename of the D2R mapping file.</LI>
   * <LI>-output:filename   : path/filename of the output file.</LI>
   * <LI>-format:name 	  : Output format. Possible values are RDF/XML, RDF/XML-ABBREV, N-TRIPLES and N3.</LI> </UL>
   * @param Command line Arguments.
   */
  public static void main(String[] args) {

    String outputfilename = null;
    String mapfilename = null;
    String fileFormat = null;

    try {

      // process parameters
      for (int i = 0; i < args.length; i++) {
        if (args[i].substring(0, 8).equals("-format:")) {
          fileFormat = args[i].substring(8).trim();
        }
        else if (args[i].substring(0, 5).equals("-map:")) {
          mapfilename = args[i].substring(5).trim();
        }
        else if (args[i].substring(0, 8).equals("-output:")) {
          outputfilename = args[i].substring(8).trim();
        }
        else {
          throw new D2RException("Unknown command line argument: " + args[i]);
        }
      }

      //initialize log4j (required by main class)
      // Get the url of the log configuration from jar
      URL logConfigAddress = (D2rProcessor.class).getResource("/log4j-d2r.xml");

      if (logConfigAddress != null) {
        // Configurate using the log file
        DOMConfigurator.configure(logConfigAddress);
      }
      else {
        // Just use the basic configurator
        BasicConfigurator.configure();
      }

      //BEGIN PROCESSING
      if (log.isDebugEnabled()) {

        log.debug("Processing D2R Map: " + mapfilename + " ....");
      }

      // generate processor instance, process map
      D2rProcessor processor = new D2rProcessor();
      processor.processMap(fileFormat, mapfilename, outputfilename);

      if (log.isDebugEnabled()) {

        log.debug("processing complete.");
      }
    }
    catch (Exception ex) {

      log.error("Could not process D2R Map.", ex);
    }
  }

  /**
   * Processes a D2R map. Main processing method. Processes a D2R Map
   * (mapFilename) and outputs it to the specified (outputFileName) file in the
   * chosen format (format).
   *
   * @param format String
   * @param map String
   * @param output String
   * @throws D2RException
   */
  public void processMap(String format, String mapFilename,
                         String outputFilename) throws D2RException {

    //validate input file path
    if (mapFilename != null) {

      //input and output
      File file = new File(mapFilename);
      OutputStream outputStream = null;

      try {

        //check if a format has been specified
        if (format != null) {

          this.outputFormat = format;
        }
        else {

          this.outputFormat = D2R.STANDARD_OUTPUT_FORMAT;
        }

        //default output is System.out
        if (outputFilename != null) {

          this.saveAs = outputFilename;

          //output to file
          File outFile = new File(this.saveAs);
          outputStream = new FileOutputStream(outFile);
        }
        else {

          //default output
          this.saveAs = "System.out";
          outputStream = System.out;
        }
      }
      catch (FileNotFoundException fileException) {

        throw new D2RException("Could not get OutputStream for: " +
                               outputFilename, fileException);
      }

      //PROCESS MAP USING INPUT AND OUTPUT (format has been set)
      this.processMap(file, outputStream);
    }
    else {

      throw new D2RException("A D2R map has to be specified with the " +
                             "command line argument -map:filename.");
    }
  }

  /**
   * Processes a D2R map. Processes Map from input (inputFile) and outputs
   * to an OutputStream (outStream) in the pre-set format.
   *
   * @param format String
   * @param map String
   * @param output String
   * @throws D2RException
   */
  public void processMap(File inputFile, OutputStream outStream)
      throws D2RException {

    // validate input
    if (inputFile != null) {

      //used to write output to an output stream
      PrintWriter out = null;

      try {

        // Read D2R Map file
        this.readMap(inputFile);

        // Generate instances for all maps
        String output = this.getAllInstancesAsString();

        // write model to output
        out = new PrintWriter(outStream);
        out.println(output);
      }
      catch (java.lang.Throwable ex) {

        //re-throw any errors as a D2RException that can be displayed to the user
        throw new D2RException(ex.getMessage(), ex);
      }
      finally {

        //close the output stream
        if (out != null) {

          out.close();
        }

        //reset cached connection (assume it has been closed)
        this.connection = null;
      }
    }
    else {

      throw new D2RException("Could not process Map. File is null.");
    }
  }

  /**
   * Processes a D2R map (as a Document object) and outputs the results to the
   * specified Model (jena Model).
   *
   * @param format String
   * @param map String
   * @param output String
   * @throws D2RException
   */
  public void processMap(URL driverClasspath, Document document,
                         Model model) throws D2RException {

    // validate arguments.
    if ( (document != null)
        && (model != null)) {

      //set the connection (used elsewhere)
      this.setDriverClasspath(driverClasspath);

      try {

        //use standard format
        this.outputFormat = D2R.STANDARD_OUTPUT_FORMAT;

        //Read D2R Map (Document)
        this.readMap(document);

        //process map and output results to the supplied model
        this.outputToModel(model);
      }
      catch (java.lang.Throwable ex) {

        //re-throw any errors as a D2RException that can be displayed to the user
        throw new D2RException(ex.getMessage(), ex);
      }
    }
    else {

      throw new D2RException("Cannot process D2R map. Document and/or Model " +
                             "can not be null.");
    }
  }

  /**
   * Processes a D2R map. Processes a D2R Map from input (inputFile) and outputs
   * directly to a Jena model (model).
   *
   * @param format String
   * @param map String
   * @param output String
   * @throws D2RException
   */
  public void processMap(File inputFile, Model model)
      throws D2RException {

    // Check if a map has been specified first.
    if (inputFile != null) {

      try {

        //Read D2R Map file
        this.readMap(inputFile);

        //output to the jena model
        this.outputToModel(model);
      }
      catch (java.lang.Throwable ex) {

        //re-throw any errors as a D2RException that can be displayed to the user
        throw new D2RException(ex.getMessage(), ex);
      }
    }
    else {

      throw new D2RException("Could not process Map. File is null.");
    }
  }

  /**
   * Processes the D2R map and returns all generated instances.
   * @return RDF, N3 or N-Triples depending on the processor instruction d2r:outputFormat.
   */
  public String getAllInstancesAsString() throws D2RException {

    // Check if a map is loaded.
    if (this.mapLoaded) {

      try {

        // Clear model
        this.model = ModelFactory.getInstance().createDefaultModel();
        // Generate instances for all maps
        this.generateInstancesForAllMaps();
        // Generate properties for all instances
        this.generatePropertiesForAllInstancesOfAllMaps();

      } catch (FactoryException factoryException) {

        throw new D2RException("Could not get default Model from the " +
                               "ModelFactory.", factoryException);
      }

      //toString
      return this.serialize();
    }
    else {
      throw new D2RException(
          "A D2R map has to be read before calling getAllInstancesAsString().");
    }
  }

  /**
   * Processes the D2R map and returns a Jena model containing all generated instances.
   * @return Jena model containing all generated instances.
   */
  public Model getAllInstancesAsModel() throws D2RException {

    // Check if a map is loaded.
    if (this.mapLoaded) {

      try {

        // Clear model
        this.model = ModelFactory.getInstance().createDefaultModel();
        // Generate instances for all maps
        this.generateInstancesForAllMaps();
        // Generate properties for all instances
        generatePropertiesForAllInstancesOfAllMaps();
        // add namespaces
        Set namespaces = this.namespaces.entrySet();
        for (Iterator it = namespaces.iterator(); it.hasNext(); ) {
          java.util.Map.Entry ent = (java.util.Map.Entry) it.next();
          this.model.setNsPrefix( (String) ent.getKey(), (String) ent.getValue());
        }
      }
      catch (FactoryException factoryException) {

        throw new D2RException("Could not get default Model from the " +
                               "ModelFactory.", factoryException);
      }

      //Return model
      return this.model;
    }
    else {
      throw new D2RException(
          "A D2R map has to be read before calling getAllInstancesAsModel().");
    }
  }

  /**
   * Processes the D2R map outputting the results to the "model" parameter.
   *
   * @param model Model to save instances to.
   * @throws D2RException
   */
  public void outputToModel(Model model) throws
      D2RException {

    // Momento Model object (maintains model's state)
    Model originalModel = this.model;

    if (log.isDebugEnabled()) {

      log.debug("Processing map to model.");
    }

    // Check if a map is loaded and ensure parameter is valid
    if ( (this.mapLoaded)
        && (model != null)) {

      // use model parameter
      this.model = model;

      // Generate instances for all maps
      this.generateInstancesForAllMaps();

      // Generate properties for all instances
      this.generatePropertiesForAllInstancesOfAllMaps();

      // add namespaces
      Set namespaces = this.namespaces.entrySet();

      for (Iterator it = namespaces.iterator(); it.hasNext(); ) {

        java.util.Map.Entry ent = (java.util.Map.Entry) it.next();
        this.model.setNsPrefix( (String) ent.getKey(), (String) ent.getValue());
      }
    }
    else {

      throw new D2RException("A D2R map has to be read before calling " +
                             "getAllInstancesAsModel().");
    }

    //reset model member
    this.model = originalModel;
  }

  /**
   * Processes the D2R map and returns all instances generated by the specified map.
   * @param ID of the d2r:ClassMap defined by the d2r:id or d2r:type attributes.
   * @return RDF, N3 or N-Triples depending on the selected output format.
   */
  // public String getMapInstancesAsString(String MapID) {
  //return " ";
  // }

  /**
   * Processes the D2R map and returns all instances generated by the specified map.
   * @param ID of the d2r:ClassMap defined by the d2r:id or d2r:type attributes.
   * @return Jena model containing all generated instances.
   */
  // public Model getMapInstancesAsModel(String MapID) {
  // }

  /**
   * Processes the D2R map and returns only the instance with the specified URI.
   * @param URI or bNode identifier of the instance.
   * @return Jena resource object.
   */
  //public Resource getInstanceByURI(String URI) {
  //}

  /**
   * Processes the D2R map and returns only the instance with the specified URI.
   * @param URI or bNode identifier of the instance.
   * @return RDF, N3 or N-Triples depending on the selected output format.
   */
  //public String getInstanceByURIAsString(String URI) {
  //		return " ";
  //}

  /**
   * Processes the D2R map and returns only the instance with the specified ID.
   * @param Instance ID. Instances are identified by the values of the d2r:groupBy fields.
   * @return Jena resource object.
   */
  //public Resource getInstanceByID(String ID) {
  //}

  /**
   * Processes the D2R map and returns only the instance with the specified ID.
   * @param Instance ID. Instances are identified by the values of the d2r:groupBy fields.
   * @return RDF, N3 or N-Triples depending on the selected output format.
   */
  //public String getInstanceByIDAsString(String ID) {
  //   return " ";
  //}

  /**
   * Sets the output Format. Possible values are: RDF/XML, RDF/XML-ABBREV, N-TRIPLE, N3
   * @param Output format.
   */
  public void setOutputFormat(String format) {
    this.outputFormat = format;
  }

  /** Frees all resources. */
  public void dropMap() {
    this.initialize();
  }

  /** Serializes model to string and includes the content of the d2r:Prepend and d2r:Postpend statements. */
  private String serialize() throws D2RException {
    try {
      String ser = "";
      if (this.prepend != null) ser += this.prepend;
      ser += this.modelToString();
      if (this.postpend != null) ser += this.postpend;
      return ser;
    }
    catch (D2RException ex) {
      throw ex;
    }
  }

  /**
   * Reads an D2R Map from the filesystem.
   * @param filename of the D2R Map
   */
  public void readMap(String filename) throws IOException, D2RException {

    //Map file
    File file = new File(filename);

    //read the Map file
    this.readMap(file);
  }

  /**
   * Reads a D2R Map from File.
   * @param filename of the D2R Map
   */
  public void readMap(File file) throws IOException, D2RException {

    //parsed file
    Document document;

    try {

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

      // Select type of parser
      //factory.setValidating(true);
      factory.setNamespaceAware(true);

      // Read document into DOM
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse(file);

      //read the Document
      this.readMap(document);
    }
    catch (SAXParseException spe) {
      throw new IOException("Error while parsing XML file: " + "line " +
                            spe.getLineNumber() +
                            ", uri: " + spe.getSystemId() + ", reason: " +
                            spe.getMessage());
    }
    catch (SAXException sxe) {
      throw new IOException("Error while parsing XML file.");
    }
    catch (ParserConfigurationException pce) {
      throw new IOException("Error while building XML parser.");
    }
  }

  /**
   * Reads a D2R Map as a Document.
   * @param filename of the D2R Map
   */
  public void readMap(Document document) throws IOException, D2RException {

    //read the document object
    if (document != null) {

      // Read namespaces
      NodeList list = document.getElementsByTagNameNS(D2R.D2RNS, "Namespace");
      int numNodes = list.getLength();
      for (int i = 0; i < numNodes; i++) {
        Element elem = (Element) list.item(i);
        this.namespaces.put(elem.getAttributeNS(D2R.D2RNS, "prefix"),
                            elem.getAttributeNS(D2R.D2RNS, "namespace"));
      }

      // Read database connection
      list = document.getElementsByTagNameNS(D2R.D2RNS, "DBConnection");
      Element elem = (Element) list.item(0);
      if (elem.hasAttributeNS(D2R.D2RNS, "odbcDSN"))
        this.odbc = elem.getAttributeNS(D2R.D2RNS, "odbcDSN");
      if (elem.hasAttributeNS(D2R.D2RNS, "jdbcDSN"))
        this.jdbc = elem.getAttributeNS(D2R.D2RNS, "jdbcDSN");
      if (elem.hasAttributeNS(D2R.D2RNS, "jdbcDriver"))
        this.jdbcDriver = elem.getAttributeNS(D2R.D2RNS, "jdbcDriver");
      if (elem.hasAttributeNS(D2R.D2RNS, "username"))
        this.databaseUsername = elem.getAttributeNS(D2R.D2RNS, "username");
      if (elem.hasAttributeNS(D2R.D2RNS, "password"))
        this.databasePassword = elem.getAttributeNS(D2R.D2RNS, "password");

      // Read prepend/postpend
      list = document.getElementsByTagNameNS(D2R.D2RNS, "Prepend");
      if (list.getLength() != 0) {
        if (list.getLength() != 1)
          throw new D2RException("Only one Prepend statement allowed.");
        elem = (Element) list.item(0);
        this.prepend = elem.getFirstChild().getNodeValue();
      }
      list = document.getElementsByTagNameNS(D2R.D2RNS, "Postpend");
      if (list.getLength() != 0) {
        if (list.getLength() != 1)
          throw new D2RException("Only one Postpend statement allowed.");
        elem = (Element) list.item(0);
        this.postpend = elem.getFirstChild().getNodeValue();
      }

      // Read processor messages
      list = document.getElementsByTagNameNS(D2R.D2RNS, "ProcessorMessage");
      numNodes = list.getLength();
      for (int i = 0; i < numNodes; i++) {
        elem = (Element) list.item(i);
        if (elem.hasAttributeNS(D2R.D2RNS, "saveAs"))
          this.saveAs = elem.getAttributeNS(D2R.D2RNS, "saveAs").trim();
        if (elem.hasAttributeNS(D2R.D2RNS, "outputFormat"))
          this.outputFormat = elem.getAttributeNS(D2R.D2RNS, "outputFormat").
              trim();
      }

      // Read translation tables
      list = document.getElementsByTagNameNS(D2R.D2RNS, "TranslationTable");
      numNodes = list.getLength();
      for (int i = 0; i < numNodes; i++) {
        elem = (Element) list.item(i);
        String tableId = elem.getAttributeNS(D2R.D2RNS, "id").trim();
        HashMap table = new HashMap();
        // Read Translations
        NodeList translationList = elem.getElementsByTagNameNS(D2R.D2RNS,
            "Translation");
        int numTranslationNodes = translationList.getLength();
        for (int j = 0; j < numTranslationNodes; j++) {
          Element translation = (Element) translationList.item(j);
          table.put(translation.getAttributeNS(D2R.D2RNS, "key").trim(),
                    translation.getAttributeNS(D2R.D2RNS, "value").trim());
        }
        this.translationTables.put(tableId, table);
      }

      // Read maps
      list = document.getElementsByTagNameNS(D2R.D2RNS, "ClassMap");
      numNodes = list.getLength();
      for (int i = 0; i < numNodes; i++) {
        elem = (Element) list.item(i);
        Map cMap = new Map();
        // Read type attribute
        if (elem.hasAttributeNS(D2R.D2RNS, "type")) {
          cMap.setId(elem.getAttributeNS(D2R.D2RNS, "type").trim());
          // add rdf:type bridge
          ObjectPropertyBridge typeBridge = new ObjectPropertyBridge();
          typeBridge.setProperty("rdf:type");
          typeBridge.setValue(elem.getAttributeNS(D2R.D2RNS, "type").trim());
          cMap.addBridge(typeBridge);
        }
        // Read id attribute
        if (elem.hasAttributeNS(D2R.D2RNS, "id"))
          cMap.setId(elem.getAttributeNS(D2R.D2RNS, "id").trim());
          // Read sql attribute
        cMap.setSql(elem.getAttributeNS(D2R.D2RNS, "sql"));
        // Read groupBy attributes
        cMap.addGroupByFields(elem.getAttributeNS(D2R.D2RNS, "groupBy"));
        // Read uriPattern
        if (elem.hasAttributeNS(D2R.D2RNS, "uriPattern"))
          cMap.setUriPattern(elem.getAttributeNS(D2R.D2RNS, "uriPattern"));
        if (elem.hasAttributeNS(D2R.D2RNS, "uriColumn"))
          cMap.setUriColumn(elem.getAttributeNS(D2R.D2RNS, "uriColumn"));

          // Read datatype property mappings
        NodeList propertyList = elem.getElementsByTagNameNS(D2R.D2RNS,
            "DatatypePropertyBridge");
        int numPropertyNodes = propertyList.getLength();
        for (int j = 0; j < numPropertyNodes; j++) {
          Element propertyElement = (Element) propertyList.item(j);
          DataPropertyBridge propertyBridge = new DataPropertyBridge();
          propertyBridge.setProperty(propertyElement.getAttributeNS(D2R.D2RNS,
              "property").trim());
          if (propertyElement.hasAttributeNS(D2R.D2RNS, "column"))
            propertyBridge.setColumn(propertyElement.getAttributeNS(D2R.D2RNS,
                "column").trim());
          if (propertyElement.hasAttributeNS(D2R.D2RNS, "pattern"))
            propertyBridge.setPattern(propertyElement.getAttributeNS(D2R.
                D2RNS,
                "pattern").trim());
          if (propertyElement.hasAttributeNS(D2R.D2RNS, "value"))
            propertyBridge.setValue(propertyElement.getAttributeNS(D2R.D2RNS,
                "value").trim());
          if (propertyElement.hasAttributeNS(D2R.D2RNS, "translate"))
            propertyBridge.setTranslation(propertyElement.getAttributeNS(D2R.
                D2RNS, "translate").trim());
          if (propertyElement.hasAttributeNS(D2R.XMLNS, "lang"))
            propertyBridge.setXmlLang(propertyElement.getAttributeNS(D2R.
                XMLNS,
                "lang").trim());
          if (propertyElement.hasAttributeNS(D2R.D2RNS, "datatype"))
            propertyBridge.setDatatype(propertyElement.getAttributeNS(D2R.
                D2RNS,
                "datatype").trim());
          cMap.addBridge(propertyBridge);
        }

        // Read object property mappings
        propertyList = elem.getElementsByTagNameNS(D2R.D2RNS,
            "ObjectPropertyBridge");
        numPropertyNodes = propertyList.getLength();
        for (int j = 0; j < numPropertyNodes; j++) {
          Element propertyElement = (Element) propertyList.item(j);
          ObjectPropertyBridge propertyBridge = new ObjectPropertyBridge();
          propertyBridge.setProperty(propertyElement.getAttributeNS(D2R.D2RNS,
              "property").trim());
          if (propertyElement.hasAttributeNS(D2R.D2RNS, "column"))
            propertyBridge.setColumn(propertyElement.getAttributeNS(D2R.D2RNS,
                "column").trim());
          if (propertyElement.hasAttributeNS(D2R.D2RNS, "pattern"))
            propertyBridge.setPattern(propertyElement.getAttributeNS(D2R.
                D2RNS,
                "pattern").trim());
          if (propertyElement.hasAttributeNS(D2R.D2RNS, "value"))
            propertyBridge.setValue(propertyElement.getAttributeNS(D2R.D2RNS,
                "value").trim());
          if (propertyElement.hasAttributeNS(D2R.D2RNS, "translate"))
            propertyBridge.setTranslation(propertyElement.getAttributeNS(D2R.
                D2RNS, "translate").trim());
          if (propertyElement.hasAttributeNS(D2R.D2RNS, "referredClass"))
            propertyBridge.setReferredClass(propertyElement.getAttributeNS(
                D2R.
                D2RNS, "referredClass").trim());
          if (propertyElement.hasAttributeNS(D2R.D2RNS, "referredGroupBy"))
            propertyBridge.setReferredGroupBy(propertyElement.getAttributeNS(
                D2R.D2RNS, "referredGroupBy").trim());
          cMap.addBridge(propertyBridge);
        }
        this.maps.add(cMap);
      }

      this.mapLoaded = true;
    }
  }

  /** Generated instances for all D2R maps. */
  private void generateInstancesForAllMaps() throws D2RException {
    for (Iterator it = this.maps.iterator(); it.hasNext(); ) {
      Map map = (Map) it.next();
      map.generateInstances(this);
    }
  }

  /**
   * Uses a Jena writer to serialize model to RDF, N3 or N-TRIPLES.
   * @return serialization of model
   */
  private String modelToString() throws D2RException {

    try {

      StringWriter writer = new StringWriter();
      Set namespaces = this.namespaces.entrySet();
      for (Iterator it = namespaces.iterator(); it.hasNext(); ) {
        java.util.Map.Entry ent = (java.util.Map.Entry) it.next();
        this.model.setNsPrefix( (String) ent.getKey(), (String) ent.getValue());
      }

      if (log.isDebugEnabled()) {

        log.debug("Converting Model to String. outputFormat: " +
                  this.outputFormat);
      }

      this.model.write(writer, this.outputFormat);
      return writer.toString();

    }
    catch (RDFException ex) {
      throw new D2RException("Error while converting Model to String.");
    }
  }

  /** Generated properties for all instances of all D2R maps. */
  private void generatePropertiesForAllInstancesOfAllMaps() throws D2RException {
    for (Iterator it = this.maps.iterator(); it.hasNext(); ) {
      Map map = (Map) it.next();
      map.generatePropertiesForAllInstances(this);
    }
  }

  /**
   * If a valid Connection has previously been set/created, it will be returned.
   * Otherwise a new connection will be made and will be cached for the next
   * call.
   *
   * NOTE: It is assumed the connection will be closed (or set to null) when
   * it is no longer needed (processing is complete).
   *
   * @throws D2RException
   * @return Connection
   */
  public Connection getConnection() throws D2RException {

    //value to be returned
    Connection con = null;

    //if there is a previous connection use it, otherwise try to create
    //one using the details supplied
    try {

      //validate connection
      if ( (this.connection != null)
          && (!this.connection.isClosed())) {

        if (log.isDebugEnabled()) {

          log.debug("Retreving existing connection.");
        }

        //use existing connection
        con = this.connection;
      }
      else {

        // Connect to database
        String url = "";

        //setup required information
        if (this.getOdbc() != null) {

          url = "jdbc:odbc:" + this.getOdbc();
        }
        else if (this.getJdbc() != null) {

          url = this.getJdbc();
        }

        if (log.isDebugEnabled()) {

          log.debug("Creating new connection. URL: " + url);
        }

        //make a new connection
        if (url != "") {

          //Driver used to establish connection
          Driver driver = this.createDriver();

          //use the Driver to establish a connection
          Properties connectionProperties = new Properties();

          //add the username and password to the properties
          if (this.getDatabaseUsername() != null &&
              this.getDatabasePassword() != null) {

            connectionProperties.setProperty("user", this.getDatabaseUsername());
            connectionProperties.setProperty("password",
                                             this.getDatabasePassword());
          }
          else {

            //no username/password supplied, used empty values
            connectionProperties.setProperty("user", "");
            connectionProperties.setProperty("password", "");
          }

          //connect to the URL using the Driver
          if (driver != null) {

            con = driver.connect(url, connectionProperties);
          } else {

            throw new D2RException("Could not establish Connection. " +
                                   "Cannot obtain Driver.");
          }
        }
        else {
          throw new D2RException(
              "Could not connect to database because of missing URL.");
        }

        //cache connection
        this.setConnection(con);
      }
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

      //re-throw any errors as a D2RException that can be displayed to the user
      throw new D2RException(ex.getMessage(), ex);
    }

    if (log.isDebugEnabled()) {

      log.debug("Returning connection: " + con);
    }

    return con;
  }

  /**
   * Creates a new JDBC Driver from this Object's Connection properties.
   *
   * @throws D2RException
   * @return Driver
   */
  private Driver createDriver() throws D2RException {

    //value to be returned
    Driver driver = null;

    //name of Driver Class
    String driverClass = null;

    try {

      //get required information
      if (this.getOdbc() != null) {

        driverClass = "sun.jdbc.odbc.JdbcOdbcDriver";
      }
      else if (this.getJdbcDriver() != null) {

        driverClass = this.getJdbcDriver();
      }
      else {

        throw new D2RException("Could not connect to database because of " +
                               "missing Driver.");
      }

      //if there is a classpath supplied, use it to instantiate Driver
      if (this.getDriverClasspath() != null) {

        //dynamically load and instantiate Driver from the classPath URL
        driver = DriverFactory.getInstance().getDriverInstance(driverClass,
            this.getDriverClasspath());
      }
      else {

        //attempt to load and instantiate Driver from the current classpath
        driver = DriverFactory.getInstance().getDriverInstance(driverClass);
      }
    }
    catch (FactoryException factoryException) {

      throw new D2RException("Could not instantiate Driver class.",
                             factoryException);
    }

    return driver;
  }

  /**
   * Sets the Connection member.
   *
   * @param connection Connection
   */
  public void setConnection(Connection connection) {

    this.connection = connection;
  }

  /**
   * Sets the driver classpath member.
   *
   * @param connection Connection
   */
  public void setDriverClasspath(URL classpath) {

    this.driverClasspath = classpath;
  }

  /**
   * Returns the classpath that is used to load JDBC Drivers.
   *
   * @return URL
   */
  public URL getDriverClasspath(){

    return this.driverClasspath;
  }

  /**
   * Returns an vector containing all D2R maps.
   * @return Vector with all maps.
   */
  protected Vector getMaps() {
    return maps;
  }

  /**
   * Returns the D2R map identified by the id parameter.
   * @return D2R Map.
   */
  protected Map getMapById(String id) {
    Map referredMap;
    for (Iterator it = this.getMaps().iterator(); it.hasNext(); ) {
      Map itmap = (Map) it.next();
      if (itmap.getId().equals(id)) {
        referredMap = itmap;
        return referredMap;
      }
    }
    return null;
  }

  /**
   * Returns an HashMap containing all translation tables.
   * @return Vector with all maps.
   */
  protected HashMap getTranslationTables() {
    return translationTables;
  }

  /**
   * Returns a reference to the Jena model.
   * @return Jena Model.
   */
  protected Model getModel() {
    return this.model;
  }

  /**
   * Returns the ODBC data source name.
   * @return odbcDSN
   */
  protected String getOdbc() {
    return this.odbc;
  }

  /**
   * Returns the JDBC data source name.
   * @return jdbcDSN
   */
  protected String getJdbc() {
    return this.jdbc;
  }

  /**
   * Returns the JDBC driver.
   * @return jdbcDriver
   */
  protected String getJdbcDriver() {
    return this.jdbcDriver;
  }

  /**
   * Returns the database username.
   * @return username
   */
  protected String getDatabaseUsername() {
    return this.databaseUsername;
  }

  /**
   * Returns the database password.
   * @return password
   */
  protected String getDatabasePassword() {
    return this.databasePassword;
  }

  /**
   * Translates a qName to an URI using the namespace mapping of the D2R map.
   * @param qName Qualified name to be translated.
   * @return the URI of the qualified name.
   */
  protected String getNormalizedURI(String qName) {
    String prefix = D2rUtil.getNamespacePrefix(qName);
    String URIprefix = (String)this.namespaces.get(prefix);
    if (URIprefix != null) {
      String localname = D2rUtil.getLocalName(qName);
      return URIprefix + localname;
    }
    else {
      return qName;
    }
  }
}
