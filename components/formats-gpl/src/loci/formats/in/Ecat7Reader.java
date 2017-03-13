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

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.units.quantity.Length;

/**
 * Ecat7Reader is the file format reader for ECAT 7 files.
 */
public class Ecat7Reader extends FormatReader {

  // -- Constants --

  public static final String ECAT7_MAGIC = "MATRIX72v";

  // there are three ECAT7 versions, see: https://github.com/neurodebian/spm12/blob/master/spm_ecat2nifti.m
  public static final String ECAT7_MAGIC_REGEX = "MATRIX7[012]v";

  private static final long HEADER_SIZE = 1536;

  // -- Constructor --

  /** Constructs a new ECAT7 reader. */
  public Ecat7Reader() {
    super("ECAT7", "v");
    domains = new String[] {FormatTools.MEDICAL_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = ECAT7_MAGIC.length();
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).equals(ECAT7_MAGIC);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] zct = getZCTCoords(no);
    int tSkip = 0;
    for (int i=0; i<zct[2]; i++) {
      tSkip += 512;
      if (i > 0 && (i % 30) == 0) tSkip += 512;
    }

    in.seek(HEADER_SIZE + no * FormatTools.getPlaneSize(this) + tSkip);
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    CoreMetadata ms0 = core.get(0);

    String check = in.readString(14).trim();
    if (!check.matches(ECAT7_MAGIC_REGEX)) {
      throw new FormatException("Invalid ECAT 7 file.");
    }

    String originalPath = in.readString(32);
    short version = in.readShort();
    short systemType = in.readShort();
    short fileType = in.readShort();
    String serialNumber = in.readString(10);
    int scanStart = in.readInt();
    String isotopeName = in.readString(8);
    float isotopeHalflife = in.readFloat();
    String radioPharmaceutical = in.readString(32);
    float gantryTilt = in.readFloat();
    float gantryRotation = in.readFloat();
    float bedElevation = in.readFloat();
    float intrinsicTilt = in.readFloat();
    short wobbleSpeed = in.readShort();
    short sourceType = in.readShort();
    float distanceScanned = in.readFloat();
    float transaxialFOV = in.readFloat();
    short angularCompression = in.readShort();
    short coinSampleMode = in.readShort();
    short axialSampleMode = in.readShort();
    float calibrationFactor = in.readFloat();
    short calibrationUnits = in.readShort();
    short calibrationLabel = in.readShort();
    short compression = in.readShort();
    String studyType = in.readString(12);
    String patientID = in.readString(16);
    String patientName = in.readString(32);
    String patientSex = in.readString(1);
    String patientDexterity = in.readString(1);
    float patientAge = in.readFloat();
    float patientHeight = in.readFloat();
    float patientWeight = in.readFloat();
    int patientBirthDate = in.readInt();
    String physicianName = in.readString(32);
    String operatorName = in.readString(32);
    String description = in.readString(32);
    short acquisitionType = in.readShort();
    short patientOrientation = in.readShort();
    String facilityName = in.readString(20);

    ms0.sizeZ = in.readShort();
    ms0.sizeT = in.readShort();

    short numGates = in.readShort();
    short numBedPositions = in.readShort();

    float initBedPosition = in.readFloat();
    float[] bedPositions = new float[15];
    for (int i=0; i<bedPositions.length; i++) {
      bedPositions[i] = in.readFloat();
    }

    float planeSeparation = in.readFloat();
    short lowerThreshold = in.readShort();
    short trueLowerThreshold = in.readShort();
    short trueUpperThreshold = in.readShort();
    String processCode = in.readString(10);
    short acquisitionMode = in.readShort();
    float binSize = in.readFloat();
    float branchingFraction = in.readFloat();
    int doseStartTime = in.readInt();
    float dosage = in.readFloat();
    float wellCounterCorrectionFactor = in.readFloat();
    String dataUnits = in.readString(32);
    short septaState = in.readShort();
    short[] fillCTI = new short[6];
    for (int i=0; i<fillCTI.length; i++) {
      fillCTI[i] = in.readShort();
    }

    in.skipBytes(512);

    short dataType = in.readShort();
    short numDimensions = in.readShort();

    ms0.sizeX = in.readShort();
    ms0.sizeY = in.readShort();
    in.skipBytes(2);

