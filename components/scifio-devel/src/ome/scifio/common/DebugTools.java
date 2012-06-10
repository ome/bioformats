/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package ome.scifio.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
      t.printStackTrace(new PrintStream(out, false, Constants.ENCODING));
      return new String(out.toByteArray(), Constants.ENCODING);
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

  /**
   * This method uses reflection to scan the values of the given class's
   * static fields, returning the first matching field's name.
   */
  public static String getFieldName(Class<?> c, int value) {
    Field[] fields = c.getDeclaredFields();
    for (int i=0; i<fields.length; i++) {
    	if (!Modifier.isStatic(fields[i].getModifiers())) continue;
      fields[i].setAccessible(true);
      try {
        if (fields[i].getInt(null) == value) return fields[i].getName();
      }
      catch (IllegalAccessException exc) { }
      catch (IllegalArgumentException exc) { }
    }
    return "" + value;
  }

}
