package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;

public class DetectorExtractor extends Extractor {

  public static List<Detector> extractDetectors(Element mainConfocalSetting, List<Element> sequentialConfocalSettings)
       {
    String zoomS = getAttributeValue(mainConfocalSetting, "Zoom");
    double zoom = parseDouble(zoomS);

    // main confocal settings > detector list: instrument detectors
    Node detectorList = getChildNodeWithName(mainConfocalSetting, "DetectorList");
    NodeList detectorNodes = detectorList.getChildNodes();
    List<Detector> detectors = new ArrayList<Detector>();

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

      detectors.add(detector);
    }

    return detectors;
  }

  public static List<DetectorSetting> extractDetectorSettings(List<Element> sequentialConfocalSettings, List<Detector> detectors){
    List<DetectorSetting> activeDetectorSettings = new ArrayList<DetectorSetting>();

    int sequenceIndex = 0;
    for (int i = 0; i < sequentialConfocalSettings.size(); i++) {
      Node seqDetectorList = getChildNodeWithName(sequentialConfocalSettings.get(i), "DetectorList");
      if (seqDetectorList == null)
        continue;

      NodeList seqDetectorNodes = seqDetectorList.getChildNodes();

      List<DetectorSetting> detectorSettings = new ArrayList<DetectorSetting>();

      int detectorListIndex = 0;
      for (int k = 0; k < seqDetectorNodes.getLength(); k++) {
        Element detectorNode;
        try {
          detectorNode = (Element) seqDetectorNodes.item(k);
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

      for (DetectorSetting setting : detectorSettings) {
        if (setting.isActive)
          activeDetectorSettings.add(setting);
      }

      sequenceIndex++;
    }

    return activeDetectorSettings;
  }
}
