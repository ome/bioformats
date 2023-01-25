package loci.formats.in.LeicaMicrosystemsMetadata.writeOME;

import java.util.List;

import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.meta.MetadataStore;
import ome.xml.model.enums.DetectorType;

public class DetectorWriter extends OMEWriter {

  public static void initDetectors(List<Detector> detectors, int seriesIndex, MetadataStore store) {
    for (int i = 0; i < detectors.size(); i++) {
      Detector detector = detectors.get(i);
      detector.detectorId = MetadataTools.createLSID("Detector", seriesIndex, i);
      store.setDetectorID(detector.detectorId, seriesIndex, i);
      store.setDetectorModel(detector.model, seriesIndex, i);
      store.setDetectorZoom(detector.zoom, seriesIndex, i);
      store.setDetectorType(detector.type.equals("PMT") ? DetectorType.PMT : DetectorType.OTHER, seriesIndex, i);
    }
  }

  public static void initDetectorSettings(List<Channel> channels, int seriesIndex, MetadataStore store){
    for (int channelIndex = 0; channelIndex < channels.size(); channelIndex++){
      Channel channel = channels.get(channelIndex);

      if (channel.detectorSetting != null) {
        store.setDetectorSettingsID(channel.detectorSetting.detector.detectorId, seriesIndex, channelIndex);
        store.setDetectorSettingsOffset(channel.detectorSetting.offset, seriesIndex, channelIndex);
        store.setDetectorSettingsGain(channel.detectorSetting.gain, seriesIndex, channelIndex);
        store.setDetectorSettingsZoom(channel.detectorSetting.detector.zoom, seriesIndex, channelIndex);
      }
    }
  }
}
