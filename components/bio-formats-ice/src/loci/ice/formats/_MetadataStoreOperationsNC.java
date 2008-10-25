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

public interface _MetadataStoreOperationsNC
{
    MetadataStore getServant();

    String getOMEXML();

    void createRoot();

    void setUUID(String uuid);

    void setArcType(String type, int instrumentIndex, int lightSourceIndex);

    void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    void setDetectorGain(float gain, int instrumentIndex, int detectorIndex);

    void setDetectorID(String id, int instrumentIndex, int detectorIndex);

    void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex);

    void setDetectorModel(String model, int instrumentIndex, int detectorIndex);

    void setDetectorOffset(float offset, int instrumentIndex, int detectorIndex);

    void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex);

    void setDetectorType(String type, int instrumentIndex, int detectorIndex);

    void setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex);

    void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex);

    void setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex);

    void setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex);

    void setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex);

    void setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex);

    void setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex);

    void setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex);

    void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex);

    void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex);

    void setDisplayOptionsID(String id, int imageIndex);

    void setDisplayOptionsZoom(float zoom, int imageIndex);

    void setDisplayOptionsProjectionZStart(int zStart, int imageIndex);

    void setDisplayOptionsProjectionZStop(int zStop, int imageIndex);

    void setDisplayOptionsTimeTStart(int tStart, int imageIndex);

    void setDisplayOptionsTimeTStop(int tStop, int imageIndex);

    void setExperimentDescription(String description, int experimentIndex);

    void setExperimentID(String id, int experimentIndex);

    void setExperimentType(String type, int experimentIndex);

    void setExperimenterEmail(String email, int experimenterIndex);

    void setExperimenterFirstName(String firstName, int experimenterIndex);

    void setExperimenterID(String id, int experimenterIndex);

    void setExperimenterInstitution(String institution, int experimenterIndex);

    void setExperimenterLastName(String lastName, int experimenterIndex);

    void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex);

    void setFilamentType(String type, int instrumentIndex, int lightSourceIndex);

    void setImageCreationDate(String creationDate, int imageIndex);

    void setImageDefaultPixels(String defaultPixels, int imageIndex);

    void setImageDescription(String description, int imageIndex);

    void setImageID(String id, int imageIndex);

    void setImageInstrumentRef(String instrumentRef, int imageIndex);

    void setImageName(String name, int imageIndex);

    void setImagingEnvironmentAirPressure(float airPressure, int imageIndex);

    void setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex);

    void setImagingEnvironmentHumidity(float humidity, int imageIndex);

    void setImagingEnvironmentTemperature(float temperature, int imageIndex);

    void setInstrumentID(String id, int instrumentIndex);

    void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex);

    void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex);

    void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex);

    void setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex);

    void setLaserType(String type, int instrumentIndex, int lightSourceIndex);

    void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex);

    void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex);

    void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

    void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex);

    void setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex);

    void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

    void setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex);

    void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex);

    void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex);

    void setOTFID(String id, int instrumentIndex, int otfIndex);

    void setOTFObjective(String objective, int instrumentIndex, int otfIndex);

    void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex);

    void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex);

    void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex);

    void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex);

    void setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex);

    void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex);

    void setObjectiveID(String id, int instrumentIndex, int objectiveIndex);

    void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex);

    void setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex);

    void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex);

    void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex);

    void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex);

    void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex);

    void setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex);

    void setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex);

    void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex);

    void setPixelsID(String id, int imageIndex, int pixelsIndex);

    void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex);

    void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex);

    void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex);

    void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex);

    void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex);

    void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex);

    void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex);

    void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex);

    void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex);

    void setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex);

    void setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex);

    void setPlateDescription(String description, int plateIndex);

    void setPlateExternalIdentifier(String externalIdentifier, int plateIndex);

    void setPlateID(String id, int plateIndex);

    void setPlateName(String name, int plateIndex);

    void setPlateStatus(String status, int plateIndex);

    void setPlateRefID(String id, int screenIndex, int plateRefIndex);

    void setROIID(String id, int imageIndex, int roiIndex);

    void setROIT0(int t0, int imageIndex, int roiIndex);

    void setROIT1(int t1, int imageIndex, int roiIndex);

    void setROIX0(int x0, int imageIndex, int roiIndex);

    void setROIX1(int x1, int imageIndex, int roiIndex);

    void setROIY0(int y0, int imageIndex, int roiIndex);

    void setROIY1(int y1, int imageIndex, int roiIndex);

    void setROIZ0(int z0, int imageIndex, int roiIndex);

    void setROIZ1(int z1, int imageIndex, int roiIndex);

    void setReagentDescription(String description, int screenIndex, int reagentIndex);

    void setReagentID(String id, int screenIndex, int reagentIndex);

    void setReagentName(String name, int screenIndex, int reagentIndex);

    void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex);

    void setScreenID(String id, int screenIndex);

    void setScreenName(String name, int screenIndex);

    void setScreenProtocolDescription(String protocolDescription, int screenIndex);

    void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex);

    void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex);

    void setScreenType(String type, int screenIndex);

    void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex);

    void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex);

    void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex);

    void setStageLabelName(String name, int imageIndex);

    void setStageLabelX(float x, int imageIndex);

    void setStageLabelY(float y, int imageIndex);

    void setStageLabelZ(float z, int imageIndex);

    void setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex);

    void setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex);

    void setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex);

    void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setWellColumn(int column, int plateIndex, int wellIndex);

    void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex);

    void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex);

    void setWellID(String id, int plateIndex, int wellIndex);

    void setWellRow(int row, int plateIndex, int wellIndex);

    void setWellType(String type, int plateIndex, int wellIndex);

    void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex);

    void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex);

    void setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex);

    void setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex);

    void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex);
}
