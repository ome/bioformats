package loci.formats.in.LeicaMicrosystemsMetadata.extract.confocal;

import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.ConfocalAcquisitionSettings;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.ConfocalChannelSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Multiband;

public class ConfocalSettingsFromSettingRecordsExtractor extends Extractor {

  public static void extractChannelSettingsFromSettingRecords(LMSMainXmlNodes xmlNodes, ConfocalAcquisitionSettings acquisitionSettings){
    acquisitionSettings.pinholeSize = xmlNodes.confocalSettingRecords.pinholeSize;
    acquisitionSettings.zoom = xmlNodes.confocalSettingRecords.zoom;
    acquisitionSettings.readOutRate = xmlNodes.confocalSettingRecords.readOutRate;

    // start with creating channel settings from multibands, since multiband records contain channel indices
    for (Detector detector : xmlNodes.confocalSettingRecords.detectorRecords){
      if (!detector.isActive) continue;
      
      DetectorSetting detectorSetting = new DetectorSetting();
      detectorSetting = new DetectorSetting();
      detectorSetting.name = detector.model;
      detectorSetting.gain = detector.gain;
      detectorSetting.offset = detector.offset;

      for (Multiband multiband : xmlNodes.confocalSettingRecords.multibandRecords){
        if (multiband.channel == detector.channel){
          detectorSetting.cutIn = multiband.leftWorld;
          detectorSetting.cutOut = multiband.rightWorld;
          detectorSetting.dyeName = multiband.dyeName;
          break;
        }
      }

      ConfocalChannelSetting channelSetting = new ConfocalChannelSetting();
      channelSetting.detectorSetting = detectorSetting;
      acquisitionSettings.channelSettings.add(channelSetting);
    }
  }
}
