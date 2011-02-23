//
// ServiceFactory.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden and Chris Allan.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.common.services;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime instantiation of services.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/services/ServiceFactory.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/services/ServiceFactory.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ServiceFactory {

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(ServiceFactory.class);

  /** Default service properties file. */
  private static final String DEFAULT_PROPERTIES_FILE = "services.properties";

  /** Constructor cache. */
  private static Map<Class<? extends Service>, Constructor<? extends Service>>
    constructorCache =
      new HashMap<Class<? extends Service>, Constructor<? extends Service>>();

  /** Set of available services. */
  private Map<Class<? extends Service>, Class<? extends Service>>
    services =
      new HashMap<Class<? extends Service>, Class<? extends Service>>();

  /** Default service factory. */
  private static ServiceFactory defaultFactory;

  /**
   * Constructor loading service configuration from the default location.
   * @throws DependencyException If there is an error locating or reading from
   * the default configuration location.
   */
  public ServiceFactory() throws DependencyException {
    if (defaultFactory == null) {
      defaultFactory = new ServiceFactory(DEFAULT_PROPERTIES_FILE);
    }
    synchronized (defaultFactory) {
      this.services.putAll(defaultFactory.services);
    }
  }

  /**
   * Constructor loading service configuration from a given location.
   * @param path Location to load service configuration from.
   * @throws DependencyException If there is an error locating or reading from
   * <code>path</code>.
   */
  public ServiceFactory(String path) throws DependencyException {
    InputStream stream = this.getClass().getResourceAsStream(path);
    Properties properties = new Properties();
    if (stream == null) {
      throw new DependencyException(path + " not found on CLASSPATH");
    }
    try {
      properties.load(stream);
      LOGGER.debug("Loaded properties from: {}", path);
    } catch (Throwable t) {
      throw new DependencyException(t);
    }
    finally {
      try {
        stream.close();
      }
      catch (IOException e) {
        LOGGER.warn("Error closing properties file stream.", e);
      }
    }
    Set<Entry<Object, Object>> entries = properties.entrySet();
    for (Entry<Object, Object> entry : entries) {
      String interfaceName = (String) entry.getKey();
      String implementationName = (String) entry.getValue();
      Class<? extends Service> interfaceClass = null;
      Class<? extends Service> implementationClass = null;
      try {
        interfaceClass = (Class<? extends Service>)
          Class.forName((String) entry.getKey());
      }
      catch (Throwable t) {
        LOGGER.debug("CLASSPATH missing interface: {}", interfaceName, t);
        continue;
      }
      try {
        implementationClass = (Class<? extends Service>)
          Class.forName((String) entry.getValue());
      }
      catch (Throwable t) {
        LOGGER.debug(
          "CLASSPATH missing implementation or implementation dependency: {}",
          implementationName, t);
      }
      services.put(interfaceClass, implementationClass);
      LOGGER.debug("Added interface {} and implementation {}",
        interfaceClass, implementationClass);
    }

  }

  /**
   * Retrieves an instance of a given service.
   * @param type Interface type of the service.
   * @return A newly instantiated service.
   * @throws DependencyException If there is an error instantiating the
   * service instance requested.
   */
  public <T extends Service> T getInstance(Class<T> type)
    throws DependencyException
  {
    Class<T> impl = (Class<T>) services.get(type);
    if (impl == null && services.containsKey(type)) {
      throw new DependencyException(
          "Unable to instantiate service. Missing implementation or " +
          "implementation dependency", type);
    }
    if (impl == null) {
      throw new DependencyException("Unknown service type: " + type);
    }
    Constructor<T> constructor = getConstructor(impl);
    try {
      return constructor.newInstance();
    } catch (Throwable t) {
      throw new DependencyException("Unable to instantiate service", type, t);
    }
  }

  /**
   * Retrieves a constructor for a given class from the constructor cache if
   * possible.
   * @param klass Class to retrieve a constructor for.
   * @return See above.
   * @throws DependencyException If there is an error retrieving the
   * constructor.
   */
  private <T extends Service> Constructor<T> getConstructor(Class<T> klass)
    throws DependencyException
  {
    synchronized (constructorCache) {
      Constructor<? extends Service> constructor =
        constructorCache.get(klass);
      if (constructor == null) {
        try {
          Class<T> concreteClass = (Class<T>) Class.forName(klass.getName());
          constructor = concreteClass.getDeclaredConstructor();
          constructorCache.put(klass, constructor);
        }
        catch (Throwable t) {
          throw new DependencyException(
              "Unable to retrieve constructor", klass, t);
        }
      }
      return (Constructor<T>) constructor;
    }
  }

}
