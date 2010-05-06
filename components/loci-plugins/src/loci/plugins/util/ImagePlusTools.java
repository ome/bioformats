//
// ImagePlusTools.java
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

import ij.CompositeImage;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.MinMaxCalculator;
import loci.formats.meta.MetadataRetrieve;

/**
 * Utility methods for working with ImagePlus objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/util/ImagePlusTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/util/ImagePlusTools.java">SVN</a></dd></dl>
 */
public final class ImagePlusTools {

  // -- Constructor --

  private ImagePlusTools() { }

  // -- Utility methods --

  /**
   * Converts the given array of ImageProcessors into a single-slice
   * RGB ImagePlus.
   */
  public static ImagePlus makeRGB(ImageProcessor[] p) {
    return makeRGB("", p);
  }

  /**
   * Converts the given array of ImageProcessors into a single-slice
   * RGB ImagePlus.
   */
  public static ImagePlus makeRGB(String title, ImageProcessor[] p) {
    if (p.length == 1) return new ImagePlus(title, p[0]);

    // check that all processors are of the same type and size
    boolean sameType = true;
    int width = p[0].getWidth();
    int height = p[0].getHeight();
    boolean byteProc = p[0] instanceof ByteProcessor;
    boolean shortProc = p[0] instanceof ShortProcessor;
    boolean floatProc = p[0] instanceof FloatProcessor;
    for (int i=1; i<p.length; i++) {
      int w = p[i].getWidth();
      int h = p[i].getHeight();
      boolean b = p[i] instanceof ByteProcessor;
      boolean s = p[i] instanceof ShortProcessor;
      boolean f = p[i] instanceof FloatProcessor;
      if (w != width || h != height || b != byteProc || s != shortProc ||
        f != floatProc)
      {
        sameType = false;
        break;
      }
    }

    if (!sameType || p.length > 4 || p[0] instanceof ColorProcessor) {
      return null;
    }

    ImagePlus imp = null;

    if (p.length < 4 && byteProc) {
      ColorProcessor cp = new ColorProcessor(width, height);
      byte[][] bytes = new byte[p.length][];
      for (int i=0; i<p.length; i++) {
        bytes[i] = (byte[]) p[i].getPixels();
      }
      cp.setRGB(bytes[0], bytes[1], bytes.length == 3 ? bytes[2] :
        new byte[width * height]);
      imp = new ImagePlus(title, cp);
    }
    else if (p.length <= 7) {
      ImageStack tmpStack = new ImageStack(width, height);
      for (int i=0; i<p.length; i++) tmpStack.addSlice("", p[i]);

      ImagePlus ii = new ImagePlus(title, tmpStack);
      imp = new CompositeImage(ii, CompositeImage.COMPOSITE);
    }

    return imp;
  }

  /** Applies spatial calibrations to an image stack. */
  public static void applyCalibration(MetadataRetrieve retrieve,
    ImagePlus imp, int series)
  {
    double xcal = Double.NaN, ycal = Double.NaN;
    double zcal = Double.NaN, tcal = Double.NaN;

    Double xd = retrieve.getPixelsPhysicalSizeX(series);
    if (xd != null) xcal = xd.floatValue();
    Double yd = retrieve.getPixelsPhysicalSizeY(series);
    if (yd != null) ycal = yd.floatValue();
    Double zd = retrieve.getPixelsPhysicalSizeZ(series);
    if (zd != null) zcal = zd.floatValue();
    Double td = retrieve.getPixelsTimeIncrement(series);
    if (td != null) tcal = td.floatValue();

    if (xcal == xcal || ycal == ycal || zcal == zcal || tcal == tcal) {
      // if the physical width or physical height are missing, assume that
      // the width and height are equal
      if (xcal != xcal) xcal = ycal;
      if (ycal != ycal) ycal = xcal;

      Calibration cal = new Calibration();
      cal.setUnit("micron");
      cal.pixelWidth = xcal;
      cal.pixelHeight = ycal;
      cal.pixelDepth = zcal;
      cal.frameInterval = tcal;
      imp.setCalibration(cal);
    }

    String type = retrieve.getPixelsType(series).toString();
    int pixelType = FormatTools.pixelTypeFromString(type);

    boolean signed = pixelType == FormatTools.INT8 ||
      pixelType == FormatTools.INT16 || pixelType == FormatTools.INT32;

    // set calibration function, so that both signed and unsigned pixel
    // values are shown
    if (signed) {
      int bitsPerPixel = FormatTools.getBytesPerPixel(pixelType) * 8;
      double min = -1 * Math.pow(2, bitsPerPixel - 1);
      imp.getLocalCalibration().setFunction(Calibration.STRAIGHT_LINE,
        new double[] {min, 1.0}, "gray value");
    }
  }

  /**
   * Adjusts the color range of the given image stack
   * to match its global minimum and maximum.
   */
  public static void adjustColorRange(ImagePlus imp, IFormatReader r) {
    ImageStack s = imp.getStack();
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;

    if (r instanceof MinMaxCalculator) {
      for (int c=0; c<r.getSizeC(); c++) {
        try {
          Double cMin = ((MinMaxCalculator) r).getChannelGlobalMinimum(c);
          Double cMax = ((MinMaxCalculator) r).getChannelGlobalMaximum(c);

          if (cMin != null && cMin.doubleValue() < min) {
            min = cMin.doubleValue();
          }
          if (cMax != null && cMax.doubleValue() > max) {
            max = cMax.doubleValue();
          }
        }
        catch (FormatException e) { }
        catch (IOException e) { }
      }
    }
    if (min == Double.MAX_VALUE && max == Double.MIN_VALUE) {
      for (int i=0; i<s.getSize(); i++) {
        ImageProcessor p = s.getProcessor(i + 1);
        p.resetMinAndMax();
        if (p.getMin() < min) min = p.getMin();
        if (p.getMax() > max) max = p.getMax();
      }
    }

    ImageProcessor p = imp.getProcessor();
    if (p instanceof ColorProcessor) {
      ((ColorProcessor) p).setMinAndMax(min, max, 3);
    }
    else p.setMinAndMax(min, max);
    imp.setProcessor(imp.getTitle(), p);
  }

  /** Reorder the given ImagePlus's stack. */
  public static ImagePlus reorder(ImagePlus imp, String origOrder,
    String newOrder)
  {
    ImageStack s = imp.getStack();
    ImageStack newStack = new ImageStack(s.getWidth(), s.getHeight());

    int z = imp.getNSlices();
    int c = imp.getNChannels();
    int t = imp.getNFrames();

    int stackSize = s.getSize();
    for (int i=0; i<stackSize; i++) {
      int ndx = FormatTools.getReorderedIndex(
        origOrder, newOrder, z, c, t, stackSize, i);
      newStack.addSlice(s.getSliceLabel(ndx + 1), s.getProcessor(ndx + 1));
    }
    ImagePlus p = new ImagePlus(imp.getTitle(), newStack);
    p.setDimensions(c, z, t);
    p.setCalibration(imp.getCalibration());
    p.setFileInfo(imp.getOriginalFileInfo());
    return p;
  }

}
