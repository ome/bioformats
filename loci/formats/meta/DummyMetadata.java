//
// DummyMetadata.java
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
 * Created by curtis via MetadataAutogen on Mar 20, 2008 12:34:36 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

/**
 * A dummy implementation for {@link MetadataStore} and
 * {@link MetadataRetrieve} that is used when no other
 * metadata implementations are available.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/meta/DummyMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/meta/DummyMetadata.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class DummyMetadata implements MetadataRetrieve, MetadataStore {

  // -- MetadataRetrieve API methods --

  // - Entity counting -

  /* @see MetadataRetrieve#getImageCount() */
  public int getImageCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getPixelsCount(int) */
  public int getPixelsCount(int imageIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getPlaneCount(int, int) */
  public int getPlaneCount(int imageIndex, int pixelsIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getLogicalChannelCount(int) */
  public int getLogicalChannelCount(int imageIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getChannelComponentCount(int, int) */
  public int getChannelComponentCount(int imageIndex, int logicalChannelIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getROICount(int) */
  public int getROICount(int imageIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getInstrumentCount() */
  public int getInstrumentCount() {
    return -1;
  }

  /* @see MetadataRetrieve#getLightSourceCount(int) */
  public int getLightSourceCount(int instrumentIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getDetectorCount(int) */
  public int getDetectorCount(int instrumentIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getObjectiveCount(int) */
  public int getObjectiveCount(int instrumentIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getOTFCount(int) */
  public int getOTFCount(int instrumentIndex) {
    return -1;
  }

  /* @see MetadataRetrieve#getExperimenterCount() */
  public int getExperimenterCount() {
    return -1;
  }

  // - Entity retrieval -

  /* @see MetadataRetrieve#getImage(int) */
  public Object getImage(int imageIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getPixels(int, int) */
  public Object getPixels(int imageIndex, int pixelsIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getDimensions(int, int) */
  public Object getDimensions(int imageIndex, int pixelsIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getImagingEnvironment(int) */
  public Object getImagingEnvironment(int imageIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getPlane(int, int, int) */
  public Object getPlane(int imageIndex, int pixelsIndex, int planeIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getPlaneTiming(int, int, int) */
  public Object getPlaneTiming(int imageIndex, int pixelsIndex, int planeIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getStagePosition(int, int, int) */
  public Object getStagePosition(int imageIndex, int pixelsIndex, int planeIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannel(int, int) */
  public Object getLogicalChannel(int imageIndex, int logicalChannelIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getChannelComponent(int, int, int) */
  public Object getChannelComponent(int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getDisplayOptions(int) */
  public Object getDisplayOptions(int imageIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getDisplayOptionsProjection(int) */
  public Object getDisplayOptionsProjection(int imageIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getDisplayOptionsTime(int) */
  public Object getDisplayOptionsTime(int imageIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getROI(int, int) */
  public Object getROI(int imageIndex, int roiIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSettings(int, int) */
  public Object getDetectorSettings(int imageIndex, int logicalChannelIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getLightSourceSettings(int, int) */
  public Object getLightSourceSettings(int imageIndex, int logicalChannelIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getStageLabel(int) */
  public Object getStageLabel(int imageIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getInstrument(int) */
  public Object getInstrument(int instrumentIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getLightSource(int, int) */
  public Object getLightSource(int instrumentIndex, int lightSourceIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getLaser(int, int) */
  public Object getLaser(int instrumentIndex, int lightSourceIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getFilament(int, int) */
  public Object getFilament(int instrumentIndex, int lightSourceIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getArc(int, int) */
  public Object getArc(int instrumentIndex, int lightSourceIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getDetector(int, int) */
  public Object getDetector(int instrumentIndex, int detectorIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getObjective(int, int) */
  public Object getObjective(int instrumentIndex, int objectiveIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getOTF(int, int) */
  public Object getOTF(int instrumentIndex, int otfIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getExperimenter(int) */
  public Object getExperimenter(int experimenterIndex) {
    return null;
  }

  // - Image property retrieval -

  /* @see MetadataRetrieve#getImageNodeID(int) */
  public String getImageNodeID(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageName(int) */
  public String getImageName(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageCreationDate(int) */
  public String getImageCreationDate(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImageDescription(int) */
  public String getImageDescription(int imageIndex) {
    return null;
  }

  // - Pixels property retrieval -

  /* @see MetadataRetrieve#getPixelsNodeID(int, int) */
  public String getPixelsNodeID(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsSizeX(int, int) */
  public Integer getPixelsSizeX(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsSizeY(int, int) */
  public Integer getPixelsSizeY(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsSizeZ(int, int) */
  public Integer getPixelsSizeZ(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsSizeC(int, int) */
  public Integer getPixelsSizeC(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsSizeT(int, int) */
  public Integer getPixelsSizeT(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsPixelType(int, int) */
  public String getPixelsPixelType(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsBigEndian(int, int) */
  public Boolean getPixelsBigEndian(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPixelsDimensionOrder(int, int) */
  public String getPixelsDimensionOrder(int imageIndex, int pixelsIndex) {
    return null;
  }

  // - Dimensions property retrieval -

  /* @see MetadataRetrieve#getDimensionsPhysicalSizeX(int, int) */
  public Float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDimensionsPhysicalSizeY(int, int) */
  public Float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDimensionsPhysicalSizeZ(int, int) */
  public Float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDimensionsTimeIncrement(int, int) */
  public Float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDimensionsWaveStart(int, int) */
  public Integer getDimensionsWaveStart(int imageIndex, int pixelsIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDimensionsWaveIncrement(int, int) */
  public Integer getDimensionsWaveIncrement(int imageIndex, int pixelsIndex) {
    return null;
  }

  // - ImagingEnvironment property retrieval -

  /* @see MetadataRetrieve#getImagingEnvironmentTemperature(int) */
  public Float getImagingEnvironmentTemperature(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImagingEnvironmentAirPressure(int) */
  public Float getImagingEnvironmentAirPressure(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImagingEnvironmentHumidity(int) */
  public Float getImagingEnvironmentHumidity(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getImagingEnvironmentCO2Percent(int) */
  public Float getImagingEnvironmentCO2Percent(int imageIndex) {
    return null;
  }

  // - Plane property retrieval -

  /* @see MetadataRetrieve#getPlaneTheZ(int, int, int) */
  public Integer getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlaneTheC(int, int, int) */
  public Integer getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlaneTheT(int, int, int) */
  public Integer getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex) {
    return null;
  }

  // - PlaneTiming property retrieval -

  /* @see MetadataRetrieve#getPlaneTimingDeltaT(int, int, int) */
  public Float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getPlaneTimingExposureTime(int, int, int) */
  public Float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex) {
    return null;
  }

  // - StagePosition property retrieval -

  /* @see MetadataRetrieve#getStagePositionPositionX(int, int, int) */
  public Float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getStagePositionPositionY(int, int, int) */
  public Float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getStagePositionPositionZ(int, int, int) */
  public Float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex) {
    return null;
  }

  // - LogicalChannel property retrieval -

  /* @see MetadataRetrieve#getLogicalChannelNodeID(int, int) */
  public String getLogicalChannelNodeID(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLogicalChannelName(int, int) */
  public String getLogicalChannelName(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLogicalChannelSamplesPerPixel(int, int) */
  public Integer getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLogicalChannelIlluminationType(int, int) */
  public String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLogicalChannelPinholeSize(int, int) */
  public Integer getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLogicalChannelPhotometricInterpretation(int, int) */
  public String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLogicalChannelMode(int, int) */
  public String getLogicalChannelMode(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLogicalChannelContrastMethod(int, int) */
  public String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLogicalChannelExWave(int, int) */
  public Integer getLogicalChannelExWave(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLogicalChannelEmWave(int, int) */
  public Integer getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLogicalChannelFluor(int, int) */
  public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLogicalChannelNdFilter(int, int) */
  public Float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLogicalChannelPockelCellSetting(int, int) */
  public Integer getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex) {
    return null;
  }

  // - ChannelComponent property retrieval -

  /* @see MetadataRetrieve#getChannelComponentIndex(int, int, int) */
  public Integer getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getChannelComponentColorDomain(int, int, int) */
  public String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    return null;
  }

  // - DisplayOptions property retrieval -

  /* @see MetadataRetrieve#getDisplayOptionsNodeID(int) */
  public String getDisplayOptionsNodeID(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDisplayOptionsZoom(int) */
  public Float getDisplayOptionsZoom(int imageIndex) {
    return null;
  }

  // - DisplayOptionsProjection property retrieval -

  /* @see MetadataRetrieve#getDisplayOptionsProjectionZStart(int) */
  public Integer getDisplayOptionsProjectionZStart(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDisplayOptionsProjectionZStop(int) */
  public Integer getDisplayOptionsProjectionZStop(int imageIndex) {
    return null;
  }

  // - DisplayOptionsTime property retrieval -

  /* @see MetadataRetrieve#getDisplayOptionsTimeTStart(int) */
  public Integer getDisplayOptionsTimeTStart(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDisplayOptionsTimeTStop(int) */
  public Integer getDisplayOptionsTimeTStop(int imageIndex) {
    return null;
  }

  // - ROI property retrieval -

  /* @see MetadataRetrieve#getROINodeID(int, int) */
  public String getROINodeID(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getROIX0(int, int) */
  public Integer getROIX0(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getROIY0(int, int) */
  public Integer getROIY0(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getROIX1(int, int) */
  public Integer getROIX1(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getROIY1(int, int) */
  public Integer getROIY1(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getROIZ0(int, int) */
  public Integer getROIZ0(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getROIZ1(int, int) */
  public Integer getROIZ1(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getROIT0(int, int) */
  public Integer getROIT0(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getROIT1(int, int) */
  public Integer getROIT1(int imageIndex, int roiIndex) {
    return null;
  }

  // - DetectorSettings property retrieval -

  /* @see MetadataRetrieve#getDetectorSettingsDetector(int, int) */
  public Object getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorSettingsOffset(int, int) */
  public Float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorSettingsGain(int, int) */
  public Float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex) {
    return null;
  }

  // - LightSourceSettings property retrieval -

  /* @see MetadataRetrieve#getLightSourceSettingsLightSource(int, int) */
  public Object getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightSourceSettingsAttenuation(int, int) */
  public Float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightSourceSettingsWavelength(int, int) */
  public Integer getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex) {
    return null;
  }

  // - StageLabel property retrieval -

  /* @see MetadataRetrieve#getStageLabelName(int) */
  public String getStageLabelName(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getStageLabelX(int) */
  public Float getStageLabelX(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getStageLabelY(int) */
  public Float getStageLabelY(int imageIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getStageLabelZ(int) */
  public Float getStageLabelZ(int imageIndex) {
    return null;
  }

  // - Instrument property retrieval -

  /* @see MetadataRetrieve#getInstrumentNodeID(int) */
  public String getInstrumentNodeID(int instrumentIndex) {
    return null;
  }

  // - LightSource property retrieval -

  /* @see MetadataRetrieve#getLightSourceNodeID(int, int) */
  public String getLightSourceNodeID(int instrumentIndex, int lightSourceIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightSourceManufacturer(int, int) */
  public String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightSourceModel(int, int) */
  public String getLightSourceModel(int instrumentIndex, int lightSourceIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightSourceSerialNumber(int, int) */
  public String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex) {
    return null;
  }

  // - Laser property retrieval -

  /* @see MetadataRetrieve#getLaserType(int, int) */
  public String getLaserType(int instrumentIndex, int lightSourceIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserLaserMedium(int, int) */
  public String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserWavelength(int, int) */
  public Integer getLaserWavelength(int instrumentIndex, int lightSourceIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserFrequencyMultiplication(int, int) */
  public Integer getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserTuneable(int, int) */
  public Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserPulse(int, int) */
  public String getLaserPulse(int instrumentIndex, int lightSourceIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLaserPower(int, int) */
  public Float getLaserPower(int instrumentIndex, int lightSourceIndex) {
    return null;
  }

  // - Filament property retrieval -

  /* @see MetadataRetrieve#getFilamentType(int, int) */
  public String getFilamentType(int instrumentIndex, int lightSourceIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getFilamentPower(int, int) */
  public Float getFilamentPower(int instrumentIndex, int lightSourceIndex) {
    return null;
  }

  // - Arc property retrieval -

  /* @see MetadataRetrieve#getArcType(int, int) */
  public String getArcType(int instrumentIndex, int lightSourceIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getArcPower(int, int) */
  public Float getArcPower(int instrumentIndex, int lightSourceIndex) {
    return null;
  }

  // - Detector property retrieval -

  /* @see MetadataRetrieve#getDetectorNodeID(int, int) */
  public String getDetectorNodeID(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorManufacturer(int, int) */
  public String getDetectorManufacturer(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorModel(int, int) */
  public String getDetectorModel(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorSerialNumber(int, int) */
  public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorType(int, int) */
  public String getDetectorType(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorGain(int, int) */
  public Float getDetectorGain(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorVoltage(int, int) */
  public Float getDetectorVoltage(int instrumentIndex, int detectorIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorOffset(int, int) */
  public Float getDetectorOffset(int instrumentIndex, int detectorIndex) {
    return null;
  }

  // - Objective property retrieval -

  /* @see MetadataRetrieve#getObjectiveNodeID(int, int) */
  public String getObjectiveNodeID(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveManufacturer(int, int) */
  public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveModel(int, int) */
  public String getObjectiveModel(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveSerialNumber(int, int) */
  public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveCorrection(int, int) */
  public String getObjectiveCorrection(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveImmersion(int, int) */
  public String getObjectiveImmersion(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveLensNA(int, int) */
  public Float getObjectiveLensNA(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveNominalMagnification(int, int) */
  public Integer getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveCalibratedMagnification(int, int) */
  public Float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getObjectiveWorkingDistance(int, int) */
  public Float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex) {
    return null;
  }

  // - OTF property retrieval -

  /* @see MetadataRetrieve#getOTFNodeID(int, int) */
  public String getOTFNodeID(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFSizeX(int, int) */
  public Integer getOTFSizeX(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFSizeY(int, int) */
  public Integer getOTFSizeY(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFPixelType(int, int) */
  public String getOTFPixelType(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFPath(int, int) */
  public String getOTFPath(int instrumentIndex, int otfIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getOTFOpticalAxisAveraged(int, int) */
  public Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex) {
    return null;
  }

  // - Experimenter property retrieval -

  /* @see MetadataRetrieve#getExperimenterNodeID(int) */
  public String getExperimenterNodeID(int experimenterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterFirstName(int) */
  public String getExperimenterFirstName(int experimenterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterLastName(int) */
  public String getExperimenterLastName(int experimenterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterEmail(int) */
  public String getExperimenterEmail(int experimenterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterInstitution(int) */
  public String getExperimenterInstitution(int experimenterIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getExperimenterDataDirectory(int) */
  public String getExperimenterDataDirectory(int experimenterIndex) {
    return null;
  }

  // -- MetadataStore API methods --

  public void createRoot() {
  }

  public Object getRoot() {
    return null;
  }

  public void setRoot(Object root) {
  }


  // - Image property storage -

  /* @see MetadataStore#setImageNodeID(String, int) */
  public void setImageNodeID(String nodeID, int imageIndex) {
  }

  /* @see MetadataStore#setImageName(String, int) */
  public void setImageName(String name, int imageIndex) {
  }

  /* @see MetadataStore#setImageCreationDate(String, int) */
  public void setImageCreationDate(String creationDate, int imageIndex) {
  }

  /* @see MetadataStore#setImageDescription(String, int) */
  public void setImageDescription(String description, int imageIndex) {
  }

  // - Pixels property storage -

  /* @see MetadataStore#setPixelsNodeID(String, int, int) */
  public void setPixelsNodeID(String nodeID, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixelsSizeX(Integer, int, int) */
  public void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixelsSizeY(Integer, int, int) */
  public void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixelsSizeZ(Integer, int, int) */
  public void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixelsSizeC(Integer, int, int) */
  public void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixelsSizeT(Integer, int, int) */
  public void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixelsPixelType(String, int, int) */
  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixelsBigEndian(Boolean, int, int) */
  public void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixelsDimensionOrder(String, int, int) */
  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex) {
  }

  // - Dimensions property storage -

  /* @see MetadataStore#setDimensionsPhysicalSizeX(Float, int, int) */
  public void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setDimensionsPhysicalSizeY(Float, int, int) */
  public void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setDimensionsPhysicalSizeZ(Float, int, int) */
  public void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setDimensionsTimeIncrement(Float, int, int) */
  public void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setDimensionsWaveStart(Integer, int, int) */
  public void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setDimensionsWaveIncrement(Integer, int, int) */
  public void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex) {
  }

  // - ImagingEnvironment property storage -

  /* @see MetadataStore#setImagingEnvironmentTemperature(Float, int) */
  public void setImagingEnvironmentTemperature(Float temperature, int imageIndex) {
  }

  /* @see MetadataStore#setImagingEnvironmentAirPressure(Float, int) */
  public void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex) {
  }

  /* @see MetadataStore#setImagingEnvironmentHumidity(Float, int) */
  public void setImagingEnvironmentHumidity(Float humidity, int imageIndex) {
  }

  /* @see MetadataStore#setImagingEnvironmentCO2Percent(Float, int) */
  public void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex) {
  }

  // - Plane property storage -

  /* @see MetadataStore#setPlaneTheZ(Integer, int, int, int) */
  public void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlaneTheC(Integer, int, int, int) */
  public void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlaneTheT(Integer, int, int, int) */
  public void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  // - PlaneTiming property storage -

  /* @see MetadataStore#setPlaneTimingDeltaT(Float, int, int, int) */
  public void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlaneTimingExposureTime(Float, int, int, int) */
  public void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  // - StagePosition property storage -

  /* @see MetadataStore#setStagePositionPositionX(Float, int, int, int) */
  public void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  /* @see MetadataStore#setStagePositionPositionY(Float, int, int, int) */
  public void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  /* @see MetadataStore#setStagePositionPositionZ(Float, int, int, int) */
  public void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  // - LogicalChannel property storage -

  /* @see MetadataStore#setLogicalChannelNodeID(String, int, int) */
  public void setLogicalChannelNodeID(String nodeID, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannelName(String, int, int) */
  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannelSamplesPerPixel(Integer, int, int) */
  public void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannelIlluminationType(String, int, int) */
  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannelPinholeSize(Integer, int, int) */
  public void setLogicalChannelPinholeSize(Integer pinholeSize, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannelPhotometricInterpretation(String, int, int) */
  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannelMode(String, int, int) */
  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannelContrastMethod(String, int, int) */
  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannelExWave(Integer, int, int) */
  public void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannelEmWave(Integer, int, int) */
  public void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannelFluor(String, int, int) */
  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannelNdFilter(Float, int, int) */
  public void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannelPockelCellSetting(Integer, int, int) */
  public void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex) {
  }

  // - ChannelComponent property storage -

  /* @see MetadataStore#setChannelComponentIndex(Integer, int, int, int) */
  public void setChannelComponentIndex(Integer index, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
  }

  /* @see MetadataStore#setChannelComponentColorDomain(String, int, int, int) */
  public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
  }

  // - DisplayOptions property storage -

  /* @see MetadataStore#setDisplayOptionsNodeID(String, int) */
  public void setDisplayOptionsNodeID(String nodeID, int imageIndex) {
  }

  /* @see MetadataStore#setDisplayOptionsZoom(Float, int) */
  public void setDisplayOptionsZoom(Float zoom, int imageIndex) {
  }

  // - DisplayOptionsProjection property storage -

  /* @see MetadataStore#setDisplayOptionsProjectionZStart(Integer, int) */
  public void setDisplayOptionsProjectionZStart(Integer zStart, int imageIndex) {
  }

  /* @see MetadataStore#setDisplayOptionsProjectionZStop(Integer, int) */
  public void setDisplayOptionsProjectionZStop(Integer zStop, int imageIndex) {
  }

  // - DisplayOptionsTime property storage -

  /* @see MetadataStore#setDisplayOptionsTimeTStart(Integer, int) */
  public void setDisplayOptionsTimeTStart(Integer tStart, int imageIndex) {
  }

  /* @see MetadataStore#setDisplayOptionsTimeTStop(Integer, int) */
  public void setDisplayOptionsTimeTStop(Integer tStop, int imageIndex) {
  }

  // - ROI property storage -

  /* @see MetadataStore#setROINodeID(String, int, int) */
  public void setROINodeID(String nodeID, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setROIX0(Integer, int, int) */
  public void setROIX0(Integer x0, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setROIY0(Integer, int, int) */
  public void setROIY0(Integer y0, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setROIX1(Integer, int, int) */
  public void setROIX1(Integer x1, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setROIY1(Integer, int, int) */
  public void setROIY1(Integer y1, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setROIZ0(Integer, int, int) */
  public void setROIZ0(Integer z0, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setROIZ1(Integer, int, int) */
  public void setROIZ1(Integer z1, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setROIT0(Integer, int, int) */
  public void setROIT0(Integer t0, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setROIT1(Integer, int, int) */
  public void setROIT1(Integer t1, int imageIndex, int roiIndex) {
  }

  // - DetectorSettings property storage -

  /* @see MetadataStore#setDetectorSettingsDetector(Object, int, int) */
  public void setDetectorSettingsDetector(Object detector, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setDetectorSettingsOffset(Float, int, int) */
  public void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setDetectorSettingsGain(Float, int, int) */
  public void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex) {
  }

  // - LightSourceSettings property storage -

  /* @see MetadataStore#setLightSourceSettingsLightSource(Object, int, int) */
  public void setLightSourceSettingsLightSource(Object lightSource, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLightSourceSettingsAttenuation(Float, int, int) */
  public void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLightSourceSettingsWavelength(Integer, int, int) */
  public void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex) {
  }

  // - StageLabel property storage -

  /* @see MetadataStore#setStageLabelName(String, int) */
  public void setStageLabelName(String name, int imageIndex) {
  }

  /* @see MetadataStore#setStageLabelX(Float, int) */
  public void setStageLabelX(Float x, int imageIndex) {
  }

  /* @see MetadataStore#setStageLabelY(Float, int) */
  public void setStageLabelY(Float y, int imageIndex) {
  }

  /* @see MetadataStore#setStageLabelZ(Float, int) */
  public void setStageLabelZ(Float z, int imageIndex) {
  }

  // - Instrument property storage -

  /* @see MetadataStore#setInstrumentNodeID(String, int) */
  public void setInstrumentNodeID(String nodeID, int instrumentIndex) {
  }

  // - LightSource property storage -

  /* @see MetadataStore#setLightSourceNodeID(String, int, int) */
  public void setLightSourceNodeID(String nodeID, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLightSourceManufacturer(String, int, int) */
  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLightSourceModel(String, int, int) */
  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLightSourceSerialNumber(String, int, int) */
  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex) {
  }

  // - Laser property storage -

  /* @see MetadataStore#setLaserType(String, int, int) */
  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLaserLaserMedium(String, int, int) */
  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLaserWavelength(Integer, int, int) */
  public void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLaserFrequencyMultiplication(Integer, int, int) */
  public void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLaserTuneable(Boolean, int, int) */
  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLaserPulse(String, int, int) */
  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLaserPower(Float, int, int) */
  public void setLaserPower(Float power, int instrumentIndex, int lightSourceIndex) {
  }

  // - Filament property storage -

  /* @see MetadataStore#setFilamentType(String, int, int) */
  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setFilamentPower(Float, int, int) */
  public void setFilamentPower(Float power, int instrumentIndex, int lightSourceIndex) {
  }

  // - Arc property storage -

  /* @see MetadataStore#setArcType(String, int, int) */
  public void setArcType(String type, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setArcPower(Float, int, int) */
  public void setArcPower(Float power, int instrumentIndex, int lightSourceIndex) {
  }

  // - Detector property storage -

  /* @see MetadataStore#setDetectorNodeID(String, int, int) */
  public void setDetectorNodeID(String nodeID, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorManufacturer(String, int, int) */
  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorModel(String, int, int) */
  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorSerialNumber(String, int, int) */
  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorType(String, int, int) */
  public void setDetectorType(String type, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorGain(Float, int, int) */
  public void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorVoltage(Float, int, int) */
  public void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetectorOffset(Float, int, int) */
  public void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex) {
  }

  // - Objective property storage -

  /* @see MetadataStore#setObjectiveNodeID(String, int, int) */
  public void setObjectiveNodeID(String nodeID, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveManufacturer(String, int, int) */
  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveModel(String, int, int) */
  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveSerialNumber(String, int, int) */
  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveCorrection(String, int, int) */
  public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveImmersion(String, int, int) */
  public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveLensNA(Float, int, int) */
  public void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveNominalMagnification(Integer, int, int) */
  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveCalibratedMagnification(Float, int, int) */
  public void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjectiveWorkingDistance(Float, int, int) */
  public void setObjectiveWorkingDistance(Float workingDistance, int instrumentIndex, int objectiveIndex) {
  }

  // - OTF property storage -

  /* @see MetadataStore#setOTFNodeID(String, int, int) */
  public void setOTFNodeID(String nodeID, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFSizeX(Integer, int, int) */
  public void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFSizeY(Integer, int, int) */
  public void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFPixelType(String, int, int) */
  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFPath(String, int, int) */
  public void setOTFPath(String path, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTFOpticalAxisAveraged(Boolean, int, int) */
  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
  }

  // - Experimenter property storage -

  /* @see MetadataStore#setExperimenterNodeID(String, int) */
  public void setExperimenterNodeID(String nodeID, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenterFirstName(String, int) */
  public void setExperimenterFirstName(String firstName, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenterLastName(String, int) */
  public void setExperimenterLastName(String lastName, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenterEmail(String, int) */
  public void setExperimenterEmail(String email, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenterInstitution(String, int) */
  public void setExperimenterInstitution(String institution, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenterDataDirectory(String, int) */
  public void setExperimenterDataDirectory(String dataDirectory, int experimenterIndex) {
  }

}
