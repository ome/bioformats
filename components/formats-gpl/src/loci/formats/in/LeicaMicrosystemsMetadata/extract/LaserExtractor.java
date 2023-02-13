package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.LaserSetting;

public class LaserExtractor extends Extractor {
  
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
          LaserSetting setting = new LaserSetting();
          setting.sequenceIndex = sequenceIndex;
          setting.laserSettingIndex = laserSettingIndex;

          String intensityDevS = getAttributeValue(laserLineSettings.item(laserSettingIndex), "IntensityDev");
          setting.intensity = parseDouble(intensityDevS);
          //realIntensity = 100d - intensity; ???

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
