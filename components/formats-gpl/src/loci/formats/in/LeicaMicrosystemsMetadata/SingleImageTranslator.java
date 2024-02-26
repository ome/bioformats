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
import java.util.List;

import org.w3c.dom.Element;

import loci.formats.CoreMetadata;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.LMSImageXmlDocument;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.ChannelExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.DimensionExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.WidefieldSettingsExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.HardwareSettingsExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.ImageSettingsExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.ConfocalSettingsExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.MicroscopeExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.PositionExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.ROIExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.TimestampExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.AtlSettingLayout;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ConfocalAcquisitionSettings;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dye;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Filter;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ImageDetails;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.LaserSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.MicroscopeDetails;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ROI;
import loci.formats.in.LeicaMicrosystemsMetadata.write.ConfocalSettingsWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.write.DimensionWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.write.ImageSettingsWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.write.InstrumentWriter;
import loci.formats.in.LeicaMicrosystemsMetadata.write.WidefieldSettingsWriter;
import loci.formats.meta.MetadataStore;

/**
 * SingleImageTranslator translates image and instrument metadata of one LMS image to
 * core (CoreMetadata) and OME metadata (MetadataStore).
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class SingleImageTranslator {
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
  List<Dye> dyes = new ArrayList<>();

  MicroscopeDetails microscopeDetails = new MicroscopeDetails();
  public ImageDetails imageDetails = new ImageDetails();
  ConfocalAcquisitionSettings confocalAcquisitionSettings = new ConfocalAcquisitionSettings();
  boolean alternateCenter = false;

  int imageCount;
  boolean useOldPhysicalSizeCalculation;
  public int currentTileIndex = 0;

  int seriesIndex;
  LMSFileReader reader;
  MetadataStore store;
  CoreMetadata core;

  public SingleImageTranslator(LMSImageXmlDocument doc, int seriesIndex, int imageCount, LMSFileReader reader){
    this.xmlNodes.imageNode = (Element)doc.getImageNode();
    this.imageDetails.originalImageName = doc.getImageName();
    imageDetails.targetImageName = this.imageDetails.originalImageName;

    this.reader = reader;

    this.seriesIndex = seriesIndex;
    this.store = reader.getMetadataStore();
    this.imageCount = imageCount;
    this.useOldPhysicalSizeCalculation = reader.useOldPhysicalSizeCalculation();
  }

  public void setTarget(int seriesIndex, String imageName, int tileIndex){
    imageDetails.targetImageName = imageName;
    this.seriesIndex = seriesIndex;
    currentTileIndex = tileIndex;

    reader.setSeries(seriesIndex);
    reader.addSeriesMeta("Image name", imageDetails.targetImageName);
    this.core = reader.getCore().get(seriesIndex);
  }

  public void extract(){
    getMainNodes();

    //image metadata
    ImageSettingsExtractor.extractImageDetails(xmlNodes, imageDetails);
    DimensionExtractor.extractChannels(xmlNodes, dimensionStore);
    DimensionExtractor.extractDimensions(xmlNodes.imageDescription, useOldPhysicalSizeCalculation, dimensionStore);
    timestamps = TimestampExtractor.extractTimestamps(xmlNodes.imageNode, reader.getImageCount());

    if (xmlNodes.hardwareSetting == null)
      return;

    microscopeDetails = MicroscopeExtractor.extractMicroscopeDetails(xmlNodes);
    PositionExtractor.extractFieldPositions(xmlNodes, dimensionStore);
    DimensionExtractor.extractExposureTimes(xmlNodes, dimensionStore);

    switch(xmlNodes.atlSettingLayout){
      case CONFOCAL_OLD:
      case CONFOCAL_NEW:
        ConfocalSettingsExtractor.extractInstrumentSettings(xmlNodes, confocalAcquisitionSettings);
        ConfocalSettingsExtractor.extractChannelSettings(xmlNodes, confocalAcquisitionSettings);
        break;
      case WIDEFIELD:
        filters = WidefieldSettingsExtractor.extractWidefieldFilters(xmlNodes);
        break;
      case MICA_CONFOCAL:
      case MICA_WIDEFIELD:
      case MICA_WIDEFOCAL:
        ConfocalSettingsExtractor.extractInstrumentSettings(xmlNodes, confocalAcquisitionSettings);
        dyes = ChannelExtractor.extractMicaDyes(xmlNodes);
        break;
      default: break;
    }

    extractROIs();
  }

  public void write(){
    ImageSettingsWriter.writeImageDetails(store, reader, imageDetails, seriesIndex);
    DimensionWriter.writeChannels(core, store, dimensionStore, reader.getImageFormat(), seriesIndex);
    DimensionWriter.writeDimensions(core, dimensionStore);
    DimensionWriter.writeTimestamps(store, reader, timestamps, seriesIndex);

    if (xmlNodes.hardwareSetting == null){
      //an empty instrument is created even when there are no hardware settings for the image (e.g. depth map image, EDF image),
      //since this is bioformats' expectation (see OMEXMLMetadataImpl, line 7801)
      String instrumentID = MetadataTools.createLSID("Instrument", seriesIndex);
      store.setInstrumentID(instrumentID, seriesIndex);
      store.setImageInstrumentRef(instrumentID, seriesIndex);
      return;
    }

    DimensionWriter.writePhysicalSizes(store, dimensionStore, seriesIndex);
    DimensionWriter.writeFieldPositions(store, dimensionStore, reader, seriesIndex, currentTileIndex);
    DimensionWriter.writeExposureTimes(store, dimensionStore, reader, seriesIndex);

    //instrument metadata
    InstrumentWriter.writeMicroscopeDetails(store, xmlNodes, microscopeDetails, seriesIndex);

    switch(xmlNodes.atlSettingLayout){
      case CONFOCAL_OLD:
      case CONFOCAL_NEW:
        ConfocalSettingsWriter.initConfocalInstrumentSettings(confocalAcquisitionSettings, seriesIndex, store);
        ConfocalSettingsWriter.initConfocalChannelSettings(confocalAcquisitionSettings, seriesIndex, store);
        break;
      case WIDEFIELD:
        WidefieldSettingsWriter.initFilters(filters, dimensionStore.channels.size(), seriesIndex, store);
        break;
      case MICA_CONFOCAL:
      case MICA_WIDEFIELD:
      case MICA_WIDEFOCAL:
        ConfocalSettingsWriter.initConfocalInstrumentSettings(confocalAcquisitionSettings, seriesIndex, store);
        DimensionWriter.addDyeInfosToChannels(store, dyes, dimensionStore, seriesIndex);
        break;
      default: break;
    }

    writeROIs();

    final Deque<String> nameStack = new ArrayDeque<String>();
    ImageSettingsWriter.populateOriginalMetadata(xmlNodes.imageNode, nameStack, reader);
  }

  public void translate(){
    extract();
    write();
  }

  private void getMainNodes(){
    xmlNodes.imageDescription = (Element) xmlNodes.imageNode.getElementsByTagName("ImageDescription").item(0);
    xmlNodes.attachments = Extractor.getDescendantNodesWithName(xmlNodes.imageNode, "Attachment");

    HardwareSettingsExtractor.extractHardwareSetting(xmlNodes);

    if (xmlNodes.hardwareSetting == null) return;

    HardwareSettingsExtractor.extractDataSourceType(xmlNodes);
    if (xmlNodes.dataSourceType == null || xmlNodes.dataSourceType == DataSourceType.SPIM || 
      xmlNodes.dataSourceType == DataSourceType.UNDEFINED){
        System.out.println("Image data source type currently not supported!");
        return;
      }

    String microscopeModel = MicroscopeExtractor.extractMicroscopeModel(xmlNodes);
    xmlNodes.isMicaImage = microscopeModel.equals("MICA");

    HardwareSettingsExtractor.extractAtlSettingLayout(xmlNodes);
    if (xmlNodes.atlSettingLayout == AtlSettingLayout.UNKNOWN) return;

    HardwareSettingsExtractor.extractHardwareSettingChildNodes(xmlNodes);
  }


  private void extractROIs(){
    if (Extractor.getDescendantNodesWithName(xmlNodes.imageNode, "ROI") != null) {
      alternateCenter = true;
    }

    rois = ROIExtractor.translateROIs(xmlNodes.imageNode, dimensionStore.physicalSizeX, dimensionStore.physicalSizeY);
    singleRois = ROIExtractor.translateSingleROIs(xmlNodes.imageNode, dimensionStore.physicalSizeX, dimensionStore.physicalSizeY);
  }

  private void writeROIs(){
    int roiCount = 0;
    for (int planeIndex = 0; planeIndex < reader.getImageCount(); planeIndex++){
      for (int roi = 0; roi < rois.size(); roi++) {
        rois.get(roi).storeROI(store, seriesIndex, roiCount++, roi,
          reader.getCore().get(seriesIndex).sizeX, reader.getCore().get(seriesIndex).sizeY, alternateCenter,
          reader.getMetadataOptions().getMetadataLevel());
      }
    }
  }
}
