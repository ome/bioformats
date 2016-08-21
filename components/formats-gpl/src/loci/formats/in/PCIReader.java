/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
import java.util.HashMap;
import java.util.Vector;

import loci.common.DataTools;
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
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;

/**
 * PCIReader is the file format reader for SimplePCI (Compix) .cxd files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/PCIReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/PCIReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class PCIReader extends FormatReader {

  // -- Constants --

  public static final int PCI_MAGIC_BYTES = 0xd0cf11e0;

  // -- Fields --

  private HashMap<Integer, String> imageFiles;
  private POIService poi;
  private HashMap<Integer, Double> timestamps;
  private String creationDate;
  private int binning;
  private Vector<Double> uniqueZ;

  // -- Constructor --

  /** Constructs a new SimplePCI reader. */
  public PCIReader() {
    super("Compix Simple-PCI", "cxd");
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readInt() == PCI_MAGIC_BYTES;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    String file = imageFiles.get(0);
    try {
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
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    String file = imageFiles.get(0);
    try {
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
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

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
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    imageFiles = new HashMap<Integer, String>();
    timestamps = new HashMap<Integer, Double>();
    uniqueZ = new Vector<Double>();

    CoreMetadata m = core.get(0);

    try {
      ServiceFactory factory = new ServiceFactory();
      poi = factory.getInstance(POIService.class);
    }
    catch (DependencyException de) {
      throw new FormatException("POI library not found", de);
    }

    poi.initialize(Location.getMappedId(currentId));

    double scaleFactor = 1;

    Vector<String> allFiles = poi.getDocumentList();
    if (allFiles.size() == 0) {
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
        if (!uniqueZ.contains(zPos) && (getImageCount() == 0 || getSizeZ() == 1))
        {
          uniqueZ.add(zPos);
        }
        if (name.indexOf("Field 1/") != -1) firstZ = zPos;
        else if (name.indexOf("Field 2/") != -1) secondZ = zPos;
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
            int eq = line.indexOf("=");
            if (eq != -1) {
              String key = line.substring(0, eq).trim();
              String value = line.substring(eq + 1).trim();
              addGlobalMeta(key, value);

              if (key.equals("factor")) {
                if (value.indexOf(";") != -1) {
                  value = value.substring(0, value.indexOf(";"));
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

    boolean zFirst = !new Double(firstZ).equals(new Double(secondZ));

    if (getSizeC() == 0) m.sizeC = 1;
    if (mode == 0) {
      m.sizeZ = 0;
    }

    if (getSizeZ() <= 1 || (getImageCount() % getSizeZ()) != 0) {
      m.sizeZ = uniqueZ.size() == 0 ? 1 : uniqueZ.size();
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

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    if (creationDate != null) {
      store.setImageAcquisitionDate(new Timestamp(creationDate), 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      PositiveFloat sizeX = FormatTools.getPhysicalSizeX(scaleFactor);
      PositiveFloat sizeY = FormatTools.getPhysicalSizeY(scaleFactor);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }

      for (int i=0; i<timestamps.size(); i++) {
        Double timestamp = new Double(timestamps.get(i).doubleValue());
        if (i >= getImageCount()) {
          break;
        }
        store.setPlaneDeltaT(timestamp, 0, i);
        if (i == 2) {
          double first = timestamps.get(1).doubleValue();
          Double increment = new Double(timestamp.doubleValue() - first);
          store.setPixelsTimeIncrement(increment, 0);
        }
      }

      if (binning > 0) {
        String instrumentID = MetadataTools.createLSID("Instrument", 0);
        String detectorID = MetadataTools.createLSID("Detector", 0);
        store.setInstrumentID(instrumentID, 0);
        store.setDetectorID(detectorID, 0, 0);
        store.setDetectorType(getDetectorType("Other"), 0, 0);
        store.setImageInstrumentRef(instrumentID, 0);

        for (int c=0; c<getEffectiveSizeC(); c++) {
          store.setDetectorSettingsID(detectorID, 0, c);
          store.setDetectorSettingsBinning(
            getBinning(binning + "x" + binning), 0, c);
        }
      }
    }
  }

  // -- Helper methods --

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
