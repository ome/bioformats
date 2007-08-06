//
// FormatTools.java
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

package loci.formats;

import java.util.StringTokenizer;

/**
 * A utility class for format reader and writer implementations.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/FormatTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/FormatTools.java">SVN</a></dd></dl>
 */
public final class FormatTools {

  // -- Constants --

  /** Identifies the <i>INT8</i> data type used to store pixel values. */
  public static final int INT8 = 0;

  /** Identifies the <i>UINT8</i> data type used to store pixel values. */
  public static final int UINT8 = 1;

  /** Identifies the <i>INT16</i> data type used to store pixel values. */
  public static final int INT16 = 2;

  /** Identifies the <i>UINT16</i> data type used to store pixel values. */
  public static final int UINT16 = 3;

  /** Identifies the <i>INT32</i> data type used to store pixel values. */
  public static final int INT32 = 4;

  /** Identifies the <i>UINT32</i> data type used to store pixel values. */
  public static final int UINT32 = 5;

  /** Identifies the <i>FLOAT</i> data type used to store pixel values. */
  public static final int FLOAT = 6;

  /** Identifies the <i>DOUBLE</i> data type used to store pixel values. */
  public static final int DOUBLE = 7;

  /** Human readable pixel type. */
  private static String[] pixelTypes;
  static {
    pixelTypes = new String[8];
    pixelTypes[INT8] = "int8";
    pixelTypes[UINT8] = "uint8";
    pixelTypes[INT16] = "int16";
    pixelTypes[UINT16] = "uint16";
    pixelTypes[INT32] = "int32";
    pixelTypes[UINT32] = "uint32";
    pixelTypes[FLOAT] = "float";
    pixelTypes[DOUBLE] = "double";
  }

  /**
   * Identifies the <i>Channel</i> dimensional type,
   * representing a generic channel dimension.
   */
  public static final String CHANNEL = "Channel";

  /**
   * Identifies the <i>Spectra</i> dimensional type,
   * representing a dimension consisting of spectral channels.
   */
  public static final String SPECTRA = "Spectra";

  /**
   * Identifies the <i>Lifetime</i> dimensional type,
   * representing a dimension consisting of a lifetime histogram.
   */
  public static final String LIFETIME = "Lifetime";

  /**
   * Identifies the <i>Polarization</i> dimensional type,
   * representing a dimension consisting of polarization states.
   */
  public static final String POLARIZATION = "Polarization";

  /** File grouping options. */
  public static final int MUST_GROUP = 0;
  public static final int CAN_GROUP = 1;
  public static final int CANNOT_GROUP = 2;

  // -- Constructor --

  private FormatTools() { }

  // -- Utility methods - dimensional positions --

  /**
   * Gets the rasterized index corresponding
   * to the given Z, C and T coordinates.
   */
  public static int getIndex(IFormatReader reader, int z, int c, int t) {
    String order = reader.getDimensionOrder();
    int zSize = reader.getSizeZ();
    int cSize = reader.getEffectiveSizeC();
    int tSize = reader.getSizeT();
    int num = reader.getImageCount();
    return getIndex(order, zSize, cSize, tSize, num, z, c, t);
  }

