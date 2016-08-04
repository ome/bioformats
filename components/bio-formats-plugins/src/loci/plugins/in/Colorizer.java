/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.LUT;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import loci.formats.ChannelFiller;
import loci.formats.DimensionSwapper;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.formats.MinMaxCalculator;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.plugins.BF;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.VirtualImagePlus;

import ome.xml.model.primitives.PositiveFloat;

import ome.units.quantity.Length;
import ome.units.UNITS;

/**
 * Logic for colorizing images.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class Colorizer {

  // -- Constants --

  private static final int BLUE_MIN = 400;
  private static final int BLUE_TO_GREEN_MIN = 500;
  private static final int GREEN_TO_RED_MIN = 560;
  private static final int RED_MAX = 700;

  // -- Fields --

  private ImportProcess process;

  // -- Constructor --

  public Colorizer(ImportProcess process) {
    this.process = process;
  }

  // -- Colorizer methods --

  public List<ImagePlus> applyColors(List<ImagePlus> imps) {
    final ImporterOptions options = process.getOptions();
    final ImageProcessorReader reader = process.getReader();
    final DimensionSwapper dimSwapper = process.getDimensionSwapper();
    final ChannelFiller channelFiller = process.getChannelFiller();
    final ImageReader imageReader = process.getImageReader();

    for (int i=0; i<imps.size(); i++) {
      ImagePlus imp = imps.get(i);
      final int series = (Integer) imp.getProperty(ImagePlusReader.PROP_SERIES);
      reader.setSeries(series);

      // get LUT for each channel
      final String stackOrder = dimSwapper.getDimensionOrder();
      final int zSize = imp.getNSlices();
      final int cSize = imp.getNChannels();
      final int tSize = imp.getNFrames();
      final int stackSize = imp.getStackSize();
      final LUT[] channelLUTs = new LUT[cSize];
      boolean hasChannelLUT = false;
      for (int c=0; c<cSize; c++) {
        final int index = FormatTools.getIndex(stackOrder,
          zSize, cSize, tSize, stackSize, 0, c, 0);
        channelLUTs[c] = (LUT)
          imp.getProperty(ImagePlusReader.PROP_LUT + index);
        if (channelLUTs[c] != null) hasChannelLUT = true;
      }

      // compute color mode and LUTs to use
      int mode = -1;
      LUT[] luts;
      if (options.isColorModeDefault()) {
        // NB: Default color mode behavior depends on the situation.
        final boolean isRGB = reader.isRGB() || imageReader.isRGB();
        if (isRGB || channelFiller.isFilled()) {
          // NB: The original data had more than one channel per plane
          // (e.g., RGB image planes), so we use the composite display mode.
          mode = CompositeImage.COMPOSITE;
          luts = makeLUTs(channelLUTs, true); // preserve original LUTs
        }
        else if (hasChannelLUT) {
          // NB: The original data had only one channel per plane,
          // but had at least one lookup table defined. We use the color
          // display mode, with missing LUTs as grayscale.
          mode = CompositeImage.COLOR;
          luts = makeLUTs(channelLUTs, true); // preserve original LUTs
        }
        else {
          // NB: The original data had only one channel per plane,
          // and had no lookup tables defined, so we use the grayscale mode.
          mode = CompositeImage.GRAYSCALE;
          luts = null;
        }
      }
      else if (options.isColorModeComposite()) {
        mode = CompositeImage.COMPOSITE;
        luts = makeLUTs(channelLUTs, true); // preserve existing channel LUTs
      }
      else if (options.isColorModeColorized()) {
        mode = CompositeImage.COLOR;
        luts = makeLUTs(channelLUTs, true); // preserve existing channel LUTs
      }
      else if (options.isColorModeGrayscale()) {
        mode = CompositeImage.GRAYSCALE;
        luts = null; // use default (grayscale) channel LUTs
      }
      else if (options.isColorModeCustom()) {
        mode = CompositeImage.COLOR;
        luts = makeLUTs(series); // override any existing channel LUTs
      }
      else {
        throw new IllegalStateException("Invalid color mode: " +
          options.getColorMode());
      }

      // apply color mode and LUTs
      final boolean doComposite = !options.isViewStandard() &&
        mode != -1 && cSize > 1 && cSize <= 7;
      if (doComposite) {
        final ImagePlus toClose = imp;
        CompositeImage compImage = new CompositeImage(imp, mode) {
          @Override
          public void close() {
            super.close();
            toClose.close();
          }

          @Override
          public void show(String message) {
            super.show(message);

            // ensure that the display settings are consistent across channels
            // autoscaling takes care of this for non-virtual stacks
            // see ticket #12267
            if (toClose instanceof VirtualImagePlus) {
              int channel = getChannel();
              double min = getDisplayRangeMin();
              double max = getDisplayRangeMax();

              for (int c=0; c<cSize; c++) {
                setPositionWithoutUpdate(c + 1, getSlice(), getFrame());
                setDisplayRange(min, max);
              }
              reset();
              setPosition(channel, getSlice(), getFrame());
            }
          }

        };
        compImage.setProperty(ImagePlusReader.PROP_SERIES, series);
        if (luts != null) compImage.setLuts(luts);
        imps.set(i, compImage);
        imp = compImage;
      }
      else {
        // NB: Cannot use CompositeImage for some reason.
        if (luts != null && luts.length > 0 && luts[0] != null) {
          if (imp instanceof VirtualImagePlus) {
            ((VirtualImagePlus) imp).setLUTs(luts);
          }
          else if (cSize == 1) imp.getProcessor().setColorModel(luts[0]);
        }
        if (mode != -1 && cSize > 7) {
          // NB: Cannot use CompositeImage with more than seven channels.
          BF.warn(options.isQuiet(), "Data has too many channels for " +
            options.getColorMode() + " color mode");
        }
      }

      applyDisplayRanges(imp, series);
    }
    return imps;
  }

  // -- Helper methods --

  private void applyDisplayRanges(ImagePlus imp, int series) {
    if (imp instanceof VirtualImagePlus) {
      // virtual stacks handle their own display ranges
      return;
    }

    final ImporterOptions options = process.getOptions();
    final ImageProcessorReader reader = process.getReader();

    final int pixelType = reader.getPixelType();
    final boolean autoscale = options.isAutoscale() ||
      FormatTools.isFloatingPoint(pixelType); // always autoscale float data

    final int cSize = imp.getNChannels();
    final double[] cMin = new double[cSize];
    final double[] cMax = new double[cSize];
    Arrays.fill(cMin, Double.NaN);
    Arrays.fill(cMax, Double.NaN);

    if (autoscale) {
      // extract display ranges for autoscaling
      final MinMaxCalculator minMaxCalc = process.getMinMaxCalculator();
      final int cBegin = process.getCBegin(series);
      final int cStep = process.getCStep(series);
      for (int c=0; c<cSize; c++) {
        final int cIndex = cBegin + c * cStep;
        Double cMinVal = null, cMaxVal = null;
        try {
          cMinVal = minMaxCalc.getChannelGlobalMinimum(cIndex);
          cMaxVal = minMaxCalc.getChannelGlobalMaximum(cIndex);

          if (cMinVal == null) {
            cMinVal = minMaxCalc.getChannelKnownMinimum(cIndex);
          }

          if (cMaxVal == null) {
            cMaxVal = minMaxCalc.getChannelKnownMaximum(cIndex);
          }
        }
        catch (FormatException exc) { }
        catch (IOException exc) { }
        if (cMinVal != null) cMin[c] = cMinVal;
        if (cMaxVal != null) cMax[c] = cMaxVal;
      }
    }

    // for calibrated data, the offset from zero
    final double zeroOffset = getZeroOffset(imp);

    // fill in default display ranges as appropriate
    final double min, max;
    if (FormatTools.isFloatingPoint(pixelType)) {
      // no defined min and max values for floating point data
      min = max = Double.NaN;
    }
    else {
      final int bitDepth = reader.getBitsPerPixel();
      final double halfPow = Math.pow(2, bitDepth - 1);
      final double fullPow = 2 * halfPow;
      final boolean signed = FormatTools.isSigned(pixelType);
      if (signed) {
        // signed data is centered at 0
        min = -halfPow;
        max = halfPow - 1;
      }
      else {
        // unsigned data begins at 0
        min = 0;
        max = fullPow - 1;
      }
      for (int c=0; c<cSize; c++) {
        if (Double.isNaN(cMin[c])) cMin[c] = min;
        if (Double.isNaN(cMax[c])) cMax[c] = max;
      }
    }

    // apply display ranges
    if (imp instanceof CompositeImage) {
      // apply channel display ranges
      final CompositeImage compImage = (CompositeImage) imp;
      for (int c=0; c<cSize; c++) {
        LUT lut = compImage.getChannelLut(c + 1);
        // NB: Uncalibrate values before assigning to LUT min/max.
        lut.min = cMin[c] - zeroOffset;
        lut.max = cMax[c] - zeroOffset;
      }
    }
    else {
      // compute global display range from channel display ranges
      double globalMin = Double.POSITIVE_INFINITY;
      double globalMax = Double.NEGATIVE_INFINITY;
      for (int c=0; c<cSize; c++) {
        if (cMin[c] < globalMin) globalMin = cMin[c];
        if (cMax[c] > globalMax) globalMax = cMax[c];
      }
      // NB: Uncalibrate values before assigning to display range min/max.
      globalMin -= zeroOffset;
      globalMax -= zeroOffset;

      // apply global display range
      ImageProcessor proc = imp.getProcessor();
      if (proc instanceof ColorProcessor) {
        // NB: Should never occur. ;-)
        final ColorProcessor colorProc = (ColorProcessor) proc;
        colorProc.setMinAndMax(globalMin, globalMax, 3);
      }
      else {
        ColorModel model = proc.getColorModel();
        proc.setMinAndMax(globalMin, globalMax);
        proc.setColorModel(model);
        imp.setDisplayRange(globalMin, globalMax);
      }
    }
  }

  private LUT[] makeLUTs(ColorModel[] cm, boolean colorize) {
    // lookup tables can come from one of three places (in order of precedence):
    //   1) Color attribute defined in the MetadataStore
    //   2) lookup table returned by the reader's get8BitLookupTable or
    //      get16BitLookupTable methods
    //   3) EmissionWavelength attribute defined in the MetadataStore

    final ImporterOptions options = process.getOptions();
    final LUT[] luts = new LUT[cm.length];
    for (int c=0; c<luts.length; c++) {
      if (cm[c] instanceof LUT) luts[c] = (LUT) cm[c];
      else if (cm[c] instanceof IndexColorModel) {
        luts[c] = new LUT((IndexColorModel) cm[c], 0, 255);
      }

      Color color = null;
      if (colorize) {
        // rather than always assuming that the first channel is red, the
        // second green, etc. we will take into account the channel color
        // metadata and the acquisition wavelength
        ImageReader reader = process.getImageReader();
        MetadataStore store = reader.getMetadataStore();
        if (store instanceof MetadataRetrieve) {
          MetadataRetrieve retrieve = (MetadataRetrieve) store;

          if (c < retrieve.getChannelCount(reader.getSeries())) {
            ome.xml.model.primitives.Color metaColor =
              retrieve.getChannelColor(reader.getSeries(), c);
            if (metaColor != null) {
              int r = metaColor.getRed();
              int g = metaColor.getGreen();
              int b = metaColor.getBlue();
              int a = metaColor.getAlpha();
              color = new Color(r, g, b, a);
            }
            else if (luts[c] == null) {
              Length wavelength =
                retrieve.getChannelEmissionWavelength(reader.getSeries(), c);
              if (wavelength != null) {
                double wave = wavelength.value(UNITS.NM).doubleValue();
                if (wave >= BLUE_MIN && wave < BLUE_TO_GREEN_MIN) {
                  color = Color.BLUE;
                }
                else if (wave >= BLUE_TO_GREEN_MIN && wave < GREEN_TO_RED_MIN) {
                  color = Color.GREEN;
                }
                else if (wave >= GREEN_TO_RED_MIN && wave <= RED_MAX) {
                  color = Color.RED;
                }
              }
            }
          }
        }
      }
      if (color == null && luts[c] == null) {
        color = options.getDefaultCustomColor(c);
      }
      if (color != null) {
        luts[c] = makeLUT(color);
      }
    }
    return luts;
  }

  private LUT[] makeLUTs(int series) {
    final ImageProcessorReader reader = process.getReader();
    reader.setSeries(series);
    LUT[] luts = new LUT[reader.getSizeC()];
    for (int c=0; c<luts.length; c++) luts[c] = makeLUT(series, c);
    return luts;
  }

  private LUT makeLUT(int series, int channel) {
    final ImporterOptions options = process.getOptions();
    Color color = options.getCustomColor(series, channel);
    if (color == null) color = options.getDefaultCustomColor(channel);
    return makeLUT(color);
  }

  private LUT makeLUT(Color color) {
    final int red = color.getRed();
    final int green = color.getGreen();
    final int blue = color.getBlue();
    final int lutLength = 256;
    final int lutDivisor = lutLength - 1;
    byte[] r = new byte[lutLength];
    byte[] g = new byte[lutLength];
    byte[] b = new byte[lutLength];
    for (int i=0; i<lutLength; i++) {
      r[i] = (byte) (i * red / lutDivisor);
      g[i] = (byte) (i * green / lutDivisor);
      b[i] = (byte) (i * blue / lutDivisor);
    }
    LUT lut = new LUT(r, g, b);
    return lut;
  }

  private static double getZeroOffset(ImagePlus imp) {
    final Calibration cal = imp.getCalibration();
    if (cal.getFunction() != Calibration.STRAIGHT_LINE) return 0;
    final double[] coeffs = cal.getCoefficients();
    if (coeffs == null || coeffs.length == 0) return 0;
    return coeffs[0];
  }

}
