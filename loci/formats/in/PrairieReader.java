//
// PrairieReader.java
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
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * PrairieReader is the file format reader for
 * Prairie Technologies' TIFF variant.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/PrairieReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/PrairieReader.java">SVN</a></dd></dl>
 */
public class PrairieReader extends FormatReader {

  // -- Constants --

  public static final String[] CFG_SUFFIX = {"cfg"};
  public static final String[] XML_SUFFIX = {"xml"};
  public static final String[] PRAIRIE_SUFFIXES = {"cfg", "xml"};

  // Private tags present in Prairie TIFF files
  // IMPORTANT NOTE: these are the same as Metamorph's private tags - therefore,
  //                 it is likely that Prairie TIFF files will be incorrectly
  //                 identified unless the XML or CFG file is specified
  private static final int PRAIRIE_TAG_1 = 33628;
  private static final int PRAIRIE_TAG_2 = 33629;
  private static final int PRAIRIE_TAG_3 = 33630;

  /** Factory for generating SAX parsers. */
  public static final SAXParserFactory SAX_FACTORY =
    SAXParserFactory.newInstance();

  // -- Fields --

  /** List of files in the current dataset */
  private String[] files;

  /** Helper reader for opening images */
  private TiffReader tiff;

  /** Names of the associated XML files */
  private String xmlFile, cfgFile;
  private boolean readXML = false, readCFG = false;

  private int zt;
  private Vector f, gains, offsets;
  private boolean isZ;
  private float pixelSizeX, pixelSizeY;
  private String date, laserPower;

  // -- Constructor --

  /** Constructs a new Prairie TIFF reader. */
  public PrairieReader() {
    super("Prairie (TIFF)", new String[] {"tif", "tiff", "cfg", "xml"});
    blockCheckLen = 1048608;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!open) return false; // not allowed to touch the file system

    String prefix = name;
    if (prefix.indexOf(".") != -1) {
      prefix = prefix.substring(0, prefix.lastIndexOf("."));
    }

    if (checkSuffix(name, CFG_SUFFIX)) {
      if (prefix.lastIndexOf("Config") == -1) return false;
      prefix = prefix.substring(0, prefix.lastIndexOf("Config"));
    }
    if (prefix.indexOf("_") != -1) {
      prefix = prefix.substring(0, prefix.indexOf("_"));
    }

    // check for appropriately named XML and CFG files

    Location xml = new Location(prefix + ".xml");
    Location cfg = new Location(prefix + "Config.cfg");

    boolean hasMetadataFiles = xml.exists() && cfg.exists();

