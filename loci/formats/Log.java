//
// Log.java
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * A simple logging class.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/Log.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/Log.java">SVN</a></dd></dl>
 */
public class Log {

  // -- Constants --

  public static final String NL = System.getProperty("line.separator");

  // -- Log API methods --

  /** Main output method. Override to control how logging occurs. */
  public void print(String x) {
    // default implementation writes to the console
    System.out.print(x);
  }

  /** Flushes buffer to the log. Override to control how logging occurs. */
  public void flush() {
    // default implementation flushes the console
    System.out.flush();
  }

  public void print(boolean x) { print("" + x); }
  public void print(char x) { print("" + x); }
  public void print(double x) { print("" + x); }
  public void print(float x) { print("" + x); }
  public void print(int x) { print("" + x); }
  public void print(long x) { print("" + x); }
  public void print(Object x) { print(x.toString()); }
  public void println() { println(""); }
  public void println(boolean x) { println("" + x); }
  public void println(char x) { println("" + x); }
  public void println(double x) { println("" + x); }
  public void println(float x) { println("" + x); }
  public void println(int x) { println("" + x); }
  public void println(long x) { println("" + x); }
  public void println(Object x) { println("" + x); }
  public void println(String x) { print(x + NL); }

  public void trace(String s) { trace(new Exception(s)); }
  public void trace(Throwable t) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    t.printStackTrace(new PrintStream(out));
    println(out);
  }

}
