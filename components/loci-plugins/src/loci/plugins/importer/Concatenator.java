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

package loci.plugins.importer;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;

import java.util.ArrayList;
import java.util.List;

import loci.formats.IFormatReader;
import loci.plugins.Slicer;

/**
 * Logic for concatenating multiple images together.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/Slicer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/Slicer.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class Concatenator {
  
  protected ImporterOptions options;
  
  public Concatenator(ImporterOptions options) {
    this.options = options;
  }

  /** Concatenates the list of images as appropriate. */
  public List<ImagePlus> concatenate(List<ImagePlus> imps, String stackOrder) {
    if (!options.isConcatenate()) return imps;

    IFormatReader r = options.getReader();

    List<Integer> widths = new ArrayList<Integer>();
    List<Integer> heights = new ArrayList<Integer>();
    List<Integer> types = new ArrayList<Integer>();
    List<ImagePlus> newImps = new ArrayList<ImagePlus>();

    for (int j=0; j<imps.size(); j++) {
      ImagePlus imp = imps.get(j);
      int wj = imp.getWidth();
      int hj = imp.getHeight();
      int tj = imp.getBitDepth();
      boolean append = false;
      for (int k=0; k<widths.size(); k++) {
        int wk = ((Integer) widths.get(k)).intValue();
        int hk = ((Integer) heights.get(k)).intValue();
        int tk = ((Integer) types.get(k)).intValue();

        if (wj == wk && hj == hk && tj == tk) {
          ImagePlus oldImp = newImps.get(k);
          ImageStack is = oldImp.getStack();
          ImageStack newStack = imp.getStack();
          for (int s=0; s<newStack.getSize(); s++) {
            is.addSlice(newStack.getSliceLabel(s + 1),
              newStack.getProcessor(s + 1));
          }
          oldImp.setStack(oldImp.getTitle(), is);
          newImps.set(k, oldImp);
          append = true;
          k = widths.size();
        }
      }
      if (!append) {
        widths.add(new Integer(wj));
        heights.add(new Integer(hj));
        types.add(new Integer(tj));
        newImps.add(imp);
      }
    }

    boolean splitC = options.isSplitChannels();
    boolean splitZ = options.isSplitFocalPlanes();
    boolean splitT = options.isSplitTimepoints();

    for (int j=0; j<newImps.size(); j++) {
      ImagePlus imp = (ImagePlus) newImps.get(j);
      if (splitC || splitZ || splitT) {
        boolean hyper = options.isViewHyperstack();
        imp = Slicer.reslice(imp, splitC, splitZ, splitT, hyper, stackOrder);
      }
      // TODO: Change IJ.runPlugIn to direct API calls in relevant plugins.
      // Should not be calling show() here just to make other plugins happy! :-(
      if (options.isMergeChannels() && options.isWindowless()) {
        imp.show();
        IJ.runPlugIn("loci.plugins.Colorizer", "stack_order=" + stackOrder +
          " merge=true merge_option=[" + options.getMergeOption() + "] " +
          "series=" + r.getSeries() + " hyper_stack=" +
          options.isViewHyperstack() + " ");
        imp.close();
      }
      else if (options.isMergeChannels()) {
        imp.show();
        IJ.runPlugIn("loci.plugins.Colorizer", "stack_order=" + stackOrder +
          " merge=true series=" + r.getSeries() + " hyper_stack=" +
          options.isViewHyperstack() + " ");
        imp.close();
      }
    }

    return newImps;
  }

}
