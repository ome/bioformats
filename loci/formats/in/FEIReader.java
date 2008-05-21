//
// FEIReader.java
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

package loci.formats.in;

import java.io.IOException;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * FEIReader is the file format reader for FEI .img files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/FEIReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/FEIReader.java">SVN</a></dd></dl>
 */
public class FEIReader extends FormatReader {

  // -- Fields --

  private int originalWidth;

  // -- Constructor --

  /** Constructs a new FEI reader. */
  public FEIReader() {
    super("FEI", "img");
    blockCheckLen = 2;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return block[0] == 'X' && block[1] == 'L';
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    in.seek(1536);
    byte[] segment = new byte[core.sizeX[0] / 2];
    int skip = (originalWidth / 2) - (core.sizeX[0] / 2);
    // interlace frames - there are four rows of two columns
    int halfRow = core.sizeX[0] / 2;
    for (int q=0; q<4; q++) {
      for (int row=q; row<h; row+=4) {
        for (int s=0; s<2; s++) {
          in.read(segment);
          in.skipBytes(skip);
          for (int col=s; col<w; col+=2) {
            buf[row*w + col] = segment[col / 2];
          }
        }
      }
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("PCXReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Reading file header");

    in.seek(0x51a);
    in.order(true);
    core.sizeX[0] = in.readShort();
    core.sizeY[0] = in.readShort();

    originalWidth = core.sizeX[0];

    // FEI files can only be 1424x968 or 712x484

    if (1424 < core.sizeX[0]) {
      core.sizeX[0] = 1424;
      core.sizeY[0] = 968;
    }
    else {
      core.sizeX[0] = 712;
      core.sizeY[0] = 484;
    }

    // always one grayscale plane per file

    core.sizeZ[0] = 1;
    core.sizeC[0] = 1;
    core.sizeT[0] = 1;
    core.imageCount[0] = 1;
    core.littleEndian[0] = true;
    core.pixelType[0] = FormatTools.UINT8;
    core.rgb[0] = false;
    core.indexed[0] = false;
    core.interleaved[0] = false;
    core.currentOrder[0] = "XYCZT";

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    store.setImageCreationDate(
      DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);
    MetadataTools.populatePixels(store, this);
  }

}
