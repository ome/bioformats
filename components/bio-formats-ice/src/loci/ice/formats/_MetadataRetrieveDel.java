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

public interface _MetadataRetrieveDel extends Ice._ObjectDel
{
    MetadataRetrieve getServant(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getOMEXML(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getChannelComponentCount(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDatasetCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDatasetRefCount(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDetectorCount(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDichroicCount(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getExperimentCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getExperimenterCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getExperimenterMembershipCount(int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getFilterCount(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getFilterSetCount(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getGroupCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getGroupRefCount(int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getImageCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getInstrumentCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLightSourceCount(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLogicalChannelCount(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getMicrobeamManipulationCount(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getMicrobeamManipulationRefCount(int experimentIndex, java.util.Map<String, String> __ctx)
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

    int getProjectCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getProjectRefCount(int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getROICount(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getROIRefCount(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getReagentCount(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getRegionCount(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getRoiLinkCount(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getScreenCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getScreenAcquisitionCount(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getScreenRefCount(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getShapeCount(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getTiffDataCount(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellCount(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellSampleCount(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getUUID(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getArcType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getCircleCx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getCircleCy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getCircleID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getCircleR(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getCircleTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getContactExperimenter(int groupIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDatasetDescription(int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDatasetExperimenterRef(int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDatasetGroupRef(int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDatasetID(int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean getDatasetLocked(int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDatasetName(int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDatasetRefID(int imageIndex, int datasetRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDetectorAmplificationGain(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
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

    float getDetectorZoom(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDichroicID(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDichroicLotNumber(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDichroicManufacturer(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDichroicModel(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
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

    String getDisplayOptionsDisplay(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDisplayOptionsID(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getDisplayOptionsZoom(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getEllipseCx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getEllipseCy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getEllipseID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getEllipseRx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getEllipseRy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getEmFilterLotNumber(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getEmFilterManufacturer(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getEmFilterModel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getEmFilterType(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExFilterLotNumber(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExFilterManufacturer(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExFilterModel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExFilterType(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExperimentDescription(int experimentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExperimentExperimenterRef(int experimentIndex, java.util.Map<String, String> __ctx)
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

    String getExperimenterOMEName(int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilamentType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterFilterWheel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterID(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterLotNumber(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterManufacturer(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterModel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterType(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterSetDichroic(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterSetEmFilter(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterSetExFilter(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterSetID(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFilterSetModel(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getGroupID(int groupIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getGroupName(int groupIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageAcquiredPixels(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageCreationDate(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageDefaultPixels(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageDescription(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageExperimentRef(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageExperimenterRef(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getImageGroupRef(int imageIndex, java.util.Map<String, String> __ctx)
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

    boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLaserPulse(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
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

    float getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLineID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLineTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLineX1(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLineX2(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLineY1(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLineY2(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelDetector(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelID(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
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

    String getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMaskHeight(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMaskID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMaskTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMaskWidth(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMaskX(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMaskY(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMicroscopeID(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMicroscopeManufacturer(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMicroscopeModel(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMicroscopeSerialNumber(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getMicroscopeType(int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getOTFBinaryFile(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
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

    boolean getObjectiveIris(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
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

    float getObjectiveSettingsCorrectionCollar(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveSettingsMedium(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveSettingsObjective(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getObjectiveSettingsRefractiveIndex(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPathD(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPathID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
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

    String getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlaneID(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
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

    String getPlateColumnNamingConvention(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateDescription(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateExternalIdentifier(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateID(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateName(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateRowNamingConvention(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateStatus(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getPlateWellOriginX(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getPlateWellOriginY(int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateRefID(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPlateRefSample(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPlateRefWell(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPointCx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPointCy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPointID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPointR(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPointTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPolygonID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPolylineID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getProjectDescription(int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getProjectExperimenterRef(int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getProjectGroupRef(int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getProjectID(int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getProjectName(int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getProjectRefID(int datasetIndex, int projectRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getPumpLightSource(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
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

    String getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getReagentDescription(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getReagentID(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getReagentName(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getReagentReagentIdentifier(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getRectHeight(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getRectID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getRectTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getRectWidth(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getRectX(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getRectY(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getRegionID(int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getRegionName(int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getRegionTag(int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenDescription(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenExtern(int screenIndex, java.util.Map<String, String> __ctx)
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

    String getScreenReagentSetIdentifier(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenType(int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getScreenRefID(int plateIndex, int screenRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeDirection(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeG(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean getShapeLocked(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeText(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getShapeTheT(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
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

    String getThumbnailHref(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getThumbnailID(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getThumbnailMIMEtype(int imageIndex, java.util.Map<String, String> __ctx)
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

    int getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellColumn(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellExternalDescription(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellExternalIdentifier(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellID(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellReagent(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellRow(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellType(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;
}
