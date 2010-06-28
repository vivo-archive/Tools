/*
 * The contents of this file are subject to the GNU Lesser General Public
 * License Version 2.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/lgpl-license.php
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is D2R Map.
 *
 * The Initial Developer of the Original Code is Plugged In Software Pty
 * Ltd (http://www.pisoftware.com, mailto:info@pisoftware.com). Portions
 * created by Plugged In Software Pty Ltd are Copyright (C) 2001,2002
 * Plugged In Software Pty Ltd. All Rights Reserved.
 *
 * Contributor(s): Robert Turner, Chris Bizer.
 *
 * [NOTE: The text of this Exhibit A may differ slightly from the text
 * of the notices in the Source Code files of the Original Code. You
 * should use the text of this Exhibit A rather than the text found in the
 * Original Code Source Code for Your Modifications.]
 *
 */
package de.fuberlin.wiwiss.d2r.factory;

//Jena Libraries
import com.hp.hpl.jena.rdf.model.Model;

//Log4J Libraries
import org.apache.log4j.Logger;
import de.fuberlin.wiwiss.d2r.exception.FactoryException;
import java.net.*;


/**
 * Factory class used to instantiate instances of Jena Models. Implementations
 * are defined in the factories properties file. Implementations can also be
 * instantiated using the class name and the implementation's classpath.
 *
 * @created 2004-05-26
 *
 * @author <a href="mailto:robert.turner@tucanatech.com">Robert Turner</a>
 *
 * @version $Revision: 1.7 $
 *
 * @modified $Date: 2004/05/21 04:51:54 $ by $Author: turnerrx $
 *
 * @company: <a href="http://www.tucanatech.com/">Tucana Technologies</a>
 *
 * @copyright &copy;2002-2003
 *   <a href="http://www.pisoftware.com/">Plugged In Software Pty Ltd</a>
 *
 */
public class ModelFactory extends AbstractFactory {

  /** log4j logger used for this class */
  private static Logger log = Logger.getLogger(ModelFactory.class);

  /** The singleton instance of this class */
  private static ModelFactory instance = null;


  /**
   * Constructor.  Creates and initialises the factory.
   */
  protected ModelFactory() throws FactoryException {

  }

  /**
   * Get the Factory frame instance.
   *
   * @return The Factory singleton instance.
   *
   * @throws FactoryException
   */
  public static ModelFactory getInstance() throws FactoryException {

    if (instance == null) {

      synchronized (ModelFactory.class) {

        if (instance == null) {

          // Create the factory
          instance = new ModelFactory();

          // Initialise the factory
          instance.initializeFactory("/modelFactory.properties");
        }
      }
    }

    return instance;
  }

  /**
   * Returns an instance of the specified Model. If the class is not a Jena
   * Model implementation or the Model could not be created, an Exception is
   * thrown.
   *
   * @param className String
   * @throws FactoryException
   * @return Model
   */
  public Model getModelInstance(String className) throws FactoryException {

    //value to be returned
    Model model = null;

    //object returned from superclass
    Object object = super.getInstance(className);

    if ((object != null)
        && (object instanceof Model)) {

      model = (Model) object;
    } else {

      throw new FactoryException("Could not create Model instance. " +
                                 className + " may not be a valid " +
                                 "implementation of " +
                                 "com.hp.hpl.jena.rdf.model.Model ");
    }

    return model;
  }

  /**
   * Returns an instance of the specified Model. If the class is not a Jena
   * Model implementation or the Model could not be created, an Exception is
   * thrown.
   *
   * @param className String
   * @throws FactoryException
   * @return Model
   */
  public Model getModelInstance(String className, URL classPath)
      throws FactoryException {

    //value to be returned
    Model model = null;

    //object returned from superclass
    Object object = super.getInstance(className, classPath);

    if ((object != null)
        && (object instanceof Model)) {

      model = (Model) object;
    } else {

      throw new FactoryException("Could not create Model instance. " +
                                 className + " may not be a valid " +
                                 "implementation of " +
                                 "com.hp.hpl.jena.rdf.model.Model ");
    }

    return model;
  }

  /**
   * Creates a an instance of Jena's default Model.
   *
   * @return The created permissions object
   */
  public Model createDefaultModel() throws FactoryException {

    //container for object
    Model model = null;

    //create a default instance from Jena's ModelFactory
    model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();

    //return the instance
    return model;
  }

}
