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

public interface _MetadataRetrieveOperationsNC
{
    MetadataRetrieve getServant();

    String getOMEXML();

    int getChannelComponentCount(int imageIndex, int logicalChannelIndex);

    int getDetectorCount(int instrumentIndex);

    int getExperimentCount();

    int getExperimenterCount();

    int getExperimenterMembershipCount(int experimenterIndex);

    int getGroupRefCount(int experimenterIndex);

    int getImageCount();

    int getInstrumentCount();

    int getLightSourceCount(int instrumentIndex);

    int getLogicalChannelCount(int imageIndex);

    int getOTFCount(int instrumentIndex);

    int getObjectiveCount(int instrumentIndex);

    int getPixelsCount(int imageIndex);

    int getPlaneCount(int imageIndex, int pixelsIndex);

    int getPlateCount();

    int getPlateRefCount(int screenIndex);

    int getROICount(int imageIndex);

    int getReagentCount(int screenIndex);

    int getScreenCount();

    int getScreenAcquisitionCount(int screenIndex);

    int getTiffDataCount(int imageIndex, int pixelsIndex);

    int getWellCount(int plateIndex);

    int getWellSampleCount(int plateIndex, int wellIndex);

    String getUUID();

    String getArcType(int instrumentIndex, int lightSourceIndex);

    String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    float getDetectorGain(int instrumentIndex, int detectorIndex);

    String getDetectorID(int instrumentIndex, int detectorIndex);

    String getDetectorManufacturer(int instrumentIndex, int detectorIndex);

    String getDetectorModel(int instrumentIndex, int detectorIndex);

    float getDetectorOffset(int instrumentIndex, int detectorIndex);

    String getDetectorSerialNumber(int instrumentIndex, int detectorIndex);

    String getDetectorType(int instrumentIndex, int detectorIndex);

    float getDetectorVoltage(int instrumentIndex, int detectorIndex);

