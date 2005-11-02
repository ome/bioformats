//
// ArbitrarySlice.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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

package loci.visbio.data;

import java.rmi.RemoteException;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import loci.visbio.state.Dynamic;
import loci.visbio.state.SaveException;
import loci.visbio.util.DataUtil;
import loci.visbio.util.XMLUtil;
import org.w3c.dom.Element;
import visad.*;

/** A transform for slicing a stack of images in 3D. */
public class ArbitrarySlice extends DataTransform
  implements TransformListener
{

  // -- Constants --

  private static final double RADIUS3 = Math.sqrt(3);
  private static final double RADIUS6 = Math.sqrt(6);
  private static final double THETA1 = 0;
  private static final double THETA2 = Math.PI / 2;
  private static final double THETA3 = 3 * Math.PI / 2;
  private static final double THETA4 = Math.PI;
  private static final float T1COS = (float) (RADIUS6 * Math.cos(THETA1));
  private static final float T1SIN = (float) (RADIUS6 * Math.sin(THETA1));
  private static final float T2COS = (float) (RADIUS6 * Math.cos(THETA2));
  private static final float T2SIN = (float) (RADIUS6 * Math.sin(THETA2));
  private static final float T3COS = (float) (RADIUS6 * Math.cos(THETA3));
  private static final float T3SIN = (float) (RADIUS6 * Math.sin(THETA3));
  private static final float T4COS = (float) (RADIUS6 * Math.cos(THETA4));
  private static final float T4SIN = (float) (RADIUS6 * Math.sin(THETA4));


  // -- Fields --

  /** Dimensional axis to slice through. */
  protected int axis;

  /** Horizontal rotational angle of slicing line. */
  protected float yaw;

  /** Vertical rotatational angle of slicing line. */
  protected float pitch;

  /** Arbitrary slice's location along slicing line. */
  protected float loc;

  /** Resolution of arbitrary slice. */
  protected int res;

  /** Flag indicating whether slicing line should be shown. */
  protected boolean showLine;

  /** Flag indicating whether arbitrary slice should actually be computed. */
  protected boolean compute;

  /** Controls for the arbitrary slice. */
  protected SliceWidget controls;


  // -- Constructor --

  /** Creates an uninitialized arbitrary slice. */
  public ArbitrarySlice() { }

  /** Creates an overlay object for the given transform. */
  public ArbitrarySlice(DataTransform parent, String name) {
    super(parent, name);
    initState(null);
    parent.addTransformListener(this);
  }


  // -- ArbitrarySlice API methods --

  /**
   * Sets the parameters for the arbitrary slice,
   * recomputing the slice if the compute flag is set.
   */
  public synchronized void setParameters(int axis, float yaw, float pitch,
    float loc, int res, boolean showLine, boolean compute)
  {
    if (this.axis != axis) {
      this.axis = axis;
      computeLengths();
    }
    if (this.yaw == yaw && this.pitch == pitch && this.loc == loc &&
      this.res == res && this.showLine == showLine && this.compute == compute)
    {
      return;
    }
    this.yaw = yaw;
    this.pitch = pitch;
    this.loc = loc;
    this.res = res;
    this.showLine = showLine;
    this.compute = compute;
    controls.refreshWidget();
    notifyListeners(new TransformEvent(this));
  }

  /** Sets the axis through which to slice. */
  public void setAxis(int axis) {
    setParameters(axis, yaw, pitch, loc, res, showLine, compute);
  }

  /** Sets the yaw for the slicing line. */
  public void setYaw(float yaw) {
    setParameters(axis, yaw, pitch, loc, res, showLine, compute);
  }

  /** Sets the pitch for the slicing line. */
  public void setPitch(float pitch) {
    setParameters(axis, yaw, pitch, loc, res, showLine, compute);
  }

  /** Sets the location for the arbitrary slice. */
  public void setLocation(float loc) {
    setParameters(axis, yaw, pitch, loc, res, showLine, compute);
  }

  /** Sets the resolution for the slicing line. */
  public void setResolution(int res) {
    setParameters(axis, yaw, pitch, loc, res, showLine, compute);
  }

  /** Sets whether white line is shown. */
  public void setLineVisible(boolean showLine) {
    setParameters(axis, yaw, pitch, loc, res, showLine, compute);
  }

  /** Sets whether arbitrary slice is computed. */
  public void setSliceComputed(boolean compute) {
    setParameters(axis, yaw, pitch, loc, res, showLine, compute);
  }

  /** Gets the axis through which to slice. */
  public int getAxis() { return axis; }

  /** Gets the yaw for the slicing line. */
  public float getYaw() { return yaw; }

  /** Gets the pitch for the slicing line. */
  public float getPitch() { return pitch; }

  /** Gets the location for the arbitrary slice. */
  public float getLocation() { return loc; }

  /** Gets the resolution for the slicing line. */
  public int getResolution() { return res; }

  /** Gets whether slicing line is shown. */
  public boolean isLineVisible() { return showLine; }

  /** Gets whether arbitary slice is computed. */
  public boolean isSliceComputed() { return compute; }


  // -- Static DataTransform API methods --

  /** Creates a new set of overlays, with user interaction. */
  public static DataTransform makeTransform(DataManager dm) {
    DataTransform dt = dm.getSelectedData();
    if (!isValidParent(dt)) return null;
    String n = (String) JOptionPane.showInputDialog(dm.getControls(),
      "Title of slice:", "Create arbitrary slice",
      JOptionPane.INFORMATION_MESSAGE, null, null, dt.getName() + " slice");
    if (n == null) return null;
    return new ArbitrarySlice(dt, n);
  }

  /**
   * Indicates whether this transform type would accept
   * the given transform as its parent transform.
   */
  public static boolean isValidParent(DataTransform data) {
    return data != null && data instanceof ImageTransform &&
      data.getLengths().length > 0;
  }

  /** Indicates whether this transform type requires a parent transform. */
  public static boolean isParentRequired() { return true; }


  // -- DataTransform API methods --

  /**
   * Retrieves the data corresponding to the given dimensional position,
   * for the given display dimensionality.
   *
   * @return null if the transform does not provide data of that dimensionality
   */
  public synchronized Data getData(int[] pos, int dim, DataCache cache) {
    if (dim != 3) {
      System.err.println(name + ": invalid dimensionality (" + dim + ")");
      return null;
    }

    // get some info from the parent transform
    ImageTransform it = (ImageTransform) parent;
    int w = it.getImageWidth();
    int h = it.getImageHeight();
    int n = parent.getLengths()[axis];
    RealType xType = it.getXType();
    RealType yType = it.getYType();
    RealType zType = it.getZType();
    RealType[] range = it.getRangeTypes();
    FunctionType imageType = it.getType();
    Unit[] imageUnits = it.getImageUnits();
    Unit zUnit = it.getZUnit(axis);
    Unit[] xyzUnits = {imageUnits[0], imageUnits[1], zUnit};
    RealTupleType xy = null, xyz = null;
    try {
      xy = new RealTupleType(xType, yType);
      xyz = new RealTupleType(xType, yType, zType);
    }
    catch (VisADException exc) { exc.printStackTrace(); }

    // convert spherical polar coordinates to cartesian coordinates for line
    double yawRadians = Math.PI * yaw / 180;
    double yawCos = Math.cos(yawRadians);
    double yawSin = Math.sin(yawRadians);
    double pitchRadians = Math.PI * (90 - pitch) / 180;
    double pitchCos = Math.cos(pitchRadians);
    double pitchSin = Math.sin(pitchRadians);

    // Let P1 = (x, y, z), P2 = -P1
    float x = (float) (RADIUS3 * pitchSin * yawCos);
    float y = (float) (RADIUS3 * pitchSin * yawSin);
    float z = (float) (RADIUS3 * pitchCos);

    // compute location along P1-P2 line
    float q = (loc - 50) / 50;
    float lx = q * x;
    float ly = q * y;
    float lz = q * z;

    // choose a point P which doesn't lie on the line through P1 and P2
    float px = 1, py = 1, pz = 1;
    float ax = x < 0 ? -x : x;
    float ay = y < 0 ? -y : y;
    float az = z < 0 ? -z : z;
    if (ax - ay < 0.1) py = -1;
    if (ax - az < 0.1) pz = -1;

    // calculate the vector R as the cross product between P - P1 and P2 - P1
    float pp1x = px - x, pp1y = py - y, pp1z = pz - z;
    float p2p1x = -x - x, p2p1y = -y - y, p2p1z = -z - z;
    float rz = pp1x * p2p1y - pp1y * p2p1x;
    float rx = pp1y * p2p1z - pp1z * p2p1y;
    float ry = pp1z * p2p1x - pp1x * p2p1z;
    // R is now perpendicular to P2 - P1

    // calculate the vector S as the cross product between R and P2 - P1
    float sz = rx * p2p1y - ry * p2p1x;
    float sx = ry * p2p1z - rz * p2p1y;
    float sy = rz * p2p1x - rx * p2p1z;
    // S is now perpendicular to both R and the P2 - P1

    // normalize R and S
    float rlen = (float) Math.sqrt(rx * rx + ry * ry + rz * rz);
    rx /= rlen; ry /= rlen; rz /= rlen;
    float slen = (float) Math.sqrt(sx * sx + sy * sy + sz * sz);
    sx /= slen; sy /= slen; sz /= slen;
    // now R and S are an orthonormal basis for the plane

    Gridded3DSet line = null;
    if (showLine) {
      // convert x=[-1,1] y=[-1,1] z=[-1,1] to x=[0,w] y=[0,h] z=[0,n]
      float[][] lineSamples = {
        {w * (x + 1) / 2, w * (-x + 1) / 2},
        {h * (y + 1) / 2, h * (-y + 1) / 2},
        {n * (z + 1) / 2, n * (-z + 1) / 2}
      };

      // construct line data object
      try {
        line = new Gridded3DSet(xyz, lineSamples,
          2, null, xyzUnits, null, false);
      }
      catch (VisADException exc) { exc.printStackTrace(); }
    }

    // compute slice data
    Data slice = null;
    if (compute) { // interpolate from parent data
      // compute plane corners from orthonormal basis
      float q1x = w * (lx + T1COS * rx + T1SIN * sx + 1) / 2;
      float q1y = h * (ly + T1COS * ry + T1SIN * sy + 1) / 2;
      float q1z = n * (lz + T1COS * rz + T1SIN * sz + 1) / 2;
      float q2x = w * (lx + T2COS * rx + T2SIN * sx + 1) / 2;
      float q2y = h * (ly + T2COS * ry + T2SIN * sy + 1) / 2;
      float q2z = n * (lz + T2COS * rz + T2SIN * sz + 1) / 2;
      float q3x = w * (lx + T3COS * rx + T3SIN * sx + 1) / 2;
      float q3y = h * (ly + T3COS * ry + T3SIN * sy + 1) / 2;
      float q3z = n * (lz + T3COS * rz + T3SIN * sz + 1) / 2;
      float q4x = w * (lx + T4COS * rx + T4SIN * sx + 1) / 2;
      float q4y = h * (ly + T4COS * ry + T4SIN * sy + 1) / 2;
      float q4z = n * (lz + T4COS * rz + T4SIN * sz + 1) / 2;

      // retrieve parent data from data cache
      int[] npos = getParentPos(pos);
      FlatField[] fields = new FlatField[n];
      for (int i=0; i<n; i++) {
        npos[axis] = i;
        Data data = parent.getData(npos, 2, cache);
        if (data == null || !(data instanceof FlatField)) {
          System.err.println(name +
            ": parent image plane #" + (i + 1) + " is not valid");
          return null;
        }
        fields[i] = (FlatField) data;
      }
      try {
        // use image transform's recommended MathType and Units
        for (int i=0; i<n; i++) {
          fields[i] = DataUtil.switchType(fields[i], imageType, imageUnits);
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }

      // generate planar domain samples and corresponding interpolated values
      int res1 = res - 1;
      float[][] planeSamples = new float[3][res * res];
      float[][] planeValues = new float[range.length][res * res];
      for (int r=0; r<res; r++) {
        float rr = (float) r / res1;
        float xmin = (1 - rr) * q1x + rr * q3x;
        float ymin = (1 - rr) * q1y + rr * q3y;
        float zmin = (1 - rr) * q1z + rr * q3z;
        float xmax = (1 - rr) * q2x + rr * q4x;
        float ymax = (1 - rr) * q2y + rr * q4y;
        float zmax = (1 - rr) * q2z + rr * q4z;
        for (int c=0; c<res; c++) {
          float cc = (float) c / res1;
          int ndx = r * res + c;
          float xs = planeSamples[0][ndx] = (1 - cc) * xmin + cc * xmax;
          float ys = planeSamples[1][ndx] = (1 - cc) * ymin + cc * ymax;
          float zs = planeSamples[2][ndx] = (1 - cc) * zmin + cc * zmax;
          if (xs < 0 || ys < 0 || zs < 0 ||
            xs > w - 1 || ys > h - 1 || zs > n - 1)
          {
            // this pixel is outside the range of the data (missing)
            for (int k=0; k<planeValues.length; k++) {
              planeValues[k][ndx] = Float.NaN;
            }
          }
          else {
            // interpolate the value of this pixel for each range component
            int xx = (int) xs, yy = (int) ys, zz = (int) zs;
            float wx = xs - xx, wy = ys - yy, wz = zs - zz;
            float[][] values0 = null, values1 = null;
            FlatField field0, field1;
            if (wz == 0) {
              // interpolate from a single field (z0 == z1)
              try { values0 = values1 = fields[zz].getFloats(false); }
              catch (VisADException exc) { exc.printStackTrace(); }
            }
            else {
              // interpolate between two fields
              try {
                values0 = fields[zz].getFloats(false);
                values1 = fields[zz + 1].getFloats(false);
              }
              catch (VisADException exc) { exc.printStackTrace(); }
            }
            int ndx00 = w * yy + xx;
            int ndx10 = w * yy + xx + 1;
            int ndx01 = w * (yy + 1) + xx;
            int ndx11 = w * (yy + 1) + xx + 1;
            for (int k=0; k<range.length; k++) {
              // tri-linear interpolation (x, then y, then z)
              float v000 = values0[k][ndx00];
              float v100 = values0[k][ndx10];
              float v010 = values0[k][ndx01];
              float v110 = values0[k][ndx11];
              float v001 = values1[k][ndx00];
              float v101 = values1[k][ndx10];
              float v011 = values1[k][ndx01];
              float v111 = values1[k][ndx11];
              float vx00 = (1 - wx) * v000 + wx * v100;
              float vx10 = (1 - wx) * v010 + wx * v110;
              float vx01 = (1 - wx) * v001 + wx * v101;
              float vx11 = (1 - wx) * v011 + wx * v111;
              float vxy0 = (1 - wy) * vx00 + wy * vx10;
              float vxy1 = (1 - wy) * vx01 + wy * vx11;
              float vxyz = (1 - wz) * vxy0 + wz * vxy1;
              planeValues[k][ndx] = vxyz;
            }
          }
        }
      }
      try {
        FunctionType planeType = new FunctionType(xyz, imageType.getRange());
        Gridded3DSet planeSet = new Gridded3DSet(xyz,
          planeSamples, res, res, null, xyzUnits, null, false);
        FlatField ff = new FlatField(planeType, planeSet);
        ff.setSamples(planeValues, false);
        slice = ff;
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else { // construct bounding circle
      // compute circle coordinates from orthonormal basis
      int num = 32;
      float[][] samples = new float[3][num];
      for (int i=0; i<num; i++) {
        double theta = i * 2 * Math.PI / num;
        float tcos = (float) (RADIUS3 * Math.cos(theta));
        float tsin = (float) (RADIUS3 * Math.sin(theta));
        float qx = lx + tcos * rx + tsin * sx;
        float qy = ly + tcos * ry + tsin * sy;
        float qz = lz + tcos * rz + tsin * sz;
        int ndx = (i < num / 2) ? i : (3 * num / 2 - i - 1);
        samples[0][ndx] = w * (qx + 1) / 2;
        samples[1][ndx] = h * (qy + 1) / 2;
        samples[2][ndx] = n * (qz + 1) / 2;
      }

      // construct bounding circle data object
      try {
        slice = new Gridded3DSet(xyz,
          samples, num / 2, 2, null, xyzUnits, null, false);
      }
      catch (VisADException exc) { exc.printStackTrace(); }
    }

    Data[] data = showLine ? new Data[] {slice, line} : new Data[] {slice};
    try { return new Tuple(data, false); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return null;
  }

  /** Gets whether this transform provides data of the given dimensionality. */
  public boolean isValidDimension(int dim) { return dim == 3; }

  /** Retrieves a set of mappings for displaying this transform effectively. */
  public ScalarMap[] getSuggestedMaps() {
    // Slice XYZ types piggyback on the parent, so that two sets of XYZ
    // coordinates don't show up during cursor probes (and so that the slice
    // is placed properly without an extra set of setRange calls).
    return parent.getSuggestedMaps();
  }

  /**
   * Gets a string id uniquely describing this data transform at the given
   * dimensional position, for the purposes of thumbnail caching.
   * If global flag is true, the id is suitable for use in the default,
   * global cache file.
   */
  public String getCacheId(int[] pos, boolean global) { return null; }

  /**
   * Arbitrary slices are rendered immediately,
   * due to frequent user interaction.
   */
  public boolean isImmediate() { return true; }

  /** Gets associated GUI controls for this transform. */
  public JComponent getControls() { return controls; }


  // -- Dynamic API methods --

  /** Tests whether two dynamic objects are equivalent. */
  public boolean matches(Dynamic dyn) {
    if (!super.matches(dyn) || !isCompatible(dyn)) return false;
    ArbitrarySlice data = (ArbitrarySlice) dyn;

    // CTR TODO return true iff data matches this object
    return false;
  }

  /**
   * Tests whether the given dynamic object can be used as an argument to
   * initState, for initializing this dynamic object.
   */
  public boolean isCompatible(Dynamic dyn) {
    return dyn instanceof ArbitrarySlice;
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */
  public void initState(Dynamic dyn) {
    if (dyn != null && !isCompatible(dyn)) return;
    super.initState(dyn);
    ArbitrarySlice data = (ArbitrarySlice) dyn;

    if (data == null) {
      axis = -1;
      yaw = 0;
      pitch = 45;
      loc = 50;
      res = 64;
      showLine = true;
      compute = true;
    }
    else {
      axis = data.axis;
      yaw = data.yaw;
      pitch = data.pitch;
      loc = data.loc;
      res = data.res;
      showLine = data.showLine;
      compute = data.compute;
    }

    computeLengths();
    controls = new SliceWidget(this);
  }


  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("DataTransforms"). */
  public void saveState(Element el) throws SaveException {
    Element child = XMLUtil.createChild(el, "ArbitrarySlice");
    super.saveState(child);
    child.setAttribute("axis", "" + axis);
    child.setAttribute("yaw", "" + yaw);
    child.setAttribute("pitch", "" + pitch);
    child.setAttribute("location", "" + loc);
    child.setAttribute("resolution", "" + res);
    child.setAttribute("showLine", "" + showLine);
    child.setAttribute("onTheFly", "" + compute);
  }

  /**
   * Restores the current state from the given DOM element ("ArbitrarySlice").
   */
  public void restoreState(Element el) throws SaveException {
    super.restoreState(el);
    axis = Integer.parseInt(el.getAttribute("axis"));
    yaw = Float.parseFloat(el.getAttribute("yaw"));
    pitch = Float.parseFloat(el.getAttribute("pitch"));
    loc = Float.parseFloat(el.getAttribute("location"));
    res = Integer.parseInt(el.getAttribute("resolution"));
    showLine = "true".equals(el.getAttribute("showLine"));
    compute = "true".equals(el.getAttribute("onTheFly"));
  }


  // -- TransformListener API methods --

  /** Called when parent data transform's parameters are updated. */
  public void transformChanged(TransformEvent e) {
    int id = e.getId();
    if (id == TransformEvent.DATA_CHANGED) {
      initState(null);
      notifyListeners(new TransformEvent(this));
    }
  }


  // -- Helper methods --

  /** Computes lengths and dims based on dimensional axis to be sliced. */
  private void computeLengths() {
    int[] plens = parent.getLengths();
    String[] pdims = parent.getDimTypes();

    if (axis < 0) {
      axis = 0;
      for (int i=0; i<pdims.length; i++) {
        if (pdims[i].equals("Slice")) {
          axis = i;
          break;
        }
      }
    }

    lengths = new int[plens.length - 1];
    System.arraycopy(plens, 0, lengths, 0, axis);
    System.arraycopy(plens, axis + 1, lengths, axis, lengths.length - axis);

    dims = new String[pdims.length - 1];
    System.arraycopy(pdims, 0, dims, 0, axis);
    System.arraycopy(pdims, axis + 1, dims, axis, dims.length - axis);

    makeLabels();
  }

  /** Gets dimensional position for parent transform. */
  private int[] getParentPos(int[] pos) {
    int[] npos = new int[pos.length + 1];
    System.arraycopy(pos, 0, npos, 0, axis);
    System.arraycopy(pos, axis, npos, axis + 1, pos.length - axis);
    return npos;
  }

}
