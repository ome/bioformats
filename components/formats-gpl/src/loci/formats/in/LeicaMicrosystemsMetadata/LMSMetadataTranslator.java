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
import loci.formats.in.LeicaMicrosystemsMetadata.doc.LMSImageXmlDocument;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
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
  public LMSMetadataTranslator(LMSFileReader reader) {
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

      translateImage((Element) image, i);
    }
    r.setSeries(0);

    MetadataStoreInitializer initializer = new MetadataStoreInitializer(r);
    initializer.initMetadataStore();

    // after the following call, we don't have 1 CoreMetadata per xlif-referenced
    // image,
    // but 1 CoreMetadata per series ( = tile )!
    setCoreMetadataForTiles();
  }

  private void initMetadataStore() {
    for (int i = 0; i < r.metaTemp.imageNames.length; i++) {
      r.setSeries(i);
      r.addSeriesMeta("Image name", r.metaTemp.imageNames[i]);
    }
    r.setSeries(0);
  }

  private void translateImage(Element image, int i) throws FormatException {
    r.setSeries(i);

    NodeList attachments = getDescendantNodesWithName(image, "Attachment");
    hardwareSetting = getNodeWithAttribute(attachments, "Name", "HardwareSetting");

    LMSMetadataExtractor extractor = new LMSMetadataExtractor(r); // TODO member ?
    extractor.translateImage(image, i);

    if (hardwareSetting != null){
      mainConfocalSetting = getChildNodeWithName(hardwareSetting, "ATLConfocalSettingDefinition");
      Element ldmBlockSequential = getChildNodeWithName(hardwareSetting, "LDM_Block_Sequential");
      ldmBlockMaster = getChildNodeWithName(ldmBlockSequential, "LDM_Block_Sequential_Master");
      ldmBlockList = getChildNodeWithName(ldmBlockSequential, "LDM_Block_Sequential_List");

      // mapLasersToChannels(i);
    }
  }

  private void initCoreMetadata(int len) {
    r.setCore(new ArrayList<CoreMetadata>(len));
    r.getCore().clear();

    for (int i = 0; i < len; i++) {
      CoreMetadata ms = new CoreMetadata();
      ms.orderCertain = true;
      ms.metadataComplete = true;
      ms.littleEndian = true;
      ms.falseColor = true;
      r.getCore().add(ms);
    }
  }

  private void setCoreMetadataForTiles() {
    ArrayList<CoreMetadata> core = new ArrayList<CoreMetadata>();
    for (int i = 0; i < r.getCore().size(); i++) {
      for (int tile = 0; tile < r.metaTemp.tileCount[i]; tile++) {
        core.add(r.getCore().get(i));
      }
    }
    r.setCore(core);
  }

  

  // -- Helper functions --

  public long parseLong(String value) {
    return value == null || value.trim().isEmpty() ? 0 : Long.parseLong(value.trim());
  }

  public int parseInt(String value) {
    return value == null || value.trim().isEmpty() ? 0 : Integer.parseInt(value.trim());
  }

  public double parseDouble(String value) {
    return value == null || value.trim().isEmpty() ? 0d : DataTools.parseDouble(value.trim());
  }

  /**
   * Returns all (grand*n)children nodes with given node name
   * 
   * @param root
   *          root node
   * @param nodeName
   *          name of children that shall be searched
   * @return list of child nodes with given name
   */
  private NodeList getDescendantNodesWithName(Element root, String nodeName) {
    NodeList nodes = root.getElementsByTagName(nodeName);
    if (nodes.getLength() == 0) {
      NodeList children = root.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Object child = children.item(i);
        if (child instanceof Element) {
          NodeList childNodes = getDescendantNodesWithName((Element) child, nodeName);
          if (childNodes != null) {
            return childNodes;
          }
        }
      }
      return null;
    } else
      return nodes;
  }

  private Element getChildNodeWithName(Node node, String nodeName) {
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getNodeName().equals(nodeName))
        return (Element) children.item(i);
    }
    return null;
  }

  private Element getNodeWithAttribute(NodeList nodes, String attributeName, String attributeValue) {
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      Node attribute = node.getAttributes().getNamedItem(attributeName);
      if (attribute != null && attribute.getTextContent().equals(attributeValue))
        return (Element) node;
    }
    return null;
  }

  private String getAttributeValue(Node node, String attributeName) {
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
