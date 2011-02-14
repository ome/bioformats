//
// ImporterDialog.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser and Stack Slicer. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden and Christopher Peterson.

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
import loci.plugins.BF;
import loci.plugins.prefs.OptionsDialog;

/**
 * Abstract superclass of importer dialogs.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/loci-plugins/src/loci/plugins/in/ImporterDialog.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/loci-plugins/src/loci/plugins/in/ImporterDialog.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public abstract class ImporterDialog extends OptionsDialog {

  // -- Fields --

  protected ImportProcess process;
  protected ImporterOptions options;

  // -- Constructor --

  public ImporterDialog(ImportProcess process) {
    super(process.getOptions());
    this.process = process;
    this.options = process.getOptions();
  }

  // -- ImporterDialog methods --

  protected abstract boolean needPrompt();

  protected abstract GenericDialog constructDialog();

  /** Displays the dialog, or grabs values from macro options. */
  protected boolean displayDialog(GenericDialog gd) {
    gd.showDialog();
    return !gd.wasCanceled();
  }

  protected abstract boolean harvestResults(GenericDialog gd);

  // -- OptionsDialog methods --

  @Override
  public int showDialog() {
    // verify whether prompt is necessary
    if (!needPrompt()) {
      BF.debug(getClass().getName() + ": skip");
      return STATUS_OK;
    }
    BF.debug(getClass().getName() + ": prompt");

    GenericDialog gd = constructDialog();
    if (!displayDialog(gd)) return STATUS_CANCELED;
    if (!harvestResults(gd)) return STATUS_CANCELED;

    return STATUS_OK;
  }

}
