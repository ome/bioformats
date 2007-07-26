//
// FormatHandler.java
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

import java.io.*;
import java.util.Vector;

/**
 * Abstract superclass of all biological file format readers and writers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/FormatHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/FormatHandler.java">SVN</a></dd></dl>
 */
public abstract class FormatHandler implements IFormatHandler {

  // -- Static fields --

  /** Debugging flag. */
  public static boolean debug = false;

  /** Debugging level. 1=basic, 2=extended, 3=everything, 4=insane. */
  public static int debugLevel = 1;

  // -- Fields --

  /** Name of this file format. */
  protected String format;

  /** Valid suffixes for this file format. */
  protected String[] suffixes;

  /** List of status listeners. */
  protected Vector statusListeners = new Vector();

  /** Name of current file. */
  protected String currentId;

  // -- Constructors --

  /** Constructs a format handler with the given name and default suffix. */
  public FormatHandler(String format, String suffix) {
    this(format, suffix == null ? null : new String[] {suffix});
  }

  /** Constructs a format handler with the given name and default suffixes. */
  public FormatHandler(String format, String[] suffixes) {
    this.format = format;
    this.suffixes = suffixes == null ? new String[0] : suffixes;
  }

  // -- Internal FormatHandler API methods --

  /** Fires a status update event. */
  protected void status(String message) {
    status(new StatusEvent(message));
  }

  /** Fires a status update event. */
  protected void status(int progress, int maximum, String message) {
    status(new StatusEvent(progress, maximum, message));
  }

  /** Fires a status update event. */
  protected void status(StatusEvent e) {
    StatusListener[] l = getStatusListeners();
    for (int i=0; i<l.length; i++) l[i].statusUpdated(e);
  }

  /** Issues a debugging statement. Convenience method for format handlers. */
  protected void debug(String s) {
    String name = getClass().getName();
    String prefix = "loci.formats.";
    if (name.startsWith(prefix)) {
      name = name.substring(name.lastIndexOf(".") + 1);
    }
    String msg = System.currentTimeMillis() + ": " + name + ": " + s;
    if (debugLevel > 3) LogTools.trace(msg);
    else LogTools.println(msg);
  }

  /** Issues a stack trace. Convenience method for format handlers. */
  protected void trace(String s) { LogTools.trace(s); }

  /** Issues a stack trace. Convenience method for format handlers. */
  protected void trace(Throwable t) { LogTools.trace(t); }

  // -- IFormatHandler API methods --

  /**
   * Checks if a file matches the type of this format handler.
   * The default implementation checks filename suffixes against
   * those known for this format.
   */
  public boolean isThisType(String name) { return isThisType(name, true); }

  /**
   * Checks if a file matches the type of this format handler.
   * The default implementation checks filename suffixes against
   * those known for this format (the open parameter does nothing).
   * @param open If true, and the file extension is insufficient to determine
   *   the file type, the (existing) file is opened for further analysis.
   *   Does nothing in the default implementation.
   */
  public boolean isThisType(String name, boolean open) {
    String lname = name.toLowerCase();
    for (int i=0; i<suffixes.length; i++) {
      if (lname.endsWith("." + suffixes[i])) return true;
      if (lname.endsWith("." + suffixes[i] + ".gz")) return true;
      if (lname.endsWith("." + suffixes[i] + ".bz2")) return true;
      if (lname.endsWith("." + suffixes[i] + ".zip")) return true;
    }
    return false;
  }

  /* @see IFormatHandler#getFormat() */
  public String getFormat() { return format; }

  /* @see IFormatHandler#getSuffixes() */
  public String[] getSuffixes() { return suffixes; }

  /* @see IFormatHandler#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    setId(id, false);
  }

  // -- StatusReporter API methods --

  /* @see StatusReporter#addStatusListener(StatusListener) */
  public void addStatusListener(StatusListener l) {
    synchronized (statusListeners) {
      if (!statusListeners.contains(l)) statusListeners.add(l);
    }
  }

  /* @see StatusReporter#removeStatusListener(StatusListener) */
  public void removeStatusListener(StatusListener l) {
    synchronized (statusListeners) {
      statusListeners.remove(l);
    }
  }

  /* @see StatusReporter#getStatusListeners() */
  public StatusListener[] getStatusListeners() {
    synchronized (statusListeners) {
      StatusListener[] l = new StatusListener[statusListeners.size()];
      statusListeners.copyInto(l);
      return l;
    }
  }

  // -- Utility methods --

  /** Toggles debug mode (more verbose output and error messages). */
  public static void setDebug(boolean debug) {
    FormatHandler.debug = debug;
  }

  /**
   * Toggles debug mode verbosity (which kinds of output are produced).
   * @param debugLevel 1=basic, 2=extended, 3=everything.
   */
  public static void setDebugLevel(int debugLevel) {
    FormatHandler.debugLevel = debugLevel;
  }

}
