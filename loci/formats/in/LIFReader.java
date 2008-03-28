//
// LIFReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
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

  private int bpp;
  private Vector xcal;
  private Vector ycal;
  private Vector zcal;
  private Vector seriesNames;
  private Vector containerNames;
  private Vector containerCounts;
  private Vector widths;
  private Vector heights;
  private Vector zs;
  private Vector ts;
  private Vector channels;
  private Vector bps;
  private Vector extraDims;

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
    int bytes = FormatTools.getBytesPerPixel(core.pixelType[series]);
    in.seek(offset + core.sizeX[series] * core.sizeY[series] * (long) no *
      bytes * getRGBChannelCount());

    in.skipBytes(y * core.sizeX[series] * bytes * getRGBChannelCount());

    int line = w * bytes * getRGBChannelCount();
    for (int row=0; row<h; row++) {
      in.skipBytes(x * bytes * getRGBChannelCount());
      in.read(buf, row * line, line);
      in.skipBytes(bytes * getRGBChannelCount() * (core.sizeX[series] - w - x));
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    bitsPerPixel = extraDimensions = null;
    bpp = 0;
    offsets = xcal = ycal = zcal = null;
    containerNames = containerCounts = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LIFReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);
    offsets = new Vector();

    core.littleEndian[0] = true;
    in.order(core.littleEndian[0]);

    xcal = new Vector();
    ycal = new Vector();
    zcal = new Vector();

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
    containerNames = new Vector();
    containerCounts = new Vector();
    seriesNames = new Vector();

    numDatasets = 0;
    widths = new Vector();
    heights = new Vector();
    zs = new Vector();
    ts = new Vector();
    channels = new Vector();
    bps = new Vector();
    extraDims = new Vector();

    LIFHandler handler = new LIFHandler();

    // the XML blocks stored in a LIF file are invalid,
    // because they don't have a root node

    xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><LEICA>" + xml +
      "</LEICA>";

    // strip out invalid characters
    for (int i=0; i<xml.length(); i++) {
      char c = xml.charAt(i);
      if (Character.isISOControl(c) || !Character.isDefined(c)) {
        xml = xml.replace(c, ' ');
      }
    }

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
      store.setImageCreationDate(
        DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), i);

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

  // -- Helper class --

  /** SAX handler for parsing XML. */
  class LIFHandler extends DefaultHandler {
    private String series;
    private String fullSeries;
    private int count = 0;
    private boolean firstElement = true;
    private boolean dcroiOpen = false;
    private int numChannels, extras;

    public void endElement(String uri, String localName, String qName) {
      if (qName.equals("Element")) {
        if (dcroiOpen) {
          dcroiOpen = false;
          return;
        }
        if (fullSeries.indexOf("/") != -1) {
          fullSeries = fullSeries.substring(0, fullSeries.lastIndexOf("/"));
        }
        else fullSeries = "";

        extraDims.add(new Integer(extras));
        if (numChannels == 0) numChannels++;
        channels.add(new Integer(numChannels));

        if (widths.size() < numDatasets && heights.size() < numDatasets) {
          numDatasets--;
        }
        else if (widths.size() > numDatasets && heights.size() > numDatasets) {
          numDatasets = widths.size();
        }
        if (widths.size() < numDatasets) widths.add(new Integer(1));
        if (heights.size() < numDatasets) heights.add(new Integer(1));
        if (zs.size() < numDatasets) zs.add(new Integer(1));
        if (ts.size() < numDatasets) ts.add(new Integer(1));
        if (bps.size() < numDatasets) bps.add(new Integer(8));
        numChannels = 0;
      }
    }

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("Element")) {
        if (!attributes.getValue("Name").equals("DCROISet") && !firstElement) {
          series = attributes.getValue("Name");
          containerNames.add(series);
          if (fullSeries == null || fullSeries.equals("")) fullSeries = series;
          else fullSeries += "/" + series;
        }
        else if (firstElement) firstElement = false;

        if (attributes.getValue("Name").equals("DCROISet")) {
          dcroiOpen = true;
        }

        numDatasets++;
        int idx = numDatasets - 1;
        if (idx >= seriesNames.size()) {
          numDatasets = seriesNames.size();
        }

        if (!dcroiOpen) {
          numChannels = 0;
          extras = 1;
        }
      }
      else if (qName.equals("Experiment")) {
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(attributes.getQName(i), attributes.getValue(i));
        }
      }
      else if (qName.equals("Image")) {
        containerNames.remove(series);
        if (containerCounts.size() < containerNames.size()) {
          containerCounts.add(new Integer(1));
        }
        else if (containerCounts.size() > 0) {
          int ndx = containerCounts.size() - 1;
          int n = ((Integer) containerCounts.get(ndx)).intValue();
          containerCounts.setElementAt(new Integer(n + 1), ndx);
        }
        if (fullSeries == null || fullSeries.equals("")) fullSeries = series;
        seriesNames.add(fullSeries);
      }
      else if (qName.equals("ChannelDescription")) {
        String prefix = fullSeries + " - Channel " + count + " - ";
        for (int i=0; i<attributes.getLength(); i++) {
          String name = attributes.getQName(i);
          addMeta(prefix + name, attributes.getValue(name));
        }
        count++;
        numChannels++;
        if (numChannels == 1) {
          bps.add(new Integer(attributes.getValue("Resolution")));
        }
      }
      else if (qName.equals("DimensionDescription")) {
        String prefix = fullSeries + " - Dimension " + count + " - ";
        for (int i=0; i<attributes.getLength(); i++) {
          String name = attributes.getQName(i);
          addMeta(prefix + name, attributes.getValue(name));
        }
        Integer w = new Integer(attributes.getValue("NumberOfElements"));
        int id = Integer.parseInt(attributes.getValue("DimID"));
        float size = Float.parseFloat(attributes.getValue("Length"));
        if (size < 0) size *= -1;
        size *= 1000000;
        Float pixelSize = new Float(size / w.intValue());

        switch (id) {
          case 1:
            widths.add(w);
            xcal.add(pixelSize);
            break;
          case 2:
            heights.add(w);
            ycal.add(pixelSize);
            break;
          case 3:
            zs.add(w);
            zcal.add(pixelSize);
            break;
          case 4:
            ts.add(w);
            break;
          default:
            extras *= w.intValue();
        }
      }
      else if (qName.equals("ScannerSettingRecord")) {
        String key = attributes.getValue("Identifier") + " - " +
          attributes.getValue("Description");
        addMeta(fullSeries + " - " + key, attributes.getValue("Variant"));
        String identifier = attributes.getValue("Identifier");
        if ("csScanMode".equals(identifier)) {
          String ordering = attributes.getValue("Variant").toLowerCase();

          if (ordering.indexOf("x") == -1 || ordering.indexOf("y") == -1 ||
            ordering.indexOf("xy") == -1)
          {
            // switch the axis sizes around, depending on the
            // stored dimension order

            int xPos = ordering.indexOf("x");
            int yPos = ordering.indexOf("y");
            int zPos = ordering.indexOf("z");
            int tPos = ordering.indexOf("t");

            if (xPos < 0) xPos = 0;
            if (yPos < 0) yPos = 1;
            if (zPos < 0) zPos = 2;
            if (tPos < 0) tPos = 3;

            int index = widths.size() - 1;

            int x = ((Integer) widths.get(index)).intValue();
            int y = index < heights.size() ?
              ((Integer) heights.get(index)).intValue() : 1;
            int z =
              index < zs.size() ? ((Integer) zs.get(index)).intValue() : 1;
            int t =
              index < ts.size() ? ((Integer) ts.get(index)).intValue() : 1;

            int[] dimensions = {x, y, z, t};

            x = dimensions[xPos];
            y = dimensions[yPos];
            z = dimensions[zPos];
            t = dimensions[tPos];

            widths.setElementAt(new Integer(x), widths.size() - 1);
            if (index < heights.size()) {
              heights.setElementAt(new Integer(y), heights.size() - 1);
            }
            else heights.add(new Integer(y));
            if (index < zs.size()) {
              zs.setElementAt(new Integer(z), zs.size() - 1);
            }
            else zs.add(new Integer(z));
            if (index < ts.size()) {
              ts.setElementAt(new Integer(t), ts.size() - 1);
            }
            else ts.add(new Integer(t));
          }
        }
        else if (identifier.startsWith("dblVoxel")) {
          if (identifier.endsWith("Z") && xcal.size() > zcal.size()) {
            String size = attributes.getValue("Variant");
            float cal = Float.parseFloat(size) * 1000000;
            zcal.add(new Float(cal));
          }
        }
      }
      else if (qName.equals("FilterSettingRecord")) {
        String key = attributes.getValue("ObjectName") + " - " +
          attributes.getValue("Description") + " - " +
          attributes.getValue("Attribute");
        addMeta(fullSeries + " - " + key, attributes.getValue("Variant"));
      }
      else if (qName.equals("ATLConfocalSettingDefinition")) {
        if (fullSeries.endsWith(" - Master sequential setting")) {
          fullSeries = fullSeries.replaceAll(" - Master sequential setting",
            " - Sequential Setting 0");
        }

        if (fullSeries.indexOf("- Sequential Setting ") == -1) {
          fullSeries += " - Master sequential setting";
        }
        else {
          int ndx = fullSeries.indexOf(" - Sequential Setting ") + 22;
          int n = Integer.parseInt(fullSeries.substring(ndx)) + 1;
          fullSeries = fullSeries.substring(0, ndx) + String.valueOf(n);
        }

        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(fullSeries + " - " + attributes.getQName(i),
            attributes.getValue(i));
        }
      }
      else if (qName.equals("Wheel")) {
        String prefix = fullSeries + " - Wheel " + count + " - ";
        for (int i=0; i<attributes.getLength(); i++) {
          String name = attributes.getQName(i);
          addMeta(prefix + name, attributes.getValue(name));
        }
        count++;
      }
      else if (qName.equals("WheelName")) {
        String prefix =
          fullSeries + " - Wheel " + (count - 1) + " - WheelName ";
        int ndx = 0;
        while (getMeta(prefix + ndx) != null) ndx++;

        addMeta(prefix + ndx, attributes.getValue("FilterName"));
      }
      else if (qName.equals("MultiBand")) {
        String prefix = fullSeries + " - MultiBand Channel " +
          attributes.getValue("Channel") + " - ";
        addMeta(prefix + "LeftWorld", attributes.getValue("LeftWorld"));
        addMeta(prefix + "RightWorld", attributes.getValue("RightWorld"));
        addMeta(prefix + "DyeName", attributes.getValue("DyeName"));
      }
      else if (qName.equals("LaserLineSetting")) {
        String prefix = fullSeries + " - LaserLine " +
          attributes.getValue("LaserLine") + " - ";
        for (int i=0; i<attributes.getLength(); i++) {
          String name = attributes.getQName(i);
          if (!name.equals("LaserLine")) {
            addMeta(prefix + name, attributes.getValue(name));
          }
        }
      }
      else if (qName.equals("Detector")) {
        String prefix = fullSeries + " - Detector Channel " +
          attributes.getValue("Channel") + " - ";
        for (int i=0; i<attributes.getLength(); i++) {
          String name = attributes.getQName(i);
          if (!name.equals("Channel")) {
            addMeta(prefix + name, attributes.getValue(name));
          }
        }
      }
      else if (qName.equals("Laser")) {
        String prefix = fullSeries + " Laser " +
          attributes.getValue("LaserName") + " - ";
        for (int i=0; i<attributes.getLength(); i++) {
          String name = attributes.getQName(i);
          if (!name.equals("LaserName")) {
            addMeta(prefix + name, attributes.getValue(name));
          }
        }
      }
      else if (qName.equals("TimeStamp")) {
        // parse the time stamp
        // we're not 100% sure of how time stamps should be stored, but this
        // seems to work
        long high = Long.parseLong(attributes.getValue("HighInteger"));
        long low = Long.parseLong(attributes.getValue("LowInteger"));

        long stamp = 0;
        high <<= 32;
        if ((int) low < 0) {
          low &= 0xffffffffL;
        }
        stamp = high + low;

        long ms = stamp / 10000;

        String n = String.valueOf(count);
        while (n.length() < 4) n = "0" + n;
        addMeta(fullSeries + " - TimeStamp " + n,
          DataTools.convertDate(ms, DataTools.COBOL));
        count++;
      }
      else if (qName.equals("ChannelScalingInfo")) {
        String prefix = fullSeries + " - ChannelScalingInfo " + count + " - ";
        for (int i=0; i<attributes.getLength(); i++) {
          String name = attributes.getQName(i);
          addMeta(prefix + name, attributes.getValue(name));
        }
      }
      else if (qName.equals("RelTimeStamp")) {
        addMeta(fullSeries + " RelTimeStamp " + attributes.getValue("Frame"),
          attributes.getValue("Time"));
      }
      else count = 0;
    }
  }

}
