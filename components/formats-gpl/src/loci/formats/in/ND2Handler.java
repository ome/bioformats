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

import java.util.ArrayList;
import java.util.Hashtable;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.xml.BaseHandler;
import loci.formats.CoreMetadata;
import loci.formats.CoreMetadataList;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.units.UNITS;
import ome.units.quantity.Length;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

/**
 * DefaultHandler implementation for handling XML produced by Nikon Elements.
 */
public class ND2Handler extends BaseHandler {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(ND2Handler.class);
  private static final String DATE_FORMAT = "dd/MM/yyyy  HH:mm:ss";

  private static final int FIELD_INDEX = 2;

  // -- Fields --

  private String prefix = null;
  private String prevRuntype = null;
  private String prevElement = null;

  private Hashtable<String, Object> metadata = new Hashtable<String, Object>();
  private CoreMetadataList core;

  private boolean isLossless;
  private ArrayList<Long> zs = new ArrayList<Long>();
  private ArrayList<Long> ts = new ArrayList<Long>();

  private int numSeries = 0;
  private double pixelSizeX, pixelSizeY, pixelSizeZ;

  private Double pinholeSize, voltage, mag, na;
  private String objectiveModel, immersion, correction;
  private Double refractiveIndex;
  private ArrayList<String> channelNames = new ArrayList<String>();
  private ArrayList<String> modality = new ArrayList<String>();
  private ArrayList<String> binning = new ArrayList<String>();
  private ArrayList<Double> speed = new ArrayList<Double>();
  private ArrayList<Double> gain = new ArrayList<Double>();
  private ArrayList<Double> temperature = new ArrayList<Double>();
  private ArrayList<Double> exposureTime = new ArrayList<Double>();
  private ArrayList<Double> exWave = new ArrayList<Double>();
  private ArrayList<Double> emWave = new ArrayList<Double>();
  private ArrayList<Integer> power = new ArrayList<Integer>();
  private ArrayList<Hashtable<String, String>> rois =
    new ArrayList<Hashtable<String, String>>();
  private ArrayList<Length> posX = new ArrayList<Length>();
  private ArrayList<Length> posY = new ArrayList<Length>();
  private ArrayList<Length> posZ = new ArrayList<Length>();
  private ArrayList<String> posNames = new ArrayList<String>();

  private String cameraModel;
  private String date;

  private Hashtable<String, Integer> colors = new Hashtable<String, Integer>();
  private Hashtable<String, String> dyes = new Hashtable<String, String>();
  private Hashtable<String, Integer> realColors =
    new Hashtable<String, Integer>();

  private int nXFields = 0, nYFields = 0;

  private boolean populateXY = true;
  private int nImages = 0;

  private ArrayList<Boolean> validLoopState = new ArrayList<Boolean>();

  private boolean canAdjustDimensions = true;
  private boolean firstTimeLoop = true;

  // -- Constructor --

  public ND2Handler(CoreMetadataList core, int nImages) {
    this(core, true, nImages);
  }

  public ND2Handler(CoreMetadataList core, boolean populateXY, int nImages) {
    super();
    this.populateXY = populateXY;
    this.nImages = nImages;
    this.core = new CoreMetadataList(core);
  }

  // -- ND2Handler API methods --

  public int getXFields() {
    return nXFields;
  }

  public CoreMetadataList getCoreMetadataList() {
    return core;
  }

