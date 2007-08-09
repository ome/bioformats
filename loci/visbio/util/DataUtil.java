//
// DataUtil.java
//

/*
VisBio application for visualization of multidimensional biological
image data. Copyright (C) 2002-@year@ Curtis Rueden and Abraham Sorber.

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

package loci.visbio.util;

import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.Vector;
import visad.*;

/**
 * DataUtil contains useful VisAD data functions.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/util/DataUtil.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/util/DataUtil.java">SVN</a></dd></dl>
 */
public final class DataUtil {

  // -- Constructor --

  private DataUtil() { }

  // -- Utility methods --

  /**
   * Resamples the given FlatField to the specified resolution,
   * keeping only the flagged range components (or null to keep them all),
   * leaving the domain set's low and high values untouched.
   */
  public static FlatField resample(FlatField f, int[] res, boolean[] range)
    throws VisADException, RemoteException
  {
    return resample(f, res, range, false);
  }

  /**
   * Resamples the given FlatField to the specified resolution,
   * keeping only the flagged range components (or null to keep them all),
   * forcing the result onto an IntegerSet of appropriate length if the
   * relevant flag is set.
   */
  public static FlatField resample(FlatField f, int[] res, boolean[] range,
    boolean forceIntegerSet) throws VisADException, RemoteException
  {
    float[] min = new float[res.length], max = new float[res.length];

    if (forceIntegerSet) {
      // use min and max appropriate for IntegerSet
      for (int i=0; i<res.length; i++) {
        min[i] = 0;
        max[i] = res[i] - 1;
      }
    }
    else {
      // compute min and max from original set
      GriddedSet set = (GriddedSet) f.getDomainSet();
      min = set.getLow();
      max = set.getHi();
    }

    return resample(f, res, range, min, max);
  }

  /**
   * Resamples the given FlatField to the specified resolution,
   * keeping only the flagged range components (or null to keep them all),
   * and using the given minimum and maximum domain set values.
   */
  public static FlatField resample(FlatField f, int[] res, boolean[] range,
    float[] min, float[] max) throws VisADException, RemoteException
  {
    //TODO: rewrite VisBio resampling logic to work on raw images
    //rework VisBio to avoid any reference to "range components"
    //instead, multiple range components should always become multiple channels
    GriddedSet set = (GriddedSet) f.getDomainSet();
    float[][] samples = f.getFloats(false);
    FunctionType function = (FunctionType) f.getType();
    MathType frange = function.getRange();
    RealType[] rt = frange instanceof RealTupleType ?
      ((RealTupleType) frange).getRealComponents() :
      new RealType[] {(RealType) frange};

    // count number of range components to keep
    int keep = 0;
    if (range != null) {
      for (int i=0; i<range.length; i++) {
        if (range[i]) keep++;
      }
    }

    // strip out unwanted range components
    FlatField ff;
    if (range == null || keep == range.length) ff = f;
    else {
      float[][] samps = new float[keep][];
      RealType[] reals = new RealType[keep];
      int count = 0;
      for (int i=0; i<range.length; i++) {
        if (range[i]) {
          samps[count] = samples[i];
          reals[count] = rt[i];
          count++;
        }
      }
      MathType funcRange = count == 1 ?
        (MathType) reals[0] : (MathType) new RealTupleType(reals);
      FunctionType func = new FunctionType(function.getDomain(), funcRange);
      ff = new FlatField(func, set);
      ff.setSamples(samps, false);
    }

    // determine whether original resolution matches desired resolution
    int[] len = set.getLengths();
    boolean same = true;
    float[] lo = set.getLow();
    float[] hi = set.getHi();
    double[] nlo = new double[len.length];
    double[] nhi = new double[len.length];
    for (int i=0; i<len.length; i++) {
      if (res != null && len[i] != res[i]) same = false;
      nlo[i] = (double) lo[i];
      nhi[i] = (double) hi[i];
    }
    if (!same) {
      // resample field to proper resolution
      ff = (FlatField) ff.resample((Set) LinearNDSet.create(set.getType(),
        nlo, nhi, res, set.getCoordinateSystem(), set.getSetUnits(),
        set.getSetErrors()), Data.WEIGHTED_AVERAGE, Data.NO_ERRORS);
    }

    // determine whether original low and high values match desired ones
    boolean canUseInteger = true;
    same = true;
    for (int i=0; i<len.length; i++) {
      if (min != null) {
        if (nlo[i] != min[i]) same = false;
        if (min[i] != 0) canUseInteger = false;
        nlo[i] = min[i];
      }
      if (max != null) {
        if (nhi[i] != max[i]) same = false;
        if (min[i] != res[i] - 1) canUseInteger = false;
        nhi[i] = max[i];
      }
    }

    if (canUseInteger || !same) {
      // adjust field set appropriately
      FunctionType ffType = (FunctionType) ff.getType();
      Set ffSet = canUseInteger ?
        (Set) IntegerNDSet.create(ffType.getDomain(), res) :
        (Set) LinearNDSet.create(ffType.getDomain(), nlo, nhi, res);
      float[][] ffSamples = ff.getFloats(false);
      ff = new FlatField(ffType, ffSet);
      ff.setSamples(ffSamples);
    }
    return ff;
  }

