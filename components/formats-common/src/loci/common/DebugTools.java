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

package loci.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * A utility class with convenience methods for debugging.
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
   * Attempts to enable SLF4J logging via logback or log4j
   * without an external configuration file.
   *
   * @param level A string indicating the desired level
   *   (i.e.: ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN).
   * @return true iff logging was successfully enabled
   */
  public static synchronized boolean enableLogging(String level) {
    final String[][] toolClasses = new String[][] {
      new String[] {"loci.common.", "LogbackTools"},
      new String[] {"loci.common.", "Log4jTools"}
    };

    for (String[] toolClass : toolClasses) {
      try {
        Class<?> k = Class.forName(toolClass[0] + toolClass[1]);
        Method m = k.getMethod("enableLogging", String.class);
        m.invoke(null, level);
        return true;
      }
      catch (Throwable t) {
        // no-op. Ignore error and try the next class.
      }
    }
    return false;
  }

  /**
   * Enable SLF4J logging using logback, in the context of ImageJ.
   * This allows logging events to be echoed to the ImageJ status bar,
   * regardless of how the logging configuration file was set up.
   *
   * @param debug true if debug-level output should be shown
   * @return whether or not ImageJ log enabling was successful
   */
  public static synchronized boolean enableIJLogging(boolean debug) {
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      r.exec("import loci.common.LogbackTools");
      r.exec("import loci.plugins.util.IJStatusEchoer");
      r.exec("appender = new IJStatusEchoer()");
      r.setVar("debug", debug);
      r.exec("LogbackTools.enableIJLogging(debug, appender)");
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
