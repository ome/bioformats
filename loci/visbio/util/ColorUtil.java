//
// ColorUtil.java
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

package loci.visbio.util;

import java.awt.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.rmi.RemoteException;

import java.util.Arrays;

import visad.BaseColorControl;
import visad.ColorControl;
import visad.Display;
import visad.DisplayImpl;
import visad.GraphicsModeControl;
import visad.RealType;
import visad.ScalarMap;
import visad.VisADException;

import loci.visbio.util.ObjectUtil;

/** ColorUtil contains useful color table functions. */
public abstract class ColorUtil {

  // -- Color table manipulation --

  /** RGB color space model. */
  public static final int RGB_MODEL = 0;

  /** HSV color space model. */
  public static final int HSV_MODEL = 1;

  /** Composite RGB color space model. */
  public static final int COMPOSITE_MODEL = 2;

  /** Resolution of color tables. */
  public static final int COLOR_DETAIL = 256;

  /** RealType for indicating nothing mapped to a color component. */
  public static final RealType CLEAR = RealType.getRealType("none");

  /** RealType for indicating uniformly solid color component. */
  public static final RealType SOLID = RealType.getRealType("full");

  /** RGB composite color table. */
  protected static final float[][] RAINBOW =
    ColorControl.initTableVis5D(new float[3][COLOR_DETAIL]);

  /**
   * Computes color tables from the given color settings.
   *
   * @param maps ScalarMaps for which to generate color tables
   * @param brightness Brightness value from 0 to 255.
   * @param contrast Contrast value from 0 to 255.
   * @param model Color space model: RGB_MODEL, HSV_MODEL or COMPOSITE_MODEL
   * @param red RealType to map to red color component
   * @param green RealType to map to green color component
   * @param blue RealType to map to blue color component
   */
  public static float[][][] computeColorTables(ScalarMap[] maps,
    int brightness, int contrast, int model,
    RealType red, RealType green, RealType blue)
  {
    // compute center and slope from brightness and contrast
    double mid = COLOR_DETAIL / 2.0;
    double slope;
    if (contrast <= mid) slope = contrast / mid;
    else slope = mid / (COLOR_DETAIL - contrast);
    if (slope == Double.POSITIVE_INFINITY) slope = Double.MAX_VALUE;
    double center = (slope + 1) * brightness / COLOR_DETAIL - 0.5 * slope;

    // compute color channel table values from center and slope
    float[] vals = new float[COLOR_DETAIL];
    float[] rvals, gvals, bvals;
    if (model == COMPOSITE_MODEL) {
      rvals = new float[COLOR_DETAIL];
      gvals = new float[COLOR_DETAIL];
      bvals = new float[COLOR_DETAIL];
      float[][] comp = RAINBOW;
      for (int i=0; i<COLOR_DETAIL; i++) {
        rvals[i] = (float) (slope * (comp[0][i] - 0.5) + center);
        gvals[i] = (float) (slope * (comp[1][i] - 0.5) + center);
        bvals[i] = (float) (slope * (comp[2][i] - 0.5) + center);
        if (rvals[i] > 1) rvals[i] = 1;
        if (rvals[i] < 0) rvals[i] = 0;
        if (gvals[i] > 1) gvals[i] = 1;
        if (gvals[i] < 0) gvals[i] = 0;
        if (bvals[i] > 1) bvals[i] = 1;
        if (bvals[i] < 0) bvals[i] = 0;
      }
    }
    else {
      for (int i=0; i<COLOR_DETAIL; i++) {
        vals[i] = (float) (0.5 * slope * (i / mid - 1.0) + center);
      }
      rvals = gvals = bvals = vals;
    }
    for (int i=0; i<COLOR_DETAIL; i++) {
      if (vals[i] < 0.0f) vals[i] = 0.0f;
      else if (vals[i] > 1.0f) vals[i] = 1.0f;
    }

    // update color tables and map ranges
    boolean rSolid = red == SOLID;
    boolean gSolid = green == SOLID;
    boolean bSolid = blue == SOLID;
    float[][][] colorTables = new float[maps.length][][];
    for (int j=0; j<maps.length; j++) {
      RealType rt = (RealType) maps[j].getScalar();

      // fill in color table elements
      float[][] t;
      if (model == COMPOSITE_MODEL) t = new float[][] {rvals, gvals, bvals};
      else {
        t = new float[][] {
          rt.equals(red) ? rvals : new float[COLOR_DETAIL],
          rt.equals(green) ? gvals : new float[COLOR_DETAIL],
          rt.equals(blue) ? bvals : new float[COLOR_DETAIL]
        };
        if (rSolid) Arrays.fill(t[0], 1.0f);
        if (gSolid) Arrays.fill(t[1], 1.0f);
        if (bSolid) Arrays.fill(t[2], 1.0f);

        // convert color table to HSV color model if necessary
        if (model == HSV_MODEL) {
          float[][] newt = new float[3][COLOR_DETAIL];
          for (int i=0; i<COLOR_DETAIL; i++) {
            Color c = new Color(Color.HSBtoRGB(t[0][i], t[1][i], t[2][i]));
            newt[0][i] = c.getRed() / 255f;
            newt[1][i] = c.getGreen() / 255f;
            newt[2][i] = c.getBlue() / 255f;
          }
          t = newt;
        }
      }
      colorTables[j] = t;
    }
    return colorTables;
  }

