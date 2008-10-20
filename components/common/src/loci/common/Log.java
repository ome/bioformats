//
// Log.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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
import java.io.PrintStream;

/**
 * A simple logging class.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/src/loci/common/Log.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/src/loci/common/Log.java">SVN</a></dd></dl>
 */
public class Log {

  // -- Constants --

  public static final String NL = System.getProperty("line.separator");

  // -- Fields --

  /** Output stream to which information should be logged. */
  protected PrintStream out = System.out;

  // -- Log API methods --

  /** Overrides the stream to which logging occurs (default is System.out). */
  public void setStream(PrintStream out) { this.out = out; }

  /** Gets the stream to which logging currenly occurs. */
  public PrintStream getStream() { return out; }

  /** Main output method. Override to control how logging occurs. */
  public void print(String x) {
    // default implementation writes to the associated print stream
    out.print(x);
  }

  /** Flushes buffer to the log. Override to control how logging occurs. */
  public void flush() {
    // default implementation flushes the associated print stream
    out.flush();
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
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    t.printStackTrace(new PrintStream(baos));
    println(baos);
  }

}
