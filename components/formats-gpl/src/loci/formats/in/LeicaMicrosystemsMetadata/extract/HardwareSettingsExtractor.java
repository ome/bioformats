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

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.AtlSettingLayout;
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
    //common hardware setting node for "newer" images
    xmlNodes.hardwareSetting = (Element)Extractor.getNodeWithAttribute(xmlNodes.attachments, "Name", "HardwareSetting");

    if (xmlNodes.hardwareSetting != null){
      //new hardware settings layout
      xmlNodes.hardwareSettingLayout = HardwareSettingLayout.NEW;
    } else {
      //look for old hardware setting layout
      Element hardwareSettingParent = (Element)Extractor.getNodeWithAttribute(xmlNodes.attachments, "Name", "HardwareSettingList");
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
    int dataSourceType = -1;

    if (xmlNodes.hardwareSettingLayout == HardwareSettingLayout.OLD){
      Element scannerSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "ScannerSetting");
      List<Element> scannerSettingRecords = Extractor.getChildNodesWithNameAsElement(scannerSetting, "ScannerSettingRecord");
      for (Element scannerSettingRecord : scannerSettingRecords){
        String identifier = scannerSettingRecord.getAttribute("Identifier");
        if (identifier.equals("eDataSource")){
          String variant = scannerSettingRecord.getAttribute("Variant");
          dataSourceType = Extractor.parseInt(variant);
          break;
        }
      }
    } else {
      String dataSourceTypeS = Extractor.getAttributeValue(xmlNodes.hardwareSetting, "DataSourceType");
      dataSourceType = Extractor.parseInt(dataSourceTypeS);
    }

    switch(dataSourceType){
      case 0:
        xmlNodes.dataSourceType = DataSourceType.CONFOCAL;
        break;
      case 1:
        xmlNodes.dataSourceType = DataSourceType.CAMERA;
        break;
      case 2:
        xmlNodes.dataSourceType = DataSourceType.SPIM;
        break;
      case 3:
        xmlNodes.dataSourceType = DataSourceType.WIDEFOCAL;
        break;
      default:
        xmlNodes.dataSourceType = DataSourceType.UNDEFINED;
        break;
    }
  }

  public static void extractAtlSettingLayout(LMSMainXmlNodes xmlNodes){
     if (xmlNodes.isMicaImage){
      if (xmlNodes.dataSourceType == DataSourceType.CONFOCAL)
        xmlNodes.atlSettingLayout = AtlSettingLayout.MICA_CONFOCAL;
      else if (xmlNodes.dataSourceType == DataSourceType.CAMERA)
        xmlNodes.atlSettingLayout = AtlSettingLayout.MICA_WIDEFIELD;
      else if (xmlNodes.dataSourceType == DataSourceType.WIDEFOCAL)
        xmlNodes.atlSettingLayout = AtlSettingLayout.MICA_WIDEFOCAL;
      else
        xmlNodes.atlSettingLayout = AtlSettingLayout.UNKNOWN;
     } else {
      if (xmlNodes.dataSourceType == DataSourceType.CONFOCAL)
        xmlNodes.atlSettingLayout = xmlNodes.hardwareSettingLayout == HardwareSettingLayout.OLD ? AtlSettingLayout.CONFOCAL_OLD : AtlSettingLayout.CONFOCAL_NEW;
      else if (xmlNodes.dataSourceType == DataSourceType.CAMERA)
        xmlNodes.atlSettingLayout = AtlSettingLayout.WIDEFIELD;
      else
        xmlNodes.atlSettingLayout = AtlSettingLayout.UNKNOWN;
     }
  }

  /**
   * Extracts main and LDM ATL settings and other nodes for different ATL settings layouts
   * @param xmlNodes has to contain atlSettingLayout
   */
  public static void extractHardwareSettingChildNodes(LMSMainXmlNodes xmlNodes){
    switch (xmlNodes.atlSettingLayout){
      case CONFOCAL_OLD:
        extractLDMConfocalSettings(xmlNodes);
        Element scannerSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "ScannerSetting");
        xmlNodes.scannerSettingRecords = Extractor.getDescendantNodesWithName(scannerSetting, "ScannerSettingRecord");
        Element filterSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "FilterSetting");
        xmlNodes.filterSettingRecords = Extractor.getDescendantNodesWithName(filterSetting, "FilterSettingRecord");
        break;
      case CONFOCAL_NEW:
        extractMainConfocalSetting(xmlNodes);
        extractLDMConfocalSettings(xmlNodes);
        break;
      case WIDEFIELD:
        extractMainCameraSetting(xmlNodes);
        extractLDMCameraSettings(xmlNodes);
        extractWidefieldChannelInfos(xmlNodes);
        break;
      case MICA_CONFOCAL:
        extractMainConfocalSetting(xmlNodes);
        extractLDMConfocalSettings(xmlNodes);
        extractLDMCameraSettings(xmlNodes);
        extractWidefocalExperimentSettings(xmlNodes);
        break;
      case MICA_WIDEFIELD:
      case MICA_WIDEFOCAL:
        extractMainCameraSetting(xmlNodes);
        extractLDMCameraSettings(xmlNodes);
        extractLDMConfocalSettings(xmlNodes);
        extractWidefieldChannelInfos(xmlNodes);
        extractWidefocalExperimentSettings(xmlNodes);
        break;
      default: break;
    }
  }


  /**
   * Adds main confocal setting to passed LMSMainXmlNodes
   * @param xmlNodes has to contain hardware setting
   */
  public static void extractMainConfocalSetting(LMSMainXmlNodes xmlNodes){
    xmlNodes.mainConfocalSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "ATLConfocalSettingDefinition");
  }


  /**
   * Adds main camera setting to passed LMSMainXmlNodes
   * @param xmlNodes has to contain hardware setting
   */
  public static void extractMainCameraSetting(LMSMainXmlNodes xmlNodes){
    xmlNodes.mainConfocalSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "ATLCameraSettingDefinition");
  }


  /**
   * Adds master and sequential confocal settings, if existing, to passed LMSMainXmlNodes
   * @param xmlNodes has to contain hardware setting
   */
  public static void extractLDMConfocalSettings(LMSMainXmlNodes xmlNodes){
    // get LDM_Block_Sequential
    Element ldmBlockSequential;
    if (xmlNodes.isMicaImage){
      Element blockWidefocal = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "Block_Widefocal");
      ldmBlockSequential = Extractor.getChildNodeWithNameAsElement(blockWidefocal, "LDM_Block_Sequential");
    } else {
      ldmBlockSequential = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "LDM_Block_Sequential");
    }

    // get master and sequential confocal settings
    if (ldmBlockSequential != null){
      Element ldmBlockSequentialMaster = Extractor.getChildNodeWithNameAsElement(ldmBlockSequential, "LDM_Block_Sequential_Master");
      xmlNodes.masterConfocalSetting = Extractor.getChildNodeWithNameAsElement(ldmBlockSequentialMaster, "ATLConfocalSettingDefinition");

      Element ldmBlockSequentialList = Extractor.getChildNodeWithNameAsElement(ldmBlockSequential, "LDM_Block_Sequential_List");
      NodeList sequentialConfocalSettings = ldmBlockSequentialList.getChildNodes();

      for (int channelIndex = 0; channelIndex < sequentialConfocalSettings.getLength(); channelIndex++){
        Element sequentialConfocalSetting;
        try {
          sequentialConfocalSetting = (Element)sequentialConfocalSettings.item(channelIndex);
        } catch (Exception e){
          continue;
        }
        xmlNodes.sequentialConfocalSettings.add(sequentialConfocalSetting);
      }
    }
  }


   /**
   * Adds master and sequential camera settings, if existing, to passed LMSMainXmlNodes
   * @param xmlNodes has to contain hardware setting
   */
  public static void extractLDMCameraSettings(LMSMainXmlNodes xmlNodes){
    // get LDM_Block_Widefield_Sequential
    Element ldmBlockWidefieldSequential;
    if (xmlNodes.isMicaImage){
      Element blockWidefocal = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "Block_Widefocal");
      ldmBlockWidefieldSequential = Extractor.getChildNodeWithNameAsElement(blockWidefocal, "LDM_Block_Widefield_Sequential");
    } else {
      ldmBlockWidefieldSequential = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "LDM_Block_Widefield_Sequential");
    }

    // get master and sequential camera settings
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
  }


  /**
   * Adds widefield channel infos, if existing, to passed LMSMainXmlNodes
   * @param xmlNodes has to contain main or sequential camera setting(s)
   */
  public static void extractWidefieldChannelInfos(LMSMainXmlNodes xmlNodes){
    if (xmlNodes.sequentialCameraSettings.size() > 0){
      //sequential camera settings > widefield channel config > widefield channel info
      for (int channelIndex = 0; channelIndex < xmlNodes.sequentialCameraSettings.size(); channelIndex++){
        Element sequentialCameraSetting = xmlNodes.sequentialCameraSettings.get(channelIndex);
        Element widefieldChannelConfig = Extractor.getChildNodeWithNameAsElement(sequentialCameraSetting, "WideFieldChannelConfigurator");
        Element widefieldChannelInfo = Extractor.getChildNodeWithNameAsElement(widefieldChannelConfig, "WideFieldChannelInfo");
        xmlNodes.widefieldChannelInfos.add(widefieldChannelInfo);
      }
    } else {
      //main camera setting > widefield channel config > widefield channel infos
      xmlNodes.widefieldChannelConfig = Extractor.getChildNodeWithNameAsElement(xmlNodes.mainCameraSetting, "WideFieldChannelConfigurator");
      if (xmlNodes.widefieldChannelConfig == null) return;

      NodeList widefieldChannelInfos = Extractor.getDescendantNodesWithName(xmlNodes.widefieldChannelConfig, "WideFieldChannelInfo");
      for (int channelIndex = 0; channelIndex < widefieldChannelInfos.getLength(); channelIndex++){
        Element widefieldChannelInfo = (Element)widefieldChannelInfos.item(channelIndex);
        xmlNodes.widefieldChannelInfos.add(widefieldChannelInfo);
      }
    }
  }

  public static void extractWidefocalExperimentSettings(LMSMainXmlNodes xmlNodes){
    xmlNodes.widefocalExperimentSettings = (Element)Extractor.getNodeWithAttribute(xmlNodes.attachments, 
      "Name", "WidefocalExperimentSettings");
  }
}
