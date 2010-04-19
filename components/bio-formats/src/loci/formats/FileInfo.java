//
// FileInfo.java
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

/**
 * Encompasses basic metadata about a file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/FileInfo.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/FileInfo.java">SVN</a></dd></dl>
 */
public class FileInfo {

  // -- Fields --

  /** Absolute path to this file. */
  public String filename;

  /** IFormatReader implementation that would be used to read this file. */
  public Class<?> reader;

  /**
   * Whether or not this file can be passed to the appropriate reader's
   * setId(String) method.
   */
  public boolean usedToInitialize;

  // -- Object API methods --

  public String toString() {
    return "filename = " + filename + "\nreader = " + reader.getName() +
      "\nused to initialize = " + usedToInitialize;
  }

}
