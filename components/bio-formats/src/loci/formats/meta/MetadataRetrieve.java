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
 * Created by curtis via MetadataAutogen on Oct 17, 2008 1:25:59 AM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

/**
 * A proxy whose responsibility it is to extract biological image data from a
 * particular storage medium.
 *
 * <p>The <code>MetadataRetrieve</code> interface encompasses the metadata
 * that any specific storage medium (file, relational database, etc.) should be
 * expected to access from its backing data model.
 *
 * <p>The <code>MetadataRetrieve</code> interface goes hand in hand with the
 * <code>MetadataStore</code> interface. Essentially,
 * <code>MetadataRetrieve</code> provides the "getter" methods for a storage
 * medium, and <code>MetadataStore</code> provides the "setter" methods.
 *
 * <p>Since it often makes sense for a storage medium to implement both
 * interfaces, there is also an {@link IMetadata} interface encompassing
 * both <code>MetadataStore</code> and <code>MetadataRetrieve</code>, which
 * reduces the need to cast between object types.
 *
 * <p>See {@link loci.formats.ome.OMEXMLMetadata} for an example
 * implementation.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/meta/MetadataRetrieve.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/meta/MetadataRetrieve.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public interface MetadataRetrieve {

  // - Entity counting -

  int getChannelComponentCount(int imageIndex, int logicalChannelIndex);

  int getDetectorCount(int instrumentIndex);

  int getExperimentCount();

  int getExperimenterCount();

  int getExperimenterMembershipCount(int experimenterIndex);

  int getGroupRefCount(int experimenterIndex);

  int getImageCount();

  int getInstrumentCount();

  int getLightSourceCount(int instrumentIndex);

  int getLogicalChannelCount(int imageIndex);

  int getOTFCount(int instrumentIndex);

  int getObjectiveCount(int instrumentIndex);

  int getPixelsCount(int imageIndex);

  int getPlaneCount(int imageIndex, int pixelsIndex);

  int getPlateCount();

  int getPlateRefCount(int screenIndex);

  int getROICount(int imageIndex);

  int getReagentCount(int screenIndex);

  int getScreenCount();

  int getScreenAcquisitionCount(int screenIndex);

  int getTiffDataCount(int imageIndex, int pixelsIndex);

  int getWellCount(int plateIndex);

  int getWellSampleCount(int plateIndex, int wellIndex);

  // - Entity retrieval -

  /** Gets the UUID associated with this collection of metadata. */
  String getUUID();

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
   * For a particular Detector, gets unique label identifying the detector.
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
   * For a particular DisplayOptions, gets unique label identifying the display options.
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

  // - Experiment property retrieval -

  /**
   * For a particular Experiment, gets TODO.
   * @param experimentIndex index of the Experiment
   */
  String getExperimentDescription(int experimentIndex);

  /**
   * For a particular Experiment, gets unique label identifying the experiment.
   * @param experimentIndex index of the Experiment
   */
  String getExperimentID(int experimentIndex);

  /**
   * For a particular Experiment, gets TODO.
   * @param experimentIndex index of the Experiment
   */
  String getExperimentType(int experimentIndex);

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
   * For a particular Experimenter, gets unique label identifying the experimenter.
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

  // - ExperimenterMembership property retrieval -

  /**
   * For a particular ExperimenterMembership, gets the group associated with this membership.
   * @param experimenterIndex index of the Experimenter
   * @param groupRefIndex index of the GroupRef
   */
  String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex);

  // - Filament property retrieval -

  /**
   * For a particular Filament, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getFilamentType(int instrumentIndex, int lightSourceIndex);

  // - GroupRef property retrieval -

  // - Image property retrieval -

  /**
   * For a particular Image, gets the creation date of the image.
   * @param imageIndex index of the Image
   */
  String getImageCreationDate(int imageIndex);

  /**
   * For a particular Image, gets default pixels set for the image.
   * @param imageIndex index of the Image
   */
  String getImageDefaultPixels(int imageIndex);

  /**
   * For a particular Image, gets the full description of the image.
   * @param imageIndex index of the Image
   */
  String getImageDescription(int imageIndex);

  /**
   * For a particular Image, gets unique label identifying the image.
   * @param imageIndex index of the Image
   */
  String getImageID(int imageIndex);

  /**
   * For a particular Image, gets label reference for the associated instrument.
   * @param imageIndex index of the Image
   */
  String getImageInstrumentRef(int imageIndex);

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
   * For a particular Instrument, gets unique label identifying the instrument.
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
   * For a particular LightSource, gets unique label identifying the light source.
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
   * For a particular LogicalChannel, gets unique label identifying the logical channel.
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
   * For a particular LogicalChannel, gets the OTF associated with the logical channel.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex);

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
  Float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex);

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
   * For a particular OTF, gets unique label identifying the optical transfer function.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  String getOTFID(int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, gets objective described by the optical transfer function.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  String getOTFObjective(int instrumentIndex, int otfIndex);

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
   * For a particular Objective, gets unique label identifying the objective.
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
   * For a particular Pixels, gets unique label identifying the pixels set.
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

  // - Plate property retrieval -

  /**
   * For a particular Plate, gets identifies a plate within a screen.
   * @param plateIndex index of the Plate
   */
  String getPlateDescription(int plateIndex);

  /**
   * For a particular Plate, gets reference to this plate in an external database.
   * @param plateIndex index of the Plate
   */
  String getPlateExternalIdentifier(int plateIndex);

  /**
   * For a particular Plate, gets unique label identifying the plate.
   * @param plateIndex index of the Plate
   */
  String getPlateID(int plateIndex);

  /**
   * For a particular Plate, gets the plate's name.
   * @param plateIndex index of the Plate
   */
  String getPlateName(int plateIndex);

  /**
   * For a particular Plate, gets current state of the plate with respect to the experiment work-flow.
   * @param plateIndex index of the Plate
   */
  String getPlateStatus(int plateIndex);

  // - PlateRef property retrieval -

  /**
   * For a particular PlateRef, gets label reference for the associated plate.
   * @param screenIndex index of the Screen
   * @param plateRefIndex index of the PlateRef
   */
  String getPlateRefID(int screenIndex, int plateRefIndex);

  // - ROI property retrieval -

  /**
   * For a particular ROI, gets unique label identifying the 5D bounding box ROI.
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

  // - Reagent property retrieval -

  /**
   * For a particular Reagent, gets the reagent's description.
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  String getReagentDescription(int screenIndex, int reagentIndex);

  /**
   * For a particular Reagent, gets unique label identifying the reagent.
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  String getReagentID(int screenIndex, int reagentIndex);

  /**
   * For a particular Reagent, gets the reagent's name.
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  String getReagentName(int screenIndex, int reagentIndex);

  /**
   * For a particular Reagent, gets reference to this reagent in an external database.
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  String getReagentReagentIdentifier(int screenIndex, int reagentIndex);

  // - Screen property retrieval -

  /**
   * For a particular Screen, gets unique label identifying the screen.
   * @param screenIndex index of the Screen
   */
  String getScreenID(int screenIndex);

  /**
   * For a particular Screen, gets the screen's name.
   * @param screenIndex index of the Screen
   */
  String getScreenName(int screenIndex);

  /**
   * For a particular Screen, gets description of the screen's protocol.
   * @param screenIndex index of the Screen
   */
  String getScreenProtocolDescription(int screenIndex);

  /**
   * For a particular Screen, gets reference to an externally defined protocol.
   * @param screenIndex index of the Screen
   */
  String getScreenProtocolIdentifier(int screenIndex);

  /**
   * For a particular Screen, gets description of a set of reagents.
   * @param screenIndex index of the Screen
   */
  String getScreenReagentSetDescription(int screenIndex);

  /**
   * For a particular Screen, gets human-readable screen type, e.g. RNAi, cDNA.
   * @param screenIndex index of the Screen
   */
  String getScreenType(int screenIndex);

  // - ScreenAcquisition property retrieval -

  /**
   * For a particular ScreenAcquisition, gets time when the last image was acquired.
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   */
  String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex);

  /**
   * For a particular ScreenAcquisition, gets unique label identifying the screen's acquisition run.
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   */
  String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex);

  /**
   * For a particular ScreenAcquisition, gets time when the first image was acquired.
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   */
  String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex);

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

  // - Well property retrieval -

  /**
   * For a particular Well, gets column index of the well, where top-left is 0.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  Integer getWellColumn(int plateIndex, int wellIndex);

  /**
   * For a particular Well, gets description of the externally defined ID for this plate.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  String getWellExternalDescription(int plateIndex, int wellIndex);

  /**
   * For a particular Well, gets reference to this well in an external database.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  String getWellExternalIdentifier(int plateIndex, int wellIndex);

  /**
   * For a particular Well, gets unique label identifying the well.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  String getWellID(int plateIndex, int wellIndex);

  /**
   * For a particular Well, gets row index of the well, where top-left is 0.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  Integer getWellRow(int plateIndex, int wellIndex);

  /**
   * For a particular Well, gets human-readable identifier of the screen status, e.g. empty, control.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  String getWellType(int plateIndex, int wellIndex);

  // - WellSample property retrieval -

  /**
   * For a particular WellSample, gets unique label identifying the individual well image.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, gets link to the Image element.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  Integer getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, gets X position of the image within the well.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  Float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, gets Y position of the image within the well.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  Float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, gets time-point at which the image started to be collected.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  Integer getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);

}
