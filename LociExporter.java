//
// LociExporter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.filter.*;
import ij.process.*;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.*;

/**
 * ImageJ plugin for writing files using the LOCI Bio-Formats package.
 *
 * @author Melissa Linkert, linkert at cs.wisc.edu
 */
public class LociExporter implements PlugInFilter, ItemListener {

  // -- Static fields --

  /** Flag indicating whether last operation was successful. */
  public boolean success = false;

  // -- Fields --

  /** Current stack. */
  private ImagePlus imp;

  private JCheckBox merge = null;
  private JCheckBox interleave = null;
  private JCheckBox sample = null;
  private boolean mergeChannels = true;
  private boolean interleaveChannels = true;
  private boolean downSample = false;

  // -- PlugInFilter API methods --

  /** Sets up the writer. */
  public int setup(String arg, ImagePlus imp) {
    this.imp = imp;
    return DOES_ALL + NO_CHANGES;
  }

  /** Executes the plugin. */
  public synchronized void run(ImageProcessor ip) {
    success = false;

    // prompt for the filename to save to

    ImageWriter writer = new ImageWriter();

    //SaveDialog sd = new SaveDialog("Save...", imp.getTitle(), "");

    String filename = null;
    JFileChooser chooser = writer.getFileChooser();
    JPanel panel = new JPanel(new BorderLayout());
    merge = new JCheckBox("Merge channels (if unchecked, this will separate " +
      "multi-channel images)", true);
    merge.addItemListener(this);
    interleave = new JCheckBox("Interleave channels?", true);
    interleave.addItemListener(this);
    sample = new JCheckBox("Enable converting 16-bit grayscale to RGB " +
      "(will result in data loss)");
    sample.addItemListener(this);
    panel.add(merge, BorderLayout.NORTH);
    panel.add(interleave, BorderLayout.EAST);
    panel.add(sample, BorderLayout.SOUTH);
    chooser.setAccessory(panel);

    int rval = chooser.showSaveDialog(null);
    if (rval == JFileChooser.APPROVE_OPTION) {
      final File file = chooser.getSelectedFile();
      if (file != null) {
        filename = file.getAbsolutePath();
      }

    }

    if (filename == null) return;

    try {
      long t1 = System.currentTimeMillis();
      FormatWriter w2 = writer.getWriter(filename);

      // make sure we prompt for a compression type, if applicable

      String[] types = w2.getCompressionTypes();
      if (types != null) {
        GenericDialog gd = new GenericDialog("Choose a compression type");
        gd.addChoice("Available compression types", types, types[0]);
        gd.showDialog();
        w2.setCompression(gd.getNextChoice());
      }

      if (imp == null) return;

      ImageStack stack = imp.getStack();
      if (mergeChannels) stack = mergeStack(stack, interleaveChannels);
      else stack = splitStack(stack, interleaveChannels);
      int size = stack.getSize();

      long t3 = System.currentTimeMillis();
      if (w2.canDoStacks(filename)) {
        for (int i=0; i<size; i++) {
          ImageProcessor proc = stack.getProcessor(i+1);
          Image img = proc.createImage();
          w2.setColorModel(proc.getColorModel());
          IJ.showStatus("Writing plane " + (i+1) + " / " + size);
          IJ.showProgress((double) i / size);
          w2.save(filename, img, i == (size - 1));
        }
      }
      else {
        ImageProcessor proc = imp.getProcessor();
        Image img = imp.getImage();
        w2.setColorModel(proc.getColorModel());
        IJ.showStatus("Writing plane 1 / 1");
        w2.save(filename, img, true);
      }

      long t4 = System.currentTimeMillis();
      if (size == 1) {
        IJ.showStatus((t4 - t1) + " ms");
      }
      else {
        long average = (t4 - t3) / size;
        IJ.showStatus("LOCI Bio-Formats Exporter: " + (t4 - t1) + " ms (" +
          average + " ms per plane)");
      }
      IJ.showProgress(1);
      success = true;
    }
    catch (Exception exc) {
      exc.printStackTrace();
      IJ.showStatus("");
      String msg = exc.getMessage();
      IJ.showMessage("LOCI Bio-Formats", "Sorry, there was a problem " +
        "writing the data" + (msg == null ? "." : (": " + msg)));
    }
  }

  /**
   * Merge a greyscale image stack into an RGB stack with 1/3 the size.
   * The interleaved flag refers to the ordering of channels within the original
   * stack.
   */
  private ImageStack mergeStack(ImageStack is, boolean interleaved)
    throws Exception
  {
    ImageStack newStack = new ImageStack(is.getWidth(), is.getHeight());
    int type = imp.getType();

    if (type == ImagePlus.GRAY8 || downSample) {
      ImageStack tempStack = new ImageStack(is.getWidth(), is.getHeight());
      ImagePlus ip = null;
      ImageConverter converter = null;
      int size = is.getSize() / 3;
      if (interleaved) size *= 3;
      int increment = size;
      if (interleaved) increment = 1;
      int loopIncrement = 1;
      if (interleaved) loopIncrement = 3;
      for (int i=1; i<=size; i+=loopIncrement) {
        while (tempStack.getSize() > 0) tempStack.deleteLastSlice();
        tempStack.addSlice(is.getSliceLabel(i), is.getProcessor(i));
        tempStack.addSlice(is.getSliceLabel(i + increment),
          is.getProcessor(i + increment));
        tempStack.addSlice(is.getSliceLabel(i + 2 * increment),
          is.getProcessor(i + 2 * increment));
        ip = new ImagePlus("temp", tempStack);
        converter = new ImageConverter(ip);
        if (type != ImagePlus.GRAY8) converter.convertToGray8();
        converter.convertRGBStackToRGB();
        newStack.addSlice("", ip.getImageStack().getProcessor(1));
      }
    }
    else {
      // exit loudly
      IJ.showStatus("LOCI Bio-Formats: Cannot convert this stack to RGB; " +
        "please check the 'Enable converting 16-bit grayscale to RGB' box");
      throw new Exception("Converting 16-bit data failed, please try again.");
    }
    return newStack;
  }

  private ImageStack splitStack(ImageStack is, boolean interleaved) {
    ImageStack newStack = new ImageStack(is.getWidth(), is.getHeight());

    // if it's not RGB, don't split the stack - just return it
    int type = imp.getType();
    if (type != ImagePlus.COLOR_RGB && type != ImagePlus.COLOR_256) {
      return is;
    }

    ImagePlus ip = new ImagePlus("temp", is);
    ImageConverter converter = new ImageConverter(ip);
    converter.convertToRGBStack();
    converter.convertToGray8();
    ImageStack tempStack = ip.getImageStack();

    if (interleaved) return tempStack;

    for (int j=1; j<=3; j++) {
      for (int i=j; i<=tempStack.getSize(); i+=3) {
        newStack.addSlice(tempStack.getSliceLabel(i),
          tempStack.getProcessor(i));
      }
    }

    return newStack;
  }

  public void itemStateChanged(ItemEvent e) {
    Object source = e.getItemSelectable();
    if (source == merge) {
      mergeChannels = e.getStateChange() == ItemEvent.SELECTED;
    }
    else if (source == interleave) {
      interleaveChannels = e.getStateChange() == ItemEvent.SELECTED;
    }
    else if (source == sample) {
      downSample = e.getStateChange() == ItemEvent.SELECTED;
    }
  }


}
