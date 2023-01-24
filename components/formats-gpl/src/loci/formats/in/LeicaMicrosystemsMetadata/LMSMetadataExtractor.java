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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.util.ElementScanner14;
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

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.in.LeicaMicrosystemsMetadata.Dimension.DimensionKey;
import loci.formats.in.LeicaMicrosystemsMetadata.LMSFileReader.ImageFormat;
import loci.common.DataTools;
import loci.common.DateTools;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.xml.model.primitives.Color;

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
  int extras = 1;
  Node hardwareSetting;

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
   * @param i
   *          image /core index
   * @throws FormatException
   */
  public void translateImage(Element image, int i) throws FormatException {
    NodeList attachments = getDescendantNodesWithName(image, "Attachment");
    hardwareSetting = getNodeWithAttribute(attachments, "Name", "HardwareSetting");

    translateChannelDescriptions(image, i);
    translateDimensionDescriptions(image, i);
    translateAttachmentNodes(image, i);
    translateScannerSettings(image, i);
    translateFilterSettings(image, i);
    translateTimestamps(image, i);
    translateLaserLines(image, i);
    translateLaserLines2(image, i);
    translateROIs(image, i);
    translateSingleROIs(image, i);
    translateDetectors(image, i);
    translateDetectors2(image, i);
    translatePhysicalChannelInfo(image, i);

    final Deque<String> nameStack = new ArrayDeque<String>();
    populateOriginalMetadata(image, nameStack);
    addUserCommentMeta(image, i);
  }

  /***
   * Extracts i.a. channel number and luts from channel descriptions and writes it
   * to reader's {@link CoreMetadata} and {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param coreIndex
   * @throws FormatException
   */
  public void translateChannelDescriptions(Element imageNode, int coreIndex)
      throws FormatException {
    CoreMetadata ms = r.getCore().get(coreIndex);

    NodeList channels = getChannelDescriptionNodes(imageNode);
    ms.sizeC = channels.getLength();

    List<String> luts = new ArrayList<String>();

    for (int ch = 0; ch < channels.getLength(); ch++) {
      Element channelElement = (Element) channels.item(ch);

      luts.add(channelElement.getAttribute("LUTName"));
      int channelTag = Integer.parseInt(channelElement.getAttribute("ChannelTag"));
      int resolution = Integer.parseInt(channelElement.getAttribute("Resolution"));
      double min = parseDouble(channelElement.getAttribute("Min"));
      double max = parseDouble(channelElement.getAttribute("Max"));
      String unit = channelElement.getAttribute("Unit");
      String lutName = channelElement.getAttribute("LUTName");
      long bytesInc = parseLong(channelElement.getAttribute("BytesInc"));

      Channel channel = new Channel(channelTag, resolution, min, max, unit, lutName, bytesInc);
      r.metaTemp.channels.get(coreIndex).add(channel);
    }

    // RGB order is defined by ChannelTag order (1,2,3 = RGB, 3,2,1=BGR)
    r.metaTemp.inverseRgb[coreIndex] = channels.getLength() >= 3 &&
        ((Element) channels.item(0)).getAttribute("ChannelTag").equals("3");

    translateLuts(luts, coreIndex);
  }

  /***
   * Translates raw channel luts of an image to Colors and writes it to reader's
   * {@link MetadataTempBuffer}
   * 
   * @param luts
   *          list of raw lut names / values from Leica XML
   */
  private void translateLuts(List<String> luts, int imgIndex) {
    CoreMetadata ms = r.getCore().get(imgIndex);
    ArrayList<Color> channelColors = new ArrayList<Color>(ms.sizeC);
    r.metaTemp.channelPrios[imgIndex] = new int[ms.sizeC];

    int nextLut = 0;
    // foreach channel of image
    for (int channel = 0; channel < ms.sizeC; channel++) {
      if (nextLut < luts.size()) {
        Color lutColor = translateLut(luts.get(nextLut));
        channelColors.add(lutColor);
        r.metaTemp.channelPrios[imgIndex][channel] = getChannelPriority(luts.get(nextLut));
        nextLut++;
      }
    }
    r.metaTemp.channelColors.add(channelColors);
  }

  private int getChannelPriority(String lutName) {
    switch (lutName) {
      case "red":
        return 0;
      case "green":
        return 1;
      case "blue":
        return 2;
      case "cyan":
        return 3;
      case "magenta":
        return 4;
      case "yellow":
        return 5;
      case "black":
        return 6;
      case "gray":
        return 7;
      case "":
      default:
        return 8;
    }
  }

  /***
   * Translates Leica XML lut name / value to Color
   * 
   * @param lutName
   * @return
   */
  private Color translateLut(String lutName) {
    lutName = lutName.replaceAll("\\s+", "");
    // some LUTs are stored as gradients
    Pattern pattern = Pattern.compile("Gradient\\(\\d+,\\d+,\\d+\\)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(lutName);
    if (matcher.find()) {
      String[] rgb = lutName.substring(9, lutName.length() - 1).split(",");
      return new Color(Integer.parseInt(rgb[2]),
          Integer.parseInt(rgb[1]),
          Integer.parseInt(rgb[0]),
          255);
    } else {
      switch (lutName.toLowerCase()) {
        case "red":
          return new Color(255, 0, 0, 255);
        case "green":
          return new Color(0, 255, 0, 255);
        case "blue":
          return new Color(0, 0, 255, 255);
        case "cyan":
          return new Color(0, 255, 255, 255);
        case "magenta":
          return new Color(255, 0, 255, 255);
        case "yellow":
          return new Color(255, 255, 0, 255);
        default:
          return new Color(255, 255, 255, 255);
      }
    }
    // TODO: numeric lut handling
  }

  /**
   * Extracts information from dimension descriptions and writes it to reader's
   * {@link CoreMetadata} and {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param coreIndex
   * @throws FormatException
   */
  private void translateDimensionDescriptions(Element imageNode, int coreIndex) throws FormatException {
    CoreMetadata cmd = r.getCore().get(coreIndex);
    NodeList dimensions = getDimensionDescriptionNodes(imageNode);

    // add dimensions
    for (int i = 0; i < dimensions.getLength(); i++) {
      Element dimensionElement = (Element) dimensions.item(i);

      int id = parseInt(dimensionElement.getAttribute("DimID"));
      int size = parseInt(dimensionElement.getAttribute("NumberOfElements"));
      long bytesInc = parseLong(dimensionElement.getAttribute("BytesInc"));
      Double length = parseDouble(dimensionElement.getAttribute("Length"));
      String unit = dimensionElement.getAttribute("Unit");
      boolean oldPhysicalSize = r.useOldPhysicalSizeCalculation();

      Dimension dimension = new Dimension(DimensionKey.with(id), size, bytesInc, unit, length, oldPhysicalSize);
      r.metaTemp.addDimension(coreIndex, dimension);

      if (DimensionKey.with(id) == null)
        extras *= dimension.size;
    }

    r.metaTemp.addChannelDimension(coreIndex);
    r.metaTemp.addMissingDimensions(coreIndex);
    setCoreDimensionSizes(coreIndex);

    setPixelType(coreIndex);

    // TIFF and JPEG files not interleaved
    if (r.getImageFormat() == ImageFormat.TIF || r.getImageFormat() == ImageFormat.JPEG) {
      cmd.interleaved = false;
    } else {
      cmd.interleaved = cmd.rgb;
    }
    cmd.indexed = !cmd.rgb;
    cmd.imageCount = cmd.sizeZ * cmd.sizeT;
    if (!cmd.rgb)
      cmd.imageCount *= cmd.sizeC;
    else {
      cmd.imageCount *= (cmd.sizeC / 3);
    }

    cmd.dimensionOrder = r.metaTemp.getDimensionOrder(coreIndex);
  }

  /**
   * Writes extracted dimension sizes to CoreMetadata
   * 
   * @param coreIndex
   */
  private void setCoreDimensionSizes(int coreIndex) {
    CoreMetadata cmd = r.getCore().get(coreIndex);
    cmd.sizeX = r.metaTemp.getDimension(coreIndex, DimensionKey.X).size;
    cmd.sizeY = r.metaTemp.getDimension(coreIndex, DimensionKey.Y).size;
    cmd.sizeZ = r.metaTemp.getDimension(coreIndex, DimensionKey.Z).size;
    cmd.sizeT = r.metaTemp.getDimension(coreIndex, DimensionKey.T).size;
    cmd.rgb = (r.metaTemp.getDimension(coreIndex, DimensionKey.X).bytesInc % 3) == 0;
    if (cmd.rgb)
      r.metaTemp.getDimension(coreIndex, DimensionKey.X).bytesInc /= 3;

    if (extras > 1) {
      if (cmd.sizeZ == 1)
        cmd.sizeZ = extras;
      else {
        if (cmd.sizeT == 0)
          cmd.sizeT = extras;
        else
          cmd.sizeT *= extras;
      }
    }

    if (cmd.sizeX == 0)
      cmd.sizeX = 1;
    if (cmd.sizeY == 0)
      cmd.sizeY = 1;
    if (cmd.sizeC == 0)
      cmd.sizeC = 1;
    if (cmd.sizeZ == 0)
      cmd.sizeZ = 1;
    if (cmd.sizeT == 0)
      cmd.sizeT = 1;
  }

  /**
   * Sets CoreMetadata.pixelType depending on extracted x bytesInc
   * 
   * @param coreIndex
   * @throws FormatException
   */
  private void setPixelType(int coreIndex) throws FormatException {
    CoreMetadata cmd = r.getCore().get(coreIndex);
    long xBytesInc = r.metaTemp.getDimension(coreIndex, DimensionKey.X).bytesInc;
    cmd.pixelType = FormatTools.pixelTypeFromBytes((int) xBytesInc, false, true);
  }

  /**
   * Extracts information from dimension descriptions and writes it to reader's
   * {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param image
   *          image / core index
   * @throws FormatException
   */
  public void translateAttachmentNodes(Element imageNode, int image)
      throws FormatException {
    boolean tilesAttachmentFound = false;
    NodeList attachmentNodes = getDescendantNodesWithName(imageNode, "Attachment");
    if (attachmentNodes == null)
      return;
    for (int i = 0; i < attachmentNodes.getLength(); i++) {
      Element attachment = (Element) attachmentNodes.item(i);

      String attachmentName = attachment.getAttribute("Name");

      if ("ContextDescription".equals(attachmentName)) {
        r.metaTemp.descriptions[image] = attachment.getAttribute("Content");
      } else if ("TileScanInfo".equals(attachmentName)) {
        NodeList tiles = getDescendantNodesWithName(attachment, "Tile");

        for (int tile = 0; tile < tiles.getLength(); tile++) {
          Element tileNode = (Element) tiles.item(tile);
          String posX = tileNode.getAttribute("PosX");
          String posY = tileNode.getAttribute("PosY");

          while (r.metaTemp.fieldPosX.size() < image) {
            r.metaTemp.fieldPosX.add(null);
          }
          while (r.metaTemp.fieldPosY.size() < image) {
            r.metaTemp.fieldPosY.add(null);
          }

          if (posX != null) {
            try {
              final Double number = DataTools.parseDouble(posX);
              r.metaTemp.fieldPosX.add(new Length(number, UNITS.METER));
            } catch (NumberFormatException e) {
              LMSFileReader.log.debug("", e);
              r.metaTemp.fieldPosX.add(null);
            }
          }
          if (posY != null) {
            try {
              final Double number = DataTools.parseDouble(posY);
              r.metaTemp.fieldPosY.add(new Length(number, UNITS.METER));
            } catch (NumberFormatException e) {
              LMSFileReader.log.debug("", e);
              r.metaTemp.fieldPosY.add(null);
            }
          }
        }
        tilesAttachmentFound = true;
      }
    }

    if (!tilesAttachmentFound) {
      NodeList confocalSettings = null;
      for (int i = 0; i < attachmentNodes.getLength(); i++) {
        Element attachment = (Element) attachmentNodes.item(i);

        String attachmentName = attachment.getAttribute("Name");

        if ("HardwareSetting".equals(attachmentName)) {
          confocalSettings = getDescendantNodesWithName(attachment, "ATLConfocalSettingDefinition");
          break;
        }
      }

      if (confocalSettings != null) {
        for (int i = 0; i < confocalSettings.getLength(); i++) {
          Element confocalSetting = (Element) confocalSettings.item(i);

          String value = confocalSetting.getAttribute("StagePosX");

          if (value != null && !value.trim().isEmpty()) {
            r.metaTemp.fieldPosX.add(new Length(DataTools.parseDouble(value.trim()), UNITS.METER));
          }

          value = confocalSetting.getAttribute("StagePosY");

          if (value != null && !value.trim().isEmpty()) {
            r.metaTemp.fieldPosY.add(new Length(DataTools.parseDouble(value.trim()), UNITS.METER));
          }
        }
      } else {
        r.metaTemp.fieldPosX.add(null);
        r.metaTemp.fieldPosY.add(null);
      }
    }
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
    NodeList attachmentNodes = getDescendantNodesWithName(imageNode, "Attachment");
    if (attachmentNodes == null) {
      return;
    }
    NodeList confocalSettings = null;
    for (int i = 0; i < attachmentNodes.getLength(); i++) {
      Element attachment = (Element) attachmentNodes.item(i);

      String attachmentName = attachment.getAttribute("Name");

      if ("HardwareSetting".equals(attachmentName)) {
        confocalSettings = getDescendantNodesWithName(attachment, "ATLConfocalSettingDefinition");
      }
    }

    if (scannerSettings == null && confocalSettings == null)
      return;

    r.metaTemp.expTimes[image] = new Double[r.getEffectiveSizeC()];
    r.metaTemp.gains[image] = new Double[r.getEffectiveSizeC()];
    r.metaTemp.detectorOffsets[image] = new Double[r.getEffectiveSizeC()];
    r.metaTemp.channelNames[image] = new String[r.getEffectiveSizeC()];
    r.metaTemp.exWaves[image] = new Double[r.getEffectiveSizeC()];

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

    if (confocalSettings != null) {
      for (int i = 0; i < confocalSettings.getLength(); i++) {
        Element confocalSetting = (Element) confocalSettings.item(i);

        String value = confocalSetting.getAttribute("Pinhole");

        if (value != null && !value.trim().isEmpty()) {
          r.metaTemp.pinholes[image] = DataTools.parseDouble(value.trim()) * METER_MULTIPLY;
        }

        value = confocalSetting.getAttribute("Zoom");

        // if (value != null && !value.trim().isEmpty()) {
        // r.metaTemp.zooms[image] = DataTools.parseDouble(value.trim());
        // }

        value = confocalSetting.getAttribute("ObjectiveName");

        if (value != null && !value.trim().isEmpty()) {
          // r.metaTemp.objectiveModels[image] = value.trim();
        }

        value = confocalSetting.getAttribute("FlipX");

        if (value != null && !value.trim().isEmpty()) {
          r.metaTemp.flipX[image] = "1".equals(value.trim());
          r.addSeriesMeta("Reverse X orientation", r.metaTemp.flipX[image]);
        }

        value = confocalSetting.getAttribute("FlipY");

        if (value != null && !value.trim().isEmpty()) {
          r.metaTemp.flipY[image] = "1".equals(value.trim());
          r.addSeriesMeta("Reverse Y orientation", r.metaTemp.flipY[image]);
        }

        value = confocalSetting.getAttribute("SwapXY");

        if (value != null && !value.trim().isEmpty()) {
          r.metaTemp.swapXY[image] = "1".equals(value.trim());
          r.addSeriesMeta("Swap XY orientation", r.metaTemp.swapXY[image]);
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

  /**
   * Extracts timestamps and writes them to reader's {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param image
   *          image / core index
   * @throws FormatException
   */
  public void translateTimestamps(Element imageNode, int image)
      throws FormatException {
    NodeList timeStampLists = getDescendantNodesWithName(imageNode, "TimeStampList");
    if (timeStampLists == null)
      return;

    Element timeStampList = (Element) timeStampLists.item(0);
    r.metaTemp.timestamps[image] = new Double[r.getImageCount()];

    // probe if timestamps are saved in the format of LAS AF 3.1 or newer
    String numberOfTimeStamps = timeStampList.getAttribute("NumberOfTimeStamps");
    if (numberOfTimeStamps != null && !numberOfTimeStamps.isEmpty()) {
      // LAS AF 3.1 (or newer) timestamps are available
      String timeStampsRaw = timeStampList.getTextContent();
      List<String> timeStamps = Arrays.asList(timeStampsRaw.split(" "));
      for (int stamp = 0; stamp < timeStamps.size(); stamp++) {
        if (stamp < r.getImageCount()) {
          String timestamp = timeStamps.get(stamp);
          r.metaTemp.timestamps[image][stamp] = translateSingleTimestamp(timestamp);
        }
      }
    } else {
      // probe if timestamps are saved in the format of LAS AF 3.0 or older
      NodeList timestampNodes = getDescendantNodesWithName(imageNode, "TimeStamp");
      if (timestampNodes != null) {
        // LAS AF 3.0 (or older) timestamps are available
        for (int stamp = 0; stamp < timestampNodes.getLength(); stamp++) {
          if (stamp < r.getImageCount()) {
            Element timestamp = (Element) timestampNodes.item(stamp);
            r.metaTemp.timestamps[image][stamp] = translateSingleTimestamp(timestamp);
          }
        }
      } else {
        return;
      }
    }

    r.metaTemp.acquiredDate[image] = r.metaTemp.timestamps[image][0];
  }

  public void translateLaserLines(Element imageNode, int image) {
    if (hardwareSetting == null)
      return;

    Node confocalSettings = getChildNodeWithName(hardwareSetting, "ATLConfocalSettingDefinition");
    if (confocalSettings == null)
      return;
    Node laserArray = getChildNodeWithName(confocalSettings, "LaserArray");
    if (laserArray == null)
      return;
    NodeList lasers = laserArray.getChildNodes();

    for (int i = 0; i < lasers.getLength(); i++) {
      Element laserNode;
      try {
        laserNode = (Element) lasers.item(i);
      } catch (Exception e) {
        continue;
      }
      String powerState = laserNode.getAttribute("PowerState");
      String outputPowerWatt = laserNode.getAttribute("OutputPowerWatt");
      Laser laser = new Laser();
      laser.isActive = powerState.equals("On") && !outputPowerWatt.equals("0");
      laser.wavelength = parseDouble(laserNode.getAttribute("Wavelength"));
      laser.name = laserNode.getAttribute("LaserName");
      laser.wavelengthUnit = "nm";
      r.metaTemp.lasers.get(image).add(laser);
    }

    Node aotfList = getChildNodeWithName(confocalSettings, "AotfList");
    if (aotfList == null)
      return;
    NodeList aotfs = aotfList.getChildNodes();
    for (int i = 0; i < aotfs.getLength(); i++) {

    }
  }

  /**
   * Extracts laser information and writes it to reader's
   * {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param image
   *          image / core index
   * @throws FormatException
   */
  public void translateLaserLines2(Element imageNode, int image)
      throws FormatException {

    NodeList aotfLists = getDescendantNodesWithName(imageNode, "AotfList");
    if (aotfLists == null || aotfLists.getLength() == 0)
      return;

    int baseIntensityIndex = 0;

    for (int channel = 0; channel < aotfLists.getLength(); channel++) {
      Element aotf = (Element) aotfLists.item(channel);
      NodeList laserLines = getDescendantNodesWithName(aotf, "LaserLineSetting");
      if (laserLines == null)
        return;
      String gpName = aotf.getParentNode().getParentNode().getNodeName();
      // might need parent for attachment
      boolean isMaster = gpName.endsWith("Sequential_Master") ||
          gpName.endsWith("Attachment");
      r.metaTemp.laserFrap.get(image).add(gpName.endsWith("FRAP_Master"));
      for (int laser = 0; laser < laserLines.getLength(); laser++) {
        Element laserLine = (Element) laserLines.item(laser);

        if (isMaster) {
          continue;
        }
        String lineIndexS = laserLine.getAttribute("LineIndex");
        String qual = laserLine.getAttribute("Qualifier");
        int lineIndex = lineIndexS == null || lineIndexS.trim().isEmpty() ? 0 : Integer.parseInt(lineIndexS.trim());
        int qualifier = qual == null || qual.trim().isEmpty() ? 0 : Integer.parseInt(qual.trim());

        lineIndex += (2 - (qualifier / 10));
        if (lineIndex < 0) {
          continue;
          // index = 0;
        }

        String v = laserLine.getAttribute("LaserLine");
        Double wavelength = 0d;
        if (v != null && !v.trim().isEmpty()) {
          wavelength = DataTools.parseDouble(v.trim());
        }
        // if (lineIndex < r.metaTemp.laserWavelength.get(image).size()) {
        // r.metaTemp.laserWavelength.get(image).set(lineIndex, wavelength);
        // }
        // else {
        // for (int i=r.metaTemp.laserWavelength.get(image).size(); i<lineIndex; i++) {
        // r.metaTemp.laserWavelength.get(image).add(Double.valueOf(0));
        // }
        r.metaTemp.laserWavelength.get(image).add(wavelength);
        // }

        String intensity = laserLine.getAttribute("IntensityDev");
        Double realIntensity = 0d;
        if (intensity != null && !intensity.trim().isEmpty()) {
          realIntensity = DataTools.parseDouble(intensity.trim());
          if (realIntensity == null) {
            realIntensity = 0d;
          }
        }
        realIntensity = 100d - realIntensity;

        int realIndex = baseIntensityIndex + lineIndex;

        if (realIndex < r.metaTemp.laserIntensity.get(image).size()) {
          r.metaTemp.laserIntensity.get(image).set(realIndex, realIntensity);
        } else {
          while (realIndex < r.metaTemp.laserIntensity.get(image).size()) {
            r.metaTemp.laserIntensity.get(image).add(100d);
          }
          r.metaTemp.laserIntensity.get(image).add(realIntensity);
        }
      }

      baseIntensityIndex += r.metaTemp.laserWavelength.get(image).size();
    }
  }

  /**
   * Extracts ROIs and writes them to reader's {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param image
   *          image / core index
   * @throws FormatException
   */
  public void translateROIs(Element imageNode, int image)
      throws FormatException {
    NodeList rois = getDescendantNodesWithName(imageNode, "Annotation");
    if (rois == null)
      return;
    r.metaTemp.imageROIs[image] = new ROI[rois.getLength()];

    for (int r = 0; r < rois.getLength(); r++) {
      Element roiNode = (Element) rois.item(r);

      ROI roi = new ROI();

      String type = roiNode.getAttribute("type");
      if (type != null && !type.trim().isEmpty()) {
        roi.type = Integer.parseInt(type.trim());
      }
      String color = roiNode.getAttribute("color");
      if (color != null && !color.trim().isEmpty()) {
        roi.color = Long.parseLong(color.trim());
      }
      roi.name = roiNode.getAttribute("name");
      roi.fontName = roiNode.getAttribute("fontName");
      roi.fontSize = roiNode.getAttribute("fontSize");

      Double transX = DataTools.parseDouble(roiNode.getAttribute("transTransX"));
      if (transX != null) {
        roi.transX = transX / this.r.metaTemp.physicalSizeXs.get(image);
      }
      Double transY = DataTools.parseDouble(roiNode.getAttribute("transTransY"));
      if (transY != null) {
        roi.transY = transY / this.r.metaTemp.physicalSizeYs.get(image);
      }
      transX = DataTools.parseDouble(roiNode.getAttribute("transScalingX"));
      if (transX != null) {
        roi.scaleX = transX / this.r.metaTemp.physicalSizeXs.get(image);
      }
      transY = DataTools.parseDouble(roiNode.getAttribute("transScalingY"));
      if (transY != null) {
        roi.scaleY = transY / this.r.metaTemp.physicalSizeYs.get(image);
      }
      Double rotation = DataTools.parseDouble(roiNode.getAttribute("transRotation"));
      if (rotation != null) {
        roi.rotation = rotation;
      }
      String linewidth = roiNode.getAttribute("linewidth");
      try {
        if (linewidth != null && !linewidth.trim().isEmpty()) {
          roi.linewidth = Integer.parseInt(linewidth.trim());
        }
      } catch (NumberFormatException e) {
      }

      roi.text = roiNode.getAttribute("text");

      NodeList vertices = getDescendantNodesWithName(roiNode, "Vertex");
      if (vertices == null) {
        continue;
      }

      for (int v = 0; v < vertices.getLength(); v++) {
        Element vertex = (Element) vertices.item(v);
        String xx = vertex.getAttribute("x");
        String yy = vertex.getAttribute("y");

        if (xx != null && !xx.trim().isEmpty()) {
          roi.x.add(DataTools.parseDouble(xx.trim()));
        }
        if (yy != null && !yy.trim().isEmpty()) {
          roi.y.add(DataTools.parseDouble(yy.trim()));
        }
      }
      this.r.metaTemp.imageROIs[image][r] = roi;

      if (getDescendantNodesWithName(imageNode, "ROI") != null) {
        this.r.metaTemp.alternateCenter = true;
      }
    }
  }

  /**
   * Extracts single ROIs and writes them to reader's {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param image
   *          image / core index
   * @throws FormatException
   */
  public void translateSingleROIs(Element imageNode, int image)
      throws FormatException {
    if (r.metaTemp.imageROIs[image] != null)
      return;
    NodeList children = getDescendantNodesWithName(imageNode, "ROI");
    if (children == null)
      return;
    children = getDescendantNodesWithName((Element) children.item(0), "Children");
    if (children == null)
      return;
    children = getDescendantNodesWithName((Element) children.item(0), "Element");
    if (children == null)
      return;
    r.metaTemp.imageROIs[image] = new ROI[children.getLength()];

    for (int r = 0; r < children.getLength(); r++) {
      NodeList rois = getDescendantNodesWithName((Element) children.item(r), "ROISingle");

      Element roiNode = (Element) rois.item(0);
      ROI roi = new ROI();

      String type = roiNode.getAttribute("RoiType");
      if (type != null && !type.trim().isEmpty()) {
        roi.type = Integer.parseInt(type.trim());
      }
      String color = roiNode.getAttribute("Color");
      if (color != null && !color.trim().isEmpty()) {
        roi.color = Long.parseLong(color.trim());
      }
      Element parent = (Element) roiNode.getParentNode();
      parent = (Element) parent.getParentNode();
      roi.name = parent.getAttribute("Name");

      NodeList vertices = getDescendantNodesWithName(roiNode, "P");

      double sizeX = this.r.metaTemp.physicalSizeXs.get(image);
      double sizeY = this.r.metaTemp.physicalSizeYs.get(image);

      for (int v = 0; v < vertices.getLength(); v++) {
        Element vertex = (Element) vertices.item(v);
        String xx = vertex.getAttribute("X");
        String yy = vertex.getAttribute("Y");

        if (xx != null && !xx.trim().isEmpty()) {
          Double x = DataTools.parseDouble(xx.trim());
          if (x != null) {
            roi.x.add(x / sizeX);
          }
        }
        if (yy != null && !yy.trim().isEmpty()) {
          Double y = DataTools.parseDouble(yy.trim());
          if (y != null) {
            roi.y.add(y / sizeY);
          }
        }
      }

      Element transform = (Element) getDescendantNodesWithName(roiNode, "Transformation").item(0);

      Double rotation = DataTools.parseDouble(transform.getAttribute("Rotation"));
      if (rotation != null) {
        roi.rotation = rotation;
      }

      Element scaling = (Element) getDescendantNodesWithName(transform, "Scaling").item(0);
      Double scaleX = DataTools.parseDouble(scaling.getAttribute("XScale"));
      Double scaleY = DataTools.parseDouble(scaling.getAttribute("YScale"));
      if (scaleX != null) {
        roi.scaleX = scaleX;
      }
      if (scaleY != null) {
        roi.scaleY = scaleY;
      }

      Element translation = (Element) getDescendantNodesWithName(transform, "Translation").item(0);
      Double transX = DataTools.parseDouble(translation.getAttribute("X"));
      Double transY = DataTools.parseDouble(translation.getAttribute("Y"));
      if (transX != null) {
        roi.transX = transX / sizeX;
      }
      if (transY != null) {
        roi.transY = transY / sizeY;
      }

      this.r.metaTemp.imageROIs[image][r] = roi;
    }
  }

  private void translatePhysicalChannelInfo(Element imageNode, int image) {
    if (hardwareSetting == null)
      return;

    Node ldmBlockSequential = getChildNodeWithName(hardwareSetting, "LDM_Block_Sequential");
    Node ldmBlockList = getChildNodeWithName(ldmBlockSequential, "LDM_Block_Sequential_List");
    NodeList sequentialConfocalSettings = ldmBlockList.getChildNodes();

    // add multiband cutin/out information to channel as filter
    int sequenceIndex = 0;
    for (int i = 0; i < sequentialConfocalSettings.getLength(); i++) {
      Node spectro = getChildNodeWithName(sequentialConfocalSettings.item(i), "Spectro");
      if (spectro == null)
        continue;

      NodeList multibands = spectro.getChildNodes();
      int multibandIndex = 0;
      for (int k = 0; k < multibands.getLength(); k++) {
        Element multiband;
        try {
          printNode(multibands.item(k));
          multiband = (Element) multibands.item(k);
        } catch (Exception e) {
          continue;
        }

        // assuming that multiband index maps detector index within a sequential setting
        DetectorSetting setting = r.metaTemp.getDetectorSetting(image, sequenceIndex, multibandIndex);
        if (setting != null) {
          int channelIndex = Integer.parseInt(multiband.getAttribute("Channel")) - 1;

          Filter filter = new Filter();
          filter.cutIn = DataTools.parseDouble(multiband.getAttribute("LeftWorld"));
          filter.cutOut = DataTools.parseDouble(multiband.getAttribute("RightWorld"));
          filter.dye = multiband.getAttribute("DyeName");
          filter.sequenceIndex = sequenceIndex;
          filter.multibandIndex = multibandIndex;

          r.metaTemp.filters.get(image).add(filter);

        }

        multibandIndex++;
      }
      sequenceIndex++;
    }

    // PMT transmission detectors don't have a matching multiband, so we have to
    // manually create a filter for them
    for (DetectorSetting setting : r.metaTemp.detectorSettings.get(image)) {
      if (setting.transmittedLightMode) {
        Filter filter = new Filter();
        filter.sequenceIndex = setting.sequenceIndex;
        filter.multibandIndex = setting.detectorListIndex;
        r.metaTemp.filters.get(image).add(filter);
      }
    }

    Collections.sort(r.metaTemp.filters.get(image), new FilterComparator());

    // assign filters to channels
    for (int i = 0; i < r.metaTemp.channels.get(image).size(); i++) {
      if (i >= r.metaTemp.filters.get(image).size())
        break;
      r.metaTemp.channels.get(image).get(i).filter = r.metaTemp.filters.get(image).get(i);
      r.metaTemp.channels.get(image).get(i).dye = r.metaTemp.channels.get(image).get(i).filter.dye;
    }
  }

  class FilterComparator implements Comparator<Filter> {
    @Override
    public int compare(Filter a, Filter b) {
      if (a.sequenceIndex < b.sequenceIndex)
        return -1;
      else if (a.sequenceIndex > b.sequenceIndex)
        return 1;
      else {
        if (a.multibandIndex < b.multibandIndex)
          return -1;
        else if (a.multibandIndex > b.multibandIndex)
          return 1;
        else
          return 0;
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
   * Extracts detector information and writes it to reader's
   * {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param image
   *          image / core index
   * @throws FormatException
   */
  public void translateDetectors(Element imageNode, int image)
      throws FormatException {
    if (hardwareSetting == null)
      return;

    Node definition = getChildNodeWithName(hardwareSetting, "ATLConfocalSettingDefinition");
    if (definition == null)
      return;

    String zoomS = getAttributeValue(definition, "Zoom");
    double zoom = parseDouble(zoomS);

    // main confocal settings > detector list: instrument detectors
    Node detectorList = getChildNodeWithName(definition, "DetectorList");
    NodeList detectorNodes = detectorList.getChildNodes();
    List<Detector> detectors = createDetectors(detectorNodes, zoom);

    r.metaTemp.detectors.get(image).addAll(detectors);

    // LDM block sequential list confocal settings: channel detector settings
    Node ldmBlockSequential = getChildNodeWithName(hardwareSetting, "LDM_Block_Sequential");
    Node ldmBlockList = getChildNodeWithName(ldmBlockSequential, "LDM_Block_Sequential_List");
    NodeList sequentialConfocalSettings = ldmBlockList.getChildNodes();

    int sequenceIndex = 0;
    for (int i = 0; i < sequentialConfocalSettings.getLength(); i++) {
      Node seqDetectorList = getChildNodeWithName(sequentialConfocalSettings.item(i), "DetectorList");
      if (seqDetectorList == null)
        continue;

      NodeList seqDetectorNodes = seqDetectorList.getChildNodes();
      List<DetectorSetting> detectorSettings = createDetectorSettings(seqDetectorNodes, detectors, sequenceIndex);

      for (DetectorSetting setting : detectorSettings) {
        if (setting.isActive)
          r.metaTemp.detectorSettings.get(image).add(setting);
      }

      sequenceIndex++;
    }

    // link detector and channel information
    // assuming that the order of channels in LMS XML maps the order of active
    // detectors over all LDM block sequential lists
    for (int i = 0; i < r.metaTemp.detectorSettings.get(image).size(); i++) {
      if (i < r.metaTemp.channels.get(image).size()) {
        r.metaTemp.channels.get(image).get(i).detectorSetting = r.metaTemp.detectorSettings.get(image).get(i);
        r.metaTemp.channels.get(image).get(i).name = r.metaTemp.detectorSettings.get(image).get(i).channelName;
      }
    }
  }

  private List<Detector> createDetectors(NodeList detectorNodes, double zoom) {
    List<Detector> detectors = new ArrayList<Detector>();

    for (int i = 0; i < detectorNodes.getLength(); i++) {
      Element detectorNode;
      try {
        detectorNode = (Element) detectorNodes.item(i);
      } catch (Exception e) {
        continue;
      }

      // for channel references such as "100": using the less secure assumption that
      // detectors and their mapped channels are listed in the same order
      // if (channelIndex >= r.metaTemp.channels.get(image).size())
      // channelIndex = r.metaTemp.instrumentDetectors.get(image).size();

      Detector detector = new Detector();

      detector.model = detectorNode.getAttribute("Name");
      detector.type = detectorNode.getAttribute("Type");
      detector.zoom = zoom;

      detectors.add(detector);
    }

    return detectors;
  }

  private List<DetectorSetting> createDetectorSettings(NodeList detectorNodes, List<Detector> detectors,
      int sequenceIndex) {
    List<DetectorSetting> detectorSettings = new ArrayList<DetectorSetting>();

    int detectorListIndex = 0;
    for (int i = 0; i < detectorNodes.getLength(); i++) {
      Element detectorNode;
      try {
        detectorNode = (Element) detectorNodes.item(i);
      } catch (Exception e) {
        continue;
      }

      DetectorSetting setting = new DetectorSetting();

      String gainS = detectorNode.getAttribute("Gain");
      setting.gain = parseDouble(gainS);

      String offsetS = detectorNode.getAttribute("Offset");
      setting.offset = parseDouble(offsetS);

      String isActiveS = detectorNode.getAttribute("IsActive");
      setting.isActive = "1".equals(isActiveS);

      String channelS = detectorNode.getAttribute("Channel");
      setting.channelIndex = parseInt(channelS) - 1;

      setting.channelName = detectorNode.getAttribute("ChannelName");

      setting.sequenceIndex = sequenceIndex;
      setting.detectorListIndex = detectorListIndex;

      String detectorName = detectorNode.getAttribute("Name");

      setting.transmittedLightMode = detectorName.toLowerCase().contains("trans")
          && setting.channelName.equals("Transmission Channel");

      for (Detector detector : detectors) {
        if (detectorName.equals(detector.model)) {
          setting.detector = detector;
          break;
        }
      }

      detectorSettings.add(setting);
      detectorListIndex++;
    }

    return detectorSettings;
  }

  /**
   * Extracts detector information and writes it to reader's
   * {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param image
   *          image / core index
   * @throws FormatException
   */
  private void translateDetectors2(Element imageNode, int image)
      throws FormatException {
    NodeList definitions = getDescendantNodesWithName(imageNode, "ATLConfocalSettingDefinition");
    if (definitions == null)
      return;
    final List<String> channels = new ArrayList<String>();
    int nextChannel = 0;
    for (int definition = 0; definition < definitions.getLength(); definition++) {
      Element definitionNode = (Element) definitions.item(definition);
      String parentName = definitionNode.getParentNode().getNodeName();
      boolean isMaster = parentName.endsWith("Master");
      NodeList detectors = getDescendantNodesWithName(definitionNode, "Detector");
      if (detectors == null)
        return;
      int count = 0;
      for (int d = 0; d < detectors.getLength(); d++) {
        Element detector = (Element) detectors.item(d);
        NodeList multibands = null;
        if (!isMaster) {
          multibands = getDescendantNodesWithName(definitionNode, "MultiBand");
        }
        String v = detector.getAttribute("Gain");
        Double gain = v == null || v.trim().isEmpty() ? null : DataTools.parseDouble(v.trim());
        v = detector.getAttribute("Offset");
        Double offset = v == null || v.trim().isEmpty() ? null : DataTools.parseDouble(v.trim());

        boolean active = "1".equals(detector.getAttribute("IsActive"));
        String c = detector.getAttribute("Channel");
        int channel = (c == null || c.trim().length() == 0) ? 0 : Integer.parseInt(c);
        if (active) {
          // if (r.metaTemp.detectorIndexes.get(image) != null &&
          // r.metaTemp.detectorModels.get(image) != null) {
          // r.metaTemp.detectorModels.get(image).add(r.metaTemp.detectorIndexes.get(image).get(channel));
          // }

          Element multiband = null;

          if (multibands != null) {
            for (int i = 0; i < multibands.getLength(); i++) {
              Element mb = (Element) multibands.item(i);
              if (channel == Integer.parseInt(mb.getAttribute("Channel"))) {
                multiband = mb;
                break;
              }
            }
          }

          if (multiband != null) {
            String dye = multiband.getAttribute("DyeName");
            if (!channels.contains(dye)) {
              channels.add(dye);
            }

            Double cutIn = DataTools.parseDouble(multiband.getAttribute("LeftWorld"));
            Double cutOut = DataTools.parseDouble(multiband.getAttribute("RightWorld"));
            if (cutIn != null && cutIn.intValue() > 0) {
              Length in = FormatTools.getCutIn((double) Math.round(cutIn));
              if (in != null) {
                r.metaTemp.cutIns.get(image).add(in);
              }
            }
            if (cutOut != null && cutOut.intValue() > 0) {
              Length out = FormatTools.getCutOut((double) Math.round(cutOut));
              if (out != null) {
                r.metaTemp.cutOuts.get(image).add(out);
              }
            }
          } else {
            channels.add("");
          }

          if (!isMaster) {
            if (channel < nextChannel) {
              nextChannel = 0;
            }

            if (nextChannel < r.getEffectiveSizeC()) {
              if (r.metaTemp.gains[image] != null) {
                r.metaTemp.gains[image][nextChannel] = gain;
              }
              if (r.metaTemp.detectorOffsets[image] != null) {
                r.metaTemp.detectorOffsets[image][nextChannel] = offset;
              }
            }

            nextChannel++;
          }
        } else {
          count++;
        }
        if (active && r.metaTemp.activeDetector.get(image) != null) {
          r.metaTemp.activeDetector.get(image).add(active);
        }
      }
      // Store values to check if actually it is active.
      if (!isMaster) {
        r.metaTemp.laserActive.get(image).add(count < detectors.getLength());
      }
    }

    if (channels != null && r.metaTemp.channelNames[image] != null) {
      for (int i = 0; i < r.getEffectiveSizeC(); i++) {
        int index = i + channels.size() - r.getEffectiveSizeC();
        if (index >= 0 && index < channels.size()) {
          if (r.metaTemp.channelNames[image][i] == null ||
              r.metaTemp.channelNames[image][i].trim().isEmpty()) {
            r.metaTemp.channelNames[image][i] = channels.get(index);
          }
        }
      }
    }
  }

  /**
   * Translates hex timestamp string to double value
   * 
   * @param timestamp
   *          timestamp in format
   * @return number of seconds
   */
  private double translateSingleTimestamp(String timestamp) {
    timestamp = timestamp.trim();
    int stampLowStart = Math.max(0, timestamp.length() - 8);
    int stampHighEnd = Math.max(0, stampLowStart);
    String stampHigh = timestamp.substring(0, stampHighEnd);
    String stampLow = timestamp.substring(stampLowStart, timestamp.length());
    long high = stampHigh == null || stampHigh.trim().isEmpty() ? 0
        : Long.parseLong(stampHigh.trim(), 16);
    long low = stampLow == null || stampLow.trim().isEmpty() ? 0
        : Long.parseLong(stampLow.trim(), 16);
    long milliseconds = DateTools.getMillisFromTicks(high, low);
    double seconds = (double) milliseconds / 1000;
    return seconds;
  }

  /**
   * Translates the content of a timestamp node to double value
   * 
   * @param imageNode
   *          timestamp node
   * @param image
   *          image / core index
   * @throws FormatException
   */
  private double translateSingleTimestamp(Element timestamp) {
    String stampHigh = timestamp.getAttribute("HighInteger");
    String stampLow = timestamp.getAttribute("LowInteger");
    long high = stampHigh == null || stampHigh.trim().isEmpty() ? 0
        : Long.parseLong(stampHigh.trim());
    long low = stampLow == null || stampLow.trim().isEmpty() ? 0
        : Long.parseLong(stampLow.trim());

    long milliseconds = DateTools.getMillisFromTicks(high, low);
    double seconds = (double) milliseconds / 1000;
    return seconds;
  }

  /**
   * Extracts user comments and adds them to the reader's {@link CoreMetadata}
   * 
   * @param imageNode
   * @param image
   * @throws FormatException
   */
  private void addUserCommentMeta(Element imageNode, int image) throws FormatException {
    NodeList attachmentNodes = getDescendantNodesWithName(imageNode, "User-Comment");
    if (attachmentNodes == null)
      return;
    for (int i = 0; i < attachmentNodes.getLength(); i++) {
      Node attachment = attachmentNodes.item(i);
      r.addSeriesMeta("User-Comment[" + i + "]", attachment.getTextContent());
      if (i == 0 && r.metaTemp.descriptions[image] == null) {
        r.metaTemp.descriptions[image] = attachment.getTextContent();
      }
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

  private Element getImageDescription(Element root) {
    return (Element) root.getElementsByTagName("ImageDescription").item(0);
  }

  private NodeList getChannelDescriptionNodes(Element root) {
    Element imageDescription = getImageDescription(root);
    Element channels = (Element) imageDescription.getElementsByTagName("Channels").item(0);
    return channels.getElementsByTagName("ChannelDescription");
  }

  private NodeList getDimensionDescriptionNodes(Element root) {
    Element imageDescription = getImageDescription(root);
    Element channels = (Element) imageDescription.getElementsByTagName("Dimensions").item(0);
    return channels.getElementsByTagName("DimensionDescription");
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

  private Node getChildNodeWithName(Node node, String nodeName) {
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getNodeName().equals(nodeName))
        return children.item(i);
    }
    return null;
  }

  private Node getNodeWithAttribute(NodeList nodes, String attributeName, String attributeValue) {
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      Node attribute = node.getAttributes().getNamedItem(attributeName);
      if (attribute != null && attribute.getTextContent().equals(attributeValue))
        return node;
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
