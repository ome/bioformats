/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2026 Open Microscopy Environment:
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

package loci.formats.out;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import loci.common.DateTools;
import loci.common.Location;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.codec.CodecOptions;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.MetadataOptions;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffRational;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.xml.model.primitives.Timestamp;

/**
 * Writes a narrow brightfield RGB subset of Hamamatsu NDPI.
 *
 * Each native pyramid resolution is supplied from largest to smallest as
 * full-width, top-to-bottom row blocks.
 *
 * Output follows the physical layout genuine Hamamatsu files use: the
 * header, the metadata every directory shares, then for each image its
 * external values, its payload, its restart index where it has one, and its
 * completed directory. The header's first-IFD pointer and each directory's
 * next-IFD pointer are filled in with full 64-bit offsets once the directory
 * they name exists.
 */
public class NDPIWriter extends FormatWriter {

  // -- Constants --

  /** Option for the free-text slide reference (ASCII). */
  public static final String REFERENCE_KEY = "ndpi.reference";
  /** Option for the image-center X position in nanometers. */
  public static final String X_POSITION_KEY = "ndpi.position_x";
  /** Option for the image-center Y position in nanometers. */
  public static final String Y_POSITION_KEY = "ndpi.position_y";
  /** Option for the image-center Z position in nanometers. */
  public static final String Z_POSITION_KEY = "ndpi.position_z";
  /** Option for the exposure ratio. */
  public static final String EXPOSURE_RATIO_KEY = "ndpi.exposure_ratio";
  /** Option for the red gain multiplier. */
  public static final String RED_MULTIPLIER_KEY = "ndpi.red_multiplier";
  /** Option for the green gain multiplier. */
  public static final String GREEN_MULTIPLIER_KEY = "ndpi.green_multiplier";
  /** Option for the blue gain multiplier. */
  public static final String BLUE_MULTIPLIER_KEY = "ndpi.blue_multiplier";
  /** Option for comma-separated signed X/Y/Z focus-point triplets. */
  public static final String FOCUS_POINTS_KEY = "ndpi.focus_points";
  /** Option for comma-separated signed focus-point region mappings. */
  public static final String FOCUS_POINT_REGIONS_KEY =
    "ndpi.focus_point_regions";
  /** Option for the capture mode; the RGB writer accepts brightfield (0). */
  public static final String CAPTURE_MODE_KEY = "ndpi.capture_mode";
  /** Option for the scanner serial number (ASCII). */
  public static final String SERIAL_NUMBER_KEY = "ndpi.scanner_serial_number";
  /** Option for the scanner manufacturer used as TIFF Make (ASCII). */
  public static final String SCANNER_MANUFACTURER_KEY =
    "ndpi.scanner_manufacturer";
  /** Option for the scanner model used as TIFF Model (ASCII). */
  public static final String SCANNER_MODEL_KEY = "ndpi.scanner_model";
  /** Option for the refocus interval in minutes. */
  public static final String REFOCUS_INTERVAL_KEY = "ndpi.refocus_interval";
  /** Option for the focus offset in nanometers. */
  public static final String FOCUS_OFFSET_KEY = "ndpi.focus_offset";
  /** Option for the scanner firmware version (ASCII). */
  public static final String FIRMWARE_VERSION_KEY = "ndpi.firmware_version";
  /** Option for CRLF-separated scanner calibration properties. */
  public static final String CALIBRATION_KEY = "ndpi.calibration";
  /** Option for the emission wavelength in nanometers. */
  public static final String WAVELENGTH_KEY = "ndpi.wavelength";
  /** Option for the lamp age in hours. */
  public static final String LAMP_AGE_KEY = "ndpi.lamp_age";
  /** Option for the exposure time in microseconds. */
  public static final String EXPOSURE_TIME_KEY = "ndpi.exposure_time";
  /** Option for the focus duration in seconds. */
  public static final String FOCUS_TIME_KEY = "ndpi.focus_time";
  /** Option for the scan duration in seconds. */
  public static final String SCAN_TIME_KEY = "ndpi.scan_time";
  /** Option for the file-write duration in seconds. */
  public static final String WRITE_TIME_KEY = "ndpi.write_time";
  /** Option for the fully automatic focus flag. */
  public static final String FULLY_AUTO_FOCUS_KEY =
    "ndpi.fully_automatic_focus";
  /** Prefix for barcode options numbered zero through seven (ASCII). */
  public static final String BARCODE_KEY_PREFIX = "ndpi.barcode.";
  /** Option for the medical-regulation string (ASCII). */
  public static final String MEDICAL_REGULATION_KEY =
    "ndpi.medical_regulation";
  /** Option overriding the base JPEG restart interval, in MCUs. */
  public static final String RESTART_MCUS_KEY = "ndpi.restart_mcus";
  /** Option identifying the optional macro/overview image series. */
  public static final String MACRO_SERIES_KEY = "ndpi.macro_series";
  /** Option identifying the optional single-channel tissue-map series. */
  public static final String TISSUE_MAP_SERIES_KEY =
    "ndpi.tissue_map_series";
  /** Macro-only option indicating whether its label region is obscured. */
  public static final String LABEL_OBSCURED_KEY = "ndpi.label_obscured";

  private static final int CHANNELS = 3;
  private static final double DEFAULT_JPEG_QUALITY = 0.8;
  private static final int[] NOMINAL_RESTART_MCUS = {512, 480, 256, 240};
  private static final int MINIMUM_MCU_STARTS = 44;
  private static final String TIFF_DATE_FORMAT = "yyyy:MM:dd HH:mm:ss";

  /** Fixed AuthCode restart-interval index and entropy-byte offset. */
  private static final int AUTH_FIXED_OFFSET = 42;
  /** Sample selector requesting AUTH_FIXED_OFFSET. */
  private static final int AUTH_FIXED_SELECTOR = -1;
  /** Constant key the sampled AuthCode bytes are combined with. */
  private static final int AUTH_KEY = 0x1d6edb2a;
  /**
   * The four AuthCode samples, in the order they are combined. Each row is
   * {restart-interval selector, entropy-byte selector}. AUTH_FIXED_SELECTOR
   * means the fixed offset 42; any other value is a percentage, of the
   * restart-interval count for the first column and of the selected
   * interval's entropy length for the second.
   */
  private static final int[][] AUTH_SAMPLES = {
    {AUTH_FIXED_SELECTOR, 72},
    {72, AUTH_FIXED_SELECTOR},
    {75, 80},
    {80, 75}
  };

