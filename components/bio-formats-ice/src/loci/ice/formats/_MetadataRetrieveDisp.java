// **********************************************************************
//
// Copyright (c) 2003-2008 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.0

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

    public final String
    getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        return getChannelComponentPixels(imageIndex, logicalChannelIndex, channelComponentIndex, null);
    }

    public final String
    getCircleCx(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getCircleCx(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getCircleCy(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getCircleCy(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getCircleID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getCircleID(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getCircleR(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getCircleR(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getCircleTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getCircleTransform(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getContactExperimenter(int groupIndex)
    {
        return getContactExperimenter(groupIndex, null);
    }

    public final int
    getDatasetCount()
    {
        return getDatasetCount(null);
    }

    public final String
    getDatasetDescription(int datasetIndex)
    {
        return getDatasetDescription(datasetIndex, null);
    }

    public final String
    getDatasetExperimenterRef(int datasetIndex)
    {
        return getDatasetExperimenterRef(datasetIndex, null);
    }

    public final String
    getDatasetGroupRef(int datasetIndex)
    {
        return getDatasetGroupRef(datasetIndex, null);
    }

    public final String
    getDatasetID(int datasetIndex)
    {
        return getDatasetID(datasetIndex, null);
    }

    public final boolean
    getDatasetLocked(int datasetIndex)
    {
        return getDatasetLocked(datasetIndex, null);
    }

    public final String
    getDatasetName(int datasetIndex)
    {
        return getDatasetName(datasetIndex, null);
    }

    public final int
    getDatasetRefCount(int imageIndex)
    {
        return getDatasetRefCount(imageIndex, null);
    }

    public final String
    getDatasetRefID(int imageIndex, int datasetRefIndex)
    {
        return getDatasetRefID(imageIndex, datasetRefIndex, null);
    }

    public final float
    getDetectorAmplificationGain(int instrumentIndex, int detectorIndex)
    {
        return getDetectorAmplificationGain(instrumentIndex, detectorIndex, null);
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
    getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex)
    {
        return getDetectorSettingsBinning(imageIndex, logicalChannelIndex, null);
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

    public final float
    getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex)
    {
        return getDetectorSettingsReadOutRate(imageIndex, logicalChannelIndex, null);
    }

    public final float
    getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex)
    {
        return getDetectorSettingsVoltage(imageIndex, logicalChannelIndex, null);
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
    getDetectorZoom(int instrumentIndex, int detectorIndex)
    {
        return getDetectorZoom(instrumentIndex, detectorIndex, null);
    }

    public final int
    getDichroicCount(int instrumentIndex)
    {
        return getDichroicCount(instrumentIndex, null);
    }

    public final String
    getDichroicID(int instrumentIndex, int dichroicIndex)
    {
        return getDichroicID(instrumentIndex, dichroicIndex, null);
    }

    public final String
    getDichroicLotNumber(int instrumentIndex, int dichroicIndex)
    {
        return getDichroicLotNumber(instrumentIndex, dichroicIndex, null);
    }

    public final String
    getDichroicManufacturer(int instrumentIndex, int dichroicIndex)
    {
        return getDichroicManufacturer(instrumentIndex, dichroicIndex, null);
    }

    public final String
    getDichroicModel(int instrumentIndex, int dichroicIndex)
    {
        return getDichroicModel(instrumentIndex, dichroicIndex, null);
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
    getDisplayOptionsDisplay(int imageIndex)
    {
        return getDisplayOptionsDisplay(imageIndex, null);
    }

    public final String
    getDisplayOptionsID(int imageIndex)
    {
        return getDisplayOptionsID(imageIndex, null);
    }

    public final float
    getDisplayOptionsZoom(int imageIndex)
    {
        return getDisplayOptionsZoom(imageIndex, null);
    }

    public final String
    getEllipseCx(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getEllipseCx(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getEllipseCy(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getEllipseCy(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getEllipseID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getEllipseID(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getEllipseRx(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getEllipseRx(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getEllipseRy(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getEllipseRy(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getEllipseTransform(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getEmFilterLotNumber(int instrumentIndex, int filterIndex)
    {
        return getEmFilterLotNumber(instrumentIndex, filterIndex, null);
    }

    public final String
    getEmFilterManufacturer(int instrumentIndex, int filterIndex)
    {
        return getEmFilterManufacturer(instrumentIndex, filterIndex, null);
    }

    public final String
    getEmFilterModel(int instrumentIndex, int filterIndex)
    {
        return getEmFilterModel(instrumentIndex, filterIndex, null);
    }

    public final String
    getEmFilterType(int instrumentIndex, int filterIndex)
    {
        return getEmFilterType(instrumentIndex, filterIndex, null);
    }

    public final String
    getExFilterLotNumber(int instrumentIndex, int filterIndex)
    {
        return getExFilterLotNumber(instrumentIndex, filterIndex, null);
    }

    public final String
    getExFilterManufacturer(int instrumentIndex, int filterIndex)
    {
        return getExFilterManufacturer(instrumentIndex, filterIndex, null);
    }

    public final String
    getExFilterModel(int instrumentIndex, int filterIndex)
    {
        return getExFilterModel(instrumentIndex, filterIndex, null);
    }

    public final String
    getExFilterType(int instrumentIndex, int filterIndex)
    {
        return getExFilterType(instrumentIndex, filterIndex, null);
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
    getExperimentExperimenterRef(int experimentIndex)
    {
        return getExperimentExperimenterRef(experimentIndex, null);
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
    getExperimenterOMEName(int experimenterIndex)
    {
        return getExperimenterOMEName(experimenterIndex, null);
    }

    public final String
    getFilamentType(int instrumentIndex, int lightSourceIndex)
    {
        return getFilamentType(instrumentIndex, lightSourceIndex, null);
    }

    public final int
    getFilterCount(int instrumentIndex)
    {
        return getFilterCount(instrumentIndex, null);
    }

    public final String
    getFilterFilterWheel(int instrumentIndex, int filterIndex)
    {
        return getFilterFilterWheel(instrumentIndex, filterIndex, null);
    }

    public final String
    getFilterID(int instrumentIndex, int filterIndex)
    {
        return getFilterID(instrumentIndex, filterIndex, null);
    }

    public final String
    getFilterLotNumber(int instrumentIndex, int filterIndex)
    {
        return getFilterLotNumber(instrumentIndex, filterIndex, null);
    }

    public final String
    getFilterManufacturer(int instrumentIndex, int filterIndex)
    {
        return getFilterManufacturer(instrumentIndex, filterIndex, null);
    }

    public final String
    getFilterModel(int instrumentIndex, int filterIndex)
    {
        return getFilterModel(instrumentIndex, filterIndex, null);
    }

    public final int
    getFilterSetCount(int instrumentIndex)
    {
        return getFilterSetCount(instrumentIndex, null);
    }

    public final String
    getFilterSetDichroic(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetDichroic(instrumentIndex, filterSetIndex, null);
    }

    public final String
    getFilterSetEmFilter(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetEmFilter(instrumentIndex, filterSetIndex, null);
    }

    public final String
    getFilterSetExFilter(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetExFilter(instrumentIndex, filterSetIndex, null);
    }

    public final String
    getFilterSetID(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetID(instrumentIndex, filterSetIndex, null);
    }

    public final String
    getFilterSetLotNumber(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetLotNumber(instrumentIndex, filterSetIndex, null);
    }

    public final String
    getFilterSetManufacturer(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetManufacturer(instrumentIndex, filterSetIndex, null);
    }

    public final String
    getFilterSetModel(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetModel(instrumentIndex, filterSetIndex, null);
    }

    public final String
    getFilterType(int instrumentIndex, int filterIndex)
    {
        return getFilterType(instrumentIndex, filterIndex, null);
    }

    public final int
    getGroupCount()
    {
        return getGroupCount(null);
    }

    public final String
    getGroupID(int groupIndex)
    {
        return getGroupID(groupIndex, null);
    }

    public final String
    getGroupName(int groupIndex)
    {
        return getGroupName(groupIndex, null);
    }

    public final int
    getGroupRefCount(int experimenterIndex)
    {
        return getGroupRefCount(experimenterIndex, null);
    }

    public final String
    getImageAcquiredPixels(int imageIndex)
    {
        return getImageAcquiredPixels(imageIndex, null);
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
    getImageExperimentRef(int imageIndex)
    {
        return getImageExperimentRef(imageIndex, null);
    }

    public final String
    getImageExperimenterRef(int imageIndex)
    {
        return getImageExperimenterRef(imageIndex, null);
    }

    public final String
    getImageGroupRef(int imageIndex)
    {
        return getImageGroupRef(imageIndex, null);
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

    public final boolean
    getLaserPockelCell(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserPockelCell(instrumentIndex, lightSourceIndex, null);
    }

    public final String
    getLaserPulse(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserPulse(instrumentIndex, lightSourceIndex, null);
    }

    public final boolean
    getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserRepetitionRate(instrumentIndex, lightSourceIndex, null);
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

    public final float
    getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        return getLightSourceRefAttenuation(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null);
    }

    public final int
    getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex)
    {
        return getLightSourceRefCount(imageIndex, microbeamManipulationIndex, null);
    }

    public final String
    getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        return getLightSourceRefLightSource(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null);
    }

    public final int
    getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        return getLightSourceRefWavelength(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null);
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
    getLineID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getLineID(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getLineTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getLineTransform(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getLineX1(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getLineX1(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getLineX2(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getLineX2(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getLineY1(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getLineY1(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getLineY2(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getLineY2(imageIndex, roiIndex, shapeIndex, null);
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

    public final String
    getLogicalChannelDetector(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelDetector(imageIndex, logicalChannelIndex, null);
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
    getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelFilterSet(imageIndex, logicalChannelIndex, null);
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
    getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelLightSource(imageIndex, logicalChannelIndex, null);
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
    getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelSecondaryEmissionFilter(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelSecondaryExcitationFilter(imageIndex, logicalChannelIndex, null);
    }

    public final String
    getMaskHeight(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskHeight(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getMaskID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskID(imageIndex, roiIndex, shapeIndex, null);
    }

    public final boolean
    getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskPixelsBigEndian(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskPixelsBinData(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskPixelsExtendedPixelType(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskPixelsID(imageIndex, roiIndex, shapeIndex, null);
    }

    public final int
    getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskPixelsSizeX(imageIndex, roiIndex, shapeIndex, null);
    }

    public final int
    getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskPixelsSizeY(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getMaskTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskTransform(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getMaskWidth(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskWidth(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getMaskX(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskX(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getMaskY(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskY(imageIndex, roiIndex, shapeIndex, null);
    }

    public final int
    getMicrobeamManipulationCount(int imageIndex)
    {
        return getMicrobeamManipulationCount(imageIndex, null);
    }

    public final String
    getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex)
    {
        return getMicrobeamManipulationExperimenterRef(imageIndex, microbeamManipulationIndex, null);
    }

    public final String
    getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex)
    {
        return getMicrobeamManipulationID(imageIndex, microbeamManipulationIndex, null);
    }

    public final int
    getMicrobeamManipulationRefCount(int experimentIndex)
    {
        return getMicrobeamManipulationRefCount(experimentIndex, null);
    }

    public final String
    getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex)
    {
        return getMicrobeamManipulationRefID(experimentIndex, microbeamManipulationRefIndex, null);
    }

    public final String
    getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex)
    {
        return getMicrobeamManipulationType(imageIndex, microbeamManipulationIndex, null);
    }

    public final String
    getMicroscopeID(int instrumentIndex)
    {
        return getMicroscopeID(instrumentIndex, null);
    }

    public final String
    getMicroscopeManufacturer(int instrumentIndex)
    {
        return getMicroscopeManufacturer(instrumentIndex, null);
    }

    public final String
    getMicroscopeModel(int instrumentIndex)
    {
        return getMicroscopeModel(instrumentIndex, null);
    }

    public final String
    getMicroscopeSerialNumber(int instrumentIndex)
    {
        return getMicroscopeSerialNumber(instrumentIndex, null);
    }

    public final String
    getMicroscopeType(int instrumentIndex)
    {
        return getMicroscopeType(instrumentIndex, null);
    }

    public final String
    getOMEXML()
    {
        return getOMEXML(null);
    }

    public final String
    getOTFBinaryFile(int instrumentIndex, int otfIndex)
    {
        return getOTFBinaryFile(instrumentIndex, otfIndex, null);
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

    public final boolean
    getObjectiveIris(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveIris(instrumentIndex, objectiveIndex, null);
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
    getObjectiveSettingsCorrectionCollar(int imageIndex)
    {
        return getObjectiveSettingsCorrectionCollar(imageIndex, null);
    }

    public final String
    getObjectiveSettingsMedium(int imageIndex)
    {
        return getObjectiveSettingsMedium(imageIndex, null);
    }

    public final String
    getObjectiveSettingsObjective(int imageIndex)
    {
        return getObjectiveSettingsObjective(imageIndex, null);
    }

    public final float
    getObjectiveSettingsRefractiveIndex(int imageIndex)
    {
        return getObjectiveSettingsRefractiveIndex(imageIndex, null);
    }

    public final float
    getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveWorkingDistance(instrumentIndex, objectiveIndex, null);
    }

    public final String
    getPathD(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPathD(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getPathID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPathID(imageIndex, roiIndex, shapeIndex, null);
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

    public final String
    getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneHashSHA1(imageIndex, pixelsIndex, planeIndex, null);
    }

    public final String
    getPlaneID(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneID(imageIndex, pixelsIndex, planeIndex, null);
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

    public final String
    getPlateColumnNamingConvention(int plateIndex)
    {
        return getPlateColumnNamingConvention(plateIndex, null);
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

    public final int
    getPlateRefSample(int screenIndex, int plateRefIndex)
    {
        return getPlateRefSample(screenIndex, plateRefIndex, null);
    }

    public final String
    getPlateRefWell(int screenIndex, int plateRefIndex)
    {
        return getPlateRefWell(screenIndex, plateRefIndex, null);
    }

    public final String
    getPlateRowNamingConvention(int plateIndex)
    {
        return getPlateRowNamingConvention(plateIndex, null);
    }

    public final String
    getPlateStatus(int plateIndex)
    {
        return getPlateStatus(plateIndex, null);
    }

    public final double
    getPlateWellOriginX(int plateIndex)
    {
        return getPlateWellOriginX(plateIndex, null);
    }

    public final double
    getPlateWellOriginY(int plateIndex)
    {
        return getPlateWellOriginY(plateIndex, null);
    }

    public final String
    getPointCx(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPointCx(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getPointCy(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPointCy(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getPointID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPointID(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getPointR(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPointR(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getPointTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPointTransform(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getPolygonID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPolygonID(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPolygonPoints(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPolygonTransform(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getPolylineID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPolylineID(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPolylinePoints(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPolylineTransform(imageIndex, roiIndex, shapeIndex, null);
    }

    public final int
    getProjectCount()
    {
        return getProjectCount(null);
    }

    public final String
    getProjectDescription(int projectIndex)
    {
        return getProjectDescription(projectIndex, null);
    }

    public final String
    getProjectExperimenterRef(int projectIndex)
    {
        return getProjectExperimenterRef(projectIndex, null);
    }

    public final String
    getProjectGroupRef(int projectIndex)
    {
        return getProjectGroupRef(projectIndex, null);
    }

    public final String
    getProjectID(int projectIndex)
    {
        return getProjectID(projectIndex, null);
    }

    public final String
    getProjectName(int projectIndex)
    {
        return getProjectName(projectIndex, null);
    }

    public final int
    getProjectRefCount(int datasetIndex)
    {
        return getProjectRefCount(datasetIndex, null);
    }

    public final String
    getProjectRefID(int datasetIndex, int projectRefIndex)
    {
        return getProjectRefID(datasetIndex, projectRefIndex, null);
    }

    public final String
    getPumpLightSource(int instrumentIndex, int lightSourceIndex)
    {
        return getPumpLightSource(instrumentIndex, lightSourceIndex, null);
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
    getROIRefCount(int imageIndex, int microbeamManipulationIndex)
    {
        return getROIRefCount(imageIndex, microbeamManipulationIndex, null);
    }

    public final String
    getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex)
    {
        return getROIRefID(imageIndex, microbeamManipulationIndex, roiRefIndex, null);
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

    public final String
    getRectHeight(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getRectHeight(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getRectID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getRectID(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getRectTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getRectTransform(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getRectWidth(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getRectWidth(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getRectX(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getRectX(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getRectY(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getRectY(imageIndex, roiIndex, shapeIndex, null);
    }

    public final int
    getRegionCount(int imageIndex)
    {
        return getRegionCount(imageIndex, null);
    }

    public final String
    getRegionID(int imageIndex, int regionIndex)
    {
        return getRegionID(imageIndex, regionIndex, null);
    }

    public final String
    getRegionName(int imageIndex, int regionIndex)
    {
        return getRegionName(imageIndex, regionIndex, null);
    }

    public final String
    getRegionTag(int imageIndex, int regionIndex)
    {
        return getRegionTag(imageIndex, regionIndex, null);
    }

    public final int
    getRoiLinkCount(int imageIndex, int roiIndex)
    {
        return getRoiLinkCount(imageIndex, roiIndex, null);
    }

    public final String
    getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex)
    {
        return getRoiLinkDirection(imageIndex, roiIndex, roiLinkIndex, null);
    }

    public final String
    getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex)
    {
        return getRoiLinkName(imageIndex, roiIndex, roiLinkIndex, null);
    }

    public final String
    getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex)
    {
        return getRoiLinkRef(imageIndex, roiIndex, roiLinkIndex, null);
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
    getScreenDescription(int screenIndex)
    {
        return getScreenDescription(screenIndex, null);
    }

    public final String
    getScreenExtern(int screenIndex)
    {
        return getScreenExtern(screenIndex, null);
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
    getScreenReagentSetIdentifier(int screenIndex)
    {
        return getScreenReagentSetIdentifier(screenIndex, null);
    }

    public final int
    getScreenRefCount(int plateIndex)
    {
        return getScreenRefCount(plateIndex, null);
    }

    public final String
    getScreenRefID(int plateIndex, int screenRefIndex)
    {
        return getScreenRefID(plateIndex, screenRefIndex, null);
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
    getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeBaselineShift(imageIndex, roiIndex, shapeIndex, null);
    }

    public final int
    getShapeCount(int imageIndex, int roiIndex)
    {
        return getShapeCount(imageIndex, roiIndex, null);
    }

    public final String
    getShapeDirection(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeDirection(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFillColor(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFillOpacity(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFillRule(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFontFamily(imageIndex, roiIndex, shapeIndex, null);
    }

    public final int
    getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFontSize(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFontStretch(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFontStyle(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFontVariant(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFontWeight(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeG(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeG(imageIndex, roiIndex, shapeIndex, null);
    }

    public final int
    getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeGlyphOrientationVertical(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeID(imageIndex, roiIndex, shapeIndex, null);
    }

    public final boolean
    getShapeLocked(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeLocked(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeAttribute(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeColor(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeDashArray(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeLineCap(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeLineJoin(imageIndex, roiIndex, shapeIndex, null);
    }

    public final int
    getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeMiterLimit(imageIndex, roiIndex, shapeIndex, null);
    }

    public final float
    getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeOpacity(imageIndex, roiIndex, shapeIndex, null);
    }

    public final int
    getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeWidth(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeText(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeText(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeTextAnchor(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeTextDecoration(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeTextFill(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeTextStroke(imageIndex, roiIndex, shapeIndex, null);
    }

    public final int
    getShapeTheT(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeTheT(imageIndex, roiIndex, shapeIndex, null);
    }

    public final int
    getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeTheZ(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeVectorEffect(imageIndex, roiIndex, shapeIndex, null);
    }

    public final boolean
    getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeVisibility(imageIndex, roiIndex, shapeIndex, null);
    }

    public final String
    getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeWritingMode(imageIndex, roiIndex, shapeIndex, null);
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

    public final String
    getThumbnailHref(int imageIndex)
    {
        return getThumbnailHref(imageIndex, null);
    }

    public final String
    getThumbnailID(int imageIndex)
    {
        return getThumbnailID(imageIndex, null);
    }

    public final String
    getThumbnailMIMEtype(int imageIndex)
    {
        return getThumbnailMIMEtype(imageIndex, null);
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

    public final int
    getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex)
    {
        return getTransmittanceRangeCutIn(instrumentIndex, filterIndex, null);
    }

    public final int
    getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex)
    {
        return getTransmittanceRangeCutInTolerance(instrumentIndex, filterIndex, null);
    }

    public final int
    getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex)
    {
        return getTransmittanceRangeCutOut(instrumentIndex, filterIndex, null);
    }

    public final int
    getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex)
    {
        return getTransmittanceRangeCutOutTolerance(instrumentIndex, filterIndex, null);
    }

    public final int
    getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex)
    {
        return getTransmittanceRangeTransmittance(instrumentIndex, filterIndex, null);
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

    public final String
    getWellReagent(int plateIndex, int wellIndex)
    {
        return getWellReagent(plateIndex, wellIndex, null);
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

    public final String
    getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex)
    {
        return getWellSampleImageRef(plateIndex, wellIndex, wellSampleIndex, null);
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
    getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex)
    {
        return getWellSampleRefCount(screenIndex, screenAcquisitionIndex, null);
    }

    public final String
    getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex)
    {
        return getWellSampleRefID(screenIndex, screenAcquisitionIndex, wellSampleRefIndex, null);
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

    public static Ice.DispatchStatus
    ___getServant(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        MetadataRetrieve __ret = __obj.getServant(__current);
        __os.writeObject(__ret);
        __os.writePendingObjects();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getOMEXML(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getOMEXML(__current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getChannelComponentCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getChannelComponentCount(imageIndex, logicalChannelIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDatasetCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getDatasetCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDatasetRefCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getDatasetRefCount(imageIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getDetectorCount(instrumentIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDichroicCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getDichroicCount(instrumentIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimentCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getExperimentCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimenterCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getExperimenterCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimenterMembershipCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getExperimenterMembershipCount(experimenterIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getFilterCount(instrumentIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterSetCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getFilterSetCount(instrumentIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getGroupCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getGroupCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getGroupRefCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getGroupRefCount(experimenterIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImageCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getImageCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getInstrumentCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getInstrumentCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getLightSourceCount(instrumentIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceRefCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getLightSourceRefCount(imageIndex, microbeamManipulationIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getLogicalChannelCount(imageIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMicrobeamManipulationCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getMicrobeamManipulationCount(imageIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMicrobeamManipulationRefCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getMicrobeamManipulationRefCount(experimentIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getOTFCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getOTFCount(instrumentIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getObjectiveCount(instrumentIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPixelsCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPixelsCount(imageIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlaneCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPlaneCount(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPlateCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateRefCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPlateRefCount(screenIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getProjectCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getProjectCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getProjectRefCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getProjectRefCount(datasetIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getROICount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getROICount(imageIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getROIRefCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getROIRefCount(imageIndex, microbeamManipulationIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getReagentCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getReagentCount(screenIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRegionCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getRegionCount(imageIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRoiLinkCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getRoiLinkCount(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getScreenCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenAcquisitionCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getScreenAcquisitionCount(screenIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenRefCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getScreenRefCount(plateIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getShapeCount(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTiffDataCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getTiffDataCount(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getWellCount(plateIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellSampleCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getWellSampleCount(plateIndex, wellIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellSampleRefCount(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getWellSampleRefCount(screenIndex, screenAcquisitionIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getUUID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getUUID(__current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getArcType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getArcType(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getChannelComponentColorDomain(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getChannelComponentIndex(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getChannelComponentPixels(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getChannelComponentPixels(imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getCircleCx(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getCircleCx(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getCircleCy(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getCircleCy(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getCircleID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getCircleID(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getCircleR(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getCircleR(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getCircleTransform(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getCircleTransform(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getContactExperimenter(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int groupIndex;
        groupIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getContactExperimenter(groupIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDatasetDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDatasetDescription(datasetIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDatasetExperimenterRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDatasetExperimenterRef(datasetIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDatasetGroupRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDatasetGroupRef(datasetIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDatasetID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDatasetID(datasetIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDatasetLocked(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.getDatasetLocked(datasetIndex, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDatasetName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDatasetName(datasetIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDatasetRefID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int datasetRefIndex;
        datasetRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDatasetRefID(imageIndex, datasetRefIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorAmplificationGain(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDetectorAmplificationGain(instrumentIndex, detectorIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorGain(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDetectorGain(instrumentIndex, detectorIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDetectorID(instrumentIndex, detectorIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorManufacturer(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDetectorManufacturer(instrumentIndex, detectorIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorModel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDetectorModel(instrumentIndex, detectorIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorOffset(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDetectorOffset(instrumentIndex, detectorIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorSerialNumber(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDetectorSerialNumber(instrumentIndex, detectorIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDetectorType(instrumentIndex, detectorIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorVoltage(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDetectorVoltage(instrumentIndex, detectorIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorZoom(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDetectorZoom(instrumentIndex, detectorIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorSettingsBinning(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDetectorSettingsBinning(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorSettingsDetector(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDetectorSettingsDetector(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorSettingsGain(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDetectorSettingsGain(imageIndex, logicalChannelIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorSettingsOffset(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDetectorSettingsOffset(imageIndex, logicalChannelIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorSettingsReadOutRate(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDetectorSettingsReadOutRate(imageIndex, logicalChannelIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDetectorSettingsVoltage(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDetectorSettingsVoltage(imageIndex, logicalChannelIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDichroicID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int dichroicIndex;
        dichroicIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDichroicID(instrumentIndex, dichroicIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDichroicLotNumber(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int dichroicIndex;
        dichroicIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDichroicLotNumber(instrumentIndex, dichroicIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDichroicManufacturer(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int dichroicIndex;
        dichroicIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDichroicManufacturer(instrumentIndex, dichroicIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDichroicModel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int dichroicIndex;
        dichroicIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDichroicModel(instrumentIndex, dichroicIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDimensionsPhysicalSizeX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDimensionsPhysicalSizeX(imageIndex, pixelsIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDimensionsPhysicalSizeY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDimensionsPhysicalSizeY(imageIndex, pixelsIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDimensionsPhysicalSizeZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDimensionsTimeIncrement(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDimensionsTimeIncrement(imageIndex, pixelsIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDimensionsWaveIncrement(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getDimensionsWaveIncrement(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDimensionsWaveStart(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getDimensionsWaveStart(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDisplayOptionsDisplay(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDisplayOptionsDisplay(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDisplayOptionsID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDisplayOptionsID(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDisplayOptionsZoom(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getDisplayOptionsZoom(imageIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getEllipseCx(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getEllipseCx(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getEllipseCy(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getEllipseCy(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getEllipseID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getEllipseID(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getEllipseRx(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getEllipseRx(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getEllipseRy(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getEllipseRy(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getEllipseTransform(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getEllipseTransform(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getEmFilterLotNumber(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getEmFilterLotNumber(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getEmFilterManufacturer(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getEmFilterManufacturer(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getEmFilterModel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getEmFilterModel(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getEmFilterType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getEmFilterType(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExFilterLotNumber(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExFilterLotNumber(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExFilterManufacturer(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExFilterManufacturer(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExFilterModel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExFilterModel(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExFilterType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExFilterType(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimentDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExperimentDescription(experimentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimentExperimenterRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExperimentExperimenterRef(experimentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimentID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExperimentID(experimentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimentType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExperimentType(experimentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimenterEmail(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExperimenterEmail(experimenterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimenterFirstName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExperimenterFirstName(experimenterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimenterID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExperimenterID(experimenterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimenterInstitution(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExperimenterInstitution(experimenterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimenterLastName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExperimenterLastName(experimenterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimenterOMEName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExperimenterOMEName(experimenterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimenterMembershipGroup(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        int groupRefIndex;
        groupRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getExperimenterMembershipGroup(experimenterIndex, groupRefIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilamentType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilamentType(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterFilterWheel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterFilterWheel(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterID(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterLotNumber(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterLotNumber(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterManufacturer(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterManufacturer(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterModel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterModel(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterType(instrumentIndex, filterIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterSetDichroic(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterSetDichroic(instrumentIndex, filterSetIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterSetEmFilter(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterSetEmFilter(instrumentIndex, filterSetIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterSetExFilter(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterSetExFilter(instrumentIndex, filterSetIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterSetID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterSetID(instrumentIndex, filterSetIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterSetLotNumber(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterSetLotNumber(instrumentIndex, filterSetIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterSetManufacturer(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterSetManufacturer(instrumentIndex, filterSetIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFilterSetModel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFilterSetModel(instrumentIndex, filterSetIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getGroupID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int groupIndex;
        groupIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getGroupID(groupIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getGroupName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int groupIndex;
        groupIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getGroupName(groupIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImageAcquiredPixels(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getImageAcquiredPixels(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImageCreationDate(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getImageCreationDate(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImageDefaultPixels(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getImageDefaultPixels(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImageDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getImageDescription(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImageExperimentRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getImageExperimentRef(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImageExperimenterRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getImageExperimenterRef(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImageGroupRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getImageGroupRef(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImageID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getImageID(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImageInstrumentRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getImageInstrumentRef(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImageName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getImageName(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImagingEnvironmentAirPressure(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getImagingEnvironmentAirPressure(imageIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImagingEnvironmentCO2Percent(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getImagingEnvironmentCO2Percent(imageIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImagingEnvironmentHumidity(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getImagingEnvironmentHumidity(imageIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getImagingEnvironmentTemperature(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getImagingEnvironmentTemperature(imageIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getInstrumentID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getInstrumentID(instrumentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLaserFrequencyMultiplication(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLaserLaserMedium(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLaserLaserMedium(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLaserPockelCell(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.getLaserPockelCell(instrumentIndex, lightSourceIndex, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLaserPulse(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLaserPulse(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLaserRepetitionRate(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.getLaserRepetitionRate(instrumentIndex, lightSourceIndex, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLaserTuneable(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.getLaserTuneable(instrumentIndex, lightSourceIndex, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLaserType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLaserType(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLaserWavelength(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getLaserWavelength(instrumentIndex, lightSourceIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLightSourceID(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceManufacturer(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLightSourceManufacturer(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceModel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLightSourceModel(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourcePower(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getLightSourcePower(instrumentIndex, lightSourceIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceSerialNumber(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLightSourceSerialNumber(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceRefAttenuation(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        int lightSourceRefIndex;
        lightSourceRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getLightSourceRefAttenuation(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceRefLightSource(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        int lightSourceRefIndex;
        lightSourceRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLightSourceRefLightSource(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceRefWavelength(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        int lightSourceRefIndex;
        lightSourceRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getLightSourceRefWavelength(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceSettingsAttenuation(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceSettingsLightSource(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceSettingsWavelength(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLineID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLineID(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLineTransform(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLineTransform(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLineX1(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLineX1(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLineX2(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLineX2(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLineY1(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLineY1(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLineY2(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLineY2(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelContrastMethod(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelDetector(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelDetector(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelEmWave(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getLogicalChannelEmWave(imageIndex, logicalChannelIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelExWave(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getLogicalChannelExWave(imageIndex, logicalChannelIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelFilterSet(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelFilterSet(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelFluor(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelFluor(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelID(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelIlluminationType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelLightSource(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelLightSource(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelMode(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelMode(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelName(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelNdFilter(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getLogicalChannelNdFilter(imageIndex, logicalChannelIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelOTF(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelOTF(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelPhotometricInterpretation(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelPinholeSize(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelPockelCellSetting(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelSamplesPerPixel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelSecondaryEmissionFilter(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelSecondaryEmissionFilter(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLogicalChannelSecondaryExcitationFilter(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getLogicalChannelSecondaryExcitationFilter(imageIndex, logicalChannelIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMaskHeight(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMaskHeight(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMaskID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMaskID(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMaskTransform(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMaskTransform(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMaskWidth(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMaskWidth(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMaskX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMaskX(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMaskY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMaskY(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMaskPixelsBigEndian(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.getMaskPixelsBigEndian(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMaskPixelsBinData(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMaskPixelsBinData(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMaskPixelsExtendedPixelType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMaskPixelsExtendedPixelType(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMaskPixelsID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMaskPixelsID(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMaskPixelsSizeX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getMaskPixelsSizeX(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMaskPixelsSizeY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getMaskPixelsSizeY(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMicrobeamManipulationExperimenterRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMicrobeamManipulationExperimenterRef(imageIndex, microbeamManipulationIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMicrobeamManipulationID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMicrobeamManipulationID(imageIndex, microbeamManipulationIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMicrobeamManipulationType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMicrobeamManipulationType(imageIndex, microbeamManipulationIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMicrobeamManipulationRefID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int experimentIndex;
        experimentIndex = __is.readInt();
        int microbeamManipulationRefIndex;
        microbeamManipulationRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMicrobeamManipulationRefID(experimentIndex, microbeamManipulationRefIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMicroscopeID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMicroscopeID(instrumentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMicroscopeManufacturer(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMicroscopeManufacturer(instrumentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMicroscopeModel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMicroscopeModel(instrumentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMicroscopeSerialNumber(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMicroscopeSerialNumber(instrumentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMicroscopeType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getMicroscopeType(instrumentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getOTFBinaryFile(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getOTFBinaryFile(instrumentIndex, otfIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getOTFID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getOTFID(instrumentIndex, otfIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getOTFObjective(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getOTFObjective(instrumentIndex, otfIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getOTFOpticalAxisAveraged(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.getOTFOpticalAxisAveraged(instrumentIndex, otfIndex, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getOTFPixelType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getOTFPixelType(instrumentIndex, otfIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getOTFSizeX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getOTFSizeX(instrumentIndex, otfIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getOTFSizeY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getOTFSizeY(instrumentIndex, otfIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveCalibratedMagnification(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveCorrection(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getObjectiveCorrection(instrumentIndex, objectiveIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getObjectiveID(instrumentIndex, objectiveIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveImmersion(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getObjectiveImmersion(instrumentIndex, objectiveIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveIris(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.getObjectiveIris(instrumentIndex, objectiveIndex, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveLensNA(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getObjectiveLensNA(instrumentIndex, objectiveIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveManufacturer(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getObjectiveManufacturer(instrumentIndex, objectiveIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveModel(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getObjectiveModel(instrumentIndex, objectiveIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveNominalMagnification(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveSerialNumber(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getObjectiveSerialNumber(instrumentIndex, objectiveIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveWorkingDistance(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveSettingsCorrectionCollar(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getObjectiveSettingsCorrectionCollar(imageIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveSettingsMedium(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getObjectiveSettingsMedium(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveSettingsObjective(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getObjectiveSettingsObjective(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getObjectiveSettingsRefractiveIndex(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getObjectiveSettingsRefractiveIndex(imageIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPathD(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPathD(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPathID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPathID(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPixelsBigEndian(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.getPixelsBigEndian(imageIndex, pixelsIndex, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPixelsDimensionOrder(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPixelsDimensionOrder(imageIndex, pixelsIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPixelsID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPixelsID(imageIndex, pixelsIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPixelsPixelType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPixelsPixelType(imageIndex, pixelsIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPixelsSizeC(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPixelsSizeC(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPixelsSizeT(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPixelsSizeT(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPixelsSizeX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPixelsSizeX(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPixelsSizeY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPixelsSizeY(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPixelsSizeZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPixelsSizeZ(imageIndex, pixelsIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlaneHashSHA1(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPlaneHashSHA1(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlaneID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPlaneID(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlaneTheC(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPlaneTheC(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlaneTheT(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPlaneTheT(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlaneTheZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPlaneTheZ(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlaneTimingDeltaT(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlaneTimingExposureTime(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateColumnNamingConvention(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPlateColumnNamingConvention(plateIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPlateDescription(plateIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateExternalIdentifier(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPlateExternalIdentifier(plateIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPlateID(plateIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPlateName(plateIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateRowNamingConvention(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPlateRowNamingConvention(plateIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateStatus(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPlateStatus(plateIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateWellOriginX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        double __ret = __obj.getPlateWellOriginX(plateIndex, __current);
        __os.writeDouble(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateWellOriginY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        double __ret = __obj.getPlateWellOriginY(plateIndex, __current);
        __os.writeDouble(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateRefID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        int plateRefIndex;
        plateRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPlateRefID(screenIndex, plateRefIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateRefSample(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        int plateRefIndex;
        plateRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPlateRefSample(screenIndex, plateRefIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateRefWell(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        int plateRefIndex;
        plateRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPlateRefWell(screenIndex, plateRefIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPointCx(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPointCx(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPointCy(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPointCy(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPointID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPointID(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPointR(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPointR(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPointTransform(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPointTransform(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPolygonID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPolygonID(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPolygonPoints(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPolygonPoints(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPolygonTransform(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPolygonTransform(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPolylineID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPolylineID(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPolylinePoints(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPolylinePoints(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPolylineTransform(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPolylineTransform(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getProjectDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getProjectDescription(projectIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getProjectExperimenterRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getProjectExperimenterRef(projectIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getProjectGroupRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getProjectGroupRef(projectIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getProjectID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getProjectID(projectIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getProjectName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getProjectName(projectIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getProjectRefID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int datasetIndex;
        datasetIndex = __is.readInt();
        int projectRefIndex;
        projectRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getProjectRefID(datasetIndex, projectRefIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPumpLightSource(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getPumpLightSource(instrumentIndex, lightSourceIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getROIID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getROIID(imageIndex, roiIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getROIT0(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getROIT0(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getROIT1(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getROIT1(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getROIX0(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getROIX0(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getROIX1(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getROIX1(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getROIY0(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getROIY0(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getROIY1(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getROIY1(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getROIZ0(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getROIZ0(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getROIZ1(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getROIZ1(imageIndex, roiIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getROIRefID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        int roiRefIndex;
        roiRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getROIRefID(imageIndex, microbeamManipulationIndex, roiRefIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getReagentDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getReagentDescription(screenIndex, reagentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getReagentID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getReagentID(screenIndex, reagentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getReagentName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getReagentName(screenIndex, reagentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getReagentReagentIdentifier(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getReagentReagentIdentifier(screenIndex, reagentIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRectHeight(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getRectHeight(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRectID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getRectID(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRectTransform(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getRectTransform(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRectWidth(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getRectWidth(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRectX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getRectX(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRectY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getRectY(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRegionID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int regionIndex;
        regionIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getRegionID(imageIndex, regionIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRegionName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int regionIndex;
        regionIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getRegionName(imageIndex, regionIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRegionTag(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int regionIndex;
        regionIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getRegionTag(imageIndex, regionIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRoiLinkDirection(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int roiLinkIndex;
        roiLinkIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getRoiLinkDirection(imageIndex, roiIndex, roiLinkIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRoiLinkName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int roiLinkIndex;
        roiLinkIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getRoiLinkName(imageIndex, roiIndex, roiLinkIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getRoiLinkRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int roiLinkIndex;
        roiLinkIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getRoiLinkRef(imageIndex, roiIndex, roiLinkIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenDescription(screenIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenExtern(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenExtern(screenIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenID(screenIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenName(screenIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenProtocolDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenProtocolDescription(screenIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenProtocolIdentifier(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenProtocolIdentifier(screenIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenReagentSetDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenReagentSetDescription(screenIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenReagentSetIdentifier(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenReagentSetIdentifier(screenIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenType(screenIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenAcquisitionEndTime(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenAcquisitionEndTime(screenIndex, screenAcquisitionIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenAcquisitionID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenAcquisitionID(screenIndex, screenAcquisitionIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenAcquisitionStartTime(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenAcquisitionStartTime(screenIndex, screenAcquisitionIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenRefID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int screenRefIndex;
        screenRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getScreenRefID(plateIndex, screenRefIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeBaselineShift(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeBaselineShift(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeDirection(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeDirection(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeFillColor(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeFillColor(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeFillOpacity(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeFillOpacity(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeFillRule(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeFillRule(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeFontFamily(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeFontFamily(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeFontSize(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getShapeFontSize(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeFontStretch(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeFontStretch(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeFontStyle(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeFontStyle(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeFontVariant(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeFontVariant(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeFontWeight(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeFontWeight(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeG(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeG(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeGlyphOrientationVertical(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getShapeGlyphOrientationVertical(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeID(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeLocked(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.getShapeLocked(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeStrokeAttribute(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeStrokeAttribute(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeStrokeColor(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeStrokeColor(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeStrokeDashArray(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeStrokeDashArray(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeStrokeLineCap(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeStrokeLineCap(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeStrokeLineJoin(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeStrokeLineJoin(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeStrokeMiterLimit(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getShapeStrokeMiterLimit(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeStrokeOpacity(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getShapeStrokeOpacity(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeStrokeWidth(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getShapeStrokeWidth(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeText(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeText(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeTextAnchor(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeTextAnchor(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeTextDecoration(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeTextDecoration(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeTextFill(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeTextFill(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeTextStroke(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeTextStroke(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeTheT(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getShapeTheT(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeTheZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getShapeTheZ(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeVectorEffect(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeVectorEffect(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeVisibility(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.getShapeVisibility(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getShapeWritingMode(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getShapeWritingMode(imageIndex, roiIndex, shapeIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getStageLabelName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getStageLabelName(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getStageLabelX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getStageLabelX(imageIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getStageLabelY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getStageLabelY(imageIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getStageLabelZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getStageLabelZ(imageIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getStagePositionPositionX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getStagePositionPositionY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getStagePositionPositionZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getThumbnailHref(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getThumbnailHref(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getThumbnailID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getThumbnailID(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getThumbnailMIMEtype(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getThumbnailMIMEtype(imageIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTiffDataFileName(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getTiffDataFileName(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTiffDataFirstC(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getTiffDataFirstC(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTiffDataFirstT(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getTiffDataFirstT(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTiffDataFirstZ(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getTiffDataFirstZ(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTiffDataIFD(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getTiffDataIFD(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTiffDataNumPlanes(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getTiffDataNumPlanes(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTiffDataUUID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getTiffDataUUID(imageIndex, pixelsIndex, tiffDataIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTransmittanceRangeCutIn(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getTransmittanceRangeCutIn(instrumentIndex, filterIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTransmittanceRangeCutInTolerance(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getTransmittanceRangeCutInTolerance(instrumentIndex, filterIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTransmittanceRangeCutOut(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getTransmittanceRangeCutOut(instrumentIndex, filterIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTransmittanceRangeCutOutTolerance(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getTransmittanceRangeCutOutTolerance(instrumentIndex, filterIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getTransmittanceRangeTransmittance(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getTransmittanceRangeTransmittance(instrumentIndex, filterIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellColumn(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getWellColumn(plateIndex, wellIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellExternalDescription(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getWellExternalDescription(plateIndex, wellIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellExternalIdentifier(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getWellExternalIdentifier(plateIndex, wellIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getWellID(plateIndex, wellIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellReagent(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getWellReagent(plateIndex, wellIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellRow(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getWellRow(plateIndex, wellIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellType(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getWellType(plateIndex, wellIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellSampleID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getWellSampleID(plateIndex, wellIndex, wellSampleIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellSampleImageRef(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getWellSampleImageRef(plateIndex, wellIndex, wellSampleIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellSampleIndex(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellSamplePosX(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getWellSamplePosX(plateIndex, wellIndex, wellSampleIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellSamplePosY(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        float __ret = __obj.getWellSamplePosY(plateIndex, wellIndex, wellSampleIndex, __current);
        __os.writeFloat(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellSampleTimepoint(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex, __current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getWellSampleRefID(MetadataRetrieve __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        int wellSampleRefIndex;
        wellSampleRefIndex = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getWellSampleRefID(screenIndex, screenAcquisitionIndex, wellSampleRefIndex, __current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "getArcType",
        "getChannelComponentColorDomain",
        "getChannelComponentCount",
        "getChannelComponentIndex",
        "getChannelComponentPixels",
        "getCircleCx",
        "getCircleCy",
        "getCircleID",
        "getCircleR",
        "getCircleTransform",
        "getContactExperimenter",
        "getDatasetCount",
        "getDatasetDescription",
        "getDatasetExperimenterRef",
        "getDatasetGroupRef",
        "getDatasetID",
        "getDatasetLocked",
        "getDatasetName",
        "getDatasetRefCount",
        "getDatasetRefID",
        "getDetectorAmplificationGain",
        "getDetectorCount",
        "getDetectorGain",
        "getDetectorID",
        "getDetectorManufacturer",
        "getDetectorModel",
        "getDetectorOffset",
        "getDetectorSerialNumber",
        "getDetectorSettingsBinning",
        "getDetectorSettingsDetector",
        "getDetectorSettingsGain",
        "getDetectorSettingsOffset",
        "getDetectorSettingsReadOutRate",
        "getDetectorSettingsVoltage",
        "getDetectorType",
        "getDetectorVoltage",
        "getDetectorZoom",
        "getDichroicCount",
        "getDichroicID",
        "getDichroicLotNumber",
        "getDichroicManufacturer",
        "getDichroicModel",
        "getDimensionsPhysicalSizeX",
        "getDimensionsPhysicalSizeY",
        "getDimensionsPhysicalSizeZ",
        "getDimensionsTimeIncrement",
        "getDimensionsWaveIncrement",
        "getDimensionsWaveStart",
        "getDisplayOptionsDisplay",
        "getDisplayOptionsID",
        "getDisplayOptionsZoom",
        "getEllipseCx",
        "getEllipseCy",
        "getEllipseID",
        "getEllipseRx",
        "getEllipseRy",
        "getEllipseTransform",
        "getEmFilterLotNumber",
        "getEmFilterManufacturer",
        "getEmFilterModel",
        "getEmFilterType",
        "getExFilterLotNumber",
        "getExFilterManufacturer",
        "getExFilterModel",
        "getExFilterType",
        "getExperimentCount",
        "getExperimentDescription",
        "getExperimentExperimenterRef",
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
        "getExperimenterOMEName",
        "getFilamentType",
        "getFilterCount",
        "getFilterFilterWheel",
        "getFilterID",
        "getFilterLotNumber",
        "getFilterManufacturer",
        "getFilterModel",
        "getFilterSetCount",
        "getFilterSetDichroic",
        "getFilterSetEmFilter",
        "getFilterSetExFilter",
        "getFilterSetID",
        "getFilterSetLotNumber",
        "getFilterSetManufacturer",
        "getFilterSetModel",
        "getFilterType",
        "getGroupCount",
        "getGroupID",
        "getGroupName",
        "getGroupRefCount",
        "getImageAcquiredPixels",
        "getImageCount",
        "getImageCreationDate",
        "getImageDefaultPixels",
        "getImageDescription",
        "getImageExperimentRef",
        "getImageExperimenterRef",
        "getImageGroupRef",
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
        "getLaserPockelCell",
        "getLaserPulse",
        "getLaserRepetitionRate",
        "getLaserTuneable",
        "getLaserType",
        "getLaserWavelength",
        "getLightSourceCount",
        "getLightSourceID",
        "getLightSourceManufacturer",
        "getLightSourceModel",
        "getLightSourcePower",
        "getLightSourceRefAttenuation",
        "getLightSourceRefCount",
        "getLightSourceRefLightSource",
        "getLightSourceRefWavelength",
        "getLightSourceSerialNumber",
        "getLightSourceSettingsAttenuation",
        "getLightSourceSettingsLightSource",
        "getLightSourceSettingsWavelength",
        "getLineID",
        "getLineTransform",
        "getLineX1",
        "getLineX2",
        "getLineY1",
        "getLineY2",
        "getLogicalChannelContrastMethod",
        "getLogicalChannelCount",
        "getLogicalChannelDetector",
        "getLogicalChannelEmWave",
        "getLogicalChannelExWave",
        "getLogicalChannelFilterSet",
        "getLogicalChannelFluor",
        "getLogicalChannelID",
        "getLogicalChannelIlluminationType",
        "getLogicalChannelLightSource",
        "getLogicalChannelMode",
        "getLogicalChannelName",
        "getLogicalChannelNdFilter",
        "getLogicalChannelOTF",
        "getLogicalChannelPhotometricInterpretation",
        "getLogicalChannelPinholeSize",
        "getLogicalChannelPockelCellSetting",
        "getLogicalChannelSamplesPerPixel",
        "getLogicalChannelSecondaryEmissionFilter",
        "getLogicalChannelSecondaryExcitationFilter",
        "getMaskHeight",
        "getMaskID",
        "getMaskPixelsBigEndian",
        "getMaskPixelsBinData",
        "getMaskPixelsExtendedPixelType",
        "getMaskPixelsID",
        "getMaskPixelsSizeX",
        "getMaskPixelsSizeY",
        "getMaskTransform",
        "getMaskWidth",
        "getMaskX",
        "getMaskY",
        "getMicrobeamManipulationCount",
        "getMicrobeamManipulationExperimenterRef",
        "getMicrobeamManipulationID",
        "getMicrobeamManipulationRefCount",
        "getMicrobeamManipulationRefID",
        "getMicrobeamManipulationType",
        "getMicroscopeID",
        "getMicroscopeManufacturer",
        "getMicroscopeModel",
        "getMicroscopeSerialNumber",
        "getMicroscopeType",
        "getOMEXML",
        "getOTFBinaryFile",
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
        "getObjectiveIris",
        "getObjectiveLensNA",
        "getObjectiveManufacturer",
        "getObjectiveModel",
        "getObjectiveNominalMagnification",
        "getObjectiveSerialNumber",
        "getObjectiveSettingsCorrectionCollar",
        "getObjectiveSettingsMedium",
        "getObjectiveSettingsObjective",
        "getObjectiveSettingsRefractiveIndex",
        "getObjectiveWorkingDistance",
        "getPathD",
        "getPathID",
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
        "getPlaneHashSHA1",
        "getPlaneID",
        "getPlaneTheC",
        "getPlaneTheT",
        "getPlaneTheZ",
        "getPlaneTimingDeltaT",
        "getPlaneTimingExposureTime",
        "getPlateColumnNamingConvention",
        "getPlateCount",
        "getPlateDescription",
        "getPlateExternalIdentifier",
        "getPlateID",
        "getPlateName",
        "getPlateRefCount",
        "getPlateRefID",
        "getPlateRefSample",
        "getPlateRefWell",
        "getPlateRowNamingConvention",
        "getPlateStatus",
        "getPlateWellOriginX",
        "getPlateWellOriginY",
        "getPointCx",
        "getPointCy",
        "getPointID",
        "getPointR",
        "getPointTransform",
        "getPolygonID",
        "getPolygonPoints",
        "getPolygonTransform",
        "getPolylineID",
        "getPolylinePoints",
        "getPolylineTransform",
        "getProjectCount",
        "getProjectDescription",
        "getProjectExperimenterRef",
        "getProjectGroupRef",
        "getProjectID",
        "getProjectName",
        "getProjectRefCount",
        "getProjectRefID",
        "getPumpLightSource",
        "getROICount",
        "getROIID",
        "getROIRefCount",
        "getROIRefID",
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
        "getRectHeight",
        "getRectID",
        "getRectTransform",
        "getRectWidth",
        "getRectX",
        "getRectY",
        "getRegionCount",
        "getRegionID",
        "getRegionName",
        "getRegionTag",
        "getRoiLinkCount",
        "getRoiLinkDirection",
        "getRoiLinkName",
        "getRoiLinkRef",
        "getScreenAcquisitionCount",
        "getScreenAcquisitionEndTime",
        "getScreenAcquisitionID",
        "getScreenAcquisitionStartTime",
        "getScreenCount",
        "getScreenDescription",
        "getScreenExtern",
        "getScreenID",
        "getScreenName",
        "getScreenProtocolDescription",
        "getScreenProtocolIdentifier",
        "getScreenReagentSetDescription",
        "getScreenReagentSetIdentifier",
        "getScreenRefCount",
        "getScreenRefID",
        "getScreenType",
        "getServant",
        "getShapeBaselineShift",
        "getShapeCount",
        "getShapeDirection",
        "getShapeFillColor",
        "getShapeFillOpacity",
        "getShapeFillRule",
        "getShapeFontFamily",
        "getShapeFontSize",
        "getShapeFontStretch",
        "getShapeFontStyle",
        "getShapeFontVariant",
        "getShapeFontWeight",
        "getShapeG",
        "getShapeGlyphOrientationVertical",
        "getShapeID",
        "getShapeLocked",
        "getShapeStrokeAttribute",
        "getShapeStrokeColor",
        "getShapeStrokeDashArray",
        "getShapeStrokeLineCap",
        "getShapeStrokeLineJoin",
        "getShapeStrokeMiterLimit",
        "getShapeStrokeOpacity",
        "getShapeStrokeWidth",
        "getShapeText",
        "getShapeTextAnchor",
        "getShapeTextDecoration",
        "getShapeTextFill",
        "getShapeTextStroke",
        "getShapeTheT",
        "getShapeTheZ",
        "getShapeVectorEffect",
        "getShapeVisibility",
        "getShapeWritingMode",
        "getStageLabelName",
        "getStageLabelX",
        "getStageLabelY",
        "getStageLabelZ",
        "getStagePositionPositionX",
        "getStagePositionPositionY",
        "getStagePositionPositionZ",
        "getThumbnailHref",
        "getThumbnailID",
        "getThumbnailMIMEtype",
        "getTiffDataCount",
        "getTiffDataFileName",
        "getTiffDataFirstC",
        "getTiffDataFirstT",
        "getTiffDataFirstZ",
        "getTiffDataIFD",
        "getTiffDataNumPlanes",
        "getTiffDataUUID",
        "getTransmittanceRangeCutIn",
        "getTransmittanceRangeCutInTolerance",
        "getTransmittanceRangeCutOut",
        "getTransmittanceRangeCutOutTolerance",
        "getTransmittanceRangeTransmittance",
        "getUUID",
        "getWellColumn",
        "getWellCount",
        "getWellExternalDescription",
        "getWellExternalIdentifier",
        "getWellID",
        "getWellReagent",
        "getWellRow",
        "getWellSampleCount",
        "getWellSampleID",
        "getWellSampleImageRef",
        "getWellSampleIndex",
        "getWellSamplePosX",
        "getWellSamplePosY",
        "getWellSampleRefCount",
        "getWellSampleRefID",
        "getWellSampleTimepoint",
        "getWellType",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping"
    };

    public Ice.DispatchStatus
    __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
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
                return ___getChannelComponentPixels(this, in, __current);
            }
            case 5:
            {
                return ___getCircleCx(this, in, __current);
            }
            case 6:
            {
                return ___getCircleCy(this, in, __current);
            }
            case 7:
            {
                return ___getCircleID(this, in, __current);
            }
            case 8:
            {
                return ___getCircleR(this, in, __current);
            }
            case 9:
            {
                return ___getCircleTransform(this, in, __current);
            }
            case 10:
            {
                return ___getContactExperimenter(this, in, __current);
            }
            case 11:
            {
                return ___getDatasetCount(this, in, __current);
            }
            case 12:
            {
                return ___getDatasetDescription(this, in, __current);
            }
            case 13:
            {
                return ___getDatasetExperimenterRef(this, in, __current);
            }
            case 14:
            {
                return ___getDatasetGroupRef(this, in, __current);
            }
            case 15:
            {
                return ___getDatasetID(this, in, __current);
            }
            case 16:
            {
                return ___getDatasetLocked(this, in, __current);
            }
            case 17:
            {
                return ___getDatasetName(this, in, __current);
            }
            case 18:
            {
                return ___getDatasetRefCount(this, in, __current);
            }
            case 19:
            {
                return ___getDatasetRefID(this, in, __current);
            }
            case 20:
            {
                return ___getDetectorAmplificationGain(this, in, __current);
            }
            case 21:
            {
                return ___getDetectorCount(this, in, __current);
            }
            case 22:
            {
                return ___getDetectorGain(this, in, __current);
            }
            case 23:
            {
                return ___getDetectorID(this, in, __current);
            }
            case 24:
            {
                return ___getDetectorManufacturer(this, in, __current);
            }
            case 25:
            {
                return ___getDetectorModel(this, in, __current);
            }
            case 26:
            {
                return ___getDetectorOffset(this, in, __current);
            }
            case 27:
            {
                return ___getDetectorSerialNumber(this, in, __current);
            }
            case 28:
            {
                return ___getDetectorSettingsBinning(this, in, __current);
            }
            case 29:
            {
                return ___getDetectorSettingsDetector(this, in, __current);
            }
            case 30:
            {
                return ___getDetectorSettingsGain(this, in, __current);
            }
            case 31:
            {
                return ___getDetectorSettingsOffset(this, in, __current);
            }
            case 32:
            {
                return ___getDetectorSettingsReadOutRate(this, in, __current);
            }
            case 33:
            {
                return ___getDetectorSettingsVoltage(this, in, __current);
            }
            case 34:
            {
                return ___getDetectorType(this, in, __current);
            }
            case 35:
            {
                return ___getDetectorVoltage(this, in, __current);
            }
            case 36:
            {
                return ___getDetectorZoom(this, in, __current);
            }
            case 37:
            {
                return ___getDichroicCount(this, in, __current);
            }
            case 38:
            {
                return ___getDichroicID(this, in, __current);
            }
            case 39:
            {
                return ___getDichroicLotNumber(this, in, __current);
            }
            case 40:
            {
                return ___getDichroicManufacturer(this, in, __current);
            }
            case 41:
            {
                return ___getDichroicModel(this, in, __current);
            }
            case 42:
            {
                return ___getDimensionsPhysicalSizeX(this, in, __current);
            }
            case 43:
            {
                return ___getDimensionsPhysicalSizeY(this, in, __current);
            }
            case 44:
            {
                return ___getDimensionsPhysicalSizeZ(this, in, __current);
            }
            case 45:
            {
                return ___getDimensionsTimeIncrement(this, in, __current);
            }
            case 46:
            {
                return ___getDimensionsWaveIncrement(this, in, __current);
            }
            case 47:
            {
                return ___getDimensionsWaveStart(this, in, __current);
            }
            case 48:
            {
                return ___getDisplayOptionsDisplay(this, in, __current);
            }
            case 49:
            {
                return ___getDisplayOptionsID(this, in, __current);
            }
            case 50:
            {
                return ___getDisplayOptionsZoom(this, in, __current);
            }
            case 51:
            {
                return ___getEllipseCx(this, in, __current);
            }
            case 52:
            {
                return ___getEllipseCy(this, in, __current);
            }
            case 53:
            {
                return ___getEllipseID(this, in, __current);
            }
            case 54:
            {
                return ___getEllipseRx(this, in, __current);
            }
            case 55:
            {
                return ___getEllipseRy(this, in, __current);
            }
            case 56:
            {
                return ___getEllipseTransform(this, in, __current);
            }
            case 57:
            {
                return ___getEmFilterLotNumber(this, in, __current);
            }
            case 58:
            {
                return ___getEmFilterManufacturer(this, in, __current);
            }
            case 59:
            {
                return ___getEmFilterModel(this, in, __current);
            }
            case 60:
            {
                return ___getEmFilterType(this, in, __current);
            }
            case 61:
            {
                return ___getExFilterLotNumber(this, in, __current);
            }
            case 62:
            {
                return ___getExFilterManufacturer(this, in, __current);
            }
            case 63:
            {
                return ___getExFilterModel(this, in, __current);
            }
            case 64:
            {
                return ___getExFilterType(this, in, __current);
            }
            case 65:
            {
                return ___getExperimentCount(this, in, __current);
            }
            case 66:
            {
                return ___getExperimentDescription(this, in, __current);
            }
            case 67:
            {
                return ___getExperimentExperimenterRef(this, in, __current);
            }
            case 68:
            {
                return ___getExperimentID(this, in, __current);
            }
            case 69:
            {
                return ___getExperimentType(this, in, __current);
            }
            case 70:
            {
                return ___getExperimenterCount(this, in, __current);
            }
            case 71:
            {
                return ___getExperimenterEmail(this, in, __current);
            }
            case 72:
            {
                return ___getExperimenterFirstName(this, in, __current);
            }
            case 73:
            {
                return ___getExperimenterID(this, in, __current);
            }
            case 74:
            {
                return ___getExperimenterInstitution(this, in, __current);
            }
            case 75:
            {
                return ___getExperimenterLastName(this, in, __current);
            }
            case 76:
            {
                return ___getExperimenterMembershipCount(this, in, __current);
            }
            case 77:
            {
                return ___getExperimenterMembershipGroup(this, in, __current);
            }
            case 78:
            {
                return ___getExperimenterOMEName(this, in, __current);
            }
            case 79:
            {
                return ___getFilamentType(this, in, __current);
            }
            case 80:
            {
                return ___getFilterCount(this, in, __current);
            }
            case 81:
            {
                return ___getFilterFilterWheel(this, in, __current);
            }
            case 82:
            {
                return ___getFilterID(this, in, __current);
            }
            case 83:
            {
                return ___getFilterLotNumber(this, in, __current);
            }
            case 84:
            {
                return ___getFilterManufacturer(this, in, __current);
            }
            case 85:
            {
                return ___getFilterModel(this, in, __current);
            }
            case 86:
            {
                return ___getFilterSetCount(this, in, __current);
            }
            case 87:
            {
                return ___getFilterSetDichroic(this, in, __current);
            }
            case 88:
            {
                return ___getFilterSetEmFilter(this, in, __current);
            }
            case 89:
            {
                return ___getFilterSetExFilter(this, in, __current);
            }
            case 90:
            {
                return ___getFilterSetID(this, in, __current);
            }
            case 91:
            {
                return ___getFilterSetLotNumber(this, in, __current);
            }
            case 92:
            {
                return ___getFilterSetManufacturer(this, in, __current);
            }
            case 93:
            {
                return ___getFilterSetModel(this, in, __current);
            }
            case 94:
            {
                return ___getFilterType(this, in, __current);
            }
            case 95:
            {
                return ___getGroupCount(this, in, __current);
            }
            case 96:
            {
                return ___getGroupID(this, in, __current);
            }
            case 97:
            {
                return ___getGroupName(this, in, __current);
            }
            case 98:
            {
                return ___getGroupRefCount(this, in, __current);
            }
            case 99:
            {
                return ___getImageAcquiredPixels(this, in, __current);
            }
            case 100:
            {
                return ___getImageCount(this, in, __current);
            }
            case 101:
            {
                return ___getImageCreationDate(this, in, __current);
            }
            case 102:
            {
                return ___getImageDefaultPixels(this, in, __current);
            }
            case 103:
            {
                return ___getImageDescription(this, in, __current);
            }
            case 104:
            {
                return ___getImageExperimentRef(this, in, __current);
            }
            case 105:
            {
                return ___getImageExperimenterRef(this, in, __current);
            }
            case 106:
            {
                return ___getImageGroupRef(this, in, __current);
            }
            case 107:
            {
                return ___getImageID(this, in, __current);
            }
            case 108:
            {
                return ___getImageInstrumentRef(this, in, __current);
            }
            case 109:
            {
                return ___getImageName(this, in, __current);
            }
            case 110:
            {
                return ___getImagingEnvironmentAirPressure(this, in, __current);
            }
            case 111:
            {
                return ___getImagingEnvironmentCO2Percent(this, in, __current);
            }
            case 112:
            {
                return ___getImagingEnvironmentHumidity(this, in, __current);
            }
            case 113:
            {
                return ___getImagingEnvironmentTemperature(this, in, __current);
            }
            case 114:
            {
                return ___getInstrumentCount(this, in, __current);
            }
            case 115:
            {
                return ___getInstrumentID(this, in, __current);
            }
            case 116:
            {
                return ___getLaserFrequencyMultiplication(this, in, __current);
            }
            case 117:
            {
                return ___getLaserLaserMedium(this, in, __current);
            }
            case 118:
            {
                return ___getLaserPockelCell(this, in, __current);
            }
            case 119:
            {
                return ___getLaserPulse(this, in, __current);
            }
            case 120:
            {
                return ___getLaserRepetitionRate(this, in, __current);
            }
            case 121:
            {
                return ___getLaserTuneable(this, in, __current);
            }
            case 122:
            {
                return ___getLaserType(this, in, __current);
            }
            case 123:
            {
                return ___getLaserWavelength(this, in, __current);
            }
            case 124:
            {
                return ___getLightSourceCount(this, in, __current);
            }
            case 125:
            {
                return ___getLightSourceID(this, in, __current);
            }
            case 126:
            {
                return ___getLightSourceManufacturer(this, in, __current);
            }
            case 127:
            {
                return ___getLightSourceModel(this, in, __current);
            }
            case 128:
            {
                return ___getLightSourcePower(this, in, __current);
            }
            case 129:
            {
                return ___getLightSourceRefAttenuation(this, in, __current);
            }
            case 130:
            {
                return ___getLightSourceRefCount(this, in, __current);
            }
            case 131:
            {
                return ___getLightSourceRefLightSource(this, in, __current);
            }
            case 132:
            {
                return ___getLightSourceRefWavelength(this, in, __current);
            }
            case 133:
            {
                return ___getLightSourceSerialNumber(this, in, __current);
            }
            case 134:
            {
                return ___getLightSourceSettingsAttenuation(this, in, __current);
            }
            case 135:
            {
                return ___getLightSourceSettingsLightSource(this, in, __current);
            }
            case 136:
            {
                return ___getLightSourceSettingsWavelength(this, in, __current);
            }
            case 137:
            {
                return ___getLineID(this, in, __current);
            }
            case 138:
            {
                return ___getLineTransform(this, in, __current);
            }
            case 139:
            {
                return ___getLineX1(this, in, __current);
            }
            case 140:
            {
                return ___getLineX2(this, in, __current);
            }
            case 141:
            {
                return ___getLineY1(this, in, __current);
            }
            case 142:
            {
                return ___getLineY2(this, in, __current);
            }
            case 143:
            {
                return ___getLogicalChannelContrastMethod(this, in, __current);
            }
            case 144:
            {
                return ___getLogicalChannelCount(this, in, __current);
            }
            case 145:
            {
                return ___getLogicalChannelDetector(this, in, __current);
            }
            case 146:
            {
                return ___getLogicalChannelEmWave(this, in, __current);
            }
            case 147:
            {
                return ___getLogicalChannelExWave(this, in, __current);
            }
            case 148:
            {
                return ___getLogicalChannelFilterSet(this, in, __current);
            }
            case 149:
            {
                return ___getLogicalChannelFluor(this, in, __current);
            }
            case 150:
            {
                return ___getLogicalChannelID(this, in, __current);
            }
            case 151:
            {
                return ___getLogicalChannelIlluminationType(this, in, __current);
            }
            case 152:
            {
                return ___getLogicalChannelLightSource(this, in, __current);
            }
            case 153:
            {
                return ___getLogicalChannelMode(this, in, __current);
            }
            case 154:
            {
                return ___getLogicalChannelName(this, in, __current);
            }
            case 155:
            {
                return ___getLogicalChannelNdFilter(this, in, __current);
            }
            case 156:
            {
                return ___getLogicalChannelOTF(this, in, __current);
            }
            case 157:
            {
                return ___getLogicalChannelPhotometricInterpretation(this, in, __current);
            }
            case 158:
            {
                return ___getLogicalChannelPinholeSize(this, in, __current);
            }
            case 159:
            {
                return ___getLogicalChannelPockelCellSetting(this, in, __current);
            }
            case 160:
            {
                return ___getLogicalChannelSamplesPerPixel(this, in, __current);
            }
            case 161:
            {
                return ___getLogicalChannelSecondaryEmissionFilter(this, in, __current);
            }
            case 162:
            {
                return ___getLogicalChannelSecondaryExcitationFilter(this, in, __current);
            }
            case 163:
            {
                return ___getMaskHeight(this, in, __current);
            }
            case 164:
            {
                return ___getMaskID(this, in, __current);
            }
            case 165:
            {
                return ___getMaskPixelsBigEndian(this, in, __current);
            }
            case 166:
            {
                return ___getMaskPixelsBinData(this, in, __current);
            }
            case 167:
            {
                return ___getMaskPixelsExtendedPixelType(this, in, __current);
            }
            case 168:
            {
                return ___getMaskPixelsID(this, in, __current);
            }
            case 169:
            {
                return ___getMaskPixelsSizeX(this, in, __current);
            }
            case 170:
            {
                return ___getMaskPixelsSizeY(this, in, __current);
            }
            case 171:
            {
                return ___getMaskTransform(this, in, __current);
            }
            case 172:
            {
                return ___getMaskWidth(this, in, __current);
            }
            case 173:
            {
                return ___getMaskX(this, in, __current);
            }
            case 174:
            {
                return ___getMaskY(this, in, __current);
            }
            case 175:
            {
                return ___getMicrobeamManipulationCount(this, in, __current);
            }
            case 176:
            {
                return ___getMicrobeamManipulationExperimenterRef(this, in, __current);
            }
            case 177:
            {
                return ___getMicrobeamManipulationID(this, in, __current);
            }
            case 178:
            {
                return ___getMicrobeamManipulationRefCount(this, in, __current);
            }
            case 179:
            {
                return ___getMicrobeamManipulationRefID(this, in, __current);
            }
            case 180:
            {
                return ___getMicrobeamManipulationType(this, in, __current);
            }
            case 181:
            {
                return ___getMicroscopeID(this, in, __current);
            }
            case 182:
            {
                return ___getMicroscopeManufacturer(this, in, __current);
            }
            case 183:
            {
                return ___getMicroscopeModel(this, in, __current);
            }
            case 184:
            {
                return ___getMicroscopeSerialNumber(this, in, __current);
            }
            case 185:
            {
                return ___getMicroscopeType(this, in, __current);
            }
            case 186:
            {
                return ___getOMEXML(this, in, __current);
            }
            case 187:
            {
                return ___getOTFBinaryFile(this, in, __current);
            }
            case 188:
            {
                return ___getOTFCount(this, in, __current);
            }
            case 189:
            {
                return ___getOTFID(this, in, __current);
            }
            case 190:
            {
                return ___getOTFObjective(this, in, __current);
            }
            case 191:
            {
                return ___getOTFOpticalAxisAveraged(this, in, __current);
            }
            case 192:
            {
                return ___getOTFPixelType(this, in, __current);
            }
            case 193:
            {
                return ___getOTFSizeX(this, in, __current);
            }
            case 194:
            {
                return ___getOTFSizeY(this, in, __current);
            }
            case 195:
            {
                return ___getObjectiveCalibratedMagnification(this, in, __current);
            }
            case 196:
            {
                return ___getObjectiveCorrection(this, in, __current);
            }
            case 197:
            {
                return ___getObjectiveCount(this, in, __current);
            }
            case 198:
            {
                return ___getObjectiveID(this, in, __current);
            }
            case 199:
            {
                return ___getObjectiveImmersion(this, in, __current);
            }
            case 200:
            {
                return ___getObjectiveIris(this, in, __current);
            }
            case 201:
            {
                return ___getObjectiveLensNA(this, in, __current);
            }
            case 202:
            {
                return ___getObjectiveManufacturer(this, in, __current);
            }
            case 203:
            {
                return ___getObjectiveModel(this, in, __current);
            }
            case 204:
            {
                return ___getObjectiveNominalMagnification(this, in, __current);
            }
            case 205:
            {
                return ___getObjectiveSerialNumber(this, in, __current);
            }
            case 206:
            {
                return ___getObjectiveSettingsCorrectionCollar(this, in, __current);
            }
            case 207:
            {
                return ___getObjectiveSettingsMedium(this, in, __current);
            }
            case 208:
            {
                return ___getObjectiveSettingsObjective(this, in, __current);
            }
            case 209:
            {
                return ___getObjectiveSettingsRefractiveIndex(this, in, __current);
            }
            case 210:
            {
                return ___getObjectiveWorkingDistance(this, in, __current);
            }
            case 211:
            {
                return ___getPathD(this, in, __current);
            }
            case 212:
            {
                return ___getPathID(this, in, __current);
            }
            case 213:
            {
                return ___getPixelsBigEndian(this, in, __current);
            }
            case 214:
            {
                return ___getPixelsCount(this, in, __current);
            }
            case 215:
            {
                return ___getPixelsDimensionOrder(this, in, __current);
            }
            case 216:
            {
                return ___getPixelsID(this, in, __current);
            }
            case 217:
            {
                return ___getPixelsPixelType(this, in, __current);
            }
            case 218:
            {
                return ___getPixelsSizeC(this, in, __current);
            }
            case 219:
            {
                return ___getPixelsSizeT(this, in, __current);
            }
            case 220:
            {
                return ___getPixelsSizeX(this, in, __current);
            }
            case 221:
            {
                return ___getPixelsSizeY(this, in, __current);
            }
            case 222:
            {
                return ___getPixelsSizeZ(this, in, __current);
            }
            case 223:
            {
                return ___getPlaneCount(this, in, __current);
            }
            case 224:
            {
                return ___getPlaneHashSHA1(this, in, __current);
            }
            case 225:
            {
                return ___getPlaneID(this, in, __current);
            }
            case 226:
            {
                return ___getPlaneTheC(this, in, __current);
            }
            case 227:
            {
                return ___getPlaneTheT(this, in, __current);
            }
            case 228:
            {
                return ___getPlaneTheZ(this, in, __current);
            }
            case 229:
            {
                return ___getPlaneTimingDeltaT(this, in, __current);
            }
            case 230:
            {
                return ___getPlaneTimingExposureTime(this, in, __current);
            }
            case 231:
            {
                return ___getPlateColumnNamingConvention(this, in, __current);
            }
            case 232:
            {
                return ___getPlateCount(this, in, __current);
            }
            case 233:
            {
                return ___getPlateDescription(this, in, __current);
            }
            case 234:
            {
                return ___getPlateExternalIdentifier(this, in, __current);
            }
            case 235:
            {
                return ___getPlateID(this, in, __current);
            }
            case 236:
            {
                return ___getPlateName(this, in, __current);
            }
            case 237:
            {
                return ___getPlateRefCount(this, in, __current);
            }
            case 238:
            {
                return ___getPlateRefID(this, in, __current);
            }
            case 239:
            {
                return ___getPlateRefSample(this, in, __current);
            }
            case 240:
            {
                return ___getPlateRefWell(this, in, __current);
            }
            case 241:
            {
                return ___getPlateRowNamingConvention(this, in, __current);
            }
            case 242:
            {
                return ___getPlateStatus(this, in, __current);
            }
            case 243:
            {
                return ___getPlateWellOriginX(this, in, __current);
            }
            case 244:
            {
                return ___getPlateWellOriginY(this, in, __current);
            }
            case 245:
            {
                return ___getPointCx(this, in, __current);
            }
            case 246:
            {
                return ___getPointCy(this, in, __current);
            }
            case 247:
            {
                return ___getPointID(this, in, __current);
            }
            case 248:
            {
                return ___getPointR(this, in, __current);
            }
            case 249:
            {
                return ___getPointTransform(this, in, __current);
            }
            case 250:
            {
                return ___getPolygonID(this, in, __current);
            }
            case 251:
            {
                return ___getPolygonPoints(this, in, __current);
            }
            case 252:
            {
                return ___getPolygonTransform(this, in, __current);
            }
            case 253:
            {
                return ___getPolylineID(this, in, __current);
            }
            case 254:
            {
                return ___getPolylinePoints(this, in, __current);
            }
            case 255:
            {
                return ___getPolylineTransform(this, in, __current);
            }
            case 256:
            {
                return ___getProjectCount(this, in, __current);
            }
            case 257:
            {
                return ___getProjectDescription(this, in, __current);
            }
            case 258:
            {
                return ___getProjectExperimenterRef(this, in, __current);
            }
            case 259:
            {
                return ___getProjectGroupRef(this, in, __current);
            }
            case 260:
            {
                return ___getProjectID(this, in, __current);
            }
            case 261:
            {
                return ___getProjectName(this, in, __current);
            }
            case 262:
            {
                return ___getProjectRefCount(this, in, __current);
            }
            case 263:
            {
                return ___getProjectRefID(this, in, __current);
            }
            case 264:
            {
                return ___getPumpLightSource(this, in, __current);
            }
            case 265:
            {
                return ___getROICount(this, in, __current);
            }
            case 266:
            {
                return ___getROIID(this, in, __current);
            }
            case 267:
            {
                return ___getROIRefCount(this, in, __current);
            }
            case 268:
            {
                return ___getROIRefID(this, in, __current);
            }
            case 269:
            {
                return ___getROIT0(this, in, __current);
            }
            case 270:
            {
                return ___getROIT1(this, in, __current);
            }
            case 271:
            {
                return ___getROIX0(this, in, __current);
            }
            case 272:
            {
                return ___getROIX1(this, in, __current);
            }
            case 273:
            {
                return ___getROIY0(this, in, __current);
            }
            case 274:
            {
                return ___getROIY1(this, in, __current);
            }
            case 275:
            {
                return ___getROIZ0(this, in, __current);
            }
            case 276:
            {
                return ___getROIZ1(this, in, __current);
            }
            case 277:
            {
                return ___getReagentCount(this, in, __current);
            }
            case 278:
            {
                return ___getReagentDescription(this, in, __current);
            }
            case 279:
            {
                return ___getReagentID(this, in, __current);
            }
            case 280:
            {
                return ___getReagentName(this, in, __current);
            }
            case 281:
            {
                return ___getReagentReagentIdentifier(this, in, __current);
            }
            case 282:
            {
                return ___getRectHeight(this, in, __current);
            }
            case 283:
            {
                return ___getRectID(this, in, __current);
            }
            case 284:
            {
                return ___getRectTransform(this, in, __current);
            }
            case 285:
            {
                return ___getRectWidth(this, in, __current);
            }
            case 286:
            {
                return ___getRectX(this, in, __current);
            }
            case 287:
            {
                return ___getRectY(this, in, __current);
            }
            case 288:
            {
                return ___getRegionCount(this, in, __current);
            }
            case 289:
            {
                return ___getRegionID(this, in, __current);
            }
            case 290:
            {
                return ___getRegionName(this, in, __current);
            }
            case 291:
            {
                return ___getRegionTag(this, in, __current);
            }
            case 292:
            {
                return ___getRoiLinkCount(this, in, __current);
            }
            case 293:
            {
                return ___getRoiLinkDirection(this, in, __current);
            }
            case 294:
            {
                return ___getRoiLinkName(this, in, __current);
            }
            case 295:
            {
                return ___getRoiLinkRef(this, in, __current);
            }
            case 296:
            {
                return ___getScreenAcquisitionCount(this, in, __current);
            }
            case 297:
            {
                return ___getScreenAcquisitionEndTime(this, in, __current);
            }
            case 298:
            {
                return ___getScreenAcquisitionID(this, in, __current);
            }
            case 299:
            {
                return ___getScreenAcquisitionStartTime(this, in, __current);
            }
            case 300:
            {
                return ___getScreenCount(this, in, __current);
            }
            case 301:
            {
                return ___getScreenDescription(this, in, __current);
            }
            case 302:
            {
                return ___getScreenExtern(this, in, __current);
            }
            case 303:
            {
                return ___getScreenID(this, in, __current);
            }
            case 304:
            {
                return ___getScreenName(this, in, __current);
            }
            case 305:
            {
                return ___getScreenProtocolDescription(this, in, __current);
            }
            case 306:
            {
                return ___getScreenProtocolIdentifier(this, in, __current);
            }
            case 307:
            {
                return ___getScreenReagentSetDescription(this, in, __current);
            }
            case 308:
            {
                return ___getScreenReagentSetIdentifier(this, in, __current);
            }
            case 309:
            {
                return ___getScreenRefCount(this, in, __current);
            }
            case 310:
            {
                return ___getScreenRefID(this, in, __current);
            }
            case 311:
            {
                return ___getScreenType(this, in, __current);
            }
            case 312:
            {
                return ___getServant(this, in, __current);
            }
            case 313:
            {
                return ___getShapeBaselineShift(this, in, __current);
            }
            case 314:
            {
                return ___getShapeCount(this, in, __current);
            }
            case 315:
            {
                return ___getShapeDirection(this, in, __current);
            }
            case 316:
            {
                return ___getShapeFillColor(this, in, __current);
            }
            case 317:
            {
                return ___getShapeFillOpacity(this, in, __current);
            }
            case 318:
            {
                return ___getShapeFillRule(this, in, __current);
            }
            case 319:
            {
                return ___getShapeFontFamily(this, in, __current);
            }
            case 320:
            {
                return ___getShapeFontSize(this, in, __current);
            }
            case 321:
            {
                return ___getShapeFontStretch(this, in, __current);
            }
            case 322:
            {
                return ___getShapeFontStyle(this, in, __current);
            }
            case 323:
            {
                return ___getShapeFontVariant(this, in, __current);
            }
            case 324:
            {
                return ___getShapeFontWeight(this, in, __current);
            }
            case 325:
            {
                return ___getShapeG(this, in, __current);
            }
            case 326:
            {
                return ___getShapeGlyphOrientationVertical(this, in, __current);
            }
            case 327:
            {
                return ___getShapeID(this, in, __current);
            }
            case 328:
            {
                return ___getShapeLocked(this, in, __current);
            }
            case 329:
            {
                return ___getShapeStrokeAttribute(this, in, __current);
            }
            case 330:
            {
                return ___getShapeStrokeColor(this, in, __current);
            }
            case 331:
            {
                return ___getShapeStrokeDashArray(this, in, __current);
            }
            case 332:
            {
                return ___getShapeStrokeLineCap(this, in, __current);
            }
            case 333:
            {
                return ___getShapeStrokeLineJoin(this, in, __current);
            }
            case 334:
            {
                return ___getShapeStrokeMiterLimit(this, in, __current);
            }
            case 335:
            {
                return ___getShapeStrokeOpacity(this, in, __current);
            }
            case 336:
            {
                return ___getShapeStrokeWidth(this, in, __current);
            }
            case 337:
            {
                return ___getShapeText(this, in, __current);
            }
            case 338:
            {
                return ___getShapeTextAnchor(this, in, __current);
            }
            case 339:
            {
                return ___getShapeTextDecoration(this, in, __current);
            }
            case 340:
            {
                return ___getShapeTextFill(this, in, __current);
            }
            case 341:
            {
                return ___getShapeTextStroke(this, in, __current);
            }
            case 342:
            {
                return ___getShapeTheT(this, in, __current);
            }
            case 343:
            {
                return ___getShapeTheZ(this, in, __current);
            }
            case 344:
            {
                return ___getShapeVectorEffect(this, in, __current);
            }
            case 345:
            {
                return ___getShapeVisibility(this, in, __current);
            }
            case 346:
            {
                return ___getShapeWritingMode(this, in, __current);
            }
            case 347:
            {
                return ___getStageLabelName(this, in, __current);
            }
            case 348:
            {
                return ___getStageLabelX(this, in, __current);
            }
            case 349:
            {
                return ___getStageLabelY(this, in, __current);
            }
            case 350:
            {
                return ___getStageLabelZ(this, in, __current);
            }
            case 351:
            {
                return ___getStagePositionPositionX(this, in, __current);
            }
            case 352:
            {
                return ___getStagePositionPositionY(this, in, __current);
            }
            case 353:
            {
                return ___getStagePositionPositionZ(this, in, __current);
            }
            case 354:
            {
                return ___getThumbnailHref(this, in, __current);
            }
            case 355:
            {
                return ___getThumbnailID(this, in, __current);
            }
            case 356:
            {
                return ___getThumbnailMIMEtype(this, in, __current);
            }
            case 357:
            {
                return ___getTiffDataCount(this, in, __current);
            }
            case 358:
            {
                return ___getTiffDataFileName(this, in, __current);
            }
            case 359:
            {
                return ___getTiffDataFirstC(this, in, __current);
            }
            case 360:
            {
                return ___getTiffDataFirstT(this, in, __current);
            }
            case 361:
            {
                return ___getTiffDataFirstZ(this, in, __current);
            }
            case 362:
            {
                return ___getTiffDataIFD(this, in, __current);
            }
            case 363:
            {
                return ___getTiffDataNumPlanes(this, in, __current);
            }
            case 364:
            {
                return ___getTiffDataUUID(this, in, __current);
            }
            case 365:
            {
                return ___getTransmittanceRangeCutIn(this, in, __current);
            }
            case 366:
            {
                return ___getTransmittanceRangeCutInTolerance(this, in, __current);
            }
            case 367:
            {
                return ___getTransmittanceRangeCutOut(this, in, __current);
            }
            case 368:
            {
                return ___getTransmittanceRangeCutOutTolerance(this, in, __current);
            }
            case 369:
            {
                return ___getTransmittanceRangeTransmittance(this, in, __current);
            }
            case 370:
            {
                return ___getUUID(this, in, __current);
            }
            case 371:
            {
                return ___getWellColumn(this, in, __current);
            }
            case 372:
            {
                return ___getWellCount(this, in, __current);
            }
            case 373:
            {
                return ___getWellExternalDescription(this, in, __current);
            }
            case 374:
            {
                return ___getWellExternalIdentifier(this, in, __current);
            }
            case 375:
            {
                return ___getWellID(this, in, __current);
            }
            case 376:
            {
                return ___getWellReagent(this, in, __current);
            }
            case 377:
            {
                return ___getWellRow(this, in, __current);
            }
            case 378:
            {
                return ___getWellSampleCount(this, in, __current);
            }
            case 379:
            {
                return ___getWellSampleID(this, in, __current);
            }
            case 380:
            {
                return ___getWellSampleImageRef(this, in, __current);
            }
            case 381:
            {
                return ___getWellSampleIndex(this, in, __current);
            }
            case 382:
            {
                return ___getWellSamplePosX(this, in, __current);
            }
            case 383:
            {
                return ___getWellSamplePosY(this, in, __current);
            }
            case 384:
            {
                return ___getWellSampleRefCount(this, in, __current);
            }
            case 385:
            {
                return ___getWellSampleRefID(this, in, __current);
            }
            case 386:
            {
                return ___getWellSampleTimepoint(this, in, __current);
            }
            case 387:
            {
                return ___getWellType(this, in, __current);
            }
            case 388:
            {
                return ___ice_id(this, in, __current);
            }
            case 389:
            {
                return ___ice_ids(this, in, __current);
            }
            case 390:
            {
                return ___ice_isA(this, in, __current);
            }
            case 391:
            {
                return ___ice_ping(this, in, __current);
            }
        }

        assert(false);
        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
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
