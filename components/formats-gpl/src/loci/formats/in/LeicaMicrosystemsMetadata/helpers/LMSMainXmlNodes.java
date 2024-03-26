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

package loci.formats.in.LeicaMicrosystemsMetadata.helpers;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.ConfocalSettingRecords;


/**
 * LMSMainXmlNodes is a storage class that holds the main XML nodes and XML layout information from LMS XML files.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class LMSMainXmlNodes {
  public Element imageNode;
  public Element imageDescription;
  public NodeList attachments;

  public Element hardwareSetting;
  
  public Element mainConfocalSetting; // ATLConfocalSettingDefinition under HardwareSetting attachment
  public Element masterConfocalSetting; //ATLConfocalSettingDefinition under LDM_Block_Sequential_Master
  public List<Element> sequentialConfocalSettings = new ArrayList<Element>(); //ATLConfocalSettingDefinitions under LDM_Block_Sequential_List
  
  public Element mainCameraSetting; // ATLCameraSettingDefinition under HardwareSetting attachment
  public Element masterCameraSetting; //ATLCameraSettingDefinition under LDM_Block_Sequential_Master
  public List<Element> sequentialCameraSettings = new ArrayList<Element>(); //ATLCameraSettingDefinitions under LDM_Block_Sequential_List
  public Element widefieldChannelConfig;
  public List<Element> widefieldChannelInfos = new ArrayList<Element>();

  public Element widefocalExperimentSettings; // attachment, only for MICA

  public NodeList scannerSettingRecords;
  public NodeList filterSettingRecords;
  public ConfocalSettingRecords confocalSettingRecords = new ConfocalSettingRecords();

  public boolean isMicaImage;

  public enum HardwareSettingLayout {
    OLD, // e.g. SP5 --> <Attachment Name "HardwareSettingList" .. > <HardwareSetting...>
    NEW, // e.g. SP8, STELLARIS, MICA, ... --> <Attachment Name="HardwareSetting" ...>
    NONE // e.g. some depth map or multifocus images don't have a hardware setting
  }
  public HardwareSettingLayout hardwareSettingLayout;

  public enum AtlSettingLayout {
    CONFOCAL_OLD,
    CONFOCAL_NEW,
    WIDEFIELD,
    MICA_CONFOCAL,
    MICA_WIDEFIELD,
    MICA_WIDEFOCAL,
    UNKNOWN
  }

  public AtlSettingLayout atlSettingLayout = AtlSettingLayout.UNKNOWN;

  public enum DataSourceType {
    UNDEFINED,
    CAMERA, // images acquired with widefield systems or widefield images acquired with MICA
    CONFOCAL, // images acquired with confocal systems or confocal images acquired with MICA
    SPIM,
    WIDEFOCAL // e.g. confocal images acquired with MICA with additional TL channel
  }
  public DataSourceType dataSourceType;

  /**
   * Depending on hardware setting layout and data source type, it returns the main ATL setting that
   * shall be used for extracting further hardware settings information.
   */
  public Element getAtlSetting(){
    switch (atlSettingLayout){
      case CONFOCAL_OLD:
        return masterConfocalSetting;
      case CONFOCAL_NEW:
      case MICA_CONFOCAL:
        return mainConfocalSetting;
      case WIDEFIELD:
      case MICA_WIDEFIELD:
      case MICA_WIDEFOCAL:
        return mainCameraSetting != null ? mainCameraSetting : masterCameraSetting;
      default:
        return null;
    }
  }

  /**
   * Depending on hardware setting layout and data source type, it returns the main ATL confocal setting that
   * shall be used for extracting further hardware settings information.
   */
  public Element getAtlConfocalSetting(){
    switch (atlSettingLayout){
      case CONFOCAL_OLD:
      case MICA_WIDEFIELD:
      case MICA_WIDEFOCAL:
        return masterConfocalSetting;
      case CONFOCAL_NEW:
      case MICA_CONFOCAL:
        return mainConfocalSetting;
      case WIDEFIELD:
      default:
        return null;
    }
  }
}
