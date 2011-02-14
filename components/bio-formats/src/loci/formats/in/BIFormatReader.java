//
// BIFormatReader.java
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

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.IOException;

import loci.common.DataTools;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.gui.AWTImageTools;

/**
 * BIFormatReader is the superclass for file format readers
 * that use java.awt.image.BufferedImage as the native data type.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/BIFormatReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/BIFormatReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public abstract class BIFormatReader extends FormatReader {

  // -- Constructors --

  /** Constructs a new BIFormatReader. */
  public BIFormatReader(String name, String suffix) {
    super(name, suffix);
  }

  /** Constructs a new BIFormatReader. */
  public BIFormatReader(String name, String[] suffixes) {
    super(name, suffixes);
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    BufferedImage data = (BufferedImage) openPlane(no, x, y, w, h);
    switch (data.getColorModel().getComponentSize(0)) {
      case 8:
        byte[] t = AWTImageTools.getBytes(data, false);
        System.arraycopy(t, 0, buf, 0, (int) Math.min(t.length, buf.length));
        break;
      case 16:
        short[][] ts = AWTImageTools.getShorts(data);
        for (int c=0; c<ts.length; c++) {
          int offset = c * ts[c].length * 2;
          for (int i=0; i<ts[c].length && offset < buf.length; i++) {
            DataTools.unpackBytes(ts[c][i], buf, offset, 2, isLittleEndian());
            offset += 2;
          }
        }
        break;
    }
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#getNativeDataType() */
  public Class<?> getNativeDataType() {
    return BufferedImage.class;
  }

}
