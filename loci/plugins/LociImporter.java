//
// LociImporter.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the 4D
Data Browser, OME Plugin and Bio-Formats Exporter. Copyright (C) 2006
Melissa Linkert, Curtis Rueden, Philip Huettl and Francis Wong.

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
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.process.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.*;
import java.io.File;
import java.util.Hashtable;
import javax.swing.*;
import loci.formats.*;

/**
 * ImageJ plugin for the LOCI Bio-Formats package.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class LociImporter implements PlugIn, ItemListener {

  // -- Static fields --

  /** Flag indicating whether last operation was successful. */
  public boolean success = false;

  private JCheckBox merge = null;
  private JCheckBox newWindows = null;
  private JCheckBox showMeta = null;
  private JCheckBox stitching = null;
  private boolean mergeChannels = true;
  private boolean splitWindows = false;
  private boolean showMetadata = true;
  private boolean stitchFiles = false;

  // -- PlugIn API methods --

  /** Executes the plugin. */
  public synchronized void run(String arg) {
    success = false;
    boolean quiet = !"".equals(arg);

    String id = null;
    ImageReader reader = new ImageReader();

    String mergeString = "Merge channels";
    String splitString = "Open each channel in its own window";
    String metadataString = "Display associated metadata";
    String stitchString = "Stitch files with similar names";
    if ((new File(arg)).exists()) {
      id = arg;

      // we still want to prompt for channel merge/split
      GenericDialog gd = new GenericDialog("LOCI Bio-Formats Import Options");
      gd.addCheckbox(mergeString, mergeChannels);
      gd.addCheckbox(splitString, splitWindows);
      gd.addCheckbox(metadataString, showMetadata);
      gd.addCheckbox(stitchString, stitchFiles);
      gd.showDialog();
      mergeChannels = gd.getNextBoolean();
      splitWindows = gd.getNextBoolean();
      showMetadata = gd.getNextBoolean();
      stitchFiles = gd.getNextBoolean();
    }
    else {
      JFileChooser chooser = reader.getFileChooser();
      String dir = OpenDialog.getDefaultDirectory();
      if (dir != null) chooser.setCurrentDirectory(new File(dir));

      // add some additional options to the file chooser
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      merge = new JCheckBox(mergeString, mergeChannels);
      newWindows = new JCheckBox(splitString, splitWindows);
      showMeta = new JCheckBox(metadataString, showMetadata);
      stitching = new JCheckBox(stitchString, stitchFiles);
      merge.addItemListener(this);
      newWindows.addItemListener(this);
      showMeta.addItemListener(this);
      stitching.addItemListener(this);
      panel.add(merge);
      panel.add(newWindows);
      panel.add(showMeta);
      panel.add(stitching);
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
      if (stitchFiles) r = new FileStitcher(r);
      if (mergeChannels) r = new ChannelMerger(r);

      // if necessary, prompt for the series to open

      GenericDialog datasets =
        new GenericDialog("LOCI Bio-Formats Series Chooser");

      int seriesCount = r.getSeriesCount(id);
      for (int i=0; i<seriesCount; i++) {
        r.setSeries(id, i);
        int sizeX = r.getSizeX(id);
        int sizeY = r.getSizeY(id);
        int imageCount = r.getImageCount(id);
        //int sizeZ = r.getSizeZ(id);
        //int sizeC = r.getSizeC(id);
        //int sizeT = r.getSizeT(id);
        datasets.addCheckbox("Series " + i + ": " +
          sizeX + " x " + sizeY + " x " + imageCount, i == 0);
      }
      if (seriesCount > 1) datasets.showDialog();

      boolean[] series = new boolean[seriesCount];
      for (int i=0; i<seriesCount; i++) series[i] = datasets.getNextBoolean();

      r.setSeparated(!mergeChannels);

      try {
        OMEXMLMetadataStore store = new OMEXMLMetadataStore();
        store.createRoot();
        r.setMetadataStore(store);
      }
      catch (Throwable t) { }

      if (showMetadata) {
        Hashtable meta = r.getMetadata(id);
        MetadataPane mp = new MetadataPane(meta);
        JFrame frame = new JFrame(id + " Metadata");
        frame.setContentPane(mp);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      }

      for (int i=0; i<series.length; i++) {
        while (!series[i] && i < series.length - 1) { i++; }
        if (!series[i]) { break; }
        r.setSeries(id, i);

        FileInfo fi = new FileInfo();
        try {
          fi.description =
            ((OMEXMLMetadataStore) r.getMetadataStore(id)).dumpXML();
        }
        catch (Throwable t) { }

        long start = System.currentTimeMillis();
        long time = start;
        int num = r.getImageCount(id);
        ImageStack stackB = null, stackS = null, stackF = null, stackO = null;
        int channels = r.getSizeC(id);

        for (int j=0; j<num; j++) {
          // limit message update rate
          long clock = System.currentTimeMillis();
          if (clock - time >= 50) {
            IJ.showStatus("Reading plane " + (j + 1) + "/" + num);
            time = clock;
          }
          IJ.showProgress((double) j / num);
          BufferedImage img = r.openImage(id, j);
          img = ImageTools.padImage(img, r.getSizeX(id), r.getSizeY(id));

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
              stackB.addSlice(fileName + ":" + (j + 1), ip);
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
              stackS.addSlice(fileName + ":" + (j + 1), ip);
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
              stackF.addSlice(fileName + ":" + (j + 1), ip);
            }
          }
          if (ip == null) {
            ip = new ImagePlus(null, img).getProcessor(); // slow
            if (stackO == null) stackO = new ImageStack(w, h);
            stackO.addSlice(fileName + ":" + (j + 1), ip);
          }
        }
        IJ.showStatus("Creating image");
        IJ.showProgress(1);
        ImagePlus ip = null;
        if (stackB != null) {
          if (!mergeChannels && splitWindows) {
            slice(stackB, fileName, channels, fi);
          }
          else ip = new ImagePlus(fileName, stackB);
        }
        if (stackS != null) {
          if (!mergeChannels && splitWindows) {
            slice(stackS, fileName, channels, fi);
          }
          else ip = new ImagePlus(fileName, stackS);
        }
        if (stackF != null) {
          if (!mergeChannels && splitWindows) {
            slice(stackF, fileName, channels, fi);
          }
          else ip = new ImagePlus(fileName, stackF);
        }
        if (stackO != null) {
          if (!mergeChannels && splitWindows) {
            slice(stackO, fileName, channels, fi);
          }
          else ip = new ImagePlus(fileName, stackO);
        }

        if (ip != null) ip.setFileInfo(fi);
        if (ip != null) ip.show();

        long end = System.currentTimeMillis();
        double elapsed = (end - start) / 1000.0;
        if (num == 1) IJ.showStatus(elapsed + " seconds");
        else {
          long average = (end - start) / num;
          IJ.showStatus("LOCI Bio-Formats: " + elapsed + " seconds (" +
            average + " ms per plane)");
        }
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

  private void slice(ImageStack is, String file, int c, FileInfo fi) {
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
      ImagePlus ip = new ImagePlus(file + " - Ch" + (i+1), newStacks[i]);
      ip.setFileInfo(fi);
      ip.show();
    }
  }

  public void itemStateChanged(ItemEvent e) {
    Object source = e.getItemSelectable();
    boolean selected = e.getStateChange() == ItemEvent.SELECTED;
    if (source == merge) mergeChannels = selected;
    else if (source == newWindows) splitWindows = selected;
    else if (source == showMeta) showMetadata = selected;
    else if (source == stitching) stitchFiles = selected;
  }

}
