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
import java.util.Properties;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URLClassLoader;

//Log4J Libraries
import org.apache.log4j.Logger;
import de.fuberlin.wiwiss.d2r.exception.FactoryException;
import java.net.URL;


/**
 * Factory class used to instantiate Classes. Implementations
 * are defined in the factory properties file. Implementations can also be
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
public class AbstractFactory
    extends URLClassLoader {

  /** log4j logger used for this class */
  private static Logger log = Logger.getLogger(AbstractFactory.class);

  /** The properties determining which implementation to use */
  protected static Properties properties;


  /**
   * Constructor.  Creates and initialises the factory.
   */
  protected AbstractFactory() throws FactoryException {

    //extend the System ClassLoader
    super(new URL[] {}
          , URLClassLoader.getSystemClassLoader());

    // Initialise the factory
    initializeFactory("/abstractFactory.properties");
  }

  /**
   * Initializes the factory. Reads the properties file.
   *
   * @throws FactoryException
   */
  protected void initializeFactory(String propertiesFilename) throws
      FactoryException {

    // Initialise our properties
    properties = new Properties();

    // Our data stream
    DataInputStream inputStream = null;
    try {

      // Open up an input stream to the properties file
      URL resource = this.getClass().getResource(propertiesFilename);

      //if the resource is found, open a stream
      if (resource != null) {

        inputStream = new DataInputStream(this.getClass().getResource(
            propertiesFilename).openStream());

        // Load the properties into the properties object
        properties.load(inputStream);
      }
    }
    catch (IOException ioException) {

      // An error occurred so report it and throw a factory exception
      log.error("Could not load properties from properties file.", ioException);
      throw new FactoryException(
          "Could not load properties from properties file.", ioException);
    }
    finally {

      if (inputStream != null) {

        try {

          // Try to close the stream if it is not null
          inputStream.close();
        }
        catch (IOException ioException) {

          // An error occurred so report it and throw a factory exception
          log.error("Could not shut down the input stream.", ioException);
        }
      }
    }
  }

  /**
   * Returns an instance of the specified class in the properties file
   * (If found).
   *
   * @param classType String
   * @throws FactoryException
   * @return Object
   */
  public Object getInstanceFromProperties(String className) throws
      FactoryException {

    //value to be returned
    Object object = null;

    //get the implementing class from the properties file and instantiate
    object = this.createObjectFromProperties(className);

    return object;
  }

  /**
   * Returns an instance of the specified class from the Factory's classpath
   * (If found).
   *
   * @param classType String
   * @throws FactoryException
   * @return Object
   */
  public Object getInstance(String className) throws
      FactoryException {

    //value to be returned
    Object object = null;

    try {

      //get the Class from the class Loader, create an instance
      Class objectClass = this.loadClass(className);
      object = objectClass.newInstance();
    }
    catch (ClassNotFoundException classException) {

      throw new FactoryException("Could not find class: " + className + ". " +
                                 "Class/Jar may not be in the classpath. " +
                                 "Classes/Jars may be added to the " +
                                 "ClassLoader with the 'addToClasspath()' " +
                                 " method.", classException);
    }
    catch (IllegalAccessException accessException) {

      throw new FactoryException("Could not access class: " + className + ".",
                                 accessException);
    }
    catch (InstantiationException instantiationException) {

      throw new FactoryException("Could not instantiate class: " + className +
                                 ".", instantiationException);
    }

    return object;
  }

  /**
   * Creates an instance of the specified class (className) from the classpath
   * supplied (classPath).
   *
   * @param className String
   * @param classPath String
   * @throws FactoryException
   * @return Object
   */
  public Object getInstance(String className, URL classPath) throws
      FactoryException {

    //value to be returned
    Object object = null;

    //load the class from the URL and instantiate
    object = this.getInstanceFromURL(classPath, className);

    return object;
  }

  /**
   * Creates and returns an instance of the class defined by
   * "classNameProperty" in the properties file. Returned Object needs to be
   * cast to correct type.
   *
   * @param classNameProperty String
   * @throws FactoryException
   * @return Object
   */
  private Object createObjectFromProperties(String classNameProperty) throws
      FactoryException {

    //The instance of the "classNameProperty" to be returned
    Object object = null;

    //class that classNameProperty represents from the properties file
    Class objectClass = null;

    try {

      //Get the objectClass
      objectClass = this.loadClass(properties.getProperty(classNameProperty));

      // Instantiate an instance of the class
      object = objectClass.newInstance();
    }
    catch (ClassNotFoundException classNotFoundException) {

      // An exception has occurred so report it and throw it as a factory exception
      log.error(classNameProperty + " was not found.",
                classNotFoundException);
      throw new FactoryException(classNameProperty + " was not found.",
                                 classNotFoundException);
    }
    catch (InstantiationException instantiationException) {

      // An exception has occurred so report it and throw it as a factory exception
      log.error("Could not instantiate " + classNameProperty + ".",
                instantiationException);
      throw new FactoryException("Could not instantiate " + classNameProperty
                                 + ".", instantiationException);
    }
    catch (IllegalAccessException illegalAccessException) {

      // An exception has occurred so report it and throw it as a factory exception
      log.error("Illegal access when creating " + classNameProperty + ".",
                illegalAccessException);
      throw new FactoryException("Illegal access when creating "
                                 + classNameProperty + ".",
                                 illegalAccessException);
    }

    //return the instance of the class as java.lang.Object
    return object;

  }

  //METHODS USED TO DYNAMICALLY INSTANTIATE OBJECTS (FROM OTHER JARS - URLS)

  /**
   * Loads the class (className) from the path URL (classPathURL) and returns an
   * instance of the Class.
   *
   * @param classPath URL
   * @param className String
   * @throws FactoryException
   * @return Object
   */
  protected Class getClassFromURL(URL classPath, String className) throws
      FactoryException {

    //value to be returned
    Class loadedClass = null;

    if ( (classPath != null)
        && (className != null)) {

      try {

        //get the class from the specified classpath using a seperate loader;
        URLClassLoader loader = URLClassLoader.newInstance(new URL[] {
            classPath}, ClassLoader.getSystemClassLoader());

        loadedClass = loader.loadClass(className);
      }
      catch (ClassNotFoundException classException) {

        //there was a problem creating a class from the class path
        throw new FactoryException("Could not load Class: " + className + "." +
                                   "The classpath: " + classPath +
                                   " may not be valid.", classException);
      }
    }

    return loadedClass;
  }

  /**
   * Loads the class (className) from the path URL (classPathURL) and returns an
   * instance of the Class.
   *
   * @param classPath URL
   * @param className String
   * @throws FactoryException
   * @return Object
   */
  protected Object getInstanceFromURL(URL classPath, String className) throws
      FactoryException {

    //value to be returned
    Object instance = null;

    if ( (classPath != null)
        && (className != null)) {

      try {

        //used to dynamically create an instance of the supplied Driver Class
        Class loadedClass = this.getClassFromURL(classPath, className);

        //instantiate Object
        instance = loadedClass.newInstance();

      }
      catch (IllegalAccessException accessException) {

        //there was a problem creating a class from the class name
        throw new FactoryException("Could not access Class: " + className + ".",
                                   accessException);
      }
      catch (InstantiationException instantiationException) {

        //there was a problem instantiating class
        throw new FactoryException("Could not instantiate Class: " + className +
                                   ".", instantiationException);
      }
    }

    return instance;
  }

  /**
   * Adds the URL to the ClassLoader's classpath.
   *
   * @param url URL
   */
  public void addToClasspath(URL url) {

    this.addURL(url);
  }
}
