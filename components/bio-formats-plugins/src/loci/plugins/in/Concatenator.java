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

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Logic for concatenating multiple images together.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats-plugins/src/loci/plugins/in/Concatenator.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats-plugins/src/loci/plugins/in/Concatenator.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class Concatenator {

  /** Concatenates the list of images as appropriate. */
  public List<ImagePlus> concatenate(List<ImagePlus> imps) {

    /* TODO : fix this to work in windowlessly
    GenericDialog axisDialog = new GenericDialog("Choose concatenation axis");
    String[] options = new String[] {"T", "Z", "C"};
    axisDialog.addMessage(
      "Choose the axis along which to concatenate image planes:");
    axisDialog.addChoice("Axis", options, options[0]);
    axisDialog.showDialog();

    if (axisDialog.wasCanceled()) {
      return imps;
    }

    String axis = axisDialog.getNextChoice();
    boolean doAppendT = axis.equals("T");
    boolean doAppendZ = axis.equals("Z");
    boolean doAppendC = axis.equals("C");
    */
    boolean doAppendT = true;
    boolean doAppendZ = true;
    boolean doAppendC = true;

    // list of output (possibly concatenated) images
    final List<ImagePlus> outputImps = new ArrayList<ImagePlus>();

    for (ImagePlus imp : imps) {
      final int width = imp.getWidth();
      final int height = imp.getHeight();
      final int type = imp.getType();
      final int cSize = imp.getNChannels();
      final int zSize = imp.getNSlices();
      final int tSize = imp.getNFrames();

      boolean append = false;
      for (int k=0; k<outputImps.size(); k++) {
        final ImagePlus outputImp = outputImps.get(k);
        final int w = outputImp.getWidth();
        final int h = outputImp.getHeight();
        final int outType = outputImp.getType();
        final int c = outputImp.getNChannels();
        final int z = outputImp.getNSlices();
        final int t = outputImp.getNFrames();

        // verify that images are compatible
        if (width != w || height != h) continue; // different XY resolution
        if (type != outType) continue; // different processor type
        final boolean canAppendT = cSize == c && zSize == z;
        final boolean canAppendZ = cSize == c && tSize == t;
        final boolean canAppendC = zSize == z && tSize == t;
        if (!canAppendT && !canAppendZ && !canAppendC) {
          // incompatible dimensions
          continue;
        }

        // concatenate planes onto this output image
        final ImageStack outputStack = outputImp.getStack();
        final ImageStack inputStack = imp.getStack();
        for (int s=0; s<inputStack.getSize(); s++) {
          outputStack.addSlice(inputStack.getSliceLabel(s + 1),
            inputStack.getProcessor(s + 1));
        }
        outputImp.setStack(outputImp.getTitle(), outputStack);

        // update image dimensions

        if (doAppendT && canAppendT) {
          outputImp.setDimensions(c, z, t + tSize);
        }
        else if (doAppendZ && canAppendZ) {
          outputImp.setDimensions(c, z + zSize, t);
        }
        else if (doAppendC && canAppendC) {
          outputImp.setDimensions(c + cSize, z, t);
        }

        append = true;
        break;
      }
      if (!append) {
        // could not concatenate input image to any existing output;
        // append it to the list of outputs directly instead
        outputImps.add(imp);
      }
    }

    return outputImps;
  }

}
