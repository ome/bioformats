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

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.common.services.ServiceException;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.services.JPEGTurboService;
import loci.formats.services.JPEGTurboServiceImpl;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDType;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffConstants;
import loci.formats.tiff.TiffIFDEntry;
import loci.formats.tiff.TiffParser;

import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.Timestamp;
import ome.units.UNITS;
import ome.units.quantity.Length;

/**
 * NDPIReader is the file format reader for Hamamatsu .ndpi files.
 */
public class NDPIReader extends BaseTiffReader {

  // -- Constants --

  private static final int MAX_SIZE = 2048;

  // Custom TIFF tags
  private static final int OFFSET_HIGH_BYTES = 65324;
  private static final int BYTE_COUNT_HIGH_BYTES = 65325;
  private static final int VERSION = 65420;
  private static final int SOURCE_LENS = 65421;
  private static final int X_POSITION = 65422;
  private static final int Y_POSITION = 65423;
  private static final int Z_POSITION = 65424;
  private static final int TISSUE_INDEX = 65425;
  private static final int MARKER_TAG = 65426;
  private static final int REFERENCE = 65427;
  private static final int FILTER_SET_NAME = 65434;
  private static final int EXPOSURE_RATIO = 65435;
  private static final int RED_MULTIPLIER = 65436;
  private static final int GREEN_MULTIPLIER = 65437;
  private static final int BLUE_MULTIPLIER = 65438;
  private static final int THUMB_TAG_2 = 65439; // focus points
  private static final int FOCUS_POINT_REGIONS = 65440;
  private static final int CAPTURE_MODE = 65441;
  private static final int SERIAL_NUMBER = 65442;
  private static final int JPEG_QUALITY = 65444;
  private static final int REFOCUS_INTERVAL = 65445;
  private static final int FOCUS_OFFSET = 65446;
  private static final int FIRMWARE_VERSION = 65448;
  private static final int METADATA_TAG = 65449; // calibration info
  private static final int LABEL_OBSCURED = 65450;
  private static final int WAVELENGTH = 65451;
  private static final int LAMP_AGE = 65453;
  private static final int EXPOSURE_TIME = 65454;
  private static final int FOCUS_TIME = 65455;
  private static final int SCAN_TIME = 65456;
  private static final int WRITE_TIME = 65457;
  private static final int FULLY_AUTO_FOCUS = 65458;
  private static final int DEFAULT_GAMMA = 65500;

  // -- Fields --

  private int initializedSeries = -1;
  private int initializedPlane = -1;

  private int sizeZ = 1;
  private int pyramidHeight = 1;

  private JPEGTurboService service = new JPEGTurboServiceImpl();

  private Double magnification;
  private String serialNumber;
  private String instrumentModel;

  // -- Constructor --