  // -- Fields --

  private NDPIClassicTiffWriter saver;
  /** Prepared entries every directory of this file repeats. */
  private final Map<Integer, NDPIClassicTiffWriter.Entry> sharedEntries =
    new LinkedHashMap<Integer, NDPIClassicTiffWriter.Entry>();
  /** Shared external values already written, keyed by their emitted bytes. */
  private final Map<String, NDPIClassicTiffWriter.Entry> sharedValues =
    new HashMap<String, NDPIClassicTiffWriter.Entry>();
  /** Prepared entries specific to the image currently being written. */
  private final Map<Integer, NDPIClassicTiffWriter.Entry> imageEntries =
    new LinkedHashMap<Integer, NDPIClassicTiffWriter.Entry>();
  /** Where the pointer to the next completed directory belongs. */
  private long nextIFDPointer;
  private boolean imageStarted;
  private NDPIJPEGAssembler assembler;
  private int activeResolution = -1;
  private int nextResolution;
  private int nextRow;
  private boolean chainComplete;
  private Integer macroSeries;
  private Integer tissueMapSeries;
  private Boolean labelObscured;
  private int expectedSeries;
  private long rawOffset;
  private long rawLength;
  private String reference;
  private Long xPosition;
  private Long yPosition;
  private Long zPosition;
  private Long exposureRatio;
  private Long redMultiplier;
  private Long greenMultiplier;
  private Long blueMultiplier;
  private int[] focusPoints;
  private int[] focusPointRegions;
  private String scannerSerialNumber;
  private String scannerManufacturer;
  private String scannerModel;
  private Long refocusInterval;
  private Long focusOffset;
  private String firmwareVersion;
  private String calibration;
  private Float wavelength;
  private Long lampAge;
  private Long exposureTime;
  private Long focusTime;
  private Long scanTime;
  private Long writeTime;
  private Boolean fullyAutomaticFocus;
  private final String[] barcodes =
    new String[NDPITags.LAST_BARCODE - NDPITags.FIRST_BARCODE + 1];
  private String medicalRegulation;
  private Integer restartMCUsOverride;
  private double jpegQuality;
  private int[] pyramidRestartMCUs;
  private boolean[] indexedPyramidLevels;

  // -- Constructor --

