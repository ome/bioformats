//
// LeicaHandler.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.DateTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatTools;
import loci.formats.meta.MetadataStore;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler for parsing XML in Leica LIF and Leica TCS files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/LeicaHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/LeicaHandler.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LeicaHandler extends DefaultHandler {

  // -- Fields --

  private String elementName, collection;
  private int count = 0, numChannels, extras;

  private Vector<String> lutNames;
  private Vector<Float> xPos, yPos, zPos;

  private int numDatasets = -1;
  private Hashtable globalMetadata;

  private MetadataStore store;

  private int nextLaser, channel, nextDetector = -1;
  private Float zoom, pinhole, readOutRate;
  private Vector<Integer> detectorIndices;
  private String filterWheelName;
  private int nextFilter = 0, filterIndex;

  private Vector<CoreMetadata> core;

  private boolean canParse = true;
  private long firstStamp = 0;

  // -- Constructor --

  public LeicaHandler(MetadataStore store) {
    super();
    globalMetadata = new Hashtable();
    lutNames = new Vector<String>();
    this.store = store;
    core = new Vector<CoreMetadata>();
    detectorIndices = new Vector<Integer>();
    xPos = new Vector<Float>();
    yPos = new Vector<Float>();
    zPos = new Vector<Float>();
  }

  // -- LeicaHandler API methods --

  public Vector<CoreMetadata> getCoreMetadata() { return core; }

  public Hashtable getGlobalMetadata() { return globalMetadata; }

  public Vector<String> getLutNames() { return lutNames; }

  // -- DefaultHandler API methods --

  public void endElement(String uri, String localName, String qName) {
    if (qName.equals("ImageDescription")) {
      CoreMetadata coreMeta = core.get(numDatasets);
      if (numChannels == 0) numChannels = 1;
      coreMeta.sizeC = numChannels;

      if (extras > 1) {
        if (coreMeta.sizeZ == 1) coreMeta.sizeZ = extras;
        else coreMeta.sizeT *= extras;
      }

      if (coreMeta.sizeX == 0 && coreMeta.sizeY == 0) {
        numDatasets--;
      }
      else {
        if (coreMeta.sizeX == 0) coreMeta.sizeX = 1;
        if (coreMeta.sizeZ == 0) coreMeta.sizeZ = 1;
        if (coreMeta.sizeT == 0) coreMeta.sizeT = 1;

        coreMeta.orderCertain = true;
        coreMeta.dimensionOrder = "XYCZT";
        coreMeta.metadataComplete = true;
        coreMeta.littleEndian = true;
        coreMeta.interleaved = coreMeta.rgb;
        coreMeta.imageCount = coreMeta.sizeZ * coreMeta.sizeT;
        if (!coreMeta.rgb) coreMeta.imageCount *= coreMeta.sizeC;
        coreMeta.indexed = true;
        coreMeta.falseColor = true;

        core.setElementAt(coreMeta, numDatasets);
      }

      if (readOutRate != null) {
        for (int c=0; c<numChannels; c++) {
          store.setDetectorSettingsReadOutRate(readOutRate, numDatasets, c);
        }
      }

      for (int c=0; c<numChannels; c++) {
        store.setLogicalChannelPinholeSize(pinhole, numDatasets, c);
      }

      for (int i=0; i<xPos.size(); i++) {
        int nPlanes = coreMeta.imageCount / (coreMeta.rgb ? 1 : coreMeta.sizeC);
        for (int image=0; image<nPlanes; image++) {
          int offset = image * numChannels + i;
          store.setStagePositionPositionX(xPos.get(i), numDatasets, 0, offset);
          store.setStagePositionPositionY(yPos.get(i), numDatasets, 0, offset);
          store.setStagePositionPositionZ(zPos.get(i), numDatasets, 0, offset);
        }
      }

      for (int c=0; c<detectorIndices.size(); c++) {
        store.setDetectorSettingsDetector(
          "Detector:" + detectorIndices.get(c), numDatasets, c);
      }

      xPos.clear();
      yPos.clear();
      zPos.clear();
      detectorIndices.clear();
    }
    else if (qName.equals("Element")) {
      nextLaser = 0;
      nextFilter = 0;
      nextDetector = -1;

      for (int c=0; c<detectorIndices.size(); c++) {
        store.setDetectorSettingsDetector(
          "Detector:" + detectorIndices.get(c), numDatasets, c);
      }
      for (int c=0; c<numChannels; c++) {
        store.setLogicalChannelPinholeSize(pinhole, numDatasets, c);
      }
    }
    else if (qName.equals("LDM_Block_Sequential_Master")) {
      canParse = true;
    }
  }

  public void startElement(String uri, String localName, String qName,
    Attributes attributes)
  {
    Hashtable h = getSeriesHashtable(numDatasets);
    if (qName.equals("LDM_Block_Sequential_Master")) {
      canParse = false;
    }

    if (!canParse) return;

    if (qName.equals("Element")) {
      elementName = attributes.getValue("Name");
    }
    else if (qName.equals("Collection")) {
      collection = elementName;
    }
    else if (qName.equals("Image")) {
      core.add(new CoreMetadata());
      numDatasets++;
      String name = elementName;
      if (collection != null) name = collection + "/" + name;
      store.setImageName(name, numDatasets);
      store.setInstrumentID("Instrument:" + numDatasets, numDatasets);
      store.setImageInstrumentRef("Instrument:" + numDatasets, numDatasets);
      channel = 0;
      numChannels = 0;
      extras = 1;
    }
    else if (qName.equals("Attachment")) {
      if (attributes.getValue("Name").equals("ContextDescription")) {
        store.setImageDescription(attributes.getValue("Content"), numDatasets);
      }
    }
    else if (qName.equals("ChannelDescription")) {
      count++;
      numChannels++;
      lutNames.add(attributes.getValue("LUTName"));
    }
    else if (qName.equals("DimensionDescription")) {
      int len = Integer.parseInt(attributes.getValue("NumberOfElements"));
      int id = Integer.parseInt(attributes.getValue("DimID"));
      float physicalLen = Float.parseFloat(attributes.getValue("Length"));
      String unit = attributes.getValue("Unit");
      int nBytes = Integer.parseInt(attributes.getValue("BytesInc"));

      physicalLen /= len;
      if (unit.equals("Ks")) {
        physicalLen /= 1000;
      }
      else if (unit.equals("m")) {
        physicalLen *= 1000000;
      }

      Float physicalSize = new Float(physicalLen);

      CoreMetadata coreMeta = core.get(core.size() - 1);

      switch (id) {
        case 1: // X axis
          coreMeta.sizeX = len;
          coreMeta.rgb = (nBytes % 3) == 0;
          if (coreMeta.rgb) nBytes /= 3;
          switch (nBytes) {
            case 1:
              coreMeta.pixelType = FormatTools.UINT8;
              break;
            case 2:
              coreMeta.pixelType = FormatTools.UINT16;
              break;
            case 4:
              coreMeta.pixelType = FormatTools.FLOAT;
              break;
          }
          store.setDimensionsPhysicalSizeX(physicalSize, numDatasets, 0);
          break;
        case 2: // Y axis
          if (coreMeta.sizeY != 0) {
            if (coreMeta.sizeZ == 1) {
              coreMeta.sizeZ = len;
              store.setDimensionsPhysicalSizeZ(physicalSize, numDatasets, 0);
            }
            else if (coreMeta.sizeT == 1) {
              coreMeta.sizeT = len;
              store.setDimensionsTimeIncrement(physicalSize, numDatasets, 0);
            }
          }
          else {
            coreMeta.sizeY = len;
            store.setDimensionsPhysicalSizeY(physicalSize, numDatasets, 0);
          }
          break;
        case 3: // Z axis
          if (coreMeta.sizeY == 0) {
            // XZ scan - swap Y and Z
            coreMeta.sizeY = len;
            coreMeta.sizeZ = 1;
            store.setDimensionsPhysicalSizeY(physicalSize, numDatasets, 0);
          }
          else {
            coreMeta.sizeZ = len;
            store.setDimensionsPhysicalSizeZ(physicalSize, numDatasets, 0);
          }
          break;
        case 4: // T axis
          if (coreMeta.sizeY == 0) {
            // XT scan - swap Y and T
            coreMeta.sizeY = len;
            coreMeta.sizeT = 1;
            store.setDimensionsPhysicalSizeY(physicalSize, numDatasets, 0);
          }
          else {
            coreMeta.sizeT = len;
            store.setDimensionsTimeIncrement(physicalSize, numDatasets, 0);
          }
          break;
        default:
          extras *= len;
      }
      count++;
    }
    else if (qName.equals("ScannerSettingRecord")) {
      String id = attributes.getValue("Identifier");
      String value = attributes.getValue("Variant");

      if (id.equals("SystemType")) {
        store.setMicroscopeModel(value, numDatasets);
        store.setMicroscopeType("Unknown", numDatasets);
      }
      else if (id.equals("dblPinhole")) {
        pinhole = new Float(Float.parseFloat(value) * 1000000);
      }
      else if (id.equals("dblZoom")) {
        zoom = new Float(value);
      }
      else if (id.equals("CameraName")) {
        store.setDetectorModel(value, numDatasets, 0);
      }
      else if (id.indexOf("WFC") == 1) {
        int c = Integer.parseInt(id.replaceAll("\\D", ""));
        if (id.endsWith("ExposureTime")) {
          store.setPlaneTimingExposureTime(new Float(value), numDatasets, 0, c);
        }
        else if (id.endsWith("Gain")) {
          store.setDetectorSettingsGain(new Float(value), numDatasets, c);
          store.setDetectorSettingsDetector("Detector:0", numDatasets, c);
        }
        else if (id.endsWith("WaveLength")) {
          store.setLogicalChannelExWave(new Integer(value), numDatasets, c);
        }
        else if (id.endsWith("UesrDefName")) {
          store.setLogicalChannelName(value, numDatasets, c);
        }
      }
    }
    else if (qName.equals("FilterSettingRecord")) {
      String object = attributes.getValue("ObjectName");
      String attribute = attributes.getValue("Attribute");
      String objectClass = attributes.getValue("ClassName");
      String variant = attributes.getValue("Variant");
      CoreMetadata coreMeta = core.get(numDatasets);

      if (attribute.equals("NumericalAperture")) {
        store.setObjectiveLensNA(new Float(variant), numDatasets, 0);
      }
      else if (attribute.equals("OrderNumber")) {
        store.setObjectiveSerialNumber(variant, numDatasets, 0);
      }
      else if (objectClass.equals("CLaser")) {
        if (attribute.equals("Wavelength")) {
          store.setLightSourceID(
            "LightSource:" + nextLaser, numDatasets, nextLaser);
          store.setLaserWavelength(
            new Integer(variant), numDatasets, nextLaser);

          String model =
            object.substring(object.indexOf("(") + 1, object.indexOf(")"));
          store.setLightSourceModel(model, numDatasets, nextLaser);

          nextLaser++;
        }
        else if (attribute.equals("Output Power")) {
          store.setLightSourcePower(
            new Float(variant), numDatasets, nextLaser - 1);
        }
      }
      else if (objectClass.equals("CDetectionUnit")) {
        if (attribute.equals("State")) {
          nextDetector++;
          String id = "Detector:" + nextDetector;
          store.setDetectorID(id, numDatasets, nextDetector);
          store.setDetectorModel(object, numDatasets, nextDetector);
          store.setDetectorType("Unknown", numDatasets, nextDetector);
          store.setDetectorZoom(zoom, numDatasets, nextDetector);
        }
        else if (attribute.equals("HighVoltage")) {
          store.setDetectorVoltage(
            new Float(variant), numDatasets, nextDetector);
        }
        else if (attribute.equals("VideoOffset")) {
          store.setDetectorOffset(
            new Float(variant), numDatasets, nextDetector);
        }
      }
      else if (attribute.equals("Objective")) {
        StringTokenizer tokens = new StringTokenizer(variant, " ");
        boolean foundMag = false;
        StringBuffer model = new StringBuffer();
        while (!foundMag) {
          String token = tokens.nextToken();
          int x = token.indexOf("x");
          if (x != -1) {
            foundMag = true;

            int mag = (int) Float.parseFloat(token.substring(0, x));
            String na = token.substring(x + 1);

            store.setObjectiveNominalMagnification(
              new Integer(mag), numDatasets, 0);
            store.setObjectiveLensNA(new Float(na), numDatasets, 0);
          }
          else {
            model.append(token);
            model.append(" ");
          }
        }

        if (tokens.hasMoreTokens()) {
          String immersion = tokens.nextToken();
          if (immersion == null || immersion.trim().equals("")) {
            immersion = "Unknown";
          }
          store.setObjectiveImmersion(immersion, numDatasets, 0);
        }
        if (tokens.hasMoreTokens()) {
          String correction = tokens.nextToken();
          if (correction == null || correction.trim().equals("")) {
            correction = "Unknown";
          }
          store.setObjectiveCorrection(correction, numDatasets, 0);
        }

        store.setObjectiveModel(model.toString().trim(), numDatasets, 0);
      }
      else if (attribute.equals("RefractionIndex")) {
        store.setObjectiveID("Objective:0", numDatasets, 0);
        store.setObjectiveSettingsObjective("Objective:0", numDatasets);
        store.setObjectiveSettingsRefractiveIndex(new Float(variant),
          numDatasets);
      }
      else if (attribute.equals("XPos")) {
        int nPlanes = coreMeta.imageCount / (coreMeta.rgb ? 1 : coreMeta.sizeC);
        Float posX = new Float(variant);
        for (int image=0; image<nPlanes; image++) {
          int index = image * numChannels + channel;
          store.setStagePositionPositionX(posX, numDatasets, 0, index);
        }
        if (numChannels == 0) xPos.add(posX);
      }
      else if (attribute.equals("YPos")) {
        int nPlanes = coreMeta.imageCount / (coreMeta.rgb ? 1 : coreMeta.sizeC);
        Float posY = new Float(variant);
        for (int image=0; image<nPlanes; image++) {
          int index = image * numChannels + channel;
          store.setStagePositionPositionY(posY, numDatasets, 0, index);
        }
        if (numChannels == 0) yPos.add(posY);
      }
      else if (attribute.equals("ZPos")) {
        int nPlanes = coreMeta.imageCount / (coreMeta.rgb ? 1 : coreMeta.sizeC);
        Float posZ = new Float(variant);
        for (int image=0; image<nPlanes; image++) {
          int index = image * numChannels + channel;
          store.setStagePositionPositionZ(posZ, numDatasets, 0, index);
        }
        if (numChannels == 0) zPos.add(posZ);
      }
      else if (attribute.equals("Speed")) {
        readOutRate = new Float(Float.parseFloat(variant) / 1000000);
      }
    }
    else if (qName.equals("MultiBand")) {
      String em = attributes.getValue("LeftWorld");
      String ex = attributes.getValue("RightWorld");
      Integer emWave = new Integer((int) Float.parseFloat(em));
      Integer exWave = new Integer((int) Float.parseFloat(ex));
      String name = attributes.getValue("DyeName");

      store.setLogicalChannelEmWave(emWave, numDatasets, channel);
      store.setLogicalChannelExWave(exWave, numDatasets, channel);
      store.setLogicalChannelName(name, numDatasets, channel);
      channel++;
    }
    else if (qName.equals("Detector")) {
      Float gain = new Float(attributes.getValue("Gain"));
      Float offset = new Float(attributes.getValue("Offset"));
      int index = Integer.parseInt(attributes.getValue("Channel")) - 1;

      int c = channel - 1;

      store.setDetectorSettingsGain(gain, numDatasets, c);
      store.setDetectorSettingsOffset(offset, numDatasets, c);
      store.setDetectorSettingsReadOutRate(readOutRate, numDatasets, c);
      detectorIndices.add(new Integer(index));
    }
    else if (qName.equals("LaserLineSetting")) {
      String wavelength = attributes.getValue("LaserLine");
      int index = Integer.parseInt(attributes.getValue("LineIndex"));
      String id = "LightSource:" + index;
      store.setLightSourceID(id, numDatasets, index);
      store.setLaserWavelength(new Integer(wavelength), numDatasets, index);
      store.setLaserType("Unknown", numDatasets, index);
      store.setLaserLaserMedium("Unknown", numDatasets, index);

      float intensity = Float.parseFloat(attributes.getValue("IntensityDev"));
      if (intensity > 0f) {
        store.setLightSourceSettingsLightSource(id, numDatasets, channel - 1);
        store.setLightSourceSettingsAttenuation(
          new Float(intensity / 100f), numDatasets, channel - 1);
      }
    }
    else if (qName.equals("TimeStamp")) {
      long high = Long.parseLong(attributes.getValue("HighInteger"));
      long low = Long.parseLong(attributes.getValue("LowInteger"));

      high <<= 32;
      if ((int) low < 0) {
        low &= 0xffffffffL;
      }
      long ms = (high + low) / 10000;
      if (count == 0) {
        String date = DateTools.convertDate(ms, DateTools.COBOL);
        store.setImageCreationDate(date, numDatasets);
        firstStamp = ms;
        store.setPlaneTimingDeltaT(new Float(0), numDatasets, 0, count);
      }
      else {
        ms -= firstStamp;
        store.setPlaneTimingDeltaT(new Float(ms / 1000), numDatasets, 0, count);
      }

      count++;
    }
    else if (qName.equals("RelTimeStamp")) {
      Float time = new Float(attributes.getValue("Time"));
      store.setPlaneTimingDeltaT(time, numDatasets, 0, count++);
    }
    else if (qName.equals("Wheel")) {
      filterIndex = Integer.parseInt(attributes.getValue("FilterIndex"));
    }
    else if (qName.equals("WheelName")) {
      String id = "Dichroic:" + nextFilter;
      store.setDichroicID(id, numDatasets, nextFilter);
      store.setDichroicModel(
        attributes.getValue("FilterName"), numDatasets, nextFilter);

      if (nextFilter == filterIndex) {
        String filterSet = "FilterSet:" + channel;
        store.setFilterSetID(filterSet, numDatasets, channel);
        store.setFilterSetDichroic(id, numDatasets, channel);
        store.setLogicalChannelFilterSet(filterSet, numDatasets, channel);
      }
      nextFilter++;
    }
    else count = 0;
    storeSeriesHashtable(numDatasets, h);
  }

  // -- Helper methods --

  private Hashtable getSeriesHashtable(int series) {
    if (series < 0 || series >= core.size()) return new Hashtable();
    return core.get(series).seriesMetadata;
  }

  private void storeSeriesHashtable(int series, Hashtable h) {
    if (series < 0) return;
    CoreMetadata coreMeta = core.get(series);
    coreMeta.seriesMetadata = h;
    core.setElementAt(coreMeta, series);
  }

}
