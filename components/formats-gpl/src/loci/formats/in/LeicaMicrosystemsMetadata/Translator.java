package loci.formats.in.LeicaMicrosystemsMetadata;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.CoreMetadata;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.LMSFileReader.ImageFormat;
import loci.formats.in.LeicaMicrosystemsMetadata.MetadataTempBuffer.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.ChannelExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.DetectorExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.DimensionExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.FilterExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.LaserExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.ROIExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.TimestampExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Filter;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.LaserSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ROI;
import loci.formats.in.LeicaMicrosystemsMetadata.writeCore.DimensionWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.writeOME.DetectorWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.writeOME.FilterWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.writeOME.LaserWriter;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.xml.model.enums.MicroscopeType;

public class Translator {
  //XML nodes
  Element imageNode;
  Element imageDescription;
  Element hardwareSetting;
  Element mainConfocalSetting;
  Element cameraSetting;
  List<Element> sequentialConfocalSettings = new ArrayList<Element>();

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

  boolean alternateCenter = false;
  DataSourceType dataSourceType;
  double acquiredDate;
  int imageCount;
  boolean inverseRgb;
  boolean useOldPhysicalSizeCalculation;
  ImageFormat imageFormat;
  int extras = 1;

  int seriesIndex;
  MetadataStore store;
  CoreMetadata core;

  public Translator(Element imageNode, int seriesIndex, MetadataStore store, CoreMetadata core, int imageCount, 
    boolean useOldPhysicalSizeCalculation, ImageFormat imageFormat){
    this.imageNode = imageNode;
    this.seriesIndex = seriesIndex;
    this.store = store;
    this.core = core;
    this.imageCount = imageCount;
    this.useOldPhysicalSizeCalculation = useOldPhysicalSizeCalculation;
  }

  public void translate(){
    getMainNodes();

    //image metadata
    translateChannels();
    translateDimensions();
    translateFieldPositions();

    //instrument metadata
    translateStandDetails();
    translateObjective();
    translateLasers();
    translateDetectors();
    translateFilters();

    //channel metadata
    translateDetectorSettings();
    translateLaserSettings();
    translateFilterSettings();

    translateROIs();
    translateTimestamps();
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
      cameraSetting = (Element)Extractor.getChildNodeWithName(hardwareSetting, "ATLCameraSettingDefinition");
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

    store.setImageInstrumentRef(instrumentID, seriesIndex);
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

  private void translatePhysicalChannelInfo(){
    //pinhole size
    for (int channelIndex = 0; channelIndex < dimensionStore.channels.size(); channelIndex++){
      Channel channel = dimensionStore.channels.get(channelIndex);
      int sequenceIndex = channel.detectorSetting.sequenceIndex;
      String pinholeSizeS = Extractor.getAttributeValue(sequentialConfocalSettings.get(sequenceIndex), "Pinhole");
      channel.pinholeSize = Extractor.parseDouble(pinholeSizeS);

      store.setChannelPinholeSize(new Length(channel.pinholeSize, UNITS.MICROMETER), seriesIndex, channelIndex);

      
    }
  }

  private void translateROIs(){
    if (Extractor.getDescendantNodesWithName(imageNode, "ROI") != null) {
      alternateCenter = true;
    }

    rois = ROIExtractor.translateROIs(imageNode, dimensionStore.physicalSizeX, dimensionStore.physicalSizeY);
    singleRois = ROIExtractor.translateSingleROIs(imageNode, dimensionStore.physicalSizeX, dimensionStore.physicalSizeY);
  }

  private void translateTimestamps(){
    timestamps = TimestampExtractor.translateTimestamps(imageNode, imageCount);
    acquiredDate = timestamps.get(0);
  }

  private void translateChannels(){
    Element channelsNode = (Element) imageDescription.getElementsByTagName("Channels").item(0);
    NodeList channelNodes = channelsNode.getElementsByTagName("ChannelDescription");;
    core.sizeC = channelNodes.getLength();

    dimensionStore.channels = ChannelExtractor.extractChannels(channelNodes);

    // RGB order is defined by ChannelTag order (1,2,3 = RGB, 3,2,1=BGR)
    inverseRgb = dimensionStore.channels.size() >= 3 && dimensionStore.channels.get(0).channelTag == 3;
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
    NodeList attachments = Extractor.getDescendantNodesWithName(imageNode, "Attachment");
    Element tilescanInfo = (Element)Extractor.getNodeWithAttribute(attachments, "Name", "TileScanInfo");
    
    if (tilescanInfo != null){
      NodeList tiles = tilescanInfo.getChildNodes();
      for (int i = 0; i < tiles.getLength(); i++){
        String posXS = Extractor.getAttributeValue(tiles.item(i), "PosX");
        dimensionStore.fieldPosXs.add(Extractor.parseLength(posXS, UNITS.METER));
        String posYS = Extractor.getAttributeValue(tiles.item(i), "PosY");
        dimensionStore.fieldPosXs.add(Extractor.parseLength(posYS, UNITS.METER));
      }
    } else {
      Element source;
      if (dataSourceType == DataSourceType.CONFOCAL)
        source = mainConfocalSetting;
      else 
        source = cameraSetting;
      
        String fieldPosXS = Extractor.getAttributeValue(source, "StagePosX");
        dimensionStore.fieldPosXs.add(Extractor.parseLength(fieldPosXS, UNITS.METER));
        String fieldPosYS = Extractor.getAttributeValue(source, "StagePosY");
        dimensionStore.fieldPosYs.add(Extractor.parseLength(fieldPosYS, UNITS.METER));
    }
  }
}
