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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.common.DataTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.MetadataTempBuffer.DataSourceType;
import loci.formats.meta.MetadataStore;
import ome.units.quantity.Length;
import ome.xml.model.enums.MicroscopeType;

public class LMSMetadataTranslator {
  // -- Fields --
  private LMSFileReader r;
  MetadataStore store;
  Element hardwareSetting;
  Element mainConfocalSetting;
  Element ldmBlockMaster;
  Element ldmBlockList;




  // -- Constructor --
  public LMSMetadataTranslator(LMSFileReader reader){
    this.r = reader;
    store = r.makeFilterMetadata();
  }

  public void translateMetadata(List<LMSImageXmlDocument> docs) throws FormatException, IOException {
    initCoreMetadata(docs.size());
    initMetadataStore();

    r.metaTemp.channelPrios = new int[r.metaTemp.tileCount.length][];

    for (int i = 0; i < docs.size(); i++) {
      r.setSeries(i);
      Node image = docs.get(i).getImageNode();
      r.metaTemp.imageNames[i] = docs.get(i).getImageName();

      translateImage((Element)image, i);
    }
    r.setSeries(0);
    
    MetadataStoreInitializer initializer = new MetadataStoreInitializer(r);
    initializer.initMetadataStore();

    //after the following call, we don't have 1 CoreMetadata per xlif-referenced image,
    //but 1 CoreMetadata per series ( = tile )!
    setCoreMetadataForTiles();
  }

  private void initMetadataStore(){
    for (int i=0; i<r.metaTemp.imageNames.length; i++) {
      r.setSeries(i);
      r.addSeriesMeta("Image name", r.metaTemp.imageNames[i]);
    }
    r.setSeries(0);
  }

  private void translateImage(Element image, int i) throws FormatException {
    r.setSeries(i);

    NodeList attachments = getDescendantNodesWithName(image, "Attachment");
    hardwareSetting = getNodeWithAttribute(attachments, "Name", "HardwareSetting");
    mainConfocalSetting = getChildNodeWithName(hardwareSetting, "ATLConfocalSettingDefinition");
    Element ldmBlockSequential = getChildNodeWithName(hardwareSetting, "LDM_Block_Sequential");
    ldmBlockMaster = getChildNodeWithName(ldmBlockSequential, "LDM_Block_Sequential_Master");
    ldmBlockList = getChildNodeWithName(ldmBlockSequential, "LDM_Block_Sequential_List");

    LMSMetadataExtractor extractor = new LMSMetadataExtractor(r); //TODO member ?
    extractor.translateImage(image, i);
    translateStandDetails(image, i);
    translateObjective(image, i);
    mapLasersToChannels(i);

  }

  private void initCoreMetadata(int len){
    r.setCore(new ArrayList<CoreMetadata>(len));
    r.getCore().clear();

    for (int i = 0; i < len; i++){
      CoreMetadata ms = new CoreMetadata();
      ms.orderCertain = true;
      ms.metadataComplete = true;
      ms.littleEndian = true;
      ms.falseColor = true;
      r.getCore().add(ms);
    }
  }

  private void setCoreMetadataForTiles(){
    ArrayList<CoreMetadata> core = new ArrayList<CoreMetadata>();
    for (int i = 0; i < r.getCore().size(); i++) {
        for (int tile = 0; tile < r.metaTemp.tileCount[i]; tile++) {
            core.add(r.getCore().get(i));
        }
    }
    r.setCore(core);
  }

  private void translateStandDetails(Element image, int series) throws FormatException {
    int index = getTileIndex(series);
    
    String instrumentID = MetadataTools.createLSID("Instrument", series);
    store.setInstrumentID(instrumentID, series);

    String model = getAttributeValue(hardwareSetting, "SystemTypeName");
    store.setMicroscopeModel(model, series);

    String dataSourceType = getAttributeValue(hardwareSetting, "DataSourceTypeName");
    r.metaTemp.dataSourceTypes[series] = dataSourceType.equals("Confocal") ? DataSourceType.CONFOCAL : DataSourceType.CAMERA;

    String serialNumber = getAttributeValue(mainConfocalSetting, "SystemSerialNumber");
    store.setMicroscopeSerialNumber(serialNumber, series);

    String isInverse = getAttributeValue(mainConfocalSetting, "IsInverseMicroscopeModel");
    MicroscopeType type = isInverse.equals("1") ? MicroscopeType.INVERTED : MicroscopeType.UPRIGHT;
    store.setMicroscopeType(type, series);

    // store.setImageInstrumentRef(instrumentID, series);
  }

