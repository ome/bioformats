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

package loci.formats.in;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.DateTools;
import loci.common.xml.BaseHandler;
import loci.formats.CoreMetadata;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.MicroscopeType;
import ome.xml.model.enums.handlers.CorrectionEnumHandler;
import ome.xml.model.enums.handlers.DetectorTypeEnumHandler;
import ome.xml.model.enums.handlers.ImmersionEnumHandler;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

/**
 * SAX handler for parsing XML in Leica LIF and Leica TCS files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class LeicaHandler extends BaseHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(LeicaHandler.class);

  // -- Fields --

  private Deque<String> nameStack = new ArrayDeque<String>();

  private String elementName, collection;
  private int count = 0, numChannels, extras = 1;

  private Vector<String> lutNames;
  private List<Length> xPos, yPos, zPos;
  private double physicalSizeX, physicalSizeY;

  private int numDatasets = -1;
  private Hashtable globalMetadata;

  private MetadataStore store;

  private int nextChannel = 0;
  private Double zoom, pinhole;
  private List<Integer> detectorIndices;
  private int nextFilter = 0;
  private int nextROI = 0;
  private ROI roi;
  private boolean alternateCenter = false;

  private boolean linkedInstruments = false;
  private int detectorChannel = 0;

  private List<CoreMetadata> core;

  private boolean canParse = true;
  private long firstStamp = 0;

  private Map<Integer, String> bytesPerAxis;
  private List<MultiBand> multiBands = new ArrayList<MultiBand>();
  private List<Detector> detectors = new ArrayList<Detector>();
  private List<Laser> lasers = new ArrayList<Laser>();
  private Map<String, Channel> channels =
    new HashMap<String, Channel>();

  private MetadataLevel level;
  private int laserCount = 0;

  // -- Constructor --

  public LeicaHandler(MetadataStore store, MetadataLevel level) {
    super();
    globalMetadata = new Hashtable();
    lutNames = new Vector<String>();
    this.store = store;
    core = new ArrayList<CoreMetadata>();
    detectorIndices = new ArrayList<Integer>();
    xPos = new ArrayList<Length>();
    yPos = new ArrayList<Length>();
    zPos = new ArrayList<Length>();
    bytesPerAxis = new HashMap<Integer, String>();
    this.level = level;
  }

  // -- LeicaHandler API methods --

  public List<CoreMetadata> getCoreMetadataList() { return core; }

  public Hashtable getGlobalMetadata() { return globalMetadata; }

  public Vector<String> getLutNames() { return lutNames; }

  // -- DefaultHandler API methods --

  @Override
  public void endElement(String uri, String localName, String qName) {
    if (!nameStack.isEmpty() && nameStack.peek().equals(qName)) nameStack.pop();

    if (qName.equals("ImageDescription")) {
      CoreMetadata coreMeta = core.get(numDatasets);
      if (numChannels == 0) numChannels = 1;
      coreMeta.sizeC = numChannels;

      if (extras > 1) {
        if (coreMeta.sizeZ == 1) coreMeta.sizeZ = extras;
        else {
          if (coreMeta.sizeT == 0) coreMeta.sizeT = extras;
          else coreMeta.sizeT *= extras;
        }
      }

      if (coreMeta.sizeX == 0 && coreMeta.sizeY == 0) {
        if (numDatasets > 0) numDatasets--;
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

        core.set(numDatasets, coreMeta);
      }

      if (level != MetadataLevel.MINIMUM) {
        int nChannels = coreMeta.rgb ? 0 : numChannels;

        for (int c=0; c<nChannels; c++) {
          store.setChannelPinholeSize(new Length(pinhole, UNITS.MICROMETER), numDatasets, c);
        }

        for (int i=0; i<xPos.size(); i++) {
          int pos = i + 1;
          globalMetadata.put("X position for position #" + pos, xPos.get(i));
          globalMetadata.put("Y position for position #" + pos, yPos.get(i));
          globalMetadata.put("Z position for position #" + pos, zPos.get(i));
          for (int image=0; image<coreMeta.imageCount; image++) {
            store.setPlanePositionX(xPos.get(i), numDatasets, image);
            store.setPlanePositionY(yPos.get(i), numDatasets, image);
            store.setPlanePositionZ(zPos.get(i), numDatasets, image);
          }
        }

        for (int c=0; c<nChannels; c++) {
          int index = c < detectorIndices.size() ?
            detectorIndices.get(c).intValue() : detectorIndices.size() - 1;
          if (index < 0 || index >= nChannels || index >= 0) break;
          String id = MetadataTools.createLSID("Detector", numDatasets, index);
          store.setDetectorSettingsID(id, numDatasets, c);
        }

        String[] keys = channels.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for (int c=0; c<keys.length; c++) {
          Channel ch = channels.get(keys[c]);
          store.setDetectorSettingsID(ch.detector, numDatasets, c);
          store.setChannelExcitationWavelength(ch.exWave, numDatasets, c);
          store.setChannelName(ch.name, numDatasets, c);
          store.setDetectorSettingsGain(ch.gain, numDatasets, c);
        }
      }

      channels.clear();
      xPos.clear();
      yPos.clear();
      zPos.clear();
      detectorIndices.clear();
    }
    else if (qName.equals("Element") && level != MetadataLevel.MINIMUM) {
      multiBands.clear();
      nextROI = 0;

      if (numDatasets >= 0) {
        int nChannels = core.get(numDatasets).rgb ? 1 : numChannels;

        for (int c=0; c<detectorIndices.size(); c++) {
          int index = detectorIndices.get(c).intValue();
          if (c >= nChannels || index >= nChannels || index >= 0) break;
          String id = MetadataTools.createLSID("Detector", numDatasets, index);
          store.setDetectorSettingsID(id, numDatasets, index);
        }
        for (int c=0; c<nChannels; c++) {
          store.setChannelPinholeSize(new Length(pinhole, UNITS.MICROMETER), numDatasets, c);
        }
      }
    }
    else if (qName.equals("Image")) {
      nextChannel = 0;
    }
    else if (qName.equals("LDM_Block_Sequential_Master")) {
      canParse = true;
    }
    else if (qName.equals("Annotation") && level != MetadataLevel.MINIMUM) {
      roi.storeROI(store, numDatasets, nextROI++);
    }
  }

  @Override
  public void startElement(String uri, String localName, String qName,
    Attributes attributes)
  {
    if (attributes.getLength() > 0 && !qName.equals("Element") &&
      !qName.equals("Attachment") && !qName.equals("LMSDataContainerHeader"))
    {
      nameStack.push(qName);
    }

    int oldSeriesCount = numDatasets;
    Hashtable h = getSeriesHashtable(numDatasets);
    if (qName.equals("LDM_Block_Sequential_Master")) {
      canParse = false;
    }
    else if (qName.startsWith("LDM")) {
      linkedInstruments = true;
    }

    if (!canParse) return;

    final StringBuilder key = new StringBuilder();
    final Iterator<String> nameStackIterator = nameStack.descendingIterator();
    while (nameStackIterator.hasNext()) {
      final String k = nameStackIterator.next();
      key.append(k);
      key.append("|");
    }
    String suffix = attributes.getValue("Identifier");
    String value = attributes.getValue("Variant");
    if (suffix == null) suffix = attributes.getValue("Description");
    if (level != MetadataLevel.MINIMUM) {
      if (suffix != null && value != null) {
        storeKeyValue(h, key.toString() + suffix, value);
      }
      else {
        for (int i=0; i<attributes.getLength(); i++) {
          String name = attributes.getQName(i);
          storeKeyValue(h, key.toString() + name, attributes.getValue(i));
        }
      }
    }

    if (qName.equals("Element")) {
      elementName = attributes.getValue("Name");
    }
    else if (qName.equals("Collection")) {
      collection = elementName;
    }
    else if (qName.equals("Image")) {
      if (!linkedInstruments && level != MetadataLevel.MINIMUM) {
        int c = 0;
        for (Detector d : detectors) {
          String id = MetadataTools.createLSID(
            "Detector", numDatasets, detectorChannel);
          store.setDetectorID(id, numDatasets, detectorChannel);
          try {
            DetectorTypeEnumHandler handler = new DetectorTypeEnumHandler();
            store.setDetectorType((DetectorType) handler.getEnumeration(d.type),
              numDatasets, detectorChannel);
          }
          catch (EnumerationException e) { }
          store.setDetectorModel(d.model, numDatasets, detectorChannel);
          store.setDetectorZoom(d.zoom, numDatasets, detectorChannel);
          store.setDetectorOffset(d.offset, numDatasets, detectorChannel);
          store.setDetectorVoltage(new ElectricPotential(d.voltage, UNITS.VOLT),
                  numDatasets, detectorChannel);

          if (c < numChannels) {
            if (d.active) {
              store.setDetectorSettingsOffset(d.offset, numDatasets, c);
              store.setDetectorSettingsID(id, numDatasets, c);
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
            String lsid = MetadataTools.createLSID("Channel", numDatasets, i);
            store.setChannelID(lsid, numDatasets, i);
            store.setLightPathEmissionFilterRef(id, numDatasets, i, 0);
          }
          filter++;
        }
      }

      core.add(new CoreMetadata());
      numDatasets++;
      laserCount = 0;
      linkedInstruments = false;
      detectorChannel = 0;
      detectors.clear();
      lasers.clear();
      nextFilter = 0;
      String name = elementName;
      if (collection != null) name = collection + "/" + name;
      store.setImageName(name, numDatasets);
      h = getSeriesHashtable(numDatasets);
      storeKeyValue(h, "Image name", name);
      storeSeriesHashtable(numDatasets, h);
      String instrumentID = MetadataTools.createLSID("Instrument", numDatasets);
      store.setInstrumentID(instrumentID, numDatasets);
      store.setImageInstrumentRef(instrumentID, numDatasets);
      numChannels = 0;
      extras = 1;
    }
    else if (qName.equals("Attachment") && level != MetadataLevel.MINIMUM) {
      if ("ContextDescription".equals(attributes.getValue("Name"))) {
        store.setImageDescription(attributes.getValue("Content"), numDatasets);
      }
    }
    else if (qName.equals("ChannelDescription")) {
      count++;
      numChannels++;
      lutNames.add(attributes.getValue("LUTName"));
      String bytesInc = attributes.getValue("BytesInc");
      int bytes = bytesInc == null ? 0 : Integer.parseInt(bytesInc);
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

      Double physicalSize = new Double(physicalLen);

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
          Length sizeX = FormatTools.getPhysicalSizeX(physicalSize);
          if (sizeX != null) {
            store.setPixelsPhysicalSizeX(sizeX, numDatasets);
          }
          break;
        case 2: // Y axis
          if (coreMeta.sizeY != 0) {
            if (coreMeta.sizeZ == 1) {
              coreMeta.sizeZ = len;
              bytesPerAxis.put(new Integer(nBytes), "Z");
            }
            else if (coreMeta.sizeT == 1) {
              coreMeta.sizeT = len;
              bytesPerAxis.put(new Integer(nBytes), "T");
            }
          }
          else {
            coreMeta.sizeY = len;
            physicalSizeY = physicalSize.doubleValue();
            Length sizeY = FormatTools.getPhysicalSizeY(physicalSize);
            if (sizeY != null) {
              store.setPixelsPhysicalSizeY(sizeY, numDatasets);
            }
          }
          break;
        case 3: // Z axis
          if (coreMeta.sizeY == 0) {
            // XZ scan - swap Y and Z
            coreMeta.sizeY = len;
            coreMeta.sizeZ = 1;
            physicalSizeY = physicalSize.doubleValue();
            Length sizeY = FormatTools.getPhysicalSizeY(physicalSize);
            if (sizeY != null) {
              store.setPixelsPhysicalSizeY(sizeY, numDatasets);
            }
            bytesPerAxis.put(new Integer(nBytes), "Y");
          }
          else {
            coreMeta.sizeZ = len;
            bytesPerAxis.put(new Integer(nBytes), "Z");
          }
          break;
        case 4: // T axis
          if (coreMeta.sizeY == 0) {
            // XT scan - swap Y and T
            coreMeta.sizeY = len;
            coreMeta.sizeT = 1;
            physicalSizeY = physicalSize.doubleValue();
            Length sizeY = FormatTools.getPhysicalSizeY(physicalSize);
            if (sizeY != null) {
              store.setPixelsPhysicalSizeY(sizeY, numDatasets);
            }
            bytesPerAxis.put(new Integer(nBytes), "Y");
          }
          else {
            coreMeta.sizeT = len;
            bytesPerAxis.put(new Integer(nBytes), "T");
          }
          break;
        default:
          extras *= len;
      }
      count++;
    }
    else if (qName.equals("ScannerSettingRecord") &&
      level != MetadataLevel.MINIMUM)
    {
      String id = attributes.getValue("Identifier");
      if (id == null) id = "";

      if (id.equals("SystemType")) {
        store.setMicroscopeModel(value, numDatasets);
        store.setMicroscopeType(MicroscopeType.OTHER, numDatasets);
      }
      else if (id.equals("dblPinhole")) {
        pinhole = new Double(Double.parseDouble(value) * 1000000);
      }
      else if (id.equals("dblZoom")) {
        zoom = new Double(value);
      }
      else if (id.equals("dblStepSize")) {
        double zStep = Double.parseDouble(value) * 1000000;
        Length sizeZ = FormatTools.getPhysicalSizeZ(zStep);
        if (sizeZ != null) {
          store.setPixelsPhysicalSizeZ(sizeZ, numDatasets);
        }
      }
      else if (id.equals("nDelayTime_s")) {
        Double timeIncrement = new Double(value);
        if (timeIncrement != null) {
          store.setPixelsTimeIncrement(new Time(timeIncrement, UNITS.SECOND), numDatasets);
        }
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
          try {
            Double exposureTime = new Double(value);
            if (exposureTime != null) {
              store.setPlaneExposureTime(new Time(exposureTime, UNITS.SECOND), numDatasets, c);
            }
          }
          catch (IndexOutOfBoundsException e) { }
        }
        else if (id.endsWith("Gain")) {
          channel.gain = new Double(value);

          String detectorID =
            MetadataTools.createLSID("Detector", numDatasets, 0);
          channel.detector = detectorID;
          store.setDetectorID(detectorID, numDatasets, 0);
          store.setDetectorType(DetectorType.CCD, numDatasets, 0);
        }
        else if (id.endsWith("WaveLength")) {
          Double exWave = new Double(value);
          Length ex = FormatTools.getExcitationWavelength(exWave);
          if (ex != null) {
            channel.exWave = ex;
          }
        }
        // NB: "UesrDefName" is not a typo.
        else if (id.endsWith("UesrDefName") && !value.equals("None")) {
          channel.name = value;
        }
        channels.put(numDatasets + "-" + c, channel);
      }
    }
    else if (qName.equals("FilterSettingRecord") &&
      level != MetadataLevel.MINIMUM)
    {
      String object = attributes.getValue("ObjectName");
      String attribute = attributes.getValue("Attribute");
      String objectClass = attributes.getValue("ClassName");
      String variant = attributes.getValue("Variant");
      CoreMetadata coreMeta = core.get(numDatasets);

      if (attribute.equals("NumericalAperture")) {
        store.setObjectiveLensNA(new Double(variant), numDatasets, 0);
      }
      else if (attribute.equals("OrderNumber")) {
        store.setObjectiveSerialNumber(variant, numDatasets, 0);
      }
      else if (objectClass.equals("CDetectionUnit")) {
        if (attribute.equals("State")) {
          Detector d = new Detector();
          String data = attributes.getValue("data");
          if (data == null) data = attributes.getValue("Data");
          d.channel = data == null ? 0 : Integer.parseInt(data);
          d.type = "PMT";
          d.model = object;
          d.active = variant.equals("Active");
          d.zoom = zoom;
          detectors.add(d);
        }
        else if (attribute.equals("HighVoltage")) {
          Detector d = detectors.get(detectors.size() - 1);
          d.voltage = new Double(variant);
        }
        else if (attribute.equals("VideoOffset")) {
          Detector d = detectors.get(detectors.size() - 1);
          d.offset = new Double(variant);
        }
      }
      else if (attribute.equals("Objective")) {
        StringTokenizer tokens = new StringTokenizer(variant, " ");
        boolean foundMag = false;
        final StringBuilder model = new StringBuilder();
        while (!foundMag) {
          String token = tokens.nextToken();
          int x = token.indexOf('x');
          if (x != -1) {
            foundMag = true;

            Double mag = Double.parseDouble(token.substring(0, x));
            String na = token.substring(x + 1);

            store.setObjectiveNominalMagnification(mag, numDatasets, 0);
            store.setObjectiveLensNA(new Double(na), numDatasets, 0);
          }
          else {
            model.append(token);
            model.append(" ");
          }
        }

        String immersion = "Other";
        if (tokens.hasMoreTokens()) {
          immersion = tokens.nextToken();
          if (immersion == null || immersion.trim().equals("")) {
            immersion = "Other";
          }
        }
        try {
          ImmersionEnumHandler handler = new ImmersionEnumHandler();
          store.setObjectiveImmersion(
            (Immersion) handler.getEnumeration(immersion), numDatasets, 0);
        }
        catch (EnumerationException e) { }

        String correction = "Other";
        if (tokens.hasMoreTokens()) {
          correction = tokens.nextToken();
          if (correction == null || correction.trim().equals("")) {
            correction = "Other";
          }
        }
        try {
          CorrectionEnumHandler handler = new CorrectionEnumHandler();
          store.setObjectiveCorrection(
            (Correction) handler.getEnumeration(correction), numDatasets, 0);
        }
        catch (EnumerationException e) { }

        store.setObjectiveModel(model.toString().trim(), numDatasets, 0);
      }
      else if (attribute.equals("RefractionIndex")) {
        String id = MetadataTools.createLSID("Objective", numDatasets, 0);
        store.setObjectiveID(id, numDatasets, 0);
        store.setObjectiveSettingsID(id, numDatasets);
        store.setObjectiveSettingsRefractiveIndex(new Double(variant),
          numDatasets);
      }
      else if (attribute.equals("XPos")) {
        int c = coreMeta.rgb || coreMeta.sizeC == 0 ? 1 : coreMeta.sizeC;
        int nPlanes = coreMeta.imageCount;
        final Double posXn = Double.valueOf(variant);
        final Length posXl = new Length(posXn, UNITS.REFERENCEFRAME);
        for (int image=0; image<nPlanes; image++) {
          store.setPlanePositionX(posXl, numDatasets, image);
        }
        if (numChannels == 0) xPos.add(posXl);
      }
      else if (attribute.equals("YPos")) {
        int c = coreMeta.rgb || coreMeta.sizeC == 0 ? 1 : coreMeta.sizeC;
        int nPlanes = coreMeta.imageCount;
        final Double posYn = Double.valueOf(variant);
        final Length posYl = new Length(posYn, UNITS.REFERENCEFRAME);
        for (int image=0; image<nPlanes; image++) {
          store.setPlanePositionY(posYl, numDatasets, image);
        }
        if (numChannels == 0) yPos.add(posYl);
      }
      else if (attribute.equals("ZPos")) {
        int c = coreMeta.rgb || coreMeta.sizeC == 0 ? 1 : coreMeta.sizeC;
        int nPlanes = coreMeta.imageCount;
        final Double posZn = Double.valueOf(variant);
        final Length posZl = new Length(posZn, UNITS.REFERENCEFRAME);
        for (int image=0; image<nPlanes; image++) {
          store.setPlanePositionZ(posZl, numDatasets, image);
        }
        if (numChannels == 0) zPos.add(posZl);
      }
      else if (objectClass.equals("CSpectrophotometerUnit")) {
        Double v = null;
        try {
          v = Double.parseDouble(variant);
        }
        catch (NumberFormatException e) { }
        if (attributes.getValue("Description").endsWith("(left)")) {
          String id =
            MetadataTools.createLSID("Filter", numDatasets, nextFilter);
          store.setFilterID(id, numDatasets, nextFilter);
          store.setFilterModel(object, numDatasets, nextFilter);

          Length in = FormatTools.getCutIn(v);
          if (in != null) {
            store.setTransmittanceRangeCutIn(in, numDatasets, nextFilter);
          }
        }
        else if (attributes.getValue("Description").endsWith("(right)")) {
          Length out = FormatTools.getCutOut(v);
          if (out != null) {
            store.setTransmittanceRangeCutOut(out, numDatasets, nextFilter);
            nextFilter++;
          }
        }
      }
    }
    else if (qName.equals("Detector") && level != MetadataLevel.MINIMUM) {
      String v = attributes.getValue("Gain");
      Double gain = v == null ? null : new Double(v);
      v = attributes.getValue("Offset");
      Double offset = v == null ? null : new Double(v);
      boolean active = "1".equals(attributes.getValue("IsActive"));

      if (active) {
        // find the corresponding MultiBand and Detector
        MultiBand m = null;
        Detector detector = null;
        Laser laser = lasers.isEmpty() ? null : lasers.get(lasers.size() - 1);
        String c = attributes.getValue("Channel");
        int channel = c == null ? 0 : Integer.parseInt(c);

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

        if (m != null) {
          String channelID = MetadataTools.createLSID(
            "Channel", numDatasets, nextChannel);
          store.setChannelID(channelID, numDatasets, nextChannel);
          store.setChannelName(m.dyeName, numDatasets, nextChannel);

          String filter =
            MetadataTools.createLSID("Filter", numDatasets, nextFilter);
          store.setFilterID(filter, numDatasets, nextFilter);

          Length in = FormatTools.getCutIn(m.cutIn);
          Length out = FormatTools.getCutOut(m.cutOut);
          if (in != null) {
            store.setTransmittanceRangeCutIn(in, numDatasets, nextFilter);
          }
          if (out != null) {
            store.setTransmittanceRangeCutOut(out, numDatasets, nextFilter);
          }
          store.setLightPathEmissionFilterRef(
            filter, numDatasets, nextChannel, 0);
          nextFilter++;

          store.setDetectorID(id, numDatasets, nextChannel);
          store.setDetectorType(DetectorType.PMT, numDatasets, nextChannel);
          store.setDetectorSettingsGain(gain, numDatasets, nextChannel);
          store.setDetectorSettingsOffset(offset, numDatasets, nextChannel);
          store.setDetectorSettingsID(id, numDatasets, nextChannel);
        }

        store.setDetectorID(id, numDatasets, nextChannel);
        if (detector != null) {
          store.setDetectorSettingsGain(gain, numDatasets, nextChannel);
          store.setDetectorSettingsOffset(offset, numDatasets, nextChannel);
          store.setDetectorSettingsID(id, numDatasets, nextChannel);
          try {
            DetectorTypeEnumHandler handler = new DetectorTypeEnumHandler();
            store.setDetectorType(
              (DetectorType) handler.getEnumeration(detector.type),
              numDatasets, nextChannel);
          }
          catch (EnumerationException e) { }
          store.setDetectorModel(detector.model, numDatasets, nextChannel);
          store.setDetectorZoom(detector.zoom, numDatasets, nextChannel);
          store.setDetectorOffset(detector.offset, numDatasets, nextChannel);
          store.setDetectorVoltage(
                  new ElectricPotential(detector.voltage, UNITS.VOLT),
                  numDatasets, nextChannel);
        }

        if (laser != null && laser.intensity < 100) {
          store.setChannelLightSourceSettingsID(laser.id, numDatasets,
            nextChannel);
          store.setChannelLightSourceSettingsAttenuation(
            new PercentFraction((float) laser.intensity / 100f),
            numDatasets, nextChannel);

          Length wavelength =
            FormatTools.getExcitationWavelength(laser.wavelength);
          if (wavelength != null) {
            store.setChannelExcitationWavelength(wavelength,
              numDatasets, nextChannel);
          }
        }

        nextChannel++;
      }
    }
    else if (qName.equals("LaserLineSetting") && level != MetadataLevel.MINIMUM)
    {
      Laser l = new Laser();
      String lineIndex = attributes.getValue("LineIndex");
      String qual = attributes.getValue("Qualifier");
      l.index = lineIndex == null ? 0 : Integer.parseInt(lineIndex);
      int qualifier = qual == null ? 0 : Integer.parseInt(qual);
      l.index += (2 - (qualifier / 10));
      if (l.index < 0) l.index = 0;
      l.id = MetadataTools.createLSID("LightSource", numDatasets, l.index);
      l.wavelength = new Double(attributes.getValue("LaserLine"));
      while (l.index > laserCount) {
        String lsid =
          MetadataTools.createLSID("LightSource", numDatasets, laserCount);
        store.setLaserID(lsid, numDatasets, laserCount);
        laserCount++;
      }
      store.setLaserID(l.id, numDatasets, l.index);
      laserCount++;

      Length wavelength = FormatTools.getWavelength(l.wavelength);
      if (wavelength != null) {
        store.setLaserWavelength(wavelength, numDatasets, l.index);
      }
      store.setLaserType(LaserType.OTHER, numDatasets, l.index);
      store.setLaserLaserMedium(LaserMedium.OTHER, numDatasets, l.index);

      String intensity = attributes.getValue("IntensityDev");
      l.intensity = intensity == null ? 0d : Double.parseDouble(intensity);

      if (l.intensity > 0) {
        l.intensity = 100d - l.intensity;
        lasers.add(l);
      }
    }
    else if (qName.equals("TimeStamp") && numDatasets >= 0) {
      String stampHigh = attributes.getValue("HighInteger");
      String stampLow = attributes.getValue("LowInteger");
      long high = stampHigh == null ? 0 : Long.parseLong(stampHigh);
      long low = stampLow == null ? 0 : Long.parseLong(stampLow);

      long ms = DateTools.getMillisFromTicks(high, low);
      if (count == 0) {
        String date = DateTools.convertDate(ms, DateTools.COBOL);
        Timestamp timestamp = Timestamp.valueOf(date);
        if (timestamp != null && timestamp.asInstant().getMillis() <
          System.currentTimeMillis())
        {
          store.setImageAcquisitionDate(new Timestamp(date), numDatasets);
        }
        firstStamp = ms;
        store.setPlaneDeltaT(new Time(0.0, UNITS.SECOND), numDatasets, count);
      }
      else if (level != MetadataLevel.MINIMUM) {
        CoreMetadata coreMeta = core.get(numDatasets);
        int nImages = coreMeta.sizeZ * coreMeta.sizeT * coreMeta.sizeC;
        if (count < nImages) {
          ms -= firstStamp;
          store.setPlaneDeltaT(new Time(ms / 1000.0, UNITS.SECOND), numDatasets, count);
        }
      }

      count++;
    }
    else if (qName.equals("RelTimeStamp") && level != MetadataLevel.MINIMUM) {
      CoreMetadata coreMeta = core.get(numDatasets);
      int nImages = coreMeta.sizeZ * coreMeta.sizeT * coreMeta.sizeC;
      if (count < nImages) {
        Double time = new Double(attributes.getValue("Time"));
        if (time != null) {
          store.setPlaneDeltaT(new Time(time, UNITS.SECOND), numDatasets, count++);
        }
      }
    }
    else if (qName.equals("Annotation") && level != MetadataLevel.MINIMUM) {
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
    else if (qName.equals("Vertex") && level != MetadataLevel.MINIMUM) {
      String x = attributes.getValue("x");
      String y = attributes.getValue("y");
      if (x != null) {
        x = x.replaceAll(",", ".");
        roi.x.add(new Double(x));
      }
      if (y != null) {
        y = y.replaceAll(",", ".");
        roi.y.add(new Double(y));
      }
    }
    else if (qName.equals("ROI")) {
      alternateCenter = true;
    }
    else if (qName.equals("MultiBand") && level != MetadataLevel.MINIMUM) {
      MultiBand m = new MultiBand();
      m.dyeName = attributes.getValue("DyeName");
      m.channel = Integer.parseInt(attributes.getValue("Channel"));
      m.cutIn = (double)
        Math.round(Double.parseDouble(attributes.getValue("LeftWorld")));
      m.cutOut = (double)
        Math.round(Double.parseDouble(attributes.getValue("RightWorld")));

      multiBands.add(m);
    }
    else if (qName.equals("ChannelInfo")) {
      int index = Integer.parseInt(attributes.getValue("Index"));
      channels.remove(numDatasets + "-" + index);
    }
    else count = 0;
    if (numDatasets == oldSeriesCount) storeSeriesHashtable(numDatasets, h);
  }

  // -- Helper methods --

  private Hashtable getSeriesHashtable(int series) {
    if (series < 0 || series >= core.size()) return new Hashtable();
    return core.get(series).seriesMetadata;
  }

  private void storeSeriesHashtable(int series, Hashtable h) {
    if (series < 0) return;

    Object[] keys = h.keySet().toArray(new Object[h.size()]);
    for (Object key : keys) {
      Object value = h.get(key);
      if (value instanceof Vector) {
        Vector v = (Vector) value;
        for (int o=0; o<v.size(); o++) {
          if (v.get(o) != null) {
            h.put(key + " " + (o + 1), v.get(o));
          }
        }
        h.remove(key);
      }
    }

    CoreMetadata coreMeta = core.get(series);
    coreMeta.seriesMetadata = h;
    core.set(series, coreMeta);
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
      if (level == MetadataLevel.NO_OVERLAYS || level == MetadataLevel.MINIMUM)
      {
        return;
      }

      // keep in mind that vertices are given relative to the center
      // point of the ROI and the transX/transY values are relative to
      // the center point of the image

      String roiID = MetadataTools.createLSID("ROI", roi);
      store.setImageROIRef(roiID, series, roi);

      store.setROIID(roiID, roi);
      store.setLabelID(MetadataTools.createLSID("Shape", roi, 0), roi, 0);
      if (text == null) text = "";
      store.setLabelText(text, roi, 0);
      if (fontSize != null) {
        double size = Double.parseDouble(fontSize);
        Length fontSize = FormatTools.getFontSize((int) size);
        if (fontSize != null) {
          store.setLabelFontSize(fontSize, roi, 0);
        }
      }
      Length l = new Length(new Double(linewidth), UNITS.PIXEL);
      store.setLabelStrokeWidth(l, roi, 0);

      if (!normalized) normalize();

      double cornerX = x.get(0).doubleValue();
      double cornerY = y.get(0).doubleValue();

      store.setLabelX(cornerX, roi, 0);
      store.setLabelY(cornerY, roi, 0);

      int centerX = (core.get(series).sizeX / 2) - 1;
      int centerY = (core.get(series).sizeY / 2) - 1;

      double roiX = centerX + transX;
      double roiY = centerY + transY;

      if (alternateCenter) {
        roiX = transX - 2 * cornerX;
        roiY = transY - 2 * cornerY;
      }

      // TODO : rotation/scaling not populated

      String shapeID = MetadataTools.createLSID("Shape", roi, 1);
      switch (type) {
        case POLYGON:
          final StringBuilder points = new StringBuilder();
          for (int i=0; i<x.size(); i++) {
            points.append(x.get(i).doubleValue() + roiX);
            points.append(",");
            points.append(y.get(i).doubleValue() + roiY);
            if (i < x.size() - 1) points.append(" ");
          }
          store.setPolygonID(shapeID, roi, 1);
          store.setPolygonPoints(points.toString(), roi, 1);

          break;
        case TEXT:
        case RECTANGLE:
          store.setRectangleID(shapeID, roi, 1);
          store.setRectangleX(roiX - Math.abs(cornerX), roi, 1);
          store.setRectangleY(roiY - Math.abs(cornerY), roi, 1);
          double width = 2 * Math.abs(cornerX);
          double height = 2 * Math.abs(cornerY);
          store.setRectangleWidth(width, roi, 1);
          store.setRectangleHeight(height, roi, 1);

          break;
        case SCALE_BAR:
        case ARROW:
        case LINE:
          store.setLineID(shapeID, roi, 1);
          store.setLineX1(roiX + x.get(0), roi, 1);
          store.setLineY1(roiY + y.get(0), roi, 1);
          store.setLineX2(roiX + x.get(1), roi, 1);
          store.setLineY2(roiY + y.get(1), roi, 1);
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
        x.setElementAt(coordinate, i);
      }

      for (int i=0; i<y.size(); i++) {
        double coordinate = y.get(i).doubleValue() * 1000000;
        coordinate *= (1 / physicalSizeY);
        y.setElementAt(coordinate, i);
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

  private void storeKeyValue(Hashtable h, String key, String value) {
    if (h.get(key) == null && key != null && value != null) {
      h.put(key, value);
    }
    else {
      Object oldValue = h.get(key);
      if (oldValue instanceof Vector) {
        Vector values = (Vector) oldValue;
        values.add(value);
        h.put(key, values);
      }
      else {
        Vector values = new Vector();
        values.add(oldValue);
        values.add(value);
        h.put(key, values);
      }
    }
  }

  // -- Helper classes --

  class MultiBand {
    public int channel;
    public double cutIn;
    public double cutOut;
    public String dyeName;
  }

  class Detector {
    public int channel;
    public Double zoom;
    public String type;
    public String model;
    public boolean active;
    public Double voltage;
    public Double offset;
  }

  class Laser {
    public Double wavelength;
    public double intensity;
    public String id;
    public int index;
  }

  class Channel {
    public String detector;
    public Double gain;
    public Length exWave;
    public String name;
  }

}
