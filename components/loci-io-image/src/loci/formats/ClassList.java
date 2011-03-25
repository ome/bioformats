//
// ClassList.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassList is a list of classes for use with ImageReader or ImageWriter,
 * parsed from a configuration file such as readers.txt or writers.txt.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/ClassList.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/ClassList.java;hb=HEAD">Gitweb</a></dd></dl>
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
   *  If null, 'file' is interpreted as an absolute path name.
   * @throws IOException if the file cannot be read.
   */
  public ClassList(String file, Class<T> base, Class<?> location)
    throws IOException
  {
    this.base = base;
    classes = new ArrayList<Class<? extends T>>();
    if (file == null) return;

    // read classes from file
    BufferedReader in = null;
    if (location == null) {
      in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    }
    else {
      in = new BufferedReader(new InputStreamReader(
        location.getResourceAsStream(file)));
    }
    while (true) {
      String line = null;
      line = in.readLine();
      if (line == null) break;

      // ignore characters following # sign (comments)
      int ndx = line.indexOf("#");
      if (ndx >= 0) line = line.substring(0, ndx);
      line = line.trim();
      if (line.equals("")) continue;

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
        continue;
      }
      classes.add(c);
    }
    in.close();
  }

  // -- ClassList API methods --

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

  /** Gets the list of classes as an array. */
  @SuppressWarnings("unchecked")
  public Class<? extends T>[] getClasses() {
    return classes.toArray(new Class[0]);
  }

  // -- Helper methods --

  @SuppressWarnings("unchecked")
  private Class<? extends T> cast(Class<?> rawClass) {
    if (!base.isAssignableFrom(rawClass)) return null;
    return (Class<? extends T>) rawClass;
  }

}
