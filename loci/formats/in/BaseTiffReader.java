//
// BaseTiffReader.java
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

package loci.formats.in;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.text.*;
import java.util.*;
import loci.formats.*;

/**
 * BaseTiffReader is the superclass for file format readers compatible with
 * or derived from the TIFF 6.0 file format.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/BaseTiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/BaseTiffReader.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public abstract class BaseTiffReader extends FormatReader {

  // -- Fields --

  /** List of IFDs for the current TIFF. */
  protected Hashtable[] ifds;

  // -- Constructors --

  /** Constructs a new BaseTiffReader. */
  public BaseTiffReader(String name, String suffix) { super(name, suffix); }

  /** Constructs a new BaseTiffReader. */
  public BaseTiffReader(String name, String[] suffixes) {
    super(name, suffixes);
  }

  // -- BaseTiffReader API methods --

  /** Gets the dimensions of the given (possibly multi-page) TIFF file. */
  public int[] getTiffDimensions() throws FormatException, IOException {
    if (ifds == null || ifds.length == 0) return null;
    return new int[] {
      TiffTools.getIFDIntValue(ifds[0], TiffTools.IMAGE_WIDTH, false, -1),
      TiffTools.getIFDIntValue(ifds[0], TiffTools.IMAGE_LENGTH, false, -1),
      core.imageCount[0]
    };
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return TiffTools.isValidHeader(block);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    int[] bits = TiffTools.getBitsPerSample(ifds[0]);
    if (bits[0] <= 8) {
      int[] colorMap =
        (int[]) TiffTools.getIFDValue(ifds[0], TiffTools.COLOR_MAP);
      if (colorMap == null) return null;

      byte[][] table = new byte[3][colorMap.length / 3];
      int next = 0;
      for (int j=0; j<table.length; j++) {
        for (int i=0; i<table[0].length; i++) {
          if (isLittleEndian()) {
            int n = colorMap[next++];
            if ((n & 0xffff) > 255) table[j][i] = (byte) ((n & 0xff00) >> 8);
            else table[j][i] = (byte) (n & 0xff);
          }
          else table[j][i] = (byte) ((colorMap[next++] & 0xff00) >> 8);
        }
      }

      return table;
    }
    return null;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    int[] bits = TiffTools.getBitsPerSample(ifds[0]);
    if (bits[0] <= 16 && bits[0] > 8) {
      int[] colorMap =
        (int[]) TiffTools.getIFDValue(ifds[0], TiffTools.COLOR_MAP);
      if (colorMap == null) return null;
      short[][] table = new short[3][colorMap.length / 3];
      int next = 0;
      for (int i=0; i<table.length; i++) {
        for (int j=0; j<table[0].length; j++) {
          if (core.littleEndian[0]) {
            table[i][j] = (short) (colorMap[next++] & 0xffff);
          }
          else {
            int n = colorMap[next++];
            table[i][j] =
              (short) (((n & 0xff0000) >> 8) | ((n & 0xff000000) >> 24));
          }
        }
      }
      return table;
    }
    return null;
  }

  /* @see loci.formats.IFormatReader#getMetadataValue(String) */
  public Object getMetadataValue(String field) {
    FormatTools.assertId(currentId, true, 1);
    return getMeta(field);
  }

  /* @see loci.formats.FormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    TiffTools.getSamples(ifds[no], in, buf);
    return swapIfRequired(buf);
  }

  // -- Internal BaseTiffReader API methods --

  /** Populates the metadata hashtable and metadata store. */
  protected void initMetadata() throws FormatException, IOException {
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
  protected void initStandardMetadata() throws FormatException, IOException {
    Hashtable ifd = ifds[0];
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
    put("Instrument Make", ifd, TiffTools.MAKE);
    put("Instrument Model", ifd, TiffTools.MODEL);
    put("Document Name", ifd, TiffTools.DOCUMENT_NAME);
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
    if (p == TiffTools.RGB_PALETTE || p == TiffTools.CFA_ARRAY) {
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

    int samples = TiffTools.getIFDIntValue(ifds[0],
      TiffTools.SAMPLES_PER_PIXEL, false, 1);
    core.rgb[0] = samples > 1 || p == TiffTools.RGB_PALETTE ||
      p == TiffTools.CFA_ARRAY || p == TiffTools.RGB;
    core.interleaved[0] = false;
    core.littleEndian[0] = TiffTools.isLittleEndian(ifds[0]);

    core.sizeX[0] =
      TiffTools.getIFDIntValue(ifds[0], TiffTools.IMAGE_WIDTH, false, 0);
    core.sizeY[0] =
      TiffTools.getIFDIntValue(ifds[0], TiffTools.IMAGE_LENGTH, false, 0);
    core.sizeZ[0] = 1;
    core.sizeC[0] = core.rgb[0] ? samples : 1;
    core.sizeT[0] = ifds.length;
    core.metadataComplete[0] = true;
    core.indexed[0] = TiffTools.getIFDIntValue(ifds[0],
      TiffTools.PHOTOMETRIC_INTERPRETATION) == TiffTools.RGB_PALETTE;
    core.falseColor[0] = false;

    int bitFormat = TiffTools.getIFDIntValue(ifds[0],
      TiffTools.SAMPLE_FORMAT);

    while (bps % 8 != 0) bps++;
    if (bps == 24 || bps == 48) bps /= 3;

    if (bitFormat == 3) core.pixelType[0] = FormatTools.FLOAT;
    else if (bitFormat == 2) {
      switch (bps) {
        case 16:
          core.pixelType[0] = FormatTools.INT16;
          break;
        case 32:
          core.pixelType[0] = FormatTools.INT32;
          break;
        default:
          core.pixelType[0] = FormatTools.UINT8;
      }
    }
    else {
      switch (bps) {
        case 16:
          core.pixelType[0] = FormatTools.UINT16;
          break;
        case 32:
          core.pixelType[0] = FormatTools.UINT32;
          break;
        default:
          core.pixelType[0] = FormatTools.UINT8;
      }
    }

    core.currentOrder[0] = "XYCZT";
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
      // the metadata store we're working with
      MetadataStore store = getMetadataStore();

      // set the pixel values in the metadata store
      FormatTools.populatePixels(store, this);

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

      // format the creation date to ISO 8061

      String creationDate = getImageCreationDate();
      try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat parse = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        Date date = parse.parse(creationDate, new ParsePosition(0));
        creationDate = sdf.format(date);
      }
      catch (NullPointerException e) {
        try {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
          SimpleDateFormat parse =
            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
          Date date = parse.parse(creationDate, new ParsePosition(0));
          creationDate = sdf.format(date);
        }
        catch (NullPointerException exc) {
          if (debug) trace(exc);
          creationDate = null;
        }
      }

      // populate Image element

      store.setImage(getImageName(),
        creationDate, getImageDescription(), null);

      // populate Logical Channel elements
      for (int i=0; i<getSizeC(); i++) {
        try {
          setLogicalChannel(i);
        }
        catch (FormatException exc) {
          if (debug) trace(exc);
        }
        catch (IOException exc) {
          if (debug) trace(exc);
        }
      }

      // set the X and Y pixel dimensions

      int resolutionUnit = TiffTools.getIFDIntValue(ifd,
        TiffTools.RESOLUTION_UNIT);
      TiffRational xResolution = TiffTools.getIFDRationalValue(ifd,
        TiffTools.X_RESOLUTION, false);
      TiffRational yResolution = TiffTools.getIFDRationalValue(ifd,
        TiffTools.Y_RESOLUTION, false);
      float pixX = xResolution == null ? 0f : xResolution.floatValue();
      float pixY = yResolution == null ? 0f : yResolution.floatValue();

      switch (resolutionUnit) {
        case 2:
          // resolution is expressed in pixels per inch
          pixX *= 0.0254;
          pixY *= 0.0254;
          break;
        case 3:
          // resolution is expressed in pixels per centimeter
          pixX /= 100;
          pixY /= 100;
          break;
      }

      store.setDimensions(new Float(pixX), new Float(pixY), null,
        null, null, null);

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
      if (stageX != null || stageY != null) {
        store.setStageLabel(null, stageX, stageY, null, null);
      }

      // populate Instrument element
      String model = (String) TiffTools.getIFDValue(ifd, TiffTools.MODEL);
      String serialNumber = (String)
        TiffTools.getIFDValue(ifd, TiffTools.MAKE);
      store.setInstrument(null, model, serialNumber, null, null);
    }
    catch (FormatException exc) { trace(exc); }
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
    Object o = TiffTools.getIFDValue(ifds[0], TiffTools.DATE_TIME);
    if (o instanceof String) return (String) o;
    if (o instanceof String[]) return ((String[]) o)[0];
    return null;
  }

  /**
   * Retrieves the image description.
   * @return the image description.
   */
  protected String getImageDescription() {
    return (String) getMeta("Comment");
  }

  /**
   * Examines a byte array to see if it needs to be byte swapped and modifies
   * the byte array directly.
   * @param byteArray The byte array to check and modify if required.
   * @return the <i>byteArray</i> either swapped or not for convenience.
   * @throws IOException if there is an error read from the file.
   * @throws FormatException if there is an error during metadata parsing.
   */
  protected byte[] swapIfRequired(byte[] byteArray)
    throws FormatException, IOException
  {
    int bitsPerSample = TiffTools.getBitsPerSample(ifds[0])[0];

    // We've got nothing to do if the samples are only 8-bits wide or if they
    // are floating point.
    if (bitsPerSample == 8 || bitsPerSample == 32) return byteArray;

    if (isLittleEndian()) {
      if (bitsPerSample == 16) { // short
        ShortBuffer buf = ByteBuffer.wrap(byteArray).asShortBuffer();
        for (int i = 0; i < (byteArray.length / 2); i++) {
          buf.put(i, DataTools.swap(buf.get(i)));
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

  // -- Internal FormatReader API methods - metadata convenience --

  protected void put(String key, Object value) {
    if (value == null) return;
    if (value instanceof String) value = ((String) value).trim();
    addMeta(key, value);
  }

  protected void put(String key, int value) {
    if (value == -1) return; // indicates missing value
    addMeta(key, new Integer(value));
  }

  protected void put(String key, boolean value) {
    put(key, new Boolean(value));
  }
  protected void put(String key, byte value) { put(key, new Byte(value)); }
  protected void put(String key, char value) { put(key, new Character(value)); }
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

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("BaseTiffReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);
    in.order(in.readShort() == 0x4949);

    status("Reading IFDs");

    ifds = TiffTools.getIFDs(in);
    if (ifds == null) throw new FormatException("No IFDs found");

    status("Populating metadata");

    core.imageCount[0] = ifds.length;
    initMetadata();
  }

  // -- Helper methods --

  /**
   * Sets the logical channel in the metadata store.
   * @param i the logical channel number.
   * @throws FormatException if there is an error parsing metadata.
   * @throws IOException if there is an error reading the file.
   */
  private void setLogicalChannel(int i) throws FormatException, IOException {
    getMetadataStore().setLogicalChannel(i, getChannelName(i), null, null, null,
      null, null, null, null, null, null, null, null,
      getPhotometricInterpretation(i), getMode(i), null, null, null, null, null,
      getEmWave(i), getExWave(i), null, getNdFilter(i), null);
  }

  private String getChannelName(int i) { return null; }

  private Float getNdFilter(int i) { return null; }

  private Integer getEmWave(int i) { return null; }

  private Integer getExWave(int i) { return null; }

  private String getPhotometricInterpretation(int i)
    throws FormatException, IOException
  {
    return (String) getMetadataValue("metaDataPhotometricInterpretation");
  }

  private String getMode(int i) { return null; }

}
