//
// MetadataRetrieve.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via MetadataAutogen on Apr 28, 2008 4:19:17 PM CDT
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

  int getChannelComponentCount(int imageIndex, int logicalChannelIndex);

  int getDetectorCount(int instrumentIndex);

  int getExperimenterCount();

  int getImageCount();

  int getInstrumentCount();

  int getLightSourceCount(int instrumentIndex);

  int getLogicalChannelCount(int imageIndex);

  int getOTFCount(int instrumentIndex);

  int getObjectiveCount(int instrumentIndex);

  int getPixelsCount(int imageIndex);

  int getPlaneCount(int imageIndex, int pixelsIndex);

  int getROICount(int imageIndex);

  int getTiffDataCount(int imageIndex, int pixelsIndex);

  // - Entity retrieval -

  // - Arc property retrieval -

  /**
   * For a particular Arc, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getArcType(int instrumentIndex, int lightSourceIndex);

  // - ChannelComponent property retrieval -

  /**
   * For a particular ChannelComponent, gets which color channel this ChannelComponent belongs to (for example, 'R' for an 'RGB' PhotometricInterpretation).
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   * @param channelComponentIndex index of the ChannelComponent
   */
  String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex);

  /**
   * For a particular ChannelComponent, gets the index into the channel dimension of the 5-D pixel array.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   * @param channelComponentIndex index of the ChannelComponent
   */
  Integer getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex);

  // - Detector property retrieval -

  /**
   * For a particular Detector, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  Float getDetectorGain(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorID(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorManufacturer(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorModel(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  Float getDetectorOffset(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorSerialNumber(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorType(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  Float getDetectorVoltage(int instrumentIndex, int detectorIndex);

  // - DetectorSettings property retrieval -

  /**
   * For a particular DetectorSettings, gets the detector associated with this channel.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, gets the detector gain.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, gets the detector offset.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex);

  // - Dimensions property retrieval -

  /**
   * For a particular Dimensions, gets the size of an individual pixel's X axis in microns.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, gets the size of an individual pixel's Y axis in microns.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, gets the size of an individual pixel's Z axis in microns.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, gets the distance between adjacent time points in seconds.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, gets the distance between adjacent wavelengths in nanometers.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getDimensionsWaveIncrement(int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, gets the starting wavelength in nanometers.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getDimensionsWaveStart(int imageIndex, int pixelsIndex);

  // - DisplayOptions property retrieval -

  /**
   * For a particular DisplayOptions, gets TODO.
   * @param imageIndex index of the Image
   */
  String getDisplayOptionsID(int imageIndex);

  /**
   * For a particular DisplayOptions, gets zoom factor for use in the display (NOT THE LENS ZOOM).
   * @param imageIndex index of the Image
   */
  Float getDisplayOptionsZoom(int imageIndex);

  // - DisplayOptionsProjection property retrieval -

  /**
   * For a particular DisplayOptionsProjection, gets the first focal plane to include in the maximum intensity projection.
   * @param imageIndex index of the Image
   */
  Integer getDisplayOptionsProjectionZStart(int imageIndex);

  /**
   * For a particular DisplayOptionsProjection, gets the last focal plane to include in the maximum intensity projection.
   * @param imageIndex index of the Image
   */
  Integer getDisplayOptionsProjectionZStop(int imageIndex);

  // - DisplayOptionsTime property retrieval -

  /**
   * For a particular DisplayOptionsTime, gets the first time point to include in the animation.
   * @param imageIndex index of the Image
   */
  Integer getDisplayOptionsTimeTStart(int imageIndex);

  /**
   * For a particular DisplayOptionsTime, gets the last time point to include in the animation.
   * @param imageIndex index of the Image
   */
  Integer getDisplayOptionsTimeTStop(int imageIndex);

  // - Experimenter property retrieval -

  /**
   * For a particular Experimenter, gets the e-mail address of the experimenter.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterEmail(int experimenterIndex);

  /**
   * For a particular Experimenter, gets the first name of the experimenter.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterFirstName(int experimenterIndex);

  /**
   * For a particular Experimenter, gets TODO.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterID(int experimenterIndex);

  /**
   * For a particular Experimenter, gets the institution to which the experimenter belongs.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterInstitution(int experimenterIndex);

  /**
   * For a particular Experimenter, gets the last name of the experimenter.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterLastName(int experimenterIndex);

  // - Filament property retrieval -

  /**
   * For a particular Filament, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getFilamentType(int instrumentIndex, int lightSourceIndex);

  // - Image property retrieval -

  /**
   * For a particular Image, gets the creation date of the image.
   * @param imageIndex index of the Image
   */
  String getImageCreationDate(int imageIndex);

  /**
   * For a particular Image, gets the full description of the image.
   * @param imageIndex index of the Image
   */
  String getImageDescription(int imageIndex);

  /**
   * For a particular Image, gets TODO.
   * @param imageIndex index of the Image
   */
  String getImageID(int imageIndex);

  /**
   * For a particular Image, gets the full name of the image.
   * @param imageIndex index of the Image
   */
  String getImageName(int imageIndex);

  // - ImagingEnvironment property retrieval -

  /**
   * For a particular ImagingEnvironment, gets TODO.
   * @param imageIndex index of the Image
   */
  Float getImagingEnvironmentAirPressure(int imageIndex);

  /**
   * For a particular ImagingEnvironment, gets TODO.
   * @param imageIndex index of the Image
   */
  Float getImagingEnvironmentCO2Percent(int imageIndex);

  /**
   * For a particular ImagingEnvironment, gets TODO.
   * @param imageIndex index of the Image
   */
  Float getImagingEnvironmentHumidity(int imageIndex);

  /**
   * For a particular ImagingEnvironment, gets TODO.
   * @param imageIndex index of the Image
   */
  Float getImagingEnvironmentTemperature(int imageIndex);

  // - Instrument property retrieval -

  /**
   * For a particular Instrument, gets TODO.
   * @param instrumentIndex index of the Instrument
   */
  String getInstrumentID(int instrumentIndex);

  // - Laser property retrieval -

  /**
   * For a particular Laser, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Integer getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLaserPulse(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLaserType(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Integer getLaserWavelength(int instrumentIndex, int lightSourceIndex);

  // - LightSource property retrieval -

  /**
   * For a particular LightSource, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLightSourceID(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLightSourceModel(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Float getLightSourcePower(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex);

  // - LightSourceSettings property retrieval -

  /**
   * For a particular LightSourceSettings, gets the primary light source attenuation.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LightSourceSettings, gets the primary light source.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LightSourceSettings, gets the primary light source wavelength.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex);

  // - LogicalChannel property retrieval -

  /**
   * For a particular LogicalChannel, gets the constrast method name.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the emission wavelength.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the excitation wavelength.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelExWave(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the fluorescence type.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets TODO.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelID(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the illumination type.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the acquisition mode.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelMode(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the logical channel's name.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelName(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the neutral-density filter value.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the photometric interpretation type.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the size of the pinhole.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets TODO.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets TODO.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex);

  // - OTF property retrieval -

  /**
   * For a particular OTF, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  String getOTFID(int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  String getOTFPixelType(int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  Integer getOTFSizeX(int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  Integer getOTFSizeY(int instrumentIndex, int otfIndex);

  // - Objective property retrieval -

  /**
   * For a particular Objective, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveCorrection(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveID(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveImmersion(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Float getObjectiveLensNA(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveModel(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Integer getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex);

  // - Pixels property retrieval -

  /**
   * For a particular Pixels, gets endianness of the pixels set.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Boolean getPixelsBigEndian(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets the dimension order of the pixels set.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  String getPixelsDimensionOrder(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets TODO.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  String getPixelsID(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets the pixel type.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  String getPixelsPixelType(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets number of channels per timepoint.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeC(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets number of timepoints.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeT(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets The size of an individual plane or section's X axis (width)..
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeX(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets The size of an individual plane or section's Y axis (height)..
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeY(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets number of optical sections per stack.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeZ(int imageIndex, int pixelsIndex);

  // - Plane property retrieval -

  /**
   * For a particular Plane, gets the channel index.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Integer getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular Plane, gets the timepoint.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Integer getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular Plane, gets the optical section index.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Integer getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex);

  // - PlaneTiming property retrieval -

  /**
   * For a particular PlaneTiming, gets the time in seconds since the beginning of the experiment.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular PlaneTiming, gets the exposure time in seconds.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex);

  // - ROI property retrieval -

  /**
   * For a particular ROI, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  String getROIID(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the starting timepoint.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIT0(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the ending timepoint.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIT1(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the starting X coordinate.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIX0(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the ending X coordinate.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIX1(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the starting Y coordinate.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIY0(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the ending Y coordinate.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIY1(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the starting Z coordinate.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIZ0(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the ending Z coordinate.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIZ1(int imageIndex, int roiIndex);

  // - StageLabel property retrieval -

  /**
   * For a particular StageLabel, gets a name for the stage label.
   * @param imageIndex index of the Image
   */
  String getStageLabelName(int imageIndex);

  /**
   * For a particular StageLabel, gets the x coordinate of the stage.
   * @param imageIndex index of the Image
   */
  Float getStageLabelX(int imageIndex);

  /**
   * For a particular StageLabel, gets the y coordinate of the stage.
   * @param imageIndex index of the Image
   */
  Float getStageLabelY(int imageIndex);

  /**
   * For a particular StageLabel, gets the z coordinate of the stage.
   * @param imageIndex index of the Image
   */
  Float getStageLabelZ(int imageIndex);

  // - StagePosition property retrieval -

  /**
   * For a particular StagePosition, gets the X coordinate of the stage position.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular StagePosition, gets the Y coordinate of the stage position.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular StagePosition, gets the Z coordinate of the stage position.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex);

  // - TiffData property retrieval -

  /**
   * For a particular TiffData, gets TODO.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, gets TODO.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  Integer getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, gets TODO.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  Integer getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, gets TODO.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  Integer getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, gets TODO.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  Integer getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, gets TODO.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  Integer getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, gets TODO.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex);

}
