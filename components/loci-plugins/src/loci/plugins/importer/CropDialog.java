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

package loci.plugins.importer;

import ij.gui.GenericDialog;

import java.awt.Rectangle;

import loci.formats.IFormatReader;
import loci.plugins.prefs.OptionsDialog;
import loci.plugins.util.WindowTools;

/**
 * Bio-Formats Importer crop options dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/importer/CropDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/importer/CropDialog.java">SVN</a></dd></dl>
 */
public class CropDialog extends OptionsDialog {

  // -- Fields --

  /** LOCI plugins configuration. */
  protected ImporterOptions options;

  protected IFormatReader r;
  protected String[] labels;
  protected boolean[] series;
  protected Rectangle[] box;

  // -- Constructor --

  /** Creates a crop options dialog for the Bio-Formats Importer. */
  public CropDialog(ImporterOptions options, IFormatReader r,
    String[] labels, boolean[] series, Rectangle[] box)
  {
    super(options);
    this.options = options;
    this.r = r;
    this.labels = labels;
    this.series = series;
    this.box = box;
  }

  // -- OptionsDialog methods --

  /**
   * Gets crop settings from macro options, or user prompt if necessary.
   *
   * @return status of operation
   */
  public int showDialog() {
    GenericDialog gd = new GenericDialog("Bio-Formats Crop Options");
    for (int i=0; i<series.length; i++) {
      if (!series[i]) continue;
      gd.addMessage(labels[i].replaceAll("_", " "));
      gd.addNumericField("X_Coordinate_" + i, 0, 0);
      gd.addNumericField("Y_Coordinate_" + i, 0, 0);
      gd.addNumericField("Width_" + i, 0, 0);
      gd.addNumericField("Height_" + i, 0, 0);
    }
    WindowTools.addScrollBars(gd);
    gd.showDialog();
    if (gd.wasCanceled()) return STATUS_CANCELED;

    for (int i=0; i<series.length; i++) {
      if (!series[i]) continue;
      r.setSeries(i);
      box[i].x = (int) gd.getNextNumber();
      box[i].y = (int) gd.getNextNumber();
      box[i].width = (int) gd.getNextNumber();
      box[i].height = (int) gd.getNextNumber();

      if (box[i].x < 0) box[i].x = 0;
      if (box[i].y < 0) box[i].y = 0;
      if (box[i].x >= r.getSizeX()) box[i].x = r.getSizeX() - box[i].width - 1;
      if (box[i].y >= r.getSizeY()) box[i].y = r.getSizeY() - box[i].height - 1;
      if (box[i].width < 1) box[i].width = 1;
      if (box[i].height < 1) box[i].height = 1;
      if (box[i].width + box[i].x > r.getSizeX()) {
        box[i].width = r.getSizeX() - box[i].x;
      }
      if (box[i].height + box[i].y > r.getSizeY()) {
        box[i].height = r.getSizeY() - box[i].y;
      }
    }

    return STATUS_OK;
  }

}
