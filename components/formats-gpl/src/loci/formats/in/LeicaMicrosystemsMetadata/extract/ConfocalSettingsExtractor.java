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
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.extract.confocal.DetectorExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.extract.confocal.LaserExtractor;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
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

    acquisitionSettings.lasers = LaserExtractor.getLasers(atlConfocalSetting, atlConfocalSetting, xmlNodes.filterSettingRecords);
    acquisitionSettings.detectors = DetectorExtractor.getDetectors(xmlNodes);
  }

  public static ConfocalAcquisitionSettings extractChannelSettings(LMSMainXmlNodes xmlNodes, ConfocalAcquisitionSettings acquisitionSettings){
    Element atlConfocalSetting = xmlNodes.getAtlConfocalSetting();
    if (atlConfocalSetting == null) return acquisitionSettings;

    String zoomS = getAttributeValue(atlConfocalSetting, "Zoom");
    String readOutRateS = getAttributeValue(atlConfocalSetting, "ScanSpeed");
    String pinholeSizeS = Extractor.getAttributeValue(atlConfocalSetting, "Pinhole");

    acquisitionSettings.zoom = parseDouble(zoomS);
    acquisitionSettings.readOutRate = parseDouble(readOutRateS);
    acquisitionSettings.pinholeSize = Extractor.parseDouble(pinholeSizeS) * METER_MULTIPLY;
    
    if (xmlNodes.sequentialConfocalSettings.size() > 0){
      for (Element setting : xmlNodes.sequentialConfocalSettings){
        acquisitionSettings.channelSettings.addAll(extractChannelSettings(setting, atlConfocalSetting, xmlNodes.filterSettingRecords));
     }
    } else {
      acquisitionSettings.channelSettings.addAll(extractChannelSettings(atlConfocalSetting, atlConfocalSetting, xmlNodes.filterSettingRecords));
    }

    mapInstrumentDetectorsToChannelDetectorSettings(acquisitionSettings);
    mapInstrumentLasersToChannelLaserSettings(acquisitionSettings);

    return acquisitionSettings;
  }


  private static List<ConfocalChannelSetting> extractChannelSettings(Element atlConfocalSetting, Element alternativeSetting, NodeList filterSettingRecords){
    // get + map detector and multiband info
    List<DetectorSetting> detectorSettings = DetectorExtractor.getActiveDetectorSettings(atlConfocalSetting);
    List<Multiband> multibands = DetectorExtractor.getMultibands(atlConfocalSetting, alternativeSetting);
    DetectorExtractor.addMultibandInfoToDetectorSettings(detectorSettings, multibands);

    // get + map laser and AOTF laser line setting info
    List<Laser> lasers = LaserExtractor.getLasers(atlConfocalSetting, alternativeSetting, filterSettingRecords);
    LaserExtractor.addShutterInfoToLasers(lasers, atlConfocalSetting, alternativeSetting);
    List<LaserSetting> laserSettings = LaserExtractor.getLaserSettings(atlConfocalSetting);
    LaserExtractor.mapLasersToLaserSettings(lasers, laserSettings);

    // map detector and laser settings
    List<ConfocalChannelSetting> channelSettings = createChannelSettings(detectorSettings, laserSettings);

    return channelSettings;
  }


  /**
   * Creates confocal channel settings by mapping detector and laser settings extracted from one sequential confocal setting
   * @param detectorSettings
   * @param laserSettings
   * @return
   */
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
