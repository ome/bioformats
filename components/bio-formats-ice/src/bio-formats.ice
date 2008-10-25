//
// bio-formats.ice
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via MetadataAutogen on Oct 24, 2008 8:37:23 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

[["java:package:loci.ice"]]

module formats {

  sequence<byte> ByteSeq;
  sequence<ByteSeq> ByteByteSeq;
  sequence<short> ShortSeq;
  sequence<ShortSeq> ShortShortSeq;
  sequence<int> IntSeq;
  sequence<string> StringSeq;

  interface MetadataRetrieve;
  interface MetadataStore;

  interface IFormatReader {
    void setId(string id);

    void setRetrieveAsStore(MetadataRetrieve* retrieve);
    //const int MUST_GROUP = 0;
    //const int CAN_GROUP = 1;
    //const int CANNOT_GROUP = 2;

    bool isThisType(string name, bool open);
    //bool isThisType2(ByteSeq block);
    int getImageCount();
    bool isRGB();
    int getSizeX();
    int getSizeY();
    int getSizeZ();
    int getSizeC();
    int getSizeT();
    int getPixelType();
    int getEffectiveSizeC();
    int getRGBChannelCount();
    bool isIndexed();
    bool isFalseColor();
    ByteByteSeq get8BitLookupTable();
    ShortShortSeq get16BitLookupTable();
    IntSeq getChannelDimLengths();
    StringSeq getChannelDimTypes();
    int getThumbSizeX();
    int getThumbSizeY();
    bool isLittleEndian();
    string getDimensionOrder();
    bool isOrderCertain();
    bool isInterleaved();
    bool isInterleavedSubC(int subC);
    //ByteSeq openBytes1(int no);
    ByteSeq openBytes(int no, int x, int y, int width, int height);
    //ByteSeq openBytes3(int no, ByteSeq buf);
    //ByteSeq openBytes4(int no, ByteSeq buf,
    //  int x, int y, int width, int height);
    ByteSeq openThumbBytes(int no);
    void closeFile(bool fileOnly);
    string getFormat();
    StringSeq getSuffixes();
    void close();
    int getSeriesCount();
    void setSeries(int no);
    int getSeries();
    void setNormalized(bool normalize);
    bool isNormalized();
    void setMetadataCollected(bool collect);
    bool isMetadataCollected();
    void setOriginalMetadataPopulated(bool populate);
    bool isOriginalMetadataPopulated();
    void setGroupFiles(bool group);
    bool isGroupFiles();
    bool isMetadataComplete();
    int fileGroupOption(string id);
    StringSeq getUsedFiles();
    string getCurrentFile();
    int getIndex(int z, int c, int t);
    IntSeq getZCTCoords(int index);
    //Object getMetadataValue(string field);
    //Hashtable getMetadata();
    //CoreMetadata getCoreMetadata();
    void setMetadataFiltered(bool filter);
    bool isMetadataFiltered();
    void setMetadataStore(MetadataStore* store);
    //MetadataStore* getMetadataStore();
    //Object getMetadataStoreRoot();
    //IFormatReader[] getUnderlyingReaders();
  };

  interface IFormatWriter {
    void setId(string id);
    void setStoreAsRetrieve(MetadataStore* store);
    void close();
    void saveBytes1(ByteSeq bytes, bool last);
    void saveBytes2(ByteSeq bytes, int series, bool lastInSeries, bool last);
    bool canDoStacks();
    void setMetadataRetrieve(MetadataRetrieve* r);
    MetadataRetrieve getMetadataRetrieve();
    void setFramesPerSecond(int rate);
    int getFramesPerSecond();
    StringSeq getCompressionTypes();
    IntSeq getPixelTypes();
    bool isSupportedType(int type);
    void setCompression(string compress);
    bool isThisType(string name);
    string getFormat();
    StringSeq getSuffixes();
  };

  interface MetadataStore {
    MetadataStore getServant();
    string getOMEXML();

    void createRoot();
    //void setRoot(Object root);
    //Object getRoot();