  /** Constructs a new NDPI reader. */
  public NDPIReader() {
    super("Hamamatsu NDPI", new String[] {"ndpi"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    canSeparateSeries = false;
  }

  // -- IFormatReader API methods --

  /* (non-Javadoc)
   * @see loci.formats.FormatReader#isThisType(java.lang.String, boolean)
   */
  @Override
  public boolean isThisType(String name, boolean open) {
    boolean isThisType = super.isThisType(name, open);
    if (isThisType && open) {
      try (RandomAccessInputStream stream = new RandomAccessInputStream(name)) {
        TiffParser parser = new TiffParser(stream);
        parser.setDoCaching(false);
        parser.setUse64BitOffsets(stream.length() >= Math.pow(2, 32));
        if (!parser.isValidHeader()) {
          return false;
        }
        IFD ifd = parser.getFirstIFD();
        if (ifd == null) {
          return false;
        }
        // non-JPEG data won't have the marker tag,
        // but should still have the metadata tag
        return ifd.containsKey(MARKER_TAG) || ifd.containsKey(METADATA_TAG);
      }
      catch (IOException e) {
        LOGGER.debug("I/O exception during isThisType() evaluation.", e);
        return false;
      }
    }
    return isThisType;
  }

  /** @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return MUST_GROUP;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int ifdIndex = getIFDIndex(getCoreIndex(), no);

    if (x == 0 && y == 0 && w == 1 && h == 1) {
      return buf;
    }
    else if (useTiffParser(ifds.get(ifdIndex))) {
      try (RandomAccessInputStream s = new RandomAccessInputStream(currentId)) {
        tiffParser = new TiffParser(s);
        tiffParser.setUse64BitOffsets(true);
        tiffParser.setYCbCrCorrection(false);
        return tiffParser.getSamples(ifds.get(ifdIndex), buf, x, y, w, h);
      }
      finally {
        tiffParser.getStream().close();
      }
    }

    if (initializedSeries != getCoreIndex() || initializedPlane != no) {
      IFD ifd = ifds.get(ifdIndex);

      long offset = ifd.getStripOffsets()[0];
      long byteCount = ifd.getStripByteCounts()[0];
      if (in != null) {
        in.close();
      }
      in = new RandomAccessInputStream(currentId);
      in.seek(offset);
      in.setLength(offset + byteCount);

      try {
        service.close();
        long[] markers = ifd.getIFDLongArray(MARKER_TAG);
        if (!use64Bit) {
          for (int i=0; i<markers.length; i++) {
            markers[i] = markers[i] & 0xffffffffL;
          }
        }
        if (markers != null) {
          service.setRestartMarkers(markers);
        }
        service.initialize(in, getSizeX(), getSizeY());
      }
      catch (ServiceException e) {
        throw new FormatException(e);
      }

      initializedSeries = getCoreIndex();
      initializedPlane = no;
    }
    service.getTile(buf, x, y, w, h);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);

    int currentSeries = getSeries();
    if (getCoreIndex() >= pyramidHeight) {
      return super.openThumbBytes(no);
    }

    int thumbX = getThumbSizeX();
    int thumbY = getThumbSizeY();
    int rgbCount = getRGBChannelCount();

    if (hasFlattenedResolutions()) {
      setSeries(pyramidHeight - 1);
    }
    else {
      setResolution(pyramidHeight - 1);
    }

    byte[] thumb = null;

    if (thumbX == getThumbSizeX() && thumbY == getThumbSizeY() &&
      rgbCount == getRGBChannelCount())
    {
      thumb = FormatTools.openThumbBytes(this, no);
      setSeries(currentSeries);
      setResolution(0);
    }
    else {
      // find the smallest series with the same aspect ratio
      for (int s=getSeriesCount()-1; s>=0; s--) {
        setSeries(s);
        if (thumbX == getThumbSizeX() && thumbY == getThumbSizeY() &&
          s != currentSeries && rgbCount == getRGBChannelCount())
        {
          thumb = FormatTools.openThumbBytes(this, no);
          break;
        }
      }
      setSeries(currentSeries);
      if (thumb == null) {
        thumb = FormatTools.openThumbBytes(this, no);
      }
    }
    return thumb;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    if (!fileOnly) {
      service.close();
      initializedSeries = -1;
      initializedPlane = -1;
      sizeZ = 1;
      pyramidHeight = 1;
      magnification = null;
      serialNumber = null;
      instrumentModel = null;
      if (tiffParser != null) {
        tiffParser.getStream().close();
      }
    }
    super.close(fileOnly);
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return 1024;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return 1024;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    RandomAccessInputStream s = new RandomAccessInputStream(id);
    use64Bit = s.length() >= Math.pow(2, 32);
    s.close();
    super.initFile(id);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    ifds = tiffParser.getMainIFDs();
    long[] ifdOffsets = tiffParser.getIFDOffsets();

    for (int i=0; i<ifds.size(); i++) {
      IFD ifd = ifds.get(i);
      ifd.remove(THUMB_TAG_2);

      TiffIFDEntry markerTag = (TiffIFDEntry) ifd.get(MARKER_TAG);

      if (markerTag != null) {
        if (markerTag.getValueOffset() > in.length()) {
          // can't rely upon the MARKER_TAG to be detected correctly
          ifd.remove(MARKER_TAG);
        }
      }

      // fix the offsets for > 4 GB files
      if (use64Bit) {
        try (RandomAccessInputStream stream = new RandomAccessInputStream(currentId)) {
          stream.order(ifd.isLittleEndian());

          // correct internal offsets within the IFD

          stream.seek(ifdOffsets[i]);
          int keyCount = stream.readUnsignedShort();
          int[] tag = new int[keyCount];
          long[] highOrder = new long[keyCount];

          for (int t=0; t<keyCount; t++) {
            tag[t] = stream.readUnsignedShort();
            IFDType type = IFDType.get(stream.readUnsignedShort());
            int valueCount = stream.readInt();
            int nValueBytes = valueCount * type.getBytesPerElement();
            if (nValueBytes > 4) {
              long offset = stream.readUnsignedInt();
              ifd.put(tag[t], new TiffIFDEntry(tag[t], type, valueCount, offset));
            }
            else {
              stream.skipBytes(4);
            }
          }
          stream.skipBytes(8);

          // 4 high bytes for each IFD entry are stored at end of IFD

          for (int t=0; t<keyCount; t++) {
            highOrder[t] = stream.readInt();

            if (highOrder[t] > 0) {
              highOrder[t] <<= 32;
              Object value = ifd.get(tag[t]);
              if (value instanceof TiffIFDEntry) {
                TiffIFDEntry entry = (TiffIFDEntry) value;
                long newOffset = entry.getValueOffset() + highOrder[t];
                TiffIFDEntry newEntry = new TiffIFDEntry(
                  entry.getTag(), entry.getType(),
                  entry.getValueCount(), newOffset);
                ifd.put(tag[t], newEntry);
              }
              else {
                ifd.put(tag[t], ((Number) value).longValue() + highOrder[t]);
              }
            }
          }

          // if there is more than one strip/tile, then each offset and count
          // needs to be corrected using separate tags that define
          // 4 high bytes for each offset and count

          tiffParser.fillInIFD(ifd);

          long[] offsetHighBytes = ifd.getIFDLongArray(OFFSET_HIGH_BYTES);
          long[] byteCountHighBytes = ifd.getIFDLongArray(BYTE_COUNT_HIGH_BYTES);

          int stripOffsetTag = IFD.STRIP_OFFSETS;
          long[] stripOffsets = ifd.getIFDLongArray(stripOffsetTag);
          if (stripOffsets == null) {
            stripOffsetTag = IFD.TILE_OFFSETS;
            stripOffsets = ifd.getIFDLongArray(stripOffsetTag);
          }
          int byteCountTag = IFD.STRIP_BYTE_COUNTS;
          long[] stripByteCounts = ifd.getIFDLongArray(byteCountTag);
          if (stripByteCounts == null) {
            byteCountTag = IFD.TILE_BYTE_COUNTS;
            stripByteCounts = ifd.getIFDLongArray(byteCountTag);
          }

          if (stripOffsets.length > 1 && offsetHighBytes != null) {
            for (int strip=0; strip<stripOffsets.length; strip++) {
              stripOffsets[strip] += (offsetHighBytes[strip] << 32);
              if (byteCountHighBytes != null) {
                stripByteCounts[strip] += (byteCountHighBytes[strip] << 32);
              }
            }
            ifd.putIFDValue(stripOffsetTag, stripOffsets);
            ifd.putIFDValue(byteCountTag, stripByteCounts);
          }
        }
      }
    }

    for (int i=1; i<ifds.size(); i++) {
      IFD ifd = ifds.get(i);

      if (ifd.getImageWidth() == ifds.get(0).getImageWidth() &&
        ifd.getImageLength() == ifds.get(0).getImageLength())
      {
        sizeZ++;
      } else if (sizeZ == 1)
      {
        boolean isPyramid;
        Object source_lens_value = ifd.getIFDValue(SOURCE_LENS);
        if (source_lens_value != null)
        {
          float source_lens = (Float) source_lens_value;
          // A value of -1 correspond to the macro image and a value of -2
          // correspond to the map image
          isPyramid = (source_lens != -1 && source_lens != -2);
        } else {
          // Assume the last IFD is the macro image
          isPyramid = i < ifds.size() - 1;
        }

        if (isPyramid) pyramidHeight++;
      }
    }

    // repopulate core metadata

    int seriesCount = pyramidHeight + (ifds.size() - pyramidHeight * sizeZ);

    for (int i=0; i<ifds.size(); i++) {
      IFD ifd = ifds.get(i);
      tiffParser.fillInIFD(ifd);

      int[] bpp = ifds.get(i).getBitsPerSample();
      for (int q=0; q<bpp.length; q++) {
        if (bpp[q] < 8) {
          bpp[q] = 8;
        }
      }
      ifds.get(i).putIFDValue(IFD.BITS_PER_SAMPLE, bpp);
    }

    core.clear();
    for (int s=0; s<seriesCount; s++) {
      CoreMetadata ms = new CoreMetadata();
      if (s == 0) {
        ms.resolutionCount = pyramidHeight;
        core.add(ms);
      }
      else if (s < pyramidHeight) {
        core.add(0, ms);
      }
      else {
        core.add(ms);
      }
    }

    for (int s=0; s<core.size(); s++) {
      for (int r = 0; r < core.size(s); r++) {
        int index = core.flattenedIndex(s, r);
        IFD ifd = ifds.get(getIFDIndex(index, 0));
        PhotoInterp p = ifd.getPhotometricInterpretation();
        int samples = ifd.getSamplesPerPixel();
        CoreMetadata ms = core.get(s, r);
        ms.rgb = samples > 1 || p == PhotoInterp.RGB;

        ms.sizeX = (int) ifd.getImageWidth();
        ms.sizeY = (int) ifd.getImageLength();
        ms.sizeZ = index < pyramidHeight ? sizeZ : 1;
        ms.sizeT = 1;
        ms.sizeC = ms.rgb ? samples : 1;
        ms.littleEndian = ifd.isLittleEndian();
        ms.indexed = p == PhotoInterp.RGB_PALETTE &&
          (get8BitLookupTable() != null || get16BitLookupTable() != null);
        ms.imageCount = ms.sizeZ * ms.sizeT;
        ms.pixelType = ifd.getPixelType();
        ms.metadataComplete = true;
        ms.interleaved = !useTiffParser(ifd);
        ms.falseColor = false;
        ms.dimensionOrder = "XYCZT";
        ms.thumbnail = index != 0;
      }
    }

    IFD first = ifds.get(0);

    Object source_lens_value = first.getIFDValue(SOURCE_LENS);
    if (source_lens_value != null)
    {
      magnification = Double.valueOf((float) source_lens_value);
    }
    String metadataTag = first.getIFDStringValue(METADATA_TAG);
    if (metadataTag != null) {
      String[] entries = metadataTag.split("\n");
      for (String entry : entries) {
        int eq = entry.indexOf('=');
        if (eq < 0) {
          continue;
        }
        String key = entry.substring(0, eq).trim();
        String value = entry.substring(eq + 1).trim();

        addGlobalMeta(key, value);

        // key name not a typo
        if (key.equals("Objective.Lens.Magnificant") && magnification == null) {
          magnification = new Double(value);
        }
        else if (key.equals("NDP.S/N")) {
          serialNumber = value;
        }
        else if (key.equals("Product")) {
          instrumentModel = value;
        }
      }
    }

    int captureMode = first.getIFDIntValue(CAPTURE_MODE);
    if (captureMode > 6) {
      // single channel data with more than 8 bits per pixel
      // the pyramid IFDs may still indicate 3 channels x 8 bits

      for (int r=0; r<core.size(0); r++) {
        int index = core.flattenedIndex(0, r);
        IFD ifd = ifds.get(getIFDIndex(index, 0));

        ifd.put(IFD.BITS_PER_SAMPLE, getBitsPerSample(captureMode));
        ifd.put(IFD.SAMPLES_PER_PIXEL, 1);
        ifd.put(IFD.PHOTOMETRIC_INTERPRETATION, PhotoInterp.BLACK_IS_ZERO);

        CoreMetadata ms = core.get(0, r);
        ms.sizeC = 1;
        ms.rgb = false;
        ms.pixelType = FormatTools.UINT16;
      }
    }
    String captureModeDescription = getCaptureMode(captureMode);

    addGlobalMeta("NDP.image version", first.get(VERSION));
    addGlobalMeta("Magnification", source_lens_value);
    addGlobalMeta("Slide center X (nm)", first.get(X_POSITION));
    addGlobalMeta("Slide center Y (nm)", first.get(Y_POSITION));
    addGlobalMeta("Slide center Z (nm)", first.get(Z_POSITION));
    addGlobalMeta("Tissue index", first.get(TISSUE_INDEX));
    addGlobalMeta("Reference", first.get(REFERENCE));
    addGlobalMeta("Filter set name", first.get(FILTER_SET_NAME));
    addGlobalMeta("Exposure ratio", first.get(EXPOSURE_RATIO));
    addGlobalMeta("Gain multiplier (red)", first.get(RED_MULTIPLIER));
    addGlobalMeta("Gain multiplier (green)", first.get(GREEN_MULTIPLIER));
    addGlobalMeta("Gain multiplier (blue)", first.get(BLUE_MULTIPLIER));
    addGlobalMeta("Capture mode", captureModeDescription);
    addGlobalMeta("Scanner serial number", first.get(SERIAL_NUMBER));
    addGlobalMeta("JPEG quality", first.get(JPEG_QUALITY));
    addGlobalMeta("Refocus interval (minutes)", first.get(REFOCUS_INTERVAL));
    addGlobalMeta("Focus offset (nm)", first.get(FOCUS_OFFSET));
    addGlobalMeta("Scanner firmware version", first.get(FIRMWARE_VERSION));
    addGlobalMeta("Label obscured", first.get(LABEL_OBSCURED));
    addGlobalMeta("Emission wavelength", first.get(WAVELENGTH));
    addGlobalMeta("Lamp age (hours)", first.get(LAMP_AGE));
    addGlobalMeta("Exposure time (microseconds)", first.get(EXPOSURE_TIME));
    addGlobalMeta("Focus time (seconds)", first.get(FOCUS_TIME));
    addGlobalMeta("Scan time (seconds)", first.get(SCAN_TIME));
    addGlobalMeta("File write time (seconds)", first.get(WRITE_TIME));
    addGlobalMeta("Fully automatic focus", first.get(FULLY_AUTO_FOCUS));
    addGlobalMeta("Default gamma correction", first.get(DEFAULT_GAMMA));
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);

    store.setInstrumentID(instrumentID, 0);
    store.setObjectiveID(objectiveID, 0, 0);

    if (instrumentModel != null) {
      store.setMicroscopeModel(instrumentModel, 0);
    }

    if (magnification != null) {
      store.setObjectiveNominalMagnification(magnification, 0, 0);
    }

    for (int i=0; i<getSeriesCount(); i++) {
      if (hasFlattenedResolutions() || i > 2) {
        store.setImageName("Series " + (i + 1), i);
      }
      else {
        switch (i) {
          case 0:
            store.setImageName("", i);
            break;
          case 1:
            store.setImageName("macro image", i);
            break;
          case 2:
            store.setImageName("macro mask image", i);
            break;
        }
      }

      store.setImageInstrumentRef(instrumentID, i);
      store.setObjectiveSettingsID(objectiveID, i);

      if (i > 0) {
        int ifdIndex = getIFDIndex(i, 0);
        String creationDate = ifds.get(ifdIndex).getIFDTextValue(IFD.DATE_TIME);
        creationDate = DateTools.formatDate(creationDate, DATE_FORMATS, ".");
        if (creationDate != null) {
          store.setImageAcquisitionDate(new Timestamp(creationDate), i);
        }

        double xResolution = ifds.get(ifdIndex).getXResolution();
        double yResolution = ifds.get(ifdIndex).getYResolution();

        Length sizeX = FormatTools.getPhysicalSizeX(xResolution);
        Length sizeY = FormatTools.getPhysicalSizeY(yResolution);

        if (sizeX != null) {
          store.setPixelsPhysicalSizeX(sizeX, i);
        }
        if (sizeY != null) {
          store.setPixelsPhysicalSizeY(sizeY, i);
        }
      }
      else {
        store.setImageDescription(serialNumber, i);

        for (int p=0; p<getImageCount(); p++) {
          int ifdIndex = getIFDIndex(i, p);
          IFD ifd = ifds.get(ifdIndex);

          Number x = (Number) ifd.getIFDValue(X_POSITION);
          Number y = (Number) ifd.getIFDValue(Y_POSITION);
          Number z = (Number) ifd.getIFDValue(Z_POSITION);

          int[] zct = getZCTCoords(p);
          if (x != null || y != null || z != null) {
            store.setPlaneTheZ(new NonNegativeInteger(zct[0]), i, p);
            store.setPlaneTheC(new NonNegativeInteger(zct[1]), i, p);
            store.setPlaneTheT(new NonNegativeInteger(zct[2]), i, p);
          }

          if (x != null) {
            store.setPlanePositionX(FormatTools.getStagePosition(
              x.doubleValue(), UNITS.NANOMETRE), i, p);
          }
          if (y != null) {
            store.setPlanePositionY(FormatTools.getStagePosition(
              y.doubleValue(), UNITS.NANOMETRE), i, p);
          }
          if (z != null) {
            store.setPlanePositionZ(FormatTools.getStagePosition(
              z.doubleValue(), UNITS.NANOMETRE), i, p);
          }

          if (zct[0] == 0 && zct[2] == 0) {
            String channelName = ifd.getIFDTextValue(FILTER_SET_NAME);
            store.setChannelName(channelName, i, zct[1]);
          }
        }
      }
    }
  }

  // -- Helper methods --

  private int getIFDIndex(int seriesIndex, int zIndex) {
    if (seriesIndex < pyramidHeight) {
      return zIndex * pyramidHeight + seriesIndex;
    }

    return sizeZ * pyramidHeight + (seriesIndex - pyramidHeight);
  }

  /**
   * @return true if tiles can be read using TiffParser logic, or
   *         false if the JPEGTurboService needs to be used
   */
  private boolean useTiffParser(IFD ifd) throws FormatException {
    return ifd.getImageWidth() <= MAX_SIZE ||
      ifd.getImageLength() <= MAX_SIZE ||
      ifd.getCompression() != TiffCompression.JPEG ||
      !ifd.containsKey(MARKER_TAG);
  }

  /**
   * Get the description of the given capture mode.
   */
  private String getCaptureMode(int mode) {
    switch (mode) {
      case 0:
        return "Brightfield";
      case 1:
        return "Fluorescence";
      case 2:
        return "Fluorescence with brightfield focusing";
      case 4:
        return "36 bit color fluorescence";
      case 5:
        return "36 bit color fluorescence with brightfield focusing";
      case 6:
        return "12 bit mono fluorescence";
      case 7:
        return "12 bit mono fluorescence with brightfield focusing";
      case 13:
        return "14 bit mono fluorescence";
      case 14:
        return "14 bit mono fluorescence with brightfield focusing";
      case 16:
        return "14 bit mono brightfield";
      case 17:
        return "16 bit mono fluorescence";
      case 18:
        return "16 bit mono fluorescence with brightfield focusing";
    }
    return "unknown";
  }

  /**
   * Get the number of bits per sample associated with the given capture mode.
   */
  private int getBitsPerSample(int mode) {
    switch (mode) {
      case 0:
      case 1:
      case 2:
        return 8;
      case 4:
      case 5:
      case 6:
      case 7:
        return 12;
      case 13:
      case 14:
      case 16:
        return 14;
      case 17:
      case 18:
        return 16;
    }
    return -1;
  }

}
