//
// OMEXML200706Metadata.java
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
 * Created by curtis via MetadataAutogen on Jan 23, 2008 3:27:29 PM CST
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.ome;

import ome.xml.r200706.ome.*;
import java.util.List;
import loci.formats.LogTools;

/**
 * A utility class for constructing and manipulating OME-XML DOMs for the
 * 2007-06 version of OME-XML. It requires the
 * ome.xml.r200706 package to compile (part of ome-java.jar).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ome/OMEXML200706Metadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ome/OMEXML200706Metadata.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OMEXML200706Metadata extends OMEXMLMetadata {

  // -- OMEXMLMetadata API methods --

  /* @see OMEXMLMetadata#dumpXML() */
  public String dumpXML() {
    if (root == null) return null;
    try {
      java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
      ome.xml.DOMUtil.writeXML(os, root.getDOMElement().getOwnerDocument());
      return os.toString();
    }
    catch (javax.xml.transform.TransformerException exc) {
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
    return getPixelsNode(imageIndex, pixelsIndex, false);
  }

  public Object getImagingEnvironment(int imageIndex) {
    return getImagingEnvironmentNode(imageIndex, false);
  }

  public Object getPlane(int imageIndex, int pixelsIndex, int planeIndex) {
    return getPlaneNode(imageIndex, pixelsIndex, planeIndex, false);
  }

  public Object getPlaneTiming(int imageIndex, int pixelsIndex, int planeIndex) {
    return getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, false);
  }

  public Object getStagePosition(int imageIndex, int pixelsIndex, int planeIndex) {
    return getStagePositionNode(imageIndex, pixelsIndex, planeIndex, false);
  }

  public Object getLogicalChannel(int imageIndex, int logicalChannelIndex) {
    return getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
  }

  public Object getDetectorSettings(int imageIndex, int logicalChannelIndex) {
    return getDetectorRefNode(imageIndex, logicalChannelIndex, false);
  }

  public Object getLightSourceSettings(int imageIndex, int logicalChannelIndex) {
    return getLightSourceRefNode(imageIndex, logicalChannelIndex, false);
  }

  public Object getDisplayROI(int imageIndex, int roiIndex) {
    return getROINode(imageIndex, roiIndex, false);
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
    return image == null ? null : image.getCreationDate();
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
    return pixels == null ? null : pixels.getBigEndian();
  }

  public String getPixelsDimensionOrder(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getDimensionOrder();
  }

  // - Dimensions property retrieval -

  public Float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPhysicalSizeX();
  }

  public Float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPhysicalSizeY();
  }

  public Float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPhysicalSizeZ();
  }

  public Float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getTimeIncrement();
  }

  public Integer getDimensionsWaveStart(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getWaveStart();
  }

  public Integer getDimensionsWaveIncrement(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getWaveIncrement();
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
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, false);
    return plane == null ? null : plane.getTheZ();
  }

  public Integer getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, false);
    return plane == null ? null : plane.getTheC();
  }

  public Integer getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, false);
    return plane == null ? null : plane.getTheT();
  }

  // - PlaneTiming property retrieval -

  public Float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneTimingNode planeTiming = getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, false);
    return planeTiming == null ? null : planeTiming.getDeltaT();
  }

  public Float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneTimingNode planeTiming = getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, false);
    return planeTiming == null ? null : planeTiming.getExposureTime();
  }

  // - StagePosition property retrieval -

  public Float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex) {
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, false);
    return stagePosition == null ? null : stagePosition.getPositionX();
  }

  public Float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex) {
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, false);
    return stagePosition == null ? null : stagePosition.getPositionY();
  }

  public Float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex) {
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, false);
    return stagePosition == null ? null : stagePosition.getPositionZ();
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
    return logicalChannel == null ? null : logicalChannel.getExWave();
  }

  public Integer getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getEmWave();
  }

  public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getFluor();
  }

  public Float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getNdFilter();
  }

  public Integer getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getPockelCellSetting();
  }

  // - DetectorSettings property retrieval -

  public Float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex) {
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, false);
    return detectorRef == null ? null : detectorRef.getOffset();
  }

  public Float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex) {
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, false);
    return detectorRef == null ? null : detectorRef.getGain();
  }

  // - LightSourceSettings property retrieval -

  public Float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex) {
    LightSourceRefNode lightSourceRef = getLightSourceRefNode(imageIndex, logicalChannelIndex, false);
    return lightSourceRef == null ? null : lightSourceRef.getAttenuation();
  }

  public Integer getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex) {
    LightSourceRefNode lightSourceRef = getLightSourceRefNode(imageIndex, logicalChannelIndex, false);
    return lightSourceRef == null ? null : lightSourceRef.getWavelength();
  }

  // - DisplayROI property retrieval -

  public Integer getDisplayROIX0(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getX0();
  }

  public Integer getDisplayROIY0(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getY0();
  }

  public Integer getDisplayROIZ0(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getZ0();
  }

  public Integer getDisplayROIT0(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getT0();
  }

  public Integer getDisplayROIX1(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getX1();
  }

  public Integer getDisplayROIY1(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getY1();
  }

  public Integer getDisplayROIZ1(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getZ1();
  }

  public Integer getDisplayROIT1(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getT1();
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
    return laser == null ? null : laser.getLaserMedium();
  }

  public Integer getLaserWavelength(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getWavelength();
  }

  public Integer getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getFrequencyMultiplication();
  }

  public Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getTuneable();
  }

  public String getLaserPulse(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getPulse();
  }

  public Float getLaserPower(int instrumentIndex, int lightSourceIndex) {
    // NB: Power unsupported for schema version 2007-06
    return null;
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
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getNominalMagnification();
  }

  public Float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getCalibratedMagnification();
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
    // NB: Path unsupported for schema version 2007-06
    return null;
  }

  public Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getOpticalAxisAveraged();
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
    // NB: DataDirectory unsupported for schema version 2007-06
    return null;
  }

  // -- MetadataStore API methods --

  /* @see loci.formats.MetadataStore#setRoot(Object) */
  public void createRoot() {
    try {
      root = ome.xml.OMEXMLFactory.newOMENode("2007-06");
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
        "Invalid root type: " + root.getClass().getName() + ". " +
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
    image.setCreationDate(creationDate);
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
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setPhysicalSizeX(physicalSizeX);
  }

  public void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex) {
    if (physicalSizeY == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setPhysicalSizeY(physicalSizeY);
  }

  public void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex) {
    if (physicalSizeZ == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setPhysicalSizeZ(physicalSizeZ);
  }

  public void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex) {
    if (timeIncrement == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setTimeIncrement(timeIncrement);
  }

  public void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex) {
    if (waveStart == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setWaveStart(waveStart);
  }

  public void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex) {
    if (waveIncrement == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setWaveIncrement(waveIncrement);
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
    if (theZ == null) return;
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, true);
    plane.setTheZ(theZ);
  }

  public void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex) {
    if (theC == null) return;
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, true);
    plane.setTheC(theC);
  }

  public void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex) {
    if (theT == null) return;
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, true);
    plane.setTheT(theT);
  }

  // - PlaneTiming property storage -

  public void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex) {
    if (deltaT == null) return;
    PlaneTimingNode planeTiming = getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, true);
    planeTiming.setDeltaT(deltaT);
  }

  public void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex) {
    if (exposureTime == null) return;
    PlaneTimingNode planeTiming = getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, true);
    planeTiming.setExposureTime(exposureTime);
  }

  // - StagePosition property storage -

  public void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex) {
    if (positionX == null) return;
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, true);
    stagePosition.setPositionX(positionX);
  }

  public void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex) {
    if (positionY == null) return;
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, true);
    stagePosition.setPositionY(positionY);
  }

  public void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex) {
    if (positionZ == null) return;
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, true);
    stagePosition.setPositionZ(positionZ);
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
    logicalChannel.setExWave(exWave);
  }

  public void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex) {
    if (emWave == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setEmWave(emWave);
  }

  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex) {
    if (fluor == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setFluor(fluor);
  }

  public void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex) {
    if (ndFilter == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setNdFilter(ndFilter);
  }

  public void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex) {
    if (pockelCellSetting == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setPockelCellSetting(pockelCellSetting);
  }

  // - DetectorSettings property storage -

  public void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex) {
    if (offset == null) return;
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, true);
    detectorRef.setOffset(offset);
  }

  public void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex) {
    if (gain == null) return;
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, true);
    detectorRef.setGain(gain);
  }

  // - LightSourceSettings property storage -

  public void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex) {
    if (attenuation == null) return;
    LightSourceRefNode lightSourceRef = getLightSourceRefNode(imageIndex, logicalChannelIndex, true);
    lightSourceRef.setAttenuation(attenuation);
  }

  public void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex) {
    if (wavelength == null) return;
    LightSourceRefNode lightSourceRef = getLightSourceRefNode(imageIndex, logicalChannelIndex, true);
    lightSourceRef.setWavelength(wavelength);
  }

  // - DisplayROI property storage -

  public void setDisplayROIX0(Integer x0, int imageIndex, int roiIndex) {
    if (x0 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setX0(x0);
  }

  public void setDisplayROIY0(Integer y0, int imageIndex, int roiIndex) {
    if (y0 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setY0(y0);
  }

  public void setDisplayROIZ0(Integer z0, int imageIndex, int roiIndex) {
    if (z0 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setZ0(z0);
  }

  public void setDisplayROIT0(Integer t0, int imageIndex, int roiIndex) {
    if (t0 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setT0(t0);
  }

  public void setDisplayROIX1(Integer x1, int imageIndex, int roiIndex) {
    if (x1 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setX1(x1);
  }

  public void setDisplayROIY1(Integer y1, int imageIndex, int roiIndex) {
    if (y1 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setY1(y1);
  }

  public void setDisplayROIZ1(Integer z1, int imageIndex, int roiIndex) {
    if (z1 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setZ1(z1);
  }

  public void setDisplayROIT1(Integer t1, int imageIndex, int roiIndex) {
    if (t1 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setT1(t1);
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
    laser.setLaserMedium(laserMedium);
  }

  public void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex) {
    if (wavelength == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setWavelength(wavelength);
  }

  public void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex) {
    if (frequencyMultiplication == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setFrequencyMultiplication(frequencyMultiplication);
  }

  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex) {
    if (tuneable == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setTuneable(tuneable);
  }

  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex) {
    if (pulse == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setPulse(pulse);
  }

  public void setLaserPower(Float power, int instrumentIndex, int lightSourceIndex) {
    // NB: Power unsupported for schema version 2007-06
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
    if (nominalMagnification == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setNominalMagnification(nominalMagnification);
  }

  public void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex) {
    if (calibratedMagnification == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setCalibratedMagnification(calibratedMagnification);
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
    // NB: Path unsupported for schema version 2007-06
  }

  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
    if (opticalAxisAveraged == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setOpticalAxisAveraged(opticalAxisAveraged);
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
    // NB: DataDirectory unsupported for schema version 2007-06
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
    count = ome.getImageCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    return image;
  }

  // Image+/Pixels+
  private PixelsNode getPixelsNode(int imageIndex, int pixelsIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.getImageCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get Pixels node
    ndx = pixelsIndex;
    count = image.getPixelsCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new PixelsNode(image);
    list = image.getPixelsList();
    PixelsNode pixels = (PixelsNode) list.get(ndx);
    return pixels;
  }

  // Image+/ImagingEnvironment
  private ImagingEnvironmentNode getImagingEnvironmentNode(int imageIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.getImageCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get ImagingEnvironment node
    ImagingEnvironmentNode imagingEnvironment = image.getImagingEnvironment();
    if (imagingEnvironment == null) {
      if (!create) return null;
      imagingEnvironment = new ImagingEnvironmentNode(image);
    }
    return imagingEnvironment;
  }

  // Image+/Pixels+/Plane+
  private PlaneNode getPlaneNode(int imageIndex, int pixelsIndex, int planeIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.getImageCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get Pixels node
    ndx = pixelsIndex;
    count = image.getPixelsCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new PixelsNode(image);
    list = image.getPixelsList();
    PixelsNode pixels = (PixelsNode) list.get(ndx);
    // get Plane node
    ndx = planeIndex;
    count = pixels.getPlaneCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new PlaneNode(pixels);
    list = pixels.getPlaneList();
    PlaneNode plane = (PlaneNode) list.get(ndx);
    return plane;
  }

  // Image+/Pixels+/Plane+/PlaneTiming
  private PlaneTimingNode getPlaneTimingNode(int imageIndex, int pixelsIndex, int planeIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.getImageCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get Pixels node
    ndx = pixelsIndex;
    count = image.getPixelsCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new PixelsNode(image);
    list = image.getPixelsList();
    PixelsNode pixels = (PixelsNode) list.get(ndx);
    // get Plane node
    ndx = planeIndex;
    count = pixels.getPlaneCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new PlaneNode(pixels);
    list = pixels.getPlaneList();
    PlaneNode plane = (PlaneNode) list.get(ndx);
    // get PlaneTiming node
    PlaneTimingNode planeTiming = plane.getPlaneTiming();
    if (planeTiming == null) {
      if (!create) return null;
      planeTiming = new PlaneTimingNode(plane);
    }
    return planeTiming;
  }

  // Image+/Pixels+/Plane+/StagePosition
  private StagePositionNode getStagePositionNode(int imageIndex, int pixelsIndex, int planeIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.getImageCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get Pixels node
    ndx = pixelsIndex;
    count = image.getPixelsCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new PixelsNode(image);
    list = image.getPixelsList();
    PixelsNode pixels = (PixelsNode) list.get(ndx);
    // get Plane node
    ndx = planeIndex;
    count = pixels.getPlaneCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new PlaneNode(pixels);
    list = pixels.getPlaneList();
    PlaneNode plane = (PlaneNode) list.get(ndx);
    // get StagePosition node
    StagePositionNode stagePosition = plane.getStagePosition();
    if (stagePosition == null) {
      if (!create) return null;
      stagePosition = new StagePositionNode(plane);
    }
    return stagePosition;
  }

  // Image+/LogicalChannel+
  private LogicalChannelNode getLogicalChannelNode(int imageIndex, int logicalChannelIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.getImageCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get LogicalChannel node
    ndx = logicalChannelIndex;
    count = image.getLogicalChannelCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new LogicalChannelNode(image);
    list = image.getLogicalChannelList();
    LogicalChannelNode logicalChannel = (LogicalChannelNode) list.get(ndx);
    return logicalChannel;
  }

  // Image+/LogicalChannel+/DetectorRef
  private DetectorRefNode getDetectorRefNode(int imageIndex, int logicalChannelIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.getImageCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get LogicalChannel node
    ndx = logicalChannelIndex;
    count = image.getLogicalChannelCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new LogicalChannelNode(image);
    list = image.getLogicalChannelList();
    LogicalChannelNode logicalChannel = (LogicalChannelNode) list.get(ndx);
    // get DetectorRef node
    DetectorRefNode detectorRef = logicalChannel.getDetectorRef();
    if (detectorRef == null) {
      if (!create) return null;
      detectorRef = new DetectorRefNode(logicalChannel);
    }
    return detectorRef;
  }

  // Image+/LogicalChannel+/LightSourceRef
  private LightSourceRefNode getLightSourceRefNode(int imageIndex, int logicalChannelIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.getImageCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get LogicalChannel node
    ndx = logicalChannelIndex;
    count = image.getLogicalChannelCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new LogicalChannelNode(image);
    list = image.getLogicalChannelList();
    LogicalChannelNode logicalChannel = (LogicalChannelNode) list.get(ndx);
    // get LightSourceRef node
    LightSourceRefNode lightSourceRef = logicalChannel.getLightSourceRef();
    if (lightSourceRef == null) {
      if (!create) return null;
      lightSourceRef = new LightSourceRefNode(logicalChannel);
    }
    return lightSourceRef;
  }

  // Image+/DisplayOptions/ROI+
  private ROINode getROINode(int imageIndex, int roiIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.getImageCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get DisplayOptions node
    DisplayOptionsNode displayOptions = image.getDisplayOptions();
    if (displayOptions == null) {
      if (!create) return null;
      displayOptions = new DisplayOptionsNode(image);
    }
    // get ROI node
    ndx = roiIndex;
    count = displayOptions.getROICount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ROINode(displayOptions);
    list = displayOptions.getROIList();
    ROINode roi = (ROINode) list.get(ndx);
    return roi;
  }

  // Image+/StageLabel
  private StageLabelNode getStageLabelNode(int imageIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.getImageCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get StageLabel node
    StageLabelNode stageLabel = image.getStageLabel();
    if (stageLabel == null) {
      if (!create) return null;
      stageLabel = new StageLabelNode(image);
    }
    return stageLabel;
  }

  // Instrument+/LightSource+
  private LightSourceNode getLightSourceNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Instrument node
    ndx = instrumentIndex;
    count = ome.getInstrumentCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ome);
    list = ome.getInstrumentList();
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get LightSource node
    ndx = lightSourceIndex;
    count = instrument.getLightSourceCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new LightSourceNode(instrument);
    list = instrument.getLightSourceList();
    LightSourceNode lightSource = (LightSourceNode) list.get(ndx);
    return lightSource;
  }

  // Instrument+/LightSource+/Laser
  private LaserNode getLaserNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Instrument node
    ndx = instrumentIndex;
    count = ome.getInstrumentCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ome);
    list = ome.getInstrumentList();
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get LightSource node
    ndx = lightSourceIndex;
    count = instrument.getLightSourceCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new LightSourceNode(instrument);
    list = instrument.getLightSourceList();
    LightSourceNode lightSource = (LightSourceNode) list.get(ndx);
    // get Laser node
    LaserNode laser = lightSource.getLaser();
    if (laser == null) {
      if (!create) return null;
      laser = new LaserNode(lightSource);
    }
    return laser;
  }

  // Instrument+/LightSource+/Filament
  private FilamentNode getFilamentNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Instrument node
    ndx = instrumentIndex;
    count = ome.getInstrumentCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ome);
    list = ome.getInstrumentList();
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get LightSource node
    ndx = lightSourceIndex;
    count = instrument.getLightSourceCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new LightSourceNode(instrument);
    list = instrument.getLightSourceList();
    LightSourceNode lightSource = (LightSourceNode) list.get(ndx);
    // get Filament node
    FilamentNode filament = lightSource.getFilament();
    if (filament == null) {
      if (!create) return null;
      filament = new FilamentNode(lightSource);
    }
    return filament;
  }

  // Instrument+/LightSource+/Arc
  private ArcNode getArcNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Instrument node
    ndx = instrumentIndex;
    count = ome.getInstrumentCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ome);
    list = ome.getInstrumentList();
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get LightSource node
    ndx = lightSourceIndex;
    count = instrument.getLightSourceCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new LightSourceNode(instrument);
    list = instrument.getLightSourceList();
    LightSourceNode lightSource = (LightSourceNode) list.get(ndx);
    // get Arc node
    ArcNode arc = lightSource.getArc();
    if (arc == null) {
      if (!create) return null;
      arc = new ArcNode(lightSource);
    }
    return arc;
  }

  // Instrument+/Detector+
  private DetectorNode getDetectorNode(int instrumentIndex, int detectorIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Instrument node
    ndx = instrumentIndex;
    count = ome.getInstrumentCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ome);
    list = ome.getInstrumentList();
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get Detector node
    ndx = detectorIndex;
    count = instrument.getDetectorCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new DetectorNode(instrument);
    list = instrument.getDetectorList();
    DetectorNode detector = (DetectorNode) list.get(ndx);
    return detector;
  }

  // Instrument+/Objective+
  private ObjectiveNode getObjectiveNode(int instrumentIndex, int objectiveIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Instrument node
    ndx = instrumentIndex;
    count = ome.getInstrumentCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ome);
    list = ome.getInstrumentList();
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get Objective node
    ndx = objectiveIndex;
    count = instrument.getObjectiveCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ObjectiveNode(instrument);
    list = instrument.getObjectiveList();
    ObjectiveNode objective = (ObjectiveNode) list.get(ndx);
    return objective;
  }

  // Instrument+/OTF+
  private OTFNode getOTFNode(int instrumentIndex, int otfIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Instrument node
    ndx = instrumentIndex;
    count = ome.getInstrumentCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ome);
    list = ome.getInstrumentList();
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get OTF node
    ndx = otfIndex;
    count = instrument.getOTFCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new OTFNode(instrument);
    list = instrument.getOTFList();
    OTFNode otf = (OTFNode) list.get(ndx);
    return otf;
  }

  // Experimenter+
  private ExperimenterNode getExperimenterNode(int experimenterIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Experimenter node
    ndx = experimenterIndex;
    count = ome.getExperimenterCount();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ExperimenterNode(ome);
    list = ome.getExperimenterList();
    ExperimenterNode experimenter = (ExperimenterNode) list.get(ndx);
    return experimenter;
  }

}