  public NDPIWriter() {
    super("Hamamatsu NDPI", "ndpi");
    compressionTypes = new String[] {"JPEG"};
    compression = compressionTypes[0];
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  @Override
  public int[] getPixelTypes(String codec) {
    return new int[] {FormatTools.UINT8};
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  @Override
  public boolean canDoStacks() {
    return false;
  }

  /* @see loci.formats.IFormatWriter#setCompression(String) */
  @Override
  public void setCompression(String compress) throws FormatException {
    if (compress != null && !"JPEG".equals(compress)) {
      throw new FormatException("NDPI supports only JPEG compression");
    }
    compression = "JPEG";
  }

  /* @see loci.formats.IFormatHandler#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    if (id.equals(currentId)) {
      return;
    }
    if (currentId != null) {
      close();
    }
    readOptions();
    jpegQuality = resolveJPEGQuality(options);
    validateMetadata();
    planRestartGeometry();
    if (!sequential) {
      throw new FormatException("NDPI requires sequential writing");
    }
    Location destination = new Location(id).getAbsoluteFile();
    if (destination.exists() && !destination.delete()) {
      throw new IOException("Could not truncate existing NDPI output: " + id);
    }
    super.setId(id);
    sharedEntries.clear();
    sharedValues.clear();
    imageEntries.clear();
    imageStarted = false;
    assembler = null;
    activeResolution = -1;
    nextResolution = 0;
    nextRow = 0;
    expectedSeries = 0;
    rawOffset = -1;
    rawLength = 0;
    chainComplete = false;

    out.order(true);
    saver = new NDPIClassicTiffWriter(out);
    saver.writeHeader();
    nextIFDPointer = NDPIClassicTiffWriter.FIRST_IFD_POINTER;
    stageSharedValues();
  }

  /**
   * Close the writer, discarding any incomplete output.
   *
   * An NDPI file whose directory chain was never completed is invalid, so it
   * is removed. Incompleteness on its own is not reported as a failure,
   * because close() usually runs from a finally block where throwing would
   * mask the conversion error that caused it. Only a genuine close failure,
   * or a failure to remove the invalid output, is thrown.
   *
   * @see loci.formats.FormatWriter#close()
   */
  @Override
  public void close() throws IOException {
    String output = currentId;
    boolean incomplete = output != null && !chainComplete;
    IOException closeFailure = null;
    try {
      super.close();
    }
    catch (IOException e) {
      closeFailure = e;
    }

    IOException cleanupFailure = null;
    if (incomplete) {
      Location location = new Location(output);
      if (location.delete() || !location.exists()) {
        LOGGER.warn("Removed incomplete NDPI output {}", output);
      }
      else {
        cleanupFailure = new IOException(
          "Could not remove incomplete NDPI output: " + output);
      }
    }

    if (closeFailure != null) {
      if (cleanupFailure != null) closeFailure.addSuppressed(cleanupFailure);
      throw closeFailure;
    }
    if (cleanupFailure != null) {
      throw cleanupFailure;
    }
  }

  /* @see loci.formats.IFormatWriter#setResolution(int) */
  @Override
  public void setResolution(int resolution) {
    if (currentId != null) {
      if (role(getSeries()) != ImageRole.PYRAMID) {
        if (resolution != 0) {
          throw new IllegalArgumentException(
            "NDPI associated images support only resolution zero");
        }
      }
      else if (resolution != activeResolution &&
        resolution != nextResolution)
      {
        throw new IllegalArgumentException(
          "NDPI resolutions must be written from largest to smallest");
      }
    }
    super.setResolution(resolution);
  }

  /* @see loci.formats.IFormatWriter#setSeries(int) */
  @Override
  public void setSeries(int series) throws FormatException {
    if (currentId != null && series != expectedSeries) {
      throw new FormatException(
        "NDPI series must be written as pyramid, macro, then tissue map");
    }
    super.setSeries(series);
  }

  /*
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int,
   *   int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);
    if (chainComplete) {
      throw new FormatException("NDPI writing is already complete");
    }
    if (no != 0) {
      throw new FormatException("NDPI supports one plane per image");
    }
    if (x != 0 || w != getSizeX()) {
      throw new FormatException("NDPI row blocks must span the full width");
    }
    if (y != nextRow) {
      throw new FormatException(
        "NDPI rows must be written once, top to bottom");
    }
    ImageRole role = role(getSeries());
    if (role == ImageRole.PYRAMID && getResolution() != nextResolution &&
      getResolution() != activeResolution)
    {
      throw new FormatException(
        "NDPI resolutions must be written from largest to smallest");
    }

    if (!imageStarted) {
      stageImageValues(getSizeX(), getSizeY(), role);
      imageStarted = true;
    }

    if (role == ImageRole.TISSUE_MAP) {
      if (rawOffset < 0) rawOffset = out.getFilePointer();
      long requiredBytes = (long) w * h;
      // Unreachable in practice: no caller whose buffer fits in a byte[] can
      // hand over a single tissue-map row block larger than 2 GiB.
      if (requiredBytes > Integer.MAX_VALUE) {
        throw new FormatException(
          "NDPI tissue-map row block is too large to write");
      }
      int byteCount = (int) requiredBytes;
      out.write(buf, 0, byteCount);
      rawLength += byteCount;
      nextRow += h;
      if (nextRow == getSizeY()) {
        completeImage(new Level(rawOffset, rawLength, getSizeX(), getSizeY(),
          ImageRole.TISSUE_MAP));
        finishImageRole();
      }
      return;
    }

    if (assembler == null) {
      activeResolution = getResolution();
      int restartMCUs = role == ImageRole.PYRAMID ?
        pyramidRestartMCUs[getResolution()] : 0;
      boolean indexed = role == ImageRole.PYRAMID &&
        indexedPyramidLevels[getResolution()];
      if (!indexed) restartMCUs = 0;
      int count = indexed ?
        (int) mcuStartCount(getSizeX(), getSizeY(), restartMCUs) : 0;
      assembler = new NDPIJPEGAssembler(out, getSizeX(), getSizeY(),
        restartMCUs, indexed, jpegQuality,
        indexed ? authSegments(count) : null);
    }
    byte[] rows = interleaved ? buf : interleaveRows(buf, w, h);
    assembler.writeRows(rows, 0, h);
    nextRow += h;

    if (nextRow == getSizeY()) {
      NDPIJPEGAssembler.Result jpeg = assembler.finish();
      long[] starts = jpeg.getMCUStarts();
      boolean indexed =
        role == ImageRole.PYRAMID && starts.length >= MINIMUM_MCU_STARTS;
      completeImage(new Level(jpeg, getSizeX(), getSizeY(), role,
        role == ImageRole.PYRAMID ? sourceLens(getSizeX()) :
        role == ImageRole.MACRO ? -1 : -2,
        indexed ? Integer.valueOf(calculateAuthCode(jpeg, starts)) : null));
      assembler = null;
      activeResolution = -1;
      nextRow = 0;
      if (role == ImageRole.PYRAMID) nextResolution++;
      if (role != ImageRole.PYRAMID ||
        nextResolution == getResolutionCount()) finishImageRole();
    }
  }

  // -- Helper methods --

  private byte[] interleaveRows(byte[] planar, int width, int height) {
    int plane = width * height;
    byte[] interleavedRows = new byte[plane * CHANNELS];
    for (int pixel = 0; pixel < plane; pixel++) {
      for (int channel = 0; channel < CHANNELS; channel++) {
        interleavedRows[pixel * CHANNELS + channel] =
          planar[channel * plane + pixel];
      }
    }
    return interleavedRows;
  }

  private void finishImageRole() {
    nextRow = 0;
    rawOffset = -1;
    rawLength = 0;
    if (expectedSeries == 0 && macroSeries != null) {
      expectedSeries = macroSeries;
    }
    else if (expectedSeries != tissueMapSeriesValue() &&
      tissueMapSeries != null)
    {
      expectedSeries = tissueMapSeries;
    }
    else {
      chainComplete = true;
    }
  }

  private int tissueMapSeriesValue() {
    return tissueMapSeries == null ? -1 : tissueMapSeries.intValue();
  }

  private void validateMetadata() throws FormatException {
    MetadataRetrieve retrieve = getMetadataRetrieve();
    validateAssociatedSeries(retrieve);
    super.setSeries(0);
    if (FormatTools.pixelTypeFromString(
      retrieve.getPixelsType(0).toString()) != FormatTools.UINT8)
    {
      throw new FormatException("NDPI supports only unsigned 8-bit pixels");
    }
    if (retrieve.getPixelsSizeC(0).getValue().intValue() != CHANNELS ||
      getSamplesPerPixel() != CHANNELS)
    {
      throw new FormatException("NDPI requires SizeC == 3 as one RGB plane");
    }
    if (getPlaneCount() != 1) {
      throw new FormatException("NDPI supports one RGB plane");
    }
    Double magnification = objectiveMagnification();
    if (magnification == null || magnification <= 0) {
      throw new FormatException(
        "NDPI requires a positive objective nominal magnification");
    }
    int previousWidth = Integer.MAX_VALUE;
    int previousHeight = Integer.MAX_VALUE;
    for (int r = 0; r < getResolutionCount(); r++) {
      super.setResolution(r);
      int width = getSizeX();
      int height = getSizeY();
      if (width > previousWidth || height > previousHeight) {
        throw new FormatException(
          "NDPI resolutions must be ordered largest to smallest");
      }
      previousWidth = width;
      previousHeight = height;
    }
    super.setResolution(0);

    if (macroSeries != null) {
      validateSeriesLayout(retrieve, macroSeries, CHANNELS, "macro");
    }
    if (tissueMapSeries != null) {
      validateSeriesLayout(retrieve, tissueMapSeries, 1, "tissue map");
    }
    super.setSeries(0);
  }

  private void validateAssociatedSeries(MetadataRetrieve retrieve)
    throws FormatException
  {
    int imageCount = retrieve.getImageCount();
    int expectedCount = 1 + (macroSeries == null ? 0 : 1) +
      (tissueMapSeries == null ? 0 : 1);
    if (imageCount != expectedCount) {
      throw new FormatException("NDPI requires every non-pyramid series to " +
        "be identified as macro or tissue map");
    }
    if (macroSeries != null) validateSeriesIndex(macroSeries, imageCount);
    if (tissueMapSeries != null) validateSeriesIndex(tissueMapSeries,
      imageCount);
    if (macroSeries != null && macroSeries.equals(tissueMapSeries)) {
      throw new FormatException(
        "NDPI macro and tissue-map series must be different");
    }
    if (labelObscured != null && macroSeries == null) {
      throw new FormatException(
        "NDPI label-obscured option requires a macro series");
    }
    if (macroSeries != null && macroSeries.intValue() != 1) {
      throw new FormatException("NDPI macro series must follow the pyramid");
    }
    // The tissue map needs no separate ordering check: the index-range and
    // distinctness rules above leave it exactly one legal index, whether or
    // not a macro series is present.
  }

  private static void validateSeriesIndex(Integer index, int imageCount)
    throws FormatException
  {
    if (index.intValue() <= 0 || index.intValue() >= imageCount) {
      throw new FormatException(
        "NDPI associated-image series index is out of range");
    }
  }

  private void validateSeriesLayout(MetadataRetrieve retrieve, int image,
    int channels, String name) throws FormatException
  {
    super.setSeries(image);
    if (FormatTools.pixelTypeFromString(
      retrieve.getPixelsType(image).toString()) != FormatTools.UINT8)
    {
      throw new FormatException("NDPI " + name +
        " supports only unsigned 8-bit pixels");
    }
    if (retrieve.getPixelsSizeC(image).getValue().intValue() != channels ||
      getSamplesPerPixel() != channels)
    {
      throw new FormatException("NDPI " + name + " requires SizeC == " +
        channels + " in one plane");
    }
    if (getPlaneCount() != 1 || getResolutionCount() != 1) {
      throw new FormatException("NDPI " + name +
        " requires exactly one image plane and resolution");
    }
  }

  private void readOptions() throws FormatException {
    clearOptions();
    MetadataOptions metadataOptions = getMetadataOptions();
    if (!(metadataOptions instanceof DynamicMetadataOptions)) return;
    DynamicMetadataOptions options =
      (DynamicMetadataOptions) metadataOptions;
    try {
      reference = ascii(options.get(REFERENCE_KEY), "slide reference");
      xPosition = signedLong(options.getLong(X_POSITION_KEY), "X position");
      yPosition = signedLong(options.getLong(Y_POSITION_KEY), "Y position");
      zPosition = signedLong(options.getLong(Z_POSITION_KEY), "Z position");
      exposureRatio = unsignedLong(options.getLong(EXPOSURE_RATIO_KEY),
        "exposure ratio");
      redMultiplier = unsignedLong(options.getLong(RED_MULTIPLIER_KEY),
        "red multiplier");
      greenMultiplier = unsignedLong(options.getLong(GREEN_MULTIPLIER_KEY),
        "green multiplier");
      blueMultiplier = unsignedLong(options.getLong(BLUE_MULTIPLIER_KEY),
        "blue multiplier");
      focusPoints = signedLongArray(options.get(FOCUS_POINTS_KEY),
        "focus points", true);
      focusPointRegions = signedLongArray(
        options.get(FOCUS_POINT_REGIONS_KEY), "focus-point regions", false);
      Long captureMode = unsignedLong(options.getLong(CAPTURE_MODE_KEY),
        "capture mode");
      if (captureMode != null && captureMode.longValue() != 0) {
        throw new IllegalArgumentException(
          "NDPI capture mode must be 0 for brightfield RGB");
      }
      scannerSerialNumber = ascii(options.get(SERIAL_NUMBER_KEY),
        "scanner serial number");
      scannerManufacturer = ascii(options.get(SCANNER_MANUFACTURER_KEY),
        "scanner manufacturer");
      scannerModel = ascii(options.get(SCANNER_MODEL_KEY), "scanner model");
      refocusInterval = signedLong(options.getLong(REFOCUS_INTERVAL_KEY),
        "refocus interval");
      focusOffset = signedLong(options.getLong(FOCUS_OFFSET_KEY),
        "focus offset");
      firmwareVersion = ascii(options.get(FIRMWARE_VERSION_KEY),
        "firmware version");
      calibration = propertyMap(options.get(CALIBRATION_KEY));
      wavelength = finiteNonNegative(options.getFloat(WAVELENGTH_KEY),
        "wavelength");
      lampAge = unsignedLong(options.getLong(LAMP_AGE_KEY), "lamp age");
      exposureTime = unsignedLong(options.getLong(EXPOSURE_TIME_KEY),
        "exposure time");
      focusTime = unsignedLong(options.getLong(FOCUS_TIME_KEY), "focus time");
      scanTime = unsignedLong(options.getLong(SCAN_TIME_KEY), "scan time");
      writeTime = unsignedLong(options.getLong(WRITE_TIME_KEY), "write time");
      fullyAutomaticFocus = options.getBoolean(FULLY_AUTO_FOCUS_KEY, null);
      for (int i = 0; i < barcodes.length; i++) {
        barcodes[i] = ascii(options.get(BARCODE_KEY_PREFIX + i), "barcode");
      }
      medicalRegulation = ascii(options.get(MEDICAL_REGULATION_KEY),
        "medical regulation");
      restartMCUsOverride = options.getInteger(RESTART_MCUS_KEY);
      if (restartMCUsOverride != null && (restartMCUsOverride < 1 ||
        restartMCUsOverride > 0xffff))
      {
        throw new IllegalArgumentException(
          "NDPI restart interval must be between 1 and 65535 MCUs");
      }
      macroSeries = options.getInteger(MACRO_SERIES_KEY);
      tissueMapSeries = options.getInteger(TISSUE_MAP_SERIES_KEY);
      labelObscured = options.getBoolean(LABEL_OBSCURED_KEY, null);
    }
    catch (IllegalArgumentException e) {
      throw new FormatException("Invalid NDPI writer option: " +
        e.getMessage(), e);
    }
  }

  private void clearOptions() {
    reference = null;
    xPosition = null;
    yPosition = null;
    zPosition = null;
    exposureRatio = null;
    redMultiplier = null;
    greenMultiplier = null;
    blueMultiplier = null;
    focusPoints = null;
    focusPointRegions = null;
    scannerSerialNumber = null;
    scannerManufacturer = null;
    scannerModel = null;
    refocusInterval = null;
    focusOffset = null;
    firmwareVersion = null;
    calibration = null;
    wavelength = null;
    lampAge = null;
    exposureTime = null;
    focusTime = null;
    scanTime = null;
    writeTime = null;
    fullyAutomaticFocus = null;
    for (int i = 0; i < barcodes.length; i++) barcodes[i] = null;
    medicalRegulation = null;
    restartMCUsOverride = null;
    macroSeries = null;
    tissueMapSeries = null;
    labelObscured = null;
  }

  /**
   * Compute the AuthCode over four sampled entropy bytes.
   *
   * The constant key and the interval and byte selection below were derived
   * from a corpus of scanner-written NDPI files and checked against the
   * AuthCode values those files carry.
   */
  private int calculateAuthCode(NDPIJPEGAssembler.Result jpeg, long[] starts)
    throws FormatException
  {
    int[] segments = authSegments(starts.length);
    int[] samples = new int[segments.length];
    for (int i = 0; i < segments.length; i++) {
      long entropyLength =
        starts[segments[i] + 1] - starts[segments[i]] - 2;
      byte[] entropy = jpeg.getCapturedEntropy(i);
      if (entropyLength <= 0 || entropy.length != entropyLength) {
        throw new FormatException("NDPI JPEG contains an invalid MCU segment");
      }
      int selector = AUTH_SAMPLES[i][1];
      long offset = selector == AUTH_FIXED_SELECTOR ? AUTH_FIXED_OFFSET :
        selector * entropyLength / 100;
      int index = (int) Math.min(entropyLength - 1, offset);
      samples[i] = entropy[index] & 0xff;
    }
    return AUTH_KEY ^ samples[0] ^ (samples[2] << 8) ^
      (samples[3] << 16) ^ (samples[1] << 24);
  }

  /** Select the restart intervals sampled by calculateAuthCode(). */
  private static int[] authSegments(int count) {
    int[] segments = new int[AUTH_SAMPLES.length];
    for (int i = 0; i < segments.length; i++) {
      int selector = AUTH_SAMPLES[i][0];
      segments[i] = selector == AUTH_FIXED_SELECTOR ? AUTH_FIXED_OFFSET :
        (int) ((long) selector * count / 100);
    }
    return segments;
  }

  /**
   * Writes the external values every directory of this file shares, and
   * prepares the entries that name them.
   *
   * The shared values sit immediately after the header, where genuine files
   * keep them, so that every directory can point back to one copy.
   */
  private void stageSharedValues() throws FormatException, IOException {
    for (NDPIClassicTiffWriter.Value value :
      NDPIClassicTiffWriter.values(sharedIFD()))
    {
      sharedEntries.put(Integer.valueOf(value.tag()), stageShared(value));
    }
  }

  /**
   * Prepares one shared entry, writing its value unless another tag has
   * already written the same bytes under the same TIFF type.
   */
  private NDPIClassicTiffWriter.Entry stageShared(
    NDPIClassicTiffWriter.Value value) throws IOException
  {
    if (!value.external()) return saver.entry(value);
    NDPIClassicTiffWriter.Entry written = sharedValues.get(value.key());
    if (written != null) return written.retag(value.tag());
    NDPIClassicTiffWriter.Entry entry = saver.entry(value);
    sharedValues.put(value.key(), entry);
    return entry;
  }

  /**
   * Writes the external values of one image and prepares the entries that
   * name them.
   *
   * These values depend only on the image's role and dimensions, so they are
   * complete before any of its pixels are written and are placed immediately
   * before its payload.
   */
  private void stageImageValues(int width, int height, ImageRole role)
    throws FormatException, IOException
  {
    imageEntries.clear();
    for (NDPIClassicTiffWriter.Value value :
      NDPIClassicTiffWriter.values(imageIFD(width, height, role)))
    {
      stageImageValue(value);
    }
  }

  private void stageImageValue(NDPIClassicTiffWriter.Value value)
    throws IOException
  {
    imageEntries.put(Integer.valueOf(value.tag()), saver.entry(value));
  }

  /**
   * Completes one image by writing its restart index where it has one, then
   * its directory, then linking that directory into the chain.
   */
  private void completeImage(Level level)
    throws FormatException, IOException
  {
    stageRestartIndex(level.mcuStarts);
    List<NDPIClassicTiffWriter.Entry> entries =
      new ArrayList<NDPIClassicTiffWriter.Entry>(sharedEntries.values());
    entries.addAll(imageEntries.values());
    for (NDPIClassicTiffWriter.Value value :
      NDPIClassicTiffWriter.values(completedIFD(level)))
    {
      entries.add(saver.entry(value));
    }
    NDPIClassicTiffWriter.Directory directory = saver.writeIFD(entries);
    saver.patchOffset(nextIFDPointer, directory.offset());
    nextIFDPointer = directory.nextPointer();
    imageEntries.clear();
    imageStarted = false;
  }

  /** Writes the restart-offset arrays between an indexed JPEG and its IFD. */
  private void stageRestartIndex(long[] mcuStarts)
    throws FormatException, IOException
  {
    if (mcuStarts.length == 0) return;
    stageImageValue(NDPIClassicTiffWriter.value(NDPITags.MCU_STARTS,
      lowWords(mcuStarts)));
    if (hasHighWords(mcuStarts)) {
      stageImageValue(NDPIClassicTiffWriter.value(
        NDPITags.MCU_STARTS_HIGH_BYTES, highWords(mcuStarts)));
    }
  }

  /** The values every directory of one NDPI file repeats. */
  private IFD sharedIFD() {
    IFD ifd = new IFD();
    MetadataRetrieve retrieve = getMetadataRetrieve();
    String make = scannerManufacturer == null ?
      microscopeManufacturer(retrieve) : scannerManufacturer;
    String model = scannerModel == null ?
      microscopeModel(retrieve) : scannerModel;
    if (make != null && !make.isEmpty()) ifd.putIFDValue(IFD.MAKE, make);
    if (model != null && !model.isEmpty()) ifd.putIFDValue(IFD.MODEL, model);
    ifd.putIFDValue(IFD.SOFTWARE, "Bio-Formats NDPIWriter");
    String acquisitionDate = acquisitionDate(retrieve);
    if (acquisitionDate != null) {
      ifd.putIFDValue(IFD.DATE_TIME, acquisitionDate);
    }
    putFileWideOptionTags(ifd);
    return ifd;
  }

  /** The values of one image that are known before its payload. */
  private IFD imageIFD(int width, int height, ImageRole role) {
    IFD ifd = new IFD();
    boolean map = role == ImageRole.TISSUE_MAP;
    ifd.put(IFD.BITS_PER_SAMPLE, map ? new int[] {8} : new int[] {8, 8, 8});
    if (!map) {
      ifd.put(IFD.Y_CB_CR_SUB_SAMPLING, new int[] {1, 1});
      ifd.put(IFD.REFERENCE_BLACK_WHITE,
        new long[] {0, 255, 128, 255, 128, 255});
    }
    addResolution(ifd, width, height, role);
    return ifd;
  }

  /** The values of one image that are known only once it is complete. */
  private IFD completedIFD(Level level) {
    IFD ifd = new IFD();
    ifd.putIFDValue(IFD.IMAGE_WIDTH, (long) level.width);
    ifd.putIFDValue(IFD.IMAGE_LENGTH, (long) level.height);
    boolean map = level.role == ImageRole.TISSUE_MAP;
    ifd.putIFDValue(IFD.COMPRESSION, map ?
      TiffCompression.UNCOMPRESSED.getCode() : TiffCompression.JPEG.getCode());
    ifd.putIFDValue(IFD.PHOTOMETRIC_INTERPRETATION,
      map ? PhotoInterp.BLACK_IS_ZERO.getCode() :
      PhotoInterp.Y_CB_CR.getCode());
    ifd.putIFDValue(IFD.SAMPLES_PER_PIXEL, map ? 1 : CHANNELS);
    ifd.putIFDValue(IFD.ROWS_PER_STRIP, (long) level.height);
    ifd.putIFDValue(IFD.PLANAR_CONFIGURATION, 1);
    ifd.putIFDValue(NDPITags.VERSION, 1);
    ifd.putIFDValue(NDPITags.SOURCE_LENS,
      Float.valueOf((float) level.sourceLens));
    if (level.role == ImageRole.PYRAMID) {
      ifd.putIFDValue(NDPITags.X_POSITION, xPosition == null ? 0L : xPosition);
      ifd.putIFDValue(NDPITags.Y_POSITION, yPosition == null ? 0L : yPosition);
      Long levelZ = zPosition == null ? planeZ() : zPosition;
      ifd.putIFDValue(NDPITags.Z_POSITION, levelZ == null ? 0L : levelZ);
      ifd.putIFDValue(NDPITags.TISSUE_INDEX, 0L);
      putPyramidOptionTags(ifd);
    }
    else {
      ifd.putIFDValue(NDPITags.X_POSITION, 0L);
      ifd.putIFDValue(NDPITags.Y_POSITION, 0L);
      if (level.role == ImageRole.MACRO) {
        ifd.putIFDValue(NDPITags.CAPTURE_MODE, 0L);
        put(ifd, NDPITags.LABEL_OBSCURED, labelObscured == null ? null :
          Long.valueOf(labelObscured.booleanValue() ? 1 : 0));
      }
    }
    if (level.authCode != null) {
      ifd.putIFDValue(NDPITags.AUTH_CODE,
        level.authCode.intValue() & 0xffffffffL);
    }
    putStripTags(ifd, level.jpegOffset, level.jpegLength);
    if (!map) {
      ifd.putIFDValue(NDPITags.JPEG_QUALITY, Math.round(jpegQuality * 100));
    }
    return ifd;
  }

  private static String microscopeManufacturer(MetadataRetrieve retrieve) {
    try {
      return retrieve.getMicroscopeManufacturer(0);
    }
    catch (NullPointerException | IndexOutOfBoundsException e) {
      // OMEXMLMetadata throws when Instrument exists without Microscope.
      return null;
    }
  }

  private static String microscopeModel(MetadataRetrieve retrieve) {
    try {
      return retrieve.getMicroscopeModel(0);
    }
    catch (NullPointerException | IndexOutOfBoundsException e) {
      // OMEXMLMetadata throws when Instrument exists without Microscope.
      return null;
    }
  }

  private static String acquisitionDate(MetadataRetrieve retrieve) {
    Timestamp timestamp = retrieve.getImageAcquisitionDate(0);
    if (timestamp == null) return null;
    return DateTools.convertDate(timestamp.asInstant().getMillis(),
      DateTools.UNIX, TIFF_DATE_FORMAT);
  }

  private void putFileWideOptionTags(IFD ifd) {
    if (reference != null) ifd.putIFDValue(NDPITags.REFERENCE, reference);
    put(ifd, NDPITags.FOCUS_POINTS, focusPoints);
    put(ifd, NDPITags.FOCUS_POINT_REGIONS, focusPointRegions);
    put(ifd, NDPITags.SERIAL_NUMBER, scannerSerialNumber);
    put(ifd, NDPITags.REFOCUS_INTERVAL, refocusInterval);
    put(ifd, NDPITags.FOCUS_OFFSET, focusOffset);
    put(ifd, NDPITags.FIRMWARE_VERSION, firmwareVersion);
    put(ifd, NDPITags.CALIBRATION, calibration);
    for (int i = 0; i < barcodes.length; i++) {
      put(ifd, NDPITags.FIRST_BARCODE + i, barcodes[i]);
    }
    put(ifd, NDPITags.MEDICAL_REGULATION, medicalRegulation);
  }

  private void putPyramidOptionTags(IFD ifd) {
    put(ifd, NDPITags.EXPOSURE_RATIO, exposureRatio);
    put(ifd, NDPITags.RED_MULTIPLIER, redMultiplier);
    put(ifd, NDPITags.GREEN_MULTIPLIER, greenMultiplier);
    put(ifd, NDPITags.BLUE_MULTIPLIER, blueMultiplier);
    ifd.putIFDValue(NDPITags.CAPTURE_MODE, 0L);
    put(ifd, NDPITags.WAVELENGTH, wavelength);
    put(ifd, NDPITags.LAMP_AGE, lampAge);
    put(ifd, NDPITags.EXPOSURE_TIME, exposureTime);
    put(ifd, NDPITags.FOCUS_TIME, focusTime);
    put(ifd, NDPITags.SCAN_TIME, scanTime);
    put(ifd, NDPITags.WRITE_TIME, writeTime);
    put(ifd, NDPITags.FULLY_AUTO_FOCUS, fullyAutomaticFocus == null ? null :
      Long.valueOf(fullyAutomaticFocus ? 1 : 0));
  }

  private static void put(IFD ifd, int tag, Object value) {
    if (value != null) ifd.putIFDValue(tag, value);
  }

  private ImageRole role(int image) {
    if (macroSeries != null && image == macroSeries.intValue()) {
      return ImageRole.MACRO;
    }
    if (tissueMapSeries != null && image == tissueMapSeries.intValue()) {
      return ImageRole.TISSUE_MAP;
    }
    return ImageRole.PYRAMID;
  }

  private Long planeZ() {
    Length z;
    try {
      z = getMetadataRetrieve().getPlanePositionZ(0, 0);
    }
    catch (NullPointerException | IndexOutOfBoundsException e) {
      return null;
    }
    if (z == null) return null;
    Number nanometers = z.value(UNITS.NANOMETER);
    if (nanometers == null) return null;
    return signedLong(Long.valueOf(Math.round(nanometers.doubleValue())),
      "plane Z position");
  }

  private static String ascii(String value, String name) {
    if (value == null || value.isEmpty()) return null;
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (c < 0x20 || c > 0x7e) {
        throw new IllegalArgumentException(
          "NDPI " + name + " must contain printable ASCII");
      }
    }
    return value;
  }

  private static String propertyMap(String value) {
    if (value == null || value.isEmpty()) return null;
    // A non-empty value always splits into at least one non-empty record, so
    // dropping an optional trailing CRLF cannot empty the record list.
    String[] records = value.split("\r\n", -1);
    int recordCount = records.length;
    if (records[recordCount - 1].isEmpty()) recordCount--;
    for (int record = 0; record < recordCount; record++) {
      String line = records[record];
      int equals = line.indexOf('=');
      if (equals <= 0) {
        throw new IllegalArgumentException(
          "NDPI calibration metadata must contain key=value records");
      }
      for (int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);
        if (c < 0x20 || c > 0x7e) {
          throw new IllegalArgumentException(
            "NDPI calibration metadata must use printable ASCII and CRLF");
        }
      }
    }
    // No further newline check is needed: splitting on CRLF leaves every
    // stray CR or LF inside a record, where the printable-ASCII scan above
    // rejects it.
    return value;
  }

