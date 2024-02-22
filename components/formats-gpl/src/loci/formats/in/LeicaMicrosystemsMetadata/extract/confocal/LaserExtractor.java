package loci.formats.in.LeicaMicrosystemsMetadata.extract.confocal;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser.LmsLightSourceQualifier;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser.LmsLightSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.model.LaserSetting;

public class LaserExtractor extends Extractor {
  
  public static List<Laser> getLasers(Element atlConfocalSetting, Element alternativeSetting, NodeList filterSettingRecords){
    Node laserArray = getChildNodeWithName(atlConfocalSetting, "LaserArray");
    // in STELLARIS, laser array nodes are not included in sequential settings
    if (laserArray == null)
      laserArray = getChildNodeWithName(alternativeSetting, "LaserArray");

    NodeList laserNodes = laserArray.getChildNodes();
    List<Laser> lasers = new ArrayList<Laser>();

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

      String powerState = laserNode.getAttribute("PowerState");
      if (powerState.isEmpty()){
        // SP5
        List<Element> laserRecords = Extractor.getNodesWithAttributeAsElements(filterSettingRecords, "ClassName", "CLaser");
      for (Element laserRecord : laserRecords){
        String name = Extractor.getAttributeValue(laserRecord, "ObjectName");
        String attribute = Extractor.getAttributeValue(laserRecord, "Attribute");
        String variant = Extractor.getAttributeValue(laserRecord, "Variant");
        if (name.contains(laser.name) && attribute.equals("Power State")){
          laser.isActive = variant.equals("On");
          break;
        }
      }
      } else {
        // SP8, STELLARIS
        laser.isActive = powerState.equals("On");
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

      lasers.add(laser);
    }

    return lasers;
  }

  public static void addShutterInfoToLasers(List<Laser> lasers, Element atlConfocalSetting, Element alternativeSetting){
    Node shutterList = getChildNodeWithName(atlConfocalSetting, "ShutterList");
    // in STELLARIS, shutterlist nodes are not included in sequential settings
    if (shutterList == null)
      shutterList = getChildNodeWithName(alternativeSetting, "ShutterList");

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
            laser.isActive = isActive;
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
            laser.isActive = isActive;
            break;
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
        if (laser.hasSameLightSourceType(laserSetting) && (laser.wavelength == laserSetting.wavelength || 
        laser.name.equals("Argon") && laser.argonWavelengths.contains(laserSetting.wavelength))){
          laserSetting.laser = laser;
          laser.laserSetting = laserSetting;
          break;
        }
      }

      // if no matching laser was found, map WLL
      if (laserSetting.laser == null){
        for (Laser laser : lasers){
          if (laser.name.equals("WLL")){
            laserSetting.laser = laser;
            laser.laserSetting = laserSetting;
            break;
          }
        }
      }
    }

    for (Laser laser : lasers){
      if (laser.laserSetting == null && laser.isActive){
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
