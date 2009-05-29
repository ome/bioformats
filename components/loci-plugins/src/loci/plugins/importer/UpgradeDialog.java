//
// UpgradeDialog.java
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

import loci.plugins.util.OptionsDialog;

/**
 * Bio-Formats Importer update checker dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/importer/UpgradeDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/importer/UpgradeDialog.java">SVN</a></dd></dl>
 */
public class UpgradeDialog extends OptionsDialog {

  // -- Fields --

  /** LOCI plugins configuration. */
  protected ImporterOptions options;

  // -- Constructor --

  /** Creates an update checker dialog for the Bio-Formats Importer. */
  public UpgradeDialog(ImporterOptions options) {
    super(options);
    this.options = options;
  }

  // -- OptionsDialog methods --

  /**
   * Asks user whether Bio-Formats should automatically check for updates.
   *
   * @return status of operation
   */
  public int showDialog() {
    // CTR TODO
    /*
    // load:
    if (Prefs.get(KEY_UPGRADE, null) == null) {
      IJ.showMessage("The Bio-Formats plugin for ImageJ can automatically " +
        "check for\nupdates. By default, this feature is enabled, but you " +
        "can disable\nit in the 'Upgrade' tab of the LOCI plugins " +
        "configuration window.");
    }

    // save:
    if (Prefs.get(KEY_UPGRADE, null) == null) upgradeCheck = true;
    */
    return STATUS_OK;
  }

}