  private static Float finiteNonNegative(Float value, String name) {
    if (value == null) return null;
    if (!Float.isFinite(value.floatValue()) || value.floatValue() < 0) {
      throw new IllegalArgumentException(
        "NDPI " + name + " must be finite and non-negative");
    }
    return value;
  }

  private static int[] signedLongArray(String value, String name,
    boolean triplets)
  {
    if (value == null || value.trim().isEmpty()) return null;
    String[] fields = value.split(",", -1);
    if (triplets && fields.length % 3 != 0) {
      throw new IllegalArgumentException(
        "NDPI " + name + " must contain X/Y/Z triplets");
    }
    int[] result = new int[fields.length];
    for (int i = 0; i < fields.length; i++) {
      String field = fields[i].trim();
      if (field.isEmpty()) {
        throw new IllegalArgumentException(
          "NDPI " + name + " contains an empty value");
      }
      long parsed = Long.parseLong(field);
      if (parsed < Integer.MIN_VALUE || parsed > Integer.MAX_VALUE) {
        throw new IllegalArgumentException(
          "NDPI " + name + " values must fit signed 32-bit integers");
      }
      result[i] = (int) parsed;
    }
    return result;
  }

  private static Long signedLong(Long value, String name) {
    if (value == null) return null;
    if (value.longValue() < Integer.MIN_VALUE ||
      value.longValue() > Integer.MAX_VALUE)
    {
      throw new IllegalArgumentException(
        "NDPI " + name + " must fit a signed 32-bit integer");
    }
    return value;
  }

