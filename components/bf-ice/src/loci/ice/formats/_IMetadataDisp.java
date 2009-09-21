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

public abstract class _IMetadataDisp extends Ice.ObjectImpl implements IMetadata
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
        "::formats::IMetadata"
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

    public final void
    createRoot()
    {
        createRoot(null);
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

    public final void
    setArcType(String type, int instrumentIndex, int lightSourceIndex)
    {
        setArcType(type, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex, null);
    }

    public final void
    setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex, null);
    }

    public final void
    setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        setChannelComponentPixels(pixels, imageIndex, logicalChannelIndex, channelComponentIndex, null);
    }

    public final void
    setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleCx(cx, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleCy(cy, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleR(r, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setContactExperimenter(String experimenter, int groupIndex)
    {
        setContactExperimenter(experimenter, groupIndex, null);
    }

    public final void
    setDatasetDescription(String description, int datasetIndex)
    {
        setDatasetDescription(description, datasetIndex, null);
    }

    public final void
    setDatasetExperimenterRef(String experimenterRef, int datasetIndex)
    {
        setDatasetExperimenterRef(experimenterRef, datasetIndex, null);
    }

    public final void
    setDatasetGroupRef(String groupRef, int datasetIndex)
    {
        setDatasetGroupRef(groupRef, datasetIndex, null);
    }

    public final void
    setDatasetID(String id, int datasetIndex)
    {
        setDatasetID(id, datasetIndex, null);
    }

    public final void
    setDatasetLocked(boolean locked, int datasetIndex)
    {
        setDatasetLocked(locked, datasetIndex, null);
    }

    public final void
    setDatasetName(String name, int datasetIndex)
    {
        setDatasetName(name, datasetIndex, null);
    }

    public final void
    setDatasetRefID(String id, int imageIndex, int datasetRefIndex)
    {
        setDatasetRefID(id, imageIndex, datasetRefIndex, null);
    }

    public final void
    setDetectorAmplificationGain(float amplificationGain, int instrumentIndex, int detectorIndex)
    {
        setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorGain(float gain, int instrumentIndex, int detectorIndex)
    {
        setDetectorGain(gain, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorID(String id, int instrumentIndex, int detectorIndex)
    {
        setDetectorID(id, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex)
    {
        setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorModel(String model, int instrumentIndex, int detectorIndex)
    {
        setDetectorModel(model, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorOffset(float offset, int instrumentIndex, int detectorIndex)
    {
        setDetectorOffset(offset, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex)
    {
        setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsBinning(binning, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setDetectorSettingsReadOutRate(float readOutRate, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsReadOutRate(readOutRate, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setDetectorSettingsVoltage(float voltage, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsVoltage(voltage, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setDetectorType(String type, int instrumentIndex, int detectorIndex)
    {
        setDetectorType(type, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex)
    {
        setDetectorVoltage(voltage, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDetectorZoom(float zoom, int instrumentIndex, int detectorIndex)
    {
        setDetectorZoom(zoom, instrumentIndex, detectorIndex, null);
    }

    public final void
    setDichroicID(String id, int instrumentIndex, int dichroicIndex)
    {
        setDichroicID(id, instrumentIndex, dichroicIndex, null);
    }

    public final void
    setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex)
    {
        setDichroicLotNumber(lotNumber, instrumentIndex, dichroicIndex, null);
    }

    public final void
    setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex)
    {
        setDichroicManufacturer(manufacturer, instrumentIndex, dichroicIndex, null);
    }

    public final void
    setDichroicModel(String model, int instrumentIndex, int dichroicIndex)
    {
        setDichroicModel(model, instrumentIndex, dichroicIndex, null);
    }

    public final void
    setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex)
    {
        setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex, null);
    }

    public final void
    setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex)
    {
        setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex, null);
    }

    public final void
    setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex)
    {
        setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex, null);
    }

    public final void
    setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex)
    {
        setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex, null);
    }

    public final void
    setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex)
    {
        setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex, null);
    }

    public final void
    setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex)
    {
        setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex, null);
    }

    public final void
    setDisplayOptionsDisplay(String display, int imageIndex)
    {
        setDisplayOptionsDisplay(display, imageIndex, null);
    }

    public final void
    setDisplayOptionsID(String id, int imageIndex)
    {
        setDisplayOptionsID(id, imageIndex, null);
    }

    public final void
    setDisplayOptionsZoom(float zoom, int imageIndex)
    {
        setDisplayOptionsZoom(zoom, imageIndex, null);
    }

    public final void
    setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseCx(cx, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseCy(cy, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseRx(rx, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseRy(ry, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex)
    {
        setEmFilterLotNumber(lotNumber, instrumentIndex, filterIndex, null);
    }

    public final void
    setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex)
    {
        setEmFilterManufacturer(manufacturer, instrumentIndex, filterIndex, null);
    }

    public final void
    setEmFilterModel(String model, int instrumentIndex, int filterIndex)
    {
        setEmFilterModel(model, instrumentIndex, filterIndex, null);
    }

    public final void
    setEmFilterType(String type, int instrumentIndex, int filterIndex)
    {
        setEmFilterType(type, instrumentIndex, filterIndex, null);
    }

    public final void
    setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex)
    {
        setExFilterLotNumber(lotNumber, instrumentIndex, filterIndex, null);
    }

    public final void
    setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex)
    {
        setExFilterManufacturer(manufacturer, instrumentIndex, filterIndex, null);
    }

    public final void
    setExFilterModel(String model, int instrumentIndex, int filterIndex)
    {
        setExFilterModel(model, instrumentIndex, filterIndex, null);
    }

    public final void
    setExFilterType(String type, int instrumentIndex, int filterIndex)
    {
        setExFilterType(type, instrumentIndex, filterIndex, null);
    }

    public final void
    setExperimentDescription(String description, int experimentIndex)
    {
        setExperimentDescription(description, experimentIndex, null);
    }

    public final void
    setExperimentExperimenterRef(String experimenterRef, int experimentIndex)
    {
        setExperimentExperimenterRef(experimenterRef, experimentIndex, null);
    }

    public final void
    setExperimentID(String id, int experimentIndex)
    {
        setExperimentID(id, experimentIndex, null);
    }

    public final void
    setExperimentType(String type, int experimentIndex)
    {
        setExperimentType(type, experimentIndex, null);
    }

    public final void
    setExperimenterEmail(String email, int experimenterIndex)
    {
        setExperimenterEmail(email, experimenterIndex, null);
    }

    public final void
    setExperimenterFirstName(String firstName, int experimenterIndex)
    {
        setExperimenterFirstName(firstName, experimenterIndex, null);
    }

    public final void
    setExperimenterID(String id, int experimenterIndex)
    {
        setExperimenterID(id, experimenterIndex, null);
    }

    public final void
    setExperimenterInstitution(String institution, int experimenterIndex)
    {
        setExperimenterInstitution(institution, experimenterIndex, null);
    }

    public final void
    setExperimenterLastName(String lastName, int experimenterIndex)
    {
        setExperimenterLastName(lastName, experimenterIndex, null);
    }

    public final void
    setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex)
    {
        setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex, null);
    }

    public final void
    setExperimenterOMEName(String omeName, int experimenterIndex)
    {
        setExperimenterOMEName(omeName, experimenterIndex, null);
    }

    public final void
    setFilamentType(String type, int instrumentIndex, int lightSourceIndex)
    {
        setFilamentType(type, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex)
    {
        setFilterFilterWheel(filterWheel, instrumentIndex, filterIndex, null);
    }

    public final void
    setFilterID(String id, int instrumentIndex, int filterIndex)
    {
        setFilterID(id, instrumentIndex, filterIndex, null);
    }

    public final void
    setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex)
    {
        setFilterLotNumber(lotNumber, instrumentIndex, filterIndex, null);
    }

    public final void
    setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex)
    {
        setFilterManufacturer(manufacturer, instrumentIndex, filterIndex, null);
    }

    public final void
    setFilterModel(String model, int instrumentIndex, int filterIndex)
    {
        setFilterModel(model, instrumentIndex, filterIndex, null);
    }

    public final void
    setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetDichroic(dichroic, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetEmFilter(emFilter, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetExFilter(exFilter, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterSetID(String id, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetID(id, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetLotNumber(lotNumber, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetManufacturer(manufacturer, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterSetModel(String model, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetModel(model, instrumentIndex, filterSetIndex, null);
    }

    public final void
    setFilterType(String type, int instrumentIndex, int filterIndex)
    {
        setFilterType(type, instrumentIndex, filterIndex, null);
    }

    public final void
    setGroupID(String id, int groupIndex)
    {
        setGroupID(id, groupIndex, null);
    }

    public final void
    setGroupName(String name, int groupIndex)
    {
        setGroupName(name, groupIndex, null);
    }

    public final void
    setImageAcquiredPixels(String acquiredPixels, int imageIndex)
    {
        setImageAcquiredPixels(acquiredPixels, imageIndex, null);
    }

    public final void
    setImageCreationDate(String creationDate, int imageIndex)
    {
        setImageCreationDate(creationDate, imageIndex, null);
    }

    public final void
    setImageDefaultPixels(String defaultPixels, int imageIndex)
    {
        setImageDefaultPixels(defaultPixels, imageIndex, null);
    }

    public final void
    setImageDescription(String description, int imageIndex)
    {
        setImageDescription(description, imageIndex, null);
    }

    public final void
    setImageExperimentRef(String experimentRef, int imageIndex)
    {
        setImageExperimentRef(experimentRef, imageIndex, null);
    }

    public final void
    setImageExperimenterRef(String experimenterRef, int imageIndex)
    {
        setImageExperimenterRef(experimenterRef, imageIndex, null);
    }

    public final void
    setImageGroupRef(String groupRef, int imageIndex)
    {
        setImageGroupRef(groupRef, imageIndex, null);
    }

    public final void
    setImageID(String id, int imageIndex)
    {
        setImageID(id, imageIndex, null);
    }

    public final void
    setImageInstrumentRef(String instrumentRef, int imageIndex)
    {
        setImageInstrumentRef(instrumentRef, imageIndex, null);
    }

    public final void
    setImageName(String name, int imageIndex)
    {
        setImageName(name, imageIndex, null);
    }

    public final void
    setImagingEnvironmentAirPressure(float airPressure, int imageIndex)
    {
        setImagingEnvironmentAirPressure(airPressure, imageIndex, null);
    }

    public final void
    setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex)
    {
        setImagingEnvironmentCO2Percent(cO2Percent, imageIndex, null);
    }

    public final void
    setImagingEnvironmentHumidity(float humidity, int imageIndex)
    {
        setImagingEnvironmentHumidity(humidity, imageIndex, null);
    }

    public final void
    setImagingEnvironmentTemperature(float temperature, int imageIndex)
    {
        setImagingEnvironmentTemperature(temperature, imageIndex, null);
    }

    public final void
    setInstrumentID(String id, int instrumentIndex)
    {
        setInstrumentID(id, instrumentIndex, null);
    }

    public final void
    setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex)
    {
        setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex)
    {
        setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserPockelCell(boolean pockelCell, int instrumentIndex, int lightSourceIndex)
    {
        setLaserPockelCell(pockelCell, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex)
    {
        setLaserPulse(pulse, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserRepetitionRate(boolean repetitionRate, int instrumentIndex, int lightSourceIndex)
    {
        setLaserRepetitionRate(repetitionRate, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex)
    {
        setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserType(String type, int instrumentIndex, int lightSourceIndex)
    {
        setLaserType(type, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex)
    {
        setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLightSourceID(String id, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourceID(id, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourceModel(model, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourcePower(power, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLightSourceRefAttenuation(float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        setLightSourceRefAttenuation(attenuation, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null);
    }

    public final void
    setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        setLightSourceRefLightSource(lightSource, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null);
    }

    public final void
    setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        setLightSourceRefWavelength(wavelength, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null);
    }

    public final void
    setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex)
    {
        setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex)
    {
        setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex)
    {
        setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLineID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineX1(x1, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineX2(x2, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineY1(y1, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineY2(y2, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelDetector(detector, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelFilterSet(filterSet, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelID(id, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelLightSource(lightSource, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelMode(mode, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelName(name, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelSecondaryEmissionFilter(secondaryEmissionFilter, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelSecondaryExcitationFilter(secondaryExcitationFilter, imageIndex, logicalChannelIndex, null);
    }

    public final void
    setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskHeight(height, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskPixelsBigEndian(boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsBigEndian(bigEndian, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskPixelsBinData(String binData, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsBinData(binData, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsExtendedPixelType(extendedPixelType, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsSizeX(sizeX, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsSizeY(sizeY, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskWidth(width, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskX(x, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskY(y, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex)
    {
        setMicrobeamManipulationExperimenterRef(experimenterRef, imageIndex, microbeamManipulationIndex, null);
    }

    public final void
    setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex)
    {
        setMicrobeamManipulationID(id, imageIndex, microbeamManipulationIndex, null);
    }

    public final void
    setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex)
    {
        setMicrobeamManipulationRefID(id, experimentIndex, microbeamManipulationRefIndex, null);
    }

    public final void
    setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex)
    {
        setMicrobeamManipulationType(type, imageIndex, microbeamManipulationIndex, null);
    }

    public final void
    setMicroscopeID(String id, int instrumentIndex)
    {
        setMicroscopeID(id, instrumentIndex, null);
    }

    public final void
    setMicroscopeManufacturer(String manufacturer, int instrumentIndex)
    {
        setMicroscopeManufacturer(manufacturer, instrumentIndex, null);
    }

    public final void
    setMicroscopeModel(String model, int instrumentIndex)
    {
        setMicroscopeModel(model, instrumentIndex, null);
    }

    public final void
    setMicroscopeSerialNumber(String serialNumber, int instrumentIndex)
    {
        setMicroscopeSerialNumber(serialNumber, instrumentIndex, null);
    }

    public final void
    setMicroscopeType(String type, int instrumentIndex)
    {
        setMicroscopeType(type, instrumentIndex, null);
    }

    public final void
    setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex)
    {
        setOTFBinaryFile(binaryFile, instrumentIndex, otfIndex, null);
    }

    public final void
    setOTFID(String id, int instrumentIndex, int otfIndex)
    {
        setOTFID(id, instrumentIndex, otfIndex, null);
    }

    public final void
    setOTFObjective(String objective, int instrumentIndex, int otfIndex)
    {
        setOTFObjective(objective, instrumentIndex, otfIndex, null);
    }

    public final void
    setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex)
    {
        setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex, null);
    }

    public final void
    setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex)
    {
        setOTFPixelType(pixelType, instrumentIndex, otfIndex, null);
    }

    public final void
    setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex)
    {
        setOTFSizeX(sizeX, instrumentIndex, otfIndex, null);
    }

    public final void
    setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex)
    {
        setOTFSizeY(sizeY, instrumentIndex, otfIndex, null);
    }

    public final void
    setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveCorrection(correction, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveID(String id, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveID(id, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveIris(iris, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveModel(String model, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveModel(model, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setObjectiveSettingsCorrectionCollar(float correctionCollar, int imageIndex)
    {
        setObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex, null);
    }

    public final void
    setObjectiveSettingsMedium(String medium, int imageIndex)
    {
        setObjectiveSettingsMedium(medium, imageIndex, null);
    }

    public final void
    setObjectiveSettingsObjective(String objective, int imageIndex)
    {
        setObjectiveSettingsObjective(objective, imageIndex, null);
    }

    public final void
    setObjectiveSettingsRefractiveIndex(float refractiveIndex, int imageIndex)
    {
        setObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex, null);
    }

    public final void
    setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex, null);
    }

    public final void
    setPathD(String d, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPathD(d, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPathID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPathID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex)
    {
        setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex)
    {
        setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsID(String id, int imageIndex, int pixelsIndex)
    {
        setPixelsID(id, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex)
    {
        setPixelsPixelType(pixelType, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeC(sizeC, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeT(sizeT, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeX(sizeX, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeY(sizeY, imageIndex, pixelsIndex, null);
    }

    public final void
    setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex, null);
    }

    public final void
    setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneHashSHA1(hashSHA1, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneID(id, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex)
    {
        setPlateColumnNamingConvention(columnNamingConvention, plateIndex, null);
    }

    public final void
    setPlateDescription(String description, int plateIndex)
    {
        setPlateDescription(description, plateIndex, null);
    }

    public final void
    setPlateExternalIdentifier(String externalIdentifier, int plateIndex)
    {
        setPlateExternalIdentifier(externalIdentifier, plateIndex, null);
    }

    public final void
    setPlateID(String id, int plateIndex)
    {
        setPlateID(id, plateIndex, null);
    }

    public final void
    setPlateName(String name, int plateIndex)
    {
        setPlateName(name, plateIndex, null);
    }

    public final void
    setPlateRefID(String id, int screenIndex, int plateRefIndex)
    {
        setPlateRefID(id, screenIndex, plateRefIndex, null);
    }

    public final void
    setPlateRefSample(int sample, int screenIndex, int plateRefIndex)
    {
        setPlateRefSample(sample, screenIndex, plateRefIndex, null);
    }

    public final void
    setPlateRefWell(String well, int screenIndex, int plateRefIndex)
    {
        setPlateRefWell(well, screenIndex, plateRefIndex, null);
    }

    public final void
    setPlateRowNamingConvention(String rowNamingConvention, int plateIndex)
    {
        setPlateRowNamingConvention(rowNamingConvention, plateIndex, null);
    }

    public final void
    setPlateStatus(String status, int plateIndex)
    {
        setPlateStatus(status, plateIndex, null);
    }

    public final void
    setPlateWellOriginX(double wellOriginX, int plateIndex)
    {
        setPlateWellOriginX(wellOriginX, plateIndex, null);
    }

    public final void
    setPlateWellOriginY(double wellOriginY, int plateIndex)
    {
        setPlateWellOriginY(wellOriginY, plateIndex, null);
    }

    public final void
    setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointCx(cx, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointCy(cy, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPointID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPointR(String r, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointR(r, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolygonID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolygonPoints(points, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolygonTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolylineID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolylinePoints(points, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolylineTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setProjectDescription(String description, int projectIndex)
    {
        setProjectDescription(description, projectIndex, null);
    }

    public final void
    setProjectExperimenterRef(String experimenterRef, int projectIndex)
    {
        setProjectExperimenterRef(experimenterRef, projectIndex, null);
    }

    public final void
    setProjectGroupRef(String groupRef, int projectIndex)
    {
        setProjectGroupRef(groupRef, projectIndex, null);
    }

    public final void
    setProjectID(String id, int projectIndex)
    {
        setProjectID(id, projectIndex, null);
    }

    public final void
    setProjectName(String name, int projectIndex)
    {
        setProjectName(name, projectIndex, null);
    }

    public final void
    setProjectRefID(String id, int datasetIndex, int projectRefIndex)
    {
        setProjectRefID(id, datasetIndex, projectRefIndex, null);
    }

    public final void
    setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex)
    {
        setPumpLightSource(lightSource, instrumentIndex, lightSourceIndex, null);
    }

    public final void
    setROIID(String id, int imageIndex, int roiIndex)
    {
        setROIID(id, imageIndex, roiIndex, null);
    }

    public final void
    setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex)
    {
        setROIRefID(id, imageIndex, microbeamManipulationIndex, roiRefIndex, null);
    }

    public final void
    setROIT0(int t0, int imageIndex, int roiIndex)
    {
        setROIT0(t0, imageIndex, roiIndex, null);
    }

    public final void
    setROIT1(int t1, int imageIndex, int roiIndex)
    {
        setROIT1(t1, imageIndex, roiIndex, null);
    }

    public final void
    setROIX0(int x0, int imageIndex, int roiIndex)
    {
        setROIX0(x0, imageIndex, roiIndex, null);
    }

    public final void
    setROIX1(int x1, int imageIndex, int roiIndex)
    {
        setROIX1(x1, imageIndex, roiIndex, null);
    }

    public final void
    setROIY0(int y0, int imageIndex, int roiIndex)
    {
        setROIY0(y0, imageIndex, roiIndex, null);
    }

    public final void
    setROIY1(int y1, int imageIndex, int roiIndex)
    {
        setROIY1(y1, imageIndex, roiIndex, null);
    }

    public final void
    setROIZ0(int z0, int imageIndex, int roiIndex)
    {
        setROIZ0(z0, imageIndex, roiIndex, null);
    }

    public final void
    setROIZ1(int z1, int imageIndex, int roiIndex)
    {
        setROIZ1(z1, imageIndex, roiIndex, null);
    }

    public final void
    setReagentDescription(String description, int screenIndex, int reagentIndex)
    {
        setReagentDescription(description, screenIndex, reagentIndex, null);
    }

    public final void
    setReagentID(String id, int screenIndex, int reagentIndex)
    {
        setReagentID(id, screenIndex, reagentIndex, null);
    }

    public final void
    setReagentName(String name, int screenIndex, int reagentIndex)
    {
        setReagentName(name, screenIndex, reagentIndex, null);
    }

    public final void
    setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex)
    {
        setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex, null);
    }

    public final void
    setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectHeight(height, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setRectID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectTransform(transform, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectWidth(width, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setRectX(String x, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectX(x, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setRectY(String y, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectY(y, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setRegionID(String id, int imageIndex, int regionIndex)
    {
        setRegionID(id, imageIndex, regionIndex, null);
    }

    public final void
    setRegionName(String name, int imageIndex, int regionIndex)
    {
        setRegionName(name, imageIndex, regionIndex, null);
    }

    public final void
    setRegionTag(String tag, int imageIndex, int regionIndex)
    {
        setRegionTag(tag, imageIndex, regionIndex, null);
    }

    public final void
    setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex)
    {
        setRoiLinkDirection(direction, imageIndex, roiIndex, roiLinkIndex, null);
    }

    public final void
    setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex)
    {
        setRoiLinkName(name, imageIndex, roiIndex, roiLinkIndex, null);
    }

    public final void
    setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex)
    {
        setRoiLinkRef(ref, imageIndex, roiIndex, roiLinkIndex, null);
    }

    public final void
    setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex)
    {
        setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex, null);
    }

    public final void
    setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex)
    {
        setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex, null);
    }

    public final void
    setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex)
    {
        setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex, null);
    }

    public final void
    setScreenDescription(String description, int screenIndex)
    {
        setScreenDescription(description, screenIndex, null);
    }

    public final void
    setScreenExtern(String extern, int screenIndex)
    {
        setScreenExtern(extern, screenIndex, null);
    }

    public final void
    setScreenID(String id, int screenIndex)
    {
        setScreenID(id, screenIndex, null);
    }

    public final void
    setScreenName(String name, int screenIndex)
    {
        setScreenName(name, screenIndex, null);
    }

    public final void
    setScreenProtocolDescription(String protocolDescription, int screenIndex)
    {
        setScreenProtocolDescription(protocolDescription, screenIndex, null);
    }

    public final void
    setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex)
    {
        setScreenProtocolIdentifier(protocolIdentifier, screenIndex, null);
    }

    public final void
    setScreenReagentSetDescription(String reagentSetDescription, int screenIndex)
    {
        setScreenReagentSetDescription(reagentSetDescription, screenIndex, null);
    }

    public final void
    setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex)
    {
        setScreenReagentSetIdentifier(reagentSetIdentifier, screenIndex, null);
    }

    public final void
    setScreenRefID(String id, int plateIndex, int screenRefIndex)
    {
        setScreenRefID(id, plateIndex, screenRefIndex, null);
    }

    public final void
    setScreenType(String type, int screenIndex)
    {
        setScreenType(type, screenIndex, null);
    }

    public final void
    setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeBaselineShift(baselineShift, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeDirection(direction, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFillColor(fillColor, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFillOpacity(fillOpacity, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFillRule(fillRule, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontFamily(fontFamily, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontSize(fontSize, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontStretch(fontStretch, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontStyle(fontStyle, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontVariant(fontVariant, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontWeight(fontWeight, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeG(g, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeGlyphOrientationVertical(glyphOrientationVertical, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeID(id, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeLocked(boolean locked, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeLocked(locked, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeAttribute(strokeAttribute, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeColor(strokeColor, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeDashArray(strokeDashArray, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeLineCap(strokeLineCap, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeLineJoin(strokeLineJoin, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeMiterLimit(strokeMiterLimit, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeOpacity(float strokeOpacity, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeOpacity(strokeOpacity, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeWidth(strokeWidth, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeText(text, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTextAnchor(textAnchor, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTextDecoration(textDecoration, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTextFill(textFill, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTextStroke(textStroke, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTheT(theT, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTheZ(theZ, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeVectorEffect(vectorEffect, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeVisibility(boolean visibility, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeVisibility(visibility, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeWritingMode(writingMode, imageIndex, roiIndex, shapeIndex, null);
    }

    public final void
    setStageLabelName(String name, int imageIndex)
    {
        setStageLabelName(name, imageIndex, null);
    }

    public final void
    setStageLabelX(float x, int imageIndex)
    {
        setStageLabelX(x, imageIndex, null);
    }

    public final void
    setStageLabelY(float y, int imageIndex)
    {
        setStageLabelY(y, imageIndex, null);
    }

    public final void
    setStageLabelZ(float z, int imageIndex)
    {
        setStageLabelZ(z, imageIndex, null);
    }

    public final void
    setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex, null);
    }

    public final void
    setThumbnailHref(String href, int imageIndex)
    {
        setThumbnailHref(href, imageIndex, null);
    }

    public final void
    setThumbnailID(String id, int imageIndex)
    {
        setThumbnailID(id, imageIndex, null);
    }

    public final void
    setThumbnailMIMEtype(String mimEtype, int imageIndex)
    {
        setThumbnailMIMEtype(mimEtype, imageIndex, null);
    }

    public final void
    setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex, null);
    }

    public final void
    setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeCutIn(cutIn, instrumentIndex, filterIndex, null);
    }

    public final void
    setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeCutInTolerance(cutInTolerance, instrumentIndex, filterIndex, null);
    }

    public final void
    setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeCutOut(cutOut, instrumentIndex, filterIndex, null);
    }

    public final void
    setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeCutOutTolerance(cutOutTolerance, instrumentIndex, filterIndex, null);
    }

    public final void
    setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex, null);
    }

    public final void
    setUUID(String uuid)
    {
        setUUID(uuid, null);
    }

    public final void
    setWellColumn(int column, int plateIndex, int wellIndex)
    {
        setWellColumn(column, plateIndex, wellIndex, null);
    }

    public final void
    setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex)
    {
        setWellExternalDescription(externalDescription, plateIndex, wellIndex, null);
    }

    public final void
    setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex)
    {
        setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex, null);
    }

    public final void
    setWellID(String id, int plateIndex, int wellIndex)
    {
        setWellID(id, plateIndex, wellIndex, null);
    }

    public final void
    setWellReagent(String reagent, int plateIndex, int wellIndex)
    {
        setWellReagent(reagent, plateIndex, wellIndex, null);
    }

    public final void
    setWellRow(int row, int plateIndex, int wellIndex)
    {
        setWellRow(row, plateIndex, wellIndex, null);
    }

    public final void
    setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleImageRef(imageRef, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex)
    {
        setWellSampleRefID(id, screenIndex, screenAcquisitionIndex, wellSampleRefIndex, null);
    }

    public final void
    setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex, null);
    }

    public final void
    setWellType(String type, int plateIndex, int wellIndex)
    {
        setWellType(type, plateIndex, wellIndex, null);
    }

    public static Ice.DispatchStatus
    ___getOMEXML(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getOMEXML(__current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getChannelComponentCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDatasetCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getDatasetCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getDatasetRefCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDichroicCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExperimentCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getExperimentCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimenterCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getExperimenterCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getExperimenterMembershipCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterSetCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getGroupCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getGroupCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getGroupRefCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImageCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getImageCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getInstrumentCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getInstrumentCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getLightSourceCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLightSourceRefCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMicrobeamManipulationCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMicrobeamManipulationRefCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getOTFCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPixelsCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlaneCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPlateCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPlateRefCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getProjectCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getProjectCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getProjectRefCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getROICount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getROIRefCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getReagentCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRegionCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRoiLinkCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getScreenCount(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getScreenAcquisitionCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenRefCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTiffDataCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellSampleCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellSampleRefCount(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getUUID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getUUID(__current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getArcType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getChannelComponentColorDomain(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getChannelComponentIndex(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getChannelComponentPixels(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getCircleCx(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getCircleCy(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getCircleID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getCircleR(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getCircleTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getContactExperimenter(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDatasetDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDatasetExperimenterRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDatasetGroupRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDatasetID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDatasetLocked(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDatasetName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDatasetRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorAmplificationGain(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorGain(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorOffset(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorSerialNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorVoltage(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorZoom(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorSettingsBinning(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorSettingsDetector(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorSettingsGain(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorSettingsOffset(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorSettingsReadOutRate(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDetectorSettingsVoltage(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDichroicID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDichroicLotNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDichroicManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDichroicModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDimensionsPhysicalSizeX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDimensionsPhysicalSizeY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDimensionsPhysicalSizeZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDimensionsTimeIncrement(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDimensionsWaveIncrement(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDimensionsWaveStart(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDisplayOptionsDisplay(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDisplayOptionsID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getDisplayOptionsZoom(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getEllipseCx(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getEllipseCy(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getEllipseID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getEllipseRx(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getEllipseRy(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getEllipseTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getEmFilterLotNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getEmFilterManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getEmFilterModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getEmFilterType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExFilterLotNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExFilterManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExFilterModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExFilterType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExperimentDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExperimentExperimenterRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExperimentID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExperimentType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExperimenterEmail(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExperimenterFirstName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExperimenterID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExperimenterInstitution(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExperimenterLastName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExperimenterOMEName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getExperimenterMembershipGroup(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilamentType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterFilterWheel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterLotNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterSetDichroic(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterSetEmFilter(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterSetExFilter(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterSetID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterSetLotNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterSetManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getFilterSetModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getGroupID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getGroupName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImageAcquiredPixels(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImageCreationDate(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImageDefaultPixels(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImageDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImageExperimentRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImageExperimenterRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImageGroupRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImageID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImageInstrumentRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImageName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImagingEnvironmentAirPressure(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImagingEnvironmentCO2Percent(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImagingEnvironmentHumidity(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getImagingEnvironmentTemperature(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getInstrumentID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLaserFrequencyMultiplication(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLaserLaserMedium(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLaserPockelCell(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLaserPulse(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLaserRepetitionRate(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLaserTuneable(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLaserType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLaserWavelength(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLightSourceID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLightSourceManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLightSourceModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLightSourcePower(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLightSourceSerialNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLightSourceRefAttenuation(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLightSourceRefLightSource(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLightSourceRefWavelength(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLightSourceSettingsAttenuation(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLightSourceSettingsLightSource(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLightSourceSettingsWavelength(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLineID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLineTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLineX1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLineX2(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLineY1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLineY2(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelContrastMethod(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelDetector(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelEmWave(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelExWave(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelFilterSet(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelFluor(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelIlluminationType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelLightSource(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelMode(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelNdFilter(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelOTF(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelPhotometricInterpretation(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelPinholeSize(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelPockelCellSetting(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelSamplesPerPixel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelSecondaryEmissionFilter(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getLogicalChannelSecondaryExcitationFilter(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMaskHeight(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMaskID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMaskTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMaskWidth(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMaskX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMaskY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMaskPixelsBigEndian(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMaskPixelsBinData(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMaskPixelsExtendedPixelType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMaskPixelsID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMaskPixelsSizeX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMaskPixelsSizeY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMicrobeamManipulationExperimenterRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMicrobeamManipulationID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMicrobeamManipulationType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMicrobeamManipulationRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMicroscopeID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMicroscopeManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMicroscopeModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMicroscopeSerialNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getMicroscopeType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getOTFBinaryFile(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getOTFID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getOTFObjective(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getOTFOpticalAxisAveraged(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getOTFPixelType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getOTFSizeX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getOTFSizeY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveCalibratedMagnification(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveCorrection(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveImmersion(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveIris(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveLensNA(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveNominalMagnification(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveSerialNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveWorkingDistance(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveSettingsCorrectionCollar(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveSettingsMedium(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveSettingsObjective(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getObjectiveSettingsRefractiveIndex(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPathD(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPathID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPixelsBigEndian(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPixelsDimensionOrder(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPixelsID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPixelsPixelType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPixelsSizeC(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPixelsSizeT(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPixelsSizeX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPixelsSizeY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPixelsSizeZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlaneHashSHA1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlaneID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlaneTheC(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlaneTheT(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlaneTheZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlaneTimingDeltaT(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlaneTimingExposureTime(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateColumnNamingConvention(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateExternalIdentifier(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateRowNamingConvention(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateStatus(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateWellOriginX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateWellOriginY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateRefSample(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPlateRefWell(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPointCx(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPointCy(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPointID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPointR(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPointTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPolygonID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPolygonPoints(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPolygonTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPolylineID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPolylinePoints(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPolylineTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getProjectDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getProjectExperimenterRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getProjectGroupRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getProjectID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getProjectName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getProjectRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getPumpLightSource(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getROIID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getROIT0(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getROIT1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getROIX0(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getROIX1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getROIY0(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getROIY1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getROIZ0(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getROIZ1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getROIRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getReagentDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getReagentID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getReagentName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getReagentReagentIdentifier(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRectHeight(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRectID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRectTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRectWidth(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRectX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRectY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRegionID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRegionName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRegionTag(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRoiLinkDirection(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRoiLinkName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getRoiLinkRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenExtern(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenProtocolDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenProtocolIdentifier(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenReagentSetDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenReagentSetIdentifier(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenAcquisitionEndTime(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenAcquisitionID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenAcquisitionStartTime(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getScreenRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeBaselineShift(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeDirection(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeFillColor(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeFillOpacity(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeFillRule(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeFontFamily(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeFontSize(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeFontStretch(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeFontStyle(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeFontVariant(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeFontWeight(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeG(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeGlyphOrientationVertical(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeLocked(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeStrokeAttribute(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeStrokeColor(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeStrokeDashArray(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeStrokeLineCap(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeStrokeLineJoin(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeStrokeMiterLimit(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeStrokeOpacity(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeStrokeWidth(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeText(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeTextAnchor(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeTextDecoration(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeTextFill(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeTextStroke(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeTheT(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeTheZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeVectorEffect(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeVisibility(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getShapeWritingMode(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getStageLabelName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getStageLabelX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getStageLabelY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getStageLabelZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getStagePositionPositionX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getStagePositionPositionY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getStagePositionPositionZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getThumbnailHref(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getThumbnailID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getThumbnailMIMEtype(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTiffDataFileName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTiffDataFirstC(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTiffDataFirstT(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTiffDataFirstZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTiffDataIFD(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTiffDataNumPlanes(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTiffDataUUID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTransmittanceRangeCutIn(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTransmittanceRangeCutInTolerance(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTransmittanceRangeCutOut(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTransmittanceRangeCutOutTolerance(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getTransmittanceRangeTransmittance(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellColumn(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellExternalDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellExternalIdentifier(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellReagent(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellRow(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellSampleID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellSampleImageRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellSampleIndex(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellSamplePosX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellSamplePosY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellSampleTimepoint(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___getWellSampleRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
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

    public static Ice.DispatchStatus
    ___createRoot(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.createRoot(__current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setUUID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String uuid;
        uuid = __is.readString();
        __is.endReadEncaps();
        __obj.setUUID(uuid, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setArcType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setArcType(type, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setChannelComponentColorDomain(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String colorDomain;
        colorDomain = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setChannelComponentIndex(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int index;
        index = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setChannelComponentPixels(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String pixels;
        pixels = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        int channelComponentIndex;
        channelComponentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setChannelComponentPixels(pixels, imageIndex, logicalChannelIndex, channelComponentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setCircleCx(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String cx;
        cx = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setCircleCx(cx, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setCircleCy(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String cy;
        cy = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setCircleCy(cy, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setCircleID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setCircleID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setCircleR(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String r;
        r = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setCircleR(r, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setCircleTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setCircleTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setContactExperimenter(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimenter;
        experimenter = __is.readString();
        int groupIndex;
        groupIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setContactExperimenter(experimenter, groupIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetDescription(description, datasetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetExperimenterRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimenterRef;
        experimenterRef = __is.readString();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetExperimenterRef(experimenterRef, datasetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetGroupRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String groupRef;
        groupRef = __is.readString();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetGroupRef(groupRef, datasetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetID(id, datasetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetLocked(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean locked;
        locked = __is.readBool();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetLocked(locked, datasetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int datasetIndex;
        datasetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetName(name, datasetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDatasetRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int datasetRefIndex;
        datasetRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDatasetRefID(id, imageIndex, datasetRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorAmplificationGain(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float amplificationGain;
        amplificationGain = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorGain(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float gain;
        gain = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorGain(gain, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorID(id, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorModel(model, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorOffset(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float offset;
        offset = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorOffset(offset, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSerialNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String serialNumber;
        serialNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorType(type, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorVoltage(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float voltage;
        voltage = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorVoltage(voltage, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorZoom(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float zoom;
        zoom = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int detectorIndex;
        detectorIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorZoom(zoom, instrumentIndex, detectorIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSettingsBinning(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String binning;
        binning = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSettingsBinning(binning, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSettingsDetector(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String detector;
        detector = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSettingsGain(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float gain;
        gain = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSettingsOffset(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float offset;
        offset = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSettingsReadOutRate(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float readOutRate;
        readOutRate = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSettingsReadOutRate(readOutRate, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDetectorSettingsVoltage(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float voltage;
        voltage = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDetectorSettingsVoltage(voltage, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDichroicID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int dichroicIndex;
        dichroicIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDichroicID(id, instrumentIndex, dichroicIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDichroicLotNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lotNumber;
        lotNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int dichroicIndex;
        dichroicIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDichroicLotNumber(lotNumber, instrumentIndex, dichroicIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDichroicManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int dichroicIndex;
        dichroicIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDichroicManufacturer(manufacturer, instrumentIndex, dichroicIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDichroicModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int dichroicIndex;
        dichroicIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDichroicModel(model, instrumentIndex, dichroicIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDimensionsPhysicalSizeX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float physicalSizeX;
        physicalSizeX = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDimensionsPhysicalSizeY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float physicalSizeY;
        physicalSizeY = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDimensionsPhysicalSizeZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float physicalSizeZ;
        physicalSizeZ = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDimensionsTimeIncrement(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float timeIncrement;
        timeIncrement = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDimensionsWaveIncrement(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int waveIncrement;
        waveIncrement = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDimensionsWaveStart(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int waveStart;
        waveStart = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDisplayOptionsDisplay(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String display;
        display = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDisplayOptionsDisplay(display, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDisplayOptionsID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDisplayOptionsID(id, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setDisplayOptionsZoom(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float zoom;
        zoom = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setDisplayOptionsZoom(zoom, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEllipseCx(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String cx;
        cx = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEllipseCx(cx, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEllipseCy(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String cy;
        cy = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEllipseCy(cy, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEllipseID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEllipseID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEllipseRx(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String rx;
        rx = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEllipseRx(rx, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEllipseRy(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String ry;
        ry = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEllipseRy(ry, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEllipseTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEllipseTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEmFilterLotNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lotNumber;
        lotNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEmFilterLotNumber(lotNumber, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEmFilterManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEmFilterManufacturer(manufacturer, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEmFilterModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEmFilterModel(model, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setEmFilterType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setEmFilterType(type, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExFilterLotNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lotNumber;
        lotNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExFilterLotNumber(lotNumber, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExFilterManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExFilterManufacturer(manufacturer, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExFilterModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExFilterModel(model, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExFilterType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExFilterType(type, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimentDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimentDescription(description, experimentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimentExperimenterRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimenterRef;
        experimenterRef = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimentExperimenterRef(experimenterRef, experimentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimentID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimentID(id, experimentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimentType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimentType(type, experimentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterEmail(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String email;
        email = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterEmail(email, experimenterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterFirstName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String firstName;
        firstName = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterFirstName(firstName, experimenterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterID(id, experimenterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterInstitution(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String institution;
        institution = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterInstitution(institution, experimenterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterLastName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lastName;
        lastName = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterLastName(lastName, experimenterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterOMEName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String omeName;
        omeName = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterOMEName(omeName, experimenterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setExperimenterMembershipGroup(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String group;
        group = __is.readString();
        int experimenterIndex;
        experimenterIndex = __is.readInt();
        int groupRefIndex;
        groupRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilamentType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilamentType(type, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterFilterWheel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String filterWheel;
        filterWheel = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterFilterWheel(filterWheel, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterID(id, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterLotNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lotNumber;
        lotNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterLotNumber(lotNumber, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterManufacturer(manufacturer, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterModel(model, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterType(type, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetDichroic(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String dichroic;
        dichroic = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetDichroic(dichroic, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetEmFilter(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String emFilter;
        emFilter = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetEmFilter(emFilter, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetExFilter(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String exFilter;
        exFilter = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetExFilter(exFilter, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetID(id, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetLotNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lotNumber;
        lotNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetLotNumber(lotNumber, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetManufacturer(manufacturer, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFilterSetModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterSetIndex;
        filterSetIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setFilterSetModel(model, instrumentIndex, filterSetIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setGroupID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int groupIndex;
        groupIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setGroupID(id, groupIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setGroupName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int groupIndex;
        groupIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setGroupName(name, groupIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageAcquiredPixels(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String acquiredPixels;
        acquiredPixels = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageAcquiredPixels(acquiredPixels, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageCreationDate(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String creationDate;
        creationDate = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageCreationDate(creationDate, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageDefaultPixels(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String defaultPixels;
        defaultPixels = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageDefaultPixels(defaultPixels, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageDescription(description, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageExperimentRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimentRef;
        experimentRef = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageExperimentRef(experimentRef, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageExperimenterRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimenterRef;
        experimenterRef = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageExperimenterRef(experimenterRef, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageGroupRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String groupRef;
        groupRef = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageGroupRef(groupRef, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageID(id, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageInstrumentRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String instrumentRef;
        instrumentRef = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageInstrumentRef(instrumentRef, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImageName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImageName(name, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImagingEnvironmentAirPressure(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float airPressure;
        airPressure = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImagingEnvironmentAirPressure(airPressure, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImagingEnvironmentCO2Percent(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float cO2Percent;
        cO2Percent = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImagingEnvironmentHumidity(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float humidity;
        humidity = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImagingEnvironmentHumidity(humidity, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setImagingEnvironmentTemperature(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float temperature;
        temperature = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setImagingEnvironmentTemperature(temperature, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setInstrumentID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setInstrumentID(id, instrumentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserFrequencyMultiplication(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int frequencyMultiplication;
        frequencyMultiplication = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserLaserMedium(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String laserMedium;
        laserMedium = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserPockelCell(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean pockelCell;
        pockelCell = __is.readBool();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserPockelCell(pockelCell, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserPulse(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String pulse;
        pulse = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserPulse(pulse, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserRepetitionRate(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean repetitionRate;
        repetitionRate = __is.readBool();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserRepetitionRate(repetitionRate, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserTuneable(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean tuneable;
        tuneable = __is.readBool();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserType(type, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLaserWavelength(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int wavelength;
        wavelength = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceID(id, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceModel(model, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourcePower(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float power;
        power = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourcePower(power, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceSerialNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String serialNumber;
        serialNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceRefAttenuation(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float attenuation;
        attenuation = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        int lightSourceRefIndex;
        lightSourceRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceRefAttenuation(attenuation, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceRefLightSource(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lightSource;
        lightSource = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        int lightSourceRefIndex;
        lightSourceRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceRefLightSource(lightSource, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceRefWavelength(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int wavelength;
        wavelength = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        int lightSourceRefIndex;
        lightSourceRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceRefWavelength(wavelength, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceSettingsAttenuation(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float attenuation;
        attenuation = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceSettingsLightSource(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lightSource;
        lightSource = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLightSourceSettingsWavelength(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int wavelength;
        wavelength = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLineID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLineID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLineTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLineTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLineX1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String x1;
        x1 = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLineX1(x1, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLineX2(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String x2;
        x2 = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLineX2(x2, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLineY1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String y1;
        y1 = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLineY1(y1, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLineY2(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String y2;
        y2 = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLineY2(y2, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelContrastMethod(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String contrastMethod;
        contrastMethod = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelDetector(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String detector;
        detector = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelDetector(detector, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelEmWave(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int emWave;
        emWave = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelExWave(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int exWave;
        exWave = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelFilterSet(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String filterSet;
        filterSet = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelFilterSet(filterSet, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelFluor(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fluor;
        fluor = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelID(id, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelIlluminationType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String illuminationType;
        illuminationType = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelLightSource(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lightSource;
        lightSource = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelLightSource(lightSource, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelMode(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String mode;
        mode = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelMode(mode, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelName(name, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelNdFilter(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float ndFilter;
        ndFilter = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelOTF(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String otf;
        otf = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelPhotometricInterpretation(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String photometricInterpretation;
        photometricInterpretation = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelPinholeSize(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float pinholeSize;
        pinholeSize = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelPockelCellSetting(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int pockelCellSetting;
        pockelCellSetting = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelSamplesPerPixel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int samplesPerPixel;
        samplesPerPixel = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelSecondaryEmissionFilter(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String secondaryEmissionFilter;
        secondaryEmissionFilter = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelSecondaryEmissionFilter(secondaryEmissionFilter, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setLogicalChannelSecondaryExcitationFilter(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String secondaryExcitationFilter;
        secondaryExcitationFilter = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int logicalChannelIndex;
        logicalChannelIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setLogicalChannelSecondaryExcitationFilter(secondaryExcitationFilter, imageIndex, logicalChannelIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskHeight(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String height;
        height = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskHeight(height, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskWidth(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String width;
        width = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskWidth(width, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String x;
        x = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskX(x, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String y;
        y = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskY(y, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskPixelsBigEndian(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean bigEndian;
        bigEndian = __is.readBool();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskPixelsBigEndian(bigEndian, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskPixelsBinData(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String binData;
        binData = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskPixelsBinData(binData, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskPixelsExtendedPixelType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String extendedPixelType;
        extendedPixelType = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskPixelsExtendedPixelType(extendedPixelType, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskPixelsID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskPixelsID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskPixelsSizeX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeX;
        sizeX = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskPixelsSizeX(sizeX, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMaskPixelsSizeY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeY;
        sizeY = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMaskPixelsSizeY(sizeY, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicrobeamManipulationExperimenterRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimenterRef;
        experimenterRef = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicrobeamManipulationExperimenterRef(experimenterRef, imageIndex, microbeamManipulationIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicrobeamManipulationID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicrobeamManipulationID(id, imageIndex, microbeamManipulationIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicrobeamManipulationType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicrobeamManipulationType(type, imageIndex, microbeamManipulationIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicrobeamManipulationRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int experimentIndex;
        experimentIndex = __is.readInt();
        int microbeamManipulationRefIndex;
        microbeamManipulationRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicrobeamManipulationRefID(id, experimentIndex, microbeamManipulationRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicroscopeID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicroscopeID(id, instrumentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicroscopeManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicroscopeManufacturer(manufacturer, instrumentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicroscopeModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicroscopeModel(model, instrumentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicroscopeSerialNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String serialNumber;
        serialNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicroscopeSerialNumber(serialNumber, instrumentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMicroscopeType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setMicroscopeType(type, instrumentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFBinaryFile(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String binaryFile;
        binaryFile = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFBinaryFile(binaryFile, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFID(id, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFObjective(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String objective;
        objective = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFObjective(objective, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFOpticalAxisAveraged(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean opticalAxisAveraged;
        opticalAxisAveraged = __is.readBool();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFPixelType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String pixelType;
        pixelType = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFPixelType(pixelType, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFSizeX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeX;
        sizeX = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFSizeX(sizeX, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setOTFSizeY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeY;
        sizeY = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int otfIndex;
        otfIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setOTFSizeY(sizeY, instrumentIndex, otfIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveCalibratedMagnification(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float calibratedMagnification;
        calibratedMagnification = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveCorrection(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String correction;
        correction = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveCorrection(correction, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveID(id, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveImmersion(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String immersion;
        immersion = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveIris(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean iris;
        iris = __is.readBool();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveIris(iris, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveLensNA(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float lensNA;
        lensNA = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveManufacturer(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String manufacturer;
        manufacturer = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveModel(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String model;
        model = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveModel(model, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveNominalMagnification(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int nominalMagnification;
        nominalMagnification = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveSerialNumber(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String serialNumber;
        serialNumber = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveWorkingDistance(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float workingDistance;
        workingDistance = __is.readFloat();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int objectiveIndex;
        objectiveIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveSettingsCorrectionCollar(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float correctionCollar;
        correctionCollar = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveSettingsMedium(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String medium;
        medium = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveSettingsMedium(medium, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveSettingsObjective(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String objective;
        objective = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveSettingsObjective(objective, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setObjectiveSettingsRefractiveIndex(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float refractiveIndex;
        refractiveIndex = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPathD(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String d;
        d = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPathD(d, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPathID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPathID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsBigEndian(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean bigEndian;
        bigEndian = __is.readBool();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsDimensionOrder(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String dimensionOrder;
        dimensionOrder = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsID(id, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsPixelType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String pixelType;
        pixelType = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsPixelType(pixelType, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsSizeC(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeC;
        sizeC = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsSizeC(sizeC, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsSizeT(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeT;
        sizeT = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsSizeT(sizeT, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsSizeX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeX;
        sizeX = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsSizeX(sizeX, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsSizeY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeY;
        sizeY = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsSizeY(sizeY, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPixelsSizeZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sizeZ;
        sizeZ = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneHashSHA1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String hashSHA1;
        hashSHA1 = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneHashSHA1(hashSHA1, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneID(id, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneTheC(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int theC;
        theC = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneTheT(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int theT;
        theT = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneTheZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int theZ;
        theZ = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneTimingDeltaT(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float deltaT;
        deltaT = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlaneTimingExposureTime(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float exposureTime;
        exposureTime = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateColumnNamingConvention(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String columnNamingConvention;
        columnNamingConvention = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateColumnNamingConvention(columnNamingConvention, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateDescription(description, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateExternalIdentifier(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String externalIdentifier;
        externalIdentifier = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateExternalIdentifier(externalIdentifier, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateID(id, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateName(name, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateRowNamingConvention(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String rowNamingConvention;
        rowNamingConvention = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateRowNamingConvention(rowNamingConvention, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateStatus(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String status;
        status = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateStatus(status, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateWellOriginX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        double wellOriginX;
        wellOriginX = __is.readDouble();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateWellOriginX(wellOriginX, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateWellOriginY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        double wellOriginY;
        wellOriginY = __is.readDouble();
        int plateIndex;
        plateIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateWellOriginY(wellOriginY, plateIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int plateRefIndex;
        plateRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateRefID(id, screenIndex, plateRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateRefSample(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sample;
        sample = __is.readInt();
        int screenIndex;
        screenIndex = __is.readInt();
        int plateRefIndex;
        plateRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateRefSample(sample, screenIndex, plateRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPlateRefWell(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String well;
        well = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int plateRefIndex;
        plateRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPlateRefWell(well, screenIndex, plateRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPointCx(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String cx;
        cx = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPointCx(cx, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPointCy(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String cy;
        cy = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPointCy(cy, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPointID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPointID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPointR(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String r;
        r = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPointR(r, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPointTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPointTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPolygonID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPolygonID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPolygonPoints(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String points;
        points = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPolygonPoints(points, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPolygonTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPolygonTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPolylineID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPolylineID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPolylinePoints(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String points;
        points = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPolylinePoints(points, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPolylineTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPolylineTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setProjectDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setProjectDescription(description, projectIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setProjectExperimenterRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String experimenterRef;
        experimenterRef = __is.readString();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setProjectExperimenterRef(experimenterRef, projectIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setProjectGroupRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String groupRef;
        groupRef = __is.readString();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setProjectGroupRef(groupRef, projectIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setProjectID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setProjectID(id, projectIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setProjectName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int projectIndex;
        projectIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setProjectName(name, projectIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setProjectRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int datasetIndex;
        datasetIndex = __is.readInt();
        int projectRefIndex;
        projectRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setProjectRefID(id, datasetIndex, projectRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setPumpLightSource(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String lightSource;
        lightSource = __is.readString();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int lightSourceIndex;
        lightSourceIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setPumpLightSource(lightSource, instrumentIndex, lightSourceIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIID(id, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIT0(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int t0;
        t0 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIT0(t0, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIT1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int t1;
        t1 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIT1(t1, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIX0(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int x0;
        x0 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIX0(x0, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIX1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int x1;
        x1 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIX1(x1, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIY0(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int y0;
        y0 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIY0(y0, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIY1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int y1;
        y1 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIY1(y1, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIZ0(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int z0;
        z0 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIZ0(z0, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIZ1(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int z1;
        z1 = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIZ1(z1, imageIndex, roiIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setROIRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int microbeamManipulationIndex;
        microbeamManipulationIndex = __is.readInt();
        int roiRefIndex;
        roiRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setROIRefID(id, imageIndex, microbeamManipulationIndex, roiRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setReagentDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setReagentDescription(description, screenIndex, reagentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setReagentID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setReagentID(id, screenIndex, reagentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setReagentName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setReagentName(name, screenIndex, reagentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setReagentReagentIdentifier(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String reagentIdentifier;
        reagentIdentifier = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int reagentIndex;
        reagentIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRectHeight(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String height;
        height = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRectHeight(height, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRectID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRectID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRectTransform(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String transform;
        transform = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRectTransform(transform, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRectWidth(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String width;
        width = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRectWidth(width, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRectX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String x;
        x = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRectX(x, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRectY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String y;
        y = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRectY(y, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRegionID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int regionIndex;
        regionIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRegionID(id, imageIndex, regionIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRegionName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int regionIndex;
        regionIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRegionName(name, imageIndex, regionIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRegionTag(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String tag;
        tag = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int regionIndex;
        regionIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRegionTag(tag, imageIndex, regionIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRoiLinkDirection(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String direction;
        direction = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int roiLinkIndex;
        roiLinkIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRoiLinkDirection(direction, imageIndex, roiIndex, roiLinkIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRoiLinkName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int roiLinkIndex;
        roiLinkIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRoiLinkName(name, imageIndex, roiIndex, roiLinkIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setRoiLinkRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String ref;
        ref = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int roiLinkIndex;
        roiLinkIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setRoiLinkRef(ref, imageIndex, roiIndex, roiLinkIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String description;
        description = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenDescription(description, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenExtern(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String extern;
        extern = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenExtern(extern, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenID(id, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenName(name, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenProtocolDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String protocolDescription;
        protocolDescription = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenProtocolDescription(protocolDescription, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenProtocolIdentifier(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String protocolIdentifier;
        protocolIdentifier = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenProtocolIdentifier(protocolIdentifier, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenReagentSetDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String reagentSetDescription;
        reagentSetDescription = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenReagentSetDescription(reagentSetDescription, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenReagentSetIdentifier(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String reagentSetIdentifier;
        reagentSetIdentifier = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenReagentSetIdentifier(reagentSetIdentifier, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenType(type, screenIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenAcquisitionEndTime(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String endTime;
        endTime = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenAcquisitionID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenAcquisitionStartTime(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String startTime;
        startTime = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setScreenRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int screenRefIndex;
        screenRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setScreenRefID(id, plateIndex, screenRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeBaselineShift(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String baselineShift;
        baselineShift = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeBaselineShift(baselineShift, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeDirection(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String direction;
        direction = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeDirection(direction, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFillColor(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fillColor;
        fillColor = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFillColor(fillColor, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFillOpacity(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fillOpacity;
        fillOpacity = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFillOpacity(fillOpacity, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFillRule(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fillRule;
        fillRule = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFillRule(fillRule, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFontFamily(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fontFamily;
        fontFamily = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFontFamily(fontFamily, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFontSize(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int fontSize;
        fontSize = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFontSize(fontSize, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFontStretch(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fontStretch;
        fontStretch = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFontStretch(fontStretch, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFontStyle(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fontStyle;
        fontStyle = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFontStyle(fontStyle, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFontVariant(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fontVariant;
        fontVariant = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFontVariant(fontVariant, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeFontWeight(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fontWeight;
        fontWeight = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeFontWeight(fontWeight, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeG(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String g;
        g = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeG(g, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeGlyphOrientationVertical(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int glyphOrientationVertical;
        glyphOrientationVertical = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeGlyphOrientationVertical(glyphOrientationVertical, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeID(id, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeLocked(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean locked;
        locked = __is.readBool();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeLocked(locked, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeAttribute(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String strokeAttribute;
        strokeAttribute = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeAttribute(strokeAttribute, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeColor(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String strokeColor;
        strokeColor = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeColor(strokeColor, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeDashArray(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String strokeDashArray;
        strokeDashArray = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeDashArray(strokeDashArray, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeLineCap(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String strokeLineCap;
        strokeLineCap = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeLineCap(strokeLineCap, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeLineJoin(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String strokeLineJoin;
        strokeLineJoin = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeLineJoin(strokeLineJoin, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeMiterLimit(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int strokeMiterLimit;
        strokeMiterLimit = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeMiterLimit(strokeMiterLimit, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeOpacity(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float strokeOpacity;
        strokeOpacity = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeOpacity(strokeOpacity, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeStrokeWidth(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int strokeWidth;
        strokeWidth = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeStrokeWidth(strokeWidth, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeText(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String text;
        text = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeText(text, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeTextAnchor(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String textAnchor;
        textAnchor = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeTextAnchor(textAnchor, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeTextDecoration(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String textDecoration;
        textDecoration = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeTextDecoration(textDecoration, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeTextFill(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String textFill;
        textFill = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeTextFill(textFill, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeTextStroke(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String textStroke;
        textStroke = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeTextStroke(textStroke, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeTheT(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int theT;
        theT = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeTheT(theT, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeTheZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int theZ;
        theZ = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeTheZ(theZ, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeVectorEffect(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String vectorEffect;
        vectorEffect = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeVectorEffect(vectorEffect, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeVisibility(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        boolean visibility;
        visibility = __is.readBool();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeVisibility(visibility, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setShapeWritingMode(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String writingMode;
        writingMode = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int roiIndex;
        roiIndex = __is.readInt();
        int shapeIndex;
        shapeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setShapeWritingMode(writingMode, imageIndex, roiIndex, shapeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStageLabelName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStageLabelName(name, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStageLabelX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float x;
        x = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStageLabelX(x, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStageLabelY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float y;
        y = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStageLabelY(y, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStageLabelZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float z;
        z = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStageLabelZ(z, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStagePositionPositionX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float positionX;
        positionX = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStagePositionPositionY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float positionY;
        positionY = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setStagePositionPositionZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float positionZ;
        positionZ = __is.readFloat();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int planeIndex;
        planeIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setThumbnailHref(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String href;
        href = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setThumbnailHref(href, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setThumbnailID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setThumbnailID(id, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setThumbnailMIMEtype(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String mimEtype;
        mimEtype = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setThumbnailMIMEtype(mimEtype, imageIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataFileName(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String fileName;
        fileName = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataFirstC(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int firstC;
        firstC = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataFirstT(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int firstT;
        firstT = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataFirstZ(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int firstZ;
        firstZ = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataIFD(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int ifd;
        ifd = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataNumPlanes(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int numPlanes;
        numPlanes = __is.readInt();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTiffDataUUID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String uuid;
        uuid = __is.readString();
        int imageIndex;
        imageIndex = __is.readInt();
        int pixelsIndex;
        pixelsIndex = __is.readInt();
        int tiffDataIndex;
        tiffDataIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTransmittanceRangeCutIn(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int cutIn;
        cutIn = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTransmittanceRangeCutIn(cutIn, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTransmittanceRangeCutInTolerance(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int cutInTolerance;
        cutInTolerance = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTransmittanceRangeCutInTolerance(cutInTolerance, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTransmittanceRangeCutOut(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int cutOut;
        cutOut = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTransmittanceRangeCutOut(cutOut, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTransmittanceRangeCutOutTolerance(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int cutOutTolerance;
        cutOutTolerance = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTransmittanceRangeCutOutTolerance(cutOutTolerance, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setTransmittanceRangeTransmittance(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int transmittance;
        transmittance = __is.readInt();
        int instrumentIndex;
        instrumentIndex = __is.readInt();
        int filterIndex;
        filterIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellColumn(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int column;
        column = __is.readInt();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellColumn(column, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellExternalDescription(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String externalDescription;
        externalDescription = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellExternalDescription(externalDescription, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellExternalIdentifier(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String externalIdentifier;
        externalIdentifier = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellID(id, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellReagent(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String reagent;
        reagent = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellReagent(reagent, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellRow(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int row;
        row = __is.readInt();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellRow(row, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellType(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String type;
        type = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellType(type, plateIndex, wellIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSampleID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSampleImageRef(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String imageRef;
        imageRef = __is.readString();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSampleImageRef(imageRef, plateIndex, wellIndex, wellSampleIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSampleIndex(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int index;
        index = __is.readInt();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSamplePosX(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float posX;
        posX = __is.readFloat();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSamplePosY(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        float posY;
        posY = __is.readFloat();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSampleTimepoint(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int timepoint;
        timepoint = __is.readInt();
        int plateIndex;
        plateIndex = __is.readInt();
        int wellIndex;
        wellIndex = __is.readInt();
        int wellSampleIndex;
        wellSampleIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setWellSampleRefID(IMetadata __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        int screenIndex;
        screenIndex = __is.readInt();
        int screenAcquisitionIndex;
        screenAcquisitionIndex = __is.readInt();
        int wellSampleRefIndex;
        wellSampleRefIndex = __is.readInt();
        __is.endReadEncaps();
        __obj.setWellSampleRefID(id, screenIndex, screenAcquisitionIndex, wellSampleRefIndex, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "createRoot",
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
        "ice_ping",
        "setArcType",
        "setChannelComponentColorDomain",
        "setChannelComponentIndex",
        "setChannelComponentPixels",
        "setCircleCx",
        "setCircleCy",
        "setCircleID",
        "setCircleR",
        "setCircleTransform",
        "setContactExperimenter",
        "setDatasetDescription",
        "setDatasetExperimenterRef",
        "setDatasetGroupRef",
        "setDatasetID",
        "setDatasetLocked",
        "setDatasetName",
        "setDatasetRefID",
        "setDetectorAmplificationGain",
        "setDetectorGain",
        "setDetectorID",
        "setDetectorManufacturer",
        "setDetectorModel",
        "setDetectorOffset",
        "setDetectorSerialNumber",
        "setDetectorSettingsBinning",
        "setDetectorSettingsDetector",
        "setDetectorSettingsGain",
        "setDetectorSettingsOffset",
        "setDetectorSettingsReadOutRate",
        "setDetectorSettingsVoltage",
        "setDetectorType",
        "setDetectorVoltage",
        "setDetectorZoom",
        "setDichroicID",
        "setDichroicLotNumber",
        "setDichroicManufacturer",
        "setDichroicModel",
        "setDimensionsPhysicalSizeX",
        "setDimensionsPhysicalSizeY",
        "setDimensionsPhysicalSizeZ",
        "setDimensionsTimeIncrement",
        "setDimensionsWaveIncrement",
        "setDimensionsWaveStart",
        "setDisplayOptionsDisplay",
        "setDisplayOptionsID",
        "setDisplayOptionsZoom",
        "setEllipseCx",
        "setEllipseCy",
        "setEllipseID",
        "setEllipseRx",
        "setEllipseRy",
        "setEllipseTransform",
        "setEmFilterLotNumber",
        "setEmFilterManufacturer",
        "setEmFilterModel",
        "setEmFilterType",
        "setExFilterLotNumber",
        "setExFilterManufacturer",
        "setExFilterModel",
        "setExFilterType",
        "setExperimentDescription",
        "setExperimentExperimenterRef",
        "setExperimentID",
        "setExperimentType",
        "setExperimenterEmail",
        "setExperimenterFirstName",
        "setExperimenterID",
        "setExperimenterInstitution",
        "setExperimenterLastName",
        "setExperimenterMembershipGroup",
        "setExperimenterOMEName",
        "setFilamentType",
        "setFilterFilterWheel",
        "setFilterID",
        "setFilterLotNumber",
        "setFilterManufacturer",
        "setFilterModel",
        "setFilterSetDichroic",
        "setFilterSetEmFilter",
        "setFilterSetExFilter",
        "setFilterSetID",
        "setFilterSetLotNumber",
        "setFilterSetManufacturer",
        "setFilterSetModel",
        "setFilterType",
        "setGroupID",
        "setGroupName",
        "setImageAcquiredPixels",
        "setImageCreationDate",
        "setImageDefaultPixels",
        "setImageDescription",
        "setImageExperimentRef",
        "setImageExperimenterRef",
        "setImageGroupRef",
        "setImageID",
        "setImageInstrumentRef",
        "setImageName",
        "setImagingEnvironmentAirPressure",
        "setImagingEnvironmentCO2Percent",
        "setImagingEnvironmentHumidity",
        "setImagingEnvironmentTemperature",
        "setInstrumentID",
        "setLaserFrequencyMultiplication",
        "setLaserLaserMedium",
        "setLaserPockelCell",
        "setLaserPulse",
        "setLaserRepetitionRate",
        "setLaserTuneable",
        "setLaserType",
        "setLaserWavelength",
        "setLightSourceID",
        "setLightSourceManufacturer",
        "setLightSourceModel",
        "setLightSourcePower",
        "setLightSourceRefAttenuation",
        "setLightSourceRefLightSource",
        "setLightSourceRefWavelength",
        "setLightSourceSerialNumber",
        "setLightSourceSettingsAttenuation",
        "setLightSourceSettingsLightSource",
        "setLightSourceSettingsWavelength",
        "setLineID",
        "setLineTransform",
        "setLineX1",
        "setLineX2",
        "setLineY1",
        "setLineY2",
        "setLogicalChannelContrastMethod",
        "setLogicalChannelDetector",
        "setLogicalChannelEmWave",
        "setLogicalChannelExWave",
        "setLogicalChannelFilterSet",
        "setLogicalChannelFluor",
        "setLogicalChannelID",
        "setLogicalChannelIlluminationType",
        "setLogicalChannelLightSource",
        "setLogicalChannelMode",
        "setLogicalChannelName",
        "setLogicalChannelNdFilter",
        "setLogicalChannelOTF",
        "setLogicalChannelPhotometricInterpretation",
        "setLogicalChannelPinholeSize",
        "setLogicalChannelPockelCellSetting",
        "setLogicalChannelSamplesPerPixel",
        "setLogicalChannelSecondaryEmissionFilter",
        "setLogicalChannelSecondaryExcitationFilter",
        "setMaskHeight",
        "setMaskID",
        "setMaskPixelsBigEndian",
        "setMaskPixelsBinData",
        "setMaskPixelsExtendedPixelType",
        "setMaskPixelsID",
        "setMaskPixelsSizeX",
        "setMaskPixelsSizeY",
        "setMaskTransform",
        "setMaskWidth",
        "setMaskX",
        "setMaskY",
        "setMicrobeamManipulationExperimenterRef",
        "setMicrobeamManipulationID",
        "setMicrobeamManipulationRefID",
        "setMicrobeamManipulationType",
        "setMicroscopeID",
        "setMicroscopeManufacturer",
        "setMicroscopeModel",
        "setMicroscopeSerialNumber",
        "setMicroscopeType",
        "setOTFBinaryFile",
        "setOTFID",
        "setOTFObjective",
        "setOTFOpticalAxisAveraged",
        "setOTFPixelType",
        "setOTFSizeX",
        "setOTFSizeY",
        "setObjectiveCalibratedMagnification",
        "setObjectiveCorrection",
        "setObjectiveID",
        "setObjectiveImmersion",
        "setObjectiveIris",
        "setObjectiveLensNA",
        "setObjectiveManufacturer",
        "setObjectiveModel",
        "setObjectiveNominalMagnification",
        "setObjectiveSerialNumber",
        "setObjectiveSettingsCorrectionCollar",
        "setObjectiveSettingsMedium",
        "setObjectiveSettingsObjective",
        "setObjectiveSettingsRefractiveIndex",
        "setObjectiveWorkingDistance",
        "setPathD",
        "setPathID",
        "setPixelsBigEndian",
        "setPixelsDimensionOrder",
        "setPixelsID",
        "setPixelsPixelType",
        "setPixelsSizeC",
        "setPixelsSizeT",
        "setPixelsSizeX",
        "setPixelsSizeY",
        "setPixelsSizeZ",
        "setPlaneHashSHA1",
        "setPlaneID",
        "setPlaneTheC",
        "setPlaneTheT",
        "setPlaneTheZ",
        "setPlaneTimingDeltaT",
        "setPlaneTimingExposureTime",
        "setPlateColumnNamingConvention",
        "setPlateDescription",
        "setPlateExternalIdentifier",
        "setPlateID",
        "setPlateName",
        "setPlateRefID",
        "setPlateRefSample",
        "setPlateRefWell",
        "setPlateRowNamingConvention",
        "setPlateStatus",
        "setPlateWellOriginX",
        "setPlateWellOriginY",
        "setPointCx",
        "setPointCy",
        "setPointID",
        "setPointR",
        "setPointTransform",
        "setPolygonID",
        "setPolygonPoints",
        "setPolygonTransform",
        "setPolylineID",
        "setPolylinePoints",
        "setPolylineTransform",
        "setProjectDescription",
        "setProjectExperimenterRef",
        "setProjectGroupRef",
        "setProjectID",
        "setProjectName",
        "setProjectRefID",
        "setPumpLightSource",
        "setROIID",
        "setROIRefID",
        "setROIT0",
        "setROIT1",
        "setROIX0",
        "setROIX1",
        "setROIY0",
        "setROIY1",
        "setROIZ0",
        "setROIZ1",
        "setReagentDescription",
        "setReagentID",
        "setReagentName",
        "setReagentReagentIdentifier",
        "setRectHeight",
        "setRectID",
        "setRectTransform",
        "setRectWidth",
        "setRectX",
        "setRectY",
        "setRegionID",
        "setRegionName",
        "setRegionTag",
        "setRoiLinkDirection",
        "setRoiLinkName",
        "setRoiLinkRef",
        "setScreenAcquisitionEndTime",
        "setScreenAcquisitionID",
        "setScreenAcquisitionStartTime",
        "setScreenDescription",
        "setScreenExtern",
        "setScreenID",
        "setScreenName",
        "setScreenProtocolDescription",
        "setScreenProtocolIdentifier",
        "setScreenReagentSetDescription",
        "setScreenReagentSetIdentifier",
        "setScreenRefID",
        "setScreenType",
        "setShapeBaselineShift",
        "setShapeDirection",
        "setShapeFillColor",
        "setShapeFillOpacity",
        "setShapeFillRule",
        "setShapeFontFamily",
        "setShapeFontSize",
        "setShapeFontStretch",
        "setShapeFontStyle",
        "setShapeFontVariant",
        "setShapeFontWeight",
        "setShapeG",
        "setShapeGlyphOrientationVertical",
        "setShapeID",
        "setShapeLocked",
        "setShapeStrokeAttribute",
        "setShapeStrokeColor",
        "setShapeStrokeDashArray",
        "setShapeStrokeLineCap",
        "setShapeStrokeLineJoin",
        "setShapeStrokeMiterLimit",
        "setShapeStrokeOpacity",
        "setShapeStrokeWidth",
        "setShapeText",
        "setShapeTextAnchor",
        "setShapeTextDecoration",
        "setShapeTextFill",
        "setShapeTextStroke",
        "setShapeTheT",
        "setShapeTheZ",
        "setShapeVectorEffect",
        "setShapeVisibility",
        "setShapeWritingMode",
        "setStageLabelName",
        "setStageLabelX",
        "setStageLabelY",
        "setStageLabelZ",
        "setStagePositionPositionX",
        "setStagePositionPositionY",
        "setStagePositionPositionZ",
        "setThumbnailHref",
        "setThumbnailID",
        "setThumbnailMIMEtype",
        "setTiffDataFileName",
        "setTiffDataFirstC",
        "setTiffDataFirstT",
        "setTiffDataFirstZ",
        "setTiffDataIFD",
        "setTiffDataNumPlanes",
        "setTiffDataUUID",
        "setTransmittanceRangeCutIn",
        "setTransmittanceRangeCutInTolerance",
        "setTransmittanceRangeCutOut",
        "setTransmittanceRangeCutOutTolerance",
        "setTransmittanceRangeTransmittance",
        "setUUID",
        "setWellColumn",
        "setWellExternalDescription",
        "setWellExternalIdentifier",
        "setWellID",
        "setWellReagent",
        "setWellRow",
        "setWellSampleID",
        "setWellSampleImageRef",
        "setWellSampleIndex",
        "setWellSamplePosX",
        "setWellSamplePosY",
        "setWellSampleRefID",
        "setWellSampleTimepoint",
        "setWellType"
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
                return ___createRoot(this, in, __current);
            }
            case 1:
            {
                return ___getArcType(this, in, __current);
            }
            case 2:
            {
                return ___getChannelComponentColorDomain(this, in, __current);
            }
            case 3:
            {
                return ___getChannelComponentCount(this, in, __current);
            }
            case 4:
            {
                return ___getChannelComponentIndex(this, in, __current);
            }
            case 5:
            {
                return ___getChannelComponentPixels(this, in, __current);
            }
            case 6:
            {
                return ___getCircleCx(this, in, __current);
            }
            case 7:
            {
                return ___getCircleCy(this, in, __current);
            }
            case 8:
            {
                return ___getCircleID(this, in, __current);
            }
            case 9:
            {
                return ___getCircleR(this, in, __current);
            }
            case 10:
            {
                return ___getCircleTransform(this, in, __current);
            }
            case 11:
            {
                return ___getContactExperimenter(this, in, __current);
            }
            case 12:
            {
                return ___getDatasetCount(this, in, __current);
            }
            case 13:
            {
                return ___getDatasetDescription(this, in, __current);
            }
            case 14:
            {
                return ___getDatasetExperimenterRef(this, in, __current);
            }
            case 15:
            {
                return ___getDatasetGroupRef(this, in, __current);
            }
            case 16:
            {
                return ___getDatasetID(this, in, __current);
            }
            case 17:
            {
                return ___getDatasetLocked(this, in, __current);
            }
            case 18:
            {
                return ___getDatasetName(this, in, __current);
            }
            case 19:
            {
                return ___getDatasetRefCount(this, in, __current);
            }
            case 20:
            {
                return ___getDatasetRefID(this, in, __current);
            }
            case 21:
            {
                return ___getDetectorAmplificationGain(this, in, __current);
            }
            case 22:
            {
                return ___getDetectorCount(this, in, __current);
            }
            case 23:
            {
                return ___getDetectorGain(this, in, __current);
            }
            case 24:
            {
                return ___getDetectorID(this, in, __current);
            }
            case 25:
            {
                return ___getDetectorManufacturer(this, in, __current);
            }
            case 26:
            {
                return ___getDetectorModel(this, in, __current);
            }
            case 27:
            {
                return ___getDetectorOffset(this, in, __current);
            }
            case 28:
            {
                return ___getDetectorSerialNumber(this, in, __current);
            }
            case 29:
            {
                return ___getDetectorSettingsBinning(this, in, __current);
            }
            case 30:
            {
                return ___getDetectorSettingsDetector(this, in, __current);
            }
            case 31:
            {
                return ___getDetectorSettingsGain(this, in, __current);
            }
            case 32:
            {
                return ___getDetectorSettingsOffset(this, in, __current);
            }
            case 33:
            {
                return ___getDetectorSettingsReadOutRate(this, in, __current);
            }
            case 34:
            {
                return ___getDetectorSettingsVoltage(this, in, __current);
            }
            case 35:
            {
                return ___getDetectorType(this, in, __current);
            }
            case 36:
            {
                return ___getDetectorVoltage(this, in, __current);
            }
            case 37:
            {
                return ___getDetectorZoom(this, in, __current);
            }
            case 38:
            {
                return ___getDichroicCount(this, in, __current);
            }
            case 39:
            {
                return ___getDichroicID(this, in, __current);
            }
            case 40:
            {
                return ___getDichroicLotNumber(this, in, __current);
            }
            case 41:
            {
                return ___getDichroicManufacturer(this, in, __current);
            }
            case 42:
            {
                return ___getDichroicModel(this, in, __current);
            }
            case 43:
            {
                return ___getDimensionsPhysicalSizeX(this, in, __current);
            }
            case 44:
            {
                return ___getDimensionsPhysicalSizeY(this, in, __current);
            }
            case 45:
            {
                return ___getDimensionsPhysicalSizeZ(this, in, __current);
            }
            case 46:
            {
                return ___getDimensionsTimeIncrement(this, in, __current);
            }
            case 47:
            {
                return ___getDimensionsWaveIncrement(this, in, __current);
            }
            case 48:
            {
                return ___getDimensionsWaveStart(this, in, __current);
            }
            case 49:
            {
                return ___getDisplayOptionsDisplay(this, in, __current);
            }
            case 50:
            {
                return ___getDisplayOptionsID(this, in, __current);
            }
            case 51:
            {
                return ___getDisplayOptionsZoom(this, in, __current);
            }
            case 52:
            {
                return ___getEllipseCx(this, in, __current);
            }
            case 53:
            {
                return ___getEllipseCy(this, in, __current);
            }
            case 54:
            {
                return ___getEllipseID(this, in, __current);
            }
            case 55:
            {
                return ___getEllipseRx(this, in, __current);
            }
            case 56:
            {
                return ___getEllipseRy(this, in, __current);
            }
            case 57:
            {
                return ___getEllipseTransform(this, in, __current);
            }
            case 58:
            {
                return ___getEmFilterLotNumber(this, in, __current);
            }
            case 59:
            {
                return ___getEmFilterManufacturer(this, in, __current);
            }
            case 60:
            {
                return ___getEmFilterModel(this, in, __current);
            }
            case 61:
            {
                return ___getEmFilterType(this, in, __current);
            }
            case 62:
            {
                return ___getExFilterLotNumber(this, in, __current);
            }
            case 63:
            {
                return ___getExFilterManufacturer(this, in, __current);
            }
            case 64:
            {
                return ___getExFilterModel(this, in, __current);
            }
            case 65:
            {
                return ___getExFilterType(this, in, __current);
            }
            case 66:
            {
                return ___getExperimentCount(this, in, __current);
            }
            case 67:
            {
                return ___getExperimentDescription(this, in, __current);
            }
            case 68:
            {
                return ___getExperimentExperimenterRef(this, in, __current);
            }
            case 69:
            {
                return ___getExperimentID(this, in, __current);
            }
            case 70:
            {
                return ___getExperimentType(this, in, __current);
            }
            case 71:
            {
                return ___getExperimenterCount(this, in, __current);
            }
            case 72:
            {
                return ___getExperimenterEmail(this, in, __current);
            }
            case 73:
            {
                return ___getExperimenterFirstName(this, in, __current);
            }
            case 74:
            {
                return ___getExperimenterID(this, in, __current);
            }
            case 75:
            {
                return ___getExperimenterInstitution(this, in, __current);
            }
            case 76:
            {
                return ___getExperimenterLastName(this, in, __current);
            }
            case 77:
            {
                return ___getExperimenterMembershipCount(this, in, __current);
            }
            case 78:
            {
                return ___getExperimenterMembershipGroup(this, in, __current);
            }
            case 79:
            {
                return ___getExperimenterOMEName(this, in, __current);
            }
            case 80:
            {
                return ___getFilamentType(this, in, __current);
            }
            case 81:
            {
                return ___getFilterCount(this, in, __current);
            }
            case 82:
            {
                return ___getFilterFilterWheel(this, in, __current);
            }
            case 83:
            {
                return ___getFilterID(this, in, __current);
            }
            case 84:
            {
                return ___getFilterLotNumber(this, in, __current);
            }
            case 85:
            {
                return ___getFilterManufacturer(this, in, __current);
            }
            case 86:
            {
                return ___getFilterModel(this, in, __current);
            }
            case 87:
            {
                return ___getFilterSetCount(this, in, __current);
            }
            case 88:
            {
                return ___getFilterSetDichroic(this, in, __current);
            }
            case 89:
            {
                return ___getFilterSetEmFilter(this, in, __current);
            }
            case 90:
            {
                return ___getFilterSetExFilter(this, in, __current);
            }
            case 91:
            {
                return ___getFilterSetID(this, in, __current);
            }
            case 92:
            {
                return ___getFilterSetLotNumber(this, in, __current);
            }
            case 93:
            {
                return ___getFilterSetManufacturer(this, in, __current);
            }
            case 94:
            {
                return ___getFilterSetModel(this, in, __current);
            }
            case 95:
            {
                return ___getFilterType(this, in, __current);
            }
            case 96:
            {
                return ___getGroupCount(this, in, __current);
            }
            case 97:
            {
                return ___getGroupID(this, in, __current);
            }
            case 98:
            {
                return ___getGroupName(this, in, __current);
            }
            case 99:
            {
                return ___getGroupRefCount(this, in, __current);
            }
            case 100:
            {
                return ___getImageAcquiredPixels(this, in, __current);
            }
            case 101:
            {
                return ___getImageCount(this, in, __current);
            }
            case 102:
            {
                return ___getImageCreationDate(this, in, __current);
            }
            case 103:
            {
                return ___getImageDefaultPixels(this, in, __current);
            }
            case 104:
            {
                return ___getImageDescription(this, in, __current);
            }
            case 105:
            {
                return ___getImageExperimentRef(this, in, __current);
            }
            case 106:
            {
                return ___getImageExperimenterRef(this, in, __current);
            }
            case 107:
            {
                return ___getImageGroupRef(this, in, __current);
            }
            case 108:
            {
                return ___getImageID(this, in, __current);
            }
            case 109:
            {
                return ___getImageInstrumentRef(this, in, __current);
            }
            case 110:
            {
                return ___getImageName(this, in, __current);
            }
            case 111:
            {
                return ___getImagingEnvironmentAirPressure(this, in, __current);
            }
            case 112:
            {
                return ___getImagingEnvironmentCO2Percent(this, in, __current);
            }
            case 113:
            {
                return ___getImagingEnvironmentHumidity(this, in, __current);
            }
            case 114:
            {
                return ___getImagingEnvironmentTemperature(this, in, __current);
            }
            case 115:
            {
                return ___getInstrumentCount(this, in, __current);
            }
            case 116:
            {
                return ___getInstrumentID(this, in, __current);
            }
            case 117:
            {
                return ___getLaserFrequencyMultiplication(this, in, __current);
            }
            case 118:
            {
                return ___getLaserLaserMedium(this, in, __current);
            }
            case 119:
            {
                return ___getLaserPockelCell(this, in, __current);
            }
            case 120:
            {
                return ___getLaserPulse(this, in, __current);
            }
            case 121:
            {
                return ___getLaserRepetitionRate(this, in, __current);
            }
            case 122:
            {
                return ___getLaserTuneable(this, in, __current);
            }
            case 123:
            {
                return ___getLaserType(this, in, __current);
            }
            case 124:
            {
                return ___getLaserWavelength(this, in, __current);
            }
            case 125:
            {
                return ___getLightSourceCount(this, in, __current);
            }
            case 126:
            {
                return ___getLightSourceID(this, in, __current);
            }
            case 127:
            {
                return ___getLightSourceManufacturer(this, in, __current);
            }
            case 128:
            {
                return ___getLightSourceModel(this, in, __current);
            }
            case 129:
            {
                return ___getLightSourcePower(this, in, __current);
            }
            case 130:
            {
                return ___getLightSourceRefAttenuation(this, in, __current);
            }
            case 131:
            {
                return ___getLightSourceRefCount(this, in, __current);
            }
            case 132:
            {
                return ___getLightSourceRefLightSource(this, in, __current);
            }
            case 133:
            {
                return ___getLightSourceRefWavelength(this, in, __current);
            }
            case 134:
            {
                return ___getLightSourceSerialNumber(this, in, __current);
            }
            case 135:
            {
                return ___getLightSourceSettingsAttenuation(this, in, __current);
            }
            case 136:
            {
                return ___getLightSourceSettingsLightSource(this, in, __current);
            }
            case 137:
            {
                return ___getLightSourceSettingsWavelength(this, in, __current);
            }
            case 138:
            {
                return ___getLineID(this, in, __current);
            }
            case 139:
            {
                return ___getLineTransform(this, in, __current);
            }
            case 140:
            {
                return ___getLineX1(this, in, __current);
            }
            case 141:
            {
                return ___getLineX2(this, in, __current);
            }
            case 142:
            {
                return ___getLineY1(this, in, __current);
            }
            case 143:
            {
                return ___getLineY2(this, in, __current);
            }
            case 144:
            {
                return ___getLogicalChannelContrastMethod(this, in, __current);
            }
            case 145:
            {
                return ___getLogicalChannelCount(this, in, __current);
            }
            case 146:
            {
                return ___getLogicalChannelDetector(this, in, __current);
            }
            case 147:
            {
                return ___getLogicalChannelEmWave(this, in, __current);
            }
            case 148:
            {
                return ___getLogicalChannelExWave(this, in, __current);
            }
            case 149:
            {
                return ___getLogicalChannelFilterSet(this, in, __current);
            }
            case 150:
            {
                return ___getLogicalChannelFluor(this, in, __current);
            }
            case 151:
            {
                return ___getLogicalChannelID(this, in, __current);
            }
            case 152:
            {
                return ___getLogicalChannelIlluminationType(this, in, __current);
            }
            case 153:
            {
                return ___getLogicalChannelLightSource(this, in, __current);
            }
            case 154:
            {
                return ___getLogicalChannelMode(this, in, __current);
            }
            case 155:
            {
                return ___getLogicalChannelName(this, in, __current);
            }
            case 156:
            {
                return ___getLogicalChannelNdFilter(this, in, __current);
            }
            case 157:
            {
                return ___getLogicalChannelOTF(this, in, __current);
            }
            case 158:
            {
                return ___getLogicalChannelPhotometricInterpretation(this, in, __current);
            }
            case 159:
            {
                return ___getLogicalChannelPinholeSize(this, in, __current);
            }
            case 160:
            {
                return ___getLogicalChannelPockelCellSetting(this, in, __current);
            }
            case 161:
            {
                return ___getLogicalChannelSamplesPerPixel(this, in, __current);
            }
            case 162:
            {
                return ___getLogicalChannelSecondaryEmissionFilter(this, in, __current);
            }
            case 163:
            {
                return ___getLogicalChannelSecondaryExcitationFilter(this, in, __current);
            }
            case 164:
            {
                return ___getMaskHeight(this, in, __current);
            }
            case 165:
            {
                return ___getMaskID(this, in, __current);
            }
            case 166:
            {
                return ___getMaskPixelsBigEndian(this, in, __current);
            }
            case 167:
            {
                return ___getMaskPixelsBinData(this, in, __current);
            }
            case 168:
            {
                return ___getMaskPixelsExtendedPixelType(this, in, __current);
            }
            case 169:
            {
                return ___getMaskPixelsID(this, in, __current);
            }
            case 170:
            {
                return ___getMaskPixelsSizeX(this, in, __current);
            }
            case 171:
            {
                return ___getMaskPixelsSizeY(this, in, __current);
            }
            case 172:
            {
                return ___getMaskTransform(this, in, __current);
            }
            case 173:
            {
                return ___getMaskWidth(this, in, __current);
            }
            case 174:
            {
                return ___getMaskX(this, in, __current);
            }
            case 175:
            {
                return ___getMaskY(this, in, __current);
            }
            case 176:
            {
                return ___getMicrobeamManipulationCount(this, in, __current);
            }
            case 177:
            {
                return ___getMicrobeamManipulationExperimenterRef(this, in, __current);
            }
            case 178:
            {
                return ___getMicrobeamManipulationID(this, in, __current);
            }
            case 179:
            {
                return ___getMicrobeamManipulationRefCount(this, in, __current);
            }
            case 180:
            {
                return ___getMicrobeamManipulationRefID(this, in, __current);
            }
            case 181:
            {
                return ___getMicrobeamManipulationType(this, in, __current);
            }
            case 182:
            {
                return ___getMicroscopeID(this, in, __current);
            }
            case 183:
            {
                return ___getMicroscopeManufacturer(this, in, __current);
            }
            case 184:
            {
                return ___getMicroscopeModel(this, in, __current);
            }
            case 185:
            {
                return ___getMicroscopeSerialNumber(this, in, __current);
            }
            case 186:
            {
                return ___getMicroscopeType(this, in, __current);
            }
            case 187:
            {
                return ___getOMEXML(this, in, __current);
            }
            case 188:
            {
                return ___getOTFBinaryFile(this, in, __current);
            }
            case 189:
            {
                return ___getOTFCount(this, in, __current);
            }
            case 190:
            {
                return ___getOTFID(this, in, __current);
            }
            case 191:
            {
                return ___getOTFObjective(this, in, __current);
            }
            case 192:
            {
                return ___getOTFOpticalAxisAveraged(this, in, __current);
            }
            case 193:
            {
                return ___getOTFPixelType(this, in, __current);
            }
            case 194:
            {
                return ___getOTFSizeX(this, in, __current);
            }
            case 195:
            {
                return ___getOTFSizeY(this, in, __current);
            }
            case 196:
            {
                return ___getObjectiveCalibratedMagnification(this, in, __current);
            }
            case 197:
            {
                return ___getObjectiveCorrection(this, in, __current);
            }
            case 198:
            {
                return ___getObjectiveCount(this, in, __current);
            }
            case 199:
            {
                return ___getObjectiveID(this, in, __current);
            }
            case 200:
            {
                return ___getObjectiveImmersion(this, in, __current);
            }
            case 201:
            {
                return ___getObjectiveIris(this, in, __current);
            }
            case 202:
            {
                return ___getObjectiveLensNA(this, in, __current);
            }
            case 203:
            {
                return ___getObjectiveManufacturer(this, in, __current);
            }
            case 204:
            {
                return ___getObjectiveModel(this, in, __current);
            }
            case 205:
            {
                return ___getObjectiveNominalMagnification(this, in, __current);
            }
            case 206:
            {
                return ___getObjectiveSerialNumber(this, in, __current);
            }
            case 207:
            {
                return ___getObjectiveSettingsCorrectionCollar(this, in, __current);
            }
            case 208:
            {
                return ___getObjectiveSettingsMedium(this, in, __current);
            }
            case 209:
            {
                return ___getObjectiveSettingsObjective(this, in, __current);
            }
            case 210:
            {
                return ___getObjectiveSettingsRefractiveIndex(this, in, __current);
            }
            case 211:
            {
                return ___getObjectiveWorkingDistance(this, in, __current);
            }
            case 212:
            {
                return ___getPathD(this, in, __current);
            }
            case 213:
            {
                return ___getPathID(this, in, __current);
            }
            case 214:
            {
                return ___getPixelsBigEndian(this, in, __current);
            }
            case 215:
            {
                return ___getPixelsCount(this, in, __current);
            }
            case 216:
            {
                return ___getPixelsDimensionOrder(this, in, __current);
            }
            case 217:
            {
                return ___getPixelsID(this, in, __current);
            }
            case 218:
            {
                return ___getPixelsPixelType(this, in, __current);
            }
            case 219:
            {
                return ___getPixelsSizeC(this, in, __current);
            }
            case 220:
            {
                return ___getPixelsSizeT(this, in, __current);
            }
            case 221:
            {
                return ___getPixelsSizeX(this, in, __current);
            }
            case 222:
            {
                return ___getPixelsSizeY(this, in, __current);
            }
            case 223:
            {
                return ___getPixelsSizeZ(this, in, __current);
            }
            case 224:
            {
                return ___getPlaneCount(this, in, __current);
            }
            case 225:
            {
                return ___getPlaneHashSHA1(this, in, __current);
            }
            case 226:
            {
                return ___getPlaneID(this, in, __current);
            }
            case 227:
            {
                return ___getPlaneTheC(this, in, __current);
            }
            case 228:
            {
                return ___getPlaneTheT(this, in, __current);
            }
            case 229:
            {
                return ___getPlaneTheZ(this, in, __current);
            }
            case 230:
            {
                return ___getPlaneTimingDeltaT(this, in, __current);
            }
            case 231:
            {
                return ___getPlaneTimingExposureTime(this, in, __current);
            }
            case 232:
            {
                return ___getPlateColumnNamingConvention(this, in, __current);
            }
            case 233:
            {
                return ___getPlateCount(this, in, __current);
            }
            case 234:
            {
                return ___getPlateDescription(this, in, __current);
            }
            case 235:
            {
                return ___getPlateExternalIdentifier(this, in, __current);
            }
            case 236:
            {
                return ___getPlateID(this, in, __current);
            }
            case 237:
            {
                return ___getPlateName(this, in, __current);
            }
            case 238:
            {
                return ___getPlateRefCount(this, in, __current);
            }
            case 239:
            {
                return ___getPlateRefID(this, in, __current);
            }
            case 240:
            {
                return ___getPlateRefSample(this, in, __current);
            }
            case 241:
            {
                return ___getPlateRefWell(this, in, __current);
            }
            case 242:
            {
                return ___getPlateRowNamingConvention(this, in, __current);
            }
            case 243:
            {
                return ___getPlateStatus(this, in, __current);
            }
            case 244:
            {
                return ___getPlateWellOriginX(this, in, __current);
            }
            case 245:
            {
                return ___getPlateWellOriginY(this, in, __current);
            }
            case 246:
            {
                return ___getPointCx(this, in, __current);
            }
            case 247:
            {
                return ___getPointCy(this, in, __current);
            }
            case 248:
            {
                return ___getPointID(this, in, __current);
            }
            case 249:
            {
                return ___getPointR(this, in, __current);
            }
            case 250:
            {
                return ___getPointTransform(this, in, __current);
            }
            case 251:
            {
                return ___getPolygonID(this, in, __current);
            }
            case 252:
            {
                return ___getPolygonPoints(this, in, __current);
            }
            case 253:
            {
                return ___getPolygonTransform(this, in, __current);
            }
            case 254:
            {
                return ___getPolylineID(this, in, __current);
            }
            case 255:
            {
                return ___getPolylinePoints(this, in, __current);
            }
            case 256:
            {
                return ___getPolylineTransform(this, in, __current);
            }
            case 257:
            {
                return ___getProjectCount(this, in, __current);
            }
            case 258:
            {
                return ___getProjectDescription(this, in, __current);
            }
            case 259:
            {
                return ___getProjectExperimenterRef(this, in, __current);
            }
            case 260:
            {
                return ___getProjectGroupRef(this, in, __current);
            }
            case 261:
            {
                return ___getProjectID(this, in, __current);
            }
            case 262:
            {
                return ___getProjectName(this, in, __current);
            }
            case 263:
            {
                return ___getProjectRefCount(this, in, __current);
            }
            case 264:
            {
                return ___getProjectRefID(this, in, __current);
            }
            case 265:
            {
                return ___getPumpLightSource(this, in, __current);
            }
            case 266:
            {
                return ___getROICount(this, in, __current);
            }
            case 267:
            {
                return ___getROIID(this, in, __current);
            }
            case 268:
            {
                return ___getROIRefCount(this, in, __current);
            }
            case 269:
            {
                return ___getROIRefID(this, in, __current);
            }
            case 270:
            {
                return ___getROIT0(this, in, __current);
            }
            case 271:
            {
                return ___getROIT1(this, in, __current);
            }
            case 272:
            {
                return ___getROIX0(this, in, __current);
            }
            case 273:
            {
                return ___getROIX1(this, in, __current);
            }
            case 274:
            {
                return ___getROIY0(this, in, __current);
            }
            case 275:
            {
                return ___getROIY1(this, in, __current);
            }
            case 276:
            {
                return ___getROIZ0(this, in, __current);
            }
            case 277:
            {
                return ___getROIZ1(this, in, __current);
            }
            case 278:
            {
                return ___getReagentCount(this, in, __current);
            }
            case 279:
            {
                return ___getReagentDescription(this, in, __current);
            }
            case 280:
            {
                return ___getReagentID(this, in, __current);
            }
            case 281:
            {
                return ___getReagentName(this, in, __current);
            }
            case 282:
            {
                return ___getReagentReagentIdentifier(this, in, __current);
            }
            case 283:
            {
                return ___getRectHeight(this, in, __current);
            }
            case 284:
            {
                return ___getRectID(this, in, __current);
            }
            case 285:
            {
                return ___getRectTransform(this, in, __current);
            }
            case 286:
            {
                return ___getRectWidth(this, in, __current);
            }
            case 287:
            {
                return ___getRectX(this, in, __current);
            }
            case 288:
            {
                return ___getRectY(this, in, __current);
            }
            case 289:
            {
                return ___getRegionCount(this, in, __current);
            }
            case 290:
            {
                return ___getRegionID(this, in, __current);
            }
            case 291:
            {
                return ___getRegionName(this, in, __current);
            }
            case 292:
            {
                return ___getRegionTag(this, in, __current);
            }
            case 293:
            {
                return ___getRoiLinkCount(this, in, __current);
            }
            case 294:
            {
                return ___getRoiLinkDirection(this, in, __current);
            }
            case 295:
            {
                return ___getRoiLinkName(this, in, __current);
            }
            case 296:
            {
                return ___getRoiLinkRef(this, in, __current);
            }
            case 297:
            {
                return ___getScreenAcquisitionCount(this, in, __current);
            }
            case 298:
            {
                return ___getScreenAcquisitionEndTime(this, in, __current);
            }
            case 299:
            {
                return ___getScreenAcquisitionID(this, in, __current);
            }
            case 300:
            {
                return ___getScreenAcquisitionStartTime(this, in, __current);
            }
            case 301:
            {
                return ___getScreenCount(this, in, __current);
            }
            case 302:
            {
                return ___getScreenDescription(this, in, __current);
            }
            case 303:
            {
                return ___getScreenExtern(this, in, __current);
            }
            case 304:
            {
                return ___getScreenID(this, in, __current);
            }
            case 305:
            {
                return ___getScreenName(this, in, __current);
            }
            case 306:
            {
                return ___getScreenProtocolDescription(this, in, __current);
            }
            case 307:
            {
                return ___getScreenProtocolIdentifier(this, in, __current);
            }
            case 308:
            {
                return ___getScreenReagentSetDescription(this, in, __current);
            }
            case 309:
            {
                return ___getScreenReagentSetIdentifier(this, in, __current);
            }
            case 310:
            {
                return ___getScreenRefCount(this, in, __current);
            }
            case 311:
            {
                return ___getScreenRefID(this, in, __current);
            }
            case 312:
            {
                return ___getScreenType(this, in, __current);
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
            case 392:
            {
                return ___setArcType(this, in, __current);
            }
            case 393:
            {
                return ___setChannelComponentColorDomain(this, in, __current);
            }
            case 394:
            {
                return ___setChannelComponentIndex(this, in, __current);
            }
            case 395:
            {
                return ___setChannelComponentPixels(this, in, __current);
            }
            case 396:
            {
                return ___setCircleCx(this, in, __current);
            }
            case 397:
            {
                return ___setCircleCy(this, in, __current);
            }
            case 398:
            {
                return ___setCircleID(this, in, __current);
            }
            case 399:
            {
                return ___setCircleR(this, in, __current);
            }
            case 400:
            {
                return ___setCircleTransform(this, in, __current);
            }
            case 401:
            {
                return ___setContactExperimenter(this, in, __current);
            }
            case 402:
            {
                return ___setDatasetDescription(this, in, __current);
            }
            case 403:
            {
                return ___setDatasetExperimenterRef(this, in, __current);
            }
            case 404:
            {
                return ___setDatasetGroupRef(this, in, __current);
            }
            case 405:
            {
                return ___setDatasetID(this, in, __current);
            }
            case 406:
            {
                return ___setDatasetLocked(this, in, __current);
            }
            case 407:
            {
                return ___setDatasetName(this, in, __current);
            }
            case 408:
            {
                return ___setDatasetRefID(this, in, __current);
            }
            case 409:
            {
                return ___setDetectorAmplificationGain(this, in, __current);
            }
            case 410:
            {
                return ___setDetectorGain(this, in, __current);
            }
            case 411:
            {
                return ___setDetectorID(this, in, __current);
            }
            case 412:
            {
                return ___setDetectorManufacturer(this, in, __current);
            }
            case 413:
            {
                return ___setDetectorModel(this, in, __current);
            }
            case 414:
            {
                return ___setDetectorOffset(this, in, __current);
            }
            case 415:
            {
                return ___setDetectorSerialNumber(this, in, __current);
            }
            case 416:
            {
                return ___setDetectorSettingsBinning(this, in, __current);
            }
            case 417:
            {
                return ___setDetectorSettingsDetector(this, in, __current);
            }
            case 418:
            {
                return ___setDetectorSettingsGain(this, in, __current);
            }
            case 419:
            {
                return ___setDetectorSettingsOffset(this, in, __current);
            }
            case 420:
            {
                return ___setDetectorSettingsReadOutRate(this, in, __current);
            }
            case 421:
            {
                return ___setDetectorSettingsVoltage(this, in, __current);
            }
            case 422:
            {
                return ___setDetectorType(this, in, __current);
            }
            case 423:
            {
                return ___setDetectorVoltage(this, in, __current);
            }
            case 424:
            {
                return ___setDetectorZoom(this, in, __current);
            }
            case 425:
            {
                return ___setDichroicID(this, in, __current);
            }
            case 426:
            {
                return ___setDichroicLotNumber(this, in, __current);
            }
            case 427:
            {
                return ___setDichroicManufacturer(this, in, __current);
            }
            case 428:
            {
                return ___setDichroicModel(this, in, __current);
            }
            case 429:
            {
                return ___setDimensionsPhysicalSizeX(this, in, __current);
            }
            case 430:
            {
                return ___setDimensionsPhysicalSizeY(this, in, __current);
            }
            case 431:
            {
                return ___setDimensionsPhysicalSizeZ(this, in, __current);
            }
            case 432:
            {
                return ___setDimensionsTimeIncrement(this, in, __current);
            }
            case 433:
            {
                return ___setDimensionsWaveIncrement(this, in, __current);
            }
            case 434:
            {
                return ___setDimensionsWaveStart(this, in, __current);
            }
            case 435:
            {
                return ___setDisplayOptionsDisplay(this, in, __current);
            }
            case 436:
            {
                return ___setDisplayOptionsID(this, in, __current);
            }
            case 437:
            {
                return ___setDisplayOptionsZoom(this, in, __current);
            }
            case 438:
            {
                return ___setEllipseCx(this, in, __current);
            }
            case 439:
            {
                return ___setEllipseCy(this, in, __current);
            }
            case 440:
            {
                return ___setEllipseID(this, in, __current);
            }
            case 441:
            {
                return ___setEllipseRx(this, in, __current);
            }
            case 442:
            {
                return ___setEllipseRy(this, in, __current);
            }
            case 443:
            {
                return ___setEllipseTransform(this, in, __current);
            }
            case 444:
            {
                return ___setEmFilterLotNumber(this, in, __current);
            }
            case 445:
            {
                return ___setEmFilterManufacturer(this, in, __current);
            }
            case 446:
            {
                return ___setEmFilterModel(this, in, __current);
            }
            case 447:
            {
                return ___setEmFilterType(this, in, __current);
            }
            case 448:
            {
                return ___setExFilterLotNumber(this, in, __current);
            }
            case 449:
            {
                return ___setExFilterManufacturer(this, in, __current);
            }
            case 450:
            {
                return ___setExFilterModel(this, in, __current);
            }
            case 451:
            {
                return ___setExFilterType(this, in, __current);
            }
            case 452:
            {
                return ___setExperimentDescription(this, in, __current);
            }
            case 453:
            {
                return ___setExperimentExperimenterRef(this, in, __current);
            }
            case 454:
            {
                return ___setExperimentID(this, in, __current);
            }
            case 455:
            {
                return ___setExperimentType(this, in, __current);
            }
            case 456:
            {
                return ___setExperimenterEmail(this, in, __current);
            }
            case 457:
            {
                return ___setExperimenterFirstName(this, in, __current);
            }
            case 458:
            {
                return ___setExperimenterID(this, in, __current);
            }
            case 459:
            {
                return ___setExperimenterInstitution(this, in, __current);
            }
            case 460:
            {
                return ___setExperimenterLastName(this, in, __current);
            }
            case 461:
            {
                return ___setExperimenterMembershipGroup(this, in, __current);
            }
            case 462:
            {
                return ___setExperimenterOMEName(this, in, __current);
            }
            case 463:
            {
                return ___setFilamentType(this, in, __current);
            }
            case 464:
            {
                return ___setFilterFilterWheel(this, in, __current);
            }
            case 465:
            {
                return ___setFilterID(this, in, __current);
            }
            case 466:
            {
                return ___setFilterLotNumber(this, in, __current);
            }
            case 467:
            {
                return ___setFilterManufacturer(this, in, __current);
            }
            case 468:
            {
                return ___setFilterModel(this, in, __current);
            }
            case 469:
            {
                return ___setFilterSetDichroic(this, in, __current);
            }
            case 470:
            {
                return ___setFilterSetEmFilter(this, in, __current);
            }
            case 471:
            {
                return ___setFilterSetExFilter(this, in, __current);
            }
            case 472:
            {
                return ___setFilterSetID(this, in, __current);
            }
            case 473:
            {
                return ___setFilterSetLotNumber(this, in, __current);
            }
            case 474:
            {
                return ___setFilterSetManufacturer(this, in, __current);
            }
            case 475:
            {
                return ___setFilterSetModel(this, in, __current);
            }
            case 476:
            {
                return ___setFilterType(this, in, __current);
            }
            case 477:
            {
                return ___setGroupID(this, in, __current);
            }
            case 478:
            {
                return ___setGroupName(this, in, __current);
            }
            case 479:
            {
                return ___setImageAcquiredPixels(this, in, __current);
            }
            case 480:
            {
                return ___setImageCreationDate(this, in, __current);
            }
            case 481:
            {
                return ___setImageDefaultPixels(this, in, __current);
            }
            case 482:
            {
                return ___setImageDescription(this, in, __current);
            }
            case 483:
            {
                return ___setImageExperimentRef(this, in, __current);
            }
            case 484:
            {
                return ___setImageExperimenterRef(this, in, __current);
            }
            case 485:
            {
                return ___setImageGroupRef(this, in, __current);
            }
            case 486:
            {
                return ___setImageID(this, in, __current);
            }
            case 487:
            {
                return ___setImageInstrumentRef(this, in, __current);
            }
            case 488:
            {
                return ___setImageName(this, in, __current);
            }
            case 489:
            {
                return ___setImagingEnvironmentAirPressure(this, in, __current);
            }
            case 490:
            {
                return ___setImagingEnvironmentCO2Percent(this, in, __current);
            }
            case 491:
            {
                return ___setImagingEnvironmentHumidity(this, in, __current);
            }
            case 492:
            {
                return ___setImagingEnvironmentTemperature(this, in, __current);
            }
            case 493:
            {
                return ___setInstrumentID(this, in, __current);
            }
            case 494:
            {
                return ___setLaserFrequencyMultiplication(this, in, __current);
            }
            case 495:
            {
                return ___setLaserLaserMedium(this, in, __current);
            }
            case 496:
            {
                return ___setLaserPockelCell(this, in, __current);
            }
            case 497:
            {
                return ___setLaserPulse(this, in, __current);
            }
            case 498:
            {
                return ___setLaserRepetitionRate(this, in, __current);
            }
            case 499:
            {
                return ___setLaserTuneable(this, in, __current);
            }
            case 500:
            {
                return ___setLaserType(this, in, __current);
            }
            case 501:
            {
                return ___setLaserWavelength(this, in, __current);
            }
            case 502:
            {
                return ___setLightSourceID(this, in, __current);
            }
            case 503:
            {
                return ___setLightSourceManufacturer(this, in, __current);
            }
            case 504:
            {
                return ___setLightSourceModel(this, in, __current);
            }
            case 505:
            {
                return ___setLightSourcePower(this, in, __current);
            }
            case 506:
            {
                return ___setLightSourceRefAttenuation(this, in, __current);
            }
            case 507:
            {
                return ___setLightSourceRefLightSource(this, in, __current);
            }
            case 508:
            {
                return ___setLightSourceRefWavelength(this, in, __current);
            }
            case 509:
            {
                return ___setLightSourceSerialNumber(this, in, __current);
            }
            case 510:
            {
                return ___setLightSourceSettingsAttenuation(this, in, __current);
            }
            case 511:
            {
                return ___setLightSourceSettingsLightSource(this, in, __current);
            }
            case 512:
            {
                return ___setLightSourceSettingsWavelength(this, in, __current);
            }
            case 513:
            {
                return ___setLineID(this, in, __current);
            }
            case 514:
            {
                return ___setLineTransform(this, in, __current);
            }
            case 515:
            {
                return ___setLineX1(this, in, __current);
            }
            case 516:
            {
                return ___setLineX2(this, in, __current);
            }
            case 517:
            {
                return ___setLineY1(this, in, __current);
            }
            case 518:
            {
                return ___setLineY2(this, in, __current);
            }
            case 519:
            {
                return ___setLogicalChannelContrastMethod(this, in, __current);
            }
            case 520:
            {
                return ___setLogicalChannelDetector(this, in, __current);
            }
            case 521:
            {
                return ___setLogicalChannelEmWave(this, in, __current);
            }
            case 522:
            {
                return ___setLogicalChannelExWave(this, in, __current);
            }
            case 523:
            {
                return ___setLogicalChannelFilterSet(this, in, __current);
            }
            case 524:
            {
                return ___setLogicalChannelFluor(this, in, __current);
            }
            case 525:
            {
                return ___setLogicalChannelID(this, in, __current);
            }
            case 526:
            {
                return ___setLogicalChannelIlluminationType(this, in, __current);
            }
            case 527:
            {
                return ___setLogicalChannelLightSource(this, in, __current);
            }
            case 528:
            {
                return ___setLogicalChannelMode(this, in, __current);
            }
            case 529:
            {
                return ___setLogicalChannelName(this, in, __current);
            }
            case 530:
            {
                return ___setLogicalChannelNdFilter(this, in, __current);
            }
            case 531:
            {
                return ___setLogicalChannelOTF(this, in, __current);
            }
            case 532:
            {
                return ___setLogicalChannelPhotometricInterpretation(this, in, __current);
            }
            case 533:
            {
                return ___setLogicalChannelPinholeSize(this, in, __current);
            }
            case 534:
            {
                return ___setLogicalChannelPockelCellSetting(this, in, __current);
            }
            case 535:
            {
                return ___setLogicalChannelSamplesPerPixel(this, in, __current);
            }
            case 536:
            {
                return ___setLogicalChannelSecondaryEmissionFilter(this, in, __current);
            }
            case 537:
            {
                return ___setLogicalChannelSecondaryExcitationFilter(this, in, __current);
            }
            case 538:
            {
                return ___setMaskHeight(this, in, __current);
            }
            case 539:
            {
                return ___setMaskID(this, in, __current);
            }
            case 540:
            {
                return ___setMaskPixelsBigEndian(this, in, __current);
            }
            case 541:
            {
                return ___setMaskPixelsBinData(this, in, __current);
            }
            case 542:
            {
                return ___setMaskPixelsExtendedPixelType(this, in, __current);
            }
            case 543:
            {
                return ___setMaskPixelsID(this, in, __current);
            }
            case 544:
            {
                return ___setMaskPixelsSizeX(this, in, __current);
            }
            case 545:
            {
                return ___setMaskPixelsSizeY(this, in, __current);
            }
            case 546:
            {
                return ___setMaskTransform(this, in, __current);
            }
            case 547:
            {
                return ___setMaskWidth(this, in, __current);
            }
            case 548:
            {
                return ___setMaskX(this, in, __current);
            }
            case 549:
            {
                return ___setMaskY(this, in, __current);
            }
            case 550:
            {
                return ___setMicrobeamManipulationExperimenterRef(this, in, __current);
            }
            case 551:
            {
                return ___setMicrobeamManipulationID(this, in, __current);
            }
            case 552:
            {
                return ___setMicrobeamManipulationRefID(this, in, __current);
            }
            case 553:
            {
                return ___setMicrobeamManipulationType(this, in, __current);
            }
            case 554:
            {
                return ___setMicroscopeID(this, in, __current);
            }
            case 555:
            {
                return ___setMicroscopeManufacturer(this, in, __current);
            }
            case 556:
            {
                return ___setMicroscopeModel(this, in, __current);
            }
            case 557:
            {
                return ___setMicroscopeSerialNumber(this, in, __current);
            }
            case 558:
            {
                return ___setMicroscopeType(this, in, __current);
            }
            case 559:
            {
                return ___setOTFBinaryFile(this, in, __current);
            }
            case 560:
            {
                return ___setOTFID(this, in, __current);
            }
            case 561:
            {
                return ___setOTFObjective(this, in, __current);
            }
            case 562:
            {
                return ___setOTFOpticalAxisAveraged(this, in, __current);
            }
            case 563:
            {
                return ___setOTFPixelType(this, in, __current);
            }
            case 564:
            {
                return ___setOTFSizeX(this, in, __current);
            }
            case 565:
            {
                return ___setOTFSizeY(this, in, __current);
            }
            case 566:
            {
                return ___setObjectiveCalibratedMagnification(this, in, __current);
            }
            case 567:
            {
                return ___setObjectiveCorrection(this, in, __current);
            }
            case 568:
            {
                return ___setObjectiveID(this, in, __current);
            }
            case 569:
            {
                return ___setObjectiveImmersion(this, in, __current);
            }
            case 570:
            {
                return ___setObjectiveIris(this, in, __current);
            }
            case 571:
            {
                return ___setObjectiveLensNA(this, in, __current);
            }
            case 572:
            {
                return ___setObjectiveManufacturer(this, in, __current);
            }
            case 573:
            {
                return ___setObjectiveModel(this, in, __current);
            }
            case 574:
            {
                return ___setObjectiveNominalMagnification(this, in, __current);
            }
            case 575:
            {
                return ___setObjectiveSerialNumber(this, in, __current);
            }
            case 576:
            {
                return ___setObjectiveSettingsCorrectionCollar(this, in, __current);
            }
            case 577:
            {
                return ___setObjectiveSettingsMedium(this, in, __current);
            }
            case 578:
            {
                return ___setObjectiveSettingsObjective(this, in, __current);
            }
            case 579:
            {
                return ___setObjectiveSettingsRefractiveIndex(this, in, __current);
            }
            case 580:
            {
                return ___setObjectiveWorkingDistance(this, in, __current);
            }
            case 581:
            {
                return ___setPathD(this, in, __current);
            }
            case 582:
            {
                return ___setPathID(this, in, __current);
            }
            case 583:
            {
                return ___setPixelsBigEndian(this, in, __current);
            }
            case 584:
            {
                return ___setPixelsDimensionOrder(this, in, __current);
            }
            case 585:
            {
                return ___setPixelsID(this, in, __current);
            }
            case 586:
            {
                return ___setPixelsPixelType(this, in, __current);
            }
            case 587:
            {
                return ___setPixelsSizeC(this, in, __current);
            }
            case 588:
            {
                return ___setPixelsSizeT(this, in, __current);
            }
            case 589:
            {
                return ___setPixelsSizeX(this, in, __current);
            }
            case 590:
            {
                return ___setPixelsSizeY(this, in, __current);
            }
            case 591:
            {
                return ___setPixelsSizeZ(this, in, __current);
            }
            case 592:
            {
                return ___setPlaneHashSHA1(this, in, __current);
            }
            case 593:
            {
                return ___setPlaneID(this, in, __current);
            }
            case 594:
            {
                return ___setPlaneTheC(this, in, __current);
            }
            case 595:
            {
                return ___setPlaneTheT(this, in, __current);
            }
            case 596:
            {
                return ___setPlaneTheZ(this, in, __current);
            }
            case 597:
            {
                return ___setPlaneTimingDeltaT(this, in, __current);
            }
            case 598:
            {
                return ___setPlaneTimingExposureTime(this, in, __current);
            }
            case 599:
            {
                return ___setPlateColumnNamingConvention(this, in, __current);
            }
            case 600:
            {
                return ___setPlateDescription(this, in, __current);
            }
            case 601:
            {
                return ___setPlateExternalIdentifier(this, in, __current);
            }
            case 602:
            {
                return ___setPlateID(this, in, __current);
            }
            case 603:
            {
                return ___setPlateName(this, in, __current);
            }
            case 604:
            {
                return ___setPlateRefID(this, in, __current);
            }
            case 605:
            {
                return ___setPlateRefSample(this, in, __current);
            }
            case 606:
            {
                return ___setPlateRefWell(this, in, __current);
            }
            case 607:
            {
                return ___setPlateRowNamingConvention(this, in, __current);
            }
            case 608:
            {
                return ___setPlateStatus(this, in, __current);
            }
            case 609:
            {
                return ___setPlateWellOriginX(this, in, __current);
            }
            case 610:
            {
                return ___setPlateWellOriginY(this, in, __current);
            }
            case 611:
            {
                return ___setPointCx(this, in, __current);
            }
            case 612:
            {
                return ___setPointCy(this, in, __current);
            }
            case 613:
            {
                return ___setPointID(this, in, __current);
            }
            case 614:
            {
                return ___setPointR(this, in, __current);
            }
            case 615:
            {
                return ___setPointTransform(this, in, __current);
            }
            case 616:
            {
                return ___setPolygonID(this, in, __current);
            }
            case 617:
            {
                return ___setPolygonPoints(this, in, __current);
            }
            case 618:
            {
                return ___setPolygonTransform(this, in, __current);
            }
            case 619:
            {
                return ___setPolylineID(this, in, __current);
            }
            case 620:
            {
                return ___setPolylinePoints(this, in, __current);
            }
            case 621:
            {
                return ___setPolylineTransform(this, in, __current);
            }
            case 622:
            {
                return ___setProjectDescription(this, in, __current);
            }
            case 623:
            {
                return ___setProjectExperimenterRef(this, in, __current);
            }
            case 624:
            {
                return ___setProjectGroupRef(this, in, __current);
            }
            case 625:
            {
                return ___setProjectID(this, in, __current);
            }
            case 626:
            {
                return ___setProjectName(this, in, __current);
            }
            case 627:
            {
                return ___setProjectRefID(this, in, __current);
            }
            case 628:
            {
                return ___setPumpLightSource(this, in, __current);
            }
            case 629:
            {
                return ___setROIID(this, in, __current);
            }
            case 630:
            {
                return ___setROIRefID(this, in, __current);
            }
            case 631:
            {
                return ___setROIT0(this, in, __current);
            }
            case 632:
            {
                return ___setROIT1(this, in, __current);
            }
            case 633:
            {
                return ___setROIX0(this, in, __current);
            }
            case 634:
            {
                return ___setROIX1(this, in, __current);
            }
            case 635:
            {
                return ___setROIY0(this, in, __current);
            }
            case 636:
            {
                return ___setROIY1(this, in, __current);
            }
            case 637:
            {
                return ___setROIZ0(this, in, __current);
            }
            case 638:
            {
                return ___setROIZ1(this, in, __current);
            }
            case 639:
            {
                return ___setReagentDescription(this, in, __current);
            }
            case 640:
            {
                return ___setReagentID(this, in, __current);
            }
            case 641:
            {
                return ___setReagentName(this, in, __current);
            }
            case 642:
            {
                return ___setReagentReagentIdentifier(this, in, __current);
            }
            case 643:
            {
                return ___setRectHeight(this, in, __current);
            }
            case 644:
            {
                return ___setRectID(this, in, __current);
            }
            case 645:
            {
                return ___setRectTransform(this, in, __current);
            }
            case 646:
            {
                return ___setRectWidth(this, in, __current);
            }
            case 647:
            {
                return ___setRectX(this, in, __current);
            }
            case 648:
            {
                return ___setRectY(this, in, __current);
            }
            case 649:
            {
                return ___setRegionID(this, in, __current);
            }
            case 650:
            {
                return ___setRegionName(this, in, __current);
            }
            case 651:
            {
                return ___setRegionTag(this, in, __current);
            }
            case 652:
            {
                return ___setRoiLinkDirection(this, in, __current);
            }
            case 653:
            {
                return ___setRoiLinkName(this, in, __current);
            }
            case 654:
            {
                return ___setRoiLinkRef(this, in, __current);
            }
            case 655:
            {
                return ___setScreenAcquisitionEndTime(this, in, __current);
            }
            case 656:
            {
                return ___setScreenAcquisitionID(this, in, __current);
            }
            case 657:
            {
                return ___setScreenAcquisitionStartTime(this, in, __current);
            }
            case 658:
            {
                return ___setScreenDescription(this, in, __current);
            }
            case 659:
            {
                return ___setScreenExtern(this, in, __current);
            }
            case 660:
            {
                return ___setScreenID(this, in, __current);
            }
            case 661:
            {
                return ___setScreenName(this, in, __current);
            }
            case 662:
            {
                return ___setScreenProtocolDescription(this, in, __current);
            }
            case 663:
            {
                return ___setScreenProtocolIdentifier(this, in, __current);
            }
            case 664:
            {
                return ___setScreenReagentSetDescription(this, in, __current);
            }
            case 665:
            {
                return ___setScreenReagentSetIdentifier(this, in, __current);
            }
            case 666:
            {
                return ___setScreenRefID(this, in, __current);
            }
            case 667:
            {
                return ___setScreenType(this, in, __current);
            }
            case 668:
            {
                return ___setShapeBaselineShift(this, in, __current);
            }
            case 669:
            {
                return ___setShapeDirection(this, in, __current);
            }
            case 670:
            {
                return ___setShapeFillColor(this, in, __current);
            }
            case 671:
            {
                return ___setShapeFillOpacity(this, in, __current);
            }
            case 672:
            {
                return ___setShapeFillRule(this, in, __current);
            }
            case 673:
            {
                return ___setShapeFontFamily(this, in, __current);
            }
            case 674:
            {
                return ___setShapeFontSize(this, in, __current);
            }
            case 675:
            {
                return ___setShapeFontStretch(this, in, __current);
            }
            case 676:
            {
                return ___setShapeFontStyle(this, in, __current);
            }
            case 677:
            {
                return ___setShapeFontVariant(this, in, __current);
            }
            case 678:
            {
                return ___setShapeFontWeight(this, in, __current);
            }
            case 679:
            {
                return ___setShapeG(this, in, __current);
            }
            case 680:
            {
                return ___setShapeGlyphOrientationVertical(this, in, __current);
            }
            case 681:
            {
                return ___setShapeID(this, in, __current);
            }
            case 682:
            {
                return ___setShapeLocked(this, in, __current);
            }
            case 683:
            {
                return ___setShapeStrokeAttribute(this, in, __current);
            }
            case 684:
            {
                return ___setShapeStrokeColor(this, in, __current);
            }
            case 685:
            {
                return ___setShapeStrokeDashArray(this, in, __current);
            }
            case 686:
            {
                return ___setShapeStrokeLineCap(this, in, __current);
            }
            case 687:
            {
                return ___setShapeStrokeLineJoin(this, in, __current);
            }
            case 688:
            {
                return ___setShapeStrokeMiterLimit(this, in, __current);
            }
            case 689:
            {
                return ___setShapeStrokeOpacity(this, in, __current);
            }
            case 690:
            {
                return ___setShapeStrokeWidth(this, in, __current);
            }
            case 691:
            {
                return ___setShapeText(this, in, __current);
            }
            case 692:
            {
                return ___setShapeTextAnchor(this, in, __current);
            }
            case 693:
            {
                return ___setShapeTextDecoration(this, in, __current);
            }
            case 694:
            {
                return ___setShapeTextFill(this, in, __current);
            }
            case 695:
            {
                return ___setShapeTextStroke(this, in, __current);
            }
            case 696:
            {
                return ___setShapeTheT(this, in, __current);
            }
            case 697:
            {
                return ___setShapeTheZ(this, in, __current);
            }
            case 698:
            {
                return ___setShapeVectorEffect(this, in, __current);
            }
            case 699:
            {
                return ___setShapeVisibility(this, in, __current);
            }
            case 700:
            {
                return ___setShapeWritingMode(this, in, __current);
            }
            case 701:
            {
                return ___setStageLabelName(this, in, __current);
            }
            case 702:
            {
                return ___setStageLabelX(this, in, __current);
            }
            case 703:
            {
                return ___setStageLabelY(this, in, __current);
            }
            case 704:
            {
                return ___setStageLabelZ(this, in, __current);
            }
            case 705:
            {
                return ___setStagePositionPositionX(this, in, __current);
            }
            case 706:
            {
                return ___setStagePositionPositionY(this, in, __current);
            }
            case 707:
            {
                return ___setStagePositionPositionZ(this, in, __current);
            }
            case 708:
            {
                return ___setThumbnailHref(this, in, __current);
            }
            case 709:
            {
                return ___setThumbnailID(this, in, __current);
            }
            case 710:
            {
                return ___setThumbnailMIMEtype(this, in, __current);
            }
            case 711:
            {
                return ___setTiffDataFileName(this, in, __current);
            }
            case 712:
            {
                return ___setTiffDataFirstC(this, in, __current);
            }
            case 713:
            {
                return ___setTiffDataFirstT(this, in, __current);
            }
            case 714:
            {
                return ___setTiffDataFirstZ(this, in, __current);
            }
            case 715:
            {
                return ___setTiffDataIFD(this, in, __current);
            }
            case 716:
            {
                return ___setTiffDataNumPlanes(this, in, __current);
            }
            case 717:
            {
                return ___setTiffDataUUID(this, in, __current);
            }
            case 718:
            {
                return ___setTransmittanceRangeCutIn(this, in, __current);
            }
            case 719:
            {
                return ___setTransmittanceRangeCutInTolerance(this, in, __current);
            }
            case 720:
            {
                return ___setTransmittanceRangeCutOut(this, in, __current);
            }
            case 721:
            {
                return ___setTransmittanceRangeCutOutTolerance(this, in, __current);
            }
            case 722:
            {
                return ___setTransmittanceRangeTransmittance(this, in, __current);
            }
            case 723:
            {
                return ___setUUID(this, in, __current);
            }
            case 724:
            {
                return ___setWellColumn(this, in, __current);
            }
            case 725:
            {
                return ___setWellExternalDescription(this, in, __current);
            }
            case 726:
            {
                return ___setWellExternalIdentifier(this, in, __current);
            }
            case 727:
            {
                return ___setWellID(this, in, __current);
            }
            case 728:
            {
                return ___setWellReagent(this, in, __current);
            }
            case 729:
            {
                return ___setWellRow(this, in, __current);
            }
            case 730:
            {
                return ___setWellSampleID(this, in, __current);
            }
            case 731:
            {
                return ___setWellSampleImageRef(this, in, __current);
            }
            case 732:
            {
                return ___setWellSampleIndex(this, in, __current);
            }
            case 733:
            {
                return ___setWellSamplePosX(this, in, __current);
            }
            case 734:
            {
                return ___setWellSamplePosY(this, in, __current);
            }
            case 735:
            {
                return ___setWellSampleRefID(this, in, __current);
            }
            case 736:
            {
                return ___setWellSampleTimepoint(this, in, __current);
            }
            case 737:
            {
                return ___setWellType(this, in, __current);
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
        ex.reason = "type formats::IMetadata was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type formats::IMetadata was not generated with stream support";
        throw ex;
    }
}
