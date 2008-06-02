//
// NAFReader.java
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
import java.util.Arrays;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * NAFReader is the file format reader for Hamamatsu Aquacosmos NAF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/NAFReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/NAFReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class NAFReader extends FormatReader {

  // -- Constants --

  private static final int LUT_SIZE = 263168;

  // -- Fields --

  private long[] offsets;
  private boolean compressed;

  // -- Constructor --

  /** Constructs a new NAF reader. */
  public NAFReader() { super("Hamamatsu Aquacosmos .naf", "naf"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return true;
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

    int bpp = FormatTools.getBytesPerPixel(core.pixelType[series]);
    int plane = core.sizeX[series] * core.sizeY[series] * bpp;
    in.seek(offsets[series] + no * plane);
    in.skipBytes(core.sizeX[series] * bpp * y);

    if (in.getFilePointer() + buf.length > in.length()) {
      throw new FormatException("Sorry, compressed data is not supported.");
    }

    if (core.sizeX[series] == w) {
      in.read(buf);
    }
    else {
      for (int row=0; row<h; row++) {
        in.skipBytes(x * bpp);
        in.read(buf, row * w * bpp, w * bpp);
        in.skipBytes((core.sizeX[series] - w) * bpp);
      }
    }

    return buf;
  }

  // -- IFormatHandler API Methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("NAFReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    String endian = in.readString(2);
    boolean little = endian.equals("II");
    in.order(little);

    in.seek(98);
    int seriesCount = in.readInt();

    in.seek(192);
    while (in.read() == 0);
    in.readCString(); // some kind of description?

    while (in.readInt() == 0);

    long fp = in.getFilePointer();
    if ((fp % 2) == 0) fp -= 4;
    else fp--;

    offsets = new long[seriesCount];
    core = new CoreMetadata(seriesCount);
    Arrays.fill(core.littleEndian, little);
    for (int i=0; i<seriesCount; i++) {
      in.seek(fp + i*256);
      core.sizeX[i] = in.readInt();
      core.sizeY[i] = in.readInt();
      int numBits = in.readInt();
      core.sizeC[i] = in.readInt();
      core.sizeZ[i] = in.readInt();
      core.sizeT[i] = in.readInt();

      core.imageCount[i] = core.sizeZ[i] * core.sizeC[i] * core.sizeT[i];
      switch (numBits) {
        case 8:
          core.pixelType[i] = FormatTools.UINT8;
          break;
        case 16:
          core.pixelType[i] = FormatTools.UINT16;
          break;
        case 32:
          core.pixelType[i] = FormatTools.UINT32;
          break;
        case 64:
          core.pixelType[i] = FormatTools.DOUBLE;
          break;
      }

      core.currentOrder[i] = "XYCZT";
      core.rgb[i] = false;

      in.skipBytes(4);

      long pointer = in.getFilePointer();
      String name = in.readCString();

      if (i == 0) {
        in.skipBytes((int) (92 - in.getFilePointer() + pointer));
        while (true) {
          int check = in.readInt();
          if (check > in.getFilePointer()) {
            offsets[i] = (long) check + LUT_SIZE;
            break;
          }
          in.skipBytes(92);
        }
      }
      else {
        offsets[i] = offsets[i - 1] + core.sizeX[i - 1] * core.sizeY[i - 1] *
          core.imageCount[i - 1] *
          FormatTools.getBytesPerPixel(core.pixelType[i - 1]);
      }
      offsets[i] += 352;
      in.seek(offsets[i]);
      while (in.getFilePointer() + 116 < in.length() && in.read() == 3 &&
        in.read() == 37)
      {
        in.skipBytes(114);
        offsets[i] = in.getFilePointer();
      }
      in.seek(in.getFilePointer() - 1);
      byte[] buf = new byte[3 * 1024 * 1024];
      int n = in.read(buf, 0, 1);
      boolean found = false;
      while (!found && in.getFilePointer() < in.length()) {
        n += in.read(buf, 1, buf.length - 1);
        for (int q=0; q<buf.length - 1; q++) {
          if ((buf[q] & 0xff) == 192 && (buf[q + 1] & 0xff) == 46) {
            offsets[i] = in.getFilePointer() - n + q;
            found = true;
            break;
          }
        }
        buf[0] = buf[buf.length - 1];
        n = 1;
      }
      if (found) offsets[i] += 16063;
      if (i == offsets.length - 1 && !compressed && i > 0) {
        offsets[i] = (int) (in.length() -
          (core.sizeX[i] * core.sizeY[i] * core.imageCount[i] * (numBits / 8)));
      }
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    store.setImageCreationDate(
      DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);
    MetadataTools.populatePixels(store, this);
  }

}
