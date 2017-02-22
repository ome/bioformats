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

import loci.formats.IFormatReader;

/**
 * A file filter for a biological file format, for use with a JFileChooser.
 */
public class FormatFileFilter extends FileFilter
  implements java.io.FileFilter, Comparable<Object>
{

  // -- Fields --

  /** Associated file format reader. */
  private IFormatReader reader;

  /** Whether it is ok to open a file to determine its type. */
  private boolean allowOpen;

  /** Description. */
  private String desc;

  // -- Constructors --

  /** Constructs a new filter that accepts files of the given reader's type. */
  public FormatFileFilter(IFormatReader reader) {
    this(reader, true);
  }

  /**
   * Constructs a new filter that accepts files of the given reader's type,
   * allowing the reader to open files only if the allowOpen flag is set.
   * @param reader The reader to use for verifying a file's type.
   * @param allowOpen Whether it is ok to open a file to determine its type.
   */
  public FormatFileFilter(IFormatReader reader, boolean allowOpen) {
    this.reader = reader;
    this.allowOpen = allowOpen;
    final StringBuilder sb = new StringBuilder(reader.getFormat());
    String[] exts = reader.getSuffixes();
    boolean first = true;
    for (int i=0; i<exts.length; i++) {
      if (exts[i] == null || exts[i].equals("")) continue;
      if (first) {
        sb.append(" (");
        first = false;
      }
      else sb.append(", ");
      sb.append("*.");
      sb.append(exts[i]);
    }
    sb.append(")");
    desc = sb.toString();
  }

  // -- FileFilter API methods --

  /** Accepts files in accordance with the file format reader. */
  @Override
  public boolean accept(File f) {
    if (f.isDirectory()) return true;
    return reader.isThisType(f.getPath(), allowOpen);
  }

  /** Returns the filter's reader. */
  public IFormatReader getReader() { return reader; }

  /** Gets the filter's description. */
  @Override
  public String getDescription() { return desc; }

  // -- Object API methods --

  /** Gets a string representation of this file filter. */
  @Override
  public String toString() { return "FormatFileFilter: " + desc; }

  // -- Comparable API methods --

  /** Compares two FileFilter objects alphanumerically. */
  @Override
  public int compareTo(Object o) {
    return desc.compareTo(((FileFilter) o).getDescription());
  }

}
