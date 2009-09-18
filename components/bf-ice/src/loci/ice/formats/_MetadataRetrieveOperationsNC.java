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

public interface _MetadataRetrieveOperationsNC
{
    MetadataRetrieve getServant();

    String getOMEXML();

    int getChannelComponentCount(int imageIndex, int logicalChannelIndex);

    int getDatasetCount();

    int getDatasetRefCount(int imageIndex);

    int getDetectorCount(int instrumentIndex);

    int getDichroicCount(int instrumentIndex);

    int getExperimentCount();

    int getExperimenterCount();

    int getExperimenterMembershipCount(int experimenterIndex);

    int getFilterCount(int instrumentIndex);

    int getFilterSetCount(int instrumentIndex);

    int getGroupCount();

    int getGroupRefCount(int experimenterIndex);

    int getImageCount();

    int getInstrumentCount();

    int getLightSourceCount(int instrumentIndex);

    int getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex);

    int getLogicalChannelCount(int imageIndex);

    int getMicrobeamManipulationCount(int imageIndex);

    int getMicrobeamManipulationRefCount(int experimentIndex);

    int getOTFCount(int instrumentIndex);

    int getObjectiveCount(int instrumentIndex);

    int getPixelsCount(int imageIndex);

    int getPlaneCount(int imageIndex, int pixelsIndex);

    int getPlateCount();

    int getPlateRefCount(int screenIndex);

    int getProjectCount();

    int getProjectRefCount(int datasetIndex);

    int getROICount(int imageIndex);

    int getROIRefCount(int imageIndex, int microbeamManipulationIndex);

    int getReagentCount(int screenIndex);

    int getRegionCount(int imageIndex);

    int getRoiLinkCount(int imageIndex, int roiIndex);

    int getScreenCount();

    int getScreenAcquisitionCount(int screenIndex);

    int getScreenRefCount(int plateIndex);

    int getShapeCount(int imageIndex, int roiIndex);

    int getTiffDataCount(int imageIndex, int pixelsIndex);

    int getWellCount(int plateIndex);

    int getWellSampleCount(int plateIndex, int wellIndex);

    int getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex);

    String getUUID();

    String getArcType(int instrumentIndex, int lightSourceIndex);

    String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    String getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    String getCircleCx(int imageIndex, int roiIndex, int shapeIndex);

    String getCircleCy(int imageIndex, int roiIndex, int shapeIndex);

    String getCircleID(int imageIndex, int roiIndex, int shapeIndex);

    String getCircleR(int imageIndex, int roiIndex, int shapeIndex);

    String getCircleTransform(int imageIndex, int roiIndex, int shapeIndex);

    String getContactExperimenter(int groupIndex);

    String getDatasetDescription(int datasetIndex);

    String getDatasetExperimenterRef(int datasetIndex);

    String getDatasetGroupRef(int datasetIndex);

    String getDatasetID(int datasetIndex);

    boolean getDatasetLocked(int datasetIndex);

    String getDatasetName(int datasetIndex);

    String getDatasetRefID(int imageIndex, int datasetRefIndex);

    float getDetectorAmplificationGain(int instrumentIndex, int detectorIndex);

    float getDetectorGain(int instrumentIndex, int detectorIndex);

    String getDetectorID(int instrumentIndex, int detectorIndex);

    String getDetectorManufacturer(int instrumentIndex, int detectorIndex);

    String getDetectorModel(int instrumentIndex, int detectorIndex);

    float getDetectorOffset(int instrumentIndex, int detectorIndex);

    String getDetectorSerialNumber(int instrumentIndex, int detectorIndex);

    String getDetectorType(int instrumentIndex, int detectorIndex);

    float getDetectorVoltage(int instrumentIndex, int detectorIndex);

    float getDetectorZoom(int instrumentIndex, int detectorIndex);

    String getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex);

    String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex);

    float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex);

    float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex);

    float getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex);

    float getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex);

    String getDichroicID(int instrumentIndex, int dichroicIndex);

    String getDichroicLotNumber(int instrumentIndex, int dichroicIndex);

    String getDichroicManufacturer(int instrumentIndex, int dichroicIndex);

    String getDichroicModel(int instrumentIndex, int dichroicIndex);

    float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex);

    float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex);

    float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex);

    float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex);

    int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex);

    int getDimensionsWaveStart(int imageIndex, int pixelsIndex);

    String getDisplayOptionsDisplay(int imageIndex);

    String getDisplayOptionsID(int imageIndex);

    float getDisplayOptionsZoom(int imageIndex);

    String getEllipseCx(int imageIndex, int roiIndex, int shapeIndex);

    String getEllipseCy(int imageIndex, int roiIndex, int shapeIndex);

    String getEllipseID(int imageIndex, int roiIndex, int shapeIndex);

    String getEllipseRx(int imageIndex, int roiIndex, int shapeIndex);

    String getEllipseRy(int imageIndex, int roiIndex, int shapeIndex);

    String getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex);

    String getEmFilterLotNumber(int instrumentIndex, int filterIndex);

    String getEmFilterManufacturer(int instrumentIndex, int filterIndex);

    String getEmFilterModel(int instrumentIndex, int filterIndex);

    String getEmFilterType(int instrumentIndex, int filterIndex);

    String getExFilterLotNumber(int instrumentIndex, int filterIndex);

    String getExFilterManufacturer(int instrumentIndex, int filterIndex);

    String getExFilterModel(int instrumentIndex, int filterIndex);

    String getExFilterType(int instrumentIndex, int filterIndex);

    String getExperimentDescription(int experimentIndex);

    String getExperimentExperimenterRef(int experimentIndex);

    String getExperimentID(int experimentIndex);

    String getExperimentType(int experimentIndex);

    String getExperimenterEmail(int experimenterIndex);

    String getExperimenterFirstName(int experimenterIndex);

    String getExperimenterID(int experimenterIndex);

    String getExperimenterInstitution(int experimenterIndex);

    String getExperimenterLastName(int experimenterIndex);

    String getExperimenterOMEName(int experimenterIndex);

    String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex);

    String getFilamentType(int instrumentIndex, int lightSourceIndex);

    String getFilterFilterWheel(int instrumentIndex, int filterIndex);

    String getFilterID(int instrumentIndex, int filterIndex);

    String getFilterLotNumber(int instrumentIndex, int filterIndex);

    String getFilterManufacturer(int instrumentIndex, int filterIndex);

    String getFilterModel(int instrumentIndex, int filterIndex);

    String getFilterType(int instrumentIndex, int filterIndex);

    String getFilterSetDichroic(int instrumentIndex, int filterSetIndex);

    String getFilterSetEmFilter(int instrumentIndex, int filterSetIndex);

    String getFilterSetExFilter(int instrumentIndex, int filterSetIndex);

    String getFilterSetID(int instrumentIndex, int filterSetIndex);

    String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex);

    String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex);

    String getFilterSetModel(int instrumentIndex, int filterSetIndex);

    String getGroupID(int groupIndex);

    String getGroupName(int groupIndex);

    String getImageAcquiredPixels(int imageIndex);

    String getImageCreationDate(int imageIndex);

    String getImageDefaultPixels(int imageIndex);

    String getImageDescription(int imageIndex);

    String getImageExperimentRef(int imageIndex);

    String getImageExperimenterRef(int imageIndex);

    String getImageGroupRef(int imageIndex);

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

    boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex);

    String getLaserPulse(int instrumentIndex, int lightSourceIndex);

    boolean getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex);

    boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex);

    String getLaserType(int instrumentIndex, int lightSourceIndex);

    int getLaserWavelength(int instrumentIndex, int lightSourceIndex);

    String getLightSourceID(int instrumentIndex, int lightSourceIndex);

    String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex);

    String getLightSourceModel(int instrumentIndex, int lightSourceIndex);

    float getLightSourcePower(int instrumentIndex, int lightSourceIndex);

    String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex);

    float getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

    String getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

    int getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

    float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex);

    String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex);

    int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex);

    String getLineID(int imageIndex, int roiIndex, int shapeIndex);

    String getLineTransform(int imageIndex, int roiIndex, int shapeIndex);

    String getLineX1(int imageIndex, int roiIndex, int shapeIndex);

    String getLineX2(int imageIndex, int roiIndex, int shapeIndex);

    String getLineY1(int imageIndex, int roiIndex, int shapeIndex);

    String getLineY2(int imageIndex, int roiIndex, int shapeIndex);

    String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelDetector(int imageIndex, int logicalChannelIndex);

    int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex);

    int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelID(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelMode(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelName(int imageIndex, int logicalChannelIndex);

    float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex);

    float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex);

    int getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex);

    int getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex);

    String getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex);

    String getMaskHeight(int imageIndex, int roiIndex, int shapeIndex);

    String getMaskID(int imageIndex, int roiIndex, int shapeIndex);

    String getMaskTransform(int imageIndex, int roiIndex, int shapeIndex);

    String getMaskWidth(int imageIndex, int roiIndex, int shapeIndex);

    String getMaskX(int imageIndex, int roiIndex, int shapeIndex);

    String getMaskY(int imageIndex, int roiIndex, int shapeIndex);

    boolean getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex);

    String getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex);

    String getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex);

    String getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex);

    int getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex);

    int getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex);

    String getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex);

    String getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex);

    String getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex);

    String getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex);

    String getMicroscopeID(int instrumentIndex);

    String getMicroscopeManufacturer(int instrumentIndex);

    String getMicroscopeModel(int instrumentIndex);

    String getMicroscopeSerialNumber(int instrumentIndex);

    String getMicroscopeType(int instrumentIndex);

    String getOTFBinaryFile(int instrumentIndex, int otfIndex);

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

    boolean getObjectiveIris(int instrumentIndex, int objectiveIndex);

    float getObjectiveLensNA(int instrumentIndex, int objectiveIndex);

    String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex);

    String getObjectiveModel(int instrumentIndex, int objectiveIndex);

    int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex);

    String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex);

    float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex);

    float getObjectiveSettingsCorrectionCollar(int imageIndex);

    String getObjectiveSettingsMedium(int imageIndex);

    String getObjectiveSettingsObjective(int imageIndex);

    float getObjectiveSettingsRefractiveIndex(int imageIndex);

    String getPathD(int imageIndex, int roiIndex, int shapeIndex);

    String getPathID(int imageIndex, int roiIndex, int shapeIndex);

    boolean getPixelsBigEndian(int imageIndex, int pixelsIndex);

    String getPixelsDimensionOrder(int imageIndex, int pixelsIndex);

    String getPixelsID(int imageIndex, int pixelsIndex);

    String getPixelsPixelType(int imageIndex, int pixelsIndex);

    int getPixelsSizeC(int imageIndex, int pixelsIndex);

    int getPixelsSizeT(int imageIndex, int pixelsIndex);

    int getPixelsSizeX(int imageIndex, int pixelsIndex);

    int getPixelsSizeY(int imageIndex, int pixelsIndex);

    int getPixelsSizeZ(int imageIndex, int pixelsIndex);

    String getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex);

    String getPlaneID(int imageIndex, int pixelsIndex, int planeIndex);

    int getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex);

    int getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex);

    int getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex);

    float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex);

    float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex);

    String getPlateColumnNamingConvention(int plateIndex);

    String getPlateDescription(int plateIndex);

    String getPlateExternalIdentifier(int plateIndex);

    String getPlateID(int plateIndex);

    String getPlateName(int plateIndex);

    String getPlateRowNamingConvention(int plateIndex);

    String getPlateStatus(int plateIndex);

    double getPlateWellOriginX(int plateIndex);

    double getPlateWellOriginY(int plateIndex);

    String getPlateRefID(int screenIndex, int plateRefIndex);

    int getPlateRefSample(int screenIndex, int plateRefIndex);

    String getPlateRefWell(int screenIndex, int plateRefIndex);

    String getPointCx(int imageIndex, int roiIndex, int shapeIndex);

    String getPointCy(int imageIndex, int roiIndex, int shapeIndex);

    String getPointID(int imageIndex, int roiIndex, int shapeIndex);

    String getPointR(int imageIndex, int roiIndex, int shapeIndex);

    String getPointTransform(int imageIndex, int roiIndex, int shapeIndex);

    String getPolygonID(int imageIndex, int roiIndex, int shapeIndex);

    String getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex);

    String getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex);

    String getPolylineID(int imageIndex, int roiIndex, int shapeIndex);

    String getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex);

    String getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex);

    String getProjectDescription(int projectIndex);

    String getProjectExperimenterRef(int projectIndex);

    String getProjectGroupRef(int projectIndex);

    String getProjectID(int projectIndex);

    String getProjectName(int projectIndex);

    String getProjectRefID(int datasetIndex, int projectRefIndex);

    String getPumpLightSource(int instrumentIndex, int lightSourceIndex);

    String getROIID(int imageIndex, int roiIndex);

    int getROIT0(int imageIndex, int roiIndex);

    int getROIT1(int imageIndex, int roiIndex);

    int getROIX0(int imageIndex, int roiIndex);

    int getROIX1(int imageIndex, int roiIndex);

    int getROIY0(int imageIndex, int roiIndex);

    int getROIY1(int imageIndex, int roiIndex);

    int getROIZ0(int imageIndex, int roiIndex);

    int getROIZ1(int imageIndex, int roiIndex);

    String getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex);

    String getReagentDescription(int screenIndex, int reagentIndex);

    String getReagentID(int screenIndex, int reagentIndex);

    String getReagentName(int screenIndex, int reagentIndex);

    String getReagentReagentIdentifier(int screenIndex, int reagentIndex);

    String getRectHeight(int imageIndex, int roiIndex, int shapeIndex);

    String getRectID(int imageIndex, int roiIndex, int shapeIndex);

    String getRectTransform(int imageIndex, int roiIndex, int shapeIndex);

    String getRectWidth(int imageIndex, int roiIndex, int shapeIndex);

    String getRectX(int imageIndex, int roiIndex, int shapeIndex);

    String getRectY(int imageIndex, int roiIndex, int shapeIndex);

    String getRegionID(int imageIndex, int regionIndex);

    String getRegionName(int imageIndex, int regionIndex);

    String getRegionTag(int imageIndex, int regionIndex);

    String getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex);

    String getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex);

    String getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex);

    String getScreenDescription(int screenIndex);

    String getScreenExtern(int screenIndex);

    String getScreenID(int screenIndex);

    String getScreenName(int screenIndex);

    String getScreenProtocolDescription(int screenIndex);

    String getScreenProtocolIdentifier(int screenIndex);

    String getScreenReagentSetDescription(int screenIndex);

    String getScreenReagentSetIdentifier(int screenIndex);

    String getScreenType(int screenIndex);

    String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex);

    String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex);

    String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex);

    String getScreenRefID(int plateIndex, int screenRefIndex);

    String getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeDirection(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex);

    int getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeG(int imageIndex, int roiIndex, int shapeIndex);

    int getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeID(int imageIndex, int roiIndex, int shapeIndex);

    boolean getShapeLocked(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex);

    int getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex);

    float getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex);

    int getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeText(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex);

    int getShapeTheT(int imageIndex, int roiIndex, int shapeIndex);

    int getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex);

    boolean getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex);

    String getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex);

    String getStageLabelName(int imageIndex);

    float getStageLabelX(int imageIndex);

    float getStageLabelY(int imageIndex);

    float getStageLabelZ(int imageIndex);

    float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex);

    float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex);

    float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex);

    String getThumbnailHref(int imageIndex);

    String getThumbnailID(int imageIndex);

    String getThumbnailMIMEtype(int imageIndex);

    String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex);

    int getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex);

    int getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex);

    int getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex);

    int getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex);

    int getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex);

    String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex);

    int getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex);

    int getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex);

    int getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex);

    int getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex);

    int getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex);

    int getWellColumn(int plateIndex, int wellIndex);

    String getWellExternalDescription(int plateIndex, int wellIndex);

    String getWellExternalIdentifier(int plateIndex, int wellIndex);

    String getWellID(int plateIndex, int wellIndex);

    String getWellReagent(int plateIndex, int wellIndex);

    int getWellRow(int plateIndex, int wellIndex);

    String getWellType(int plateIndex, int wellIndex);

    String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex);

    String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex);

    int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex);

    float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex);

    float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex);

    int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);

    String getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex);
}
