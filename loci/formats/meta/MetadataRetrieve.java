//
// MetadataRetrieve.java
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
 * Created by curtis via MetadataAutogen on Jan 30, 2008 1:38:52 PM CST
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

/**
 * TODO - MetadataRetrieve javadoc.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/meta/MetadataRetrieve.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/meta/MetadataRetrieve.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public interface MetadataRetrieve {

  // - Entity counting -

  int getImageCount();

  int getPixelsCount(int imageIndex);

  int getPlaneCount(int imageIndex, int pixelsIndex);

  int getLogicalChannelCount(int imageIndex);

  int getROICount(int imageIndex);

  int getInstrumentCount();

  int getLightSourceCount(int instrumentIndex);

  int getDetectorCount(int instrumentIndex);

  int getObjectiveCount(int instrumentIndex);

  int getOTFCount(int instrumentIndex);

  int getExperimenterCount();

  // - Entity retrieval -

  /**
   * Gets an object representing a particular Image, for use with certain methods of {@link MetadataStore}.
   */
  Object getImage(int imageIndex);

  /**
   * Gets an object representing a particular Pixels, for use with certain methods of {@link MetadataStore}.
   */
  Object getPixels(int imageIndex, int pixelsIndex);

  /**
   * Gets an object representing a particular Dimensions, for use with certain methods of {@link MetadataStore}.
   */
  Object getDimensions(int imageIndex, int pixelsIndex);

  /**
   * Gets an object representing a particular ImagingEnvironment, for use with certain methods of {@link MetadataStore}.
   */
  Object getImagingEnvironment(int imageIndex);

  /**
   * Gets an object representing a particular Plane, for use with certain methods of {@link MetadataStore}.
   */
  Object getPlane(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Gets an object representing a particular PlaneTiming, for use with certain methods of {@link MetadataStore}.
   */
  Object getPlaneTiming(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Gets an object representing a particular StagePosition, for use with certain methods of {@link MetadataStore}.
   */
  Object getStagePosition(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Gets an object representing a particular LogicalChannel, for use with certain methods of {@link MetadataStore}.
   */
  Object getLogicalChannel(int imageIndex, int logicalChannelIndex);

  /**
   * Gets an object representing a particular DetectorSettings, for use with certain methods of {@link MetadataStore}.
   */
  Object getDetectorSettings(int imageIndex, int logicalChannelIndex);

  /**
   * Gets an object representing a particular LightSourceSettings, for use with certain methods of {@link MetadataStore}.
   */
  Object getLightSourceSettings(int imageIndex, int logicalChannelIndex);

  /**
   * Gets an object representing a particular ROI, for use with certain methods of {@link MetadataStore}.
   */
  Object getROI(int imageIndex, int roiIndex);

  /**
   * Gets an object representing a particular StageLabel, for use with certain methods of {@link MetadataStore}.
   */
  Object getStageLabel(int imageIndex);

  /**
   * Gets an object representing a particular Instrument, for use with certain methods of {@link MetadataStore}.
   */
  Object getInstrument(int instrumentIndex);

  /**
   * Gets an object representing a particular LightSource, for use with certain methods of {@link MetadataStore}.
   */
  Object getLightSource(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets an object representing a particular Laser, for use with certain methods of {@link MetadataStore}.
   */
  Object getLaser(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets an object representing a particular Filament, for use with certain methods of {@link MetadataStore}.
   */
  Object getFilament(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets an object representing a particular Arc, for use with certain methods of {@link MetadataStore}.
   */
  Object getArc(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets an object representing a particular Detector, for use with certain methods of {@link MetadataStore}.
   */
  Object getDetector(int instrumentIndex, int detectorIndex);

  /**
   * Gets an object representing a particular Objective, for use with certain methods of {@link MetadataStore}.
   */
  Object getObjective(int instrumentIndex, int objectiveIndex);

  /**
   * Gets an object representing a particular OTF, for use with certain methods of {@link MetadataStore}.
   */
  Object getOTF(int instrumentIndex, int otfIndex);

  /**
   * Gets an object representing a particular Experimenter, for use with certain methods of {@link MetadataStore}.
   */
  Object getExperimenter(int experimenterIndex);

  // - Image property retrieval -

  /**
   * Gets the full name of the image for a particular Image object.
   * @param imageIndex index of the Image
   */
  String getImageName(int imageIndex);

  /**
   * Gets the creation date of the image for a particular Image object.
   * @param imageIndex index of the Image
   */
  String getImageCreationDate(int imageIndex);

  /**
   * Gets the full description of the image for a particular Image object.
   * @param imageIndex index of the Image
   */
  String getImageDescription(int imageIndex);

  // - Pixels property retrieval -

  /**
   * Gets size of an individual plane or section's X axis (width) for a particular Pixels object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeX(int imageIndex, int pixelsIndex);

  /**
   * Gets size of an individual plane of section's Y axis (height) for a particular Pixels object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeY(int imageIndex, int pixelsIndex);

  /**
   * Gets number of optical sections per stack for a particular Pixels object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeZ(int imageIndex, int pixelsIndex);

  /**
   * Gets number of channels per timepoint for a particular Pixels object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeC(int imageIndex, int pixelsIndex);

  /**
   * Gets number of timepoints for a particular Pixels object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeT(int imageIndex, int pixelsIndex);

  /**
   * Gets the pixel type for a particular Pixels object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  String getPixelsPixelType(int imageIndex, int pixelsIndex);

  /**
   * Gets if the pixels set is big endian or not for a particular Pixels object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Boolean getPixelsBigEndian(int imageIndex, int pixelsIndex);

  /**
   * Gets the dimension order of the pixels set for a particular Pixels object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  String getPixelsDimensionOrder(int imageIndex, int pixelsIndex);

  // - Dimensions property retrieval -

  /**
   * Gets size of an individual pixel's X axis in microns for a particular Dimensions object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex);

  /**
   * Gets size of an individual pixel's Y axis in microns for a particular Dimensions object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex);

  /**
   * Gets size of an individual pixel's Z axis in microns for a particular Dimensions object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex);

  /**
   * Gets distance between adjacent time points in seconds for a particular Dimensions object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex);

  /**
   * Gets starting wavelength in nanometers for a particular Dimensions object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getDimensionsWaveStart(int imageIndex, int pixelsIndex);

  /**
   * Gets distance between adjacent wavelengths in nanometers for a particular Dimensions object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getDimensionsWaveIncrement(int imageIndex, int pixelsIndex);

  // - ImagingEnvironment property retrieval -

  /**
   * Gets CTR TODO for a particular ImagingEnvironment object.
   * @param imageIndex index of the Image
   */
  Float getImagingEnvironmentTemperature(int imageIndex);

  /**
   * Gets CTR TODO for a particular ImagingEnvironment object.
   * @param imageIndex index of the Image
   */
  Float getImagingEnvironmentAirPressure(int imageIndex);

  /**
   * Gets CTR TODO for a particular ImagingEnvironment object.
   * @param imageIndex index of the Image
   */
  Float getImagingEnvironmentHumidity(int imageIndex);

  /**
   * Gets CTR TODO for a particular ImagingEnvironment object.
   * @param imageIndex index of the Image
   */
  Float getImagingEnvironmentCO2Percent(int imageIndex);

  // - Plane property retrieval -

  /**
   * Gets the optical section index for a particular Plane object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Integer getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Gets the channel index for a particular Plane object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Integer getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Gets the timepoint for a particular Plane object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Integer getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex);

  // - PlaneTiming property retrieval -

  /**
   * Gets the time in seconds since the beginning of the experiment for a particular PlaneTiming object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Gets the exposure time in seconds for a particular PlaneTiming object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex);

  // - StagePosition property retrieval -

  /**
   * Gets the X coordinate of the stage position for a particular StagePosition object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Gets the Y coordinate of the stage position for a particular StagePosition object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Gets the Z coordinate of the stage position for a particular StagePosition object.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex);

  // - LogicalChannel property retrieval -

  /**
   * Gets the logical channel's name for a particular LogicalChannel object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelName(int imageIndex, int logicalChannelIndex);

  /**
   * Gets CTR TODO for a particular LogicalChannel object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex);

  /**
   * Gets the illumination type for a particular LogicalChannel object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex);

  /**
   * Gets the size of the pinhole for a particular LogicalChannel object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex);

  /**
   * Gets the photometric interpretation type for a particular LogicalChannel object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex);

  /**
   * Gets the acquisition mode for a particular LogicalChannel object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelMode(int imageIndex, int logicalChannelIndex);

  /**
   * Gets the constrast method name for a particular LogicalChannel object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex);

  /**
   * Gets the excitation wavelength for a particular LogicalChannel object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelExWave(int imageIndex, int logicalChannelIndex);

  /**
   * Gets the emission wavelength for a particular LogicalChannel object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex);

  /**
   * Gets the fluorescence type for a particular LogicalChannel object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex);

  /**
   * Gets the neutral-density filter value for a particular LogicalChannel object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex);

  /**
   * Gets CTR TODO for a particular LogicalChannel object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex);

  // - DetectorSettings property retrieval -

  /**
   * Gets the detector offset for a particular DetectorSettings object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex);

  /**
   * Gets the detector gain for a particular DetectorSettings object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex);

  // - LightSourceSettings property retrieval -

  /**
   * Gets the primary light source attenuation for a particular LightSourceSettings object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex);

  /**
   * Gets the primary light source wavelength for a particular LightSourceSettings object.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex);

  // - ROI property retrieval -

  /**
   * Gets the starting X coordinate for a particular ROI object.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIX0(int imageIndex, int roiIndex);

  /**
   * Gets the starting Y coordinate for a particular ROI object.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIY0(int imageIndex, int roiIndex);

  /**
   * Gets the starting Z coordinate for a particular ROI object.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIZ0(int imageIndex, int roiIndex);

  /**
   * Gets the starting timepoint for a particular ROI object.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIT0(int imageIndex, int roiIndex);

  /**
   * Gets the ending X coordinate for a particular ROI object.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIX1(int imageIndex, int roiIndex);

  /**
   * Gets the ending Y coordinate for a particular ROI object.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIY1(int imageIndex, int roiIndex);

  /**
   * Gets the ending Z coordinate for a particular ROI object.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIZ1(int imageIndex, int roiIndex);

  /**
   * Gets the ending timepoint for a particular ROI object.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIT1(int imageIndex, int roiIndex);

  // - StageLabel property retrieval -

  /**
   * Gets a name for the stage label for a particular StageLabel object.
   * @param imageIndex index of the Image
   */
  String getStageLabelName(int imageIndex);

  /**
   * Gets x coordinate of the stage for a particular StageLabel object.
   * @param imageIndex index of the Image
   */
  Float getStageLabelX(int imageIndex);

  /**
   * Gets y coordinate of the stage for a particular StageLabel object.
   * @param imageIndex index of the Image
   */
  Float getStageLabelY(int imageIndex);

  /**
   * Gets z coordinate of the stage for a particular StageLabel object.
   * @param imageIndex index of the Image
   */
  Float getStageLabelZ(int imageIndex);

  // - Instrument property retrieval -

  // - LightSource property retrieval -

  /**
   * Gets CTR TODO for a particular LightSource object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets CTR TODO for a particular LightSource object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLightSourceModel(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets CTR TODO for a particular LightSource object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex);

  // - Laser property retrieval -

  /**
   * Gets CTR TODO for a particular Laser object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLaserType(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets CTR TODO for a particular Laser object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets CTR TODO for a particular Laser object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Integer getLaserWavelength(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets CTR TODO for a particular Laser object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Integer getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets CTR TODO for a particular Laser object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets CTR TODO for a particular Laser object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLaserPulse(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets CTR TODO for a particular Laser object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Float getLaserPower(int instrumentIndex, int lightSourceIndex);

  // - Filament property retrieval -

  /**
   * Gets CTR TODO for a particular Filament object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getFilamentType(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets CTR TODO for a particular Filament object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Float getFilamentPower(int instrumentIndex, int lightSourceIndex);

  // - Arc property retrieval -

  /**
   * Gets CTR TODO for a particular Arc object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getArcType(int instrumentIndex, int lightSourceIndex);

  /**
   * Gets CTR TODO for a particular Arc object.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Float getArcPower(int instrumentIndex, int lightSourceIndex);

  // - Detector property retrieval -

  /**
   * Gets CTR TODO for a particular Detector object.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorManufacturer(int instrumentIndex, int detectorIndex);

  /**
   * Gets CTR TODO for a particular Detector object.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorModel(int instrumentIndex, int detectorIndex);

  /**
   * Gets CTR TODO for a particular Detector object.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorSerialNumber(int instrumentIndex, int detectorIndex);

  /**
   * Gets CTR TODO for a particular Detector object.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorType(int instrumentIndex, int detectorIndex);

  /**
   * Gets CTR TODO for a particular Detector object.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  Float getDetectorGain(int instrumentIndex, int detectorIndex);

  /**
   * Gets CTR TODO for a particular Detector object.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  Float getDetectorVoltage(int instrumentIndex, int detectorIndex);

  /**
   * Gets CTR TODO for a particular Detector object.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  Float getDetectorOffset(int instrumentIndex, int detectorIndex);

  // - Objective property retrieval -

  /**
   * Gets CTR TODO for a particular Objective object.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex);

  /**
   * Gets CTR TODO for a particular Objective object.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveModel(int instrumentIndex, int objectiveIndex);

  /**
   * Gets CTR TODO for a particular Objective object.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex);

  /**
   * Gets CTR TODO for a particular Objective object.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveCorrection(int instrumentIndex, int objectiveIndex);

  /**
   * Gets CTR TODO for a particular Objective object.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveImmersion(int instrumentIndex, int objectiveIndex);

  /**
   * Gets CTR TODO for a particular Objective object.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Float getObjectiveLensNA(int instrumentIndex, int objectiveIndex);

  /**
   * Gets CTR TODO for a particular Objective object.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Integer getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex);

  /**
   * Gets CTR TODO for a particular Objective object.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex);

  /**
   * Gets CTR TODO for a particular Objective object.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex);

  // - OTF property retrieval -

  /**
   * Gets CTR TODO for a particular OTF object.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  Integer getOTFSizeX(int instrumentIndex, int otfIndex);

  /**
   * Gets CTR TODO for a particular OTF object.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  Integer getOTFSizeY(int instrumentIndex, int otfIndex);

  /**
   * Gets CTR TODO for a particular OTF object.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  String getOTFPixelType(int instrumentIndex, int otfIndex);

  /**
   * Gets CTR TODO for a particular OTF object.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  String getOTFPath(int instrumentIndex, int otfIndex);

  /**
   * Gets CTR TODO for a particular OTF object.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex);

  // - Experimenter property retrieval -

  /**
   * Gets the first name of the experimenter for a particular Experimenter object.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterFirstName(int experimenterIndex);

  /**
   * Gets the last name of the experimenter for a particular Experimenter object.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterLastName(int experimenterIndex);

  /**
   * Gets the e-mail address of the experimenter for a particular Experimenter object.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterEmail(int experimenterIndex);

  /**
   * Gets the institution to which the experimenter belongs for a particular Experimenter object.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterInstitution(int experimenterIndex);

  /**
   * Gets the fully qualified path to the experimenter's data for a particular Experimenter object.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterDataDirectory(int experimenterIndex);

}
