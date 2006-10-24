//
// BaseTiffReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Hashtable;
import loci.formats.DataTools;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataStore;
import loci.formats.RandomAccessStream;
import loci.formats.TiffRational;
import loci.formats.TiffTools;

/**
 * BaseTiffReader is the superclass for file format readers compatible with
 * or derived from the TIFF 6.0 file format.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public abstract class BaseTiffReader extends FormatReader {

  /** The minimum index in the channelMinMax array */
  private static final int MIN = 0;

  /** The maximum index in the channelMinMax array */
  private static final int MAX = 1;

  // -- Fields --

  /** Current TIFF file. */
  protected RandomAccessStream in;

  /** List of IFDs for the current TIFF. */
  protected Hashtable[] ifds;

  /** Number of images in the current TIFF stack. */
  protected int numImages;

  /** The global min and max for each channel. */
  protected Double[][] channelMinMax;

  // -- Constructors --

  /** Constructs a new BaseTiffReader. */
  public BaseTiffReader(String name, String suffix) { super(name, suffix); }

  /** Constructs a new BaseTiffReader. */
  public BaseTiffReader(String name, String[] suffixes) {
    super(name, suffixes);
  }

  // -- BaseTiffReader API methods --

  /** Gets the dimensions of the given (possibly multi-page) TIFF file. */
  public int[] getTiffDimensions(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (ifds == null || ifds.length == 0) return null;
    return new int[] {
      TiffTools.getIFDIntValue(ifds[0], TiffTools.IMAGE_WIDTH, false, -1),
      TiffTools.getIFDIntValue(ifds[0], TiffTools.IMAGE_LENGTH, false, -1),
      numImages
    };
  }

  /**
   * Obtains an object which represents a given plane within the file.
   * @param id The path to the file.
   * @param no The plane or section within the file to obtain.
   * @return an object which represents the plane.
   * @throws FormatException if there is an error parsing the file.
   * @throws IOException if there is an error reading from the file or
   *   acquiring permissions to read the file.
   */
  public Plane2D openPlane2D(String id, int no)
    throws FormatException, IOException
  {
    return new Plane2D(
      ByteBuffer.wrap(openBytes(id, no)), getPixelType(id), isLittleEndian(id),
                      getSizeX(id), getSizeY(id));
  }

  // -- Internal BaseTiffReader API methods --

  /** Populates the metadata hashtable and metadata store. */
  protected void initMetadata() {
    initStandardMetadata();
    initMetadataStore();
  }

  /**
   * Parses standard metadata.
   *
   * NOTE: Absolutely <b>no</b> calls to the metadata store should be made in
   * this method or methods that override this method. Data <b>will</b> be
   * overwritten if you do so.
   */
  protected void initStandardMetadata() {
    Hashtable ifd = ifds[0];
    if (metadata == null) metadata = new Hashtable();
    put("ImageWidth", ifd, TiffTools.IMAGE_WIDTH);
    put("ImageLength", ifd, TiffTools.IMAGE_LENGTH);
    put("BitsPerSample", ifd, TiffTools.BITS_PER_SAMPLE);

    int comp = TiffTools.getIFDIntValue(ifd, TiffTools.COMPRESSION);
    String compression = null;
    switch (comp) {
      case TiffTools.UNCOMPRESSED:
        compression = "None";
        break;
      case TiffTools.CCITT_1D:
        compression = "CCITT Group 3 1-Dimensional Modified Huffman";
        break;
      case TiffTools.GROUP_3_FAX:
        compression = "CCITT T.4 bilevel encoding";
        break;
      case TiffTools.GROUP_4_FAX:
        compression = "CCITT T.6 bilevel encoding";
        break;
      case TiffTools.LZW:
        compression = "LZW";
        break;
      case TiffTools.JPEG:
        compression = "JPEG";
        break;
      case TiffTools.PACK_BITS:
        compression = "PackBits";
        break;
    }
    put("Compression", compression);

    int photo = TiffTools.getIFDIntValue(ifd,
      TiffTools.PHOTOMETRIC_INTERPRETATION);
    String photoInterp = null;
    String metaDataPhotoInterp = null;

    switch (photo) {
      case TiffTools.WHITE_IS_ZERO:
        photoInterp = "WhiteIsZero";
        metaDataPhotoInterp = "Monochrome";
        break;
      case TiffTools.BLACK_IS_ZERO:
        photoInterp = "BlackIsZero";
        metaDataPhotoInterp = "Monochrome";
        break;
      case TiffTools.RGB:
        photoInterp = "RGB";
        metaDataPhotoInterp = "RGB";
        break;
      case TiffTools.RGB_PALETTE:
        photoInterp = "Palette";
        metaDataPhotoInterp = "Monochrome";
        break;
      case TiffTools.TRANSPARENCY_MASK:
        photoInterp = "Transparency Mask";
        metaDataPhotoInterp = "RGB";
        break;
      case TiffTools.CMYK:
        photoInterp = "CMYK";
        metaDataPhotoInterp = "CMYK";
        break;
      case TiffTools.Y_CB_CR:
        photoInterp = "YCbCr";
        metaDataPhotoInterp = "RGB";
        break;
      case TiffTools.CIE_LAB:
        photoInterp = "CIELAB";
        metaDataPhotoInterp = "RGB";
        break;
      case TiffTools.CFA_ARRAY:
        photoInterp = "Color Filter Array";
        metaDataPhotoInterp = "RGB";
        break;
    }
    put("PhotometricInterpretation", photoInterp);
    put("MetaDataPhotometricInterpretation", metaDataPhotoInterp);

    putInt("CellWidth", ifd, TiffTools.CELL_WIDTH);
    putInt("CellLength", ifd, TiffTools.CELL_LENGTH);

    int or = TiffTools.getIFDIntValue(ifd, TiffTools.ORIENTATION);

    // adjust the width and height if necessary
    if (or == 8) {
      put("ImageWidth", ifd, TiffTools.IMAGE_LENGTH);
      put("ImageLength", ifd, TiffTools.IMAGE_WIDTH);
    }

    String orientation = null;
    // there is no case 0
    switch (or) {
      case 1:
        orientation = "1st row -> top; 1st column -> left";
        break;
      case 2:
        orientation = "1st row -> top; 1st column -> right";
        break;
      case 3:
        orientation = "1st row -> bottom; 1st column -> right";
        break;
      case 4:
        orientation = "1st row -> bottom; 1st column -> left";
        break;
      case 5:
        orientation = "1st row -> left; 1st column -> top";
        break;
      case 6:
        orientation = "1st row -> right; 1st column -> top";
        break;
      case 7:
        orientation = "1st row -> right; 1st column -> bottom";
        break;
      case 8:
        orientation = "1st row -> left; 1st column -> bottom";
        break;
    }
    put("Orientation", orientation);
    putInt("SamplesPerPixel", ifd, TiffTools.SAMPLES_PER_PIXEL);

    put("Software", ifd, TiffTools.SOFTWARE);
    put("DateTime", ifd, TiffTools.DATE_TIME);
    put("Artist", ifd, TiffTools.ARTIST);

    put("HostComputer", ifd, TiffTools.HOST_COMPUTER);
    put("Copyright", ifd, TiffTools.COPYRIGHT);

    put("NewSubfileType", ifd, TiffTools.NEW_SUBFILE_TYPE);

    int thresh = TiffTools.getIFDIntValue(ifd, TiffTools.THRESHHOLDING);
    String threshholding = null;
    switch (thresh) {
      case 1:
        threshholding = "No dithering or halftoning";
        break;
      case 2:
        threshholding = "Ordered dithering or halftoning";
        break;
      case 3:
        threshholding = "Randomized error diffusion";
        break;
    }
    put("Threshholding", threshholding);

    int fill = TiffTools.getIFDIntValue(ifd, TiffTools.FILL_ORDER);
    String fillOrder = null;
    switch (fill) {
      case 1:
        fillOrder = "Pixels with lower column values are stored " +
          "in the higher order bits of a byte";
        break;
      case 2:
        fillOrder = "Pixels with lower column values are stored " +
          "in the lower order bits of a byte";
        break;
    }
    put("FillOrder", fillOrder);

    putInt("Make", ifd, TiffTools.MAKE);
    putInt("Model", ifd, TiffTools.MODEL);
    putInt("MinSampleValue", ifd, TiffTools.MIN_SAMPLE_VALUE);
    putInt("MaxSampleValue", ifd, TiffTools.MAX_SAMPLE_VALUE);
    putInt("XResolution", ifd, TiffTools.X_RESOLUTION);
    putInt("YResolution", ifd, TiffTools.Y_RESOLUTION);

    int planar = TiffTools.getIFDIntValue(ifd,
      TiffTools.PLANAR_CONFIGURATION);
    String planarConfig = null;
    switch (planar) {
      case 1:
        planarConfig = "Chunky";
        break;
      case 2:
        planarConfig = "Planar";
        break;
    }
    put("PlanarConfiguration", planarConfig);

    putInt("XPosition", ifd, TiffTools.X_POSITION);
    putInt("YPosition", ifd, TiffTools.Y_POSITION);
    putInt("FreeOffsets", ifd, TiffTools.FREE_OFFSETS);
    putInt("FreeByteCounts", ifd, TiffTools.FREE_BYTE_COUNTS);
    putInt("GrayResponseUnit", ifd, TiffTools.GRAY_RESPONSE_UNIT);
    putInt("GrayResponseCurve", ifd, TiffTools.GRAY_RESPONSE_CURVE);
    putInt("T4Options", ifd, TiffTools.T4_OPTIONS);
    putInt("T6Options", ifd, TiffTools.T6_OPTIONS);

    int res = TiffTools.getIFDIntValue(ifd, TiffTools.RESOLUTION_UNIT);
    String resUnit = null;
    switch (res) {
      case 1:
        resUnit = "None";
        break;
      case 2:
        resUnit = "Inch";
        break;
      case 3:
        resUnit = "Centimeter";
        break;
    }
    put("ResolutionUnit", resUnit);

    putInt("PageNumber", ifd, TiffTools.PAGE_NUMBER);
    putInt("TransferFunction", ifd, TiffTools.TRANSFER_FUNCTION);

    int predict = TiffTools.getIFDIntValue(ifd, TiffTools.PREDICTOR);
    String predictor = null;
    switch (predict) {
      case 1:
        predictor = "No prediction scheme";
        break;
      case 2:
        predictor = "Horizontal differencing";
        break;
    }
    put("Predictor", predictor);

    putInt("WhitePoint", ifd, TiffTools.WHITE_POINT);
    putInt("PrimaryChromacities", ifd, TiffTools.PRIMARY_CHROMATICITIES);

    putInt("HalftoneHints", ifd, TiffTools.HALFTONE_HINTS);
    putInt("TileWidth", ifd, TiffTools.TILE_WIDTH);
    putInt("TileLength", ifd, TiffTools.TILE_LENGTH);
    putInt("TileOffsets", ifd, TiffTools.TILE_OFFSETS);
    putInt("TileByteCounts", ifd, TiffTools.TILE_BYTE_COUNTS);

    int ink = TiffTools.getIFDIntValue(ifd, TiffTools.INK_SET);
    String inkSet = null;
    switch (ink) {
      case 1:
        inkSet = "CMYK";
        break;
      case 2:
        inkSet = "Other";
        break;
    }
    put("InkSet", inkSet);

    putInt("InkNames", ifd, TiffTools.INK_NAMES);
    putInt("NumberOfInks", ifd, TiffTools.NUMBER_OF_INKS);
    putInt("DotRange", ifd, TiffTools.DOT_RANGE);
    put("TargetPrinter", ifd, TiffTools.TARGET_PRINTER);
    putInt("ExtraSamples", ifd, TiffTools.EXTRA_SAMPLES);

    int fmt = TiffTools.getIFDIntValue(ifd, TiffTools.SAMPLE_FORMAT);
    String sampleFormat = null;
    switch (fmt) {
      case 1:
        sampleFormat = "unsigned integer";
        break;
      case 2:
        sampleFormat = "two's complement signed integer";
        break;
      case 3:
        sampleFormat = "IEEE floating point";
        break;
      case 4:
        sampleFormat = "undefined";
        break;
    }
    put("SampleFormat", sampleFormat);

    putInt("SMinSampleValue", ifd, TiffTools.S_MIN_SAMPLE_VALUE);
    putInt("SMaxSampleValue", ifd, TiffTools.S_MAX_SAMPLE_VALUE);
    putInt("TransferRange", ifd, TiffTools.TRANSFER_RANGE);

    int jpeg = TiffTools.getIFDIntValue(ifd, TiffTools.JPEG_PROC);
    String jpegProc = null;
    switch (jpeg) {
      case 1:
        jpegProc = "baseline sequential process";
        break;
      case 14:
        jpegProc = "lossless process with Huffman coding";
        break;
    }
    put("JPEGProc", jpegProc);

    putInt("JPEGInterchangeFormat", ifd, TiffTools.JPEG_INTERCHANGE_FORMAT);
    putInt("JPEGRestartInterval", ifd, TiffTools.JPEG_RESTART_INTERVAL);

    putInt("JPEGLosslessPredictors",
      ifd, TiffTools.JPEG_LOSSLESS_PREDICTORS);
    putInt("JPEGPointTransforms", ifd, TiffTools.JPEG_POINT_TRANSFORMS);
    putInt("JPEGQTables", ifd, TiffTools.JPEG_Q_TABLES);
    putInt("JPEGDCTables", ifd, TiffTools.JPEG_DC_TABLES);
    putInt("JPEGACTables", ifd, TiffTools.JPEG_AC_TABLES);
    putInt("YCbCrCoefficients", ifd, TiffTools.Y_CB_CR_COEFFICIENTS);

    int ycbcr = TiffTools.getIFDIntValue(ifd,
      TiffTools.Y_CB_CR_SUB_SAMPLING);
    String subSampling = null;
    switch (ycbcr) {
      case 1:
        subSampling = "chroma image dimensions = luma image dimensions";
        break;
      case 2:
        subSampling = "chroma image dimensions are " +
          "half the luma image dimensions";
        break;
      case 4:
        subSampling = "chroma image dimensions are " +
          "1/4 the luma image dimensions";
        break;
    }
    put("YCbCrSubSampling", subSampling);

    putInt("YCbCrPositioning", ifd, TiffTools.Y_CB_CR_POSITIONING);
    putInt("ReferenceBlackWhite", ifd, TiffTools.REFERENCE_BLACK_WHITE);

    // bits per sample and number of channels
    Object bpsObj = TiffTools.getIFDValue(ifd, TiffTools.BITS_PER_SAMPLE);
    int bps = -1, numC = 3;
    if (bpsObj instanceof int[]) {
      int[] q = (int[]) bpsObj;
      bps = q[0];
      numC = q.length;
    }
    else if (bpsObj instanceof Number) {
      bps = ((Number) bpsObj).intValue();
      numC = 1;
    }

    // numC isn't set properly if we have an indexed color image, so we need
    // to reset it here

    int p = TiffTools.getIFDIntValue(ifd, TiffTools.PHOTOMETRIC_INTERPRETATION);
    if (!ignoreColorTable &&
      (p == TiffTools.RGB_PALETTE || p == TiffTools.CFA_ARRAY)) {
      numC = 3;
      bps *= 3;
    }

    put("BitsPerSample", bps);
    put("NumberOfChannels", numC);

    // TIFF comment
    String comment = null;
    Object o = TiffTools.getIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION);
    if (o instanceof String) comment = (String) o;
    else if (o instanceof String[]) {
      String[] s = (String[]) o;
      if (s.length > 0) comment = s[0];
    }
    else if (o != null) comment = o.toString();
    if (comment != null) {
      // sanitize comment
      comment = comment.replaceAll("\r\n", "\n"); // CR-LF to LF
      comment = comment.replaceAll("\r", "\n"); // CR to LF
      put("Comment", comment);
    }

    try {
      sizeX[0] =
        TiffTools.getIFDIntValue(ifds[0], TiffTools.IMAGE_WIDTH, false, 0);
      sizeY[0] =
        TiffTools.getIFDIntValue(ifds[0], TiffTools.IMAGE_LENGTH, false, 0);
      sizeZ[0] = 1;
      sizeC[0] = isRGB(currentId) ? 3 : 1;
      sizeT[0] = ifds.length;

      int bitsPerSample = TiffTools.getIFDIntValue(ifds[0],
        TiffTools.BITS_PER_SAMPLE);
      int bitFormat = TiffTools.getIFDIntValue(ifds[0],
        TiffTools.SAMPLE_FORMAT);

      //if (bitsPerSample == 12) bitsPerSample = 8;  // special case
      while (bitsPerSample % 8 != 0) bitsPerSample++;
      if (bitsPerSample == 24 || bitsPerSample == 48) bitsPerSample /= 3;

      if (bitFormat == 3) pixelType[0] = FormatReader.FLOAT;
      else if (bitFormat == 2) {
        switch (bitsPerSample) {
          case 8:
            pixelType[0] = FormatReader.INT8;
            break;
          case 16:
            pixelType[0] = FormatReader.INT16;
            break;
          case 32:
            pixelType[0] = FormatReader.INT32;
            break;
        }
      }
      else {
        switch (bitsPerSample) {
          case 8:
            pixelType[0] = FormatReader.UINT8;
            break;
          case 16:
            pixelType[0] = FormatReader.UINT16;
            break;
          case 32:
            pixelType[0] = FormatReader.UINT32;
            break;
        }
      }

      currentOrder[0] = "XYCZT";
    }
    catch (Exception e) { }
  }

  /**
   * Populates the metadata store using the data parsed in
   * {@link #initStandardMetadata()} along with some further parsing done in
   * the method itself.
   *
   * All calls to the active <code>MetadataStore</code> should be made in this
   * method and <b>only</b> in this method. This is especially important for
   * sub-classes that override the getters for pixel set array size, etc.
   */
  protected void initMetadataStore() {
    Hashtable ifd = ifds[0];
    try {
      // Set the pixel values in the metadata store.
      setPixels();

      // The metadata store we're working with.
      MetadataStore store = getMetadataStore(currentId);

      // populate Experimenter element
      String artist = (String) TiffTools.getIFDValue(ifd, TiffTools.ARTIST);
      if (artist != null) {
        String firstName = null, lastName = null;
        int ndx = artist.indexOf(" ");
        if (ndx < 0) lastName = artist;
        else {
          firstName = artist.substring(0, ndx);
          lastName = artist.substring(ndx + 1);
        }
        String email = (String)
          TiffTools.getIFDValue(ifd, TiffTools.HOST_COMPUTER);
        store.setExperimenter(firstName, lastName, email,
          null, null, null, null);
      }

      // populate Image element
      setImage();

      // populate Logical Channel elements
      for (int i=0; i<getSizeC(currentId); i++) {
        try {
          setLogicalChannel(i);
          if ((getChannelGlobalMinimum(currentId, 0) == null ||
            getChannelGlobalMaximum(currentId, 0) == null) &&
            enableChannelStatCalculation)
          {
            setChannelGlobalMinMax(i);
          }
        }
        catch (Exception e) { }
      }

      //Populate the default display options
      store.setDefaultDisplaySettings(null);

      // Use a default "real" pixel dimension of 1 for each dimensionality.
      Float f = new Float(1);
      store.setDimensions(f, f, f, f, f, null);

      // populate Dimensions element
      int pixelSizeX = TiffTools.getIFDIntValue(ifd,
        TiffTools.CELL_WIDTH, false, 0);
      int pixelSizeY = TiffTools.getIFDIntValue(ifd,
        TiffTools.CELL_LENGTH, false, 0);
      int pixelSizeZ = TiffTools.getIFDIntValue(ifd,
        TiffTools.ORIENTATION, false, 0);
      store.setDimensions(new Float(pixelSizeX), new Float(pixelSizeY),
                          new Float(pixelSizeZ), null, null, null);

//      OMETools.setAttribute(ome, "ChannelInfo", "SamplesPerPixel", "" +
//        TiffTools.getIFDIntValue(ifd, TiffTools.SAMPLES_PER_PIXEL));

      // populate StageLabel element
      Object x = TiffTools.getIFDValue(ifd, TiffTools.X_POSITION);
      Object y = TiffTools.getIFDValue(ifd, TiffTools.Y_POSITION);
      Float stageX;
      Float stageY;
      if (x instanceof TiffRational) {
        stageX = x == null ? null : new Float(((TiffRational) x).floatValue());
        stageY = y == null ? null : new Float(((TiffRational) y).floatValue());
      }
      else {
        stageX = x == null ? null : new Float((String) x);
        stageY = y == null ? null : new Float((String) y);
      }
      store.setStageLabel(null, stageX, stageY, null, null);

      // populate Instrument element
      String model = (String) TiffTools.getIFDValue(ifd, TiffTools.MODEL);
      String serialNumber = (String)
        TiffTools.getIFDValue(ifd, TiffTools.MAKE);
      store.setInstrument(null, model, serialNumber, null, null);
    }
    catch (FormatException exc) { exc.printStackTrace(); }
    catch (IOException ex) { ex.printStackTrace(); }
  }

  /**
   * If the TIFF is big-endian.
   * @return <code>true</code> if the TIFF is big-endian, <code>false</code>
   * otherwise.
   * @throws FormatException if there is a problem parsing this metadata.
   */
  protected Boolean getBigEndian() throws FormatException {
    return new Boolean(!TiffTools.isLittleEndian(ifds[0]));
  }

  /**
   * Retrieves the image name from the TIFF.
   * @return the image name.
   */
  protected String getImageName() { return currentId; }

  /**
   * Retrieves the image creation date.
   * @return the image creation date.
   */
  protected String getImageCreationDate() {
    return (String) TiffTools.getIFDValue(ifds[0], TiffTools.DATE_TIME);
  }

  /**
   * Retrieves the image description.
   * @return the image description.
   */
  protected String getImageDescription() {
    return (String) metadata.get("Comment");
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a TIFF file. */
  public boolean isThisType(byte[] block) {
    return TiffTools.isValidHeader(block);
  }

  /** Determines the number of images in the given TIFF file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (TiffTools.getIFDIntValue(ifds[0],
      TiffTools.SAMPLES_PER_PIXEL, false, 1) > 1)
    {
      return true;
    }
    try {
      int p = TiffTools.getIFDIntValue(ifds[0],
        TiffTools.PHOTOMETRIC_INTERPRETATION, true, 0);
      return (!ignoreColorTable &&
        (p == TiffTools.RGB_PALETTE || p == TiffTools.CFA_ARRAY)) ||
        p == TiffTools.RGB;
    }
    catch (Exception e) {
      return TiffTools.getIFDIntValue(ifds[0],
        TiffTools.SAMPLES_PER_PIXEL, true, 0) > 1;
    }
  }

  /**
   * Obtains the specified metadata field's value for the given file.
   *
   * @param field the name associated with the metadata field
   * @return the value, or null if the field doesn't exist
   */
  public Object getMetadataValue(String id, String field)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    return metadata.get(field);
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMinimum(int) */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (channelMinMax == null || channelMinMax[theC] == null)
      return null;
    return channelMinMax[theC][MIN];
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMaximum(int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (channelMinMax == null || channelMinMax[theC] == null)
      return null;
    return channelMinMax[theC][MAX];
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return TiffTools.isLittleEndian(ifds[0]);
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.FormatReader#openBytes(String, int, byte[]) */
  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    byte[][] p = null;
    p = TiffTools.getSamples(ifds[no], in, 0, ignoreColorTable);
    for (int i=0; i<p.length; i++) {
      swapIfRequired(p[i]);
      System.arraycopy(p[i], 0, buf, i * p[0].length, p[0].length);
    }
    return buf;
  }

  /* @see loci.formats.FormatReader#openBytes(String, int) */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    int bytesPerPixel = ImageReader.getBytesPerPixel(getPixelType(id));
    byte[] buf = new byte[getSizeX(id) * getSizeY(id) * getSizeC(id) *
      bytesPerPixel];
    return openBytes(id, no, buf);
  }

  /**
   * Examines a byte array to see if it needs to be byte swapped and modifies
   * the byte array directly.
   * @param byteArray The byte array to check and modify if required.
   * @return the <i>byteArray</i> either swapped or not for convenience.
   * @throws IOException if there is an error read from the file.
   * @throws FormatException if there is an error during metadata parsing.
   */
  private byte[] swapIfRequired(byte[] byteArray)
    throws FormatException, IOException
  {
    int bitsPerSample = TiffTools.getBitsPerSample(ifds[0])[0];

    // We've got nothing to do if the samples are only 8-bits wide or if they
    // are floating point.
    if (bitsPerSample == 8 || bitsPerSample == 32) return byteArray;

    if (isLittleEndian(currentId)) {
      if (bitsPerSample == 16) { // short
        ShortBuffer buf = ByteBuffer.wrap(byteArray).asShortBuffer();
        for (int i = 0; i < (byteArray.length / 2); i++) {
          buf.put(i, Bits.swap(buf.get(i)));
        }
      }
      else {
        throw new FormatException(
          "Unsupported sample bit width: '" + bitsPerSample + "'");
      }
    }
    // We've got a big-endian file with a big-endian byte array.
    return byteArray;
  }

