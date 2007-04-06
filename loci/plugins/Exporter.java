//
// Exporter.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including
the 4D Data Browser, OME Plugin and Bio-Formats Exporter.
Copyright (C) 2006-@year@ Melissa Linkert, Christopher Peterson,
Curtis Rueden, Philip Huettl and Francis Wong.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins;

import ij.*;
import ij.gui.GenericDialog;
import ij.io.SaveDialog;
import ij.process.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import loci.formats.*;

/**
 * Core logic for the LOCI Exporter ImageJ plugin.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class Exporter {

  // -- Fields --

  /** Current stack. */
  private ImagePlus imp;

  private LociExporter plugin;

  // -- Constructor --

  public Exporter(LociExporter plugin, ImagePlus imp) {
    this.plugin = plugin;
    this.imp = imp;
  }

  // -- Exporter API methods --

  /** Executes the plugin. */
  public synchronized void run(ImageProcessor ip) {
    String outfile = null;

    if (plugin.arg != null && plugin.arg.startsWith("outfile=")) {
      outfile = Macro.getValue(plugin.arg, "outfile", null);
      plugin.arg = null;
    }

    if (outfile == null) {
      String options = Macro.getOptions();
      if (options != null) {
        String save = Macro.getValue(options, "save", null);
        if (save != null) outfile = save;
      }
    }

    if (outfile == null || outfile.length() == 0) {
      // open a dialog prompting for the filename to save
      SaveDialog sd = new SaveDialog("LOCI Bio-Formats Exporter", "", "");
      outfile = new File(sd.getDirectory(), sd.getFileName()).getAbsolutePath();
      if (outfile == null) {
        return;
      }
    }

    try {
      IFormatWriter w = new ImageWriter().getWriter(outfile);

      // prompt for options

      String[] codecs = w.getCompressionTypes();
      ImageProcessor proc = imp.getStack().getProcessor(1);
      Image firstImage = proc.createImage();
      firstImage = ImageTools.makeBuffered(firstImage, proc.getColorModel());
      int thisType = ImageTools.getPixelType((BufferedImage) firstImage);
      boolean forceType = false;

      if ((codecs != null && codecs.length > 1) ||
        !w.isSupportedType(null, thisType))
      {
        GenericDialog gd =
          new GenericDialog("LOCI Bio-Formats Exporter Options");
        if (codecs != null) {
          gd.addChoice("Compression type: ", codecs, codecs[0]);
        }
        if (!w.isSupportedType(null, thisType)) {
          gd.addCheckbox("Force compatible pixel type", true);
        }
        gd.showDialog();
        if (gd.wasCanceled()) return;

        if (codecs != null) w.setCompression(gd.getNextChoice());
        if (!w.isSupportedType(null, thisType)) {
          forceType = gd.getNextBoolean();
        }
      }

      // convert and save each slice

      ImageStack is = imp.getStack();
      for (int i=0; i<is.getSize(); i++) {
        IJ.showStatus("Saving plane " + (i + 1) + "/" + is.getSize());
        IJ.showProgress((double) (i + 1) / is.getSize());
        proc = is.getProcessor(i + 1);
        
        BufferedImage img = null;
        int x = proc.getWidth();
        int y = proc.getHeight();

        if (proc instanceof ByteProcessor) {
          byte[] b = (byte[]) proc.getPixels();
          img = ImageTools.makeImage(b, x, y);
        }
        else if (proc instanceof ShortProcessor) {
          short[] s = (short[]) proc.getPixels();
          img = ImageTools.makeImage(s, x, y);
        }
        else if (proc instanceof FloatProcessor) {
          float[] b = (float[]) proc.getPixels();
          img = ImageTools.makeImage(b, x, y);
        }
        else if (proc instanceof ColorProcessor) {
          byte[][] pix = new byte[3][x*y];
          ((ColorProcessor) proc).getRGB(pix[0], pix[1], pix[2]);
          img = ImageTools.makeImage(pix, x, y); 
        }

        if (forceType) {
          if (!w.isSupportedType(null, thisType)) {
            int[] types = w.getPixelTypes(null);
            img = ImageTools.makeType(img, types[types.length - 1]);
          }
          w.saveImage(outfile, img, i == is.getSize() - 1);
        }
        else w.saveImage(outfile, img, i == is.getSize() - 1);
      }
    }
    catch (FormatException e) {
      IJ.error(e.getMessage());
      e.printStackTrace();
    }
    catch (IOException e) {
      IJ.error(e.getMessage());
      e.printStackTrace();
    }
  }

}
