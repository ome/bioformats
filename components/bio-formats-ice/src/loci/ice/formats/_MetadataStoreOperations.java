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

public interface _MetadataStoreOperations
{
    MetadataStore getServant(Ice.Current __current);

    String getOMEXML(Ice.Current __current);

    void createRoot(Ice.Current __current);

    void setUUID(String uuid, Ice.Current __current);

    void setArcType(String type, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    void setDetectorGain(float gain, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorID(String id, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorModel(String model, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorOffset(float offset, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorType(String type, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex, Ice.Current __current);

    void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setDisplayOptionsID(String id, int imageIndex, Ice.Current __current);

    void setDisplayOptionsZoom(float zoom, int imageIndex, Ice.Current __current);

    void setDisplayOptionsProjectionZStart(int zStart, int imageIndex, Ice.Current __current);

    void setDisplayOptionsProjectionZStop(int zStop, int imageIndex, Ice.Current __current);

    void setDisplayOptionsTimeTStart(int tStart, int imageIndex, Ice.Current __current);

    void setDisplayOptionsTimeTStop(int tStop, int imageIndex, Ice.Current __current);

    void setExperimentDescription(String description, int experimentIndex, Ice.Current __current);

    void setExperimentID(String id, int experimentIndex, Ice.Current __current);

    void setExperimentType(String type, int experimentIndex, Ice.Current __current);

    void setExperimenterEmail(String email, int experimenterIndex, Ice.Current __current);

    void setExperimenterFirstName(String firstName, int experimenterIndex, Ice.Current __current);

    void setExperimenterID(String id, int experimenterIndex, Ice.Current __current);

    void setExperimenterInstitution(String institution, int experimenterIndex, Ice.Current __current);

    void setExperimenterLastName(String lastName, int experimenterIndex, Ice.Current __current);

    void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex, Ice.Current __current);

    void setFilamentType(String type, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setImageCreationDate(String creationDate, int imageIndex, Ice.Current __current);

    void setImageDefaultPixels(String defaultPixels, int imageIndex, Ice.Current __current);

    void setImageDescription(String description, int imageIndex, Ice.Current __current);

    void setImageID(String id, int imageIndex, Ice.Current __current);

    void setImageInstrumentRef(String instrumentRef, int imageIndex, Ice.Current __current);

    void setImageName(String name, int imageIndex, Ice.Current __current);

    void setImagingEnvironmentAirPressure(float airPressure, int imageIndex, Ice.Current __current);

    void setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex, Ice.Current __current);

    void setImagingEnvironmentHumidity(float humidity, int imageIndex, Ice.Current __current);

    void setImagingEnvironmentTemperature(float temperature, int imageIndex, Ice.Current __current);

    void setInstrumentID(String id, int instrumentIndex, Ice.Current __current);

    void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLaserType(String type, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    void setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex, Ice.Current __current);

    void setOTFID(String id, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setOTFObjective(String objective, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex, Ice.Current __current);

    void setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveID(String id, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex, Ice.Current __current);

    void setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsID(String id, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex, Ice.Current __current);

    void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setPlateDescription(String description, int plateIndex, Ice.Current __current);

    void setPlateExternalIdentifier(String externalIdentifier, int plateIndex, Ice.Current __current);

    void setPlateID(String id, int plateIndex, Ice.Current __current);

    void setPlateName(String name, int plateIndex, Ice.Current __current);

    void setPlateStatus(String status, int plateIndex, Ice.Current __current);

    void setPlateRefID(String id, int screenIndex, int plateRefIndex, Ice.Current __current);

    void setROIID(String id, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIT0(int t0, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIT1(int t1, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIX0(int x0, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIX1(int x1, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIY0(int y0, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIY1(int y1, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIZ0(int z0, int imageIndex, int roiIndex, Ice.Current __current);

    void setROIZ1(int z1, int imageIndex, int roiIndex, Ice.Current __current);

    void setReagentDescription(String description, int screenIndex, int reagentIndex, Ice.Current __current);

    void setReagentID(String id, int screenIndex, int reagentIndex, Ice.Current __current);

    void setReagentName(String name, int screenIndex, int reagentIndex, Ice.Current __current);

    void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex, Ice.Current __current);

    void setScreenID(String id, int screenIndex, Ice.Current __current);

    void setScreenName(String name, int screenIndex, Ice.Current __current);

    void setScreenProtocolDescription(String protocolDescription, int screenIndex, Ice.Current __current);

    void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex, Ice.Current __current);

    void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex, Ice.Current __current);

    void setScreenType(String type, int screenIndex, Ice.Current __current);

    void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    void setStageLabelName(String name, int imageIndex, Ice.Current __current);

    void setStageLabelX(float x, int imageIndex, Ice.Current __current);

    void setStageLabelY(float y, int imageIndex, Ice.Current __current);

    void setStageLabelZ(float z, int imageIndex, Ice.Current __current);

    void setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    void setWellColumn(int column, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellID(String id, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellRow(int row, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellType(String type, int plateIndex, int wellIndex, Ice.Current __current);

    void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    void setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    void setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);
}
