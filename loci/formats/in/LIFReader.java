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
  protected Vector offsets;

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

  // -- Constructor --

  /** Constructs a new Leica LIF reader. */
  public LIFReader() { super("Leica Image File Format", "lif"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return block[0] == 0x70;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    long offset = ((Long) offsets.get(series)).longValue();
    in.seek(offset + core.sizeX[series] * core.sizeY[series] * no *
      FormatTools.getBytesPerPixel(getPixelType()) * getRGBChannelCount());

    in.read(buf);
    return buf;
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
    // parse raw key/value pairs - adapted from FlexReader

    containerNames = new Vector();
    containerCounts = new Vector();
    seriesNames = new Vector();

    LIFHandler handler = new LIFHandler();

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

    Vector elements = new Vector();

    status("Populating native metadata");

    // first parse each element in the XML string

    StringTokenizer st = new StringTokenizer(xml, ">");
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      elements.add(token.substring(1));
    }

    // the first element contains version information

    String token = (String) elements.get(0);
    String key = token.substring(0, token.indexOf("\""));
    String value = token.substring(token.indexOf("\"") + 1, token.length()-1);
    addMeta(key, value);

    // what we have right now is a vector of XML elements, which need to
    // be parsed into the appropriate image dimensions

    int ndx = 1;

    // the image data we need starts with the token "ElementName='blah'" and
    // ends with the token "/ImageDescription"

    int numDatasets = 0;
    Vector widths = new Vector();
    Vector heights = new Vector();
    Vector zs = new Vector();
    Vector ts = new Vector();
    Vector channels = new Vector();
    Vector bps = new Vector();
    Vector extraDims = new Vector();

    while (ndx < elements.size()) {
      token = (String) elements.get(ndx);

      // if the element contains a key/value pair, parse it and put it in
      // the metadata hashtable

      if (token.startsWith("ScannerSettingRecord")) {
        if (token.indexOf("csScanMode") != -1) {
          int index = token.indexOf("Variant") + 7;
          String ordering = token.substring(index + 2,
            token.indexOf("\"", index + 3));
          ordering = ordering.toLowerCase();

          if (ordering.indexOf("x") == -1 || ordering.indexOf("y") == -1 ||
            ordering.indexOf("xy") == -1)
          {
            int xPos = ordering.indexOf("x");
            int yPos = ordering.indexOf("y");
            int zPos = ordering.indexOf("z");
            int tPos = ordering.indexOf("t");

            if (xPos < 0) xPos = 0;
            if (yPos < 0) yPos = 1;
            if (zPos < 0) zPos = 2;
            if (tPos < 0) tPos = 3;

            int x = ((Integer) widths.get(widths.size() - 1)).intValue();
            int y = ((Integer) heights.get(widths.size() - 1)).intValue();
            int z = ((Integer) zs.get(widths.size() - 1)).intValue();
            int t = ((Integer) ts.get(widths.size() - 1)).intValue();

            int[] dimensions = {x, y, z, t};

            x = dimensions[xPos];
            y = dimensions[yPos];
            z = dimensions[zPos];
            t = dimensions[tPos];

            widths.setElementAt(new Integer(x), widths.size() - 1);
            heights.setElementAt(new Integer(y), heights.size() - 1);
            zs.setElementAt(new Integer(z), zs.size() - 1);
            ts.setElementAt(new Integer(t), ts.size() - 1);
          }
        }
        else if (token.indexOf("dblVoxel") != -1) {
          int index = token.indexOf("Variant") + 7;
          String size = token.substring(index + 2,
            token.indexOf("\"", index + 3));
          float cal = Float.parseFloat(size) * 1000000;
          if (token.indexOf("X") != -1) xcal.add(new Float(cal));
          else if (token.indexOf("Y") != -1) ycal.add(new Float(cal));
          else if (token.indexOf("Z") != -1) zcal.add(new Float(cal));
        }
      }
      else if (token.startsWith("Element Name")) {
        // loop until we find "/ImageDescription"

        numDatasets++;
        int numChannels = 0;
        int extras = 1;

        while (token.indexOf("/ImageDescription") == -1) {
          if (token.indexOf("=") != -1) {
            // create a small hashtable to store just this element's data

            if (token.startsWith("Element Name")) {
              // hack to override first series name
              int idx = numDatasets - 1;
              if (idx >= seriesNames.size()) {
                numDatasets = seriesNames.size();
                idx = numDatasets - 1;
              }
            }

            Hashtable tmp = new Hashtable();
            while (token.length() > 2) {
              key = token.substring(0, token.indexOf("\"") - 1);
              value = token.substring(token.indexOf("\"") + 1,
                token.indexOf("\"", token.indexOf("\"") + 1));

              token = token.substring(key.length() + value.length() + 3);

              key = key.trim();
              value = value.trim();
              tmp.put(key, value);
            }

            if (tmp.get("ChannelDescription DataType") != null) {
              // found channel description block
              numChannels++;
              if (numChannels == 1) {
                bps.add(new Integer((String) tmp.get("Resolution")));
              }
            }
            else if (tmp.get("DimensionDescription DimID") != null) {
              // found dimension description block

              int w = Integer.parseInt((String) tmp.get("NumberOfElements"));
              int id = Integer.parseInt((String)
                tmp.get("DimensionDescription DimID"));

              switch (id) {
                case 1:
                  widths.add(new Integer(w));
                  break;
                case 2:
                  heights.add(new Integer(w));
                  break;
                case 3:
                  zs.add(new Integer(w));
                  break;
                case 4:
                  ts.add(new Integer(w));
                  break;
                default:
                  extras *= w;
              }
            }
          }

          ndx++;
          if (elements != null && ndx < elements.size()) {
            token = (String) elements.get(ndx);
          }
          else break;
        }
        extraDims.add(new Integer(extras));
        if (numChannels == 0) numChannels++;
        channels.add(new Integer(numChannels));

        if (widths.size() < numDatasets && heights.size() < numDatasets) {
          numDatasets--;
        }
        else {
          if (widths.size() < numDatasets) widths.add(new Integer(1));
          if (heights.size() < numDatasets) heights.add(new Integer(1));
          if (zs.size() < numDatasets) zs.add(new Integer(1));
          if (ts.size() < numDatasets) ts.add(new Integer(1));
          if (bps.size() < numDatasets) bps.add(new Integer(8));
        }
      }
      ndx++;
    }

    numDatasets = widths.size();

    bitsPerPixel = new int[numDatasets];
    extraDimensions = new int[numDatasets];

    // Populate metadata store

    status("Populating metadata");

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    core = new CoreMetadata(numDatasets);
    Arrays.fill(core.orderCertain, true);

    for (int i=0; i<numDatasets; i++) {
      core.sizeX[i] = ((Integer) widths.get(i)).intValue();
      core.sizeY[i] = ((Integer) heights.get(i)).intValue();
      core.sizeZ[i] = ((Integer) zs.get(i)).intValue();
      core.sizeC[i] = ((Integer) channels.get(i)).intValue();
      core.sizeT[i] = ((Integer) ts.get(i)).intValue();
      core.currentOrder[i] =
        (core.sizeZ[i] > core.sizeT[i]) ? "XYCZT" : "XYCTZ";

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

      Integer ii = new Integer(i);

      String seriesName = (String) seriesNames.get(i);
      if (seriesName == null || seriesName.trim().length() == 0) {
        seriesName = "Series " + (i + 1);
      }
      store.setImage(seriesName, null, null, ii);

      FormatTools.populatePixels(store, this);

      Float xf = i < xcal.size() ? (Float) xcal.get(i) : null;
      Float yf = i < ycal.size() ? (Float) ycal.get(i) : null;
      Float zf = i < zcal.size() ? (Float) zcal.get(i) : null;

      store.setDimensions(xf, yf, zf, null, null, ii);
      for (int j=0; j<core.sizeC[i]; j++) {
        store.setLogicalChannel(j, null, null, null, null, null, null, null,
          null, null, null, null, null, null, null, null, null, null, null,
          null, null, null, null, null, ii);
      }

      String zoom = (String) getMeta(seriesName + " - dblZoom");
      store.setDisplayOptions(zoom == null ? null : new Float(zoom),
        new Boolean(core.sizeC[i] > 1), new Boolean(core.sizeC[i] > 1),
        new Boolean(core.sizeC[i] > 2), new Boolean(isRGB()), null,
        null, null, null, null, ii, null, null, null, null, null);

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
        addMeta(prefix + "Min", attributes.getValue("Min"));
        addMeta(prefix + "Max", attributes.getValue("Max"));
        addMeta(prefix + "Resolution", attributes.getValue("Resolution"));
        addMeta(prefix + "LUTName", attributes.getValue("LUTName"));
        addMeta(prefix + "IsLUTInverted", attributes.getValue("IsLUTInverted"));
        count++;
      }
      else if (qName.equals("DimensionDescription")) {
        String prefix = fullSeries + " - Dimension " + count + " - ";
        addMeta(prefix + "NumberOfElements",
          attributes.getValue("NumberOfElements"));
        addMeta(prefix + "Length", attributes.getValue("Length"));
        addMeta(prefix + "Origin", attributes.getValue("Origin"));
        addMeta(prefix + "DimID", attributes.getValue("DimID"));
      }
      else if (qName.equals("ScannerSettingRecord")) {
        String key = attributes.getValue("Identifier") + " - " +
          attributes.getValue("Description");
        addMeta(fullSeries + " - " + key, attributes.getValue("Variant"));
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
          int n = Integer.parseInt(fullSeries.substring(ndx));
          n++;
          fullSeries = fullSeries.substring(0, ndx) + String.valueOf(n);
        }

        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(fullSeries + " - " + attributes.getQName(i),
            attributes.getValue(i));
        }
      }
      else if (qName.equals("Wheel")) {
        String prefix = fullSeries + " - Wheel " + count + " - ";
        addMeta(prefix + "Qualifier", attributes.getValue("Qualifier"));
        addMeta(prefix + "FilterIndex", attributes.getValue("FilterIndex"));
        addMeta(prefix + "FilterSpectrumPos",
          attributes.getValue("FilterSpectrumPos"));
        addMeta(prefix + "IsSpectrumTurnMode",
          attributes.getValue("IsSpectrumTurnMode"));
        addMeta(prefix + "IndexChanged", attributes.getValue("IndexChanged"));
        addMeta(prefix + "SpectrumChanged",
          attributes.getValue("SpectrumChanged"));
        count++;
      }
      else if (qName.equals("WheelName")) {
        String prefix = fullSeries + " - Wheel " + (count - 1) + " - WheelName ";
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
        addMeta(prefix + "IntensityDev", attributes.getValue("IntensityDev"));
        addMeta(prefix + "IntensityLowDev",
          attributes.getValue("IntensityLowDev"));
        addMeta(prefix + "AOBSIntensityDev",
          attributes.getValue("AOBSIntensityDev"));
        addMeta(prefix + "AOBSIntensityLowDev",
          attributes.getValue("AOBSIntensityLowDev"));
        addMeta(prefix + "EnableDoubleMode",
          attributes.getValue("EnableDoubleMode"));
        addMeta(prefix + "LineIndex", attributes.getValue("LineIndex"));
        addMeta(prefix + "Qualifier", attributes.getValue("Qualifier"));
        addMeta(prefix + "SequenceIndex",
          attributes.getValue("SequenceIndex"));
      }
      else if (qName.equals("Detector")) {
        String prefix = fullSeries + " - Detector Channel " +
          attributes.getValue("Channel") + " - ";
        addMeta(prefix + "IsActive", attributes.getValue("IsActive"));
        addMeta(prefix + "IsReferenceUnitActivatedForCorrection",
          attributes.getValue("IsReferenceUnitActivatedForCorrection"));
        addMeta(prefix + "Gain", attributes.getValue("Gain"));
        addMeta(prefix + "Offset", attributes.getValue("Offset"));
      }
      else if (qName.equals("Laser")) {
        String prefix = fullSeries + " Laser " +
          attributes.getValue("LaserName") + " - ";
        addMeta(prefix + "CanDoLinearOutputPower",
          attributes.getValue("CanDoLinearOutputPower"));
        addMeta(prefix + "OutputPower", attributes.getValue("OutputPower"));
        addMeta(prefix + "Wavelength", attributes.getValue("Wavelength"));
      }
      else if (qName.equals("TimeStamp")) {

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
        addMeta(prefix + "WhiteValue", attributes.getValue("WhiteValue"));
        addMeta(prefix + "BlackValue", attributes.getValue("BlackValue"));
        addMeta(prefix + "GammaValue", attributes.getValue("GammaValue"));
        addMeta(prefix + "Automatic", attributes.getValue("Automatic"));
      }
      else if (qName.equals("RelTimeStamp")) {
        addMeta(fullSeries + " RelTimeStamp " + attributes.getValue("Frame"),
          attributes.getValue("Time"));
      }
      else count = 0;
    }
  }

}
