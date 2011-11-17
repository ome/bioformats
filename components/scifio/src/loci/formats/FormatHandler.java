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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// HACK: for scan-deps.pl: The following packages are not actually "optional":
// optional org.apache.log4j, optional org.slf4j.impl,
// optional org.apache.xalan, optional org.apache.xml.serializer

/**
 * Abstract superclass of all biological file format readers and writers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/FormatHandler.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/FormatHandler.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public abstract class FormatHandler implements IFormatHandler {

  // -- Constants --

  protected static final Logger LOGGER =
    LoggerFactory.getLogger(FormatHandler.class);

  /** Suffixes for supported compression types. */
  public static final String[] COMPRESSION_SUFFIXES = {"bz2", "gz"};

  // -- Fields --

  /** Name of this file format. */
  protected String format;

  /** Valid suffixes for this file format. */
  protected String[] suffixes;

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

  /* @see IFormatHandler#getNativeDataType() */
  public Class<?> getNativeDataType() {
    // NB: Handlers use byte arrays by default as the native type.
    return byte[].class;
  }

  // -- Utility methods --

  /** Performs suffix matching for the given filename. */
  public static boolean checkSuffix(String name, String suffix) {
    return checkSuffix(name, new String[] {suffix});
  }

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
