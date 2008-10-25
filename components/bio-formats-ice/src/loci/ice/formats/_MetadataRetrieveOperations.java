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

public interface _MetadataRetrieveOperations
{
    MetadataRetrieve getServant(Ice.Current __current);

    String getOMEXML(Ice.Current __current);

    int getChannelComponentCount(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getDetectorCount(int instrumentIndex, Ice.Current __current);

    int getExperimentCount(Ice.Current __current);

    int getExperimenterCount(Ice.Current __current);

    int getExperimenterMembershipCount(int experimenterIndex, Ice.Current __current);

    int getGroupRefCount(int experimenterIndex, Ice.Current __current);

    int getImageCount(Ice.Current __current);

    int getInstrumentCount(Ice.Current __current);

    int getLightSourceCount(int instrumentIndex, Ice.Current __current);

    int getLogicalChannelCount(int imageIndex, Ice.Current __current);

    int getOTFCount(int instrumentIndex, Ice.Current __current);

    int getObjectiveCount(int instrumentIndex, Ice.Current __current);

    int getPixelsCount(int imageIndex, Ice.Current __current);

    int getPlaneCount(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPlateCount(Ice.Current __current);

    int getPlateRefCount(int screenIndex, Ice.Current __current);

    int getROICount(int imageIndex, Ice.Current __current);

    int getReagentCount(int screenIndex, Ice.Current __current);

    int getScreenCount(Ice.Current __current);

    int getScreenAcquisitionCount(int screenIndex, Ice.Current __current);

    int getTiffDataCount(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getWellCount(int plateIndex, Ice.Current __current);

    int getWellSampleCount(int plateIndex, int wellIndex, Ice.Current __current);

    String getUUID(Ice.Current __current);

    String getArcType(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    float getDetectorGain(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorID(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorManufacturer(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorModel(int instrumentIndex, int detectorIndex, Ice.Current __current);

    float getDetectorOffset(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorSerialNumber(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorType(int instrumentIndex, int detectorIndex, Ice.Current __current);

    float getDetectorVoltage(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex, Ice.Current __current);

    float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex, Ice.Current __current);

    float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex, Ice.Current __current);

    float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getDimensionsWaveStart(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getDisplayOptionsID(int imageIndex, Ice.Current __current);

    float getDisplayOptionsZoom(int imageIndex, Ice.Current __current);

    int getDisplayOptionsProjectionZStart(int imageIndex, Ice.Current __current);

    int getDisplayOptionsProjectionZStop(int imageIndex, Ice.Current __current);

    int getDisplayOptionsTimeTStart(int imageIndex, Ice.Current __current);

    int getDisplayOptionsTimeTStop(int imageIndex, Ice.Current __current);

    String getExperimentDescription(int experimentIndex, Ice.Current __current);

    String getExperimentID(int experimentIndex, Ice.Current __current);

    String getExperimentType(int experimentIndex, Ice.Current __current);

    String getExperimenterEmail(int experimenterIndex, Ice.Current __current);

    String getExperimenterFirstName(int experimenterIndex, Ice.Current __current);

    String getExperimenterID(int experimenterIndex, Ice.Current __current);

    String getExperimenterInstitution(int experimenterIndex, Ice.Current __current);

    String getExperimenterLastName(int experimenterIndex, Ice.Current __current);

    String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex, Ice.Current __current);

    String getFilamentType(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getImageCreationDate(int imageIndex, Ice.Current __current);

    String getImageDefaultPixels(int imageIndex, Ice.Current __current);

    String getImageDescription(int imageIndex, Ice.Current __current);

    String getImageID(int imageIndex, Ice.Current __current);

    String getImageInstrumentRef(int imageIndex, Ice.Current __current);

    String getImageName(int imageIndex, Ice.Current __current);

    float getImagingEnvironmentAirPressure(int imageIndex, Ice.Current __current);

    float getImagingEnvironmentCO2Percent(int imageIndex, Ice.Current __current);

    float getImagingEnvironmentHumidity(int imageIndex, Ice.Current __current);

    float getImagingEnvironmentTemperature(int imageIndex, Ice.Current __current);

    String getInstrumentID(int instrumentIndex, Ice.Current __current);

    int getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLaserPulse(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLaserType(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    int getLaserWavelength(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLightSourceID(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLightSourceModel(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    float getLightSourcePower(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelID(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelMode(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelName(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getOTFID(int instrumentIndex, int otfIndex, Ice.Current __current);

    String getOTFObjective(int instrumentIndex, int otfIndex, Ice.Current __current);

    boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex, Ice.Current __current);

    String getOTFPixelType(int instrumentIndex, int otfIndex, Ice.Current __current);

    int getOTFSizeX(int instrumentIndex, int otfIndex, Ice.Current __current);

    int getOTFSizeY(int instrumentIndex, int otfIndex, Ice.Current __current);

    float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveCorrection(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveID(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveImmersion(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    float getObjectiveLensNA(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveModel(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    boolean getPixelsBigEndian(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getPixelsDimensionOrder(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getPixelsID(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getPixelsPixelType(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeC(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeT(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeX(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeY(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeZ(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    int getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    int getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    String getPlateDescription(int plateIndex, Ice.Current __current);

    String getPlateExternalIdentifier(int plateIndex, Ice.Current __current);

    String getPlateID(int plateIndex, Ice.Current __current);

    String getPlateName(int plateIndex, Ice.Current __current);

    String getPlateStatus(int plateIndex, Ice.Current __current);

    String getPlateRefID(int screenIndex, int plateRefIndex, Ice.Current __current);

    String getROIID(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIT0(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIT1(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIX0(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIX1(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIY0(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIY1(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIZ0(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIZ1(int imageIndex, int roiIndex, Ice.Current __current);

    String getReagentDescription(int screenIndex, int reagentIndex, Ice.Current __current);

    String getReagentID(int screenIndex, int reagentIndex, Ice.Current __current);

    String getReagentName(int screenIndex, int reagentIndex, Ice.Current __current);

    String getReagentReagentIdentifier(int screenIndex, int reagentIndex, Ice.Current __current);

    String getScreenID(int screenIndex, Ice.Current __current);

    String getScreenName(int screenIndex, Ice.Current __current);

    String getScreenProtocolDescription(int screenIndex, Ice.Current __current);

    String getScreenProtocolIdentifier(int screenIndex, Ice.Current __current);

    String getScreenReagentSetDescription(int screenIndex, Ice.Current __current);

    String getScreenType(int screenIndex, Ice.Current __current);

    String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    String getStageLabelName(int imageIndex, Ice.Current __current);

    float getStageLabelX(int imageIndex, Ice.Current __current);

    float getStageLabelY(int imageIndex, Ice.Current __current);

    float getStageLabelZ(int imageIndex, Ice.Current __current);

    float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getWellColumn(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellExternalDescription(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellExternalIdentifier(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellID(int plateIndex, int wellIndex, Ice.Current __current);

    int getWellRow(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellType(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);
}
