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

import java.util.Enumeration;

/**
 * A utility class with convenience methods for log4j.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/Log4jTools.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/Log4jTools.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public final class Log4jTools {

  // -- Constructor --

  private Log4jTools() { }

  /**
   * Attempts to enable SLF4J logging via log4j
   * without an external configuration file.
   *
   * @param level A string indicating the desired level
   *   (i.e.: ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN).
   * @return true iff logging was successfully enabled
   */
  public static synchronized boolean enableLogging(String level) {
    try {
      ReflectedUniverse r = new ReflectedUniverse();
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
    } catch (ReflectException exc) {
      return false;
    }
    return true;
  }
}
