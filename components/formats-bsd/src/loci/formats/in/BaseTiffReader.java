/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.DateTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffRational;

import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Time;
import ome.units.quantity.Length;
import ome.units.UNITS;

/**
 * BaseTiffReader is the superclass for file format readers compatible with
 * or derived from the TIFF 6.0 file format.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public abstract class BaseTiffReader extends MinimalTiffReader {

  // -- Constants --

  /** Logger for this class. */
  protected static final Logger LOGGER =
    LoggerFactory.getLogger(BaseTiffReader.class);

  public static final String[] DATE_FORMATS = {
    "yyyy:MM:dd HH:mm:ss",
    "dd/MM/yyyy HH:mm:ss",
    "MM/dd/yyyy hh:mm:ss aa",
    "yyyyMMdd HH:mm:ss",
    "yyyy/MM/dd HH:mm:ss",
    "yyyy-MM-dd'T'HH:mm:ssZ"
  };

  // -- Constructors --

  /** Constructs a new BaseTiffReader. */
  public BaseTiffReader(String name, String suffix) { super(name, suffix); }

  /** Constructs a new BaseTiffReader. */
  public BaseTiffReader(String name, String[] suffixes) {
    super(name, suffixes);
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
    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
      return;
    }

    for (int i=0; i<ifds.size(); i++) {
      put("PageName #" + i, ifds.get(i), IFD.PAGE_NAME);
    }

    IFD firstIFD = ifds.get(0);
    put("ImageWidth", firstIFD, IFD.IMAGE_WIDTH);
    put("ImageLength", firstIFD, IFD.IMAGE_LENGTH);
    put("BitsPerSample", firstIFD, IFD.BITS_PER_SAMPLE);

    // retrieve EXIF values, if available

    if (ifds.get(0).containsKey(IFD.EXIF)) {
      IFDList exifIFDs = tiffParser.getExifIFDs();
      if (exifIFDs.size() > 0) {
        IFD exif = exifIFDs.get(0);
        tiffParser.fillInIFD(exif);
        for (Integer key : exif.keySet()) {
          int k = key.intValue();
          addGlobalMeta(getExifTagName(k), exif.get(key));
        }
      }
    }

    TiffCompression comp = firstIFD.getCompression();
    put("Compression", comp.getCodecName());

    PhotoInterp photo = firstIFD.getPhotometricInterpretation();
    String photoInterp = photo.getName();
    String metaDataPhotoInterp = photo.getMetadataType();
    put("PhotometricInterpretation", photoInterp);
    put("MetaDataPhotometricInterpretation", metaDataPhotoInterp);

    putInt("CellWidth", firstIFD, IFD.CELL_WIDTH);
    putInt("CellLength", firstIFD, IFD.CELL_LENGTH);

    int or = firstIFD.getIFDIntValue(IFD.ORIENTATION);

    // adjust the width and height if necessary
    if (or == 8) {
      put("ImageWidth", firstIFD, IFD.IMAGE_LENGTH);
      put("ImageLength", firstIFD, IFD.IMAGE_WIDTH);
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
    putInt("SamplesPerPixel", firstIFD, IFD.SAMPLES_PER_PIXEL);

    put("Software", firstIFD, IFD.SOFTWARE);
    put("Instrument Make", firstIFD, IFD.MAKE);
    put("Instrument Model", firstIFD, IFD.MODEL);
    put("Document Name", firstIFD, IFD.DOCUMENT_NAME);
    put("DateTime", getImageCreationDate());
    put("Artist", firstIFD, IFD.ARTIST);

    put("HostComputer", firstIFD, IFD.HOST_COMPUTER);
    put("Copyright", firstIFD, IFD.COPYRIGHT);

    put("NewSubfileType", firstIFD, IFD.NEW_SUBFILE_TYPE);

    int thresh = firstIFD.getIFDIntValue(IFD.THRESHHOLDING);
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

    int fill = firstIFD.getIFDIntValue(IFD.FILL_ORDER);
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

    putInt("Make", firstIFD, IFD.MAKE);
    putInt("Model", firstIFD, IFD.MODEL);
    putInt("MinSampleValue", firstIFD, IFD.MIN_SAMPLE_VALUE);
    putInt("MaxSampleValue", firstIFD, IFD.MAX_SAMPLE_VALUE);

    putDouble("XResolution", firstIFD, IFD.X_RESOLUTION);
    putDouble("YResolution", firstIFD, IFD.Y_RESOLUTION);

    int planar = firstIFD.getIFDIntValue(IFD.PLANAR_CONFIGURATION);
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

    putDouble("XPosition", firstIFD, IFD.X_POSITION);
    putDouble("YPosition", firstIFD, IFD.Y_POSITION);
    putInt("FreeOffsets", firstIFD, IFD.FREE_OFFSETS);
    putInt("FreeByteCounts", firstIFD, IFD.FREE_BYTE_COUNTS);
    putInt("GrayResponseUnit", firstIFD, IFD.GRAY_RESPONSE_UNIT);
    putInt("GrayResponseCurve", firstIFD, IFD.GRAY_RESPONSE_CURVE);
    putInt("T4Options", firstIFD, IFD.T4_OPTIONS);
    putInt("T6Options", firstIFD, IFD.T6_OPTIONS);

    int res = firstIFD.getIFDIntValue(IFD.RESOLUTION_UNIT);
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

    putString("PageNumber", firstIFD, IFD.PAGE_NUMBER);
    putInt("TransferFunction", firstIFD, IFD.TRANSFER_FUNCTION);

    int predict = firstIFD.getIFDIntValue(IFD.PREDICTOR);
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

    putInt("WhitePoint", firstIFD, IFD.WHITE_POINT);
    putInt("PrimaryChromacities", firstIFD, IFD.PRIMARY_CHROMATICITIES);

    putInt("HalftoneHints", firstIFD, IFD.HALFTONE_HINTS);
    putInt("TileWidth", firstIFD, IFD.TILE_WIDTH);
    putInt("TileLength", firstIFD, IFD.TILE_LENGTH);
    putInt("TileOffsets", firstIFD, IFD.TILE_OFFSETS);
    putInt("TileByteCounts", firstIFD, IFD.TILE_BYTE_COUNTS);

    int ink = firstIFD.getIFDIntValue(IFD.INK_SET);
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

    putInt("InkNames", firstIFD, IFD.INK_NAMES);
    putInt("NumberOfInks", firstIFD, IFD.NUMBER_OF_INKS);
    putInt("DotRange", firstIFD, IFD.DOT_RANGE);
    put("TargetPrinter", firstIFD, IFD.TARGET_PRINTER);
    putInt("ExtraSamples", firstIFD, IFD.EXTRA_SAMPLES);

    int fmt = firstIFD.getIFDIntValue(IFD.SAMPLE_FORMAT);
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

    putInt("SMinSampleValue", firstIFD, IFD.S_MIN_SAMPLE_VALUE);
    putInt("SMaxSampleValue", firstIFD, IFD.S_MAX_SAMPLE_VALUE);
    putInt("TransferRange", firstIFD, IFD.TRANSFER_RANGE);

    int jpeg = firstIFD.getIFDIntValue(IFD.JPEG_PROC);
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

    putInt("JPEGInterchangeFormat", firstIFD, IFD.JPEG_INTERCHANGE_FORMAT);
    putInt("JPEGRestartInterval", firstIFD, IFD.JPEG_RESTART_INTERVAL);

    putInt("JPEGLosslessPredictors", firstIFD, IFD.JPEG_LOSSLESS_PREDICTORS);
    putInt("JPEGPointTransforms", firstIFD, IFD.JPEG_POINT_TRANSFORMS);
    putInt("JPEGQTables", firstIFD, IFD.JPEG_Q_TABLES);
    putInt("JPEGDCTables", firstIFD, IFD.JPEG_DC_TABLES);
    putInt("JPEGACTables", firstIFD, IFD.JPEG_AC_TABLES);
    putInt("YCbCrCoefficients", firstIFD, IFD.Y_CB_CR_COEFFICIENTS);

    int ycbcr = firstIFD.getIFDIntValue(IFD.Y_CB_CR_SUB_SAMPLING);
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

    putInt("YCbCrPositioning", firstIFD, IFD.Y_CB_CR_POSITIONING);
    putInt("ReferenceBlackWhite", firstIFD, IFD.REFERENCE_BLACK_WHITE);

    // bits per sample and number of channels
    int[] q = firstIFD.getBitsPerSample();
    int bps = q[0];
    int numC = q.length;

    // numC isn't set properly if we have an indexed color image, so we need
    // to reset it here

    if (photo == PhotoInterp.RGB_PALETTE || photo == PhotoInterp.CFA_ARRAY) {
      numC = 3;
    }

    put("BitsPerSample", bps);
    put("NumberOfChannels", numC);
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
  protected void initMetadataStore() throws FormatException {
    LOGGER.info("Populating OME metadata");

    // the metadata store we're working with
    MetadataStore store = makeFilterMetadata();

    IFD firstIFD = ifds.get(0);
    IFD exif = null;

    if (ifds.get(0).containsKey(IFD.EXIF)) {
      try {
        IFDList exifIFDs = tiffParser.getExifIFDs();
        if (exifIFDs.size() > 0) {
          exif = exifIFDs.get(0);
        }
        tiffParser.fillInIFD(exif);
      }
      catch (IOException e) {
        LOGGER.debug("Could not read EXIF IFDs", e);
      }
    }

    MetadataTools.populatePixels(store, this, exif != null);

    // format the creation date to ISO 8601

    String creationDate = getImageCreationDate();
    String date = DateTools.formatDate(creationDate, DATE_FORMATS, ".");
    if (creationDate != null && date == null) {
      LOGGER.warn("unknown creation date format: {}", creationDate);
    }
    creationDate = date;

    // populate Image

    if (creationDate != null) {
      store.setImageAcquisitionDate(new Timestamp(creationDate), 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // populate Experimenter
      String artist = firstIFD.getIFDTextValue(IFD.ARTIST);

      if (artist != null) {
        String firstName = null, lastName = null;
        int ndx = artist.indexOf(' ');
        if (ndx < 0) lastName = artist;
        else {
          firstName = artist.substring(0, ndx);
          lastName = artist.substring(ndx + 1);
        }
        String email = firstIFD.getIFDStringValue(IFD.HOST_COMPUTER);
        store.setExperimenterFirstName(firstName, 0);
        store.setExperimenterLastName(lastName, 0);
        store.setExperimenterEmail(email, 0);
        store.setExperimenterID(MetadataTools.createLSID("Experimenter", 0), 0);
      }

      store.setImageDescription(firstIFD.getComment(), 0);

      // set the X and Y pixel dimensions

      double pixX = firstIFD.getXResolution();
      double pixY = firstIFD.getYResolution();

      // only look for a unit in the comment if the unit is unclear
      // from the RESOLUTION_UNIT tag
      String unit = null;
      if (firstIFD.getResolutionMultiplier() == 1) {
        unit = getResolutionUnitFromComment(firstIFD);
      }

      Length sizeX = FormatTools.getPhysicalSizeX(pixX, unit);
      Length sizeY = FormatTools.getPhysicalSizeY(pixY, unit);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
      store.setPixelsPhysicalSizeZ(null, 0);

      if (exif != null) {
        if (exif.containsKey(IFD.EXPOSURE_TIME)) {
          Object exp = exif.get(IFD.EXPOSURE_TIME);
          if (exp instanceof TiffRational) {
            Time exposure = new Time(((TiffRational) exp).doubleValue(), UNITS.SECOND);
            for (int i=0; i<getImageCount(); i++) {
              store.setPlaneExposureTime(exposure, 0, i);
            }
          }
        }
      }
    }
  }
  
  /**
   * Extracts the resolution unit symbol from the comment field
   * 
   * @param ifd
   *          The {@link IFD}
   * @return The unit symbol or <code>null</code> if the information is not
   *         available
   */
  private String getResolutionUnitFromComment(IFD ifd) {
    String comment = ifd.getComment();
    if (comment != null && comment.trim().length() > 0) {
      String p = "(.*)unit=(\\w+)(.*)";
      Pattern pattern = Pattern.compile(p, Pattern.DOTALL);
      Matcher m = pattern.matcher(comment);
      if (m.matches()) {
        return m.group(2);
      }
    }
    return null;
  }

  /**
   * Retrieves the image creation date.
   * @return the image creation date.
   */
  protected String getImageCreationDate() {
    Object o = ifds.get(0).getIFDValue(IFD.DATE_TIME);
    if (o instanceof String) return (String) o;
    if (o instanceof String[] && ((String[]) o).length > 0) return ((String[]) o)[0];
    return null;
  }

  // -- Internal FormatReader API methods - metadata convenience --

  // TODO : the 'put' methods that accept primitive types could probably be
  // removed, as there are now 'addGlobalMeta' methods that accept
  // primitive types

  protected void put(String key, Object value) {
    if (value == null) return;
    if (value instanceof String) value = ((String) value).trim();
    addGlobalMeta(key, value);
  }

  protected void put(String key, int value) {
    if (value == -1) return; // indicates missing value
    addGlobalMeta(key, value);
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

  protected void put(String key, IFD ifd, int tag) {
    put(key, ifd.getIFDValue(tag));
  }

  protected void putInt(String key, IFD ifd, int tag) {
    put(key, ifd.getIFDIntValue(tag));
  }

  protected void putString(String key, IFD ifd, int tag) {
    String value = "";
    try {
      value = ifd.getIFDStringValue(tag);
    } catch (FormatException e) {
    }
    put(key, value);
  }

  protected void putDouble(String key, IFD ifd, int tag) {
    if (ifd.getIFDValue(tag) instanceof Number) {
      Number number = (Number) ifd.getIFDValue(tag);
      if (number != null) {
        put(key, number.doubleValue());
      }
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    initMetadata();
  }

  // -- Helper methods --

  public static String getExifTagName(int tag) {
    return IFD.getIFDTagName(tag);
  }

}
