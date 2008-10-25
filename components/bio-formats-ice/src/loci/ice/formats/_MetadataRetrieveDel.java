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

public interface _MetadataRetrieveDel extends Ice._ObjectDel
{
    MetadataRetrieve getServant(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getOMEXML(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getChannelComponentCount(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDetectorCount(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getExperimentCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getExperimenterCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getExperimenterMembershipCount(int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getGroupRefCount(int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getImageCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getInstrumentCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLightSourceCount(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLogicalChannelCount(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getOTFCount(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getObjectiveCount(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPixelsCount(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPlaneCount(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPlateCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPlateRefCount(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getROICount(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getReagentCount(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getScreenCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getScreenAcquisitionCount(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getTiffDataCount(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellCount(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellSampleCount(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getUUID(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getArcType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDetectorGain(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorID(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorManufacturer(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorModel(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDetectorOffset(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorSerialNumber(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorType(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDetectorVoltage(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDimensionsWaveStart(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDisplayOptionsID(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDisplayOptionsZoom(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDisplayOptionsProjectionZStart(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDisplayOptionsProjectionZStop(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDisplayOptionsTimeTStart(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDisplayOptionsTimeTStop(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExperimentDescription(int experimentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExperimentID(int experimentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExperimentType(int experimentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExperimenterEmail(int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExperimenterFirstName(int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExperimenterID(int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExperimenterInstitution(int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExperimenterLastName(int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilamentType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageCreationDate(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageDefaultPixels(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageDescription(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageID(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageInstrumentRef(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageName(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getImagingEnvironmentAirPressure(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getImagingEnvironmentCO2Percent(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getImagingEnvironmentHumidity(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getImagingEnvironmentTemperature(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getInstrumentID(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLaserPulse(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLaserType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLaserWavelength(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLightSourceID(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLightSourceModel(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getLightSourcePower(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelID(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelMode(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelName(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getOTFID(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getOTFObjective(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getOTFPixelType(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getOTFSizeX(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getOTFSizeY(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveCorrection(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveID(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveImmersion(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getObjectiveLensNA(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveModel(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean getPixelsBigEndian(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPixelsDimensionOrder(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPixelsID(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPixelsPixelType(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPixelsSizeC(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPixelsSizeT(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPixelsSizeX(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPixelsSizeY(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPixelsSizeZ(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateDescription(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateExternalIdentifier(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateID(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateName(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateStatus(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateRefID(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getROIID(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getROIT0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getROIT1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getROIX0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getROIX1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getROIY0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getROIY1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getROIZ0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getROIZ1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getReagentDescription(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getReagentID(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getReagentName(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getReagentReagentIdentifier(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenID(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenName(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenProtocolDescription(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenProtocolIdentifier(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenReagentSetDescription(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenType(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getStageLabelName(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getStageLabelX(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getStageLabelY(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getStageLabelZ(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellColumn(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellExternalDescription(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellExternalIdentifier(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellID(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellRow(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellType(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;
}