  /**
   * Gets the rasterized index corresponding
   * to the given Z, C and T coordinates.
   */
  public static int getIndex(String order, int zSize, int cSize, int tSize,
    int num, int z, int c, int t)
  {
    // check DimensionOrder
    if (order == null) {
      throw new IllegalArgumentException("Dimension order is null");
    }
    if (!order.startsWith("XY")) {
      throw new IllegalArgumentException("Invalid dimension order: " + order);
    }
    int iz = order.indexOf("Z") - 2;
    int ic = order.indexOf("C") - 2;
    int it = order.indexOf("T") - 2;
    if (iz < 0 || iz > 2 || ic < 0 || ic > 2 || it < 0 || it > 2) {
      throw new IllegalArgumentException("Invalid dimension order: " + order);
    }

    // check SizeZ
    if (zSize <= 0) {
      throw new IllegalArgumentException("Invalid Z size: " + zSize);
    }
    if (z < 0 || z >= zSize) {
      throw new IllegalArgumentException("Invalid Z index: " + z + "/" + zSize);
    }

    // check SizeC
    if (cSize <= 0) {
      throw new IllegalArgumentException("Invalid C size: " + cSize);
    }
    if (c < 0 || c >= cSize) {
      throw new IllegalArgumentException("Invalid C index: " + c + "/" + cSize);
    }

    // check SizeT
    if (tSize <= 0) {
      throw new IllegalArgumentException("Invalid T size: " + tSize);
    }
    if (t < 0 || t >= tSize) {
      throw new IllegalArgumentException("Invalid T index: " + t + "/" + tSize);
    }

    // check image count
    if (num <= 0) {
      throw new IllegalArgumentException("Invalid image count: " + num);
    }
    if (num != zSize * cSize * tSize) {
      // if this happens, there is probably a bug in metadata population --
      // either one of the ZCT sizes, or the total number of images --
      // or else the input file is invalid
      throw new IllegalArgumentException("ZCT size vs image count mismatch " +
        "(sizeZ=" + zSize + ", sizeC=" + cSize + ", sizeT=" + tSize +
        ", total=" + num + ")");
    }

    // assign rasterization order
    int v0 = iz == 0 ? z : (ic == 0 ? c : t);
    int v1 = iz == 1 ? z : (ic == 1 ? c : t);
    int v2 = iz == 2 ? z : (ic == 2 ? c : t);
    int len0 = iz == 0 ? zSize : (ic == 0 ? cSize : tSize);
    int len1 = iz == 1 ? zSize : (ic == 1 ? cSize : tSize);
   
    return v0 + v1 * len0 + v2 * len0 * len1;
  }

  /**
   * Gets the Z, C and T coordinates corresponding
   * to the given rasterized index value.
   */
  public static int[] getZCTCoords(IFormatReader reader, int index) {
    String order = reader.getDimensionOrder();
    int zSize = reader.getSizeZ();
    int cSize = reader.getEffectiveSizeC();
    int tSize = reader.getSizeT();
    int num = reader.getImageCount();
    return getZCTCoords(order, zSize, cSize, tSize, num, index);
  }

  /**
   * Gets the Z, C and T coordinates corresponding to the given rasterized
   * index value.
   */
  public static int[] getZCTCoords(String order,
    int zSize, int cSize, int tSize, int num, int index)
  {
    // check DimensionOrder
    if (order == null) {
      throw new IllegalArgumentException("Dimension order is null");
    }
    if (!order.startsWith("XY")) {
      throw new IllegalArgumentException("Invalid dimension order: " + order);
    }
    int iz = order.indexOf("Z") - 2;
    int ic = order.indexOf("C") - 2;
    int it = order.indexOf("T") - 2;
    if (iz < 0 || iz > 2 || ic < 0 || ic > 2 || it < 0 || it > 2) {
      throw new IllegalArgumentException("Invalid dimension order: " + order);
    }

    // check SizeZ
    if (zSize <= 0) {
      throw new IllegalArgumentException("Invalid Z size: " + zSize);
    }

    // check SizeC
    if (cSize <= 0) {
      throw new IllegalArgumentException("Invalid C size: " + cSize);
    }

    // check SizeT
    if (tSize <= 0) {
      throw new IllegalArgumentException("Invalid T size: " + tSize);
    }

    // check image count
    if (num <= 0) {
      throw new IllegalArgumentException("Invalid image count: " + num);
    }
    if (num != zSize * cSize * tSize) {
      // if this happens, there is probably a bug in metadata population --
      // either one of the ZCT sizes, or the total number of images --
      // or else the input file is invalid
      throw new IllegalArgumentException("ZCT size vs image count mismatch " +
        "(sizeZ=" + zSize + ", sizeC=" + cSize + ", sizeT=" + tSize +
        ", total=" + num + ")");
    }
    if (index < 0 || index >= num) {
      throw new IllegalArgumentException("Invalid image index: " +
        index + "/" + num);
    }

    // assign rasterization order
    int len0 = iz == 0 ? zSize : (ic == 0 ? cSize : tSize);
    int len1 = iz == 1 ? zSize : (ic == 1 ? cSize : tSize);
    //int len2 = iz == 2 ? sizeZ : (ic == 2 ? sizeC : sizeT);
    int v0 = index % len0;
    int v1 = index / len0 % len1;
    int v2 = index / len0 / len1;
    int z = iz == 0 ? v0 : (iz == 1 ? v1 : v2);
    int c = ic == 0 ? v0 : (ic == 1 ? v1 : v2);
    int t = it == 0 ? v0 : (it == 1 ? v1 : v2);

    return new int[] {z, c, t};
  }

