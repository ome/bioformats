/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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

package loci.plugins.util;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.TextRoi;
import ij.plugin.frame.RoiManager;

import java.awt.Color;
import java.awt.Rectangle;

import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;

import ome.xml.model.Ellipse;
import ome.xml.model.Image;
import ome.xml.model.OME;
import ome.xml.model.Point;
import ome.xml.model.Polygon;
import ome.xml.model.Polyline;
import ome.xml.model.Shape;
import ome.xml.model.Union;

// TODO: Stored ROIs are not correctly linked to Image.

/**
 * Utility class for managing regions of interest within ImageJ.
 * Capable of constructing ROIs within ImageJ's ROI manager matching
 * those specified in an OME metadata store, and vice versa.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats-plugins/src/loci/plugins/util/ROIHandler.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats-plugins/src/loci/plugins/util/ROIHandler.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ROIHandler {

  // -- ROIHandler API methods --

  /**
   * Look for ROIs in the given OMEXMLMetadata; if any are present, apply
   * them to the given images and display them in the ROI manager.
   */
  public static void openROIs(IMetadata retrieve, ImagePlus[] images) {
    if (!(retrieve instanceof OMEXMLMetadata)) return;
    int nextRoi = 0;
    RoiManager manager = RoiManager.getInstance();

    OME root = (OME) retrieve.getRoot();

    int imageCount = images.length;
    for (int imageNum=0; imageNum<imageCount; imageNum++) {
      int roiCount = root.sizeOfROIList();
      if (roiCount > 0 && manager == null) {
        manager = new RoiManager();
      }
      for (int roiNum=0; roiNum<roiCount; roiNum++) {
        Union shapeSet = root.getROI(roiNum).getUnion();
        int shapeCount = shapeSet.sizeOfShapeList();

        for (int shape=0; shape<shapeCount; shape++) {
          Shape shapeObject = shapeSet.getShape(shape);

          Roi roi = null;

          if (shapeObject instanceof Ellipse) {
            Ellipse ellipse = (Ellipse) shapeObject;
            int cx = ellipse.getX().intValue();
            int cy = ellipse.getY().intValue();
            int rx = ellipse.getRadiusX().intValue();
            int ry = ellipse.getRadiusY().intValue();
            roi = new OvalRoi(cx - rx, cy - ry, rx * 2, ry * 2);
          }
          else if (shapeObject instanceof ome.xml.model.Line) {
            ome.xml.model.Line line = (ome.xml.model.Line) shapeObject;
            int x1 = line.getX1().intValue();
            int x2 = line.getX2().intValue();
            int y1 = line.getY1().intValue();
            int y2 = line.getY2().intValue();
            roi = new Line(x1, y1, x2, y2);
          }
          else if (shapeObject instanceof Point) {
            Point point = (Point) shapeObject;
            int x = point.getX().intValue();
            int y = point.getY().intValue();
            roi = new OvalRoi(x, y, 0, 0);
          }
          else if (shapeObject instanceof Polyline) {
            Polyline polyline = (Polyline) shapeObject;
            String points = polyline.getPoints();
            int[][] coordinates = parsePoints(points);
            roi = new PolygonRoi(coordinates[0], coordinates[1],
              coordinates[0].length, Roi.POLYLINE);
          }
          else if (shapeObject instanceof Polygon) {
            Polygon polygon = (Polygon) shapeObject;
            String points = polygon.getPoints();
            int[][] coordinates = parsePoints(points);
            roi = new PolygonRoi(coordinates[0], coordinates[1],
              coordinates[0].length, Roi.POLYGON);
          }
          else if (shapeObject instanceof ome.xml.model.Rectangle) {
            ome.xml.model.Rectangle rectangle =
              (ome.xml.model.Rectangle) shapeObject;
            int x = rectangle.getX().intValue();
            int y = rectangle.getY().intValue();
            int w = rectangle.getWidth().intValue();
            int h = rectangle.getHeight().intValue();
            String label = shapeObject.getText();
            if (label != null) {
              roi = new TextRoi(x, y, label);
            }
            else {
              roi = new Roi(x, y, w, h);
            }
          }

          if (roi != null) {
            Roi.setColor(Color.WHITE);
            roi.setImage(images[imageNum]);
            manager.add(images[imageNum], roi, nextRoi++);
          }
        }
      }
    }
  }

  /** Save ROIs in the ROI manager to the given MetadataStore. */
  public static void saveROIs(MetadataStore store) {
    RoiManager manager = RoiManager.getInstance();
    if (manager == null) return;

    Roi[] rois = manager.getRoisAsArray();
    for (int i=0; i<rois.length; i++) {
      if (rois[i] instanceof Line) {
        storeLine((Line) rois[i], store, i, 0);
      }
      else if (rois[i] instanceof PolygonRoi) {
        storePolygon((PolygonRoi) rois[i], store, i, 0);
      }
      else if (rois[i] instanceof ShapeRoi) {
        Roi[] subRois = ((ShapeRoi) rois[i]).getRois();
        for (int q=0; q<subRois.length; q++) {
          if (subRois[q] instanceof Line) {
            storeLine((Line) subRois[q], store, i, q);
          }
          else if (subRois[q] instanceof PolygonRoi) {
            storePolygon((PolygonRoi) subRois[q], store, i, q);
          }
          else if (subRois[q] instanceof OvalRoi) {
            storeOval((OvalRoi) subRois[q], store, i, q);
          }
          else storeRectangle(subRois[q], store, i, q);
        }
      }
      else if (rois[i] instanceof OvalRoi) {
        storeOval((OvalRoi) rois[i], store, i, 0);
      }
      else storeRectangle(rois[i], store, i, 0);
    }
  }

  // -- Helper methods --

  /** Store a Line ROI in the given MetadataStore. */
  private static void storeLine(Line roi, MetadataStore store,
    int roiNum, int shape)
  {
    store.setLineX1(new Double(roi.x1), roiNum, shape);
    store.setLineX2(new Double(roi.x2), roiNum, shape);
    store.setLineY1(new Double(roi.y1), roiNum, shape);
    store.setLineY2(new Double(roi.y2), roiNum, shape);
  }

  /** Store an Roi (rectangle) in the given MetadataStore. */
  private static void storeRectangle(Roi roi, MetadataStore store,
    int roiNum, int shape)
  {
    Rectangle bounds = roi.getBounds();
    store.setRectangleX(new Double(bounds.x), roiNum, shape);
    store.setRectangleY(new Double(bounds.y), roiNum, shape);
    store.setRectangleWidth(new Double(bounds.width), roiNum, shape);
    store.setRectangleHeight(new Double(bounds.height), roiNum, shape);
  }

  /** Store a Polygon ROI in the given MetadataStore. */
  private static void storePolygon(PolygonRoi roi, MetadataStore store,
    int roiNum, int shape)
  {
    Rectangle bounds = roi.getBounds();
    int[] xCoordinates = roi.getXCoordinates();
    int[] yCoordinates = roi.getYCoordinates();
    StringBuffer points = new StringBuffer();
    for (int i=0; i<xCoordinates.length; i++) {
      points.append(xCoordinates[i] + bounds.x);
      points.append(",");
      points.append(yCoordinates[i] + bounds.y);
      if (i < xCoordinates.length - 1) points.append(" ");
    }
    store.setPolygonPoints(points.toString(), roiNum, shape);
  }

  /** Store an Oval ROI in the given MetadataStore. */
  @SuppressWarnings("unused")
  private static void storeOval(OvalRoi roi,
    MetadataStore store, int roiNum, int shape)
  {
    // TODO: storeOval
  }

  /**
   * Parse (x, y) coordinates from a String returned by
   * MetadataRetrieve.getPolygonpoints(...) or
   * MetadataRetrieve.getPolylinepoints(...)
   */
  private static int[][] parsePoints(String points) {
    // assuming points are stored like this:
    // x0,y0 x1,y1 x2,y2 ...
    String[] pointList = points.split(" ");
    int[][] coordinates = new int[2][pointList.length];

    for (int q=0; q<pointList.length; q++) {
      pointList[q] = pointList[q].trim();
      int delim = pointList[q].indexOf(",");
      coordinates[0][q] =
        (int) Double.parseDouble(pointList[q].substring(0, delim));
      coordinates[1][q] =
        (int) Double.parseDouble(pointList[q].substring(delim + 1));
    }
    return coordinates;
  }

}
