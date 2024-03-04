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
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.HardwareSettingLayout;
import loci.formats.in.LeicaMicrosystemsMetadata.model.MicroscopeDetails;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Objective;
import ome.xml.model.enums.MicroscopeType;

/**
 * MicroscopeExtractor is a helper class for extracting microscope hardware information from LMS XML files.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class MicroscopeExtractor {
  
  public static MicroscopeDetails extractMicroscopeDetails(LMSMainXmlNodes xmlNodes){
    MicroscopeDetails micDetails = new MicroscopeDetails();

    micDetails.microscopeModel = MicroscopeExtractor.extractMicroscopeModel(xmlNodes);
    micDetails.microscopeType = MicroscopeExtractor.extractMicroscopeType(xmlNodes);
    micDetails.serialNumber = MicroscopeExtractor.extractMicroscopeSerialNumber(xmlNodes);
    micDetails.objective = MicroscopeExtractor.extractObjective(xmlNodes);

    return micDetails;
  }

  public static String extractMicroscopeModel(LMSMainXmlNodes xmlNodes){
    if (xmlNodes.hardwareSettingLayout == HardwareSettingLayout.OLD){
      Element scannerSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "ScannerSetting");
      NodeList scannerSettingRecords = Extractor.getDescendantNodesWithName(scannerSetting, "ScannerSettingRecord");
      Element systemTypeNode = (Element)Extractor.getNodeWithAttribute(scannerSettingRecords, "Identifier", "SystemType");
      return Extractor.getAttributeValue(systemTypeNode, "Variant");
    } else {
      return Extractor.getAttributeValue(xmlNodes.hardwareSetting, "SystemTypeName");
    }
  }

  public static MicroscopeType extractMicroscopeType(LMSMainXmlNodes xmlNodes){
    MicroscopeType micType = MicroscopeType.OTHER;
    Element setting = xmlNodes.getAtlSetting();
    String isInverse = Extractor.getAttributeValue(setting, "IsInverseMicroscopeModel");
    if (isInverse != ""){
      micType = isInverse.equals("1") ? MicroscopeType.INVERTED : MicroscopeType.UPRIGHT;
    }

    return micType;
  }

  public static String extractMicroscopeSerialNumber(LMSMainXmlNodes xmlNodes) {
    if (xmlNodes.masterConfocalSetting != null){
        return Extractor.getAttributeValue(xmlNodes.masterConfocalSetting, "SystemSerialNumber");
    } else if (xmlNodes.filterSettingRecords != null){
      Element systemNumberNode = (Element)Extractor.getNodeWithAttribute(xmlNodes.filterSettingRecords, "Description", "System Number");
      return Extractor.getAttributeValue(systemNumberNode, "Variant");
    } else {
      return "";
    }
  }

  public static Objective extractObjective(LMSMainXmlNodes xmlNodes){
    Element setting = xmlNodes.getAtlSetting();
    if (setting == null) return null;

    Objective objective = new Objective();
    // e.g. "HC PL APO    20x/0.70 DRY" or "HCX PL FLUOTAR    40x/0.75 DRY"
    objective.model = setting.getAttribute("ObjectiveName");
    objective.setCorrectionFromObjectiveName(objective.model);
    String naS = setting.getAttribute("NumericalAperture");
    objective.numericalAperture = Extractor.parseDouble(naS);
    objective.objectiveNumber = setting.getAttribute("ObjectiveNumber");
    String magnificationS = setting.getAttribute("Magnification");
    objective.magnification = Extractor.parseDouble(magnificationS);
    objective.immersion = setting.getAttribute("Immersion");
    String refractionIndexS = setting.getAttribute("RefractionIndex");
    objective.refractionIndex = Extractor.parseDouble(refractionIndexS);

    return objective;
  }
}
