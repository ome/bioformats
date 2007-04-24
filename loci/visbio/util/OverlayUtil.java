//
// OverlayUtil.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.visbio.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.rmi.RemoteException;
import java.util.Arrays;
import loci.visbio.overlays.*;
import loci.visbio.view.TransformLink;
import visad.*;

/** Utility methods for Overlays */
public final class OverlayUtil {

  // -- Constants -- 

  /** Width of the selection layer beyond the object's boundaries */
  protected static final float GLOW_WIDTH = 1.0f;

  /** Alpha of the selection layer */
  protected static final float GLOW_ALPHA = 0.15f; 

  /** Color of the selection layer */
  protected static final Color GLOW_COLOR = Color.yellow;

  /** Color of the outline layer */
  protected static final Color OUTLINE_COLOR = Color.cyan;

  /** Computed (X, Y) pairs for top 1/2 of a unit circle. */
  protected static final float[][] ARC = arc();

  /** Computes the top 1/2 of a unit circle. */
  private static float[][] arc() {
    int res = 16; // resolution for 1/8 of circle
    float[][] arc = new float[2][4 * res];
    for (int i=0; i<res; i++) {
      float t = 0.5f * (i + 0.5f) / res;
      float x = (float) Math.sqrt(t);
      float y = (float) Math.sqrt(1 - t);

      arc[0][i] = -y;
      arc[1][i] = x;

      int i1 = 2 * res - i - 1;
      arc[0][i1] = -x;
      arc[1][i1] = y;

      int i2 = 2 * res + i;
      arc[0][i2] = x;
      arc[1][i2] = y;

      int i3 = 4 * res - i - 1;
      arc[0][i3] = y;
      arc[1][i3] = x;
    }
    return arc;
  }

  // -- Constructor --
  private OverlayUtil() { }

  // -- Selection Layer Utility Methods --

  /** Computes a type-specific selection layer for the given OverlayObject */ 
  public static DataImpl getSelectionLayer(OverlayObject obj, 
      TransformLink link, boolean outline) {
    DataImpl layer = null;
    if (!obj.hasData()) layer = null;
    else if (outline) {
      layer = getOutlineLayer(obj, link);
    }
    else {
      if (obj instanceof OverlayArrow) layer = getArrowLayer(obj, link);
      if (obj instanceof OverlayBox) layer = getBoxLayer(obj, link);
      if (obj instanceof OverlayLine) layer = getLineLayer(obj, link);
      if (obj instanceof OverlayMarker) layer = getMarkerLayer(obj, link);
      if (obj instanceof OverlayNodedObject) layer = getNodedLayer(obj, link);
      if (obj instanceof OverlayOval) layer = getOvalLayer(obj, link);
      if (obj instanceof OverlayText) layer = getTextLayer(obj, link);
    }
    return layer;
  }