/** Obtains the specified image from the given TIFF file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    return TiffTools.getImage(ifds[no], in, 0, ignoreColorTable);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given TIFF file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    channelMinMax = null;
    in = new RandomAccessStream(id);
    if (in.readShort() == 0x4949) in.order(true);

    ifds = TiffTools.getIFDs(in);
    if (ifds == null) throw new FormatException("No IFDs found");
    numImages = ifds.length;
    initMetadata();
  }

  // -- Helper methods --

  /**
   * Performs the actual setting of the pixels attributes in the active
   * metadata store by calling:
   *
   * <ul>
   *   <li>{@link #getSizeX()}</li>
   *   <li>{@link #getSizeY()}</li>
   *   <li>{@link #getSizeZ()}</li>
   *   <li>{@link #getSizeC()}</li>
   *   <li>{@link #getSizeT()}</li>
   *   <li>{@link #getPixelType()}</li>
   *   <li>{@link #getDimensionOrder()}</li>
   *   <li>{@link #getBigEndian()}</li>
   * </ul>
   *
   * If the retrieval of any of these attributes is non-standard, the sub-class
   * should override the corresponding method.
   * @throws FormatException if there is a problem parsing any of the
   * attributes.
   */
  private void setPixels() throws FormatException, IOException {
    getMetadataStore(currentId).setPixels(
      new Integer(getSizeX(currentId)), new Integer(getSizeY(currentId)),
      new Integer(getSizeZ(currentId)), new Integer(getSizeC(currentId)),
      new Integer(getSizeT(currentId)), new Integer(getPixelType(currentId)),
      getBigEndian(), getDimensionOrder(currentId), null);
  }

  /**
   * Performs the actual setting of the image attributes in the active metadata
   * store by calling:
   *
   * <ul>
   *   <li>{@link #getImageName()}</li>
   *   <li>{@link #getImageCreationDate()}</li>
   *   <li>{@link #getgetImageDescription()}</li>
   * </ul>
   *
   * If the retrieval of any of these attributes is non-standard, the sub-class
   * should override the corresponding method.
   * @throws FormatException if there is a problem parsing any of the
   * attributes.
   */
  private void setImage() throws FormatException, IOException {
    getMetadataStore(currentId).setImage(getImageName(),
        getImageCreationDate(), getImageDescription(), null);
  }

  /**
   * Sets the channels global min and max in the metadata store.
   * @param channelIdx the channel to set.
   * @throws FormatException if there is an error parsing metadata.
   * @throws IOException if there is an error reading the file.
   */
  protected void setChannelGlobalMinMax(int channelIdx)
    throws FormatException, IOException
  {
    getChannelGlobalMinMax();
    getMetadataStore(currentId).setChannelGlobalMinMax(channelIdx,
        channelMinMax[channelIdx][MIN], channelMinMax[channelIdx][MAX], null);
  }

  /**
   * Retrieves the global min and max for each channel.
   * @throws FormatException if there is an error parsing metadata.
   * @throws IOException if there is an error reading the file.
   */
  public void getChannelGlobalMinMax() throws FormatException, IOException {
    if (channelMinMax == null) {
      channelMinMax = new Double[getSizeC(currentId)][2];
    }
    else return;

    for (int c = 0; c < getSizeC(currentId); c++) {
      double min = Double.MAX_VALUE;
      double max = Double.MIN_VALUE;
      for (int t = 0; t < getSizeT(currentId); t++) {
        for (int z = 0; z < getSizeZ(currentId); z++) {
          int index = getIndex(currentId, z, c, t);
          Plane2D plane = openPlane2D(currentId, index);
          for (int x = 0; x < getSizeX(currentId); x++) {
            for (int y = 0; y < getSizeY(currentId); y++) {
              double pixelValue = plane.getPixelValue(x, y);
              if (pixelValue < min) min = pixelValue;
              if (pixelValue > max) max = pixelValue;
            }
          }
        }
      }
      channelMinMax[c][MIN] = new Double(min);
      channelMinMax[c][MAX] = new Double(max);
    }
  }

  /**
   * Sets the logical channel in the metadata store.
   * @param i the logical channel number.
   * @throws FormatException if there is an error parsing metadata.
   * @throws IOException if there is an error reading the file.
   */
  private void setLogicalChannel(int i) throws FormatException, IOException {
    getMetadataStore(currentId).setLogicalChannel(
      i,
      getChannelName(i),
      getNdFilter(i),
      getEmWave(i),
      getExWave(i),
      getPhotometricInterpretation(i),
      getMode(i), // aquisition mode
      null);
  }

  private String getChannelName(int i) { return null; }

  private Float getNdFilter(int i) { return null; }

  Integer getEmWave(int i) { return null; }

  private Integer getExWave(int i) { return null; }

  private String getPhotometricInterpretation(int i)
    throws FormatException, IOException
  {
    return (String) getMetadataValue(currentId,
      "metaDataPhotometricInterpretation");
  }

  private String getMode(int i) { return null; }

  protected void put(String key, Object value) {
    if (value == null) return;
    if (value instanceof String) value = ((String) value).trim();
    metadata.put(key, value);
  }

  protected void put(String key, int value) {
    if (value == -1) return; // indicates missing value
    metadata.put(key, new Integer(value));
  }

  protected void put(String key, boolean value) {
    put(key, new Boolean(value));
  }
  protected void put(String key, byte value) { put(key, new Byte(value)); }
  protected void put(String key, char value) {
    put(key, new Character(value));
  }
  protected void put(String key, double value) { put(key, new Double(value)); }
  protected void put(String key, float value) { put(key, new Float(value)); }
  protected void put(String key, long value) { put(key, new Long(value)); }
  protected void put(String key, short value) { put(key, new Short(value)); }

  protected void put(String key, Hashtable ifd, int tag) {
    put(key, TiffTools.getIFDValue(ifd, tag));
  }

  protected void putInt(String key, Hashtable ifd, int tag) {
    put(key, TiffTools.getIFDIntValue(ifd, tag));
  }
}