  /**
   * Computes a unique 1-D index corresponding
   * to the given multidimensional position.
   * @param lengths the maximum value for each positional dimension
   * @param pos position along each dimensional axis
   * @return rasterized index value
   */
  public static int positionToRaster(int[] lengths, int[] pos) {
    int offset = 1;
    int raster = 0;
    for (int i=0; i<pos.length; i++) {
      raster += offset * pos[i];
      offset *= lengths[i];
    }
    return raster;
  }

  /**
   * Computes a unique N-D position corresponding
   * to the given rasterized index value.
   * @param lengths the maximum value at each positional dimension
   * @param raster rasterized index value
   * @return position along each dimensional axis
   */
  public static int[] rasterToPosition(int[] lengths, int raster) {
    return rasterToPosition(lengths, raster, new int[lengths.length]);
  }

  /**
   * Computes a unique N-D position corresponding
   * to the given rasterized index value.
   * @param lengths the maximum value at each positional dimension
   * @param raster rasterized index value
   * @param pos preallocated position array to populate with the result
   * @return position along each dimensional axis
   */
  public static int[] rasterToPosition(int[] lengths, int raster, int[] pos) {
    int offset = 1;
    for (int i=0; i<pos.length; i++) {
      int offset1 = offset * lengths[i];
      int q = i < pos.length - 1 ? raster % offset1 : raster;
      pos[i] = q / offset;
      raster -= q;
      offset = offset1;
    }
    return pos;
  }

  /**
   * Computes the number of raster values for a positional array
   * with the given lengths.
   */
  public static int getRasterLength(int[] lengths) {
    int len = 1;
    for (int i=0; i<lengths.length; i++) len *= lengths[i];
    return len;
  }

  // -- Utility methods - pixel types --

  /**
   * Takes a string value and maps it to one of the pixel type enumerations.
   * @param pixelTypeAsString the pixel type as a string.
   * @return type enumeration value for use with class constants.
   */
  public static int pixelTypeFromString(String pixelTypeAsString) {
    String lowercaseTypeAsString = pixelTypeAsString.toLowerCase();
    for (int i = 0; i < pixelTypes.length; i++) {
      if (pixelTypes[i].equals(lowercaseTypeAsString)) return i;
    }
    throw new RuntimeException("Unknown type: '" + pixelTypeAsString + "'");
  }

  /**
   * Takes a pixel type value and gets a corresponding string representation.
   * @param pixelType the pixel type.
   * @return string value for human-readable output.
   */
  public static String getPixelTypeString(int pixelType) {
    return pixelType < 0 || pixelType >= pixelTypes.length ?
      "unknown (" + pixelType + ")" : pixelTypes[pixelType];
  }

  /**
   * Retrieves how many bytes per pixel the current plane or section has.
   * @param type the pixel type as retrieved from
   *   {@link IFormatReader#getPixelType(String)}.
   * @return the number of bytes per pixel.
   * @see IFormatReader#getPixelType(String)
   */
  public static int getBytesPerPixel(int type) {
    switch (type) {
      case INT8:
      case UINT8:
        return 1;
      case INT16:
      case UINT16:
        return 2;
      case INT32:
      case UINT32:
      case FLOAT:
        return 4;
      case DOUBLE:
        return 8;
    }
    throw new RuntimeException("Unknown type with id: '" + type + "'");
  }

  // -- Utility methods - XML --

  /** Indents XML to be more readable. */
  public static String indentXML(String xml) { return indentXML(xml, 3); }

