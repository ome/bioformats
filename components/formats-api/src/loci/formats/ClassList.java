/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.formats;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import loci.common.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassList is a list of classes for use with ImageReader or ImageWriter,
 * parsed from a configuration file such as readers.txt or writers.txt.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ClassList<T> {

  // -- Constants --

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassList.class);

  // -- Fields --

  /** Base class to which all classes are assignable. */
  private Class<T> base;

  /** List of classes. */
  private List<Class<? extends T>> classes;

  // -- Constructor --

  /**
   * Constructs a list of classes, initially empty.
   * @param base Base class to which all classes are assignable.
   */
  public ClassList(Class<T> base) {
    this.base = base;
    classes = new ArrayList<Class<? extends T>>();
  }

  /**
   * Constructs a list of classes from the given configuration file.
   * @param file Configuration file containing the list of classes.
   * @param base Base class to which all classes are assignable.
   * @throws IOException if the file cannot be read.
   */
  public ClassList(String file, Class<T> base) throws IOException {
    this(file, base, ClassList.class);
  }

  /**
   * Constructs a list of classes from the given configuration file.
   * @param file Configuration file containing the list of classes.
   * @param base Base class to which all classes are assignable.
   * @param location Class indicating which package to search for the file.
   *        If {@code null}, 'file' is interpreted as an absolute path name.
   * @throws IOException if the file cannot be read.
   */
  public ClassList(String file, Class<T> base, Class<?> location)
    throws IOException
  {
    this(base);
    if (file == null) return;
    parseFile(file, location);
  }

  // -- ClassList API methods --

  /**
   * Parses a class from a configuration line
   * @param line A line containing the class followed by comments. This line
   *             must be formatted as "package.class  # comments".
   * @return A class parsed from the input string or {@code null}.
   */
  public void parseLine(String line)
   {
      // ignore characters following # sign (comments)
      int ndx = line.indexOf("#");
      if (ndx >= 0) line = line.substring(0, ndx);
      line = line.trim();
      if (line.equals("")) return;

      // load class
      Class<? extends T> c = null;
      try {
        Class<?> rawClass = Class.forName(line);
        c = cast(rawClass);
      }
      catch (ClassNotFoundException exc) {
        LOGGER.debug("Could not find {}", line, exc);
      }
      catch (NoClassDefFoundError err) {
        LOGGER.debug("Could not find {}", line, err);
      }
      catch (ExceptionInInitializerError err) {
        LOGGER.debug("Failed to create an instance of {}", line, err);
      }
      catch (RuntimeException exc) {
        // HACK: workaround for bug in Apache Axis2
        String msg = exc.getMessage();
        if (msg != null && msg.indexOf("ClassNotFound") < 0) throw exc;
        LOGGER.debug("", exc);
      }
      if (c == null) {
        LOGGER.error("\"{}\" is not valid.", line);
      } else {
        classes.add(c);
      }
  }

   /**
    * Parses a list of classes from a configuration file.
    * @param file Configuration file containing the list of classes.
    * @param location Class indicating which package to search for the file.
    *        If {@code null}, 'file' is interpreted as an absolute path name.
    * @return A list of classes parsed from the file
    * @throws IOException if the file cannot be read.
    */
  public void parseFile(String file, Class<?> location)
    throws IOException
  {
    // locate an input stream
    InputStream stream = null;
    if (location == null) {
      try {
        stream = new FileInputStream(file);
      } catch (FileNotFoundException e) {
        LOGGER.debug(e.getMessage());
      }
    } else stream = location.getResourceAsStream(file);
    if (stream == null) {
      LOGGER.warn("Could not find " + file);
      return;
    }

    // Read classes from file
    BufferedReader in = null;
    in = new BufferedReader(new InputStreamReader(stream, Constants.ENCODING));
    while (true) {
      String line = null;
      line = in.readLine();
      if (line == null) break;
      parseLine(line);
    }
    in.close();
  }

  /**
   * Adds the given class, which must be assignable
   * to the base class, to the list.
   */
  public void addClass(int index, Class<? extends T> c) {
    classes.add(index, c);
  }

  /**
   * Adds the given class, which must be assignable
   * to the base class, to the list.
   */
  public void addClass(Class<? extends T> c) {
    classes.add(c);
  }

  /** Removes the given class from the list. */
  public void removeClass(Class<? extends T> c) {
    classes.remove(c);
  }

  /**
   * Appends a list of classes which must be assignable to the base class
   */
  public void append(ClassList<T> c) {
    append(Arrays.asList(c.getClasses()));
  }

  /**
   * Appends a list of classes which must be assignable to the base class
   */
  public void append(List<Class<? extends T>> l) {
    for (int i = 0; i < l.size(); i++) {
      addClass(l.get(i));
    }
  }

  /**
   * Appends a list of classes which must be assignable to the base class
   */
  public void prepend(ClassList<T> c) {
    prepend(Arrays.asList(c.getClasses()));
  }

  /**
   * Prepends a list of classes which must be assignable to the base class
   */
  public void prepend(List<Class<? extends T>> l) {
    for (int i = l.size() -1; i >= 0; i--) {
      addClass(0, l.get(i));
    }
  }

  /** Gets the list of classes as an array. */
  @SuppressWarnings("unchecked")
  public Class<? extends T>[] getClasses() {
    return classes.toArray(new Class[0]);
  }

  // -- Helper methods --

  /**
   * Cast the given class to something that extends the base class.
   * @param rawClass the class to be cast
   */
  @SuppressWarnings("unchecked")
  private Class<? extends T> cast(Class<?> rawClass) {
    if (!base.isAssignableFrom(rawClass)) return null;
    return (Class<? extends T>) rawClass;
  }

}
