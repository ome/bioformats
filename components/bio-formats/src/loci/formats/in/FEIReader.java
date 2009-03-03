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
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * FEIReader is the file format reader for FEI .img files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/FEIReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/FEIReader.java">SVN</a></dd></dl>
 */
public class FEIReader extends FormatReader {

  // -- Fields --

  private int originalWidth;

  // -- Constructor --

  /** Constructs a new FEI reader. */
  public FEIReader() {
    super("FEI", "img");
    blockCheckLen = 2;
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    return stream.readString(blockCheckLen).startsWith("XL");
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
    byte[] segment = new byte[getSizeX() / 2];
    int skip = (originalWidth / 2) - (getSizeX() / 2);
    // interlace frames - there are four rows of two columns
    int halfRow = getSizeX() / 2;
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
    if (debug) debug("FEIReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Reading file header");

    in.seek(0x51a);
    in.order(true);
    core[0].sizeX = in.readShort();
    core[0].sizeY = in.readShort();

    originalWidth = getSizeX();

    // FEI files can only be 1424x968 or 712x484

    if (1424 < getSizeX()) {
      core[0].sizeX = 1424;
      core[0].sizeY = 968;
    }
    else {
      core[0].sizeX = 712;
      core[0].sizeY = 484;
    }

    // always one grayscale plane per file

    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].littleEndian = true;
    core[0].pixelType = FormatTools.UINT8;
    core[0].rgb = false;
    core[0].indexed = false;
    core[0].interleaved = false;
    core[0].dimensionOrder = "XYCZT";

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);
  }

}
