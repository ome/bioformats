//
// LociImporter.java
//

/*
LOCI 4D Data Browser package for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-2006 Francis Wong, Curtis Rueden and Melissa Linkert.

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

package loci.browser;

import ij.*;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.process.*;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.*;
import java.io.File;
import javax.swing.*;
import loci.formats.*;

/**
 * ImageJ plugin for the LOCI Bio-Formats package.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LociImporter implements PlugIn, ItemListener {

  // -- Static fields --

  /** Flag indicating whether last operation was successful. */
  public boolean success = false;

  private JCheckBox merge = null;
  private JCheckBox newWindows = null;
  private JCheckBox stitching = null;
  private boolean mergeChannels = true;
  private boolean splitWindows = false;
  private boolean stitchFiles = true;

  // -- PlugIn API methods --

  /** Executes the plugin. */
  public synchronized void run(String arg) {
    success = false;
    boolean quiet = !"".equals(arg);

    String id = null;
    ImageReader reader = new ImageReader();

    if ((new File(arg)).exists()) {
      id = arg;

      // we still want to prompt for channel merge/split

      GenericDialog gd = new GenericDialog("LOCI Bio-Formats Import Options");
      gd.addCheckbox("Merge channels", true);
      gd.addCheckbox("Open each channel in a new window", false);
      gd.addCheckbox("Stitch together files with a similar name", true);
      gd.showDialog();
      mergeChannels = gd.getNextBoolean();
      splitWindows = gd.getNextBoolean();
      stitchFiles = gd.getNextBoolean();
    }
    else {
      JFileChooser chooser = reader.getFileChooser();

      // add some additional options to the file chooser
      JPanel panel = new JPanel(new BorderLayout());
      merge = new JCheckBox("Merge channels", true);
      newWindows = new JCheckBox("Open each channel in a new window", false);
      stitching = new JCheckBox("Stitch together files with a similar name",
        true);
      merge.addItemListener(this);
      newWindows.addItemListener(this);
      stitching.addItemListener(this);
      panel.add(merge, BorderLayout.NORTH);
      panel.add(newWindows, BorderLayout.EAST);
      panel.add(stitching, BorderLayout.SOUTH);
      chooser.setAccessory(panel);

      int rval = chooser.showOpenDialog(null);
      if (rval == JFileChooser.APPROVE_OPTION) {
        final File file = chooser.getSelectedFile();
        if (file != null) {
          id = file.getAbsolutePath();
          OpenDialog.setDefaultDirectory(
            chooser.getCurrentDirectory().getPath());
        }
      }
    }

    if (id == null) return;
    String fileName = id.substring(id.lastIndexOf(File.separator) + 1);

    IJ.showStatus("Opening " + fileName);
    try {
      FormatReader r = (FormatReader) reader.getReader(id);
      FileStitcher fs = new FileStitcher(r);
      ChannelMerger cm = new ChannelMerger(stitchFiles ? fs : r);
      cm.setSeparated(!mergeChannels);
      int num = cm.getImageCount(id);
      ImageStack stackB = null, stackS = null, stackF = null, stackO = null;
      long start = System.currentTimeMillis();
      long time = start;
      int channels = cm.getSizeC(id);

      for (int i=0; i<num; i++) {
        // limit message update rate
        long clock = System.currentTimeMillis();
        if (clock - time >= 50) {
          IJ.showStatus("Reading plane " + (i + 1) + "/" + num);
          time = clock;
        }
        IJ.showProgress((double) i / num);
        BufferedImage img = cm.openImage(id, i);

        ImageProcessor ip = null;
        WritableRaster raster = img.getRaster();
        int c = raster.getNumBands();
        int tt = raster.getTransferType();
        int w = img.getWidth(), h = img.getHeight();
        if (c == 1) {
          if (tt == DataBuffer.TYPE_BYTE) {
            byte[] b = ImageTools.getBytes(img)[0];
            if (b.length > w*h) {
              byte[] tmp = b;
              b = new byte[w*h];
              System.arraycopy(tmp, 0, b, 0, b.length);
            }
            ip = new ByteProcessor(w, h, b, null);
            if (stackB == null) stackB = new ImageStack(w, h);
            stackB.addSlice(fileName + ":" + (i + 1), ip);
          }
          else if (tt == DataBuffer.TYPE_USHORT) {
            short[] s = ImageTools.getShorts(img)[0];
            if (s.length > w*h) {
              short[] tmp = s;
              s = new short[w*h];
              System.arraycopy(tmp, 0, s, 0, s.length);
            }
            ip = new ShortProcessor(w, h, s, null);
            if (stackS == null) stackS = new ImageStack(w, h);
            stackS.addSlice(fileName + ":" + (i + 1), ip);
          }
          else if (tt == DataBuffer.TYPE_FLOAT) {
            float[] f = ImageTools.getFloats(img)[0];
            if (f.length > w*h) {
              float[] tmp = f;
              f = new float[w*h];
              System.arraycopy(tmp, 0, f, 0, f.length);
            }
            ip = new FloatProcessor(w, h, f, null);
            if (stackF == null) stackF = new ImageStack(w, h);
            stackF.addSlice(fileName + ":" + (i + 1), ip);
          }
        }
        if (ip == null) {
          ip = new ImagePlus(null, img).getProcessor(); // slow
          if (stackO == null) stackO = new ImageStack(w, h);
          stackO.addSlice(fileName + ":" + (i + 1), ip);
        }
      }
      IJ.showStatus("Creating image");
      IJ.showProgress(1);
      if (stackB != null) {
        if (!mergeChannels && splitWindows) slice(stackB, fileName, channels);
        else new ImagePlus(fileName, stackB).show();
      }
      if (stackS != null) {
        if (!mergeChannels && splitWindows) slice(stackS, fileName, channels);
        else new ImagePlus(fileName, stackS).show();
      }
      if (stackF != null) {
        if (!mergeChannels && splitWindows) slice(stackF, fileName, channels);
        else new ImagePlus(fileName, stackF).show();
      }
      if (stackO != null) {
        if (!mergeChannels && splitWindows) slice(stackO, fileName, channels);
        else new ImagePlus(fileName, stackO).show();
      }
      long end = System.currentTimeMillis();
      double elapsed = (end - start) / 1000.0;
      if (num == 1) IJ.showStatus(elapsed + " seconds");
      else {
        long average = (end - start) / num;
        IJ.showStatus("LOCI Bio-Formats : " + elapsed + " seconds (" +
          average + " ms per plane)");
      }
      success = true;
      mergeChannels = true;
      splitWindows = false;
    }
    catch (Exception exc) {
      exc.printStackTrace();
      IJ.showStatus("");
      if (!quiet) {
        String msg = exc.getMessage();
        IJ.showMessage("LOCI Bio-Formats", "Sorry, there was a problem " +
          "reading the data" + (msg == null ? "." : (": " + msg)));
      }
    }
  }

  private void slice(ImageStack is, String file, int c) {
    ImageStack[] newStacks = new ImageStack[c];
    for (int i=0; i<newStacks.length; i++) {
      newStacks[i] = new ImageStack(is.getWidth(), is.getHeight());
    }

    for (int i=1; i<=is.getSize(); i+=c) {
      for (int j=0; j<c; j++) {
        newStacks[j].addSlice(is.getSliceLabel(i+j), is.getProcessor(i+j));
      }
    }

    for (int i=0; i<newStacks.length; i++) {
      new ImagePlus(file + " - Ch" + (i+1), newStacks[i]).show();
    }
  }

  public void itemStateChanged(ItemEvent e) {
    Object source = e.getItemSelectable();
    if (source == merge) {
      mergeChannels = e.getStateChange() == ItemEvent.SELECTED;
    }
    else if (source == newWindows) {
      splitWindows = e.getStateChange() == ItemEvent.SELECTED;
    }
    else if (source == stitching) {
      stitchFiles = e.getStateChange() == ItemEvent.SELECTED;
    }
  }

}

/*
Add the following code to HandleExtraFileTypes.java to enable
LOCI Bio-Formats through File/Open, drag & drop, etc.

    // CTR: try opening the file with LOCI Bio-Formats
    Object loci = IJ.runPlugIn("LociPlugin", path);
    if (loci != null) {
      // plugin exists and was launched
      try {
        // check whether plugin was successful
        boolean ok = loci.getClass().getField("success").getBoolean(loci);
        if (ok) {
          width = IMAGE_OPENED;
          return null;
        }
      }
      catch (Exception exc) { }
    }
*/
