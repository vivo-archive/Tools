package de.fuberlin.wiwiss.d2r;

import org.joseki.vocabulary.JosekiVocab;

import com.hp.hpl.jena.rdf.model.*;

import org.joseki.server.SourceController;
import org.joseki.server.ModelSource;
import org.joseki.server.source.ModelSourcePermanent;
import org.joseki.server.module.Loadable;
import org.apache.log4j.Logger;
import java.io.*;

/** Joseki SourceController for the D2R MAP.
 *
 * The Joseki-D2R SourceController allows you to use RDF models build
 * with D2R MAP within the Joseki RDF Server. Joseki is a server for
 * publishing RDF models on the web (see: http://www.joseki.org/).
 * For details about installing Joseki and D2R MAP together see
 * http://www.wiwiss.fu-berlin.de/suhl/bizer/d2rmap/usecases/joseki/HowTo-D2R4Joseki2.pdf
 *
 * <BR><BR>History: 
 * <BR>07-21-2004   : Error handling changed to Log4J.
 * <BR>09-25-2003   : Initial version of this class.
 * @author Chris Bizer chris@bizer.de
 * @version V0.2
 */

public class SourceControllerD2R
    implements SourceController, Loadable {
  static Logger logger = Logger.getLogger(SourceControllerD2R.class.getName());
  static Model m_model = ModelFactory.createDefaultModel();
  static final Property d2rmapfile = m_model.createProperty(
      "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RMap/0.1#map");

  String serverURI;

  // ----------------------------------------------------------
  // -- Loadable interface

  public String getInterfaceURI() {
    return JosekiVocab.SourceController.getURI();
  }

  public void init(Resource binding, Resource implementation) {}

  // ----------------------------------------------------------
  // -- SourceController interface

  // Called once, during configuration
  public ModelSource createSourceModel(Resource description, String _serverURI) {
    serverURI = _serverURI;

    // Get filename of D2R map
    String map;
    try {
      map = description.getRequiredProperty(d2rmapfile).getString();
    }
    catch (RDFException rdfEx) {
      logger.warn("Failed to gather all the D2R MAP information: " + rdfEx);
      return null;
    }

    // Build model using the D2R map
    logger.info("Building model from D2R map: " + map);
    try {
      D2rProcessor processor = new D2rProcessor();
      processor.readMap(map);
      Model model = processor.getAllInstancesAsModel();
      return new ModelSourcePermanent(this, model, serverURI);

    }
    catch (de.fuberlin.wiwiss.d2r.exception.D2RException d2rex) {
      logger.warn("D2R Exception caught: " + d2rex.getMessage(), d2rex);
      return null;
    }
    catch (IOException ioex) {
      logger.warn("IO Exception caught: " + ioex.getMessage(), ioex);
      return null;
    }
    catch (java.lang.Throwable ex) {
      logger.warn("Exception_caught: " + ex.getMessage(), ex);
      return null;
    }
  }

  public String getServerURI() {
    return serverURI;
  }

  // Called when used.
  public void activate() {
    return;
  }

  // Called when not in use any more.
  public void deactivate() {
    return;
  }

  // Called each time a source needs to be built.
  public Model buildSource() {
    logger.warn("Attempt to build a database source");
    return null;
  }

  // Called when released (if releasable)
  public void releaseSource() {
    logger.warn("Attempt to release a database source");
  }
}
