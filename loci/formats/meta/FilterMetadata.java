//
// FilterMetadata.vm
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
 * Created by melissa via MetadataAutogen on Jul 1, 2008 8:53:09 AM PDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

import loci.formats.DataTools;

/**
 * An implementation of {@link MetadataStore} that removes unprintable
 * characters from metadata values before storing them in a delegate
 * MetadataStore.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/meta/FilterMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/meta/FilterMetadata.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FilterMetadata implements MetadataStore {

  // -- Fields --

  private MetadataStore store;
  private boolean filter;

  // -- Constructor --

  public FilterMetadata(MetadataStore store, boolean filter) {
    this.store = store;
    this.filter = filter;
  }

  // -- MetadataStore API methods --

  /* @see MetadataStore#createRoot() */
  public void createRoot() {
    store.createRoot();
  }

  /* @see MetadataStore#getRoot() */
  public Object getRoot() {
    return store.getRoot();
  }

  /* @see MetadataStore#setRoot(Object) */
  public void setRoot(Object root) {
    store.setRoot(root);
  }

  /* @see MetadataStore#setUUID(String) */
  public void setUUID(String uuid) {
    store.setUUID(uuid);
  }

  // -- Arc property storage -

  /* @see MetadataStore#setArcType(String, int, int) */
  public void setArcType(String type, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setArcType(value, instrumentIndex, lightSourceIndex);
  }

  // -- ChannelComponent property storage -

  /* @see MetadataStore#setChannelComponentColorDomain(String, int, int, int) */
  public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    String value = filter ? DataTools.sanitize(colorDomain) : colorDomain;
    store.setChannelComponentColorDomain(value, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  /* @see MetadataStore#setChannelComponentIndex(Integer, int, int, int) */
  public void setChannelComponentIndex(Integer index, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    store.setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  // -- Detector property storage -

  /* @see MetadataStore#setDetectorGain(Float, int, int) */
  public void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex) {
    store.setDetectorGain(gain, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorID(String, int, int) */
  public void setDetectorID(String id, int instrumentIndex, int detectorIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setDetectorID(value, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorManufacturer(String, int, int) */
  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex) {
    String value = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setDetectorManufacturer(value, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorModel(String, int, int) */
  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex) {
    String value = filter ? DataTools.sanitize(model) : model;
    store.setDetectorModel(value, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorOffset(Float, int, int) */
  public void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex) {
    store.setDetectorOffset(offset, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorSerialNumber(String, int, int) */
  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex) {
    String value = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setDetectorSerialNumber(value, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorType(String, int, int) */
  public void setDetectorType(String type, int instrumentIndex, int detectorIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setDetectorType(value, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorVoltage(Float, int, int) */
  public void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex) {
    store.setDetectorVoltage(voltage, instrumentIndex, detectorIndex);
  }

  // -- DetectorSettings property storage -

  /* @see MetadataStore#setDetectorSettingsDetector(String, int, int) */
  public void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(detector) : detector;
    store.setDetectorSettingsDetector(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsGain(Float, int, int) */
  public void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex) {
    store.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsOffset(Float, int, int) */
  public void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex) {
    store.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex);
  }

  // -- Dimensions property storage -

  /* @see MetadataStore#setDimensionsPhysicalSizeX(Float, int, int) */
  public void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex) {
    store.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsPhysicalSizeY(Float, int, int) */
  public void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex) {
    store.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsPhysicalSizeZ(Float, int, int) */
  public void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex) {
    store.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsTimeIncrement(Float, int, int) */
  public void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex) {
    store.setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsWaveIncrement(Integer, int, int) */
  public void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex) {
    store.setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsWaveStart(Integer, int, int) */
  public void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex) {
    store.setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex);
  }

  // -- DisplayOptions property storage -

  /* @see MetadataStore#setDisplayOptionsID(String, int) */
  public void setDisplayOptionsID(String id, int imageIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setDisplayOptionsID(value, imageIndex);
  }

  /* @see MetadataStore#setDisplayOptionsZoom(Float, int) */
  public void setDisplayOptionsZoom(Float zoom, int imageIndex) {
    store.setDisplayOptionsZoom(zoom, imageIndex);
  }

  // -- DisplayOptionsProjection property storage -

  /* @see MetadataStore#setDisplayOptionsProjectionZStart(Integer, int) */
  public void setDisplayOptionsProjectionZStart(Integer zStart, int imageIndex) {
    store.setDisplayOptionsProjectionZStart(zStart, imageIndex);
  }

  /* @see MetadataStore#setDisplayOptionsProjectionZStop(Integer, int) */
  public void setDisplayOptionsProjectionZStop(Integer zStop, int imageIndex) {
    store.setDisplayOptionsProjectionZStop(zStop, imageIndex);
  }

  // -- DisplayOptionsTime property storage -

  /* @see MetadataStore#setDisplayOptionsTimeTStart(Integer, int) */
  public void setDisplayOptionsTimeTStart(Integer tStart, int imageIndex) {
    store.setDisplayOptionsTimeTStart(tStart, imageIndex);
  }

  /* @see MetadataStore#setDisplayOptionsTimeTStop(Integer, int) */
  public void setDisplayOptionsTimeTStop(Integer tStop, int imageIndex) {
    store.setDisplayOptionsTimeTStop(tStop, imageIndex);
  }

  // -- Experimenter property storage -

  /* @see MetadataStore#setExperimenterEmail(String, int) */
  public void setExperimenterEmail(String email, int experimenterIndex) {
    String value = filter ? DataTools.sanitize(email) : email;
    store.setExperimenterEmail(value, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterFirstName(String, int) */
  public void setExperimenterFirstName(String firstName, int experimenterIndex) {
    String value = filter ? DataTools.sanitize(firstName) : firstName;
    store.setExperimenterFirstName(value, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterID(String, int) */
  public void setExperimenterID(String id, int experimenterIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setExperimenterID(value, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterInstitution(String, int) */
  public void setExperimenterInstitution(String institution, int experimenterIndex) {
    String value = filter ? DataTools.sanitize(institution) : institution;
    store.setExperimenterInstitution(value, experimenterIndex);
  }

  /* @see MetadataStore#setExperimenterLastName(String, int) */
  public void setExperimenterLastName(String lastName, int experimenterIndex) {
    String value = filter ? DataTools.sanitize(lastName) : lastName;
    store.setExperimenterLastName(value, experimenterIndex);
  }

  // -- Filament property storage -

  /* @see MetadataStore#setFilamentType(String, int, int) */
  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setFilamentType(value, instrumentIndex, lightSourceIndex);
  }

  // -- Image property storage -

  /* @see MetadataStore#setImageCreationDate(String, int) */
  public void setImageCreationDate(String creationDate, int imageIndex) {
    String value = filter ? DataTools.sanitize(creationDate) : creationDate;
    store.setImageCreationDate(value, imageIndex);
  }

  /* @see MetadataStore#setImageDescription(String, int) */
  public void setImageDescription(String description, int imageIndex) {
    String value = filter ? DataTools.sanitize(description) : description;
    store.setImageDescription(value, imageIndex);
  }

  /* @see MetadataStore#setImageID(String, int) */
  public void setImageID(String id, int imageIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setImageID(value, imageIndex);
  }

  /* @see MetadataStore#setImageInstrumentRef(String, int) */
  public void setImageInstrumentRef(String instrumentRef, int imageIndex) {
    String value = filter ? DataTools.sanitize(instrumentRef) : instrumentRef;
    store.setImageInstrumentRef(value, imageIndex);
  }

  /* @see MetadataStore#setImageName(String, int) */
  public void setImageName(String name, int imageIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setImageName(value, imageIndex);
  }

  // -- ImagingEnvironment property storage -

  /* @see MetadataStore#setImagingEnvironmentAirPressure(Float, int) */
  public void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex) {
    store.setImagingEnvironmentAirPressure(airPressure, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentCO2Percent(Float, int) */
  public void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex) {
    store.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentHumidity(Float, int) */
  public void setImagingEnvironmentHumidity(Float humidity, int imageIndex) {
    store.setImagingEnvironmentHumidity(humidity, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentTemperature(Float, int) */
  public void setImagingEnvironmentTemperature(Float temperature, int imageIndex) {
    store.setImagingEnvironmentTemperature(temperature, imageIndex);
  }

  // -- Instrument property storage -

  /* @see MetadataStore#setInstrumentID(String, int) */
  public void setInstrumentID(String id, int instrumentIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setInstrumentID(value, instrumentIndex);
  }

  // -- Laser property storage -

  /* @see MetadataStore#setLaserFrequencyMultiplication(Integer, int, int) */
  public void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex) {
    store.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserLaserMedium(String, int, int) */
  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(laserMedium) : laserMedium;
    store.setLaserLaserMedium(value, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserPulse(String, int, int) */
  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(pulse) : pulse;
    store.setLaserPulse(value, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserTuneable(Boolean, int, int) */
  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex) {
    store.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserType(String, int, int) */
  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setLaserType(value, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLaserWavelength(Integer, int, int) */
  public void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex) {
    store.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex);
  }

  // -- LightSource property storage -

  /* @see MetadataStore#setLightSourceID(String, int, int) */
  public void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setLightSourceID(value, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLightSourceManufacturer(String, int, int) */
  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setLightSourceManufacturer(value, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLightSourceModel(String, int, int) */
  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(model) : model;
    store.setLightSourceModel(value, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLightSourcePower(Float, int, int) */
  public void setLightSourcePower(Float power, int instrumentIndex, int lightSourceIndex) {
    store.setLightSourcePower(power, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLightSourceSerialNumber(String, int, int) */
  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setLightSourceSerialNumber(value, instrumentIndex, lightSourceIndex);
  }

  // -- LightSourceSettings property storage -

  /* @see MetadataStore#setLightSourceSettingsAttenuation(Float, int, int) */
  public void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex) {
    store.setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLightSourceSettingsLightSource(String, int, int) */
  public void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(lightSource) : lightSource;
    store.setLightSourceSettingsLightSource(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLightSourceSettingsWavelength(Integer, int, int) */
  public void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex) {
    store.setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex);
  }

  // -- LogicalChannel property storage -

  /* @see MetadataStore#setLogicalChannelContrastMethod(String, int, int) */
  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(contrastMethod) : contrastMethod;
    store.setLogicalChannelContrastMethod(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelEmWave(Integer, int, int) */
  public void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex) {
    store.setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelExWave(Integer, int, int) */
  public void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex) {
    store.setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelFluor(String, int, int) */
  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(fluor) : fluor;
    store.setLogicalChannelFluor(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelID(String, int, int) */
  public void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setLogicalChannelID(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelIlluminationType(String, int, int) */
  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(illuminationType) : illuminationType;
    store.setLogicalChannelIlluminationType(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelMode(String, int, int) */
  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(mode) : mode;
    store.setLogicalChannelMode(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelName(String, int, int) */
  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setLogicalChannelName(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelNdFilter(Float, int, int) */
  public void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex) {
    store.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelPhotometricInterpretation(String, int, int) */
  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex) {
    String value = filter ? DataTools.sanitize(photometricInterpretation) : photometricInterpretation;
    store.setLogicalChannelPhotometricInterpretation(value, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelPinholeSize(Integer, int, int) */
  public void setLogicalChannelPinholeSize(Integer pinholeSize, int imageIndex, int logicalChannelIndex) {
    store.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelPockelCellSetting(Integer, int, int) */
  public void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex) {
    store.setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setLogicalChannelSamplesPerPixel(Integer, int, int) */
  public void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex) {
    store.setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex);
  }

  // -- OTF property storage -

  /* @see MetadataStore#setOTFID(String, int, int) */
  public void setOTFID(String id, int instrumentIndex, int otfIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setOTFID(value, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFOpticalAxisAveraged(Boolean, int, int) */
  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
    store.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFPixelType(String, int, int) */
  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex) {
    String value = filter ? DataTools.sanitize(pixelType) : pixelType;
    store.setOTFPixelType(value, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFSizeX(Integer, int, int) */
  public void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex) {
    store.setOTFSizeX(sizeX, instrumentIndex, otfIndex);
  }

  /* @see MetadataStore#setOTFSizeY(Integer, int, int) */
  public void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex) {
    store.setOTFSizeY(sizeY, instrumentIndex, otfIndex);
  }

  // -- Objective property storage -

  /* @see MetadataStore#setObjectiveCalibratedMagnification(Float, int, int) */
  public void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveCorrection(String, int, int) */
  public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex) {
    String value = filter ? DataTools.sanitize(correction) : correction;
    store.setObjectiveCorrection(value, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveID(String, int, int) */
  public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setObjectiveID(value, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveImmersion(String, int, int) */
  public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex) {
    String value = filter ? DataTools.sanitize(immersion) : immersion;
    store.setObjectiveImmersion(value, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveLensNA(Float, int, int) */
  public void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveManufacturer(String, int, int) */
  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex) {
    String value = filter ? DataTools.sanitize(manufacturer) : manufacturer;
    store.setObjectiveManufacturer(value, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveModel(String, int, int) */
  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex) {
    String value = filter ? DataTools.sanitize(model) : model;
    store.setObjectiveModel(value, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveNominalMagnification(Integer, int, int) */
  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveSerialNumber(String, int, int) */
  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex) {
    String value = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setObjectiveSerialNumber(value, instrumentIndex, objectiveIndex);
  }

  /* @see MetadataStore#setObjectiveWorkingDistance(Float, int, int) */
  public void setObjectiveWorkingDistance(Float workingDistance, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex);
  }

  // -- Pixels property storage -

  /* @see MetadataStore#setPixelsBigEndian(Boolean, int, int) */
  public void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex) {
    store.setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsDimensionOrder(String, int, int) */
  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex) {
    String value = filter ? DataTools.sanitize(dimensionOrder) : dimensionOrder;
    store.setPixelsDimensionOrder(value, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsID(String, int, int) */
  public void setPixelsID(String id, int imageIndex, int pixelsIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPixelsID(value, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsPixelType(String, int, int) */
  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex) {
    String value = filter ? DataTools.sanitize(pixelType) : pixelType;
    store.setPixelsPixelType(value, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeC(Integer, int, int) */
  public void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex) {
    store.setPixelsSizeC(sizeC, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeT(Integer, int, int) */
  public void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex) {
    store.setPixelsSizeT(sizeT, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeX(Integer, int, int) */
  public void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex) {
    store.setPixelsSizeX(sizeX, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeY(Integer, int, int) */
  public void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex) {
    store.setPixelsSizeY(sizeY, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setPixelsSizeZ(Integer, int, int) */
  public void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex) {
    store.setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex);
  }

  // -- Plane property storage -

  /* @see MetadataStore#setPlaneTheC(Integer, int, int, int) */
  public void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTheT(Integer, int, int, int) */
  public void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTheZ(Integer, int, int, int) */
  public void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex);
  }

  // -- PlaneTiming property storage -

  /* @see MetadataStore#setPlaneTimingDeltaT(Float, int, int, int) */
  public void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTimingExposureTime(Float, int, int, int) */
  public void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex);
  }

  // -- Plate property storage -

  /* @see MetadataStore#setPlateDescription(String, int) */
  public void setPlateDescription(String description, int plateIndex) {
    String value = filter ? DataTools.sanitize(description) : description;
    store.setPlateDescription(value, plateIndex);
  }

  /* @see MetadataStore#setPlateExternalIdentifier(String, int) */
  public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex) {
    String value = filter ? DataTools.sanitize(externalIdentifier) : externalIdentifier;
    store.setPlateExternalIdentifier(value, plateIndex);
  }

  /* @see MetadataStore#setPlateID(String, int) */
  public void setPlateID(String id, int plateIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPlateID(value, plateIndex);
  }

  /* @see MetadataStore#setPlateName(String, int) */
  public void setPlateName(String name, int plateIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setPlateName(value, plateIndex);
  }

  /* @see MetadataStore#setPlateStatus(String, int) */
  public void setPlateStatus(String status, int plateIndex) {
    String value = filter ? DataTools.sanitize(status) : status;
    store.setPlateStatus(value, plateIndex);
  }

  // -- PlateRef property storage -

  /* @see MetadataStore#setPlateRefID(String, int, int) */
  public void setPlateRefID(String id, int screenIndex, int plateRefIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPlateRefID(value, screenIndex, plateRefIndex);
  }

  // -- ROI property storage -

  /* @see MetadataStore#setROIID(String, int, int) */
  public void setROIID(String id, int imageIndex, int roiIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setROIID(value, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIT0(Integer, int, int) */
  public void setROIT0(Integer t0, int imageIndex, int roiIndex) {
    store.setROIT0(t0, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIT1(Integer, int, int) */
  public void setROIT1(Integer t1, int imageIndex, int roiIndex) {
    store.setROIT1(t1, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIX0(Integer, int, int) */
  public void setROIX0(Integer x0, int imageIndex, int roiIndex) {
    store.setROIX0(x0, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIX1(Integer, int, int) */
  public void setROIX1(Integer x1, int imageIndex, int roiIndex) {
    store.setROIX1(x1, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIY0(Integer, int, int) */
  public void setROIY0(Integer y0, int imageIndex, int roiIndex) {
    store.setROIY0(y0, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIY1(Integer, int, int) */
  public void setROIY1(Integer y1, int imageIndex, int roiIndex) {
    store.setROIY1(y1, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIZ0(Integer, int, int) */
  public void setROIZ0(Integer z0, int imageIndex, int roiIndex) {
    store.setROIZ0(z0, imageIndex, roiIndex);
  }

  /* @see MetadataStore#setROIZ1(Integer, int, int) */
  public void setROIZ1(Integer z1, int imageIndex, int roiIndex) {
    store.setROIZ1(z1, imageIndex, roiIndex);
  }

  // -- Reagent property storage -

  /* @see MetadataStore#setReagentDescription(String, int, int) */
  public void setReagentDescription(String description, int screenIndex, int reagentIndex) {
    String value = filter ? DataTools.sanitize(description) : description;
    store.setReagentDescription(value, screenIndex, reagentIndex);
  }

  /* @see MetadataStore#setReagentID(String, int, int) */
  public void setReagentID(String id, int screenIndex, int reagentIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setReagentID(value, screenIndex, reagentIndex);
  }

  /* @see MetadataStore#setReagentName(String, int, int) */
  public void setReagentName(String name, int screenIndex, int reagentIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setReagentName(value, screenIndex, reagentIndex);
  }

  /* @see MetadataStore#setReagentReagentIdentifier(String, int, int) */
  public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex) {
    String value = filter ? DataTools.sanitize(reagentIdentifier) : reagentIdentifier;
    store.setReagentReagentIdentifier(value, screenIndex, reagentIndex);
  }

  // -- Screen property storage -

  /* @see MetadataStore#setScreenID(String, int) */
  public void setScreenID(String id, int screenIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setScreenID(value, screenIndex);
  }

  /* @see MetadataStore#setScreenName(String, int) */
  public void setScreenName(String name, int screenIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setScreenName(value, screenIndex);
  }

  /* @see MetadataStore#setScreenProtocolDescription(String, int) */
  public void setScreenProtocolDescription(String protocolDescription, int screenIndex) {
    String value = filter ? DataTools.sanitize(protocolDescription) : protocolDescription;
    store.setScreenProtocolDescription(value, screenIndex);
  }

  /* @see MetadataStore#setScreenProtocolIdentifier(String, int) */
  public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex) {
    String value = filter ? DataTools.sanitize(protocolIdentifier) : protocolIdentifier;
    store.setScreenProtocolIdentifier(value, screenIndex);
  }

  /* @see MetadataStore#setScreenReagentSetDescription(String, int) */
  public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex) {
    String value = filter ? DataTools.sanitize(reagentSetDescription) : reagentSetDescription;
    store.setScreenReagentSetDescription(value, screenIndex);
  }

  /* @see MetadataStore#setScreenType(String, int) */
  public void setScreenType(String type, int screenIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setScreenType(value, screenIndex);
  }

  // -- ScreenAcquisition property storage -

  /* @see MetadataStore#setScreenAcquisitionEndTime(String, int, int) */
  public void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex) {
    String value = filter ? DataTools.sanitize(endTime) : endTime;
    store.setScreenAcquisitionEndTime(value, screenIndex, screenAcquisitionIndex);
  }

  /* @see MetadataStore#setScreenAcquisitionID(String, int, int) */
  public void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setScreenAcquisitionID(value, screenIndex, screenAcquisitionIndex);
  }

  /* @see MetadataStore#setScreenAcquisitionStartTime(String, int, int) */
  public void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex) {
    String value = filter ? DataTools.sanitize(startTime) : startTime;
    store.setScreenAcquisitionStartTime(value, screenIndex, screenAcquisitionIndex);
  }

  // -- StageLabel property storage -

  /* @see MetadataStore#setStageLabelName(String, int) */
  public void setStageLabelName(String name, int imageIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setStageLabelName(value, imageIndex);
  }

  /* @see MetadataStore#setStageLabelX(Float, int) */
  public void setStageLabelX(Float x, int imageIndex) {
    store.setStageLabelX(x, imageIndex);
  }

  /* @see MetadataStore#setStageLabelY(Float, int) */
  public void setStageLabelY(Float y, int imageIndex) {
    store.setStageLabelY(y, imageIndex);
  }

  /* @see MetadataStore#setStageLabelZ(Float, int) */
  public void setStageLabelZ(Float z, int imageIndex) {
    store.setStageLabelZ(z, imageIndex);
  }

  // -- StagePosition property storage -

  /* @see MetadataStore#setStagePositionPositionX(Float, int, int, int) */
  public void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setStagePositionPositionY(Float, int, int, int) */
  public void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setStagePositionPositionZ(Float, int, int, int) */
  public void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex);
  }

  // -- TiffData property storage -

  /* @see MetadataStore#setTiffDataFileName(String, int, int, int) */
  public void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    String value = filter ? DataTools.sanitize(fileName) : fileName;
    store.setTiffDataFileName(value, imageIndex, pixelsIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataFirstC(Integer, int, int, int) */
  public void setTiffDataFirstC(Integer firstC, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    store.setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataFirstT(Integer, int, int, int) */
  public void setTiffDataFirstT(Integer firstT, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    store.setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataFirstZ(Integer, int, int, int) */
  public void setTiffDataFirstZ(Integer firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    store.setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataIFD(Integer, int, int, int) */
  public void setTiffDataIFD(Integer ifd, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    store.setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataNumPlanes(Integer, int, int, int) */
  public void setTiffDataNumPlanes(Integer numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    store.setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex);
  }

  /* @see MetadataStore#setTiffDataUUID(String, int, int, int) */
  public void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    String value = filter ? DataTools.sanitize(uuid) : uuid;
    store.setTiffDataUUID(value, imageIndex, pixelsIndex, tiffDataIndex);
  }

  // -- Well property storage -

  /* @see MetadataStore#setWellColumn(Integer, int, int) */
  public void setWellColumn(Integer column, int plateIndex, int wellIndex) {
    store.setWellColumn(column, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellExternalDescription(String, int, int) */
  public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex) {
    String value = filter ? DataTools.sanitize(externalDescription) : externalDescription;
    store.setWellExternalDescription(value, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellExternalIdentifier(String, int, int) */
  public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex) {
    String value = filter ? DataTools.sanitize(externalIdentifier) : externalIdentifier;
    store.setWellExternalIdentifier(value, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellID(String, int, int) */
  public void setWellID(String id, int plateIndex, int wellIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setWellID(value, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellRow(Integer, int, int) */
  public void setWellRow(Integer row, int plateIndex, int wellIndex) {
    store.setWellRow(row, plateIndex, wellIndex);
  }

  /* @see MetadataStore#setWellType(String, int, int) */
  public void setWellType(String type, int plateIndex, int wellIndex) {
    String value = filter ? DataTools.sanitize(type) : type;
    store.setWellType(value, plateIndex, wellIndex);
  }

  // -- WellSample property storage -

  /* @see MetadataStore#setWellSampleID(String, int, int, int) */
  public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setWellSampleID(value, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSampleIndex(Integer, int, int, int) */
  public void setWellSampleIndex(Integer index, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSamplePosX(Float, int, int, int) */
  public void setWellSamplePosX(Float posX, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSamplePosY(Float, int, int, int) */
  public void setWellSamplePosY(Float posY, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSampleTimepoint(Integer, int, int, int) */
  public void setWellSampleTimepoint(Integer timepoint, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex);
  }

}