  /**
   * Creates a field of the form (z -> type) from the given list of fields,
   * where type is the MathType of each component field.
   */
  public static FieldImpl makeField(FlatField[] fields, RealType zType)
    throws VisADException, RemoteException
  {
    if (fields == null || zType == null || fields.length == 0) return null;
    return makeField(fields, zType, new Integer1DSet(zType, fields.length));
  }

  /**
   * Creates a field of the form (z -> type) from the given list of fields,
   * where type is the MathType of each component field.
   */
  public static FieldImpl makeField(FlatField[] fields, RealType zType,
    Unit setUnit) throws VisADException, RemoteException
  {
    if (fields == null || zType == null || fields.length == 0) return null;
    return makeField(fields, zType, new Integer1DSet(zType,
      fields.length, null, new Unit[] {setUnit}, null));
  }

  /**
   * Creates a field of the form (z -> type) from the given list of fields,
   * where type is the MathType of each component field.
   */
  public static FieldImpl makeField(FlatField[] fields, RealType zType,
    double min, double max) throws VisADException, RemoteException
  {
    if (fields == null || zType == null || fields.length == 0) return null;
    return makeField(fields, zType,
      new Linear1DSet(zType, min, max, fields.length));
  }

  /**
   * Creates a field of the form (z -> type) from the given list of fields,
   * where type is the MathType of each component field.
   */
  public static FieldImpl makeField(FlatField[] fields, RealType zType,
    double min, double max, Unit setUnit)
    throws VisADException, RemoteException
  {
    if (fields == null || zType == null || fields.length == 0) return null;
    return makeField(fields, zType, new Linear1DSet(zType,
      min, max, fields.length, null, new Unit[] {setUnit}, null));
  }

  /**
   * Creates a field of the form (z -> type) from the given list of fields,
   * where type is the MathType of each component field.
   */
  public static FieldImpl makeField(FlatField[] fields, RealType zType,
    SampledSet fieldSet) throws VisADException, RemoteException
  {
    if (fields == null || zType == null ||
      fields.length == 0 || fields[0] == null)
    {
      return null;
    }
    if (fields[0] == null) return null;
    FunctionType fieldType = new FunctionType(zType, fields[0].getType());
    FieldImpl field = new FieldImpl(fieldType, fieldSet);
    field.setSamples(fields, false);
    return field;
  }

  /**
   * Collapses a field from the form <tt>(z -> ((x, y) -> (v1, ..., vn)))</tt>
   * into the form <tt>((x, y, z) -> (v1, ..., vn))</tt>.
   */
  public static FlatField collapse(FieldImpl f)
    throws VisADException, RemoteException
  {
    if (f == null) return null;

    try { return (FlatField) f.domainMultiply(); }
    catch (FieldException exc) { }

    // images dimensions do not match; resample so that they do
    GriddedSet set = (GriddedSet) f.getDomainSet();
    int len = set.getLengths()[0];
    int resX = 0;
    int resY = 0;
    for (int i=0; i<len; i++) {
      FlatField flat = (FlatField) f.getSample(i);
      GriddedSet flatSet = (GriddedSet) flat.getDomainSet();
      int[] l = flatSet.getLengths();
      if (l[0] > resX) resX = l[0];
      if (l[1] > resY) resY = l[1];
    }
    FieldImpl nf = new FieldImpl((FunctionType) f.getType(), set);
    for (int i=0; i<len; i++) {
      FlatField flat = (FlatField) f.getSample(i);
      GriddedSet flatSet = (GriddedSet) flat.getDomainSet();
      int[] l = flatSet.getLengths();
      if (l[0] > resX) resX = l[0];
      if (l[1] > resY) resY = l[1];
    }
    for (int i=0; i<len; i++) {
      FlatField flat = (FlatField) f.getSample(i);
      GriddedSet flatSet = (GriddedSet) flat.getDomainSet();
      nf.setSample(i, flat.resample(new Integer2DSet(flatSet.getType(),
        resX, resY, flatSet.getCoordinateSystem(), flatSet.getSetUnits(),
        flatSet.getSetErrors()), Data.WEIGHTED_AVERAGE, Data.NO_ERRORS),
        false);
    }
    return (FlatField) nf.domainMultiply();
  }

