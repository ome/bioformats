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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Aotf;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.ConfocalSettingRecords;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.LaserSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.confocal.Multiband;

/**
 * Helper class for translating confocal scanner and filter setting records to more easily accessible data structures
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class ConfocalSettingRecordsExtractor extends Extractor {
  public static void extractSettingRecords(LMSMainXmlNodes xmlNodes){

    Element scannerSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "ScannerSetting");
    xmlNodes.scannerSettingRecords = Extractor.getDescendantNodesWithName(scannerSetting, "ScannerSettingRecord");
    Element filterSetting = Extractor.getChildNodeWithNameAsElement(xmlNodes.hardwareSetting, "FilterSetting");
    xmlNodes.filterSettingRecords = Extractor.getDescendantNodesWithName(filterSetting, "FilterSettingRecord");

    if (xmlNodes.scannerSettingRecords == null || xmlNodes.filterSettingRecords == null) return;

    xmlNodes.confocalSettingRecords = new ConfocalSettingRecords();
    ConfocalSettingRecords records = xmlNodes.confocalSettingRecords;

    // scanner setting records
    for (int i = 0; i < xmlNodes.scannerSettingRecords.getLength(); i++){
      Node scannerRecord = xmlNodes.scannerSettingRecords.item(i);

      String identifier = getAttributeValue(scannerRecord, "Identifier");
      String variant = getAttributeValue(scannerRecord, "Variant");
      String description = getAttributeValue(scannerRecord, "Description");

      if (identifier.equals("dblPinhole")){
        records.pinholeSize = Extractor.parseDouble(variant) * METER_MULTIPLY;
      } else if (identifier.equals("dblZoom")){
        records.zoom = parseDouble(variant);
      } else if (identifier.equals("eDirectional")){
        records.reverseX = variant.equals("1");
      } else if (identifier.equals("eDirectionalY")){
        records.reverseY = variant.equals("1");
      } else if (identifier.contains("Shutter") || description.contains("Shutter")){
        extractShutterRecord(records, identifier, variant);
      }
    }

    // filter setting records
    for (int i = 0; i < xmlNodes.filterSettingRecords.getLength(); i++){
      Node filterRecord = xmlNodes.filterSettingRecords.item(i);
      
      String objectName = Extractor.getAttributeValue(filterRecord, "ObjectName");
      String className = Extractor.getAttributeValue(filterRecord, "ClassName");
      String attribute = Extractor.getAttributeValue(filterRecord, "Attribute");
      String data = Extractor.getAttributeValue(filterRecord, "Data");
      String variant = Extractor.getAttributeValue(filterRecord, "Variant");

      if (className.equals("CScanCtrlUnit") && attribute.equals("Speed")){
        records.readOutRate = parseDouble(variant);
      } else if (className.equals("CAotf")){
        extractAotfRecord(records, objectName, attribute, data, variant);
      } else if (className.equals("CLaser")){
        extractLaserRecord(records, objectName, attribute, variant);
      } else if (className.equals("CDetectionUnit")){
        extractDetectorRecord(records, objectName, attribute, data, variant);
      } else if (className.equals("CTurret")){
        extractTurretRecord(records, objectName, attribute, variant);
      } else if (className.equals("CSpectrophotometerUnit")){
        extractMultibandRecord(records, objectName, attribute, data, variant);
      }
    }
  }

  private static void extractShutterRecord(ConfocalSettingRecords records, String identifier, String variant){
    if (identifier.equals("bUseCARSLight"))
      records.shutterInfo.cars = variant.equals("1");
    else if (identifier.equals("bUseChaserUVShutter"))
      records.shutterInfo.chaserUv = variant.equals("1");
    else if (identifier.equals("bUseChaserVisibleShutter"))
      records.shutterInfo.chaserVisible = variant.equals("1");
    else if (identifier.equals("bUseFSOPOLight"))
      records.shutterInfo.fsopo = variant.equals("1");
    else if (identifier.equals("bUseMP2Shutter"))
      records.shutterInfo.mp2 = variant.equals("1");
    else if (identifier.equals("bUseMPShutter"))
      records.shutterInfo.mp = variant.equals("1");
    else if (identifier.equals("bUsePulsed635VisibleLight"))
      records.shutterInfo.pulsed635 = variant.equals("1");
    else if (identifier.equals("bUsePumpLight"))
      records.shutterInfo.pump = variant.equals("1");
    else if (identifier.equals("bUseSTED1Light"))
      records.shutterInfo.sted1 = variant.equals("1");
    else if (identifier.equals("bUseSTED2Light"))
      records.shutterInfo.sted2 = variant.equals("1");
    else if (identifier.equals("bUseSTED3Light"))
      records.shutterInfo.sted3 = variant.equals("1");
    else if (identifier.equals("bUseSTED4Light"))
      records.shutterInfo.sted4 = variant.equals("1");
    else if (identifier.equals("bUseStokesLight"))
      records.shutterInfo.stokes = variant.equals("1");
    else if (identifier.equals("bUseSuperContVisibleShutter"))
      records.shutterInfo.superContinuumVisible = variant.equals("1");
    else if (identifier.equals("bUseUV405Shutter"))
      records.shutterInfo.uv405 = variant.equals("1");
    else if (identifier.equals("bUseUVShutter"))
      records.shutterInfo.uv = variant.equals("1");
    else if (identifier.equals("bUseVisibleShutter"))
      records.shutterInfo.visible = variant.equals("1");
  }

  private static void extractLaserRecord(ConfocalSettingRecords records, String objectName, String attribute, String variant){
    //e.g. "Laser (HeNe 543, visible)"
    Pattern pattern = Pattern.compile("Laser \\(([a-zA-Z | \\d | \\w]+), ([a-zA-Z | \\d | \\w]+)\\)");
    Matcher matcher = pattern.matcher(objectName);
    if (!matcher.find()) return;

    String name = matcher.group(1);

    Laser currentLaser = new Laser();
    boolean laserAlreadyExists = false;

    for (Laser laser : records.laserRecords){
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
      records.laserRecords.add(currentLaser);
    }
  }

  private static void extractDetectorRecord(ConfocalSettingRecords records, String objectName, String attribute, String data, String variant){
    Detector currentDetector = new Detector();
    boolean detectorAlreadyExists = false;
    
    for (Detector detector : records.detectorRecords){
      if (detector.model.equals(objectName)){
        detectorAlreadyExists = true;
        currentDetector = detector;
        break;
      }
    }

    if(attribute.equals("State"))
      currentDetector.isActive = variant.equals("Active");
    else if (attribute.equals("VideoOffset"))
      currentDetector.offset = parseDouble(variant);
    else if (attribute.equals("HighVoltage"))
      currentDetector.gain = parseDouble(variant);

    if (!detectorAlreadyExists){
      currentDetector.model = objectName;
      currentDetector.channel = parseInt(data);
      records.detectorRecords.add(currentDetector);
    }
  }

  private static void extractTurretRecord(ConfocalSettingRecords records, String objectName, String attribute, String variant){
    if (attribute.equals("Objective")){
      records.objectiveRecord.model = variant;
      records.objectiveRecord.setCorrectionFromObjectiveName(variant);
      records.objectiveRecord.setImmersionFromObjectiveName(variant);
    } else if (attribute.equals("OrderNumber")){
      records.objectiveRecord.objectiveNumber = variant;
    } else if (attribute.equals("NumericalAperture")){
      records.objectiveRecord.numericalAperture = parseDouble(variant);
    } else if (attribute.equals("RefractionIndex")){
      records.objectiveRecord.refractionIndex = parseDouble(variant);
    }
  }

  private static void extractAotfRecord(ConfocalSettingRecords records, String objectName, String attribute, String data, String variant){
    if (objectName.endsWith("Low") || objectName.contains("AOBS")) return;

    Aotf currentAotf = new Aotf();
    boolean aotfAlreadyExists = false;
    
    for (Aotf aotf : records.aotfRecords){
      if (aotf.name.equals(objectName)){
        aotfAlreadyExists = true;
        currentAotf = aotf;
        break;
      }
    }

    if (attribute.equals("Intensity")){
      double intensity = parseDouble(variant);

      if (intensity > 0){
        double wavelength = parseDouble(data);

        LaserSetting currentLaserLineSetting = new LaserSetting();
        boolean laserLineAlreadyExists = false;
  
        for (LaserSetting laserLineSetting : currentAotf.laserLineSettings){
          if (laserLineSetting.wavelength == wavelength){
            laserLineAlreadyExists = true;
            currentLaserLineSetting = laserLineSetting;
            break;
          }
        }
  
        currentLaserLineSetting.intensity = intensity;
  
        if (!laserLineAlreadyExists){
          currentLaserLineSetting.wavelength = wavelength;
          currentAotf.laserLineSettings.add(currentLaserLineSetting);
        }
      }
    }

    if (!aotfAlreadyExists){
      currentAotf.name = objectName;
      Pattern pattern = Pattern.compile("(\\S+) [AOTF|EOM].*");
      Matcher matcher = pattern.matcher(objectName);
      if (matcher.find()){
        currentAotf.type = matcher.group(1);
      }
      
      records.aotfRecords.add(currentAotf);
    }
  }

  private static void extractMultibandRecord(ConfocalSettingRecords records, String objectName, String attribute, String data, String variant){
    Multiband currentMultiband = new Multiband();
    boolean multibandAlreadyExists = false;
    
    for (Multiband multiband : records.multibandRecords){
      if (multiband.name.equals(objectName)){
        multibandAlreadyExists = true;
        currentMultiband = multiband;
        break;
      }
    }
    
    if (attribute.equals("Wavelength")){
      if (data.equals("0"))
        currentMultiband.leftWorld = parseDouble(variant);
      else if (data.equals("1"))
        currentMultiband.rightWorld = parseDouble(variant);

    } else if (attribute.equals("Stain")){
      currentMultiband.dyeName = variant;
    }

    if (!multibandAlreadyExists){
      currentMultiband.name = objectName;

      Pattern pattern = Pattern.compile(".+Channel (\\d+)");
      Matcher matcher = pattern.matcher(objectName);
      if (matcher.find()){
        currentMultiband.channel = parseInt(matcher.group(1));
      }

      records.multibandRecords.add(currentMultiband);
    }
  }
}
