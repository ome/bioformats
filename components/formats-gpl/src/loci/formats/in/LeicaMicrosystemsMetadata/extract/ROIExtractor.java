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

package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import loci.common.DataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ROI;

/**
 * ROIExtractor is a helper class for extracting ROI information from LMS XML files.
 * 
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class ROIExtractor extends Extractor {
  
   /**
    * Returns a list of {@link ROI}s extracted from an image node
    */
  public static List<ROI> translateROIs(Element imageNode, double physicalSizeX, double physicalSizeY) {
    List<ROI> imageROIs = new ArrayList<ROI>();

    NodeList rois = getDescendantNodesWithName(imageNode, "Annotation");
    if (rois == null)
      return imageROIs;

    for (int r = 0; r < rois.getLength(); r++) {
      Element roiNode = (Element) rois.item(r);

      ROI roi = new ROI();

      String type = roiNode.getAttribute("type");
      if (type != null && !type.trim().isEmpty()) {
        roi.type = Integer.parseInt(type.trim());
      }
      String color = roiNode.getAttribute("color");
      if (color != null && !color.trim().isEmpty()) {
        roi.color = Long.parseLong(color.trim());
      }
      roi.name = roiNode.getAttribute("name");
      roi.fontName = roiNode.getAttribute("fontName");
      roi.fontSize = roiNode.getAttribute("fontSize");

      Double transX = DataTools.parseDouble(roiNode.getAttribute("transTransX"));
      if (transX != null) {
        roi.transX = transX / physicalSizeX;
      }
      Double transY = DataTools.parseDouble(roiNode.getAttribute("transTransY"));
      if (transY != null) {
        roi.transY = transY / physicalSizeY;
      }
      transX = DataTools.parseDouble(roiNode.getAttribute("transScalingX"));
      if (transX != null) {
        roi.scaleX = transX / physicalSizeX;
      }
      transY = DataTools.parseDouble(roiNode.getAttribute("transScalingY"));
      if (transY != null) {
        roi.scaleY = transY / physicalSizeY;
      }
      Double rotation = DataTools.parseDouble(roiNode.getAttribute("transRotation"));
      if (rotation != null) {
        roi.rotation = rotation;
      }
      String linewidth = roiNode.getAttribute("linewidth");
      try {
        if (linewidth != null && !linewidth.trim().isEmpty()) {
          roi.linewidth = Integer.parseInt(linewidth.trim());
        }
      } catch (NumberFormatException e) {
      }

      roi.text = roiNode.getAttribute("text");

      NodeList vertices = getDescendantNodesWithName(roiNode, "Vertex");
      if (vertices == null) {
        continue;
      }

      for (int v = 0; v < vertices.getLength(); v++) {
        Element vertex = (Element) vertices.item(v);
        String xx = vertex.getAttribute("x");
        String yy = vertex.getAttribute("y");

        if (xx != null && !xx.trim().isEmpty()) {
          roi.x.add(DataTools.parseDouble(xx.trim()));
        }
        if (yy != null && !yy.trim().isEmpty()) {
          roi.y.add(DataTools.parseDouble(yy.trim()));
        }
      }
      imageROIs.add(roi);
    }

    return imageROIs;
  }

  /**
   * Returns a list of "single" {@link ROI}s extracted from an image node 
   */
  public static List<ROI> translateSingleROIs(Element imageNode, double physicalSizeX, double physicalSizeY) {
    List<ROI> imageROIs = new ArrayList<ROI>();

    NodeList children = getDescendantNodesWithName(imageNode, "ROI");
    if (children == null)
      return imageROIs;
    children = getDescendantNodesWithName((Element) children.item(0), "Children");
    if (children == null)
      return imageROIs;
    children = getDescendantNodesWithName((Element) children.item(0), "Element");
    if (children == null)
      return imageROIs;

    for (int r = 0; r < children.getLength(); r++) {
      NodeList rois = getDescendantNodesWithName((Element) children.item(r), "ROISingle");

      Element roiNode = (Element) rois.item(0);
      ROI roi = new ROI();

      String type = roiNode.getAttribute("RoiType");
      if (type != null && !type.trim().isEmpty()) {
        roi.type = Integer.parseInt(type.trim());
      }
      String color = roiNode.getAttribute("Color");
      if (color != null && !color.trim().isEmpty()) {
        roi.color = Long.parseLong(color.trim());
      }
      Element parent = (Element) roiNode.getParentNode();
      parent = (Element) parent.getParentNode();
      roi.name = parent.getAttribute("Name");

      NodeList vertices = getDescendantNodesWithName(roiNode, "P");

      for (int v = 0; v < vertices.getLength(); v++) {
        Element vertex = (Element) vertices.item(v);
        String xx = vertex.getAttribute("X");
        String yy = vertex.getAttribute("Y");

        if (xx != null && !xx.trim().isEmpty()) {
          Double x = DataTools.parseDouble(xx.trim());
          if (x != null) {
            roi.x.add(x / physicalSizeX);
          }
        }
        if (yy != null && !yy.trim().isEmpty()) {
          Double y = DataTools.parseDouble(yy.trim());
          if (y != null) {
            roi.y.add(y / physicalSizeY);
          }
        }
      }

      Element transform = (Element) getDescendantNodesWithName(roiNode, "Transformation").item(0);

      Double rotation = DataTools.parseDouble(transform.getAttribute("Rotation"));
      if (rotation != null) {
        roi.rotation = rotation;
      }

      Element scaling = (Element) getDescendantNodesWithName(transform, "Scaling").item(0);
      Double scaleX = DataTools.parseDouble(scaling.getAttribute("XScale"));
      Double scaleY = DataTools.parseDouble(scaling.getAttribute("YScale"));
      if (scaleX != null) {
        roi.scaleX = scaleX;
      }
      if (scaleY != null) {
        roi.scaleY = scaleY;
      }

      Element translation = (Element) getDescendantNodesWithName(transform, "Translation").item(0);
      Double transX = DataTools.parseDouble(translation.getAttribute("X"));
      Double transY = DataTools.parseDouble(translation.getAttribute("Y"));
      if (transX != null) {
        roi.transX = transX / physicalSizeX;
      }
      if (transY != null) {
        roi.transY = transY / physicalSizeY;
      }

      imageROIs.add(roi);
    }

    return imageROIs;
  }
}