    String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex);

    float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex);

    float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex);

    float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex);

    float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex);

    float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex);

    float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex);

    int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex);

    int getDimensionsWaveStart(int imageIndex, int pixelsIndex);

    String getDisplayOptionsID(int imageIndex);

    float getDisplayOptionsZoom(int imageIndex);

    int getDisplayOptionsProjectionZStart(int imageIndex);

    int getDisplayOptionsProjectionZStop(int imageIndex);

    int getDisplayOptionsTimeTStart(int imageIndex);

    int getDisplayOptionsTimeTStop(int imageIndex);

    String getExperimentDescription(int experimentIndex);

    String getExperimentID(int experimentIndex);

    String getExperimentType(int experimentIndex);

    String getExperimenterEmail(int experimenterIndex);

    String getExperimenterFirstName(int experimenterIndex);

    String getExperimenterID(int experimenterIndex);

    String getExperimenterInstitution(int experimenterIndex);

    String getExperimenterLastName(int experimenterIndex);

    String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex);

    String getFilamentType(int instrumentIndex, int lightSourceIndex);

    String getImageCreationDate(int imageIndex);

    String getImageDefaultPixels(int imageIndex);

    String getImageDescription(int imageIndex);

    String getImageID(int imageIndex);

    String getImageInstrumentRef(int imageIndex);

    String getImageName(int imageIndex);

    float getImagingEnvironmentAirPressure(int imageIndex);

    float getImagingEnvironmentCO2Percent(int imageIndex);

    float getImagingEnvironmentHumidity(int imageIndex);

    float getImagingEnvironmentTemperature(int imageIndex);

    String getInstrumentID(int instrumentIndex);

    int getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex);

    String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex);

    String getLaserPulse(int instrumentIndex, int lightSourceIndex);

    boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex);

    String getLaserType(int instrumentIndex, int lightSourceIndex);

    int getLaserWavelength(int instrumentIndex, int lightSourceIndex);

    String getLightSourceID(int instrumentIndex, int lightSourceIndex);

    String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex);

    String getLightSourceModel(int instrumentIndex, int lightSourceIndex);

    float getLightSourcePower(int instrumentIndex, int lightSourceIndex);

    String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex);

    float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex);

    String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex);

    int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex);

    int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex);

    int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelID(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelMode(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelName(int imageIndex, int logicalChannelIndex);

    float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex);

    float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex);

    int getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex);

    int getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex);

    String getOTFID(int instrumentIndex, int otfIndex);

    String getOTFObjective(int instrumentIndex, int otfIndex);

    boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex);

    String getOTFPixelType(int instrumentIndex, int otfIndex);

    int getOTFSizeX(int instrumentIndex, int otfIndex);

    int getOTFSizeY(int instrumentIndex, int otfIndex);

    float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex);

    String getObjectiveCorrection(int instrumentIndex, int objectiveIndex);

    String getObjectiveID(int instrumentIndex, int objectiveIndex);

    String getObjectiveImmersion(int instrumentIndex, int objectiveIndex);

    float getObjectiveLensNA(int instrumentIndex, int objectiveIndex);

    String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex);

    String getObjectiveModel(int instrumentIndex, int objectiveIndex);

    int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex);

    String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex);

    float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex);

    boolean getPixelsBigEndian(int imageIndex, int pixelsIndex);

    String getPixelsDimensionOrder(int imageIndex, int pixelsIndex);

    String getPixelsID(int imageIndex, int pixelsIndex);

    String getPixelsPixelType(int imageIndex, int pixelsIndex);

    int getPixelsSizeC(int imageIndex, int pixelsIndex);

    int getPixelsSizeT(int imageIndex, int pixelsIndex);

    int getPixelsSizeX(int imageIndex, int pixelsIndex);

    int getPixelsSizeY(int imageIndex, int pixelsIndex);

    int getPixelsSizeZ(int imageIndex, int pixelsIndex);

    int getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex);

    int getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex);

    int getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex);

    float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex);

    float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex);

    String getPlateDescription(int plateIndex);

    String getPlateExternalIdentifier(int plateIndex);

    String getPlateID(int plateIndex);

    String getPlateName(int plateIndex);

    String getPlateStatus(int plateIndex);

    String getPlateRefID(int screenIndex, int plateRefIndex);

    String getROIID(int imageIndex, int roiIndex);

    int getROIT0(int imageIndex, int roiIndex);

    int getROIT1(int imageIndex, int roiIndex);

    int getROIX0(int imageIndex, int roiIndex);

    int getROIX1(int imageIndex, int roiIndex);

    int getROIY0(int imageIndex, int roiIndex);

    int getROIY1(int imageIndex, int roiIndex);

    int getROIZ0(int imageIndex, int roiIndex);

    int getROIZ1(int imageIndex, int roiIndex);

    String getReagentDescription(int screenIndex, int reagentIndex);

    String getReagentID(int screenIndex, int reagentIndex);

    String getReagentName(int screenIndex, int reagentIndex);

    String getReagentReagentIdentifier(int screenIndex, int reagentIndex);

    String getScreenID(int screenIndex);

    String getScreenName(int screenIndex);

    String getScreenProtocolDescription(int screenIndex);

    String getScreenProtocolIdentifier(int screenIndex);

    String getScreenReagentSetDescription(int screenIndex);

    String getScreenType(int screenIndex);

    String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex);

    String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex);

    String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex);

    String getStageLabelName(int imageIndex);

    float getStageLabelX(int imageIndex);

    float getStageLabelY(int imageIndex);

    float getStageLabelZ(int imageIndex);

    float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex);

    float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex);

    float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex);

    String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex);

    int getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex);

    int getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex);

    int getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex);

    int getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex);

    int getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex);

    String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex);

    int getWellColumn(int plateIndex, int wellIndex);

    String getWellExternalDescription(int plateIndex, int wellIndex);

    String getWellExternalIdentifier(int plateIndex, int wellIndex);

    String getWellID(int plateIndex, int wellIndex);

    int getWellRow(int plateIndex, int wellIndex);

    String getWellType(int plateIndex, int wellIndex);

    String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex);

    int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex);

    float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex);

    float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex);

    int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);
}
