//
// JPEGWriter.java
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

package loci.formats.out;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import loci.common.*;
import loci.formats.*;

/**
 * JPEGWriter is the file format writer for JPEG files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/out/JPEGWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/out/JPEGWriter.java">SVN</a></dd></dl>
 */
public class JPEGWriter extends ImageIOWriter {

  // -- Constructor --

  public JPEGWriter() {
    super("Joint Photographic Experts Group",
      new String[] {"jpg", "jpeg", "jpe"}, "jpeg");
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#save(Image, int, boolean, boolean) */
  public void saveImage(Image image, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    BufferedImage img = (cm == null) ?
      ImageTools.makeBuffered(image) : ImageTools.makeBuffered(image, cm);
    int type = ImageTools.getPixelType(img);
    int[] types = getPixelTypes();
    for (int i=0; i<types.length; i++) {
      if (types[i] == type) {
        super.saveImage(image, series, lastInSeries, last);
        return;
      }
    }
    throw new FormatException("Unsupported data type");
  }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes() {
    return new int[] {FormatTools.UINT8};
  }

}
