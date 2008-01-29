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
 * Created by curtis via MetadataAutogen on Jan 29, 2008 2:12:52 PM CST
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

  // - Entity counting -

  /* @see loci.formats.meta.MetadataRetrieve#getImageCount() */
  public int getImageCount() {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getImage(i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsCount(int) */
  public int getPixelsCount(int imageIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getPixels(imageIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneCount(int, int) */
  public int getPlaneCount(int imageIndex, int pixelsIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getPlane(imageIndex, pixelsIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelCount(int) */
  public int getLogicalChannelCount(int imageIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getLogicalChannel(imageIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROICount(int) */
  public int getROICount(int imageIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getROI(imageIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceCount(int) */
  public int getLightSourceCount(int instrumentIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getLightSource(instrumentIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorCount(int) */
  public int getDetectorCount(int instrumentIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getDetector(instrumentIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveCount(int) */
  public int getObjectiveCount(int instrumentIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getObjective(instrumentIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFCount(int) */
  public int getOTFCount(int instrumentIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getOTF(instrumentIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterCount() */
  public int getExperimenterCount() {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getExperimenter(i) == null) return i;
    }
    return -1;
  }

  // - Entity retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getImage(int) */
  public Object getImage(int imageIndex) {
    return getImageNode(imageIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixels(int, int) */
  public Object getPixels(int imageIndex, int pixelsIndex) {
    return getPixelsNode(imageIndex, pixelsIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensions(int, int) */
  public Object getDimensions(int imageIndex, int pixelsIndex) {
    return getPixelsNode(imageIndex, pixelsIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironment(int) */
  public Object getImagingEnvironment(int imageIndex) {
    return getImagingEnvironmentNode(imageIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlane(int, int, int) */
  public Object getPlane(int imageIndex, int pixelsIndex, int planeIndex) {
    return getPlaneNode(imageIndex, pixelsIndex, planeIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTiming(int, int, int) */
  public Object getPlaneTiming(int imageIndex, int pixelsIndex, int planeIndex) {
    return getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStagePosition(int, int, int) */
  public Object getStagePosition(int imageIndex, int pixelsIndex, int planeIndex) {
    return getStagePositionNode(imageIndex, pixelsIndex, planeIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannel(int, int) */
  public Object getLogicalChannel(int imageIndex, int logicalChannelIndex) {
    return getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettings(int, int) */
  public Object getDetectorSettings(int imageIndex, int logicalChannelIndex) {
    return getDetectorRefNode(imageIndex, logicalChannelIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSettings(int, int) */
  public Object getLightSourceSettings(int imageIndex, int logicalChannelIndex) {
    return getLightSourceRefNode(imageIndex, logicalChannelIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROI(int, int) */
  public Object getROI(int imageIndex, int roiIndex) {
    return getROINode(imageIndex, roiIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStageLabel(int) */
  public Object getStageLabel(int imageIndex) {
    return getStageLabelNode(imageIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSource(int, int) */
  public Object getLightSource(int instrumentIndex, int lightSourceIndex) {
    return getLightSourceNode(instrumentIndex, lightSourceIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaser(int, int) */
  public Object getLaser(int instrumentIndex, int lightSourceIndex) {
    return getLaserNode(instrumentIndex, lightSourceIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getFilament(int, int) */
  public Object getFilament(int instrumentIndex, int lightSourceIndex) {
    return getFilamentNode(instrumentIndex, lightSourceIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getArc(int, int) */
  public Object getArc(int instrumentIndex, int lightSourceIndex) {
    return getArcNode(instrumentIndex, lightSourceIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetector(int, int) */
  public Object getDetector(int instrumentIndex, int detectorIndex) {
    return getDetectorNode(instrumentIndex, detectorIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjective(int, int) */
  public Object getObjective(int instrumentIndex, int objectiveIndex) {
    return getObjectiveNode(instrumentIndex, objectiveIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTF(int, int) */
  public Object getOTF(int instrumentIndex, int otfIndex) {
    return getOTFNode(instrumentIndex, otfIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenter(int) */
  public Object getExperimenter(int experimenterIndex) {
    return getExperimenterNode(experimenterIndex, false);
  }

  // - Image property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getImageName(int) */
  public String getImageName(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImageCreationDate(int) */
  public String getImageCreationDate(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getCreationDate();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImageDescription(int) */
  public String getImageDescription(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getDescription();
  }

  // - Pixels property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsSizeX(int, int) */
  public Integer getPixelsSizeX(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeX();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsSizeY(int, int) */
  public Integer getPixelsSizeY(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeY();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsSizeZ(int, int) */
  public Integer getPixelsSizeZ(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeZ();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsSizeC(int, int) */
  public Integer getPixelsSizeC(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeC();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsSizeT(int, int) */
  public Integer getPixelsSizeT(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeT();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsPixelType(int, int) */
  public String getPixelsPixelType(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPixelType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsBigEndian(int, int) */
  public Boolean getPixelsBigEndian(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getBigEndian();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsDimensionOrder(int, int) */
  public String getPixelsDimensionOrder(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getDimensionOrder();
  }

  // - Dimensions property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsPhysicalSizeX(int, int) */
  public Float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPhysicalSizeX();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsPhysicalSizeY(int, int) */
  public Float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPhysicalSizeY();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsPhysicalSizeZ(int, int) */
  public Float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPhysicalSizeZ();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsTimeIncrement(int, int) */
  public Float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getTimeIncrement();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsWaveStart(int, int) */
  public Integer getDimensionsWaveStart(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getWaveStart();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsWaveIncrement(int, int) */
  public Integer getDimensionsWaveIncrement(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getWaveIncrement();
  }

  // - ImagingEnvironment property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironmentTemperature(int) */
  public Float getImagingEnvironmentTemperature(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getTemperature();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironmentAirPressure(int) */
  public Float getImagingEnvironmentAirPressure(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getAirPressure();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironmentHumidity(int) */
  public Float getImagingEnvironmentHumidity(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getHumidity();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironmentCO2Percent(int) */
  public Float getImagingEnvironmentCO2Percent(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getCO2Percent();
  }

  // - Plane property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTheZ(int, int, int) */
  public Integer getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, false);
    return plane == null ? null : plane.getTheZ();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTheC(int, int, int) */
  public Integer getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, false);
    return plane == null ? null : plane.getTheC();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTheT(int, int, int) */
  public Integer getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, false);
    return plane == null ? null : plane.getTheT();
  }

  // - PlaneTiming property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTimingDeltaT(int, int, int) */
  public Float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneTimingNode planeTiming = getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, false);
    return planeTiming == null ? null : planeTiming.getDeltaT();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTimingExposureTime(int, int, int) */
  public Float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneTimingNode planeTiming = getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, false);
    return planeTiming == null ? null : planeTiming.getExposureTime();
  }

  // - StagePosition property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getStagePositionPositionX(int, int, int) */
  public Float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex) {
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, false);
    return stagePosition == null ? null : stagePosition.getPositionX();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStagePositionPositionY(int, int, int) */
  public Float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex) {
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, false);
    return stagePosition == null ? null : stagePosition.getPositionY();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStagePositionPositionZ(int, int, int) */
  public Float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex) {
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, false);
    return stagePosition == null ? null : stagePosition.getPositionZ();
  }

  // - LogicalChannel property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelName(int, int) */
  public String getLogicalChannelName(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelSamplesPerPixel(int, int) */
  public Integer getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getSamplesPerPixel();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelIlluminationType(int, int) */
  public String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getIlluminationType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelPinholeSize(int, int) */
  public Integer getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getPinholeSize();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelPhotometricInterpretation(int, int) */
  public String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getPhotometricInterpretation();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelMode(int, int) */
  public String getLogicalChannelMode(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getMode();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelContrastMethod(int, int) */
  public String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getContrastMethod();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelExWave(int, int) */
  public Integer getLogicalChannelExWave(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getExWave();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelEmWave(int, int) */
  public Integer getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getEmWave();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelFluor(int, int) */
  public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getFluor();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelNdFilter(int, int) */
  public Float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getNdFilter();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelPockelCellSetting(int, int) */
  public Integer getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getPockelCellSetting();
  }

  // - DetectorSettings property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettingsOffset(int, int) */
  public Float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex) {
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, false);
    return detectorRef == null ? null : detectorRef.getOffset();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettingsGain(int, int) */
  public Float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex) {
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, false);
    return detectorRef == null ? null : detectorRef.getGain();
  }

  // - LightSourceSettings property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSettingsAttenuation(int, int) */
  public Float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex) {
    LightSourceRefNode lightSourceRef = getLightSourceRefNode(imageIndex, logicalChannelIndex, false);
    return lightSourceRef == null ? null : lightSourceRef.getAttenuation();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSettingsWavelength(int, int) */
  public Integer getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex) {
    LightSourceRefNode lightSourceRef = getLightSourceRefNode(imageIndex, logicalChannelIndex, false);
    return lightSourceRef == null ? null : lightSourceRef.getWavelength();
  }

  // - ROI property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getROIX0(int, int) */
  public Integer getROIX0(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getX0();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIY0(int, int) */
  public Integer getROIY0(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getY0();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIZ0(int, int) */
  public Integer getROIZ0(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getZ0();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIT0(int, int) */
  public Integer getROIT0(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getT0();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIX1(int, int) */
  public Integer getROIX1(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getX1();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIY1(int, int) */
  public Integer getROIY1(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getY1();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIZ1(int, int) */
  public Integer getROIZ1(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getZ1();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIT1(int, int) */
  public Integer getROIT1(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getT1();
  }

  // - StageLabel property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getStageLabelName(int) */
  public String getStageLabelName(int imageIndex) {
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, false);
    return stageLabel == null ? null : stageLabel.getName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStageLabelX(int) */
  public Float getStageLabelX(int imageIndex) {
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, false);
    return stageLabel == null ? null : stageLabel.getX();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStageLabelY(int) */
  public Float getStageLabelY(int imageIndex) {
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, false);
    return stageLabel == null ? null : stageLabel.getY();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStageLabelZ(int) */
  public Float getStageLabelZ(int imageIndex) {
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, false);
    return stageLabel == null ? null : stageLabel.getZ();
  }

  // - LightSource property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceManufacturer(int, int) */
  public String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex) {
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, false);
    return lightSource == null ? null : lightSource.getManufacturer();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceModel(int, int) */
  public String getLightSourceModel(int instrumentIndex, int lightSourceIndex) {
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, false);
    return lightSource == null ? null : lightSource.getModel();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSerialNumber(int, int) */
  public String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex) {
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, false);
    return lightSource == null ? null : lightSource.getSerialNumber();
  }

  // - Laser property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getLaserType(int, int) */
  public String getLaserType(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserLaserMedium(int, int) */
  public String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getLaserMedium();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserWavelength(int, int) */
  public Integer getLaserWavelength(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getWavelength();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserFrequencyMultiplication(int, int) */
  public Integer getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getFrequencyMultiplication();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserTuneable(int, int) */
  public Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getTuneable();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserPulse(int, int) */
  public String getLaserPulse(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getPulse();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserPower(int, int) */
  public Float getLaserPower(int instrumentIndex, int lightSourceIndex) {
    // NB: Power unsupported for schema version 2007-06
    return null;
  }

  // - Filament property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getFilamentType(int, int) */
  public String getFilamentType(int instrumentIndex, int lightSourceIndex) {
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, false);
    return filament == null ? null : filament.getType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getFilamentPower(int, int) */
  public Float getFilamentPower(int instrumentIndex, int lightSourceIndex) {
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, false);
    return filament == null ? null : filament.getPower();
  }

  // - Arc property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getArcType(int, int) */
  public String getArcType(int instrumentIndex, int lightSourceIndex) {
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, false);
    return arc == null ? null : arc.getType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getArcPower(int, int) */
  public Float getArcPower(int instrumentIndex, int lightSourceIndex) {
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, false);
    return arc == null ? null : arc.getPower();
  }

  // - Detector property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorManufacturer(int, int) */
  public String getDetectorManufacturer(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getManufacturer();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorModel(int, int) */
  public String getDetectorModel(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getModel();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSerialNumber(int, int) */
  public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getSerialNumber();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorType(int, int) */
  public String getDetectorType(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorGain(int, int) */
  public Float getDetectorGain(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getGain();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorVoltage(int, int) */
  public Float getDetectorVoltage(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getVoltage();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorOffset(int, int) */
  public Float getDetectorOffset(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getOffset();
  }

  // - Objective property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveManufacturer(int, int) */
  public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getManufacturer();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveModel(int, int) */
  public String getObjectiveModel(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getModel();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveSerialNumber(int, int) */
  public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getSerialNumber();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveCorrection(int, int) */
  public String getObjectiveCorrection(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getCorrection();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveImmersion(int, int) */
  public String getObjectiveImmersion(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getImmersion();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveLensNA(int, int) */
  public Float getObjectiveLensNA(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getLensNA();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveNominalMagnification(int, int) */
  public Integer getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getNominalMagnification();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveCalibratedMagnification(int, int) */
  public Float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getCalibratedMagnification();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveWorkingDistance(int, int) */
  public Float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getWorkingDistance();
  }

  // - OTF property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getOTFSizeX(int, int) */
  public Integer getOTFSizeX(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getSizeX();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFSizeY(int, int) */
  public Integer getOTFSizeY(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getSizeY();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFPixelType(int, int) */
  public String getOTFPixelType(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getPixelType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFPath(int, int) */
  public String getOTFPath(int instrumentIndex, int otfIndex) {
    // NB: Path unsupported for schema version 2007-06
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFOpticalAxisAveraged(int, int) */
  public Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getOpticalAxisAveraged();
  }

  // - Experimenter property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterFirstName(int) */
  public String getExperimenterFirstName(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getFirstName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterLastName(int) */
  public String getExperimenterLastName(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getLastName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterEmail(int) */
  public String getExperimenterEmail(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getEmail();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterInstitution(int) */
  public String getExperimenterInstitution(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getInstitution();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterDataDirectory(int) */
  public String getExperimenterDataDirectory(int experimenterIndex) {
    // NB: DataDirectory unsupported for schema version 2007-06
    return null;
  }

  // -- MetadataStore API methods --

  /* @see loci.formats.meta.MetadataStore#setRoot(Object) */
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

  /* @see loci.formats.meta.MetadataStore#setRoot(Object) */
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

  /* @see loci.formats.meta.MetadataStore#setImageName(String, int) */
  public void setImageName(String name, int imageIndex) {
    if (name == null) return;
    ImageNode image = getImageNode(imageIndex, true);
    image.setName(name);
  }

  /* @see loci.formats.meta.MetadataStore#setImageCreationDate(String, int) */
  public void setImageCreationDate(String creationDate, int imageIndex) {
    if (creationDate == null) return;
    ImageNode image = getImageNode(imageIndex, true);
    image.setCreationDate(creationDate);
  }

  /* @see loci.formats.meta.MetadataStore#setImageDescription(String, int) */
  public void setImageDescription(String description, int imageIndex) {
    if (description == null) return;
    ImageNode image = getImageNode(imageIndex, true);
    image.setDescription(description);
  }

  // - Pixels property storage -

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeX(Integer, int, int) */
  public void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex) {
    if (sizeX == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeX(sizeX);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeY(Integer, int, int) */
  public void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex) {
    if (sizeY == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeY(sizeY);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeZ(Integer, int, int) */
  public void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex) {
    if (sizeZ == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeZ(sizeZ);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeC(Integer, int, int) */
  public void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex) {
    if (sizeC == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeC(sizeC);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeT(Integer, int, int) */
  public void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex) {
    if (sizeT == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeT(sizeT);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsPixelType(String, int, int) */
  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex) {
    if (pixelType == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setPixelType(pixelType);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsBigEndian(Boolean, int, int) */
  public void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex) {
    if (bigEndian == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setBigEndian(bigEndian);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsDimensionOrder(String, int, int) */
  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex) {
    if (dimensionOrder == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setDimensionOrder(dimensionOrder);
  }

  // - Dimensions property storage -

  /* @see loci.formats.meta.MetadataStore#setDimensionsPhysicalSizeX(Float, int, int) */
  public void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex) {
    if (physicalSizeX == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setPhysicalSizeX(physicalSizeX);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsPhysicalSizeY(Float, int, int) */
  public void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex) {
    if (physicalSizeY == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setPhysicalSizeY(physicalSizeY);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsPhysicalSizeZ(Float, int, int) */
  public void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex) {
    if (physicalSizeZ == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setPhysicalSizeZ(physicalSizeZ);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsTimeIncrement(Float, int, int) */
  public void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex) {
    if (timeIncrement == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setTimeIncrement(timeIncrement);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsWaveStart(Integer, int, int) */
  public void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex) {
    if (waveStart == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setWaveStart(waveStart);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsWaveIncrement(Integer, int, int) */
  public void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex) {
    if (waveIncrement == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setWaveIncrement(waveIncrement);
  }

  // - ImagingEnvironment property storage -

  /* @see loci.formats.meta.MetadataStore#setImagingEnvironmentTemperature(Float, int) */
  public void setImagingEnvironmentTemperature(Float temperature, int imageIndex) {
    if (temperature == null) return;
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironment.setTemperature(temperature);
  }

  /* @see loci.formats.meta.MetadataStore#setImagingEnvironmentAirPressure(Float, int) */
  public void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex) {
    if (airPressure == null) return;
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironment.setAirPressure(airPressure);
  }

  /* @see loci.formats.meta.MetadataStore#setImagingEnvironmentHumidity(Float, int) */
  public void setImagingEnvironmentHumidity(Float humidity, int imageIndex) {
    if (humidity == null) return;
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironment.setHumidity(humidity);
  }

  /* @see loci.formats.meta.MetadataStore#setImagingEnvironmentCO2Percent(Float, int) */
  public void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex) {
    if (cO2Percent == null) return;
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironment.setCO2Percent(cO2Percent);
  }

  // - Plane property storage -

  /* @see loci.formats.meta.MetadataStore#setPlaneTheZ(Integer, int, int, int) */
  public void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex) {
    if (theZ == null) return;
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, true);
    plane.setTheZ(theZ);
  }

  /* @see loci.formats.meta.MetadataStore#setPlaneTheC(Integer, int, int, int) */
  public void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex) {
    if (theC == null) return;
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, true);
    plane.setTheC(theC);
  }

  /* @see loci.formats.meta.MetadataStore#setPlaneTheT(Integer, int, int, int) */
  public void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex) {
    if (theT == null) return;
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, true);
    plane.setTheT(theT);
  }

  // - PlaneTiming property storage -

  /* @see loci.formats.meta.MetadataStore#setPlaneTimingDeltaT(Float, int, int, int) */
  public void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex) {
    if (deltaT == null) return;
    PlaneTimingNode planeTiming = getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, true);
    planeTiming.setDeltaT(deltaT);
  }

  /* @see loci.formats.meta.MetadataStore#setPlaneTimingExposureTime(Float, int, int, int) */
  public void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex) {
    if (exposureTime == null) return;
    PlaneTimingNode planeTiming = getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, true);
    planeTiming.setExposureTime(exposureTime);
  }

  // - StagePosition property storage -

  /* @see loci.formats.meta.MetadataStore#setStagePositionPositionX(Float, int, int, int) */
  public void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex) {
    if (positionX == null) return;
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, true);
    stagePosition.setPositionX(positionX);
  }

  /* @see loci.formats.meta.MetadataStore#setStagePositionPositionY(Float, int, int, int) */
  public void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex) {
    if (positionY == null) return;
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, true);
    stagePosition.setPositionY(positionY);
  }

  /* @see loci.formats.meta.MetadataStore#setStagePositionPositionZ(Float, int, int, int) */
  public void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex) {
    if (positionZ == null) return;
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, true);
    stagePosition.setPositionZ(positionZ);
  }

  // - LogicalChannel property storage -

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelName(String, int, int) */
  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex) {
    if (name == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setName(name);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelSamplesPerPixel(Integer, int, int) */
  public void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex) {
    if (samplesPerPixel == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setSamplesPerPixel(samplesPerPixel);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelIlluminationType(String, int, int) */
  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex) {
    if (illuminationType == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setIlluminationType(illuminationType);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelPinholeSize(Integer, int, int) */
  public void setLogicalChannelPinholeSize(Integer pinholeSize, int imageIndex, int logicalChannelIndex) {
    if (pinholeSize == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setPinholeSize(pinholeSize);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelPhotometricInterpretation(String, int, int) */
  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex) {
    if (photometricInterpretation == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setPhotometricInterpretation(photometricInterpretation);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelMode(String, int, int) */
  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex) {
    if (mode == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setMode(mode);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelContrastMethod(String, int, int) */
  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex) {
    if (contrastMethod == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setContrastMethod(contrastMethod);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelExWave(Integer, int, int) */
  public void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex) {
    if (exWave == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setExWave(exWave);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelEmWave(Integer, int, int) */
  public void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex) {
    if (emWave == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setEmWave(emWave);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelFluor(String, int, int) */
  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex) {
    if (fluor == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setFluor(fluor);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelNdFilter(Float, int, int) */
  public void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex) {
    if (ndFilter == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setNdFilter(ndFilter);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelPockelCellSetting(Integer, int, int) */
  public void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex) {
    if (pockelCellSetting == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setPockelCellSetting(pockelCellSetting);
  }

  // - DetectorSettings property storage -

  /* @see loci.formats.meta.MetadataStore#setDetectorSettingsOffset(Float, int, int) */
  public void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex) {
    if (offset == null) return;
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, true);
    detectorRef.setOffset(offset);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorSettingsGain(Float, int, int) */
  public void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex) {
    if (gain == null) return;
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, true);
    detectorRef.setGain(gain);
  }

  // - LightSourceSettings property storage -

  /* @see loci.formats.meta.MetadataStore#setLightSourceSettingsAttenuation(Float, int, int) */
  public void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex) {
    if (attenuation == null) return;
    LightSourceRefNode lightSourceRef = getLightSourceRefNode(imageIndex, logicalChannelIndex, true);
    lightSourceRef.setAttenuation(attenuation);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceSettingsWavelength(Integer, int, int) */
  public void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex) {
    if (wavelength == null) return;
    LightSourceRefNode lightSourceRef = getLightSourceRefNode(imageIndex, logicalChannelIndex, true);
    lightSourceRef.setWavelength(wavelength);
  }

  // - ROI property storage -

  /* @see loci.formats.meta.MetadataStore#setROIX0(Integer, int, int) */
  public void setROIX0(Integer x0, int imageIndex, int roiIndex) {
    if (x0 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setX0(x0);
  }

  /* @see loci.formats.meta.MetadataStore#setROIY0(Integer, int, int) */
  public void setROIY0(Integer y0, int imageIndex, int roiIndex) {
    if (y0 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setY0(y0);
  }

  /* @see loci.formats.meta.MetadataStore#setROIZ0(Integer, int, int) */
  public void setROIZ0(Integer z0, int imageIndex, int roiIndex) {
    if (z0 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setZ0(z0);
  }

  /* @see loci.formats.meta.MetadataStore#setROIT0(Integer, int, int) */
  public void setROIT0(Integer t0, int imageIndex, int roiIndex) {
    if (t0 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setT0(t0);
  }

  /* @see loci.formats.meta.MetadataStore#setROIX1(Integer, int, int) */
  public void setROIX1(Integer x1, int imageIndex, int roiIndex) {
    if (x1 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setX1(x1);
  }

  /* @see loci.formats.meta.MetadataStore#setROIY1(Integer, int, int) */
  public void setROIY1(Integer y1, int imageIndex, int roiIndex) {
    if (y1 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setY1(y1);
  }

  /* @see loci.formats.meta.MetadataStore#setROIZ1(Integer, int, int) */
  public void setROIZ1(Integer z1, int imageIndex, int roiIndex) {
    if (z1 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setZ1(z1);
  }

  /* @see loci.formats.meta.MetadataStore#setROIT1(Integer, int, int) */
  public void setROIT1(Integer t1, int imageIndex, int roiIndex) {
    if (t1 == null) return;
    ROINode roi = getROINode(imageIndex, roiIndex, true);
    roi.setT1(t1);
  }

  // - StageLabel property storage -

  /* @see loci.formats.meta.MetadataStore#setStageLabelName(String, int) */
  public void setStageLabelName(String name, int imageIndex) {
    if (name == null) return;
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, true);
    stageLabel.setName(name);
  }

  /* @see loci.formats.meta.MetadataStore#setStageLabelX(Float, int) */
  public void setStageLabelX(Float x, int imageIndex) {
    if (x == null) return;
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, true);
    stageLabel.setX(x);
  }

  /* @see loci.formats.meta.MetadataStore#setStageLabelY(Float, int) */
  public void setStageLabelY(Float y, int imageIndex) {
    if (y == null) return;
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, true);
    stageLabel.setY(y);
  }

  /* @see loci.formats.meta.MetadataStore#setStageLabelZ(Float, int) */
  public void setStageLabelZ(Float z, int imageIndex) {
    if (z == null) return;
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, true);
    stageLabel.setZ(z);
  }

  // - LightSource property storage -

  /* @see loci.formats.meta.MetadataStore#setLightSourceManufacturer(String, int, int) */
  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex) {
    if (manufacturer == null) return;
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSource.setManufacturer(manufacturer);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceModel(String, int, int) */
  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex) {
    if (model == null) return;
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSource.setModel(model);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceSerialNumber(String, int, int) */
  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex) {
    if (serialNumber == null) return;
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSource.setSerialNumber(serialNumber);
  }

  // - Laser property storage -

  /* @see loci.formats.meta.MetadataStore#setLaserType(String, int, int) */
  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex) {
    if (type == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setType(type);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserLaserMedium(String, int, int) */
  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex) {
    if (laserMedium == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setLaserMedium(laserMedium);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserWavelength(Integer, int, int) */
  public void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex) {
    if (wavelength == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setWavelength(wavelength);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserFrequencyMultiplication(Integer, int, int) */
  public void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex) {
    if (frequencyMultiplication == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setFrequencyMultiplication(frequencyMultiplication);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserTuneable(Boolean, int, int) */
  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex) {
    if (tuneable == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setTuneable(tuneable);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserPulse(String, int, int) */
  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex) {
    if (pulse == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setPulse(pulse);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserPower(Float, int, int) */
  public void setLaserPower(Float power, int instrumentIndex, int lightSourceIndex) {
    // NB: Power unsupported for schema version 2007-06
  }

  // - Filament property storage -

  /* @see loci.formats.meta.MetadataStore#setFilamentType(String, int, int) */
  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex) {
    if (type == null) return;
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, true);
    filament.setType(type);
  }

  /* @see loci.formats.meta.MetadataStore#setFilamentPower(Float, int, int) */
  public void setFilamentPower(Float power, int instrumentIndex, int lightSourceIndex) {
    if (power == null) return;
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, true);
    filament.setPower(power);
  }

  // - Arc property storage -

  /* @see loci.formats.meta.MetadataStore#setArcType(String, int, int) */
  public void setArcType(String type, int instrumentIndex, int lightSourceIndex) {
    if (type == null) return;
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, true);
    arc.setType(type);
  }

  /* @see loci.formats.meta.MetadataStore#setArcPower(Float, int, int) */
  public void setArcPower(Float power, int instrumentIndex, int lightSourceIndex) {
    if (power == null) return;
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, true);
    arc.setPower(power);
  }

  // - Detector property storage -

  /* @see loci.formats.meta.MetadataStore#setDetectorManufacturer(String, int, int) */
  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex) {
    if (manufacturer == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setManufacturer(manufacturer);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorModel(String, int, int) */
  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex) {
    if (model == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setModel(model);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorSerialNumber(String, int, int) */
  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex) {
    if (serialNumber == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setSerialNumber(serialNumber);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorType(String, int, int) */
  public void setDetectorType(String type, int instrumentIndex, int detectorIndex) {
    if (type == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setType(type);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorGain(Float, int, int) */
  public void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex) {
    if (gain == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setGain(gain);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorVoltage(Float, int, int) */
  public void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex) {
    if (voltage == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setVoltage(voltage);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorOffset(Float, int, int) */
  public void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex) {
    if (offset == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setOffset(offset);
  }

  // - Objective property storage -

  /* @see loci.formats.meta.MetadataStore#setObjectiveManufacturer(String, int, int) */
  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex) {
    if (manufacturer == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setManufacturer(manufacturer);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveModel(String, int, int) */
  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex) {
    if (model == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setModel(model);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveSerialNumber(String, int, int) */
  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex) {
    if (serialNumber == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setSerialNumber(serialNumber);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveCorrection(String, int, int) */
  public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex) {
    if (correction == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setCorrection(correction);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveImmersion(String, int, int) */
  public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex) {
    if (immersion == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setImmersion(immersion);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveLensNA(Float, int, int) */
  public void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex) {
    if (lensNA == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setLensNA(lensNA);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveNominalMagnification(Integer, int, int) */
  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex) {
    if (nominalMagnification == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setNominalMagnification(nominalMagnification);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveCalibratedMagnification(Float, int, int) */
  public void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex) {
    if (calibratedMagnification == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setCalibratedMagnification(calibratedMagnification);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveWorkingDistance(Float, int, int) */
  public void setObjectiveWorkingDistance(Float workingDistance, int instrumentIndex, int objectiveIndex) {
    if (workingDistance == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setWorkingDistance(workingDistance);
  }

  // - OTF property storage -

  /* @see loci.formats.meta.MetadataStore#setOTFSizeX(Integer, int, int) */
  public void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex) {
    if (sizeX == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setSizeX(sizeX);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFSizeY(Integer, int, int) */
  public void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex) {
    if (sizeY == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setSizeY(sizeY);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFPixelType(String, int, int) */
  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex) {
    if (pixelType == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setPixelType(pixelType);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFPath(String, int, int) */
  public void setOTFPath(String path, int instrumentIndex, int otfIndex) {
    // NB: Path unsupported for schema version 2007-06
  }

  /* @see loci.formats.meta.MetadataStore#setOTFOpticalAxisAveraged(Boolean, int, int) */
  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
    if (opticalAxisAveraged == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setOpticalAxisAveraged(opticalAxisAveraged);
  }

  // - Experimenter property storage -

  /* @see loci.formats.meta.MetadataStore#setExperimenterFirstName(String, int) */
  public void setExperimenterFirstName(String firstName, int experimenterIndex) {
    if (firstName == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setFirstName(firstName);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterLastName(String, int) */
  public void setExperimenterLastName(String lastName, int experimenterIndex) {
    if (lastName == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setLastName(lastName);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterEmail(String, int) */
  public void setExperimenterEmail(String email, int experimenterIndex) {
    if (email == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setEmail(email);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterInstitution(String, int) */
  public void setExperimenterInstitution(String institution, int experimenterIndex) {
    if (institution == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setInstitution(institution);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterDataDirectory(String, int) */
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
