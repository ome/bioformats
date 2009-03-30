//
// MRWReader.java
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

import java.io.*;
import java.util.Hashtable;
import loci.common.*;
import loci.formats.*;
import loci.formats.codec.BitBuffer;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * MRWReader is the file format reader for Minolta MRW files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/MRWReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/MRWReader.java">SVN</a></dd></dl>
 */
public class MRWReader extends FormatReader {

  // -- Fields --

  /** Offset to image data. */
  private int offset;

  private int sensorWidth, sensorHeight;
  private int bayerPattern;
  private int storageMethod;
  private int dataSize;

  private float[] wbg;

  // -- Constructor --

  /** Constructs a new MRW reader. */
  public MRWReader() {
    super("Minolta MRW", "mrw");
    blockCheckLen = 4;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    return stream.readString(4).endsWith("MRM");
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

    int nBytes = sensorWidth * sensorHeight;
    if (dataSize == 12) nBytes *= 3;
    else if (dataSize == 16) nBytes *= 4;

    byte[] tmp = new byte[nBytes];
    in.seek(offset);
    in.read(tmp);
    BitBuffer bb = new BitBuffer(tmp);

    short[] s = new short[getSizeX() * getSizeY() * 3];

    for (int row=0; row<getSizeY(); row++) {
      boolean evenRow = (row % 2) == 0;
      for (int col=0; col<getSizeX(); col++) {
        boolean evenCol = (col % 2) == 0;
        short val = (short) (bb.getBits(dataSize) & 0xffff);

        int redOffset = row * getSizeX() + col;
        int greenOffset = (getSizeY() + row) * getSizeX() + col;
        int blueOffset = (2 * getSizeY() + row) * getSizeX() + col;

        if (evenRow) {
          if (evenCol) {
            val = (short) (val * wbg[0]);
            if (bayerPattern == 1) s[redOffset] = val;
            else s[greenOffset] = val;
          }
          else {
            val = (short) (val * wbg[1]);
            if (bayerPattern == 1) s[greenOffset] = val;
            else s[blueOffset] = val;
          }
        }
        else {
          if (evenCol) {
            val = (short) (val * wbg[2]);
            if (bayerPattern == 1) s[greenOffset] = val;
            else s[redOffset] = val;
          }
          else {
            val = (short) (val * wbg[3]);
            if (bayerPattern == 1) s[blueOffset] = val;
            else s[greenOffset] = val;
          }
        }
      }
      bb.skipBits(dataSize * (sensorWidth - getSizeX()));
    }

    int[] colorMap = new int[4];
    if (bayerPattern == 1) {
      colorMap[0] = 0;
      colorMap[1] = 1;
      colorMap[2] = 1;
      colorMap[3] = 2;
    }
    else {
      colorMap[0] = 1;
      colorMap[1] = 2;
      colorMap[2] = 0;
      colorMap[3] = 1;
    }

    return ImageTools.interpolate(s, buf, colorMap, getSizeX(), getSizeY(),
      isLittleEndian());
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("AliconaReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    in.skipBytes(4); // magic number
    offset = in.readInt() + 8;

    while (in.getFilePointer() < offset) {
      String blockName = in.readString(4);
      int len = in.readInt();
      long fp = in.getFilePointer();

      if (blockName.endsWith("PRD")) {
        in.skipBytes(8);
        sensorHeight = in.readShort();
        sensorWidth = in.readShort();
        core[0].sizeY = in.readShort();
        core[0].sizeX = in.readShort();
        dataSize = in.read();
        in.skipBytes(1);
        storageMethod = in.read();
        in.skipBytes(4);
        bayerPattern = in.read();
      }
      else if (blockName.endsWith("WBG")) {
        wbg = new float[4];
        byte[] wbScale = new byte[4];
        in.read(wbScale);
        for (int i=0; i<wbg.length; i++) {
          float coeff = in.readShort();
          wbg[i] = coeff / (64 << wbScale[i]);
        }
      }
      else if (blockName.endsWith("RIF")) {

      }
      else if (blockName.endsWith("TTW")) {
        byte[] b = new byte[len];
        in.read(b);
        RandomAccessStream ras = new RandomAccessStream(b);
        Hashtable[] ifds = TiffTools.getIFDs(ras);

        for (int i=0; i<ifds.length; i++) {
          Integer[] keys = (Integer[]) ifds[i].keySet().toArray(new Integer[0]);
          for (int q=0; q<keys.length; q++) {
            addMeta(TiffTools.getIFDTagName(keys[q].intValue()),
              ifds[i].get(keys[q]));
          }

          long exifOffset =
            TiffTools.getIFDLongValue(ifds[i], TiffTools.EXIF, false, 0);
          if (exifOffset != 0 && exifOffset < ras.length()) {
            Hashtable exif = TiffTools.getIFD(ras, 1, exifOffset);

            Integer[] k = (Integer[]) exif.keySet().toArray(new Integer[0]);
            for (int q=0; q<k.length; q++) {
              addMeta(BaseTiffReader.getExifTagName(k[q].intValue()),
                exif.get(k[q]));
            }
          }
        }
        ras.close();
      }

      in.seek(fp + len);
    }

    core[0].pixelType = FormatTools.UINT16;
    core[0].rgb = true;
    core[0].littleEndian = false;
    core[0].dimensionOrder = "XYCZT";
    core[0].imageCount = 1;
    core[0].sizeC = 3;
    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    core[0].interleaved = true;

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    store.setImageName("", 0);
  }

}
