package loci.formats.in.LeicaMicrosystemsMetadata;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.common.DateTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.LMSFileReader.ImageFormat;
import loci.formats.in.LeicaMicrosystemsMetadata.MetadataTempBuffer.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.LMSImageXmlDocument;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.ChannelExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.DetectorExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.DimensionExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.FilterExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.LaserExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.PositionExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.ROIExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.TimestampExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.Tuple;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Filter;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.LaserSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ROI;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension.DimensionKey;
import loci.formats.in.LeicaMicrosystemsMetadata.writeCore.DimensionWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.writeOME.DetectorWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.writeOME.FilterWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.writeOME.LaserWriter;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.xml.model.enums.MicroscopeType;
import ome.xml.model.primitives.Timestamp;

public class Translator {
  private static final long METER_MULTIPLY = 1000000;

  //XML nodes
  Element imageNode;
  Element imageDescription;
  Element hardwareSetting;
  Element mainConfocalSetting;
  Element mainCameraSetting;
  List<Element> sequentialConfocalSettings = new ArrayList<Element>();
  List<Element> sequentialCameraSettings = new ArrayList<Element>();
  List<Element> widefieldChannelInfos = new ArrayList<Element>();

  //extracted data
  DimensionStore dimensionStore = new DimensionStore();
  List<Laser> lasers = new ArrayList<Laser>();
  List<LaserSetting> laserSettings = new ArrayList<LaserSetting>();
  List<Detector> detectors = new ArrayList<Detector>();
  List<DetectorSetting> detectorSettings = new ArrayList<DetectorSetting>();
  List<Filter> filters = new ArrayList<Filter>();
  List<ROI> rois = new ArrayList<ROI>();
  List<ROI> singleRois = new ArrayList<ROI>();
  List<Double> timestamps = new ArrayList<Double>();

  String imageName;
  String description;
  boolean alternateCenter = false;
  DataSourceType dataSourceType;
  int imageCount;
  boolean inverseRgb;
  boolean useOldPhysicalSizeCalculation;
  ImageFormat imageFormat;
  int extras = 1;

  int seriesIndex;
  LMSFileReader reader;
  MetadataStore store;
  CoreMetadata core;

  public Translator(LMSImageXmlDocument doc, int seriesIndex, int imageCount, LMSFileReader reader){
    this.imageNode = (Element)doc.getImageNode();
    this.imageName = doc.getImageName();
    this.reader = reader;
    reader.setSeries(seriesIndex);
    reader.addSeriesMeta("Image name", imageName);

    this.seriesIndex = seriesIndex;
    this.store = reader.getMetadataStore();
    this.core = reader.getCore().get(seriesIndex);
    this.imageCount = imageCount;
    this.useOldPhysicalSizeCalculation = reader.useOldPhysicalSizeCalculation();
  }

  public void translate(){
    getMainNodes();

    //image metadata
    translateImageDetails();
    translateChannels();
    translateDimensions();
    translatePhysicalSizes();
    translateFieldPositions();
    translateTimestamps();
    translateExposureTimes();

    //instrument metadata
    translateStandDetails();
    translateObjective();
    translateLasers();
    translateDetectors();
    translateDetectorSettings();
    translateFilters(); //checks for detector settings
    translateFilterSettings();
    translateLaserSettings(); //checks for channel filter

    //translateChannelNames();

    translateROIs();
  }

