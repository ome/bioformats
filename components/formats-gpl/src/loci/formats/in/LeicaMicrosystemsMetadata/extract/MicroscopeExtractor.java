package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import org.w3c.dom.Element;

import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.HardwareSettingLayout;
import ome.xml.model.enums.MicroscopeType;

public class MicroscopeExtractor {
  
  public static String extractMicroscopeModel(LMSMainXmlNodes xmlNodes){
    if (xmlNodes.hardwareSettingLayout == HardwareSettingLayout.OLD){
      Element systemTypeNode = (Element)Extractor.getNodeWithAttribute(xmlNodes.scannerSettingRecords, "Identifier", "SystemType");
      return Extractor.getAttributeValue(systemTypeNode, "Variant");
    } else {
      return Extractor.getAttributeValue(xmlNodes.hardwareSetting, "SystemTypeName");
    }
  }

  public static MicroscopeType extractMicroscopeType(LMSMainXmlNodes xmlNodes){
    MicroscopeType micType = null;
    
    if (xmlNodes.hardwareSettingLayout == HardwareSettingLayout.OLD){
      try {
        micType = MetadataTools.getMicroscopeType("Other");
      } catch (Exception e){
        e.printStackTrace();
      }
    } else {
      Element setting = xmlNodes.dataSourceType == DataSourceType.CONFOCAL ? xmlNodes.mainConfocalSetting : xmlNodes.mainCameraSetting;
      String isInverse = Extractor.getAttributeValue(setting, "IsInverseMicroscopeModel");
      micType = isInverse.equals("1") ? MicroscopeType.INVERTED : MicroscopeType.UPRIGHT;
    }

    return micType;
  }

  public static String extractMicroscopeSerialNumber(LMSMainXmlNodes xmlNodes) {
    if (xmlNodes.hardwareSettingLayout == HardwareSettingLayout.OLD){
      Element systemNumberNode = (Element)Extractor.getNodeWithAttribute(xmlNodes.filterSettingRecords, "Description", "System Number");
      return Extractor.getAttributeValue(systemNumberNode, "Variant");
    } else {
      Element setting = xmlNodes.dataSourceType == DataSourceType.CONFOCAL ? xmlNodes.mainConfocalSetting : xmlNodes.mainCameraSetting;
      return Extractor.getAttributeValue(setting, "SystemSerialNumber");
    }
  }
}
