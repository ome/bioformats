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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffRational;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * FluoviewReader is the file format reader for
 * Olympus Fluoview TIFF files AND Andor Bio-imaging Division (ABD) TIFF files.
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FluoviewReader extends BaseTiffReader {

  // -- Constants --

  /** String identifying a Fluoview file. */
  private static final String FLUOVIEW_MAGIC_STRING = "FLUOVIEW";
  private static final String ANDOR_MAGIC_STRING = "Andor";

  /** Private TIFF tags */
  private static final int MMHEADER = 34361;
  private static final int MMSTAMP = 34362;

  private static final int TEMPERATURE = 4869;
  private static final int EXPOSURE_TIME = 4876;
  private static final int KINETIC_CYCLE_TIME = 4878;
  private static final int N_ACCUMULATIONS = 4879;
  private static final int ACQUISITION_CYCLE_TIME = 4881;
  private static final int READOUT_TIME = 4882;
  private static final int EM_DAC = 4885;
  private static final int N_FRAMES = 4890;
  private static final int HORIZONTAL_FLIP = 4896;
  private static final int VERTICAL_FLIP = 4897;
  private static final int CLOCKWISE = 4898;
  private static final int COUNTER_CLOCKWISE = 4899;
  private static final int VERTICAL_CLOCK_VOLTAGE = 4904;
  private static final int VERTICAL_SHIFT_SPEED = 4905;
  private static final int PRE_AMP_SETTING = 4907;
  private static final int CAMERA_SERIAL_SETTING = 4908;
  private static final int ACTUAL_TEMPERATURE = 4911;
  private static final int BASELINE_CLAMP = 4912;
  private static final int PRESCANS = 4913;
  private static final int MODEL = 4914;
  private static final int CHIP_SIZE_X = 4915;
  private static final int CHIP_SIZE_Y = 4916;
  private static final int BASELINE_OFFSET = 4944;

  /** Date format */
  private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss.SSS";

  // -- Fields --

  /** Pixel dimensions for this file. */
  private double voxelX = 1, voxelY = 1, voxelZ = 1, voxelC = 1, voxelT = 1;

  private String dimensionOrder;
  private String date = null;
  private int timeIndex = -1;
  private int fieldIndex = -1;
  private int montageIndex = -1;

  /** Timestamps for each plane, in seconds. */
  private double[][] stamps = null;
  private double[] zPositions = null;
  
  // hardware settings
  private String[] gains, voltages, offsets, channelNames, lensNA;
  private String mag, detectorManufacturer, objectiveManufacturer, comment;

  private Float temperature, exposureTime, readoutTime;
  private String model;

  private double[][] montageOffsets;
  private double[][] fieldOffsets;

  // -- Constructor --

  /** Constructs a new Fluoview TIFF reader. */
  public FluoviewReader() {
    super("Olympus Fluoview/ABD TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
    datasetDescription =
      "One or more .tif/.tiff files, and an optional .txt file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    String com = ifd.getComment();
    if (com == null) com = "";
    return (com.indexOf(FLUOVIEW_MAGIC_STRING) != -1 &&
      ifd.containsKey(new Integer(MMHEADER)) ||
      ifd.containsKey(new Integer(MMSTAMP))) ||
      com.startsWith(ANDOR_MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    int image = getImageIndex(no);

    if (tiffParser == null) {
      initTiffParser();
    }

    if (getSizeY() == ifds.get(0).getImageLength()) {
      tiffParser.getSamples(ifds.get(image), buf, x, y, w, h);
    }
    else {
      FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
      tiffParser.getSamples(ifds.get(0), buf, x, image, w, 1);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      voxelX = voxelY = voxelZ = voxelC = voxelT = 1;
      dimensionOrder = null;
      gains = voltages = offsets = channelNames = lensNA = null;
      mag = detectorManufacturer = objectiveManufacturer = comment = null;
      date = null;
      timeIndex = -1;
      stamps = null;
      fieldIndex = -1;
      montageIndex = -1;
      fieldOffsets = null;
      montageOffsets = null;
      model = null;
      temperature = null;
      exposureTime = null;
      readoutTime = null;
    }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    // First, we want to determine whether this file is a Fluoview TIFF.
    // Originally, Andor TIFF had its own reader; however, the two formats are
    // very similar, so it made more sense to merge the two formats into one
    // reader.

    short[] s = ifds.get(0).getIFDShortArray(MMHEADER);
    if (s == null) {
      initAlternateMetadata();
      return;
    }
    byte[] mmheader = shortArrayToBytes(s);

    RandomAccessInputStream ras = new RandomAccessInputStream(mmheader);
    ras.order(isLittleEndian());

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      put("Header Flag", ras.readShort());
      put("Image Type", ras.read());

      String name = ras.readString(257);
      name = name.substring(0, name.indexOf("\0"));
      put("Image name", name);

      ras.skipBytes(4); // skip pointer to data field

      put("Number of colors", ras.readInt());
      ras.skipBytes(4); // skip pointer to palette field
      ras.skipBytes(4); // skip pointer to other palette field

      put("Comment size", ras.readInt());
      ras.skipBytes(4); // skip pointer to comment field
    }
    else ras.skipBytes(284);

    // read dimension information
    String[] names = new String[10];
    int[] sizes = new int[10];
    double[] resolutions = new double[10];
    for (int i=0; i<10; i++) {
      names[i] = ras.readString(16);
      sizes[i] = ras.readInt();
      double origin = ras.readDouble();
      resolutions[i] = ras.readDouble();

      put("Dimension " + (i + 1) + " Name", names[i]);
      put("Dimension " + (i + 1) + " Size", sizes[i]);
      put("Dimension " + (i + 1) + " Origin", origin);
      put("Dimension " + (i + 1) + " Resolution", resolutions[i]);
      put("Dimension " + (i + 1) + " Units", ras.readString(64));
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      ras.skipBytes(4); // skip pointer to spatial position data

      put("Map type", ras.readShort());
      put("Map min", ras.readDouble());
      put("Map max", ras.readDouble());
      put("Min value", ras.readDouble());
      put("Max value", ras.readDouble());

      ras.skipBytes(4); // skip pointer to map data

      put("Gamma", ras.readDouble());
      put("Offset", ras.readDouble());

      // read gray channel data
      put("Gray Channel Name", ras.readString(16));
      put("Gray Channel Size", ras.readInt());
      put("Gray Channel Origin", ras.readDouble());
      put("Gray Channel Resolution", ras.readDouble());
      put("Gray Channel Units", ras.readString(64));

      ras.skipBytes(4); // skip pointer to thumbnail data

      put("Voice field", ras.readInt());
      ras.skipBytes(4); // skip pointer to voice field

      // now we need to read the MMSTAMP data to determine dimension order

      readStamps();
    }
    ras.close();

    // calculate the dimension order and axis sizes

    CoreMetadata m = core.get(0);

    dimensionOrder = "XY";
    int seriesCount = 1;
    m.sizeZ = m.sizeC = m.sizeT = 1;

    for (int i=0; i<10; i++) {
      String name = names[i];
      int size = sizes[i];
      double voxel = resolutions[i];
      if (name == null || size == 0) continue;
      name = name.toLowerCase().trim();
      if (name.length() == 0) continue;

      if (name.equals("x")) {
        voxelX = voxel;
      }
      else if (name.equals("y")) {
        voxelY = voxel;
      }
      else if (name.equals("event")) {
        m.sizeZ *= size;
        if (dimensionOrder.indexOf('Z') == -1) {
          dimensionOrder += 'Z';
        }
        if (Double.compare(voxelZ, 1) == 0) {
          voxelZ = voxel;
        }
      }
      else if (name.equals("z")) {
        m.sizeZ *= size;
        if (dimensionOrder.indexOf('Z') == -1) {
          dimensionOrder += 'Z';
        }
        
        ArrayList<Double> uniqueZ = new ArrayList<Double>();
        if (i > 1 && stamps != null) {
          zPositions = stamps[i - 2];
          if (zPositions != null) {
            for (Double z : zPositions) {
              BigDecimal bd = new BigDecimal(z);
              bd = bd.setScale(10, RoundingMode.HALF_UP);
              if (!uniqueZ.contains(bd.doubleValue())) uniqueZ.add(bd.doubleValue());
            }
          }
        }
        if (uniqueZ.size() > 1 && uniqueZ.size() == size) {
          BigDecimal lastZ = BigDecimal.valueOf(uniqueZ.get(uniqueZ.size() - 1));
          BigDecimal firstZ = BigDecimal.valueOf(uniqueZ.get(0));
          BigDecimal zRange = (lastZ.subtract(firstZ)).abs();
          BigDecimal zSize = BigDecimal.valueOf((double)(getSizeZ() - 1));
          MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
          voxelZ = zRange.divide(zSize, mc).doubleValue();
          // Need to convert from millimetre to micrometre
          voxelZ *= Math.pow(10, 3);
        }
        else {
          voxelZ = voxel;
        }
      }
      else if (name.equals("ch") || name.equals("wavelength")) {
        m.sizeC *= size;
        if (dimensionOrder.indexOf('C') == -1) {
          dimensionOrder += 'C';
        }
        voxelC = voxel;
      }
      else if (name.equals("time") || name.equals("t") ||
        name.equals("animation"))
      {
        m.sizeT *= size;
        if (dimensionOrder.indexOf('T') == -1) {
          dimensionOrder += 'T';
        }
        voxelT = voxel;
        timeIndex = i - 2;
      }
      else {
        if (dimensionOrder.indexOf('S') == -1) dimensionOrder += 'S';
        seriesCount *= size;

        if (name.equals("montage")) montageIndex = i - 2;
        else if (name.equals("xy")) fieldIndex = i - 2;
      }
    }

    if (dimensionOrder.indexOf('Z') == -1) dimensionOrder += 'Z';
    if (dimensionOrder.indexOf('T') == -1) dimensionOrder += 'T';
    if (dimensionOrder.indexOf('C') == -1) dimensionOrder += 'C';
    if (dimensionOrder.indexOf('S') == -1) dimensionOrder += 'S';

    m.imageCount = ifds.size() / seriesCount;
    if (getSizeZ() > getImageCount()) m.sizeZ = getImageCount();
    if (getSizeT() > getImageCount()) m.sizeT = getImageCount();
    if (getSizeZ() * getSizeC() * getSizeT() > getImageCount()) {
      int diff = getSizeZ() * getSizeC() * getSizeT() - getImageCount();
      if (diff == getSizeC()) {
        if (getSizeZ() > 1) m.sizeZ--;
        else if (getSizeT() > 1) m.sizeT--;
        else m.sizeC /= getSizeC();
      }
    }

    if (getImageCount() == 1 && (getSizeT() == getSizeY() ||
      getSizeZ() == getSizeY()) && (getSizeT() > getImageCount() ||
      getSizeZ() > getImageCount()))
    {
      m.sizeY = 1;
      m.imageCount = getSizeZ() * getSizeC() * getSizeT();
    }
    m.dimensionOrder = dimensionOrder.replaceAll("S", "");

    if (getPixelType() == FormatTools.UINT32) {
      m.pixelType = FormatTools.FLOAT;
    }

    if (seriesCount > 1) {
      core.clear();
      for (int i=0; i<seriesCount; i++) {
        core.add(m);
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // cut up the comment, if necessary
      comment = ifds.get(0).getComment();

      gains = new String[getSizeC()];
      offsets = new String[getSizeC()];
      voltages = new String[getSizeC()];
      channelNames = new String[getSizeC()];
      lensNA = new String[getSizeC()];

      parsePageName();
      parseComment();
      addGlobalMeta("Comment", comment);
    }
  }

  /* @see loci.formats.in.BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    if (date != null) {
      store.setImageAcquisitionDate(new Timestamp(date), 0);
    }

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
      return;
    }

    if (ifds.get(0).get(MMHEADER) == null) {
      initAlternateMetadataStore();
      return;
    }

    store.setImageDescription(comment, 0);

    // link Instrument and Image
    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);
    store.setImageInstrumentRef(instrumentID, 0);

    // populate timing data
    if (timeIndex >= 0) {
      for (int s=0; s<getSeriesCount(); s++) {
        setSeries(s);
        for (int i=0; i<getImageCount(); i++) {
          int index = getImageIndex(i);
          store.setPlaneDeltaT(new Time(stamps[timeIndex][index], UNITS.SECOND), s, i);
        }
      }
      setSeries(0);
    }

    // populate Dimensions
    for (int i=0; i<getSeriesCount(); i++) {
      Length sizeX = FormatTools.getPhysicalSizeX(voxelX);
      Length sizeY = FormatTools.getPhysicalSizeY(voxelY);
      Length sizeZ = FormatTools.getPhysicalSizeZ(voxelZ);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, i);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, i);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, i);
      }
      store.setPixelsTimeIncrement(new Time(voxelT, UNITS.SECOND), i);

      int montage = getMontage(i);
      int field = getField(i);

      double posX = 0d, posY = 0d, posZ = 0d;

      if (montageOffsets != null && montage < montageOffsets.length) {
        if (montageOffsets[montage].length > 0) {
          posX += montageOffsets[montage][0];
        }
        if (montageOffsets[montage].length > 1) {
          posY += montageOffsets[montage][1];
        }
        if (montageOffsets[montage].length > 2) {
          posZ += montageOffsets[montage][2];
        }
      }
      if (fieldOffsets != null && field < fieldOffsets.length) {
        if (fieldOffsets[field].length > 0) {
          posX += fieldOffsets[field][0];
        }
        if (fieldOffsets[field].length > 1) {
          posY += fieldOffsets[field][1];
        }
        if (fieldOffsets[field].length > 2) {
          posZ += fieldOffsets[field][2];
        }
      }

      for (int image=0; image<getImageCount(); image++) {
        final Length xl = new Length(posX, UNITS.REFERENCEFRAME);
        final Length yl = new Length(posY, UNITS.REFERENCEFRAME);
        Length zl = new Length(posZ, UNITS.REFERENCEFRAME);
        if (zPositions != null && zPositions.length > image) {
          zl = new Length(zPositions[image], UNITS.MICROMETER);
        }
        store.setPlanePositionX(xl, i, image);
        store.setPlanePositionY(yl, i, image);
        store.setPlanePositionZ(zl, i, image);
      }
    }

    // populate LogicalChannel data

    if (channelNames != null) {
      for (int i=0; i<getSizeC(); i++) {
        if (channelNames[i] != null) {
          store.setChannelName(channelNames[i].trim(), 0, i);
        }
      }
    }

    // populate Detector data

    for (int i=0; i<getSizeC(); i++) {
      if (voltages != null && voltages[i] != null) {
        store.setDetectorSettingsVoltage(
                new ElectricPotential(new Double(voltages[i]), UNITS.VOLT), 0, i);
      }
      if (gains != null && gains[i] != null) {
        store.setDetectorSettingsGain(new Double(gains[i]), 0, i);
      }
      if (offsets != null && offsets[i] != null) {
        store.setDetectorSettingsOffset(new Double(offsets[i]), 0, i);
      }
      store.setDetectorType(getDetectorType("Other"), 0, i);
      if (detectorManufacturer != null) {
        store.setDetectorManufacturer(detectorManufacturer, 0, i);
      }

      // link DetectorSettings to an actual Detector
      String detectorID = MetadataTools.createLSID("Detector", 0, i);
      store.setDetectorID(detectorID, 0, i);
      store.setDetectorSettingsID(detectorID, 0, i);
    }

    // populate Objective data

    if (mag != null && mag.toLowerCase().endsWith("x")) {
      mag = mag.substring(0, mag.length() - 1);
    }
    else if (mag == null) mag = "1";

    store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
    store.setObjectiveImmersion(getImmersion("Other"), 0, 0);

    if (objectiveManufacturer != null) {
      String[] objectiveData = objectiveManufacturer.split(" ");
      store.setObjectiveModel(objectiveData[0], 0, 0);
      if (objectiveData.length > 2) {
        store.setObjectiveImmersion(getImmersion(objectiveData[2]), 0, 0);
      }
    }

    if (mag != null) {
      store.setObjectiveCalibratedMagnification(new Double(mag), 0, 0);
    }

    if (lensNA != null) {
      for (int i=0; i<getSizeC(); i++) {
        if (lensNA[i] != null) {
          store.setObjectiveLensNA(new Double(lensNA[i]), 0, i);
        }
      }
    }

    // link Objective to Image using ObjectiveSettings
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);
    store.setObjectiveSettingsID(objectiveID, 0);
  }

  // -- Helper methods --

  private void initAlternateMetadata() throws FormatException, IOException {
    IFD firstIFD = ifds.get(0);

    temperature = (Float) firstIFD.getIFDValue(TEMPERATURE);
    exposureTime = (Float) firstIFD.getIFDValue(EXPOSURE_TIME);
    Float kineticCycleTime = (Float) firstIFD.getIFDValue(KINETIC_CYCLE_TIME);
    Double nAccumulations = (Double) firstIFD.getIFDValue(N_ACCUMULATIONS);
    Float acquisitionCycleTime =
      (Float) firstIFD.getIFDValue(ACQUISITION_CYCLE_TIME);
    readoutTime = (Float) firstIFD.getIFDValue(READOUT_TIME);
    Double emDAC = (Double) firstIFD.getIFDValue(EM_DAC);
    Double nFrames = (Double) firstIFD.getIFDValue(N_FRAMES);
    Double horizontalFlip = (Double) firstIFD.getIFDValue(HORIZONTAL_FLIP);
    Double verticalFlip = (Double) firstIFD.getIFDValue(VERTICAL_FLIP);
    Double clockwise = (Double) firstIFD.getIFDValue(CLOCKWISE);
    Double counterClockwise = (Double) firstIFD.getIFDValue(COUNTER_CLOCKWISE);
    Double verticalClockVoltage =
      (Double) firstIFD.getIFDValue(VERTICAL_CLOCK_VOLTAGE);
    Float verticalShiftSpeed =
      (Float) firstIFD.getIFDValue(VERTICAL_SHIFT_SPEED);
    Float preamp = (Float) firstIFD.getIFDValue(PRE_AMP_SETTING);
    Double serialSetting = (Double) firstIFD.getIFDValue(CAMERA_SERIAL_SETTING);
    Float actualTemperature = (Float) firstIFD.getIFDValue(ACTUAL_TEMPERATURE);
    Double baselineClamp = (Double) firstIFD.getIFDValue(BASELINE_CLAMP);
    Double prescans = (Double) firstIFD.getIFDValue(PRESCANS);
    model = firstIFD.getIFDTextValue(MODEL);
    Double chipSizeX = (Double) firstIFD.getIFDValue(CHIP_SIZE_X);
    Double chipSizeY = (Double) firstIFD.getIFDValue(CHIP_SIZE_Y);
    Double baselineOffset = (Double) firstIFD.getIFDValue(BASELINE_OFFSET);

    addGlobalMeta("Temperature", temperature);
    addGlobalMeta("Exposure time (in seconds)", exposureTime);
    addGlobalMeta("Kinetic cycle time", kineticCycleTime);
    addGlobalMeta("Number of accumulations", nAccumulations);
    addGlobalMeta("Acquisition cycle time", acquisitionCycleTime);
    addGlobalMeta("Readout time", readoutTime);
    addGlobalMeta("EM DAC", emDAC);
    addGlobalMeta("Number of frames", nFrames);
    addGlobalMeta("Horizontal flip", horizontalFlip);
    addGlobalMeta("Vertical flip", verticalFlip);
    addGlobalMeta("Clockwise rotation", clockwise);
    addGlobalMeta("Counter-clockwise rotation", counterClockwise);
    addGlobalMeta("Vertical clock voltage", verticalClockVoltage);
    addGlobalMeta("Vertical shift speed", verticalShiftSpeed);
    addGlobalMeta("Pre-amp", preamp);
    addGlobalMeta("Camera serial setting", serialSetting);
    addGlobalMeta("Actual temperature", actualTemperature);
    addGlobalMeta("Baseline clamp", baselineClamp);
    addGlobalMeta("Prescans", prescans);
    addGlobalMeta("Camera model", model);
    addGlobalMeta("Chip size X", chipSizeX);
    addGlobalMeta("Chip size Y", chipSizeY);
    addGlobalMeta("Baseline offset", baselineOffset);
  }

  private void initAlternateMetadataStore() throws FormatException {
    MetadataStore store = makeFilterMetadata();
    store.setImagingEnvironmentTemperature(
      new Temperature(new Double(temperature.floatValue()), UNITS.CELSIUS), 0);

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    String detectorID = MetadataTools.createLSID("Detector", 0, 0);
    store.setInstrumentID(instrumentID, 0);
    store.setDetectorID(detectorID, 0, 0);
    store.setDetectorModel(model, 0, 0);

    store.setImageInstrumentRef(instrumentID, 0);

    if (exposureTime != null) {
      for (int i=0; i<getImageCount(); i++) {
        store.setPlaneExposureTime(new Time(new Double(exposureTime.floatValue()), UNITS.SECOND), 0, i);
      }
    }

    for (int i=0; i<getEffectiveSizeC(); i++) {
      store.setDetectorSettingsID(detectorID, 0, i);
      store.setDetectorSettingsReadOutRate(
        new Frequency(new Double(readoutTime.floatValue()), UNITS.HERTZ), 0, i);
    }
  }

  private int getImageIndex(int no) {
    // the 'series' axis can be in any position relative to Z, C and T
    // we need to convert the plane number within the series into an IFD number
    if (getSeriesCount() == 1) return no;
    int[] lengths = new int[4];
    int[] pos = getZCTCoords(no);
    int[] realPos = new int[4];
    for (int i=2; i<dimensionOrder.length(); i++) {
      char axis = dimensionOrder.charAt(i);
      if (axis == 'Z') {
        lengths[i - 2] = getSizeZ();
        realPos[i - 2] = pos[0];
      }
      else if (axis == 'C') {
        lengths[i - 2] = getEffectiveSizeC();
        realPos[i - 2] = pos[1];
      }
      else if (axis == 'T') {
        lengths[i - 2] = getSizeT();
        realPos[i - 2] = pos[2];
      }
      else if (axis == 'S') {
        lengths[i - 2] = getSeriesCount();
        realPos[i - 2] = getSeries();
      }
    }

    return FormatTools.positionToRaster(lengths, realPos);
  }

  private void readStamps() throws FormatException, IOException {
    stamps = new double[8][ifds.size()];
    for (int i=0; i<ifds.size(); i++) {
      byte[] stamp = shortArrayToBytes(ifds.get(i).getIFDShortArray(MMSTAMP));
      RandomAccessInputStream ras = new RandomAccessInputStream(stamp);
      ras.order(isLittleEndian());

      // each stamp is 8 doubles, representing the position on dimensions 3-10
      for (int j=0; j<8; j++) {
        stamps[j][i] = ras.readDouble() / 1000;
      }
      ras.close();
    }
  }

  private byte[] shortArrayToBytes(short[] s) {
    byte[] b = new byte[s.length];
    for (int i=0; i<s.length; i++) {
      b[i] = (byte) s[i];
    }
    return b;
  }

  private void parsePageName() {
    String pageName = ifds.get(0).getIFDTextValue(IFD.PAGE_NAME);
    if (pageName == null) return;
    String[] lines = pageName.split("\n");
    for (String line : lines) {
      if (line.startsWith("Resolution")) {
        String[] resolutions = line.split("\t");
        if (resolutions.length > 1) {
          voxelX = Double.parseDouble(resolutions[1].trim());
        }
        if (resolutions.length > 2) {
          voxelY = Double.parseDouble(resolutions[2].trim());
        }

        break;
      }
    }
  }

  private void parseComment() {
    if (comment != null) {
      // this is an INI-style comment, with one key/value pair per line

      String[] lines = comment.split("\n");
      for (String token : lines) {
        token = token.trim();
        int eq = token.indexOf('=');
        if (eq != -1) {
          String key = token.substring(0, eq);
          String value = token.substring(eq + 1);
          addGlobalMeta(key, value);
          if (key.startsWith("Gain Ch")) {
            int index = Integer.parseInt(key.substring(7).trim());
            if (index > 0 && index <= gains.length) {
              gains[index - 1] = value;
            }
          }
          else if (key.startsWith("PMT Voltage Ch")) {
            int index = Integer.parseInt(key.substring(14).trim());
            if (index > 0 && index <= voltages.length) {
              voltages[index - 1] = value;
            }
          }
          else if (key.startsWith("Offset Ch")) {
            int index = Integer.parseInt(key.substring(9).trim());
            if (index > 0 && index <= offsets.length) {
              offsets[index - 1] = value;
            }
          }
          else if (key.equals("Magnification")) mag = value;
          else if (key.equals("System Configuration")) {
            detectorManufacturer = value;
          }
          else if (key.equals("Objective Lens")) objectiveManufacturer = value;
          else if (key.startsWith("Channel ") && key.endsWith("Dye")) {
            for (int i=0; i<channelNames.length; i++) {
              if (channelNames[i] == null) {
                channelNames[i] = value;
                break;
              }
            }
          }
          else if (key.startsWith("Confocal Aperture-Ch")) {
            int index = Integer.parseInt(key.substring(20).trim());
            if (index > 0 && index <= lensNA.length) {
              lensNA[index - 1] = value.substring(0, value.length() - 2);
            }
          }
          else if (key.equals("Date")) {
            date = value;
          }
          else if (key.equals("Time")) {
            date += " " + value;
          }
          else if (key.equals("MontageOffsets")) {
            String[] offsets = value.split("\t");
            montageOffsets = new double[offsets.length - 1][3];
            for (int i=1; i<offsets.length; i++) {
              String[] v = offsets[i].trim().split(",");
              for (int j=0; j<v.length; j++) {
                montageOffsets[i - 1][j] = Double.parseDouble(v[j].trim());
              }
            }
          }
          else if (key.equals("XYFields")) {
            String[] offsets = value.split("\t");
            fieldOffsets = new double[offsets.length - 1][3];
            for (int i=1; i<offsets.length; i++) {
              String[] v = offsets[i].trim().split(",");
              for (int j=0; j<v.length; j++) {
                try {
                  fieldOffsets[i - 1][j] = Double.parseDouble(v[j].trim());
                }
                catch (NumberFormatException e) { }
              }
            }
          }
        }
        else if (token.startsWith("Z") && token.indexOf(" um ") != -1) {
          // looking for "Z - x um in y planes"
          String z = token.substring(token.indexOf('-') + 1);
          z = z.replaceAll("\\p{Alpha}", "").trim();
          int firstSpace = z.indexOf(' ');
          double size = Double.parseDouble(z.substring(0, firstSpace));
          double nPlanes = Double.parseDouble(z.substring(firstSpace).trim());
          voxelZ = size / nPlanes;
        }
      }
      if (date != null) {
        date = DateTools.formatDate(date.trim(),
          new String[] {"MM/dd/yyyy hh:mm:ss a", "MM-dd-yyyy hh:mm:ss","MM/dd/yyyy H:mm:ss"}, true);
        Timestamp timestamp = Timestamp.valueOf(date);
        if (timeIndex >= 0 && timestamp != null) {
          long ms = timestamp.asInstant().getMillis();
          int nChars = String.valueOf(getImageCount()).length();
          for (int i=0; i<getImageCount(); i++) {
            int[] zct = getZCTCoords(i);
            String key = String.format(
              "Timestamp for Z=%2s, C=%2s, T=%2s", zct[0], zct[1], zct[2]);
            long stamp = ms + (long) (stamps[timeIndex][i] * 1000);
            addGlobalMeta(key,
              DateTools.convertDate(stamp, DateTools.UNIX, DATE_FORMAT));
          }
        }
      }

      int start = comment.indexOf("[Version Info]");
      int end = comment.indexOf("[Version Info End]");
      if (start != -1 && end != -1 && end > start) {
        comment = comment.substring(start + 14, end).trim();
        start = comment.indexOf('=') + 1;
        end = comment.indexOf("\n");
        if (end > start) comment = comment.substring(start, end).trim();
        else comment = comment.substring(start).trim();
      }
      else comment = "";
    }
  }

  private int getMontage(int seriesIndex) {
    if (montageOffsets == null && fieldOffsets == null) return 0;
    int[] pos = getPos(seriesIndex);
    return montageIndex < fieldIndex ? pos[0] : pos[1];
  }

  private int getField(int seriesIndex) {
    if (montageOffsets == null && fieldOffsets == null) return 0;
    int[] pos = getPos(seriesIndex);
    return montageIndex < fieldIndex ? pos[1] : pos[0];
  }

  private int[] getPos(int seriesIndex) {
    int[] lengths = new int[2];
    if (montageIndex < fieldIndex) {
      lengths[0] = montageOffsets == null ? 1 : montageOffsets.length;
      lengths[1] = fieldOffsets == null ? 1 : fieldOffsets.length;
    }
    else {
      lengths[1] = montageOffsets == null ? 1 : montageOffsets.length;
      lengths[0] = fieldOffsets == null ? 1 : fieldOffsets.length;
    }
    if (lengths[0] == 0) lengths[0] = 1;
    if (lengths[1] == 0) lengths[1] = 1;
    return FormatTools.rasterToPosition(lengths, seriesIndex);
  }

}
