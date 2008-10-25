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

public interface _MetadataStoreDel extends Ice._ObjectDel
{
    MetadataStore getServant(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getOMEXML(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void createRoot(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setUUID(String uuid, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setArcType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorGain(float gain, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorID(String id, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorModel(String model, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorOffset(float offset, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorType(String type, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDisplayOptionsID(String id, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDisplayOptionsZoom(float zoom, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDisplayOptionsProjectionZStart(int zStart, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDisplayOptionsProjectionZStop(int zStop, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDisplayOptionsTimeTStart(int tStart, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDisplayOptionsTimeTStop(int tStop, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimentDescription(String description, int experimentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimentID(String id, int experimentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimentType(String type, int experimentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterEmail(String email, int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterFirstName(String firstName, int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterID(String id, int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterInstitution(String institution, int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterLastName(String lastName, int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilamentType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageCreationDate(String creationDate, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageDefaultPixels(String defaultPixels, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageDescription(String description, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageID(String id, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageInstrumentRef(String instrumentRef, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageName(String name, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImagingEnvironmentAirPressure(float airPressure, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImagingEnvironmentHumidity(float humidity, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImagingEnvironmentTemperature(float temperature, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setInstrumentID(String id, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFID(String id, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFObjective(String objective, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveID(String id, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsID(String id, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateDescription(String description, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateExternalIdentifier(String externalIdentifier, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateID(String id, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateName(String name, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateStatus(String status, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateRefID(String id, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIID(String id, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIT0(int t0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIT1(int t1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIX0(int x0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIX1(int x1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIY0(int y0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIY1(int y1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIZ0(int z0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setROIZ1(int z1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setReagentDescription(String description, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setReagentID(String id, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setReagentName(String name, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenID(String id, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenName(String name, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenProtocolDescription(String protocolDescription, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenType(String type, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStageLabelName(String name, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStageLabelX(float x, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStageLabelY(float y, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStageLabelZ(float z, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellColumn(int column, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellID(String id, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellRow(int row, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellType(String type, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;
}
