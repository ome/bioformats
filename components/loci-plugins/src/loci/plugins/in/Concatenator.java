//
// Concatenator.java
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

package loci.plugins.in;

import ij.ImagePlus;
import ij.ImageStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Logic for concatenating multiple images together.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/Concatenator.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/Concatenator.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class Concatenator {

  /** Concatenates the list of images as appropriate. */
  public List<ImagePlus> concatenate(List<ImagePlus> imps) {

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
        // NB: For now, we prioritize adding to the time points, then
        // focal planes, and lastly channels. In some cases, there may be
        // multiple compatible dimensions; in the future, we may prompt the
        // user to choose the axis for concatenation.
        if (canAppendT) outputImp.setDimensions(c, z, t + tSize);
        else if (canAppendZ) outputImp.setDimensions(c, z + zSize, t);
        else if (canAppendC) outputImp.setDimensions(c + cSize, z, t);
        else throw new IllegalStateException("Dimensional mismatch");

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
