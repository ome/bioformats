//
// OMEXML2003FCMetadata.java
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

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via MetadataAutogen on Jan 22, 2008 4:26:50 PM CST
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.ome;

import org.openmicroscopy.xml.*;
import org.openmicroscopy.xml.st.*;
import java.util.List;
import loci.formats.LogTools;

/**
 * A utility class for constructing and manipulating OME-XML DOMs for the
 * 2003 (FC) version of OME-XML. It requires the
 * org.openmicroscopy.xml package to compile (part of ome-java.jar).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ome/OMEXML2003FCMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ome/OMEXML2003FCMetadata.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OMEXML2003FCMetadata extends OMEXMLMetadata {

  // -- OMEXMLMetadata API methods --

  /* @see OMEXMLMetadata#dumpXML() */
  public String dumpXML() {
    if (root == null) return null;
    try { return ((OMENode) root).writeOME(false); }
    catch (javax.xml.transform.TransformerException exc) {
      LogTools.trace(exc);
    }
    catch (java.io.IOException exc) { LogTools.trace(exc); }
    catch (org.xml.sax.SAXException exc) { LogTools.trace(exc); }
    catch (javax.xml.parsers.ParserConfigurationException exc) {
      LogTools.trace(exc);
    }
    return null;
  }

  // -- MetadataRetrieve API methods --

  // - Entity retrieval -

  public Object getImage(int imageIndex) {
    return getImageNode(imageIndex, false);
  }

  public Object getPixels(int imageIndex, int pixelsIndex) {
    return getPixelsNode(imageIndex, pixelsIndex, false);
  }

  public Object getDimensions(int imageIndex, int pixelsIndex) {
    return getDimensionsNode(imageIndex, pixelsIndex, false);
  }

  public Object getImagingEnvironment(int imageIndex) {
    return getImagingEnvironmentNode(imageIndex, false);
  }

  public Object getPlane(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: Plane unsupported for schema version 2003 (FC)
    return null;
  }

  public Object getPlaneTiming(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PlaneTiming unsupported for schema version 2003 (FC)
    return null;
  }

  public Object getStagePosition(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: StagePosition unsupported for schema version 2003 (FC)
    return null;
  }

  public Object getLogicalChannel(int imageIndex, int logicalChannelIndex) {
    return getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
  }

  public Object getDetectorSettings(int imageIndex, int logicalChannelIndex) {
    return getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
  }

  public Object getLightSourceSettings(int imageIndex, int logicalChannelIndex) {
    return getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
  }

  public Object getDisplayROI(int imageIndex, int roiIndex) {
    return getDisplayROINode(imageIndex, roiIndex, false);
  }

  public Object getStageLabel(int imageIndex) {
    return getStageLabelNode(imageIndex, false);
  }

  public Object getLightSource(int instrumentIndex, int lightSourceIndex) {
    return getLightSourceNode(instrumentIndex, lightSourceIndex, false);
  }

  public Object getLaser(int instrumentIndex, int lightSourceIndex) {
    return getLaserNode(instrumentIndex, lightSourceIndex, false);
  }

  public Object getFilament(int instrumentIndex, int lightSourceIndex) {
    return getFilamentNode(instrumentIndex, lightSourceIndex, false);
  }

  public Object getArc(int instrumentIndex, int lightSourceIndex) {
    return getArcNode(instrumentIndex, lightSourceIndex, false);
  }

  public Object getDetector(int instrumentIndex, int detectorIndex) {
    return getDetectorNode(instrumentIndex, detectorIndex, false);
  }

  public Object getObjective(int instrumentIndex, int objectiveIndex) {
    return getObjectiveNode(instrumentIndex, objectiveIndex, false);
  }

  public Object getOTF(int instrumentIndex, int otfIndex) {
    return getOTFNode(instrumentIndex, otfIndex, false);
  }

  public Object getExperimenter(int experimenterIndex) {
    return getExperimenterNode(experimenterIndex, false);
  }

  // - Image property retrieval -

  public String getImageName(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getName();
  }

  public String getImageCreationDate(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getCreated();
  }

  public String getImageDescription(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getDescription();
  }

  // - Pixels property retrieval -

  public Integer getPixelsSizeX(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeX();
  }

  public Integer getPixelsSizeY(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeY();
  }

  public Integer getPixelsSizeZ(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeZ();
  }

  public Integer getPixelsSizeC(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeC();
  }

  public Integer getPixelsSizeT(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeT();
  }

  public String getPixelsPixelType(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPixelType();
  }

  public Boolean getPixelsBigEndian(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.isBigEndian();
  }

  public String getPixelsDimensionOrder(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getDimensionOrder();
  }

  // - Dimensions property retrieval -

  public Float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex) {
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, false);
    return dimensions == null ? null : dimensions.getPixelSizeX();
  }

  public Float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex) {
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, false);
    return dimensions == null ? null : dimensions.getPixelSizeY();
  }

  public Float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex) {
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, false);
    return dimensions == null ? null : dimensions.getPixelSizeZ();
  }

  public Float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex) {
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, false);
    return dimensions == null ? null : dimensions.getPixelSizeT();
  }

  public Integer getDimensionsWaveStart(int imageIndex, int pixelsIndex) {
    // NB: WaveStart unsupported for schema version 2003 (FC)
    return null;
  }

  public Integer getDimensionsWaveIncrement(int imageIndex, int pixelsIndex) {
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, false);
    return dimensions == null ? null : dimensionsPixelSizeCToInteger(dimensions.getPixelSizeC());
  }

  // - ImagingEnvironment property retrieval -

  public Float getImagingEnvironmentTemperature(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getTemperature();
  }

  public Float getImagingEnvironmentAirPressure(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getAirPressure();
  }

  public Float getImagingEnvironmentHumidity(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getHumidity();
  }

  public Float getImagingEnvironmentCO2Percent(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getCO2Percent();
  }

  // - Plane property retrieval -

  public Integer getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: TheZ unsupported for schema version 2003 (FC)
    return null;
  }

  public Integer getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: TheC unsupported for schema version 2003 (FC)
    return null;
  }

  public Integer getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: TheT unsupported for schema version 2003 (FC)
    return null;
  }

  // - PlaneTiming property retrieval -

  public Float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: DeltaT unsupported for schema version 2003 (FC)
    return null;
  }

  public Float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: ExposureTime unsupported for schema version 2003 (FC)
    return null;
  }

  // - StagePosition property retrieval -

  public Float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PositionX unsupported for schema version 2003 (FC)
    return null;
  }

  public Float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PositionY unsupported for schema version 2003 (FC)
    return null;
  }

  public Float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PositionZ unsupported for schema version 2003 (FC)
    return null;
  }

  // - LogicalChannel property retrieval -

  public String getLogicalChannelName(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getName();
  }

  public Integer getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getSamplesPerPixel();
  }

  public String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getIlluminationType();
  }

  public Integer getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getPinholeSize();
  }

  public String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getPhotometricInterpretation();
  }

  public String getLogicalChannelMode(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getMode();
  }

  public String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getContrastMethod();
  }

  public Integer getLogicalChannelExWave(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getExcitationWavelength();
  }

  public Integer getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getEmissionWavelength();
  }

  public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getFluor();
  }

  public Float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getNDFilter();
  }

  public Integer getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex) {
    // NB: PockelCellSetting unsupported for schema version 2003 (FC)
    return null;
  }

  // - DetectorSettings property retrieval -

  public Float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getDetectorOffset();
  }

  public Float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getDetectorGain();
  }

  // - LightSourceSettings property retrieval -

  public Float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getLightAttenuation();
  }

  public Integer getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getLightWavelength();
  }

  // - DisplayROI property retrieval -

  public Integer getDisplayROIX0(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getX0();
  }

  public Integer getDisplayROIY0(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getY0();
  }

  public Integer getDisplayROIZ0(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getZ0();
  }

  public Integer getDisplayROIT0(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getT0();
  }

  public Integer getDisplayROIX1(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getX1();
  }

  public Integer getDisplayROIY1(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getY1();
  }

  public Integer getDisplayROIZ1(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getZ1();
  }

  public Integer getDisplayROIT1(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getT1();
  }

  // - StageLabel property retrieval -

  public String getStageLabelName(int imageIndex) {
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, false);
    return stageLabel == null ? null : stageLabel.getName();
  }

  public Float getStageLabelX(int imageIndex) {
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, false);
    return stageLabel == null ? null : stageLabel.getX();
  }

  public Float getStageLabelY(int imageIndex) {
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, false);
    return stageLabel == null ? null : stageLabel.getY();
  }

  public Float getStageLabelZ(int imageIndex) {
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, false);
    return stageLabel == null ? null : stageLabel.getZ();
  }

  // - LightSource property retrieval -

  public String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex) {
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, false);
    return lightSource == null ? null : lightSource.getManufacturer();
  }

  public String getLightSourceModel(int instrumentIndex, int lightSourceIndex) {
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, false);
    return lightSource == null ? null : lightSource.getModel();
  }

  public String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex) {
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, false);
    return lightSource == null ? null : lightSource.getSerialNumber();
  }

  // - Laser property retrieval -

  public String getLaserType(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getType();
  }

  public String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getMedium();
  }

  public Integer getLaserWavelength(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getWavelength();
  }

  public Integer getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laserFrequencyDoubledToInteger(laser.isFrequencyDoubled());
  }

  public Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.isTunable();
  }

  public String getLaserPulse(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getPulse();
  }

  public Float getLaserPower(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getPower();
  }

  // - Filament property retrieval -

  public String getFilamentType(int instrumentIndex, int lightSourceIndex) {
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, false);
    return filament == null ? null : filament.getType();
  }

  public Float getFilamentPower(int instrumentIndex, int lightSourceIndex) {
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, false);
    return filament == null ? null : filament.getPower();
  }

  // - Arc property retrieval -

  public String getArcType(int instrumentIndex, int lightSourceIndex) {
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, false);
    return arc == null ? null : arc.getType();
  }

  public Float getArcPower(int instrumentIndex, int lightSourceIndex) {
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, false);
    return arc == null ? null : arc.getPower();
  }

  // - Detector property retrieval -

  public String getDetectorManufacturer(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getManufacturer();
  }

  public String getDetectorModel(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getModel();
  }

  public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getSerialNumber();
  }

  public String getDetectorType(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getType();
  }

  public Float getDetectorGain(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getGain();
  }

  public Float getDetectorVoltage(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getVoltage();
  }

  public Float getDetectorOffset(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getOffset();
  }

  // - Objective property retrieval -

  public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getManufacturer();
  }

  public String getObjectiveModel(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getModel();
  }

  public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getSerialNumber();
  }

  public Float getObjectiveLensNA(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getLensNA();
  }

  public Integer getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex) {
    // NB: NominalMagnification unsupported for schema version 2003 (FC)
    return null;
  }

  public Float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getMagnification();
  }

  // - OTF property retrieval -

  public Integer getOTFSizeX(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getSizeX();
  }

  public Integer getOTFSizeY(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getSizeY();
  }

  public String getOTFPixelType(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getPixelType();
  }

  public String getOTFPath(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getPath();
  }

  public Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.isOpticalAxisAverage();
  }

  // - Experimenter property retrieval -

  public String getExperimenterFirstName(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getFirstName();
  }

  public String getExperimenterLastName(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getLastName();
  }

  public String getExperimenterEmail(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getEmail();
  }

  public String getExperimenterInstitution(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getInstitution();
  }

  public String getExperimenterDataDirectory(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getDataDirectory();
  }

  // -- MetadataStore API methods --

  /* @see loci.formats.MetadataStore#setRoot(Object) */
  public void createRoot() {
    try {
      root = new OMENode();
    }
    catch (javax.xml.transform.TransformerException exc) {
      LogTools.trace(exc);
    }
    catch (java.io.IOException exc) { LogTools.trace(exc); }
    catch (org.xml.sax.SAXException exc) { LogTools.trace(exc); }
    catch (javax.xml.parsers.ParserConfigurationException exc) {
      LogTools.trace(exc);
    }
  }

  /* @see loci.formats.MetadataStore#setRoot(Object) */
  public void setRoot(Object root) {
    if (!(root instanceof OMENode)) {
      throw new IllegalArgumentException(
        "This metadata store accepts root objects of type " +
        OMENode.class.getName());
    }
    this.root = (OMENode) root;
  }


  // - Image property storage -

  public void setImageName(String name, int imageIndex) {
    if (name == null) return;
    ImageNode image = getImageNode(imageIndex, true);
    image.setName(name);
  }

  public void setImageCreationDate(String creationDate, int imageIndex) {
    if (creationDate == null) return;
    ImageNode image = getImageNode(imageIndex, true);
    image.setCreated(creationDate);
  }

  public void setImageDescription(String description, int imageIndex) {
    if (description == null) return;
    ImageNode image = getImageNode(imageIndex, true);
    image.setDescription(description);
  }

  // - Pixels property storage -

  public void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex) {
    if (sizeX == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeX(sizeX);
  }

  public void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex) {
    if (sizeY == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeY(sizeY);
  }

  public void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex) {
    if (sizeZ == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeZ(sizeZ);
  }

  public void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex) {
    if (sizeC == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeC(sizeC);
  }

  public void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex) {
    if (sizeT == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeT(sizeT);
  }

  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex) {
    if (pixelType == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setPixelType(pixelType);
  }

  public void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex) {
    if (bigEndian == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setBigEndian(bigEndian);
  }

  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex) {
    if (dimensionOrder == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setDimensionOrder(dimensionOrder);
  }

  // - Dimensions property storage -

  public void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex) {
    if (physicalSizeX == null) return;
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, true);
    dimensions.setPixelSizeX(physicalSizeX);
  }

  public void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex) {
    if (physicalSizeY == null) return;
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, true);
    dimensions.setPixelSizeY(physicalSizeY);
  }

  public void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex) {
    if (physicalSizeZ == null) return;
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, true);
    dimensions.setPixelSizeZ(physicalSizeZ);
  }

  public void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex) {
    if (timeIncrement == null) return;
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, true);
    dimensions.setPixelSizeT(timeIncrement);
  }

  public void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex) {
    // NB: WaveStart unsupported for schema version 2003 (FC)
  }

  public void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex) {
    if (waveIncrement == null) return;
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, true);
    dimensions.setPixelSizeC(dimensionsPixelSizeCFromInteger(waveIncrement));
  }

  // - ImagingEnvironment property storage -

  public void setImagingEnvironmentTemperature(Float temperature, int imageIndex) {
    if (temperature == null) return;
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironment.setTemperature(temperature);
  }

  public void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex) {
    if (airPressure == null) return;
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironment.setAirPressure(airPressure);
  }

  public void setImagingEnvironmentHumidity(Float humidity, int imageIndex) {
    if (humidity == null) return;
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironment.setHumidity(humidity);
  }

  public void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex) {
    if (cO2Percent == null) return;
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironment.setCO2Percent(cO2Percent);
  }

  // - Plane property storage -

  public void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: TheZ unsupported for schema version 2003 (FC)
  }

  public void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: TheC unsupported for schema version 2003 (FC)
  }

  public void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: TheT unsupported for schema version 2003 (FC)
  }

  // - PlaneTiming property storage -

  public void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: DeltaT unsupported for schema version 2003 (FC)
  }

  public void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: ExposureTime unsupported for schema version 2003 (FC)
  }

  // - StagePosition property storage -

  public void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PositionX unsupported for schema version 2003 (FC)
  }

  public void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PositionY unsupported for schema version 2003 (FC)
  }

  public void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PositionZ unsupported for schema version 2003 (FC)
  }

  // - LogicalChannel property storage -

  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex) {
    if (name == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setName(name);
  }

  public void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex) {
    if (samplesPerPixel == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setSamplesPerPixel(samplesPerPixel);
  }

  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex) {
    if (illuminationType == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setIlluminationType(illuminationType);
  }

  public void setLogicalChannelPinholeSize(Integer pinholeSize, int imageIndex, int logicalChannelIndex) {
    if (pinholeSize == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setPinholeSize(pinholeSize);
  }

  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex) {
    if (photometricInterpretation == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setPhotometricInterpretation(photometricInterpretation);
  }

  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex) {
    if (mode == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setMode(mode);
  }

  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex) {
    if (contrastMethod == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setContrastMethod(contrastMethod);
  }

  public void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex) {
    if (exWave == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setExcitationWavelength(exWave);
  }

  public void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex) {
    if (emWave == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setEmissionWavelength(emWave);
  }

  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex) {
    if (fluor == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setFluor(fluor);
  }

  public void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex) {
    if (ndFilter == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setNDFilter(ndFilter);
  }

  public void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex) {
    // NB: PockelCellSetting unsupported for schema version 2003 (FC)
  }

  // - DetectorSettings property storage -

  public void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex) {
    if (offset == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setDetectorOffset(offset);
  }

  public void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex) {
    if (gain == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setDetectorGain(gain);
  }

  // - LightSourceSettings property storage -

  public void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex) {
    if (attenuation == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setLightAttenuation(attenuation);
  }

  public void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex) {
    if (wavelength == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setLightWavelength(wavelength);
  }

  // - DisplayROI property storage -

  public void setDisplayROIX0(Integer x0, int imageIndex, int roiIndex) {
    if (x0 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setX0(x0);
  }

  public void setDisplayROIY0(Integer y0, int imageIndex, int roiIndex) {
    if (y0 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setY0(y0);
  }

  public void setDisplayROIZ0(Integer z0, int imageIndex, int roiIndex) {
    if (z0 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setZ0(z0);
  }

  public void setDisplayROIT0(Integer t0, int imageIndex, int roiIndex) {
    if (t0 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setT0(t0);
  }

  public void setDisplayROIX1(Integer x1, int imageIndex, int roiIndex) {
    if (x1 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setX1(x1);
  }

  public void setDisplayROIY1(Integer y1, int imageIndex, int roiIndex) {
    if (y1 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setY1(y1);
  }

  public void setDisplayROIZ1(Integer z1, int imageIndex, int roiIndex) {
    if (z1 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setZ1(z1);
  }

  public void setDisplayROIT1(Integer t1, int imageIndex, int roiIndex) {
    if (t1 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setT1(t1);
  }

  // - StageLabel property storage -

  public void setStageLabelName(String name, int imageIndex) {
    if (name == null) return;
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, true);
    stageLabel.setName(name);
  }

  public void setStageLabelX(Float x, int imageIndex) {
    if (x == null) return;
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, true);
    stageLabel.setX(x);
  }

  public void setStageLabelY(Float y, int imageIndex) {
    if (y == null) return;
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, true);
    stageLabel.setY(y);
  }

  public void setStageLabelZ(Float z, int imageIndex) {
    if (z == null) return;
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, true);
    stageLabel.setZ(z);
  }

  // - LightSource property storage -

  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex) {
    if (manufacturer == null) return;
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSource.setManufacturer(manufacturer);
  }

  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex) {
    if (model == null) return;
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSource.setModel(model);
  }

  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex) {
    if (serialNumber == null) return;
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSource.setSerialNumber(serialNumber);
  }

  // - Laser property storage -

  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex) {
    if (type == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setType(type);
  }

  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex) {
    if (laserMedium == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setMedium(laserMedium);
  }

  public void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex) {
    if (wavelength == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setWavelength(wavelength);
  }

  public void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex) {
    if (frequencyMultiplication == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setFrequencyDoubled(laserFrequencyDoubledFromInteger(frequencyMultiplication));
  }

  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex) {
    if (tuneable == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setTunable(tuneable);
  }

  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex) {
    if (pulse == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setPulse(pulse);
  }

  public void setLaserPower(Float power, int instrumentIndex, int lightSourceIndex) {
    if (power == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setPower(power);
  }

  // - Filament property storage -

  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex) {
    if (type == null) return;
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, true);
    filament.setType(type);
  }

  public void setFilamentPower(Float power, int instrumentIndex, int lightSourceIndex) {
    if (power == null) return;
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, true);
    filament.setPower(power);
  }

  // - Arc property storage -

  public void setArcType(String type, int instrumentIndex, int lightSourceIndex) {
    if (type == null) return;
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, true);
    arc.setType(type);
  }

  public void setArcPower(Float power, int instrumentIndex, int lightSourceIndex) {
    if (power == null) return;
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, true);
    arc.setPower(power);
  }

  // - Detector property storage -

  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex) {
    if (manufacturer == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setManufacturer(manufacturer);
  }

  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex) {
    if (model == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setModel(model);
  }

  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex) {
    if (serialNumber == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setSerialNumber(serialNumber);
  }

  public void setDetectorType(String type, int instrumentIndex, int detectorIndex) {
    if (type == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setType(type);
  }

  public void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex) {
    if (gain == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setGain(gain);
  }

  public void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex) {
    if (voltage == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setVoltage(voltage);
  }

  public void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex) {
    if (offset == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setOffset(offset);
  }

  // - Objective property storage -

  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex) {
    if (manufacturer == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setManufacturer(manufacturer);
  }

  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex) {
    if (model == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setModel(model);
  }

  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex) {
    if (serialNumber == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setSerialNumber(serialNumber);
  }

  public void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex) {
    if (lensNA == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setLensNA(lensNA);
  }

  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex) {
    // NB: NominalMagnification unsupported for schema version 2003 (FC)
  }

  public void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex) {
    if (calibratedMagnification == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setMagnification(calibratedMagnification);
  }

  // - OTF property storage -

  public void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex) {
    if (sizeX == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setSizeX(sizeX);
  }

  public void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex) {
    if (sizeY == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setSizeY(sizeY);
  }

  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex) {
    if (pixelType == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setPixelType(pixelType);
  }

  public void setOTFPath(String path, int instrumentIndex, int otfIndex) {
    if (path == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setPath(path);
  }

  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
    if (opticalAxisAveraged == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setOpticalAxisAverage(opticalAxisAveraged);
  }

  // - Experimenter property storage -

  public void setExperimenterFirstName(String firstName, int experimenterIndex) {
    if (firstName == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setFirstName(firstName);
  }

  public void setExperimenterLastName(String lastName, int experimenterIndex) {
    if (lastName == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setLastName(lastName);
  }

  public void setExperimenterEmail(String email, int experimenterIndex) {
    if (email == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setEmail(email);
  }

  public void setExperimenterInstitution(String institution, int experimenterIndex) {
    if (institution == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setInstitution(institution);
  }

  public void setExperimenterDataDirectory(String dataDirectory, int experimenterIndex) {
    if (dataDirectory == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setDataDirectory(dataDirectory);
  }

  // -- Helper methods --

  // Image+
  private ImageNode getImageNode(int imageIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    return image;
  }

  // Image+/CA/Pixels+
  private PixelsNode getPixelsNode(int imageIndex, int pixelsIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get Pixels node
    ndx = pixelsIndex;
    count = ca.countCAList("Pixels");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new PixelsNode(ca);
    list = ca.getCAList("Pixels");
    PixelsNode pixels = (PixelsNode) list.get(ndx);
    return pixels;
  }

  // Image+/CA/Dimensions
  private DimensionsNode getDimensionsNode(int imageIndex, int pixelsIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get Dimensions node
    DimensionsNode dimensions = null;
    count = ca.countCAList("Dimensions");
    if (count >= 1) {
      dimensions = (DimensionsNode) ca.getCAList("Dimensions").get(0);
    }
    if (dimensions == null) {
      if (!create) return null;
      dimensions = new DimensionsNode(ca);
    }
    return dimensions;
  }

  // Image+/CA/ImagingEnvironment
  private ImagingEnvironmentNode getImagingEnvironmentNode(int imageIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get ImagingEnvironment node
    ImagingEnvironmentNode imagingEnvironment = null;
    count = ca.countCAList("ImagingEnvironment");
    if (count >= 1) {
      imagingEnvironment = (ImagingEnvironmentNode) ca.getCAList("ImagingEnvironment").get(0);
    }
    if (imagingEnvironment == null) {
      if (!create) return null;
      imagingEnvironment = new ImagingEnvironmentNode(ca);
    }
    return imagingEnvironment;
  }

  // Image+/CA/LogicalChannel+
  private LogicalChannelNode getLogicalChannelNode(int imageIndex, int logicalChannelIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get LogicalChannel node
    ndx = logicalChannelIndex;
    count = ca.countCAList("LogicalChannel");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new LogicalChannelNode(ca);
    list = ca.getCAList("LogicalChannel");
    LogicalChannelNode logicalChannel = (LogicalChannelNode) list.get(ndx);
    return logicalChannel;
  }

  // Image+/CA/DisplayROI+
  private DisplayROINode getDisplayROINode(int imageIndex, int roiIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get DisplayROI node
    ndx = roiIndex;
    count = ca.countCAList("DisplayROI");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new DisplayROINode(ca);
    list = ca.getCAList("DisplayROI");
    DisplayROINode displayROI = (DisplayROINode) list.get(ndx);
    return displayROI;
  }

  // Image+/CA/StageLabel
  private StageLabelNode getStageLabelNode(int imageIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get StageLabel node
    StageLabelNode stageLabel = null;
    count = ca.countCAList("StageLabel");
    if (count >= 1) {
      stageLabel = (StageLabelNode) ca.getCAList("StageLabel").get(0);
    }
    if (stageLabel == null) {
      if (!create) return null;
      stageLabel = new StageLabelNode(ca);
    }
    return stageLabel;
  }

  // CA/Instrument+/@LightSource+
  private LightSourceNode getLightSourceNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get LightSource node
    ndx = lightSourceIndex;
    count = instrument.countLightSourceList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new LightSourceNode(ca).setInstrument(instrument);
    }
    list = instrument.getLightSourceList();
    LightSourceNode lightSource = (LightSourceNode) list.get(ndx);
    return lightSource;
  }

  // CA/Instrument+/@LightSource+/@!Laser
  private LaserNode getLaserNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get LightSource node
    ndx = lightSourceIndex;
    count = instrument.countLightSourceList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new LightSourceNode(ca).setInstrument(instrument);
    }
    list = instrument.getLightSourceList();
    LightSourceNode lightSource = (LightSourceNode) list.get(ndx);
    // get Laser node
    LaserNode laser = null;
    count = lightSource.countLaserListByLightSource();
    if (count >= 1) {
      laser = (LaserNode) lightSource.getLaserListByLightSource().get(0);
    }
    if (laser == null) {
      if (!create) return null;
      laser = new LaserNode(ca);
      laser.setLightSource(lightSource);
    }
    return laser;
  }

  // CA/Instrument+/@LightSource+/@Filament
  private FilamentNode getFilamentNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get LightSource node
    ndx = lightSourceIndex;
    count = instrument.countLightSourceList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new LightSourceNode(ca).setInstrument(instrument);
    }
    list = instrument.getLightSourceList();
    LightSourceNode lightSource = (LightSourceNode) list.get(ndx);
    // get Filament node
    FilamentNode filament = null;
    count = lightSource.countFilamentList();
    if (count >= 1) {
      filament = (FilamentNode) lightSource.getFilamentList().get(0);
    }
    if (filament == null) {
      if (!create) return null;
      filament = new FilamentNode(ca);
      filament.setLightSource(lightSource);
    }
    return filament;
  }

  // CA/Instrument+/@LightSource+/@Arc
  private ArcNode getArcNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get LightSource node
    ndx = lightSourceIndex;
    count = instrument.countLightSourceList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new LightSourceNode(ca).setInstrument(instrument);
    }
    list = instrument.getLightSourceList();
    LightSourceNode lightSource = (LightSourceNode) list.get(ndx);
    // get Arc node
    ArcNode arc = null;
    count = lightSource.countArcList();
    if (count >= 1) {
      arc = (ArcNode) lightSource.getArcList().get(0);
    }
    if (arc == null) {
      if (!create) return null;
      arc = new ArcNode(ca);
      arc.setLightSource(lightSource);
    }
    return arc;
  }

  // CA/Instrument+/@Detector+
  private DetectorNode getDetectorNode(int instrumentIndex, int detectorIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get Detector node
    ndx = detectorIndex;
    count = instrument.countDetectorList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new DetectorNode(ca).setInstrument(instrument);
    }
    list = instrument.getDetectorList();
    DetectorNode detector = (DetectorNode) list.get(ndx);
    return detector;
  }

  // CA/Instrument+/@Objective+
  private ObjectiveNode getObjectiveNode(int instrumentIndex, int objectiveIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get Objective node
    ndx = objectiveIndex;
    count = instrument.countObjectiveList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new ObjectiveNode(ca).setInstrument(instrument);
    }
    list = instrument.getObjectiveList();
    ObjectiveNode objective = (ObjectiveNode) list.get(ndx);
    return objective;
  }

  // CA/Instrument+/@OTF+
  private OTFNode getOTFNode(int instrumentIndex, int otfIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get OTF node
    ndx = otfIndex;
    count = instrument.countOTFList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new OTFNode(ca).setInstrument(instrument);
    }
    list = instrument.getOTFList();
    OTFNode otf = (OTFNode) list.get(ndx);
    return otf;
  }

  // CA/Experimenter+
  private ExperimenterNode getExperimenterNode(int experimenterIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Experimenter node
    ndx = experimenterIndex;
    count = ca.countCAList("Experimenter");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ExperimenterNode(ca);
    list = ca.getCAList("Experimenter");
    ExperimenterNode experimenter = (ExperimenterNode) list.get(ndx);
    return experimenter;
  }

}
