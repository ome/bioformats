/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
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
      ClassLoader loader = this.getClass().getClassLoader();
      try {
        interfaceClass = (Class<? extends Service>)
          Class.forName(interfaceName, false, loader);
      }
      catch (Throwable t) {
        LOGGER.debug("CLASSPATH missing interface: {}", interfaceName, t);
        continue;
      }
      try {
        implementationClass = (Class<? extends Service>)
          Class.forName(implementationName, false, loader);
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
