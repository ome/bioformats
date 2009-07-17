//
// FormatHandler.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats;

import java.util.Vector;

import loci.common.LogTools;

/**
 * Abstract superclass of all biological file format readers and writers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/FormatHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/FormatHandler.java">SVN</a></dd></dl>
 */
public abstract class FormatHandler implements IFormatHandler {

  // -- Constants --

  /** Suffixes for supported compression types. */
  public static final String[] COMPRESSION_SUFFIXES = {"bz2", "gz"};

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

  /** Fires a warning update event. */
  protected void warn(String message) {
    status(new StatusEvent("Warning: " + message, true));
  }

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

  // -- Internal FormatHandler API methods - debugging --

  /**
   * Issues a debugging statement if the debug flag is set.
   * Convenience method for format handlers.
   */
  protected void debug(String s) { LogTools.debug(s); }

  /**
   * Issues a debugging statement if the debug flag is set and the
   * debugging level is greater than or equal to the specified level.
   */
  protected void debug(String s, int minLevel) {
    LogTools.debug(s, minLevel);
  }

  /** Issues a stack trace. Convenience method for format handlers. */
  protected void trace(String s) { LogTools.trace(s); }

  /** Issues a stack trace. Convenience method for format handlers. */
  protected void trace(Throwable t) { LogTools.trace(t); }

  /**
   * Issues a stack trace if debug flag is set.
   * Convenience method for format handlers.
   */
  protected void traceDebug(String s) { LogTools.traceDebug(s); }

  /**
   * Issues a stack trace if debug flag is set.
   * Convenience method for format handlers.
   */
  protected void traceDebug(Throwable t) { LogTools.traceDebug(t); }

  /**
   * Issues a warning if the debug flag is set.
   * Convenience method for format handlers.
   */
  protected void warnDebug(String message) { LogTools.warnDebug(message); }

  // -- IFormatHandler API methods --

  /**
   * Checks if a file matches the type of this format handler.
   * The default implementation checks filename suffixes against
   * those known for this format.
   */
  public boolean isThisType(String name) {
    return checkSuffix(name, suffixes);
  }

  /* @see IFormatHandler#getFormat() */
  public String getFormat() { return format; }

  /* @see IFormatHandler#getSuffixes() */
  public String[] getSuffixes() { return suffixes; }

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

  /** Performs suffix matching for the given filename. */
  public static boolean checkSuffix(String name, String[] suffixList) {
    String lname = name.toLowerCase();
    for (int i=0; i<suffixList.length; i++) {
      String s = "." + suffixList[i];
      if (lname.endsWith(s)) return true;
      for (int j=0; j<COMPRESSION_SUFFIXES.length; j++) {
        if (lname.endsWith(s + "." + COMPRESSION_SUFFIXES[j])) return true;
      }
    }
    return false;
  }

}
