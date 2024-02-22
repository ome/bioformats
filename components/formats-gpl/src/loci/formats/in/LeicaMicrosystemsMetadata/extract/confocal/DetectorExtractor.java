package loci.formats.in.LeicaMicrosystemsMetadata.extract.confocal;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.AtlSettingLayout;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Multiband;

public class DetectorExtractor extends Extractor {
  
  /**
   * Returns a list of detectors it extracts from LMS XML detector nodes
   * @param xmlNodes
   * @return
   */
  public static List<Detector> getDetectors(LMSMainXmlNodes xmlNodes)
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
      List<Element> detectorRecords = getNodesWithAttributeAsElements(xmlNodes.filterSettingRecords, "ClassName", "CDetectionUnit");
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
