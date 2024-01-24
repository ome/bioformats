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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.CameraSettingsLayout;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.HardwareSettingLayout;

/**
 * HardwareSettingsExtractor is a helper class for extracting hardware setting nodes from LMS XML files with different layouts.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class HardwareSettingsExtractor {

/**
 * Adds the hardware setting node to the passed LMSMainXmlNodes
 * @param xmlNodes has to contain the imageNode, its hardwareSetting will be updated
 */
  public static void extractHardwareSetting(LMSMainXmlNodes xmlNodes){
    NodeList attachments = Extractor.getDescendantNodesWithName(xmlNodes.imageNode, "Attachment");

    //common hardware setting node for "newer" images
    xmlNodes.hardwareSetting = (Element)Extractor.getNodeWithAttribute(attachments, "Name", "HardwareSetting");

    if (xmlNodes.hardwareSetting != null){
      //new hardware settings layout
      xmlNodes.hardwareSettingLayout = HardwareSettingLayout.NEW;
    } else {
      //look for old hardware setting layout
      Element hardwareSettingParent = (Element)Extractor.getNodeWithAttribute(attachments, "Name", "HardwareSettingList");
      xmlNodes.hardwareSetting = hardwareSettingParent != null ? (Element)Extractor.getChildNodeWithNameAsElement(hardwareSettingParent, "HardwareSetting") : null;

      // no hardware setting found until here: it is assumed that it doesn't exist (e.g. multifocus / depth map images)
      xmlNodes.hardwareSettingLayout = xmlNodes.hardwareSetting != null ? HardwareSettingLayout.OLD : HardwareSettingLayout.NONE;
    }
  }

  /**
   * Adds the data source type to the LMSMainXmlNode
   * @param xmlNodes has to contain hardwareSetting and hardwareSettingLayout
   */
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

  /**
   * Adds main, master and sequential confocal settings, if existing, to passed LMSMainXmlNodes
   * @param xmlNodes has to contain hardwareSetting
   */
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

  /**
   * Adds main, master and sequential camera settings, if existing, to passed LMSMainXmlNodes
   * @param xmlNodes has to contain hardware setting
   */
  public static void extractCameraSettings(LMSMainXmlNodes xmlNodes){
    xmlNodes.mainCameraSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "ATLCameraSettingDefinition");

    Element ldmBlockWidefieldSequential = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "LDM_Block_Widefield_Sequential");
    if (ldmBlockWidefieldSequential != null){
      Element ldmBlockSequentialMaster = Extractor.getChildNodeWithNameAsElement(ldmBlockWidefieldSequential, "LDM_Block_Sequential_Master");
      xmlNodes.masterCameraSetting = Extractor.getChildNodeWithNameAsElement(ldmBlockSequentialMaster, "ATLCameraSettingDefinition");

      Element ldmBlockSequentialList = Extractor.getChildNodeWithNameAsElement(ldmBlockWidefieldSequential, "LDM_Block_Sequential_List");
      NodeList sequentialCameraSettings = ldmBlockSequentialList.getChildNodes();

      for (int channelIndex = 0; channelIndex < sequentialCameraSettings.getLength(); channelIndex++){
        Element sequentialCameraSetting;
        try {
          sequentialCameraSetting = (Element)sequentialCameraSettings.item(channelIndex);
        } catch (Exception e){
          continue;
        }
        xmlNodes.sequentialCameraSettings.add(sequentialCameraSetting);
      }
    }
    
    if (xmlNodes.sequentialCameraSettings.size() > 0){
      //sequential camera settings > config > info
      xmlNodes.cameraSettingsLayout = CameraSettingsLayout.SEQUENTIAL;
      
      for (int channelIndex = 0; channelIndex < xmlNodes.sequentialCameraSettings.size(); channelIndex++){
        Element sequentialCameraSetting = xmlNodes.sequentialCameraSettings.get(channelIndex);
        Element widefieldChannelConfig = Extractor.getChildNodeWithNameAsElement(sequentialCameraSetting, "WideFieldChannelConfigurator");
        Element widefieldChannelInfo = Extractor.getChildNodeWithNameAsElement(widefieldChannelConfig, "WideFieldChannelInfo");
        xmlNodes.widefieldChannelInfos.add(widefieldChannelInfo);
      }
    } else {
      //main camera setting > config > infos
      xmlNodes.cameraSettingsLayout = CameraSettingsLayout.SIMPLE;

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
