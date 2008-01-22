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
 * Created by curtis via MetadataAutogen on Jan 22, 2008 4:26:50 PM CST
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
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/DummyMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/DummyMetadata.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class DummyMetadata implements MetadataRetrieve, MetadataStore {

  // -- MetadataRetrieve API methods --

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

  /* @see MetadataRetrieve#getDetectorSettings(int, int) */
  public Object getDetectorSettings(int imageIndex, int logicalChannelIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getLightSourceSettings(int, int) */
  public Object getLightSourceSettings(int imageIndex, int logicalChannelIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getDisplayROI(int, int) */
  public Object getDisplayROI(int imageIndex, int roiIndex) {
    return null;
  }

  /* @see MetadataRetrieve#getStageLabel(int) */
  public Object getStageLabel(int imageIndex) {
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

  // - DetectorSettings property retrieval -

  /* @see MetadataRetrieve#getDetectorSettingsOffset(int, int) */
  public Float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDetectorSettingsGain(int, int) */
  public Float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex) {
    return null;
  }

  // - LightSourceSettings property retrieval -

  /* @see MetadataRetrieve#getLightSourceSettingsAttenuation(int, int) */
  public Float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getLightSourceSettingsWavelength(int, int) */
  public Integer getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex) {
    return null;
  }

  // - DisplayROI property retrieval -

  /* @see MetadataRetrieve#getDisplayROIX0(int, int) */
  public Integer getDisplayROIX0(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDisplayROIY0(int, int) */
  public Integer getDisplayROIY0(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDisplayROIZ0(int, int) */
  public Integer getDisplayROIZ0(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDisplayROIT0(int, int) */
  public Integer getDisplayROIT0(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDisplayROIX1(int, int) */
  public Integer getDisplayROIX1(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDisplayROIY1(int, int) */
  public Integer getDisplayROIY1(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDisplayROIZ1(int, int) */
  public Integer getDisplayROIZ1(int imageIndex, int roiIndex) {
    return null;
  }
  /* @see MetadataRetrieve#getDisplayROIT1(int, int) */
  public Integer getDisplayROIT1(int imageIndex, int roiIndex) {
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

  // - LightSource property retrieval -

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

  // - OTF property retrieval -

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

  /* @see MetadataStore#setImage(String, String, String, int) */
  public void setImageName(String name, int imageIndex) {
  }

  /* @see MetadataStore#setImage(String, String, String, int) */
  public void setImageCreationDate(String creationDate, int imageIndex) {
  }

  /* @see MetadataStore#setImage(String, String, String, int) */
  public void setImageDescription(String description, int imageIndex) {
  }

  // - Pixels property storage -

  /* @see MetadataStore#setPixels(Integer, Integer, Integer, Integer, Integer, String, Boolean, String, int, int) */
  public void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixels(Integer, Integer, Integer, Integer, Integer, String, Boolean, String, int, int) */
  public void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixels(Integer, Integer, Integer, Integer, Integer, String, Boolean, String, int, int) */
  public void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixels(Integer, Integer, Integer, Integer, Integer, String, Boolean, String, int, int) */
  public void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixels(Integer, Integer, Integer, Integer, Integer, String, Boolean, String, int, int) */
  public void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixels(Integer, Integer, Integer, Integer, Integer, String, Boolean, String, int, int) */
  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixels(Integer, Integer, Integer, Integer, Integer, String, Boolean, String, int, int) */
  public void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setPixels(Integer, Integer, Integer, Integer, Integer, String, Boolean, String, int, int) */
  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex) {
  }

  // - Dimensions property storage -

  /* @see MetadataStore#setDimensions(Float, Float, Float, Float, Integer, Integer, int, int) */
  public void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setDimensions(Float, Float, Float, Float, Integer, Integer, int, int) */
  public void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setDimensions(Float, Float, Float, Float, Integer, Integer, int, int) */
  public void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setDimensions(Float, Float, Float, Float, Integer, Integer, int, int) */
  public void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setDimensions(Float, Float, Float, Float, Integer, Integer, int, int) */
  public void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex) {
  }

  /* @see MetadataStore#setDimensions(Float, Float, Float, Float, Integer, Integer, int, int) */
  public void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex) {
  }

  // - ImagingEnvironment property storage -

  /* @see MetadataStore#setImagingEnvironment(Float, Float, Float, Float, int) */
  public void setImagingEnvironmentTemperature(Float temperature, int imageIndex) {
  }

  /* @see MetadataStore#setImagingEnvironment(Float, Float, Float, Float, int) */
  public void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex) {
  }

  /* @see MetadataStore#setImagingEnvironment(Float, Float, Float, Float, int) */
  public void setImagingEnvironmentHumidity(Float humidity, int imageIndex) {
  }

  /* @see MetadataStore#setImagingEnvironment(Float, Float, Float, Float, int) */
  public void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex) {
  }

  // - Plane property storage -

  /* @see MetadataStore#setPlane(Integer, Integer, Integer, int, int, int) */
  public void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlane(Integer, Integer, Integer, int, int, int) */
  public void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlane(Integer, Integer, Integer, int, int, int) */
  public void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  // - PlaneTiming property storage -

  /* @see MetadataStore#setPlaneTiming(Float, Float, int, int, int) */
  public void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  /* @see MetadataStore#setPlaneTiming(Float, Float, int, int, int) */
  public void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  // - StagePosition property storage -

  /* @see MetadataStore#setStagePosition(Float, Float, Float, int, int, int) */
  public void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  /* @see MetadataStore#setStagePosition(Float, Float, Float, int, int, int) */
  public void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  /* @see MetadataStore#setStagePosition(Float, Float, Float, int, int, int) */
  public void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex) {
  }

  // - LogicalChannel property storage -

  /* @see MetadataStore#setLogicalChannel(String, Integer, String, Integer, String, String, String, Integer, Integer, String, Float, Integer, int, int) */
  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannel(String, Integer, String, Integer, String, String, String, Integer, Integer, String, Float, Integer, int, int) */
  public void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannel(String, Integer, String, Integer, String, String, String, Integer, Integer, String, Float, Integer, int, int) */
  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannel(String, Integer, String, Integer, String, String, String, Integer, Integer, String, Float, Integer, int, int) */
  public void setLogicalChannelPinholeSize(Integer pinholeSize, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannel(String, Integer, String, Integer, String, String, String, Integer, Integer, String, Float, Integer, int, int) */
  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannel(String, Integer, String, Integer, String, String, String, Integer, Integer, String, Float, Integer, int, int) */
  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannel(String, Integer, String, Integer, String, String, String, Integer, Integer, String, Float, Integer, int, int) */
  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannel(String, Integer, String, Integer, String, String, String, Integer, Integer, String, Float, Integer, int, int) */
  public void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannel(String, Integer, String, Integer, String, String, String, Integer, Integer, String, Float, Integer, int, int) */
  public void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannel(String, Integer, String, Integer, String, String, String, Integer, Integer, String, Float, Integer, int, int) */
  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannel(String, Integer, String, Integer, String, String, String, Integer, Integer, String, Float, Integer, int, int) */
  public void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLogicalChannel(String, Integer, String, Integer, String, String, String, Integer, Integer, String, Float, Integer, int, int) */
  public void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex) {
  }

  // - DetectorSettings property storage -

  /* @see MetadataStore#setDetectorSettings(Float, Float, int, int) */
  public void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setDetectorSettings(Float, Float, int, int) */
  public void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex) {
  }

  // - LightSourceSettings property storage -

  /* @see MetadataStore#setLightSourceSettings(Float, Integer, int, int) */
  public void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex) {
  }

  /* @see MetadataStore#setLightSourceSettings(Float, Integer, int, int) */
  public void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex) {
  }

  // - DisplayROI property storage -

  /* @see MetadataStore#setDisplayROI(Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, int, int) */
  public void setDisplayROIX0(Integer x0, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setDisplayROI(Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, int, int) */
  public void setDisplayROIY0(Integer y0, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setDisplayROI(Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, int, int) */
  public void setDisplayROIZ0(Integer z0, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setDisplayROI(Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, int, int) */
  public void setDisplayROIT0(Integer t0, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setDisplayROI(Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, int, int) */
  public void setDisplayROIX1(Integer x1, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setDisplayROI(Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, int, int) */
  public void setDisplayROIY1(Integer y1, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setDisplayROI(Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, int, int) */
  public void setDisplayROIZ1(Integer z1, int imageIndex, int roiIndex) {
  }

  /* @see MetadataStore#setDisplayROI(Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, int, int) */
  public void setDisplayROIT1(Integer t1, int imageIndex, int roiIndex) {
  }

  // - StageLabel property storage -

  /* @see MetadataStore#setStageLabel(String, Float, Float, Float, int) */
  public void setStageLabelName(String name, int imageIndex) {
  }

  /* @see MetadataStore#setStageLabel(String, Float, Float, Float, int) */
  public void setStageLabelX(Float x, int imageIndex) {
  }

  /* @see MetadataStore#setStageLabel(String, Float, Float, Float, int) */
  public void setStageLabelY(Float y, int imageIndex) {
  }

  /* @see MetadataStore#setStageLabel(String, Float, Float, Float, int) */
  public void setStageLabelZ(Float z, int imageIndex) {
  }

  // - LightSource property storage -

  /* @see MetadataStore#setLightSource(String, String, String, int, int) */
  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLightSource(String, String, String, int, int) */
  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLightSource(String, String, String, int, int) */
  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex) {
  }

  // - Laser property storage -

  /* @see MetadataStore#setLaser(String, String, Integer, Integer, Boolean, String, Float, int, int) */
  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLaser(String, String, Integer, Integer, Boolean, String, Float, int, int) */
  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLaser(String, String, Integer, Integer, Boolean, String, Float, int, int) */
  public void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLaser(String, String, Integer, Integer, Boolean, String, Float, int, int) */
  public void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLaser(String, String, Integer, Integer, Boolean, String, Float, int, int) */
  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLaser(String, String, Integer, Integer, Boolean, String, Float, int, int) */
  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setLaser(String, String, Integer, Integer, Boolean, String, Float, int, int) */
  public void setLaserPower(Float power, int instrumentIndex, int lightSourceIndex) {
  }

  // - Filament property storage -

  /* @see MetadataStore#setFilament(String, Float, int, int) */
  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setFilament(String, Float, int, int) */
  public void setFilamentPower(Float power, int instrumentIndex, int lightSourceIndex) {
  }

  // - Arc property storage -

  /* @see MetadataStore#setArc(String, Float, int, int) */
  public void setArcType(String type, int instrumentIndex, int lightSourceIndex) {
  }

  /* @see MetadataStore#setArc(String, Float, int, int) */
  public void setArcPower(Float power, int instrumentIndex, int lightSourceIndex) {
  }

  // - Detector property storage -

  /* @see MetadataStore#setDetector(String, String, String, String, Float, Float, Float, int, int) */
  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetector(String, String, String, String, Float, Float, Float, int, int) */
  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetector(String, String, String, String, Float, Float, Float, int, int) */
  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetector(String, String, String, String, Float, Float, Float, int, int) */
  public void setDetectorType(String type, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetector(String, String, String, String, Float, Float, Float, int, int) */
  public void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetector(String, String, String, String, Float, Float, Float, int, int) */
  public void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex) {
  }

  /* @see MetadataStore#setDetector(String, String, String, String, Float, Float, Float, int, int) */
  public void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex) {
  }

  // - Objective property storage -

  /* @see MetadataStore#setObjective(String, String, String, Float, Integer, Float, int, int) */
  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjective(String, String, String, Float, Integer, Float, int, int) */
  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjective(String, String, String, Float, Integer, Float, int, int) */
  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjective(String, String, String, Float, Integer, Float, int, int) */
  public void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjective(String, String, String, Float, Integer, Float, int, int) */
  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex) {
  }

  /* @see MetadataStore#setObjective(String, String, String, Float, Integer, Float, int, int) */
  public void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex) {
  }

  // - OTF property storage -

  /* @see MetadataStore#setOTF(Integer, Integer, String, String, Boolean, int, int) */
  public void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTF(Integer, Integer, String, String, Boolean, int, int) */
  public void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTF(Integer, Integer, String, String, Boolean, int, int) */
  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTF(Integer, Integer, String, String, Boolean, int, int) */
  public void setOTFPath(String path, int instrumentIndex, int otfIndex) {
  }

  /* @see MetadataStore#setOTF(Integer, Integer, String, String, Boolean, int, int) */
  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
  }

  // - Experimenter property storage -

  /* @see MetadataStore#setExperimenter(String, String, String, String, String, int) */
  public void setExperimenterFirstName(String firstName, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenter(String, String, String, String, String, int) */
  public void setExperimenterLastName(String lastName, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenter(String, String, String, String, String, int) */
  public void setExperimenterEmail(String email, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenter(String, String, String, String, String, int) */
  public void setExperimenterInstitution(String institution, int experimenterIndex) {
  }

  /* @see MetadataStore#setExperimenter(String, String, String, String, String, int) */
  public void setExperimenterDataDirectory(String dataDirectory, int experimenterIndex) {
  }

}
