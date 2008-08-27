//
// CoreMetadata.java
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

package loci.formats;

import java.util.Hashtable;

/**
 * Encompasses core metadata values.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/CoreMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/CoreMetadata.java">SVN</a></dd></dl>
 */
public class CoreMetadata {
  // TODO
  //
  // We may also want to consider refactoring the FormatReader getter methods
  // that populate missing CoreMetadata fields on the fly
  // (getChannelDimLengths, getChannelDimTypes, getThumbSizeX, getThumbSizeY)
  // to avoid doing so -- one alternate approach would be to have this class
  // use getter methods instead of public fields.

  // Lastly, we should add javadoc comments to the fields in this class.

  public int sizeX, sizeY, sizeZ, sizeC, sizeT;
  public int thumbSizeX, thumbSizeY;
  public int pixelType;
  public int imageCount;
  public int[] cLengths;
  public String[] cTypes;
  public String currentOrder;
  public boolean orderCertain, rgb, littleEndian, interleaved;
  public boolean indexed, falseColor, metadataComplete;
  public Hashtable seriesMetadata;

  public CoreMetadata() {
    seriesMetadata = new Hashtable();
  }

}