  /** Indents XML by the given spacing to be more readable. */
  public static String indentXML(String xml, int spacing) {
    int indent = 0;
    StringBuffer sb = new StringBuffer();
    StringTokenizer st = new StringTokenizer(xml, "<>", true);
    boolean element = false;
    while (st.hasMoreTokens()) {
      String token = st.nextToken().trim();
      if (token.equals("")) continue;
      if (token.equals("<")) {
        element = true;
        continue;
      }
      if (element && token.equals(">")) {
        element = false;
        continue;
      }
      if (element && token.startsWith("/")) indent -= spacing;
      for (int j=0; j<indent; j++) sb.append(" ");
      if (element) sb.append("<");
      sb.append(token);
      if (element) sb.append(">");
      sb.append("\n");
      if (element && !token.startsWith("?") &&
        !token.startsWith("/") && !token.endsWith("/"))
      {
        indent += spacing;
      }
    }
    return sb.toString();
  }

  // -- Utility methods - sanity checking

  /**
   * Asserts that the current file is either null, nor not, according to the
   * given flag. If the assertion fails, an IllegalStateException is thrown.
   * @param currentId File name to test.
   * @param notNull True iff id should be non-null.
   * @param depth How far back in the stack the calling method is; this name
   *   is reported as part of the exception message, if available. Use zero
   *   to suppress output of the calling method name.
   */
  public static void assertId(String currentId, boolean notNull, int depth) {
    String msg = null;
    if (currentId == null && notNull) {
      msg = "Current file should not be null; call setId(String) first";
    }
    else if (currentId != null && !notNull) {
      msg = "Current file should be null, but is '" +
        currentId + "'; call close() first";
    }
    if (msg == null) return;

    StackTraceElement[] ste = new Exception().getStackTrace();
    String header;
    if (depth > 0 && ste.length > depth) {
      String c = ste[depth].getClassName();
      if (c.startsWith("loci.formats.")) {
        c = c.substring(c.lastIndexOf(".") + 1);
      }
      header = c + "." + ste[depth].getMethodName() + ": ";
    }
    else header = "";
    throw new IllegalStateException(header + msg);
  }

  // -- Utility methods -- metadata conversion --

