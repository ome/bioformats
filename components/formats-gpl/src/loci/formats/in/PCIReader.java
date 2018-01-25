/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import loci.common.Constants;
import loci.common.DateTools;
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
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;
import ome.xml.model.enums.Binning;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * PCIReader is the file format reader for SimplePCI (Compix) .cxd files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class PCIReader extends FormatReader {

  // -- Constants --

  public static final int PCI_MAGIC_BYTES = 0xd0cf11e0;

  // -- Fields --

  private HashMap<Integer, String> imageFiles;
  private transient POIService poi;
  private HashMap<Integer, Double> timestamps;
  private String creationDate;
  private int binning;
  private List<Double> uniqueZ;

  // -- Constructor --

  /** Constructs a new SimplePCI reader. */
  public PCIReader() {
    super("Compix Simple-PCI", "cxd");
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readInt() == PCI_MAGIC_BYTES;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    String file = imageFiles.get(0);

    try {
      if (poi == null) {
       initPOIService();
      }

      RandomAccessInputStream s = poi.getDocumentStream(file);
      TiffParser tp = new TiffParser(s);
      if (tp.isValidHeader()) {
        IFD ifd = tp.getFirstIFD();
        s.close();
        return (int) ifd.getTileWidth();
      }
      s.close();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile width", e);
    }
    catch (IOException e) {
      LOGGER.debug("Could not retrieve tile width", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    String file = imageFiles.get(0);

    try {
      if (poi == null) {
        initPOIService();
      }

      RandomAccessInputStream s = poi.getDocumentStream(file);
      TiffParser tp = new TiffParser(s);
      if (tp.isValidHeader()) {
        IFD ifd = tp.getFirstIFD();
        s.close();
        return (int) ifd.getTileLength();
      }
      s.close();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile height", e);
    }
    catch (IOException e) {
      LOGGER.debug("Could not retrieve tile height", e);
    }
    return super.getOptimalTileHeight();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (poi == null) {
      initPOIService();
    }

    String file = imageFiles.get(no);
    RandomAccessInputStream s = poi.getDocumentStream(file);
    TiffParser tp = new TiffParser(s);

    // can be raw pixel data or an embedded TIFF file

    if (tp.isValidHeader()) {
      IFD ifd = tp.getFirstIFD();
      tp.getSamples(ifd, buf, x, y, w, h);
    }
    else {
      s.seek(0);
      readPlane(s, x, y, w, h, buf);
    }
    s.close();

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      imageFiles = null;
      timestamps = null;
      if (poi != null) poi.close();
      poi = null;
      binning = 0;
      creationDate = null;
      uniqueZ = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    imageFiles = new HashMap<Integer, String>();
    timestamps = new HashMap<Integer, Double>();
    uniqueZ = new ArrayList<Double>();

    CoreMetadata m = core.get(0);

    initPOIService();

    double scaleFactor = 1;

    final List<String> allFiles = poi.getDocumentList();
    if (allFiles.isEmpty()) {
      throw new FormatException(
        "No files were found - the .cxd may be corrupt.");
    }

    double firstZ = 0d, secondZ = 0d;
    int mode = 0;

    for (String name : allFiles) {
      int separator = name.lastIndexOf(File.separator);
      String parent = name.substring(0, separator);
      String relativePath = name.substring(separator + 1);
      RandomAccessInputStream stream = null;

      if (!(relativePath.startsWith("Bitmap") ||
        (relativePath.equals("Data") && parent.indexOf("Image") != -1)))
      {
        stream = poi.getDocumentStream(name);
        stream.order(true);
      }

      if (stream != null && stream.length() == 8) {
        double value = stream.readDouble();
        stream.seek(0);

        String key = name.replace(File.separatorChar, ' ');
        key = key.replaceAll("Root Entry ", "");
        key = key.replaceAll("Field Data ", "");
        key = key.replaceAll("Details ", "");

        addGlobalMeta(key, value);
      }

      if (relativePath.equals("Field Count")) {
        m.imageCount = stream.readInt();
      }
      else if (relativePath.equals("File Has Image")) {
        if (stream.readShort() == 0) {
          throw new FormatException("This file does not contain image data.");
        }
      }
      else if (relativePath.startsWith("Bitmap") ||
        (relativePath.equals("Data") && parent.indexOf("Image") != -1))
      {
        imageFiles.put(imageFiles.size(), name);

        if (getSizeX() != 0 && getSizeY() != 0) {
          int bpp = FormatTools.getBytesPerPixel(getPixelType());
          int plane = getSizeX() * getSizeY() * bpp;
          if (getSizeC() == 0 || getSizeC() * plane > poi.getFileSize(name)) {
            m.sizeC = poi.getFileSize(name) / plane;
          }
        }
      }
      else if (relativePath.indexOf("Image_Depth") != -1) {
        boolean firstBits = m.bitsPerPixel == 0;
        int bits = (int) stream.readDouble();
        m.bitsPerPixel = bits;
        while (bits % 8 != 0 || bits == 0) bits++;
        if (bits % 3 == 0) {
          m.sizeC = 3;
          bits /= 3;
          m.bitsPerPixel /= 3;
        }
        bits /= 8;
        m.pixelType = FormatTools.pixelTypeFromBytes(bits, false, false);
        if (getSizeC() > 1 && firstBits) {
          m.sizeC /= bits;
        }
      }
      else if (relativePath.indexOf("Image_Height") != -1 && getSizeY() == 0) {
        m.sizeY = (int) stream.readDouble();
      }
      else if (relativePath.indexOf("Image_Width") != -1 && getSizeX() == 0) {
        m.sizeX = (int) stream.readDouble();
      }
      else if (relativePath.indexOf("Time_From_Start") != -1) {
        timestamps.put(getTimestampIndex(parent), stream.readDouble());
      }
      else if (relativePath.indexOf("Position_Z") != -1) {
        double zPos = stream.readDouble();
        if (!uniqueZ.contains(zPos) && getSizeZ() <= 1)
        {
          uniqueZ.add(zPos);
        }
        if (name.indexOf("Field 1" + File.separator) != -1) firstZ = zPos;
        else if (name.indexOf("Field 2" + File.separator) != -1) secondZ = zPos;
      }
      else if (relativePath.equals("First Field Date & Time")) {
        long date = (long) stream.readDouble() * 1000;
        creationDate = DateTools.convertDate(date, DateTools.COBOL);
      }
      else if (relativePath.equals("GroupMode")) {
        mode = stream.readInt();
      }
      else if (relativePath.equals("GroupSelectedFields")) {
        m.sizeZ = (int) (stream.length() / 8);
      }
      else if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM)
      {
        if (relativePath.equals("Binning")) {
          binning = (int) stream.readDouble();
        }
        else if (relativePath.equals("Comments")) {
          String comments = stream.readString((int) stream.length());
          String[] lines = comments.split("\n");
          for (String line : lines) {
            int eq = line.indexOf('=');
            if (eq != -1) {
              String key = line.substring(0, eq).trim();
              String value = line.substring(eq + 1).trim();
              addGlobalMeta(key, value);

              if (key.equals("factor")) {
                if (value.indexOf(';') != -1) {
                  value = value.substring(0, value.indexOf(';'));
                }
                scaleFactor = Double.parseDouble(value.trim());
              }
            }
          }
        }
      }
      if (stream != null) {
        stream.close();
      }
    }

    boolean zFirst = Math.abs(firstZ - secondZ) > Constants.EPSILON;

    if (getSizeC() == 0) m.sizeC = 1;
    if (mode == 0) {
      m.sizeZ = 0;
    }

    if (getSizeZ() <= 1 || (getImageCount() % getSizeZ()) != 0) {
      m.sizeZ = uniqueZ.isEmpty() ? 1 : uniqueZ.size();
    }
    m.sizeT = getImageCount() / getSizeZ();

    while (getSizeZ() * getSizeT() < getImageCount()) {
      m.sizeZ++;
      m.sizeT = getImageCount() / getSizeZ();
    }

    m.rgb = getSizeC() > 1;
    if (imageFiles.size() > getImageCount() && getSizeC() == 1) {
      m.sizeC = imageFiles.size() / getImageCount();
      m.imageCount *= getSizeC();
    }
    else {
      m.imageCount = getSizeZ() * getSizeT();
    }
    m.interleaved = false;
    m.dimensionOrder = zFirst ? "XYCZT" : "XYCTZ";
    m.littleEndian = true;
    m.indexed = false;
    m.falseColor = false;
    m.metadataComplete = true;

    // re-index image files
    String[] files = imageFiles.values().toArray(new String[imageFiles.size()]);
    for (String file : files) {
      int separator = file.lastIndexOf(File.separator);
      String parent = file.substring(0, separator);
      imageFiles.put(getImageIndex(parent), file);
    }

    int bpp = FormatTools.getBytesPerPixel(m.pixelType);
    int expectedPlaneSize = m.sizeX * m.sizeY * bpp * m.sizeC;
    String file = imageFiles.get(0);
    RandomAccessInputStream s = poi.getDocumentStream(file);
    TiffParser tp = new TiffParser(s);
    // don't correct the image width if it's stored as a TIFF or if binning
    if (!tp.isValidHeader() && binning == 0 && s.length() > expectedPlaneSize) {
      m.sizeX += (s.length() - expectedPlaneSize) / (m.sizeY * bpp * m.sizeC);
    }
    s.close();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    if (creationDate != null) {
      store.setImageAcquisitionDate(new Timestamp(creationDate), 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      Length sizeX = FormatTools.getPhysicalSizeX(scaleFactor);
      Length sizeY = FormatTools.getPhysicalSizeY(scaleFactor);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }

      for (int i=0; i<timestamps.size(); i++) {
        if (i >= getImageCount()) {
          break;
        }
        Double timestamp = timestamps.get(i);
        if (timestamp != null) {
          store.setPlaneDeltaT(new Time(timestamp, UNITS.SECOND), 0, i);
        }
        if (i == 2) {
          Double first = timestamps.get(1);
          Double increment = timestamp - first;
          if (increment != null) {
            store.setPixelsTimeIncrement(new Time(increment, UNITS.SECOND), 0);
          }
        }
      }

      if (binning > 0) {
        String instrumentID = MetadataTools.createLSID("Instrument", 0);
        String detectorID = MetadataTools.createLSID("Detector", 0);
        store.setInstrumentID(instrumentID, 0);
        store.setDetectorID(detectorID, 0, 0);
        store.setDetectorType(getDetectorType("Other"), 0, 0);
        store.setImageInstrumentRef(instrumentID, 0);

        Binning binningEnum = getBinning(binning + "x" + binning);
        for (int c=0; c<getEffectiveSizeC(); c++) {
          store.setDetectorSettingsID(detectorID, 0, c);
          store.setDetectorSettingsBinning(binningEnum, 0, c);
        }
      }
    }
  }

  // -- Helper methods --

  private void initPOIService() throws FormatException, IOException {
   try {
      ServiceFactory factory = new ServiceFactory();
      poi = factory.getInstance(POIService.class);
    }
    catch (DependencyException de) {
      throw new FormatException("POI library not found", de);
    }

    poi.initialize(Location.getMappedId(getCurrentFile()));
  }

  /** Get the image index from the image file name. */
  private Integer getImageIndex(String path) {
    int space = path.lastIndexOf(" ") + 1;
    if (space >= path.length()) return null;
    int end = path.indexOf(File.separator, space);
    String field = path.substring(space, end);
    String image = "1";
    int imageIndex = path.indexOf("Image") + 5;
    if (imageIndex >= 0) {
      end = path.indexOf(File.separator, imageIndex);
      if (end < 0) end = path.length();
      image = path.substring(imageIndex, end);
    }
    try {
      int channel = Integer.parseInt(image) - 1;
      return getEffectiveSizeC() * (Integer.parseInt(field) - 1) + channel;
    }
    catch (NumberFormatException e) { }
    return null;
  }

  private Integer getTimestampIndex(String path) {
    int space = path.lastIndexOf(" ") + 1;
    if (space >= path.length()) return null;
    int end = path.indexOf(File.separator, space);
    return Integer.parseInt(path.substring(space , end)) - 1;
  }

}
