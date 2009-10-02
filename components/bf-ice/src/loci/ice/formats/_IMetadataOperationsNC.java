// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.1

package loci.ice.formats;

public interface _IMetadataOperationsNC
{
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

    byte[] getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex);

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

    void createRoot();

    void setUUID(String uuid);

    void setArcType(String type, int instrumentIndex, int lightSourceIndex);

    void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    void setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    void setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex);

    void setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex);

    void setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex);

    void setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex);

    void setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

    void setContactExperimenter(String experimenter, int groupIndex);

    void setDatasetDescription(String description, int datasetIndex);

    void setDatasetExperimenterRef(String experimenterRef, int datasetIndex);

    void setDatasetGroupRef(String groupRef, int datasetIndex);

    void setDatasetID(String id, int datasetIndex);

    void setDatasetLocked(boolean locked, int datasetIndex);

    void setDatasetName(String name, int datasetIndex);

    void setDatasetRefID(String id, int imageIndex, int datasetRefIndex);

    void setDetectorAmplificationGain(float amplificationGain, int instrumentIndex, int detectorIndex);

    void setDetectorGain(float gain, int instrumentIndex, int detectorIndex);

    void setDetectorID(String id, int instrumentIndex, int detectorIndex);

    void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex);

    void setDetectorModel(String model, int instrumentIndex, int detectorIndex);

    void setDetectorOffset(float offset, int instrumentIndex, int detectorIndex);

    void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex);

    void setDetectorType(String type, int instrumentIndex, int detectorIndex);

    void setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex);

    void setDetectorZoom(float zoom, int instrumentIndex, int detectorIndex);

    void setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex);

    void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex);

    void setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex);

    void setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex);

    void setDetectorSettingsReadOutRate(float readOutRate, int imageIndex, int logicalChannelIndex);

    void setDetectorSettingsVoltage(float voltage, int imageIndex, int logicalChannelIndex);

    void setDichroicID(String id, int instrumentIndex, int dichroicIndex);

    void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex);

    void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex);

    void setDichroicModel(String model, int instrumentIndex, int dichroicIndex);

    void setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex);

    void setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex);

    void setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex);

    void setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex);

    void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex);

    void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex);

    void setDisplayOptionsDisplay(String display, int imageIndex);

    void setDisplayOptionsID(String id, int imageIndex);

    void setDisplayOptionsZoom(float zoom, int imageIndex);

    void setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex);

    void setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex);

    void setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex);

    void setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex);

    void setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex);

    void setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

    void setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex);

    void setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex);

    void setEmFilterModel(String model, int instrumentIndex, int filterIndex);

    void setEmFilterType(String type, int instrumentIndex, int filterIndex);

    void setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex);

    void setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex);

    void setExFilterModel(String model, int instrumentIndex, int filterIndex);

    void setExFilterType(String type, int instrumentIndex, int filterIndex);

    void setExperimentDescription(String description, int experimentIndex);

    void setExperimentExperimenterRef(String experimenterRef, int experimentIndex);

    void setExperimentID(String id, int experimentIndex);

    void setExperimentType(String type, int experimentIndex);

    void setExperimenterEmail(String email, int experimenterIndex);

    void setExperimenterFirstName(String firstName, int experimenterIndex);

    void setExperimenterID(String id, int experimenterIndex);

    void setExperimenterInstitution(String institution, int experimenterIndex);

    void setExperimenterLastName(String lastName, int experimenterIndex);

    void setExperimenterOMEName(String omeName, int experimenterIndex);

    void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex);

    void setFilamentType(String type, int instrumentIndex, int lightSourceIndex);

    void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex);

    void setFilterID(String id, int instrumentIndex, int filterIndex);

    void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex);

    void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex);

    void setFilterModel(String model, int instrumentIndex, int filterIndex);

    void setFilterType(String type, int instrumentIndex, int filterIndex);

    void setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex);

    void setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex);

    void setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex);

    void setFilterSetID(String id, int instrumentIndex, int filterSetIndex);

    void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex);

    void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex);

    void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex);

    void setGroupID(String id, int groupIndex);

    void setGroupName(String name, int groupIndex);

    void setImageAcquiredPixels(String acquiredPixels, int imageIndex);

    void setImageCreationDate(String creationDate, int imageIndex);

    void setImageDefaultPixels(String defaultPixels, int imageIndex);

    void setImageDescription(String description, int imageIndex);

    void setImageExperimentRef(String experimentRef, int imageIndex);

    void setImageExperimenterRef(String experimenterRef, int imageIndex);

    void setImageGroupRef(String groupRef, int imageIndex);

    void setImageID(String id, int imageIndex);

    void setImageInstrumentRef(String instrumentRef, int imageIndex);

    void setImageName(String name, int imageIndex);

    void setImagingEnvironmentAirPressure(float airPressure, int imageIndex);

    void setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex);

    void setImagingEnvironmentHumidity(float humidity, int imageIndex);

    void setImagingEnvironmentTemperature(float temperature, int imageIndex);

    void setInstrumentID(String id, int instrumentIndex);

    void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex);

    void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex);

    void setLaserPockelCell(boolean pockelCell, int instrumentIndex, int lightSourceIndex);

    void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex);

    void setLaserRepetitionRate(boolean repetitionRate, int instrumentIndex, int lightSourceIndex);

    void setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex);

    void setLaserType(String type, int instrumentIndex, int lightSourceIndex);

    void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex);

    void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex);

    void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

    void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex);

    void setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex);

    void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

    void setLightSourceRefAttenuation(float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

    void setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

    void setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

    void setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex);

    void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex);

    void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex);

    void setLineID(String id, int imageIndex, int roiIndex, int shapeIndex);

    void setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

    void setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex);

    void setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex);

    void setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex);

    void setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex);

    void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex);

    void setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex);

    void setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex);

    void setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex);

    void setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

    void setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex);

    void setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex);

    void setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex);

    void setMaskPixelsBigEndian(boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex);

    void setMaskPixelsBinData(byte[] binData, int imageIndex, int roiIndex, int shapeIndex);

    void setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex);

    void setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex);

    void setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex);

    void setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex);

    void setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex);

    void setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex);

    void setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex);

    void setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex);

    void setMicroscopeID(String id, int instrumentIndex);

    void setMicroscopeManufacturer(String manufacturer, int instrumentIndex);

    void setMicroscopeModel(String model, int instrumentIndex);

    void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex);

    void setMicroscopeType(String type, int instrumentIndex);

    void setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex);

    void setOTFID(String id, int instrumentIndex, int otfIndex);

    void setOTFObjective(String objective, int instrumentIndex, int otfIndex);

    void setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex);

    void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex);

    void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex);

    void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex);

    void setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex);

    void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex);

    void setObjectiveID(String id, int instrumentIndex, int objectiveIndex);

    void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex);

    void setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex);

    void setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex);

    void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex);

    void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex);

    void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex);

    void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex);

    void setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex);

    void setObjectiveSettingsCorrectionCollar(float correctionCollar, int imageIndex);

    void setObjectiveSettingsMedium(String medium, int imageIndex);

    void setObjectiveSettingsObjective(String objective, int imageIndex);

    void setObjectiveSettingsRefractiveIndex(float refractiveIndex, int imageIndex);

    void setPathD(String d, int imageIndex, int roiIndex, int shapeIndex);

    void setPathID(String id, int imageIndex, int roiIndex, int shapeIndex);

    void setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex);

    void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex);

    void setPixelsID(String id, int imageIndex, int pixelsIndex);

    void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex);

    void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex);

    void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex);

    void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex);

    void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex);

    void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex);

    void setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex);

    void setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex);

    void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex);

    void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex);

    void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex);

    void setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex);

    void setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex);

    void setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex);

    void setPlateDescription(String description, int plateIndex);

    void setPlateExternalIdentifier(String externalIdentifier, int plateIndex);

    void setPlateID(String id, int plateIndex);

    void setPlateName(String name, int plateIndex);

    void setPlateRowNamingConvention(String rowNamingConvention, int plateIndex);

    void setPlateStatus(String status, int plateIndex);

    void setPlateWellOriginX(double wellOriginX, int plateIndex);

    void setPlateWellOriginY(double wellOriginY, int plateIndex);

    void setPlateRefID(String id, int screenIndex, int plateRefIndex);

    void setPlateRefSample(int sample, int screenIndex, int plateRefIndex);

    void setPlateRefWell(String well, int screenIndex, int plateRefIndex);

    void setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex);

    void setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex);

    void setPointID(String id, int imageIndex, int roiIndex, int shapeIndex);

    void setPointR(String r, int imageIndex, int roiIndex, int shapeIndex);

    void setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

    void setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex);

    void setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex);

    void setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

    void setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex);

    void setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex);

    void setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

    void setProjectDescription(String description, int projectIndex);

    void setProjectExperimenterRef(String experimenterRef, int projectIndex);

    void setProjectGroupRef(String groupRef, int projectIndex);

    void setProjectID(String id, int projectIndex);

    void setProjectName(String name, int projectIndex);

    void setProjectRefID(String id, int datasetIndex, int projectRefIndex);

    void setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex);

    void setROIID(String id, int imageIndex, int roiIndex);

    void setROIT0(int t0, int imageIndex, int roiIndex);

    void setROIT1(int t1, int imageIndex, int roiIndex);

    void setROIX0(int x0, int imageIndex, int roiIndex);

    void setROIX1(int x1, int imageIndex, int roiIndex);

    void setROIY0(int y0, int imageIndex, int roiIndex);

    void setROIY1(int y1, int imageIndex, int roiIndex);

    void setROIZ0(int z0, int imageIndex, int roiIndex);

    void setROIZ1(int z1, int imageIndex, int roiIndex);

    void setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex);

    void setReagentDescription(String description, int screenIndex, int reagentIndex);

    void setReagentID(String id, int screenIndex, int reagentIndex);

    void setReagentName(String name, int screenIndex, int reagentIndex);

    void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex);

    void setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex);

    void setRectID(String id, int imageIndex, int roiIndex, int shapeIndex);

    void setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

    void setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex);

    void setRectX(String x, int imageIndex, int roiIndex, int shapeIndex);

    void setRectY(String y, int imageIndex, int roiIndex, int shapeIndex);

    void setRegionID(String id, int imageIndex, int regionIndex);

    void setRegionName(String name, int imageIndex, int regionIndex);

    void setRegionTag(String tag, int imageIndex, int regionIndex);

    void setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex);

    void setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex);

    void setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex);

    void setScreenDescription(String description, int screenIndex);

    void setScreenExtern(String extern, int screenIndex);

    void setScreenID(String id, int screenIndex);

    void setScreenName(String name, int screenIndex);

    void setScreenProtocolDescription(String protocolDescription, int screenIndex);

    void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex);

    void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex);

    void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex);

    void setScreenType(String type, int screenIndex);

    void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex);

    void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex);

    void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex);

    void setScreenRefID(String id, int plateIndex, int screenRefIndex);

    void setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeLocked(boolean locked, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeStrokeOpacity(float strokeOpacity, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeVisibility(boolean visibility, int imageIndex, int roiIndex, int shapeIndex);

    void setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex);

    void setStageLabelName(String name, int imageIndex);

    void setStageLabelX(float x, int imageIndex);

    void setStageLabelY(float y, int imageIndex);

    void setStageLabelZ(float z, int imageIndex);

    void setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex);

    void setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex);

    void setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex);

    void setThumbnailHref(String href, int imageIndex);

    void setThumbnailID(String id, int imageIndex);

    void setThumbnailMIMEtype(String mimEtype, int imageIndex);

    void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex);

    void setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex);

    void setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex);

    void setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex);

    void setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex);

    void setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex);

    void setWellColumn(int column, int plateIndex, int wellIndex);

    void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex);

    void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex);

    void setWellID(String id, int plateIndex, int wellIndex);

    void setWellReagent(String reagent, int plateIndex, int wellIndex);

    void setWellRow(int row, int plateIndex, int wellIndex);

    void setWellType(String type, int plateIndex, int wellIndex);

    void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex);

    void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex);

    void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex);

    void setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex);

    void setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex);

    void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex);

    void setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex);
}