  private static Long unsignedLong(Long value, String name) {
    if (value == null) return null;
    if (value.longValue() < 0 || value.longValue() > 0xffffffffL) {
      throw new IllegalArgumentException(
        "NDPI " + name + " must fit an unsigned 32-bit integer");
    }
    return Long.valueOf(value);
  }

  private static double resolveJPEGQuality(CodecOptions options)
    throws FormatException
  {
    double quality = options == null || options.quality == 0 ?
      DEFAULT_JPEG_QUALITY : options.quality;
    if (!Double.isFinite(quality) || quality < 0.25 || quality > 1) {
      throw new FormatException(
        "NDPI JPEG quality must be between 0.25 and 1");
    }
    return quality;
  }

  /**
   * Records where one payload lies, in the two tags whose 64-bit values NDPI
   * splits across an entry and its extension word.
   */
  static void putStripTags(IFD ifd, long payloadOffset, long payloadLength) {
    ifd.put(IFD.STRIP_OFFSETS, new long[] {payloadOffset});
    ifd.put(IFD.STRIP_BYTE_COUNTS, new long[] {payloadLength});
  }

  private void planRestartGeometry() throws FormatException {
    int resolutions = getResolutionCount();
    pyramidRestartMCUs = new int[resolutions];
    indexedPyramidLevels = new boolean[resolutions];
    super.setResolution(0);
    int baseColumns = mcuColumns(getSizeX());
    int baseRestart =
      baseRestartInterval(baseColumns, restartMCUsOverride);
    int tileColumns = baseRestart == 0 ? 1 : baseColumns / baseRestart;

    for (int r = 0; r < resolutions; r++) {
      super.setResolution(r);
      int width = getSizeX();
      int height = getSizeY();
      int columns = mcuColumns(width);
      long rows = ((long) height + 7) / 8;
      boolean completeMCUs = width % 8 == 0 && height % 8 == 0;
      int restart = restartIntervalForGrid(columns, tileColumns);
      boolean preservesGrid = baseRestart != 0 && restart != 0;
      long proposedCount = (long) tileColumns * rows;
      boolean needsIndex = proposedCount >= MINIMUM_MCU_STARTS;
      boolean indexed = needsIndex && completeMCUs && preservesGrid;

      // Only reachable for images far beyond the dimensions a classic-TIFF
      // NDPI container can address.
      if (indexed && proposedCount > Integer.MAX_VALUE) {
        throw new FormatException("NDPI contains too many MCU start entries");
      }
      indexedPyramidLevels[r] = indexed;
      pyramidRestartMCUs[r] = indexed ? restart : 0;
      // restartIntervalForGrid() already rejects any interval that does not
      // fit the 16-bit DRI field, so an indexed level's interval is always
      // representable here.
      if (!indexed) {
        validateNonIndexedBuffer(width, height, "resolution " + r);
      }
    }
    super.setResolution(0);
    if (macroSeries != null) {
      super.setSeries(macroSeries);
      validateNonIndexedBuffer(getSizeX(), getSizeY(), "macro");
    }
    super.setSeries(0);
    super.setResolution(0);
  }

