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

public interface _MetadataRetrieveOperations
{
    MetadataRetrieve getServant(Ice.Current __current);

    String getOMEXML(Ice.Current __current);

    int getChannelComponentCount(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getDatasetCount(Ice.Current __current);

    int getDatasetRefCount(int imageIndex, Ice.Current __current);

    int getDetectorCount(int instrumentIndex, Ice.Current __current);

    int getDichroicCount(int instrumentIndex, Ice.Current __current);

    int getExperimentCount(Ice.Current __current);

    int getExperimenterCount(Ice.Current __current);

    int getExperimenterMembershipCount(int experimenterIndex, Ice.Current __current);

    int getFilterCount(int instrumentIndex, Ice.Current __current);

    int getFilterSetCount(int instrumentIndex, Ice.Current __current);

    int getGroupCount(Ice.Current __current);

    int getGroupRefCount(int experimenterIndex, Ice.Current __current);

    int getImageCount(Ice.Current __current);

    int getInstrumentCount(Ice.Current __current);

    int getLightSourceCount(int instrumentIndex, Ice.Current __current);

    int getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    int getLogicalChannelCount(int imageIndex, Ice.Current __current);

    int getMicrobeamManipulationCount(int imageIndex, Ice.Current __current);

    int getMicrobeamManipulationRefCount(int experimentIndex, Ice.Current __current);

    int getOTFCount(int instrumentIndex, Ice.Current __current);

    int getObjectiveCount(int instrumentIndex, Ice.Current __current);

    int getPixelsCount(int imageIndex, Ice.Current __current);

    int getPlaneCount(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPlateCount(Ice.Current __current);

    int getPlateRefCount(int screenIndex, Ice.Current __current);

    int getProjectCount(Ice.Current __current);

    int getProjectRefCount(int datasetIndex, Ice.Current __current);

    int getROICount(int imageIndex, Ice.Current __current);

    int getROIRefCount(int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    int getReagentCount(int screenIndex, Ice.Current __current);

    int getRegionCount(int imageIndex, Ice.Current __current);

    int getRoiLinkCount(int imageIndex, int roiIndex, Ice.Current __current);

    int getScreenCount(Ice.Current __current);

    int getScreenAcquisitionCount(int screenIndex, Ice.Current __current);

    int getScreenRefCount(int plateIndex, Ice.Current __current);

    int getShapeCount(int imageIndex, int roiIndex, Ice.Current __current);

    int getTiffDataCount(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getWellCount(int plateIndex, Ice.Current __current);

    int getWellSampleCount(int plateIndex, int wellIndex, Ice.Current __current);

    int getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    String getUUID(Ice.Current __current);

    String getArcType(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    String getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current __current);

    String getCircleCx(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getCircleCy(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getCircleID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getCircleR(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getCircleTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getContactExperimenter(int groupIndex, Ice.Current __current);

    String getDatasetDescription(int datasetIndex, Ice.Current __current);

    String getDatasetExperimenterRef(int datasetIndex, Ice.Current __current);

    String getDatasetGroupRef(int datasetIndex, Ice.Current __current);

    String getDatasetID(int datasetIndex, Ice.Current __current);

    boolean getDatasetLocked(int datasetIndex, Ice.Current __current);

    String getDatasetName(int datasetIndex, Ice.Current __current);

    String getDatasetRefID(int imageIndex, int datasetRefIndex, Ice.Current __current);

    float getDetectorAmplificationGain(int instrumentIndex, int detectorIndex, Ice.Current __current);

    float getDetectorGain(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorID(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorManufacturer(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorModel(int instrumentIndex, int detectorIndex, Ice.Current __current);

    float getDetectorOffset(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorSerialNumber(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorType(int instrumentIndex, int detectorIndex, Ice.Current __current);

    float getDetectorVoltage(int instrumentIndex, int detectorIndex, Ice.Current __current);

    float getDetectorZoom(int instrumentIndex, int detectorIndex, Ice.Current __current);

    String getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    float getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    float getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getDichroicID(int instrumentIndex, int dichroicIndex, Ice.Current __current);

    String getDichroicLotNumber(int instrumentIndex, int dichroicIndex, Ice.Current __current);

    String getDichroicManufacturer(int instrumentIndex, int dichroicIndex, Ice.Current __current);

    String getDichroicModel(int instrumentIndex, int dichroicIndex, Ice.Current __current);

    float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex, Ice.Current __current);

    float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex, Ice.Current __current);

    float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex, Ice.Current __current);

    float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getDimensionsWaveStart(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getDisplayOptionsDisplay(int imageIndex, Ice.Current __current);

    String getDisplayOptionsID(int imageIndex, Ice.Current __current);

    float getDisplayOptionsZoom(int imageIndex, Ice.Current __current);

    String getEllipseCx(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getEllipseCy(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getEllipseID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getEllipseRx(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getEllipseRy(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getEmFilterLotNumber(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getEmFilterManufacturer(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getEmFilterModel(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getEmFilterType(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getExFilterLotNumber(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getExFilterManufacturer(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getExFilterModel(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getExFilterType(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getExperimentDescription(int experimentIndex, Ice.Current __current);

    String getExperimentExperimenterRef(int experimentIndex, Ice.Current __current);

    String getExperimentID(int experimentIndex, Ice.Current __current);

    String getExperimentType(int experimentIndex, Ice.Current __current);

    String getExperimenterEmail(int experimenterIndex, Ice.Current __current);

    String getExperimenterFirstName(int experimenterIndex, Ice.Current __current);

    String getExperimenterID(int experimenterIndex, Ice.Current __current);

    String getExperimenterInstitution(int experimenterIndex, Ice.Current __current);

    String getExperimenterLastName(int experimenterIndex, Ice.Current __current);

    String getExperimenterOMEName(int experimenterIndex, Ice.Current __current);

    String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex, Ice.Current __current);

    String getFilamentType(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getFilterFilterWheel(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getFilterID(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getFilterLotNumber(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getFilterManufacturer(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getFilterModel(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getFilterType(int instrumentIndex, int filterIndex, Ice.Current __current);

    String getFilterSetDichroic(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getFilterSetEmFilter(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getFilterSetExFilter(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getFilterSetID(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getFilterSetModel(int instrumentIndex, int filterSetIndex, Ice.Current __current);

    String getGroupID(int groupIndex, Ice.Current __current);

    String getGroupName(int groupIndex, Ice.Current __current);

    String getImageAcquiredPixels(int imageIndex, Ice.Current __current);

    String getImageCreationDate(int imageIndex, Ice.Current __current);

    String getImageDefaultPixels(int imageIndex, Ice.Current __current);

    String getImageDescription(int imageIndex, Ice.Current __current);

    String getImageExperimentRef(int imageIndex, Ice.Current __current);

    String getImageExperimenterRef(int imageIndex, Ice.Current __current);

    String getImageGroupRef(int imageIndex, Ice.Current __current);

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

    boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLaserPulse(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    boolean getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLaserType(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    int getLaserWavelength(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLightSourceID(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLightSourceModel(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    float getLightSourcePower(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    float getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Ice.Current __current);

    String getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Ice.Current __current);

    int getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, Ice.Current __current);

    float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLineID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getLineTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getLineX1(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getLineX2(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getLineY1(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getLineY2(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelDetector(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelID(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelMode(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelName(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    int getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex, Ice.Current __current);

    String getMaskHeight(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskWidth(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskX(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskY(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    boolean getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    String getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    String getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex, Ice.Current __current);

    String getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex, Ice.Current __current);

    String getMicroscopeID(int instrumentIndex, Ice.Current __current);

    String getMicroscopeManufacturer(int instrumentIndex, Ice.Current __current);

    String getMicroscopeModel(int instrumentIndex, Ice.Current __current);

    String getMicroscopeSerialNumber(int instrumentIndex, Ice.Current __current);

    String getMicroscopeType(int instrumentIndex, Ice.Current __current);

    String getOTFBinaryFile(int instrumentIndex, int otfIndex, Ice.Current __current);

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

    boolean getObjectiveIris(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    float getObjectiveLensNA(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveModel(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex, Ice.Current __current);

    float getObjectiveSettingsCorrectionCollar(int imageIndex, Ice.Current __current);

    String getObjectiveSettingsMedium(int imageIndex, Ice.Current __current);

    String getObjectiveSettingsObjective(int imageIndex, Ice.Current __current);

    float getObjectiveSettingsRefractiveIndex(int imageIndex, Ice.Current __current);

    String getPathD(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPathID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    boolean getPixelsBigEndian(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getPixelsDimensionOrder(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getPixelsID(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getPixelsPixelType(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeC(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeT(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeX(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeY(int imageIndex, int pixelsIndex, Ice.Current __current);

    int getPixelsSizeZ(int imageIndex, int pixelsIndex, Ice.Current __current);

    String getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    String getPlaneID(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    int getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    int getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    int getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    String getPlateColumnNamingConvention(int plateIndex, Ice.Current __current);

    String getPlateDescription(int plateIndex, Ice.Current __current);

    String getPlateExternalIdentifier(int plateIndex, Ice.Current __current);

    String getPlateID(int plateIndex, Ice.Current __current);

    String getPlateName(int plateIndex, Ice.Current __current);

    String getPlateRowNamingConvention(int plateIndex, Ice.Current __current);

    String getPlateStatus(int plateIndex, Ice.Current __current);

    double getPlateWellOriginX(int plateIndex, Ice.Current __current);

    double getPlateWellOriginY(int plateIndex, Ice.Current __current);

    String getPlateRefID(int screenIndex, int plateRefIndex, Ice.Current __current);

    int getPlateRefSample(int screenIndex, int plateRefIndex, Ice.Current __current);

    String getPlateRefWell(int screenIndex, int plateRefIndex, Ice.Current __current);

    String getPointCx(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPointCy(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPointID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPointR(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPointTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPolygonID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPolylineID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getProjectDescription(int projectIndex, Ice.Current __current);

    String getProjectExperimenterRef(int projectIndex, Ice.Current __current);

    String getProjectGroupRef(int projectIndex, Ice.Current __current);

    String getProjectID(int projectIndex, Ice.Current __current);

    String getProjectName(int projectIndex, Ice.Current __current);

    String getProjectRefID(int datasetIndex, int projectRefIndex, Ice.Current __current);

    String getPumpLightSource(int instrumentIndex, int lightSourceIndex, Ice.Current __current);

    String getROIID(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIT0(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIT1(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIX0(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIX1(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIY0(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIY1(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIZ0(int imageIndex, int roiIndex, Ice.Current __current);

    int getROIZ1(int imageIndex, int roiIndex, Ice.Current __current);

    String getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex, Ice.Current __current);

    String getReagentDescription(int screenIndex, int reagentIndex, Ice.Current __current);

    String getReagentID(int screenIndex, int reagentIndex, Ice.Current __current);

    String getReagentName(int screenIndex, int reagentIndex, Ice.Current __current);

    String getReagentReagentIdentifier(int screenIndex, int reagentIndex, Ice.Current __current);

    String getRectHeight(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getRectID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getRectTransform(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getRectWidth(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getRectX(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getRectY(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getRegionID(int imageIndex, int regionIndex, Ice.Current __current);

    String getRegionName(int imageIndex, int regionIndex, Ice.Current __current);

    String getRegionTag(int imageIndex, int regionIndex, Ice.Current __current);

    String getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex, Ice.Current __current);

    String getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex, Ice.Current __current);

    String getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex, Ice.Current __current);

    String getScreenDescription(int screenIndex, Ice.Current __current);

    String getScreenExtern(int screenIndex, Ice.Current __current);

    String getScreenID(int screenIndex, Ice.Current __current);

    String getScreenName(int screenIndex, Ice.Current __current);

    String getScreenProtocolDescription(int screenIndex, Ice.Current __current);

    String getScreenProtocolIdentifier(int screenIndex, Ice.Current __current);

    String getScreenReagentSetDescription(int screenIndex, Ice.Current __current);

    String getScreenReagentSetIdentifier(int screenIndex, Ice.Current __current);

    String getScreenType(int screenIndex, Ice.Current __current);

    String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex, Ice.Current __current);

    String getScreenRefID(int plateIndex, int screenRefIndex, Ice.Current __current);

    String getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeDirection(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeG(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeID(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    boolean getShapeLocked(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    float getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeText(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getShapeTheT(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    int getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    boolean getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex, Ice.Current __current);

    String getStageLabelName(int imageIndex, Ice.Current __current);

    float getStageLabelX(int imageIndex, Ice.Current __current);

    float getStageLabelY(int imageIndex, Ice.Current __current);

    float getStageLabelZ(int imageIndex, Ice.Current __current);

    float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current __current);

    String getThumbnailHref(int imageIndex, Ice.Current __current);

    String getThumbnailID(int imageIndex, Ice.Current __current);

    String getThumbnailMIMEtype(int imageIndex, Ice.Current __current);

    String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current __current);

    int getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex, Ice.Current __current);

    int getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex, Ice.Current __current);

    int getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex, Ice.Current __current);

    int getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex, Ice.Current __current);

    int getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex, Ice.Current __current);

    int getWellColumn(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellExternalDescription(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellExternalIdentifier(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellID(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellReagent(int plateIndex, int wellIndex, Ice.Current __current);

    int getWellRow(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellType(int plateIndex, int wellIndex, Ice.Current __current);

    String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current __current);

    String getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, Ice.Current __current);
}
