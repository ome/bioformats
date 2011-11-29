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

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.DateTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
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

  private Stack<String> nameStack = new Stack<String>();

  private String elementName, collection;
  private int count = 0, numChannels, extras;

  private Vector<String> lutNames;
  private Vector<Float> xPos, yPos, zPos;
  private double physicalSizeX, physicalSizeY;

  private int numDatasets = -1;
  private Hashtable globalMetadata;

  private MetadataStore store;

  private int nextChannel = 0;
  private Float zoom, pinhole;
  private Vector<Integer> detectorIndices;
  private String filterWheelName;
  private int nextFilter = 0;
  private int nextROI = 0;
  private ROI roi;
  private boolean alternateCenter = false;

  private boolean linkedInstruments = false;
  private int detectorChannel = 0;

  private Vector<CoreMetadata> core;

  private boolean canParse = true;
  private long firstStamp = 0;

  private Hashtable<Integer, String> bytesPerAxis;
  private Vector<MultiBand> multiBands = new Vector<MultiBand>();
  private Vector<Detector> detectors = new Vector<Detector>();
  private Vector<Laser> lasers = new Vector<Laser>();
  private Hashtable<String, Channel> channels =
    new Hashtable<String, Channel>();

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
    bytesPerAxis = new Hashtable<Integer, String>();
  }

  // -- LeicaHandler API methods --

  public Vector<CoreMetadata> getCoreMetadata() { return core; }

  public Hashtable getGlobalMetadata() { return globalMetadata; }

  public Vector<String> getLutNames() { return lutNames; }

  // -- DefaultHandler API methods --

  public void endElement(String uri, String localName, String qName) {
    if (!nameStack.empty() && nameStack.peek().equals(qName)) nameStack.pop();

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
        coreMeta.metadataComplete = true;
        coreMeta.littleEndian = true;
        coreMeta.interleaved = coreMeta.rgb;
        coreMeta.imageCount = coreMeta.sizeZ * coreMeta.sizeT;
        if (!coreMeta.rgb) coreMeta.imageCount *= coreMeta.sizeC;
        coreMeta.indexed = !coreMeta.rgb;
        coreMeta.falseColor = true;

        Integer[] bytes = bytesPerAxis.keySet().toArray(new Integer[0]);
        Arrays.sort(bytes);
        coreMeta.dimensionOrder = "XY";
        for (Integer nBytes : bytes) {
          String axis = bytesPerAxis.get(nBytes);
          if (coreMeta.dimensionOrder.indexOf(axis) == -1) {
            coreMeta.dimensionOrder += axis;
          }
        }
        String[] axes = new String[] {"Z", "C", "T"};
        for (String axis : axes) {
          if (coreMeta.dimensionOrder.indexOf(axis) == -1) {
            coreMeta.dimensionOrder += axis;
          }
        }

        core.setElementAt(coreMeta, numDatasets);
      }

      int nChannels = coreMeta.rgb ? 0 : numChannels;

      for (int c=0; c<nChannels; c++) {
        store.setLogicalChannelPinholeSize(pinhole, numDatasets, c);
      }

      for (int i=0; i<xPos.size(); i++) {
        int nPlanes = coreMeta.imageCount / (coreMeta.rgb ? 1 : coreMeta.sizeC);
        for (int image=0; image<nPlanes; image++) {
          int offset = image * nChannels + i;
          store.setStagePositionPositionX(xPos.get(i), numDatasets, 0, offset);
          store.setStagePositionPositionY(yPos.get(i), numDatasets, 0, offset);
          store.setStagePositionPositionZ(zPos.get(i), numDatasets, 0, offset);
        }
      }

      for (int c=0; c<nChannels; c++) {
        int index = c < detectorIndices.size() ?
          detectorIndices.get(c).intValue() : detectorIndices.size() - 1;
        if (index < 0 || index >= nChannels || index >= 0) break;
        String id = MetadataTools.createLSID("Detector", numDatasets, index);
        store.setDetectorSettingsDetector(id, numDatasets, c);
      }

      String[] keys = channels.keySet().toArray(new String[0]);
      Arrays.sort(keys);
      int nextC = 0;
      for (int c=0; c<keys.length; c++) {
        Channel ch = channels.get(keys[c]);
        if (ch.detector != null) {
          store.setDetectorSettingsDetector(ch.detector, numDatasets, nextC);
          store.setDetectorSettingsGain(ch.gain, numDatasets, nextC);
          store.setLogicalChannelExWave(ch.exWave, numDatasets, nextC);
          store.setLogicalChannelName(ch.name, numDatasets, nextC);
          nextC++;
        }
      }

      channels.clear();
      xPos.clear();
      yPos.clear();
      zPos.clear();
      detectorIndices.clear();
    }
    else if (qName.equals("Element")) {
      multiBands.clear();
      nextROI = 0;

      int nChannels = core.get(numDatasets).rgb ? 1 : numChannels;

      for (int c=0; c<detectorIndices.size(); c++) {
        int index = detectorIndices.get(c).intValue();
        if (c >= nChannels || index >= nChannels || index >= 0) break;
        String id = MetadataTools.createLSID("Detector", numDatasets, index);
        store.setDetectorSettingsDetector(id, numDatasets, index);
      }
      for (int c=0; c<nChannels; c++) {
        store.setLogicalChannelPinholeSize(pinhole, numDatasets, c);
      }
    }
    else if (qName.equals("Image")) {
      nextChannel = 0;
    }
    else if (qName.equals("LDM_Block_Sequential_Master")) {
      canParse = true;
    }
    else if (qName.equals("Annotation")) {
      roi.storeROI(store, numDatasets, nextROI++);
    }
  }

  public void startElement(String uri, String localName, String qName,
    Attributes attributes)
  {
    if (attributes.getLength() > 0 && !qName.equals("Element") &&
      !qName.equals("Attachment") && !qName.equals("LMSDataContainerHeader"))
    {
      nameStack.push(qName);
    }

    Hashtable h = getSeriesHashtable(numDatasets);
    if (qName.equals("LDM_Block_Sequential_Master")) {
      canParse = false;
    }
    else if (qName.startsWith("LDM")) {
      linkedInstruments = true;
    }

    if (!canParse) return;

    StringBuffer key = new StringBuffer();
    for (String k : nameStack) {
      key.append(k);
      key.append("|");
    }
    String suffix = attributes.getValue("Identifier");
    String value = attributes.getValue("Variant");
    if (suffix == null) suffix = attributes.getValue("Description");
    if (suffix != null && value != null) {
      int index = 1;
      while (h.get(key.toString() + suffix + " " + index) != null) index++;
      h.put(key.toString() + suffix + " " + index, value);
    }
    else {
      for (int i=0; i<attributes.getLength(); i++) {
        int index = 1;
        while (
          h.get(key.toString() + attributes.getQName(i) + " " + index) != null)
        {
          index++;
        }
        h.put(key.toString() + attributes.getQName(i) + " " + index,
          attributes.getValue(i));
      }
    }

    if (qName.equals("Element")) {
      elementName = attributes.getValue("Name");
    }
    else if (qName.equals("Collection")) {
      collection = elementName;
    }
    else if (qName.equals("Image")) {
      if (!linkedInstruments) {
        int c = 0;
        for (Detector d : detectors) {
          String id = MetadataTools.createLSID(
            "Detector", numDatasets, detectorChannel);
          store.setDetectorID(id, numDatasets, detectorChannel);
          store.setDetectorType(d.type, numDatasets, detectorChannel);
          store.setDetectorModel(d.model, numDatasets, detectorChannel);
          store.setDetectorZoom(d.zoom, numDatasets, detectorChannel);
          store.setDetectorOffset(d.offset, numDatasets, detectorChannel);
          store.setDetectorVoltage(d.voltage, numDatasets, detectorChannel);

          if (c < numChannels) {
            if (d.active) {
              store.setDetectorSettingsOffset(d.offset, numDatasets, c);
              store.setDetectorSettingsDetector(id, numDatasets, c);
              c++;
            }
          }
          detectorChannel++;
        }

        int filter = 0;
        for (int i=0; i<nextFilter; i++) {
          while (filter < detectors.size() && !detectors.get(filter).active) {
            filter++;
          }
          if (filter >= detectors.size() || filter >= nextFilter) break;
          String id = MetadataTools.createLSID("Filter", numDatasets, filter);
          if (i < numChannels && detectors.get(filter).active) {
            store.setLogicalChannelSecondaryEmissionFilter(
              id, numDatasets, i);
          }
          filter++;
        }
      }

      core.add(new CoreMetadata());
      numDatasets++;
      linkedInstruments = false;
      detectorChannel = 0;
      detectors.clear();
      lasers.clear();
      nextFilter = 0;
      String name = elementName;
      if (collection != null) name = collection + "/" + name;
      store.setImageName(name, numDatasets);
      String instrumentID = MetadataTools.createLSID("Instrument", numDatasets);
      store.setInstrumentID(instrumentID, numDatasets);
      store.setImageInstrumentRef(instrumentID, numDatasets);
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
      int bytes = Integer.parseInt(attributes.getValue("BytesInc"));
      if (bytes > 0) {
        bytesPerAxis.put(new Integer(bytes), "C");
      }
    }
    else if (qName.equals("DimensionDescription")) {
      int len = Integer.parseInt(attributes.getValue("NumberOfElements"));
      int id = Integer.parseInt(attributes.getValue("DimID"));
      double physicalLen = Double.parseDouble(attributes.getValue("Length"));
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
          physicalSizeX = physicalSize.doubleValue();
          store.setDimensionsPhysicalSizeX(physicalSize, numDatasets, 0);
          break;
        case 2: // Y axis
          if (coreMeta.sizeY != 0) {
            if (coreMeta.sizeZ == 1) {
              coreMeta.sizeZ = len;
            }
            else if (coreMeta.sizeT == 1) {
              coreMeta.sizeT = len;
            }
          }
          else {
            coreMeta.sizeY = len;
            physicalSizeY = physicalSize.doubleValue();
            store.setDimensionsPhysicalSizeY(physicalSize, numDatasets, 0);
          }
          break;
        case 3: // Z axis
          if (coreMeta.sizeY == 0) {
            // XZ scan - swap Y and Z
            coreMeta.sizeY = len;
            coreMeta.sizeZ = 1;
            physicalSizeY = physicalSize.doubleValue();
            store.setDimensionsPhysicalSizeY(physicalSize, numDatasets, 0);
          }
          else {
            coreMeta.sizeZ = len;
          }
          bytesPerAxis.put(new Integer(nBytes), "Z");
          break;
        case 4: // T axis
          if (coreMeta.sizeY == 0) {
            // XT scan - swap Y and T
            coreMeta.sizeY = len;
            coreMeta.sizeT = 1;
            physicalSizeY = physicalSize.doubleValue();
            store.setDimensionsPhysicalSizeY(physicalSize, numDatasets, 0);
          }
          else {
            coreMeta.sizeT = len;
          }
          bytesPerAxis.put(new Integer(nBytes), "T");
          break;
        default:
          extras *= len;
      }
      count++;
    }
    else if (qName.equals("ScannerSettingRecord")) {
      String id = attributes.getValue("Identifier");

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
      else if (id.equals("dblStepSize")) {
        float zStep = Float.parseFloat(value) * 1000000;
        store.setDimensionsPhysicalSizeZ(new Float(zStep), numDatasets, 0);
      }
      else if (id.equals("nDelayTime_s")) {
        store.setDimensionsTimeIncrement(new Float(value), numDatasets, 0);
      }
      else if (id.equals("CameraName")) {
        store.setDetectorModel(value, numDatasets, 0);
      }
      else if (id.indexOf("WFC") == 1) {
        int c = 0;
        try {
          c = Integer.parseInt(id.replaceAll("\\D", ""));
        }
        catch (NumberFormatException e) { }
        Channel channel = channels.get(numDatasets + "-" + c);
        if (channel == null) channel = new Channel();
        if (id.endsWith("ExposureTime") && c < numChannels) {
          store.setPlaneTimingExposureTime(new Float(value),
            numDatasets, 0, c);
        }
        else if (id.endsWith("Gain")) {
          channel.gain = new Float(value);

          String detectorID =
            MetadataTools.createLSID("Detector", numDatasets, 0);

          channel.detector = detectorID;
          store.setDetectorID(detectorID, numDatasets, 0);
          store.setDetectorType("CCD", numDatasets, 0);
        }
        else if (id.endsWith("WaveLength")) {
          channel.exWave = new Integer(value);
        }
        // NB: "UesrDefName" is not a typo.
        else if (id.endsWith("UesrDefName") && !value.equals("None")) {
          channel.name = value;
        }
        channels.put(numDatasets + "-" + c, channel);
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
      else if (objectClass.equals("CDetectionUnit")) {
        if (attribute.equals("State")) {
          Detector d = new Detector();
          d.channel = Integer.parseInt(attributes.getValue("Data"));
          d.type = "PMT";
          d.model = object;
          d.active = variant.equals("Active");
          d.zoom = zoom;
          detectors.add(d);
        }
        else if (attribute.equals("HighVoltage")) {
          Detector d = detectors.get(detectors.size() - 1);
          d.voltage = new Float(variant);
        }
        else if (attribute.equals("VideoOffset")) {
          Detector d = detectors.get(detectors.size() - 1);
          d.offset = new Float(variant);
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

            int mag = (int) Double.parseDouble(token.substring(0, x));
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
        String id = MetadataTools.createLSID("Objective", numDatasets, 0);
        store.setObjectiveID(id, numDatasets, 0);
        store.setObjectiveSettingsObjective(id, numDatasets);
        store.setObjectiveSettingsRefractiveIndex(new Float(variant),
          numDatasets);
      }
      else if (attribute.equals("XPos")) {
        int c = coreMeta.rgb || coreMeta.sizeC == 0 ? 1 : coreMeta.sizeC;
        int nPlanes = coreMeta.imageCount / c;
        Float posX = new Float(variant);
        for (int image=0; image<nPlanes; image++) {
          int index = image * (coreMeta.rgb ? 1 : coreMeta.sizeC);
          if (index >= nPlanes) continue;
          store.setStagePositionPositionX(posX, numDatasets, 0, index);
        }
        if (numChannels == 0) xPos.add(posX);
      }
      else if (attribute.equals("YPos")) {
        int c = coreMeta.rgb || coreMeta.sizeC == 0 ? 1 : coreMeta.sizeC;
        int nPlanes = coreMeta.imageCount / c;
        Float posY = new Float(variant);
        for (int image=0; image<nPlanes; image++) {
          int index = image * (coreMeta.rgb ? 1 : coreMeta.sizeC);
          if (index >= nPlanes) continue;
          store.setStagePositionPositionY(posY, numDatasets, 0, index);
        }
        if (numChannels == 0) yPos.add(posY);
      }
      else if (attribute.equals("ZPos")) {
        int c = coreMeta.rgb || coreMeta.sizeC == 0 ? 1 : coreMeta.sizeC;
        int nPlanes = coreMeta.imageCount / c;
        Float posZ = new Float(variant);
        for (int image=0; image<nPlanes; image++) {
          int index = image * (coreMeta.rgb ? 1 : coreMeta.sizeC);
          if (index >= nPlanes) continue;
          store.setStagePositionPositionZ(posZ, numDatasets, 0, index);
        }
        if (numChannels == 0) zPos.add(posZ);
      }
      else if (objectClass.equals("CSpectrophotometerUnit")) {
        Integer v = null;
        try {
          v = new Integer((int) Double.parseDouble(variant));
        }
        catch (NumberFormatException e) { }
        if (attributes.getValue("Description").endsWith("(left)")) {
          String id =
            MetadataTools.createLSID("Filter", numDatasets, nextFilter);
          store.setFilterID(id, numDatasets, nextFilter);
          store.setFilterModel(object, numDatasets, nextFilter);
          if (v != null) {
            store.setTransmittanceRangeCutIn(v, numDatasets, nextFilter);
          }
        }
        else if (attributes.getValue("Description").endsWith("(right)")) {
          if (v != null) {
            store.setTransmittanceRangeCutOut(v, numDatasets, nextFilter);
            nextFilter++;
          }
        }
      }
    }
    else if (qName.equals("Detector")) {
      Float gain = new Float(attributes.getValue("Gain"));
      Float offset = new Float(attributes.getValue("Offset"));
      boolean active = attributes.getValue("IsActive").equals("1");

      if (active) {
        // find the corresponding MultiBand and Detector
        MultiBand m = null;
        Detector detector = null;
        Laser laser = lasers.size() == 0 ? null : lasers.get(lasers.size() - 1);
        int channel = Integer.parseInt(attributes.getValue("Channel"));

        for (MultiBand mb : multiBands) {
          if (mb.channel == channel) {
            m = mb;
            break;
          }
        }

        for (Detector d : detectors) {
          if (d.channel == channel) {
            detector = d;
            break;
          }
        }

        String id =
          MetadataTools.createLSID("Detector", numDatasets, nextChannel);

        /* debug */
        System.out.println("nextChannel = " + nextChannel);
        System.out.println("  channel = " + channel);
        System.out.println("  numChannels = " + numChannels);
        System.out.println("  core.sizeC = " + core.get(numDatasets).sizeC);
        /* end debug */

        boolean validChannel = numChannels <= 0 || nextChannel < numChannels;

        if (m != null && validChannel) {
          store.setLogicalChannelName(m.dyeName, numDatasets, nextChannel);

          String filter =
            MetadataTools.createLSID("Filter", numDatasets, nextFilter);
          store.setFilterID(filter, numDatasets, nextFilter);
          store.setTransmittanceRangeCutIn(new Integer(m.cutIn), numDatasets,
            nextFilter);
          store.setTransmittanceRangeCutOut(new Integer(m.cutOut), numDatasets,
            nextFilter);
          store.setLogicalChannelSecondaryEmissionFilter(filter, numDatasets,
            nextChannel);
          nextFilter++;

          store.setDetectorID(id, numDatasets, nextChannel);
          store.setDetectorType("PMT", numDatasets, nextChannel);
          store.setDetectorSettingsGain(gain, numDatasets, nextChannel);
          store.setDetectorSettingsOffset(offset, numDatasets, nextChannel);
          store.setDetectorSettingsDetector(id, numDatasets, nextChannel);
        }

        if (detector != null && validChannel) {
          store.setDetectorID(id, numDatasets, nextChannel);
          store.setDetectorSettingsGain(gain, numDatasets, nextChannel);
          store.setDetectorSettingsOffset(offset, numDatasets, nextChannel);
          store.setDetectorSettingsDetector(id, numDatasets, nextChannel);
          store.setDetectorType(detector.type, numDatasets, nextChannel);
          store.setDetectorModel(detector.model, numDatasets, nextChannel);
          store.setDetectorZoom(detector.zoom, numDatasets, nextChannel);
          store.setDetectorOffset(detector.offset, numDatasets, nextChannel);
          store.setDetectorVoltage(detector.voltage, numDatasets,
            nextChannel);
        }

        if (laser != null && laser.intensity > 0 && validChannel) {
          store.setLightSourceSettingsLightSource(laser.id, numDatasets,
            nextChannel);
          store.setLightSourceSettingsAttenuation(
            new Float(laser.intensity / 100.0), numDatasets, nextChannel);
          store.setLogicalChannelExWave(laser.wavelength, numDatasets,
            nextChannel);
        }

        nextChannel++;
      }
    }
    else if (qName.equals("LaserLineSetting")) {
      Laser l = new Laser();
      l.index = Integer.parseInt(attributes.getValue("LineIndex"));
      int qualifier = Integer.parseInt(attributes.getValue("Qualifier"));
      l.index += (2 - (qualifier / 10));
      if (l.index < 0) l.index = 0;
      l.id = MetadataTools.createLSID("LightSource", numDatasets, l.index);
      l.wavelength = new Integer(attributes.getValue("LaserLine"));
      store.setLightSourceID(l.id, numDatasets, l.index);
      store.setLaserWavelength(l.wavelength, numDatasets, l.index);
      store.setLaserType("Unknown", numDatasets, l.index);
      store.setLaserLaserMedium("Unknown", numDatasets, l.index);

      l.intensity = Float.parseFloat(attributes.getValue("IntensityDev"));

      if (l.intensity > 0) lasers.add(l);
    }
    else if (qName.equals("TimeStamp")) {
      long high = Long.parseLong(attributes.getValue("HighInteger"));
      long low = Long.parseLong(attributes.getValue("LowInteger"));

      long ms = DateTools.getMillisFromTicks(high, low);
      if (numDatasets >= 0) {
        if (count == 0) {
          String date = DateTools.convertDate(ms, DateTools.COBOL);
          if (DateTools.getTime(date, DateTools.ISO8601_FORMAT) <
            System.currentTimeMillis())
          {
            store.setImageCreationDate(date, numDatasets);
          }
          firstStamp = ms;
          store.setPlaneTimingDeltaT(new Float(0), numDatasets, 0, count);
        }
        else {
          CoreMetadata coreMeta = core.get(numDatasets);
          int nImages = coreMeta.sizeZ * coreMeta.sizeT * coreMeta.sizeC;
          if (count < nImages) {
            ms -= firstStamp;
            store.setPlaneTimingDeltaT(
              new Float(ms / 1000.0), numDatasets, 0, count);
          }
        }
      }

      count++;
    }
    else if (qName.equals("RelTimeStamp")) {
      CoreMetadata coreMeta = core.get(numDatasets);
      int nImages = coreMeta.sizeZ * coreMeta.sizeT * coreMeta.sizeC;
      if (count < nImages) {
        Float time = new Float(attributes.getValue("Time"));
        store.setPlaneTimingDeltaT(time, numDatasets, 0, count++);
      }
    }
    else if (qName.equals("Annotation")) {
      roi = new ROI();
      String type = attributes.getValue("type");
      if (type != null) roi.type = Integer.parseInt(type);
      String color = attributes.getValue("color");
      if (color != null) roi.color = Integer.parseInt(color);
      roi.name = attributes.getValue("name");
      roi.fontName = attributes.getValue("fontName");
      roi.fontSize = attributes.getValue("fontSize");
      roi.transX = parseDouble(attributes.getValue("transTransX"));
      roi.transY = parseDouble(attributes.getValue("transTransY"));
      roi.scaleX = parseDouble(attributes.getValue("transScalingX"));
      roi.scaleY = parseDouble(attributes.getValue("transScalingY"));
      roi.rotation = parseDouble(attributes.getValue("transRotation"));
      String linewidth = attributes.getValue("linewidth");
      if (linewidth != null) roi.linewidth = Integer.parseInt(linewidth);
      roi.text = attributes.getValue("text");
    }
    else if (qName.equals("Vertex")) {
      String x = attributes.getValue("x").replaceAll(",", ".");
      String y = attributes.getValue("y").replaceAll(",", ".");
      roi.x.add(new Double(x));
      roi.y.add(new Double(y));
    }
    else if (qName.equals("ROI")) {
      alternateCenter = true;
    }
    else if (qName.equals("MultiBand")) {
      MultiBand m = new MultiBand();
      m.dyeName = attributes.getValue("DyeName");
      m.channel = Integer.parseInt(attributes.getValue("Channel"));
      m.cutIn = (int)
        Math.round(Double.parseDouble(attributes.getValue("LeftWorld")));
      m.cutOut = (int)
        Math.round(Double.parseDouble(attributes.getValue("RightWorld")));

      multiBands.add(m);
    }
    else if (qName.equals("ChannelInfo")) {
      int index = Integer.parseInt(attributes.getValue("Index"));
      channels.remove(numDatasets + "-" + index);
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

  // -- Helper class --

  class ROI {
    // -- Constants --

    public static final int TEXT = 512;
    public static final int SCALE_BAR = 8192;
    public static final int POLYGON = 32;
    public static final int RECTANGLE = 16;
    public static final int LINE = 256;
    public static final int ARROW = 2;

    // -- Fields --
    public int type;

    public Vector<Double> x = new Vector<Double>();
    public Vector<Double> y = new Vector<Double>();

    // center point of the ROI
    public double transX, transY;

    // transformation parameters
    public double scaleX, scaleY;
    public double rotation;

    public int color;
    public int linewidth;

    public String text;
    public String fontName;
    public String fontSize;
    public String name;

    private boolean normalized = false;

    // -- ROI API methods --

    public void storeROI(MetadataStore store, int series, int roi) {
      // keep in mind that vertices are given relative to the center
      // point of the ROI and the transX/transY values are relative to
      // the center point of the image

      if (text != null) store.setShapeText(text, series, roi, 0);
      if (fontName != null) store.setShapeFontFamily(fontName, series, roi, 0);
      if (fontSize != null) {
        store.setShapeFontSize(
          new Integer((int) Double.parseDouble(fontSize)), series, roi, 0);
      }
      store.setShapeStrokeColor(String.valueOf(color), series, roi, 0);
      store.setShapeStrokeWidth(new Integer(linewidth), series, roi, 0);

      if (!normalized) normalize();

      double cornerX = x.get(0).doubleValue();
      double cornerY = y.get(0).doubleValue();

      int centerX = (core.get(series).sizeX / 2) - 1;
      int centerY = (core.get(series).sizeY / 2) - 1;

      double roiX = centerX + transX;
      double roiY = centerY + transY;

      if (alternateCenter) {
        roiX = transX - 2 * cornerX;
        roiY = transY - 2 * cornerY;
      }

      // TODO : rotation/scaling not populated

      switch (type) {
        case POLYGON:
          StringBuffer points = new StringBuffer();
          for (int i=0; i<x.size(); i++) {
            points.append(x.get(i).doubleValue() + roiX);
            points.append(",");
            points.append(y.get(i).doubleValue() + roiY);
            if (i < x.size() - 1) points.append(" ");
          }
          store.setPolygonPoints(points.toString(), series, roi, 0);

          break;
        case TEXT:
        case RECTANGLE:
          store.setRectX(
            String.valueOf(roiX - Math.abs(cornerX)), series, roi, 0);
          store.setRectY(
            String.valueOf(roiY - Math.abs(cornerY)), series, roi, 0);
          double width = 2 * Math.abs(cornerX);
          double height = 2 * Math.abs(cornerY);
          store.setRectWidth(String.valueOf(width), series, roi, 0);
          store.setRectHeight(String.valueOf(height), series, roi, 0);

          break;
        case SCALE_BAR:
        case ARROW:
        case LINE:
          store.setLineX1(String.valueOf(roiX + x.get(0)), series, roi, 0);
          store.setLineY1(String.valueOf(roiY + y.get(0)), series, roi, 0);
          store.setLineX2(String.valueOf(roiX + x.get(1)), series, roi, 0);
          store.setLineY2(String.valueOf(roiY + y.get(1)), series, roi, 0);
          break;
      }
    }

    // -- Helper methods --

    /**
     * Vertices and transformation values are not stored in pixel coordinates.
     * We need to convert them from physical coordinates to pixel coordinates
     * so that they can be stored in a MetadataStore.
     */
    private void normalize() {
      if (normalized) return;

      // coordinates are in meters

      transX *= 1000000;
      transY *= 1000000;
      transX *= (1 / physicalSizeX);
      transY *= (1 / physicalSizeY);

      for (int i=0; i<x.size(); i++) {
        double coordinate = x.get(i).doubleValue() * 1000000;
        coordinate *= (1 / physicalSizeX);
        x.setElementAt(new Double(coordinate), i);
      }

      for (int i=0; i<y.size(); i++) {
        double coordinate = y.get(i).doubleValue() * 1000000;
        coordinate *= (1 / physicalSizeY);
        y.setElementAt(new Double(coordinate), i);
      }

      normalized = true;
    }
  }

  private double parseDouble(String number) {
    if (number != null) {
      number = number.replaceAll(",", ".");
      return Double.parseDouble(number);
    }
    return 0;
  }

  // -- Helper classes --

  class MultiBand {
    public int channel;
    public int cutIn;
    public int cutOut;
    public String dyeName;
  }

  class Detector {
    public int channel;
    public Float zoom;
    public String type;
    public String model;
    public boolean active;
    public Float voltage;
    public Float offset;
  }

  class Laser {
    public Integer wavelength;
    public double intensity;
    public String id;
    public int index;
  }

  class Channel {
    public String detector;
    public Float gain;
    public Integer exWave;
    public String name;
  }

}
