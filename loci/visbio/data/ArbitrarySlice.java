//
// ArbitrarySlice.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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
//import loci.visbio.util.VisUtil;
import visad.*;

/** A transform for slicing a stack of images in 3D. */
public class ArbitrarySlice extends DataTransform
  implements TransformListener
{

  // -- Constants --

  private static final double RADIUS = Math.sqrt(3);
  private static final double THETA1 = Math.PI / 4;
  private static final double THETA2 = 3 * THETA1;
  private static final double THETA3 = 7 * THETA1;
  private static final double THETA4 = 5 * THETA1;
  private static final float T1COS = (float) (RADIUS * Math.cos(THETA1));
  private static final float T1SIN = (float) (RADIUS * Math.sin(THETA1));
  private static final float T2COS = (float) (RADIUS * Math.cos(THETA2));
  private static final float T2SIN = (float) (RADIUS * Math.sin(THETA2));
  private static final float T3COS = (float) (RADIUS * Math.cos(THETA3));
  private static final float T3SIN = (float) (RADIUS * Math.sin(THETA3));
  private static final float T4COS = (float) (RADIUS * Math.cos(THETA4));
  private static final float T4SIN = (float) (RADIUS * Math.sin(THETA4));


  // -- Fields --

  /** Dimensional axis to collapse. */
  protected int axis;

  /** Horizontal rotational angle of slicing line. */
  protected double yaw;

  /** Vertical rotatational angle of slicing line. */
  protected double pitch;

  /** Arbitrary slice's location along slicing line. */
  protected double loc = 50;

  /** Resolution of arbitrary slice. */
  protected int res = 64;

  /** Controls for the arbitrary slice. */
  protected SliceWidget controls;


  // -- Constructor --

  /** Creates an overlay object for the given transform. */
  public ArbitrarySlice(DataTransform parent, String name) {
    super(parent, name);
    initState(null);
    parent.addTransformListener(this);
  }


  // -- ArbitrarySlice API methods --

  /**
   * Sets the parameters for the arbitrary slice,
   * recomputing the slice if the recompute flag is set.
   */
  public void setParameters(int axis, double yaw, double pitch,
    double loc, int res)
  {
    if (this.axis == axis && this.yaw == yaw && this.pitch == pitch &&
      this.loc == loc && this.res == res)
    {
      return;
    }
    this.axis = axis;
    this.yaw = yaw;
    this.pitch = pitch;
    this.loc = loc;
    this.res = res;
    controls.refreshWidget();
    notifyListeners(new TransformEvent(this));
  }

  /** Sets the axis through which to slice. */
  public void setAxis(int axis) { setParameters(axis, yaw, pitch, loc, res); }

  /** Sets the yaw for the slicing line. */
  public void setYaw(double yaw) { setParameters(axis, yaw, pitch, loc, res); }

  /** Sets the pitch for the slicing line. */
  public void setPitch(double pitch) {
    setParameters(axis, yaw, pitch, loc, res);
  }

  /** Sets the location for the arbitrary slice. */
  public void setLocation(double loc) {
    setParameters(axis, yaw, pitch, loc, res);
  }

  /** Sets the resolution for the slicing line. */
  public void setResolution(int res) {
    setParameters(axis, yaw, pitch, loc, res);
  }

  /** Gets the axis through which to slice. */
  public int getAxis() { return axis; }

  /** Gets the yaw for the slicing line. */
  public double getYaw() { return yaw; }

  /** Gets the pitch for the slicing line. */
  public double getPitch() { return pitch; }

  /** Gets the location for the arbitrary slice. */
  public double getLocation() { return loc; }

  /** Gets the resolution for the slicing line. */
  public int getResolution() { return res; }


  // -- Static DataTransform API methods --

  /** Creates a new set of overlays, with user interaction. */
  public static DataTransform makeTransform(DataManager dm) {
    DataTransform dt = dm.getSelectedData();
    if (!isValidParent(dt)) return null;
    String n = (String) JOptionPane.showInputDialog(dm.getControlPanel(),
      "Title of slice:", "Create arbitrary slice",
      JOptionPane.INFORMATION_MESSAGE, null, null,
      dt.getName() + " slice");
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
  public Data getData(int[] pos, int dim) {
    if (dim != 3) return null;

    /*
    int len = parent.getLengths()[axis];
    FlatField[] fields = new FlatField[len];
    int[] npos = getParentPos(pos);
    for (int i=0; i<len; i++) {
      npos[axis] = i;
      Data data = parent.getData(npos, dim);
      if (data == null || !(data instanceof FlatField)) return null;
      fields[i] = (FlatField) data;
    }
    */

    ImageTransform it = (ImageTransform) parent;
    RealType zType = ((ImageTransform) parent).getZType();
    /*
    try {
      FieldImpl field = VisUtil.makeField(fields, zType);
      // CTR START HERE
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    */

    // convert spherical polar coordinates to cartesian coordinates for line
    double yawRadians = Math.PI * yaw / 180;
    double yawCos = Math.cos(yawRadians);
    double yawSin = Math.sin(yawRadians);
    double pitchRadians = Math.PI * (90 - pitch) / 180;
    double pitchCos = Math.cos(pitchRadians);
    double pitchSin = Math.sin(pitchRadians);

    // Let P1 = (x, y, z), P2 = -P1
    float x = (float) (RADIUS * pitchSin * yawCos);
    float y = (float) (RADIUS * pitchSin * yawSin);
    float z = (float) (RADIUS * pitchCos);

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

    // compute plane corners from orthonormal basis
    float q1x = T1COS * rx + T1SIN * sx;
    float q1y = T1COS * ry + T1SIN * sy;
    float q1z = T1COS * rz + T1SIN * sz;
    float q2x = T2COS * rx + T2SIN * sx;
    float q2y = T2COS * ry + T2SIN * sy;
    float q2z = T2COS * rz + T2SIN * sz;
    float q3x = T3COS * rx + T3SIN * sx;
    float q3y = T3COS * ry + T3SIN * sy;
    float q3z = T3COS * rz + T3SIN * sz;
    float q4x = T4COS * rx + T4SIN * sx;
    float q4y = T4COS * ry + T4SIN * sy;
    float q4z = T4COS * rz + T4SIN * sz;

    // convert x=[-1,1] y=[-1,1] z=[-1,1] to x=[0,w] y=[0,h] z=[-1,1]
    int w = it.getImageWidth();
    int h = it.getImageHeight();
    float[][] lineSamples = {
      {w * (x + 1) / 2, w * (-x + 1) / 2},
      {h * (y + 1) / 2, h * (-y + 1) / 2},
      {z, -z}
    };
    float[][] planeSamples = {
      {w*(q1x + 1)/2, w*(q2x + 1)/2, w*(q3x + 1)/2, w*(q4x + 1)/2},
      {h*(q1y + 1)/2, h*(q2y + 1)/2, h*(q3y + 1)/2, h*(q4y + 1)/2},
      {q1z, q2z, q3z, q4z}
    };

    RealType xType = it.getXType();
    RealType yType = it.getYType();
    try {
      RealTupleType xyz = new RealTupleType(xType, yType, zType);
      Gridded3DSet line = new Gridded3DSet(xyz,
        lineSamples, 2, null, null, null, false);
      Gridded3DSet plane = new Gridded3DSet(xyz,
        planeSamples, 2, 2, null, null, null, false);

      return new Tuple(new Data[] {line, plane}, false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }

    return null; // TEMP
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

    if (data != null) {
      yaw = data.yaw;
      pitch = data.pitch;
      loc = data.loc;
      res = data.res;
    }

    lengths = parent.getLengths();
    dims = parent.getDimTypes();
    makeLabels();

    controls = new SliceWidget(this);
  }

  /**
   * Called when this object is being discarded in favor of
   * another object with a matching state.
   */
  public void discard() { }


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
    lengths = new int[plens.length - 1];
    System.arraycopy(plens, 0, lengths, 0, axis);
    System.arraycopy(plens, axis + 1, lengths, axis, lengths.length - axis);

    String[] pdims = parent.getDimTypes();
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