  public static void convertMetadata(MetadataRetrieve source,
    MetadataStore dest)
  {
    Integer ii = null;
    int globalPixCount = 0;

    for (int i=0; i<source.getImageCount(); i++) {
      ii = new Integer(i);
      dest.setImage(source.getImageName(ii), source.getCreationDate(ii),
        source.getDescription(ii), ii);

      dest.setDimensions(source.getPixelSizeX(ii),
        source.getPixelSizeY(ii), source.getPixelSizeZ(ii),
        source.getPixelSizeC(ii), source.getPixelSizeT(ii), ii);

      for (int j=0; j<source.getPixelsCount(ii); j++) {
        Integer p = new Integer(j);
        dest.setPixels(source.getSizeX(ii), source.getSizeY(ii),
          source.getSizeZ(ii), source.getSizeC(ii),
          source.getSizeT(ii),
          new Integer(pixelTypeFromString(source.getPixelType(ii))),
          source.getBigEndian(ii), source.getDimensionOrder(ii), ii, p);

        dest.setDisplayOptions(source.getZoom(ii),
          source.isRedChannelOn(ii), source.isGreenChannelOn(ii),
          source.isBlueChannelOn(ii), source.isDisplayRGB(ii),
          source.getColorMap(ii), source.getZStart(ii),
          source.getZStop(ii), source.getTStart(ii),
          source.getTStop(ii), ii, p, new Integer(0), new Integer(1),
            new Integer(2), new Integer(0));

        Integer globalPix = new Integer(globalPixCount);
        for (int ch=0; ch<source.getChannelCount(globalPix); ch++) {
          Integer c = new Integer(ch);
          dest.setLogicalChannel(ch, source.getChannelName(globalPix, c),
            null, null, null, null, null, null, null, null, null, null, null,
            source.getPhotometricInterpretation(globalPix, c),
            source.getMode(globalPix, c), null, null, null, null, null,
            source.getEmWave(globalPix, c), source.getExWave(globalPix, c),
            null, source.getChannelNDFilter(globalPix, c), globalPix);

          dest.setChannelGlobalMinMax(ch, source.getGlobalMin(globalPix, c),
            source.getGlobalMax(globalPix, c), globalPix);

          dest.setDisplayChannel(c, source.getBlackLevel(globalPix, c),
            source.getWhiteLevel(globalPix, c), source.getGamma(globalPix, c),
            globalPix);
        }

        globalPixCount++;
      }

      dest.setImagingEnvironment(source.getTemperature(ii),
        source.getAirPressure(ii), source.getHumidity(ii),
        source.getCO2Percent(ii), ii);
    }

    for (int i=0; i<source.getExperimenterCount(); i++) {
      ii = new Integer(i);
      dest.setExperimenter(source.getFirstName(ii),
        source.getLastName(ii), source.getEmail(ii),
        source.getInstitution(ii), source.getDataDirectory(ii),
        source.getGroup(ii), ii);
    }

    for (int i=0; i<source.getGroupCount(); i++) {
      ii = new Integer(i);
      dest.setGroup(source.getGroupName(ii), source.getLeader(ii),
        source.getContact(ii), ii);
    }

    for (int i=0; i<source.getInstrumentCount(); i++) {
      ii = new Integer(i);
      dest.setInstrument(source.getManufacturer(ii),
        source.getModel(ii), source.getSerialNumber(ii),
        source.getType(ii), ii);
    }

    for (int i=0; i<source.getDisplayROICount(); i++) {
      ii = new Integer(i);
      dest.setDisplayROI(source.getX0(ii), source.getY0(ii),
        source.getZ0(ii), source.getX1(ii), source.getY1(ii),
        source.getZ1(ii), source.getT0(ii), source.getT1(ii),
        source.getDisplayOptions(ii), ii);
    }

    for (int i=0; i<source.getStageLabelCount(); i++) {
      ii = new Integer(i);
      dest.setStageLabel(source.getStageName(ii), source.getStageX(ii),
        source.getStageY(ii), source.getStageZ(ii), ii);
    }

    ii = null;

    dest.setPlaneInfo(0, 0, 0, source.getTimestamp(ii, ii, ii, ii),
      source.getExposureTime(ii, ii, ii, ii), ii);

    dest.setLightSource(source.getLightManufacturer(ii),
      source.getLightModel(ii), source.getLightSerial(ii), ii, ii);

    dest.setLaser(source.getLaserType(ii), source.getLaserMedium(ii),
      source.getLaserWavelength(ii), source.isFrequencyDoubled(ii),
      source.isTunable(ii), source.getPulse(ii),
      source.getPower(ii), ii, ii, ii, ii);

    dest.setFilament(source.getFilamentType(ii),
      source.getFilamentPower(ii), ii, ii);

    dest.setArc(source.getArcType(ii), source.getArcPower(ii), ii, ii);

    dest.setDetector(source.getDetectorManufacturer(ii),
      source.getDetectorModel(ii), source.getDetectorSerial(ii),
      source.getDetectorType(ii), source.getDetectorGain(ii),
      source.getDetectorVoltage(ii),
      source.getDetectorOffset(ii), ii, ii);

    dest.setObjective(source.getObjectiveManufacturer(ii),
      source.getObjectiveModel(ii), source.getObjectiveSerial(ii),
      source.getLensNA(ii),
      source.getObjectiveMagnification(ii), ii, ii);

    dest.setExcitationFilter(source.getExcitationManufacturer(ii),
      source.getExcitationModel(ii), source.getExcitationLotNumber(ii),
      source.getExcitationType(ii), ii);

    dest.setDichroic(source.getDichroicManufacturer(ii),
      source.getDichroicModel(ii), source.getDichroicLotNumber(ii), ii);

    dest.setEmissionFilter(source.getEmissionManufacturer(ii),
      source.getEmissionModel(ii), source.getEmissionLotNumber(ii),
      source.getEmissionType(ii), ii);

    dest.setFilterSet(source.getFilterSetManufacturer(ii),
      source.getFilterSetModel(ii),
      source.getFilterSetLotNumber(ii), ii, ii);

    dest.setOTF(source.getOTFSizeX(ii), source.getOTFSizeY(ii),
      source.getOTFPixelType(ii), source.getOTFPath(ii),
      source.getOTFOpticalAxisAverage(ii), ii, ii, ii, ii);
  }

}
