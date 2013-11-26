/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.LZOCodec;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.PositiveFloat;

/**
 * VolocityClippingReader is the file format reader for Volocity library
 * clipping files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/VolocityClippingReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/VolocityClippingReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class VolocityClippingReader extends FormatReader {

  // -- Constants --

  private static final String CLIPPING_MAGIC_STRING = "FFCA";
  private static final int AISF = 0x46534941;
  private static final int FSIA = 0x41495346;

  // -- Fields --

  private List<Long> pixelOffsets = new ArrayList<Long>();
  private List<String> channelNames = new ArrayList<String>();
  private boolean aisf = false;

  // -- Constructor --

  /** Constructs a new Volocity clipping reader. */
  public VolocityClippingReader() {
    super("Volocity Library Clipping", "acff");
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return false;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] zct = getZCTCoords(no);
    int index = getIndex(zct[0], 0, zct[2]);
    in.seek(pixelOffsets.get(zct[1]) + index * FormatTools.getPlaneSize(this));

    if (FormatTools.getPlaneSize(this) * 2 + in.getFilePointer() < in.length())
    {
      readPlane(in, x, y, w, h, buf);
      return buf;
    }

    byte[] b = new LZOCodec().decompress(in, null);

    RandomAccessInputStream s = new RandomAccessInputStream(b);
    s.seek(0);
    readPlane(s, x, y, w, h, buf);
    s.close();

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelOffsets.clear();
      channelNames.clear();
      aisf = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);

    m.littleEndian = in.read() == 'I';
    in.order(isLittleEndian());

    in.skipBytes(4);

    String magicString = in.readString(4);

    if (!magicString.equals(CLIPPING_MAGIC_STRING)) {
      throw new FormatException("Found invalid magic string: " + magicString);
    }

    in.skipBytes(12);
    int len = in.readInt();

    String imageName = in.readString(len);

    Double physicalX = null;
    Double physicalY = null;
    Double physicalZ = null;

    String key = null;
    Object value = null;
    while (true) {
      if (in.readInt() == 0x258) {
        if (key == null) {
          len = in.readInt();
          key = in.readString(len);
        }
        else {
          int type = in.readInt();

          switch (type) {
            case 0x1f9:
              value = in.readLong();
              break;
            case 0x1f4:
              value = in.readDouble();
              break;
            case 0x1f8:
              len = in.readInt();
              value = in.readString(len);
              break;
          }

          if (value == null) {
            if (key.equals("Channels")) {
              len = type;
              channelNames.add(in.readString(len));
            }
            break;
          }

          if (key.equals("um/pixel (X)")) {
            physicalX = new Double(value.toString());
          }
          else if (key.equals("um/pixel (Y)")) {
            physicalY = new Double(value.toString());
          }
          else if (key.equals("um/pixel (Z)")) {
            physicalZ = new Double(value.toString());
          }

          addGlobalMeta(key, value);
          key = null;
          value = null;
        }
      }
      else {
        in.seek(in.getFilePointer() - 3);
      }
    }

    long firstOffset = findDimensions(0);

    m.sizeX = in.readInt();
    m.sizeY = in.readInt();
    m.sizeZ = in.readInt();
    m.sizeC = 1;

    m.sizeT = 1;
    m.imageCount = getSizeZ() * getSizeT();
    m.dimensionOrder = "XYZTC";
    m.pixelType = aisf ? FormatTools.UINT16 : FormatTools.UINT8;

    if (firstOffset == 0) {
      firstOffset = in.getFilePointer() + 65;
    }
    else {
      firstOffset -= 59;
    }

    if (getSizeX() * getSizeY() * 100 >= in.length()) {
      while (in.getFilePointer() < in.length()) {
        try {
          byte[] b = new LZOCodec().decompress(in, null);
          if (b.length > 0 && (b.length % (getSizeX() * getSizeY())) == 0) {
            int bytes = b.length / (getSizeX() * getSizeY());
            m.pixelType =
              FormatTools.pixelTypeFromBytes(bytes, false, false);
            break;
          }
        }
        catch (EOFException e) { }
        firstOffset++;
        in.seek(firstOffset);
      }
    }

    pixelOffsets.add(firstOffset);

    in.seek(firstOffset);
    long length = (long) getImageCount() * FormatTools.getPlaneSize(this);
    while (in.getFilePointer() < in.length()) {
      long newOffset = in.getFilePointer() + length;
      if (newOffset < 0 || newOffset >= in.length()) {
        break;
      }
      in.seek(newOffset);
      long base = in.getFilePointer();
      in.skipBytes(30);
      String check = in.readString(2);
      if (!check.equals("I1")) {
        break;
      }

      in.skipBytes(3);
      check = in.readString(4);
      if (!check.equals("xIIA")) {
        break;
      }

      try {
        findDimensions(base);
      }
      catch (EOFException e) {
        break;
      }
      long offset = in.getFilePointer() + 12 + 65;
      in.seek(offset);
      while (in.read() == 0);
      offset = in.getFilePointer() - 1;

      pixelOffsets.add(offset);
      in.seek(offset);
    }

    m.sizeC = pixelOffsets.size();
    m.imageCount *= m.sizeC;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    store.setImageName(imageName, 0);

    for (int c=0; c<getSizeC(); c++) {
      if (c < channelNames.size()) {
        String name = channelNames.get(c);
        store.setChannelName(name, 0, c);

        name = name.toLowerCase();

        if (name.startsWith("red")) {
          store.setChannelColor(new Color(255, 0, 0, 255), 0, c);
        }
        else if (name.startsWith("green")) {
          store.setChannelColor(new Color(0, 255, 0, 255), 0, c);
        }
        else if (name.startsWith("blue")) {
          store.setChannelColor(new Color(0, 0, 255, 255), 0, c);
        }
      }
    }

    if (physicalX != null) {
      store.setPixelsPhysicalSizeX(new PositiveFloat(physicalX), 0);
    }
    if (physicalY != null) {
      store.setPixelsPhysicalSizeY(new PositiveFloat(physicalY), 0);
    }
    if (physicalZ != null) {
      store.setPixelsPhysicalSizeZ(new PositiveFloat(physicalZ), 0);
    }
  }

  private long findDimensions(long base) throws IOException {
    long firstOffset = 0;

    if (base > 0) {
      in.skipBytes(32);
      while (in.read() == 0);
      in.skipBytes(11);
      int len = in.readInt();
      String channelName = in.readString(len);
      channelNames.add(channelName);
    }

    int check = in.readInt();
    while (check != 0x208 && check != AISF && check != FSIA) {
      in.seek(in.getFilePointer() - 3);
      check = in.readInt();
    }
    if (check == AISF || check == FSIA) {
      aisf = true;
      core.get(0).littleEndian = check == FSIA;
      in.order(isLittleEndian());
      firstOffset = in.readInt() + base;
      in.skipBytes(24);
    }

    return firstOffset;
  }

}
