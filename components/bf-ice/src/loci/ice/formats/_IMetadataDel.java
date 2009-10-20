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

public interface _IMetadataDel extends Ice._ObjectDel
{
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

    double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDetectorGain(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorID(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorManufacturer(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorModel(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDetectorOffset(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorSerialNumber(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorType(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDetectorVoltage(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDetectorZoom(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDichroicID(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDichroicLotNumber(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDichroicManufacturer(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDichroicModel(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDimensionsTimeIncrement(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getDimensionsWaveStart(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDisplayOptionsDisplay(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDisplayOptionsID(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getDisplayOptionsZoom(int imageIndex, java.util.Map<String, String> __ctx)
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

    double getImagingEnvironmentAirPressure(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getImagingEnvironmentCO2Percent(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getImagingEnvironmentHumidity(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getImagingEnvironmentTemperature(int imageIndex, java.util.Map<String, String> __ctx)
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

    double getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
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

    double getLightSourcePower(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
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

    double getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
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

    byte[] getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
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

    double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveCorrection(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveID(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveImmersion(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean getObjectiveIris(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getObjectiveLensNA(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveModel(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getObjectiveSettingsCorrectionCollar(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveSettingsMedium(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getObjectiveSettingsObjective(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getObjectiveSettingsRefractiveIndex(int imageIndex, java.util.Map<String, String> __ctx)
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

    double getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
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

    double getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
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

    double getStageLabelX(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getStageLabelY(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getStageLabelZ(int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
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

    double getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    double getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, java.util.Map<String, String> __ctx)
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

    void setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setContactExperimenter(String experimenter, int groupIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetDescription(String description, int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetExperimenterRef(String experimenterRef, int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetGroupRef(String groupRef, int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetID(String id, int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetLocked(boolean locked, int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetName(String name, int datasetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDatasetRefID(String id, int imageIndex, int datasetRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorAmplificationGain(double amplificationGain, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorGain(double gain, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorID(String id, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorModel(String model, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorOffset(double offset, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorType(String type, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorVoltage(double voltage, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorZoom(double zoom, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsGain(double gain, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsOffset(double offset, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsReadOutRate(double readOutRate, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDetectorSettingsVoltage(double voltage, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDichroicID(String id, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDichroicModel(String model, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsPhysicalSizeX(double physicalSizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsPhysicalSizeY(double physicalSizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsPhysicalSizeZ(double physicalSizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsTimeIncrement(double timeIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDisplayOptionsDisplay(String display, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDisplayOptionsID(String id, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setDisplayOptionsZoom(double zoom, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEmFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setEmFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimentDescription(String description, int experimentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimentExperimenterRef(String experimenterRef, int experimentIndex, java.util.Map<String, String> __ctx)
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

    void setExperimenterOMEName(String omeName, int experimenterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilamentType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterID(String id, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetID(String id, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setGroupID(String id, int groupIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setGroupName(String name, int groupIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageAcquiredPixels(String acquiredPixels, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageCreationDate(String creationDate, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageDefaultPixels(String defaultPixels, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageDescription(String description, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageExperimentRef(String experimentRef, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageExperimenterRef(String experimenterRef, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageGroupRef(String groupRef, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageID(String id, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageInstrumentRef(String instrumentRef, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImageName(String name, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImagingEnvironmentAirPressure(double airPressure, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImagingEnvironmentCO2Percent(double cO2Percent, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImagingEnvironmentHumidity(double humidity, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setImagingEnvironmentTemperature(double temperature, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setInstrumentID(String id, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserPockelCell(boolean pockelCell, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLaserRepetitionRate(double repetitionRate, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
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

    void setLightSourcePower(double power, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceRefAttenuation(double attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceSettingsAttenuation(double attenuation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLineID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelNdFilter(double ndFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelPinholeSize(double pinholeSize, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskPixelsBigEndian(boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskPixelsBinData(byte[] binData, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicroscopeID(String id, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicroscopeManufacturer(String manufacturer, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicroscopeModel(String model, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMicroscopeType(String type, int instrumentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
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

    void setObjectiveCalibratedMagnification(double calibratedMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveID(String id, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveLensNA(double lensNA, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveWorkingDistance(double workingDistance, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveSettingsCorrectionCollar(double correctionCollar, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveSettingsMedium(String medium, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveSettingsObjective(String objective, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setObjectiveSettingsRefractiveIndex(double refractiveIndex, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPathD(String d, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPathID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
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

    void setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTimingDeltaT(double deltaT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlaneTimingExposureTime(double exposureTime, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateDescription(String description, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateExternalIdentifier(String externalIdentifier, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateID(String id, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateName(String name, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateRowNamingConvention(String rowNamingConvention, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateStatus(String status, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateWellOriginX(double wellOriginX, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateWellOriginY(double wellOriginY, int plateIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateRefID(String id, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateRefSample(int sample, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPlateRefWell(String well, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPointID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPointR(String r, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setProjectDescription(String description, int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setProjectExperimenterRef(String experimenterRef, int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setProjectGroupRef(String groupRef, int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setProjectID(String id, int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setProjectName(String name, int projectIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setProjectRefID(String id, int datasetIndex, int projectRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
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

    void setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setReagentDescription(String description, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setReagentID(String id, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setReagentName(String name, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRectID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRectX(String x, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRectY(String y, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRegionID(String id, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRegionName(String name, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRegionTag(String tag, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenDescription(String description, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenExtern(String extern, int screenIndex, java.util.Map<String, String> __ctx)
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

    void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenType(String type, int screenIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setScreenRefID(String id, int plateIndex, int screenRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeLocked(boolean locked, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeOpacity(double strokeOpacity, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeVisibility(boolean visibility, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStageLabelName(String name, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStageLabelX(double x, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStageLabelY(double y, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStageLabelZ(double z, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStagePositionPositionX(double positionX, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStagePositionPositionY(double positionY, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStagePositionPositionZ(double positionZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setThumbnailHref(String href, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setThumbnailID(String id, int imageIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setThumbnailMIMEtype(String mimEtype, int imageIndex, java.util.Map<String, String> __ctx)
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

    void setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellColumn(int column, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellID(String id, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellReagent(String reagent, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellRow(int row, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellType(String type, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSamplePosX(double posX, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSamplePosY(double posY, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;
}
