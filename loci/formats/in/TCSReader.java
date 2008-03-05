//
// TCSReader.java
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
import java.text.*;
import java.util.*;
import javax.xml.parsers.*;
import loci.formats.*;
import loci.formats.in.TiffReader;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * TCSReader is the file format reader for Leica TCS TIFF files and their
 * companion XML file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/TCSReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/TCSReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class TCSReader extends FormatReader {

  // -- Constants --

  /** Factory for generating SAX parsers. */
  public static final SAXParserFactory SAX_FACTORY =
    SAXParserFactory.newInstance();

  // -- Fields --

  /** List of TIFF files. */
  private Vector tiffs;

  /** Helper readers. */
  private TiffReader[] tiffReaders;

  private Vector seriesNames;
  private Vector containerNames;
  private Vector containerCounts;
  private Vector xcal, ycal, zcal;
  private Vector x, y, z, c, t, bits;

  // -- Constructor --

  public TCSReader() {
    super("Leica TCS TIFF", new String[] {"tif", "tiff", "xml"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!open) return false; // not allowed to touch the file system

    // check that there is no LEI file
    String prefix = name;
    if (prefix.indexOf(".") != -1) {
      prefix = prefix.substring(0, prefix.lastIndexOf("."));
    }
    Location lei = new Location(prefix + ".lei");
    if (!lei.exists()) {
      lei = new Location(prefix + ".LEI");
      while (!lei.exists() && prefix.indexOf("_") != -1) {
        prefix = prefix.substring(0, prefix.lastIndexOf("_"));
        lei = new Location(prefix + ".lei");
        if (!lei.exists()) lei = new Location(prefix + ".LEI");
      }
    }
    if (lei.exists()) return false;
    try {
      return isThisType(new RandomAccessStream(name));
    }
    catch (IOException e) {
      if (debug) LogTools.trace(e);
      return false;
    }
  }

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    try {
      return isThisType(new RandomAccessStream(block));
    }
    catch (IOException e) {
      if (debug) LogTools.trace(e);
      return false;
    }
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReaders[0].get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReaders[0].get16BitLookupTable();
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

    int n = 0;
    for (int i=0; i<series; i++) {
      n += core.imageCount[i];
    }
    n += no;

    if (tiffReaders.length == 1) {
      return tiffReaders[0].openBytes(n, buf, x, y, w, h);
    }
    return tiffReaders[n].openBytes(0, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    Vector v = new Vector();
    for (int i=0; i<tiffs.size(); i++) {
      v.add(tiffs.get(i));
    }
    return (String[]) v.toArray(new String[0]);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    tiffs = null;
    tiffReaders = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("TCSReader.initFile(" + id + ")");

    String lname = id.toLowerCase();
    if (lname.endsWith("tiff") || lname.endsWith("tif")) {
      // find the associated XML file, if it exists
      Location l = new Location(id).getAbsoluteFile();
      Location parent = l.getParentFile();
      String[] list = parent.list();
      for (int i=0; i<list.length; i++) {
        if (list[i].toLowerCase().endsWith("xml")) {
          initFile(new Location(parent, list[i]).getAbsolutePath());
          return;
        }
      }
    }

    super.initFile(id);

    if (lname.endsWith("xml")) {
      in = new RandomAccessStream(id);
      xcal = new Vector();
      ycal = new Vector();
      zcal = new Vector();
      seriesNames = new Vector();
      containerNames = new Vector();
      containerCounts = new Vector();
      x = new Vector();
      y = new Vector();
      z = new Vector();
      c = new Vector();
      t = new Vector();
      bits = new Vector();

      // parse XML metadata

      TCSHandler handler = new TCSHandler();
      String prefix = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><LEICA>";
      String suffix = "</LEICA>";
      String xml = prefix + in.readString((int) in.length()) + suffix;

      for (int i=0; i<xml.length(); i++) {
        char ch = xml.charAt(i);
        if (Character.isISOControl(ch) || !Character.isDefined(ch)) {
          xml = xml.replace(ch, ' ');
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

      // look for associated TIFF files

      tiffs = new Vector();

      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      String[] list = parent.list();
      Arrays.sort(list);
      for (int i=0; i<list.length; i++) {
        String lcase = list[i].toLowerCase();
        if (lcase.endsWith("tif") || lcase.endsWith("tiff")) {
          String file = new Location(parent, list[i]).getAbsolutePath();
          Hashtable ifd = TiffTools.getIFDs(new RandomAccessStream(file))[0];
          String software =
            (String) TiffTools.getIFDValue(ifd, TiffTools.SOFTWARE);
          if (software != null && software.trim().equals("TCSNTV")) {
            tiffs.add(file);
          }
        }
      }

      tiffReaders = new TiffReader[tiffs.size()];

      for (int i=0; i<tiffReaders.length; i++) {
        tiffReaders[i] = new TiffReader();
        tiffReaders[i].setId((String) tiffs.get(i));
      }

      core = new CoreMetadata(x.size());
      for (int i=0; i<x.size(); i++) {
        core.sizeX[i] = ((Integer) x.get(i)).intValue();
        core.sizeY[i] = ((Integer) y.get(i)).intValue();
        if (z.size() > 0) core.sizeZ[i] = ((Integer) z.get(i)).intValue();
        else core.sizeZ[i] = 1;
        if (c.size() > 0) core.sizeC[i] = ((Integer) c.get(i)).intValue();
        else core.sizeC[i] = 1;
        if (t.size() > 0) core.sizeT[i] = ((Integer) t.get(i)).intValue();
        else core.sizeT[i] = 1;
        core.imageCount[i] = core.sizeZ[i] * core.sizeC[i] * core.sizeT[i];
        int bpp = ((Integer) bits.get(i)).intValue();
        while (bpp % 8 != 0) bpp++;
        switch (bpp) {
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
        if (tiffs.size() < core.imageCount[i]) {
          int div = core.imageCount[i] / core.sizeC[i];
          core.imageCount[i] = tiffs.size();
          if (div >= core.sizeZ[i]) core.sizeZ[i] /= div;
          else if (div >= core.sizeT[i]) core.sizeT[i] /= div;
        }
      }
      Arrays.fill(core.currentOrder,
        core.sizeZ[0] > core.sizeT[0] ? "XYCZT" : "XYCTZ");
      Arrays.fill(core.metadataComplete, true);
      Arrays.fill(core.rgb, false);
      Arrays.fill(core.interleaved, false);
      Arrays.fill(core.indexed, tiffReaders[0].isIndexed());
      Arrays.fill(core.falseColor, true);

      MetadataStore store =
        new FilterMetadata(getMetadataStore(), isMetadataFiltered());

      for (int i=0; i<x.size(); i++) {
        store.setImageName((String) seriesNames.get(i), i);
        store.setImageCreationDate(
          DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), i);
      }

      MetadataTools.populatePixels(store, this);

      for (int i=0; i<x.size(); i++) {
        // CTR CHECK
//        for (int cc=0; cc<core.sizeC[i]; cc++) {
//          store.setLogicalChannel(cc, null, null, null, null, null, null, null,
//            null, null, null, null, null, null, null, null,
//            null, null, null, null, null, null, null, null, new Integer(i));
//        }
        store.setDimensionsPhysicalSizeX((Float) xcal.get(i), i, 0);
        store.setDimensionsPhysicalSizeY((Float) ycal.get(i), i, 0);
        store.setDimensionsPhysicalSizeZ((Float) zcal.get(i), i, 0);
      }
    }
    else {
      tiffs = new Vector();
      tiffs.add(id);
      tiffReaders = new TiffReader[1];
      tiffReaders[0] = new TiffReader();
      tiffReaders[0].setMetadataStore(getMetadataStore());
      tiffReaders[0].setId(id);
      in = new RandomAccessStream(id);

      Hashtable[] ifds = TiffTools.getIFDs(in);

      int[] ch = new int[ifds.length];
      int[] idx = new int[ifds.length];
      long[] stamp = new long[ifds.length];

      int channelCount = 0;
      SimpleDateFormat fmt = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");

      for (int i=0; i<ifds.length; i++) {
        String document =
          (String) ifds[i].get(new Integer(TiffTools.DOCUMENT_NAME));
        if (document == null) continue;

        int index = document.indexOf("INDEX");
        String s = document.substring(8, index).trim();
        ch[i] = Integer.parseInt(s);
        if (ch[i] > channelCount) channelCount = ch[i];

        String n = document.substring(index + 6,
          document.indexOf(" ", index + 6)).trim();
        idx[i] = Integer.parseInt(n);

        String date = document.substring(document.indexOf(" ", index + 6),
          document.indexOf("FORMAT")).trim();
        stamp[i] = fmt.parse(date, new ParsePosition(0)).getTime();
      }

      core.sizeT[0] = 0;
      core.currentOrder[0] = core.rgb[0] ? "XYC" : "XY";

      // determine the axis sizes and ordering
      boolean unique = true;
      for (int i=0; i<stamp.length; i++) {
        for (int j=i+1; j<stamp.length; j++) {
          if (stamp[j] == stamp[i]) {
            unique = false;
            break;
          }
        }
        if (unique) {
          core.sizeT[0]++;
          if (core.currentOrder[0].indexOf("T") < 0) {
            core.currentOrder[0] += "T";
          }
        }
        else if (i > 0) {
          if ((ch[i] != ch[i - 1]) && core.currentOrder[0].indexOf("C") < 0) {
            core.currentOrder[0] += "C";
          }
          else if (core.currentOrder[0].indexOf("Z") < 0) {
            core.currentOrder[0] += "Z";
          }
        }
        unique = true;
      }

      if (core.currentOrder[0].indexOf("Z") < 0) core.currentOrder[0] += "Z";
      if (core.currentOrder[0].indexOf("C") < 0) core.currentOrder[0] += "C";
      if (core.currentOrder[0].indexOf("T") < 0) core.currentOrder[0] += "T";

      if (core.sizeT[0] == 0) core.sizeT[0] = 1;
      if (channelCount == 0) channelCount = 1;
      core.sizeZ[0] = ifds.length / (core.sizeT[0] * channelCount);
      core.sizeC[0] *= channelCount;
      core.imageCount[0] = core.sizeZ[0] * core.sizeT[0] * channelCount;

      // cut up comment

      String comment = TiffTools.getComment(ifds[0]);
      if (comment != null && comment.startsWith("[")) {
        StringTokenizer st = new StringTokenizer(comment, "\n");
        while (st.hasMoreTokens()) {
          String token = st.nextToken();
          if (!token.startsWith("[")) {
            int eq = token.indexOf("=");
            String key = token.substring(0, eq);
            String value = token.substring(eq + 1);
            addMeta(key, value);
          }
        }
        metadata.remove("Comment");
      }
      core = tiffReaders[0].getCoreMetadata();
    }
  }

  // -- Helper methods --

  private boolean isThisType(RandomAccessStream stream) throws IOException {
    // check for Leica TCS IFD directory entries
    Hashtable ifd = TiffTools.getFirstIFD(stream);
    stream.close();

    if (ifd == null) return false;
    String document = (String) ifd.get(new Integer(TiffTools.DOCUMENT_NAME));
    if (document == null) document = "";
    Object s = ifd.get(new Integer(TiffTools.SOFTWARE));
    String software = null;
    if (s instanceof String) software = (String) s;
    else if (s instanceof String[]) {
      String[] ss = (String[]) s;
      if (ss.length > 0) software = ss[0];
    }
    if (software == null) software = "";
    return document.startsWith("CHANNEL") || software.trim().equals("TCSNTV");
  }

  // -- Helper classes --

  /** SAX handler for parsing XML. */
  class TCSHandler extends DefaultHandler {
    private String series = "", fullSeries = "";
    private int count = 0;
    private boolean firstElement = true;

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("Element")) {
        if (!attributes.getValue("Name").equals("DCROISet") && !firstElement) {
          series = attributes.getValue("Name");
          containerNames.add(series);
          if (fullSeries == null || fullSeries.equals("")) fullSeries = series;
          else fullSeries = "/" + series;
        }
        else if (firstElement) firstElement = false;
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
        String prefix = "Channel " + count + " - ";
        if (fullSeries != null && !fullSeries.equals("")) {
          prefix = fullSeries + " - " + prefix;
        }
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(prefix + attributes.getQName(i), attributes.getValue(i));
        }
        count++;
        if (c.size() > seriesNames.size() - 1) {
          c.setElementAt(new Integer(count), seriesNames.size() - 1);
        }
        else c.add(new Integer(count));
      }
      else if (qName.equals("DimensionDescription")) {
        String prefix = "Dimension " + count + " - ";
        if (fullSeries != null && !fullSeries.equals("")) {
          prefix = fullSeries + " - " + prefix;
        }
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(prefix + attributes.getQName(i), attributes.getValue(i));
        }
        int len = Integer.parseInt(attributes.getValue("NumberOfElements"));
        int id = Integer.parseInt(attributes.getValue("DimID"));
        float size = Float.parseFloat(attributes.getValue("Length"));
        if (size < 0) size *= -1;

        switch (id) {
          case 1:
            x.add(new Integer(len));
            xcal.add(new Float((size * 1000000) / len));
            int b = Integer.parseInt(attributes.getValue("BytesInc"));
            bits.add(new Integer(b * 8));
            break;
          case 2:
            y.add(new Integer(len));
            ycal.add(new Float((size * 1000000) / len));
            break;
          case 3:
            z.add(new Integer(len));
            zcal.add(new Float((size * 1000000) / len));
            break;
          default:
            t.add(new Integer(len));
            break;
        }
      }
      else if (qName.equals("ScannerSettingRecord")) {
        String key = attributes.getValue("Identifier") + " - " +
          attributes.getValue("Description");
        if (fullSeries != null && !fullSeries.equals("")) {
          key = fullSeries + " - " + key;
        }
        addMeta(key, attributes.getValue("Variant"));
      }
      else if (qName.equals("FilterSettingRecord")) {
        String key = attributes.getValue("ObjectName") + " - " +
          attributes.getValue("Description") + " - " +
          attributes.getValue("Attribute");
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(key + " - " + attributes.getQName(i),
            attributes.getValue(i));
        }
      }
      else if (qName.equals("ATLConfocalSettingDefinition")) {
        if (fullSeries.endsWith("Master sequential setting")) {
          fullSeries = fullSeries.replaceAll("Master sequential setting",
            "Sequential Setting 0");
        }

        if (fullSeries.indexOf("Sequential Setting ") == -1) {
          if (fullSeries.equals("")) fullSeries = "Master sequential setting";
          else fullSeries += " - Master sequential setting";
        }
        else {
          int ndx = fullSeries.indexOf("Sequential Setting ") + 19;
          int n = Integer.parseInt(fullSeries.substring(ndx)) + 1;
          fullSeries = fullSeries.substring(0, ndx) + String.valueOf(n);
        }
        String prefix = "";
        if (fullSeries != null && !fullSeries.equals("")) {
          prefix = fullSeries + " - ";
        }
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(prefix + attributes.getQName(i), attributes.getValue(i));
        }
      }
      else if (qName.equals("Wheel")) {
        String prefix = "Wheel " + count + " - ";
        if (fullSeries != null && !fullSeries.equals("")) {
          prefix = fullSeries + " - " + prefix;
        }
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(prefix + attributes.getQName(i), attributes.getValue(i));
        }
        count++;
      }
      else if (qName.equals("WheelName")) {
        String prefix = "Wheel " + (count - 1) + " - WheelName ";
        if (fullSeries != null && !fullSeries.equals("")) {
          prefix = fullSeries + " - " + prefix;
        }
        int ndx = 0;
        while (getMeta(prefix + ndx) != null) ndx++;
        addMeta(prefix + ndx, attributes.getValue("FilterName"));
      }
      else if (qName.equals("MultiBand")) {
        String prefix = "MultiBand Channel " + attributes.getValue("Channel");
        if (fullSeries != null && !fullSeries.equals("")) {
          prefix = fullSeries + " - " + prefix;
        }
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(prefix + " - " + attributes.getQName(i),
            attributes.getValue(i));
        }
      }
      else if (qName.equals("LaserLineSetting")) {
        String prefix = "LaserLine " + attributes.getValue("LaserLine");
        if (fullSeries != null && !fullSeries.equals("")) {
          prefix = fullSeries + " - " + prefix;
        }
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(prefix + " - " + attributes.getQName(i),
            attributes.getValue(i));
        }
      }
      else if (qName.equals("Detector")) {
        String prefix = "Detector Channel " + attributes.getValue("Channel");
        if (fullSeries != null && !fullSeries.equals("")) {
          prefix = fullSeries + " - " + prefix;
        }
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(prefix + " - " + attributes.getQName(i),
            attributes.getValue(i));
        }
      }
      else if (qName.equals("Laser")) {
        String prefix = "Laser " + attributes.getValue("LaserName");
        if (fullSeries != null && !fullSeries.equals("")) {
          prefix = fullSeries + " - " + prefix;
        }
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(prefix + " - " + attributes.getQName(i),
            attributes.getValue(i));
        }
      }
      else if (qName.equals("TimeStamp")) {
        long high = Long.parseLong(attributes.getValue("HighInteger"));
        long low = Long.parseLong(attributes.getValue("LowInteger"));

        high <<= 32;
        if ((int) low < 0) {
          low &= 0xffffffffL;
        }
        long stamp = high + low;
        long ms = stamp / 10000;

        String n = String.valueOf(count);
        while (n.length() < 4) n = "0" + n;
        addMeta(fullSeries + " - TimeStamp " + n,
          DataTools.convertDate(ms, DataTools.COBOL));
        count++;
      }
      else if (qName.equals("ChannelScalingInfo")) {
        String prefix = "ChannelScalingInfo " + count;
        if (fullSeries != null && !fullSeries.equals("")) {
          prefix = fullSeries + " - " + prefix;
        }
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta(prefix + " - " + attributes.getQName(i),
            attributes.getValue(i));
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
