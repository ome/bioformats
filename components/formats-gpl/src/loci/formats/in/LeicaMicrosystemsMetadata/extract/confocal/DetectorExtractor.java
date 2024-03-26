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

package loci.formats.in.LeicaMicrosystemsMetadata.extract.confocal;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.ConfocalSettingRecords;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Multiband;

/**
 * DetectorExtractor is a helper class for extracting detector information from LMS XML files.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class DetectorExtractor extends Extractor {

  /**
   * Creates Detectors from information extracted from ATL confocal settings
   * @param setting atl confocal setting from which detector info is extracted
   * @param records these are required to look up detector names for SP5 images
   * @return
   */
  public static List<Detector> getDetectors(Element setting, ConfocalSettingRecords records){
    List<Detector> detectors = new ArrayList<>();

    String zoomS = getAttributeValue(setting, "Zoom");
    double zoom = parseDouble(zoomS);

    // main confocal settings > detector list: instrument detectors
    Node detectorList = getChildNodeWithName(setting, "DetectorList");
    NodeList detectorNodes = detectorList.getChildNodes();

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
      detector.channel = parseInt(detectorNode.getAttribute("Channel"));
      detector.zoom = zoom;
      detector.detectorListIndex = detectorListIndex;

        // SP5: detectors in ATL settings have no names, this information can be found in filter setting records
      if (detector.model.isEmpty() && records != null){
        for (Detector detectorRecord : records.detectorRecords){
          if (detector.channel == detectorRecord.channel){
            detector.model = detectorRecord.model;
            break;
          }
        }
      }

      detectors.add(detector);

      detectorListIndex++;
    }

    return detectors;
  }

  public static List<DetectorSetting> getActiveDetectorSettings(Element atlConfocalSetting){
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
      detectorSetting.dyeName = detectorNode.getAttribute("DyeName"); // STELLARIS

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


  public static List<Multiband> getMultibands(Element atlConfocalSetting, Element alternativeSetting){
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


  public static void addMultibandInfoToDetectorSettings(List<DetectorSetting> detectorSettings, List<Multiband> multibands){
    for (DetectorSetting detectorSetting : detectorSettings){
      for (Multiband multiband : multibands){
        if (multiband.channel == detectorSetting.channel){
          detectorSetting.cutIn = multiband.leftWorld;
          detectorSetting.cutOut = multiband.rightWorld;
          if (detectorSetting.dyeName == null || detectorSetting.dyeName.isEmpty())
            detectorSetting.dyeName = multiband.dyeName;
          break;
        }
      }
    }
  }
}
