//
// ColorHandler.java
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

package loci.visbio.view;

import java.util.Arrays;

import loci.visbio.VisBioFrame;

import loci.visbio.util.ColorUtil;
import loci.visbio.util.ObjectUtil;
import loci.visbio.util.VisUtil;

import visad.*;

/** Provides logic for controlling a VisAD display's color settings. */
public class ColorHandler {

  // -- Constants --

  /** Starting brightness value. */
  protected static final int NORMAL_BRIGHTNESS = 128;

  /** Starting contrast value. */
  protected static final int NORMAL_CONTRAST = 128;


  // -- Fields --

  /** Associated display window. */
  protected DisplayWindow window;

  /** GUI controls for color handler. */
  protected ColorPanel panel;

  /** Brightness and contrast of images. */
  protected int brightness, contrast;

  /** Color model (RGB_MODEL, HSV_MODEL or COMPOSITE_MODEL). */
  protected int model;

  /** Red, green and blue components of images. */
  protected RealType red, green, blue;

  /**
   * Minimum and maximum color range values
   * (for uninitialized color handlers only).
   */
  protected double[] lo, hi;

  /**
   * Flags indicating color ranges should not be auto-scaled
   * (for uninitialized color handlers only).
   */
  protected boolean[] fixed;

  /** Color tables (for uninitialized color handlers only). */
  protected float[][][] colorTables;


  // -- Constructor --

  /** Creates a display color handler. */
  public ColorHandler(DisplayWindow dw) {
    window = dw;

    // default color settings
    brightness = NORMAL_BRIGHTNESS;
    contrast = NORMAL_CONTRAST;
    model = ColorUtil.RGB_MODEL;
    red = green = blue = ColorUtil.CLEAR;
  }


  // -- ColorHandler API methods --

  /** Gets associated display window. */
  public DisplayWindow getWindow() { return window; }

  /** Gets GUI controls for this color handler. */
  public ColorPanel getPanel() { return panel; }

  /** Gets color mappings for this color handler's display. */
  public ScalarMap[] getMaps() {
    DisplayImpl display = window.getDisplay();
    return display == null ? null : VisUtil.getMaps(display,
      new DisplayRealType[] {Display.RGB, Display.RGBA});
  }

  /** Guesses at good initial color mappings and applies them. */
  public void initColors() {
    panel.refreshPreviewData();

    // apply default color table behavior
    ColorPane colorPane = panel.getColorPane();
    red = colorPane.getRed();
    green = colorPane.getGreen();
    blue = colorPane.getBlue();
    setParameters(brightness, contrast, model, red, green, blue, true);

    // apply default range scaling behavior
    ScalarMap[] maps = getMaps();
    lo = new double[maps.length];
    Arrays.fill(lo, 0.0);
    hi = new double[maps.length];
    Arrays.fill(hi, 255.0);
    fixed = new boolean[maps.length];
    Arrays.fill(fixed, false);
    setRanges(lo, hi, fixed);
  }

  /** Updates color settings to those given. */
  public void setParameters(int brightness, int contrast, int model,
    RealType red, RealType green, RealType blue, boolean compute)
  {
    this.brightness = brightness;
    this.contrast = contrast;
    this.model = model;
    this.red = red;
    this.green = green;
    this.blue = blue;
    if (compute) {
      setTables(ColorUtil.computeColorTables(getMaps(),
        brightness, contrast, model, red, green, blue));
    }
  }

  /** Updates map ranges to those given. */
  public void setRanges(double[] lo, double[] hi, boolean[] fixed) {
    ColorUtil.setColorRanges(window.getDisplay(), getMaps(), lo, hi, fixed);
  }

  /** Updates color tables to those given. */
  public void setTables(float[][][] tables) {
    ColorUtil.setColorMode(window.getDisplay(), model);
    ColorUtil.setColorTables(getMaps(), tables);
    VisBioFrame bio = window.getVisBio();
    bio.generateEvent(bio.getManager(DisplayManager.class),
      "color adjustment for " + window.getName(), true);
  }

  /** Gets brightness value. */
  public int getBrightness() { return brightness; }

  /** Gets contrast value. */
  public int getContrast() { return contrast; }

  /** Gets color model (RGB_MODEL, HSV_MODEL or COMPOSITE_MODEL). */
  public int getModel() { return model; }

  /** Gets RealType mapped to red. */
  public RealType getRed() { return red; }

  /** Gets RealType mapped to green. */
  public RealType getGreen() { return green; }

  /** Gets RealType mapped to blue. */
  public RealType getBlue() { return blue; }

  /** Gets color mapping minimums. */
  public double[] getLo() {
    ScalarMap[] maps = getMaps();
    if (maps == null) return lo;
    double[] min = new double[maps.length];
    for (int i=0; i<maps.length; i++) min[i] = maps[i].getRange()[0];
    return min;
  }

  /** Gets color mapping maximums. */
  public double[] getHi() {
    ScalarMap[] maps = getMaps();
    if (maps == null) return hi;
    double[] max = new double[maps.length];
    for (int i=0; i<maps.length; i++) max[i] = maps[i].getRange()[1];
    return max;
  }

