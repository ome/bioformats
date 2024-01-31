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

import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.HardwareSettingLayout;
import ome.xml.model.enums.MicroscopeType;

/**
 * MicroscopeExtractor is a helper class for extracting microscope hardware information from LMS XML files.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
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
      Element setting = xmlNodes.dataSourceType == DataSourceType.CONFOCAL || xmlNodes.dataSourceType == DataSourceType.WIDEFOCAL ?
        xmlNodes.mainConfocalSetting : xmlNodes.mainCameraSetting;
      return Extractor.getAttributeValue(setting, "SystemSerialNumber");
    }
  }
}