  private void translateObjective(Element image, int series) throws FormatException{
    int index = getTileIndex(series);

    String objectiveID = MetadataTools.createLSID("Objective", series, 0);
    store.setObjectiveID(objectiveID, series, 0);

    String model = mainConfocalSetting.getAttribute("ObjectiveName");
    store.setObjectiveModel(model, series, 0);

    String naS = mainConfocalSetting.getAttribute("NumericalAperture");
    double na = parseDouble(naS);
    store.setObjectiveLensNA(na, series, 0);
    
    String nr = mainConfocalSetting.getAttribute("ObjectiveNumber");
    store.setObjectiveSerialNumber(nr, series, 0);
    
    String magnificationS = mainConfocalSetting.getAttribute("Magnification");
    double magnification = parseDouble(magnificationS);
    store.setObjectiveNominalMagnification(magnification, series, 0);

    String immersion = mainConfocalSetting.getAttribute("Immersion");
    store.setObjectiveImmersion(MetadataTools.getImmersion(immersion), series, 0);

    String refractionIndexS = mainConfocalSetting.getAttribute("RefractionIndex");
    double refractionIndex = parseDouble(refractionIndexS);

    store.setObjectiveSettingsID(objectiveID, series);
    store.setObjectiveSettingsRefractiveIndex(refractionIndex, series);
    // store.setObjectiveCorrection(MetadataTools.getCorrection(r.metaTemp.corrections[index]), series, 0);
  }

  private void mapLasersToChannels(int series){
    for (Channel channel : r.metaTemp.channels.get(series)){
      if (channel.filter == null) continue;

      double cutIn = channel.filter.cutIn;
      double cutOut = channel.filter.cutOut;
      
      Laser selectedLaser = null;
      for (Laser laser : r.metaTemp.lasers.get(series)){
        if (laser.wavelength < cutOut){
          if (selectedLaser == null || selectedLaser.wavelength < laser.wavelength)
            selectedLaser = laser;
        }
      }

      channel.laser = selectedLaser;
    }


  }

  // -- Helper functions --

  public long parseLong(String value){
    return value == null || value.trim().isEmpty() ? 0 : Long.parseLong(value.trim());
  }

  public int parseInt(String value){
      return value == null || value.trim().isEmpty() ? 0 : Integer.parseInt(value.trim());
  }

  public double parseDouble(String value){
      return value == null || value.trim().isEmpty() ? 0d : DataTools.parseDouble(value.trim());
  }

  /**
   * Returns all (grand*n)children nodes with given node name
   * @param root root node
   * @param nodeName name of children that shall be searched
   * @return list of child nodes with given name
   */
  private NodeList getDescendantNodesWithName(Element root, String nodeName) {
    NodeList nodes = root.getElementsByTagName(nodeName);
    if (nodes.getLength() == 0) {
        NodeList children = root.getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
            Object child = children.item(i);
            if (child instanceof Element) {
                NodeList childNodes = getDescendantNodesWithName((Element) child, nodeName);
                if (childNodes != null) {
                    return childNodes;
                }
            }
        }
        return null;
    }
    else return nodes;
  }

    private Element getChildNodeWithName(Node node, String nodeName) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeName().equals(nodeName))
                return (Element)children.item(i);
        }
        return null;
    }

  private Element getNodeWithAttribute(NodeList nodes, String attributeName, String attributeValue) {
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      Node attribute = node.getAttributes().getNamedItem(attributeName);
      if (attribute != null && attribute.getTextContent().equals(attributeValue))
        return (Element)node;
    }
    return null;
  }

  private String getAttributeValue(Node node, String attributeName){
    Node attribute = node.getAttributes().getNamedItem(attributeName);
    if (attribute != null)
      return attribute.getTextContent();
    else
      return "";
  }

  public int getTileIndex(int coreIndex) {
    int count = 0;
    for (int tile = 0; tile < r.metaTemp.tileCount.length; tile++) {
        if (coreIndex < count + r.metaTemp.tileCount[tile]) {
        return tile;
        }
        count += r.metaTemp.tileCount[tile];
    }
    return -1;
  }
}
