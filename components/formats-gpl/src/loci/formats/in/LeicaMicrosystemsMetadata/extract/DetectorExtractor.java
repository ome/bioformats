package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;

public class DetectorExtractor extends Extractor {

  public static List<Detector> extractDetectors(LMSMainXmlNodes xmlNodes)
  {
    Element setting = xmlNodes.getAtlSetting();

    String zoomS = getAttributeValue(setting, "Zoom");
    double zoom = parseDouble(zoomS);

    // main confocal settings > detector list: instrument detectors
    Node detectorList = getChildNodeWithName(setting, "DetectorList");
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

  public static List<DetectorSetting> extractDetectorSettings(LMSMainXmlNodes xmlNodes, List<Detector> detectors){
    Element atlSetting = xmlNodes.getAtlSetting();

    List<DetectorSetting> activeDetectorSettings = new ArrayList<DetectorSetting>();

    int sequenceIndex = 0;
    for (int i = 0; i < xmlNodes.sequentialConfocalSettings.size(); i++) {
      Node seqDetectorList = getChildNodeWithName(xmlNodes.sequentialConfocalSettings.get(i), "DetectorList");
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

        String detectorName = detectorNode.getAttribute("Name");

        detectorSetting.transmittedLightMode = detectorName.toLowerCase().contains("trans")
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

        for (Detector detector : detectors) {
          if (detectorName.equals(detector.model)) {
            detectorSetting.detector = detector;
            break;
          }
        }

        detectorSettings.add(detectorSetting);
        detectorListIndex++;
      }

      String readOutRateS = getAttributeValue(atlSetting, "ScanSpeed");
      double readOutRate = parseDouble(readOutRateS);

      for (DetectorSetting setting : detectorSettings) {
        if (setting.isActive){
          setting.readOutRate = readOutRate;
          activeDetectorSettings.add(setting);
        }
      }

      sequenceIndex++;
    }

    return activeDetectorSettings;
  }
}
