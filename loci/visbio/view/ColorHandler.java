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

import java.rmi.RemoteException;
import java.util.Arrays;
import loci.visbio.VisBioFrame;
import loci.visbio.data.*;
import loci.visbio.util.*;
import visad.*;

/** Provides logic for controlling a TransformLink's color settings. */
public class ColorHandler {

  // -- Constants --

  /** Starting brightness value. */
  protected static final int NORMAL_BRIGHTNESS = 128;

  /** Starting contrast value. */
  protected static final int NORMAL_CONTRAST = 128;

  /** Starting opacity value. */
  protected static final int NORMAL_OPACITY = 256;


  // -- Fields --

  /** Associated link between data object and display. */
  protected TransformLink link;

  /** Dialog pane for adjusting color settings. */
  protected ColorPane colorPane;

  /** Brightness and contrast of images. */
  protected int brightness, contrast;

  /** Opacity value. */
  protected int opacityValue;

  /** Transparency model (CONSTANT_ALPHA or CURVED_ALPHA). */
  protected int opacityModel;

  /** Color model (RGB_MODEL, HSV_MODEL or COMPOSITE_MODEL). */
  protected int colorModel;

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

  /** Creates a color handler for the given transform link. */
  public ColorHandler(TransformLink link) {
    this.link = link;

    // default color settings
    brightness = NORMAL_BRIGHTNESS;
    contrast = NORMAL_CONTRAST;
    opacityValue = NORMAL_OPACITY;
    opacityModel = ColorUtil.CONSTANT_ALPHA;
    colorModel = ColorUtil.RGB_MODEL;
    red = green = blue = ColorUtil.CLEAR;
  }


  // -- ColorHandler API methods --

  /** Gets associated transform link. */
  public TransformLink getLink() { return link; }

  /** Gets GUI control pane for this color handler. */
  public ColorPane getColorPane() { return colorPane; }

  /** Gets the VisBio display window affected by this color handler. */
  public DisplayWindow getWindow() { return link.getHandler().getWindow(); }

  /** Gets color mappings for this color handler's transform link. */
  public ScalarMap[] getMaps() {
    DisplayImpl display = getWindow().getDisplay();
    DataTransform trans = link.getTransform();
    if (!(trans instanceof ImageTransform)) return null;
    ImageTransform it = (ImageTransform) trans;
    return VisUtil.getMaps(display, it.getRangeTypes(),
      new DisplayRealType[] {Display.RGB, Display.RGBA});
  }

