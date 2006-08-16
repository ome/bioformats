//
// ImageJUtil.java
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

package loci.visbio.util;

import ij.*;
import ij.process.*;
import java.awt.Component;
import java.io.File;
import javax.swing.JOptionPane;
import loci.visbio.VisBioFrame;
import loci.visbio.view.DisplayManager;
import loci.visbio.state.OptionManager;
import visad.*;

/** ImageJUtil contains useful ImageJ functions. */
public abstract class ImageJUtil {

  /**
   * Converts a VisAD FlatField of the form
   * <tt>((x, y) -&gt; (r1, ..., rn))</tt> to an ImageJ ImageProcessor object.
   */
  public static ImageProcessor extractImage(FlatField field)
    throws VisADException
  {
    return extractImage(field, null);
  }

  /**
   * Converts a VisAD FlatField of the form
   * <tt>((x, y) -&gt; (r1, ..., rn))</tt> to an ImageJ ImageProcessor object.
   */
  public static ImageProcessor extractImage(FlatField field, Component c)
    throws VisADException
  {
    ImageProcessor proc = null;
    GriddedSet set = (GriddedSet) field.getDomainSet();
    int[] wh = set.getLengths();
    int w = wh[0];
    int h = wh[1];
    float[][] samples = field.getFloats(false);
    if (samples.length == 3) {
      // 24-bit color is the best we can do
      boolean mangling = false;
      int[] pixels = new int[samples[0].length];
      for (int i=0; i<pixels.length; i++) {
        int r = (int) samples[0][i];
        int g = (int) samples[1][i];
        int b = (int) samples[2][i];

        // check whether data mangling will occur
        if (r < 0 || r > 255) { r &= 0x000000ff; mangling = true; }
        if (g < 0 || g > 255) { g &= 0x000000ff; mangling = true; }
        if (b < 0 || b > 255) { b &= 0x000000ff; mangling = true; }
        pixels[i] = r << 16 | g << 8 | b;
      }
      if (mangling) {
        // warn user
        int ans = JOptionPane.showConfirmDialog(c,
          "Some data values will be truncated when VisBio converts " +
          "this data to 24-bit RGB format. Are you sure you wish to proceed?",
          "VisBio", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ans != JOptionPane.YES_OPTION) return null;
      }
      proc = new ColorProcessor(w, h, pixels);
    }
    else if (samples.length == 1) {
      // check for 8-bit, 16-bit or 32-bit grayscale
      float lo = Float.POSITIVE_INFINITY, hi = Float.NEGATIVE_INFINITY;
      for (int i=0; i<samples[0].length; i++) {
        float value = samples[0][i];
        if (value != (int) value) {
          // force 32-bit floats
          hi = Float.POSITIVE_INFINITY;
          break;
        }
        if (value < lo) {
          lo = value;
          if (lo < 0) break; // need 32-bit floats
        }
        if (value > hi) {
          hi = value;
          if (hi >= 65536) break; // need 32-bit floats
        }
      }
      if (lo >= 0 && hi < 256) {
        // 8-bit grayscale
        byte[] pixels = new byte[samples[0].length];
        for (int i=0; i<pixels.length; i++) {
          int val = (int) samples[0][i] & 0x000000ff;
          pixels[i] = (byte) val;
        }
        proc = new ByteProcessor(w, h, pixels, null);
      }
      else if (lo >= 0 && hi < 65536) {
        // 16-bit grayscale
        short[] pixels = new short[samples[0].length];
        for (int i=0; i<pixels.length; i++) {
          int val = (int) samples[0][i];
          pixels[i] = (short) val;
        }
        proc = new ShortProcessor(w, h, pixels, null);
      }
      else {
        // 32-bit floating point grayscale
        proc = new FloatProcessor(w, h, samples[0], null);
      }
    }
    return proc;
  }

  /**
   * Displays the given ImageJ image object within ImageJ,
   * launching ImageJ if necessary.
   */
  public static void sendToImageJ(ImagePlus image) {
    sendToImageJ(image, null);
  }

  /**
   * Displays the given ImageJ image object within ImageJ,
   * launching ImageJ if necessary.
   */
  public static void sendToImageJ(ImagePlus image, VisBioFrame bio) {
    ImageJ ij = IJ.getInstance();
    if (ij == null || (ij != null && !ij.isShowing())) {
      // create new ImageJ instance
      File dir = new File(System.getProperty("user.dir"));
      File newDir = new File(dir.getParentFile().getParentFile(), "ij");
      System.setProperty("user.dir", newDir.getPath());
      new ImageJ(null);
      System.setProperty("user.dir", dir.getPath());

      if (bio != null) {
        // display ImageJ warning
        OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
        om.checkWarning(ij, DisplayManager.WARN_IMAGEJ, false,
          "Quitting VisBio will also shut down ImageJ, with no\n" +
          "warning or opportunity to save your work. Please remember\n" +
          "to save your work in ImageJ before closing VisBio.");
      }
    }
    image.show();
  }

}