  private void getMainNodes(){
    imageDescription = (Element) imageNode.getElementsByTagName("ImageDescription").item(0);

    NodeList attachments = Extractor.getDescendantNodesWithName(imageNode, "Attachment");
    hardwareSetting = (Element)Extractor.getNodeWithAttribute(attachments, "Name", "HardwareSetting");
    if (hardwareSetting == null) return;

    String dataSourceTypeS = Extractor.getAttributeValue(hardwareSetting, "DataSourceTypeName");
    dataSourceType = dataSourceTypeS.equals("Confocal") ? DataSourceType.CONFOCAL : DataSourceType.CAMERA;

    if (dataSourceType == DataSourceType.CONFOCAL){
      mainConfocalSetting = (Element)Extractor.getChildNodeWithName(hardwareSetting, "ATLConfocalSettingDefinition");
    
      Node ldmBlockSequential = Extractor.getChildNodeWithName(hardwareSetting, "LDM_Block_Sequential");
      if (ldmBlockSequential == null) return;
  
      Node ldmBlockList = Extractor.getChildNodeWithName(ldmBlockSequential, "LDM_Block_Sequential_List");
      NodeList sequentialConfocalSettings = ldmBlockList.getChildNodes();
  
      for (int i = 0; i < sequentialConfocalSettings.getLength(); i++){
        Element sequentialConfocalSetting;
        try {
          sequentialConfocalSetting = (Element) sequentialConfocalSettings.item(i);
          this.sequentialConfocalSettings.add(sequentialConfocalSetting);
        } catch (Exception e) {
          continue;
        }
      }
    } else {
      mainCameraSetting = (Element)Extractor.getChildNodeWithName(hardwareSetting, "ATLCameraSettingDefinition");

      Element ldmBlockWidefieldSequential = (Element)Extractor.getChildNodeWithName(hardwareSetting, "LDM_Block_Widefield_Sequential");

      Element ldmBlockSequentialList = (Element)Extractor.getChildNodeWithName(ldmBlockWidefieldSequential, "LDM_Block_Sequential_List");
      NodeList sequentialCameraSettings = ldmBlockSequentialList.getChildNodes();

      for (int channelIndex = 0; channelIndex < sequentialCameraSettings.getLength(); channelIndex++){
        Element sequentialCameraSetting = (Element)sequentialCameraSettings.item(channelIndex);
        this.sequentialCameraSettings.add(sequentialCameraSetting);
      }

      if (this.sequentialCameraSettings.size() > 0){
        //sequential camera settigns > config > info
        for (int channelIndex = 0; channelIndex < this.sequentialCameraSettings.size(); channelIndex++){
          Element sequentialCameraSetting = this.sequentialCameraSettings.get(channelIndex);
          Element widefieldChannelConfig = (Element)Extractor.getChildNodeWithName(sequentialCameraSetting, "WideFieldChannelConfigurator");
          Element widefieldChannelInfo = (Element)Extractor.getChildNodeWithName(widefieldChannelConfig, "WideFieldChannelInfo");
          this.widefieldChannelInfos.add(widefieldChannelInfo);
        }
      } else {
        //main camera setting > config > infos
        Element widefieldChannelConfig = (Element)Extractor.getChildNodeWithName(mainCameraSetting, "WideFieldChannelConfigurator");
        NodeList widefieldChannelInfos = Extractor.getDescendantNodesWithName(widefieldChannelConfig, "WideFieldChannelInfo");
        for (int channelIndex = 0; channelIndex < widefieldChannelInfos.getLength(); channelIndex++){
          Element widefieldChannelInfo = (Element)widefieldChannelInfos.item(channelIndex);
          this.widefieldChannelInfos.add(widefieldChannelInfo);
        }
      }
    }
  }

  private void translateLasers(){
    lasers = LaserExtractor.extractLasers(mainConfocalSetting);
    LaserWriter.initLasers(lasers, seriesIndex, store);
  }

  private void translateLaserSettings(){
    laserSettings = LaserExtractor.extractLaserSettings(sequentialConfocalSettings, lasers);

    //link laser setting and channel information //TODO easier for STELLARIS
    for (Channel channel : dimensionStore.channels) {
      if (channel.filter == null)
        continue;

      LaserSetting selectedLaserSetting = null;
      for (LaserSetting laserSetting : laserSettings) {
        if (laserSetting.laser.wavelength < channel.filter.cutIn) {
          if (selectedLaserSetting == null || selectedLaserSetting.laser.wavelength < laserSetting.laser.wavelength)
            selectedLaserSetting = laserSetting;
        }
      }

      channel.laserSetting = selectedLaserSetting;
    }

    LaserWriter.initLaserSettings(dimensionStore.channels, seriesIndex, store);
  }

  private void translateDetectors(){
    detectors = DetectorExtractor.extractDetectors(mainConfocalSetting, sequentialConfocalSettings);
    DetectorWriter.initDetectors(detectors, seriesIndex, store);
  }

