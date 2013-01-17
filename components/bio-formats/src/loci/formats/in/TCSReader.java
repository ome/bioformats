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
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.XMLTools;
import loci.formats.AxisGuesser;
import loci.formats.CoreMetadata;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.TiffParser;
import ome.xml.model.primitives.PositiveFloat;

/**
 * TCSReader is the file format reader for Leica TCS TIFF files and their
 * companion XML file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/TCSReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/TCSReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class TCSReader extends FormatReader {

  // -- Constants --

  public static final String DATE_FORMAT = "yyyy:MM:dd HH:mm:ss.SSS";
  public static final String PREFIX =
    "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><LEICA>";
  public static final String SUFFIX = "</LEICA>";

  public static final String[] XML_SUFFIX = {"xml"};

  // -- Fields --

  /** List of TIFF files. */
  private Vector<String> tiffs;

  /** Helper readers. */
  private TiffReader[] tiffReaders;

  private TiffParser tiffParser;

  private int lastPlane = 0;
  private long datestamp;
  private String xmlFile;

  private double voxelX, voxelY, voxelZ;

  // -- Constructor --

  public TCSReader() {
    super("Leica TCS TIFF", new String[] {"tif", "tiff", "xml"});
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    if (checkSuffix(id, "xml")) return false;
    Location file = new Location(id);
    String[] list = file.getParentFile().list();
    for (String f : list) {
      if (checkSuffix(f, "xml") && DataTools.samePrefix(file.getName(), f)) {
        return false;
      }
    }
    return true;
  }

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
      RandomAccessInputStream s = new RandomAccessInputStream(name);
      boolean isThisType = isThisType(s);
      s.close();
      return isThisType;
    }
    catch (IOException e) {
      LOGGER.debug("", e);
      return false;
    }
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    // check for Leica TCS IFD directory entries
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) {
      stream.seek(0);
      return stream.readString(6).equals("<Data>");
    }
    String document = ifd.getIFDTextValue(IFD.DOCUMENT_NAME);
    if (document == null) document = "";
    String software = ifd.getIFDTextValue(IFD.SOFTWARE);
    if (software == null) software = "";
    software = software.trim();
    return document.startsWith("CHANNEL") || software.startsWith("TCS");
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReaders[lastPlane].get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReaders[lastPlane].get16BitLookupTable();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int n = no;
    for (int i=0; i<getSeries(); i++) {
      n += core.get(i).imageCount;
    }

    if (tiffReaders.length == 1) {
      return tiffReaders[0].openBytes(n, buf, x, y, w, h);
    }
    int plane = 0;
    if (tiffReaders[0].getImageCount() > 1) {
      n /= tiffReaders.length;
      plane = n % tiffReaders.length;
    }
    if (lastPlane != 0) {
      tiffReaders[lastPlane].close();
    }
    lastPlane = n;
    tiffReaders[n].setId(tiffs.get(n));
    return tiffReaders[n].openBytes(plane, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      return xmlFile == null ? null : new String[] {xmlFile};
    }
    Vector<String> v = new Vector<String>();
    v.addAll(tiffs);
    if (xmlFile != null) v.add(xmlFile);
    return v.toArray(new String[v.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      tiffs = null;
      if (tiffReaders != null) {
        for (TiffReader r : tiffReaders) {
          if (r != null) r.close();
        }
      }
      tiffReaders = null;
      tiffParser = null;
      datestamp = 0;
      xmlFile = null;
      lastPlane = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    Location l = new Location(id).getAbsoluteFile();
    Location parent = l.getParentFile();
    String[] list = parent.list();
    Arrays.sort(list);

    boolean isXML = checkSuffix(id, XML_SUFFIX);

    if (list != null) {
      for (String file : list) {
        if (checkSuffix(file, XML_SUFFIX) && !isXML && isGroupFiles()) {
          xmlFile = new Location(parent, file).getAbsolutePath();
          break;
        }
        else if (checkSuffix(file, TiffReader.TIFF_SUFFIXES) && isXML) {
          initFile(new Location(parent, file).getAbsolutePath());
          return;
        }
      }
    }

    if (isXML) xmlFile = l.getAbsolutePath();

    super.initFile(id);

    MetadataStore store = makeFilterMetadata();

    in = new RandomAccessInputStream(id);
    tiffParser = new TiffParser(in);
    tiffs = new Vector<String>();

    IFDList ifds = tiffParser.getIFDs();
    String date = ifds.get(0).getIFDStringValue(IFD.DATE_TIME);
    if (date != null) {
      datestamp = DateTools.getTime(date, "yyyy:MM:dd HH:mm:ss");
    }

    groupFiles();
    addGlobalMeta("Number of image files", tiffs.size());
    tiffReaders = new TiffReader[tiffs.size()];

    for (int i=0; i<tiffReaders.length; i++) {
      tiffReaders[i] = new TiffReader();
    }
    tiffReaders[0].setId(tiffs.get(0));

    int[] ch = new int[ifds.size()];
    int[] idx = new int[ifds.size()];
    long[] stamp = new long[ifds.size()];

    int channelCount = 0;

    CoreMetadata ms0 = core.get(0);

    ms0.sizeZ = 1;
    ms0.sizeC = tiffReaders[0].getSizeC();
    ms0.dimensionOrder = isRGB() ? "XYC" : "XY";

    if (isGroupFiles()) {
      try {
        FilePattern fp =
          new FilePattern(new Location(currentId).getAbsoluteFile());
        AxisGuesser guesser =
          new AxisGuesser(fp, "XYTZC", 1, ifds.size(), 1, true);

        int[] axisTypes = guesser.getAxisTypes();
        int[] count = fp.getCount();

        for (int i=axisTypes.length-1; i>=0; i--) {
          if (axisTypes[i] == AxisGuesser.Z_AXIS) {
            if (getDimensionOrder().indexOf("Z") == -1) {
              ms0.dimensionOrder += "Z";
            }
            ms0.sizeZ *= count[i];
          }
          else if (axisTypes[i] == AxisGuesser.C_AXIS) {
            if (getDimensionOrder().indexOf("C") == -1) {
              ms0.dimensionOrder += "C";
            }
            ms0.sizeC *= count[i];
          }
        }
      }
      catch (NullPointerException e) { }
    }

    for (int i=0; i<ifds.size(); i++) {
      String document = ifds.get(i).getIFDStringValue(IFD.DOCUMENT_NAME);
      if (document == null) continue;

      int index = document.indexOf("INDEX");
      String s = document.substring(8, index).trim();
      ch[i] = Integer.parseInt(s);
      if (ch[i] > channelCount) channelCount = ch[i];

      int space = document.indexOf(" ", index + 6);
      if (space < 0) continue;
      String n = document.substring(index + 6, space).trim();
      idx[i] = Integer.parseInt(n);

      date = document.substring(space, document.indexOf("FORMAT")).trim();
      stamp[i] = DateTools.getTime(date, DATE_FORMAT);
      addGlobalMetaList("Timestamp for plane", stamp[i]);
    }

    ms0.sizeT = 0;

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
        ms0.sizeT++;
        if (getDimensionOrder().indexOf("T") < 0) {
          ms0.dimensionOrder += "T";
        }
      }
      else if (i > 0) {
        if ((ch[i] != ch[i - 1]) && getDimensionOrder().indexOf("C") < 0) {
          ms0.dimensionOrder += "C";
        }
        else if (getDimensionOrder().indexOf("Z") < 0) {
          ms0.dimensionOrder += "Z";
        }
      }
      unique = true;
    }

    if (getDimensionOrder().indexOf("Z") < 0) ms0.dimensionOrder += "Z";
    if (getDimensionOrder().indexOf("C") < 0) ms0.dimensionOrder += "C";
    if (getDimensionOrder().indexOf("T") < 0) ms0.dimensionOrder += "T";

    if (getSizeC() == 0) ms0.sizeC = 1;
    if (getSizeT() == 0) ms0.sizeT = 1;
    if (channelCount == 0) channelCount = 1;
    if (getSizeZ() <= 1) {
      ms0.sizeZ = ifds.size() / (getSizeT() * channelCount);
    }
    ms0.sizeC *= channelCount;
    ms0.imageCount = getSizeZ() * getSizeT() * getSizeC();

    // cut up comment

    String comment = ifds.get(0).getComment();
    if (comment != null && comment.startsWith("[") &&
      getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM)
    {
      String[] lines = comment.split("\n");
      for (String line : lines) {
        if (!line.startsWith("[")) {
          int eq = line.indexOf("=");
          if (eq < 0) continue;
          String key = line.substring(0, eq).trim();
          String value = line.substring(eq + 1).trim();

          if (key.equals("VoxelSizeX")) {
            try {
              voxelX = Double.parseDouble(value);
            }
            catch (NumberFormatException e) { }
          }
          else if (key.equals("VoxelSizeY")) {
            try {
              voxelY = Double.parseDouble(value);
            }
            catch (NumberFormatException e) { }
          }
          else if (key.equals("VoxelSizeZ")) {
            try {
              voxelZ = Double.parseDouble(value);
            }
            catch (NumberFormatException e) { }
          }

          addGlobalMeta(key, value);
        }
      }
      metadata.remove("Comment");
    }
    ms0.sizeX = tiffReaders[0].getSizeX();
    ms0.sizeY = tiffReaders[0].getSizeY();
    ms0.rgb = tiffReaders[0].isRGB();
    ms0.pixelType = tiffReaders[0].getPixelType();
    ms0.littleEndian = tiffReaders[0].isLittleEndian();
    ms0.interleaved = tiffReaders[0].isInterleaved();
    ms0.falseColor = true;
    ms0.indexed = tiffReaders[0].isIndexed();

    if (isRGB()) ms0.imageCount /= (getSizeC() / channelCount);

    if (getSizeZ() * getSizeT() * getEffectiveSizeC() !=
      (ifds.size() * tiffReaders.length))
    {
      int c = getEffectiveSizeC();
      if (c == 0) c = 1;
      ms0.sizeT = (ifds.size() * tiffReaders.length) / (c * getSizeZ());
      ms0.imageCount = getSizeT() * c * getSizeZ();
      if (getSizeT() == 0) {
        ms0.sizeT = 1;
        ms0.imageCount = ifds.size() * tiffReaders.length;
      }
    }

    if (getImageCount() == ifds.size() * getSizeZ() * getSizeT() &&
      ifds.size() > 1)
    {
      if (getSizeZ() == 1) {
        ms0.sizeZ = ifds.size();
      }
      else if (getSizeT() == 1) {
        ms0.sizeT = ifds.size();
      }
      else ms0.sizeZ *= ifds.size();
    }

    if (xmlFile != null) {
      // parse XML metadata

      String xml = DataTools.readFile(xmlFile);
      xml = XMLTools.sanitizeXML(PREFIX + xml + SUFFIX);

      LeicaHandler handler =
        new LeicaHandler(store, getMetadataOptions().getMetadataLevel());
      XMLTools.parseXML(xml, handler);

      metadata = handler.getGlobalMetadata();
      MetadataTools.merge(handler.getGlobalMetadata(), metadata, "");

      core = handler.getCoreMetadataList();

      for (int i=0; i<getSeriesCount(); i++) {
        CoreMetadata ms = core.get(i);
        if (tiffs.size() < ms.imageCount) {
          int div = ms.imageCount / ms.sizeC;
          ms.imageCount = tiffs.size();
          if (div >= ms.sizeZ) ms.sizeZ /= div;
          else if (div >= ms.sizeT) ms.sizeT /= div;
        }
        ms.dimensionOrder = getSizeZ() > getSizeT() ? "XYCZT" : "XYCTZ";
        ms.rgb = false;
        ms.interleaved = false;
        ms.indexed = tiffReaders[0].isIndexed();
      }
    }

    MetadataTools.populatePixels(store, this, true);

    if (voxelX > 0) {
      store.setPixelsPhysicalSizeX(new PositiveFloat(voxelX), 0);
    }
    else {
      LOGGER.warn("Expected positive value for PhysicalSizeX; got {}", voxelX);
    }
    if (voxelY > 0) {
      store.setPixelsPhysicalSizeY(new PositiveFloat(voxelY), 0);
    }
    else {
      LOGGER.warn("Expected positive value for PhysicalSizeY; got {}", voxelY);
    }
    if (voxelZ > 0) {
      store.setPixelsPhysicalSizeZ(new PositiveFloat(voxelZ), 0);
    }
    else {
      LOGGER.warn("Expected positive value for PhysicalSizeZ; got {}", voxelZ);
    }
  }

  // -- Helper methods --

  private void groupFiles() throws FormatException, IOException {
    // look for associated TIFF files

    // we assume that two files are grouped if all of the following are true:
    //
    //  * the files are in the same directory
    //  * the file names are the same length
    //  * the time stamps are less than 60 seconds apart
    //  * the files have the same number of bytes

    Location current = new Location(currentId).getAbsoluteFile();
    if (!checkSuffix(currentId, XML_SUFFIX)) {
      tiffs.add(current.getAbsolutePath());
    }
    if (!isGroupFiles()) return;

    Location parent = current.getParentFile();

    String[] list = parent.list();
    Arrays.sort(list);

    HashMap<String, Long> timestamps = new HashMap<String, Long>();

    RandomAccessInputStream s =
      new RandomAccessInputStream(current.getAbsolutePath());
    TiffParser p = new TiffParser(s);
    IFD ifd = p.getIFDs().get(0);
    s.close();

    int expectedIFDCount = p.getIFDs().size();
    long width = ifd.getImageWidth();
    long height = ifd.getImageLength();
    int samples = ifd.getSamplesPerPixel();

    for (String file : list) {
      file = new Location(parent, file).getAbsolutePath();
      if (file.length() != current.getAbsolutePath().length()) continue;

      RandomAccessInputStream rais = new RandomAccessInputStream(file);
      TiffParser tp = new TiffParser(rais);
      if (!tp.isValidHeader()) {
        continue;
      }
      ifd = tp.getIFDs().get(0);

      if (tp.getIFDs().size() != expectedIFDCount ||
        ifd.getImageWidth() != width || ifd.getImageLength() != height ||
        ifd.getSamplesPerPixel() != samples)
      {
        continue;
      }

      String date = ifd.getIFDStringValue(IFD.DATE_TIME);
      if (date != null) {
        long stamp = DateTools.getTime(date, "yyyy:MM:dd HH:mm:ss");

        String software = ifd.getIFDStringValue(IFD.SOFTWARE);
        if (software != null && software.trim().startsWith("TCS")) {
          timestamps.put(file, new Long(stamp));
        }
      }
      rais.close();
    }

    String[] files = timestamps.keySet().toArray(new String[timestamps.size()]);
    Arrays.sort(files);

    for (String file : files) {
      long thisStamp = timestamps.get(file).longValue();
      boolean match = false;
      for (String tiff : tiffs) {
        s = new RandomAccessInputStream(tiff);
        TiffParser parser = new TiffParser(s);
        ifd = parser.getIFDs().get(0);
        s.close();

        String date = ifd.getIFDStringValue(IFD.DATE_TIME);
        long nextStamp = DateTools.getTime(date, "yyyy:MM:dd HH:mm:ss");
        if (Math.abs(thisStamp - nextStamp) < 600000) {
          match = true;
          break;
        }
      }
      if (match && !tiffs.contains(file)) tiffs.add(file);
    }
  }

}