  /**
   * Resamples a volume of the form <tt>((x, y, z) -> (v1, ..., vn))</tt>
   * to the given resolution along each of its three spatial axes.
   */
  public static FlatField makeCube(FlatField volume, int res)
    throws VisADException, RemoteException
  {
    GriddedSet set = (GriddedSet) volume.getDomainSet();
    int[] len = set.getLengths();
    if (len[0] == res && len[1] == res && len[2] == res) return volume;
    else {
      float[] lo = set.getLow();
      float[] hi = set.getHi();
      Linear3DSet nset = new Linear3DSet(set.getType(),
        lo[0], hi[0], res, lo[1], hi[1], res, lo[2], hi[2], res,
        set.getCoordinateSystem(), set.getSetUnits(), set.getSetErrors());
      return (FlatField) volume.resample(nset,
        Data.WEIGHTED_AVERAGE, Data.NO_ERRORS);
    }
  }

  /**
   * Gets an array of ScalarMaps from the given display,
   * corresponding to the specified RealTypes.
   */
  public static ScalarMap[] getMaps(DisplayImpl display, RealType[] types) {
    ScalarMap[] maps = new ScalarMap[types.length];
    Vector v = display.getMapVector();
    int size = v.size();
    for (int i=0; i<size; i++) {
      ScalarMap map = (ScalarMap) v.elementAt(i);
      ScalarType type = map.getScalar();
      for (int j=0; j<types.length; j++) {
        if (type.equals(types[j])) {
          maps[j] = map;
          break;
        }
      }
    }
    return maps;
  }

  /** Converts a FlatField's MathType into the one given (if compatible). */
  public static FlatField switchType(FlatField field, FunctionType newType)
    throws VisADException, RemoteException
  {
    return switchType(field, newType, null);
  }

  /**
   * Converts a FlatField's MathType into the one given (if compatible),
   * and uses the specified domain set units.
   */
  public static FlatField switchType(FlatField field, FunctionType newType,
    Unit[] units) throws VisADException, RemoteException
  {
    if (field == null) throw new VisADException("field cannot be null");
    if (newType == null) throw new VisADException("newType cannot be null");
    if (!newType.equalsExceptName(field.getType())) {
      throw new VisADException("Types do not match");
    }

    // there is no way to alter a domain set's units, so we are forced to
    // reconstruct the domain set to set the units properly
    Set domainSet = field.getDomainSet();
    if (domainSet instanceof LinearSet) {
      LinearSet set = (LinearSet) domainSet;
      int dim = domainSet.getDimension();
      double[] first = new double[dim];
      double[] last = new double[dim];
      int[] lengths = new int[dim];
      for (int i=0; i<dim; i++) {
        Linear1DSet iset = set.getLinear1DComponent(i);
        first[i] = iset.getFirst();
        last[i] = iset.getLast();
        lengths[i] = iset.getLength();
      }
      domainSet = (Set) LinearNDSet.create(newType.getDomain(),
        first, last, lengths, null, units, null);
    }
    else throw new VisADException("domain set not linear");

    CoordinateSystem rangeCoordSys = field.getRangeCoordinateSystem()[0];
    Set[] rangeSets = field.getRangeSets();
    Unit[][] rangeUnits = field.getRangeUnits();
    Unit[] newRangeUnits = new Unit[rangeUnits.length];
    for (int i=0; i<rangeUnits.length; i++) {
      newRangeUnits[i] = rangeUnits[i][0];
    }
    if (field instanceof ImageFlatField) {
      BufferedImage image = ((ImageFlatField) field).getImage();
      ImageFlatField newField = new ImageFlatField(newType,
        domainSet, rangeCoordSys, rangeSets, newRangeUnits);
      newField.setImage(image);
      return newField;
    }
    else {
      float[][] samples = field.getFloats(false);
      FlatField newField = new FlatField(newType,
        domainSet, rangeCoordSys, rangeSets, newRangeUnits);
      newField.setSamples(samples, false);
      return newField;
    }
  }

  /**
   * Gets a RealType with the given name, converting
   * invalid characters within the name to underscores.
   */
  public static RealType getRealType(String name) {
    return getRealType(name, null);
  }

  /**
   * Gets a RealType with the given name and unit, converting
   * invalid characters within the name to underscores.
   */
  public static RealType getRealType(String name, Unit unit) {
    char[] c = name.toCharArray();
    for (int i=0; i<c.length; i++) {
      if (" .()".indexOf(c[i]) >= 0) c[i] = '_';
    }
    return RealType.getRealType(new String(c), unit);
  }

}
