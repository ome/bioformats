//
// CropDialog.java
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

import ij.gui.GenericDialog;

import loci.common.Region;
import loci.formats.IFormatReader;
import loci.plugins.prefs.OptionsDialog;
import loci.plugins.util.WindowTools;

/**
 * Bio-Formats Importer crop options dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/CropDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/CropDialog.java">SVN</a></dd></dl>
 */
public class CropDialog extends OptionsDialog {

  // -- Fields --

  /** LOCI plugins configuration. */
  protected ImporterOptions options;

  protected IFormatReader r;
  protected String[] labels;

  // -- Constructor --

  /** Creates a crop options dialog for the Bio-Formats Importer. */
  public CropDialog(ImporterOptions options, IFormatReader r, String[] labels) {
    super(options);
    this.options = options;
    this.r = r;
    this.labels = labels;
  }

  // -- OptionsDialog methods --

  /**
   * Gets crop settings from macro options, or user prompt if necessary.
   *
   * @return status of operation
   */
  public int showDialog() {
    GenericDialog gd = new GenericDialog("Bio-Formats Crop Options");
    for (int s=0; s<options.getSeriesCount(); s++) {
      if (!options.isSeriesOn(s)) continue;
      r.setSeries(s);
      gd.addMessage(labels[s].replaceAll("_", " "));
      gd.addNumericField("X_Coordinate_" + (s + 1), 0, 0);
      gd.addNumericField("Y_Coordinate_" + (s + 1), 0, 0);
      gd.addNumericField("Width_" + (s + 1), r.getSizeX(), 0);
      gd.addNumericField("Height_" + (s + 1), r.getSizeY(), 0);
    }
    WindowTools.addScrollBars(gd);
    gd.showDialog();
    if (gd.wasCanceled()) return STATUS_CANCELED;

    for (int s=0; s<options.getSeriesCount(); s++) {
      if (!options.isSeriesOn(s)) continue;
      r.setSeries(s);

      Region box = options.getCropRegion(s);

      box.x = (int) gd.getNextNumber();
      box.y = (int) gd.getNextNumber();
      box.width = (int) gd.getNextNumber();
      box.height = (int) gd.getNextNumber();

      if (box.x < 0) box.x = 0;
      if (box.y < 0) box.y = 0;
      if (box.x >= r.getSizeX()) box.x = r.getSizeX() - box.width - 1;
      if (box.y >= r.getSizeY()) box.y = r.getSizeY() - box.height - 1;
      if (box.width < 1) box.width = 1;
      if (box.height < 1) box.height = 1;
      if (box.width + box.x > r.getSizeX()) {
        box.width = r.getSizeX() - box.x;
      }
      if (box.height + box.y > r.getSizeY()) {
        box.height = r.getSizeY() - box.y;
      }

      options.setCropRegion(s, box); // in case we got a copy
    }

    return STATUS_OK;
  }

}
