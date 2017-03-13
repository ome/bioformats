/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.plugins.in;

import ij.CompositeImage;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.LUT;

import java.io.IOException;

import loci.common.Region;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.plugins.BF;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for testing the Bio-Formats Importer's
 * autoscaling behavior in various cases.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class AutoscaleTest {

  // -- Constants --

  protected static final Logger LOGGER =
    LoggerFactory.getLogger(AutoscaleTest.class);

  private static final int WIDTH = 51, HEIGHT = 16;
  private static final int CROP_X = 5, CROP_Y = 10;
  private static final Region CROP_REGION =
    new Region(CROP_X, CROP_Y, WIDTH - 2 * CROP_X, HEIGHT - CROP_Y);

  // -- AutoscaleTest methods --

  @Test
  public void testAutoscale() {
    // pixel type shorthands
    final int ui08 = FormatTools.UINT8;
    final int ui16 = FormatTools.UINT16;
    final int ui32 = FormatTools.UINT32;
    final int si08 = FormatTools.INT8;
    final int si16 = FormatTools.INT16;
    final int si32 = FormatTools.INT32;
    final int  f32 = FormatTools.FLOAT;
    final int  f64 = FormatTools.DOUBLE;

    // min and max values for various pixel types
    final double sMin08 = -128.0,        sMax08 = 127.0;
    final double sMin16 = -32768.0,      sMax16 = 32767.0;
    final double sMin32 = -2147483648.0, sMax32 = 2147483647.0;
    final double uMin08 = 0.0,           uMax08 = 255.0;
    final double uMin16 = 0.0,           uMax16 = 65535.0;
    final double uMin32 = 0.0,           uMax32 = 4294967295.0;

    // expected values
    final double noCal = 0.0;
    final double eMin = 0.0, eMax = WIDTH - 1;
    final double esMax08 = sMin08 + eMax;
    final double esMax16 = sMin16 + eMax;
    final double esMax32 = sMin32 + eMax;

    // -- vanilla images --

    // unsigned integer types, no autoscaling
    test (ui08,  noCal, uMin08, uMax08);
    test (ui16,  noCal, uMin16, uMax16);
    test (ui32,  noCal, uMin32, uMax32 + 1); // !!
    // signed integer types, no autoscaling
    test (si08, sMin08, sMin08, sMax08);
    test (si16, sMin16, sMin16, sMax16);
    test (si32,  noCal, sMin32, sMax32 + 1); // !!
    // floating point types, no autoscaling (though autoscaling is forced)
    test ( f32,  noCal,   eMin,   eMax);
    test ( f64,  noCal,   eMin,   eMax);
    // unsigned integer types, with autoscaling
    testA(ui08,  noCal,   eMin,   eMax);
    testA(ui16,  noCal,   eMin,   eMax);
    testA(ui32,  noCal,   eMin,   eMax);
    // signed integer types, with autoscaling
    testA(si08, sMin08, sMin08,    0.0);
    testA(si16, sMin16, sMin16,    0.0);
    testA(si32,  noCal, sMin32,    0.0);
    // floating point types, with autoscaling
    testA( f32,  noCal,   eMin,   eMax);
    testA( f64,  noCal,   eMin,   eMax);
    // unsigned integer types, with autoscaling and crop
    testC(ui08,  noCal,   eMin,   eMax);
    testC(ui16,  noCal,   eMin,   eMax);
    testC(ui32,  noCal,   eMin,   eMax);
    // signed integer types, with autoscaling and crop
    testC(si08, sMin08, sMin08, esMax08);
    testC(si16, sMin16, sMin16, esMax16);
    //testC(si32,  noCal, sMin32, esMax32);
    // floating point types, with autoscaling and crop
    testC( f32,  noCal,   eMin,   eMax);
    testC( f64,  noCal,   eMin,   eMax);

    // -- composite images --

    // unsigned integer types, no autoscaling
    test (ui08,  noCal, uMin08, uMax08, uMin08, uMax08, uMin08, uMax08);
    test (ui16,  noCal, uMin16, uMax16, uMin16, uMax16, uMin16, uMax16);
    test (ui32,  noCal, uMin32, uMax32, uMin32, uMax32, uMin32, uMax32);
    // signed integer types, no autoscaling
    test (si08, sMin08, sMin08, sMax08, sMin08, sMax08, sMin08, sMax08);
    test (si16, sMin16, sMin16, sMax16, sMin16, sMax16, sMin16, sMax16);
    test (si32,  noCal, sMin32, sMax32, sMin32, sMax32, sMin32, sMax32);
    // floating point types, no autoscaling (though autoscaling is forced)
    test ( f32,  noCal,   eMin,   eMax,   eMin,   eMax,   eMin,   eMax);
    test ( f64,  noCal,   eMin,   eMax,   eMin,   eMax,   eMin,   eMax);
    // unsigned integer types, with autoscaling
    testA(ui08,  noCal,   eMin,   eMax,   eMin,   eMax,   eMin,   eMax);
    testA(ui16,  noCal,   eMin,   eMax,   eMin,   eMax,   eMin,   eMax);
    testA(ui32,  noCal,   eMin,   eMax,   eMin,   eMax,   eMin,   eMax);
    // signed integer types, with autoscaling
    testA(si08, sMin08, sMin08,    0.0, sMin08,    1.0, sMin08,    2.0);
    testA(si16, sMin16, sMin16,    0.0, sMin16,    1.0, sMin16,    2.0);
    testA(si32,  noCal, sMin32,    0.0, sMin32,    1.0, sMin32,    2.0);
    // floating point types, with autoscaling
    testA( f32,  noCal,   eMin,   eMax,   eMin,   eMax,   eMin,   eMax);
    testA( f64,  noCal,   eMin,   eMax,   eMin,   eMax,   eMin,   eMax);
    // unsigned integer types, with autoscaling and crop
    testC(ui08,  noCal,   eMin,   eMax,   eMin,   eMax,   eMin,   eMax);
    testC(ui16,  noCal,   eMin,   eMax,   eMin,   eMax,   eMin,   eMax);
    testC(ui32,  noCal,   eMin,   eMax,   eMin,   eMax,   eMin,   eMax);
    // signed integer types, with autoscaling and crop
    testC(si08, sMin08, sMin08, esMax08, sMin08, esMax08, sMin08, esMax08);
    testC(si16, sMin16, sMin16, esMax16, sMin16, esMax16, sMin16, esMax16);
    testC(si32,  noCal, sMin32, esMax32, sMin32, esMax32, sMin32, esMax32);
    // floating point types, with autoscaling and crop
    testC( f32,  noCal,   eMin,   eMax,   eMin,   eMax,   eMin,   eMax);
    testC( f64,  noCal,   eMin,   eMax,   eMin,   eMax,   eMin,   eMax);
  }

  /** Tester for vanilla (single-channel) images, no autoscale, no crop. */
  private void test(int pixelType, double calib, double min, double max) {
    test(pixelType, false, false, calib, min, max);
  }

  /** Tester for vanilla (single-channel) images, with autoscale, no crop. */
  private void testA(int pixelType, double calib, double min, double max) {
    test(pixelType, true, false, calib, min, max);
  }

  /** Tester for vanilla (single-channel) images, with autoscale and crop. */
  private void testC(int pixelType, double calib, double min, double max) {
    test(pixelType, true, true, calib, min + CROP_X, max - CROP_X);
  }

  /** Tester for composite (multi-channel) images, no autoscale, no crop. */
  private void test(int pixelType, double calib, double min1, double max1,
    double min2, double max2, double min3, double max3)
  {
    test(pixelType, false, false, calib, min1, max1, min2, max2, min3, max3);
  }

  /** Tester for composite (multi-channel) images, with autoscale, no crop. */
  private void testA(int pixelType, double calib, double min1, double max1,
    double min2, double max2, double min3, double max3)
  {
    test(pixelType, true, false, calib, min1, max1, min2, max2, min3, max3);
  }

  /** Tester for composite (multi-channel) images, with autoscale and crop. */
  private void testC(int pixelType, double calib, double min1, double max1,
    double min2, double max2, double min3, double max3)
  {
    test(pixelType, true, true, calib,
      min1 + CROP_X, max1 - CROP_X,
      min2 + CROP_X, max2 - CROP_X,
      min3 + CROP_X, max3 - CROP_X);
  }

  /** Tester for vanilla (single-channel) images. */
  private void test(int pixelType, boolean autoscale, boolean crop,
    double calib, double min, double max)
  {
    final ImagePlus imp = setup(pixelType, autoscale, crop, calib, 1);
    final double actualMin = imp.getDisplayRangeMin();
    final double actualMax = imp.getDisplayRangeMax();
    assertEquals(min - calib, actualMin, 0.0);
    assertEquals(max - calib, actualMax, 0.0);
  }

  /** Tester for composite (multi-channel) images. */
  private void test(int pixelType, boolean autoscale, boolean crop,
    double calib, double min1, double max1, double min2, double max2,
    double min3, double max3)
  {
    final ImagePlus imp = setup(pixelType, autoscale, crop, calib, 3);
    final CompositeImage ci = (CompositeImage) imp;
    final LUT lut1 = ci.getChannelLut(1);
    final LUT lut2 = ci.getChannelLut(2);
    final LUT lut3 = ci.getChannelLut(3);
    assertEquals(min1 - calib, lut1.min, 0.0);
    assertEquals(max1 - calib, lut1.max, 0.0);
    assertEquals(min2 - calib, lut2.min, 0.0);
    assertEquals(max2 - calib, lut2.max, 0.0);
    assertEquals(min3 - calib, lut3.min, 0.0);
    assertEquals(max3 - calib, lut3.max, 0.0);
  }

  /** Reads the image and performs some initial tests. */
  private ImagePlus setup(int pixelType, boolean autoscale,
    boolean crop, double calib, int sizeC)
  {
    final String pix = FormatTools.getPixelTypeString(pixelType);
    final String id = "autoscale&pixelType=" + pix +
      "&sizeX=" + WIDTH + "&sizeY=" + HEIGHT + "&sizeC=" + sizeC + ".fake";

    ImagePlus[] imps = null;
    try {
      final ImporterOptions options = new ImporterOptions();
      options.setId(id);
      options.setAutoscale(autoscale);
      options.setCrop(crop);
      if (crop) options.setCropRegion(0, CROP_REGION);
      imps = BF.openImagePlus(options);
    }
    catch (FormatException exc) {
      fail(exc.getMessage());
      LOGGER.debug("", exc);
    }
    catch (IOException exc) {
      fail(exc.getMessage());
      LOGGER.debug("", exc);
    }
    assertTrue(imps != null && imps.length == 1);
    final ImagePlus imp = imps[0];
    assertEquals(sizeC > 1, imp instanceof CompositeImage);

    // check calibration function
    final Calibration cal = imp.getCalibration();
    final int calFunction = cal.getFunction();
    if (calib == 0) {
      assertFalse(calFunction == Calibration.STRAIGHT_LINE);
    }
    else {
      assertTrue(calFunction == Calibration.STRAIGHT_LINE);
      double[] coeffs = cal.getCoefficients();
      assertTrue(coeffs.length >= 1);
      assertEquals(calib, coeffs[0], 0.0);
    }

    dump(pix, sizeC, autoscale, crop, imp);

    return imp;
  }

  private void dump(String pix, int sizeC,
    boolean autoscale, boolean crop, ImagePlus imp)
  {
    if (!LOGGER.isDebugEnabled()) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("pixelType=");
    for (int s=pix.length(); s<6; s++) sb.append(" "); // fixed width
    sb.append(pix);
    sb.append(", sizeC=");
    sb.append(sizeC);
    sb.append(", autoscale=");
    sb.append(autoscale ? " true" : "false");
    sb.append(", crop=");
    sb.append(crop ? " true" : "false");
    sb.append(": image=");
    if (imp instanceof CompositeImage) {
      final CompositeImage ci = (CompositeImage) imp;
      sb.append("composite");
      for (int c=0; c<sizeC; c++) {
        final LUT lut = ci.getChannelLut(c + 1);
        sb.append("; min");
        sb.append(c);
        sb.append("=");
        sb.append(lut.min);
        sb.append("; max");
        sb.append(c);
        sb.append("=");
        sb.append(lut.max);
      }
    }
    else {
      sb.append("  vanilla");
      final double min = imp.getDisplayRangeMin();
      final double max = imp.getDisplayRangeMax();
      sb.append("; min=");
      sb.append(min);
      sb.append("; max=");
      sb.append(max);
    }
    final Calibration cal = imp.getCalibration();
    if (cal.getFunction() == Calibration.STRAIGHT_LINE) {
      sb.append("; calibration=");
      double[] coeffs = cal.getCoefficients();
      sb.append(coeffs[0]);
    }
    LOGGER.debug(sb.toString());
  }

}