    float xOffset = in.readFloat();
    float yOffset = in.readFloat();
    float zOffset = in.readFloat();
    float reconZoom = in.readFloat();
    float scaleFactor = in.readFloat();
    short imageMin = in.readShort();
    short imageMax = in.readShort();
    float xPixelSize = in.readFloat();
    float yPixelSize = in.readFloat();
    float zPixelSize = in.readFloat();
    int frameDuration = in.readInt();
    int frameStartTime = in.readInt();
    short filterCode = in.readShort();
    float xResolution = in.readFloat();
    float yResolution = in.readFloat();
    float zResolution = in.readFloat();
    float numRElements = in.readFloat();
    float numAngles = in.readFloat();
    float zRotationAngle = in.readFloat();
    float decayCorrectionFactor = in.readFloat();
    int processingCode = in.readInt();
    int gateDuration = in.readInt();
    int rWaveOffset = in.readInt();
    int numAcceptedBeats = in.readInt();
    float filterCutoffFrequency = in.readFloat();
    float filterResolution = in.readFloat();
    float filterRampSlope = in.readFloat();
    short filterOrder = in.readShort();
    float filterScatterFraction = in.readFloat();
    float filterScatterSlope = in.readFloat();
    String annotation = in.readString(40);
    float[][] matrix = new float[3][4];
    for (int i=0; i<matrix.length; i++) {
      for (int j=0; j<matrix[i].length-1; j++) {
        matrix[i][j] = in.readFloat();
      }
    }
    float rFilterCutoff = in.readFloat();
    float rFilterResolution = in.readFloat();
    short rFilterCode = in.readShort();
    short rFilterOrder = in.readShort();
    float zFilterCutoff = in.readFloat();
    float zFilterResolution = in.readFloat();
    short zFilterCode = in.readShort();
    short zFilterOrder = in.readShort();
    matrix[0][3] = in.readFloat();
    matrix[1][3] = in.readFloat();
    matrix[2][3] = in.readFloat();
    short scatterType = in.readShort();
    short reconType = in.readShort();
    short reconViews = in.readShort();
    short[] ctiFill = new short[87];
    for (int i=0; i<ctiFill.length; i++) {
      ctiFill[i] = in.readShort();
    }
    short[] userFill = new short[48];
    for (int i=0; i<userFill.length; i++) {
      userFill[i] = in.readShort();
    }

    ms0.sizeC = 1;
    ms0.imageCount = getSizeZ() * getSizeT() * getSizeC();
    ms0.dimensionOrder = "XYZTC";

    switch (dataType) {
      case 6:
        ms0.pixelType = FormatTools.UINT16;
        break;
      default:
        throw new FormatException("Unsupported data type: " + dataType);
    }

    addGlobalMeta("Original path", originalPath);
    addGlobalMeta("Version", version);
    addGlobalMeta("System type", systemType);
    addGlobalMeta("File type", fileType);
    addGlobalMeta("Serial number", serialNumber);
    addGlobalMeta("Scan start", scanStart);
    addGlobalMeta("Isotope Name", isotopeName);
    addGlobalMeta("Isotope half-life", isotopeHalflife);
    addGlobalMeta("Radiopharmaceutical", radioPharmaceutical);
    addGlobalMeta("Gantry tilt", gantryTilt);
    addGlobalMeta("Gantry rotation", gantryRotation);
    addGlobalMeta("Bed elevation", bedElevation);
    addGlobalMeta("Intrinsic tilt", intrinsicTilt);
    addGlobalMeta("Wobble speed", wobbleSpeed);
    addGlobalMeta("Source type", sourceType);
    addGlobalMeta("Distance scanned", distanceScanned);
    addGlobalMeta("Transaxial FOV", transaxialFOV);
    addGlobalMeta("Angular compression", angularCompression);
    addGlobalMeta("Coin. sample mode", coinSampleMode);
    addGlobalMeta("Axial sample mode", axialSampleMode);
    addGlobalMeta("Calibration factor", calibrationFactor);
    addGlobalMeta("Calibration units", calibrationUnits);
    addGlobalMeta("Calibration units label", calibrationLabel);
    addGlobalMeta("Compression", compression);
    addGlobalMeta("Study type", studyType);
    addGlobalMeta("Patient ID", patientID);
    addGlobalMeta("Patient name", patientName);
    addGlobalMeta("Patient sex", patientSex);
    addGlobalMeta("Patient dexterity", patientDexterity);
    addGlobalMeta("Patient age", patientAge);
    addGlobalMeta("Patient height", patientHeight);
    addGlobalMeta("Patient weight", patientWeight);
    addGlobalMeta("Patient birth date", patientBirthDate);
    addGlobalMeta("Physician name", physicianName);
    addGlobalMeta("Operator name", operatorName);
    addGlobalMeta("Description", description);
    addGlobalMeta("Acquisition type", acquisitionType);
    addGlobalMeta("Patient orientation", patientOrientation);
    addGlobalMeta("Facility name", facilityName);
    addGlobalMeta("Number of gates", numGates);
    addGlobalMeta("Number of bed positions", numBedPositions);
    for (float bedPos : bedPositions) {
      addGlobalMetaList("Bed position", bedPos);
    }
    addGlobalMeta("Plane separation", planeSeparation);
    addGlobalMeta("Lower threshold", lowerThreshold);
    addGlobalMeta("True lower threshold", trueLowerThreshold);
    addGlobalMeta("True upper threshold", trueUpperThreshold);
    addGlobalMeta("Process code", processCode);
    addGlobalMeta("Acquistion mode", acquisitionMode);
    addGlobalMeta("Bin size", binSize);
    addGlobalMeta("Branching fraction", branchingFraction);
    addGlobalMeta("Dose start time", doseStartTime);
    addGlobalMeta("Dosage", dosage);
    addGlobalMeta("Well counter correction factor",
      wellCounterCorrectionFactor);
    addGlobalMeta("Data units", dataUnits);
    addGlobalMeta("Septa state", septaState);
    for (float fill : fillCTI) {
      addGlobalMetaList("Fill CTI", fill);
    }