    return hasMetadataFiles && super.isThisType(name, false);
  }

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block.length < blockCheckLen) return false;

    String s = new String(block);
    if (s.indexOf("xml") != -1 && s.indexOf("PV") != -1) return true;

    try {
      RandomAccessStream stream = new RandomAccessStream(block);
      Hashtable ifd = TiffTools.getFirstIFD(stream);
      String software = (String) TiffTools.getIFDValue(ifd, TiffTools.SOFTWARE);
      stream.close();
      return software.indexOf("Prairie") != -1 &&
        ifd.containsKey(new Integer(PRAIRIE_TAG_1)) &&
        ifd.containsKey(new Integer(PRAIRIE_TAG_2)) &&
        ifd.containsKey(new Integer(PRAIRIE_TAG_3));
    }
    catch (Exception e) {
      if (debug) LogTools.trace(e);
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    id = id.toLowerCase();
    return (checkSuffix(id, PRAIRIE_SUFFIXES)) ?
      FormatTools.MUST_GROUP : FormatTools.CAN_GROUP;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    String[] s = new String[files.length + 2];
    System.arraycopy(files, 0, s, 0, files.length);
    s[files.length] = xmlFile;
    s[files.length + 1] = cfgFile;
    return s;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    tiff.setId(files[no]);
    return tiff.openBytes(0, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (fileOnly && tiff != null) tiff.close(fileOnly);
    else if (!fileOnly) close();
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (tiff != null) tiff.close();
    currentId = xmlFile = cfgFile = null;
    tiff = null;
    files = null;
    readXML = false;
    readCFG = false;
    isZ = false;
    zt = 0;
    f = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.IFormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("PrairieReader.initFile(" + id + ")");

    if (metadata == null) metadata = new Hashtable();
    if (core == null) core = new CoreMetadata(1);

    if (checkSuffix(id, PRAIRIE_SUFFIXES)) {
      // we have been given the XML file that lists TIFF files (best case)

      status("Parsing XML");

      if (checkSuffix(id, XML_SUFFIX)) {
        super.initFile(id);
        tiff = new TiffReader();
        xmlFile = id;
        readXML = true;
      }
      else if (checkSuffix(id, CFG_SUFFIX)) {
        cfgFile = id;
        readCFG = true;
      }

      f = new Vector();
      gains = new Vector();
      offsets = new Vector();
      zt = 0;

      RandomAccessStream is = new RandomAccessStream(id);
      byte[] b = new byte[(int) is.length()];
      is.read(b);
      is.close();

      PrairieHandler handler = new PrairieHandler();
      try {
        SAXParser parser = SAX_FACTORY.newSAXParser();
        parser.parse(new ByteArrayInputStream(b), handler);
      }
      catch (ParserConfigurationException exc) {
        throw new FormatException(exc);
      }
      catch (SAXException exc) {
        throw new FormatException(exc);
      }

      if (checkSuffix(id, XML_SUFFIX)) {
        files = new String[f.size()];
        f.copyInto(files);
        tiff.setId(files[0]);

        status("Populating metadata");

        if (zt == 0) zt = 1;

        core.sizeZ[0] = isZ ? zt : 1;
        core.sizeT[0] = isZ ? 1 : zt;
        core.sizeC[0] = core.imageCount[0] / (core.sizeZ[0] * core.sizeT[0]);
        core.currentOrder[0] = "XYC" + (isZ ? "ZT" : "TZ");
        core.pixelType[0] = FormatTools.UINT16;
        core.rgb[0] = false;
        core.interleaved[0] = false;
        core.littleEndian[0] = tiff.isLittleEndian();
        core.indexed[0] = tiff.isIndexed();
        core.falseColor[0] = false;

        MetadataStore store =
          new FilterMetadata(getMetadataStore(), isMetadataFiltered());
        store.setImageName("", 0);

        MetadataTools.populatePixels(store, this);
        store.setDimensionsPhysicalSizeX(new Float(pixelSizeX), 0, 0);
        store.setDimensionsPhysicalSizeY(new Float(pixelSizeY), 0, 0);
        for (int i=0; i<core.sizeC[0]; i++) {
          String gain = (String) gains.get(i);
          String offset = (String) offsets.get(i);

          /*
          if (offset != null) {
            store.setDetectorSettingsOffset(new Float(offset), 0, i);
          }
          if (gain != null) {
            store.setDetectorSettingsGain(new Float(gain), 0, i);
          }
          */
        }

        if (date != null) {
          SimpleDateFormat parse = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
          Date d = parse.parse(date, new ParsePosition(0));
          SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
          date = fmt.format(d);
        }
        store.setImageCreationDate(date, 0);

        /*
        if (laserPower != null) {
          store.setLaserPower(new Float(laserPower), 0, 0);
        }
        */

        // CTR CHECK
        /*
        String zoom = (String) getMeta("opticalZoom");
        if (zoom != null) {
          store.setDisplayOptions(new Float(zoom),
            new Boolean(core.sizeC[0] > 1), new Boolean(core.sizeC[0] > 1),
            new Boolean(core.sizeC[0] > 2), Boolean.FALSE,
            null, null, null, null, null, null, null, null, null, null, null);
        }
        */
      }

      if (!readXML || !readCFG) {
        File file = new File(id).getAbsoluteFile();
        File parent = file.getParentFile();
        String[] listing = file.exists() ? parent.list() :
          (String[]) Location.getIdMap().keySet().toArray(new String[0]);
        for (int i=0; i<listing.length; i++) {
          if ((!readXML && checkSuffix(listing[i], XML_SUFFIX)) ||
            (readXML && checkSuffix(listing[i], CFG_SUFFIX)))
          {
            String dir = "";
            if (file.exists()) {
              dir = parent.getPath();
              if (!dir.endsWith(File.separator)) dir += File.separator;
            }
            initFile(dir + listing[i]);
          }
        }
      }
    }
    else {
      // we have been given a TIFF file - reinitialize with the proper XML file

      status("Finding XML file");

      Location f = new Location(id);
      f = f.getAbsoluteFile();
      Location parent = f.getParentFile();
      String[] listing = parent.list();
      for (int i=0; i<listing.length; i++) {
        if (checkSuffix(listing[i], PRAIRIE_SUFFIXES)) {
          initFile(new Location(parent, listing[i]).getAbsolutePath());
        }
      }
    }
    if (currentId == null) currentId = id;
  }

  // -- Helper classes --

  /** SAX handler for parsing XML. */
  public class PrairieHandler extends DefaultHandler {
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("PVScan")) {
        date = attributes.getValue("date");
      }
      else if (qName.equals("Frame")) zt++;
      else if (qName.equals("Sequence")) {
        isZ = attributes.getValue("type").equals("ZSeries");
      }
      else if (qName.equals("File")) {
        core.imageCount[0]++;
        File current = new File(currentId).getAbsoluteFile();
        String dir = "";
        if (current.exists()) {
          dir = current.getPath();
          dir = dir.substring(0, dir.lastIndexOf(File.separator) + 1);
        }
        f.add(dir + attributes.getValue("filename"));
      }
      else if (qName.equals("Key")) {
        String key = attributes.getValue("key");
        String value = attributes.getValue("value");
        addMeta(key, value);

        if (key.equals("pixelsPerLine")) {
          core.sizeX[0] = Integer.parseInt(value);
        }
        else if (key.equals("linesPerFrame")) {
          core.sizeY[0] = Integer.parseInt(value);
        }
        else if (key.equals("micronsPerPixel_XAxis")) {
          pixelSizeX = Float.parseFloat(value);
        }
        else if (key.equals("micronsPerPixel_YAxis")) {
          pixelSizeY = Float.parseFloat(value);
        }
        else if (key.startsWith("pmtGain_")) gains.add(value);
        else if (key.startsWith("pmtOffset_")) offsets.add(value);
        else if (key.equals("laserPower_0")) laserPower = value;
      }
    }
  }

}