  private static void validateNonIndexedBuffer(int width, int height,
    String description) throws FormatException
  {
    long bytes = (long) width * height * CHANNELS;
    // A non-indexed image is buffered whole, so reject it here rather than
    // attempting a >2 GiB allocation.
    if (bytes > Integer.MAX_VALUE) {
      throw new FormatException("NDPI non-indexed " + description +
        " is too large to buffer as one JPEG");
    }
  }

  static int automaticRestartInterval(int mcuColumns) {
    if (mcuColumns <= 0) return 0;
    if (mcuColumns <= 512) return mcuColumns;
    for (int restart : NOMINAL_RESTART_MCUS) {
      if (mcuColumns % restart == 0) return restart;
    }
    for (int restart = 512; restart > 1; restart--) {
      if (mcuColumns % restart == 0) return restart;
    }
    // Every positive column count is divisible by one, so a prime column
    // count wider than the largest candidate falls back to one MCU per
    // restart interval.
    return 1;
  }

  static int baseRestartInterval(int mcuColumns, Integer override)
    throws FormatException
  {
    if (override == null) return automaticRestartInterval(mcuColumns);
    if (mcuColumns % override.intValue() != 0) {
      throw new FormatException("NDPI restart interval must exactly divide " +
        "the base image MCU-column count");
    }
    return override.intValue();
  }