  /** Gets whether each color mapping has a fixed range. */
  public boolean[] getFixed() {
    ScalarMap[] maps = getMaps();
    if (maps == null) return fixed;
    boolean[] fix = new boolean[maps.length];
    for (int i=0; i<maps.length; i++) fix[i] = !maps[i].isAutoScale();
    return fix;
  }

  /** Gets current color tables. */
  public float[][][] getTables() {
    ScalarMap[] maps = getMaps();
    if (maps == null) return colorTables;
    float[][][] tables = new float[maps.length][][];
    for (int i=0; i<maps.length; i++) {
      BaseColorControl cc = (BaseColorControl) maps[i].getControl();
      tables[i] = cc.getTable();
    }
    return tables;
  }


  // -- ColorHandler API methods - state logic --

  /** Writes the current state to the given OME-CA XML object. */
  public void saveState() {
    window.setAttr("brightness", "" + brightness);
    window.setAttr("contrast", "" + contrast);
    window.setAttr("colorModel", "" + model);
    window.setAttr("red", red == null ? "null" : red.getName());
    window.setAttr("green", green == null ? "null" : green.getName());
    window.setAttr("blue", blue == null ? "null" : blue.getName());
    window.setAttr("colorMin", ObjectUtil.arrayToString(getLo()));
    window.setAttr("colorMax", ObjectUtil.arrayToString(getHi()));
    window.setAttr("colorFixed", ObjectUtil.arrayToString(getFixed()));

    float[][][] tables = getTables();
    if (tables == null) {
      window.setAttr("tables", "null");
    }
    else {
      window.setAttr("tables", "" + tables.length);
      for (int i=0; i<tables.length; i++) {
        if (tables[i] == null) window.setAttr("table" + i, "null");
        else {
          window.setAttr("table" + i, "" + tables[i].length);
          for (int j=0; j<tables[i].length; j++) {
            if (tables[i][j] == null) {
              window.setAttr("table" + i + "-" + j, "null");
            }
            else {
              window.setAttr("table" + i + "-" + j,
                ObjectUtil.arrayToString(tables[i][j]));
            }
          }
        }
      }
    }
  }

  /** Restores the current state from the given OME-CA XML object. */
  public void restoreState() {
    brightness = Integer.parseInt(window.getAttr("brightness"));
    contrast = Integer.parseInt(window.getAttr("contrast"));
    model = Integer.parseInt(window.getAttr("colorModel"));

    String r = window.getAttr("red");
    String g = window.getAttr("green");
    String b = window.getAttr("blue");
    red = r.equals("null") ? null : RealType.getRealType(r);
    green = g.equals("null") ? null : RealType.getRealType(g);
    blue = b.equals("null") ? null : RealType.getRealType(b);

    lo = ObjectUtil.stringToDoubleArray(window.getAttr("colorMin"));
    hi = ObjectUtil.stringToDoubleArray(window.getAttr("colorMax"));
    fixed = ObjectUtil.stringToBooleanArray(window.getAttr("colorFixed"));

    colorTables = null;
    String s = window.getAttr("tables");
    if (!s.equals("null")) {
      int ilen = Integer.parseInt(s);
      colorTables = new float[ilen][][];
      for (int i=0; i<ilen; i++) {
        s = window.getAttr("table" + i);
        if (s.equals("null")) colorTables[i] = null;
        else {
          int jlen = Integer.parseInt(s);
          colorTables[i] = new float[jlen][];
          for (int j=0; j<jlen; j++) {
            colorTables[i][j] = ObjectUtil.stringToFloatArray(
              window.getAttr("table" + i + "-" + j));
          }
        }
      }
    }
  }

  /** Tests whether two objects are in equivalent states. */
  public boolean matches(ColorHandler handler) {
    return brightness == handler.brightness &&
      contrast == handler.contrast &&
      model == handler.model &&
      ObjectUtil.objectsEqual(red, handler.red) &&
      ObjectUtil.objectsEqual(green, handler.green) &&
      ObjectUtil.objectsEqual(blue, handler.blue) &&
      ObjectUtil.arraysEqual(getLo(), handler.getLo()) &&
      ObjectUtil.arraysEqual(getHi(), handler.getHi()) &&
      ObjectUtil.arraysEqual(getFixed(), handler.getFixed()) &&
      ObjectUtil.arraysEqual(getTables(), handler.getTables());
  }

  /**
   * Modifies this object's state to match that of the given object.
   * If the argument is null, the object is initialized according to
   * its current state instead.
   */ 
  public void initState(ColorHandler handler) {
    if (handler != null) {
      brightness = handler.brightness;
      contrast = handler.contrast;
      model = handler.model;
      red = handler.red;
      green = handler.green;
      blue = handler.blue;
      lo = handler.getLo();
      hi = handler.getHi();
      fixed = handler.getFixed();
      colorTables = handler.getTables();
    }

    if (panel == null) panel = new ColorPanel(this);
  }

}
