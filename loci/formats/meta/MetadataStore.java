//
// MetadataStore.java
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

package loci.formats.meta;

/**
 * A proxy whose responsibility it is to marshal biological image data into a
 * particular storage medium.
 *
 * The <code>MetadataStore</code> interface encompasses the basic metadata that
 * any specific storage medium (file, relational database, etc.) should be
 * expected to store into its backing data model.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/MetadataStore.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/MetadataStore.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public interface MetadataStore {

  void createRoot();

  Object getRoot();

  void setRoot(Object root);

  // - Image property storage -

  /**
   * Sets the Name property for an image in the metadata store with a particular index.
   * @param name the full name of the image
   * @param imageIndex index of the Image
   */
  void setImageName(String name, int imageIndex);

  /**
   * Sets the CreationDate property for an image in the metadata store with a particular index.
   * @param creationDate the creation date of the image
   * @param imageIndex index of the Image
   */
  void setImageCreationDate(String creationDate, int imageIndex);

  /**
   * Sets the Description property for an image in the metadata store with a particular index.
   * @param description the full description of the image
   * @param imageIndex index of the Image
   */
  void setImageDescription(String description, int imageIndex);

  // - Pixels property storage -

  /**
   * Sets the SizeX property for a pixels set in the metadata store with a particular image and pixels index.
   * @param sizeX size of an individual plane or section's X axis (width)
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex);

  /**
   * Sets the SizeY property for a pixels set in the metadata store with a particular image and pixels index.
   * @param sizeY size of an individual plane of section's Y axis (height)
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex);

  /**
   * Sets the SizeZ property for a pixels set in the metadata store with a particular image and pixels index.
   * @param sizeZ number of optical sections per stack
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex);

  /**
   * Sets the SizeC property for a pixels set in the metadata store with a particular image and pixels index.
   * @param sizeC number of channels per timepoint
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex);

  /**
   * Sets the SizeT property for a pixels set in the metadata store with a particular image and pixels index.
   * @param sizeT number of timepoints
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex);

  /**
   * Sets the PixelType property for a pixels set in the metadata store with a particular image and pixels index.
   * @param pixelType the pixel type
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex);

  /**
   * Sets the BigEndian property for a pixels set in the metadata store with a particular image and pixels index.
   * @param bigEndian if the pixels set is big endian or not
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex);

  /**
   * Sets the DimensionOrder property for a pixels set in the metadata store with a particular image and pixels index.
   * @param dimensionOrder the dimension order of the pixels set
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex);

  // - Dimensions property storage -

  /**
   * Sets the PhysicalSizeX property for a set of pixel dimensions in the metadata store with a particular index. Unless both values are non-null, the MetadataStore should assume pixelSizeX equals pixelSizeY (i.e., should populate the null field with the other field's value).
   * @param physicalSizeX size of an individual pixel's X axis in microns
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex);

  /**
   * Sets the PhysicalSizeY property for a set of pixel dimensions in the metadata store with a particular index. Unless both values are non-null, the MetadataStore should assume pixelSizeX equals pixelSizeY (i.e., should populate the null field with the other field's value).
   * @param physicalSizeY size of an individual pixel's Y axis in microns
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex);

  /**
   * Sets the PhysicalSizeZ property for a set of pixel dimensions in the metadata store with a particular index. Unless both values are non-null, the MetadataStore should assume pixelSizeX equals pixelSizeY (i.e., should populate the null field with the other field's value).
   * @param physicalSizeZ size of an individual pixel's Z axis in microns
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex);

  /**
   * Sets the TimeIncrement property for a set of pixel dimensions in the metadata store with a particular index. Unless both values are non-null, the MetadataStore should assume pixelSizeX equals pixelSizeY (i.e., should populate the null field with the other field's value).
   * @param timeIncrement distance between adjacent time points in seconds
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex);

  /**
   * Sets the WaveStart property for a set of pixel dimensions in the metadata store with a particular index. Unless both values are non-null, the MetadataStore should assume pixelSizeX equals pixelSizeY (i.e., should populate the null field with the other field's value).
   * @param waveStart starting wavelength in nanometers
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex);

  /**
   * Sets the WaveIncrement property for a set of pixel dimensions in the metadata store with a particular index. Unless both values are non-null, the MetadataStore should assume pixelSizeX equals pixelSizeY (i.e., should populate the null field with the other field's value).
   * @param waveIncrement distance between adjacent wavelengths in nanometers
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex);

  // - ImagingEnvironment property storage -

  /**
   * Sets the Temperature property for the imaging environment for a particular image.
   * @param temperature CTR TODO
   * @param imageIndex index of the Image
   */
  void setImagingEnvironmentTemperature(Float temperature, int imageIndex);

  /**
   * Sets the AirPressure property for the imaging environment for a particular image.
   * @param airPressure CTR TODO
   * @param imageIndex index of the Image
   */
  void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex);

  /**
   * Sets the Humidity property for the imaging environment for a particular image.
   * @param humidity CTR TODO
   * @param imageIndex index of the Image
   */
  void setImagingEnvironmentHumidity(Float humidity, int imageIndex);

  /**
   * Sets the CO2Percent property for the imaging environment for a particular image.
   * @param cO2Percent CTR TODO
   * @param imageIndex index of the Image
   */
  void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex);

  // - Plane property storage -

  /**
   * Sets the TheZ property for the plane information for a specific X-Y plane (section) within a particular pixels set.
   * @param theZ the optical section index
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Sets the TheC property for the plane information for a specific X-Y plane (section) within a particular pixels set.
   * @param theC the channel index
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Sets the TheT property for the plane information for a specific X-Y plane (section) within a particular pixels set.
   * @param theT the timepoint
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex);

  // - PlaneTiming property storage -

  /**
   * Sets the DeltaT property for the timing information for a specific X-Y plane (section) within a particular pixels set.
   * @param deltaT the time in seconds since the beginning of the experiment
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Sets the ExposureTime property for the timing information for a specific X-Y plane (section) within a particular pixels set.
   * @param exposureTime the exposure time in seconds
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex);

  // - StagePosition property storage -

  /**
   * Sets the PositionX property for the stage position for a specific X-Y plane (section) within a particular pixels set.
   * @param positionX the X coordinate of the stage position
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Sets the PositionY property for the stage position for a specific X-Y plane (section) within a particular pixels set.
   * @param positionY the Y coordinate of the stage position
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * Sets the PositionZ property for the stage position for a specific X-Y plane (section) within a particular pixels set.
   * @param positionZ the Z coordinate of the stage position
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex);

  // - LogicalChannel property storage -

  /**
   * Sets the Name property for a logical channel and physical channel in the metadata store for a particular pixels.
   * @param name the logical channel's name
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the SamplesPerPixel property for a logical channel and physical channel in the metadata store for a particular pixels.
   * @param samplesPerPixel CTR TODO
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the IlluminationType property for a logical channel and physical channel in the metadata store for a particular pixels.
   * @param illuminationType the illumination type
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the PinholeSize property for a logical channel and physical channel in the metadata store for a particular pixels.
   * @param pinholeSize the size of the pinhole
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelPinholeSize(Integer pinholeSize, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the PhotometricInterpretation property for a logical channel and physical channel in the metadata store for a particular pixels.
   * @param photometricInterpretation the photometric interpretation type
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the Mode property for a logical channel and physical channel in the metadata store for a particular pixels.
   * @param mode the acquisition mode
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the ContrastMethod property for a logical channel and physical channel in the metadata store for a particular pixels.
   * @param contrastMethod the constrast method name
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the ExWave property for a logical channel and physical channel in the metadata store for a particular pixels.
   * @param exWave the excitation wavelength
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the EmWave property for a logical channel and physical channel in the metadata store for a particular pixels.
   * @param emWave the emission wavelength
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the Fluor property for a logical channel and physical channel in the metadata store for a particular pixels.
   * @param fluor the fluorescence type
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the NdFilter property for a logical channel and physical channel in the metadata store for a particular pixels.
   * @param ndFilter the neutral-density filter value
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the PockelCellSetting property for a logical channel and physical channel in the metadata store for a particular pixels.
   * @param pockelCellSetting CTR TODO
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex);

  // - DetectorSettings property storage -

  /**
   * Sets the Offset property for the detector associated with a particular logical channel.
   * @param offset the detector offset
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the Gain property for the detector associated with a particular logical channel.
   * @param gain the detector gain
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex);

  // - LightSourceSettings property storage -

  /**
   * Sets the Attenuation property for the light source associated with a particular logical channel.
   * @param attenuation the primary light source attenuation
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex);

  /**
   * Sets the Wavelength property for the light source associated with a particular logical channel.
   * @param wavelength the primary light source wavelength
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex);

  // - ROI property storage -

  /**
   * Sets the X0 property for a 5D bounding box region of interest in the metadata store with a particular index.
   * @param x0 the starting X coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIX0(Integer x0, int imageIndex, int roiIndex);

  /**
   * Sets the Y0 property for a 5D bounding box region of interest in the metadata store with a particular index.
   * @param y0 the starting Y coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIY0(Integer y0, int imageIndex, int roiIndex);

  /**
   * Sets the Z0 property for a 5D bounding box region of interest in the metadata store with a particular index.
   * @param z0 the starting Z coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIZ0(Integer z0, int imageIndex, int roiIndex);

  /**
   * Sets the T0 property for a 5D bounding box region of interest in the metadata store with a particular index.
   * @param t0 the starting timepoint
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIT0(Integer t0, int imageIndex, int roiIndex);

  /**
   * Sets the X1 property for a 5D bounding box region of interest in the metadata store with a particular index.
   * @param x1 the ending X coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIX1(Integer x1, int imageIndex, int roiIndex);

  /**
   * Sets the Y1 property for a 5D bounding box region of interest in the metadata store with a particular index.
   * @param y1 the ending Y coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIY1(Integer y1, int imageIndex, int roiIndex);

  /**
   * Sets the Z1 property for a 5D bounding box region of interest in the metadata store with a particular index.
   * @param z1 the ending Z coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIZ1(Integer z1, int imageIndex, int roiIndex);

  /**
   * Sets the T1 property for a 5D bounding box region of interest in the metadata store with a particular index.
   * @param t1 the ending timepoint
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIT1(Integer t1, int imageIndex, int roiIndex);

  // - StageLabel property storage -

  /**
   * Sets the Name property for a stage label in the metadata store with a particular index.
   * @param name a name for the stage label
   * @param imageIndex index of the Image
   */
  void setStageLabelName(String name, int imageIndex);

  /**
   * Sets the X property for a stage label in the metadata store with a particular index.
   * @param x x coordinate of the stage
   * @param imageIndex index of the Image
   */
  void setStageLabelX(Float x, int imageIndex);

  /**
   * Sets the Y property for a stage label in the metadata store with a particular index.
   * @param y y coordinate of the stage
   * @param imageIndex index of the Image
   */
  void setStageLabelY(Float y, int imageIndex);

  /**
   * Sets the Z property for a stage label in the metadata store with a particular index.
   * @param z z coordinate of the stage
   * @param imageIndex index of the Image
   */
  void setStageLabelZ(Float z, int imageIndex);

  // - LightSource property storage -

  /**
   * Sets the Manufacturer property for a light source for a particular instrument.
   * @param manufacturer CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

  /**
   * Sets the Model property for a light source for a particular instrument.
   * @param model CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex);

  /**
   * Sets the SerialNumber property for a light source for a particular instrument.
   * @param serialNumber CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

  // - Laser property storage -

  /**
   * Sets the Type property for a laser for a particular instrument.
   * @param type CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserType(String type, int instrumentIndex, int lightSourceIndex);

  /**
   * Sets the LaserMedium property for a laser for a particular instrument.
   * @param laserMedium CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex);

  /**
   * Sets the Wavelength property for a laser for a particular instrument.
   * @param wavelength CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex);

  /**
   * Sets the FrequencyMultiplication property for a laser for a particular instrument.
   * @param frequencyMultiplication CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex);

  /**
   * Sets the Tuneable property for a laser for a particular instrument.
   * @param tuneable CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex);

  /**
   * Sets the Pulse property for a laser for a particular instrument.
   * @param pulse CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex);

  /**
   * Sets the Power property for a laser for a particular instrument.
   * @param power CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserPower(Float power, int instrumentIndex, int lightSourceIndex);

  // - Filament property storage -

  /**
   * Sets the Type property for a filament for a particular instrument.
   * @param type CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setFilamentType(String type, int instrumentIndex, int lightSourceIndex);

  /**
   * Sets the Power property for a filament for a particular instrument.
   * @param power CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setFilamentPower(Float power, int instrumentIndex, int lightSourceIndex);

  // - Arc property storage -

  /**
   * Sets the Type property for an arc for a particular instrument.
   * @param type CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setArcType(String type, int instrumentIndex, int lightSourceIndex);

  /**
   * Sets the Power property for an arc for a particular instrument.
   * @param power CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setArcPower(Float power, int instrumentIndex, int lightSourceIndex);

  // - Detector property storage -

  /**
   * Sets the Manufacturer property for a detector for a particular instrument.
   * @param manufacturer CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex);

  /**
   * Sets the Model property for a detector for a particular instrument.
   * @param model CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorModel(String model, int instrumentIndex, int detectorIndex);

  /**
   * Sets the SerialNumber property for a detector for a particular instrument.
   * @param serialNumber CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex);

  /**
   * Sets the Type property for a detector for a particular instrument.
   * @param type CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorType(String type, int instrumentIndex, int detectorIndex);

  /**
   * Sets the Gain property for a detector for a particular instrument.
   * @param gain CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex);

  /**
   * Sets the Voltage property for a detector for a particular instrument.
   * @param voltage CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex);

  /**
   * Sets the Offset property for a detector for a particular instrument.
   * @param offset CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex);

  // - Objective property storage -

  /**
   * Sets the Manufacturer property for an objective for a particular instrument.
   * @param manufacturer CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex);

  /**
   * Sets the Model property for an objective for a particular instrument.
   * @param model CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex);

  /**
   * Sets the SerialNumber property for an objective for a particular instrument.
   * @param serialNumber CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex);

  /**
   * Sets the Correction property for an objective for a particular instrument.
   * @param correction CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex);

  /**
   * Sets the Immersion property for an objective for a particular instrument.
   * @param immersion CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex);

  /**
   * Sets the LensNA property for an objective for a particular instrument.
   * @param lensNA CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex);

  /**
   * Sets the NominalMagnification property for an objective for a particular instrument.
   * @param nominalMagnification CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex);

  /**
   * Sets the CalibratedMagnification property for an objective for a particular instrument.
   * @param calibratedMagnification CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex);

  /**
   * Sets the WorkingDistance property for an objective for a particular instrument.
   * @param workingDistance CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveWorkingDistance(Float workingDistance, int instrumentIndex, int objectiveIndex);

  // - OTF property storage -

  /**
   * Sets the SizeX property for an optical transfer function for a particular instrument.
   * @param sizeX CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex);

  /**
   * Sets the SizeY property for an optical transfer function for a particular instrument.
   * @param sizeY CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex);

  /**
   * Sets the PixelType property for an optical transfer function for a particular instrument.
   * @param pixelType CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex);

  /**
   * Sets the Path property for an optical transfer function for a particular instrument.
   * @param path CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFPath(String path, int instrumentIndex, int otfIndex);

  /**
   * Sets the OpticalAxisAveraged property for an optical transfer function for a particular instrument.
   * @param opticalAxisAveraged CTR TODO
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex);

  // - Experimenter property storage -

  /**
   * Sets the FirstName property for an experimenter in the metadata store with a particular index.
   * @param firstName the first name of the experimenter
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterFirstName(String firstName, int experimenterIndex);

  /**
   * Sets the LastName property for an experimenter in the metadata store with a particular index.
   * @param lastName the last name of the experimenter
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterLastName(String lastName, int experimenterIndex);

  /**
   * Sets the Email property for an experimenter in the metadata store with a particular index.
   * @param email the e-mail address of the experimenter
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterEmail(String email, int experimenterIndex);

  /**
   * Sets the Institution property for an experimenter in the metadata store with a particular index.
   * @param institution the institution to which the experimenter belongs
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterInstitution(String institution, int experimenterIndex);

  /**
   * Sets the DataDirectory property for an experimenter in the metadata store with a particular index.
   * @param dataDirectory the fully qualified path to the experimenter's data
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterDataDirectory(String dataDirectory, int experimenterIndex);

}
