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
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.LaserSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ConfocalChannelSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ConfocalAcquisitionSettings;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Multiband;

/**
 * This is a helper class for extracting confocal acquisition settings from LMS XML.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class ConfocalSettingsExtractor extends Extractor {
  private static final long METER_MULTIPLY = 1000000;

  public static void extractInstrumentSettings(LMSMainXmlNodes xmlNodes, ConfocalAcquisitionSettings acquisitionSettings){
    Element atlConfocalSetting = xmlNodes.getAtlConfocalSetting();

    acquisitionSettings.lasers = getLasers(atlConfocalSetting, atlConfocalSetting);
    acquisitionSettings.detectors = getDetectors(xmlNodes);
  }

  public static ConfocalAcquisitionSettings extractChannelSettings(LMSMainXmlNodes xmlNodes, ConfocalAcquisitionSettings acquisitionSettings){
    Element atlConfocalSetting = xmlNodes.getAtlConfocalSetting();

    String zoomS = getAttributeValue(atlConfocalSetting, "Zoom");
    String readOutRateS = getAttributeValue(atlConfocalSetting, "ScanSpeed");
    String pinholeSizeS = Extractor.getAttributeValue(atlConfocalSetting, "Pinhole");

    acquisitionSettings.zoom = parseDouble(zoomS);
    acquisitionSettings.readOutRate = parseDouble(readOutRateS);
    acquisitionSettings.pinholeSize = Extractor.parseDouble(pinholeSizeS) * METER_MULTIPLY;
    
    if (xmlNodes.sequentialConfocalSettings.size() > 0){
      for (Element setting : xmlNodes.sequentialConfocalSettings){
        acquisitionSettings.channelSettings.addAll(extractChannelSettings(setting, atlConfocalSetting));
     }
    } else {
      acquisitionSettings.channelSettings.addAll(extractChannelSettings(atlConfocalSetting, atlConfocalSetting));
    }

    mapInstrumentDetectorsToChannelDetectorSettings(acquisitionSettings);
    mapInstrumentLasersToChannelLaserSettings(acquisitionSettings);

    return acquisitionSettings;
  }

  private static List<Laser> getLasers(Element atlConfocalSetting, Element alternativeSetting){
    Node laserArray = getChildNodeWithName(atlConfocalSetting, "LaserArray");
    // in STELLARIS, laser array nodes are not included in sequential settings
    if (laserArray == null)
      laserArray = getChildNodeWithName(alternativeSetting, "LaserArray");

    NodeList laserNodes = laserArray.getChildNodes();
    List<Laser> lasers = new ArrayList<Laser>();

    for (int i = 0; i < laserNodes.getLength(); i++) {
      Element laserNode;
      try {
        laserNode = (Element) laserNodes.item(i);
      } catch (Exception e) {
        continue;
      }

      Laser laser = new Laser();
      laser.name = laserNode.getAttribute("LaserName");
      String wavelengthS = laserNode.getAttribute("Wavelength");
      laser.wavelength = parseDouble(wavelengthS);

      lasers.add(laser);
    }

    return lasers;
  }

  /**
   * Returns a list of detectors it extracts from LMS XML detector nodes
   * @param xmlNodes
   * @return
   */
  private static List<Detector> getDetectors(LMSMainXmlNodes xmlNodes)
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

  private static List<ConfocalChannelSetting> extractChannelSettings(Element atlConfocalSetting, Element alternativeSetting){
    // get + map detector and multiband info
    List<DetectorSetting> detectorSettings = getActiveDetectorSettings(atlConfocalSetting);
    List<Multiband> multibands = getMultibands(atlConfocalSetting, alternativeSetting);
    addMultibandInfoToDetectorSettings(detectorSettings, multibands);

    // get + map laser and AOTF laser line setting info
    List<Laser> lasers = getLasers(atlConfocalSetting, alternativeSetting);
    List<LaserSetting> laserSettings = getLaserSettings(atlConfocalSetting);
    mapLasersToLaserSettings(lasers, laserSettings);

    return createChannelSettings(detectorSettings, laserSettings);
  }

  private static List<DetectorSetting> getActiveDetectorSettings(Element atlConfocalSetting){
    Node detectorList = getChildNodeWithName(atlConfocalSetting, "DetectorList");
    NodeList detectorNodes = detectorList.getChildNodes();
    List<DetectorSetting> detectorSettings = new ArrayList<DetectorSetting>();

    int detectorListIndex = 0; // store order of appearance of detector, for later mapping to channels
    for (int i = 0; i < detectorNodes.getLength(); i++) {
      Element detectorNode;
      try {
        detectorNode = (Element) detectorNodes.item(i);
      } catch (Exception e) {
        continue;
      }

      String isActive = detectorNode.getAttribute("IsActive");
      if (!isActive.equals("1")) continue;

      DetectorSetting detectorSetting = new DetectorSetting();

      detectorSetting.name = detectorNode.getAttribute("Name");
      detectorSetting.type = detectorNode.getAttribute("Type");
      String channelS = detectorNode.getAttribute("Channel");
      detectorSetting.channel = parseInt(channelS);
      String gainS = detectorNode.getAttribute("Gain");
      detectorSetting.gain = parseDouble(gainS);
      String offsetS = detectorNode.getAttribute("Offset");
      detectorSetting.offset = parseDouble(offsetS);
      detectorSetting.detectorListIndex = detectorListIndex;

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

  private static List<Multiband> getMultibands(Element atlConfocalSetting, Element alternativeSetting){
    Node spectro = getChildNodeWithName(atlConfocalSetting, "Spectro");
    // in STELLARIS, spectro nodes are not included in sequential settings
    if (spectro == null)
      spectro = getChildNodeWithName(alternativeSetting, "Spectro");

    NodeList multibandNodes = spectro.getChildNodes();
    List<Multiband> multibands = new ArrayList<Multiband>();

    for (int i = 0; i < multibandNodes.getLength(); i++) {
      Element multibandNode;
      try {
        multibandNode = (Element) multibandNodes.item(i);
      } catch (Exception e) {
        continue;
      }

      Multiband multiband = new Multiband();

      String channelS = multibandNode.getAttribute("Channel");
      multiband.channel = parseInt(channelS);
      String leftWorldS = multibandNode.getAttribute("LeftWorld");
      multiband.leftWorld = parseDouble(leftWorldS);
      String rightWorldS = multibandNode.getAttribute("RightWorld");
      multiband.rightWorld = parseDouble(rightWorldS);
      multiband.dyeName = multibandNode.getAttribute("DyeName");

      multibands.add(multiband);
    }

    return multibands;
  }

  private static void addMultibandInfoToDetectorSettings(List<DetectorSetting> detectorSettings, List<Multiband> multibands){
    for (DetectorSetting detectorSetting : detectorSettings){
      for (Multiband multiband : multibands){
        if (multiband.channel == detectorSetting.channel){
          detectorSetting.cutIn = multiband.leftWorld;
          detectorSetting.cutOut = multiband.rightWorld;
          detectorSetting.dyeName = multiband.dyeName;
          break;
        }
      }
    }
  }

  private static List<LaserSetting> getLaserSettings(Element atlConfocalSetting){
    Node aotfList = getChildNodeWithName(atlConfocalSetting, "AotfList");
    List<Element> aotfs = getChildNodesWithNameAsElement(aotfList, "Aotf");

    List<LaserSetting> laserSettings = new ArrayList<>();

    for (Element aotf : aotfs){
      List<Element> laserLineSettingNodes = getChildNodesWithNameAsElement(aotf, "LaserLineSetting");
      for (Element laserLineSettingNode : laserLineSettingNodes){
        String intensityDevS = laserLineSettingNode.getAttribute("IntensityDev");
        double intensityDev = parseDouble(intensityDevS);
        if(intensityDev == 0)
          continue;

        LaserSetting laserSetting = new LaserSetting();
        laserSetting.intensity = intensityDev;
        String waveLengthS = laserLineSettingNode.getAttribute("LaserLine");
        laserSetting.wavelength = parseDouble(waveLengthS);

        laserSettings.add(laserSetting);
      }
    }

    return laserSettings;
  }

  private static void mapLasersToLaserSettings(List<Laser> lasers, List<LaserSetting> laserSettings){
    for (LaserSetting laserSetting : laserSettings){
      for (Laser laser : lasers){
        if (laser.wavelength == laserSetting.wavelength || 
        laser.name.equals("Argon") && laser.argonWavelengths.contains(laserSetting.wavelength)){
          laserSetting.laser = laser;
          break;
        }
      }

      // if no matching laser was found, map WLL
      if (laserSetting.laser == null){
        for (Laser laser : lasers){
          if (laser.name.equals("WLL")){
            laserSetting.laser = laser;
            break;
          }
        }
      }
    }
  }

  private static List<ConfocalChannelSetting> createChannelSettings(List<DetectorSetting> detectorSettings, List<LaserSetting> laserSettings){
    List<ConfocalChannelSetting> channelSettings = new ArrayList<>();

    for (DetectorSetting detectorSetting : detectorSettings){
      ConfocalChannelSetting channelSetting = new ConfocalChannelSetting();
      channelSetting.detectorSetting = detectorSetting;

      // STELLARIS
      if (detectorSetting.referenceLineWavelength > 0){
        for (LaserSetting laserSetting : laserSettings){
          if (detectorSetting.referenceLineWavelength == laserSetting.wavelength)
            channelSetting.laserSetting = laserSetting;
        }
      // OLDER
      } else {
        LaserSetting selectedLaserSetting = null;
        for (LaserSetting laserSetting : laserSettings) {
          if (laserSetting.wavelength < detectorSetting.cutIn) {
            if (selectedLaserSetting == null || selectedLaserSetting.laser.wavelength < laserSetting.laser.wavelength)
              selectedLaserSetting = laserSetting;
          }
        }
        channelSetting.laserSetting = selectedLaserSetting;
      }

      channelSettings.add(channelSetting);
    }

    return channelSettings;
  }

  private static void mapInstrumentDetectorsToChannelDetectorSettings(ConfocalAcquisitionSettings acquisitionSettings){
    for (ConfocalChannelSetting channelSetting : acquisitionSettings.channelSettings){
      for (Detector detector : acquisitionSettings.detectors){
        if (channelSetting.detectorSetting.name.equals(detector.model) || channelSetting.detectorSetting.detectorListIndex == detector.detectorListIndex){
          channelSetting.detectorSetting.detector = detector;
          break;
        }
      }
    }
  }

  private static void mapInstrumentLasersToChannelLaserSettings(ConfocalAcquisitionSettings acquisitionSettings){
    for (ConfocalChannelSetting channelSetting : acquisitionSettings.channelSettings){
      for (Laser laser : acquisitionSettings.lasers){
        if (channelSetting.laserSetting != null && channelSetting.laserSetting.laser.name.equals(laser.name)){
          channelSetting.laserSetting.laser = laser;
          break;
        }
      }
    }
  }
}