    // - Entity storage -

    void setUUID(string uuid);

    // - Arc property storage -
    void setArcType(string type, int instrumentIndex, int lightSourceIndex);

    // - ChannelComponent property storage -
    void setChannelComponentColorDomain(string colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    void setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    // - Detector property storage -
    void setDetectorGain(float gain, int instrumentIndex, int detectorIndex);
    void setDetectorID(string id, int instrumentIndex, int detectorIndex);
    void setDetectorManufacturer(string manufacturer, int instrumentIndex, int detectorIndex);
    void setDetectorModel(string model, int instrumentIndex, int detectorIndex);
    void setDetectorOffset(float offset, int instrumentIndex, int detectorIndex);
    void setDetectorSerialNumber(string serialNumber, int instrumentIndex, int detectorIndex);
    void setDetectorType(string type, int instrumentIndex, int detectorIndex);
    void setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex);

    // - DetectorSettings property storage -
    void setDetectorSettingsDetector(string detector, int imageIndex, int logicalChannelIndex);
    void setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex);
    void setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex);

    // - Dimensions property storage -
    void setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex);
    void setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex);
    void setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex);
    void setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex);
    void setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex);
    void setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex);

    // - DisplayOptions property storage -
    void setDisplayOptionsID(string id, int imageIndex);
    void setDisplayOptionsZoom(float zoom, int imageIndex);

    // - DisplayOptionsProjection property storage -
    void setDisplayOptionsProjectionZStart(int zStart, int imageIndex);
    void setDisplayOptionsProjectionZStop(int zStop, int imageIndex);

    // - DisplayOptionsTime property storage -
    void setDisplayOptionsTimeTStart(int tStart, int imageIndex);
    void setDisplayOptionsTimeTStop(int tStop, int imageIndex);

    // - Experiment property storage -
    void setExperimentDescription(string description, int experimentIndex);
    void setExperimentID(string id, int experimentIndex);
    void setExperimentType(string type, int experimentIndex);

    // - Experimenter property storage -
    void setExperimenterEmail(string email, int experimenterIndex);
    void setExperimenterFirstName(string firstName, int experimenterIndex);
    void setExperimenterID(string id, int experimenterIndex);
    void setExperimenterInstitution(string institution, int experimenterIndex);
    void setExperimenterLastName(string lastName, int experimenterIndex);

    // - ExperimenterMembership property storage -
    void setExperimenterMembershipGroup(string group, int experimenterIndex, int groupRefIndex);

    // - Filament property storage -
    void setFilamentType(string type, int instrumentIndex, int lightSourceIndex);

    // - GroupRef property storage -

    // - Image property storage -
    void setImageCreationDate(string creationDate, int imageIndex);
    void setImageDefaultPixels(string defaultPixels, int imageIndex);
    void setImageDescription(string description, int imageIndex);
    void setImageID(string id, int imageIndex);
    void setImageInstrumentRef(string instrumentRef, int imageIndex);
    void setImageName(string name, int imageIndex);

    // - ImagingEnvironment property storage -
    void setImagingEnvironmentAirPressure(float airPressure, int imageIndex);
    void setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex);
    void setImagingEnvironmentHumidity(float humidity, int imageIndex);
    void setImagingEnvironmentTemperature(float temperature, int imageIndex);

    // - Instrument property storage -
    void setInstrumentID(string id, int instrumentIndex);

    // - Laser property storage -
    void setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex);
    void setLaserLaserMedium(string laserMedium, int instrumentIndex, int lightSourceIndex);
    void setLaserPulse(string pulse, int instrumentIndex, int lightSourceIndex);
    void setLaserTuneable(bool tuneable, int instrumentIndex, int lightSourceIndex);
    void setLaserType(string type, int instrumentIndex, int lightSourceIndex);
    void setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex);

    // - LightSource property storage -
    void setLightSourceID(string id, int instrumentIndex, int lightSourceIndex);
    void setLightSourceManufacturer(string manufacturer, int instrumentIndex, int lightSourceIndex);
    void setLightSourceModel(string model, int instrumentIndex, int lightSourceIndex);
    void setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex);
    void setLightSourceSerialNumber(string serialNumber, int instrumentIndex, int lightSourceIndex);

    // - LightSourceSettings property storage -
    void setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex);
    void setLightSourceSettingsLightSource(string lightSource, int imageIndex, int logicalChannelIndex);
    void setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex);

    // - LogicalChannel property storage -
    void setLogicalChannelContrastMethod(string contrastMethod, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelFluor(string fluor, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelID(string id, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelIlluminationType(string illuminationType, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelMode(string mode, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelName(string name, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelOTF(string otf, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelPhotometricInterpretation(string photometricInterpretation, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex);
    void setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex);

    // - OTF property storage -
    void setOTFID(string id, int instrumentIndex, int otfIndex);
    void setOTFObjective(string objective, int instrumentIndex, int otfIndex);
    void setOTFOpticalAxisAveraged(bool opticalAxisAveraged, int instrumentIndex, int otfIndex);
    void setOTFPixelType(string pixelType, int instrumentIndex, int otfIndex);
    void setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex);
    void setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex);

    // - Objective property storage -
    void setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex);
    void setObjectiveCorrection(string correction, int instrumentIndex, int objectiveIndex);
    void setObjectiveID(string id, int instrumentIndex, int objectiveIndex);
    void setObjectiveImmersion(string immersion, int instrumentIndex, int objectiveIndex);
    void setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex);
    void setObjectiveManufacturer(string manufacturer, int instrumentIndex, int objectiveIndex);
    void setObjectiveModel(string model, int instrumentIndex, int objectiveIndex);
    void setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex);
    void setObjectiveSerialNumber(string serialNumber, int instrumentIndex, int objectiveIndex);
    void setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex);

    // - Pixels property storage -
    void setPixelsBigEndian(bool bigEndian, int imageIndex, int pixelsIndex);
    void setPixelsDimensionOrder(string dimensionOrder, int imageIndex, int pixelsIndex);
    void setPixelsID(string id, int imageIndex, int pixelsIndex);
    void setPixelsPixelType(string pixelType, int imageIndex, int pixelsIndex);
    void setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex);
    void setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex);
    void setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex);
    void setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex);
    void setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex);

    // - Plane property storage -
    void setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex);
    void setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex);
    void setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex);

    // - PlaneTiming property storage -
    void setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex);
    void setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex);

    // - Plate property storage -
    void setPlateDescription(string description, int plateIndex);
    void setPlateExternalIdentifier(string externalIdentifier, int plateIndex);
    void setPlateID(string id, int plateIndex);
    void setPlateName(string name, int plateIndex);
    void setPlateStatus(string status, int plateIndex);

    // - PlateRef property storage -
    void setPlateRefID(string id, int screenIndex, int plateRefIndex);

    // - ROI property storage -
    void setROIID(string id, int imageIndex, int roiIndex);
    void setROIT0(int t0, int imageIndex, int roiIndex);
    void setROIT1(int t1, int imageIndex, int roiIndex);
    void setROIX0(int x0, int imageIndex, int roiIndex);
    void setROIX1(int x1, int imageIndex, int roiIndex);
    void setROIY0(int y0, int imageIndex, int roiIndex);
    void setROIY1(int y1, int imageIndex, int roiIndex);
    void setROIZ0(int z0, int imageIndex, int roiIndex);
    void setROIZ1(int z1, int imageIndex, int roiIndex);

    // - Reagent property storage -
    void setReagentDescription(string description, int screenIndex, int reagentIndex);
    void setReagentID(string id, int screenIndex, int reagentIndex);
    void setReagentName(string name, int screenIndex, int reagentIndex);
    void setReagentReagentIdentifier(string reagentIdentifier, int screenIndex, int reagentIndex);

    // - Screen property storage -
    void setScreenID(string id, int screenIndex);
    void setScreenName(string name, int screenIndex);
    void setScreenProtocolDescription(string protocolDescription, int screenIndex);
    void setScreenProtocolIdentifier(string protocolIdentifier, int screenIndex);
    void setScreenReagentSetDescription(string reagentSetDescription, int screenIndex);
    void setScreenType(string type, int screenIndex);

    // - ScreenAcquisition property storage -
    void setScreenAcquisitionEndTime(string endTime, int screenIndex, int screenAcquisitionIndex);
    void setScreenAcquisitionID(string id, int screenIndex, int screenAcquisitionIndex);
    void setScreenAcquisitionStartTime(string startTime, int screenIndex, int screenAcquisitionIndex);

    // - StageLabel property storage -
    void setStageLabelName(string name, int imageIndex);
    void setStageLabelX(float x, int imageIndex);
    void setStageLabelY(float y, int imageIndex);
    void setStageLabelZ(float z, int imageIndex);

    // - StagePosition property storage -
    void setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex);
    void setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex);
    void setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex);

    // - TiffData property storage -
    void setTiffDataFileName(string fileName, int imageIndex, int pixelsIndex, int tiffDataIndex);
    void setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex);
    void setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex);
    void setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex);
    void setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex);
    void setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex);
    void setTiffDataUUID(string uuid, int imageIndex, int pixelsIndex, int tiffDataIndex);

    // - Well property storage -
    void setWellColumn(int column, int plateIndex, int wellIndex);
    void setWellExternalDescription(string externalDescription, int plateIndex, int wellIndex);
    void setWellExternalIdentifier(string externalIdentifier, int plateIndex, int wellIndex);
    void setWellID(string id, int plateIndex, int wellIndex);
    void setWellRow(int row, int plateIndex, int wellIndex);
    void setWellType(string type, int plateIndex, int wellIndex);

    // - WellSample property storage -
    void setWellSampleID(string id, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex);
    void setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex);
  };

  interface MetadataRetrieve {
    MetadataRetrieve getServant();
    string getOMEXML();

    // - Entity counting -

    int getChannelComponentCount(int imageIndex, int logicalChannelIndex);
    int getDetectorCount(int instrumentIndex);
    int getExperimentCount();
    int getExperimenterCount();
    int getExperimenterMembershipCount(int experimenterIndex);
    int getGroupRefCount(int experimenterIndex);
    int getImageCount();
    int getInstrumentCount();
    int getLightSourceCount(int instrumentIndex);
    int getLogicalChannelCount(int imageIndex);
    int getOTFCount(int instrumentIndex);
    int getObjectiveCount(int instrumentIndex);
    int getPixelsCount(int imageIndex);
    int getPlaneCount(int imageIndex, int pixelsIndex);
    int getPlateCount();
    int getPlateRefCount(int screenIndex);
    int getROICount(int imageIndex);
    int getReagentCount(int screenIndex);
    int getScreenCount();
    int getScreenAcquisitionCount(int screenIndex);
    int getTiffDataCount(int imageIndex, int pixelsIndex);
    int getWellCount(int plateIndex);
    int getWellSampleCount(int plateIndex, int wellIndex);

    // - Entity retrieval -

    string getUUID();

    // - Arc property retrieval -

    string getArcType(int instrumentIndex, int lightSourceIndex);

    // - ChannelComponent property retrieval -

    string getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex);
    int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex);

    // - Detector property retrieval -

    float getDetectorGain(int instrumentIndex, int detectorIndex);
    string getDetectorID(int instrumentIndex, int detectorIndex);
    string getDetectorManufacturer(int instrumentIndex, int detectorIndex);
    string getDetectorModel(int instrumentIndex, int detectorIndex);
    float getDetectorOffset(int instrumentIndex, int detectorIndex);
    string getDetectorSerialNumber(int instrumentIndex, int detectorIndex);
    string getDetectorType(int instrumentIndex, int detectorIndex);
    float getDetectorVoltage(int instrumentIndex, int detectorIndex);

    // - DetectorSettings property retrieval -

    string getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex);
    float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex);
    float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex);

    // - Dimensions property retrieval -

    float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex);
    float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex);
    float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex);
    float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex);
    int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex);
    int getDimensionsWaveStart(int imageIndex, int pixelsIndex);

    // - DisplayOptions property retrieval -

    string getDisplayOptionsID(int imageIndex);
    float getDisplayOptionsZoom(int imageIndex);

    // - DisplayOptionsProjection property retrieval -

    int getDisplayOptionsProjectionZStart(int imageIndex);
    int getDisplayOptionsProjectionZStop(int imageIndex);

    // - DisplayOptionsTime property retrieval -

    int getDisplayOptionsTimeTStart(int imageIndex);
    int getDisplayOptionsTimeTStop(int imageIndex);

    // - Experiment property retrieval -

    string getExperimentDescription(int experimentIndex);
    string getExperimentID(int experimentIndex);
    string getExperimentType(int experimentIndex);

    // - Experimenter property retrieval -

    string getExperimenterEmail(int experimenterIndex);
    string getExperimenterFirstName(int experimenterIndex);
    string getExperimenterID(int experimenterIndex);
    string getExperimenterInstitution(int experimenterIndex);
    string getExperimenterLastName(int experimenterIndex);

    // - ExperimenterMembership property retrieval -

    string getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex);

    // - Filament property retrieval -

    string getFilamentType(int instrumentIndex, int lightSourceIndex);

    // - GroupRef property retrieval -


    // - Image property retrieval -

    string getImageCreationDate(int imageIndex);
    string getImageDefaultPixels(int imageIndex);
    string getImageDescription(int imageIndex);
    string getImageID(int imageIndex);
    string getImageInstrumentRef(int imageIndex);
    string getImageName(int imageIndex);

    // - ImagingEnvironment property retrieval -

    float getImagingEnvironmentAirPressure(int imageIndex);
    float getImagingEnvironmentCO2Percent(int imageIndex);
    float getImagingEnvironmentHumidity(int imageIndex);
    float getImagingEnvironmentTemperature(int imageIndex);

    // - Instrument property retrieval -

    string getInstrumentID(int instrumentIndex);

    // - Laser property retrieval -

    int getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex);
    string getLaserLaserMedium(int instrumentIndex, int lightSourceIndex);
    string getLaserPulse(int instrumentIndex, int lightSourceIndex);
    bool getLaserTuneable(int instrumentIndex, int lightSourceIndex);
    string getLaserType(int instrumentIndex, int lightSourceIndex);
    int getLaserWavelength(int instrumentIndex, int lightSourceIndex);

    // - LightSource property retrieval -

    string getLightSourceID(int instrumentIndex, int lightSourceIndex);
    string getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex);
    string getLightSourceModel(int instrumentIndex, int lightSourceIndex);
    float getLightSourcePower(int instrumentIndex, int lightSourceIndex);
    string getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex);

    // - LightSourceSettings property retrieval -

    float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex);
    string getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex);
    int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex);

    // - LogicalChannel property retrieval -

    string getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex);
    int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex);
    int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelFluor(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelID(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelMode(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelName(int imageIndex, int logicalChannelIndex);
    float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelOTF(int imageIndex, int logicalChannelIndex);
    string getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex);
    float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex);
    int getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex);
    int getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex);

    // - OTF property retrieval -

    string getOTFID(int instrumentIndex, int otfIndex);
    string getOTFObjective(int instrumentIndex, int otfIndex);
    bool getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex);
    string getOTFPixelType(int instrumentIndex, int otfIndex);
    int getOTFSizeX(int instrumentIndex, int otfIndex);
    int getOTFSizeY(int instrumentIndex, int otfIndex);

    // - Objective property retrieval -

    float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex);
    string getObjectiveCorrection(int instrumentIndex, int objectiveIndex);
    string getObjectiveID(int instrumentIndex, int objectiveIndex);
    string getObjectiveImmersion(int instrumentIndex, int objectiveIndex);
    float getObjectiveLensNA(int instrumentIndex, int objectiveIndex);
    string getObjectiveManufacturer(int instrumentIndex, int objectiveIndex);
    string getObjectiveModel(int instrumentIndex, int objectiveIndex);
    int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex);
    string getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex);
    float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex);

    // - Pixels property retrieval -

    bool getPixelsBigEndian(int imageIndex, int pixelsIndex);
    string getPixelsDimensionOrder(int imageIndex, int pixelsIndex);
    string getPixelsID(int imageIndex, int pixelsIndex);
    string getPixelsPixelType(int imageIndex, int pixelsIndex);
    int getPixelsSizeC(int imageIndex, int pixelsIndex);
    int getPixelsSizeT(int imageIndex, int pixelsIndex);
    int getPixelsSizeX(int imageIndex, int pixelsIndex);
    int getPixelsSizeY(int imageIndex, int pixelsIndex);
    int getPixelsSizeZ(int imageIndex, int pixelsIndex);

    // - Plane property retrieval -

    int getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex);
    int getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex);
    int getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex);

    // - PlaneTiming property retrieval -

    float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex);
    float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex);

    // - Plate property retrieval -

    string getPlateDescription(int plateIndex);
    string getPlateExternalIdentifier(int plateIndex);
    string getPlateID(int plateIndex);
    string getPlateName(int plateIndex);
    string getPlateStatus(int plateIndex);

    // - PlateRef property retrieval -

    string getPlateRefID(int screenIndex, int plateRefIndex);

    // - ROI property retrieval -

    string getROIID(int imageIndex, int roiIndex);
    int getROIT0(int imageIndex, int roiIndex);
    int getROIT1(int imageIndex, int roiIndex);
    int getROIX0(int imageIndex, int roiIndex);
    int getROIX1(int imageIndex, int roiIndex);
    int getROIY0(int imageIndex, int roiIndex);
    int getROIY1(int imageIndex, int roiIndex);
    int getROIZ0(int imageIndex, int roiIndex);
    int getROIZ1(int imageIndex, int roiIndex);

    // - Reagent property retrieval -

    string getReagentDescription(int screenIndex, int reagentIndex);
    string getReagentID(int screenIndex, int reagentIndex);
    string getReagentName(int screenIndex, int reagentIndex);
    string getReagentReagentIdentifier(int screenIndex, int reagentIndex);

    // - Screen property retrieval -

    string getScreenID(int screenIndex);
    string getScreenName(int screenIndex);
    string getScreenProtocolDescription(int screenIndex);
    string getScreenProtocolIdentifier(int screenIndex);
    string getScreenReagentSetDescription(int screenIndex);
    string getScreenType(int screenIndex);

    // - ScreenAcquisition property retrieval -

    string getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex);
    string getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex);
    string getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex);

    // - StageLabel property retrieval -

    string getStageLabelName(int imageIndex);
    float getStageLabelX(int imageIndex);
    float getStageLabelY(int imageIndex);
    float getStageLabelZ(int imageIndex);

    // - StagePosition property retrieval -

    float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex);
    float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex);
    float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex);

    // - TiffData property retrieval -

    string getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex);
    int getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex);
    int getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex);
    int getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex);
    int getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex);
    int getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex);
    string getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex);

    // - Well property retrieval -

    int getWellColumn(int plateIndex, int wellIndex);
    string getWellExternalDescription(int plateIndex, int wellIndex);
    string getWellExternalIdentifier(int plateIndex, int wellIndex);
    string getWellID(int plateIndex, int wellIndex);
    int getWellRow(int plateIndex, int wellIndex);
    string getWellType(int plateIndex, int wellIndex);

    // - WellSample property retrieval -

    string getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex);
    int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex);
    float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex);
    float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex);
    int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);
  };
};
