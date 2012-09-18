/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * DefaultHandler implementation for handling XML produced by Nikon Elements.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ND2Handler.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ND2Handler.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ND2Handler extends DefaultHandler {

  // -- Constants --

  private static final String DATE_FORMAT = "dd/MM/yyyy  HH:mm:ss";

  // -- Fields --

  private String prefix = null;
  private String prevRuntype = null;
  private String prevElement = null;

  private Hashtable<String, Object> metadata = new Hashtable<String, Object>();
  private CoreMetadata[] core;

  private boolean isLossless;
  private ArrayList<Long> zs = new ArrayList<Long>();
  private ArrayList<Long> ts = new ArrayList<Long>();

  private int numSeries = 0;
  private float pixelSizeX, pixelSizeY, pixelSizeZ;

  private Float pinholeSize, voltage, mag, na;
  private String objectiveModel, immersion, correction;
  private Float refractiveIndex;
  private ArrayList<String> channelNames = new ArrayList<String>();
  private ArrayList<String> modality = new ArrayList<String>();
  private ArrayList<String> binning = new ArrayList<String>();
  private ArrayList<Float> speed = new ArrayList<Float>();
  private ArrayList<Float> gain = new ArrayList<Float>();
  private ArrayList<Float> temperature = new ArrayList<Float>();
  private ArrayList<Float> exposureTime = new ArrayList<Float>();
  private ArrayList<Integer> exWave = new ArrayList<Integer>();
  private ArrayList<Integer> emWave = new ArrayList<Integer>();
  private ArrayList<Integer> power = new ArrayList<Integer>();
  private ArrayList<Hashtable<String, String>> rois =
    new ArrayList<Hashtable<String, String>>();
  private ArrayList<Float> posX = new ArrayList<Float>();
  private ArrayList<Float> posY = new ArrayList<Float>();
  private ArrayList<Float> posZ = new ArrayList<Float>();
  private ArrayList<String> posNames = new ArrayList<String>();

  private String cameraModel;
  private int fieldIndex = 0;
  private String date;

  private Hashtable<String, Integer> colors = new Hashtable<String, Integer>();
  private Hashtable<String, String> dyes = new Hashtable<String, String>();
  private Hashtable<String, Integer> realColors =
    new Hashtable<String, Integer>();

  private int nXFields = 0, nYFields = 0;

  private boolean populateXY = true;
  private int nImages = 0;

  private boolean validLoopState = false;

  // -- Constructor --

  public ND2Handler(CoreMetadata[] core, int nImages) {
    this(core, true, nImages);
  }

  public ND2Handler(CoreMetadata[] core, boolean populateXY, int nImages) {
    super();
    this.populateXY = populateXY;
    this.nImages = nImages;
    this.core = core;
    if (this.core.length > 1) {
      fieldIndex = 2;
    }
  }

  // -- ND2Handler API methods --

  public int getXFields() {
    return nXFields;
  }

  public CoreMetadata[] getCoreMetadata() {
    return core;
  }

  public void populateROIs(MetadataStore store) {
    for (int r=0; r<rois.size(); r++) {
      Hashtable<String, String> roi = rois.get(r);
      String type = roi.get("ROIType");

      if (type.equals("Text")) {
        String roiID = MetadataTools.createLSID("ROI", 0, r);
        store.setROIID(roiID, 0, r);

        int fontSize = Integer.parseInt(roi.get("fHeight"));

        String rectangle = roi.get("rectangle");
        String[] p = rectangle.split(",");
        double[] points = new double[p.length];
        for (int i=0; i<p.length; i++) {
          points[i] = Double.parseDouble(p[i]);
        }

        store.setRectID(MetadataTools.createLSID("Shape", 0, r, 0), 0, r, 0);
        store.setShapeStrokeWidth(
          (int) Float.parseFloat(roi.get("line-width")), 0, r, 0);
        store.setShapeFontSize(fontSize, 0, r, 0);
        store.setShapeText(roi.get("eval-text"), 0, r, 0);
        store.setRectX(p[0], 0, r, 0);
        store.setRectY(p[1], 0, r, 0);
        store.setRectWidth(String.valueOf(points[2] - points[0]), 0, r, 0);
        store.setRectHeight(String.valueOf(points[3] - points[1]), 0, r, 0);
      }
      else if (type.equals("HorizontalLine") || type.equals("VerticalLine")) {
        String roiID = MetadataTools.createLSID("ROI", 0, r);
        store.setROIID(roiID, 0, r);

        String segments = roi.get("segments");
        segments = segments.replaceAll("\\[\\]", "");
        String[] points = segments.split("\\)");

        StringBuffer sb = new StringBuffer();
        for (int i=0; i<points.length; i++) {
          points[i] = points[i].substring(points[i].indexOf(":") + 1);
          sb.append(points[i]);
          if (i < points.length - 1) sb.append(" ");
        }

        store.setPolylineID(MetadataTools.createLSID("Shape", 0, r, 0), 0, r, 0);
        store.setPolylinePoints(sb.toString(), 0, r, 0);
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

  public float getPixelSizeX() {
    return pixelSizeX;
  }

  public float getPixelSizeY() {
    return pixelSizeY;
  }

  public float getPixelSizeZ() {
    return pixelSizeZ;
  }

  public Float getPinholeSize() {
    return pinholeSize;
  }

  public Float getVoltage() {
    return voltage;
  }

  public Float getMagnification() {
    return mag;
  }

  public Float getNumericalAperture() {
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

  public Float getRefractiveIndex() {
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

  public ArrayList<Float> getSpeeds() {
    return speed;
  }

  public ArrayList<Float> getGains() {
    return gain;
  }

  public ArrayList<Float> getTemperatures() {
    return temperature;
  }

  public ArrayList<Float> getExposureTimes() {
    return exposureTime;
  }

  public ArrayList<Integer> getExcitationWavelengths() {
    return exWave;
  }

  public ArrayList<Integer> getEmissionWavelengths() {
    return emWave;
  }

  public ArrayList<Integer> getPowers() {
    return power;
  }

  public ArrayList<Hashtable<String, String>> getROIs() {
    return rois;
  }

  public ArrayList<Float> getXPositions() {
    return posX;
  }

  public ArrayList<Float> getYPositions() {
    return posY;
  }

  public ArrayList<Float> getZPositions() {
    return posZ;
  }

  public ArrayList<String> getPositionNames() {
    return posNames;
  }

  public String getCameraModel() {
    return cameraModel;
  }

  public int getFieldIndex() {
    return fieldIndex;
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

  public void startElement(String uri, String localName, String qName,
    Attributes attributes)
  {
    String runtype = attributes.getValue("runtype");
    if ("CLxListVariant".equals(runtype) || "RLxIRect".equals(runtype)) {
      prevElement = qName;
    }

    String value = attributes.getValue("value");

    if (qName.equals("uiWidth")) {
      int x = Integer.parseInt(value);
      if (x != 0 && populateXY) {
        core[0].sizeX = x;
      }
    }
    else if (qName.equals("uiCamPxlCountX")) {
      if (core[0].sizeX == 0 && populateXY) {
        try {
          core[0].sizeX = Integer.parseInt(value);
        }
        catch (NumberFormatException e) { }
      }
    }
    else if (qName.equals("uiCamPxlCountY")) {
      if (core[0].sizeY == 0 && populateXY) {
        try {
          core[0].sizeY = Integer.parseInt(value);
        }
        catch (NumberFormatException e) { }
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
      if (qName.equals("left") && core[0].sizeX == 0) {
        core[0].sizeX = -1 * Integer.parseInt(value);
      }
      else if (qName.equals("top") && core[0].sizeY == 0) {
        core[0].sizeY = -1 * Integer.parseInt(value);
      }
      else if (qName.equals("right") && core[0].sizeX <= 0) {
        core[0].sizeX += Integer.parseInt(value);
      }
      else if (qName.equals("bottom") && core[0].sizeY <= 0) {
        core[0].sizeY += Integer.parseInt(value);
      }
    }
    else if ("LoopState".equals(prevElement) && value != null) {
      if (!validLoopState) {
        validLoopState = !value.equals("529");
      }
    }
    else if ("LoopSize".equals(prevElement) && value != null) {
      int v = Integer.parseInt(value);

      if (core[0].sizeT == 0) {
        core[0].sizeT = v;
      }
      else if (qName.equals("no_name") && v > 0 && core.length == 1) {
        CoreMetadata previous = core[0];
        core = new CoreMetadata[v];
        for (int q=0; q<v; q++) {
          core[q] = previous;
        }
        fieldIndex = 2;
      }
      else if (core[0].sizeZ == 0) {
        core[0].sizeZ = v;
      }
      core[0].dimensionOrder = "CZT";
    }
    else if ("pPosName".equals(prevElement) && value != null) {
      posNames.add(value);
    }
    else if (qName.equals("FramesBefore")) {
      if (core[0].sizeZ == 0) {
        core[0].sizeZ = 1;
      }
      if (core.length == 1) {
        core[0].sizeZ *= Integer.parseInt(value);
      }
    }
    else if (qName.equals("FramesAfter")) {
      if (core.length == 1) {
        core[0].sizeZ *= Integer.parseInt(value);

        if (core[0].sizeT * core[0].sizeZ > nImages &&
          core[0].sizeT <= nImages && validLoopState &&
          core[0].sizeT != core[0].sizeZ)
        {
          core[0].sizeZ = core[0].sizeT;
          core[0].sizeT = 1;
        }
      }
    }
    else if (qName.equals("TimeBefore")) {
      if (core[0].sizeT == 0) {
        core[0].sizeT = 1;
      }
      core[0].sizeT *= Integer.parseInt(value);
    }
    else if (qName.equals("TimeAfter")) {
      core[0].sizeT *= Integer.parseInt(value);
    }
    else if (qName.equals("uiMaxDst")) {
      int maxPixelValue = Integer.parseInt(value) + 1;
      int bits = 0;
      while (maxPixelValue > 0) {
        maxPixelValue /= 2;
        bits++;
      }
      if (core[0].pixelType == 0) {
        switch (bits / 8) {
          case 1:
            core[0].pixelType = FormatTools.UINT8;
            break;
          case 2:
            core[0].pixelType = FormatTools.UINT16;
            break;
          case 4:
            core[0].pixelType = FormatTools.UINT32;
            break;
        }
      }
    }
    else if (qName.equals("uiWidthBytes") || qName.equals("uiBpcInMemory")) {
      int div = qName.equals("uiWidthBytes") ? core[0].sizeX : 8;
      if (div > 0) {
        int bytes = Integer.parseInt(value) / div;

        switch (bytes) {
          case 1:
            core[0].pixelType = FormatTools.UINT8;
            break;
          case 2:
            core[0].pixelType = FormatTools.UINT16;
            break;
          case 4:
            core[0].pixelType = FormatTools.UINT32;
            break;
        }
        parseKeyAndValue(qName, value, prevRuntype);
      }
    }
    else if ("dPosX".equals(prevElement) && qName.startsWith("item_")) {
      posX.add(new Float(DataTools.sanitizeDouble(value)));
      metadata.put("X position for position #" + posX.size(), value);
    }
    else if ("dPosY".equals(prevElement) && qName.startsWith("item_")) {
      posY.add(new Float(DataTools.sanitizeDouble(value)));
      metadata.put("Y position for position #" + posY.size(), value);
    }
    else if ("dPosZ".equals(prevElement) && qName.startsWith("item_")) {
      posZ.add(new Float(DataTools.sanitizeDouble(value)));
      metadata.put("Z position for position #" + posZ.size(), value);
    }
    else if (qName.startsWith("item_")) {
      int v = Integer.parseInt(qName.substring(qName.indexOf("_") + 1));
      if (v == numSeries) {
        fieldIndex = 2;
        //fieldIndex = core[0].dimensionOrder.length();
        numSeries++;
      }
      else if (v < numSeries && fieldIndex < core[0].dimensionOrder.length()) {
        fieldIndex = 2;
        //fieldIndex = core[0].dimensionOrder.length();
      }
    }
    else if (qName.equals("uiCompCount")) {
      int v = Integer.parseInt(value);
      core[0].sizeC = (int) Math.max(core[0].sizeC, v);
    }
    else if (qName.equals("uiHeight") && populateXY) {
      int y = Integer.parseInt(value);
      if (y != 0) {
        core[0].sizeY = y;
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
      pinholeSize = new Float(DataTools.sanitizeDouble(value));
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
      if (core.length > 0) {
        int newCount = imageCount / core.length;
        if (newCount * core.length == imageCount) {
          imageCount = newCount;
        }
      }
      if (core[0].sizeZ * core[0].sizeT != imageCount &&
        core[0].sizeZ * core[0].sizeC * core[0].sizeT != imageCount)
      {
        if (core[0].sizeZ > 1 && core[0].sizeT <= 1) {
          core[0].sizeZ = imageCount;
          core[0].sizeT = 1;
          core[0].imageCount = imageCount;
        }
        else if (core[0].sizeT > 1 && core[0].sizeZ <= 1) {
          core[0].sizeT = imageCount;
          core[0].sizeZ = 1;
          core[0].imageCount = imageCount;
        }
        else if (imageCount == 0) {
          core[0].sizeT = 0;
          core[0].sizeZ = 0;
          core[0].imageCount = 0;
        }
      }
      metadata.put(qName, value);
    }
    else {
      StringBuffer sb = new StringBuffer();
      if (prefix != null) {
        sb.append(prefix);
        sb.append(" ");
      }
      sb.append(qName);
      parseKeyAndValue(sb.toString(), value, prevRuntype);
    }

    prevRuntype = attributes.getValue("runtype");
  }

  public void endDocument() {
    for (String name : colors.keySet()) {
      String chName = dyes.get(name);
      if (chName == null) chName = name;
      realColors.put(chName, colors.get(name));
    }

    if (nXFields > 0 && nXFields < 10 && nYFields > 0 && nYFields < 10 &&
      populateXY)
    {
      core[0].sizeX *= nXFields;
      core[0].sizeY *= nYFields;
    }
  }

  // -- Helper methods --

  private void parseKeyAndValue(String key, String value, String runtype) {
    if (key == null || value == null) return;
    metadata.put(key, value);
    if (key.endsWith("dCalibration")) {
      pixelSizeX = Float.parseFloat(DataTools.sanitizeDouble(value));
      pixelSizeY = pixelSizeX;
    }
    else if (key.endsWith("dZStep")) {
      pixelSizeZ = Float.parseFloat(DataTools.sanitizeDouble(value));
    }
    else if (key.endsWith("Gain")) {
      value = DataTools.sanitizeDouble(value);
      if (!value.equals("")) {
        gain.add(new Float(value));
      }
    }
    else if (key.endsWith("dLampVoltage")) {
      voltage = new Float(DataTools.sanitizeDouble(value));
    }
    else if (key.endsWith("dObjectiveMag") && mag == null) {
      mag = new Float(DataTools.sanitizeDouble(value));
    }
    else if (key.endsWith("dObjectiveNA")) {
      na = new Float(DataTools.sanitizeDouble(value));
    }
    else if (key.endsWith("dRefractIndex1")) {
      refractiveIndex = new Float(DataTools.sanitizeDouble(value));
    }
    else if (key.equals("sObjective") || key.equals("wsObjectiveName") ||
      key.equals("sOptics"))
    {
      String[] tokens = value.split(" ");
      int magIndex = -1;
      for (int i=0; i<tokens.length; i++) {
        if (tokens[i].indexOf("x") != -1) {
          magIndex = i;
          break;
        }
      }
      StringBuffer s = new StringBuffer();
      for (int i=0; i<magIndex; i++) {
        s.append(tokens[i]);
      }
      correction = s.toString();
      if (magIndex >= 0) {
        String m = tokens[magIndex].substring(0, tokens[magIndex].indexOf("x"));
        m = DataTools.sanitizeDouble(m);
        if (m.length() > 0) {
          mag = new Float(m);
        }
      }
      if (magIndex + 1 < tokens.length) immersion = tokens[magIndex + 1];
    }
    else if (key.endsWith("dTimeMSec")) {
      long v = (long) Double.parseDouble(DataTools.sanitizeDouble(value));
      if (!ts.contains(new Long(v))) {
        ts.add(new Long(v));
        metadata.put("number of timepoints", ts.size());
      }
    }
    else if (key.endsWith("dZPos")) {
      long v = (long) Double.parseDouble(DataTools.sanitizeDouble(value));
      if (!zs.contains(new Long(v))) {
        zs.add(new Long(v));
      }
    }
    else if (key.endsWith("uiCount")) {
      if (runtype != null) {
        if (runtype.endsWith("ZStackLoop")) {
          if (core[0].sizeZ == 0) {
            core[0].sizeZ = Integer.parseInt(value);
            if (core[0].dimensionOrder.indexOf("Z") == -1) {
              core[0].dimensionOrder = "Z" + core[0].dimensionOrder;
            }
          }
        }
        else if (runtype.endsWith("TimeLoop")) {
          if (core[0].sizeT == 0) {
            core[0].sizeT = Integer.parseInt(value);
            if (core[0].dimensionOrder.indexOf("T") == -1) {
              core[0].dimensionOrder = "T" + core[0].dimensionOrder;
            }
          }
        }
        else if (runtype.endsWith("XYPosLoop") && core.length == 1) {
          CoreMetadata oldCore = core[0];
          core = new CoreMetadata[Integer.parseInt(value)];
          for (int i=0; i<core.length; i++) {
            core[i] = oldCore;
          }
        }
      }
    }
    else if (key.equals("VirtualComponents")) {
      if (core[0].sizeC == 0) {
        core[0].sizeC = Integer.parseInt(value);
        if (core[0].dimensionOrder.indexOf("C") == -1) {
          core[0].dimensionOrder += "C" + core[0].dimensionOrder;
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
        if (t.startsWith("Dimensions:")) {
          t = t.substring(11);
          String[] dims = t.split(" x ");

          if (core[0].sizeZ == 0) core[0].sizeZ = 1;
          if (core[0].sizeT == 0) core[0].sizeT = 1;
          if (core[0].sizeC == 0) core[0].sizeC = 1;

          for (String dim : dims) {
            dim = dim.trim();
            int v = Integer.parseInt(dim.replaceAll("\\D", ""));
            v = (int) Math.max(v, 1);
            if (dim.startsWith("XY")) {
              numSeries = v;
              if (numSeries > 1) {
                int x = core[0].sizeX;
                int y = core[0].sizeY;
                int z = core[0].sizeZ;
                int tSize = core[0].sizeT;
                int c = core[0].sizeC;
                String order = core[0].dimensionOrder;
                core = new CoreMetadata[numSeries];
                for (int i=0; i<numSeries; i++) {
                  core[i] = new CoreMetadata();
                  core[i].sizeX = x;
                  core[i].sizeY = y;
                  core[i].sizeZ = z == 0 ? 1 : z;
                  core[i].sizeC = c == 0 ? 1 : c;
                  core[i].sizeT = tSize == 0 ? 1 : tSize;
                  core[i].dimensionOrder = order;
                }
              }
            }
            else if (dim.startsWith("T")) {
              if (core[0].sizeT <= 1 || v < core[0].sizeT) {
                core[0].sizeT = v;
              }
            }
            else if (dim.startsWith("Z")) {
              if (core[0].sizeZ <= 1) {
                core[0].sizeZ = v;
              }
            }
            else if (core[0].sizeC <= 1) {
              core[0].sizeC = v;
            }
          }

          core[0].imageCount = core[0].sizeZ * core[0].sizeC * core[0].sizeT;
        }
        else if (t.startsWith("Number of Picture Planes")) {
          core[0].sizeC = Integer.parseInt(t.replaceAll("\\D", ""));
        }
        else {
          String[] v = t.split(":");
          if (v.length == 2) {
            v[1] = v[1].trim();
            if (v[0].equals("Name")) {
              channelNames.add(v[1]);
            }
            else if (v[0].equals("Modality")) {
              modality.add(v[1]);
            }
            else if (v[0].equals("Camera Type")) {
              cameraModel = v[1];
            }
            else if (v[0].equals("Binning")) {
              binning.add(v[1]);
            }
            else if (v[0].equals("Readout Speed")) {
              int last = v[1].lastIndexOf(" ");
              if (last != -1) v[1] = v[1].substring(0, last);
              speed.add(new Float(DataTools.sanitizeDouble(v[1])));
            }
            else if (v[0].equals("Temperature")) {
              String temp = v[1].replaceAll("[\\D&&[^-.]]", "");
              temperature.add(new Float(DataTools.sanitizeDouble(temp)));
            }
            else if (v[0].equals("Exposure")) {
              String[] s = v[1].trim().split(" ");
              try {
                float time =
                  Float.parseFloat(DataTools.sanitizeDouble(s[0]));
                // TODO: check for other units
                if (s[1].equals("ms")) time /= 1000;
                exposureTime.add(new Float(time));
              }
              catch (NumberFormatException e) { }
            }
            else if (v[0].equals("{Pinhole Size}")) {
              pinholeSize = new Float(DataTools.sanitizeDouble(v[1]));
              metadata.put("Pinhole size", v[1]);
            }
          }
          else if (v[0].startsWith("- Step")) {
            int space = v[0].indexOf(" ", v[0].indexOf("Step") + 1);
            int last = v[0].indexOf(" ", space + 1);
            if (last == -1) last = v[0].length();
            pixelSizeZ = Float.parseFloat(
              DataTools.sanitizeDouble(v[0].substring(space, last)));
          }
          else if (v[0].equals("Line")) {
            String[] values = t.split(";");
            for (int q=0; q<values.length; q++) {
              int colon = values[q].indexOf(":");
              if (colon < 0) continue;
              String nextKey = values[q].substring(0, colon).trim();
              String nextValue = values[q].substring(colon + 1).trim();
              if (nextKey.equals("Emission wavelength")) {
                emWave.add(new Integer(nextValue));
              }
              else if (nextKey.equals("Excitation wavelength")) {
                exWave.add(new Integer(nextValue));
              }
              else if (nextKey.equals("Power")) {
                nextValue = DataTools.sanitizeDouble(nextValue);
                power.add(new Integer((int) Double.parseDouble(nextValue)));
              }
            }
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
    }
    else if (key.equals("CameraUniqueName")) {
      cameraModel = value;
    }
    else if (key.equals("ExposureTime")) {
      exposureTime.add(new Float(value) / 1000f);
    }
    else if (key.equals("sDate")) {
      date = DateTools.formatDate(value, DATE_FORMAT);
    }
  }

}
