//
// LogTools.java
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

/**
 * A utility class for logging.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/LogTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/LogTools.java">SVN</a></dd></dl>
 */
public final class LogTools {

  // -- Static fields --

  private static Log log = new Log();

  // -- Constructor --

  private LogTools() { }

  // -- Utility methods - logging --

  public static void setLog(Log log) { LogTools.log = log; }
  public static Log getLog() { return log; }

  public static void print(boolean x) { log.print(x); }
  public static void print(char x) { log.print(x); }
  public static void print(double x) { log.print(x); }
  public static void print(float x) { log.print(x); }
  public static void print(int x) { log.print(x); }
  public static void print(long x) { log.print(x); }
  public static void print(Object x) { log.print(x); }
  public static void print(String x) { log.print(x); }
  public static void println() { log.println(); }
  public static void println(boolean x) { log.println(x); }
  public static void println(char x) { log.println(x); }
  public static void println(double x) { log.println(x); }
  public static void println(float x) { log.println(x); }
  public static void println(int x) { log.println(x); }
  public static void println(long x) { log.println(x); }
  public static void println(Object x) { log.println(x); }
  public static void println(String x) { log.println(x); }

  public static void trace(String s) { log.trace(s); }
  public static void trace(Throwable t) { log.trace(t); }

  public static void flush()  { log.flush(); }

}
