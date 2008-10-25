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

public interface MetadataRetrievePrx extends Ice.ObjectPrx
{
    public MetadataRetrieve getServant();
    public MetadataRetrieve getServant(java.util.Map<String, String> __ctx);

    public String getOMEXML();
    public String getOMEXML(java.util.Map<String, String> __ctx);

    public int getChannelComponentCount(int imageIndex, int logicalChannelIndex);
    public int getChannelComponentCount(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public int getDetectorCount(int instrumentIndex);
    public int getDetectorCount(int instrumentIndex, java.util.Map<String, String> __ctx);

    public int getExperimentCount();
    public int getExperimentCount(java.util.Map<String, String> __ctx);

    public int getExperimenterCount();
    public int getExperimenterCount(java.util.Map<String, String> __ctx);

    public int getExperimenterMembershipCount(int experimenterIndex);
    public int getExperimenterMembershipCount(int experimenterIndex, java.util.Map<String, String> __ctx);

    public int getGroupRefCount(int experimenterIndex);
    public int getGroupRefCount(int experimenterIndex, java.util.Map<String, String> __ctx);

    public int getImageCount();
    public int getImageCount(java.util.Map<String, String> __ctx);

    public int getInstrumentCount();
    public int getInstrumentCount(java.util.Map<String, String> __ctx);

    public int getLightSourceCount(int instrumentIndex);
    public int getLightSourceCount(int instrumentIndex, java.util.Map<String, String> __ctx);

    public int getLogicalChannelCount(int imageIndex);
    public int getLogicalChannelCount(int imageIndex, java.util.Map<String, String> __ctx);

    public int getOTFCount(int instrumentIndex);
    public int getOTFCount(int instrumentIndex, java.util.Map<String, String> __ctx);

    public int getObjectiveCount(int instrumentIndex);
    public int getObjectiveCount(int instrumentIndex, java.util.Map<String, String> __ctx);

    public int getPixelsCount(int imageIndex);
    public int getPixelsCount(int imageIndex, java.util.Map<String, String> __ctx);

    public int getPlaneCount(int imageIndex, int pixelsIndex);
    public int getPlaneCount(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public int getPlateCount();
    public int getPlateCount(java.util.Map<String, String> __ctx);

    public int getPlateRefCount(int screenIndex);
    public int getPlateRefCount(int screenIndex, java.util.Map<String, String> __ctx);

    public int getROICount(int imageIndex);
    public int getROICount(int imageIndex, java.util.Map<String, String> __ctx);

    public int getReagentCount(int screenIndex);
    public int getReagentCount(int screenIndex, java.util.Map<String, String> __ctx);

    public int getScreenCount();
    public int getScreenCount(java.util.Map<String, String> __ctx);

    public int getScreenAcquisitionCount(int screenIndex);
    public int getScreenAcquisitionCount(int screenIndex, java.util.Map<String, String> __ctx);

    public int getTiffDataCount(int imageIndex, int pixelsIndex);
    public int getTiffDataCount(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public int getWellCount(int plateIndex);
    public int getWellCount(int plateIndex, java.util.Map<String, String> __ctx);

    public int getWellSampleCount(int plateIndex, int wellIndex);
    public int getWellSampleCount(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public String getUUID();
    public String getUUID(java.util.Map<String, String> __ctx);

    public String getArcType(int instrumentIndex, int lightSourceIndex);
    public String getArcType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    public String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx);

    public int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    public int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx);

    public float getDetectorGain(int instrumentIndex, int detectorIndex);
    public float getDetectorGain(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public String getDetectorID(int instrumentIndex, int detectorIndex);
    public String getDetectorID(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public String getDetectorManufacturer(int instrumentIndex, int detectorIndex);
    public String getDetectorManufacturer(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public String getDetectorModel(int instrumentIndex, int detectorIndex);
    public String getDetectorModel(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public float getDetectorOffset(int instrumentIndex, int detectorIndex);
    public float getDetectorOffset(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex);
    public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public String getDetectorType(int instrumentIndex, int detectorIndex);
    public String getDetectorType(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public float getDetectorVoltage(int instrumentIndex, int detectorIndex);
    public float getDetectorVoltage(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex);
    public String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex);
    public float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex);
    public float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex);
    public float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex);
    public float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex);
    public float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex);
    public float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex);
    public int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public int getDimensionsWaveStart(int imageIndex, int pixelsIndex);
    public int getDimensionsWaveStart(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public String getDisplayOptionsID(int imageIndex);
    public String getDisplayOptionsID(int imageIndex, java.util.Map<String, String> __ctx);

    public float getDisplayOptionsZoom(int imageIndex);
    public float getDisplayOptionsZoom(int imageIndex, java.util.Map<String, String> __ctx);

    public int getDisplayOptionsProjectionZStart(int imageIndex);
    public int getDisplayOptionsProjectionZStart(int imageIndex, java.util.Map<String, String> __ctx);

    public int getDisplayOptionsProjectionZStop(int imageIndex);
    public int getDisplayOptionsProjectionZStop(int imageIndex, java.util.Map<String, String> __ctx);

    public int getDisplayOptionsTimeTStart(int imageIndex);
    public int getDisplayOptionsTimeTStart(int imageIndex, java.util.Map<String, String> __ctx);

    public int getDisplayOptionsTimeTStop(int imageIndex);
    public int getDisplayOptionsTimeTStop(int imageIndex, java.util.Map<String, String> __ctx);

    public String getExperimentDescription(int experimentIndex);
    public String getExperimentDescription(int experimentIndex, java.util.Map<String, String> __ctx);

    public String getExperimentID(int experimentIndex);
    public String getExperimentID(int experimentIndex, java.util.Map<String, String> __ctx);

    public String getExperimentType(int experimentIndex);
    public String getExperimentType(int experimentIndex, java.util.Map<String, String> __ctx);

    public String getExperimenterEmail(int experimenterIndex);
    public String getExperimenterEmail(int experimenterIndex, java.util.Map<String, String> __ctx);

    public String getExperimenterFirstName(int experimenterIndex);
    public String getExperimenterFirstName(int experimenterIndex, java.util.Map<String, String> __ctx);

    public String getExperimenterID(int experimenterIndex);
    public String getExperimenterID(int experimenterIndex, java.util.Map<String, String> __ctx);

    public String getExperimenterInstitution(int experimenterIndex);
    public String getExperimenterInstitution(int experimenterIndex, java.util.Map<String, String> __ctx);

    public String getExperimenterLastName(int experimenterIndex);
    public String getExperimenterLastName(int experimenterIndex, java.util.Map<String, String> __ctx);

    public String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex);
    public String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx);

    public String getFilamentType(int instrumentIndex, int lightSourceIndex);
    public String getFilamentType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public String getImageCreationDate(int imageIndex);
    public String getImageCreationDate(int imageIndex, java.util.Map<String, String> __ctx);

    public String getImageDefaultPixels(int imageIndex);
    public String getImageDefaultPixels(int imageIndex, java.util.Map<String, String> __ctx);

    public String getImageDescription(int imageIndex);
    public String getImageDescription(int imageIndex, java.util.Map<String, String> __ctx);

    public String getImageID(int imageIndex);
    public String getImageID(int imageIndex, java.util.Map<String, String> __ctx);

    public String getImageInstrumentRef(int imageIndex);
    public String getImageInstrumentRef(int imageIndex, java.util.Map<String, String> __ctx);

    public String getImageName(int imageIndex);
    public String getImageName(int imageIndex, java.util.Map<String, String> __ctx);

    public float getImagingEnvironmentAirPressure(int imageIndex);
    public float getImagingEnvironmentAirPressure(int imageIndex, java.util.Map<String, String> __ctx);

    public float getImagingEnvironmentCO2Percent(int imageIndex);
    public float getImagingEnvironmentCO2Percent(int imageIndex, java.util.Map<String, String> __ctx);

    public float getImagingEnvironmentHumidity(int imageIndex);
    public float getImagingEnvironmentHumidity(int imageIndex, java.util.Map<String, String> __ctx);

    public float getImagingEnvironmentTemperature(int imageIndex);
    public float getImagingEnvironmentTemperature(int imageIndex, java.util.Map<String, String> __ctx);

    public String getInstrumentID(int instrumentIndex);
    public String getInstrumentID(int instrumentIndex, java.util.Map<String, String> __ctx);

    public int getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex);
    public int getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex);
    public String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public String getLaserPulse(int instrumentIndex, int lightSourceIndex);
    public String getLaserPulse(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex);
    public boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public String getLaserType(int instrumentIndex, int lightSourceIndex);
    public String getLaserType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public int getLaserWavelength(int instrumentIndex, int lightSourceIndex);
    public int getLaserWavelength(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public String getLightSourceID(int instrumentIndex, int lightSourceIndex);
    public String getLightSourceID(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex);
    public String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public String getLightSourceModel(int instrumentIndex, int lightSourceIndex);
    public String getLightSourceModel(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public float getLightSourcePower(int instrumentIndex, int lightSourceIndex);
    public float getLightSourcePower(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex);
    public String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex);
    public float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex);
    public String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex);
    public int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex);
    public int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex);
    public int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelID(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelID(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelMode(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelMode(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelName(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelName(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex);
    public float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex);
    public float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public int getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex);
    public int getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public int getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex);
    public int getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getOTFID(int instrumentIndex, int otfIndex);
    public String getOTFID(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public String getOTFObjective(int instrumentIndex, int otfIndex);
    public String getOTFObjective(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex);
    public boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public String getOTFPixelType(int instrumentIndex, int otfIndex);
    public String getOTFPixelType(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public int getOTFSizeX(int instrumentIndex, int otfIndex);
    public int getOTFSizeX(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public int getOTFSizeY(int instrumentIndex, int otfIndex);
    public int getOTFSizeY(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

    public float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex);
    public float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public String getObjectiveCorrection(int instrumentIndex, int objectiveIndex);
    public String getObjectiveCorrection(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public String getObjectiveID(int instrumentIndex, int objectiveIndex);
    public String getObjectiveID(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public String getObjectiveImmersion(int instrumentIndex, int objectiveIndex);
    public String getObjectiveImmersion(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public float getObjectiveLensNA(int instrumentIndex, int objectiveIndex);
    public float getObjectiveLensNA(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex);
    public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public String getObjectiveModel(int instrumentIndex, int objectiveIndex);
    public String getObjectiveModel(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex);
    public int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex);
    public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex);
    public float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

    public boolean getPixelsBigEndian(int imageIndex, int pixelsIndex);
    public boolean getPixelsBigEndian(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public String getPixelsDimensionOrder(int imageIndex, int pixelsIndex);
    public String getPixelsDimensionOrder(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public String getPixelsID(int imageIndex, int pixelsIndex);
    public String getPixelsID(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public String getPixelsPixelType(int imageIndex, int pixelsIndex);
    public String getPixelsPixelType(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public int getPixelsSizeC(int imageIndex, int pixelsIndex);
    public int getPixelsSizeC(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public int getPixelsSizeT(int imageIndex, int pixelsIndex);
    public int getPixelsSizeT(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public int getPixelsSizeX(int imageIndex, int pixelsIndex);
    public int getPixelsSizeX(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public int getPixelsSizeY(int imageIndex, int pixelsIndex);
    public int getPixelsSizeY(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public int getPixelsSizeZ(int imageIndex, int pixelsIndex);
    public int getPixelsSizeZ(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public int getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex);
    public int getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public int getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex);
    public int getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public int getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex);
    public int getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex);
    public float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex);
    public float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public String getPlateDescription(int plateIndex);
    public String getPlateDescription(int plateIndex, java.util.Map<String, String> __ctx);

    public String getPlateExternalIdentifier(int plateIndex);
    public String getPlateExternalIdentifier(int plateIndex, java.util.Map<String, String> __ctx);

    public String getPlateID(int plateIndex);
    public String getPlateID(int plateIndex, java.util.Map<String, String> __ctx);

    public String getPlateName(int plateIndex);
    public String getPlateName(int plateIndex, java.util.Map<String, String> __ctx);

    public String getPlateStatus(int plateIndex);
    public String getPlateStatus(int plateIndex, java.util.Map<String, String> __ctx);

    public String getPlateRefID(int screenIndex, int plateRefIndex);
    public String getPlateRefID(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx);

    public String getROIID(int imageIndex, int roiIndex);
    public String getROIID(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public int getROIT0(int imageIndex, int roiIndex);
    public int getROIT0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public int getROIT1(int imageIndex, int roiIndex);
    public int getROIT1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public int getROIX0(int imageIndex, int roiIndex);
    public int getROIX0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public int getROIX1(int imageIndex, int roiIndex);
    public int getROIX1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public int getROIY0(int imageIndex, int roiIndex);
    public int getROIY0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public int getROIY1(int imageIndex, int roiIndex);
    public int getROIY1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public int getROIZ0(int imageIndex, int roiIndex);
    public int getROIZ0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public int getROIZ1(int imageIndex, int roiIndex);
    public int getROIZ1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public String getReagentDescription(int screenIndex, int reagentIndex);
    public String getReagentDescription(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public String getReagentID(int screenIndex, int reagentIndex);
    public String getReagentID(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public String getReagentName(int screenIndex, int reagentIndex);
    public String getReagentName(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public String getReagentReagentIdentifier(int screenIndex, int reagentIndex);
    public String getReagentReagentIdentifier(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public String getScreenID(int screenIndex);
    public String getScreenID(int screenIndex, java.util.Map<String, String> __ctx);

    public String getScreenName(int screenIndex);
    public String getScreenName(int screenIndex, java.util.Map<String, String> __ctx);

    public String getScreenProtocolDescription(int screenIndex);
    public String getScreenProtocolDescription(int screenIndex, java.util.Map<String, String> __ctx);

    public String getScreenProtocolIdentifier(int screenIndex);
    public String getScreenProtocolIdentifier(int screenIndex, java.util.Map<String, String> __ctx);

    public String getScreenReagentSetDescription(int screenIndex);
    public String getScreenReagentSetDescription(int screenIndex, java.util.Map<String, String> __ctx);

    public String getScreenType(int screenIndex);
    public String getScreenType(int screenIndex, java.util.Map<String, String> __ctx);

    public String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex);
    public String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex);
    public String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex);
    public String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public String getStageLabelName(int imageIndex);
    public String getStageLabelName(int imageIndex, java.util.Map<String, String> __ctx);

    public float getStageLabelX(int imageIndex);
    public float getStageLabelX(int imageIndex, java.util.Map<String, String> __ctx);

    public float getStageLabelY(int imageIndex);
    public float getStageLabelY(int imageIndex, java.util.Map<String, String> __ctx);

    public float getStageLabelZ(int imageIndex);
    public float getStageLabelZ(int imageIndex, java.util.Map<String, String> __ctx);

    public float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex);
    public float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex);
    public float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex);
    public float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex);
    public String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public int getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex);
    public int getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public int getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex);
    public int getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public int getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex);
    public int getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public int getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex);
    public int getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public int getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex);
    public int getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex);
    public String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx);

    public int getWellColumn(int plateIndex, int wellIndex);
    public int getWellColumn(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public String getWellExternalDescription(int plateIndex, int wellIndex);
    public String getWellExternalDescription(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public String getWellExternalIdentifier(int plateIndex, int wellIndex);
    public String getWellExternalIdentifier(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public String getWellID(int plateIndex, int wellIndex);
    public String getWellID(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public int getWellRow(int plateIndex, int wellIndex);
    public int getWellRow(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public String getWellType(int plateIndex, int wellIndex);
    public String getWellType(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex);
    public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex);
    public int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex);
    public float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex);
    public float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);
    public int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);
}
