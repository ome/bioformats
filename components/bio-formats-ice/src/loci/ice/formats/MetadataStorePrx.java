// **********************************************************************
//
// Copyright (c) 2003-2007 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.2.1

package loci.ice.formats;

public interface MetadataStorePrx extends Ice.ObjectPrx
{
    public MetadataStore getServant();
    public MetadataStore getServant(java.util.Map<String, String> __ctx);

    public String getOMEXML();
    public String getOMEXML(java.util.Map<String, String> __ctx);

    public void createRoot();
    public void createRoot(java.util.Map<String, String> __ctx);

    public void setUUID(String uuid);
    public void setUUID(String uuid, java.util.Map<String, String> __ctx);

    public void setArcType(String type, int instrumentIndex, int lightSourceIndex);
    public void setArcType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx);

    public void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    public void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx);

    public void setDetectorGain(float gain, int instrumentIndex, int detectorIndex);
    public void setDetectorGain(float gain, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorID(String id, int instrumentIndex, int detectorIndex);
    public void setDetectorID(String id, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex);
    public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorModel(String model, int instrumentIndex, int detectorIndex);
    public void setDetectorModel(String model, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorOffset(float offset, int instrumentIndex, int detectorIndex);
    public void setDetectorOffset(float offset, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex);
    public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorType(String type, int instrumentIndex, int detectorIndex);
    public void setDetectorType(String type, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex);
    public void setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex);
    public void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex);
    public void setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex);
    public void setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex);
    public void setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex);
    public void setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex);
    public void setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex);
    public void setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex);
    public void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex);
    public void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setDisplayOptionsID(String id, int imageIndex);
    public void setDisplayOptionsID(String id, int imageIndex, java.util.Map<String, String> __ctx);

    public void setDisplayOptionsZoom(float zoom, int imageIndex);
    public void setDisplayOptionsZoom(float zoom, int imageIndex, java.util.Map<String, String> __ctx);

    public void setDisplayOptionsProjectionZStart(int zStart, int imageIndex);
    public void setDisplayOptionsProjectionZStart(int zStart, int imageIndex, java.util.Map<String, String> __ctx);

    public void setDisplayOptionsProjectionZStop(int zStop, int imageIndex);
    public void setDisplayOptionsProjectionZStop(int zStop, int imageIndex, java.util.Map<String, String> __ctx);

    public void setDisplayOptionsTimeTStart(int tStart, int imageIndex);
    public void setDisplayOptionsTimeTStart(int tStart, int imageIndex, java.util.Map<String, String> __ctx);

    public void setDisplayOptionsTimeTStop(int tStop, int imageIndex);
    public void setDisplayOptionsTimeTStop(int tStop, int imageIndex, java.util.Map<String, String> __ctx);

    public void setExperimentDescription(String description, int experimentIndex);
    public void setExperimentDescription(String description, int experimentIndex, java.util.Map<String, String> __ctx);

    public void setExperimentID(String id, int experimentIndex);
    public void setExperimentID(String id, int experimentIndex, java.util.Map<String, String> __ctx);

    public void setExperimentType(String type, int experimentIndex);
    public void setExperimentType(String type, int experimentIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterEmail(String email, int experimenterIndex);
    public void setExperimenterEmail(String email, int experimenterIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterFirstName(String firstName, int experimenterIndex);
    public void setExperimenterFirstName(String firstName, int experimenterIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterID(String id, int experimenterIndex);
    public void setExperimenterID(String id, int experimenterIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterInstitution(String institution, int experimenterIndex);
    public void setExperimenterInstitution(String institution, int experimenterIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterLastName(String lastName, int experimenterIndex);
    public void setExperimenterLastName(String lastName, int experimenterIndex, java.util.Map<String, String> __ctx);

    public void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex);
    public void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx);

    public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex);
    public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setImageCreationDate(String creationDate, int imageIndex);
    public void setImageCreationDate(String creationDate, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageDefaultPixels(String defaultPixels, int imageIndex);
    public void setImageDefaultPixels(String defaultPixels, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageDescription(String description, int imageIndex);
    public void setImageDescription(String description, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageID(String id, int imageIndex);
    public void setImageID(String id, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageInstrumentRef(String instrumentRef, int imageIndex);
    public void setImageInstrumentRef(String instrumentRef, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImageName(String name, int imageIndex);
    public void setImageName(String name, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImagingEnvironmentAirPressure(float airPressure, int imageIndex);
    public void setImagingEnvironmentAirPressure(float airPressure, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex);
    public void setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImagingEnvironmentHumidity(float humidity, int imageIndex);
    public void setImagingEnvironmentHumidity(float humidity, int imageIndex, java.util.Map<String, String> __ctx);

    public void setImagingEnvironmentTemperature(float temperature, int imageIndex);
    public void setImagingEnvironmentTemperature(float temperature, int imageIndex, java.util.Map<String, String> __ctx);

    public void setInstrumentID(String id, int instrumentIndex);
    public void setInstrumentID(String id, int instrumentIndex, java.util.Map<String, String> __ctx);

    public void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex);
    public void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex);
    public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex);
    public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex);
    public void setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLaserType(String type, int instrumentIndex, int lightSourceIndex);
    public void setLaserType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex);
    public void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex);
    public void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);
    public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex);
    public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex);
    public void setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);
    public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex);
    public void setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex);
    public void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex);
    public void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex);
    public void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public void setOTFID(String id, int instrumentIndex, int otfIndex);
    public void setOTFID(String id, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setOTFObjective(String objective, int instrumentIndex, int otfIndex);
    public void setOTFObjective(String objective, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex);
    public void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex);
    public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex);
    public void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex);
    public void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex);
    public void setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex);
    public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex);
    public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex);
    public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex);
    public void setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex);
    public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex);
    public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex);
    public void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex);
    public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex);
    public void setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public void setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex);
    public void setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex);
    public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsID(String id, int imageIndex, int pixelsIndex);
    public void setPixelsID(String id, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex);
    public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex);
    public void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex);
    public void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex);
    public void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex);
    public void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex);
    public void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex);
    public void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex);
    public void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex);
    public void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex);
    public void setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex);
    public void setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setPlateDescription(String description, int plateIndex);
    public void setPlateDescription(String description, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex);
    public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateID(String id, int plateIndex);
    public void setPlateID(String id, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateName(String name, int plateIndex);
    public void setPlateName(String name, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateStatus(String status, int plateIndex);
    public void setPlateStatus(String status, int plateIndex, java.util.Map<String, String> __ctx);

    public void setPlateRefID(String id, int screenIndex, int plateRefIndex);
    public void setPlateRefID(String id, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx);

    public void setROIID(String id, int imageIndex, int roiIndex);
    public void setROIID(String id, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIT0(int t0, int imageIndex, int roiIndex);
    public void setROIT0(int t0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIT1(int t1, int imageIndex, int roiIndex);
    public void setROIT1(int t1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIX0(int x0, int imageIndex, int roiIndex);
    public void setROIX0(int x0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIX1(int x1, int imageIndex, int roiIndex);
    public void setROIX1(int x1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIY0(int y0, int imageIndex, int roiIndex);
    public void setROIY0(int y0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIY1(int y1, int imageIndex, int roiIndex);
    public void setROIY1(int y1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIZ0(int z0, int imageIndex, int roiIndex);
    public void setROIZ0(int z0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setROIZ1(int z1, int imageIndex, int roiIndex);
    public void setROIZ1(int z1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public void setReagentDescription(String description, int screenIndex, int reagentIndex);
    public void setReagentDescription(String description, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public void setReagentID(String id, int screenIndex, int reagentIndex);
    public void setReagentID(String id, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public void setReagentName(String name, int screenIndex, int reagentIndex);
    public void setReagentName(String name, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex);
    public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public void setScreenID(String id, int screenIndex);
    public void setScreenID(String id, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenName(String name, int screenIndex);
    public void setScreenName(String name, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenProtocolDescription(String protocolDescription, int screenIndex);
    public void setScreenProtocolDescription(String protocolDescription, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex);
    public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex);
    public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenType(String type, int screenIndex);
    public void setScreenType(String type, int screenIndex, java.util.Map<String, String> __ctx);

    public void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex);
    public void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex);
    public void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex);
    public void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public void setStageLabelName(String name, int imageIndex);
    public void setStageLabelName(String name, int imageIndex, java.util.Map<String, String> __ctx);

    public void setStageLabelX(float x, int imageIndex);
    public void setStageLabelX(float x, int imageIndex, java.util.Map<String, String> __ctx);

    public void setStageLabelY(float y, int imageIndex);
    public void setStageLabelY(float y, int imageIndex, java.util.Map<String, String> __ctx);

    public void setStageLabelZ(float z, int imageIndex);
    public void setStageLabelZ(float z, int imageIndex, java.util.Map<String, String> __ctx);

    public void setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex);
    public void setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex);
    public void setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex);
    public void setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex);
    public void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public void setWellColumn(int column, int plateIndex, int wellIndex);
    public void setWellColumn(int column, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex);
    public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex);
    public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellID(String id, int plateIndex, int wellIndex);
    public void setWellID(String id, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellRow(int row, int plateIndex, int wellIndex);
    public void setWellRow(int row, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellType(String type, int plateIndex, int wellIndex);
    public void setWellType(String type, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex);
    public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex);
    public void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public void setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex);
    public void setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public void setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex);
    public void setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex);
    public void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);
}
