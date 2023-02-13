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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.LMSImageXmlDocument;
import loci.common.DataTools;
import ome.units.UNITS;
import ome.units.quantity.Length;

/**
 * This class extracts metadata information from Leica Microsystems image XML
 * and stores it in the reader's CoreMetadata and MetadataTempBuffer
 * 
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class LMSMetadataExtractor {
  // -- Constants --
  private static final long METER_MULTIPLY = 1000000;

  // -- Fields --
  private LMSFileReader r;
  List<Long> channelBytesIncs = new ArrayList<Long>();
  Node hardwareSetting;
  List<Element> sequentialConfocalSettings = new ArrayList<Element>();

  // -- Constructor --
  public LMSMetadataExtractor(LMSFileReader reader) {
    this.r = reader;
  }

  // -- Methods --
  /**
   * Extracts all information from Leica image xml Documents and writes it to
   * reader's {@link CoreMetadata} and {@link MetadataTempBuffer}
   * 
   * @param docs
   *          list of Leica xml documents
   * @throws FormatException
   * @throws IOException
   */
  public void translateMetadata(List<LMSImageXmlDocument> docs) throws FormatException, IOException {
    // create CoreMetadata for each xml referenced image (=series)
    for (int i = 0; i < docs.size(); i++) {
      r.setSeries(i);
      Node image = docs.get(i).getImageNode();
      r.metaTemp.imageNames[i] = docs.get(i).getImageName();
      translateImage((Element) image, i);
    }
    r.setSeries(0);
  }

  /**
   * Extracts information from element node and writes it to reader's
   * {@link CoreMetadata} and {@link MetadataTempBuffer}
   * 
   * @param img
   *          node with tag name "element" from Leica XML
   * @param series
   *          image /core index
   * @throws FormatException
   */
  public void translateImage(Element imageNode, int series) throws FormatException {
    
    // getHardwareNodes(imageNode, series);

    // translateChannelDescriptions(imageNode, series);
    // translateDimensionDescriptions(imageNode, series);
    // translateAttachmentNodes(imageNode, series);

    if (hardwareSetting != null){
      //translateScannerSettings(imageNode, series);
      //translateFilterSettings(imageNode, series);
      // translateTimestamps(imageNode, series);
      // translateLasers(imageNode, series);
      // translateLaserLines2(imageNode, series);
      // translateROIs(imageNode, series);
      // translateSingleROIs(imageNode, series);
      // translateDetectors(imageNode, series);
      // translateDetectors2(imageNode, series);
      // translateFilters(imageNode, series);
      // mapFiltersToChannels(imageNode, series);
    }

    final Deque<String> nameStack = new ArrayDeque<String>();
    populateOriginalMetadata(imageNode, nameStack);
    // addUserCommentMeta(imageNode, series);
  }

  
  /**
   * Extracts scanner information and writes it to reader's {@link CoreMetadata}
   * and {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param image
   *          image / core index
   * @throws FormatException
   */
  public void translateScannerSettings(Element imageNode, int image)
      throws FormatException {
    NodeList scannerSettings = getDescendantNodesWithName(imageNode, "ScannerSettingRecord");

    if (scannerSettings != null) {
      for (int i = 0; i < scannerSettings.getLength(); i++) {
        Element scannerSetting = (Element) scannerSettings.item(i);
        String id = scannerSetting.getAttribute("Identifier");
        if (id == null)
          id = "";
        // String suffix = scannerSetting.getAttribute("Identifier");
        String value = scannerSetting.getAttribute("Variant");

        if (value == null || value.trim().isEmpty()) {
          continue;
        }

        // if (id.equals("SystemType")) {
        // r.metaTemp.microscopeModels[image] = value;
        // }
        if (id.equals("dblPinhole")) {
          r.metaTemp.pinholes[image] = DataTools.parseDouble(value.trim()) * METER_MULTIPLY;
        } else if (id.equals("dblZoom")) {
          // r.metaTemp.zooms[image] = DataTools.parseDouble(value.trim());
        } else if (id.equals("dblStepSize")) {
          r.metaTemp.zSteps[image] = DataTools.parseDouble(value.trim()) * METER_MULTIPLY;
        } else if (id.equals("nDelayTime_s")) {
          r.metaTemp.tSteps[image] = DataTools.parseDouble(value.trim());
        } else if (id.equals("CameraName")) {
          // r.metaTemp.detectorModels.get(image).add(value);
        } else if (id.equals("eDirectional")) {
          r.addSeriesMeta("Reverse X orientation", "1".equals(value.trim()));
        } else if (id.equals("eDirectionalY")) {
          r.addSeriesMeta("Reverse Y orientation", "1".equals(value.trim()));
        } else if (id.indexOf("WFC") == 1) {
          int c = 0;
          try {
            c = Integer.parseInt(id.replaceAll("\\D", ""));
          } catch (NumberFormatException e) {
          }
          if (c < 0 || c >= r.getEffectiveSizeC()) {
            continue;
          }

          if (id.endsWith("ExposureTime")) {
            r.metaTemp.expTimes[image][c] = DataTools.parseDouble(value.trim());
          } else if (id.endsWith("Gain")) {
            r.metaTemp.gains[image][c] = DataTools.parseDouble(value.trim());
          } else if (id.endsWith("WaveLength")) {
            Double exWave = DataTools.parseDouble(value.trim());
            if (exWave != null && exWave > 0) {
              r.metaTemp.exWaves[image][c] = exWave;
            }
          }
          // NB: "UesrDefName" is not a typo.
          else if ((id.endsWith("UesrDefName") || id.endsWith("UserDefName")) &&
              !value.equals("None")) {
            if (r.metaTemp.channelNames[image][c] == null ||
                r.metaTemp.channelNames[image][c].trim().isEmpty()) {
              r.metaTemp.channelNames[image][c] = value;
            }
          }
        }
      }
    }
  }

  /**
   * Extracts filter information and writes it to reader's
   * {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param image
   *          image / core index
   * @throws FormatException
   */
  public void translateFilterSettings(Element imageNode, int image)
      throws FormatException {
    NodeList filterSettings = getDescendantNodesWithName(imageNode, "FilterSettingRecord");
    if (filterSettings == null)
      return;

    int nextChannel = 0;

    for (int i = 0; i < filterSettings.getLength(); i++) {
      Element filterSetting = (Element) filterSettings.item(i);

      String object = filterSetting.getAttribute("ObjectName");
      String attribute = filterSetting.getAttribute("Attribute");
      String objectClass = filterSetting.getAttribute("ClassName");
      String variant = filterSetting.getAttribute("Variant");
      String data = filterSetting.getAttribute("Data");

      if (attribute.equals("NumericalAperture")) {
        if (variant != null && !variant.trim().isEmpty()) {
          r.metaTemp.lensNA[image] = DataTools.parseDouble(variant.trim());
        }
      } else if (attribute.equals("OrderNumber")) {
        // if (variant != null && !variant.trim().isEmpty()) {
        // r.metaTemp.serialNumber[image] = variant.trim();
        // }
      } else if (objectClass.equals("CDetectionUnit")) {
        if (attribute.equals("State")) {
          int channel = getChannelIndex(filterSetting);
          if (channel < 0)
            continue;

          // r.metaTemp.detectorIndexes.get(image).put(Integer.parseInt(data), object);
          r.metaTemp.activeDetector.get(image).add("Active".equals(variant.trim()));
        }
      } else if (attribute.equals("Objective")) {
        StringTokenizer tokens = new StringTokenizer(variant, " ");
        boolean foundMag = false;
        final StringBuilder model = new StringBuilder();
        while (!foundMag) {
          String token = tokens.nextToken();
          int x = token.indexOf('x');
          if (x != -1) {
            foundMag = true;

            String na = token.substring(x + 1);

            if (na != null && !na.trim().isEmpty()) {
              r.metaTemp.lensNA[image] = DataTools.parseDouble(na.trim());
            }
            na = token.substring(0, x);
            if (na != null && !na.trim().isEmpty()) {
              // r.metaTemp.magnification[image] = DataTools.parseDouble(na.trim());
            }
          } else {
            model.append(token);
            model.append(" ");
          }
        }

        String immersion = "Other";
        if (tokens.hasMoreTokens()) {
          immersion = tokens.nextToken();
          if (immersion == null || immersion.trim().isEmpty()) {
            immersion = "Other";
          }
        }
        // r.metaTemp.immersions[image] = immersion;

        String correction = "Other";
        if (tokens.hasMoreTokens()) {
          correction = tokens.nextToken();
          if (correction == null || correction.trim().isEmpty()) {
            correction = "Other";
          }
        }
        r.metaTemp.corrections[image] = correction;

        // r.metaTemp.objectiveModels[image] = model.toString().trim();
      } else if (attribute.equals("RefractionIndex")) {
        if (variant != null && !variant.trim().isEmpty()) {
          r.metaTemp.refractiveIndex[image] = DataTools.parseDouble(variant.trim());
        }
      } else if (attribute.equals("XPos")) {
        if (variant != null && !variant.trim().isEmpty()) {
          final Double number = DataTools.parseDouble(variant.trim());
          r.metaTemp.posX[image] = new Length(number, UNITS.REFERENCEFRAME);
        }
      } else if (attribute.equals("YPos")) {
        if (variant != null && !variant.trim().isEmpty()) {
          final Double number = DataTools.parseDouble(variant.trim());
          r.metaTemp.posY[image] = new Length(number, UNITS.REFERENCEFRAME);
        }
      } else if (attribute.equals("ZPos")) {
        if (variant != null && !variant.trim().isEmpty()) {
          final Double number = DataTools.parseDouble(variant.trim());
          r.metaTemp.posZ[image] = new Length(number, UNITS.REFERENCEFRAME);
        }
      } else if (objectClass.equals("CSpectrophotometerUnit")) {
        Double v = DataTools.parseDouble(variant);
        String description = filterSetting.getAttribute("Description");
        if (description.endsWith("(left)")) {
          r.metaTemp.filterModels.get(image).add(object);
          if (v != null && v > 0) {
            Length in = FormatTools.getCutIn(v);
            if (in != null) {
              r.metaTemp.cutIns.get(image).add(in);
            }
          }
        } else if (description.endsWith("(right)")) {
          if (v != null && v > 0) {
            Length out = FormatTools.getCutOut(v);
            if (out != null) {
              r.metaTemp.cutOuts.get(image).add(out);
            }
          }
        } else if (attribute.equals("Stain")) {
          if (nextChannel < r.metaTemp.channelNames[image].length) {
            r.metaTemp.channelNames[image][nextChannel++] = variant;
          }
        }
      }
    }
  }

  private void printNode(Node node) {
    try {
      // Set up the output transformer
      TransformerFactory transfac = TransformerFactory.newInstance();
      Transformer trans = transfac.newTransformer();
      trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      trans.setOutputProperty(OutputKeys.INDENT, "yes");

      // Print the DOM node

      StringWriter sw = new StringWriter();
      StreamResult result = new StreamResult(sw);
      DOMSource source = new DOMSource(node);
      trans.transform(source, result);
      String xmlString = sw.toString();

      System.out.println(xmlString);
    } catch (TransformerException e) {
      e.printStackTrace();
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
        r.addSeriesMetaList(key.toString() + suffix, value);
      } else {
        NamedNodeMap attributes = root.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
          Attr attr = (Attr) attributes.item(i);
          if (!attr.getName().equals("HighInteger") && !attr.getName().equals("LowInteger")) {
            r.addSeriesMeta(key.toString() + attr.getName(), attr.getValue());
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

  public static int getChannelIndex(Element filterSetting) {
    String data = filterSetting.getAttribute("data");
    if (data == null || data.equals("")) {
      data = filterSetting.getAttribute("Data");
    }
    int channel = data == null || data.equals("") ? 0 : Integer.parseInt(data);
    if (channel < 0)
      return -1;
    return channel - 1;
  }
}