  public void populateROIs(MetadataStore store) {
    for (int r=0; r<rois.size(); r++) {
      Hashtable<String, String> roi = rois.get(r);
      String type = roi.get("ROIType");

      if (type.equals("Text")) {
        String roiID = MetadataTools.createLSID("ROI", r);
        for (int i=0; i<core.size(); i++) {
          store.setImageROIRef(roiID, i, r);
        }
        store.setROIID(roiID, r);
        store.setLabelID(MetadataTools.createLSID("Shape", r, 0), r, 0);

        int fontSize = Integer.parseInt(roi.get("fHeight"));
        if (fontSize >= 0) {
          store.setLabelFontSize(new Length(fontSize, UNITS.POINT), r, 0);
        }
        store.setLabelText(roi.get("eval-text"), r, 0);
        Length l = new Length(new Double(roi.get("line-width")), UNITS.PIXEL);
        store.setLabelStrokeWidth(l, r, 0);

        String rectangle = roi.get("rectangle");
        String[] p = rectangle.split(",");
        double[] points = new double[p.length];
        for (int i=0; i<p.length; i++) {
          points[i] = Double.parseDouble(p[i]);
        }

        store.setLabelX(points[0], r, 0);
        store.setLabelY(points[1], r, 0);

        store.setRectangleID(MetadataTools.createLSID("Shape", r, 1), r, 1);
        store.setRectangleX(points[0], r, 1);
        store.setRectangleY(points[1], r, 1);
        store.setRectangleWidth(points[2] - points[0], r, 1);
        store.setRectangleHeight(points[3] - points[1], r, 1);
      }
      else if (type.equals("HorizontalLine") || type.equals("VerticalLine")) {
        String roiID = MetadataTools.createLSID("ROI", r);
        for (int i=0; i<core.size(); i++) {
          store.setImageROIRef(roiID, i, r);
        }
        store.setROIID(roiID, r);

        String segments = roi.get("segments");
        segments = segments.replaceAll("\\[\\]", "");
        String[] points = segments.split("\\)");

        final StringBuilder sb = new StringBuilder();
        for (int i=0; i<points.length; i++) {
          points[i] = points[i].substring(points[i].indexOf(':') + 1);
          sb.append(points[i]);
          if (i < points.length - 1) sb.append(" ");
        }

        store.setPolylineID(MetadataTools.createLSID("Shape", r, 0), r, 0);
        store.setPolylinePoints(sb.toString(), r, 0);
      }
    }
  }

  public String getDate() {
    return date;
  }

  public Hashtable<String, Object> getMetadata() {
    return metadata;
  }

  public int getSeriesCount() {
    return numSeries;
  }

  public boolean isLossless() {
    return isLossless;
  }

  public ArrayList<Long> getZSections() {
    return zs;
  }

  public ArrayList<Long> getTimepoints() {
    return ts;
  }

  public double getPixelSizeX() {
    return pixelSizeX;
  }

  public double getPixelSizeY() {
    return pixelSizeY;
  }

  public double getPixelSizeZ() {
    return pixelSizeZ;
  }

  public Double getPinholeSize() {
    return pinholeSize;
  }

  public Double getVoltage() {
    return voltage;
  }

  public Double getMagnification() {
    return mag;
  }

  public Double getNumericalAperture() {
    return na;
  }

  public String getObjectiveModel() {
    return objectiveModel;
  }

  public String getImmersion() {
    return immersion;
  }

  public String getCorrection() {
    return correction;
  }

  public Double getRefractiveIndex() {
    return refractiveIndex;
  }

  public ArrayList<String> getChannelNames() {
    return channelNames;
  }

  public ArrayList<String> getModalities() {
    return modality;
  }

  public ArrayList<String> getBinnings() {
    return binning;
  }

  public ArrayList<Double> getSpeeds() {
    return speed;
  }

  public ArrayList<Double> getGains() {
    return gain;
  }

  public ArrayList<Double> getTemperatures() {
    return temperature;
  }

  public ArrayList<Double> getExposureTimes() {
    return exposureTime;
  }

  public ArrayList<Double> getExcitationWavelengths() {
    return exWave;
  }

  public ArrayList<Double> getEmissionWavelengths() {
    return emWave;
  }

  public ArrayList<Integer> getPowers() {
    return power;
  }

  public ArrayList<Hashtable<String, String>> getROIs() {
    return rois;
  }

  public ArrayList<Length> getXPositions() {
    return posX;
  }

  public ArrayList<Length> getYPositions() {
    return posY;
  }

  public ArrayList<Length> getZPositions() {
    return posZ;
  }

  public ArrayList<String> getPositionNames() {
    return posNames;
  }

  public String getCameraModel() {
    return cameraModel;
  }

  public int getFieldIndex() {
    return FIELD_INDEX;
  }

  public Hashtable<String, Integer> getChannelColors() {
    return realColors;
  }

  // -- DefaultHandler API methods --

  public void endElement(String uri, String localName, String qName,
    Attributes attributes)
  {
    if (qName.equals("CalibrationSeq") || qName.equals("MetadataSeq")) {
      prefix = null;
    }
    if (qName.equals(prevElement)) {
      prevElement = null;
    }
  }

