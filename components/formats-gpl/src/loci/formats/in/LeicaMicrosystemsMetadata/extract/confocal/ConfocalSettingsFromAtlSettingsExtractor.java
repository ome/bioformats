package loci.formats.in.LeicaMicrosystemsMetadata.extract.confocal;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ConfocalAcquisitionSettings;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ConfocalChannelSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ConfocalSettingRecords;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.LaserSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Multiband;

public class ConfocalSettingsFromAtlSettingsExtractor extends Extractor {

  public static void extractChannelSettings(LMSMainXmlNodes xmlNodes, ConfocalAcquisitionSettings acquisitionSettings){
    Element atlConfocalSetting = xmlNodes.getAtlConfocalSetting();

    String zoomS = getAttributeValue(atlConfocalSetting, "Zoom");
    String readOutRateS = getAttributeValue(atlConfocalSetting, "ScanSpeed");
    String pinholeSizeS = Extractor.getAttributeValue(atlConfocalSetting, "Pinhole");

    acquisitionSettings.zoom = parseDouble(zoomS);
    acquisitionSettings.readOutRate = parseDouble(readOutRateS);
    acquisitionSettings.pinholeSize = Extractor.parseDouble(pinholeSizeS) * METER_MULTIPLY;
    
    if (xmlNodes.sequentialConfocalSettings.size() > 0){
      for (Element setting : xmlNodes.sequentialConfocalSettings){
        acquisitionSettings.channelSettings.addAll(extractChannelSettings(setting, atlConfocalSetting, xmlNodes.confocalSettingRecords));
     }
    } else {
      acquisitionSettings.channelSettings.addAll(extractChannelSettings(atlConfocalSetting, atlConfocalSetting, xmlNodes.confocalSettingRecords));
    }

    mapInstrumentDetectorsToChannelDetectorSettings(acquisitionSettings);
    mapInstrumentLasersToChannelLaserSettings(acquisitionSettings);
  }


  private static List<ConfocalChannelSetting> extractChannelSettings(Element atlConfocalSetting, Element alternativeSetting, ConfocalSettingRecords records){
    // get + map detector and multiband info
    List<DetectorSetting> detectorSettings = DetectorExtractor.getActiveDetectorSettings(atlConfocalSetting);
    List<Multiband> multibands = DetectorExtractor.getMultibands(atlConfocalSetting, alternativeSetting);
    DetectorExtractor.addMultibandInfoToDetectorSettings(detectorSettings, multibands);

    // get + map laser and AOTF laser line setting info
    List<Laser> lasers = LaserFromAtlSettingsExtractor.getLasers(atlConfocalSetting, alternativeSetting, records);
    LaserFromAtlSettingsExtractor.addShutterInfoToLasers(lasers, atlConfocalSetting, alternativeSetting);
    List<LaserSetting> laserSettings = LaserFromAtlSettingsExtractor.getLaserSettings(atlConfocalSetting);
    LaserFromAtlSettingsExtractor.mapLasersToLaserSettings(lasers, laserSettings);

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
