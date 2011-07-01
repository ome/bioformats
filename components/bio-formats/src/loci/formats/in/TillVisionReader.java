//
// TillVisionReader.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.DateTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.services.POIService;

/**
 * TillVisionReader is the file format reader for TillVision files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/TillVisionReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/TillVisionReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class TillVisionReader extends FormatReader {

  // -- Constants --

  private static final byte[] MARKER_0 = new byte[] {(byte) 0x80, 3, 0};
  private static final byte[] MARKER_1 = new byte[] {(byte) 0x81, 3, 0};
  private static final byte[] MARKER_2 =
    new byte[] {0x43, 0x49, 0x6d, 0x61, 0x67, 0x65, 0x03, 0x00};

  private static final String[] DATE_FORMATS = new String[] {
    "mm/dd/yy HH:mm:ss aa", "mm/dd/yy HH:mm:ss.SSS aa", "mm/dd/yy",
    "HH:mm:ss aa", "HH:mm:ss.SSS aa"};

  // -- Fields --

  private String[] pixelsFiles;
  private RandomAccessInputStream pixelsStream;
  private Hashtable<Integer, Double> exposureTimes;
  private boolean embeddedImages;
  private long embeddedOffset;
  private String[] infFiles;

  private Vector<String> imageNames = new Vector<String>();
  private Vector<String> types = new Vector<String>();
  private Vector<String> dates = new Vector<String>();

  // -- Constructor --

  /** Constructs a new TillVision reader. */
  public TillVisionReader() {
    super("TillVision", new String[] {"vws", "pst", "inf"});
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "vws") || checkSuffix(name, "pst")) {
      return true;
    }
    String pstFile = name;
    if (name.indexOf(".") != -1) {
      pstFile = pstFile.substring(0, pstFile.lastIndexOf("."));
    }
    pstFile += ".pst";
    return new Location(pstFile).exists();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int plane = FormatTools.getPlaneSize(this);
    if (embeddedImages) {
      in.seek(embeddedOffset + no * plane);
      readPlane(in, x, y, w, h, buf);
    }
    else {
      pixelsStream = new RandomAccessInputStream(pixelsFiles[series]);
      if ((no + 1) * plane <= pixelsStream.length()) {
        pixelsStream.seek(no * plane);
        readPlane(pixelsStream, x, y, w, h, buf);
      }
      pixelsStream.close();
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (pixelsStream != null) pixelsStream.close();
      pixelsStream = null;
      pixelsFiles = null;
      infFiles = null;
      embeddedOffset = 0;
      embeddedImages = false;
      exposureTimes = null;
      imageNames.clear();
      types.clear();
      dates.clear();
    }
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return !new Location(id.replaceAll(".vws", ".pst")).exists();
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    Vector<String> files = new Vector<String>();
    files.add(currentId);
    if (!noPixels) {
      if (pixelsFiles[getSeries()] != null) {
        files.add(pixelsFiles[getSeries()]);
      }
    }
    if (infFiles[getSeries()] != null) {
      files.add(infFiles[getSeries()]);
    }
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    // make sure that we have the .vws file

    if (!checkSuffix(id, "vws")) {
      Location pst = new Location(id).getAbsoluteFile();
      String name = pst.getParentFile().getName();
      Location parent = pst.getParentFile().getParentFile();
      Location vwsFile = new Location(parent, name.replaceAll(".pst", ".vws"));
      if (vwsFile.exists() && !vwsFile.isDirectory()) {
        id = vwsFile.getAbsolutePath();
      }
      else if (vwsFile.isDirectory()) {
        parent = pst.getParentFile();
        String[] list = parent.list(true);
        for (String f : list) {
          if (checkSuffix(f, "vws")) {
            id = new Location(parent, f).getAbsolutePath();
            break;
          }
        }
      }
      else throw new FormatException("Could not find .vws file.");
    }

    super.initFile(id);

    exposureTimes = new Hashtable<Integer, Double>();

    POIService poi = null;
    try {
      ServiceFactory factory = new ServiceFactory();
      poi = factory.getInstance(POIService.class);
    }
    catch (DependencyException de) {
      throw new FormatException("POI library not found", de);
    }

    poi.initialize(id);
    Vector<String> documents = poi.getDocumentList();

    int nImages = 0;

    Hashtable tmpSeriesMetadata = new Hashtable();

    for (String name : documents) {
      LOGGER.debug("Reading {}", name);

      if (name.equals("Root Entry" + File.separator + "Contents")) {
        RandomAccessInputStream s = poi.getDocumentStream(name);
        s.order(true);
        int nFound = 0;

        embeddedImages = s.read() == 1;
        LOGGER.debug("Images are {}embedded", embeddedImages ? "" : "not ");

        if (embeddedImages) {
          s.skipBytes(12);
          int len = s.readShort();
          String type = s.readString(len);
          if (!type.equals("CImage")) {
            embeddedImages = false;
            s.close();
            continue;
          }

          s.seek(27);
          len = s.read();
          while (len != 0) {
            s.skipBytes(len);
            len = s.read();
          }

          s.skipBytes(1243);

          core[0].sizeX = s.readInt();
          core[0].sizeY = s.readInt();
          core[0].sizeZ = s.readInt();
          core[0].sizeC = s.readInt();
          core[0].sizeT = s.readInt();
          core[0].pixelType = convertPixelType(s.readInt());
          embeddedOffset = s.getFilePointer() + 28;
          if (in != null) in.close();
          in = poi.getDocumentStream(name);
          nImages++;
          s.close();
          break;
        }

        s.seek(0);

        while (s.getFilePointer() < s.length() - 2) {
          LOGGER.debug("  Looking for image at {}", s.getFilePointer());
          s.order(false);
          int nextOffset = findNextOffset(s);
          if (nextOffset < 0 || nextOffset >= s.length()) break;
          s.seek(nextOffset);
          s.skipBytes(3);
          int len = s.readShort();
          if (len <= 0) continue;
          imageNames.add(s.readString(len));
          if (s.getFilePointer() + 8 >= s.length()) break;
          s.skipBytes(6);
          s.order(true);
          len = s.readShort();
          if (len < 0 || len > 0x1000) continue;
          String description = s.readString(len);
          LOGGER.debug("Description: {}", description);

          // parse key/value pairs from description

          String dateTime = "";

          String[] lines = description.split("[\r\n]");
          for (String line : lines) {
            line = line.trim();
            int colon = line.indexOf(":");
            if (colon != -1 && !line.startsWith(";")) {
              String key = line.substring(0, colon).trim();
              String value = line.substring(colon + 1).trim();
              String metaKey = "Series " + nImages + " " + key;
              addMeta(metaKey, value, tmpSeriesMetadata);

              if (key.equals("Start time of experiment")) {
                // HH:mm:ss aa OR HH:mm:ss.sss aa
                dateTime += " " + value;
              }
              else if (key.equals("Date")) {
                // mm/dd/yy ?
                dateTime = value + " " + dateTime;
              }
              else if (key.equals("Exposure time [ms]")) {
                double exp = Double.parseDouble(value) / 1000;
                exposureTimes.put(new Integer(nImages), new Double(exp));
              }
              else if (key.equals("Image type")) {
                types.add(value);
              }
            }
          }

          dateTime = dateTime.trim();
          if (!dateTime.equals("")) {
            boolean success = false;
            for (String format : DATE_FORMATS) {
              try {
                dateTime = DateTools.formatDate(dateTime, format);
                success = true;
              }
              catch (NullPointerException e) { }
            }
            dates.add(success ? dateTime : "");
          }
          nImages++;
        }
        s.close();
      }
    }

    Location directory =
      new Location(currentId).getAbsoluteFile().getParentFile();

    String[] pixelsFile = new String[nImages];

    if (!embeddedImages) {
      if (nImages == 0) {
        throw new FormatException("No images found.");
      }
      core = new CoreMetadata[nImages];

      // look for appropriate pixels files

      String[] files = directory.list(true);
      String name = currentId.substring(
        currentId.lastIndexOf(File.separator) + 1, currentId.lastIndexOf("."));

      int nextFile = 0;

      for (String f : files) {
        if (checkSuffix(f, "pst")) {
          Location pst = new Location(directory, f);
          if (pst.isDirectory() && f.startsWith(name)) {
            String[] subfiles = pst.list(true);
            for (String q : subfiles) {
              if (checkSuffix(q, "pst") && nextFile < nImages) {
                pixelsFile[nextFile++] = f + File.separator + q;
              }
            }
          }
        }
      }
      if (nextFile == 0) {
        for (String f : files) {
          if (checkSuffix(f, "pst")) {
            pixelsFile[nextFile++] =
              new Location(directory, f).getAbsolutePath();
          }
        }

        if (nextFile == 0) throw new FormatException("No image files found.");
      }
    }

    Arrays.sort(pixelsFile);

    pixelsFiles = new String[getSeriesCount()];
    infFiles = new String[getSeriesCount()];

    Object[] metadataKeys = tmpSeriesMetadata.keySet().toArray();
    IniParser parser = new IniParser();

    for (int i=0; i<getSeriesCount(); i++) {
      if (!embeddedImages) {
        core[i] = new CoreMetadata();
        setSeries(i);

        // make sure that pixels file exists

        String file = pixelsFile[i];

        file = file.replace('/', File.separatorChar);
        file = file.replace('\\', File.separatorChar);
        String oldFile = file;

        Location f = new Location(directory, oldFile);

        if (!f.exists()) {
          oldFile = oldFile.substring(oldFile.lastIndexOf(File.separator) + 1);
          f = new Location(directory, oldFile);
          if (!f.exists()) {
            throw new FormatException("Could not find pixels file '" + file);
          }
        }

        file = f.getAbsolutePath();
        pixelsFiles[i] = file;

        // read key/value pairs from .inf files

        int dot = file.lastIndexOf(".");
        String inf = file.substring(0, dot) + ".inf";
        infFiles[i] = inf;

        BufferedReader reader = new BufferedReader(new FileReader(inf));
        IniList data = parser.parseINI(reader);
        reader.close();
        IniTable infoTable = data.getTable("Info");

        core[i].sizeX = Integer.parseInt(infoTable.get("Width"));
        core[i].sizeY = Integer.parseInt(infoTable.get("Height"));
        core[i].sizeC = Integer.parseInt(infoTable.get("Bands"));
        core[i].sizeZ = Integer.parseInt(infoTable.get("Slices"));
        core[i].sizeT = Integer.parseInt(infoTable.get("Frames"));
        int dataType = Integer.parseInt(infoTable.get("Datatype"));
        core[i].pixelType = convertPixelType(dataType);

        if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
          HashMap<String, String> iniMap = data.flattenIntoHashMap();
          core[i].seriesMetadata.putAll(iniMap);
        }
      }

      core[i].imageCount = core[i].sizeZ * core[i].sizeC * core[i].sizeT;
      core[i].rgb = false;
      core[i].littleEndian = true;
      core[i].dimensionOrder = "XYCZT";

      core[i].seriesMetadata = new Hashtable();
      for (Object key : metadataKeys) {
        String keyName = key.toString();
        if (keyName.startsWith("Series " + i + " ")) {
          keyName = keyName.replaceAll("Series " + i + " ", "");
          core[i].seriesMetadata.put(keyName, tmpSeriesMetadata.get(key));
        }
      }
    }
    tmpSeriesMetadata = null;
    populateMetadataStore();

    poi.close();
    poi = null;
  }

  // -- Helper methods --

  private void populateMetadataStore() throws FormatException {
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    for (int i=0; i<getSeriesCount(); i++) {
      // populate Image data
      if (i < imageNames.size()) {
        store.setImageName(imageNames.get(i), i);
      }
      String date = i < dates.size() ? dates.get(i) : "";
      if (date != null && !date.equals("")) {
        store.setImageAcquiredDate(date, i);
      }
      else MetadataTools.setDefaultCreationDate(store, currentId, i);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      for (int i=0; i<getSeriesCount(); i++) {
        // populate PlaneTiming data

        for (int q=0; q<core[i].imageCount; q++) {
          store.setPlaneExposureTime(exposureTimes.get(i), i, q);
        }

        // populate Experiment data

        if (i < types.size()) {
          store.setExperimentID(MetadataTools.createLSID("Experiment", i), i);
          store.setExperimentType(getExperimentType(types.get(i)), i);
        }
      }
    }
  }

  private int convertPixelType(int type) throws FormatException {
    boolean signed = type % 2 == 1;
    int bytes = (type / 2) + (signed ? 1 : 0);
    return FormatTools.pixelTypeFromBytes(bytes, signed, false);
  }

  private int findNextOffset(RandomAccessInputStream s) throws IOException {
    long fp = s.getFilePointer();
    int offset0 = findNextOffset(s, MARKER_0);
    s.seek(fp);
    int offset1 = findNextOffset(s, MARKER_1);
    s.seek(fp);
    int offset2 = findNextOffset(s, MARKER_2);

    if (offset0 < 0) offset0 = Integer.MAX_VALUE;
    if (offset1 < 0) offset1 = Integer.MAX_VALUE;
    if (offset2 < 0) offset2 = Integer.MAX_VALUE;

    if (offset0 < offset1 && offset0 < offset2) {
      return offset0;
    }
    if (offset1 < offset0 && offset1 < offset2) {
      return offset1;
    }
    if (offset2 < offset1 && offset2 < offset0) {
      return offset2;
    }
    return -1;
  }

  private int findNextOffset(RandomAccessInputStream s, byte[] marker)
    throws IOException
  {
    for (long i=s.getFilePointer(); i<s.length()-marker.length; i++) {
      s.seek(i);
      boolean found = true;
      for (int q=0; q<marker.length; q++) {
        if (marker[q] != s.readByte()) {
          found = false;
          break;
        }
      }
      if (found) return (int) (i + marker.length);
    }
    return -1;
  }

}
