package loci.formats.in.LeicaMicrosystemsMetadata;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.MetadataTempBuffer.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.DetectorExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.FilterExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.LaserExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.ROIExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.TimestampExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Filter;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.LaserSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ROI;
import loci.formats.in.LeicaMicrosystemsMetadata.writeOME.DetectorWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.writeOME.FilterWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.writeOME.LaserWriter;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.xml.model.enums.MicroscopeType;
import ome.xml.model.primitives.PercentFraction;

public class Translator {
  //XML nodes
  Element imageNode;
  Element hardwareSetting;
  Element mainConfocalSetting;
  List<Element> sequentialConfocalSettings = new ArrayList<Element>();

  //extracted data
  List<Channel> channels = new ArrayList<Channel>();
  List<Laser> lasers = new ArrayList<Laser>();
  List<LaserSetting> laserSettings = new ArrayList<LaserSetting>();
  List<Detector> detectors = new ArrayList<Detector>();
  List<DetectorSetting> detectorSettings = new ArrayList<DetectorSetting>();
  List<Filter> filters = new ArrayList<Filter>();
  List<ROI> rois = new ArrayList<ROI>();
  List<ROI> singleRois = new ArrayList<ROI>();
  List<Double> timestamps = new ArrayList<Double>();

  boolean alternateCenter = false;
  double physicalSizeX;
  double physicalSizeY;
  DataSourceType dataSourceType;
  double acquiredDate;
  int imageCount;

  int seriesIndex;
  MetadataStore store;

  public Translator(Element imageNode, int seriesIndex, MetadataStore store, int imageCount){
    this.imageNode = imageNode;
    this.seriesIndex = seriesIndex;
    this.store = store;
    this.imageCount = imageCount;
  }

  public void translate(){
    getHardwareNodes();

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

  private void getHardwareNodes(){
    NodeList attachments = Extractor.getDescendantNodesWithName(imageNode, "Attachment");
    hardwareSetting = (Element)Extractor.getNodeWithAttribute(attachments, "Name", "HardwareSetting");
    if (hardwareSetting == null) return;

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
  }

  private void translateLasers(){
    lasers = LaserExtractor.extractLasers(mainConfocalSetting);
    LaserWriter.initLasers(lasers, seriesIndex, store);
  }

  private void translateLaserSettings(){
    laserSettings = LaserExtractor.extractLaserSettings(sequentialConfocalSettings, lasers);

    //link laser setting and channel information //TODO easier for STELLARIS
    for (Channel channel : channels) {
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

    LaserWriter.initLaserSettings(channels, seriesIndex, store);
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
      if (i < channels.size()) {
        channels.get(i).detectorSetting = detectorSettings.get(i);
        channels.get(i).name = detectorSettings.get(i).channelName;
      }
    }

    DetectorWriter.initDetectorSettings(channels, seriesIndex, store);
  }

  private void translateFilters(){
    filters = FilterExtractor.translateFilters(sequentialConfocalSettings, detectorSettings);
    FilterWriter.initFilters(filters, detectorSettings, seriesIndex, store, dataSourceType);
  }

  private void translateFilterSettings(){
    for (int channelIndex = 0; channelIndex < channels.size(); channelIndex++) {
      if (channelIndex >= filters.size())
        break;
  
      // map filters to channels
      channels.get(channelIndex).filter = filters.get(channelIndex);
      channels.get(channelIndex).dye = channels.get(channelIndex).filter.dye;

      store.setChannelFilterSetRef(channels.get(channelIndex).filter.filterSetId, seriesIndex, channelIndex);
    }
  }

  private void translateStandDetails()  {
    String instrumentID = MetadataTools.createLSID("Instrument", seriesIndex);
    store.setInstrumentID(instrumentID, seriesIndex);

    String model = Extractor.getAttributeValue(hardwareSetting, "SystemTypeName");
    store.setMicroscopeModel(model, seriesIndex);

    String dataSourceTypeS = Extractor.getAttributeValue(hardwareSetting, "DataSourceTypeName");
    dataSourceType = dataSourceTypeS.equals("Confocal") ? DataSourceType.CONFOCAL : DataSourceType.CAMERA;

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
    for (int channelIndex = 0; channelIndex < channels.size(); channelIndex++){
      Channel channel = channels.get(channelIndex);
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

    rois = ROIExtractor.translateROIs(imageNode, physicalSizeX, physicalSizeY);
    singleRois = ROIExtractor.translateSingleROIs(imageNode, physicalSizeX, physicalSizeY);
  }

  private void translateTimestamps(){
    timestamps = TimestampExtractor.translateTimestamps(imageNode, imageCount);
    acquiredDate = timestamps.get(0);
  }
}