  private void translateDetectorSettings(){
    detectorSettings = DetectorExtractor.extractDetectorSettings(sequentialConfocalSettings, detectors);

    // link detector and channel information
    // assuming that the order of channels in LMS XML maps the order of active
    // detectors over all LDM block sequential lists
    for (int i = 0; i < detectorSettings.size(); i++) {
      if (i < dimensionStore.channels.size()) {
        dimensionStore.channels.get(i).detectorSetting = detectorSettings.get(i);
        dimensionStore.channels.get(i).name = detectorSettings.get(i).channelName;
      }
    }

    DetectorWriter.initDetectorSettings(dimensionStore.channels, seriesIndex, store);

    //pinhole size
    for (int channelIndex = 0; channelIndex < dimensionStore.channels.size(); channelIndex++){
      Channel channel = dimensionStore.channels.get(channelIndex);
      int sequenceIndex = channel.detectorSetting.sequenceIndex;
      String pinholeSizeS = Extractor.getAttributeValue(sequentialConfocalSettings.get(sequenceIndex), "Pinhole");
      channel.pinholeSize = Extractor.parseDouble(pinholeSizeS) * METER_MULTIPLY;

      store.setChannelPinholeSize(new Length(channel.pinholeSize, UNITS.MICROMETER), seriesIndex, channelIndex);
    }
  }

  private void translateChannelNames(){
    for (int channelIndex = 0; channelIndex < dimensionStore.channels.size(); channelIndex++){
      Channel channel = dimensionStore.channels.get(channelIndex);

      if (dataSourceType == DataSourceType.CONFOCAL){
        channel.channelName = detectorSettings.get(channelIndex).channelName;
      } else {
        channel.channelName = Extractor.getAttributeValue(widefieldChannelInfos.get(channelIndex), "UserDefName");
      }
      store.setChannelName(channel.channelName, seriesIndex, channelIndex);
    }
  }

  private void translateFilters(){
    filters = FilterExtractor.translateFilters(sequentialConfocalSettings, detectorSettings);
    FilterWriter.initFilters(filters, detectorSettings, seriesIndex, store, dataSourceType);
  }

  private void translateFilterSettings(){
    for (int channelIndex = 0; channelIndex < dimensionStore.channels.size(); channelIndex++) {
      if (channelIndex >= filters.size())
        break;
  
      // map filters to channels
      dimensionStore.channels.get(channelIndex).filter = filters.get(channelIndex);
      dimensionStore.channels.get(channelIndex).dye = dimensionStore.channels.get(channelIndex).filter.dye;

      store.setChannelFilterSetRef(dimensionStore.channels.get(channelIndex).filter.filterSetId, seriesIndex, channelIndex);
    }
  }

  private void translateStandDetails()  {
    String instrumentID = MetadataTools.createLSID("Instrument", seriesIndex);
    store.setInstrumentID(instrumentID, seriesIndex);

    String model = Extractor.getAttributeValue(hardwareSetting, "SystemTypeName");
    store.setMicroscopeModel(model, seriesIndex);

    String serialNumber = Extractor.getAttributeValue(mainConfocalSetting, "SystemSerialNumber");
    store.setMicroscopeSerialNumber(serialNumber, seriesIndex);

    String isInverse = Extractor.getAttributeValue(mainConfocalSetting, "IsInverseMicroscopeModel");
    MicroscopeType type = isInverse.equals("1") ? MicroscopeType.INVERTED : MicroscopeType.UPRIGHT;
    store.setMicroscopeType(type, seriesIndex);

    // store.setImageInstrumentRef(instrumentID, seriesIndex);
  }

