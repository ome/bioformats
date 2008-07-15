//
// LIFReader.java
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

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * LIFReader is the file format reader for Leica LIF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/LIFReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/LIFReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LIFReader extends FormatReader {

  // -- Constants --

  /** Factory for generating SAX parsers. */
  public static final SAXParserFactory SAX_FACTORY =
    SAXParserFactory.newInstance();

  // -- Fields --

  /** Offsets to memory blocks, paired with their corresponding description. */
  private Vector offsets;

  /** Bits per pixel. */
  private int[] bitsPerPixel;

  /** Extra dimensions. */
  private int[] extraDimensions;

  private int numDatasets;

  // -- Constructor --

  /** Constructs a new Leica LIF reader. */
  public LIFReader() { super("Leica Image File Format", "lif"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return block[0] == 0x70;
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

    long offset = ((Long) offsets.get(series)).longValue();
    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    int bpp = bytes * getRGBChannelCount();
    in.seek(offset + getSizeX() * getSizeY() * (long) no * bpp);
    DataTools.readPlane(in, x, y, w, h, this, buf);

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LIFReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);
    offsets = new Vector();

    core.littleEndian[0] = true;
    in.order(isLittleEndian());

    // read the header

    status("Reading header");

    byte checkOne = (byte) in.read();
    in.skipBytes(2);
    byte checkTwo = (byte) in.read();
    if (checkOne != 0x70 && checkTwo != 0x70) {
      throw new FormatException(id + " is not a valid Leica LIF file");
    }

    in.skipBytes(4);

    // read and parse the XML description

    if (in.read() != 0x2a) {
      throw new FormatException("Invalid XML description");
    }

    // number of Unicode characters in the XML block
    int nc = in.readInt();
    String xml = DataTools.stripString(in.readString(nc * 2));

    status("Finding image offsets");

    while (in.getFilePointer() < in.length()) {
      if (in.readInt() != 0x70) {
        throw new FormatException("Invalid Memory Block");
      }

      in.skipBytes(4);
      if (in.read() != 0x2a) {
        throw new FormatException("Invalid Memory Description");
      }

      long blockLength = in.readInt();
      if (in.read() != 0x2a) {
        in.seek(in.getFilePointer() - 5);
        blockLength = in.readLong();
        if (in.read() != 0x2a) {
          throw new FormatException("Invalid Memory Description");
        }
      }

      int descrLength = in.readInt();
      in.skipBytes(descrLength * 2);

      if (blockLength > 0) {
        offsets.add(new Long(in.getFilePointer()));
      }
      long skipped = 0;
      while (skipped < blockLength) {
        if (blockLength - skipped > 4096) {
          skipped += in.skipBytes(4096);
        }
        else {
          skipped += in.skipBytes((int) (blockLength - skipped));
        }
      }
    }
    initMetadata(xml);
  }

  // -- Helper methods --

  /** Parses a string of XML and puts the values in a Hashtable. */
  private void initMetadata(String xml) throws FormatException, IOException {
    LeicaHandler handler = new LeicaHandler();

    // the XML blocks stored in a LIF file are invalid,
    // because they don't have a root node

    xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><LEICA>" + xml +
      "</LEICA>";

    xml = DataTools.sanitizeXML(xml);

    try {
      SAXParser parser = SAX_FACTORY.newSAXParser();
      parser.parse(new ByteArrayInputStream(xml.getBytes()), handler);
    }
    catch (ParserConfigurationException exc) {
      throw new FormatException(exc);
    }
    catch (SAXException exc) {
      throw new FormatException(exc);
    }

    Vector widths = handler.getWidths();
    Vector heights = handler.getHeights();
    Vector zs = handler.getZs();
    Vector ts = handler.getTs();
    Vector channels = handler.getChannels();
    Vector bps = handler.getBPS();
    Vector extraDims = handler.getExtraDims();
    metadata = handler.getMetadata();
    Vector containerNames = handler.getContainerNames();
    Vector containerCounts = handler.getContainerCounts();
    Vector seriesNames = handler.getSeriesNames();
    Vector xcal = handler.getXCal();
    Vector ycal = handler.getYCal();
    Vector zcal = handler.getZCal();

    numDatasets = widths.size();

    bitsPerPixel = new int[numDatasets];
    extraDimensions = new int[numDatasets];

    // Populate metadata store

    status("Populating metadata");

    // The metadata store we're working with.
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    core = new CoreMetadata(numDatasets);
    Arrays.fill(core.orderCertain, true);
    Arrays.fill(core.currentOrder, "XYCZT");

    for (int i=0; i<numDatasets; i++) {
      core.sizeX[i] = ((Integer) widths.get(i)).intValue();
      core.sizeY[i] = ((Integer) heights.get(i)).intValue();
      core.sizeZ[i] = ((Integer) zs.get(i)).intValue();
      core.sizeC[i] = ((Integer) channels.get(i)).intValue();
      core.sizeT[i] = ((Integer) ts.get(i)).intValue();

      bitsPerPixel[i] = ((Integer) bps.get(i)).intValue();
      extraDimensions[i] = ((Integer) extraDims.get(i)).intValue();

      if (extraDimensions[i] > 1) {
        if (core.sizeZ[i] == 1) core.sizeZ[i] = extraDimensions[i];
        else core.sizeT[i] *= extraDimensions[i];
        extraDimensions[i] = 1;
      }

      core.metadataComplete[i] = true;
      core.littleEndian[i] = true;
      core.rgb[i] = false;
      core.interleaved[i] = false;
      core.imageCount[i] = core.sizeZ[i] * core.sizeT[i];
      core.imageCount[i] *= core.sizeC[i];
      core.indexed[i] = false;
      core.falseColor[i] = false;

      while (bitsPerPixel[i] % 8 != 0) bitsPerPixel[i]++;
      switch (bitsPerPixel[i]) {
        case 8:
          core.pixelType[i] = FormatTools.UINT8;
          break;
        case 16:
          core.pixelType[i] = FormatTools.UINT16;
          break;
        case 32:
          core.pixelType[i] = FormatTools.FLOAT;
          break;
      }

      String seriesName = (String) seriesNames.get(i);
      if (seriesName == null || seriesName.trim().length() == 0) {
        seriesName = "Series " + (i + 1);
      }
      store.setImageName(seriesName, i);
      MetadataTools.setDefaultCreationDate(store, getCurrentFile(), i);

      MetadataTools.populatePixels(store, this);

      Float xf = i < xcal.size() ? (Float) xcal.get(i) : null;
      Float yf = i < ycal.size() ? (Float) ycal.get(i) : null;
      Float zf = i < zcal.size() ? (Float) zcal.get(i) : null;

      store.setDimensionsPhysicalSizeX(xf, i, 0);
      store.setDimensionsPhysicalSizeY(yf, i, 0);
      store.setDimensionsPhysicalSizeZ(zf, i, 0);

      // CTR CHECK
//      String zoom = (String) getMeta(seriesName + " - dblZoom");
//      store.setDisplayOptions(zoom == null ? null : new Float(zoom),
//        new Boolean(core.sizeC[i] > 1), new Boolean(core.sizeC[i] > 1),
//        new Boolean(core.sizeC[i] > 2), new Boolean(isRGB()), null,
//        null, null, null, null, ii, null, null, null, null, null);

      Enumeration keys = metadata.keys();
      while (keys.hasMoreElements()) {
        String k = (String) keys.nextElement();
        if (k.startsWith((String) seriesNames.get(i) + " ")) {
          core.seriesMetadata[i].put(k, metadata.get(k));
        }
      }
    }
  }

}