  static int restartIntervalForGrid(int mcuColumns, int tileColumns) {
    if (tileColumns <= 0 || mcuColumns % tileColumns != 0) return 0;
    int restart = mcuColumns / tileColumns;
    return restart > 0 && restart <= 0xffff ? restart : 0;
  }

  private static int mcuColumns(int width) {
    return (int) (((long) width + 7) / 8);
  }

  private static long mcuStartCount(int width, int height, int restartMCUs) {
    long columns = ((long) width + 7) / 8;
    long rows = ((long) height + 7) / 8;
    return columns / restartMCUs * rows;
  }

  private double sourceLens(int width) {
    Double magnification = objectiveMagnification();
    if (magnification == null) return 0;
    int baseWidth =
      getMetadataRetrieve().getPixelsSizeX(0).getValue().intValue();
    return magnification * width / baseWidth;
  }

  private Double objectiveMagnification() {
    MetadataRetrieve retrieve = getMetadataRetrieve();
    String objectiveID = retrieve.getObjectiveSettingsID(0);
    for (int instrument = 0;
      instrument < retrieve.getInstrumentCount(); instrument++)
    {
      for (int objective = 0;
        objective < retrieve.getObjectiveCount(instrument); objective++)
      {
        if (objectiveID == null ||
          objectiveID.equals(retrieve.getObjectiveID(instrument, objective)))
        {
          Double magnification =
            retrieve.getObjectiveNominalMagnification(instrument, objective);
          if (magnification != null) return magnification;
        }
      }
    }
    return null;
  }