  private void translateObjective() {
    String objectiveID = MetadataTools.createLSID("Objective", seriesIndex, 0);
    store.setObjectiveID(objectiveID, seriesIndex, 0);

    String model = mainConfocalSetting.getAttribute("ObjectiveName");
    store.setObjectiveModel(model, seriesIndex, 0);

    String naS = mainConfocalSetting.getAttribute("NumericalAperture");
    double na = Extractor.parseDouble(naS);
    store.setObjectiveLensNA(na, seriesIndex, 0);

    String nr = mainConfocalSetting.getAttribute("ObjectiveNumber");
    store.setObjectiveSerialNumber(nr, seriesIndex, 0);

    String magnificationS = mainConfocalSetting.getAttribute("Magnification");
    double magnification = Extractor.parseDouble(magnificationS);
    store.setObjectiveNominalMagnification(magnification, seriesIndex, 0);

    try {

      String immersion = mainConfocalSetting.getAttribute("Immersion");
      store.setObjectiveImmersion(MetadataTools.getImmersion(immersion), seriesIndex, 0);
    } catch (Exception e){
      System.out.println("Objective immersion could not be read.");
      e.printStackTrace();
    }

    String refractionIndexS = mainConfocalSetting.getAttribute("RefractionIndex");
    double refractionIndex = Extractor.parseDouble(refractionIndexS);

    store.setObjectiveSettingsID(objectiveID, seriesIndex);
    store.setObjectiveSettingsRefractiveIndex(refractionIndex, seriesIndex);
    // store.setObjectiveCorrection(MetadataTools.getCorrection(r.metaTemp.corrections[index]),
    // series, 0);
  }

  private void translateROIs(){
    if (Extractor.getDescendantNodesWithName(imageNode, "ROI") != null) {
      alternateCenter = true;
    }

    rois = ROIExtractor.translateROIs(imageNode, dimensionStore.physicalSizeX, dimensionStore.physicalSizeY);
    singleRois = ROIExtractor.translateSingleROIs(imageNode, dimensionStore.physicalSizeX, dimensionStore.physicalSizeY);

    int roiCount = 0;
    for (int planeIndex = 0; planeIndex < reader.getImageCount(); planeIndex++){
      for (int roi = 0; roi < rois.size(); roi++) {
        rois.get(roi).storeROI(store, seriesIndex, roiCount++, roi,
          reader.getCore().get(seriesIndex).sizeX, reader.getCore().get(seriesIndex).sizeY, alternateCenter,
          reader.getMetadataOptions().getMetadataLevel());
      }
    }
  }

  private void translateTimestamps(){
    timestamps = TimestampExtractor.translateTimestamps(imageNode, imageCount);
    double acquiredDate = timestamps.get(0);

    store.setImageAcquisitionDate(new Timestamp(DateTools.convertDate(
      (long) (acquiredDate * 1000), DateTools.COBOL,
      DateTools.ISO8601_FORMAT, false)), seriesIndex);

    for (int planeIndex = 0; planeIndex < reader.getImageCount(); planeIndex++){
      int t = reader.getZCTCoords(planeIndex)[2];
      double timestamp = timestamps.get(t);
      if (timestamps.get(0) == acquiredDate) {
        timestamp -= acquiredDate;
      } else if (timestamp == acquiredDate && t > 0) {
        timestamp = timestamps.get(0);
      }

      store.setPlaneDeltaT(new Time(timestamp, UNITS.SECOND), seriesIndex, planeIndex);
    }
  }

  private void translateExposureTimes(){
    if (dataSourceType == DataSourceType.CONFOCAL) return;

    Element ldmBlockWidefieldSequential = (Element)Extractor.getChildNodeWithName(hardwareSetting, "LDM_Block_Widefield_Sequential");
    Element ldmBlockSequentialList = (Element)Extractor.getChildNodeWithName(ldmBlockWidefieldSequential, "LDM_Block_Sequential_List");
    NodeList sequentialCameraSettings = ldmBlockSequentialList.getChildNodes();

    for (int channelIndex = 0; channelIndex < sequentialCameraSettings.getLength(); channelIndex++){
      Element sequentialCameraSetting = (Element)sequentialCameraSettings.item(channelIndex);
      Element widefieldChannelConfig = (Element)Extractor.getChildNodeWithName(sequentialCameraSetting, "WideFieldChannelConfigurator");
      Element widefieldChannelInfo = (Element)Extractor.getChildNodeWithName(widefieldChannelConfig, "WideFieldChannelInfo");
      String exposureTimeS = Extractor.getAttributeValue(widefieldChannelInfo, "ExposureTime");
      dimensionStore.channels.get(channelIndex).exposureTime = Extractor.parseDouble(exposureTimeS);
    }

    for (int planeIndex = 0; planeIndex < reader.getImageCount(); planeIndex++){
      int channelIndex = reader.getZCTCoords(planeIndex)[1];
        store.setPlaneExposureTime(new Time(dimensionStore.channels.get(channelIndex).exposureTime, UNITS.SECOND), seriesIndex, planeIndex);
    }
    
  }

