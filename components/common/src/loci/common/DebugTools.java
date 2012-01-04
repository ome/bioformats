//
// DebugTools.java
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

package loci.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;

/**
 * A utility class with convenience methods for debugging.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/DebugTools.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/DebugTools.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public final class DebugTools {

  // -- Constructor --

  private DebugTools() { }

  // -- DebugTools methods --

  /** Extracts the given exception's corresponding stack trace to a string. */
  public static String getStackTrace(Throwable t) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      t.printStackTrace(new PrintStream(out, false, "UTF-8"));
      return new String(out.toByteArray(), "UTF-8");
    }
    catch (IOException e) { }
    return null;
  }

  /**
   * Attempts to enable SLF4J logging via log4j
   * without an external configuration file.
   *
   * @param level A string indicating the desired level
   *   (i.e.: ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN).
   * @return true iff logging was successfully enabled
   */
  public static synchronized boolean enableLogging(String level) {
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      r.exec("import org.apache.log4j.Level");
      r.exec("import org.apache.log4j.Logger");
      r.exec("root = Logger.getRootLogger()");
      r.exec("root.setLevel(Level." + level + ")");
      Enumeration en = (Enumeration) r.exec("root.getAllAppenders()");
      if (!en.hasMoreElements()) {
        // no appenders yet; attach a simple console appender
        r.exec("import org.apache.log4j.ConsoleAppender");
        r.exec("import org.apache.log4j.PatternLayout");
        r.setVar("pattern", "%m%n");
        r.exec("layout = new PatternLayout(pattern)");
        r.exec("appender = new ConsoleAppender(layout)");
        r.exec("root.addAppender(appender)");
      }
    }
    catch (ReflectException exc) {
      return false;
    }
    return true;
  }

}