  @Override
  public void startElement(String uri, String localName, String qName,
    Attributes attributes)
  {
    String runtype = attributes.getValue("runtype");
    if ("CLxListVariant".equals(runtype) || "RLxIRect".equals(runtype)) {
      prevElement = qName;
    }

    String value = attributes.getValue("value");
    CoreMetadata ms0 = core.get(0, 0);

    try {
      if (qName.equals("uiWidth")) {
        int x = Integer.parseInt(value);
        if (x != 0 && populateXY) {
          ms0.sizeX = x;
        }
      }
      else if (qName.equals("uiCamPxlCountX")) {
        if (ms0.sizeX == 0 && populateXY) {
          ms0.sizeX = Integer.parseInt(value);
        }
      }
      else if (qName.equals("uiCamPxlCountY")) {
        if (ms0.sizeY == 0 && populateXY) {
          ms0.sizeY = Integer.parseInt(value);
        }
      }
      else if (qName.equals("iXFields")) {
        int fields = Integer.parseInt(value);
        nXFields += fields;
      }
      else if (qName.equals("iYFields")) {
        int fields = Integer.parseInt(value);
        nYFields += fields;
      }
      else if ("rectSensorUser".equals(prevElement) && populateXY) {
        if (qName.equals("left") && ms0.sizeX == 0) {
          ms0.sizeX = -1 * Integer.parseInt(value);
        }
        else if (qName.equals("top") && ms0.sizeY == 0) {
          ms0.sizeY = -1 * Integer.parseInt(value);
        }
        else if (qName.equals("right") && ms0.sizeX <= 0) {
          ms0.sizeX += Integer.parseInt(value);
        }
        else if (qName.equals("bottom") && ms0.sizeY <= 0) {
          ms0.sizeY += Integer.parseInt(value);
        }
      }
      else if ("LoopState".equals(prevElement) && value != null) {
        long v = Long.parseLong(value);
        validLoopState.add(v != 529 && v < 0xfffff);
      }
      else if ("LoopSize".equals(prevElement) && value != null) {
        int v = Integer.parseInt(value);
        int loopIndex = 0;
        while (validLoopState.get(loopIndex) == null) {
          loopIndex++;
        }
        boolean loop = validLoopState.get(loopIndex);

        if (!loop && validLoopState.contains(true)) {
          v = 1;
        }

        if (ms0.sizeT == 0 || (ms0.sizeT * ms0.sizeZ > nImages && v < ms0.sizeT)) {
          ms0.sizeT = v;
        }
        else if (qName.equals("no_name") && v > 1 && core.size() == 1) {
          core = new CoreMetadataList();
          for (int q=0; q<v; q++) {
            core.add(ms0);
          }
        }
        else if (ms0.sizeZ == 0) {
          ms0.sizeZ = v;
        }
        else {
          canAdjustDimensions = false;
        }
        ms0.dimensionOrder = "CZT";
      }
      else if ("pPosName".equals(prevElement) && value != null) {
        posNames.add(value);
      }
      else if (qName.equals("FramesBefore")) {
        if (ms0.sizeZ == 0) {
          ms0.sizeZ = 1;
        }
        // only adjust if we haven't parsed all of the dimensions already
        if (canAdjustDimensions && core.size() == 1 &&
          (ms0.sizeT <= 1 || ms0.sizeT * ms0.sizeZ != nImages))
        {
          ms0.sizeZ *= Integer.parseInt(value);
        }
      }
      else if (qName.equals("FramesAfter")) {
        int loopIndex = 0;
        while (validLoopState.get(loopIndex) == null) {
          loopIndex++;
        }
        boolean loop = validLoopState.get(loopIndex);
        validLoopState.set(loopIndex, null);

        if (canAdjustDimensions && core.size() == 1) {
          ms0.sizeZ *= Integer.parseInt(value);

          if (ms0.sizeT * ms0.sizeZ > nImages &&
            ms0.sizeT <= nImages &&
            ms0.sizeT != ms0.sizeZ)
          {
            ms0.sizeZ = ms0.sizeT;
            ms0.sizeT = 1;
          }
        }
      }
      else if (qName.equals("TimeBefore") && canAdjustDimensions) {
        if (ms0.sizeT == 0) {
          ms0.sizeT = 1;
        }
        ms0.sizeT *= Integer.parseInt(value);
      }
      else if (qName.equals("TimeAfter") && canAdjustDimensions) {
        ms0.sizeT *= Integer.parseInt(value);
      }
      else if (qName.equals("uiMaxDst")) {
        int maxPixelValue = Integer.parseInt(value) + 1;
        int bits = 0;
        while (maxPixelValue > 0) {
          maxPixelValue /= 2;
          bits++;
        }
        try {
          if (ms0.pixelType == 0) {
            ms0.pixelType =
              FormatTools.pixelTypeFromBytes(bits / 8, false, false);
          }
        }
        catch (FormatException e) {
          LOGGER.warn("Could not set the pixel type", e);
        }
      }
      else if (qName.equals("uiWidthBytes") || qName.startsWith("uiBpcInMemory") || qName.startsWith("uiBpcSignificant")) {
        int div = qName.equals("uiWidthBytes") ? ms0.sizeX : 8;
        if (div > 0) {
          int bits = Integer.parseInt(value);
          int bytes = bits / div;

          if (bytes * div == bits) {
            try {
              ms0.pixelType =
                FormatTools.pixelTypeFromBytes(bytes, false, false);
            }
            catch (FormatException e) { }
          }
          parseKeyAndValue(qName, value, prevRuntype);
        }
      }
      else if ("dPosX".equals(prevElement) && qName.startsWith("item_")) {
        final Double number = DataTools.parseDouble(value);
        posX.add(new Length(number, UNITS.REFERENCEFRAME));
        metadata.put("X position for position #" + posX.size(), value);
      }
      else if ("dPosY".equals(prevElement) && qName.startsWith("item_")) {
        final Double number = DataTools.parseDouble(value);
        posY.add(new Length(number, UNITS.REFERENCEFRAME));
        metadata.put("Y position for position #" + posY.size(), value);
      }
      else if ("dPosZ".equals(prevElement) && qName.startsWith("item_")) {
        final Double number = DataTools.parseDouble(value);
        posZ.add(new Length(number, UNITS.REFERENCEFRAME));
        metadata.put("Z position for position #" + posZ.size(), value);
      }
      else if (qName.startsWith("item_")) {
        int v = Integer.parseInt(qName.substring(qName.indexOf('_') + 1));
        if (v == numSeries) {
          numSeries++;
        }
      }
      else if (qName.equals("uiCompCount")) {
        int v = Integer.parseInt(value);
        ms0.sizeC = (int) Math.max(ms0.sizeC, v);
      }
      else if (qName.equals("uiHeight") && populateXY) {
        int y = Integer.parseInt(value);
        if (y != 0) {
          ms0.sizeY = y;
        }
      }
      else if (qName.startsWith("TextInfo")) {
        parseKeyAndValue(qName, attributes.getValue("Text"), prevRuntype);
        parseKeyAndValue(qName, value, prevRuntype);
      }
      else if (qName.equals("dCompressionParam")) {
        isLossless = Double.parseDouble(value) > 0;
        parseKeyAndValue(qName, value, prevRuntype);
      }
      else if (qName.equals("CalibrationSeq") || qName.equals("MetadataSeq")) {
        prefix = qName + " " + attributes.getValue("_SEQUENCE_INDEX");
      }
      else if (qName.equals("HorizontalLine") || qName.equals("VerticalLine") ||
        qName.equals("Text"))
      {
        Hashtable<String, String> roi = new Hashtable<String, String>();
        roi.put("ROIType", qName);
        for (int q=0; q<attributes.getLength(); q++) {
          roi.put(attributes.getQName(q), attributes.getValue(q));
        }
        rois.add(roi);
      }
      else if (qName.equals("dPinholeRadius")) {
        pinholeSize = DataTools.parseDouble(value);
        metadata.put("Pinhole size", value);
      }
      else if (qName.endsWith("ChannelColor")) {
        String name = qName.substring(0, qName.indexOf("Channel"));
        colors.put(name, new Integer(value));
      }
      else if (qName.endsWith("DyeName")) {
        int channelIndex = qName.indexOf("Channel");
        if (channelIndex < 0) channelIndex = 0;
        dyes.put(qName.substring(0, channelIndex), value);
      }
      else if (qName.equals("uiSequenceCount")) {
        int imageCount = Integer.parseInt(value);
        if (core.size() > 0) {
          int newCount = imageCount / core.size();
          if (newCount * core.size() == imageCount) {
            imageCount = newCount;
          }
        }
        if (ms0.sizeZ * ms0.sizeT != imageCount &&
          ms0.sizeZ * ms0.sizeC * ms0.sizeT != imageCount)
        {
          if (ms0.sizeZ > 1 && ms0.sizeT <= 1) {
            ms0.sizeZ = imageCount;
            ms0.sizeT = 1;
            ms0.imageCount = imageCount;
          }
          else if (ms0.sizeT > 1 && ms0.sizeZ <= 1) {
            ms0.sizeT = imageCount;
            ms0.sizeZ = 1;
            ms0.imageCount = imageCount;
          }
          else if (imageCount == 0) {
            ms0.sizeT = 0;
            ms0.sizeZ = 0;
            ms0.imageCount = 0;
          }
        }
        metadata.put(qName, value);
      }
      else {
        final StringBuilder sb = new StringBuilder();
        if (prefix != null) {
          sb.append(prefix);
          sb.append(" ");
        }
        sb.append(qName);
        parseKeyAndValue(sb.toString(), value, prevRuntype);
      }
    }
    catch (NumberFormatException exc) {
      LOGGER.warn("Could not parse {} value: {}", qName, value);
    }

    prevRuntype = attributes.getValue("runtype");
  }

