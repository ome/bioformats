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

package loci.formats.in.LeicaMicrosystemsMetadata;

import java.util.ArrayList;
import java.util.List;

import loci.common.DataTools;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.in.MetadataLevel;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Length;

/** This class helps saving ROI information to a reader's MetadataStore
 * 
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
class ROI {
    // -- Constants --

    public static final int TEXT = 512;
    public static final int SCALE_BAR = 8192;
    public static final int POLYGON = 32;
    public static final int RECTANGLE = 16;
    public static final int LINE = 256;
    public static final int ARROW = 2;
    public static final long METER_MULTIPLY = 1000000;

    // -- Fields --
    public int type;

    public List<Double> x = new ArrayList<Double>();
    public List<Double> y = new ArrayList<Double>();

    // center point of the ROI
    public double transX, transY;

    // transformation parameters
    public double scaleX, scaleY;
    public double rotation;

    public long color;
    public int linewidth;

    public String text;
    public String fontName;
    public String fontSize;
    public String name;

    private boolean normalized = false;

    // -- ROI API methods --

    public void storeROI(MetadataStore store, int series, int roi, int roiIndex, int sizeX, int sizeY, boolean alternateCenter, MetadataLevel level)
    {
      if (level == MetadataLevel.NO_OVERLAYS || level == MetadataLevel.MINIMUM)
      {
        return;
      }

      // keep in mind that vertices are given relative to the center
      // point of the ROI and the transX/transY values are relative to
      // the center point of the image

      String roiID = MetadataTools.createLSID("ROI", roi);
      store.setImageROIRef(roiID, series, roiIndex);
      store.setROIID(roiID, roi);
      store.setLabelID(MetadataTools.createLSID("Shape", roi, 0), roi, 0);
      if (text == null) {
        text = name;
      }
      store.setLabelText(text, roi, 0);
      if (fontSize != null) {
        Double size = DataTools.parseDouble(fontSize);
        if (size != null) {
          Length fontSize = FormatTools.getFontSize(size.intValue());
          if (fontSize != null) {
            store.setLabelFontSize(fontSize, roi, 0);
          }
        }
      }
      Length l = new Length((double) linewidth, UNITS.PIXEL);
      store.setLabelStrokeWidth(l, roi, 0);

      if (!normalized) normalize();

      double cornerX = x.get(0).doubleValue();
      double cornerY = y.get(0).doubleValue();

      store.setLabelX(cornerX, roi, 0);
      store.setLabelY(cornerY, roi, 0);

      int centerX = (sizeX / 2) - 1;
      int centerY = (sizeY / 2) - 1;

      double roiX = centerX + transX;
      double roiY = centerY + transY;

      if (alternateCenter) {
        roiX = transX - 2 * cornerX;
        roiY = transY - 2 * cornerY;
      }

      // TODO : rotation not populated

      String shapeID = MetadataTools.createLSID("Shape", roi, 1);
      switch (type) {
        case POLYGON:
          final StringBuilder points = new StringBuilder();
          for (int i=0; i<x.size(); i++) {
            points.append(x.get(i).doubleValue() * scaleX + roiX);
            points.append(",");
            points.append(y.get(i).doubleValue() * scaleY + roiY);
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

      transX *= METER_MULTIPLY;
      transY *= METER_MULTIPLY;
      transX *= 1;
      transY *= 1;

      for (int i=0; i<x.size(); i++) {
        double coordinate = x.get(i).doubleValue() * METER_MULTIPLY;
        coordinate *= 1;
        x.set(i, coordinate);
      }

      for (int i=0; i<y.size(); i++) {
        double coordinate = y.get(i).doubleValue() * METER_MULTIPLY;
        coordinate *= 1;
        y.set(i, coordinate);
      }

      normalized = true;
    }
}