  /** Guesses at good initial color mappings and applies them. */
  public void initColors() {
    refreshPreviewData();

    // apply default color table behavior
    red = colorPane.getRed();
    green = colorPane.getGreen();
    blue = colorPane.getBlue();
    setColors(brightness, contrast, colorModel, red, green, blue, true);

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

  /** Refreshes preview data from the transform link. */
  public void refreshPreviewData() {
    DataTransform trans = link.getTransform();
    ThumbnailHandler thumbs = trans.getThumbHandler();
    if (thumbs != null) {
      int[] pos = link.getHandler().getPos(trans);
      FlatField ff = thumbs.getThumb(pos);
      try {
        if (trans instanceof ImageTransform) {
          ImageTransform it = (ImageTransform) trans;
          ff = VisUtil.switchType(ff, it.getType());
        }
        colorPane.setPreviewData(ff);
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
  }

  /**
   * Displays the color dialog pane onscreen and
   * alters the color scheme as requested.
   */
  public void showColorDialog() {
    refreshPreviewData();
    DisplayWindow window = getWindow();
    if (colorPane.showDialog(window) == ColorPane.APPROVE_OPTION) {
      DisplayImpl d = window.getDisplay();
      VisUtil.setDisplayDisabled(d, true);
      setColors(colorPane.getBrightness(), colorPane.getContrast(),
        colorPane.getColorMode(), colorPane.getRed(), colorPane.getGreen(),
        colorPane.getBlue(), false);
      setOpacity(colorPane.getOpacityValue(),
        colorPane.getOpacityModel(), false);
      setRanges(colorPane.getLo(), colorPane.getHi(), colorPane.getFixed());
      setTables(colorPane.getTables());
      VisUtil.setDisplayDisabled(d, false);
    }
  }

  /** Updates color settings to those given. */
  public void setColors(int brightness, int contrast, int colorModel,
    RealType red, RealType green, RealType blue, boolean compute)
  {
    this.brightness = brightness;
    this.contrast = contrast;
    this.colorModel = colorModel;
    this.red = red;
    this.green = green;
    this.blue = blue;
    if (compute) {
      setTables(ColorUtil.computeColorTables(getMaps(),
        brightness, contrast, colorModel, red, green, blue));
    }
  }

  /**
   * Sets opacity.
   * @param opacityValue How opaque data should be, ranging from 0 - 256
   * @param opacityModel ColorUtil.CONSTANT_ALPHA or ColorUtil.CURVED_ALPHA
   * @param compute Whether to actually update the data
   */
  public void setOpacity(int opacityValue, int opacityModel, boolean compute) {
    this.opacityValue = opacityValue;
    this.opacityModel = opacityModel;
    if (compute) {
      float[] alpha = ColorUtil.computeAlphaTable(opacityValue, opacityModel);
      ColorUtil.setAlphaTable(getMaps(), alpha);
    }
  }

  /** Updates map ranges to those given. */
  public void setRanges(double[] lo, double[] hi, boolean[] fixed) {
    ColorUtil.setColorRanges(getWindow().getDisplay(),
      getMaps(), lo, hi, fixed);
  }

  /** Updates color tables to those given. */
  public void setTables(float[][][] tables) {
    DisplayWindow window = getWindow();
    ColorUtil.setColorMode(window.getDisplay(), colorModel);
    ColorUtil.setColorTables(getMaps(), tables);
    VisBioFrame bio = window.getVisBio();
    bio.generateEvent(bio.getManager(DisplayManager.class),
      "color adjustment for " + window.getName(), true);
  }

  /** Recomputes autoscaled color range bounds. */
  public void reAutoScale() {
    DisplayImpl display = getWindow().getDisplay();
    ScalarMap[] maps = getMaps();
    for (int i=0; i<maps.length; i++) {
      if (fixed[i]) continue;
      ColorUtil.reAutoScale(display, maps[i]);
    }
  }

  /** Gets brightness value. */
  public int getBrightness() { return brightness; }

  /** Gets contrast value. */
  public int getContrast() { return contrast; }

  /** Gets opacity value. */
  public int getOpacityValue() { return opacityValue; }

  /** Gets opacity model (CONSTANT_ALPHA or CURVED_ALPHA). */
  public int getOpacityModel() { return opacityModel; }

  /** Gets color model (RGB_MODEL, HSV_MODEL or COMPOSITE_MODEL). */
  public int getColorModel() { return colorModel; }

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

  /** Writes the current state to the given XML object. */
  public void saveState(String attrName) {
    DisplayWindow window = getWindow();
    window.setAttr(attrName + "_brightness", "" + brightness);
    window.setAttr(attrName + "_contrast", "" + contrast);
    window.setAttr(attrName + "_opacityValue", "" + opacityValue);
    window.setAttr(attrName + "_opacityModel", "" + opacityModel);
    window.setAttr(attrName + "_colorModel", "" + colorModel);

    String r = red == null ? "null" : red.getName();
    String g = green == null ? "null" : green.getName();
    String b = blue == null ? "null" : blue.getName();
    window.setAttr(attrName + "_red", r);
    window.setAttr(attrName + "_green", g);
    window.setAttr(attrName + "_blue", b);

    String min = ObjectUtil.arrayToString(getLo());
    String max = ObjectUtil.arrayToString(getHi());
    String fix = ObjectUtil.arrayToString(getFixed());
    window.setAttr(attrName + "_colorMin", min);
    window.setAttr(attrName + "_colorMax", max);
    window.setAttr(attrName + "_colorFixed", fix);

    float[][][] tables = getTables();
    if (tables == null) {
      window.setAttr(attrName + "_tables", "null");
    }
    else {
      window.setAttr(attrName + "_tables", "" + tables.length);
      for (int i=0; i<tables.length; i++) {
        if (tables[i] == null) window.setAttr(attrName + "_table" + i, "null");
        else {
          window.setAttr(attrName + "_table" + i, "" + tables[i].length);
          for (int j=0; j<tables[i].length; j++) {
            if (tables[i][j] == null) {
              window.setAttr(attrName + "_table" + i + "-" + j, "null");
            }
            else {
              window.setAttr(attrName + "_table" + i + "-" + j,
                ObjectUtil.arrayToString(tables[i][j]));
            }
          }
        }
      }
    }
  }

  /** Restores the current state from the given XML object. */
  public void restoreState(String attrName) {
    DisplayWindow window = getWindow();
    brightness = Integer.parseInt(window.getAttr(attrName + "_brightness"));
    contrast = Integer.parseInt(window.getAttr(attrName + "_contrast"));
    opacityValue = Integer.parseInt(
      window.getAttr(attrName + "_opacityValue"));
    opacityModel = Integer.parseInt(
      window.getAttr(attrName + "_opacityModel"));
    colorModel = Integer.parseInt(window.getAttr(attrName + "_colorModel"));

    String r = window.getAttr(attrName + "_red");
    String g = window.getAttr(attrName + "_green");
    String b = window.getAttr(attrName + "_blue");
    red = r.equals("null") ? null : RealType.getRealType(r);
    green = g.equals("null") ? null : RealType.getRealType(g);
    blue = b.equals("null") ? null : RealType.getRealType(b);

    String min = window.getAttr(attrName + "_colorMin");
    String max = window.getAttr(attrName + "_colorMax");
    String fix = window.getAttr(attrName + "_colorFixed");
    lo = ObjectUtil.stringToDoubleArray(min);
    hi = ObjectUtil.stringToDoubleArray(max);
    fixed = ObjectUtil.stringToBooleanArray(fix);

    colorTables = null;
    String s = window.getAttr(attrName + "_tables");
    if (!s.equals("null")) {
      int ilen = Integer.parseInt(s);
      colorTables = new float[ilen][][];
      for (int i=0; i<ilen; i++) {
        s = window.getAttr(attrName + "_table" + i);
        if (s.equals("null")) colorTables[i] = null;
        else {
          int jlen = Integer.parseInt(s);
          colorTables[i] = new float[jlen][];
          for (int j=0; j<jlen; j++) {
            colorTables[i][j] = ObjectUtil.stringToFloatArray(
              window.getAttr(attrName + "_table" + i + "-" + j));
          }
        }
      }
    }
  }

  /** Tests whether two objects are in equivalent states. */
  public boolean matches(ColorHandler handler) {
    return brightness == handler.brightness &&
      contrast == handler.contrast &&
      opacityValue == handler.opacityValue &&
      opacityModel == handler.opacityModel &&
      colorModel == handler.colorModel &&
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
      opacityValue = handler.opacityValue;
      opacityModel = handler.opacityModel;
      colorModel = handler.colorModel;
      red = handler.red;
      green = handler.green;
      blue = handler.blue;
      lo = handler.getLo();
      hi = handler.getHi();
      fixed = handler.getFixed();
      colorTables = handler.getTables();
    }

    if (colorPane == null) colorPane = new ColorPane(this);
  }

}
