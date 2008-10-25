//
// MetadataStoreI.java
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
 * Created by curtis via MetadataAutogen on Oct 24, 2008 7:57:38 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.ice.formats;

//import loci.ice.formats._MetadataStoreDisp;
//import loci.ice.formats.MetadataStore;
import loci.formats.*;
import loci.formats.meta.*;

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

  private MetadataStore metadataObject;

  // -- Constructors --

  public MetadataStoreI() {
    metadataObject = MetadataTools.createOMEXMLMetadata();
  }

  public MetadataStoreI(MetadataStore store) {
    metadataObject = store;
  }

  // -- MetadataStoreI methods --

  public MetadataStore getWrappedObject() {
    return metadataObject;
  }

  public void setMetadataObject(MetadataStore store) {
    metadataObject = store;
  }

  // -- _MetadataStoreDisp methods --

  public MetadataStore getServant(Ice.Current current) {
    return this;
  }

  public String getOMEXML(Ice.Current current) {
    return MetadataTools.getOMEXML((MetadataRetrieve) metadataObject);
  }

  public void createRoot(Ice.Current current) {
    metadataObject.createRoot();
  }

  // - Entity storage -

  public void setUUID(String uuid, Ice.Current current) {
    metadataObject.setUUID(uuid);
  }

  // - Arc property storage -

  public void setArcType(String type, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setArcType(type, instrumentIndex, lightSourceIndex);
  }

  // - ChannelComponent property storage -

  public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current current) {
    metadataObject.setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  public void setChannelComponentIndex(Integer index, int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current current) {
    metadataObject.setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  // - Detector property storage -

  public void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex, Ice.Current current) {
    metadataObject.setDetectorGain(gain, instrumentIndex, detectorIndex);
  }

  public void setDetectorID(String id, int instrumentIndex, int detectorIndex, Ice.Current current) {
    metadataObject.setDetectorID(id, instrumentIndex, detectorIndex);
  }

  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex, Ice.Current current) {
    metadataObject.setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex);
  }

  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex, Ice.Current current) {
    metadataObject.setDetectorModel(model, instrumentIndex, detectorIndex);
  }

  public void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex, Ice.Current current) {
    metadataObject.setDetectorOffset(offset, instrumentIndex, detectorIndex);
  }

  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, Ice.Current current) {
    metadataObject.setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex);
  }

  public void setDetectorType(String type, int instrumentIndex, int detectorIndex, Ice.Current current) {
    metadataObject.setDetectorType(type, instrumentIndex, detectorIndex);
  }

  public void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex, Ice.Current current) {
    metadataObject.setDetectorVoltage(voltage, instrumentIndex, detectorIndex);
  }

  // - DetectorSettings property storage -

  public void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex);
  }

  public void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex);
  }

  // - Dimensions property storage -

  public void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex);
  }

  public void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex);
  }

  public void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex);
  }

  public void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex);
  }

  public void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex);
  }

  public void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex);
  }

  // - DisplayOptions property storage -

  public void setDisplayOptionsID(String id, int imageIndex, Ice.Current current) {
    metadataObject.setDisplayOptionsID(id, imageIndex);
  }

  public void setDisplayOptionsZoom(Float zoom, int imageIndex, Ice.Current current) {
    metadataObject.setDisplayOptionsZoom(zoom, imageIndex);
  }

  // - DisplayOptionsProjection property storage -

  public void setDisplayOptionsProjectionZStart(Integer zStart, int imageIndex, Ice.Current current) {
    metadataObject.setDisplayOptionsProjectionZStart(zStart, imageIndex);
  }

  public void setDisplayOptionsProjectionZStop(Integer zStop, int imageIndex, Ice.Current current) {
    metadataObject.setDisplayOptionsProjectionZStop(zStop, imageIndex);
  }

  // - DisplayOptionsTime property storage -

  public void setDisplayOptionsTimeTStart(Integer tStart, int imageIndex, Ice.Current current) {
    metadataObject.setDisplayOptionsTimeTStart(tStart, imageIndex);
  }

  public void setDisplayOptionsTimeTStop(Integer tStop, int imageIndex, Ice.Current current) {
    metadataObject.setDisplayOptionsTimeTStop(tStop, imageIndex);
  }

  // - Experiment property storage -

  public void setExperimentDescription(String description, int experimentIndex, Ice.Current current) {
    metadataObject.setExperimentDescription(description, experimentIndex);
  }

  public void setExperimentID(String id, int experimentIndex, Ice.Current current) {
    metadataObject.setExperimentID(id, experimentIndex);
  }

  public void setExperimentType(String type, int experimentIndex, Ice.Current current) {
    metadataObject.setExperimentType(type, experimentIndex);
  }

  // - Experimenter property storage -

  public void setExperimenterEmail(String email, int experimenterIndex, Ice.Current current) {
    metadataObject.setExperimenterEmail(email, experimenterIndex);
  }

  public void setExperimenterFirstName(String firstName, int experimenterIndex, Ice.Current current) {
    metadataObject.setExperimenterFirstName(firstName, experimenterIndex);
  }

  public void setExperimenterID(String id, int experimenterIndex, Ice.Current current) {
    metadataObject.setExperimenterID(id, experimenterIndex);
  }

  public void setExperimenterInstitution(String institution, int experimenterIndex, Ice.Current current) {
    metadataObject.setExperimenterInstitution(institution, experimenterIndex);
  }

  public void setExperimenterLastName(String lastName, int experimenterIndex, Ice.Current current) {
    metadataObject.setExperimenterLastName(lastName, experimenterIndex);
  }

  // - ExperimenterMembership property storage -

  public void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex, Ice.Current current) {
    metadataObject.setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex);
  }

  // - Filament property storage -

  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setFilamentType(type, instrumentIndex, lightSourceIndex);
  }

  // - GroupRef property storage -

  // - Image property storage -

  public void setImageCreationDate(String creationDate, int imageIndex, Ice.Current current) {
    metadataObject.setImageCreationDate(creationDate, imageIndex);
  }

  public void setImageDefaultPixels(String defaultPixels, int imageIndex, Ice.Current current) {
    metadataObject.setImageDefaultPixels(defaultPixels, imageIndex);
  }

  public void setImageDescription(String description, int imageIndex, Ice.Current current) {
    metadataObject.setImageDescription(description, imageIndex);
  }

  public void setImageID(String id, int imageIndex, Ice.Current current) {
    metadataObject.setImageID(id, imageIndex);
  }

  public void setImageInstrumentRef(String instrumentRef, int imageIndex, Ice.Current current) {
    metadataObject.setImageInstrumentRef(instrumentRef, imageIndex);
  }

  public void setImageName(String name, int imageIndex, Ice.Current current) {
    metadataObject.setImageName(name, imageIndex);
  }

  // - ImagingEnvironment property storage -

  public void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex, Ice.Current current) {
    metadataObject.setImagingEnvironmentAirPressure(airPressure, imageIndex);
  }

  public void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex, Ice.Current current) {
    metadataObject.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex);
  }

  public void setImagingEnvironmentHumidity(Float humidity, int imageIndex, Ice.Current current) {
    metadataObject.setImagingEnvironmentHumidity(humidity, imageIndex);
  }

  public void setImagingEnvironmentTemperature(Float temperature, int imageIndex, Ice.Current current) {
    metadataObject.setImagingEnvironmentTemperature(temperature, imageIndex);
  }

  // - Instrument property storage -

  public void setInstrumentID(String id, int instrumentIndex, Ice.Current current) {
    metadataObject.setInstrumentID(id, instrumentIndex);
  }

  // - Laser property storage -

  public void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex);
  }

  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex);
  }

  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setLaserPulse(pulse, instrumentIndex, lightSourceIndex);
  }

  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex);
  }

  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setLaserType(type, instrumentIndex, lightSourceIndex);
  }

  public void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex);
  }

  // - LightSource property storage -

  public void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setLightSourceID(id, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setLightSourceModel(model, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourcePower(Float power, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setLightSourcePower(power, instrumentIndex, lightSourceIndex);
  }

  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    metadataObject.setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex);
  }

  // - LightSourceSettings property storage -

  public void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex);
  }

  public void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex);
  }

  public void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex);
  }

  // - LogicalChannel property storage -

  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelID(id, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelMode(mode, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelName(name, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelPinholeSize(Float pinholeSize, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex);
  }

  public void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex, Ice.Current current) {
    metadataObject.setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex);
  }

  // - OTF property storage -

  public void setOTFID(String id, int instrumentIndex, int otfIndex, Ice.Current current) {
    metadataObject.setOTFID(id, instrumentIndex, otfIndex);
  }

  public void setOTFObjective(String objective, int instrumentIndex, int otfIndex, Ice.Current current) {
    metadataObject.setOTFObjective(objective, instrumentIndex, otfIndex);
  }

  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex, Ice.Current current) {
    metadataObject.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex);
  }

  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex, Ice.Current current) {
    metadataObject.setOTFPixelType(pixelType, instrumentIndex, otfIndex);
  }

  public void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex, Ice.Current current) {
    metadataObject.setOTFSizeX(sizeX, instrumentIndex, otfIndex);
  }

  public void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex, Ice.Current current) {
    metadataObject.setOTFSizeY(sizeY, instrumentIndex, otfIndex);
  }

  // - Objective property storage -

  public void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex, Ice.Current current) {
    metadataObject.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex, Ice.Current current) {
    metadataObject.setObjectiveCorrection(correction, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex, Ice.Current current) {
    metadataObject.setObjectiveID(id, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex, Ice.Current current) {
    metadataObject.setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex, Ice.Current current) {
    metadataObject.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex, Ice.Current current) {
    metadataObject.setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex, Ice.Current current) {
    metadataObject.setObjectiveModel(model, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex, Ice.Current current) {
    metadataObject.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex, Ice.Current current) {
    metadataObject.setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex);
  }

  public void setObjectiveWorkingDistance(Float workingDistance, int instrumentIndex, int objectiveIndex, Ice.Current current) {
    metadataObject.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex);
  }

  // - Pixels property storage -

  public void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex);
  }

  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex);
  }

  public void setPixelsID(String id, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setPixelsID(id, imageIndex, pixelsIndex);
  }

  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setPixelsPixelType(pixelType, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setPixelsSizeC(sizeC, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setPixelsSizeT(sizeT, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setPixelsSizeX(sizeX, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setPixelsSizeY(sizeY, imageIndex, pixelsIndex);
  }

  public void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex, Ice.Current current) {
    metadataObject.setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex);
  }

  // - Plane property storage -

  public void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    metadataObject.setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex);
  }

  public void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    metadataObject.setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex);
  }

  public void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    metadataObject.setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex);
  }

  // - PlaneTiming property storage -

  public void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    metadataObject.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex);
  }

  public void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    metadataObject.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex);
  }

  // - Plate property storage -

  public void setPlateDescription(String description, int plateIndex, Ice.Current current) {
    metadataObject.setPlateDescription(description, plateIndex);
  }

  public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex, Ice.Current current) {
    metadataObject.setPlateExternalIdentifier(externalIdentifier, plateIndex);
  }

  public void setPlateID(String id, int plateIndex, Ice.Current current) {
    metadataObject.setPlateID(id, plateIndex);
  }

  public void setPlateName(String name, int plateIndex, Ice.Current current) {
    metadataObject.setPlateName(name, plateIndex);
  }

  public void setPlateStatus(String status, int plateIndex, Ice.Current current) {
    metadataObject.setPlateStatus(status, plateIndex);
  }

  // - PlateRef property storage -

  public void setPlateRefID(String id, int screenIndex, int plateRefIndex, Ice.Current current) {
    metadataObject.setPlateRefID(id, screenIndex, plateRefIndex);
  }

  // - ROI property storage -

  public void setROIID(String id, int imageIndex, int roiIndex, Ice.Current current) {
    metadataObject.setROIID(id, imageIndex, roiIndex);
  }

  public void setROIT0(Integer t0, int imageIndex, int roiIndex, Ice.Current current) {
    metadataObject.setROIT0(t0, imageIndex, roiIndex);
  }

  public void setROIT1(Integer t1, int imageIndex, int roiIndex, Ice.Current current) {
    metadataObject.setROIT1(t1, imageIndex, roiIndex);
  }

  public void setROIX0(Integer x0, int imageIndex, int roiIndex, Ice.Current current) {
    metadataObject.setROIX0(x0, imageIndex, roiIndex);
  }

  public void setROIX1(Integer x1, int imageIndex, int roiIndex, Ice.Current current) {
    metadataObject.setROIX1(x1, imageIndex, roiIndex);
  }

  public void setROIY0(Integer y0, int imageIndex, int roiIndex, Ice.Current current) {
    metadataObject.setROIY0(y0, imageIndex, roiIndex);
  }

  public void setROIY1(Integer y1, int imageIndex, int roiIndex, Ice.Current current) {
    metadataObject.setROIY1(y1, imageIndex, roiIndex);
  }

  public void setROIZ0(Integer z0, int imageIndex, int roiIndex, Ice.Current current) {
    metadataObject.setROIZ0(z0, imageIndex, roiIndex);
  }

  public void setROIZ1(Integer z1, int imageIndex, int roiIndex, Ice.Current current) {
    metadataObject.setROIZ1(z1, imageIndex, roiIndex);
  }

  // - Reagent property storage -

  public void setReagentDescription(String description, int screenIndex, int reagentIndex, Ice.Current current) {
    metadataObject.setReagentDescription(description, screenIndex, reagentIndex);
  }

  public void setReagentID(String id, int screenIndex, int reagentIndex, Ice.Current current) {
    metadataObject.setReagentID(id, screenIndex, reagentIndex);
  }

  public void setReagentName(String name, int screenIndex, int reagentIndex, Ice.Current current) {
    metadataObject.setReagentName(name, screenIndex, reagentIndex);
  }

  public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex, Ice.Current current) {
    metadataObject.setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex);
  }

  // - Screen property storage -

  public void setScreenID(String id, int screenIndex, Ice.Current current) {
    metadataObject.setScreenID(id, screenIndex);
  }

  public void setScreenName(String name, int screenIndex, Ice.Current current) {
    metadataObject.setScreenName(name, screenIndex);
  }

  public void setScreenProtocolDescription(String protocolDescription, int screenIndex, Ice.Current current) {
    metadataObject.setScreenProtocolDescription(protocolDescription, screenIndex);
  }

  public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex, Ice.Current current) {
    metadataObject.setScreenProtocolIdentifier(protocolIdentifier, screenIndex);
  }

  public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex, Ice.Current current) {
    metadataObject.setScreenReagentSetDescription(reagentSetDescription, screenIndex);
  }

  public void setScreenType(String type, int screenIndex, Ice.Current current) {
    metadataObject.setScreenType(type, screenIndex);
  }

  // - ScreenAcquisition property storage -

  public void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex, Ice.Current current) {
    metadataObject.setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex);
  }

  public void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex, Ice.Current current) {
    metadataObject.setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex);
  }

  public void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex, Ice.Current current) {
    metadataObject.setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex);
  }

  // - StageLabel property storage -

  public void setStageLabelName(String name, int imageIndex, Ice.Current current) {
    metadataObject.setStageLabelName(name, imageIndex);
  }

  public void setStageLabelX(Float x, int imageIndex, Ice.Current current) {
    metadataObject.setStageLabelX(x, imageIndex);
  }

  public void setStageLabelY(Float y, int imageIndex, Ice.Current current) {
    metadataObject.setStageLabelY(y, imageIndex);
  }

  public void setStageLabelZ(Float z, int imageIndex, Ice.Current current) {
    metadataObject.setStageLabelZ(z, imageIndex);
  }

  // - StagePosition property storage -

  public void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    metadataObject.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex);
  }

  public void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    metadataObject.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex);
  }

  public void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    metadataObject.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex);
  }

  // - TiffData property storage -

  public void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    metadataObject.setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataFirstC(Integer firstC, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    metadataObject.setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataFirstT(Integer firstT, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    metadataObject.setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataFirstZ(Integer firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    metadataObject.setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataIFD(Integer ifd, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    metadataObject.setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataNumPlanes(Integer numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    metadataObject.setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex);
  }

  public void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    metadataObject.setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex);
  }

  // - Well property storage -

  public void setWellColumn(Integer column, int plateIndex, int wellIndex, Ice.Current current) {
    metadataObject.setWellColumn(column, plateIndex, wellIndex);
  }

  public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex, Ice.Current current) {
    metadataObject.setWellExternalDescription(externalDescription, plateIndex, wellIndex);
  }

  public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex, Ice.Current current) {
    metadataObject.setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex);
  }

  public void setWellID(String id, int plateIndex, int wellIndex, Ice.Current current) {
    metadataObject.setWellID(id, plateIndex, wellIndex);
  }

  public void setWellRow(Integer row, int plateIndex, int wellIndex, Ice.Current current) {
    metadataObject.setWellRow(row, plateIndex, wellIndex);
  }

  public void setWellType(String type, int plateIndex, int wellIndex, Ice.Current current) {
    metadataObject.setWellType(type, plateIndex, wellIndex);
  }

  // - WellSample property storage -

  public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current current) {
    metadataObject.setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSampleIndex(Integer index, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current current) {
    metadataObject.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSamplePosX(Float posX, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current current) {
    metadataObject.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSamplePosY(Float posY, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current current) {
    metadataObject.setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex);
  }

  public void setWellSampleTimepoint(Integer timepoint, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current current) {
    metadataObject.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex);
  }

}
