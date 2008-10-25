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

public abstract class _MetadataRetrieveDisp extends Ice.ObjectImpl implements MetadataRetrieve
{
    protected void
    ice_copyStateFrom(Ice.Object __obj)
        throws java.lang.CloneNotSupportedException
    {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::formats::MetadataRetrieve"
    };

    public boolean
    ice_isA(String s)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean
    ice_isA(String s, Ice.Current __current)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[]
    ice_ids()
    {
        return __ids;
    }

    public String[]
    ice_ids(Ice.Current __current)
    {
        return __ids;
    }

    public String
    ice_id()
    {
        return __ids[1];
    }

    public String
    ice_id(Ice.Current __current)
    {
        return __ids[1];
    }

    public static String
    ice_staticId()
    {
        return __ids[1];
    }

    public final String
    getArcType(int instrumentIndex, int lightSourceIndex)
    {
        return getArcType(instrumentIndex, lightSourceIndex, null);
    }

    public final String
    getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        return getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex, null);
    }

    public final int
    getChannelComponentCount(int imageIndex, int logicalChannelIndex)
    {
        return getChannelComponentCount(imageIndex, logicalChannelIndex, null);
    }

    public final int
    getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        return getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex, null);
    }

    public final int
    getDetectorCount(int instrumentIndex)
    {
        return getDetectorCount(instrumentIndex, null);
    }

    public final float
    getDetectorGain(int instrumentIndex, int detectorIndex)
    {
        return getDetectorGain(instrumentIndex, detectorIndex, null);
    }

    public final String
    getDetectorID(int instrumentIndex, int detectorIndex)
    {
        return getDetectorID(instrumentIndex, detectorIndex, null);
    }

    public final String
    getDetectorManufacturer(int instrumentIndex, int detectorIndex)
    {
        return getDetectorManufacturer(instrumentIndex, detectorIndex, null);
    }

    public final String
    getDetectorModel(int instrumentIndex, int detectorIndex)
    {
        return getDetectorModel(instrumentIndex, detectorIndex, null);
    }

    public final float
    getDetectorOffset(int instrumentIndex, int detectorIndex)
    {
        return getDetectorOffset(instrumentIndex, detectorIndex, null);
    }

    public final String
    getDetectorSerialNumber(int instrumentIndex, int detectorIndex)
    {
        return getDetectorSerialNumber(instrumentIndex, detectorIndex, null);
    }

    public final String
    getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex)
    {
        return getDetectorSettingsDetector(imageIndex, logicalChannelIndex, null);
    }

    public final float
    getDetectorSettingsGain(int imageIndex, int logicalChannelIndex)
    {
        return getDetectorSettingsGain(imageIndex, logicalChannelIndex, null);
    }

    public final float
    getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex)
    {
        return getDetectorSettingsOffset(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getDetectorType(int instrumentIndex, int detectorIndex)
    {
        return getDetectorType(instrumentIndex, detectorIndex, null);
    }

    public final float
    getDetectorVoltage(int instrumentIndex, int detectorIndex)
    {
        return getDetectorVoltage(instrumentIndex, detectorIndex, null);
    }

    public final float
    getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex)
    {
        return getDimensionsPhysicalSizeX(imageIndex, pixelsIndex, null);
    }

    public final float
    getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex)
    {
        return getDimensionsPhysicalSizeY(imageIndex, pixelsIndex, null);
    }

    public final float
    getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex)
    {
        return getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex, null);
    }

    public final float
    getDimensionsTimeIncrement(int imageIndex, int pixelsIndex)
    {
        return getDimensionsTimeIncrement(imageIndex, pixelsIndex, null);
    }

    public final int
    getDimensionsWaveIncrement(int imageIndex, int pixelsIndex)
    {
        return getDimensionsWaveIncrement(imageIndex, pixelsIndex, null);
    }

    public final int
    getDimensionsWaveStart(int imageIndex, int pixelsIndex)
    {
        return getDimensionsWaveStart(imageIndex, pixelsIndex, null);
    }

    public final String
    getDisplayOptionsID(int imageIndex)
    {
        return getDisplayOptionsID(imageIndex, null);
    }

    public final int
    getDisplayOptionsProjectionZStart(int imageIndex)
    {
        return getDisplayOptionsProjectionZStart(imageIndex, null);
    }

    public final int
    getDisplayOptionsProjectionZStop(int imageIndex)
    {
        return getDisplayOptionsProjectionZStop(imageIndex, null);
    }

    public final int
    getDisplayOptionsTimeTStart(int imageIndex)
    {
        return getDisplayOptionsTimeTStart(imageIndex, null);
    }

    public final int
    getDisplayOptionsTimeTStop(int imageIndex)
    {
        return getDisplayOptionsTimeTStop(imageIndex, null);
    }

    public final float
    getDisplayOptionsZoom(int imageIndex)
    {
        return getDisplayOptionsZoom(imageIndex, null);
    }

    public final int
    getExperimentCount()
    {
        return getExperimentCount(null);
    }

    public final String
    getExperimentDescription(int experimentIndex)
    {
        return getExperimentDescription(experimentIndex, null);
    }

    public final String
    getExperimentID(int experimentIndex)
    {
        return getExperimentID(experimentIndex, null);
    }

    public final String
    getExperimentType(int experimentIndex)
    {
        return getExperimentType(experimentIndex, null);
    }

    public final int
    getExperimenterCount()
    {
        return getExperimenterCount(null);
    }

    public final String
    getExperimenterEmail(int experimenterIndex)
    {
        return getExperimenterEmail(experimenterIndex, null);
    }

    public final String
    getExperimenterFirstName(int experimenterIndex)
    {
        return getExperimenterFirstName(experimenterIndex, null);
    }

    public final String
    getExperimenterID(int experimenterIndex)
    {
        return getExperimenterID(experimenterIndex, null);
    }

    public final String
    getExperimenterInstitution(int experimenterIndex)
    {
        return getExperimenterInstitution(experimenterIndex, null);
    }

    public final String
    getExperimenterLastName(int experimenterIndex)
    {
        return getExperimenterLastName(experimenterIndex, null);
    }

    public final int
    getExperimenterMembershipCount(int experimenterIndex)
    {
        return getExperimenterMembershipCount(experimenterIndex, null);
    }

    public final String
    getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex)
    {
        return getExperimenterMembershipGroup(experimenterIndex, groupRefIndex, null);
    }

    public final String
    getFilamentType(int instrumentIndex, int lightSourceIndex)
    {
        return getFilamentType(instrumentIndex, lightSourceIndex, null);
    }

    public final int
    getGroupRefCount(int experimenterIndex)
    {
        return getGroupRefCount(experimenterIndex, null);
    }

    public final int
    getImageCount()
    {
        return getImageCount(null);
    }

    public final String
    getImageCreationDate(int imageIndex)
    {
        return getImageCreationDate(imageIndex, null);
    }

    public final String
    getImageDefaultPixels(int imageIndex)
    {
        return getImageDefaultPixels(imageIndex, null);
    }

    public final String
    getImageDescription(int imageIndex)
    {
        return getImageDescription(imageIndex, null);
    }

    public final String
    getImageID(int imageIndex)
    {
        return getImageID(imageIndex, null);
    }

    public final String
    getImageInstrumentRef(int imageIndex)
    {
        return getImageInstrumentRef(imageIndex, null);
    }

    public final String
    getImageName(int imageIndex)
    {
        return getImageName(imageIndex, null);
    }

    public final float
    getImagingEnvironmentAirPressure(int imageIndex)
    {
        return getImagingEnvironmentAirPressure(imageIndex, null);
    }

    public final float
    getImagingEnvironmentCO2Percent(int imageIndex)
    {
        return getImagingEnvironmentCO2Percent(imageIndex, null);
    }

    public final float
    getImagingEnvironmentHumidity(int imageIndex)
    {
        return getImagingEnvironmentHumidity(imageIndex, null);
    }

    public final float
    getImagingEnvironmentTemperature(int imageIndex)
    {
        return getImagingEnvironmentTemperature(imageIndex, null);
    }

    public final int
    getInstrumentCount()
    {
        return getInstrumentCount(null);
    }

    public final String
    getInstrumentID(int instrumentIndex)
    {
        return getInstrumentID(instrumentIndex, null);
    }

    public final int
    getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex, null);
    }

    public final String
    getLaserLaserMedium(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserLaserMedium(instrumentIndex, lightSourceIndex, null);
    }

    public final String
    getLaserPulse(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserPulse(instrumentIndex, lightSourceIndex, null);
    }

    public final boolean
    getLaserTuneable(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserTuneable(instrumentIndex, lightSourceIndex, null);
    }

    public final String
    getLaserType(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserType(instrumentIndex, lightSourceIndex, null);
    }

    public final int
    getLaserWavelength(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserWavelength(instrumentIndex, lightSourceIndex, null);
    }

    public final int
    getLightSourceCount(int instrumentIndex)
    {
        return getLightSourceCount(instrumentIndex, null);
    }

    public final String
    getLightSourceID(int instrumentIndex, int lightSourceIndex)
    {
        return getLightSourceID(instrumentIndex, lightSourceIndex, null);
    }

    public final String
    getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex)
    {
        return getLightSourceManufacturer(instrumentIndex, lightSourceIndex, null);
    }

    public final String
    getLightSourceModel(int instrumentIndex, int lightSourceIndex)
    {
        return getLightSourceModel(instrumentIndex, lightSourceIndex, null);
    }

    public final float
    getLightSourcePower(int instrumentIndex, int lightSourceIndex)
    {
        return getLightSourcePower(instrumentIndex, lightSourceIndex, null);
    }

    public final String
    getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex)
    {
        return getLightSourceSerialNumber(instrumentIndex, lightSourceIndex, null);
    }

    public final float
    getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex)
    {
        return getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex)
    {
        return getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex, null);
    }

    public final int
    getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex)
    {
        return getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex, null);
    }

    public final int
    getLogicalChannelCount(int imageIndex)
    {
        return getLogicalChannelCount(imageIndex, null);
    }

    public final int
    getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelEmWave(imageIndex, logicalChannelIndex, null);
    }

    public final int
    getLogicalChannelExWave(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelExWave(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getLogicalChannelFluor(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelFluor(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getLogicalChannelID(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelID(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getLogicalChannelMode(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelMode(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getLogicalChannelName(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelName(imageIndex, logicalChannelIndex, null);
    }

    public final float
    getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelNdFilter(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getLogicalChannelOTF(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelOTF(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex, null);
    }

    public final float
    getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex, null);
    }

    public final int
    getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex, null);
    }

    public final int
    getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getOMEXML()
    {
        return getOMEXML(null);
    }

    public final int
    getOTFCount(int instrumentIndex)
    {
        return getOTFCount(instrumentIndex, null);
    }

    public final String
    getOTFID(int instrumentIndex, int otfIndex)
    {
        return getOTFID(instrumentIndex, otfIndex, null);
    }

    public final String
    getOTFObjective(int instrumentIndex, int otfIndex)
    {
        return getOTFObjective(instrumentIndex, otfIndex, null);
    }

    public final boolean
    getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex)
    {
        return getOTFOpticalAxisAveraged(instrumentIndex, otfIndex, null);
    }

    public final String
    getOTFPixelType(int instrumentIndex, int otfIndex)
    {
        return getOTFPixelType(instrumentIndex, otfIndex, null);
    }

    public final int
    getOTFSizeX(int instrumentIndex, int otfIndex)
    {
        return getOTFSizeX(instrumentIndex, otfIndex, null);
    }

    public final int
    getOTFSizeY(int instrumentIndex, int otfIndex)
    {
        return getOTFSizeY(instrumentIndex, otfIndex, null);
    }

    public final float
    getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex, null);
    }

    public final String
    getObjectiveCorrection(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveCorrection(instrumentIndex, objectiveIndex, null);
    }

    public final int
    getObjectiveCount(int instrumentIndex)
    {
        return getObjectiveCount(instrumentIndex, null);
    }

    public final String
    getObjectiveID(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveID(instrumentIndex, objectiveIndex, null);
    }

    public final String
    getObjectiveImmersion(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveImmersion(instrumentIndex, objectiveIndex, null);
    }

    public final float
    getObjectiveLensNA(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveLensNA(instrumentIndex, objectiveIndex, null);
    }

    public final String
    getObjectiveManufacturer(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveManufacturer(instrumentIndex, objectiveIndex, null);
    }

    public final String
    getObjectiveModel(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveModel(instrumentIndex, objectiveIndex, null);
    }

    public final int
    getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveNominalMagnification(instrumentIndex, objectiveIndex, null);
    }

    public final String
    getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveSerialNumber(instrumentIndex, objectiveIndex, null);
    }

    public final float
    getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveWorkingDistance(instrumentIndex, objectiveIndex, null);
    }

    public final boolean
    getPixelsBigEndian(int imageIndex, int pixelsIndex)
    {
        return getPixelsBigEndian(imageIndex, pixelsIndex, null);
    }

    public final int
    getPixelsCount(int imageIndex)
    {
        return getPixelsCount(imageIndex, null);
    }

    public final String
    getPixelsDimensionOrder(int imageIndex, int pixelsIndex)
    {
        return getPixelsDimensionOrder(imageIndex, pixelsIndex, null);
    }

    public final String
    getPixelsID(int imageIndex, int pixelsIndex)
    {
        return getPixelsID(imageIndex, pixelsIndex, null);
    }

    public final String
    getPixelsPixelType(int imageIndex, int pixelsIndex)
    {
        return getPixelsPixelType(imageIndex, pixelsIndex, null);
    }

    public final int
    getPixelsSizeC(int imageIndex, int pixelsIndex)
    {
        return getPixelsSizeC(imageIndex, pixelsIndex, null);
    }

    public final int
    getPixelsSizeT(int imageIndex, int pixelsIndex)
    {
        return getPixelsSizeT(imageIndex, pixelsIndex, null);
    }

    public final int
    getPixelsSizeX(int imageIndex, int pixelsIndex)
    {
        return getPixelsSizeX(imageIndex, pixelsIndex, null);
    }

    public final int
    getPixelsSizeY(int imageIndex, int pixelsIndex)
    {
        return getPixelsSizeY(imageIndex, pixelsIndex, null);
    }

    public final int
    getPixelsSizeZ(int imageIndex, int pixelsIndex)
    {
        return getPixelsSizeZ(imageIndex, pixelsIndex, null);
    }

    public final int
    getPlaneCount(int imageIndex, int pixelsIndex)
    {
        return getPlaneCount(imageIndex, pixelsIndex, null);
    }

    public final int
    getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneTheC(imageIndex, pixelsIndex, planeIndex, null);
    }

    public final int
    getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneTheT(imageIndex, pixelsIndex, planeIndex, null);
    }

    public final int
    getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneTheZ(imageIndex, pixelsIndex, planeIndex, null);
    }

    public final float
    getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex, null);
    }

    public final float
    getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex, null);
    }

    public final int
    getPlateCount()
    {
        return getPlateCount(null);
    }

    public final String
    getPlateDescription(int plateIndex)
    {
        return getPlateDescription(plateIndex, null);
    }

    public final String
    getPlateExternalIdentifier(int plateIndex)
    {
        return getPlateExternalIdentifier(plateIndex, null);
    }

    public final String
    getPlateID(int plateIndex)
    {
        return getPlateID(plateIndex, null);
    }

    public final String
    getPlateName(int plateIndex)
    {
        return getPlateName(plateIndex, null);
    }

    public final int
    getPlateRefCount(int screenIndex)
    {
        return getPlateRefCount(screenIndex, null);
    }

    public final String
    getPlateRefID(int screenIndex, int plateRefIndex)
    {
        return getPlateRefID(screenIndex, plateRefIndex, null);
    }

    public final String
    getPlateStatus(int plateIndex)
    {
        return getPlateStatus(plateIndex, null);
    }

    public final int
    getROICount(int imageIndex)
    {
        return getROICount(imageIndex, null);
    }

    public final String
    getROIID(int imageIndex, int roiIndex)
    {
        return getROIID(imageIndex, roiIndex, null);
    }

    public final int
    getROIT0(int imageIndex, int roiIndex)
    {
        return getROIT0(imageIndex, roiIndex, null);
    }

    public final int
    getROIT1(int imageIndex, int roiIndex)
    {
        return getROIT1(imageIndex, roiIndex, null);
    }

    public final int
    getROIX0(int imageIndex, int roiIndex)
    {
        return getROIX0(imageIndex, roiIndex, null);
    }

    public final int
    getROIX1(int imageIndex, int roiIndex)
    {
        return getROIX1(imageIndex, roiIndex, null);
    }

    public final int
    getROIY0(int imageIndex, int roiIndex)
    {
        return getROIY0(imageIndex, roiIndex, null);
    }

    public final int
    getROIY1(int imageIndex, int roiIndex)
    {
        return getROIY1(imageIndex, roiIndex, null);
    }

    public final int
    getROIZ0(int imageIndex, int roiIndex)
    {
        return getROIZ0(imageIndex, roiIndex, null);
    }

    public final int
    getROIZ1(int imageIndex, int roiIndex)
    {
        return getROIZ1(imageIndex, roiIndex, null);
    }

    public final int
    getReagentCount(int screenIndex)
    {
        return getReagentCount(screenIndex, null);
    }

    public final String
    getReagentDescription(int screenIndex, int reagentIndex)
    {
        return getReagentDescription(screenIndex, reagentIndex, null);
    }

    public final String
    getReagentID(int screenIndex, int reagentIndex)
    {
        return getReagentID(screenIndex, reagentIndex, null);
    }

    public final String
    getReagentName(int screenIndex, int reagentIndex)
    {
        return getReagentName(screenIndex, reagentIndex, null);
    }

    public final String
    getReagentReagentIdentifier(int screenIndex, int reagentIndex)
    {
        return getReagentReagentIdentifier(screenIndex, reagentIndex, null);
    }

    public final int
    getScreenAcquisitionCount(int screenIndex)
    {
        return getScreenAcquisitionCount(screenIndex, null);
    }

    public final String
    getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex)
    {
        return getScreenAcquisitionEndTime(screenIndex, screenAcquisitionIndex, null);
    }

    public final String
    getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex)
    {
        return getScreenAcquisitionID(screenIndex, screenAcquisitionIndex, null);
    }

    public final String
    getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex)
    {
        return getScreenAcquisitionStartTime(screenIndex, screenAcquisitionIndex, null);
    }

    public final int
    getScreenCount()
    {
        return getScreenCount(null);
    }

    public final String
    getScreenID(int screenIndex)
    {
        return getScreenID(screenIndex, null);
    }

    public final String
    getScreenName(int screenIndex)
    {
        return getScreenName(screenIndex, null);
    }

    public final String
    getScreenProtocolDescription(int screenIndex)
    {
        return getScreenProtocolDescription(screenIndex, null);
    }

    public final String
    getScreenProtocolIdentifier(int screenIndex)
    {
        return getScreenProtocolIdentifier(screenIndex, null);
    }

    public final String
    getScreenReagentSetDescription(int screenIndex)
    {
        return getScreenReagentSetDescription(screenIndex, null);
    }

    public final String
    getScreenType(int screenIndex)
    {
        return getScreenType(screenIndex, null);
    }

    public final MetadataRetrieve
    getServant()
    {
        return getServant(null);
    }

    public final String
    getStageLabelName(int imageIndex)
    {
        return getStageLabelName(imageIndex, null);
    }

    public final float
    getStageLabelX(int imageIndex)
    {
        return getStageLabelX(imageIndex, null);
    }

    public final float
    getStageLabelY(int imageIndex)
    {
        return getStageLabelY(imageIndex, null);
    }

    public final float
    getStageLabelZ(int imageIndex)
    {
        return getStageLabelZ(imageIndex, null);
    }

    public final float
    getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex, null);
    }

    public final float
    getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex, null);
    }

    public final float
    getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex, null);
    }

    public final int
    getTiffDataCount(int imageIndex, int pixelsIndex)
    {
        return getTiffDataCount(imageIndex, pixelsIndex, null);
    }

    public final String
    getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataFileName(imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final int
    getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataFirstC(imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final int
    getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataFirstT(imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final int
    getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataFirstZ(imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final int
    getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataIFD(imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final int
    getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataNumPlanes(imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final String
    getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataUUID(imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final String
    getUUID()
    {
        return getUUID(null);
    }

    public final int
    getWellColumn(int plateIndex, int wellIndex)
    {
        return getWellColumn(plateIndex, wellIndex, null);
    }

    public final int
    getWellCount(int plateIndex)
    {
        return getWellCount(plateIndex, null);
    }

    public final String
    getWellExternalDescription(int plateIndex, int wellIndex)
    {
        return getWellExternalDescription(plateIndex, wellIndex, null);
    }

    public final String
    getWellExternalIdentifier(int plateIndex, int wellIndex)
    {
        return getWellExternalIdentifier(plateIndex, wellIndex, null);
    }

    public final String
    getWellID(int plateIndex, int wellIndex)
    {
        return getWellID(plateIndex, wellIndex, null);
    }

    public final int
    getWellRow(int plateIndex, int wellIndex)
    {
        return getWellRow(plateIndex, wellIndex, null);
    }

    public final int
    getWellSampleCount(int plateIndex, int wellIndex)
    {
        return getWellSampleCount(plateIndex, wellIndex, null);
    }

    public final String
    getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex)
    {
        return getWellSampleID(plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final int
    getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex)
    {
        return getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final float
    getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex)
    {
        return getWellSamplePosX(plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final float
    getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex)
    {
        return getWellSamplePosY(plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final int
    getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex)
    {
        return getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final String
    getWellType(int plateIndex, int wellIndex)
    {
        return getWellType(plateIndex, wellIndex, null);
    }

    public static IceInternal.DispatchStatus
    ___getServant(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        MetadataRetrieve __ret = __obj.getServant(__current);
        __os.writeObject(__ret);
        __os.writePendingObjects();
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getOMEXML(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getOMEXML(__current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getChannelComponentCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int __ret = __obj.getChannelComponentCount(imageIndex, logicalChannelIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDetectorCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int __ret = __obj.getDetectorCount(instrumentIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getExperimentCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getExperimentCount(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getExperimenterCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getExperimenterCount(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getExperimenterMembershipCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        int __ret = __obj.getExperimenterMembershipCount(experimenterIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getGroupRefCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        int __ret = __obj.getGroupRefCount(experimenterIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getImageCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getImageCount(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getInstrumentCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getInstrumentCount(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLightSourceCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int __ret = __obj.getLightSourceCount(instrumentIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int __ret = __obj.getLogicalChannelCount(imageIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getOTFCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int __ret = __obj.getOTFCount(instrumentIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getObjectiveCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int __ret = __obj.getObjectiveCount(instrumentIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPixelsCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int __ret = __obj.getPixelsCount(imageIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlaneCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int __ret = __obj.getPlaneCount(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlateCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPlateCount(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlateRefCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        int __ret = __obj.getPlateRefCount(screenIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getROICount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int __ret = __obj.getROICount(imageIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getReagentCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        int __ret = __obj.getReagentCount(screenIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getScreenCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getScreenCount(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getScreenAcquisitionCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        int __ret = __obj.getScreenAcquisitionCount(screenIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getTiffDataCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int __ret = __obj.getTiffDataCount(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int __ret = __obj.getWellCount(plateIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellSampleCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int __ret = __obj.getWellSampleCount(plateIndex, wellIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getUUID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getUUID(__current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getArcType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        String __ret = __obj.getArcType(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getChannelComponentColorDomain(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        String __ret = __obj.getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getChannelComponentIndex(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        int __ret = __obj.getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDetectorGain(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        float __ret = __obj.getDetectorGain(instrumentIndex, detectorIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDetectorID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        String __ret = __obj.getDetectorID(instrumentIndex, detectorIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDetectorManufacturer(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        String __ret = __obj.getDetectorManufacturer(instrumentIndex, detectorIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDetectorModel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        String __ret = __obj.getDetectorModel(instrumentIndex, detectorIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDetectorOffset(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        float __ret = __obj.getDetectorOffset(instrumentIndex, detectorIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDetectorSerialNumber(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        String __ret = __obj.getDetectorSerialNumber(instrumentIndex, detectorIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDetectorType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        String __ret = __obj.getDetectorType(instrumentIndex, detectorIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDetectorVoltage(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        float __ret = __obj.getDetectorVoltage(instrumentIndex, detectorIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDetectorSettingsDetector(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        String __ret = __obj.getDetectorSettingsDetector(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDetectorSettingsGain(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        float __ret = __obj.getDetectorSettingsGain(imageIndex, logicalChannelIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDetectorSettingsOffset(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        float __ret = __obj.getDetectorSettingsOffset(imageIndex, logicalChannelIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDimensionsPhysicalSizeX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        float __ret = __obj.getDimensionsPhysicalSizeX(imageIndex, pixelsIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDimensionsPhysicalSizeY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        float __ret = __obj.getDimensionsPhysicalSizeY(imageIndex, pixelsIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDimensionsPhysicalSizeZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        float __ret = __obj.getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDimensionsTimeIncrement(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        float __ret = __obj.getDimensionsTimeIncrement(imageIndex, pixelsIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDimensionsWaveIncrement(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int __ret = __obj.getDimensionsWaveIncrement(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDimensionsWaveStart(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int __ret = __obj.getDimensionsWaveStart(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDisplayOptionsID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        String __ret = __obj.getDisplayOptionsID(imageIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDisplayOptionsZoom(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        float __ret = __obj.getDisplayOptionsZoom(imageIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDisplayOptionsProjectionZStart(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int __ret = __obj.getDisplayOptionsProjectionZStart(imageIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDisplayOptionsProjectionZStop(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int __ret = __obj.getDisplayOptionsProjectionZStop(imageIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDisplayOptionsTimeTStart(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int __ret = __obj.getDisplayOptionsTimeTStart(imageIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDisplayOptionsTimeTStop(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int __ret = __obj.getDisplayOptionsTimeTStop(imageIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getExperimentDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int experimentIndex;
        experimentIndex = __is.readInt();
        String __ret = __obj.getExperimentDescription(experimentIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getExperimentID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int experimentIndex;
        experimentIndex = __is.readInt();
        String __ret = __obj.getExperimentID(experimentIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getExperimentType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int experimentIndex;
        experimentIndex = __is.readInt();
        String __ret = __obj.getExperimentType(experimentIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getExperimenterEmail(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        String __ret = __obj.getExperimenterEmail(experimenterIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getExperimenterFirstName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        String __ret = __obj.getExperimenterFirstName(experimenterIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getExperimenterID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        String __ret = __obj.getExperimenterID(experimenterIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getExperimenterInstitution(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        String __ret = __obj.getExperimenterInstitution(experimenterIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getExperimenterLastName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        String __ret = __obj.getExperimenterLastName(experimenterIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getExperimenterMembershipGroup(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        int groupRefIndex;
        groupRefIndex = __is.readInt();
        String __ret = __obj.getExperimenterMembershipGroup(experimenterIndex, groupRefIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getFilamentType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        String __ret = __obj.getFilamentType(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getImageCreationDate(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        String __ret = __obj.getImageCreationDate(imageIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getImageDefaultPixels(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        String __ret = __obj.getImageDefaultPixels(imageIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getImageDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        String __ret = __obj.getImageDescription(imageIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getImageID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        String __ret = __obj.getImageID(imageIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getImageInstrumentRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        String __ret = __obj.getImageInstrumentRef(imageIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getImageName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        String __ret = __obj.getImageName(imageIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getImagingEnvironmentAirPressure(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        float __ret = __obj.getImagingEnvironmentAirPressure(imageIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getImagingEnvironmentCO2Percent(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        float __ret = __obj.getImagingEnvironmentCO2Percent(imageIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getImagingEnvironmentHumidity(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        float __ret = __obj.getImagingEnvironmentHumidity(imageIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getImagingEnvironmentTemperature(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        float __ret = __obj.getImagingEnvironmentTemperature(imageIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getInstrumentID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        String __ret = __obj.getInstrumentID(instrumentIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLaserFrequencyMultiplication(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        int __ret = __obj.getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLaserLaserMedium(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        String __ret = __obj.getLaserLaserMedium(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLaserPulse(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        String __ret = __obj.getLaserPulse(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLaserTuneable(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        boolean __ret = __obj.getLaserTuneable(instrumentIndex, lightSourceIndex, __current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLaserType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        String __ret = __obj.getLaserType(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLaserWavelength(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        int __ret = __obj.getLaserWavelength(instrumentIndex, lightSourceIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLightSourceID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        String __ret = __obj.getLightSourceID(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLightSourceManufacturer(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        String __ret = __obj.getLightSourceManufacturer(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLightSourceModel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        String __ret = __obj.getLightSourceModel(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLightSourcePower(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        float __ret = __obj.getLightSourcePower(instrumentIndex, lightSourceIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLightSourceSerialNumber(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        String __ret = __obj.getLightSourceSerialNumber(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLightSourceSettingsAttenuation(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        float __ret = __obj.getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLightSourceSettingsLightSource(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        String __ret = __obj.getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLightSourceSettingsWavelength(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int __ret = __obj.getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelContrastMethod(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        String __ret = __obj.getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelEmWave(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int __ret = __obj.getLogicalChannelEmWave(imageIndex, logicalChannelIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelExWave(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int __ret = __obj.getLogicalChannelExWave(imageIndex, logicalChannelIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelFluor(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        String __ret = __obj.getLogicalChannelFluor(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        String __ret = __obj.getLogicalChannelID(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelIlluminationType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        String __ret = __obj.getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelMode(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        String __ret = __obj.getLogicalChannelMode(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        String __ret = __obj.getLogicalChannelName(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelNdFilter(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        float __ret = __obj.getLogicalChannelNdFilter(imageIndex, logicalChannelIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelOTF(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        String __ret = __obj.getLogicalChannelOTF(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelPhotometricInterpretation(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        String __ret = __obj.getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelPinholeSize(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        float __ret = __obj.getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelPockelCellSetting(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int __ret = __obj.getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getLogicalChannelSamplesPerPixel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int __ret = __obj.getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getOTFID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        String __ret = __obj.getOTFID(instrumentIndex, otfIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getOTFObjective(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        String __ret = __obj.getOTFObjective(instrumentIndex, otfIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getOTFOpticalAxisAveraged(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        boolean __ret = __obj.getOTFOpticalAxisAveraged(instrumentIndex, otfIndex, __current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getOTFPixelType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        String __ret = __obj.getOTFPixelType(instrumentIndex, otfIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getOTFSizeX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        int __ret = __obj.getOTFSizeX(instrumentIndex, otfIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getOTFSizeY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        int __ret = __obj.getOTFSizeY(instrumentIndex, otfIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getObjectiveCalibratedMagnification(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        float __ret = __obj.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getObjectiveCorrection(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        String __ret = __obj.getObjectiveCorrection(instrumentIndex, objectiveIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getObjectiveID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        String __ret = __obj.getObjectiveID(instrumentIndex, objectiveIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getObjectiveImmersion(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        String __ret = __obj.getObjectiveImmersion(instrumentIndex, objectiveIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getObjectiveLensNA(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        float __ret = __obj.getObjectiveLensNA(instrumentIndex, objectiveIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getObjectiveManufacturer(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        String __ret = __obj.getObjectiveManufacturer(instrumentIndex, objectiveIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getObjectiveModel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        String __ret = __obj.getObjectiveModel(instrumentIndex, objectiveIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getObjectiveNominalMagnification(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        int __ret = __obj.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getObjectiveSerialNumber(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        String __ret = __obj.getObjectiveSerialNumber(instrumentIndex, objectiveIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getObjectiveWorkingDistance(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        float __ret = __obj.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPixelsBigEndian(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        boolean __ret = __obj.getPixelsBigEndian(imageIndex, pixelsIndex, __current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPixelsDimensionOrder(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        String __ret = __obj.getPixelsDimensionOrder(imageIndex, pixelsIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPixelsID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        String __ret = __obj.getPixelsID(imageIndex, pixelsIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPixelsPixelType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        String __ret = __obj.getPixelsPixelType(imageIndex, pixelsIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPixelsSizeC(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int __ret = __obj.getPixelsSizeC(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPixelsSizeT(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int __ret = __obj.getPixelsSizeT(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPixelsSizeX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int __ret = __obj.getPixelsSizeX(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPixelsSizeY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int __ret = __obj.getPixelsSizeY(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPixelsSizeZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int __ret = __obj.getPixelsSizeZ(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlaneTheC(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        int __ret = __obj.getPlaneTheC(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlaneTheT(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        int __ret = __obj.getPlaneTheT(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlaneTheZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        int __ret = __obj.getPlaneTheZ(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlaneTimingDeltaT(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        float __ret = __obj.getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlaneTimingExposureTime(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        float __ret = __obj.getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlateDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        String __ret = __obj.getPlateDescription(plateIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlateExternalIdentifier(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        String __ret = __obj.getPlateExternalIdentifier(plateIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlateID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        String __ret = __obj.getPlateID(plateIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlateName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        String __ret = __obj.getPlateName(plateIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlateStatus(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        String __ret = __obj.getPlateStatus(plateIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPlateRefID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        int plateRefIndex;
        plateRefIndex = __is.readInt();
        String __ret = __obj.getPlateRefID(screenIndex, plateRefIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getROIID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        String __ret = __obj.getROIID(imageIndex, roiIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getROIT0(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int __ret = __obj.getROIT0(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getROIT1(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int __ret = __obj.getROIT1(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getROIX0(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int __ret = __obj.getROIX0(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getROIX1(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int __ret = __obj.getROIX1(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getROIY0(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int __ret = __obj.getROIY0(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getROIY1(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int __ret = __obj.getROIY1(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getROIZ0(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int __ret = __obj.getROIZ0(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getROIZ1(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int __ret = __obj.getROIZ1(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getReagentDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        String __ret = __obj.getReagentDescription(screenIndex, reagentIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getReagentID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        String __ret = __obj.getReagentID(screenIndex, reagentIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getReagentName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        String __ret = __obj.getReagentName(screenIndex, reagentIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getReagentReagentIdentifier(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        String __ret = __obj.getReagentReagentIdentifier(screenIndex, reagentIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getScreenID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        String __ret = __obj.getScreenID(screenIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getScreenName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        String __ret = __obj.getScreenName(screenIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getScreenProtocolDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        String __ret = __obj.getScreenProtocolDescription(screenIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getScreenProtocolIdentifier(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        String __ret = __obj.getScreenProtocolIdentifier(screenIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getScreenReagentSetDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        String __ret = __obj.getScreenReagentSetDescription(screenIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getScreenType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        String __ret = __obj.getScreenType(screenIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getScreenAcquisitionEndTime(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        String __ret = __obj.getScreenAcquisitionEndTime(screenIndex, screenAcquisitionIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getScreenAcquisitionID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        String __ret = __obj.getScreenAcquisitionID(screenIndex, screenAcquisitionIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getScreenAcquisitionStartTime(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        String __ret = __obj.getScreenAcquisitionStartTime(screenIndex, screenAcquisitionIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getStageLabelName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        String __ret = __obj.getStageLabelName(imageIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getStageLabelX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        float __ret = __obj.getStageLabelX(imageIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getStageLabelY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        float __ret = __obj.getStageLabelY(imageIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getStageLabelZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        float __ret = __obj.getStageLabelZ(imageIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getStagePositionPositionX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        float __ret = __obj.getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getStagePositionPositionY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        float __ret = __obj.getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getStagePositionPositionZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        float __ret = __obj.getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getTiffDataFileName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        String __ret = __obj.getTiffDataFileName(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getTiffDataFirstC(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        int __ret = __obj.getTiffDataFirstC(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getTiffDataFirstT(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        int __ret = __obj.getTiffDataFirstT(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getTiffDataFirstZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        int __ret = __obj.getTiffDataFirstZ(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getTiffDataIFD(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        int __ret = __obj.getTiffDataIFD(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getTiffDataNumPlanes(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        int __ret = __obj.getTiffDataNumPlanes(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getTiffDataUUID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        String __ret = __obj.getTiffDataUUID(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellColumn(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int __ret = __obj.getWellColumn(plateIndex, wellIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellExternalDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        String __ret = __obj.getWellExternalDescription(plateIndex, wellIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellExternalIdentifier(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        String __ret = __obj.getWellExternalIdentifier(plateIndex, wellIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        String __ret = __obj.getWellID(plateIndex, wellIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellRow(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int __ret = __obj.getWellRow(plateIndex, wellIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        String __ret = __obj.getWellType(plateIndex, wellIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellSampleID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        String __ret = __obj.getWellSampleID(plateIndex, wellIndex, wellSampleIndex, __current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellSampleIndex(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        int __ret = __obj.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellSamplePosX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        float __ret = __obj.getWellSamplePosX(plateIndex, wellIndex, wellSampleIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellSamplePosY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        float __ret = __obj.getWellSamplePosY(plateIndex, wellIndex, wellSampleIndex, __current);
        __os.writeFloat(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getWellSampleTimepoint(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        int __ret = __obj.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "getArcType",
        "getChannelComponentColorDomain",
        "getChannelComponentCount",
        "getChannelComponentIndex",
        "getDetectorCount",
        "getDetectorGain",
        "getDetectorID",
        "getDetectorManufacturer",
        "getDetectorModel",
        "getDetectorOffset",
        "getDetectorSerialNumber",
        "getDetectorSettingsDetector",
        "getDetectorSettingsGain",
        "getDetectorSettingsOffset",
        "getDetectorType",
        "getDetectorVoltage",
        "getDimensionsPhysicalSizeX",
        "getDimensionsPhysicalSizeY",
        "getDimensionsPhysicalSizeZ",
        "getDimensionsTimeIncrement",
        "getDimensionsWaveIncrement",
        "getDimensionsWaveStart",
        "getDisplayOptionsID",
        "getDisplayOptionsProjectionZStart",
        "getDisplayOptionsProjectionZStop",
        "getDisplayOptionsTimeTStart",
        "getDisplayOptionsTimeTStop",
        "getDisplayOptionsZoom",
        "getExperimentCount",
        "getExperimentDescription",
        "getExperimentID",
        "getExperimentType",
        "getExperimenterCount",
        "getExperimenterEmail",
        "getExperimenterFirstName",
        "getExperimenterID",
        "getExperimenterInstitution",
        "getExperimenterLastName",
        "getExperimenterMembershipCount",
        "getExperimenterMembershipGroup",
        "getFilamentType",
        "getGroupRefCount",
        "getImageCount",
        "getImageCreationDate",
        "getImageDefaultPixels",
        "getImageDescription",
        "getImageID",
        "getImageInstrumentRef",
        "getImageName",
        "getImagingEnvironmentAirPressure",
        "getImagingEnvironmentCO2Percent",
        "getImagingEnvironmentHumidity",
        "getImagingEnvironmentTemperature",
        "getInstrumentCount",
        "getInstrumentID",
        "getLaserFrequencyMultiplication",
        "getLaserLaserMedium",
        "getLaserPulse",
        "getLaserTuneable",
        "getLaserType",
        "getLaserWavelength",
        "getLightSourceCount",
        "getLightSourceID",
        "getLightSourceManufacturer",
        "getLightSourceModel",
        "getLightSourcePower",
        "getLightSourceSerialNumber",
        "getLightSourceSettingsAttenuation",
        "getLightSourceSettingsLightSource",
        "getLightSourceSettingsWavelength",
        "getLogicalChannelContrastMethod",
        "getLogicalChannelCount",
        "getLogicalChannelEmWave",
        "getLogicalChannelExWave",
        "getLogicalChannelFluor",
        "getLogicalChannelID",
        "getLogicalChannelIlluminationType",
        "getLogicalChannelMode",
        "getLogicalChannelName",
        "getLogicalChannelNdFilter",
        "getLogicalChannelOTF",
        "getLogicalChannelPhotometricInterpretation",
        "getLogicalChannelPinholeSize",
        "getLogicalChannelPockelCellSetting",
        "getLogicalChannelSamplesPerPixel",
        "getOMEXML",
        "getOTFCount",
        "getOTFID",
        "getOTFObjective",
        "getOTFOpticalAxisAveraged",
        "getOTFPixelType",
        "getOTFSizeX",
        "getOTFSizeY",
        "getObjectiveCalibratedMagnification",
        "getObjectiveCorrection",
        "getObjectiveCount",
        "getObjectiveID",
        "getObjectiveImmersion",
        "getObjectiveLensNA",
        "getObjectiveManufacturer",
        "getObjectiveModel",
        "getObjectiveNominalMagnification",
        "getObjectiveSerialNumber",
        "getObjectiveWorkingDistance",
        "getPixelsBigEndian",
        "getPixelsCount",
        "getPixelsDimensionOrder",
        "getPixelsID",
        "getPixelsPixelType",
        "getPixelsSizeC",
        "getPixelsSizeT",
        "getPixelsSizeX",
        "getPixelsSizeY",
        "getPixelsSizeZ",
        "getPlaneCount",
        "getPlaneTheC",
        "getPlaneTheT",
        "getPlaneTheZ",
        "getPlaneTimingDeltaT",
        "getPlaneTimingExposureTime",
        "getPlateCount",
        "getPlateDescription",
        "getPlateExternalIdentifier",
        "getPlateID",
        "getPlateName",
        "getPlateRefCount",
        "getPlateRefID",
        "getPlateStatus",
        "getROICount",
        "getROIID",
        "getROIT0",
        "getROIT1",
        "getROIX0",
        "getROIX1",
        "getROIY0",
        "getROIY1",
        "getROIZ0",
        "getROIZ1",
        "getReagentCount",
        "getReagentDescription",
        "getReagentID",
        "getReagentName",
        "getReagentReagentIdentifier",
        "getScreenAcquisitionCount",
        "getScreenAcquisitionEndTime",
        "getScreenAcquisitionID",
        "getScreenAcquisitionStartTime",
        "getScreenCount",
        "getScreenID",
        "getScreenName",
        "getScreenProtocolDescription",
        "getScreenProtocolIdentifier",
        "getScreenReagentSetDescription",
        "getScreenType",
        "getServant",
        "getStageLabelName",
        "getStageLabelX",
        "getStageLabelY",
        "getStageLabelZ",
        "getStagePositionPositionX",
        "getStagePositionPositionY",
        "getStagePositionPositionZ",
        "getTiffDataCount",
        "getTiffDataFileName",
        "getTiffDataFirstC",
        "getTiffDataFirstT",
        "getTiffDataFirstZ",
        "getTiffDataIFD",
        "getTiffDataNumPlanes",
        "getTiffDataUUID",
        "getUUID",
        "getWellColumn",
        "getWellCount",
        "getWellExternalDescription",
        "getWellExternalIdentifier",
        "getWellID",
        "getWellRow",
        "getWellSampleCount",
        "getWellSampleID",
        "getWellSampleIndex",
        "getWellSamplePosX",
        "getWellSamplePosY",
        "getWellSampleTimepoint",
        "getWellType",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping"
    };

    public IceInternal.DispatchStatus
    __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            return IceInternal.DispatchStatus.DispatchOperationNotExist;
        }

        switch(pos)
        {
            case 0:
            {
                return ___getArcType(this, in, __current);
            }
            case 1:
            {
                return ___getChannelComponentColorDomain(this, in, __current);
            }
            case 2:
            {
                return ___getChannelComponentCount(this, in, __current);
            }
            case 3:
            {
                return ___getChannelComponentIndex(this, in, __current);
            }
            case 4:
            {
                return ___getDetectorCount(this, in, __current);
            }
            case 5:
            {
                return ___getDetectorGain(this, in, __current);
            }
            case 6:
            {
                return ___getDetectorID(this, in, __current);
            }
            case 7:
            {
                return ___getDetectorManufacturer(this, in, __current);
            }
            case 8:
            {
                return ___getDetectorModel(this, in, __current);
            }
            case 9:
            {
                return ___getDetectorOffset(this, in, __current);
            }
            case 10:
            {
                return ___getDetectorSerialNumber(this, in, __current);
            }
            case 11:
            {
                return ___getDetectorSettingsDetector(this, in, __current);
            }
            case 12:
            {
                return ___getDetectorSettingsGain(this, in, __current);
            }
            case 13:
            {
                return ___getDetectorSettingsOffset(this, in, __current);
            }
            case 14:
            {
                return ___getDetectorType(this, in, __current);
            }
            case 15:
            {
                return ___getDetectorVoltage(this, in, __current);
            }
            case 16:
            {
                return ___getDimensionsPhysicalSizeX(this, in, __current);
            }
            case 17:
            {
                return ___getDimensionsPhysicalSizeY(this, in, __current);
            }
            case 18:
            {
                return ___getDimensionsPhysicalSizeZ(this, in, __current);
            }
            case 19:
            {
                return ___getDimensionsTimeIncrement(this, in, __current);
            }
            case 20:
            {
                return ___getDimensionsWaveIncrement(this, in, __current);
            }
            case 21:
            {
                return ___getDimensionsWaveStart(this, in, __current);
            }
            case 22:
            {
                return ___getDisplayOptionsID(this, in, __current);
            }
            case 23:
            {
                return ___getDisplayOptionsProjectionZStart(this, in, __current);
            }
            case 24:
            {
                return ___getDisplayOptionsProjectionZStop(this, in, __current);
            }
            case 25:
            {
                return ___getDisplayOptionsTimeTStart(this, in, __current);
            }
            case 26:
            {
                return ___getDisplayOptionsTimeTStop(this, in, __current);
            }
            case 27:
            {
                return ___getDisplayOptionsZoom(this, in, __current);
            }
            case 28:
            {
                return ___getExperimentCount(this, in, __current);
            }
            case 29:
            {
                return ___getExperimentDescription(this, in, __current);
            }
            case 30:
            {
                return ___getExperimentID(this, in, __current);
            }
            case 31:
            {
                return ___getExperimentType(this, in, __current);
            }
            case 32:
            {
                return ___getExperimenterCount(this, in, __current);
            }
            case 33:
            {
                return ___getExperimenterEmail(this, in, __current);
            }
            case 34:
            {
                return ___getExperimenterFirstName(this, in, __current);
            }
            case 35:
            {
                return ___getExperimenterID(this, in, __current);
            }
            case 36:
            {
                return ___getExperimenterInstitution(this, in, __current);
            }
            case 37:
            {
                return ___getExperimenterLastName(this, in, __current);
            }
            case 38:
            {
                return ___getExperimenterMembershipCount(this, in, __current);
            }
            case 39:
            {
                return ___getExperimenterMembershipGroup(this, in, __current);
            }
            case 40:
            {
                return ___getFilamentType(this, in, __current);
            }
            case 41:
            {
                return ___getGroupRefCount(this, in, __current);
            }
            case 42:
            {
                return ___getImageCount(this, in, __current);
            }
            case 43:
            {
                return ___getImageCreationDate(this, in, __current);
            }
            case 44:
            {
                return ___getImageDefaultPixels(this, in, __current);
            }
            case 45:
            {
                return ___getImageDescription(this, in, __current);
            }
            case 46:
            {
                return ___getImageID(this, in, __current);
            }
            case 47:
            {
                return ___getImageInstrumentRef(this, in, __current);
            }
            case 48:
            {
                return ___getImageName(this, in, __current);
            }
            case 49:
            {
                return ___getImagingEnvironmentAirPressure(this, in, __current);
            }
            case 50:
            {
                return ___getImagingEnvironmentCO2Percent(this, in, __current);
            }
            case 51:
            {
                return ___getImagingEnvironmentHumidity(this, in, __current);
            }
            case 52:
            {
                return ___getImagingEnvironmentTemperature(this, in, __current);
            }
            case 53:
            {
                return ___getInstrumentCount(this, in, __current);
            }
            case 54:
            {
                return ___getInstrumentID(this, in, __current);
            }
            case 55:
            {
                return ___getLaserFrequencyMultiplication(this, in, __current);
            }
            case 56:
            {
                return ___getLaserLaserMedium(this, in, __current);
            }
            case 57:
            {
                return ___getLaserPulse(this, in, __current);
            }
            case 58:
            {
                return ___getLaserTuneable(this, in, __current);
            }
            case 59:
            {
                return ___getLaserType(this, in, __current);
            }
            case 60:
            {
                return ___getLaserWavelength(this, in, __current);
            }
            case 61:
            {
                return ___getLightSourceCount(this, in, __current);
            }
            case 62:
            {
                return ___getLightSourceID(this, in, __current);
            }
            case 63:
            {
                return ___getLightSourceManufacturer(this, in, __current);
            }
            case 64:
            {
                return ___getLightSourceModel(this, in, __current);
            }
            case 65:
            {
                return ___getLightSourcePower(this, in, __current);
            }
            case 66:
            {
                return ___getLightSourceSerialNumber(this, in, __current);
            }
            case 67:
            {
                return ___getLightSourceSettingsAttenuation(this, in, __current);
            }
            case 68:
            {
                return ___getLightSourceSettingsLightSource(this, in, __current);
            }
            case 69:
            {
                return ___getLightSourceSettingsWavelength(this, in, __current);
            }
            case 70:
            {
                return ___getLogicalChannelContrastMethod(this, in, __current);
            }
            case 71:
            {
                return ___getLogicalChannelCount(this, in, __current);
            }
            case 72:
            {
                return ___getLogicalChannelEmWave(this, in, __current);
            }
            case 73:
            {
                return ___getLogicalChannelExWave(this, in, __current);
            }
            case 74:
            {
                return ___getLogicalChannelFluor(this, in, __current);
            }
            case 75:
            {
                return ___getLogicalChannelID(this, in, __current);
            }
            case 76:
            {
                return ___getLogicalChannelIlluminationType(this, in, __current);
            }
            case 77:
            {
                return ___getLogicalChannelMode(this, in, __current);
            }
            case 78:
            {
                return ___getLogicalChannelName(this, in, __current);
            }
            case 79:
            {
                return ___getLogicalChannelNdFilter(this, in, __current);
            }
            case 80:
            {
                return ___getLogicalChannelOTF(this, in, __current);
            }
            case 81:
            {
                return ___getLogicalChannelPhotometricInterpretation(this, in, __current);
            }
            case 82:
            {
                return ___getLogicalChannelPinholeSize(this, in, __current);
            }
            case 83:
            {
                return ___getLogicalChannelPockelCellSetting(this, in, __current);
            }
            case 84:
            {
                return ___getLogicalChannelSamplesPerPixel(this, in, __current);
            }
            case 85:
            {
                return ___getOMEXML(this, in, __current);
            }
            case 86:
            {
                return ___getOTFCount(this, in, __current);
            }
            case 87:
            {
                return ___getOTFID(this, in, __current);
            }
            case 88:
            {
                return ___getOTFObjective(this, in, __current);
            }
            case 89:
            {
                return ___getOTFOpticalAxisAveraged(this, in, __current);
            }
            case 90:
            {
                return ___getOTFPixelType(this, in, __current);
            }
            case 91:
            {
                return ___getOTFSizeX(this, in, __current);
            }
            case 92:
            {
                return ___getOTFSizeY(this, in, __current);
            }
            case 93:
            {
                return ___getObjectiveCalibratedMagnification(this, in, __current);
            }
            case 94:
            {
                return ___getObjectiveCorrection(this, in, __current);
            }
            case 95:
            {
                return ___getObjectiveCount(this, in, __current);
            }
            case 96:
            {
                return ___getObjectiveID(this, in, __current);
            }
            case 97:
            {
                return ___getObjectiveImmersion(this, in, __current);
            }
            case 98:
            {
                return ___getObjectiveLensNA(this, in, __current);
            }
            case 99:
            {
                return ___getObjectiveManufacturer(this, in, __current);
            }
            case 100:
            {
                return ___getObjectiveModel(this, in, __current);
            }
            case 101:
            {
                return ___getObjectiveNominalMagnification(this, in, __current);
            }
            case 102:
            {
                return ___getObjectiveSerialNumber(this, in, __current);
            }
            case 103:
            {
                return ___getObjectiveWorkingDistance(this, in, __current);
            }
            case 104:
            {
                return ___getPixelsBigEndian(this, in, __current);
            }
            case 105:
            {
                return ___getPixelsCount(this, in, __current);
            }
            case 106:
            {
                return ___getPixelsDimensionOrder(this, in, __current);
            }
            case 107:
            {
                return ___getPixelsID(this, in, __current);
            }
            case 108:
            {
                return ___getPixelsPixelType(this, in, __current);
            }
            case 109:
            {
                return ___getPixelsSizeC(this, in, __current);
            }
            case 110:
            {
                return ___getPixelsSizeT(this, in, __current);
            }
            case 111:
            {
                return ___getPixelsSizeX(this, in, __current);
            }
            case 112:
            {
                return ___getPixelsSizeY(this, in, __current);
            }
            case 113:
            {
                return ___getPixelsSizeZ(this, in, __current);
            }
            case 114:
            {
                return ___getPlaneCount(this, in, __current);
            }
            case 115:
            {
                return ___getPlaneTheC(this, in, __current);
            }
            case 116:
            {
                return ___getPlaneTheT(this, in, __current);
            }
            case 117:
            {
                return ___getPlaneTheZ(this, in, __current);
            }
            case 118:
            {
                return ___getPlaneTimingDeltaT(this, in, __current);
            }
            case 119:
            {
                return ___getPlaneTimingExposureTime(this, in, __current);
            }
            case 120:
            {
                return ___getPlateCount(this, in, __current);
            }
            case 121:
            {
                return ___getPlateDescription(this, in, __current);
            }
            case 122:
            {
                return ___getPlateExternalIdentifier(this, in, __current);
            }
            case 123:
            {
                return ___getPlateID(this, in, __current);
            }
            case 124:
            {
                return ___getPlateName(this, in, __current);
            }
            case 125:
            {
                return ___getPlateRefCount(this, in, __current);
            }
            case 126:
            {
                return ___getPlateRefID(this, in, __current);
            }
            case 127:
            {
                return ___getPlateStatus(this, in, __current);
            }
            case 128:
            {
                return ___getROICount(this, in, __current);
            }
            case 129:
            {
                return ___getROIID(this, in, __current);
            }
            case 130:
            {
                return ___getROIT0(this, in, __current);
            }
            case 131:
            {
                return ___getROIT1(this, in, __current);
            }
            case 132:
            {
                return ___getROIX0(this, in, __current);
            }
            case 133:
            {
                return ___getROIX1(this, in, __current);
            }
            case 134:
            {
                return ___getROIY0(this, in, __current);
            }
            case 135:
            {
                return ___getROIY1(this, in, __current);
            }
            case 136:
            {
                return ___getROIZ0(this, in, __current);
            }
            case 137:
            {
                return ___getROIZ1(this, in, __current);
            }
            case 138:
            {
                return ___getReagentCount(this, in, __current);
            }
            case 139:
            {
                return ___getReagentDescription(this, in, __current);
            }
            case 140:
            {
                return ___getReagentID(this, in, __current);
            }
            case 141:
            {
                return ___getReagentName(this, in, __current);
            }
            case 142:
            {
                return ___getReagentReagentIdentifier(this, in, __current);
            }
            case 143:
            {
                return ___getScreenAcquisitionCount(this, in, __current);
            }
            case 144:
            {
                return ___getScreenAcquisitionEndTime(this, in, __current);
            }
            case 145:
            {
                return ___getScreenAcquisitionID(this, in, __current);
            }
            case 146:
            {
                return ___getScreenAcquisitionStartTime(this, in, __current);
            }
            case 147:
            {
                return ___getScreenCount(this, in, __current);
            }
            case 148:
            {
                return ___getScreenID(this, in, __current);
            }
            case 149:
            {
                return ___getScreenName(this, in, __current);
            }
            case 150:
            {
                return ___getScreenProtocolDescription(this, in, __current);
            }
            case 151:
            {
                return ___getScreenProtocolIdentifier(this, in, __current);
            }
            case 152:
            {
                return ___getScreenReagentSetDescription(this, in, __current);
            }
            case 153:
            {
                return ___getScreenType(this, in, __current);
            }
            case 154:
            {
                return ___getServant(this, in, __current);
            }
            case 155:
            {
                return ___getStageLabelName(this, in, __current);
            }
            case 156:
            {
                return ___getStageLabelX(this, in, __current);
            }
            case 157:
            {
                return ___getStageLabelY(this, in, __current);
            }
            case 158:
            {
                return ___getStageLabelZ(this, in, __current);
            }
            case 159:
            {
                return ___getStagePositionPositionX(this, in, __current);
            }
            case 160:
            {
                return ___getStagePositionPositionY(this, in, __current);
            }
            case 161:
            {
                return ___getStagePositionPositionZ(this, in, __current);
            }
            case 162:
            {
                return ___getTiffDataCount(this, in, __current);
            }
            case 163:
            {
                return ___getTiffDataFileName(this, in, __current);
            }
            case 164:
            {
                return ___getTiffDataFirstC(this, in, __current);
            }
            case 165:
            {
                return ___getTiffDataFirstT(this, in, __current);
            }
            case 166:
            {
                return ___getTiffDataFirstZ(this, in, __current);
            }
            case 167:
            {
                return ___getTiffDataIFD(this, in, __current);
            }
            case 168:
            {
                return ___getTiffDataNumPlanes(this, in, __current);
            }
            case 169:
            {
                return ___getTiffDataUUID(this, in, __current);
            }
            case 170:
            {
                return ___getUUID(this, in, __current);
            }
            case 171:
            {
                return ___getWellColumn(this, in, __current);
            }
            case 172:
            {
                return ___getWellCount(this, in, __current);
            }
            case 173:
            {
                return ___getWellExternalDescription(this, in, __current);
            }
            case 174:
            {
                return ___getWellExternalIdentifier(this, in, __current);
            }
            case 175:
            {
                return ___getWellID(this, in, __current);
            }
            case 176:
            {
                return ___getWellRow(this, in, __current);
            }
            case 177:
            {
                return ___getWellSampleCount(this, in, __current);
            }
            case 178:
            {
                return ___getWellSampleID(this, in, __current);
            }
            case 179:
            {
                return ___getWellSampleIndex(this, in, __current);
            }
            case 180:
            {
                return ___getWellSamplePosX(this, in, __current);
            }
            case 181:
            {
                return ___getWellSamplePosY(this, in, __current);
            }
            case 182:
            {
                return ___getWellSampleTimepoint(this, in, __current);
            }
            case 183:
            {
                return ___getWellType(this, in, __current);
            }
            case 184:
            {
                return ___ice_id(this, in, __current);
            }
            case 185:
            {
                return ___ice_ids(this, in, __current);
            }
            case 186:
            {
                return ___ice_isA(this, in, __current);
            }
            case 187:
            {
                return ___ice_ping(this, in, __current);
            }
        }

        assert(false);
        return IceInternal.DispatchStatus.DispatchOperationNotExist;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeTypeId(ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void
    __read(IceInternal.BasicStream __is, boolean __rid)
    {
        if(__rid)
        {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void
    __write(Ice.OutputStream __outS)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type formats::MetadataRetrieve was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type formats::MetadataRetrieve was not generated with stream support";
        throw ex;
    }
}
