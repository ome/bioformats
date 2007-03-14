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

import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/** Abstract superclass of all biological file format readers and writers. */
public abstract class FormatHandler implements IFormatHandler {

  // -- Fields --

  /** Name of this file format. */
  protected String format;

  /** Valid suffixes for this file format. */
  protected String[] suffixes;

  /** File filters for this file format, for use with a JFileChooser. */
  protected FileFilter[] filters;

  /** File chooser for this file format. */
  protected JFileChooser chooser;

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

  // -- Internal FormatHandler methods --

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
    }
    return false;
  }

  /* @see IFormatHandler#getFormat() */
  public String getFormat() { return format; }

  /* @see IFormatHandler#getSuffixes() */
  public String[] getSuffixes() { return suffixes; }

  /* @see IFormatHandler#getFileFilters() */
  public FileFilter[] getFileFilters() {
    if (filters == null) {
      filters = new FileFilter[] {new ExtensionFileFilter(suffixes, format)};
    }
    return filters;
  }

  /* @see IFormatHandler#getFileChooser() */
  public JFileChooser getFileChooser() {
    if (chooser == null) {
      chooser = FormatTools.buildFileChooser(getFileFilters());
    }
    return chooser;
  }

  /* @see IFormatHandler#addStatusListener(StatusListener) */
  public void addStatusListener(StatusListener l) {
    synchronized (statusListeners) {
      if (!statusListeners.contains(l)) statusListeners.add(l);
    }
  }

  /* @see IFormatHandler#removeStatusListener(StatusListener) */
  public void removeStatusListener(StatusListener l) {
    synchronized (statusListeners) {
      statusListeners.remove(l);
    }
  }

  /* @see IFormatHandler#getStatusListeners() */
  public StatusListener[] getStatusListeners() {
    synchronized (statusListeners) {
      StatusListener[] l = new StatusListener[statusListeners.size()];
      statusListeners.copyInto(l);
      return l;
    }
  }

}
