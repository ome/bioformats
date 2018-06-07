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

import java.io.IOException;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * AnalyzeReader is the file format reader for Analyze 7.5 files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class AnalyzeReader extends FormatReader {

  // -- Constants --

  private static final int MAGIC = 0x15c;

  // -- Fields --

  /** Offset to the pixel data in the .img file. */
  private int pixelOffset;

  /** File containing the pixel data. */
  private transient RandomAccessInputStream pixelFile;

  private String pixelsFilename;

  // -- Constructor --

  /** Constructs a new Analyze reader. */
  public AnalyzeReader() {
    super("Analyze 7.5", new String[] {"img", "hdr"});
    domains = new String[] {FormatTools.MEDICAL_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .img file and one similarly-named .hdr file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (!super.isThisType(name, open)) return false;
    if (!open) return false;

    String headerFile = checkSuffix(name, "hdr") ? name : null;
    String extension = name.substring(name.lastIndexOf(".") + 1);
    name = name.substring(0, name.lastIndexOf("."));
    if (extension.equals("img")) extension = "hdr";
    else if (extension.equals("IMG")) extension = "HDR";
    else if (extension.equals("hdr")) extension = "img";
    else if (extension.equals("HDR")) extension = "IMG";
    if (extension.equalsIgnoreCase("hdr")) {
      headerFile = name + "." + extension;
    }

    boolean validHeader = false;
    try {
      RandomAccessInputStream headerStream =
        new RandomAccessInputStream(headerFile);
      validHeader = isThisType(headerStream);
      headerStream.close();
    }
    catch (IOException e) { }

    return new Location(name + "." + extension).exists() && validHeader;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    stream.order(true);
    int checkLittleEndian = stream.readInt();
    stream.seek(stream.getFilePointer() - 4);
    stream.order(false);
    int checkBigEndian = stream.readInt();
    return checkLittleEndian == MAGIC || checkBigEndian == MAGIC;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int planeSize = FormatTools.getPlaneSize(this);
    pixelFile.seek(pixelOffset + no * planeSize);
    readPlane(pixelFile, x, y, w, h, buf);

    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    return noPixels ? new String[] {currentId} :
      new String[] {currentId, pixelsFilename};
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (pixelFile != null) pixelFile.close();
    if (!fileOnly) {
      pixelOffset = 0;
      pixelFile = null;
      pixelsFilename = null;
    }
  }

  /* @see loci.formats.IFormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    super.reopenFile();
    if (pixelFile == null) {
      pixelFile = new RandomAccessInputStream(pixelsFilename);
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    // the dataset has two files - we want the one ending in '.hdr'
    if (id.endsWith(".img")) {
      LOGGER.info("Looking for header file");
      String header = id.substring(0, id.lastIndexOf(".")) + ".hdr";
      if (new Location(header).exists()) {
        setId(header);
        return;
      }
      else throw new FormatException("Header file not found.");
    }

    super.initFile(id);
    in = new RandomAccessInputStream(id);
    pixelsFilename = id.substring(0, id.lastIndexOf(".")) + ".img";
    pixelFile = new RandomAccessInputStream(pixelsFilename);

    LOGGER.info("Reading header");

    int fileSize = in.readInt();
    boolean little = fileSize != in.length();
    in.order(little);
    pixelFile.order(little);

    in.skipBytes(10);

    String imageName = in.readString(18);
    in.skipBytes(8);

    int ndims = in.readShort();

    int x = in.readShort();
    int y = in.readShort();
    int z = in.readShort();
    int t = in.readShort();

    in.skipBytes(20);

    int dataType = in.readShort();
    int nBitsPerPixel = in.readShort();

    String description = null;
    double voxelWidth = 0d, voxelHeight = 0d, sliceThickness = 0d, deltaT = 0d;

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      in.skipBytes(6);

      voxelWidth = in.readFloat();
      voxelHeight = in.readFloat();
      sliceThickness = in.readFloat();
      deltaT = in.readFloat();

      in.skipBytes(12);

      pixelOffset = (int) in.readFloat();
      in.skipBytes(12);

      float calibratedMax = in.readFloat();
      float calibratedMin = in.readFloat();
      float compressed = in.readFloat();
      float verified = in.readFloat();
      float pixelMax = in.readFloat();
      float pixelMin = in.readFloat();

      description = in.readString(80);
      String auxFile = in.readString(24);
      char orient = (char) in.readByte();
      String originator = in.readString(10);
      String generated = in.readString(10);
      String scannum = in.readString(10);
      String patientID = in.readString(10);
      String expDate = in.readString(10);
      String expTime = in.readString(10);

      in.skipBytes(3);

      int views = in.readInt();
      int volsAdded = in.readInt();
      int startField = in.readInt();
      int fieldSkip = in.readInt();
      int omax = in.readInt();
      int omin = in.readInt();
      int smax = in.readInt();
      int smin = in.readInt();

      addGlobalMeta("Database name", imageName);
      addGlobalMeta("Number of dimensions", ndims);
      addGlobalMeta("Data type", dataType);
      addGlobalMeta("Number of bits per pixel", nBitsPerPixel);
      addGlobalMeta("Voxel width", voxelWidth);
      addGlobalMeta("Voxel height", voxelHeight);
      addGlobalMeta("Slice thickness", sliceThickness);
      addGlobalMeta("Exposure time", deltaT);
      addGlobalMeta("Pixel offset", pixelOffset);
      addGlobalMeta("Calibrated maximum", calibratedMax);
      addGlobalMeta("Calibrated minimum", calibratedMin);
      addGlobalMeta("Compressed", compressed);
      addGlobalMeta("Verified", verified);
      addGlobalMeta("Pixel maximum", pixelMax);
      addGlobalMeta("Pixel minimum", pixelMin);
      addGlobalMeta("Description", description);
      addGlobalMeta("Auxiliary file", auxFile);
      addGlobalMeta("Orientation", orient);
      addGlobalMeta("Originator", originator);
      addGlobalMeta("Generated", generated);
      addGlobalMeta("Scan Number", scannum);
      addGlobalMeta("Patient ID", patientID);
      addGlobalMeta("Acquisition Date", expDate);
      addGlobalMeta("Acquisition Time", expTime);
    }
    else {
      in.skipBytes(34);
      pixelOffset = (int) in.readFloat();
    }

    LOGGER.info("Populating core metadata");

    CoreMetadata m = core.get(0);

    m.littleEndian = little;
    m.sizeX = x;
    m.sizeY = y;
    m.sizeZ = z;
    m.sizeT = t;
    m.sizeC = 1;
    if (getSizeZ() == 0) m.sizeZ = 1;
    if (getSizeT() == 0) m.sizeT = 1;

    m.imageCount = getSizeZ() * getSizeT();
    m.rgb = false;
    m.interleaved = false;
    m.indexed = false;
    m.dimensionOrder = "XYZTC";

    switch (dataType) {
      case 1:
      case 2:
        m.pixelType = FormatTools.UINT8;
        break;
      case 4:
        m.pixelType = FormatTools.INT16;
        break;
      case 8:
        m.pixelType = FormatTools.INT32;
        break;
      case 16:
        m.pixelType = FormatTools.FLOAT;
        break;
      case 64:
        m.pixelType = FormatTools.DOUBLE;
        break;
      case 128:
        m.pixelType = FormatTools.UINT8;
        m.sizeC = 3;
        m.rgb = true;
        m.interleaved = true;
        m.dimensionOrder = "XYCZT";
      default:
        throw new FormatException("Unsupported data type: " + dataType);
    }

    LOGGER.info("Populating MetadataStore");

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    store.setImageName(imageName, 0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(description, 0);

      Length sizeX = FormatTools.getPhysicalSizeX(voxelWidth, UNITS.MILLIMETER);
      Length sizeY = FormatTools.getPhysicalSizeY(voxelHeight, UNITS.MILLIMETER);
      Length sizeZ = FormatTools.getPhysicalSizeZ(sliceThickness, UNITS.MILLIMETER);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, 0);
      }
      store.setPixelsTimeIncrement(new Time(deltaT, UNITS.MILLISECOND), 0);
    }
  }

}
