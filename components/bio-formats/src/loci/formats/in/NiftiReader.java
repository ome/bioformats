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

import java.io.IOException;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * NiftiReader is the file format reader for NIfTI files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/NiftiReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/NiftiReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
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

  // -- Constructor --

  /** Constructs a new NIfTI reader. */
  public NiftiReader() {
    super("NIfTI", new String[] {"nii", "img", "hdr"});
    suffixSufficient = false;
    blockCheckLen = 348;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    int dot = name.lastIndexOf(".") + 1;
    if (dot == 0) dot = name.length();
    String extension = name.substring(dot, name.length()).toLowerCase();
    if (extension.equals("nii")) return true;
    String headerFile = name.substring(0, dot - 1) + ".hdr";
    try {
      RandomAccessInputStream header = new RandomAccessInputStream(headerFile);
      boolean isValid = isThisType(header);
      header.close();
      return isValid;
    }
    catch (IOException e) {
      if (debug) trace(e);
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    stream.seek(344);
    String magic = stream.readString(3);
    return magic.equals("ni1") || magic.equals("n+1");
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

    int planeSize = getSizeX() * getSizeY() * getSizeC() *
      FormatTools.getBytesPerPixel(getPixelType());
    pixelFile.seek(pixelOffset + no * planeSize);
    readPlane(pixelFile, x, y, w, h, buf);

    return buf;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    if (pixelsFilename.equals(currentId)) return new String[] {currentId};
    return new String[] {currentId, pixelsFilename};
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    pixelOffset = 0;
    if (pixelFile != null) pixelFile.close();
    pixelFile = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("NiftiReader.initFile(" + id + ")");

    // the dataset has two files - we want the one ending in '.hdr'
    if (id.endsWith(".img")) {
      status("Looking for header file");
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
    in.seek(0);

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

    status("Reading header");

    int fileSize = in.readInt();

    in.skipBytes(35);

    char sliceOrdering = in.readChar();

    short nDimensions = in.readShort();
    short x = in.readShort();
    short y = in.readShort();
    short z = in.readShort();
    short t = in.readShort();
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

    float voxelWidth = in.readFloat();
    float voxelHeight = in.readFloat();
    float sliceThickness = in.readFloat();
    float deltaT = in.readFloat();
    in.skipBytes(12);

    pixelOffset = (int) in.readFloat();
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

    String description = in.readString(80);
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

    in.skipBytes(4);

    if (in.getFilePointer() < in.length()) {
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

    status("Populating metadata table");

    for (int i=0; i<transform.length; i++) {
      String axis = i == 0 ? "X" : i == 1 ? "Y" : "Z";
      for (int j=0; j<transform[i].length; j++) {
        addMeta("Affine transform " + axis + "[" + j + "]", transform[i][j]);
      }
    }
    addMeta("Intent name", intentName);
    addMeta("Slice Ordering", sliceOrdering);
    addMeta("Number of dimensions", nDimensions);
    addMeta("Width", x);
    addMeta("Height", y);
    addMeta("Number of Z slices", z);
    addMeta("Number of time points", t);
    addMeta("Dimension 5", dim5);
    addMeta("Dimension 6", dim6);
    addMeta("Dimension 7", dim7);
    addMeta("Intent #1", intent1);
    addMeta("Intent #2", intent2);
    addMeta("Intent #3", intent3);
    addMeta("Intent code", intentCode);
    addMeta("Data type", dataType);
    addMeta("Bits per pixel", bitpix);
    addMeta("Slice start", sliceStart);
    addMeta("Voxel width", voxelWidth);
    addMeta("Voxel height", voxelHeight);
    addMeta("Slice thickness", sliceThickness);
    addMeta("Time increment", deltaT);
    addMeta("Scale slope", scaleSlope);
    addMeta("Scale intercept", scaleIntercept);
    addMeta("Slice end", sliceEnd);
    addMeta("Calibrated maximum", calMax);
    addMeta("Calibrated minimum", calMin);
    addMeta("Slice duration", sliceDuration);
    addMeta("Time offset", toffset);
    addMeta("Description", description);
    addMeta("Q-form Code", qformCode);
    addMeta("S-form Code", sformCode);
    addMeta("Quaternion b parameter", quaternionB);
    addMeta("Quaternion c parameter", quaternionC);
    addMeta("Quaternion d parameter", quaternionD);
    addMeta("Quaternion x parameter", quaternionX);
    addMeta("Quaternion y parameter", quaternionY);
    addMeta("Quaternion z parameter", quaternionZ);

    status("Populating core metadata");

    core[0].sizeX = x;
    core[0].sizeY = y;
    core[0].sizeZ = z;
    core[0].sizeT = t;
    core[0].sizeC = 1;
    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;

    core[0].imageCount = getSizeZ() * getSizeT();
    core[0].rgb = false;
    core[0].interleaved = false;
    core[0].indexed = false;
    core[0].dimensionOrder = "XYZTC";

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
        core[0].rgb = true;
        core[0].interleaved = true;
        core[0].dimensionOrder = "XYCZT";
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
        core[0].rgb = true;
        core[0].interleaved = true;
        core[0].dimensionOrder = "XYCZT";
      default:
        throw new FormatException("Unsupported data type: " + dataType);
    }

    status("Populating MetadataStore");

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);

    store.setImageDescription(description, 0);

    store.setDimensionsPhysicalSizeX(new Float(voxelWidth), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(voxelHeight), 0, 0);
    store.setDimensionsPhysicalSizeZ(new Float(sliceThickness), 0, 0);
    store.setDimensionsTimeIncrement(new Float(deltaT), 0, 0);
  }

}
