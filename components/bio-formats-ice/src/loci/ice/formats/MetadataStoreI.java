//
// MetadataStoreI.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
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
 * Created by curtis via MetadataAutogen on Oct 24, 2008 8:37:23 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.ice.formats;

import Ice.Current;
//import loci.ice.formats._MetadataStoreDisp;
//import loci.ice.formats.MetadataStore;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

/**
 * Server-side Ice wrapper for client/server
 * {@link loci.formats.meta.MetadataStore} objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats-ice/src/loci/ice/formats/MetadataStoreI.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats-ice/src/loci/ice/formats/MetadataStoreI.java">SVN</a></dd></dl>
 *
 * @author Hidayath Ansari mansari at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MetadataStoreI extends _MetadataStoreDisp {

  // -- Fields --

  private IMetadata metadataObject;

  // -- Constructors --

  public MetadataStoreI() {
    metadataObject = MetadataTools.createOMEXMLMetadata();
  }

  public MetadataStoreI(loci.formats.meta.MetadataStore store) {
    metadataObject = (IMetadata) store;
  }

  // -- MetadataStoreI methods --

  public loci.formats.meta.MetadataStore getWrappedObject() {
    return metadataObject;
  }

  public void setMetadataObject(loci.formats.meta.MetadataStore store) {
    metadataObject = (IMetadata) store;
  }

  // -- MetadataStore methods --

  public MetadataStore getServant(Current current) {
    return this;
  }

  public String getOMEXML(Current current) {
    return MetadataTools.getOMEXML(
      (loci.formats.meta.MetadataRetrieve) metadataObject);
  }

  public void createRoot(Current current) {
    metadataObject.createRoot();
  }

  // - Entity storage -

  public void setUUID(String uuid, Current current) {
    metadataObject.setUUID(uuid);
  }

  // - Arc property storage -

  public void setArcType(String type, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setArcType(type, instrumentIndex, lightSourceIndex);
  }

  // - ChannelComponent property storage -

  public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex, Current current) {
    metadataObject.setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  public void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex, Current current) {
    metadataObject.setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  // - Detector property storage -

  public void setDetectorGain(float gain, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorGain(gain, instrumentIndex, detectorIndex);
  }

  public void setDetectorID(String id, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorID(id, instrumentIndex, detectorIndex);
  }

  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex);
  }

  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorModel(model, instrumentIndex, detectorIndex);
  }

  public void setDetectorOffset(float offset, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorOffset(offset, instrumentIndex, detectorIndex);
  }

  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex);
  }

  public void setDetectorType(String type, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorType(type, instrumentIndex, detectorIndex);
  }

  public void setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex, Current current) {
    metadataObject.setDetectorVoltage(voltage, instrumentIndex, detectorIndex);
  }

  // - DetectorSettings property storage -

  public void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex);
  }

  // - Dimensions property storage -

  public void setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex);
  }

  public void setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex);
  }

  public void setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex);
  }

  public void setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex);
  }

  public void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex);
  }

  public void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex);
  }

  // - DisplayOptions property storage -

  public void setDisplayOptionsID(String id, int imageIndex, Current current) {
    metadataObject.setDisplayOptionsID(id, imageIndex);
  }

  public void setDisplayOptionsZoom(float zoom, int imageIndex, Current current) {
    metadataObject.setDisplayOptionsZoom(zoom, imageIndex);
  }

  // - DisplayOptionsProjection property storage -

  public void setDisplayOptionsProjectionZStart(int zStart, int imageIndex, Current current) {
    metadataObject.setDisplayOptionsProjectionZStart(zStart, imageIndex);
  }

  public void setDisplayOptionsProjectionZStop(int zStop, int imageIndex, Current current) {
    metadataObject.setDisplayOptionsProjectionZStop(zStop, imageIndex);
  }

  // - DisplayOptionsTime property storage -

  public void setDisplayOptionsTimeTStart(int tStart, int imageIndex, Current current) {
    metadataObject.setDisplayOptionsTimeTStart(tStart, imageIndex);
  }

  public void setDisplayOptionsTimeTStop(int tStop, int imageIndex, Current current) {
    metadataObject.setDisplayOptionsTimeTStop(tStop, imageIndex);
  }

  // - Experiment property storage -

  public void setExperimentDescription(String description, int experimentIndex, Current current) {
    metadataObject.setExperimentDescription(description, experimentIndex);
  }

  public void setExperimentID(String id, int experimentIndex, Current current) {
    metadataObject.setExperimentID(id, experimentIndex);
  }

  public void setExperimentType(String type, int experimentIndex, Current current) {
    metadataObject.setExperimentType(type, experimentIndex);
  }

  // - Experimenter property storage -

  public void setExperimenterEmail(String email, int experimenterIndex, Current current) {
    metadataObject.setExperimenterEmail(email, experimenterIndex);
  }

  public void setExperimenterFirstName(String firstName, int experimenterIndex, Current current) {
    metadataObject.setExperimenterFirstName(firstName, experimenterIndex);
  }

  public void setExperimenterID(String id, int experimenterIndex, Current current) {
    metadataObject.setExperimenterID(id, experimenterIndex);
  }

  public void setExperimenterInstitution(String institution, int experimenterIndex, Current current) {
    metadataObject.setExperimenterInstitution(institution, experimenterIndex);
  }

  public void setExperimenterLastName(String lastName, int experimenterIndex, Current current) {
    metadataObject.setExperimenterLastName(lastName, experimenterIndex);
  }

  // - ExperimenterMembership property storage -

  public void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex, Current current) {
    metadataObject.setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex);
  }

  // - Filament property storage -

  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setFilamentType(type, instrumentIndex, lightSourceIndex);
  }

  // - GroupRef property storage -

  // - Image property storage -

  public void setImageCreationDate(String creationDate, int imageIndex, Current current) {
    metadataObject.setImageCreationDate(creationDate, imageIndex);
  }

  public void setImageDefaultPixels(String defaultPixels, int imageIndex, Current current) {
    metadataObject.setImageDefaultPixels(defaultPixels, imageIndex);
  }

  public void setImageDescription(String description, int imageIndex, Current current) {
    metadataObject.setImageDescription(description, imageIndex);
  }

  public void setImageID(String id, int imageIndex, Current current) {
    metadataObject.setImageID(id, imageIndex);
  }

  public void setImageInstrumentRef(String instrumentRef, int imageIndex, Current current) {
    metadataObject.setImageInstrumentRef(instrumentRef, imageIndex);
  }

  public void setImageName(String name, int imageIndex, Current current) {
    metadataObject.setImageName(name, imageIndex);
  }

  // - ImagingEnvironment property storage -

  public void setImagingEnvironmentAirPressure(float airPressure, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentAirPressure(airPressure, imageIndex);
  }

  public void setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex);
  }

  public void setImagingEnvironmentHumidity(float humidity, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentHumidity(humidity, imageIndex);
  }

  public void setImagingEnvironmentTemperature(float temperature, int imageIndex, Current current) {
    metadataObject.setImagingEnvironmentTemperature(temperature, imageIndex);
  }

  // - Instrument property storage -

  public void setInstrumentID(String id, int instrumentIndex, Current current) {
    metadataObject.setInstrumentID(id, instrumentIndex);
  }

  // - Laser property storage -

  public void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex);
  }

  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex);
  }

  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserPulse(pulse, instrumentIndex, lightSourceIndex);
  }

  public void setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex);
  }

  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserType(type, instrumentIndex, lightSourceIndex);
  }

  public void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex);
  }

  // - LightSource property storage -

  public void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLightSourceID(id, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLightSourceModel(model, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLightSourcePower(power, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, Current current) {
    metadataObject.setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex);
  }

  // - LightSourceSettings property storage -

  public void setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex);
  }

  public void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex);
  }

  public void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex);
  }

  // - LogicalChannel property storage -

  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelID(id, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelMode(mode, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelName(name, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex, Current current) {
    metadataObject.setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex);
  }

  // - OTF property storage -

  public void setOTFID(String id, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFID(id, instrumentIndex, otfIndex);
  }

  public void setOTFObjective(String objective, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFObjective(objective, instrumentIndex, otfIndex);
  }

  public void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex);
  }

  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFPixelType(pixelType, instrumentIndex, otfIndex);
  }

  public void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFSizeX(sizeX, instrumentIndex, otfIndex);
  }

  public void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex, Current current) {
    metadataObject.setOTFSizeY(sizeY, instrumentIndex, otfIndex);
  }

  // - Objective property storage -

  public void setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveCorrection(correction, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveID(id, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveModel(model, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex, Current current) {
    metadataObject.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex);
  }

  // - Pixels property storage -

  public void setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex);
  }

  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex);
  }

  public void setPixelsID(String id, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsID(id, imageIndex, pixelsIndex);
  }

  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsPixelType(pixelType, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsSizeC(sizeC, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsSizeT(sizeT, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsSizeX(sizeX, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsSizeY(sizeY, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex, Current current) {
    metadataObject.setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex);
  }

  // - Plane property storage -

  public void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex);
  }

  public void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex);
  }

  public void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex);
  }

  // - PlaneTiming property storage -

  public void setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex);
  }

  public void setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex);
  }

  // - Plate property storage -

  public void setPlateDescription(String description, int plateIndex, Current current) {
    metadataObject.setPlateDescription(description, plateIndex);
  }

  public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex, Current current) {
    metadataObject.setPlateExternalIdentifier(externalIdentifier, plateIndex);
  }

  public void setPlateID(String id, int plateIndex, Current current) {
    metadataObject.setPlateID(id, plateIndex);
  }

  public void setPlateName(String name, int plateIndex, Current current) {
    metadataObject.setPlateName(name, plateIndex);
  }

  public void setPlateStatus(String status, int plateIndex, Current current) {
    metadataObject.setPlateStatus(status, plateIndex);
  }

  // - PlateRef property storage -

  public void setPlateRefID(String id, int screenIndex, int plateRefIndex, Current current) {
    metadataObject.setPlateRefID(id, screenIndex, plateRefIndex);
  }

  // - ROI property storage -

  public void setROIID(String id, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIID(id, imageIndex, roiIndex);
  }

  public void setROIT0(int t0, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIT0(t0, imageIndex, roiIndex);
  }

  public void setROIT1(int t1, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIT1(t1, imageIndex, roiIndex);
  }

  public void setROIX0(int x0, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIX0(x0, imageIndex, roiIndex);
  }

  public void setROIX1(int x1, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIX1(x1, imageIndex, roiIndex);
  }

  public void setROIY0(int y0, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIY0(y0, imageIndex, roiIndex);
  }

  public void setROIY1(int y1, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIY1(y1, imageIndex, roiIndex);
  }

  public void setROIZ0(int z0, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIZ0(z0, imageIndex, roiIndex);
  }

  public void setROIZ1(int z1, int imageIndex, int roiIndex, Current current) {
    metadataObject.setROIZ1(z1, imageIndex, roiIndex);
  }

  // - Reagent property storage -

  public void setReagentDescription(String description, int screenIndex, int reagentIndex, Current current) {
    metadataObject.setReagentDescription(description, screenIndex, reagentIndex);
  }

  public void setReagentID(String id, int screenIndex, int reagentIndex, Current current) {
    metadataObject.setReagentID(id, screenIndex, reagentIndex);
  }

  public void setReagentName(String name, int screenIndex, int reagentIndex, Current current) {
    metadataObject.setReagentName(name, screenIndex, reagentIndex);
  }

  public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex, Current current) {
    metadataObject.setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex);
  }

  // - Screen property storage -

  public void setScreenID(String id, int screenIndex, Current current) {
    metadataObject.setScreenID(id, screenIndex);
  }

  public void setScreenName(String name, int screenIndex, Current current) {
    metadataObject.setScreenName(name, screenIndex);
  }

  public void setScreenProtocolDescription(String protocolDescription, int screenIndex, Current current) {
    metadataObject.setScreenProtocolDescription(protocolDescription, screenIndex);
  }

  public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex, Current current) {
    metadataObject.setScreenProtocolIdentifier(protocolIdentifier, screenIndex);
  }

  public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex, Current current) {
    metadataObject.setScreenReagentSetDescription(reagentSetDescription, screenIndex);
  }

  public void setScreenType(String type, int screenIndex, Current current) {
    metadataObject.setScreenType(type, screenIndex);
  }

  // - ScreenAcquisition property storage -

  public void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex, Current current) {
    metadataObject.setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex);
  }

  public void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex, Current current) {
    metadataObject.setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex);
  }

  public void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex, Current current) {
    metadataObject.setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex);
  }

  // - StageLabel property storage -

  public void setStageLabelName(String name, int imageIndex, Current current) {
    metadataObject.setStageLabelName(name, imageIndex);
  }

  public void setStageLabelX(float x, int imageIndex, Current current) {
    metadataObject.setStageLabelX(x, imageIndex);
  }

  public void setStageLabelY(float y, int imageIndex, Current current) {
    metadataObject.setStageLabelY(y, imageIndex);
  }

  public void setStageLabelZ(float z, int imageIndex, Current current) {
    metadataObject.setStageLabelZ(z, imageIndex);
  }

  // - StagePosition property storage -

  public void setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex);
  }

  public void setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex);
  }

  public void setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex, Current current) {
    metadataObject.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex);
  }

  // - TiffData property storage -

  public void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex, Current current) {
    metadataObject.setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex);
  }

  // - Well property storage -

  public void setWellColumn(int column, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellColumn(column, plateIndex, wellIndex);
  }

  public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellExternalDescription(externalDescription, plateIndex, wellIndex);
  }

  public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex);
  }

  public void setWellID(String id, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellID(id, plateIndex, wellIndex);
  }

  public void setWellRow(int row, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellRow(row, plateIndex, wellIndex);
  }

  public void setWellType(String type, int plateIndex, int wellIndex, Current current) {
    metadataObject.setWellType(type, plateIndex, wellIndex);
  }

  // - WellSample property storage -

  public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex, Current current) {
    metadataObject.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex);
  }

}
