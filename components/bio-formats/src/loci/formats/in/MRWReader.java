/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.codec.BitBuffer;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.TiffParser;

/**
 * MRWReader is the file format reader for Minolta MRW files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/MRWReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/MRWReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MRWReader extends FormatReader {

  // -- Constants --

  public static final String MRW_MAGIC_STRING = "MRM";

  private static final int[] COLOR_MAP_1 = {0, 1, 1, 2};
  private static final int[] COLOR_MAP_2 = {1, 2, 0, 1};

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
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(4).endsWith(MRW_MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

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

    int[] colorMap = bayerPattern == 1 ? COLOR_MAP_1 : COLOR_MAP_2;

    return ImageTools.interpolate(s, buf, colorMap, w, h, isLittleEndian());
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      offset = 0;
      sensorWidth = sensorHeight = 0;
      bayerPattern = 0;
      storageMethod = 0;
      dataSize = 0;
      wbg = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

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
      else if (blockName.endsWith("TTW") &&
        getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM)
      {
        byte[] b = new byte[len];
        in.read(b);
        RandomAccessInputStream ras = new RandomAccessInputStream(b);
        TiffParser tp = new TiffParser(ras);
        IFDList ifds = tp.getIFDs();

        for (IFD ifd : ifds) {
          Integer[] keys = (Integer[]) ifd.keySet().toArray(new Integer[0]);
          // CTR FIXME - getIFDTagName is for debugging only!
          for (int q=0; q<keys.length; q++) {
            addGlobalMeta(IFD.getIFDTagName(keys[q].intValue()),
              ifd.get(keys[q]));
          }
        }

        IFDList exifIFDs = tp.getExifIFDs();
        for (IFD exif : exifIFDs) {
          for (Integer key : exif.keySet()) {
            addGlobalMeta(IFD.getIFDTagName(key.intValue()), exif.get(key));
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
    core[0].bitsPerPixel = dataSize;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