  private void translateChannels(){
    Element channelsNode = (Element) imageDescription.getElementsByTagName("Channels").item(0);
    NodeList channelNodes = channelsNode.getElementsByTagName("ChannelDescription");;
    core.sizeC = channelNodes.getLength();

    dimensionStore.channels = ChannelExtractor.extractChannels(channelNodes);

    // RGB order is defined by ChannelTag order (1,2,3 = RGB, 3,2,1=BGR)
    inverseRgb = dimensionStore.channels.size() >= 3 && dimensionStore.channels.get(0).channelTag == 3;

    for (int channelIndex = 0; channelIndex < dimensionStore.channels.size(); channelIndex++){
      Channel channel = dimensionStore.channels.get(channelIndex);

      if (!reader.isRGB()) {
        store.setChannelColor(channel.lutColor, seriesIndex, channelIndex);
      }
    }

  }

  private void translateDimensions(){
    List<Dimension> dimensions = DimensionExtractor.extractDimensions(imageDescription, useOldPhysicalSizeCalculation);

    //for all dimensions
    for (Dimension dimension : dimensions){
      dimensionStore.addDimension(dimension);
      if(dimension.key == null)
        extras *= dimension.size;

    }
  
    dimensionStore.addChannelDimension();
    dimensionStore.addMissingDimensions();

    DimensionWriter.setupCoreDimensionParameters(imageFormat, core, dimensionStore, extras);
  }

  public void translateFieldPositions() {
    Element setting;
    if (dataSourceType == DataSourceType.CONFOCAL)
      setting = mainConfocalSetting;
    else 
      setting = mainCameraSetting;

    //XY
    PositionExtractor.extractFieldPositions(imageNode, setting, dimensionStore);

    reader.addSeriesMeta("Reverse X orientation", dimensionStore.flipX);
    reader.addSeriesMeta("Reverse Y orientation", dimensionStore.flipY);
    reader.addSeriesMeta("Swap XY orientation", dimensionStore.swapXY);

    for (int tileIndex = 0; tileIndex < dimensionStore.fieldPositions.size(); tileIndex++){
      Tuple<Length,Length> fieldPosition = dimensionStore.fieldPositions.get(tileIndex);
      for (int planeIndex = 0; planeIndex < reader.getImageCount(); planeIndex++){
        store.setPlanePositionX(fieldPosition.x, tileIndex, planeIndex);
        store.setPlanePositionY(fieldPosition.y, tileIndex, planeIndex);
      }
    }

    //Z
    Dimension dimZ = dimensionStore.getDimension(DimensionKey.Z);
    for (int planeIndex = 0; planeIndex < dimZ.size; planeIndex++){
      Length zPos = FormatTools.createLength(dimZ.origin + dimensionStore.zStep * planeIndex, UNITS.METER);
      store.setPlanePositionZ(zPos, seriesIndex, planeIndex);
    }
  }

  private void translatePhysicalSizes(){
    store.setPixelsPhysicalSizeX(FormatTools.getPhysicalSizeX(dimensionStore.physicalSizeX), seriesIndex);
    store.setPixelsPhysicalSizeY(FormatTools.getPhysicalSizeY(dimensionStore.physicalSizeY), seriesIndex);
    store.setPixelsPhysicalSizeZ(FormatTools.getPhysicalSizeZ(dimensionStore.zStep), seriesIndex);
    store.setPixelsTimeIncrement(new Time(dimensionStore.tStep, UNITS.SECOND), seriesIndex);
  }

  /**
   * Extracts user comments and adds them to the reader's {@link CoreMetadata}
   * 
   * @param imageNode
   * @param image
   * @throws FormatException
   */
  private void translateImageDetails() {
    store.setImageName(imageName, seriesIndex);

    NodeList attachmentNodes = Extractor.getDescendantNodesWithName(imageNode, "User-Comment");
    if (attachmentNodes != null){
      for (int i = 0; i < attachmentNodes.getLength(); i++) {
        Node attachment = attachmentNodes.item(i);
        reader.addSeriesMeta("User-Comment[" + i + "]", attachment.getTextContent());
        if (i == 0)
          description = attachment.getTextContent();
      }
  
      store.setImageDescription(description, seriesIndex);
    }
    


  }
}
