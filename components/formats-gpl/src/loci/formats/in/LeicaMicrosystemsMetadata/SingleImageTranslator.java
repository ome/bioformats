/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in.LeicaMicrosystemsMetadata;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.common.DateTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.LMSImageXmlDocument;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.ChannelExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.DetectorExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.DimensionExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.FilterExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.HardwareSettingsExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.LaserExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.MicroscopeExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.PositionExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.ROIExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.TimestampExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.Tuple;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.HardwareSettingLayout;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Filter;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.LaserSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ROI;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore.ZDriveMode;
import loci.formats.in.LeicaMicrosystemsMetadata.write.DetectorWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.write.DimensionWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.write.FilterWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.write.LaserWriter;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.xml.model.enums.MicroscopeType;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.Timestamp;

/**
 * SingleImageTranslator translates image and instrument metadata of one LMS image to
 * core (CoreMetadata) and OME metadata (MetadataStore).
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class SingleImageTranslator {
  private static final long METER_MULTIPLY = 1000000;

  //XML nodes
  LMSMainXmlNodes xmlNodes = new LMSMainXmlNodes();

  //extracted data
  public DimensionStore dimensionStore = new DimensionStore();
  List<Laser> lasers = new ArrayList<Laser>();
  List<LaserSetting> laserSettings = new ArrayList<LaserSetting>();
  List<Detector> detectors = new ArrayList<Detector>();
  List<DetectorSetting> detectorSettings = new ArrayList<DetectorSetting>();
  List<Filter> filters = new ArrayList<Filter>();
  List<ROI> rois = new ArrayList<ROI>();
  List<ROI> singleRois = new ArrayList<ROI>();
  List<Double> timestamps = new ArrayList<Double>();

  String microscopeModel;
  public String imageName;
  String description;
  boolean alternateCenter = false;

  int imageCount;
  boolean useOldPhysicalSizeCalculation;
  int extras = 1;

  int seriesIndex;
  LMSFileReader reader;
  MetadataStore store;
  CoreMetadata core;

  public SingleImageTranslator(LMSImageXmlDocument doc, int seriesIndex, int imageCount, LMSFileReader reader){
    this.xmlNodes.imageNode = (Element)doc.getImageNode();
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
    translateTimestamps();


    translateStandDetails();
    if (xmlNodes.hardwareSetting == null) return;

    translatePhysicalSizes();
    translateFieldPositions();
    translateExposureTimes();

    //instrument metadata
    translateObjective();

    if (xmlNodes.dataSourceType == DataSourceType.CONFOCAL || xmlNodes.dataSourceType == DataSourceType.WIDEFOCAL){
      translateDetectors();
      translateDetectorSettings();
    }
      
    translateFilters(); //confocal checks for detector settings
    translateFilterSettings();
    
    if (xmlNodes.dataSourceType == DataSourceType.CONFOCAL || xmlNodes.dataSourceType == DataSourceType.WIDEFOCAL){
      translateLasers();
      translateLaserSettings(); //checks for channel filter
    }

    translateROIs();

    final Deque<String> nameStack = new ArrayDeque<String>();
    populateOriginalMetadata(xmlNodes.imageNode, nameStack);
  }

  private void getMainNodes(){
    xmlNodes.imageDescription = (Element) xmlNodes.imageNode.getElementsByTagName("ImageDescription").item(0);

    HardwareSettingsExtractor.extractHardwareSetting(xmlNodes);

    if (xmlNodes.hardwareSetting == null) return;

    HardwareSettingsExtractor.extractDataSourceType(xmlNodes);
    if (xmlNodes.dataSourceType == null) return;

    microscopeModel = MicroscopeExtractor.extractMicroscopeModel(xmlNodes);
    xmlNodes.isMicaImage = microscopeModel.equals("MICA");

    if (xmlNodes.dataSourceType == DataSourceType.WIDEFOCAL) {
      HardwareSettingsExtractor.extractWidefocalSettings(xmlNodes);
    } else if (xmlNodes.dataSourceType == DataSourceType.CONFOCAL){
      HardwareSettingsExtractor.extractConfocalSettings(xmlNodes);
    } else if (xmlNodes.dataSourceType == DataSourceType.CAMERA) {
      HardwareSettingsExtractor.extractCameraSettings(xmlNodes);
    } else {
      System.out.println("Image data source type currently not supported!");
      return;
    }

    if (xmlNodes.hardwareSettingLayout == HardwareSettingLayout.OLD){
      Element scannerSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "ScannerSetting");
      xmlNodes.scannerSettingRecords = Extractor.getDescendantNodesWithName(scannerSetting, "ScannerSettingRecord");
      Element filterSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "FilterSetting");
      xmlNodes.filterSettingRecords = Extractor.getDescendantNodesWithName(filterSetting, "FilterSettingRecord");
    }
  }

  private void translateLasers(){
    Element setting = xmlNodes.getAtlSetting();
    lasers = LaserExtractor.extractLasers(setting);
    LaserWriter.initLasers(lasers, seriesIndex, store);
  }

  //link laser setting and channel information
  private void translateLaserSettings(){
    laserSettings = LaserExtractor.extractLaserSettings(xmlNodes.sequentialConfocalSettings, lasers);

    // <= SP8: laser wavelength is assumed to lie left of detected emission spectrum
    if (microscopeModel.contains("SP")){
      for (Channel channel : dimensionStore.channels) {
        if (channel.filter == null)
          continue;
  
        LaserSetting selectedLaserSetting = null;
        for (LaserSetting laserSetting : laserSettings) {
          if (laserSetting.laser != null && laserSetting.laser.wavelength < channel.filter.cutIn) {
            if (selectedLaserSetting == null || selectedLaserSetting.laser.wavelength < laserSetting.laser.wavelength)
              selectedLaserSetting = laserSetting;
          }
        }
  
        channel.laserSetting = selectedLaserSetting;
      }
    // STELLARIS: reference line info exists in detector node
    } else if (microscopeModel.contains("STELLARIS")){
      for (Channel channel: dimensionStore.channels){
        for (LaserSetting laserSetting : laserSettings){
          if (laserSetting.laser != null && channel.detectorSetting.referenceLineWavelength == laserSetting.laser.wavelength)
            channel.laserSetting = laserSetting;
        }
      }
    }

    LaserWriter.initLaserSettings(dimensionStore.channels, seriesIndex, store);
  }

  private void translateDetectors(){
      detectors = DetectorExtractor.extractDetectors(xmlNodes);
      DetectorWriter.initDetectors(detectors, seriesIndex, store);
  }

  private void translateDetectorSettings(){
    detectorSettings = DetectorExtractor.extractDetectorSettings(xmlNodes, detectors);

    // link detector and channel information
    // assuming that the order of channels in LMS XML maps the order of active
    // detectors over all LDM block sequential lists
    for (int i = 0; i < detectorSettings.size(); i++) {
      if (i < dimensionStore.channels.size()) {
        dimensionStore.channels.get(i).detectorSetting = detectorSettings.get(i);
        dimensionStore.channels.get(i).channelName = detectorSettings.get(i).channelName;
      }
    }

    DetectorWriter.initDetectorSettings(dimensionStore.channels, seriesIndex, store);

    //pinhole size
    for (int channelIndex = 0; channelIndex < dimensionStore.channels.size(); channelIndex++){
      Channel channel = dimensionStore.channels.get(channelIndex);
      if (channel.detectorSetting != null){
        int sequenceIndex = channel.detectorSetting.sequenceIndex;
        String pinholeSizeS = Extractor.getAttributeValue(xmlNodes.sequentialConfocalSettings.get(sequenceIndex), "Pinhole");
        channel.pinholeSize = Extractor.parseDouble(pinholeSizeS) * METER_MULTIPLY;
  
        store.setChannelPinholeSize(new Length(channel.pinholeSize, UNITS.MICROMETER), seriesIndex, channelIndex);
      }
    }
  }

  private void translateFilters(){
    if (xmlNodes.dataSourceType == DataSourceType.CONFOCAL){
      filters = FilterExtractor.translateConfocalFilters(xmlNodes.sequentialConfocalSettings, detectorSettings);
    } else {
      filters = FilterExtractor.translateWidefieldFilters(xmlNodes);
    }
    
    FilterWriter.initFilters(filters, detectorSettings, seriesIndex, store, xmlNodes.dataSourceType);
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
    
    //an empty instrument is created even when there are no hardware settings for the image (e.g. depth map image, EDF image),
    //since this is bioformats' expectation (see OMEXMLMetadataImpl, line 7801)
    if (xmlNodes.hardwareSetting == null) return;

    microscopeModel = MicroscopeExtractor.extractMicroscopeModel(xmlNodes);
    store.setMicroscopeModel(microscopeModel, seriesIndex);

    MicroscopeType micType = MicroscopeExtractor.extractMicroscopeType(xmlNodes);
    store.setMicroscopeType(micType, seriesIndex);

    String serialNumber = MicroscopeExtractor.extractMicroscopeSerialNumber(xmlNodes);
    store.setMicroscopeSerialNumber(serialNumber, seriesIndex);

    store.setImageInstrumentRef(instrumentID, seriesIndex);
  }

  private void translateObjective() {
    String objectiveID = MetadataTools.createLSID("Objective", seriesIndex, 0);
    store.setObjectiveID(objectiveID, seriesIndex, 0);

    Element setting = xmlNodes.getAtlSetting();

    String model = setting.getAttribute("ObjectiveName");
    store.setObjectiveModel(model, seriesIndex, 0);

    String naS = setting.getAttribute("NumericalAperture");
    double na = Extractor.parseDouble(naS);
    store.setObjectiveLensNA(na, seriesIndex, 0);

    String nr = setting.getAttribute("ObjectiveNumber");
    store.setObjectiveSerialNumber(nr, seriesIndex, 0);

    String magnificationS = setting.getAttribute("Magnification");
    double magnification = Extractor.parseDouble(magnificationS);
    store.setObjectiveNominalMagnification(magnification, seriesIndex, 0);

    try {

      String immersion = setting.getAttribute("Immersion");
      store.setObjectiveImmersion(MetadataTools.getImmersion(immersion), seriesIndex, 0);
    } catch (Exception e){
      System.out.println("Objective immersion could not be read.");
      e.printStackTrace();
    }

    String refractionIndexS = setting.getAttribute("RefractionIndex");
    double refractionIndex = Extractor.parseDouble(refractionIndexS);

    store.setObjectiveSettingsID(objectiveID, seriesIndex);
    store.setObjectiveSettingsRefractiveIndex(refractionIndex, seriesIndex);
    // store.setObjectiveCorrection(MetadataTools.getCorrection(r.metaTemp.corrections[index]),
    // series, 0);
  }

  private void translateROIs(){
    if (Extractor.getDescendantNodesWithName(xmlNodes.imageNode, "ROI") != null) {
      alternateCenter = true;
    }

    rois = ROIExtractor.translateROIs(xmlNodes.imageNode, dimensionStore.physicalSizeX, dimensionStore.physicalSizeY);
    singleRois = ROIExtractor.translateSingleROIs(xmlNodes.imageNode, dimensionStore.physicalSizeX, dimensionStore.physicalSizeY);

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
    timestamps = TimestampExtractor.translateTimestamps(xmlNodes.imageNode, reader.getImageCount());
    if (timestamps.size() == 0) return;

    double acquiredDate = timestamps.get(0);

    store.setImageAcquisitionDate(new Timestamp(DateTools.convertDate(
      (long) (acquiredDate * 1000), DateTools.COBOL,
      DateTools.ISO8601_FORMAT, false)), seriesIndex);

    for (int planeIndex = 0; planeIndex < reader.getImageCount(); planeIndex++){
      int t = reader.getZCTCoords(planeIndex)[2];
      if (t < timestamps.size()){
        double timestamp = timestamps.get(t);
        if (timestamps.get(0) == acquiredDate) {
          timestamp -= acquiredDate;
        } else if (timestamp == acquiredDate && t > 0) {
          timestamp = timestamps.get(0);
        }
  
        store.setPlaneDeltaT(new Time(timestamp, UNITS.SECOND), seriesIndex, planeIndex);
      }
    }
  }

  private void translateExposureTimes(){
    if (xmlNodes.dataSourceType == DataSourceType.CONFOCAL) return;

    for (int channelIndex = 0; channelIndex < dimensionStore.channels.size(); channelIndex++){
      int logicalChannelIndex = dimensionStore.rgb ? channelIndex / 3 : channelIndex;
      String exposureTimeS = Extractor.getAttributeValue(xmlNodes.widefieldChannelInfos.get(logicalChannelIndex), "ExposureTime");
      dimensionStore.channels.get(channelIndex).exposureTime = Extractor.parseDouble(exposureTimeS);
    }

    for (int planeIndex = 0; planeIndex < reader.getImageCount(); planeIndex++){
      int channelIndex = reader.getZCTCoords(planeIndex)[1];
        store.setPlaneExposureTime(new Time(dimensionStore.channels.get(channelIndex).exposureTime, UNITS.SECOND), seriesIndex, planeIndex);
    }
  }

  private void translateChannels(){
    Element channelsNode = (Element) xmlNodes.imageDescription.getElementsByTagName("Channels").item(0);
    NodeList channelNodes = channelsNode.getElementsByTagName("ChannelDescription");
    core.sizeC = channelNodes.getLength();

    List<Channel> channels = ChannelExtractor.extractChannels(channelNodes);
    dimensionStore.setChannels(channels);
    
    DimensionWriter.setChannels(core, store, dimensionStore, reader.getImageFormat(), seriesIndex);
  }

  private void translateDimensions(){
    List<Dimension> dimensions = DimensionExtractor.extractDimensions(xmlNodes.imageDescription, useOldPhysicalSizeCalculation);

    for (Dimension dimension : dimensions){
      dimensionStore.addDimension(dimension);
      if(dimension.key == null)
        extras *= dimension.size;
    }
  
    dimensionStore.addChannelDimension();
    dimensionStore.addMissingDimensions();

    DimensionWriter.setupCoreDimensionParameters(core, dimensionStore, extras);
  }

  public void translateFieldPositions() {
    //XY
    PositionExtractor.extractFieldPositions(xmlNodes, dimensionStore);

    reader.addSeriesMeta("Reverse X orientation", dimensionStore.flipX);
    reader.addSeriesMeta("Reverse Y orientation", dimensionStore.flipY);
    reader.addSeriesMeta("Swap XY orientation", dimensionStore.swapXY);

    for (int tileIndex = 0; tileIndex < dimensionStore.fieldPositions.size(); tileIndex++){
      Tuple<Length,Length> fieldPosition = dimensionStore.fieldPositions.get(tileIndex);
      int nPlanesPerTile = dimensionStore.getNumberOfPlanesPerTile();
      for (int planeIndexWithinTile = 0; planeIndexWithinTile < nPlanesPerTile; planeIndexWithinTile++){
        int absolutePlaneIndex = tileIndex * nPlanesPerTile + planeIndexWithinTile;
        store.setPlanePositionX(fieldPosition.x, seriesIndex, absolutePlaneIndex);
        store.setPlanePositionY(fieldPosition.y, seriesIndex, absolutePlaneIndex);
      }
    }

    //Z
    for (int planeIndex = 0; planeIndex < reader.getImageCount(); planeIndex++){
      int sign = dimensionStore.zBegin <= dimensionStore.zEnd ? 1 : -1;
      int zIndex = reader.getZCTCoords(planeIndex)[0];
      double otherZDrivePos = dimensionStore.zDriveMode == ZDriveMode.ZGalvo ? dimensionStore.zWidePosition : dimensionStore.zGalvoPosition;
      Length zPos = FormatTools.createLength(otherZDrivePos + dimensionStore.zBegin + dimensionStore.zStep * sign * zIndex, UNITS.METER);
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

    NodeList attachmentNodes = Extractor.getDescendantNodesWithName(xmlNodes.imageNode, "User-Comment");
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

  /**
   * Creates key value pairs from attributes of the root's child nodes (tag |
   * attribute name : attribute value) and adds them to reader's
   * {@link CoreMetadata}
   * 
   * @param root
   *          xml node
   * @param nameStack
   *          list of node names to be prepended to key name
   */
  private void populateOriginalMetadata(Element root, Deque<String> nameStack) {
    String name = root.getNodeName();
    if (root.hasAttributes() && !name.equals("Element") && !name.equals("Attachment")
        && !name.equals("LMSDataContainerHeader")) {
      nameStack.push(name);

      String suffix = root.getAttribute("Identifier");
      String value = root.getAttribute("Variant");
      if (suffix == null || suffix.trim().length() == 0) {
        suffix = root.getAttribute("Description");
      }
      final StringBuilder key = new StringBuilder();
      final Iterator<String> nameStackIterator = nameStack.descendingIterator();
      while (nameStackIterator.hasNext()) {
        final String k = nameStackIterator.next();
        key.append(k);
        key.append("|");
      }
      if (suffix != null && value != null && suffix.length() > 0 && value.length() > 0 && !suffix.equals("HighInteger")
          && !suffix.equals("LowInteger")) {
        reader.addSeriesMetaList(key.toString() + suffix, value);
      } else {
        NamedNodeMap attributes = root.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
          Attr attr = (Attr) attributes.item(i);
          if (!attr.getName().equals("HighInteger") && !attr.getName().equals("LowInteger")) {
            reader.addSeriesMeta(key.toString() + attr.getName(), attr.getValue());
          }
        }
      }
    }

    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Object child = children.item(i);
      if (child instanceof Element) {
        populateOriginalMetadata((Element) child, nameStack);
      }
    }

    if (root.hasAttributes() && !name.equals("Element") && !name.equals("Attachment")
        && !name.equals("LMSDataContainerHeader")) {
      nameStack.pop();
    }
  }
}
