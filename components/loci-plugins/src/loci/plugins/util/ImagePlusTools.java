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

import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.FormatTools;
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

    final boolean xcalPresent = !Double.isNaN(xcal);
    final boolean ycalPresent = !Double.isNaN(ycal);
    final boolean zcalPresent = !Double.isNaN(zcal);
    final boolean tcalPresent = !Double.isNaN(tcal);

    // if the physical width or physical height are missing,
    // assume that the width and height are equal
    if (xcalPresent && !ycalPresent) ycal = xcal;
    else if (ycalPresent && !xcalPresent) xcal = ycal;

    final boolean hasSpatial = xcalPresent || ycalPresent || zcalPresent;
    final boolean hasCalibration = hasSpatial || ycalPresent;

    if (hasCalibration) {
      // set calibration only if at least one value is present
      Calibration cal = new Calibration();
      if (hasSpatial) cal.setUnit("micron");
      if (xcalPresent) cal.pixelWidth = xcal;
      if (ycalPresent) cal.pixelHeight = ycal;
      if (zcalPresent) cal.pixelDepth = zcal;
      if (tcalPresent) cal.frameInterval = tcal;
      imp.setCalibration(cal);
    }

    String type = retrieve.getPixelsType(series).toString();
    int pixelType = FormatTools.pixelTypeFromString(type);

    // NB: INT32 is represented with FloatProcessor, so no need to calibrate.
    boolean signed = pixelType == FormatTools.INT8 ||
      pixelType == FormatTools.INT16; // || pixelType == FormatTools.INT32;

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
   * Autoscales the color range of the given image stack
   * to match its global minimum and maximum.
   */
  public static void adjustColorRange(ImagePlus imp,
    MinMaxCalculator minMaxCalc)
  {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;

    // try to grab min and max values from the MinMaxCalculator
    if (minMaxCalc != null) {
      for (int c=0; c<minMaxCalc.getSizeC(); c++) {
        try {
          Double cMin = minMaxCalc.getChannelGlobalMinimum(c);
          Double cMax = minMaxCalc.getChannelGlobalMaximum(c);

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

    // couldn't find min and max values; determine manually
    if (min == Double.POSITIVE_INFINITY && max == Double.NEGATIVE_INFINITY) {
      ImageStack stack = imp.getStack();
      for (int i=0; i<stack.getSize(); i++) {
        ImageProcessor p = stack.getProcessor(i + 1);
        p.resetMinAndMax();
        if (p.getMin() < min) min = p.getMin();
        if (p.getMax() > max) max = p.getMax();
      }
    }

    // assign min/max range to the active image processor
    ImageProcessor p = imp.getProcessor();
    if (p instanceof ColorProcessor) {
      ((ColorProcessor) p).setMinAndMax(min, max, 3);
    }
    else p.setMinAndMax(min, max);
    // HACK: refresh display
    //imp.setProcessor(imp.getTitle(), p);
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
