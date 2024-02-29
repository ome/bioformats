package loci.formats.in.LeicaMicrosystemsMetadata.extract.confocal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.Tuple;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser.LmsLightSourceQualifier;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser.LmsLightSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.model.LaserSetting;

public class LaserExtractor extends Extractor {
  
  public static List<Laser> getLasers(Element atlConfocalSetting, Element alternativeSetting, NodeList filterSettingRecords){
    List<Laser> lasers = new ArrayList<Laser>();

    if (atlConfocalSetting == null && alternativeSetting == null){
      //SP5 images without LDM block
      lasers = extractLasersFromFilterSettingRecords(filterSettingRecords);
    } else {
      //SP5 images with LDM block, SP8, STELLARIS
      lasers = extractLasersFromAtlConfocalSetting(atlConfocalSetting, alternativeSetting);

      if (lasers.size() == 0) return lasers;

      // power state attribute could not be found in ATL setting > laser array > lasers
      if (lasers.get(0).powerState.isEmpty() && filterSettingRecords != null){
        // extract power state info from filter setting records
        List<Tuple<String, Boolean>> powerStateTuples = new ArrayList<>();
        List<Element> laserRecords = Extractor.getNodesWithAttributeAsElements(filterSettingRecords, "ClassName", "CLaser");
          for (Element laserRecord : laserRecords){
            String objectName = Extractor.getAttributeValue(laserRecord, "ObjectName");
            String attribute = Extractor.getAttributeValue(laserRecord, "Attribute");
            String variant = Extractor.getAttributeValue(laserRecord, "Variant");

            Pattern pattern = Pattern.compile("Laser \\(([a-zA-Z | \\d | \\w]+), ([a-zA-Z | \\d | \\w]+)\\)");
            Matcher matcher = pattern.matcher(objectName);
            if (!matcher.find()) continue;

            String name = matcher.group(1);

            if (attribute.equals("Power State")){
              Tuple<String, Boolean> tuple = new Tuple<String,Boolean>(name, variant.equals("On"));
              powerStateTuples.add(tuple);
            }
          }

        // add power state info to lasers
        for (Tuple<String, Boolean> tuple : powerStateTuples){
          for (Laser laser : lasers){
            if (laser.name.equals(tuple.first)){
              laser.powerStateOn = tuple.second;
              break;
            }
          }
        }
      }
    }

    return lasers;
  }

  private static List<Laser> extractLasersFromFilterSettingRecords(NodeList filterSettingRecords){
    List<Laser> lasers = new ArrayList<Laser>();

    List<Element> laserRecords = Extractor.getNodesWithAttributeAsElements(filterSettingRecords, "ClassName", "CLaser");
    for (Element laserRecord : laserRecords){
      String objectName = Extractor.getAttributeValue(laserRecord, "ObjectName");
      String attribute = Extractor.getAttributeValue(laserRecord, "Attribute");
      String variant = Extractor.getAttributeValue(laserRecord, "Variant");
      //e.g. "Laser (HeNe 543, visible)"
      Pattern pattern = Pattern.compile("Laser \\(([a-zA-Z | \\d | \\w]+), ([a-zA-Z | \\d | \\w]+)\\)");
      Matcher matcher = pattern.matcher(objectName);
      if (!matcher.find()) continue;

      String name = matcher.group(1);

      Laser currentLaser = new Laser();
      boolean laserAlreadyExists = false;
      for (Laser laser : lasers){
        if (laser.name.equals(name)){
          laserAlreadyExists = true;
          currentLaser = laser;
          break;
        }
      }

      if(attribute.equals("Wavelength"))
        currentLaser.wavelength = parseDouble(variant);
      else if (attribute.equals("Power State"))
        currentLaser.powerStateOn = variant.equals("On");

      if (!laserAlreadyExists){
        currentLaser.name = name;
        lasers.add(currentLaser);
      }
    }

    return lasers;
  }

  private static List<Laser> extractLasersFromAtlConfocalSetting(Element atlConfocalSetting, Element alternativeSetting){
    List<Laser> lasers = new ArrayList<Laser>();

    //SP5 images with LDM block, SP8
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
