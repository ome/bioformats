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

import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Aotf;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.ConfocalAcquisitionSettings;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.ConfocalChannelSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.LaserSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Multiband;

/**
 * Helper class for extracting confocal acquisition settings from filter and scanner setting records
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class ConfocalSettingsFromSettingRecordsExtractor extends Extractor {

  public static void extractChannelSettings(LMSMainXmlNodes xmlNodes, ConfocalAcquisitionSettings acquisitionSettings){
    acquisitionSettings.pinholeSize = xmlNodes.confocalSettingRecords.pinholeSize;
    acquisitionSettings.zoom = xmlNodes.confocalSettingRecords.zoom;
    acquisitionSettings.readOutRate = xmlNodes.confocalSettingRecords.readOutRate;

    List<DetectorSetting> detectorSettings = getDetectorSettings(xmlNodes);
    List<LaserSetting> laserSettings = getLaserSettings(xmlNodes);
    acquisitionSettings.channelSettings = createChannelSettings(detectorSettings, laserSettings);
  }

  private static List<DetectorSetting> getDetectorSettings(LMSMainXmlNodes xmlNodes){
    List<DetectorSetting> detectorSettings = new ArrayList<>();

    for (Detector detector : xmlNodes.confocalSettingRecords.detectorRecords){
      if (!detector.isActive) continue;
      
      DetectorSetting detectorSetting = new DetectorSetting();
      detectorSetting.detector = detector;
      detectorSetting.name = detector.model;
      detectorSetting.gain = detector.gain;
      detectorSetting.offset = detector.offset;

      // this will not match all detectors, since NDD and TLD detectors do not have a range
      for (Multiband multiband : xmlNodes.confocalSettingRecords.multibandRecords){
        if (multiband.channel == detector.channel){
          detectorSetting.cutIn = multiband.leftWorld;
          detectorSetting.cutOut = multiband.rightWorld;
          detectorSetting.dyeName = multiband.dyeName;
          detectorSetting.multiband = multiband;
          break;
        }
      }

      detectorSettings.add(detectorSetting);
    }

    return detectorSettings;
  }

  private static List<LaserSetting> getLaserSettings(LMSMainXmlNodes xmlNodes){
    // getting AOTF shutter info
    for (Aotf aotf : xmlNodes.confocalSettingRecords.aotfRecords){
      if (aotf.type.equals("Visible"))
        aotf.shutterOpen = xmlNodes.confocalSettingRecords.shutterInfo.visible;
      else if (aotf.type.equals("UV"))
        aotf.shutterOpen = xmlNodes.confocalSettingRecords.shutterInfo.uv;
      else if (aotf.type.equals("MP"))
      aotf.shutterOpen = xmlNodes.confocalSettingRecords.shutterInfo.mp;
    }

    // getting laser shutter info
    for (Laser laser : xmlNodes.confocalSettingRecords.laserRecords){
      if (laser.wavelength == 405)
        laser.shutterOpen = xmlNodes.confocalSettingRecords.shutterInfo.uv405;
    }

    //map lasers to AOTFs' laser line settings
    List<LaserSetting> laserSettings = new ArrayList<>();
    for (Aotf aotf : xmlNodes.confocalSettingRecords.aotfRecords){
      if (!aotf.shutterOpen) continue;
      
      for (LaserSetting laserLineSetting : aotf.laserLineSettings){
        for (Laser laser : xmlNodes.confocalSettingRecords.laserRecords){
          if (laser.powerStateOn && laser.shutterOpen && (laser.wavelength == laserLineSetting.wavelength || 
            laser.name.equals("Argon") && laser.argonWavelengths.contains(laserLineSetting.wavelength))){
              laserLineSetting.laser = laser;
              laser.laserSetting = laserLineSetting;
            }
        }

        laserSettings.add(laserLineSetting);
      }
    }

    // after lasers with matching wavelengths were matched and there are still unmatched laser line settings, look for active WLL lasers
    for (LaserSetting laserSetting : laserSettings){
      if (laserSetting.laser == null){
        for (Laser laser : xmlNodes.confocalSettingRecords.laserRecords){
          if (laser.powerStateOn && laser.shutterOpen && laser.name.equals("WLL")){
            laserSetting.laser = laser;
            laser.laserSetting = laserSetting;
            break;
          }
        }
      }
    }

    for (Laser laser : xmlNodes.confocalSettingRecords.laserRecords){
      if (laser.laserSetting == null && laser.powerStateOn && laser.shutterOpen){
        // laser is not connected to an AOTF, but it is switched on and its associated shutter is open.
        // therefore, we assume here that it is connected to the light path and we create a laser setting for it.
        LaserSetting laserSetting = new LaserSetting();
        laserSetting.intensity = 1;
        laserSetting.wavelength = laser.wavelength;
        laserSetting.laser = laser;
        laserSettings.add(laserSetting);
      }
    }

    return laserSettings;
  }

  private static List<ConfocalChannelSetting> createChannelSettings(List<DetectorSetting> detectorSettings, List<LaserSetting> laserSettings){
    List<ConfocalChannelSetting> channelSettings = new ArrayList<>();

    for (DetectorSetting detectorSetting : detectorSettings){
      ConfocalChannelSetting channelSetting = new ConfocalChannelSetting();
      channelSetting.detectorSetting = detectorSetting;

      if (detectorSetting.name.contains("NDD")){
      // map MP lasers to NDD detectors 
        for (LaserSetting laserSetting : laserSettings){
          if (laserSetting.laser.name.contains("MP")){
            channelSetting.laserSetting = laserSetting;
            break;
          }
        }
      } else {
        // map detector settings to laser settings whose excitation wavelengths lie "left" of the detector range
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
}
