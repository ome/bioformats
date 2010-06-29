//
// Colorizer.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser and Stack Slicer. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden and Christopher Peterson.

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

package loci.plugins.in;

import ij.CompositeImage;
import ij.ImagePlus;
import ij.process.LUT;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.util.List;

import loci.formats.ChannelFiller;
import loci.formats.DimensionSwapper;
import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.plugins.BF;
import loci.plugins.util.ImageProcessorReader;

/**
 * Logic for colorizing images.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/Colorizer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/Colorizer.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class Colorizer {

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
      final ImagePlus imp = imps.get(i);
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
          luts = makeLUTs(channelLUTs, false); // preserve original LUTs
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

      final boolean doComposite = mode != -1 && cSize > 1 && cSize <= 7;
      if (doComposite) {
        CompositeImage compImage = new CompositeImage(imp, mode);
        if (luts != null) compImage.setLuts(luts);
        imps.set(i, compImage);
      }
      else {
        // NB: Cannot use CompositeImage for some reason.
        if (luts != null && luts.length > 0 && luts[0] != null) {
          imp.getProcessor().setColorModel(luts[0]);
        }
        if (mode != -1 && cSize > 1) {
          // NB: Cannot use CompositeImage with more than seven channels.
          BF.warn(options.isQuiet(), "Data has too many channels for " +
            options.getColorMode() + " color mode");
        }
      }
    }
    return imps;
  }

  // -- Helper methods --

  private LUT[] makeLUTs(ColorModel[] cm, boolean colorize) {
    final ImporterOptions options = process.getOptions();
    final LUT[] luts = new LUT[cm.length];
    for (int c=0; c<luts.length; c++) {
      if (cm[c] instanceof LUT) luts[c] = (LUT) cm[c];
      else if (cm[c] instanceof IndexColorModel) {
        luts[c] = new LUT((IndexColorModel) cm[c], 0, 255);
      }
      else {
        Color color = colorize ? options.getDefaultCustomColor(c) : Color.white;
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
    lut.min = 0;
    lut.max = 255;
    return lut;
  }
}
