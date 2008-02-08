//
// Exporter.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
4D Data Browser, Bio-Formats Importer, Bio-Formats Exporter and OME plugins.
Copyright (C) 2005-@year@ Melissa Linkert, Christopher Peterson,
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
import ij.io.FileInfo;
import ij.io.SaveDialog;
import ij.process.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;

/**
 * Core logic for the LOCI Exporter ImageJ plugin.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/Exporter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/Exporter.java">SVN</a></dd></dl>
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
  public void run(ImageProcessor ip) {
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
      String dir = sd.getDirectory();
      String name = sd.getFileName();
      if (dir == null || name == null) return;
      outfile = new File(dir, name).getAbsolutePath();
      if (outfile == null) return;
    }

    try {
      IFormatWriter w = new ImageWriter().getWriter(outfile);
      FileInfo fi = imp.getOriginalFileInfo();
      String xml = fi == null ? null : fi.description == null ? null :
        fi.description.indexOf("xml") == -1 ? null : fi.description;
      MetadataStore store = MetadataTools.createOMEXMLMetadata(xml);
      if (store == null) IJ.error("OME-Java library not found.");
      else if (store instanceof MetadataRetrieve) {
        if (xml == null) {
          store.createRoot();

          int ptype = 0;
          int channels = 1;
          switch (imp.getType()) {
            case ImagePlus.GRAY8:
            case ImagePlus.COLOR_256:
              ptype = FormatTools.UINT8;
              break;
            case ImagePlus.COLOR_RGB:
              channels = 3;
              ptype = FormatTools.UINT8;
              break;
            case ImagePlus.GRAY16:
              ptype = FormatTools.UINT16;
              break;
            case ImagePlus.GRAY32:
              ptype = FormatTools.FLOAT;
              break;
          }

          store.setPixelsSizeX(new Integer(imp.getWidth()), 0, 0);
          store.setPixelsSizeY(new Integer(imp.getHeight()), 0, 0);
          store.setPixelsSizeZ(new Integer(imp.getNSlices()), 0, 0);
          store.setPixelsSizeC(new Integer(channels*imp.getNChannels()), 0, 0);
          store.setPixelsSizeT(new Integer(imp.getNFrames()), 0, 0);
          store.setPixelsPixelType(FormatTools.getPixelTypeString(ptype), 0, 0);
          store.setPixelsBigEndian(Boolean.FALSE, 0, 0);
          store.setPixelsDimensionOrder("XYCZT", 0, 0);
        }
        w.setMetadataRetrieve((MetadataRetrieve) store);
      }

      w.setId(outfile);

      // prompt for options

      String[] codecs = w.getCompressionTypes();
      ImageProcessor proc = imp.getStack().getProcessor(1);
      Image firstImage = proc.createImage();
      firstImage = ImageTools.makeBuffered(firstImage, proc.getColorModel());
      int thisType = ImageTools.getPixelType((BufferedImage) firstImage);
      if (proc instanceof ColorProcessor) {
        thisType = FormatTools.UINT8;
      }

      boolean notSupportedType = !w.isSupportedType(thisType);
      if (notSupportedType) {
        IJ.error("Pixel type (" + FormatTools.getPixelTypeString(thisType) +
          ") not supported by this format.");
      }

      if (codecs != null && codecs.length > 1) {
        GenericDialog gd =
          new GenericDialog("LOCI Bio-Formats Exporter Options");
        if (codecs != null) {
          gd.addChoice("Compression type: ", codecs, codecs[0]);
        }
        gd.showDialog();
        if (gd.wasCanceled()) return;

        if (codecs != null) w.setCompression(gd.getNextChoice());
      }

      // convert and save slices

      Class c = null;
      try {
        c = Class.forName("ij.CompositeImage");
      }
      catch (ClassNotFoundException e) { }
      boolean fakeRGB = imp.getClass().equals(c);
      int n = fakeRGB ? imp.getNChannels() : 1;

      ImageStack is = imp.getStack();
      int size = is.getSize();
      boolean doStack = w.canDoStacks() && size > 1;
      int start = doStack ? 0 : imp.getCurrentSlice() - 1;
      int end = doStack ? size : start + 1;

      for (int i=start; i<end; i+=n) {
        if (doStack) {
          IJ.showStatus("Saving plane " + (i + 1) + "/" + size);
          IJ.showProgress((double) (i + 1) / size);
        }
        else IJ.showStatus("Saving image");
        proc = is.getProcessor(i + 1);

        BufferedImage img = null;
        int x = proc.getWidth();
        int y = proc.getHeight();

        if (proc instanceof ByteProcessor) {
          if (fakeRGB) {
            byte[][] b = new byte[n][];
            for (int j=0; j<n; j++) {
              b[j] = (byte[]) is.getProcessor(i + j + 1).getPixels();
            }
            img = ImageTools.makeImage(b, x, y);
          }
          else {
            byte[] b = (byte[]) proc.getPixels();
            img = ImageTools.makeImage(b, x, y);
          }
        }
        else if (proc instanceof ShortProcessor) {
          if (fakeRGB) {
            short[][] s = new short[n][];
            for (int j=0; j<n; j++) {
              s[j] = (short[]) is.getProcessor(i + j + 1).getPixels();
            }
            img = ImageTools.makeImage(s, x, y);
          }
          else {
            short[] s = (short[]) proc.getPixels();
            img = ImageTools.makeImage(s, x, y);
          }
        }
        else if (proc instanceof FloatProcessor) {
          if (fakeRGB) {
            float[][] f = new float[n][];
            for (int j=0; j<n; j++) {
              f[j] = (float[]) is.getProcessor(i + j + 1).getPixels();
            }
            img = ImageTools.makeImage(f, x, y);
          }
          else {
            float[] b = (float[]) proc.getPixels();
            img = ImageTools.makeImage(b, x, y);
          }
        }
        else if (proc instanceof ColorProcessor) {
          byte[][] pix = new byte[3][x*y];
          ((ColorProcessor) proc).getRGB(pix[0], pix[1], pix[2]);
          img = ImageTools.makeImage(pix, x, y);
        }

        if (notSupportedType) {
          IJ.error("Pixel type not supported by this format.");
        }
        else w.saveImage(img, i == end - 1);
      }
    }
    catch (FormatException e) {
      e.printStackTrace();
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      e.printStackTrace(new PrintStream(buf));
      IJ.error(e.getMessage() + ":\n" + buf.toString());
    }
    catch (IOException e) {
      e.printStackTrace();
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      e.printStackTrace(new PrintStream(buf));
      IJ.error(e.getMessage() + ":\n" + buf.toString());
    }
  }

}
