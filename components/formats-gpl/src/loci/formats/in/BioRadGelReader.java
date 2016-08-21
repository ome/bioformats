/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;

/**
 * BioRadGelReader is the file format reader for Bio-Rad gel files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/BioRadGelReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/BioRadGelReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class BioRadGelReader extends FormatReader {

  // -- Constants --

  private static final int MAGIC_BYTES = 0xafaf;

  private static final long PIXEL_OFFSET = 59654;
  private static final long START_OFFSET = 160;
  private static final long BASE_OFFSET = 352;

  // -- Fields --

  private long offset;
  private long diff;

  // -- Constructor --

  /** Constructs a new Bio-Rad gel reader. */
  public BioRadGelReader() {
    super("Bio-Rad GEL", "1sc");
    domains = new String[] {FormatTools.GEL_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readShort() & 0xffff) == MAGIC_BYTES;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int planeSize = FormatTools.getPlaneSize(this);

    if (PIXEL_OFFSET + planeSize < in.length()) {
      // offsets were determined by trial and error, and may not be 100% correct
      if (diff < 0) {
        in.seek(0x379d1);
      }
      else if (diff == 0) {
        in.seek(PIXEL_OFFSET);
      }
      else if (in.length() - planeSize > 61000) {
        in.seek(PIXEL_OFFSET - 196);

        while (!in.readString(3).equals("scn")) {
          in.seek(in.getFilePointer() - 2);
        }

        in.skipBytes(91);
        int len = in.readShort();
        in.skipBytes(len);
        in.skipBytes(32);
      }
      else in.seek(in.length() - planeSize);
    }
    else in.seek(in.length() - planeSize);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bpp * getSizeC();

    in.skipBytes(pixel * getSizeX() * (getSizeY() - h - y));

    for (int row=h-1; row>=0; row--) {
      in.skipBytes(x * pixel);
      in.read(buf, row * w * pixel, w * pixel);
      in.skipBytes(pixel * (getSizeX() - w - x));
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    String check = in.readString(48);
    if (check.indexOf("Intel Format") != -1) {
      in.order(true);
    }

    in.seek(START_OFFSET);

    boolean codeFound = false;
    int skip = 0;

    while (!codeFound) {
      short code = in.readShort();
      if (code == 0x81) codeFound = true;
      short length = in.readShort();

      in.skipBytes(2 + 2 * length);
      if (codeFound) {
        skip = (in.readShort() & 0xffff) - 32;
      }
      else {
        if (length == 1) in.skipBytes(12);
        else if (length == 2) in.skipBytes(10);
      }
    }

    long baseFP = in.getFilePointer();
    diff = BASE_OFFSET - baseFP;
    skip += diff;

    double physicalWidth = 0d, physicalHeight = 0d;
    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (baseFP + skip - 8187 > 0) {
        in.seek(baseFP + skip - 8187);
        String scannerName = in.readCString();
        in.skipBytes(8);
        in.readCString();
        in.skipBytes(8);
        String imageArea = in.readCString();

        imageArea = imageArea.substring(imageArea.indexOf(":") + 1).trim();
        int xIndex = imageArea.indexOf("x");
        if (xIndex > 0) {
          int space = imageArea.indexOf(" ");
          if (space >= 0) {
            String width = imageArea.substring(1, space);
            int nextSpace = imageArea.indexOf(" ", xIndex + 2);
            if (nextSpace > xIndex) {
              String height = imageArea.substring(xIndex + 1, nextSpace);
              physicalWidth = Double.parseDouble(width.trim()) * 1000;
              physicalHeight = Double.parseDouble(height.trim()) * 1000;
            }
          }
        }
      }
    }

    in.seek(baseFP + skip - 298);
    String date = in.readString(17);
    date = DateTools.formatDate(date, "dd-MMM-yyyy HH:mm");
    in.skipBytes(73);
    String scannerName = in.readCString();
    addGlobalMeta("Scanner name", scannerName);

    in.seek(baseFP + skip);

    CoreMetadata m = core.get(0);

    m.sizeX = in.readShort() & 0xffff;
    m.sizeY = in.readShort() & 0xffff;
    if (getSizeX() * getSizeY() > in.length()) {
      in.order(true);
      in.seek(in.getFilePointer() - 4);
      m.sizeX = in.readShort();
      m.sizeY = in.readShort();
    }
    in.skipBytes(2);

    int bpp = in.readShort();
    m.pixelType = FormatTools.pixelTypeFromBytes(bpp, false, false);

    offset = in.getFilePointer();

    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = 1;
    m.imageCount = 1;
    m.dimensionOrder = "XYCZT";
    m.rgb = false;
    m.interleaved = false;
    m.indexed = false;
    m.littleEndian = in.isLittleEndian();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (date != null) {
      store.setImageAcquisitionDate(new Timestamp(date), 0);
    }
    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      PositiveFloat sizeX =
        FormatTools.getPhysicalSizeX(physicalWidth / getSizeX());
      PositiveFloat sizeY =
        FormatTools.getPhysicalSizeY(physicalHeight / getSizeY());

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
    }
  }

}
