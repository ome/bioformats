/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

import java.io.FileNotFoundException;
import java.io.IOException;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * NiftiReader is the file format reader for NIfTI files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class NiftiReader extends FormatReader {

  // -- Constants --

  /** Code for meters. */
  private static final int UNITS_METER = 1;

  /** Code for millimeters. */
  private static final int UNITS_MM = 2;

  /** Code for milliseconds. */
  private static final int UNITS_MSEC = 16;

  /** Code for microseconds. */
  private static final int UNITS_USEC = 24;

  // -- Fields --

  /** Offset to the pixel data in the .img file. */
  private int pixelOffset;

  /** File containing the pixel data. */
  private transient RandomAccessInputStream pixelFile;

  private String pixelsFilename;
  private short nDimensions;
  private String description;
  private double voxelWidth, voxelHeight, sliceThickness, deltaT;

  // -- Constructor --

  /** Constructs a new NIfTI reader. */
  public NiftiReader() {
    super("NIfTI", new String[] {"nii", "img", "hdr", "nii.gz"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.MEDICAL_DOMAIN,
      FormatTools.UNKNOWN_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "A single .nii file or a single .nii.gz file or one" +
      " .img file and a similarly-named .hdr file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return checkSuffix(id, "nii");
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "nii")) return true;
    int dot = name.lastIndexOf(".");
    if (dot == 0) dot = name.length() - 1;
    if (dot < 0) return false;
    if (!open) return false;
    String headerFile = name.substring(0, dot) + ".hdr";
    try {
      RandomAccessInputStream header = new RandomAccessInputStream(headerFile);
      boolean isValid = isThisType(header);
      header.close();
      return isValid;
    }
    catch (FileNotFoundException e) { } // NB: No output for missing header.
    catch (IOException e) { LOGGER.debug("", e); }
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 348;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    stream.seek(344);
    String magic = stream.readString(3);
    return magic.equals("ni1") || magic.equals("n+1");
  }

  /* @see loci.formats.IFormatReader#getDomains() */
  @Override
  public String[] getDomains() {
    FormatTools.assertId(currentId, true, 1);
    String[] domain = new String[1];
    domain[0] = nDimensions <= 3 ?
      FormatTools.UNKNOWN_DOMAIN : FormatTools.MEDICAL_DOMAIN;
    return domain;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    long planeSize = FormatTools.getPlaneSize(this);
    pixelFile.seek(pixelOffset + no * planeSize);
    readPlane(pixelFile, x, y, w, h, buf);

    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (pixelsFilename.equals(currentId) && noPixels) return null;
    if (!noPixels && !pixelsFilename.equals(currentId)) {
      return new String[] {currentId, pixelsFilename};
    }
    return new String[] {currentId};
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
      nDimensions = 0;
      description = null;
      voxelWidth = voxelHeight = sliceThickness = deltaT = 0d;
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

    CoreMetadata m = core.get(0);

    in.seek(40);
    short check = in.readShort();
    boolean little = check < 1 || check > 7;
    in.seek(40);

    if (id.endsWith(".hdr")) {
      pixelsFilename = id.substring(0, id.lastIndexOf(".")) + ".img";
      pixelFile = new RandomAccessInputStream(pixelsFilename);
    }
    else if (checkSuffix(id, "nii")) {
      pixelsFilename = id;
      pixelFile = in;
    } else {
      throw new FormatException("File does not have one of the required NIfTI extensions (.hdr, .nii, .nii.gz)");
    }

    in.order(little);
    pixelFile.order(little);

    m.littleEndian = little;

    LOGGER.info("Reading header");

    nDimensions = in.readShort();
    m.sizeX = in.readShort();
    m.sizeY = in.readShort();
    m.sizeZ = in.readShort();
    m.sizeT = in.readShort();

    in.skipBytes(20);
    short dataType = in.readShort();
    in.skipBytes(36);
    pixelOffset = (int) in.readFloat();

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      populateExtendedMetadata();
    }

    LOGGER.info("Populating core metadata");

    m.sizeC = 1;
    if (getSizeZ() == 0) m.sizeZ = 1;
    if (getSizeT() == 0) m.sizeT = 1;

    m.imageCount = getSizeZ() * getSizeT();
    m.indexed = false;
    m.dimensionOrder = "XYCZT";

    populatePixelType(dataType);
    m.rgb = getSizeC() > 1;
    m.interleaved = isRGB();

    LOGGER.info("Populating MetadataStore");

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(description, 0);

      Length sizeX =
        FormatTools.getPhysicalSizeX(new Double(voxelWidth));
      Length sizeY =
        FormatTools.getPhysicalSizeY(new Double(voxelHeight));
      Length sizeZ =
        FormatTools.getPhysicalSizeZ(new Double(sliceThickness));

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, 0);
      }
      store.setPixelsTimeIncrement(new Time(new Double(deltaT), UNITS.SECOND), 0);
    }
  }

  // -- Helper methods --

  private void populatePixelType(int dataType) throws FormatException {
    CoreMetadata m = core.get(0);

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
      case 256:
        m.pixelType = FormatTools.INT8;
        break;
      case 512:
        m.pixelType = FormatTools.UINT16;
        break;
      case 768:
        m.pixelType = FormatTools.UINT32;
        break;
      case 2304:
        m.pixelType = FormatTools.UINT8;
        m.sizeC = 4;
      default:
        throw new FormatException("Unsupported data type: " + dataType);
    }
  }

  private void populateExtendedMetadata() throws IOException {
    in.seek(40);
    char sliceOrdering = in.readChar();
    in.skipBytes(8);
    short dim5 = in.readShort();
    short dim6 = in.readShort();
    short dim7 = in.readShort();

    float intent1 = in.readFloat();
    float intent2 = in.readFloat();
    float intent3 = in.readFloat();

    short intentCode = in.readShort();
    short dataType = in.readShort();

    short bitpix = in.readShort();
    short sliceStart = in.readShort();

    in.skipBytes(4);

    voxelWidth = in.readFloat();
    voxelHeight = in.readFloat();
    sliceThickness = in.readFloat();
    deltaT = in.readFloat();
    in.skipBytes(16);

    float scaleSlope = in.readFloat();
    float scaleIntercept = in.readFloat();
    short sliceEnd = in.readShort();
    char sliceCode = in.readChar();
    char units = in.readChar();

    int spatialUnits = (units & 7);
    int timeUnits = (units & 0x38);

    // correct physical dimensions according to spatial and time units

    int spatialCorrection = 1;

    switch (spatialUnits) {
      case UNITS_METER:
        spatialCorrection = 1000000;
        break;
      case UNITS_MM:
        spatialCorrection = 1000;
        break;
    }

    voxelWidth *= spatialCorrection;
    voxelHeight *= spatialCorrection;
    sliceThickness *= spatialCorrection;

    int timeCorrection = 1;

    switch (timeUnits) {
      case UNITS_MSEC:
        timeCorrection = 1000;
        break;
      case UNITS_USEC:
        timeCorrection = 1000000;
        break;
    }

    deltaT /= timeCorrection;

    float calMax = in.readFloat();
    float calMin = in.readFloat();
    float sliceDuration = in.readFloat();
    float toffset = in.readFloat();

    in.skipBytes(8);

    description = in.readString(80);
    in.skipBytes(24);

    short qformCode = in.readShort();
    short sformCode = in.readShort();

    float quaternionB = in.readFloat();
    float quaternionC = in.readFloat();
    float quaternionD = in.readFloat();
    float quaternionX = in.readFloat();
    float quaternionY = in.readFloat();
    float quaternionZ = in.readFloat();

    float[][] transform = new float[3][4];
    for (int i=0; i<transform.length; i++) {
      for (int j=0; j<transform[i].length; j++) {
        transform[i][j] = in.readFloat();
      }
    }

    String intentName = in.readString(16);

    if (in.getFilePointer() + 4 < in.length()) {
      in.skipBytes(4);
      byte extension = in.readByte();
      in.skipBytes(3);

      if (extension != 0) {
        long max = in.equals(pixelFile) ? pixelOffset : in.length();
        while (in.getFilePointer() < max) {
          long fp = in.getFilePointer();
          int esize = in.readInt();
          int ecode = in.readInt();

          // TODO : parse key/value pairs from header extensions
          if (ecode == 2) {
            // DICOM format - attribute tag and value
          }
          else if (ecode == 4) {
            // AFNI format - XML-style elements
          }
          in.seek(fp + esize);
        }
      }
    }

    LOGGER.info("Populating metadata table");

    for (int i=0; i<transform.length; i++) {
      String axis = i == 0 ? "X" : i == 1 ? "Y" : "Z";
      for (int j=0; j<transform[i].length; j++) {
        addGlobalMeta("Affine transform " + axis + "[" + j + "]",
          transform[i][j]);
      }
    }
    addGlobalMeta("Intent name", intentName);
    addGlobalMeta("Slice Ordering", sliceOrdering);
    addGlobalMeta("Number of dimensions", nDimensions);
    addGlobalMeta("Width", getSizeX());
    addGlobalMeta("Height", getSizeY());
    addGlobalMeta("Number of Z slices", getSizeZ());
    addGlobalMeta("Number of time points", getSizeT());
    addGlobalMeta("Dimension 5", dim5);
    addGlobalMeta("Dimension 6", dim6);
    addGlobalMeta("Dimension 7", dim7);
    addGlobalMeta("Intent #1", intent1);
    addGlobalMeta("Intent #2", intent2);
    addGlobalMeta("Intent #3", intent3);
    addGlobalMeta("Intent code", intentCode);
    addGlobalMeta("Data type", dataType);
    addGlobalMeta("Bits per pixel", bitpix);
    addGlobalMeta("Slice start", sliceStart);
    addGlobalMeta("Voxel width", voxelWidth);
    addGlobalMeta("Voxel height", voxelHeight);
    addGlobalMeta("Slice thickness", sliceThickness);
    addGlobalMeta("Time increment", deltaT);
    addGlobalMeta("Scale slope", scaleSlope);
    addGlobalMeta("Scale intercept", scaleIntercept);
    addGlobalMeta("Slice end", sliceEnd);
    addGlobalMeta("Calibrated maximum", calMax);
    addGlobalMeta("Calibrated minimum", calMin);
    addGlobalMeta("Slice duration", sliceDuration);
    addGlobalMeta("Time offset", toffset);
    addGlobalMeta("Description", description);
    addGlobalMeta("Q-form Code", qformCode);
    addGlobalMeta("S-form Code", sformCode);
    addGlobalMeta("Quaternion b parameter", quaternionB);
    addGlobalMeta("Quaternion c parameter", quaternionC);
    addGlobalMeta("Quaternion d parameter", quaternionD);
    addGlobalMeta("Quaternion x parameter", quaternionX);
    addGlobalMeta("Quaternion y parameter", quaternionY);
    addGlobalMeta("Quaternion z parameter", quaternionZ);
  }

}
