//
// ImageTransform.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

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

import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.rmi.RemoteException;
import loci.visbio.state.SaveException;
import loci.visbio.util.*;
import org.w3c.dom.Element;
import visad.*;

/** ImageTransform is a DataTransform superclass that provides image data. */
public abstract class ImageTransform extends DataTransform {

  // -- Constants --

  /** Micron unit. */
  public static final ScaledUnit MICRON =
    new ScaledUnit(1e-6, SI.meter, "mu"); // VisAD font does not show MU char

  // -- Fields --

  /** Physical image dimensions in microns. */
  protected double micronWidth, micronHeight, micronStep;

  /** X-axis type. */
  protected RealType xType;

  /** Y-axis type. */
  protected RealType yType;

  /** Z-axis type. */
  protected RealType zType;

  /** Range types. */
  private RealType[] rangeTypes;

  /** MathType of generated images. */
  private FunctionType functionType;

  // -- Constructors --

  /** Constructs an uninitialized image transform. */
  public ImageTransform() { this(null, null); }

  /** Creates an image transform with the given transform as its parent. */
  public ImageTransform(DataTransform parent, String name) {
    this(parent, name, Double.NaN, Double.NaN, Double.NaN);
  }

  /**
   * Creates an image transform with the given transform as its parent,
   * that is dimensioned according to the specified values in microns.
   */
  public ImageTransform(DataTransform parent, String name,
    double width, double height, double step)
  {
    super(parent, name);

    // initialize physical dimensions
    if (parent instanceof ImageTransform) {
      ImageTransform it = (ImageTransform) parent;
      // use parent values for physical dimensions if none specified
      if (width != width) width = it.getMicronWidth();
      if (height != height) height = it.getMicronHeight();
      if (step != step) step = it.getMicronStep();
    }
    micronWidth = width;
    micronHeight = height;
    micronStep = step;

    // initialize internal MathTypes
    if (width == width) xType = DataUtil.getRealType(name + "_X_mu", MICRON);
    else xType = DataUtil.getRealType(name + "_X", null);
    if (height == height) yType = DataUtil.getRealType(name + "_Y_mu", MICRON);
    else yType = DataUtil.getRealType(name + "_Y", null);
    if (step == step) zType = DataUtil.getRealType(name + "_Z_mu", MICRON);
    else zType = DataUtil.getRealType(name + "_Z", null);
  }

  // -- ImageTransform API methods --

  /** Gets the width in pixels of each image. */
  public abstract int getImageWidth();

  /** Gets the height in pixels of each image. */
  public abstract int getImageHeight();

  /** Gets number of range components at each pixel. */
  public abstract int getRangeCount();

  /** Obtains an image from the source(s) at the given dimensional position. */
  public BufferedImage getImage(int[] pos) { return null; }

  /** Gets physical image width in microns. */
  public double getMicronWidth() { return micronWidth; }

  /** Gets physical image height in microns. */
  public double getMicronHeight() { return micronHeight; }

  /** Gets physical distance between image slices in microns. */
  public double getMicronStep() { return micronStep; }

  /**
   * Gets physical distance between image slices in microns for the given axis.
   * The default implementation simply returns the default step value. This
   * method matters because derivative data objects such as DataSamplings could
   * have a different distance between slices depending on the stack axis.
   */
  public double getMicronStep(int axis) { return getMicronStep(); }

  /** Gets the RealType used for the X-axis. */
  public RealType getXType() { return xType; }

  /** Gets the RealType used for the Y-axis. */
  public RealType getYType() { return yType; }

  /** Gets the RealType used for the Z-axis. */
  public RealType getZType() { return zType; }

  /** Gets the RealTypes used for the range components. */
  public RealType[] getRangeTypes() {
    if (rangeTypes == null) {
      rangeTypes = new RealType[getRangeCount()];
      for (int i=0; i<rangeTypes.length; i++) {
        String s = "range" + (i + 1);
        rangeTypes[i] = DataUtil.getRealType(name + "_" + s);
      }
    }
    return rangeTypes;
  }

  /** Gets MathType of images generated by this image transform. */
  public FunctionType getType() {
    if (functionType == null) {
      try {
        RealTupleType domain = new RealTupleType(xType, yType);
        RealTupleType range = new RealTupleType(getRangeTypes());
        functionType = new FunctionType(domain, range);
      }
      catch (VisADException exc) { exc.printStackTrace(); }
    }
    return functionType;
  }

  /**
   * Gets X and Y units for use with image domain sets, computed from micron
   * information. Pixel range is assumed to be from 0 to width-1 (X) and
   * 0 to height-1 (Y).
   */
  public Unit[] getImageUnits() {
    Unit xu = micronWidth == micronWidth ?
      new ScaledUnit(micronWidth / getImageWidth(), MICRON) : null;
    Unit yu = micronHeight == micronHeight ?
      new ScaledUnit(micronHeight / getImageHeight(), MICRON) : null;
    return new Unit[] {xu, yu};
  }

