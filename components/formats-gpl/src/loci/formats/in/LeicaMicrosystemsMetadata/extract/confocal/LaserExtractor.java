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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.ConfocalSettingRecords;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.LaserSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Laser.LmsLightSourceQualifier;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Laser.LmsLightSourceType;

/**
 * LaserExtractor is a helper class for extracting laser information from LMS XML files.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class LaserExtractor extends Extractor {
  
  /**
   * Creates Lasers from information extracted from ATL confocal settings
   * @param atlConfocalSetting the ATL confocal setting from which laser info shall be extracted
   * @param alternativeSetting the alternative ATL confocal setting (main ATL confocal setting) to look up laser array info for STELLARIS
   * @param confocalSettingRecords these are required to look up laser power states for SP5 images
   * @return
   */
  public static List<Laser> getLasers(Element atlConfocalSetting, Element alternativeSetting, ConfocalSettingRecords confocalSettingRecords){
    List<Laser> lasers = new ArrayList<Laser>();

    //SP5, SP8
    Node laserArray = getChildNodeWithName(atlConfocalSetting, "LaserArray");
    // in STELLARIS, laser array nodes are not included in sequential settings
    if (laserArray == null)
      laserArray = getChildNodeWithName(alternativeSetting, "LaserArray");

    NodeList laserNodes = laserArray.getChildNodes();

    for (int i = 0; i < laserNodes.getLength(); i++) {
      Element laserNode;
      try {
        laserNode = (Element) laserNodes.item(i);
      } catch (Exception e) {
        continue;
      }

      Laser laser = new Laser();
      laser.name = laserNode.getAttribute("LaserName");
      String wavelengthS = laserNode.getAttribute("Wavelength");
      laser.wavelength = parseDouble(wavelengthS);

      laser.powerState = laserNode.getAttribute("PowerState");
      if (!laser.powerState.isEmpty()){
        // SP8, STELLARIS
        laser.powerStateOn = laser.powerState.equals("On");
      }

      String lightSourceQualifierS = laserNode.getAttribute("LightSourceQualifier");
      if (!lightSourceQualifierS.isEmpty()){
        // SP5
        int lightSourceQualifier = parseInt(lightSourceQualifierS);
        laser.lightSourceQualifier = LmsLightSourceQualifier.getValue(lightSourceQualifier);
      } else {
        // SP8, STELLARIS
        String lightSourceTypeS = laserNode.getAttribute("LightSourceType");
        int lightSourceType = parseInt(lightSourceTypeS);
        laser.lightSourceType = LmsLightSourceType.getValue(lightSourceType);
      }

      // in SP5 images, the power state attribute can not be found in ATL setting > laser array > lasers,
      // in this case we need to look into the laser info extracted from filter setting records.
      if (laser.powerState.isEmpty() && confocalSettingRecords != null){
        for (Laser laserRecord : confocalSettingRecords.laserRecords){
          if (laserRecord.name.equals(laser.name)){
            laser.powerStateOn = laserRecord.powerStateOn;
            break;
          }
        }
      }

      lasers.add(laser);
    }

    return lasers;
  }

  public static void addShutterInfoToLasers(List<Laser> lasers, Element atlConfocalSetting, Element alternativeSetting){
    Node shutterList = getChildNodeWithName(atlConfocalSetting, "ShutterList");
    // in STELLARIS, shutterlist nodes are not included in sequential settings
    if (shutterList == null)
      shutterList = getChildNodeWithName(alternativeSetting, "ShutterList");
    // in some SP5 images, there is no ShutterList in ATL confocal settings
    if (shutterList == null) return;

    NodeList shutterNodes = shutterList.getChildNodes();
    
    for (int i = 0; i < shutterNodes.getLength(); i++) {
      Element shutterNode;
      try {
        shutterNode = (Element) shutterNodes.item(i);
      } catch (Exception e) {
        continue;
      }

      String isActiveS = shutterNode.getAttribute("IsActive");
      boolean isActive = isActiveS.equals("1");
      
      String lightSourceQualifierS = shutterNode.getAttribute("LightSourceQualifier");
      if (!lightSourceQualifierS.isEmpty()){
        // SP5
        int lightSourceQualifierInt = parseInt(lightSourceQualifierS);
        LmsLightSourceQualifier lightSourceQualifier = LmsLightSourceQualifier.getValue(lightSourceQualifierInt);
        for (Laser laser : lasers){
          if (laser.lightSourceQualifier == lightSourceQualifier){
            laser.shutterOpen = isActive;
            break;
          }
        }
      } else {
        // SP8, STELLARIS
        String lightSourceTypeS = shutterNode.getAttribute("LightSourceType");
        int lightSourceTypeInt = parseInt(lightSourceTypeS);
        LmsLightSourceType lightSourceType = LmsLightSourceType.getValue(lightSourceTypeInt);
        for (Laser laser : lasers){
          if (laser.lightSourceType == lightSourceType){
            laser.shutterOpen = isActive;
          }
        }
      }
    }
  }

  public static List<LaserSetting> getLaserSettings(Element atlConfocalSetting){
    Node aotfList = getChildNodeWithName(atlConfocalSetting, "AotfList");
    List<Element> aotfs = getChildNodesWithNameAsElement(aotfList, "Aotf");

    List<LaserSetting> laserSettings = new ArrayList<>();

    for (Element aotf : aotfs){
      List<Element> laserLineSettingNodes = getChildNodesWithNameAsElement(aotf, "LaserLineSetting");
      for (Element laserLineSettingNode : laserLineSettingNodes){
        String intensityDevS = laserLineSettingNode.getAttribute("IntensityDev");
        double intensityDev = parseDouble(intensityDevS);
        if(intensityDev == 0)
          continue;

        LaserSetting laserSetting = new LaserSetting();
        laserSetting.intensity = intensityDev;
        String waveLengthS = laserLineSettingNode.getAttribute("LaserLine");
        laserSetting.wavelength = parseDouble(waveLengthS);

        String lightSourceQualifierS = aotf.getAttribute("LightSourceQualifier");
        if (!lightSourceQualifierS.isEmpty()){
          // SP5
          int lightSourceQualifier = parseInt(lightSourceQualifierS);
          laserSetting.lightSourceQualifier = LmsLightSourceQualifier.getValue(lightSourceQualifier);
        } else {
          // SP8, STELLARIS
          String lightSourceTypeS = aotf.getAttribute("LightSourceType");
          int lightSourceType = parseInt(lightSourceTypeS);
          laserSetting.lightSourceType = LmsLightSourceType.getValue(lightSourceType);
        }

        laserSettings.add(laserSetting);
      }
    }

    return laserSettings;
  }

  public static void mapLasersToLaserSettings(List<Laser> lasers, List<LaserSetting> laserSettings){
    for (LaserSetting laserSetting : laserSettings){
      for (Laser laser : lasers){
        if (laser.powerStateOn && laser.shutterOpen && laser.hasSameLightSourceType(laserSetting) && 
        (laser.wavelength == laserSetting.wavelength || laser.name.equals("Argon") && laser.argonWavelengths.contains(laserSetting.wavelength))){
          laserSetting.laser = laser;
          laser.laserSetting = laserSetting;
          break;
        }
      }

      // after lasers with matching wavelengths were matched and there are still unmatched laser line settings, look for active WLL lasers
      if (laserSetting.laser == null){
        for (Laser laser : lasers){
          if (laser.powerStateOn && laser.shutterOpen && laser.name.equals("WLL") && laser.hasSameLightSourceType(laserSetting)){
            laserSetting.laser = laser;
            laser.laserSetting = laserSetting;
            break;
          }
        }
      }
    }

    for (Laser laser : lasers){
      if (laser.laserSetting == null && laser.powerStateOn && laser.shutterOpen){
        // laser is not connected to an AOTF, but it is switched on and its associated shutter is open.
        // therefore, we assume here that it is connected to the light path and we create a laser setting for it.
        LaserSetting laserSetting = new LaserSetting();
        laserSetting.intensity = 1;
        laserSetting.wavelength = laser.wavelength;
        laserSetting.laser = laser;
        laserSetting.lightSourceQualifier = laser.lightSourceQualifier;
        laserSetting.lightSourceType = laser.lightSourceType;
        laserSettings.add(laserSetting); 
      }
    }
  }
}
