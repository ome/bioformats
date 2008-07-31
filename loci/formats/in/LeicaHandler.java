//
// LeicaHandler.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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

import java.util.*;
import loci.formats.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler for parsing XML in Leica LIF and Leica TCS files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/LeicaHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/LeicaHandler.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LeicaHandler extends DefaultHandler {

  // -- Fields --

  private String series, fullSeries;
  private int count = 0, numChannels, extras;
  private boolean firstElement = true, dcroiOpen = false;

  private Vector extraDims, channels, widths, heights, zs, ts, bps;
  private Vector seriesNames, containerNames, containerCounts;
  private Vector xcal, ycal, zcal, bits;

  private int numDatasets;
  private Hashtable metadata;

  // -- Constructor --

  public LeicaHandler() {
    super();
    extraDims = new Vector();
    channels = new Vector();
    widths = new Vector();
    heights = new Vector();
    zs = new Vector();
    ts = new Vector();
    bps = new Vector();
    seriesNames = new Vector();
    containerNames = new Vector();
    containerCounts = new Vector();
    metadata = new Hashtable();
    xcal = new Vector();
    ycal = new Vector();
    zcal = new Vector();
    bits = new Vector();
  }

  // -- LeicaHandler API methods --

  public Vector getExtraDims() { return extraDims; }

  public Vector getChannels() { return channels; }

  public Vector getWidths() { return widths; }

  public Vector getHeights() { return heights; }

  public Vector getZs() { return zs; }

  public Vector getTs() { return ts; }

  public Vector getBPS() { return bps; }

  public Vector getSeriesNames() { return seriesNames; }

  public Vector getContainerNames() { return containerNames; }

  public Vector getContainerCounts() { return containerCounts; }

  public Hashtable getMetadata() { return metadata; }

  public int getNumDatasets() { return numDatasets; }

  public Vector getXCal() { return xcal; }

  public Vector getYCal() { return ycal; }

  public Vector getZCal() { return zcal; }

  public Vector getBits() { return bits; }

  // -- DefaultHandler API methods --

  public void endElement(String uri, String localName, String qName) {
    if (qName.equals("Element")) {
      if (dcroiOpen) {
        dcroiOpen = false;
        return;
      }
      if (fullSeries.indexOf("/") != -1) {
        fullSeries = fullSeries.substring(0, fullSeries.indexOf("/"));
      }
      else fullSeries = "";

      extraDims.add(new Integer(extras));
      if (numChannels == 0) numChannels = 1;
      channels.add(new Integer(numChannels));

      if (widths.size() < numDatasets && heights.size() < numDatasets) {
        numDatasets--;
      }
      else if (widths.size() > numDatasets && heights.size() > numDatasets) {
        numDatasets = widths.size();
      }

      if (widths.size() < numDatasets) widths.add(new Integer(1));
      if (heights.size() < numDatasets) heights.add(new Integer(1));
      if (zs.size() < numDatasets) zs.add(new Integer(1));
      if (ts.size() < numDatasets) ts.add(new Integer(1));
      if (bps.size() < numDatasets) bps.add(new Integer(8));

      numChannels = 0;
    }
  }

  public void startElement(String uri, String localName, String qName,
    Attributes attributes)
  {
    if (qName.equals("Element")) {
      if (!attributes.getValue("Name").equals("DCROISet") && !firstElement) {
        series = attributes.getValue("Name");
        containerNames.add(series);
        if (fullSeries == null || fullSeries.equals("")) fullSeries = series;
        else fullSeries += "/" + series;
      }
      else if (firstElement) firstElement = false;

      if (attributes.getValue("Name").equals("DCROISet")) {
        dcroiOpen = true;
      }

      numDatasets++;
      int idx = numDatasets - 1;
      if (idx >= seriesNames.size()) {
        numDatasets = seriesNames.size();
      }

      if (!dcroiOpen) {
        numChannels = 0;
        extras = 1;
      }
    }
    else if (qName.equals("Experiment")) {
      for (int i=0; i<attributes.getLength(); i++) {
        metadata.put(attributes.getQName(i), attributes.getValue(i));
      }
    }
    else if (qName.equals("Image")) {
      containerNames.remove(series);
      if (containerCounts.size() < containerNames.size()) {
        containerCounts.add(new Integer(1));
      }
      else if (containerCounts.size() > 0) {
        int ndx = containerCounts.size() - 1;
        int n = ((Integer) containerCounts.get(ndx)).intValue();
        containerCounts.setElementAt(new Integer(n + 1), ndx);
      }
      if (fullSeries == null || fullSeries.equals("")) fullSeries = series;
      seriesNames.add(fullSeries);
    }
    else if (qName.equals("ChannelDescription")) {
      String prefix = "Channel " + count + " - ";
      if (fullSeries != null && !fullSeries.equals("")) {
        prefix = fullSeries + " - " + prefix;
      }
      for (int i=0; i<attributes.getLength(); i++) {
        metadata.put(prefix + attributes.getQName(i), attributes.getValue(i));
      }
      count++;
      numChannels++;
      if (channels.size() > seriesNames.size() - 1) {
        channels.setElementAt(new Integer(count), seriesNames.size() - 1);
      }
      else channels.add(new Integer(count));
      if (numChannels == 1) {
        bps.add(new Integer(attributes.getValue("Resolution")));
      }
    }
    else if (qName.equals("DimensionDescription")) {
      String prefix = "Dimension " + count + " - ";
      if (fullSeries != null && !fullSeries.equals("")) {
        prefix = fullSeries + " - " + prefix;
      }
      for (int i=0; i<attributes.getLength(); i++) {
        metadata.put(prefix + attributes.getQName(i), attributes.getValue(i));
      }
      int len = Integer.parseInt(attributes.getValue("NumberOfElements"));
      int id = Integer.parseInt(attributes.getValue("DimID"));

      switch (id) {
        case 1:
          widths.add(new Integer(len));
          int b = Integer.parseInt(attributes.getValue("BytesInc"));
          bits.add(new Integer(b * 8));
          break;
        case 2:
          heights.add(new Integer(len));
          break;
        case 3:
          zs.add(new Integer(len));
          break;
        case 4:
          ts.add(new Integer(len));
          break;
        default:
          extras *= len;
      }
    }
    else if (qName.equals("ScannerSettingRecord")) {
      String identifier = attributes.getValue("Identifier");
      String key = identifier + " - " + attributes.getValue("Description");
      if (fullSeries != null && !fullSeries.equals("")) {
        key = fullSeries + " - " + key;
      }
      metadata.put(key, attributes.getValue("Variant"));
      if ("csScanMode".equals(identifier)) {
        String order = attributes.getValue("Variant").toLowerCase();

        if (order.indexOf("xy") == -1) {
          // switch the axis sizes around, depending on the
          // stored dimension order

          int xPos = order.indexOf("x");
          int yPos = order.indexOf("y");
          int zPos = order.indexOf("z");
          int tPos = order.indexOf("t");

          if (xPos < 0) xPos = 0;
          if (yPos < 0) yPos = 1;
          if (zPos < 0) zPos = 2;
          if (tPos < 0) tPos = 3;

          int index = widths.size() - 1;

          int x = ((Integer) widths.get(index)).intValue();
          int y = index < heights.size() ?
            ((Integer) heights.get(index)).intValue() : 1;
          int z = index < zs.size() ? ((Integer) zs.get(index)).intValue() : 1;
          int t = index < ts.size() ? ((Integer) ts.get(index)).intValue() : 1;

          int[] dimensions = {x, y, z, t};

          x = dimensions[xPos];
          y = dimensions[yPos];
          z = dimensions[zPos];
          t = dimensions[tPos];

          widths.setElementAt(new Integer(x), widths.size() - 1);
          if (index < heights.size()) {
            heights.setElementAt(new Integer(y), heights.size() - 1);
          }
          else heights.add(new Integer(y));
          if (index < zs.size()) {
            zs.setElementAt(new Integer(z), zs.size() - 1);
          }
          else zs.add(new Integer(z));
          if (index < ts.size()) {
            ts.setElementAt(new Integer(t), ts.size() - 1);
          }
          else ts.add(new Integer(t));
        }
      }
      else if (identifier.startsWith("dblVoxel")) {
        String size = attributes.getValue("Variant");
        float cal = Float.parseFloat(size) * 1000000;
        if (identifier.endsWith("X")) xcal.add(new Float(cal));
        else if (identifier.endsWith("Y")) ycal.add(new Float(cal));
        else if (identifier.endsWith("Z")) zcal.add(new Float(cal));
      }
    }
    else if (qName.equals("FilterSettingRecord")) {
      String key = attributes.getValue("ObjectName") + " - " +
        attributes.getValue("Description") + " - " +
        attributes.getValue("Attribute");
      if (fullSeries != null && !fullSeries.equals("")) {
        key = fullSeries + " - " + key;
      }
      for (int i=0; i<attributes.getLength(); i++) {
        metadata.put(key + " - " + attributes.getQName(i),
          attributes.getValue(i));
      }
    }
    else if (qName.equals("ATLConfocalSettingDefinition")) {
      if (fullSeries == null) fullSeries = "";
      if (fullSeries.endsWith(" - Master sequential setting")) {
        fullSeries = fullSeries.replaceAll("Master sequential setting",
          "Sequential Setting 0");
      }

      if (fullSeries.indexOf("Sequential Setting ") == -1) {
        if (fullSeries.equals("")) fullSeries = "Master sequential setting";
        else fullSeries += " - Master sequential setting";
      }
      else {
        int ndx = fullSeries.indexOf("Sequential Setting ") + 19;
        try {
          int n = Integer.parseInt(fullSeries.substring(ndx)) + 1;
          fullSeries = fullSeries.substring(0, ndx) + String.valueOf(n);
        }
        catch (NumberFormatException exc) {
          fullSeries = fullSeries.substring(0, fullSeries.indexOf("-")).trim();
        }
      }

      for (int i=0; i<attributes.getLength(); i++) {
        metadata.put(fullSeries + " - " + attributes.getQName(i),
          attributes.getValue(i));
      }
    }
    else if (qName.equals("Wheel")) {
      String prefix = qName + " " + count + " - ";
      if (fullSeries != null && !fullSeries.equals("")) {
        prefix = fullSeries + " - " + prefix;
      }
      for (int i=0; i<attributes.getLength(); i++) {
        metadata.put(prefix + attributes.getQName(i), attributes.getValue(i));
      }
      count++;
    }
    else if (qName.equals("WheelName")) {
      String prefix = "Wheel " + (count - 1) + " - WheelName ";
      if (fullSeries != null && !fullSeries.equals("")) {
        prefix = fullSeries + " - " + prefix;
      }
      int ndx = 0;
      while (metadata.get(prefix + ndx) != null) ndx++;
      metadata.put(prefix + ndx, attributes.getValue("FilterName"));
    }
    else if (qName.equals("MultiBand")) {
      String prefix = qName + " Channel " + attributes.getValue("Channel");
      if (fullSeries != null && !fullSeries.equals("")) {
        prefix = fullSeries + " - " + prefix;
      }
      for (int i=0; i<attributes.getLength(); i++) {
        metadata.put(prefix + " - " + attributes.getQName(i),
          attributes.getValue(i));
      }
    }
    else if (qName.equals("LaserLineSetting")) {
      String prefix = "LaserLine " + attributes.getValue("LaserLine");
      if (fullSeries != null && !fullSeries.equals("")) {
        prefix = fullSeries + " - " + prefix;
      }
      for (int i=0; i<attributes.getLength(); i++) {
        String name = attributes.getQName(i);
        if (!name.equals("LaserLine")) {
          metadata.put(prefix + " - " + name, attributes.getValue(i));
        }
      }
    }
    else if (qName.equals("Detector")) {
      String prefix = qName + " Channel " + attributes.getValue("Channel");
      if (fullSeries != null && !fullSeries.equals("")) {
        prefix = fullSeries + " - " + prefix;
      }
      for (int i=0; i<attributes.getLength(); i++) {
        String name = attributes.getQName(i);
        if (!name.equals("Channel")) {
          metadata.put(prefix + " - " + name, attributes.getValue(i));
        }
      }
    }
    else if (qName.equals("Laser")) {
      String prefix = qName + " " + attributes.getValue("LaserName");
      if (fullSeries != null && !fullSeries.equals("")) {
        prefix = fullSeries + " - " + prefix;
      }
      for (int i=0; i<attributes.getLength(); i++) {
        String name = attributes.getQName(i);
        if (!name.equals("LaserName")) {
          metadata.put(prefix + " - " + name, attributes.getValue(i));
        }
      }
    }
    else if (qName.equals("TimeStamp")) {
      long high = Long.parseLong(attributes.getValue("HighInteger"));
      long low = Long.parseLong(attributes.getValue("LowInteger"));

      high <<= 32;
      if ((int) low < 0) {
        low &= 0xffffffffL;
      }
      long stamp = high + low;
      long ms = stamp / 10000;

      String n = String.valueOf(count);
      while (n.length() < 4) n = "0" + n;
      metadata.put(fullSeries + " - " + qName + n,
        DataTools.convertDate(ms, DataTools.COBOL));
      count++;
    }
    else if (qName.equals("ChannelScalingInfo")) {
      String prefix = qName + count;
      if (fullSeries != null && !fullSeries.equals("")) {
        prefix = fullSeries + " - " + prefix;
      }
      for (int i=0; i<attributes.getLength(); i++) {
        String name = attributes.getQName(i);
        metadata.put(prefix + " - " + name, attributes.getValue(i));
      }
    }
    else if (qName.equals("RelTimeStamp")) {
      metadata.put(fullSeries + qName + attributes.getValue("Frame"),
        attributes.getValue("Time"));
    }
    else count = 0;
  }

}