  /**
   * Gets Z unit for use with image stack domain sets, computed from
   * micron information. Z range is assumed to be from 0 to numSlices-1.
   */
  public Unit getZUnit(int axis) {
    if (axis < 0 || axis >= lengths.length) return null;
    if (micronStep != micronStep) return null;
    return new ScaledUnit(getMicronStep(axis), MICRON);
  }

  // -- DataTransform API methods --

  /**
   * Retrieves the data corresponding to the given dimensional position,
   * for the given display dimensionality.
   */
  public Data getData(int[] pos, int dim, DataCache cache) {
    if (dim != 2) return null;
    if (cache != null) return cache.getData(this, pos, null, dim);

    BufferedImage img = getImage(pos);
    if (img == null) return null;
    try { return new ImageFlatField(img); }
    catch (VisADException exc) {
      exc.printStackTrace();
      return null;
    }
    catch (RemoteException exc) {
      exc.printStackTrace();
      return null;
    }
  }

  /** Retrieves a set of mappings for displaying this transform effectively. */
  public ScalarMap[] getSuggestedMaps() {
    RealType[] range = getRangeTypes();
    ScalarMap[] maps = new ScalarMap[2 + range.length];
    try {
      maps[0] = new ScalarMap(xType, Display.XAxis);
      maps[1] = new ScalarMap(yType, Display.YAxis);
      for (int i=0; i<range.length; i++) {
        maps[i + 2] = new ScalarMap(range[i], Display.RGBA);
      }
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    return maps;
  }

  /** Most image transforms are not rendered immediately. */
  public boolean isImmediate() { return false; }

  /** Gets whether this data object can be displayed in 3D. */
  public boolean canDisplay3D() {
    return super.canDisplay3D() || canDisplay2D();
  }

  /** Gets a description of this transform, with HTML markup. */
  public String getHTMLDescription() {
    StringBuffer sb = new StringBuffer();
    int[] len = getLengths();
    String[] dimTypes = getDimTypes();

    int width = getImageWidth();
    int height = getImageHeight();
    int rangeCount = getRangeCount();

    // name
    sb.append(getName());
    sb.append("<p>\n\n");

    // list of dimensional axes
    sb.append("Dimensionality: ");
    sb.append(len.length + 2);
    sb.append("D\n");
    sb.append("<ul>\n");
    BigInteger images = BigInteger.ONE;
    if (len.length > 0) {
      for (int i=0; i<len.length; i++) {
        images = images.multiply(new BigInteger("" + len[i]));
        sb.append("<li>");
        sb.append(len[i]);
        sb.append(" ");
        sb.append(getUnitDescription(dimTypes[i], len[i]));
        sb.append("</li>\n");
      }
    }

    // image resolution
    sb.append("<li>");
    sb.append(width);
    sb.append(" x ");
    sb.append(height);
    sb.append(" pixel");
    if (width * height != 1) sb.append("s");

    // physical width and height in microns
    if (micronWidth == micronWidth && micronHeight == micronHeight) {
      sb.append(" (");
      sb.append(micronWidth);
      sb.append(" x ");
      sb.append(micronHeight);
      sb.append(" " + MU + ")");
    }
    sb.append("</li>\n");

    // physical distance between slices in microns
    if (micronStep == micronStep) {
      sb.append("<li>");
      sb.append(micronStep);
      sb.append(" " + MU + " between slices</li>\n");
    }

    // range component count
    sb.append("<li>");
    sb.append(rangeCount);
    sb.append(" range component");
    if (rangeCount != 1) sb.append("s");
    sb.append("</li>\n");
    sb.append("</ul>\n");

    // image and pixel counts
    BigInteger pixels = images.multiply(new BigInteger("" + width));
    pixels = pixels.multiply(new BigInteger("" + height));
    pixels = pixels.multiply(new BigInteger("" + rangeCount));
    sb.append(images);
    sb.append(" image");
    if (!images.equals(BigInteger.ONE)) sb.append("s");
    sb.append(" totaling ");
    sb.append(MathUtil.getValueWithUnit(pixels, 2));
    sb.append("pixel");
    if (!pixels.equals(BigInteger.ONE)) sb.append("s");
    sb.append(".<p>\n");

    return sb.toString();
  }

  // -- Saveable API methods --

  /**
   * Writes the current state to the given DOM element
   * (a child of "DataTransforms").
   */
  public void saveState(Element el) throws SaveException {
    super.saveState(el);
    el.setAttribute("width", "" + micronWidth);
    el.setAttribute("height", "" + micronHeight);
    el.setAttribute("step", "" + micronStep);
  }

  /**
   * Restores the current state from the given DOM element
   * (a child of "DataTransforms").
   */
  public void restoreState(Element el) throws SaveException {
    super.restoreState(el);
    micronWidth = ObjectUtil.stringToDouble(el.getAttribute("width"));
    micronHeight = ObjectUtil.stringToDouble(el.getAttribute("height"));
    micronStep = ObjectUtil.stringToDouble(el.getAttribute("step"));
  }

}
