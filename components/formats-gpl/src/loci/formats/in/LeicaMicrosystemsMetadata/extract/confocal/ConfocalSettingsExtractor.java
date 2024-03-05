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

package loci.formats.in.LeicaMicrosystemsMetadata.extract.confocal;

import org.w3c.dom.Element;

import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.ConfocalAcquisitionSettings;

/**
 * This is a helper class for extracting confocal acquisition settings from LMS XML.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class ConfocalSettingsExtractor extends Extractor {

  public static void extractInstrumentSettings(LMSMainXmlNodes xmlNodes, ConfocalAcquisitionSettings acquisitionSettings){
    Element atlConfocalSetting = xmlNodes.getAtlConfocalSetting();

    if (atlConfocalSetting == null && xmlNodes.sequentialConfocalSettings.size() == 0 && xmlNodes.confocalSettingRecords != null){
      acquisitionSettings.lasers = xmlNodes.confocalSettingRecords.laserRecords;
      acquisitionSettings.detectors = xmlNodes.confocalSettingRecords.detectorRecords;
    }
    else {
      acquisitionSettings.lasers = LaserExtractor.getLasers(atlConfocalSetting, atlConfocalSetting, 
        xmlNodes.confocalSettingRecords);
      acquisitionSettings.detectors = DetectorExtractor.getDetectors(atlConfocalSetting, xmlNodes.confocalSettingRecords);
    }
  }

  public static void extractChannelSettings(LMSMainXmlNodes xmlNodes, ConfocalAcquisitionSettings acquisitionSettings){
    Element atlConfocalSetting = xmlNodes.getAtlConfocalSetting();
    if (atlConfocalSetting == null && xmlNodes.sequentialConfocalSettings.size() == 0 && xmlNodes.confocalSettingRecords != null)
      ConfocalSettingsFromSettingRecordsExtractor.extractChannelSettingsFromSettingRecords(xmlNodes, acquisitionSettings);
    else
      ConfocalSettingsFromAtlSettingsExtractor.extractChannelSettings(xmlNodes, acquisitionSettings);
  }
}
