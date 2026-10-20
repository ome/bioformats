/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2025 Open Microscopy Environment:
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.services.POIService;
import loci.formats.services.POIServiceImpl;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.xml.model.primitives.Timestamp;

/**
 * ZeissXRMReader is the file format reader for Zeiss X-Ray Microscopy
 * .txm and .txrm files.
 */
public class ZeissXRMReader extends FormatReader {

  // -- Constants --

  private static final String DATESTAMP = "mm/dd/yyyy HH:mm:ss.SSS";

  // parent paths for various metadata values
  private static final String IMAGE_INFO_PATH = "Root Entry/ImageInfo/";
  private static final String RECON_SETTINGS_PATH = "Root Entry/ReconSettings/";
  private static final String AUTORECON_PATH = "Root Entry/AutoRecon/";
  private static final String REFERENCE_PATH = "Root Entry/ReferenceData/";

  // prefixes for organizing original metadata
  private static final String POSITIONS = "Positions: ";
  private static final String DATASET = "Dataset Info: ";
  private static final String RECON_SETTINGS = "Reconstruction Settings: ";
  private static final String IMAGE_DETAILS = "Image Details: ";
  private static final String SOURCE_ASSEMBLY = "Source Assembly Info: ";
  private static final String GENERAL_PARAMS = "General Parameters: ";
  private static final String PROJECTION = "Projection Info: ";

  // -- Fields --

  private transient POIService poi;
  private List<String> imagePaths = new ArrayList<String>();

  private transient Double pixelSize = null;
  private transient double[] exposureTimes = null;
  private transient double[] current = null;
  private transient double[] voltage = null;
  private transient double[] xPos = null;
  private transient double[] yPos = null;
  private transient double[] zPos = null;
  private transient String[] datestamps = null;

  // -- Constructor --

