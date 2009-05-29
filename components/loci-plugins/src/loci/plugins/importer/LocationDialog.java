//
// LocationDialog.java
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
import loci.plugins.util.OptionsDialog;

/**
 * Bio-Formats Importer location chooser dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/importer/LocationDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/importer/LocationDialog.java">SVN</a></dd></dl>
 */
public class LocationDialog extends OptionsDialog {

  // -- Fields --

  /** LOCI plugins configuration. */
  protected ImporterOptions options;

  // -- Constructor --

  /** Creates a location chooser dialog for the Bio-Formats Importer. */
  public LocationDialog(ImporterOptions options) {
    super(options);
    this.options = options;
  }

  // -- OptionsDialog methods --

  /**
   * Gets the location (type of data source) from macro options,
   * or user prompt if necessary.
   *
   * @return status of operation
   */
  public int showDialog() {
    if (options.getLocation() == null) {
      // Open a dialog asking the user what kind of dataset to handle.
      // Ask only if the location was not already specified somehow.
      // ImageJ will grab the value from the macro options, when possible.
      GenericDialog gd = new GenericDialog("Bio-Formats Dataset Location");
      addChoice(gd, ImporterOptions.KEY_LOCATION);
      gd.showDialog();
      if (gd.wasCanceled()) return STATUS_CANCELED;
      options.setLocation(gd.getNextChoice());
    }
    return STATUS_OK;
  }

}
