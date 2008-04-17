//
// ClassList.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * ClassList is a list of classes for use with ImageReader or ImageWriter,
 * parsed from a configuration file such as readers.txt or writers.txt.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ClassList.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ClassList.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ClassList {

  // -- Fields --

  /** Base class to which all classes are assignable. */
  private Class base;

  /** List of classes. */
  private Vector classes;

  // -- Constructor --

  /**
   * Constructs a list of classes, initially empty.
   * @param base Base class to which all classes are assignable.
   */
  public ClassList(Class base) {
    this.base = base;
    classes = new Vector();
  }

  /**
   * Constructs a list of classes from the given configuration file.
   * @param file Configuration file containing the list of classes.
   * @param base Base class to which all classes are assignable.
   * @throws IOException if the file cannot be read.
   */
  public ClassList(String file, Class base) throws IOException {
    this.base = base;
    classes = new Vector();
    if (file == null) return;

    // read classes from file
    BufferedReader in = new BufferedReader(new InputStreamReader(
      getClass().getResourceAsStream(file)));
    while (true) {
      String line = null;
      line = in.readLine();
      if (line == null) break;

      // ignore characters following # sign (comments)
      int ndx = line.indexOf("#");
      if (ndx >= 0) line = line.substring(0, ndx);
      line = line.trim();
      if (line.equals("")) continue;

      // load reader class
      Class c = null;
      try { c = Class.forName(line); }
      catch (ClassNotFoundException exc) {
        if (FormatHandler.debug) LogTools.trace(exc);
      }
      catch (NoClassDefFoundError err) {
        if (FormatHandler.debug) LogTools.trace(err);
      }
      catch (ExceptionInInitializerError err) {
        if (FormatHandler.debug) LogTools.trace(err);
      }
      catch (RuntimeException exc) {
        // HACK: workaround for bug in Apache Axis2
        String msg = exc.getMessage();
        if (msg != null && msg.indexOf("ClassNotFound") < 0) throw exc;
        if (FormatHandler.debug) LogTools.trace(exc);
      }
      if (c == null || (base != null && !base.isAssignableFrom(c))) {
        LogTools.println("Error: \"" + line + "\" is not valid.");
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
   *
   * @throws FormatException if the class is not assignable to the base class.
   */
  public void addClass(Class c) throws FormatException {
    if (base != null && !base.isAssignableFrom(c)) {
      throw new FormatException(
        "Class is not assignable to the base class");
    }
    classes.add(c);
  }

  /** Removes the given class from the list. */
  public void removeClass(Class c) {
    classes.remove(c);
  }

  /** Gets the list of classes as an array. */
  public Class[] getClasses() {
    Class[] c = new Class[classes.size()];
    classes.copyInto(c);
    return c;
  }

}
