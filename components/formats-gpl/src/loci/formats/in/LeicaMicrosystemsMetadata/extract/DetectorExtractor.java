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

package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.AtlSettingLayout;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;

/**
 * DetectorExtractor is a helper class for extracting detector information from LMS XML files.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class DetectorExtractor extends Extractor {

  /**
   * Returns a list of detectors it extracts from LMS XML detector nodes
   * @param xmlNodes
   * @return
   */
  public static List<Detector> extractDetectors(LMSMainXmlNodes xmlNodes)
  {
    Element setting = xmlNodes.getAtlConfocalSetting();

    String zoomS = getAttributeValue(setting, "Zoom");
    double zoom = parseDouble(zoomS);

    // main confocal settings > detector list: instrument detectors
    Node detectorList = getChildNodeWithName(setting, "DetectorList");
    NodeList detectorNodes = detectorList.getChildNodes();
    List<Detector> detectors = new ArrayList<Detector>();

    int detectorListIndex = 0;
    for (int i = 0; i < detectorNodes.getLength(); i++) {
      Element detectorNode;
      try {
        detectorNode = (Element) detectorNodes.item(i);
      } catch (Exception e) {
        continue;
      }

      Detector detector = new Detector();

      detector.model = detectorNode.getAttribute("Name");
      detector.type = detectorNode.getAttribute("Type");
      detector.zoom = zoom;
      detector.detectorListIndex = detectorListIndex;

      detectors.add(detector);

      detectorListIndex++;
    }

    if (xmlNodes.atlSettingLayout == AtlSettingLayout.CONFOCAL_OLD){
      List<String> detectorNames = new ArrayList<String>();
      List<Element> detectorRecords = Extractor.getNodesWithAttributeAsElements(xmlNodes.filterSettingRecords, "ClassName", "CDetectionUnit");
      for (Element detectorRecord : detectorRecords){
        String name = Extractor.getAttributeValue(detectorRecord, "ObjectName");
        if (!name.isEmpty() && !detectorNames.contains(name))
          detectorNames.add(name);
      }

      for (int i = 0; i < detectors.size(); i++){
        if (detectors.get(i).model.isEmpty() && i < detectorNames.size())
          detectors.get(i).model = detectorNames.get(i);
      }
    }

    return detectors;
  }

  /**
   * Returns a list of detector settings it creates from Detectors and LMS XML sequential detector nodes
   */
  public static List<DetectorSetting> extractDetectorSettings(LMSMainXmlNodes xmlNodes, List<Detector> detectors){
    Element atlSetting = xmlNodes.getAtlConfocalSetting();

    String readOutRateS = getAttributeValue(atlSetting, "ScanSpeed");
    double readOutRate = parseDouble(readOutRateS);

    List<DetectorSetting> activeDetectorSettings = new ArrayList<DetectorSetting>();

    List<DetectorSetting> detectorSettings = new ArrayList<DetectorSetting>();
    int sequenceIndex = 0;
    if (xmlNodes.sequentialConfocalSettings.size() > 0){
      for (int i = 0; i < xmlNodes.sequentialConfocalSettings.size(); i++) {
        detectorSettings.addAll(extractDetectorSettingsFromHardwareSettings(xmlNodes.sequentialConfocalSettings.get(i), sequenceIndex, detectors));
  
        // if a sequential hardware setting contained detector settings, increase sequence index, "empty" sequential settings are not counted
        if (detectorSettings.size() > 0) sequenceIndex++;
      }
    } else {
      // some images have no LDM_Blocks, detector settings are instead taken from the main ATL confocal setting
      detectorSettings = extractDetectorSettingsFromHardwareSettings(atlSetting, sequenceIndex, detectors);
    }
    
    for (DetectorSetting setting : detectorSettings) {
      if (setting.isActive){
        setting.readOutRate = readOutRate;
        activeDetectorSettings.add(setting);
      }
    }

    mapDetectorSettingsToDetectors(activeDetectorSettings, detectors);

    return activeDetectorSettings;
  }

  /**
   * Extracts detector settings from given hardware settings
   * @param setting setting (main, master or sequential) from which the other detector settings are extracted
   * @param sequenceIndex
   * @param detectors
   * @return
   */
  private static List<DetectorSetting> extractDetectorSettingsFromHardwareSettings(Element setting, int sequenceIndex, List<Detector> detectors){
    List<DetectorSetting> detectorSettings = new ArrayList<DetectorSetting>();
    
    Node seqDetectorList = getChildNodeWithName(setting, "DetectorList");
    if (seqDetectorList == null)
      return detectorSettings;

    NodeList seqDetectorNodes = seqDetectorList.getChildNodes();

    int detectorListIndex = 0;
    for (int k = 0; k < seqDetectorNodes.getLength(); k++) {
      Element detectorNode;
      try {
        detectorNode = (Element) seqDetectorNodes.item(k);
      } catch (Exception e) {
        continue;
      }

      DetectorSetting detectorSetting = new DetectorSetting();

      String gainS = detectorNode.getAttribute("Gain");
      detectorSetting.gain = parseDouble(gainS);

      String offsetS = detectorNode.getAttribute("Offset");
      detectorSetting.offset = parseDouble(offsetS);

      String isActiveS = detectorNode.getAttribute("IsActive");
      detectorSetting.isActive = "1".equals(isActiveS);

      String channelS = detectorNode.getAttribute("Channel");
      detectorSetting.channelIndex = parseInt(channelS) - 1;

      detectorSetting.channelName = detectorNode.getAttribute("ChannelName");

      detectorSetting.sequenceIndex = sequenceIndex;
      detectorSetting.detectorListIndex = detectorListIndex;

      detectorSetting.name = detectorNode.getAttribute("Name");

      detectorSetting.transmittedLightMode = detectorSetting.name.toLowerCase().contains("trans")
          && detectorSetting.channelName.equals("Transmission Channel");
      
      //only in STELLARIS
      Element detectionReferenceLine;
      try {
        detectionReferenceLine = (Element)getChildNodeWithName(detectorNode, "DetectionReferenceLine");
        if (detectionReferenceLine != null){
          detectorSetting.referenceLineName = getAttributeValue(detectionReferenceLine, "LaserName");
          String referenceWavelengthS = getAttributeValue(detectionReferenceLine, "LaserWavelength");
          detectorSetting.referenceLineWavelength = parseInt(referenceWavelengthS);
        }
      } catch (Exception e) {}

      detectorSettings.add(detectorSetting);
      detectorListIndex++;
    }



    return detectorSettings;
  }

  private static void mapDetectorSettingsToDetectors(List<DetectorSetting> detectorSettings, List<Detector> detectors){
    // in e.g. STELLARIS images, all detector nodes should contain names, so that we can map sequential detector information
    //(used for detector settings) to main detector information (used for detectors) using these names. this is what we try first.
    boolean mapByNames = true;
    for (Detector detector : detectors) {
      if (detector.model == null || detector.model.isEmpty()){
        mapByNames = false;
        break;
      }
    }
    if (mapByNames){
      for (DetectorSetting detectorSetting : detectorSettings){
        if (detectorSetting.name == null || detectorSetting.name.isEmpty()){
          mapByNames = false;
        }
      }
    }

    if (mapByNames){
      for (int i = 0; i < detectorSettings.size(); i++) {
        for (Detector detector : detectors){
          if (detector.model.equals(detectorSettings.get(i).name)){
            detectorSettings.get(i).detector = detector;
            break;
          }
        }
      }
    } else {
      // detector names do not exist in e.g. SP5 images. in this case, we rely on the assumption that
      // the indices of detectors in sequential detector lists are the same as in the main detector list.
      for (int i = 0; i < detectorSettings.size(); i++) {
        for (Detector detector : detectors){
          if (detector.detectorListIndex == detectorSettings.get(i).detectorListIndex){
            detectorSettings.get(i).detector = detector;
            break;
          }
        }
      }
    }
  }
}
