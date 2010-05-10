//
// SwapDialog.java
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

import java.io.IOException;

import ij.IJ;
import ij.gui.GenericDialog;
import loci.formats.DimensionSwapper;
import loci.formats.FormatException;
import loci.plugins.util.WindowTools;

/**
 * Bio-Formats Importer dimension swapper dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/SwapDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/SwapDialog.java">SVN</a></dd></dl>
 */
public class SwapDialog extends ImporterDialog {

  // -- Fields --

  protected DimensionSwapper dimSwap;

  // -- Constructor --

  /** Creates a dimension swapper dialog for the Bio-Formats Importer. */
  public SwapDialog(ImportProcess process) {
    super(process);
    try {
      dimSwap = (DimensionSwapper)
        process.getReader().unwrap(DimensionSwapper.class, null);
    }
    catch (FormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  // -- ImporterDialog methods --
  
  @Override
  protected boolean needPrompt() {
    return !process.isWindowless() && options.isSwapDimensions();
  }
  
  @Override
  protected GenericDialog constructDialog() {
    GenericDialog gd = new GenericDialog("Dimension swapping options");

    String[] labels = {"Z", "C", "T"};
    int[] sizes = new int[] {
      dimSwap.getSizeZ(), dimSwap.getSizeC(), dimSwap.getSizeT()
    };
    for (int s=0; s<dimSwap.getSeriesCount(); s++) {
      if (!options.isSeriesOn(s)) continue;
      dimSwap.setSeries(s);

      gd.addMessage("Series " + (s + 1) + ":\n");

      for (int i=0; i<labels.length; i++) {
        gd.addChoice(sizes[i] + "_planes", labels, labels[i]);
      }
    }
    WindowTools.addScrollBars(gd);
    
    return gd;
  }
  
  @Override
  protected boolean harvestResults(GenericDialog gd) {
    for (int s=0; s<dimSwap.getSeriesCount(); s++) {
      if (!options.isSeriesOn(s)) continue;
      dimSwap.setSeries(s);
      String z = gd.getNextChoice();
      String c = gd.getNextChoice();
      String t = gd.getNextChoice();

      if (z.equals(t) || z.equals(c) || c.equals(t)) {
        IJ.error("Invalid swapping options - each axis can be used only once.");
        throw new IllegalStateException(); // CTR FIXME
      }

      String originalOrder = dimSwap.getDimensionOrder();
      StringBuffer sb = new StringBuffer();
      sb.append("XY");
      for (int i=2; i<originalOrder.length(); i++) {
        if (originalOrder.charAt(i) == 'Z') sb.append(z);
        else if (originalOrder.charAt(i) == 'C') sb.append(c);
        else if (originalOrder.charAt(i) == 'T') sb.append(t);
      }

      dimSwap.swapDimensions(sb.toString());
    }
    return true;
  }

}