  private void addResolution(IFD ifd, int width, int height, ImageRole role) {
    MetadataRetrieve retrieve = getMetadataRetrieve();
    int image = role == ImageRole.PYRAMID ? 0 :
      role == ImageRole.MACRO ? macroSeries.intValue() :
      tissueMapSeries.intValue();
    Length physicalX = retrieve.getPixelsPhysicalSizeX(image);
    Length physicalY = retrieve.getPixelsPhysicalSizeY(image);
    Number valueX =
      physicalX == null ? null : physicalX.value(UNITS.MICROMETER);
    Number valueY =
      physicalY == null ? null : physicalY.value(UNITS.MICROMETER);
    Double micronsX = valueX == null ? null : valueX.doubleValue();
    Double micronsY = valueY == null ? null : valueY.doubleValue();
    int baseWidth = retrieve.getPixelsSizeX(image).getValue().intValue();
    int baseHeight = retrieve.getPixelsSizeY(image).getValue().intValue();
    if (micronsX != null && micronsX > 0 &&
      micronsY != null && micronsY > 0)
    {
      double pixelsPerCentimeter =
        10000d / (micronsX * baseWidth / width);
      ifd.put(IFD.X_RESOLUTION, rational(pixelsPerCentimeter));
      pixelsPerCentimeter =
        10000d / (micronsY * baseHeight / height);
      ifd.put(IFD.Y_RESOLUTION, rational(pixelsPerCentimeter));
      ifd.putIFDValue(IFD.RESOLUTION_UNIT, 3);
    }
  }

  private static TiffRational rational(double value) {
    long denominator = 10000;
    return new TiffRational(Math.round(value * denominator), denominator);
  }

  private static long lowWord(long value) {
    return value & 0xffffffffL;
  }

  private static long highWord(long value) {
    return value >>> 32;
  }

  private static long[] lowWords(long[] values) {
    long[] words = new long[values.length];
    for (int i = 0; i < values.length; i++) words[i] = lowWord(values[i]);
    return words;
  }

  private static long[] highWords(long[] values) {
    long[] words = new long[values.length];
    for (int i = 0; i < values.length; i++) words[i] = highWord(values[i]);
    return words;
  }

  private static boolean hasHighWords(long[] values) {
    for (long value : values) {
      if (highWord(value) != 0) return true;
    }
    return false;
  }

  private static final class Level {

    private final long jpegOffset;
    private final long jpegLength;
    private final long[] mcuStarts;
    private final Integer authCode;
    private final int width;
    private final int height;
    private final double sourceLens;
    private final ImageRole role;

    private Level(NDPIJPEGAssembler.Result jpeg, int width, int height,
      ImageRole role, double sourceLens, Integer authCode)
    {
      jpegOffset = jpeg.getJPEGOffset();
      jpegLength = jpeg.getJPEGLength();
      mcuStarts = authCode == null ? new long[0] : jpeg.getMCUStarts();
      this.width = width;
      this.height = height;
      this.sourceLens = sourceLens;
      this.authCode = authCode;
      this.role = role;
    }

    private Level(long offset, long length, int width, int height,
      ImageRole role)
    {
      jpegOffset = offset;
      jpegLength = length;
      mcuStarts = new long[0];
      authCode = null;
      this.width = width;
      this.height = height;
      sourceLens = -2;
      this.role = role;
    }
  }

  private enum ImageRole {
    PYRAMID, MACRO, TISSUE_MAP
  }
}
