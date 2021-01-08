/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import loci.common.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassList is a list of classes for use with {@link ImageReader} or
 * {@link ImageWriter}, parsed from a configuration file such as readers.txt
 * or writers.txt.
 * <p>
 * When constructing a {@link ClassList} object, each line of the
 * configuration file is parsed using the following rules:
 * <ul>
 * <li>all text following a hash symbol <i>#</i> is interpreted as comments
 * and ignored</li>
 * <li>a key-value pair options can be specified for a given class by using the
 * square brackets <i>[]</i> and an equal separator <i>=</i></li>
 * <li>multiple options can be passed for each class and need to be separated
 * using a comma<i>,</i></li>
 * </ul>
 * <p>
 * For instance, constructing a {@link ClassList} object using the absolute
 * path to a configuration file containing the following lines:
 * <pre>
 * # List of classes
 * package1.class1
 * package2.class2 # a simple class
 * package3.class3[key1=value1,key2=value2] # a class with options
 * </pre>
 * will attempt to create a list of three classes . Additionally, if the
 * last class is added, two key/value pairs will be stored in the options map:
 * <i>package3.class3.key1=value</i> and <i>package3.class3.key2=value2</i>.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ClassList<T> {

  // -- Constants --

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassList.class);

  /* A string array containing a list*/
  private static final String[] KEYS = {"type"};

  // -- Fields --

  /** Base class to which all classes are assignable. */
  private Class<T> base;

  /** List of classes. */
  private List<Class<? extends T>> classes;

  /** List of options. */
  private Map<String, String> options;

  // -- Constructor --

  /**
   * Constructs a list of classes, initially empty.
   * @param base Base class to which all classes are assignable.
   */
  public ClassList(Class<T> base) {
    this.base = base;
    classes = new ArrayList<Class<? extends T>>();
    options = new HashMap<String, String>();
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
   * Parses one or more options from a string.
   * @param s  A string containing a series of options formatted as
   *           <i>key1=value1,key2=value2</i>.
   * @return a map populated with the parsed key/value pairs
   */
  public Map<String, String> parseOptions(String s)
   {
      Map<String, String> map = new HashMap<String, String>();
      StringTokenizer st1 = new StringTokenizer(s, ",");
      StringTokenizer st2;
      while (st1.hasMoreTokens()) {
        st2 = new StringTokenizer(st1.nextToken(), "=");
        if (st2.hasMoreTokens()) {
          String key = st2.nextToken();
          if (st2.hasMoreTokens()) {
            map.put(key, st2.nextToken());
          }
        }
      }
      return map;
   }

  /**
   * Parses a class from a string including options and comments.
   *
   * This function assumes the string is formatted as
   * <i>package.class[key1=value1,key2=value2] # comments</i>. Options will be
   * parsed and stored in a local map. If the class can be loaded, then each
   * key/value pair will be stored in the options as package.class.key/value.
   *
   * @param line A string containing the class, options and comments
   */
  public void parseLine(String line)
   {
      // Ignore characters following # sign (comments)
      int ndx = line.indexOf('#');
      if (ndx >= 0) line = line.substring(0, ndx);
      line = line.trim();
      if (line.equals("")) return;

      Map<String, String> o = new HashMap<String, String>();
      if (line.endsWith("]")) {
        ndx = line.indexOf('[');
        if (ndx >= 0) {
          o = parseOptions(line.substring(ndx + 1, line.length() - 1));
          line = line.substring(0, ndx).trim();
        }
      }

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
        if (!o.containsKey("type") || !o.get("type").equals("external")) {
          LOGGER.error("\"{}\" is not valid.", line);
        }
      } else {
        classes.add(c);
        for (Map.Entry<String, String> entry : o.entrySet())
        {
          addOption(line + "." + entry.getKey(), entry.getValue());
        }
      }
  }

   /**
    * Parses a list of classes from a configuration file.
    * @param file Configuration file containing the list of classes.
    * @param location Class indicating which package to search for the file.
    *        If {@code null}, 'file' is interpreted as an absolute path name.
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
   * Appends a class list which must be assignable to the base class
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
   * Prepends a class list which must be assignable to the base class
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

  /** Gets the list of options as a map. */
  public Map<String, String> getOptions() {
    return options;
  }

  /**
   * Returns whether a given key is an allowed option.
   *
   * @deprecated Use {@link #isAllowedKey(String)} instead.
   */
  @Deprecated
  public boolean isWhitelistedKey(String s) {
    return isAllowedKey(s);
  }

  /** Returns whether a given key is an allowed option.*/
  public boolean isAllowedKey(String s) {
    String key = s.substring(s.lastIndexOf(".") + 1);
    for (String k: KEYS) {
      if (key.equals(k)) return true;
    }
    return false;
  }

  /** Add a key/value pair to the list of options.*/
  public void addOption(String key, String value) {
    if (!isAllowedKey(key)) {
      LOGGER.debug("{} is not an allowed key", key);
    }
    options.put(key, value);
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
