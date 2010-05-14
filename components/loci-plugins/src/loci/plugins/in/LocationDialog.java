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

package loci.plugins.in;

import ij.gui.GenericDialog;

/**
 * Bio-Formats Importer location chooser dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/LocationDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/LocationDialog.java">SVN</a></dd></dl>
 */
public class LocationDialog extends ImporterDialog {

  // -- Constructor --

  /** Creates a location chooser dialog for the Bio-Formats Importer. */
  public LocationDialog(ImportProcess process) {
    super(process);
  }

  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    // NB: Prompt only if location wasn't already specified.
    return !process.isWindowless() && options.getLocation() == null;
  }

  @Override
  protected GenericDialog constructDialog() {
    GenericDialog gd = new GenericDialog("Bio-Formats Dataset Location");
    addChoice(gd, ImporterOptions.KEY_LOCATION);
    return gd;
  }

  @Override
  protected boolean harvestResults(GenericDialog gd) {
    String location = gd.getNextChoice();
    options.setLocation(location);
    return true;
  }

}