    addGlobalMeta("Data type", dataType);
    addGlobalMeta("Number of dimensions", numDimensions);
    addGlobalMeta("X offset", xOffset);
    addGlobalMeta("Y offset", yOffset);
    addGlobalMeta("Z offset", zOffset);
    addGlobalMeta("Recon. zoom", reconZoom);
    addGlobalMeta("Scale factor", scaleFactor);
    addGlobalMeta("Image minimum", imageMin);
    addGlobalMeta("Image maximum", imageMax);
    addGlobalMeta("X pixel size", xPixelSize);
    addGlobalMeta("Y pixel size", yPixelSize);
    addGlobalMeta("Z pixel size", zPixelSize);
    addGlobalMeta("Frame duration", frameDuration);
    addGlobalMeta("Frame start time", frameStartTime);
    addGlobalMeta("Filter code", filterCode);
    addGlobalMeta("X resolution", xResolution);
    addGlobalMeta("Y resolution", yResolution);
    addGlobalMeta("Z resolution", zResolution);
    addGlobalMeta("Number of R elements", numRElements);
    addGlobalMeta("Number of angles", numAngles);
    addGlobalMeta("Z rotation angle", zRotationAngle);
    addGlobalMeta("Decay correction factor", decayCorrectionFactor);
    addGlobalMeta("Processing code", processingCode);
    addGlobalMeta("Gate duration", gateDuration);
    addGlobalMeta("R wave offset", rWaveOffset);
    addGlobalMeta("Number of accepted beats", numAcceptedBeats);
    addGlobalMeta("Filter cutoff frequency", filterCutoffFrequency);
    addGlobalMeta("Filter resolution", filterResolution);
    addGlobalMeta("Filter ramp slope", filterRampSlope);
    addGlobalMeta("Filter order", filterOrder);
    addGlobalMeta("Filter scatter fraction", filterScatterFraction);
    addGlobalMeta("Filter scatter slope", filterScatterSlope);
    addGlobalMeta("Annotation", annotation);
    for (int i=0; i<matrix.length; i++) {
      for (int j=0; j<matrix[i].length; j++) {
        addGlobalMeta("MT (" + (i + 1) + ", " + (j + 1) + ")", matrix[i][j]);
      }
    }
    addGlobalMeta("R filter cutoff", rFilterCutoff);
    addGlobalMeta("R filter resolution", rFilterResolution);
    addGlobalMeta("R filter code", rFilterCode);
    addGlobalMeta("R filter order", rFilterOrder);
    addGlobalMeta("Z filter cutoff", zFilterCutoff);
    addGlobalMeta("Z filter resolution", zFilterResolution);
    addGlobalMeta("Z filter code", zFilterCode);
    addGlobalMeta("Z filter order", zFilterOrder);
    addGlobalMeta("Scatter type", scatterType);
    addGlobalMeta("Recon. type", reconType);
    addGlobalMeta("Recon. views", reconViews);
    for (float cti : ctiFill) {
      addGlobalMeta("CTI fill", cti);
    }
    for (float user : userFill) {
      addGlobalMeta("User fill", user);
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(description, 0);
      Length sizeX =
        FormatTools.getPhysicalSizeX(new Double(xPixelSize));
      Length sizeY =
        FormatTools.getPhysicalSizeY(new Double(yPixelSize));
      Length sizeZ =
        FormatTools.getPhysicalSizeZ(new Double(zPixelSize));

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, 0);
      }
    }
  }

}
