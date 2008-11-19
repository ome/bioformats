//
// MetadataStore.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2008 UW-Madison LOCI and Glencoe Software, Inc.

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
 * Created by curtis via MetadataAutogen on Nov 19, 2008 10:52:54 AM CST
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

/**
 * A proxy whose responsibility it is to marshal biological image data into a
 * particular storage medium.
 *
 * <p>The <code>MetadataStore</code> interface encompasses the metadata that
 * any specific storage medium (file, relational database, etc.) should be
 * expected to store into its backing data model.
 *
 * <p>The <code>MetadataStore</code> interface goes hand in hand with the
 * <code>MetadataRetrieve</code> interface. Essentially,
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
 * <p><b>Important note:</b> It is strongly recommended that applications
 * (e.g., file format readers) using <code>MetadataStore</code> populate
 * information in a linear order. Specifically, iterating over entities
 * from "leftmost" index to "rightmost" index is required for certain
 * <code>MetadataStore</code> implementations such as OMERO's
 * <code>OMEROMetadataStore</code>. For example, when populating Image, Pixels
 * and Plane information, an outer loop should iterate across
 * <code>imageIndex</code>, an inner loop should iterate across
 * <code>pixelsIndex</code>, and an innermost loop should handle
 * <code>planeIndex</code>. For an illustration of the ideal traversal order,
 * see {@link loci.formats.meta.MetadataConverter#convertMetadata}.</p>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/meta/MetadataStore.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/meta/MetadataStore.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public interface MetadataStore {

  void createRoot();

  Object getRoot();

  void setRoot(Object root);

  // - Entity storage -

  /** Sets the UUID associated with this collection of metadata. */
  void setUUID(String uuid);

  // - Arc property storage -

  /**
   * For a particular Arc, sets TODO.
   * @param type TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setArcType(String type, int instrumentIndex, int lightSourceIndex);

  // - ChannelComponent property storage -

  /**
   * For a particular ChannelComponent, sets which color channel this ChannelComponent belongs to (for example, 'R' for an 'RGB' PhotometricInterpretation).
   * @param colorDomain which color channel this ChannelComponent belongs to (for example, 'R' for an 'RGB' PhotometricInterpretation)
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   * @param channelComponentIndex index of the ChannelComponent
   */
  void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex);

  /**
   * For a particular ChannelComponent, sets the index into the channel dimension of the 5-D pixel array.
   * @param index the index into the channel dimension of the 5-D pixel array
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   * @param channelComponentIndex index of the ChannelComponent
   */
  void setChannelComponentIndex(Integer index, int imageIndex, int logicalChannelIndex, int channelComponentIndex);

  // - Detector property storage -

  /**
   * For a particular Detector, sets TODO.
   * @param gain TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets unique label identifying the detector.
   * @param id unique label identifying the detector
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorID(String id, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets TODO.
   * @param manufacturer TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets TODO.
   * @param model TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorModel(String model, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets TODO.
   * @param offset TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets TODO.
   * @param serialNumber TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets TODO.
   * @param type TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorType(String type, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets TODO.
   * @param voltage TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex);

  // - DetectorSettings property storage -

  /**
   * For a particular DetectorSettings, sets the detector associated with this channel.
   * @param detector the detector associated with this channel
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, sets the detector gain.
   * @param gain the detector gain
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, sets the detector offset.
   * @param offset the detector offset
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex);

  // - Dimensions property storage -

  /**
   * For a particular Dimensions, sets the size of an individual pixel's X axis in microns.
   * @param physicalSizeX the size of an individual pixel's X axis in microns
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, sets the size of an individual pixel's Y axis in microns.
   * @param physicalSizeY the size of an individual pixel's Y axis in microns
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, sets the size of an individual pixel's Z axis in microns.
   * @param physicalSizeZ the size of an individual pixel's Z axis in microns
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, sets the distance between adjacent time points in seconds.
   * @param timeIncrement the distance between adjacent time points in seconds
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, sets the distance between adjacent wavelengths in nanometers.
   * @param waveIncrement the distance between adjacent wavelengths in nanometers
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, sets the starting wavelength in nanometers.
   * @param waveStart the starting wavelength in nanometers
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex);

  // - DisplayOptions property storage -

  /**
   * For a particular DisplayOptions, sets unique label identifying the display options.
   * @param id unique label identifying the display options
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsID(String id, int imageIndex);

  /**
   * For a particular DisplayOptions, sets zoom factor for use in the display (NOT THE LENS ZOOM).
   * @param zoom zoom factor for use in the display (NOT THE LENS ZOOM)
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsZoom(Float zoom, int imageIndex);

  // - DisplayOptionsProjection property storage -

  /**
   * For a particular DisplayOptionsProjection, sets the first focal plane to include in the maximum intensity projection.
   * @param zStart the first focal plane to include in the maximum intensity projection
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsProjectionZStart(Integer zStart, int imageIndex);

  /**
   * For a particular DisplayOptionsProjection, sets the last focal plane to include in the maximum intensity projection.
   * @param zStop the last focal plane to include in the maximum intensity projection
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsProjectionZStop(Integer zStop, int imageIndex);

  // - DisplayOptionsTime property storage -

  /**
   * For a particular DisplayOptionsTime, sets the first time point to include in the animation.
   * @param tStart the first time point to include in the animation
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsTimeTStart(Integer tStart, int imageIndex);

  /**
   * For a particular DisplayOptionsTime, sets the last time point to include in the animation.
   * @param tStop the last time point to include in the animation
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsTimeTStop(Integer tStop, int imageIndex);

  // - Experiment property storage -

  /**
   * For a particular Experiment, sets TODO.
   * @param description TODO
   * @param experimentIndex index of the Experiment
   */
  void setExperimentDescription(String description, int experimentIndex);

  /**
   * For a particular Experiment, sets unique label identifying the experiment.
   * @param id unique label identifying the experiment
   * @param experimentIndex index of the Experiment
   */
  void setExperimentID(String id, int experimentIndex);

  /**
   * For a particular Experiment, sets TODO.
   * @param type TODO
   * @param experimentIndex index of the Experiment
   */
  void setExperimentType(String type, int experimentIndex);

  // - Experimenter property storage -

  /**
   * For a particular Experimenter, sets the e-mail address of the experimenter.
   * @param email the e-mail address of the experimenter
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterEmail(String email, int experimenterIndex);

  /**
   * For a particular Experimenter, sets the first name of the experimenter.
   * @param firstName the first name of the experimenter
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterFirstName(String firstName, int experimenterIndex);

  /**
   * For a particular Experimenter, sets unique label identifying the experimenter.
   * @param id unique label identifying the experimenter
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterID(String id, int experimenterIndex);

  /**
   * For a particular Experimenter, sets the institution to which the experimenter belongs.
   * @param institution the institution to which the experimenter belongs
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterInstitution(String institution, int experimenterIndex);

  /**
   * For a particular Experimenter, sets the last name of the experimenter.
   * @param lastName the last name of the experimenter
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterLastName(String lastName, int experimenterIndex);

  // - ExperimenterMembership property storage -

  /**
   * For a particular ExperimenterMembership, sets the group associated with this membership.
   * @param group the group associated with this membership
   * @param experimenterIndex index of the Experimenter
   * @param groupRefIndex index of the GroupRef
   */
  void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex);

  // - Filament property storage -

  /**
   * For a particular Filament, sets TODO.
   * @param type TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setFilamentType(String type, int instrumentIndex, int lightSourceIndex);

  // - GroupRef property storage -

  // - Image property storage -

  /**
   * For a particular Image, sets the creation date of the image.
   * @param creationDate the creation date of the image
   * @param imageIndex index of the Image
   */
  void setImageCreationDate(String creationDate, int imageIndex);

  /**
   * For a particular Image, sets default pixels set for the image.
   * @param defaultPixels default pixels set for the image
   * @param imageIndex index of the Image
   */
  void setImageDefaultPixels(String defaultPixels, int imageIndex);

  /**
   * For a particular Image, sets the full description of the image.
   * @param description the full description of the image
   * @param imageIndex index of the Image
   */
  void setImageDescription(String description, int imageIndex);

  /**
   * For a particular Image, sets unique label identifying the image.
   * @param id unique label identifying the image
   * @param imageIndex index of the Image
   */
  void setImageID(String id, int imageIndex);

  /**
   * For a particular Image, sets label reference for the associated instrument.
   * @param instrumentRef label reference for the associated instrument
   * @param imageIndex index of the Image
   */
  void setImageInstrumentRef(String instrumentRef, int imageIndex);

  /**
   * For a particular Image, sets the full name of the image.
   * @param name the full name of the image
   * @param imageIndex index of the Image
   */
  void setImageName(String name, int imageIndex);

  // - ImagingEnvironment property storage -

  /**
   * For a particular ImagingEnvironment, sets TODO.
   * @param airPressure TODO
   * @param imageIndex index of the Image
   */
  void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex);

  /**
   * For a particular ImagingEnvironment, sets TODO.
   * @param cO2Percent TODO
   * @param imageIndex index of the Image
   */
  void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex);

  /**
   * For a particular ImagingEnvironment, sets TODO.
   * @param humidity TODO
   * @param imageIndex index of the Image
   */
  void setImagingEnvironmentHumidity(Float humidity, int imageIndex);

  /**
   * For a particular ImagingEnvironment, sets TODO.
   * @param temperature TODO
   * @param imageIndex index of the Image
   */
  void setImagingEnvironmentTemperature(Float temperature, int imageIndex);

  // - Instrument property storage -

  /**
   * For a particular Instrument, sets unique label identifying the instrument.
   * @param id unique label identifying the instrument
   * @param instrumentIndex index of the Instrument
   */
  void setInstrumentID(String id, int instrumentIndex);

  // - Laser property storage -

  /**
   * For a particular Laser, sets TODO.
   * @param frequencyMultiplication TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, sets TODO.
   * @param laserMedium TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, sets TODO.
   * @param pulse TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, sets TODO.
   * @param tuneable TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, sets TODO.
   * @param type TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserType(String type, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, sets TODO.
   * @param wavelength TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex);

  // - LightSource property storage -

  /**
   * For a particular LightSource, sets unique label identifying the light source.
   * @param id unique label identifying the light source
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, sets TODO.
   * @param manufacturer TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, sets TODO.
   * @param model TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, sets TODO.
   * @param power TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourcePower(Float power, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, sets TODO.
   * @param serialNumber TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

  // - LightSourceSettings property storage -

  /**
   * For a particular LightSourceSettings, sets the primary light source attenuation.
   * @param attenuation the primary light source attenuation
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LightSourceSettings, sets the primary light source.
   * @param lightSource the primary light source
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LightSourceSettings, sets the primary light source wavelength.
   * @param wavelength the primary light source wavelength
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex);

  // - LogicalChannel property storage -

  /**
   * For a particular LogicalChannel, sets the constrast method name.
   * @param contrastMethod the constrast method name
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the emission wavelength.
   * @param emWave the emission wavelength
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the excitation wavelength.
   * @param exWave the excitation wavelength
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the fluorescence type.
   * @param fluor the fluorescence type
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets unique label identifying the logical channel.
   * @param id unique label identifying the logical channel
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the illumination type.
   * @param illuminationType the illumination type
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the acquisition mode.
   * @param mode the acquisition mode
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the logical channel's name.
   * @param name the logical channel's name
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the neutral-density filter value.
   * @param ndFilter the neutral-density filter value
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the OTF associated with the logical channel.
   * @param otf the OTF associated with the logical channel
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the photometric interpretation type.
   * @param photometricInterpretation the photometric interpretation type
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the size of the pinhole.
   * @param pinholeSize the size of the pinhole
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelPinholeSize(Float pinholeSize, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets TODO.
   * @param pockelCellSetting TODO
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets TODO.
   * @param samplesPerPixel TODO
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex);

  // - OTF property storage -

  /**
   * For a particular OTF, sets unique label identifying the optical transfer function.
   * @param id unique label identifying the optical transfer function
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFID(String id, int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, sets objective described by the optical transfer function.
   * @param objective objective described by the optical transfer function
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFObjective(String objective, int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, sets TODO.
   * @param opticalAxisAveraged TODO
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, sets TODO.
   * @param pixelType TODO
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, sets TODO.
   * @param sizeX TODO
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, sets TODO.
   * @param sizeY TODO
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex);

  // - Objective property storage -

  /**
   * For a particular Objective, sets TODO.
   * @param calibratedMagnification TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets TODO.
   * @param correction TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets unique label identifying the objective.
   * @param id unique label identifying the objective
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveID(String id, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets TODO.
   * @param immersion TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets TODO.
   * @param lensNA TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets TODO.
   * @param manufacturer TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets TODO.
   * @param model TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets TODO.
   * @param nominalMagnification TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets TODO.
   * @param serialNumber TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets TODO.
   * @param workingDistance TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveWorkingDistance(Float workingDistance, int instrumentIndex, int objectiveIndex);

  // - Pixels property storage -

  /**
   * For a particular Pixels, sets endianness of the pixels set.
   * @param bigEndian endianness of the pixels set
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets the dimension order of the pixels set.
   * @param dimensionOrder the dimension order of the pixels set
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets unique label identifying the pixels set.
   * @param id unique label identifying the pixels set
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsID(String id, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets the pixel type.
   * @param pixelType the pixel type
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets number of channels per timepoint.
   * @param sizeC number of channels per timepoint
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets number of timepoints.
   * @param sizeT number of timepoints
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets The size of an individual plane or section's X axis (width)..
   * @param sizeX The size of an individual plane or section's X axis (width).
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets The size of an individual plane or section's Y axis (height)..
   * @param sizeY The size of an individual plane or section's Y axis (height).
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets number of optical sections per stack.
   * @param sizeZ number of optical sections per stack
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex);

  // - Plane property storage -

  /**
   * For a particular Plane, sets the channel index.
   * @param theC the channel index
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular Plane, sets the timepoint.
   * @param theT the timepoint
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular Plane, sets the optical section index.
   * @param theZ the optical section index
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex);

  // - PlaneTiming property storage -

  /**
   * For a particular PlaneTiming, sets the time in seconds since the beginning of the experiment.
   * @param deltaT the time in seconds since the beginning of the experiment
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular PlaneTiming, sets the exposure time in seconds.
   * @param exposureTime the exposure time in seconds
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex);

  // - Plate property storage -

  /**
   * For a particular Plate, sets identifies a plate within a screen.
   * @param description identifies a plate within a screen
   * @param plateIndex index of the Plate
   */
  void setPlateDescription(String description, int plateIndex);

  /**
   * For a particular Plate, sets reference to this plate in an external database.
   * @param externalIdentifier reference to this plate in an external database
   * @param plateIndex index of the Plate
   */
  void setPlateExternalIdentifier(String externalIdentifier, int plateIndex);

  /**
   * For a particular Plate, sets unique label identifying the plate.
   * @param id unique label identifying the plate
   * @param plateIndex index of the Plate
   */
  void setPlateID(String id, int plateIndex);

  /**
   * For a particular Plate, sets the plate's name.
   * @param name the plate's name
   * @param plateIndex index of the Plate
   */
  void setPlateName(String name, int plateIndex);

  /**
   * For a particular Plate, sets current state of the plate with respect to the experiment work-flow.
   * @param status current state of the plate with respect to the experiment work-flow
   * @param plateIndex index of the Plate
   */
  void setPlateStatus(String status, int plateIndex);

  // - PlateRef property storage -

  /**
   * For a particular PlateRef, sets label reference for the associated plate.
   * @param id label reference for the associated plate
   * @param screenIndex index of the Screen
   * @param plateRefIndex index of the PlateRef
   */
  void setPlateRefID(String id, int screenIndex, int plateRefIndex);

  // - ROI property storage -

  /**
   * For a particular ROI, sets unique label identifying the 5D bounding box ROI.
   * @param id unique label identifying the 5D bounding box ROI
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIID(String id, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the starting timepoint.
   * @param t0 the starting timepoint
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIT0(Integer t0, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the ending timepoint.
   * @param t1 the ending timepoint
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIT1(Integer t1, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the starting X coordinate.
   * @param x0 the starting X coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIX0(Integer x0, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the ending X coordinate.
   * @param x1 the ending X coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIX1(Integer x1, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the starting Y coordinate.
   * @param y0 the starting Y coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIY0(Integer y0, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the ending Y coordinate.
   * @param y1 the ending Y coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIY1(Integer y1, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the starting Z coordinate.
   * @param z0 the starting Z coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIZ0(Integer z0, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the ending Z coordinate.
   * @param z1 the ending Z coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIZ1(Integer z1, int imageIndex, int roiIndex);

  // - Reagent property storage -

  /**
   * For a particular Reagent, sets the reagent's description.
   * @param description the reagent's description
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  void setReagentDescription(String description, int screenIndex, int reagentIndex);

  /**
   * For a particular Reagent, sets unique label identifying the reagent.
   * @param id unique label identifying the reagent
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  void setReagentID(String id, int screenIndex, int reagentIndex);

  /**
   * For a particular Reagent, sets the reagent's name.
   * @param name the reagent's name
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  void setReagentName(String name, int screenIndex, int reagentIndex);

  /**
   * For a particular Reagent, sets reference to this reagent in an external database.
   * @param reagentIdentifier reference to this reagent in an external database
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex);

  // - Screen property storage -

  /**
   * For a particular Screen, sets unique label identifying the screen.
   * @param id unique label identifying the screen
   * @param screenIndex index of the Screen
   */
  void setScreenID(String id, int screenIndex);

  /**
   * For a particular Screen, sets the screen's name.
   * @param name the screen's name
   * @param screenIndex index of the Screen
   */
  void setScreenName(String name, int screenIndex);

  /**
   * For a particular Screen, sets description of the screen's protocol.
   * @param protocolDescription description of the screen's protocol
   * @param screenIndex index of the Screen
   */
  void setScreenProtocolDescription(String protocolDescription, int screenIndex);

  /**
   * For a particular Screen, sets reference to an externally defined protocol.
   * @param protocolIdentifier reference to an externally defined protocol
   * @param screenIndex index of the Screen
   */
  void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex);

  /**
   * For a particular Screen, sets description of a set of reagents.
   * @param reagentSetDescription description of a set of reagents
   * @param screenIndex index of the Screen
   */
  void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex);

  /**
   * For a particular Screen, sets human-readable screen type, e.g. RNAi, cDNA.
   * @param type human-readable screen type, e.g. RNAi, cDNA
   * @param screenIndex index of the Screen
   */
  void setScreenType(String type, int screenIndex);

  // - ScreenAcquisition property storage -

  /**
   * For a particular ScreenAcquisition, sets time when the last image was acquired.
   * @param endTime time when the last image was acquired
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   */
  void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex);

  /**
   * For a particular ScreenAcquisition, sets unique label identifying the screen's acquisition run.
   * @param id unique label identifying the screen's acquisition run
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   */
  void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex);

  /**
   * For a particular ScreenAcquisition, sets time when the first image was acquired.
   * @param startTime time when the first image was acquired
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   */
  void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex);

  // - StageLabel property storage -

  /**
   * For a particular StageLabel, sets a name for the stage label.
   * @param name a name for the stage label
   * @param imageIndex index of the Image
   */
  void setStageLabelName(String name, int imageIndex);

  /**
   * For a particular StageLabel, sets the x coordinate of the stage.
   * @param x the x coordinate of the stage
   * @param imageIndex index of the Image
   */
  void setStageLabelX(Float x, int imageIndex);

  /**
   * For a particular StageLabel, sets the y coordinate of the stage.
   * @param y the y coordinate of the stage
   * @param imageIndex index of the Image
   */
  void setStageLabelY(Float y, int imageIndex);

  /**
   * For a particular StageLabel, sets the z coordinate of the stage.
   * @param z the z coordinate of the stage
   * @param imageIndex index of the Image
   */
  void setStageLabelZ(Float z, int imageIndex);

  // - StagePosition property storage -

  /**
   * For a particular StagePosition, sets the X coordinate of the stage position.
   * @param positionX the X coordinate of the stage position
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular StagePosition, sets the Y coordinate of the stage position.
   * @param positionY the Y coordinate of the stage position
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular StagePosition, sets the Z coordinate of the stage position.
   * @param positionZ the Z coordinate of the stage position
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex);

  // - TiffData property storage -

  /**
   * For a particular TiffData, sets TODO.
   * @param fileName TODO
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, sets TODO.
   * @param firstC TODO
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataFirstC(Integer firstC, int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, sets TODO.
   * @param firstT TODO
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataFirstT(Integer firstT, int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, sets TODO.
   * @param firstZ TODO
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataFirstZ(Integer firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, sets TODO.
   * @param ifd TODO
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataIFD(Integer ifd, int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, sets TODO.
   * @param numPlanes TODO
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataNumPlanes(Integer numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, sets TODO.
   * @param uuid TODO
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex);

  // - Well property storage -

  /**
   * For a particular Well, sets column index of the well, where top-left is 0.
   * @param column column index of the well, where top-left is 0
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellColumn(Integer column, int plateIndex, int wellIndex);

  /**
   * For a particular Well, sets description of the externally defined ID for this plate.
   * @param externalDescription description of the externally defined ID for this plate
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex);

  /**
   * For a particular Well, sets reference to this well in an external database.
   * @param externalIdentifier reference to this well in an external database
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex);

  /**
   * For a particular Well, sets unique label identifying the well.
   * @param id unique label identifying the well
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellID(String id, int plateIndex, int wellIndex);

  /**
   * For a particular Well, sets row index of the well, where top-left is 0.
   * @param row row index of the well, where top-left is 0
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellRow(Integer row, int plateIndex, int wellIndex);

  /**
   * For a particular Well, sets human-readable identifier of the screen status, e.g. empty, control.
   * @param type human-readable identifier of the screen status, e.g. empty, control
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellType(String type, int plateIndex, int wellIndex);

  // - WellSample property storage -

  /**
   * For a particular WellSample, sets unique label identifying the individual well image.
   * @param id unique label identifying the individual well image
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, sets link to the Image element.
   * @param index link to the Image element
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  void setWellSampleIndex(Integer index, int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, sets X position of the image within the well.
   * @param posX X position of the image within the well
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  void setWellSamplePosX(Float posX, int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, sets Y position of the image within the well.
   * @param posY Y position of the image within the well
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  void setWellSamplePosY(Float posY, int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, sets time-point at which the image started to be collected.
   * @param timepoint time-point at which the image started to be collected
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  void setWellSampleTimepoint(Integer timepoint, int plateIndex, int wellIndex, int wellSampleIndex);

}