  /** Constructs a new Zeiss XRM reader. */
  public ZeissXRMReader() {
    super("Zeiss XRM", new String[] {"txm", "txrm"});
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
    suffixSufficient = true;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readInt() == POIServiceImpl.POI_MAGIC_BYTES;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (poi == null) {
      initPOIService();
    }
    int bpp = FormatTools.getBytesPerPixel(getPixelType());

    try (RandomAccessInputStream stream = poi.getDocumentStream(imagePaths.get(no))) {
      int skipBeginRow = x * bpp;
      int skipEndRow = bpp * (getSizeX() - w - x);
      int rowLen = w * bpp;
      for (int row=h-1; row>=0; row--) {
        stream.skipBytes(skipBeginRow);
        stream.read(buf, row * rowLen, rowLen);
        stream.skipBytes(skipEndRow);
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (poi != null) poi.close();
      poi = null;
      imagePaths.clear();

      pixelSize = null;
      exposureTimes = null;
      current = null;
      voltage = null;
      xPos = null;
      yPos = null;
      zPos = null;
      datestamps = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    String suffix = getCurrentFile().substring(getCurrentFile().lastIndexOf(".") + 1);
    boolean isTXM = suffix.equalsIgnoreCase("txm");
    boolean isTXRM = suffix.equalsIgnoreCase("txrm");

    CoreMetadata m = core.get(0);

    initPOIService();

    final List<String> allFiles = poi.getDocumentList();
    allFiles.sort(null);
    if (allFiles.isEmpty()) {
      throw new FormatException(
        "No files were found - the file may be corrupt.");
    }

    for (String originalFile : allFiles) {
      String name = originalFile.replaceAll("\\\\", "/");
      if (name.startsWith("Root Entry/ImageData")) {
        int index = Integer.parseInt(name.substring(name.lastIndexOf("Image") + 5)) - 1;
        while (index >= imagePaths.size()) {
          imagePaths.add(null);
        }
        imagePaths.set(index, originalFile);

        // this is just a raw image with no metadata, so don't try to open it here
        continue;
      }
      try (RandomAccessInputStream stream = poi.getDocumentStream(originalFile)) {
        stream.order(true);

        // type-specific metadata may be present in both types of files,
        // but will likely be invalid (sometimes in a misleading or confusing way)

        if (isTXM) {
          handleTXMMetadata(name, stream);
        }
        else if (isTXRM) {
          handleTXRMMetadata(name, stream);
        }

        String paramsPrefix = isTXM ? GENERAL_PARAMS : PROJECTION;

        if (name.equals(IMAGE_INFO_PATH + "ImageWidth")) {
          m.sizeX = stream.readInt();
          addGlobalMeta(IMAGE_DETAILS + "Image width (pixels)", m.sizeX);
        }
        else if (name.equals(IMAGE_INFO_PATH + "ImageHeight")) {
          m.sizeY = stream.readInt();
          addGlobalMeta(IMAGE_DETAILS + "Image height (pixels)", m.sizeY);
        }
        else if (name.equals(IMAGE_INFO_PATH + "DataType")) {
          int type = stream.readInt();
          m.pixelType = getPixelType(type);
          if (isTXM) {
            addGlobalMeta(RECON_SETTINGS + "Output data type", getMetadataPixelType(type));
          }
          addGlobalMeta(IMAGE_DETAILS + "Data type", getMetadataPixelType(type));
        }
        else if (name.equals(IMAGE_INFO_PATH + "FileType")) {
          addGlobalMeta(IMAGE_DETAILS + "File type", stream.readString(4));
        }
        else if (name.equals(IMAGE_INFO_PATH + "PixelSize")) {
          pixelSize = (double) stream.readFloat();
          addGlobalMeta(IMAGE_DETAILS + "Pixel size (µm)", formatDouble(pixelSize));
        }
        else if (name.equals(IMAGE_INFO_PATH + "AcquisitionMode")) {
          int mode = stream.readInt();
          String modeValue = String.valueOf(mode);
          if (mode == 0) {
            modeValue = "Tomography";
          }
          else if (mode == 10) {
            modeValue = "Recon";
          }
          else {
            LOGGER.warn("Could not identify acquisition mode: {}", mode);
          }

          addGlobalMeta(IMAGE_DETAILS + "Acquisition mode", modeValue);
        }
        else if (name.equals(IMAGE_INFO_PATH + "Current") && current == null) {
          current = readAsDoubles(stream);
          addGlobalMeta(SOURCE_ASSEMBLY + "Current (µA)", current[0]);
          if (isTXM) {
            addGlobalMeta(GENERAL_PARAMS + "X-ray current (µA)", current[0]);
          }
        }
        else if (name.equals(IMAGE_INFO_PATH + "XrayVoltage") && voltage == null) {
          voltage = readAsDoubles(stream);
          addMetadataList(voltage, paramsPrefix + "X-ray voltage (kV)", !isTXM);
        }
        else if (name.equals(IMAGE_INFO_PATH + "SourceFilterName")) {
          String sourceFilter = readAsString(stream);
          addGlobalMeta(SOURCE_ASSEMBLY + "Source Filter Name", sourceFilter);
          addGlobalMeta(paramsPrefix + "Source filter name", sourceFilter);
        }
        else if (name.equals(IMAGE_INFO_PATH + "Voltage")) {
          addGlobalMeta(SOURCE_ASSEMBLY + "Voltage (kV)", stream.readFloat());
        }
        else if (name.equals("Root Entry/exeVersion")) {
          addGlobalMeta(DATASET + "Executable version", readAsString(stream));
        }
        else if (name.equals("Root Entry/DetAssemblyInfo/LensInfo/LensName")) {
          String objective = readAsString(stream);
          addGlobalMeta(paramsPrefix + "Objective name", objective);
        }
        else if (name.equals(IMAGE_INFO_PATH + "CameraNumberOfFramesPerImage")) {
          addGlobalMeta(paramsPrefix + "Frames per image", stream.readInt());
        }
        else if (name.equals(IMAGE_INFO_PATH + "NoOfImagesAveraged")) {
          addGlobalMeta(paramsPrefix + "Images per projection", stream.readInt());
        }
        else if (name.equals(IMAGE_INFO_PATH + "ExpTimes")) {
          exposureTimes = readAsDoubles(stream);
          if (exposureTimes.length > 1) {
            addGlobalMeta(paramsPrefix + "Exposure time (s)", formatDouble(exposureTimes[0]));
          }
        }
        else if (name.equals(IMAGE_INFO_PATH + "CameraBinning")) {
          addGlobalMeta(paramsPrefix + "Camera binning", stream.readInt());
        }
        else if (stream.getFilePointer() == 0) {
          LOGGER.trace("Skipped '{}' ({}) bytes", name, stream.length());
        }
      }
    }

    m.sizeZ = imagePaths.size();
    m.sizeT = 1;
    m.sizeC = 1;
    m.imageCount = getSizeZ() * getSizeC() * getSizeT();
    m.dimensionOrder = "XYZTC";
    m.littleEndian = true;

    m.moduloZ.type = FormatTools.ROTATION;
    m.moduloZ.step = 1;
    m.moduloZ.start = 0;
    m.moduloZ.end = m.sizeZ - 1;

    addGlobalMeta(DATASET + "Data file name", new Location(getCurrentFile()).getAbsolutePath());
    if (isTXM) {
      addGlobalMeta(RECON_SETTINGS + "Output file-format", suffix);

      if (current != null && voltage != null) {
        addGlobalMeta(GENERAL_PARAMS + "X-ray power (W)", (current[0] * voltage[0]) / 1000);
      }
    }
    else if (isTXRM) {
      if (current != null && voltage != null) {
        for (int i=0; i<(int) Math.min(current.length, voltage.length); i++) {
          double calcPower = (current[i] * voltage[i]) / 1000;
          addGlobalMetaList(PROJECTION + "X-ray power (W)", formatDouble(calcPower));
        }
      }
    }
    addGlobalMeta(IMAGE_DETAILS + "File type", suffix);

    if (pixelSize != null) {
      double fovX = m.sizeX * pixelSize;
      double fovY = m.sizeY * pixelSize;
      addGlobalMeta(IMAGE_DETAILS + "Field of view (µm)",
        formatDouble(fovX) + ", " + formatDouble(fovY));
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    if (pixelSize != null) {
      Length physicalSize = FormatTools.getPhysicalSize(pixelSize, "µm");
      store.setPixelsPhysicalSizeX(physicalSize, 0);
      store.setPixelsPhysicalSizeY(physicalSize, 0);
      store.setPixelsPhysicalSizeZ(physicalSize, 0);
    }

    double firstTimestamp = 0;
    for (int p=0; p<getImageCount(); p++) {
      if (exposureTimes != null && p < exposureTimes.length) {
        store.setPlaneExposureTime(new Time(exposureTimes[p], UNITS.SECOND), 0, p);
      }
      if (xPos != null && p < xPos.length) {
        store.setPlanePositionX(new Length(xPos[p], UNITS.MICROMETER), 0, p);
      }
      if (yPos != null && p < yPos.length) {
        store.setPlanePositionY(new Length(yPos[p], UNITS.MICROMETER), 0, p);
      }
      if (zPos != null && p < zPos.length) {
        store.setPlanePositionZ(new Length(zPos[p], UNITS.MICROMETER), 0, p);
      }
      if (datestamps != null) {
        if (p == 0) {
          firstTimestamp = DateTools.getTime(datestamps[0], DATESTAMP);
          store.setImageAcquisitionDate(new Timestamp(DateTools.formatDate(datestamps[0], DATESTAMP)), 0);
        }
        double delta = DateTools.getTime(datestamps[p], DATESTAMP) - firstTimestamp;
        store.setPlaneDeltaT(new Time(delta, UNITS.MILLISECOND), 0, p);
      }
    }
  }

  // -- Helper methods --

  /** Parse metadata fields specific to .txm files. */
  private void handleTXMMetadata(String name, RandomAccessInputStream stream) throws IOException {
    if (name.equals(AUTORECON_PATH + "MeanSampleX")) {
      addGlobalMeta(POSITIONS + "Mean sample X (µm)", formatDouble(stream.readFloat()));
    }
    else if (name.equals(AUTORECON_PATH + "MeanSampleY")) {
      addGlobalMeta(POSITIONS + "Mean sample Y (µm)", formatDouble(stream.readFloat()));
    }
    else if (name.equals(AUTORECON_PATH + "MeanSampleZ")) {
      addGlobalMeta(POSITIONS + "Mean sample Z (µm)", formatDouble(stream.readFloat()));
    }
    else if (name.equals(RECON_SETTINGS_PATH + "SourceVoltage")) {
      voltage = readAsDoubles(stream);
      addGlobalMeta(GENERAL_PARAMS + "X-ray voltage (kV)", voltage[0]);
    }
    else if (name.equals(RECON_SETTINGS_PATH + "OutputFileLocation")) {
      addGlobalMeta(RECON_SETTINGS + "Output file location", readAsString(stream));
    }
    else if (name.equals(RECON_SETTINGS_PATH + "InputFileName")) {
      addGlobalMeta(RECON_SETTINGS + "Input filename", readAsString(stream));
    }
    else if (name.equals(RECON_SETTINGS_PATH + "CenterShift")) {
      addGlobalMeta(RECON_SETTINGS + "Center shift", stream.readFloat());
    }
    else if (name.equals(RECON_SETTINGS_PATH + "BeamHardeningFileName")) {
      addGlobalMeta(RECON_SETTINGS + "Beam hardening", readAsString(stream));
    }
    else if (name.equals(RECON_SETTINGS_PATH + "BeamHardening")) {
      addGlobalMeta(RECON_SETTINGS + "Beam-hardening constant", stream.readFloat());
    }
    else if (name.equals(RECON_SETTINGS_PATH + "RotationAngle")) {
      addGlobalMeta(RECON_SETTINGS + "Rotation angle", stream.readFloat());
    }
    else if (name.equals(RECON_SETTINGS_PATH + "ReconFilterChoice")) {
      int filter = stream.readInt();
      String filterName = String.valueOf(filter);
      // this is very likely an enum, but we don't know all of the possible values
      if (filter == 2) {
        filterName = "Smooth";
      }
      else {
        LOGGER.warn("Could not identify reconstruction filter type: {}", filter);
      }
      addGlobalMeta(RECON_SETTINGS + "Recon filter", filterName);
    }
    else if (name.equals(RECON_SETTINGS_PATH + "ReconFilterSmoothFactor")) {
      addGlobalMeta(RECON_SETTINGS + "Sigma", stream.readFloat());
    }
    else if (name.equals(RECON_SETTINGS_PATH + "ReconScalingEnum")) {
      int scaling = stream.readInt();
      String scalingType = String.valueOf(scaling);
      // this is very likely an enum, but we don't know all of the possible values
      if (scaling == 0) {
        scalingType = "Global";
      }
      else {
        LOGGER.warn("Could not identify reconstruction scaling: {}", scaling);
      }

      addGlobalMeta(RECON_SETTINGS + "Recon scaling", scalingType);
    }
    else if (name.equals(RECON_SETTINGS_PATH + "GlobalMax")) {
      addGlobalMeta(RECON_SETTINGS + "Global max", stream.readFloat());
    }
    else if (name.equals(RECON_SETTINGS_PATH + "GlobalMin")) {
      addGlobalMeta(RECON_SETTINGS + "Global min", stream.readFloat());
    }
    else if (name.equals(RECON_SETTINGS_PATH + "UserMinMax")) {
      addGlobalMeta(RECON_SETTINGS + "User min-max", getYesNo(stream));
    }
    else if (name.equals(RECON_SETTINGS_PATH + "UseCTScaleFilter")) {
      addGlobalMeta(RECON_SETTINGS + "Use CT-Scaling", getYesNo(stream));
    }
    else if (name.equals(RECON_SETTINGS_PATH + "CTScaleFilter")) {
      addGlobalMeta(RECON_SETTINGS + "CT-scale name", readAsString(stream));
    }
    else if (name.equals(RECON_SETTINGS_PATH + "SecondaryReferenceFileName")) {
      addGlobalMeta(RECON_SETTINGS + "Secondary ref. filename", readAsString(stream));
    }
    else if (name.equals(RECON_SETTINGS_PATH + "SecRefSourceFilterName")) {
      addGlobalMeta(RECON_SETTINGS + "Secondary ref. filter name", readAsString(stream));
    }
    else if (name.equals(RECON_SETTINGS_PATH + "SecondaryRefCollectionMode")) {
      int v = stream.readInt();
      String mode = v == 0 ? "None" : String.valueOf(v);
      addGlobalMeta(RECON_SETTINGS + "Secondary ref. collection", mode);
    }
    else if (name.equals(RECON_SETTINGS_PATH + "ReconOperation")) {
      addGlobalMeta(RECON_SETTINGS + "Output down-sampling", stream.readInt());
    }
    else if (name.equals(AUTORECON_PATH + "StoRADistance")) {
      // dividing by -1000 is intentional, this matches what DataExplorer shows
      float sRADistance = stream.readFloat() / -1000;

      addGlobalMeta(POSITIONS + "Source to RA (mm)", formatDouble(sRADistance));
      addGlobalMeta(GENERAL_PARAMS + "Source-RA (mm)", formatDouble(sRADistance));
    }
    else if (name.equals(AUTORECON_PATH + "DtoRADistance")) {
      float dRADistance = stream.readFloat() / 1000;

      addGlobalMeta(POSITIONS + "Detector to RA (mm)", formatDouble(dRADistance));
      addGlobalMeta(GENERAL_PARAMS + "Detector-RA (mm)", formatDouble(dRADistance));
    }
    else if (name.equals(AUTORECON_PATH + "NumOfProjects")) {
      addGlobalMeta(GENERAL_PARAMS + "Number of projections used", stream.readInt());
    }
    else if (name.equals(RECON_SETTINGS_PATH + "ReconServiceVersion")) {
      addGlobalMeta(DATASET + "Recon Service Version", readAsString(stream));
    }
  }

  /** Parse metadata fields specific to .txrm files. */
  private void handleTXRMMetadata(String name, RandomAccessInputStream stream) throws IOException {
    if (name.equals(REFERENCE_PATH + "ImageInfo/XrayMagnification")) {
      addGlobalMeta(PROJECTION + "Geometric Magnification", formatDouble(stream.readFloat()));
    }
    else if (name.equals("Root Entry/Selection/SelectedImages")) {
      addGlobalMeta(PROJECTION + "Selected", getYesNo(stream));
    }
    else if (name.equals(IMAGE_INFO_PATH + "XrayCurrent")) {
      current = readAsDoubles(stream);
      addMetadataList(current, PROJECTION + "X-ray current (µA)", true);
    }
    else if (name.equals(IMAGE_INFO_PATH + "XPosition")) {
      xPos = readAsDoubles(stream);
    }
    else if (name.equals(IMAGE_INFO_PATH + "YPosition")) {
      yPos = readAsDoubles(stream);
    }
    else if (name.equals(IMAGE_INFO_PATH + "ZPosition")) {
      zPos = readAsDoubles(stream);
    }
    else if (name.equals(IMAGE_INFO_PATH + "DtoRADistance")) {
      addGlobalMeta(PROJECTION + "Detector-RA (mm)", formatDouble(stream.readFloat()));
    }
    else if (name.equals(IMAGE_INFO_PATH + "StoRADistance")) {
      addGlobalMeta(PROJECTION + "Source-RA (mm)", formatDouble(stream.readFloat()));
    }
    else if (name.equals(IMAGE_INFO_PATH + "FanAngle")) {
      double[] fanAngle = readAsDoubles(stream);
      addMetadataList(fanAngle, PROJECTION + "Fan angle", true);
    }
    else if (name.equals(IMAGE_INFO_PATH + "ConeAngle")) {
      double[] coneAngle = readAsDoubles(stream);
      addMetadataList(coneAngle, PROJECTION + "Cone angle", true);
    }
    else if (name.equals(IMAGE_INFO_PATH + "Angles")) {
      double[] angles = readAsDoubles(stream);
      addMetadataList(angles, PROJECTION + "Angle", true);
    }
    else if (name.equals(IMAGE_INFO_PATH + "ReadOutTime")) {
      addGlobalMeta(PROJECTION + "Camera Readout Speed", stream.readInt());
    }
    else if (name.equals(IMAGE_INFO_PATH + "Temperature")) {
      addGlobalMeta(PROJECTION + "Camera Temperature", stream.readInt());
    }
    else if (name.equals(IMAGE_INFO_PATH + "Date")) {
      datestamps = new String[(int) (stream.length() / 40)];
      for (int i=0; i<datestamps.length; i++) {
        datestamps[i] = stream.readString(DATESTAMP.length());
        stream.skipBytes(40 - DATESTAMP.length());
        addGlobalMetaList(PROJECTION + "Date", datestamps[i]);
      }
    }
  }

  private void initPOIService() throws FormatException, IOException {
   try {
      ServiceFactory factory = new ServiceFactory();
      poi = factory.getInstance(POIService.class);
    }
    catch (DependencyException de) {
      throw new FormatException("POI library not found", de);
    }

    poi.initialize(Location.getMappedId(getCurrentFile()));
  }

  /**
   * Convert an XRM data type to a Bio-Formats pixel type.
   */
  private int getPixelType(int dataType) throws FormatException {
    switch (dataType) {
      case 2:
        return FormatTools.INT8;
      case 3:
        return FormatTools.UINT8;
      case 4:
        return FormatTools.INT16;
      case 5:
        return FormatTools.UINT16;
      case 6:
        return FormatTools.INT32;
      case 7:
        return FormatTools.UINT32;
      case 10:
        return FormatTools.FLOAT;
      case 11:
        return FormatTools.DOUBLE;
    }
    throw new FormatException("Unsupported data type: " + dataType);
  }

  private String getMetadataPixelType(int dataType) throws FormatException {
    switch (dataType) {
      case 2:
        return "byte";
      case 3:
        return "ubyte";
      case 4:
        return "short";
      case 5:
        return "ushort";
      case 6:
        return "int";
      case 7:
        return "uint";
      case 10:
        return "float";
      case 11:
        return "double";
    }
    throw new FormatException("Unsupported data type: " + dataType);
  }

  private String readAsString(RandomAccessInputStream stream) throws IOException {
    long len = stream.length();
    if (len > Integer.MAX_VALUE) {
      throw new IOException("Length too large to read as string: " + len);
    }
    String value = stream.readString((int) len);
    return value.trim();
  }

  private String getYesNo(RandomAccessInputStream stream) throws IOException {
    int v = stream.read();
    return v == 0 ? "No" : "Yes";
  }

  private double[] readAsDoubles(RandomAccessInputStream stream) throws IOException {
    double[] v = new double[(int) (stream.length() / 4)];
    for (int i=0; i<v.length; i++) {
      v[i] = (double) stream.readFloat();
    }
    return v;
  }

  /**
   * Add each double value in an array to the original metadata table,
   * using the given key. Double values are not formatted.
   */
  private void addMetadataList(double[] v, String key) {
    addMetadataList(v, key, false);
  }

  /**
   * Add each double value in an array to the original metadata table,
   * using the given key. Double values are formatted to 2 decimal places
   * if the formatDoubles parameter is true.
   */
  private void addMetadataList(double[] v, String key, boolean formatDoubles) {
    boolean singleValue = true;
    for (double d : v) {
      if (Math.abs(d - v[0]) > Constants.EPSILON) {
        singleValue = false;
        break;
      }
    }
    if (singleValue) {
      addGlobalMeta(key, formatDoubles ? formatDouble(v[0]) : v[0]);
    }
    else {
      for (double d : v) {
        addGlobalMetaList(key, formatDoubles ? formatDouble(d) : d);
      }
    }
  }

  /** Format double value to 2 decimal places, rounding to nearest. */
  private String formatDouble(double v) {
    return String.format("%.02f", v);
  }

}
