package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.HardwareSettingLayout;

public class HardwareSettingsExtractor {
  public static void getHardwareSetting(LMSMainXmlNodes xmlNodes){
    NodeList attachments = Extractor.getDescendantNodesWithName(xmlNodes.imageNode, "Attachment");

    //common hardware setting node for "newer" images
    xmlNodes.hardwareSetting = (Element)Extractor.getNodeWithAttribute(attachments, "Name", "HardwareSetting");

    //hardware setting node for e.g. SP5 images
    if (xmlNodes.hardwareSetting == null){
      xmlNodes.hardwareSettingLayout = HardwareSettingLayout.OLD;
      Element hardwareSettingParent = (Element)Extractor.getNodeWithAttribute(attachments, "Name", "HardwareSettingList");
      xmlNodes.hardwareSetting = hardwareSettingParent != null ? (Element)Extractor.getChildNodeWithNameAsElement(hardwareSettingParent, "HardwareSetting") : null;
    } else {
      xmlNodes.hardwareSettingLayout = HardwareSettingLayout.NEW;
    }
  }

  public static void extractDataSourceType(LMSMainXmlNodes xmlNodes){
    if (xmlNodes.hardwareSettingLayout == HardwareSettingLayout.OLD){
      // DataSourceType(Name) does not exist in older images (e.g. SP5), therefore we use the "hacky" way here
      // and check if confocal or camera settings definition nodes exist within hardware settings
      Element ldmBlockSequential = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "LDM_Block_Sequential");
      Element ldmBlockSequentialMaster = Extractor.getChildNodeWithNameAsElement(ldmBlockSequential, "LDM_Block_Sequential_Master");
      Element confocalSetting = Extractor.getChildNodeWithNameAsElement(ldmBlockSequentialMaster, "ATLConfocalSettingDefinition");
      if (confocalSetting != null){
        xmlNodes.dataSourceType = DataSourceType.CONFOCAL;
      }
      else {
        Element ldmBlockSequentialWF = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "LDM_Block_Sequential");
        Element ldmBlockSequentialMasterWF = Extractor.getChildNodeWithNameAsElement(ldmBlockSequentialWF, "LDM_Block_Sequential_Master");
        Element cameraSetting = Extractor.getChildNodeWithNameAsElement(ldmBlockSequentialMasterWF, "ATLCameraSettingDefinition");
        if (cameraSetting != null)
        xmlNodes.dataSourceType = DataSourceType.CAMERA;
      }
    } else {
      String dataSourceTypeS = Extractor.getAttributeValue(xmlNodes.hardwareSetting, "DataSourceTypeName");
      xmlNodes.dataSourceType = dataSourceTypeS.equals("Confocal") ? DataSourceType.CONFOCAL : DataSourceType.CAMERA;
    }
  }

  public static void extractConfocalSettings(LMSMainXmlNodes xmlNodes){
    if (xmlNodes.hardwareSettingLayout == HardwareSettingLayout.NEW){
      xmlNodes.mainConfocalSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "ATLConfocalSettingDefinition");
    }
    
    Node ldmBlockSequential = Extractor.getChildNodeWithName(xmlNodes.hardwareSetting, "LDM_Block_Sequential");
    if (ldmBlockSequential == null) return;

    Element ldmBlockSequentialMaster = Extractor.getChildNodeWithNameAsElement(ldmBlockSequential, "LDM_Block_Sequential_Master");
    xmlNodes.masterConfocalSetting = Extractor.getChildNodeWithNameAsElement(ldmBlockSequentialMaster, "ATLConfocalSettingDefinition");

    Node ldmBlockList = Extractor.getChildNodeWithName(ldmBlockSequential, "LDM_Block_Sequential_List");
    NodeList sequentialConfocalSettings = ldmBlockList.getChildNodes();

    for (int i = 0; i < sequentialConfocalSettings.getLength(); i++){
      Element sequentialConfocalSetting;
      try {
        sequentialConfocalSetting = (Element) sequentialConfocalSettings.item(i);
        xmlNodes.sequentialConfocalSettings.add(sequentialConfocalSetting);
      } catch (Exception e) {
        continue;
      }
    }
  }

  public static void extractCameraSettings(LMSMainXmlNodes xmlNodes){
    xmlNodes.mainCameraSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "ATLCameraSettingDefinition");

    Element ldmBlockWidefieldSequential = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "LDM_Block_Widefield_Sequential");
    if (ldmBlockWidefieldSequential != null){
      Element ldmBlockSequentialMaster = Extractor.getChildNodeWithNameAsElement(ldmBlockWidefieldSequential, "LDM_Block_Sequential_Master");
      xmlNodes.masterCameraSetting = Extractor.getChildNodeWithNameAsElement(ldmBlockSequentialMaster, "ATLConfocalSettingDefinition");

      Element ldmBlockSequentialList = Extractor.getChildNodeWithNameAsElement(ldmBlockWidefieldSequential, "LDM_Block_Sequential_List");
      NodeList sequentialCameraSettings = ldmBlockSequentialList.getChildNodes();

      for (int channelIndex = 0; channelIndex < sequentialCameraSettings.getLength(); channelIndex++){
        Element sequentialCameraSetting = (Element)sequentialCameraSettings.item(channelIndex);
        xmlNodes.sequentialCameraSettings.add(sequentialCameraSetting);
      }
    }
    
    if (xmlNodes.sequentialCameraSettings.size() > 0){
      //sequential camera settings > config > info
      for (int channelIndex = 0; channelIndex < xmlNodes.sequentialCameraSettings.size(); channelIndex++){
        Element sequentialCameraSetting = xmlNodes.sequentialCameraSettings.get(channelIndex);
        Element widefieldChannelConfig = Extractor.getChildNodeWithNameAsElement(sequentialCameraSetting, "WideFieldChannelConfigurator");
        Element widefieldChannelInfo = Extractor.getChildNodeWithNameAsElement(widefieldChannelConfig, "WideFieldChannelInfo");
        xmlNodes.widefieldChannelInfos.add(widefieldChannelInfo);
      }
    } else {
      //main camera setting > config > infos
      xmlNodes.widefieldChannelConfig = Extractor.getChildNodeWithNameAsElement(xmlNodes.mainCameraSetting, "WideFieldChannelConfigurator");
      if (xmlNodes.widefieldChannelConfig == null) return;

      NodeList widefieldChannelInfos = Extractor.getDescendantNodesWithName(xmlNodes.widefieldChannelConfig, "WideFieldChannelInfo");
      for (int channelIndex = 0; channelIndex < widefieldChannelInfos.getLength(); channelIndex++){
        Element widefieldChannelInfo = (Element)widefieldChannelInfos.item(channelIndex);
        xmlNodes.widefieldChannelInfos.add(widefieldChannelInfo);
      }
    }
  }
}
