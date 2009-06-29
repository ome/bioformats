//
// Exporter.java
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

package loci.plugins.exporter;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Macro;
import ij.gui.GenericDialog;
import ij.io.FileInfo;
import ij.io.OpenDialog;
import ij.plugin.frame.Recorder;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import loci.common.DataTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatWriter;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.ExtensionFileFilter;
import loci.formats.gui.GUITools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.plugins.LociExporter;
import loci.plugins.util.RecordedImageProcessor;

/**
 * Core logic for the Bio-Formats Exporter ImageJ plugin.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/exporter/Exporter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/exporter/Exporter.java">SVN</a></dd></dl>
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

      //SaveDialog sd = new SaveDialog("Bio-Formats Exporter", "", "");
      //String dir = sd.getDirectory();
      //String name = sd.getFileName();

      // NB: Copied and adapted from ij.io.SaveDIalog.jSaveDispatchThread,
      // so that the save dialog has a file filter for choosing output format.

      String dir = null, name = null;
      JFileChooser fc = GUITools.buildFileChooser(new ImageWriter(), false);
      fc.setDialogTitle("Bio-Formats Exporter");
      String defaultDir = OpenDialog.getDefaultDirectory();
      if (defaultDir != null) fc.setCurrentDirectory(new File(defaultDir));

      // set OME-TIFF as the default output format
      FileFilter[] ff = fc.getChoosableFileFilters();
      FileFilter defaultFilter = null;
      for (int i=0; i<ff.length; i++) {
        if (ff[i] instanceof ExtensionFileFilter) {
          ExtensionFileFilter eff = (ExtensionFileFilter) ff[i];
          if (i == 0 || eff.getExtension().equals("ome.tif")) {
            defaultFilter = eff;
            break;
          }
        }
      }
      if (defaultFilter != null) fc.setFileFilter(defaultFilter);

      int returnVal = fc.showSaveDialog(IJ.getInstance());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        Macro.abort();
        return;
      }
      File f = fc.getSelectedFile();
      if (f.exists()) {
        int ret = JOptionPane.showConfirmDialog(fc,
          "The file " + f.getName() + " already exists. \n" +
          "Would you like to replace it?", "Replace?",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ret != JOptionPane.OK_OPTION) f = null;
      }
      if (f == null) Macro.abort();
      else {
        dir = fc.getCurrentDirectory().getPath() + File.separator;
        name = fc.getName(f);

        // ensure filename matches selected filter
        FileFilter filter = fc.getFileFilter();
        if (filter instanceof ExtensionFileFilter) {
          ExtensionFileFilter eff = (ExtensionFileFilter) filter;
          String[] ext = eff.getExtensions();
          String lName = name.toLowerCase();
          boolean hasExtension = false;
          for (int i=0; i<ext.length; i++) {
            if (lName.endsWith("." + ext[i])) {
              hasExtension = true;
              break;
            }
          }
          if (!hasExtension && ext.length > 0) {
            // append chosen extension
            name = name + "." + ext[0];
          }
        }

        // do some ImageJ bookkeeping
        OpenDialog.setDefaultDirectory(dir);
        if (Recorder.record) Recorder.recordPath("save", dir+name);
      }

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
      if (store == null) IJ.error("OME-XML Java library not found.");
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
          store.setLogicalChannelSamplesPerPixel(new Integer(channels), 0, 0);
        }

        if (imp.getImageStackSize() !=
          imp.getNChannels() * imp.getNSlices() * imp.getNFrames())
        {
          IJ.showMessageWithCancel("Bio-Formats Exporter Warning",
            "The number of planes in the stack (" + imp.getImageStackSize() +
            ") does not match the number of expected planes (" +
            (imp.getNChannels() * imp.getNSlices() * imp.getNFrames()) + ")." +
            "\nIf you select 'OK', only " + imp.getImageStackSize() +
            " planes will be exported. If you wish to export all of the " +
            "planes,\nselect 'Cancel' and convert the Image5D window " +
            "to a stack.");
          store.setPixelsSizeZ(new Integer(imp.getImageStackSize()), 0, 0);
          store.setPixelsSizeC(new Integer(1), 0, 0);
          store.setPixelsSizeT(new Integer(1), 0, 0);
        }

        w.setMetadataRetrieve((MetadataRetrieve) store);
      }

      w.setId(outfile);

      // prompt for options

      String[] codecs = w.getCompressionTypes();
      ImageProcessor proc = imp.getImageStack().getProcessor(1);
      Image firstImage = proc.createImage();
      firstImage = AWTImageTools.makeBuffered(firstImage, proc.getColorModel());
      int thisType = AWTImageTools.getPixelType((BufferedImage) firstImage);
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
          new GenericDialog("Bio-Formats Exporter Options");
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

      int size = imp.getImageStackSize();
      ImageStack is = imp.getImageStack();
      boolean doStack = w.canDoStacks() && size > 1;
      int start = doStack ? 0 : imp.getCurrentSlice() - 1;
      int end = doStack ? size : start + 1;

      boolean littleEndian =
        !w.getMetadataRetrieve().getPixelsBigEndian(0, 0).booleanValue();
      byte[] plane = null;
      w.setInterleaved(false);

      for (int i=start; i<end; i+=n) {
        if (doStack) {
          IJ.showStatus("Saving plane " + (i + 1) + "/" + size);
          IJ.showProgress((double) (i + 1) / size);
        }
        else IJ.showStatus("Saving image");
        proc = is.getProcessor(i + 1);

        if (proc instanceof RecordedImageProcessor) {
          proc = ((RecordedImageProcessor) proc).getChild();
        }

        int x = proc.getWidth();
        int y = proc.getHeight();

        if (proc instanceof ByteProcessor) {
          if (fakeRGB) {
            plane = new byte[n * x * y];
            for (int j=0; j<n; j++) {
              byte[] b = (byte[]) is.getProcessor(i + j + 1).getPixels();
              System.arraycopy(b, 0, plane, j * x * y, b.length);
            }
          }
          else {
            plane = (byte[]) proc.getPixels();
          }
        }
        else if (proc instanceof ShortProcessor) {
          if (fakeRGB) {
            plane = new byte[n * x * y * 2];
            for (int j=0; j<n; j++) {
              byte[] b = DataTools.shortsToBytes(
                (short[]) is.getProcessor(i + j + 1).getPixels(), littleEndian);
              System.arraycopy(b, 0, plane, j * x * y * 2, b.length);
            }
          }
          else {
            plane = DataTools.shortsToBytes(
              (short[]) proc.getPixels(), littleEndian);
          }
        }
        else if (proc instanceof FloatProcessor) {
          if (fakeRGB) {
            plane = new byte[n * x * y * 4];
            for (int j=0; j<n; j++) {
              byte[] b = DataTools.floatsToBytes(
                (float[]) is.getProcessor(i + j + 1).getPixels(), littleEndian);
              System.arraycopy(b, 0, plane, j * x * y * 4, b.length);
            }
          }
          else {
            plane = DataTools.floatsToBytes(
              (float[]) proc.getPixels(), littleEndian);
          }
        }
        else if (proc instanceof ColorProcessor) {
          byte[][] pix = new byte[3][x*y];
          ((ColorProcessor) proc).getRGB(pix[0], pix[1], pix[2]);
          plane = new byte[3 * x * y];
          System.arraycopy(pix[0], 0, plane, 0, x * y);
          System.arraycopy(pix[1], 0, plane, x * y, x * y);
          System.arraycopy(pix[2], 0, plane, 2 * x * y, x * y);
        }

        if (notSupportedType) {
          IJ.error("Pixel type not supported by this format.");
        }
        else w.saveBytes(plane, i == end - n);
      }
      w.close();
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