  /** Computes selection layer for OverlayArrow objects */
  public static DataImpl getArrowLayer(OverlayObject obj, TransformLink link) {
    OverlayTransform overlay = (OverlayTransform) link.getTransform();
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float x1 = obj.getX();
    float x2 = obj.getX2();
    float y1 = obj.getY();
    float y2 = obj.getY2();

    // compute corners of arrow tail
    float padding = 0.02f * overlay.getScalingValue();
    double xx = x2 - x1;
    double yy = y2 - y1;
    double dist = Math.sqrt(xx * xx + yy * yy);
    double mult = padding / dist;
    float qx = (float) (mult * xx);
    float qy = (float) (mult * yy);

    // determine internal coordinate sys. basis vectors
    // assuming arrow is oriented like this:
    //
    // (x1, y1) <--------{{{ (x2, y2)
    //
    //
    double[] x = {xx / dist, yy / dist};
    double[] y = {-yy / dist, xx /dist};

    // arrow lengths:
    double a, b, c;
    a = Math.sqrt(qx * qx + qy * qy);
    b = dist;
    c = Math.sqrt(a * a + b * b);

    double scl = 1; // for now
    double d = GLOW_WIDTH * scl;

    // compute four corners of highlighted zone
    float c1x = (float) (x1 - x[0] * d + y[0] * (d * c) / b);
    float c1y = (float) (y1 - x[1] * d + y[1] * (d * c) / b);
    float c2x = (float) (x2 + x[0] * d + y[0] * (a + d * (a + c) / b));
    float c2y = (float) (y2 + x[1] * d + y[1] * (a + d * (a + c) / b));
    float c3x = (float) (x2 + x[0] * d - y[0] * (a + d * (a + c) / b));
    float c3y = (float) (y2 + x[1] * d - y[1] * (a + d * (a + c) / b));
    float c4x = (float) (x1 - x[0] * d - y[0] * (d * c) / b);
    float c4y = (float) (y1 - x[1] * d - y[1] * (d * c) / b);

    // order and combine the coordinates for a Gridded2DSet [2 2]
    float[][] setSamples = {
      {c1x, c2x, c4x, c3x},
      {c1y, c2y, c4y, c3y}
    };
    
    // construct range samples
    float r = GLOW_COLOR.getRed() / 255f;
    float g = GLOW_COLOR.getGreen() / 255f;
    float bl = GLOW_COLOR.getBlue() / 255f;
    
    float[][] rangeSamples = new float[4][setSamples[0].length];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], bl);
    Arrays.fill(rangeSamples[3], GLOW_ALPHA);

    // construct Field
    Gridded2DSet domainSet = null;
    FlatField field = null;
    try {
      domainSet = new Gridded2DSet(domain, setSamples,
        2, 2, null, null, null, false);
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, domainSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

  /** Computes selection layer for OverlayBox objects */
  public static DataImpl getBoxLayer(OverlayObject obj, TransformLink link) {
    OverlayTransform overlay = (OverlayTransform) link.getTransform();
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float delta = GLOW_WIDTH;
    float x1 = obj.getX();
    float x2 = obj.getX2();
    float y1 = obj.getY();
    float y2 = obj.getY2();

    // Determine orientation of (x1, y1) relative to (x2, y2)
    // and flip if need be.
    // I've set up the code in this section based on the 
    // supposition that the box is oriented like this:
    //
    // (x1, y1) +--------+
    //          |        |
    //          |        |
    //          +--------+ (x2, y2)
    //
    // which means x1 is supposed to be _less_ than x2, but inconsistently,
    // y1 is supposed to be _greater_ than y2.

    boolean flipX = x2 > x1;
    float xx1 = flipX ? x1 : x2;
    float xx2 = flipX ? x2 : x1;
    boolean flipY = y2 < y1;
    float yy1 = flipY ? y1 : y2;
    float yy2 = flipY ? y2 : y1;

    // just throw down a translucent rectangle over the box
    float[][] setSamples = {
      {xx1 - delta, xx2 + delta, xx1 - delta, xx2 + delta},
      {yy1 + delta, yy1 + delta, yy2 - delta, yy2 - delta}};
    
    // construct range samples
    float[][] rangeSamples = new float[4][4];
    float r = GLOW_COLOR.getRed() / 255f;
    float g = GLOW_COLOR.getGreen() / 255f;
    float b = GLOW_COLOR.getBlue() / 255f;
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);
    Arrays.fill(rangeSamples[3], GLOW_ALPHA);

    // construct field
    Gridded2DSet domainSet = null;
    FlatField field = null;
    try {
      domainSet = new Gridded2DSet(domain, setSamples, 2, 2,
        null, null, null, false);
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, domainSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }

    return field;
  }

  /** Returns a selection layer for OverlayLine objects */
  public static DataImpl getLineLayer(OverlayObject obj, TransformLink link) {
    OverlayTransform overlay = (OverlayTransform) link.getTransform();
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float x1 = obj.getX();
    float x2 = obj.getX2();
    float y1 = obj.getY();
    float y2 = obj.getY2();

    float delta = GLOW_WIDTH;

    // compute locations of grid points
    // (uses similar triangles instead of raw trig fcns)
    float x = x2 - x1;
    float y = y2 - y1;
    float hyp = (float) Math.sqrt(x * x + y * y);
    float ratio = delta / hyp;
    // offsets from endpoints of line segments 
    float dx1 = ratio * y; 
    float dy1 = ratio * x;
    float dx2 = ratio * x;
    float dy2 = ratio * y;

    float[] p1 = {x1 - dx1 - dx2, y1 + dy1 - dy2};
    float[] p2 = {x2 - dx1 + dx2, y2 + dy1 + dy2};
    float[] p3 = {x1 + dx1 - dx2, y1 - dy1 - dy2};
    float[] p4 = {x2 + dx1 + dx2, y2 - dy1 + dy2};

    float[][] setSamples = {{p1[0], p2[0], p3[0], p4[0]},
                            {p1[1], p2[1], p3[1], p4[1]}};

    // construct range samples;
    Color col = GLOW_COLOR;

    float r = col.getRed() / 255f;
    float g = col.getGreen() / 255f;
    float b = col.getBlue() / 255f;

    float[][] rangeSamples = new float[4][4];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);
    Arrays.fill(rangeSamples[3], GLOW_ALPHA);

    Gridded2DSet domainSet = null;
    FlatField field = null;
    try {
      domainSet = new Gridded2DSet(domain, setSamples,
            2, 2, null, null, null, false);
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, domainSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }

    return field;
  }
  
  /** Returns a selection layer for OverlayMarker objects */
  public static DataImpl getMarkerLayer(OverlayObject obj, TransformLink link) {
    OverlayTransform overlay = (OverlayTransform) link.getTransform();
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float x1 = obj.getX();
    float x2 = obj.getX2();
    float y1 = obj.getY();
    float y2 = obj.getY2();

    float size = 0.02f * overlay.getScalingValue();
    float delta = GLOW_WIDTH;

    float xx1 = x1 - size - delta;
    float xx2 = x1 + size + delta;
    float yy1 = y1 + size + delta;
    float yy2 = y1 - size - delta;

    float dx = 0.0001f; // TEMP

    SampledSet domainSet = null;
    int samplesLength = 4;
    if (2 * delta > size) {
      // return box
      float[][] setSamples;
      setSamples = new float[][]{
        {xx1, xx2, xx1, xx2},
        {yy1, yy1, yy2, yy2}
      };

      try {
        domainSet = new Gridded2DSet(domain, setSamples, setSamples[0].length /
            2, 2, null, null, null, false);
      }
      catch (VisADException ex) { ex.printStackTrace(); }
    }
    else {
      // return cross shape
      // using a UnionSet for now--couldn't get a single
      // Gridded2D set to appear as a cross
      float[][] setSamples1 = {
        {xx1, x1-delta, xx1, x1 - delta},
        {y1 + delta, y1 + delta, y1 - delta, y1 - delta}
      };

      float[][] setSamples2 = { 
        {x1 - delta, x1 + delta, x1 - delta, x1 + delta},
        {yy1, yy1, yy2, yy2}
      };

      float[][] setSamples3 = {
        {x1 + delta, xx2, x1 + delta, xx2},
        {y1 + delta, y1 + delta, y1 - delta, y1 - delta}
      };

      float[][][] setSamples = {setSamples1, setSamples2, setSamples3};

      samplesLength = 12;
      
      Gridded2DSet[] sets = new Gridded2DSet[3]; 
      try {
        for (int j=0; j<3; j++) {
          sets[j] = new Gridded2DSet(domain, setSamples[j], 2, 2, 
              null, null, null, false);
        }
        domainSet = new UnionSet(domain, sets);
      }
      catch (VisADException ex) { ex.printStackTrace(); }
      
      /*
      setSamples = new float[][]{
        {xx1, x1 - delta, x1 - delta + dx, x1 + delta - dx, x1 + delta, xx2,
          xx1, x1 - delta, x1 - delta + dx, x1 + delta - dx, x1 + delta, xx2},
        {y1 + delta, y1 + delta, yy1, yy1, y1 + delta, y1 + delta,
          y1 - delta, y1 - delta, yy2, yy2, y1 - delta, y1 - delta}
      };*/

      // Run this example by curtis:
      // Start with this:
      /*
      setSamples = new float[][] {
        {xx1, x1 - delta, 
          xx1, x1 - delta,},
        {y1 + delta, y1 + delta, 
          y1 - delta, y1 - delta,}
      };
      */

      // then try this:
      /*
      setSamples = new float[][] {
        {xx1, x1 - delta, x1 - delta + dx,
          xx1, x1 - delta, x1 -delta + dx},
        {y1 + delta, y1 + delta, y1 + size + delta, 
          y1 - delta, y1 - delta, y1 - size - delta}
      };
      */
      // I would expect the second one to look like a sideways 'T', but 
      // it looks like a triangle instead
    }

    // construct range samples
    float r = GLOW_COLOR.getRed() / 255f;
    float g = GLOW_COLOR.getGreen() / 255f;
    float b = GLOW_COLOR.getBlue() / 255f;

    float[][] rangeSamples = new float[4][samplesLength];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);
    Arrays.fill(rangeSamples[3], GLOW_ALPHA);

    // construct field
    FlatField field = null;
    try {
      FunctionType fieldType = new FunctionType (domain, range);
      field = new FlatField(fieldType, domainSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException ex) { ex.printStackTrace(); }
    catch (RemoteException ex) { ex.printStackTrace(); }
    return field;
  }

  /** Computes selection layer for OverlayNodedObject objects */
  public static DataImpl getNodedLayer(OverlayObject obj, TransformLink link) {
    OverlayTransform overlay = (OverlayTransform) link.getTransform();
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();
    OverlayNodedObject nobj = (OverlayNodedObject) obj;

    float delta = GLOW_WIDTH;
    float[][] nodes = nobj.getNodes();
    int numNodes = nobj.getNumNodes(); 

    /*
    // compute angle bisectors at each node
    for (int i=0; i<numNodes; i++) {
      if (i == 0){
      }
      if (i == numNodes - 1){
      }
      else {
        float[] v1 = {nodes[0][i] - nodes[0][i-1], nodes[1][i] - nodes[1][i-1]};
        float[] v2 = {nodes[0][i+1] - nodes[0][i], nodes[1][i+1] - nodes[1][i]};


    }*/

    Gridded2DSet[] segments = new Gridded2DSet[numNodes-1];
    for (int i=0; i<numNodes - 1; i++) {
      float[] v = new float[]{nodes[0][i+1] - nodes[0][i], nodes[1][i+1] -
          nodes[1][i]};
      // angle of vector perpendicular to line
      double theta =  Math.PI / 2 + Math.atan2(v[1], v[0]); 

      float dx = (float) (delta * Math.cos(theta));
      float dy = (float) (delta * Math.sin(theta));

      float[] p1 = {nodes[0][i] + dx, nodes[1][i] + dy};
      float[] p2 = {nodes[0][i+1] + dx, nodes[1][i+1] + dy};
      float[] p3 = {nodes[0][i] - dx, nodes[1][i] - dy};
      float[] p4 = {nodes[0][i+1] - dx, nodes[1][i+1] - dy};

      float[][] setSamples = {{p1[0], p2[0], p3[0], p4[0]},
                              {p1[1], p2[1], p3[1], p4[1]}};

      try {
        segments[i] = new Gridded2DSet(domain, setSamples,
            2, 2, null, null, null, false);
      }

      catch (VisADException exc) { exc.printStackTrace(); }
    }

    // construct range samples;
    Color col = GLOW_COLOR;
    float r = col.getRed() / 255f;
    float g = col.getGreen() / 255f;
    float b = col.getBlue() / 255f;

    float[][] rangeSamples = new float[4][4*(numNodes-1)];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);
    Arrays.fill(rangeSamples[3], GLOW_ALPHA);

    FlatField field = null;
    UnionSet fieldSet = null;
    try {
      fieldSet = new UnionSet (domain, segments);

      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }

    return field;
  }
  
  /** Computes a selection layer for OverlayOval objects */
  public static DataImpl getOvalLayer(OverlayObject obj, TransformLink link) {
    OverlayTransform overlay = (OverlayTransform) link.getTransform();
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float x1 = obj.getX();
    float x2 = obj.getX2();
    float y1 = obj.getY();
    float y2 = obj.getY2();

    float cx = (x1 + x2) / 2;
    float cy = (y1 + y2) / 2;
    float rrx = cx > x1 ? cx - x1 : cx - x2;
    float rry = cy > y1 ? cy - y1 : cy - y2;

    float scl = 1; // for now
    float rx = rrx + GLOW_WIDTH * scl;
    float ry = rry + GLOW_WIDTH * scl;

    int arcLen = ARC[0].length;
    int len = 2 * arcLen;
    float[][] setSamples = new float[2][len];

    // top half of circle
    for (int i=0; i<arcLen; i++) {
      setSamples[0][i] = cx + rx * ARC[0][i];
      setSamples[1][i] = cy + ry * ARC[1][i];
    }

    // bottom half of circle
    for (int i=0; i<arcLen; i++) {
      int ndx = arcLen + i;
      setSamples[0][ndx] = cx + rx * ARC[0][i];
      setSamples[1][ndx] = cy - ry * ARC[1][i];
    }

    // construct range samples
    float r = GLOW_COLOR.getRed() / 255f;
    float g = GLOW_COLOR.getGreen() / 255f;
    float b = GLOW_COLOR.getBlue() / 255f;

    float[][] rangeSamples = new float[4][setSamples[0].length];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);
    Arrays.fill(rangeSamples[3], GLOW_ALPHA);

    GriddedSet fieldSet = null;
    FlatField field = null;
    try {
      fieldSet = new Gridded2DSet(domain,
        setSamples, arcLen, 2, null, null, null, false);

      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

  /** Computes a selection layer for OverlayText objects */
  public static DataImpl getTextLayer(OverlayObject obj, TransformLink link) {
    OverlayTransform overlay = (OverlayTransform) link.getTransform();
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float[][] corners = computeTextOutline((OverlayText) obj, overlay);

    float[][] setSamples = {
      {corners[0][0], corners[1][0], corners[0][0], corners[1][0]},
      {corners[0][1], corners[0][1], corners[1][1], corners[1][1]},
    };

    // construct range samples
    float r = GLOW_COLOR.getRed() / 255f;
    float g = GLOW_COLOR.getGreen() / 255f;
    float b = GLOW_COLOR.getBlue() / 255f;

    float[][] rangeSamples = new float[4][setSamples[0].length];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);
    Arrays.fill(rangeSamples[3], GLOW_ALPHA);

    FlatField field = null;
    try {
      Gridded2DSet domainSet = new Gridded2DSet(domain, setSamples, 2, 2,
          null, null, null, false);
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, domainSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException ex) { ex.printStackTrace(); }
    catch (RemoteException ex) { ex.printStackTrace(); }
    return field;
  }

  /** Computes a standard outline layer for an OverlayObject */
  public static DataImpl getOutlineLayer(OverlayObject obj, TransformLink link)   {
    OverlayTransform overlay = (OverlayTransform) link.getTransform();
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float x1 = obj.getX();
    float x2 = obj.getX2();
    float y1 = obj.getY();
    float y2 = obj.getY2();

    float[][] cnrs = computeOutline(obj, overlay);

    float[][] setSamples = null;
    GriddedSet fieldSet = null;
    try {
      setSamples = new float[][] {
        {cnrs[0][0], cnrs[1][0], cnrs[1][0], cnrs[0][0], cnrs[0][0]},
        {cnrs[0][1], cnrs[0][1], cnrs[1][1], cnrs[1][1], cnrs[0][1]}
      };
      fieldSet = new Gridded2DSet(domain,
        setSamples, setSamples[0].length, null, null, null, false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }

    Color col = OUTLINE_COLOR; 
    float r = col.getRed() / 255f;
    float g = col.getGreen() / 255f;
    float b = col.getBlue() / 255f;

    float[][] rangeSamples = new float[4][setSamples[0].length];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);
    Arrays.fill(rangeSamples[3], 1.0f);

    FlatField field = null;
    try {
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

  // -- Helper Methods --

  // Note: Both of these methods are basically the old 
  // OverlayObject.computeGridParameters()
  // methods, the first from OverlayText, the second from OverlayBox.

  /** Computes the corners of an OverlayText object's outline */
  private static float[][] computeTextOutline(OverlayText obj, 
      OverlayTransform overlay) {
    // Computing the grid for text overlays is difficult because the size of
    // the overlay depends on the font metrics, which are obtained from an AWT
    // component (in this case, window.getDisplay().getComponent() for a
    // display window), but data transforms have no knowledge of which display
    // windows are currently displaying them.

    float x1 = obj.getX();
    float x2 = obj.getX2();
    float y1 = obj.getY();
    float y2 = obj.getY2();

    // HACK - for now, use this harebrained scheme to estimate the bounds
    int sx = overlay.getScalingValueX();
    int sy = overlay.getScalingValueY();
    float mw = sx / 318f, mh = sy / 640f; // obtained through experimentation
    FontMetrics fm = overlay.getFontMetrics();
    x2 = x1 + mw * fm.stringWidth(obj.getText());
    y2 = y1 + mh * fm.getHeight();

    float padx = 0.02f * sx;
    float pady = 0.02f * sy;
    float xx1 = x1 - padx;
    float xx2 = x2 + padx;
    float yy1 = y1 - pady;
    float yy2 = y2 + pady;

    return new float[][]{{xx1, yy1}, {xx2, yy2}};
  }

  /** Computes corners of an OverlayObject's outline */
  private static float[][] computeOutline(OverlayObject obj,
     OverlayTransform overlay) { 
    if (obj instanceof OverlayText) return computeTextOutline(
        (OverlayText) obj, overlay);

    float x1 = obj.getX();
    float x2 = obj.getX2();
    float y1 = obj.getY();
    float y2 = obj.getY2();

    float padding = 0.02f * overlay.getScalingValue();
    boolean flipX = x2 < x1;
    float xx1 = flipX ? (x1 + padding) : (x1 - padding);
    float xx2 = flipX ? (x2 - padding) : (x2 + padding);
    boolean flipY = y2 < y1;
    float yy1 = flipY ? (y1 + padding) : (y1 - padding);
    float yy2 = flipY ? (y2 - padding) : (y2 + padding);

    return new float[][]{{x1, y1}, {x2, y2}};
  }
}
