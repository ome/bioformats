//
// ROIHandler.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins.util;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.frame.RoiManager;

import java.awt.Color;
import java.awt.Rectangle;

import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;

/**
 * Utility class for managing regions of interest within ImageJ.
 * Capable of constructing ROIs within ImageJ's ROI manager matching
 * those specified in an OME metadata store, and vice versa.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/util/ROIHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/util/ROIHandler.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class ROIHandler {

  // -- ROIHandler API methods --

  /**
   * Look for ROIs in the given MetadataRetrieve; if any are present, apply
   * them to the given images and display them in the ROI manager.
   */
  public static void openROIs(MetadataRetrieve retrieve, ImagePlus[] images) {
    int nextRoi = 0;
    RoiManager manager = RoiManager.getInstance();

    int imageCount = retrieve.getImageCount();
    for (int image=0; image<imageCount; image++) {
      int roiCount = retrieve.getROICount(image);
      if (roiCount > 0 && manager == null) {
        manager = new RoiManager();
      }
      for (int roiNum=0; roiNum<roiCount; roiNum++) {
        int shapeCount = retrieve.getShapeCount(image, roiNum);

        for (int shape=0; shape<shapeCount; shape++) {
          // determine the ROI type
          String circle = retrieve.getCircleCx(image, roiNum, shape);
          String ellipse = retrieve.getEllipseCx(image, roiNum, shape);
          String line = retrieve.getLineX1(image, roiNum, shape);
          String point = retrieve.getPointCx(image, roiNum, shape);
          String polygon = retrieve.getPolygonPoints(image, roiNum, shape);
          String polyline = retrieve.getPolylinePoints(image, roiNum, shape);
          String rectangle = retrieve.getRectX(image, roiNum, shape);

          Roi roi = null;

          if (circle != null) {
            int cx = (int) Double.parseDouble(retrieve.getCircleCx(
              image, roiNum, shape));
            int cy = (int) Double.parseDouble(retrieve.getCircleCy(
              image, roiNum, shape));
            int r = (int) Double.parseDouble(retrieve.getCircleR(
              image, roiNum, shape));
            roi = new OvalRoi(cx - r, cy - r, r * 2, r * 2);
          }
          else if (ellipse != null) {
            int cx = (int) Double.parseDouble(retrieve.getEllipseCx(
              image, roiNum, shape));
            int cy = (int) Double.parseDouble(retrieve.getEllipseCy(
              image, roiNum, shape));
            int rx = (int) Double.parseDouble(retrieve.getEllipseRx(
              image, roiNum, shape));
            int ry = (int) Double.parseDouble(retrieve.getEllipseRy(
              image, roiNum, shape));
            roi = new OvalRoi(cx - rx, cy - ry, rx * 2, ry * 2);
          }
          else if (line != null) {
            int x1 = (int) Double.parseDouble(retrieve.getLineX1(
              image, roiNum, shape));
            int x2 = (int) Double.parseDouble(retrieve.getLineX2(
              image, roiNum, shape));
            int y1 = (int) Double.parseDouble(retrieve.getLineY1(
              image, roiNum, shape));
            int y2 = (int) Double.parseDouble(retrieve.getLineY2(
              image, roiNum, shape));
            roi = new Line(x1, y1, x2, y2);
          }
          else if (point != null) {
            int x = (int) Double.parseDouble(retrieve.getPointCx(
              image, roiNum, shape));
            int y = (int) Double.parseDouble(retrieve.getPointCy(
              image, roiNum, shape));
            roi = new OvalRoi(x, y, 0, 0);
          }
          else if (polygon != null) {
            String points = retrieve.getPolygonPoints(image, roiNum, shape);
            int[][] coordinates = parsePoints(points);
            roi = new PolygonRoi(coordinates[0], coordinates[1],
              coordinates[0].length, Roi.POLYGON);
          }
          else if (polyline != null) {
            String points = retrieve.getPolylinePoints(image, roiNum, shape);
            int[][] coordinates = parsePoints(points);
            roi = new PolygonRoi(coordinates[0], coordinates[1],
              coordinates[0].length, Roi.POLYLINE);
          }
          else if (rectangle != null) {
            int x =
              (int) Double.parseDouble(retrieve.getRectX(image, roiNum, shape));
            int y =
              (int) Double.parseDouble(retrieve.getRectY(image, roiNum, shape));
            int w = (int) Double.parseDouble(retrieve.getRectWidth(
              image, roiNum, shape));
            int h = (int) Double.parseDouble(retrieve.getRectHeight(
              image, roiNum, shape));
            roi = new Roi(x, y, w, h);
          }

          if (roi != null) {
            Roi.setColor(Color.WHITE);
            roi.setImage(images[image]);
            manager.add(images[image], roi, nextRoi++);
          }
        }
      }
    }
  }

  /** Save ROIs in the ROI manager to the given MetadataStore. */
  public static void saveROIs(MetadataStore store, int imageIndex) {
    RoiManager manager = RoiManager.getInstance();
    if (manager == null) return;

    Roi[] rois = manager.getRoisAsArray();
    for (int i=0; i<rois.length; i++) {
      if (rois[i] instanceof Line) {
        storeLine((Line) rois[i], store, imageIndex, i, 0);
      }
      else if (rois[i] instanceof PolygonRoi) {
        storePolygon((PolygonRoi) rois[i], store, imageIndex, i, 0);
      }
      else if (rois[i] instanceof ShapeRoi) {
        Roi[] subRois = ((ShapeRoi) rois[i]).getRois();
        for (int q=0; q<subRois.length; q++) {
          if (subRois[q] instanceof Line) {
            storeLine((Line) subRois[q], store, imageIndex, i, q);
          }
          else if (subRois[q] instanceof PolygonRoi) {
            storePolygon((PolygonRoi) subRois[q], store, imageIndex, i, q);
          }
          else if (subRois[q] instanceof OvalRoi) {
            storeOval((OvalRoi) subRois[q], store, imageIndex, i, q);
          }
          else storeRectangle(subRois[q], store, imageIndex, i, q);
        }
      }
      else if (rois[i] instanceof OvalRoi) {
        storeOval((OvalRoi) rois[i], store, imageIndex, i, 0);
      }
      else storeRectangle(rois[i], store, imageIndex, i, 0);
    }
  }

  // -- Helper methods --

  /** Store a Line ROI in the given MetadataStore. */
  private static void storeLine(Line roi, MetadataStore store, int image,
    int roiNum, int shape)
  {
    store.setLineX1(String.valueOf(roi.x1), image, roiNum, shape);
    store.setLineX2(String.valueOf(roi.x2), image, roiNum, shape);
    store.setLineY1(String.valueOf(roi.y1), image, roiNum, shape);
    store.setLineY2(String.valueOf(roi.y2), image, roiNum, shape);
  }

  /** Store an Roi (rectangle) in the given MetadataStore. */
  private static void storeRectangle(Roi roi, MetadataStore store, int image,
    int roiNum, int shape)
  {
    Rectangle bounds = roi.getBounds();
    store.setRectX(String.valueOf(bounds.x), image, roiNum, shape);
    store.setRectY(String.valueOf(bounds.y), image, roiNum, shape);
    store.setRectWidth(String.valueOf(bounds.width), image, roiNum, shape);
    store.setRectHeight(String.valueOf(bounds.height), image, roiNum, shape);
  }

  /** Store a Polygon ROI in the given MetadataStore. */
  private static void storePolygon(PolygonRoi roi, MetadataStore store,
    int image, int roiNum, int shape)
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
    store.setPolygonPoints(points.toString(), image, roiNum, shape);
  }

  /** Store an Oval ROI in the given MetadataStore. */
  private static void storeOval(OvalRoi roi, MetadataStore store, int image,
    int roiNum, int shape)
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
