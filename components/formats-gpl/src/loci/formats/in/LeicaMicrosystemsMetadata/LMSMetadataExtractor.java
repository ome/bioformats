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
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
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
import loci.formats.in.LeicaMicrosystemsMetadata.LMSFileReader.ImageFormat;
import loci.common.DataTools;
import loci.common.DateTools;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.xml.model.primitives.Color;

/**
 * This class extracts metadata information from Leica Microsystems image XML and stores it in the reader's CoreMetadata and MetadataTempBuffer
 * 
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class LMSMetadataExtractor {
    // -- Constants --
    private static final long METER_MULTIPLY = 1000000;

    // -- Fields --
    private LMSFileReader r;
    private HashMap<Long, String> bytesIncPerAxis;
    int extras = 1;

    // -- Constructor --
    public LMSMetadataExtractor(LMSFileReader reader){
      this.r = reader;  
    }

    // -- Methods --
    /**
     * Extracts all information from Leica image xml Documents and writes it to reader's {@link CoreMetadata} and {@link MetadataTempBuffer}
     * @param docs list of Leica xml documents
     * @throws FormatException
     * @throws IOException
     */
    public void translateMetadata(List<LMSImageXmlDocument> docs) throws FormatException, IOException{
      int len = docs.size();
      r.setCore(new ArrayList<CoreMetadata>(len));
      r.getCore().clear();

      //create CoreMetadata for each xml referenced image (=series)
      for (int i = 0; i < docs.size(); i++) {
          CoreMetadata ms = new CoreMetadata();
          bytesIncPerAxis = new HashMap<Long, String>();
          r.getCore().add(ms);
          r.setSeries(i);

          Node image = docs.get(i).getImageNode();
          r.metaTemp.imageNames[i] = docs.get(i).getImageName();
          translateImage((Element)image, i);
      }
      r.setSeries(0);

      // int totalSeries = 0;
      // for (int count : rawMetadata.tileCount) {
      //     totalSeries += count;
      // }
      ArrayList<CoreMetadata> newCore = new ArrayList<CoreMetadata>();
      for (int i = 0; i < r.getCore().size(); i++) {
          for (int tile = 0; tile < r.metaTemp.tileCount[i]; tile++) {
              newCore.add(r.getCore().get(i));
          }
      }
      r.setCore(newCore);
    }

  /**
   * Extracts information from element node and writes it to reader's {@link CoreMetadata} and {@link MetadataTempBuffer}
   * @param img node with tag name "element" from Leica XML
   * @param i image /core index
   * @throws FormatException
   */
  public void translateImage(Element image, int i) throws FormatException{
    CoreMetadata ms = r.getCore().get(i);
    ms.orderCertain = true;
    ms.metadataComplete = true;
    ms.littleEndian = true;
    ms.falseColor = true;

    translateChannelDescriptions(image, i);
    translateDimensionDescriptions(image, i);
    translateAttachmentNodes(image, i);
    translateScannerSettings(image, i);
    translateFilterSettings(image, i);
    translateTimestamps(image, i);
    translateLaserLines(image, i);
    translateROIs(image, i);
    translateSingleROIs(image, i);
    translateDetectors(image, i);

    final Deque<String> nameStack = new ArrayDeque<String>();
    populateOriginalMetadata(image, nameStack);
    addUserCommentMeta(image, i);
  }

  /***
   * Extracts i.a. channel number and luts from channel descriptions and writes it to reader's {@link CoreMetadata} and {@link MetadataTempBuffer}
   * @param imageNode Image node from Leica xml
   * @param i image / core index
   * @throws FormatException
   */
  public void translateChannelDescriptions(Element imageNode, int i)
    throws FormatException
  {
    CoreMetadata ms = r.getCore().get(i);

    NodeList channels = getChannelDescriptionNodes(imageNode);
    ms.sizeC = channels.getLength();

    List<String> luts = new ArrayList<String>();
    r.metaTemp.channelPrios = new int[r.metaTemp.tileCount.length][];

    for (int ch=0; ch<channels.getLength(); ch++) {
      Element channel = (Element) channels.item(ch);

      luts.add(channel.getAttribute("LUTName"));
      long bytesInc = parseLong(channel.getAttribute("BytesInc"));
      if (bytesInc > 0) {
        bytesIncPerAxis.put(bytesInc, "C");
      }
    }

    //BGR order is assumed when no LUT names exist, RGB order is only assumed when explicitly described //hotfix
    r.metaTemp.inverseRgb[i] = !(channels.getLength() >= 3 && 
      ((Element)channels.item(0)).getAttribute("LUTName").equals("Red") && 
      ((Element)channels.item(1)).getAttribute("LUTName").equals("Green") &&
      ((Element)channels.item(2)).getAttribute("LUTName").equals("Blue"));

    translateLuts(luts, i);
  }

  /***
   * Translates raw channel luts of an image to Colors and writes it to reader's {@link MetadataTempBuffer}
   * @param luts list of raw lut names / values from Leica XML
   */
  private void translateLuts(List<String> luts, int imgIndex){
    CoreMetadata ms = r.getCore().get(imgIndex);
    ArrayList<Color> channelColors = new ArrayList<Color>(ms.sizeC);
    r.metaTemp.channelPrios[imgIndex] = new int[ms.sizeC];
    
    int nextLut = 0;
    //foreach channel of image
    for (int channel=0; channel<ms.sizeC; channel++) {
      if (nextLut < luts.size()) {
        Color lutColor = translateLut(luts.get(nextLut));
        channelColors.add(lutColor);
        r.metaTemp.channelPrios[imgIndex][channel] = getChannelPriority(luts.get(nextLut));
        nextLut++;
      }
    }
    r.metaTemp.channelColors.add(channelColors);
  }

  private int getChannelPriority(String lutName){
    switch (lutName){
      case "red": return 0;
      case "green": return 1;
      case "blue": return 2;
      case "cyan": return 3;
      case "magenta": return 4;
      case "yellow": return 5;
      case "black": return 6;
      case "gray": return 7;
      case "":
      default:
        return 8;
    }
  }

  /***
   * Translates Leica XML lut name / value to Color 
   * @param lutName
   * @return
   */
  private Color translateLut(String lutName){
    lutName = lutName.replaceAll("\\s+","");
    //some LUTs are stored as gradients
    Pattern pattern = Pattern.compile("Gradient\\(\\d+,\\d+,\\d+\\)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(lutName);
    if (matcher.find()){
      String[] rgb = lutName.substring(9, lutName.length() - 1).split(",");
      return new Color(Integer.parseInt(rgb[2]),
        Integer.parseInt(rgb[1]), 
        Integer.parseInt(rgb[0]), 
        255);
    } else {
      switch(lutName.toLowerCase()){
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
    //TODO: numeric lut handling
  }

  /**
   * Extracts information from dimension descriptions and writes it to reader's {@link CoreMetadata} and {@link MetadataTempBuffer}
   * @param imageNode Image node from Leica xml
   * @param image image / core index
   * @throws FormatException
   */
  private void translateDimensionDescriptions(Element imageNode, int image) throws FormatException{
    CoreMetadata ms = r.getCore().get(image);
    NodeList dimensions = getDimensionDescriptionNodes(imageNode);

    Double physicalSizeX = null;
    Double physicalSizeY = null;
    Double physicalSizeZ = null;

    for (int dim=0; dim<dimensions.getLength(); dim++) {
      Element dimension = (Element) dimensions.item(dim);

      int dimId = parseInt(dimension.getAttribute("DimID"));
      int dimSize = parseInt(dimension.getAttribute("NumberOfElements"));
      long bytesInc = parseLong(dimension.getAttribute("BytesInc"));
      Double physicalLenPp = parseDouble(dimension.getAttribute("Length"));
      String unit = dimension.getAttribute("Unit");

      double offByOnePhysicalLenPp = 0d;
      if (dimSize > 1) {
        offByOnePhysicalLenPp = physicalLenPp / dimSize;
        physicalLenPp /= (dimSize - 1); //length per pixel
      }
      else {
        physicalLenPp = 0d;
      }
      
      if (unit.equals("Ks")) {
        physicalLenPp /= 1000;
        offByOnePhysicalLenPp /= 1000;
      }
      else if (unit.equals("m")) {
        physicalLenPp *= METER_MULTIPLY;
        offByOnePhysicalLenPp *= METER_MULTIPLY;
      }

      boolean oldPhysicalSize = r.useOldPhysicalSizeCalculation();
      switch (dimId) {
        case 1: // X axis
          ms.sizeX = dimSize;
          ms.rgb = (bytesInc % 3) == 0;
          if (ms.rgb) bytesInc /= 3;
          ms.pixelType =
            FormatTools.pixelTypeFromBytes((int) bytesInc, false, true);
          physicalSizeX = oldPhysicalSize ? offByOnePhysicalLenPp : physicalLenPp;
          break;
        case 2: // Y axis
          if (ms.sizeY != 0) {
            if (ms.sizeZ == 1) {
              ms.sizeZ = dimSize;
              bytesIncPerAxis.put(bytesInc, "Z");
              physicalSizeZ = physicalLenPp;
            }
            else if (ms.sizeT == 1) {
              ms.sizeT = dimSize;
              bytesIncPerAxis.put(bytesInc, "T");
            }
          }
          else {
            ms.sizeY = dimSize;
            physicalSizeY = oldPhysicalSize ? offByOnePhysicalLenPp : physicalLenPp;
          }
          break;
        case 3: // Z axis
          if (ms.sizeY == 0) {
            // XZ scan - swap Y and Z
            ms.sizeY = dimSize;
            ms.sizeZ = 1;
            bytesIncPerAxis.put(bytesInc, "Y");
            physicalSizeY = oldPhysicalSize ? offByOnePhysicalLenPp : physicalLenPp;
          }
          else {
            ms.sizeZ = dimSize;
            bytesIncPerAxis.put(bytesInc, "Z");
            physicalSizeZ = physicalLenPp;
          }
          break;
        case 4: // T axis
          if (ms.sizeY == 0) {
            // XT scan - swap Y and T
            ms.sizeY = dimSize;
            ms.sizeT = 1;
            bytesIncPerAxis.put(bytesInc, "Y");
            physicalSizeY = oldPhysicalSize ? offByOnePhysicalLenPp : physicalLenPp;
          }
          else {
            ms.sizeT = dimSize;
            bytesIncPerAxis.put(bytesInc, "T");
          }
          break;
        case 10: // tile axis
          r.metaTemp.tileCount[image] *= dimSize;
          r.metaTemp.tileBytesInc[image] = bytesInc;
          break;
        default:
          extras *= dimSize;
      }
    }

    r.metaTemp.physicalSizeXs.add(physicalSizeX);
    r.metaTemp.physicalSizeYs.add(physicalSizeY);

    if (r.metaTemp.zSteps[image] == null && physicalSizeZ != null) {
      r.metaTemp.zSteps[image] = Math.abs(physicalSizeZ);
    }

    if (extras > 1) {
      if (ms.sizeZ == 1) ms.sizeZ = extras;
      else {
        if (ms.sizeT == 0) ms.sizeT = extras;
        else ms.sizeT *= extras;
      }
    }

    if (ms.sizeC == 0) ms.sizeC = 1;
    if (ms.sizeZ == 0) ms.sizeZ = 1;
    if (ms.sizeT == 0) ms.sizeT = 1;

    if (ms.sizeX == 0) ms.sizeX = 1;
    if (ms.sizeY == 0) ms.sizeY = 1;

    //TIFF and JPEG files not interleaved
    if (r.getImageFormat() == ImageFormat.TIF || r.getImageFormat() == ImageFormat.JPEG){
      ms.interleaved = false;
    } else {
      ms.interleaved = ms.rgb;
    }
    ms.indexed = !ms.rgb;
    ms.imageCount = ms.sizeZ * ms.sizeT;
    if (!ms.rgb) ms.imageCount *= ms.sizeC;
    else {
      ms.imageCount *= (ms.sizeC / 3);
    }

    Long[] bytesIncs = bytesIncPerAxis.keySet().toArray(new Long[0]);
    Arrays.sort(bytesIncs);
    ms.dimensionOrder = "XY";
    if (r.getRGBChannelCount() == 1 || r.getRGBChannelCount() == r.getSizeC()) {
      if (r.getRGBChannelCount() > 1) {
        ms.dimensionOrder += 'C';
      }
      for (Long bytesInc : bytesIncs) {
        String axis = bytesIncPerAxis.get(bytesInc);
        if (ms.dimensionOrder.indexOf(axis) == -1) {
          ms.dimensionOrder += axis;
        }
      }
    }

    if (ms.dimensionOrder.indexOf('Z') == -1) {
      ms.dimensionOrder += 'Z';
    }
    if (ms.dimensionOrder.indexOf('C') == -1) {
      ms.dimensionOrder += 'C';
    }
    if (ms.dimensionOrder.indexOf('T') == -1) {
      ms.dimensionOrder += 'T';
    }
  }

  /**
   * Extracts information from dimension descriptions and writes it to reader's {@link MetadataTempBuffer}
   * @param imageNode Image node from Leica xml
   * @param image image / core index
   * @throws FormatException
   */
  public void translateAttachmentNodes(Element imageNode, int image)
    throws FormatException
  {
    boolean tilesAttachmentFound = false;
    NodeList attachmentNodes = getNodes(imageNode, "Attachment");
    if (attachmentNodes == null) return;
    for (int i=0; i<attachmentNodes.getLength(); i++) {
      Element attachment = (Element) attachmentNodes.item(i);

      String attachmentName = attachment.getAttribute("Name");

      if ("ContextDescription".equals(attachmentName)) {
        r.metaTemp.descriptions[image] = attachment.getAttribute("Content");
      }
      else if ("TileScanInfo".equals(attachmentName)) {
        NodeList tiles = getNodes(attachment, "Tile");

        for (int tile=0; tile<tiles.getLength(); tile++) {
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
            }
            catch (NumberFormatException e) {
              LMSFileReader.log.debug("", e);
              r.metaTemp.fieldPosX.add(null);
            }
          }
          if (posY != null) {
            try {
              final Double number = DataTools.parseDouble(posY);
              r.metaTemp.fieldPosY.add(new Length(number, UNITS.METER));
            }
            catch (NumberFormatException e) {
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
      for (int i=0; i<attachmentNodes.getLength(); i++) {
        Element attachment = (Element) attachmentNodes.item(i);

        String attachmentName = attachment.getAttribute("Name");

        if ("HardwareSetting".equals(attachmentName)) {
          confocalSettings = getNodes(attachment, "ATLConfocalSettingDefinition");
          break;
        }
      }

      if (confocalSettings != null) {
        for (int i=0; i<confocalSettings.getLength(); i++) {
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
      }
      else {
        r.metaTemp.fieldPosX.add(null);
        r.metaTemp.fieldPosY.add(null);
      }
    }
  }

   /**
   * Extracts scanner information and writes it to reader's {@link CoreMetadata} and {@link MetadataTempBuffer}
   * @param imageNode Image node from Leica xml
   * @param image image / core index
   * @throws FormatException
   */
  public void translateScannerSettings(Element imageNode, int image)
    throws FormatException
  {
    NodeList scannerSettings = getNodes(imageNode, "ScannerSettingRecord");
    NodeList attachmentNodes = getNodes(imageNode, "Attachment");
    if (attachmentNodes == null) {
      return;
    }
    NodeList confocalSettings = null;
    for (int i=0; i<attachmentNodes.getLength(); i++) {
      Element attachment = (Element) attachmentNodes.item(i);

      String attachmentName = attachment.getAttribute("Name");

      if ("HardwareSetting".equals(attachmentName)) {
        confocalSettings = getNodes(attachment, "ATLConfocalSettingDefinition");
      }
    }

    if (scannerSettings == null && confocalSettings == null) return;

    r.metaTemp.expTimes[image] = new Double[r.getEffectiveSizeC()];
    r.metaTemp.gains[image] = new Double[r.getEffectiveSizeC()];
    r.metaTemp.detectorOffsets[image] = new Double[r.getEffectiveSizeC()];
    r.metaTemp.channelNames[image] = new String[r.getEffectiveSizeC()];
    r.metaTemp.exWaves[image] = new Double[r.getEffectiveSizeC()];
    r.metaTemp.detectorModels.set(image, new ArrayList<String>());

    if (scannerSettings != null) {
      for (int i=0; i<scannerSettings.getLength(); i++) {
        Element scannerSetting = (Element) scannerSettings.item(i);
        String id = scannerSetting.getAttribute("Identifier");
        if (id == null) id = "";
        // String suffix = scannerSetting.getAttribute("Identifier");
        String value = scannerSetting.getAttribute("Variant");

        if (value == null || value.trim().isEmpty()) {
          continue;
        }

        if (id.equals("SystemType")) {
          r.metaTemp.microscopeModels[image] = value;
        }
        else if (id.equals("dblPinhole")) {
          r.metaTemp.pinholes[image] = DataTools.parseDouble(value.trim()) * METER_MULTIPLY;
        }
        else if (id.equals("dblZoom")) {
          r.metaTemp.zooms[image] = DataTools.parseDouble(value.trim());
        }
        else if (id.equals("dblStepSize")) {
          r.metaTemp.zSteps[image] = DataTools.parseDouble(value.trim()) * METER_MULTIPLY;
        }
        else if (id.equals("nDelayTime_s")) {
          r.metaTemp.tSteps[image] = DataTools.parseDouble(value.trim());
        }
        else if (id.equals("CameraName")) {
          r.metaTemp.detectorModels.get(image).add(value);
        }
        else if (id.equals("eDirectional")) {
          r.addSeriesMeta("Reverse X orientation", "1".equals(value.trim()));
        }
        else if (id.equals("eDirectionalY")) {
          r.addSeriesMeta("Reverse Y orientation", "1".equals(value.trim()));
        }
        else if (id.indexOf("WFC") == 1) {
          int c = 0;
          try {
            c = Integer.parseInt(id.replaceAll("\\D", ""));
          }
          catch (NumberFormatException e) { }
          if (c < 0 || c >= r.getEffectiveSizeC()) {
            continue;
          }

          if (id.endsWith("ExposureTime")) {
            r.metaTemp.expTimes[image][c] = DataTools.parseDouble(value.trim());
          }
          else if (id.endsWith("Gain")) {
            r.metaTemp.gains[image][c] = DataTools.parseDouble(value.trim());
          }
          else if (id.endsWith("WaveLength")) {
            Double exWave = DataTools.parseDouble(value.trim());
            if (exWave != null && exWave > 0) {
              r.metaTemp.exWaves[image][c] = exWave;
            }
          }
          // NB: "UesrDefName" is not a typo.
          else if ((id.endsWith("UesrDefName") || id.endsWith("UserDefName")) &&
            !value.equals("None"))
          {
            if (r.metaTemp.channelNames[image][c] == null ||
            r.metaTemp.channelNames[image][c].trim().isEmpty())
            {
              r.metaTemp.channelNames[image][c] = value;
            }
          }
        }
      }
    }

    if (confocalSettings != null) {
      for (int i=0; i<confocalSettings.getLength(); i++) {
        Element confocalSetting = (Element) confocalSettings.item(i);

        String value = confocalSetting.getAttribute("Pinhole");

        if (value != null && !value.trim().isEmpty()) {
          r.metaTemp.pinholes[image] = DataTools.parseDouble(value.trim()) * METER_MULTIPLY;
        }

        value = confocalSetting.getAttribute("Zoom");

        if (value != null && !value.trim().isEmpty()) {
          r.metaTemp.zooms[image] = DataTools.parseDouble(value.trim());
        }

        value = confocalSetting.getAttribute("ObjectiveName");

        if (value != null && !value.trim().isEmpty()) {
          r.metaTemp.objectiveModels[image] = value.trim();
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
   * Extracts filter information and writes it to reader's {@link MetadataTempBuffer}
   * @param imageNode Image node from Leica xml
   * @param image image / core index
   * @throws FormatException
   */
  public void translateFilterSettings(Element imageNode, int image)
    throws FormatException
  {
    NodeList filterSettings = getNodes(imageNode, "FilterSettingRecord");
    if (filterSettings == null) return;

    r.metaTemp.activeDetector.set(image, new ArrayList<Boolean>());
    r.metaTemp.cutIns.set(image, new ArrayList<Length>());
    r.metaTemp.cutOuts.set(image, new ArrayList<Length>());
    r.metaTemp.filterModels.set(image, new ArrayList<String>());
    r.metaTemp.detectorIndexes.set(image, new HashMap<Integer, String>());

    int nextChannel = 0;

    for (int i=0; i<filterSettings.getLength(); i++) {
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
      }
      else if (attribute.equals("OrderNumber")) {
        if (variant != null && !variant.trim().isEmpty()) {
          r.metaTemp.serialNumber[image] = variant.trim();
        }
      }
      else if (objectClass.equals("CDetectionUnit")) {
        if (attribute.equals("State")) {
          int channel = getChannelIndex(filterSetting);
          if (channel < 0) continue;

          r.metaTemp.detectorIndexes.get(image).put(Integer.parseInt(data), object);
          r.metaTemp.activeDetector.get(image).add("Active".equals(variant.trim()));
        }
      }
      else if (attribute.equals("Objective")) {
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
              r.metaTemp.magnification[image] = DataTools.parseDouble(na.trim());
            }
          }
          else {
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
        r.metaTemp.immersions[image] = immersion;

        String correction = "Other";
        if (tokens.hasMoreTokens()) {
          correction = tokens.nextToken();
          if (correction == null || correction.trim().isEmpty()) {
            correction = "Other";
          }
        }
        r.metaTemp.corrections[image] = correction;

        r.metaTemp.objectiveModels[image] = model.toString().trim();
      }
      else if (attribute.equals("RefractionIndex")) {
        if (variant != null && !variant.trim().isEmpty()) {
          r.metaTemp.refractiveIndex[image] = DataTools.parseDouble(variant.trim());
        }
      }
      else if (attribute.equals("XPos")) {
        if (variant != null && !variant.trim().isEmpty()) {
          final Double number = DataTools.parseDouble(variant.trim());
          r.metaTemp.posX[image] = new Length(number, UNITS.REFERENCEFRAME);
        }
      }
      else if (attribute.equals("YPos")) {
        if (variant != null && !variant.trim().isEmpty()) {
          final Double number = DataTools.parseDouble(variant.trim());
          r.metaTemp.posY[image] = new Length(number, UNITS.REFERENCEFRAME);
        }
      }
      else if (attribute.equals("ZPos")) {
        if (variant != null && !variant.trim().isEmpty()) {
          final Double number = DataTools.parseDouble(variant.trim());
          r.metaTemp.posZ[image] = new Length(number, UNITS.REFERENCEFRAME);
        }
      }
      else if (objectClass.equals("CSpectrophotometerUnit")) {
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
        }
        else if (description.endsWith("(right)")) {
          if (v != null && v > 0) {
            Length out = FormatTools.getCutOut(v);
            if (out != null) {
              r.metaTemp.cutOuts.get(image).add(out);
            }
          }
        }
        else if (attribute.equals("Stain")) {
          if (nextChannel < r.metaTemp.channelNames[image].length) {
            r.metaTemp.channelNames[image][nextChannel++] = variant;
          }
        }
      }
    }
  }
  
  /**
   * Extracts timestamps and writes them to reader's {@link MetadataTempBuffer}
   * @param imageNode Image node from Leica xml
   * @param image image / core index
   * @throws FormatException
   */
  public void translateTimestamps(Element imageNode, int image)
    throws FormatException
  {
    NodeList timeStampLists = getNodes(imageNode, "TimeStampList");
    if (timeStampLists == null) return;

    Element timeStampList = (Element)timeStampLists.item(0);
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
    }
    else {
      // probe if timestamps are saved in the format of LAS AF 3.0 or older
      NodeList timestampNodes = getNodes(imageNode, "TimeStamp");
      if (timestampNodes != null) {
        // LAS AF 3.0 (or older) timestamps are available
        for (int stamp = 0; stamp < timestampNodes.getLength(); stamp++) {
          if (stamp < r.getImageCount()) {
            Element timestamp = (Element) timestampNodes.item(stamp);
            r.metaTemp.timestamps[image][stamp] = translateSingleTimestamp(timestamp);
          }
        }
      }
      else {
        return;
      }
    }
    
    r.metaTemp.acquiredDate[image] = r.metaTemp.timestamps[image][0];
  }

  /**
   * Extracts laser information and writes it to reader's {@link MetadataTempBuffer}
   * @param imageNode Image node from Leica xml
   * @param image image / core index
   * @throws FormatException
   */
  public void translateLaserLines(Element imageNode, int image)
    throws FormatException
  {
    NodeList aotfLists = getNodes(imageNode, "AotfList");
    if (aotfLists == null || aotfLists.getLength() == 0) return;

    r.metaTemp.laserWavelength.set(image, new ArrayList<Double>());
    r.metaTemp.laserIntensity.set(image, new ArrayList<Double>());
    r.metaTemp.laserFrap.set(image, new ArrayList<Boolean>());

    int baseIntensityIndex = 0;

    for (int channel=0; channel<aotfLists.getLength(); channel++) {
      Element aotf = (Element) aotfLists.item(channel);
      NodeList laserLines = getNodes(aotf, "LaserLineSetting");
      if (laserLines == null) return;
      String gpName = aotf.getParentNode().getParentNode().getNodeName();
      //might need parent for attachment
      boolean isMaster = gpName.endsWith("Sequential_Master") ||
              gpName.endsWith("Attachment");
      r.metaTemp.laserFrap.get(image).add(gpName.endsWith("FRAP_Master"));
      for (int laser=0; laser<laserLines.getLength(); laser++) {
        Element laserLine = (Element) laserLines.item(laser);
        
        if (isMaster) {
          continue;
        }
        String lineIndex = laserLine.getAttribute("LineIndex");
        String qual = laserLine.getAttribute("Qualifier");
        int index =
          lineIndex == null || lineIndex.trim().isEmpty() ? 0 :
          Integer.parseInt(lineIndex.trim());
        int qualifier =
          qual == null || qual.trim().isEmpty() ? 0:
          Integer.parseInt(qual.trim());

        index += (2 - (qualifier / 10));
        if (index < 0) {
            continue;
            //index = 0;
        }

        String v = laserLine.getAttribute("LaserLine");
        Double wavelength = 0d;
        if (v != null && !v.trim().isEmpty()) {
            wavelength = DataTools.parseDouble(v.trim());
        }
        if (index < r.metaTemp.laserWavelength.get(image).size()) {
          r.metaTemp.laserWavelength.get(image).set(index, wavelength);
        }
        else {
          for (int i=r.metaTemp.laserWavelength.get(image).size(); i<index; i++) {
            r.metaTemp.laserWavelength.get(image).add(Double.valueOf(0));
          }
          r.metaTemp.laserWavelength.get(image).add(wavelength);
        }

        String intensity = laserLine.getAttribute("IntensityDev");
        Double realIntensity = 0d;
        if (intensity != null && !intensity.trim().isEmpty()) {
          realIntensity = DataTools.parseDouble(intensity.trim());
          if (realIntensity == null) {
            realIntensity = 0d;
          }
        }
        realIntensity = 100d - realIntensity;

        int realIndex = baseIntensityIndex + index;

        if (realIndex < r.metaTemp.laserIntensity.get(image).size()) {
          r.metaTemp.laserIntensity.get(image).set(realIndex, realIntensity);
        }
        else {
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
   * @param imageNode Image node from Leica xml
   * @param image image / core index
   * @throws FormatException
   */
  public void translateROIs(Element imageNode, int image)
    throws FormatException
  {
    NodeList rois = getNodes(imageNode, "Annotation");
    if (rois == null) return;
    r.metaTemp.imageROIs[image] = new ROI[rois.getLength()];

    for (int r=0; r<rois.getLength(); r++) {
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
      }
      catch (NumberFormatException e) { }

      roi.text = roiNode.getAttribute("text");

      NodeList vertices = getNodes(roiNode, "Vertex");
      if (vertices == null) {
        continue;
      }

      for (int v=0; v<vertices.getLength(); v++) {
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

      if (getNodes(imageNode, "ROI") != null) {
        this.r.metaTemp.alternateCenter = true;
      }
    }
  }

  /**
   * Extracts single ROIs and writes them to reader's {@link MetadataTempBuffer}
   * @param imageNode Image node from Leica xml
   * @param image image / core index
   * @throws FormatException
   */
  public void translateSingleROIs(Element imageNode, int image)
    throws FormatException
  {
    if (r.metaTemp.imageROIs[image] != null) return;
    NodeList children = getNodes(imageNode, "ROI");
    if (children == null) return;
    children = getNodes((Element) children.item(0), "Children");
    if (children == null) return;
    children = getNodes((Element) children.item(0), "Element");
    if (children == null) return;
    r.metaTemp.imageROIs[image] = new ROI[children.getLength()];

    for (int r=0; r<children.getLength(); r++) {
      NodeList rois = getNodes((Element) children.item(r), "ROISingle");

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

      NodeList vertices = getNodes(roiNode, "P");

      double sizeX = this.r.metaTemp.physicalSizeXs.get(image);
      double sizeY = this.r.metaTemp.physicalSizeYs.get(image);

      for (int v=0; v<vertices.getLength(); v++) {
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

      Element transform = (Element) getNodes(roiNode, "Transformation").item(0);

      Double rotation = DataTools.parseDouble(transform.getAttribute("Rotation"));
      if (rotation != null) {
        roi.rotation = rotation;
      }

      Element scaling = (Element) getNodes(transform, "Scaling").item(0);
      Double scaleX = DataTools.parseDouble(scaling.getAttribute("XScale"));
      Double scaleY = DataTools.parseDouble(scaling.getAttribute("YScale"));
      if (scaleX != null) {
        roi.scaleX = scaleX;
      }
      if (scaleY != null) {
        roi.scaleY = scaleY;
      }

      Element translation =
        (Element) getNodes(transform, "Translation").item(0);
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

  /**
   * Extracts detector information and writes it to reader's {@link MetadataTempBuffer}
   * @param imageNode Image node from Leica xml
   * @param image image / core index
   * @throws FormatException
   */
  public void translateDetectors(Element imageNode, int image)
    throws FormatException
  {
    NodeList definitions = getNodes(imageNode, "ATLConfocalSettingDefinition");
    if (definitions == null) return;

    final List<String> channels = new ArrayList<String>();
    r.metaTemp.laserActive.set(image, new ArrayList<Boolean>());
    int nextChannel = 0;
    for (int definition=0; definition<definitions.getLength(); definition++) {
      Element definitionNode = (Element) definitions.item(definition);
      String parentName = definitionNode.getParentNode().getNodeName();
      boolean isMaster = parentName.endsWith("Master");
      NodeList detectors = getNodes(definitionNode, "Detector");
      if (detectors == null) return;
      int count = 0;
      for (int d=0; d<detectors.getLength(); d++) {
        Element detector = (Element) detectors.item(d);
        NodeList multibands = null;
        if (!isMaster) {
          multibands = getNodes(definitionNode, "MultiBand");
        }

        String v = detector.getAttribute("Gain");
        Double gain =
          v == null || v.trim().isEmpty() ? null : DataTools.parseDouble(v.trim());
        v = detector.getAttribute("Offset");
        Double offset =
          v == null || v.trim().isEmpty() ? null : DataTools.parseDouble(v.trim());

        boolean active = "1".equals(detector.getAttribute("IsActive"));
        String c = detector.getAttribute("Channel");
        int channel = (c == null || c.trim().length() == 0) ? 0 : Integer.parseInt(c);
        if (active) {
          if (r.metaTemp.detectorIndexes.get(image) != null && r.metaTemp.detectorModels.get(image) != null) {
            r.metaTemp.detectorModels.get(image).add(r.metaTemp.detectorIndexes.get(image).get(channel));
          }

          Element multiband = null;

          if (multibands != null) {
            for (int i=0; i<multibands.getLength(); i++) {
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
              if (r.metaTemp.cutIns.get(image) == null) {
                r.metaTemp.cutIns.set(image, new ArrayList<Length>());
              }
              Length in =
                FormatTools.getCutIn((double) Math.round(cutIn));
              if (in != null) {
                r.metaTemp.cutIns.get(image).add(in);
              }
            }
            if (cutOut != null && cutOut.intValue() > 0) {
              if (r.metaTemp.cutOuts.get(image) == null) {
                r.metaTemp.cutOuts.set(image, new ArrayList<Length>());
              }
              Length out =
                FormatTools.getCutOut((double) Math.round(cutOut));
              if (out != null) {
                r.metaTemp.cutOuts.get(image).add(out);
              }
            }
          }
          else {
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
      //Store values to check if actually it is active.
      if (!isMaster) {
        r.metaTemp.laserActive.get(image).add(count < detectors.getLength());
      }
    }

    if (channels != null && r.metaTemp.channelNames[image] != null) {
      for (int i=0; i<r.getEffectiveSizeC(); i++) {
        int index = i + channels.size() - r.getEffectiveSizeC();
        if (index >= 0 && index < channels.size()) {
          if (r.metaTemp.channelNames[image][i] == null ||
            r.metaTemp.channelNames[image][i].trim().isEmpty())
          {
            r.metaTemp.channelNames[image][i] = channels.get(index);
          }
        }
      }
    }
  }

  /**
   Translates hex timestamp string to double value
   * @param timestamp timestamp in format
   * @return number of seconds
   */
  private double translateSingleTimestamp(String timestamp) {
    timestamp = timestamp.trim();
    int stampLowStart = Math.max(0, timestamp.length() - 8);
    int stampHighEnd = Math.max(0, stampLowStart);
    String stampHigh = timestamp.substring(0, stampHighEnd);
    String stampLow = timestamp.substring(stampLowStart, timestamp.length());
    long high
            = stampHigh == null || stampHigh.trim().isEmpty() ? 0
                    : Long.parseLong(stampHigh.trim(), 16);
    long low
            = stampLow == null || stampLow.trim().isEmpty() ? 0
                    : Long.parseLong(stampLow.trim(), 16);
    long milliseconds = DateTools.getMillisFromTicks(high, low);
    double seconds = (double)milliseconds / 1000;
    return seconds;
  }
  
  /**
   * Translates the content of a timestamp node to double value
   * @param imageNode timestamp node
   * @param image image / core index
   * @throws FormatException
   */
  private double translateSingleTimestamp(Element timestamp) {
    String stampHigh = timestamp.getAttribute("HighInteger");
    String stampLow = timestamp.getAttribute("LowInteger");
    long high
            = stampHigh == null || stampHigh.trim().isEmpty() ? 0
                    : Long.parseLong(stampHigh.trim());
    long low
            = stampLow == null || stampLow.trim().isEmpty() ? 0
                    : Long.parseLong(stampLow.trim());

    long milliseconds = DateTools.getMillisFromTicks(high, low);
    double seconds = (double)milliseconds / 1000;
    return seconds;
  }

  /**
   * Extracts user comments and adds them to the reader's {@link CoreMetadata}
   * @param imageNode
   * @param image
   * @throws FormatException
   */
  private void addUserCommentMeta(Element imageNode, int image) throws FormatException {
    NodeList attachmentNodes = getNodes(imageNode, "User-Comment");
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
   * Creates key value pairs from attributes of the root's child nodes (tag | attribute name : attribute value) and adds them to reader's {@link CoreMetadata}  
   * @param root xml node
   * @param nameStack list of node names to be prepended to key name
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

  public long parseLong(String value){
    return value == null || value.trim().isEmpty() ? 0 : Long.parseLong(value.trim());
  }

  public int parseInt(String value){
      return value == null || value.trim().isEmpty() ? 0 : Integer.parseInt(value.trim());
  }

  public double parseDouble(String value){
      return StringUtils.isBlank(value) ? 0d : DataTools.parseDouble(value.trim());
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
   * @param root root node
   * @param nodeName name of children that shall be searched
   * @return list of child nodes with given name
   */
  private NodeList getNodes(Element root, String nodeName) {
    NodeList nodes = root.getElementsByTagName(nodeName);
    if (nodes.getLength() == 0) {
        NodeList children = root.getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
            Object child = children.item(i);
            if (child instanceof Element) {
                NodeList childNodes = getNodes((Element) child, nodeName);
                if (childNodes != null) {
                    return childNodes;
                }
            }
        }
        return null;
    }
    else return nodes;
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
