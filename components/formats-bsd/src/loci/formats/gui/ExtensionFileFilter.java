/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.formats.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A file filter based on file extensions, for use with a JFileChooser.
 */
public class ExtensionFileFilter extends FileFilter
  implements java.io.FileFilter, Comparable
{

  // -- Fields --

  /** List of valid extensions. */
  private String[] exts;

  /** Description. */
  private String desc;

  // -- Constructors --

  /** Constructs a new filter that accepts the given extension. */
  public ExtensionFileFilter(String extension, String description) {
    this(new String[] {extension}, description);
  }

  /** Constructs a new filter that accepts the given extensions. */
  public ExtensionFileFilter(String[] extensions, String description) {
    exts = new String[extensions.length];
    System.arraycopy(extensions, 0, exts, 0, extensions.length);
    final StringBuilder sb = new StringBuilder(description);
    boolean first = true;
    for (int i=0; i<exts.length; i++) {
      if (exts[i] == null) exts[i] = "";
      if (exts[i].equals("")) continue;
      if (first) {
        sb.append(" (");
        first = false;
      }
      else sb.append(", ");
      sb.append("*.");
      sb.append(exts[i]);
    }
    if (!first) {
      sb.append(")");
    }
    desc = sb.toString();
  }

  // -- ExtensionFileFilter API methods --

  /** Gets the filter's first valid extension. */
  public String getExtension() { return exts[0]; }

  /** Gets the filter's valid extensions. */
  public String[] getExtensions() { return exts; }

  // -- FileFilter API methods --

  /** Accepts files with the proper extensions. */
  @Override
  public boolean accept(File f) {
    if (f.isDirectory()) return true;

    String name = f.getName().toLowerCase();

    for (int i=0; i<exts.length; i++) {
      if (name.endsWith(exts[i].toLowerCase())) return true;
    }

    return false;
  }

  /** Gets the filter's description. */
  @Override
  public String getDescription() { return desc; }

  // -- Object API methods --

  /** Gets a string representation of this file filter. */
  @Override
  public String toString() { return "ExtensionFileFilter: " + desc; }

  // -- Comparable API methods --

  /** Compares two FileFilter objects alphanumerically. */
  @Override
  public int compareTo(Object o) {
    return desc.compareToIgnoreCase(((FileFilter) o).getDescription());
  }

}
