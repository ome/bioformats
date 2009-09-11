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

public interface MetadataRetrievePrx extends Ice.ObjectPrx
{
    public MetadataRetrieve getServant();
    public MetadataRetrieve getServant(java.util.Map<String, String> __ctx);

    public String getOMEXML();
    public String getOMEXML(java.util.Map<String, String> __ctx);

    public int getChannelComponentCount(int imageIndex, int logicalChannelIndex);
    public int getChannelComponentCount(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public int getDatasetCount();
    public int getDatasetCount(java.util.Map<String, String> __ctx);

    public int getDatasetRefCount(int imageIndex);
    public int getDatasetRefCount(int imageIndex, java.util.Map<String, String> __ctx);

    public int getDetectorCount(int instrumentIndex);
    public int getDetectorCount(int instrumentIndex, java.util.Map<String, String> __ctx);

    public int getDichroicCount(int instrumentIndex);
    public int getDichroicCount(int instrumentIndex, java.util.Map<String, String> __ctx);

    public int getExperimentCount();
    public int getExperimentCount(java.util.Map<String, String> __ctx);

    public int getExperimenterCount();
    public int getExperimenterCount(java.util.Map<String, String> __ctx);

    public int getExperimenterMembershipCount(int experimenterIndex);
    public int getExperimenterMembershipCount(int experimenterIndex, java.util.Map<String, String> __ctx);

    public int getFilterCount(int instrumentIndex);
    public int getFilterCount(int instrumentIndex, java.util.Map<String, String> __ctx);

    public int getFilterSetCount(int instrumentIndex);
    public int getFilterSetCount(int instrumentIndex, java.util.Map<String, String> __ctx);

    public int getGroupCount();
    public int getGroupCount(java.util.Map<String, String> __ctx);

    public int getGroupRefCount(int experimenterIndex);
    public int getGroupRefCount(int experimenterIndex, java.util.Map<String, String> __ctx);

    public int getImageCount();
    public int getImageCount(java.util.Map<String, String> __ctx);

    public int getInstrumentCount();
    public int getInstrumentCount(java.util.Map<String, String> __ctx);

    public int getLightSourceCount(int instrumentIndex);
    public int getLightSourceCount(int instrumentIndex, java.util.Map<String, String> __ctx);

    public int getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex);
    public int getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx);

    public int getLogicalChannelCount(int imageIndex);
    public int getLogicalChannelCount(int imageIndex, java.util.Map<String, String> __ctx);

    public int getMicrobeamManipulationCount(int imageIndex);
    public int getMicrobeamManipulationCount(int imageIndex, java.util.Map<String, String> __ctx);

    public int getMicrobeamManipulationRefCount(int experimentIndex);
    public int getMicrobeamManipulationRefCount(int experimentIndex, java.util.Map<String, String> __ctx);

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

    public int getProjectCount();
    public int getProjectCount(java.util.Map<String, String> __ctx);

    public int getProjectRefCount(int datasetIndex);
    public int getProjectRefCount(int datasetIndex, java.util.Map<String, String> __ctx);

    public int getROICount(int imageIndex);
    public int getROICount(int imageIndex, java.util.Map<String, String> __ctx);

    public int getROIRefCount(int imageIndex, int microbeamManipulationIndex);
    public int getROIRefCount(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx);

    public int getReagentCount(int screenIndex);
    public int getReagentCount(int screenIndex, java.util.Map<String, String> __ctx);

    public int getRegionCount(int imageIndex);
    public int getRegionCount(int imageIndex, java.util.Map<String, String> __ctx);

    public int getRoiLinkCount(int imageIndex, int roiIndex);
    public int getRoiLinkCount(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public int getScreenCount();
    public int getScreenCount(java.util.Map<String, String> __ctx);

    public int getScreenAcquisitionCount(int screenIndex);
    public int getScreenAcquisitionCount(int screenIndex, java.util.Map<String, String> __ctx);

    public int getScreenRefCount(int plateIndex);
    public int getScreenRefCount(int plateIndex, java.util.Map<String, String> __ctx);

    public int getShapeCount(int imageIndex, int roiIndex);
    public int getShapeCount(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx);

    public int getTiffDataCount(int imageIndex, int pixelsIndex);
    public int getTiffDataCount(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx);

    public int getWellCount(int plateIndex);
    public int getWellCount(int plateIndex, java.util.Map<String, String> __ctx);

    public int getWellSampleCount(int plateIndex, int wellIndex);
    public int getWellSampleCount(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public int getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex);
    public int getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public String getUUID();
    public String getUUID(java.util.Map<String, String> __ctx);

    public String getArcType(int instrumentIndex, int lightSourceIndex);
    public String getArcType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    public String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx);

    public int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    public int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx);

    public String getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    public String getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx);

    public String getCircleCx(int imageIndex, int roiIndex, int shapeIndex);
    public String getCircleCx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getCircleCy(int imageIndex, int roiIndex, int shapeIndex);
    public String getCircleCy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getCircleID(int imageIndex, int roiIndex, int shapeIndex);
    public String getCircleID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getCircleR(int imageIndex, int roiIndex, int shapeIndex);
    public String getCircleR(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getCircleTransform(int imageIndex, int roiIndex, int shapeIndex);
    public String getCircleTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getContactExperimenter(int groupIndex);
    public String getContactExperimenter(int groupIndex, java.util.Map<String, String> __ctx);

    public String getDatasetDescription(int datasetIndex);
    public String getDatasetDescription(int datasetIndex, java.util.Map<String, String> __ctx);

    public String getDatasetExperimenterRef(int datasetIndex);
    public String getDatasetExperimenterRef(int datasetIndex, java.util.Map<String, String> __ctx);

    public String getDatasetGroupRef(int datasetIndex);
    public String getDatasetGroupRef(int datasetIndex, java.util.Map<String, String> __ctx);

    public String getDatasetID(int datasetIndex);
    public String getDatasetID(int datasetIndex, java.util.Map<String, String> __ctx);

    public boolean getDatasetLocked(int datasetIndex);
    public boolean getDatasetLocked(int datasetIndex, java.util.Map<String, String> __ctx);

    public String getDatasetName(int datasetIndex);
    public String getDatasetName(int datasetIndex, java.util.Map<String, String> __ctx);

    public String getDatasetRefID(int imageIndex, int datasetRefIndex);
    public String getDatasetRefID(int imageIndex, int datasetRefIndex, java.util.Map<String, String> __ctx);

    public float getDetectorAmplificationGain(int instrumentIndex, int detectorIndex);
    public float getDetectorAmplificationGain(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

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

    public float getDetectorZoom(int instrumentIndex, int detectorIndex);
    public float getDetectorZoom(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx);

    public String getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex);
    public String getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex);
    public String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex);
    public float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex);
    public float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public float getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex);
    public float getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public float getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex);
    public float getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getDichroicID(int instrumentIndex, int dichroicIndex);
    public String getDichroicID(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx);

    public String getDichroicLotNumber(int instrumentIndex, int dichroicIndex);
    public String getDichroicLotNumber(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx);

    public String getDichroicManufacturer(int instrumentIndex, int dichroicIndex);
    public String getDichroicManufacturer(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx);

    public String getDichroicModel(int instrumentIndex, int dichroicIndex);
    public String getDichroicModel(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx);

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

    public String getDisplayOptionsDisplay(int imageIndex);
    public String getDisplayOptionsDisplay(int imageIndex, java.util.Map<String, String> __ctx);

    public String getDisplayOptionsID(int imageIndex);
    public String getDisplayOptionsID(int imageIndex, java.util.Map<String, String> __ctx);

    public float getDisplayOptionsZoom(int imageIndex);
    public float getDisplayOptionsZoom(int imageIndex, java.util.Map<String, String> __ctx);

    public String getEllipseCx(int imageIndex, int roiIndex, int shapeIndex);
    public String getEllipseCx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getEllipseCy(int imageIndex, int roiIndex, int shapeIndex);
    public String getEllipseCy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getEllipseID(int imageIndex, int roiIndex, int shapeIndex);
    public String getEllipseID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getEllipseRx(int imageIndex, int roiIndex, int shapeIndex);
    public String getEllipseRx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getEllipseRy(int imageIndex, int roiIndex, int shapeIndex);
    public String getEllipseRy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex);
    public String getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getEmFilterLotNumber(int instrumentIndex, int filterIndex);
    public String getEmFilterLotNumber(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getEmFilterManufacturer(int instrumentIndex, int filterIndex);
    public String getEmFilterManufacturer(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getEmFilterModel(int instrumentIndex, int filterIndex);
    public String getEmFilterModel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getEmFilterType(int instrumentIndex, int filterIndex);
    public String getEmFilterType(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getExFilterLotNumber(int instrumentIndex, int filterIndex);
    public String getExFilterLotNumber(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getExFilterManufacturer(int instrumentIndex, int filterIndex);
    public String getExFilterManufacturer(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getExFilterModel(int instrumentIndex, int filterIndex);
    public String getExFilterModel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getExFilterType(int instrumentIndex, int filterIndex);
    public String getExFilterType(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getExperimentDescription(int experimentIndex);
    public String getExperimentDescription(int experimentIndex, java.util.Map<String, String> __ctx);

    public String getExperimentExperimenterRef(int experimentIndex);
    public String getExperimentExperimenterRef(int experimentIndex, java.util.Map<String, String> __ctx);

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

    public String getExperimenterOMEName(int experimenterIndex);
    public String getExperimenterOMEName(int experimenterIndex, java.util.Map<String, String> __ctx);

    public String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex);
    public String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx);

    public String getFilamentType(int instrumentIndex, int lightSourceIndex);
    public String getFilamentType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public String getFilterFilterWheel(int instrumentIndex, int filterIndex);
    public String getFilterFilterWheel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getFilterID(int instrumentIndex, int filterIndex);
    public String getFilterID(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getFilterLotNumber(int instrumentIndex, int filterIndex);
    public String getFilterLotNumber(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getFilterManufacturer(int instrumentIndex, int filterIndex);
    public String getFilterManufacturer(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getFilterModel(int instrumentIndex, int filterIndex);
    public String getFilterModel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getFilterType(int instrumentIndex, int filterIndex);
    public String getFilterType(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public String getFilterSetDichroic(int instrumentIndex, int filterSetIndex);
    public String getFilterSetDichroic(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public String getFilterSetEmFilter(int instrumentIndex, int filterSetIndex);
    public String getFilterSetEmFilter(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public String getFilterSetExFilter(int instrumentIndex, int filterSetIndex);
    public String getFilterSetExFilter(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public String getFilterSetID(int instrumentIndex, int filterSetIndex);
    public String getFilterSetID(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex);
    public String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex);
    public String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public String getFilterSetModel(int instrumentIndex, int filterSetIndex);
    public String getFilterSetModel(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx);

    public String getGroupID(int groupIndex);
    public String getGroupID(int groupIndex, java.util.Map<String, String> __ctx);

    public String getGroupName(int groupIndex);
    public String getGroupName(int groupIndex, java.util.Map<String, String> __ctx);

    public String getImageAcquiredPixels(int imageIndex);
    public String getImageAcquiredPixels(int imageIndex, java.util.Map<String, String> __ctx);

    public String getImageCreationDate(int imageIndex);
    public String getImageCreationDate(int imageIndex, java.util.Map<String, String> __ctx);

    public String getImageDefaultPixels(int imageIndex);
    public String getImageDefaultPixels(int imageIndex, java.util.Map<String, String> __ctx);

    public String getImageDescription(int imageIndex);
    public String getImageDescription(int imageIndex, java.util.Map<String, String> __ctx);

    public String getImageExperimentRef(int imageIndex);
    public String getImageExperimentRef(int imageIndex, java.util.Map<String, String> __ctx);

    public String getImageExperimenterRef(int imageIndex);
    public String getImageExperimenterRef(int imageIndex, java.util.Map<String, String> __ctx);

    public String getImageGroupRef(int imageIndex);
    public String getImageGroupRef(int imageIndex, java.util.Map<String, String> __ctx);

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

    public boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex);
    public boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public String getLaserPulse(int instrumentIndex, int lightSourceIndex);
    public String getLaserPulse(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

    public boolean getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex);
    public boolean getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

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

    public float getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);
    public float getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx);

    public String getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);
    public String getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx);

    public int getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);
    public int getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx);

    public float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex);
    public float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex);
    public String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex);
    public int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLineID(int imageIndex, int roiIndex, int shapeIndex);
    public String getLineID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getLineTransform(int imageIndex, int roiIndex, int shapeIndex);
    public String getLineTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getLineX1(int imageIndex, int roiIndex, int shapeIndex);
    public String getLineX1(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getLineX2(int imageIndex, int roiIndex, int shapeIndex);
    public String getLineX2(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getLineY1(int imageIndex, int roiIndex, int shapeIndex);
    public String getLineY1(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getLineY2(int imageIndex, int roiIndex, int shapeIndex);
    public String getLineY2(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelDetector(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelDetector(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex);
    public int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex);
    public int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelID(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelID(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

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

    public String getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex);
    public String getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx);

    public String getMaskHeight(int imageIndex, int roiIndex, int shapeIndex);
    public String getMaskHeight(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getMaskID(int imageIndex, int roiIndex, int shapeIndex);
    public String getMaskID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getMaskTransform(int imageIndex, int roiIndex, int shapeIndex);
    public String getMaskTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getMaskWidth(int imageIndex, int roiIndex, int shapeIndex);
    public String getMaskWidth(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getMaskX(int imageIndex, int roiIndex, int shapeIndex);
    public String getMaskX(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getMaskY(int imageIndex, int roiIndex, int shapeIndex);
    public String getMaskY(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public boolean getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex);
    public boolean getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex);
    public String getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex);
    public String getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex);
    public String getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public int getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex);
    public int getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public int getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex);
    public int getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex);
    public String getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx);

    public String getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex);
    public String getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx);

    public String getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex);
    public String getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx);

    public String getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex);
    public String getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex, java.util.Map<String, String> __ctx);

    public String getMicroscopeID(int instrumentIndex);
    public String getMicroscopeID(int instrumentIndex, java.util.Map<String, String> __ctx);

    public String getMicroscopeManufacturer(int instrumentIndex);
    public String getMicroscopeManufacturer(int instrumentIndex, java.util.Map<String, String> __ctx);

    public String getMicroscopeModel(int instrumentIndex);
    public String getMicroscopeModel(int instrumentIndex, java.util.Map<String, String> __ctx);

    public String getMicroscopeSerialNumber(int instrumentIndex);
    public String getMicroscopeSerialNumber(int instrumentIndex, java.util.Map<String, String> __ctx);

    public String getMicroscopeType(int instrumentIndex);
    public String getMicroscopeType(int instrumentIndex, java.util.Map<String, String> __ctx);

    public String getOTFBinaryFile(int instrumentIndex, int otfIndex);
    public String getOTFBinaryFile(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx);

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

    public boolean getObjectiveIris(int instrumentIndex, int objectiveIndex);
    public boolean getObjectiveIris(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx);

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

    public float getObjectiveSettingsCorrectionCollar(int imageIndex);
    public float getObjectiveSettingsCorrectionCollar(int imageIndex, java.util.Map<String, String> __ctx);

    public String getObjectiveSettingsMedium(int imageIndex);
    public String getObjectiveSettingsMedium(int imageIndex, java.util.Map<String, String> __ctx);

    public String getObjectiveSettingsObjective(int imageIndex);
    public String getObjectiveSettingsObjective(int imageIndex, java.util.Map<String, String> __ctx);

    public float getObjectiveSettingsRefractiveIndex(int imageIndex);
    public float getObjectiveSettingsRefractiveIndex(int imageIndex, java.util.Map<String, String> __ctx);

    public String getPathD(int imageIndex, int roiIndex, int shapeIndex);
    public String getPathD(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getPathID(int imageIndex, int roiIndex, int shapeIndex);
    public String getPathID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

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

    public String getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex);
    public String getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

    public String getPlaneID(int imageIndex, int pixelsIndex, int planeIndex);
    public String getPlaneID(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx);

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

    public String getPlateColumnNamingConvention(int plateIndex);
    public String getPlateColumnNamingConvention(int plateIndex, java.util.Map<String, String> __ctx);

    public String getPlateDescription(int plateIndex);
    public String getPlateDescription(int plateIndex, java.util.Map<String, String> __ctx);

    public String getPlateExternalIdentifier(int plateIndex);
    public String getPlateExternalIdentifier(int plateIndex, java.util.Map<String, String> __ctx);

    public String getPlateID(int plateIndex);
    public String getPlateID(int plateIndex, java.util.Map<String, String> __ctx);

    public String getPlateName(int plateIndex);
    public String getPlateName(int plateIndex, java.util.Map<String, String> __ctx);

    public String getPlateRowNamingConvention(int plateIndex);
    public String getPlateRowNamingConvention(int plateIndex, java.util.Map<String, String> __ctx);

    public String getPlateStatus(int plateIndex);
    public String getPlateStatus(int plateIndex, java.util.Map<String, String> __ctx);

    public double getPlateWellOriginX(int plateIndex);
    public double getPlateWellOriginX(int plateIndex, java.util.Map<String, String> __ctx);

    public double getPlateWellOriginY(int plateIndex);
    public double getPlateWellOriginY(int plateIndex, java.util.Map<String, String> __ctx);

    public String getPlateRefID(int screenIndex, int plateRefIndex);
    public String getPlateRefID(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx);

    public int getPlateRefSample(int screenIndex, int plateRefIndex);
    public int getPlateRefSample(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx);

    public String getPlateRefWell(int screenIndex, int plateRefIndex);
    public String getPlateRefWell(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx);

    public String getPointCx(int imageIndex, int roiIndex, int shapeIndex);
    public String getPointCx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getPointCy(int imageIndex, int roiIndex, int shapeIndex);
    public String getPointCy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getPointID(int imageIndex, int roiIndex, int shapeIndex);
    public String getPointID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getPointR(int imageIndex, int roiIndex, int shapeIndex);
    public String getPointR(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getPointTransform(int imageIndex, int roiIndex, int shapeIndex);
    public String getPointTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getPolygonID(int imageIndex, int roiIndex, int shapeIndex);
    public String getPolygonID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex);
    public String getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex);
    public String getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getPolylineID(int imageIndex, int roiIndex, int shapeIndex);
    public String getPolylineID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex);
    public String getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex);
    public String getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getProjectDescription(int projectIndex);
    public String getProjectDescription(int projectIndex, java.util.Map<String, String> __ctx);

    public String getProjectExperimenterRef(int projectIndex);
    public String getProjectExperimenterRef(int projectIndex, java.util.Map<String, String> __ctx);

    public String getProjectGroupRef(int projectIndex);
    public String getProjectGroupRef(int projectIndex, java.util.Map<String, String> __ctx);

    public String getProjectID(int projectIndex);
    public String getProjectID(int projectIndex, java.util.Map<String, String> __ctx);

    public String getProjectName(int projectIndex);
    public String getProjectName(int projectIndex, java.util.Map<String, String> __ctx);

    public String getProjectRefID(int datasetIndex, int projectRefIndex);
    public String getProjectRefID(int datasetIndex, int projectRefIndex, java.util.Map<String, String> __ctx);

    public String getPumpLightSource(int instrumentIndex, int lightSourceIndex);
    public String getPumpLightSource(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx);

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

    public String getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex);
    public String getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex, java.util.Map<String, String> __ctx);

    public String getReagentDescription(int screenIndex, int reagentIndex);
    public String getReagentDescription(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public String getReagentID(int screenIndex, int reagentIndex);
    public String getReagentID(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public String getReagentName(int screenIndex, int reagentIndex);
    public String getReagentName(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public String getReagentReagentIdentifier(int screenIndex, int reagentIndex);
    public String getReagentReagentIdentifier(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx);

    public String getRectHeight(int imageIndex, int roiIndex, int shapeIndex);
    public String getRectHeight(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getRectID(int imageIndex, int roiIndex, int shapeIndex);
    public String getRectID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getRectTransform(int imageIndex, int roiIndex, int shapeIndex);
    public String getRectTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getRectWidth(int imageIndex, int roiIndex, int shapeIndex);
    public String getRectWidth(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getRectX(int imageIndex, int roiIndex, int shapeIndex);
    public String getRectX(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getRectY(int imageIndex, int roiIndex, int shapeIndex);
    public String getRectY(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getRegionID(int imageIndex, int regionIndex);
    public String getRegionID(int imageIndex, int regionIndex, java.util.Map<String, String> __ctx);

    public String getRegionName(int imageIndex, int regionIndex);
    public String getRegionName(int imageIndex, int regionIndex, java.util.Map<String, String> __ctx);

    public String getRegionTag(int imageIndex, int regionIndex);
    public String getRegionTag(int imageIndex, int regionIndex, java.util.Map<String, String> __ctx);

    public String getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex);
    public String getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx);

    public String getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex);
    public String getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx);

    public String getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex);
    public String getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx);

    public String getScreenDescription(int screenIndex);
    public String getScreenDescription(int screenIndex, java.util.Map<String, String> __ctx);

    public String getScreenExtern(int screenIndex);
    public String getScreenExtern(int screenIndex, java.util.Map<String, String> __ctx);

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

    public String getScreenReagentSetIdentifier(int screenIndex);
    public String getScreenReagentSetIdentifier(int screenIndex, java.util.Map<String, String> __ctx);

    public String getScreenType(int screenIndex);
    public String getScreenType(int screenIndex, java.util.Map<String, String> __ctx);

    public String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex);
    public String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex);
    public String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex);
    public String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx);

    public String getScreenRefID(int plateIndex, int screenRefIndex);
    public String getScreenRefID(int plateIndex, int screenRefIndex, java.util.Map<String, String> __ctx);

    public String getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeDirection(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeDirection(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public int getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex);
    public int getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeG(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeG(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public int getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex);
    public int getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeID(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public boolean getShapeLocked(int imageIndex, int roiIndex, int shapeIndex);
    public boolean getShapeLocked(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public int getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex);
    public int getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public float getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex);
    public float getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public int getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex);
    public int getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeText(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeText(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public int getShapeTheT(int imageIndex, int roiIndex, int shapeIndex);
    public int getShapeTheT(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public int getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex);
    public int getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public boolean getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex);
    public boolean getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

    public String getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex);
    public String getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx);

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

    public String getThumbnailHref(int imageIndex);
    public String getThumbnailHref(int imageIndex, java.util.Map<String, String> __ctx);

    public String getThumbnailID(int imageIndex);
    public String getThumbnailID(int imageIndex, java.util.Map<String, String> __ctx);

    public String getThumbnailMIMEtype(int imageIndex);
    public String getThumbnailMIMEtype(int imageIndex, java.util.Map<String, String> __ctx);

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

    public int getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex);
    public int getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public int getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex);
    public int getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public int getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex);
    public int getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public int getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex);
    public int getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public int getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex);
    public int getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx);

    public int getWellColumn(int plateIndex, int wellIndex);
    public int getWellColumn(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public String getWellExternalDescription(int plateIndex, int wellIndex);
    public String getWellExternalDescription(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public String getWellExternalIdentifier(int plateIndex, int wellIndex);
    public String getWellExternalIdentifier(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public String getWellID(int plateIndex, int wellIndex);
    public String getWellID(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public String getWellReagent(int plateIndex, int wellIndex);
    public String getWellReagent(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public int getWellRow(int plateIndex, int wellIndex);
    public int getWellRow(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public String getWellType(int plateIndex, int wellIndex);
    public String getWellType(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx);

    public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex);
    public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex);
    public String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex);
    public int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex);
    public float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex);
    public float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);
    public int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx);

    public String getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex);
    public String getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, java.util.Map<String, String> __ctx);
}
