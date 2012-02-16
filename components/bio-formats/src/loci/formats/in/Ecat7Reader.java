//
// Ecat7Reader.java
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

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

/**
 * Ecat7Reader is the file format reader for ECAT 7 files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/Ecat7Reader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/Ecat7Reader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class Ecat7Reader extends FormatReader {

  // -- Constants --

  public static final String ECAT7_MAGIC = "MATRIX72v";
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
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = ECAT7_MAGIC.length();
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).equals(ECAT7_MAGIC);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
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
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    String check = in.readString(14).trim();
    if (!check.equals(ECAT7_MAGIC)) {
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

    core[0].sizeZ = in.readShort();
    core[0].sizeT = in.readShort();

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

    core[0].sizeX = in.readShort();
    core[0].sizeY = in.readShort();
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

    core[0].sizeC = 1;
    core[0].imageCount = getSizeZ() * getSizeT() * getSizeC();
    core[0].dimensionOrder = "XYZTC";

    switch (dataType) {
      case 6:
        core[0].pixelType = FormatTools.UINT16;
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
    for (int i=0; i<bedPositions.length; i++) {
      addGlobalMeta("Bed position #" + (i + 1), bedPositions[i]);
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
    for (int i=0; i<fillCTI.length; i++) {
      addGlobalMeta("Fill CTI #" + (i + 1), fillCTI[i]);
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
    for (int i=0; i<ctiFill.length; i++) {
      addGlobalMeta("CTI fill #" + (i + 1), ctiFill[i]);
    }
    for (int i=0; i<userFill.length; i++) {
      addGlobalMeta("User fill #" + (i + 1), userFill[i]);
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(description, 0);
      if (xPixelSize > 0) {
        store.setPixelsPhysicalSizeX(
          new PositiveFloat(new Double(xPixelSize)), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
          xPixelSize);
      }
      if (yPixelSize > 0) {
        store.setPixelsPhysicalSizeY(
          new PositiveFloat(new Double(yPixelSize)), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
          yPixelSize);
      }
      if (zPixelSize > 0) {
        store.setPixelsPhysicalSizeZ(
          new PositiveFloat(new Double(zPixelSize)), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeZ; got {}",
          zPixelSize);
      }
    }
  }

}
