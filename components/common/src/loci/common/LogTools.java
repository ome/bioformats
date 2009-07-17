//
// LogTools.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert and Curtis Rueden.

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

/**
 * A utility class for logging.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/src/loci/common/LogTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/src/loci/common/LogTools.java">SVN</a></dd></dl>
 */
public final class LogTools {

  // -- Static fields --

  /** Debugging flag. */
  private static boolean debug = false;

  /** Debugging level. 1=basic, 2=extended, 3=everything, 4=insane. */
  private static int debugLevel = 1;

  /** Log object to which logging statements will be issued. */
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

  // -- Utility methods - Debugging --

  /** Toggles debug mode (more verbose output and error messages). */
  public static void setDebug(boolean debug) {
    LogTools.debug = debug;
  }

  /** Gets whether debug mode is currently enabled. */
  public static boolean isDebug() { return debug; }

  /**
   * Toggles debug mode verbosity (which kinds of output are produced).
   * @param debugLevel 1=basic, 2=extended, 3=everything, 4=insane.
   */
  public static void setDebugLevel(int debugLevel) {
    LogTools.debugLevel = debugLevel;
  }

  /**
   * Gets the current debugging level.
   * @return debugging level: 1=basic, 2=extended, 3=everything, 4=insane.
   */
  public static int getDebugLevel() { return debugLevel; }

  /**
   * Issues a debugging statement if the debug flag is set.
   * Convenience method for format handlers.
   */
  public static void debug(String s) { debug(s, 0); }

  /**
   * Issues a debugging statement if the debug flag is set and the
   * debugging level is greater than or equal to the specified level.
   */
  public static void debug(String s, int minLevel) {
    if (!debug || debugLevel < minLevel) return;

    // get calling class
    StackTraceElement[] trace = Thread.currentThread().getStackTrace();
    String className = null;
    for (StackTraceElement ste : trace) {
      String cn = ste.getClassName();
      String mn = ste.getMethodName();
      if (!cn.equals(LogTools.class.getName()) || !mn.equals("debug")) {
        className = cn;
        break;
      }
    }

    // output message
    String prefix = "loci.";
    if (className.startsWith(prefix)) {
      className = className.substring(className.lastIndexOf(".") + 1);
    }
    String msg = System.currentTimeMillis() + ": " + className + ": " + s;
    if (debugLevel > 3) trace(msg);
    else println(msg);
  }

  /** Issues a stack trace if the debug flag is set. */
  public static void traceDebug(String s) { if (debug) trace(s); }

  /** Issues a stack trace if the debug flag is set. */
  public static void traceDebug(Throwable t) { if (debug) trace(t); }

  /** Issues a warning if the debug flag is set. */
  public static void warnDebug(String message) {
    if (debug) println("Warning: " + message);
  }

}
