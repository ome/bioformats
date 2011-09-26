//
// IFormatHandler.java
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

import java.io.IOException;

/**
 * Interface for all biological file format readers and writers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/IFormatHandler.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/IFormatHandler.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public interface IFormatHandler {

  /** Checks if the given string is a valid filename for this file format. */
  boolean isThisType(String name);

  /** Gets the name of this file format. */
  String getFormat();

  /** Gets the default file suffixes for this file format. */
  String[] getSuffixes();

  /**
   * Returns the native data type of image planes for this reader, as returned
   * by {@link IFormatReader#openPlane} or {@link IFormatWriter#savePlane}.
   * For most readers this type will be a byte array; however, some readers
   * call external APIs that work with other types such as
   * {@link java.awt.image.BufferedImage}.
   */
  Class<?> getNativeDataType();

  /** Sets the current file name. */
  void setId(String id) throws FormatException, IOException;

  /** Closes currently open file(s) and frees allocated memory. */
  void close() throws IOException;

}
