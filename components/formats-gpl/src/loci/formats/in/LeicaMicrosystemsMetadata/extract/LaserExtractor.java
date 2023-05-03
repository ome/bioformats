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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.LaserSetting;

/**
 * LaserExtractor is a helper class for extracting laser information from LMS XML files.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class LaserExtractor extends Extractor {
  
  /**
   * Returns a list of {@Laser}s created from laser nodes
   * @param mainConfocalSettings
   */
  public static List<Laser> extractLasers(Element mainConfocalSettings) {
    List<Laser> lasers = new ArrayList<Laser>();

    Node laserArray = getChildNodeWithName(mainConfocalSettings, "LaserArray");
    if (laserArray == null)
      return lasers;

    NodeList laserNodes = laserArray.getChildNodes();

    for (int i = 0; i < laserNodes.getLength(); i++) {
      Element laserNode;
      try {
        laserNode = (Element) laserNodes.item(i);
      } catch (Exception e) {
        continue;
      }
      String powerState = laserNode.getAttribute("PowerState");
      String outputPowerWatt = laserNode.getAttribute("OutputPowerWatt");
      Laser laser = new Laser();
      laser.isActive = powerState.equals("On") && !outputPowerWatt.equals("0");
      laser.wavelength = parseDouble(laserNode.getAttribute("Wavelength"));
      laser.name = laserNode.getAttribute("LaserName");
      laser.wavelengthUnit = "nm";
      lasers.add(laser);
    }

    return lasers;
  }

  public static List<LaserSetting> extractLaserSettings(List<Element> sequentialConfocalSettings, List<Laser> lasers){
    List<LaserSetting> laserSettings = new ArrayList<LaserSetting>();

    for (int sequenceIndex = 0; sequenceIndex < sequentialConfocalSettings.size(); sequenceIndex++){
      Node aotfList = getChildNodeWithName(sequentialConfocalSettings.get(sequenceIndex), "AotfList");
      if (aotfList == null)
        continue;

      NodeList aotfs = aotfList.getChildNodes();
      for (int aotfIndex = 0; aotfIndex < aotfs.getLength(); aotfIndex++) {
        Element aotf;
        try {
          aotf = (Element)aotfs.item(aotfIndex);
        } catch (Exception e){
          continue;
        }
  
        NodeList laserLineSettings = getDescendantNodesWithName((Element)aotf, "LaserLineSetting");
        for (int laserSettingIndex = 0; laserSettingIndex < laserLineSettings.getLength(); laserSettingIndex++){
          String intensityDevS = getAttributeValue(laserLineSettings.item(laserSettingIndex), "IntensityDev");
          double intensity = parseDouble(intensityDevS);
          //ignore laser settings if lasers are not active
          if (intensity <= 0)
            continue;

          //realIntensity = 100d - intensity; ???

          LaserSetting setting = new LaserSetting();
          setting.sequenceIndex = sequenceIndex;
          setting.laserSettingIndex = laserSettingIndex;
          setting.intensity = intensity;

          String wavelengthS = getAttributeValue(laserLineSettings.item(laserSettingIndex), "LaserLine");
          int wavelength = parseInt(wavelengthS);

          for (Laser laser : lasers){
            if (laser.wavelength == wavelength){
              setting.laser = laser;
              break;
            }
          }

          laserSettings.add(setting);
        }
      }
    }

    return laserSettings;
  }
}