  @Override
  public void endDocument() {
    for (String name : colors.keySet()) {
      String chName = dyes.get(name);
      if (chName == null) chName = name;
      realColors.put(chName, colors.get(name));
    }

    if (nXFields > 0 && nXFields < 10 && nYFields > 0 && nYFields < 10 &&
      populateXY)
    {
      CoreMetadata ms0 = core.get(0, 0);
      ms0.sizeX *= nXFields;
      ms0.sizeY *= nYFields;
    }
  }

  // -- Helper methods --

  public void parseKeyAndValue(String key, String value, String runtype) {
    if (key == null || value == null) return;
    CoreMetadata ms0 = core.get(0, 0);
    metadata.put(key, value);

    try {
      if (key.endsWith("dCalibration")) {
        pixelSizeX = DataTools.parseDouble(value);
        pixelSizeY = pixelSizeX;
      }
      else if (key.endsWith("dZStep")) {
        pixelSizeZ =  DataTools.parseDouble(value);
      }
      else if (key.endsWith("Gain")) {
        Double gainValue = DataTools.parseDouble(value);
        if (gainValue != null) gain.add(gainValue);
      }
      else if (key.endsWith("dLampVoltage")) {
        voltage = DataTools.parseDouble(value);
      }
      else if (key.endsWith("dObjectiveMag") && mag == null) {
        mag = DataTools.parseDouble(value);
      }
      else if (key.endsWith("dObjectiveNA")) {
        na = DataTools.parseDouble(value);
      }
      else if (key.endsWith("dRefractIndex1")) {
        refractiveIndex = DataTools.parseDouble(value);
      }
      else if (key.equals("sObjective") || key.equals("wsObjectiveName") ||
        key.equals("sOptics"))
      {
        String[] tokens = value.split(" ");
        int magIndex = -1;
        for (int i=0; i<tokens.length; i++) {
          if (tokens[i].indexOf('x') != -1) {
            magIndex = i;
            break;
          }
        }
        final StringBuilder s = new StringBuilder();
        for (int i=0; i<magIndex; i++) {
          s.append(tokens[i]);
        }
        correction = s.toString();
        if (magIndex >= 0) {
          String m =
            tokens[magIndex].substring(0, tokens[magIndex].indexOf('x'));
          mag = DataTools.parseDouble(m);
        }
        if (magIndex + 1 < tokens.length) immersion = tokens[magIndex + 1];
      }
      else if (key.endsWith("dTimeMSec")) {
        Long v = DataTools.parseDouble(value).longValue();
        if (!ts.contains(v)) {
          ts.add(v);
          metadata.put("number of timepoints", ts.size());
        }
      }
      else if (key.endsWith("dZPos")) {
        Long v = DataTools.parseLong(value);
        if (!zs.contains(v)) zs.add(v);
      }
      else if (key.endsWith("uiCount")) {
        if (runtype != null) {
          if (runtype.endsWith("ZStackLoop")) {
            if (ms0.sizeZ == 0) {
              ms0.sizeZ = Integer.parseInt(value);
              if (ms0.dimensionOrder.indexOf('Z') == -1) {
                ms0.dimensionOrder = "Z" + ms0.dimensionOrder;
              }
            }
          }
          else if (runtype.endsWith("TimeLoop")) {
            if (ms0.sizeT == 0) {
              ms0.sizeT = Integer.parseInt(value);
              if (ms0.dimensionOrder.indexOf('T') == -1) {
                ms0.dimensionOrder = "T" + ms0.dimensionOrder;
              }
            }
          }
          else if (runtype.endsWith("XYPosLoop") && core.size() == 1) {
            int len = Integer.parseInt(value);
            core = new CoreMetadataList();
            for (int i=0; i<len; i++) {
              core.add(ms0);
            }
          }
        }
      }
      else if (key.endsWith("uiBpcSignificant")) {
        ms0.bitsPerPixel = Integer.parseInt(value);
      }
      else if (key.equals("VirtualComponents")) {
        if (ms0.sizeC == 0) {
          ms0.sizeC = Integer.parseInt(value);
          if (ms0.dimensionOrder.indexOf('C') == -1) {
            ms0.dimensionOrder += "C" + ms0.dimensionOrder;
          }
        }
      }
      else if (key.startsWith("TextInfoItem") || key.endsWith("TextInfoItem")) {
        metadata.remove(key);
        value = value.replaceAll("&#x000d;", "");
        value = value.replaceAll("#x000d;", "");
        value = value.replaceAll("&#x000a;", "\n");
        value = value.replaceAll("#x000a;", "\n");
        String[] tokens = value.split("\n");
        for (String t : tokens) {
          t = t.trim();

          String[] v = t.split(":");
          if (v.length == 0) {
            continue;
          }
          else if (v.length == 2) {
            parseKeyAndValue(v[0].trim(), v[1].trim(), runtype);
          }
          else if (v[0].equals("Line")) {
            parseKeyAndValue(v[0], t.substring(t.indexOf(':') + 1).trim(), runtype);
          }
          else if (v.length > 1) {
            v[0] = v[0].replace('{', ' ');
            v[0] = v[0].replace('}', ' ');
            metadata.put(v[0].trim(), v[1]);
          }
          else if (v.length == 1) {
            metadata.put(key, v[0]);
          }
        }
      }
      else if (isDimensions(key)) {
        String[] dims = value.split(" x ");

        if (ms0.sizeZ == 0) ms0.sizeZ = 1;
        if (ms0.sizeT == 0) ms0.sizeT = 1;
        if (ms0.sizeC == 0) ms0.sizeC = 1;

        for (String dim : dims) {
          dim = dim.trim();
          int v = Integer.parseInt(dim.replaceAll("\\D", ""));
          v = (int) Math.max(v, 1);
          if (dim.startsWith("XY")) {
            numSeries = v;
            if (numSeries > 1) {
              int x = ms0.sizeX;
              int y = ms0.sizeY;
              int z = ms0.sizeZ;
              int tSize = ms0.sizeT;
              int c = ms0.sizeC;
              String order = ms0.dimensionOrder;
              core = new CoreMetadataList();
              for (int i=0; i<numSeries; i++) {
                CoreMetadata ms = new CoreMetadata();
                core.add(ms);
                ms.sizeX = x;
                ms.sizeY = y;
                ms.sizeZ = z == 0 ? 1 : z;
                ms.sizeC = c == 0 ? 1 : c;
                ms.sizeT = tSize == 0 ? 1 : tSize;
                ms.dimensionOrder = order;
              }
              ms0 = core.get(0, 0);
            }
          }
          else if (dim.startsWith("T")) {
            if (ms0.sizeT <= 1 || v < ms0.sizeT) {
              ms0.sizeT = v;
            }
          }
          else if (dim.startsWith("Z")) {
            if (ms0.sizeZ <= 1) {
              ms0.sizeZ = v;
            }
          }
          else if (ms0.sizeC <= 1) {
            ms0.sizeC = v;
          }
        }

        ms0.imageCount = ms0.sizeZ * ms0.sizeC * ms0.sizeT;
      }
      else if (key.startsWith("Number of Picture Planes")) {
        ms0.sizeC = Integer.parseInt(value.replaceAll("\\D", ""));
      }
      else if (key.equals("Name")) {
        channelNames.add(value);
      }
      else if (key.equals("Modality")) {
        modality.add(value);
      }
      else if (key.equals("Camera Type")) {
        cameraModel = value;
      }
      else if (key.equals("Binning")) {
        binning.add(value);
      }
      else if (key.equals("Readout Speed")) {
        int last = value.lastIndexOf(" ");
        if (last != -1) value = value.substring(0, last);
        speed.add(DataTools.parseDouble(value));
      }
      else if (key.equals("Temperature")) {
        String temp = value.replaceAll("[\\D&&[^-.]]", "");
        temperature.add(DataTools.parseDouble(temp));
      }
      // exposure times are often defined once in a PropertiesFast block,
      // and again in a PropertiesQuality block
      else if (key.equals("Exposure") &&
        (prevElement == null || "no_name".equals(prevElement) || "PropertiesQuality".equals(prevElement)))
      {
        String[] s = value.trim().split(" ");
        Double time = DataTools.parseDouble(s[0]);
        if (time != null) {
          // TODO: check for other units
          if (s.length > 1) {
            if (s[1].equals("ms")) time /= 1000;
          }
          else {
            // assume time is in milliseconds
            time /= 1000;
          }
          exposureTime.add(time);
        }
      }
      else if (key.equals("{Pinhole Size}")) {
        pinholeSize = DataTools.parseDouble(value);
        metadata.put("Pinhole size", value);
      }
      else if (key.startsWith("- Step")) {
        Double step = parsePixelsSizeZFromKey(key);
        if (step != null) pixelSizeZ = step;
      }
      else if (key.equals("Line")) {
        String[] values = value.split(";");
        for (int q=0; q<values.length; q++) {
          int colon = values[q].indexOf(':');
          if (colon < 0) continue;
          String nextKey = values[q].substring(0, colon).trim();
          String nextValue = values[q].substring(colon + 1).trim();
          parseKeyAndValue(nextKey, nextValue, runtype);
        }
      }
      else if (key.equalsIgnoreCase("Emission wavelength")) {
        String[] v = value.split(" ");
        emWave.add(new Double(v[0]));
      }
      else if (key.equalsIgnoreCase("Excitation wavelength")) {
        String[] v = value.split(" ");
        exWave.add(new Double(v[0]));
      }
      else if (key.equals("Power")) {
        power.add(DataTools.parseDouble(value).intValue());
      }
      else if (key.equals("CameraUniqueName")) {
        cameraModel = value;
      }
      else if (key.equals("ExposureTime")) {
        exposureTime.add(new Double(value) / 1000d);
      }
      else if (key.equals("sDate")) {
        date = DateTools.formatDate(value, DATE_FORMAT);
      }
      else if (key.equals("Name") && channelNames.size() < ms0.sizeC) {
        channelNames.add(value);
      }
      else if (key.equals("Z Stack Loop")) {
        int v = Integer.parseInt(value);
        if (v <= nImages || nImages <= 0) {
          core.get(0, 0).sizeZ = v;
        }
      }
      else if (key.equals("Time Loop")) {
        int v = Integer.parseInt(value);
        if (v <= nImages && firstTimeLoop) {
          core.get(0, 0).sizeT = v;
          firstTimeLoop = false;
        }
      }
    }
    catch (NumberFormatException | NullPointerException exc) {
      LOGGER.warn("Could not parse {} value: {}", key, value);
    }
  }


  /**
   * Returns whether or not the specified key is denotes dimensions.
   * Full versions of Elements allow the user to choose a language,
   * though it's not clear what all of the choices are.
   * Most keys are always recorded in English, but some are recorded
   * in the language specified by the user.
   */
  public boolean isDimensions(String key) {
     return key.startsWith("Dimensions") || key.startsWith("Abmessungen");
  }

  /**
   * Parses the physical size from a key.
   * The expected format for the key is "- Step <value>".
   */
  private Double parsePixelsSizeZFromKey(String key) {
    int space = key.indexOf(' ', key.indexOf("Step") + 1);
    if (space == -1) return null;

    int last = key.indexOf(' ', space + 1);
    if (last == -1) last = key.length();
    return DataTools.parseDouble(key.substring(space, last).trim());
  }
}
