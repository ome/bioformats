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

import ij.IJ;
import ij.gui.GenericDialog;
import loci.formats.DimensionSwapper;
import loci.plugins.prefs.OptionsDialog;
import loci.plugins.util.WindowTools;

/**
 * Bio-Formats Importer dimension swapper dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/SwapDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/SwapDialog.java">SVN</a></dd></dl>
 */
public class SwapDialog extends OptionsDialog {

  // -- Fields --

  /** LOCI plugins configuration. */
  protected ImporterOptions options;

  protected DimensionSwapper r;

  // -- Constructor --

  /** Creates a dimension swapper dialog for the Bio-Formats Importer. */
  public SwapDialog(ImporterOptions options, DimensionSwapper r) {
    super(options);
    this.options = options;
    this.r = r;
  }

  // -- OptionsDialog methods --

  /**
   * Gets dimension order from macro options, or user prompt if necessary.
   *
   * @return status of operation
   */
  public int showDialog() {
    GenericDialog gd = new GenericDialog("Dimension swapping options");

    int oldSeries = r.getSeries();
    String[] labels = {"Z", "C", "T"};
    int[] sizes = new int[] {r.getSizeZ(), r.getSizeC(), r.getSizeT()};
    for (int s=0; s<r.getSeriesCount(); s++) {
      if (!options.isSeriesOn(s)) continue;
      r.setSeries(s);

      gd.addMessage("Series " + (s + 1) + ":\n");

      for (int i=0; i<labels.length; i++) {
        gd.addChoice(sizes[i] + "_planes", labels, labels[i]);
      }
    }
    WindowTools.addScrollBars(gd);
    gd.showDialog();
    if (gd.wasCanceled()) return STATUS_CANCELED;

    for (int s=0; s<r.getSeriesCount(); s++) {
      if (!options.isSeriesOn(s)) continue;
      r.setSeries(s);
      String z = gd.getNextChoice();
      String c = gd.getNextChoice();
      String t = gd.getNextChoice();

      if (z.equals(t) || z.equals(c) || c.equals(t)) {
        IJ.error("Invalid swapping options - each axis can be used only once.");
        return showDialog(); // TODO: fix bad recursion
      }

      String originalOrder = r.getDimensionOrder();
      StringBuffer sb = new StringBuffer();
      sb.append("XY");
      for (int i=2; i<originalOrder.length(); i++) {
        if (originalOrder.charAt(i) == 'Z') sb.append(z);
        else if (originalOrder.charAt(i) == 'C') sb.append(c);
        else if (originalOrder.charAt(i) == 'T') sb.append(t);
      }

      r.swapDimensions(sb.toString());
    }
    r.setSeries(oldSeries);

    return STATUS_OK;
  }

}
