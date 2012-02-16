//
// NiftiReader.java
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

import java.io.FileNotFoundException;
import java.io.IOException;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

/**
 * NiftiReader is the file format reader for NIfTI files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/NiftiReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/NiftiReader.java;hb=HEAD">Gitweb</a></dd></dl>
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
  private RandomAccessInputStream pixelFile;

  private String pixelsFilename;
  private short nDimensions;
  private String description;
  private double voxelWidth, voxelHeight, sliceThickness, deltaT;

  // -- Constructor --

  /** Constructs a new NIfTI reader. */
  public NiftiReader() {
    super("NIfTI", new String[] {"nii", "img", "hdr"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.MEDICAL_DOMAIN,
      FormatTools.UNKNOWN_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "A single .nii file or one .img file and a " +
      "similarly-named .hdr file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return checkSuffix(id, "nii");
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
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
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 348;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    stream.seek(344);
    String magic = stream.readString(3);
    return magic.equals("ni1") || magic.equals("n+1");
  }

  /* @see loci.formats.IFormatReader#getDomains() */
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
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (pixelsFilename.equals(currentId) && noPixels) return null;
    if (!noPixels && !pixelsFilename.equals(currentId)) {
      return new String[] {currentId, pixelsFilename};
    }
    return new String[] {currentId};
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
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

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
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

    in.seek(40);
    short check = in.readShort();
    boolean little = check < 1 || check > 7;
    in.seek(40);

    if (id.endsWith(".hdr")) {
      pixelsFilename = id.substring(0, id.lastIndexOf(".")) + ".img";
      pixelFile = new RandomAccessInputStream(pixelsFilename);
    }
    else if (id.endsWith(".nii")) {
      pixelsFilename = id;
      pixelFile = in;
    }

    in.order(little);
    pixelFile.order(little);

    core[0].littleEndian = little;

    LOGGER.info("Reading header");

    nDimensions = in.readShort();
    core[0].sizeX = in.readShort();
    core[0].sizeY = in.readShort();
    core[0].sizeZ = in.readShort();
    core[0].sizeT = in.readShort();

    in.skipBytes(20);
    short dataType = in.readShort();
    in.skipBytes(36);
    pixelOffset = (int) in.readFloat();

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      populateExtendedMetadata();
    }

    LOGGER.info("Populating core metadata");

    core[0].sizeC = 1;
    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;

    core[0].imageCount = getSizeZ() * getSizeT();
    core[0].indexed = false;
    core[0].dimensionOrder = "XYCZT";

    populatePixelType(dataType);
    core[0].rgb = getSizeC() > 1;
    core[0].interleaved = isRGB();

    LOGGER.info("Populating MetadataStore");

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(description, 0);
      if (voxelWidth > 0) {
        store.setPixelsPhysicalSizeX(
          new PositiveFloat(new Double(voxelWidth)), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
          voxelWidth);
      }
      if (voxelHeight > 0) {
        store.setPixelsPhysicalSizeY(
          new PositiveFloat(new Double(voxelHeight)), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
          voxelHeight);
      }
      if (sliceThickness > 0) {
        store.setPixelsPhysicalSizeZ(
          new PositiveFloat(new Double(sliceThickness)), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeZ; got {}",
          sliceThickness);
      }
      store.setPixelsTimeIncrement(new Double(deltaT), 0);
    }
  }

  // -- Helper methods --

  private void populatePixelType(int dataType) throws FormatException {
    switch (dataType) {
      case 1:
      case 2:
        core[0].pixelType = FormatTools.UINT8;
        break;
      case 4:
        core[0].pixelType = FormatTools.INT16;
        break;
      case 8:
        core[0].pixelType = FormatTools.INT32;
        break;
      case 16:
        core[0].pixelType = FormatTools.FLOAT;
        break;
      case 64:
        core[0].pixelType = FormatTools.DOUBLE;
        break;
      case 128:
        core[0].pixelType = FormatTools.UINT8;
        core[0].sizeC = 3;
      case 256:
        core[0].pixelType = FormatTools.INT8;
        break;
      case 512:
        core[0].pixelType = FormatTools.UINT16;
        break;
      case 768:
        core[0].pixelType = FormatTools.UINT32;
        break;
      case 2304:
        core[0].pixelType = FormatTools.UINT8;
        core[0].sizeC = 4;
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
