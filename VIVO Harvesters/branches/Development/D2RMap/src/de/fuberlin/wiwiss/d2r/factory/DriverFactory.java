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


//Standard Java Libraries
import java.net.*;
import java.sql.*;

//Log4J Libraries
import org.apache.log4j.Logger;

//D2R Libraries
import de.fuberlin.wiwiss.d2r.exception.FactoryException;


/**
 * Factory class used to instantiate instances of JDBC Drivers.
 * Implementations are defined in the factories properties file.
 * Implementations can also be instantiated using the class name and the
 * implementation's classpath.
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
public class DriverFactory extends AbstractFactory {

  /** log4j logger used for this class */
  private static Logger log = Logger.getLogger(DriverFactory.class);

  /** The singleton instance of this class */
  private static DriverFactory instance = null;


  /**
   * Constructor.  Creates and initialises the factory.
   */
  protected DriverFactory() throws FactoryException {

  }

  /**
   * Get the Factory frame instance.
   *
   * @return The Factory singleton instance.
   *
   * @throws FactoryException
   */
  public static DriverFactory getInstance() throws FactoryException {

    if (instance == null) {

      synchronized (DriverFactory.class) {

        if (instance == null) {

          // Create the factory
          instance = new DriverFactory();

          // Initialise the factory
          instance.initializeFactory("/driverFactory.properties");
        }
      }
    }

    return instance;
  }

  /**
   * Creates a JDBC Driver instance of the specified class name. Loads and
   * instantiates the class with the System Class Loader.
   *
   * @param className String
   * @throws FactoryException
   * @return Driver
   */
  public Driver getDriverInstance(String className)
      throws FactoryException {

    //value to be returned
    Driver driver = null;

    try {

      //Get the driver class
      Class driverClass = Class.forName(className);

      //get instance of Class from the System ClassLoader
      Object object = driverClass.newInstance();

      if ( (object != null)
          && (object instanceof Driver)) {

        driver = (Driver) object;
      }
      else {

        throw new FactoryException("Could not create Driver instance. " +
                                   className + " may not be a valid " +
                                   "implementation of java.sql.Driver. ");
      }
    }
    catch (ClassNotFoundException classException) {

      throw new FactoryException("Could not find class: " + className,
                                 classException);
    }
    catch (IllegalAccessException accessException) {

      throw new FactoryException("Unable to access class: " + className,
                                 accessException);
    }
    catch (InstantiationException instantiationException) {

      throw new FactoryException("Could not instantiate class: " + className,
                                 instantiationException);
    }

    return driver;
  }

  /**
   * Creates a JDBC Driver instance of the specified class name from the
   * supplied classpath.
   *
   * @param className String
   * @param classPath URL
   * @throws FactoryException
   * @return Driver
   */
  public Driver getDriverInstance(String className, URL classPath)
      throws FactoryException {

    //value to be returned
    Driver driver = null;

    //Object returned by superclass
    Object object = super.getInstance(className, classPath);

    if ((object != null)
        && (object instanceof Driver)) {

      driver = (Driver) object;
    } else {

      throw new FactoryException("Could not create Driver instance. " +
                                 className + " may not be a valid " +
                                 "implementation of java.sql.Driver. ");
    }

    return driver;
  }

}