  /** Assigns the given color range to the specified display and mapping. */
  public static void setColorRange(DisplayImpl display,
    ScalarMap map, double lo, double hi, boolean fixed)
  {
    if (hi <= lo) hi = lo + 1;
    /*CTR TEMP*/System.out.println("Setting map range for map " + map + ": lo=" + lo + "; hi=" + hi + "; fixed=" + fixed);

    if (fixed) {
      double[] range = map.getRange();
      if (map.isAutoScale() || range[0] != lo || range[1] != hi) {
        try { map.setRange(lo, hi); }
        catch (VisADException exc) { exc.printStackTrace(); }
        catch (RemoteException exc) { exc.printStackTrace(); }
      }
    }
    else if (!map.isAutoScale()) reAutoScale(display, map);
  }

  /** Assigns the given color ranges to the specified display and mappings. */
  public static void setColorRanges(DisplayImpl display,
    ScalarMap[] maps, double[] lo, double[] hi, boolean[] fixed)
  {
    if (lo.length != maps.length || hi.length != maps.length ||
      fixed.length != maps.length)
    {
      return;
    }
    for (int i=0; i<maps.length; i++) {
      setColorRange(display, maps[i], lo[i], hi[i], fixed[i]);
    }
  }

  /** Recomputes autoscaled color bounds for the given color map. */
  public static void reAutoScale(DisplayImpl display, ScalarMap map) {
    /*CTR TEMP*/System.out.println("Rescaling map " + map);
    map.resetAutoScale();
    display.reAutoScale();

    // HACK - stupid trick to force immediate rescale
    BaseColorControl cc = (BaseColorControl) map.getControl();
    try { cc.setTable(cc.getTable()); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }

    // HACK - check for bogus (-0.5 - 0.5) range and reassign to (0.0 - 1.0)
    double[] range = map.getRange();
    if (range[0] == -0.5 && range[1] == 0.5) {
      try { map.setRange(0, 1); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
  }

  /** Assigns the given color tables to the specified mappings. */
  public static void setColorTables(ScalarMap[] maps, float[][][] tables) {
    if (tables.length != maps.length) return;

    for (int i=0; i<maps.length; i++) {
      boolean doAlpha = maps[i].getDisplayScalar().equals(Display.RGBA);

      BaseColorControl cc = (BaseColorControl) maps[i].getControl();
      float[][] oldTable = cc.getTable();

      float[][] t = ColorUtil.adjustColorTable(tables[i],
        oldTable.length > 3 ? oldTable[3] : null, doAlpha);
      if (ObjectUtil.arraysEqual(oldTable, t)) continue;

      try { cc.setTable(t); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
  }

  /**
   * Ensures the color table is of the proper type (RGB or RGBA).
   *
   * If the alpha is not required but the table has an alpha channel,
   * a new table is returned with the alpha channel stripped out.
   *
   * If alpha is required but the table does not have an alpha channel,
   * a new table is returned with an alpha channel matching the provided
   * one (or all 1s if the provided alpha channel is null or invalid).
   */
  public static float[][] adjustColorTable(float[][] table,
    float[] alpha, boolean doAlpha)
  {
    if (table == null || table.length == 0 || table[0] == null) return null;
    if (table.length == 3) {
      if (!doAlpha) return table;
      int len = table[0].length;
      if (alpha == null || alpha.length != len) {
        alpha = new float[len];
        Arrays.fill(alpha, 1.0f);
      }
      return new float[][] {table[0], table[1], table[2], alpha};
    }
    else { // table.length == 4
      if (doAlpha) return table;
      return new float[][] {table[0], table[1], table[2]};
    }
  }

  /**
   * Sets the given display's color mode to use either averaging or sums,
   * depending on the specified color model.
   */
  public static void setColorMode(DisplayImpl display, int model) {
    try {
      display.getGraphicsModeControl().setColorMode(model == COMPOSITE_MODEL ?
        GraphicsModeControl.AVERAGE_COLOR_MODE :
        GraphicsModeControl.SUM_COLOR_MODE);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }


  // -- Color table I/O --

  /** Color table file length. */
  protected static final int LUT_LEN = 768;

  /** Loads the color table from the given file. */
  public static float[][] loadColorTable(File file) {
    if (file == null || !file.exists()) return null;
    long fileLen = file.length();
    if (fileLen > Integer.MAX_VALUE) return null;
    int offset = (int) fileLen - LUT_LEN;
    if (offset < 0) return null;

    // read in LUT data
    int[] b = new int[LUT_LEN];
    try {
      DataInputStream fin = new DataInputStream(new FileInputStream(file));
      int skipped = 0;
      while (skipped < offset) skipped += fin.skipBytes(offset - skipped);
      for (int i=0; i<LUT_LEN; i++) b[i] = fin.readUnsignedByte();
      fin.close();
    }
    catch (IOException exc) { return null; }

    // convert data to VisAD format
    int len = LUT_LEN / 3;
    int count = 0;
    float[][] table = new float[3][len];
    for (int i=0; i<3; i++) {
      for (int j=0; j<len; j++) table[i][j] = b[count++] / 255f;
    }
    return table;
  }

  /**
   * Saves the given color table into the specified file.
   * @return true if file was saved successfully
   */
  public static boolean saveColorTable(float[][] table, File file) {
    if (file == null || table == null || table.length != 3) return false;

    // convert LUT data to binary format
    int len = LUT_LEN / 3;
    int[] b = new int[LUT_LEN];
    int count = 0;
    for (int i=0; i<3; i++) {
      if (table[i].length != len) return false;
      for (int j=0; j<len; j++) b[count++] = (int) (255 * table[i][j]);
    }

    // write out LUT data
    try {
      DataOutputStream fout = new DataOutputStream(new FileOutputStream(file));
      for (int i=0; i<LUT_LEN; i++) fout.writeByte(b[i]);
      fout.close();
    }
    catch (IOException exc) { return false; }
    return true;
  }

}
