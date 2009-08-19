//
// PrairieReader.java
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

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * PrairieReader is the file format reader for
 * Prairie Technologies' TIFF variant.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/PrairieReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/PrairieReader.java">SVN</a></dd></dl>
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

  // -- Fields --

  /** List of files in the current dataset */
  private String[] files;

  /** Helper reader for opening images */
  private MinimalTiffReader tiff;

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
    super("Prairie TIFF", new String[] {"tif", "tiff", "cfg", "xml"});
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

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 1048608;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String s = stream.readString(blockLen);
    if (s.indexOf("xml") != -1 && s.indexOf("PV") != -1) return true;

    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    String software = null;
    try {
      software = ifd.getIFDStringValue(IFD.SOFTWARE, true);
    }
    catch (FormatException exc) {
      return false; // no software tag, or tag is wrong type
    }
    if (software == null) return false;
    if (software.indexOf("Prairie") < 0) return false; // not Prairie software
    return ifd.containsKey(new Integer(PRAIRIE_TAG_1)) &&
      ifd.containsKey(new Integer(PRAIRIE_TAG_2)) &&
      ifd.containsKey(new Integer(PRAIRIE_TAG_3));
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      return new String[] {xmlFile, cfgFile};
    }
    if (files == null) files = new String[0];
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
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    tiff.setId(files[no]);
    return tiff.openBytes(0, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (tiff != null) tiff.close(fileOnly);
    if (!fileOnly) {
      xmlFile = cfgFile = null;
      tiff = null;
      files = null;
      readXML = false;
      readCFG = false;
      isZ = false;
      zt = 0;
      f = gains = offsets = null;
      pixelSizeX = pixelSizeY = 0;
      date = laserPower = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.IFormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("PrairieReader.initFile(" + id + ")");

    if (metadata == null) metadata = new Hashtable();
    if (core == null) core = new CoreMetadata[] {new CoreMetadata()};

    if (checkSuffix(id, PRAIRIE_SUFFIXES)) {
      // we have been given the XML file that lists TIFF files (best case)

      if (checkSuffix(id, XML_SUFFIX)) {
        status("Parsing XML");
        super.initFile(id);
        tiff = new MinimalTiffReader();
        xmlFile = id;
        readXML = true;
      }
      else if (checkSuffix(id, CFG_SUFFIX)) {
        status("Parsing CFG");
        cfgFile = id;
        readCFG = true;
        currentId = id;
      }

      f = new Vector();
      gains = new Vector();
      offsets = new Vector();
      zt = 0;

      RandomAccessInputStream is = new RandomAccessInputStream(id);
      byte[] b = new byte[(int) is.length()];
      is.read(b);
      is.close();

      if (checkSuffix(id, XML_SUFFIX)) {
        core[0].imageCount = 0;
      }

      DefaultHandler handler = new PrairieHandler();
      XMLTools.parseXML(b, handler);

      if (checkSuffix(id, XML_SUFFIX)) {
        files = new String[f.size()];
        f.copyInto(files);
        tiff.setId(files[0]);

        status("Populating metadata");

        if (zt == 0) zt = 1;

        core[0].sizeZ = isZ ? zt : 1;
        core[0].sizeT = isZ ? 1 : zt;
        core[0].sizeC = getImageCount() / (getSizeZ() * getSizeT());
        core[0].dimensionOrder = "XYC" + (isZ ? "ZT" : "TZ");
        core[0].pixelType = FormatTools.UINT16;
        core[0].rgb = false;
        core[0].interleaved = false;
        core[0].littleEndian = tiff.isLittleEndian();
        core[0].indexed = tiff.isIndexed();
        core[0].falseColor = false;

        MetadataStore store =
          new FilterMetadata(getMetadataStore(), isMetadataFiltered());
        MetadataTools.populatePixels(store, this);

        // link Instrument and Image
        String instrumentID = MetadataTools.createLSID("Instrument", 0);
        store.setInstrumentID(instrumentID, 0);
        store.setImageInstrumentRef(instrumentID, 0);

        store.setDimensionsPhysicalSizeX(new Float(pixelSizeX), 0, 0);
        store.setDimensionsPhysicalSizeY(new Float(pixelSizeY), 0, 0);
        for (int i=0; i<getSizeC(); i++) {
          String gain = i < gains.size() ? (String) gains.get(i) : null;
          String offset = i < offsets.size() ? (String) offsets.get(i) : null;

          if (offset != null) {
            store.setDetectorSettingsOffset(new Float(offset), 0, i);
          }
          if (gain != null) {
            store.setDetectorSettingsGain(new Float(gain), 0, i);
          }

          // link DetectorSettings to an actual Detector
          String detectorID = MetadataTools.createLSID("Detector", 0, i);
          store.setDetectorID(detectorID, 0, i);
          store.setDetectorSettingsDetector(detectorID, 0, i);
          store.setDetectorType("Unknown", 0, i);
        }

        if (date != null) {
          date = DateTools.formatDate(date, "MM/dd/yyyy h:mm:ss a");
          if (date != null) store.setImageCreationDate(date, 0);
        }
        else MetadataTools.setDefaultCreationDate(store, id, 0);

        /* TODO : check if this is correct
        if (laserPower != null) {
          store.setLaserPower(new Float(laserPower), 0, 0);
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

      Location file = new Location(id);
      file = file.getAbsoluteFile();
      Location parent = file.getParentFile();
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
        core[0].imageCount++;
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
        addGlobalMeta(key, value);

        if (key.equals("pixelsPerLine")) {
          core[0].sizeX = Integer.parseInt(value);
        }
        else if (key.equals("linesPerFrame")) {
          core[0].sizeY = Integer.parseInt(value);
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
