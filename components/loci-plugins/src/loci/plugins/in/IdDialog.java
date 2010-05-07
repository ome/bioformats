//
// IdDialog.java
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
import ij.io.OpenDialog;
import loci.common.Location;
import loci.plugins.BF;
import loci.plugins.prefs.OptionsDialog;

/**
 * Bio-Formats Importer id chooser dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/IdDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/IdDialog.java">SVN</a></dd></dl>
 */
public class IdDialog extends OptionsDialog {

  // -- Fields --

  protected ImporterOptions options;

  // -- Constructor --

  /** Creates an id chooser dialog for the Bio-Formats Importer. */
  public IdDialog(ImporterOptions options) {
    super(options);
    this.options = options;
  }
  
  // -- IdDialog methods --

  /**
   * Gets the id (filename, URL, etc.) to open from macro options,
   * or user prompt if necessary.
   *
   * @return status of operation
   */
  public int showDialogLocal() {
    if (options.isFirstTime() && IJ.isMacOSX() && !options.isQuiet()) {
      String osVersion = System.getProperty("os.version");
      if (osVersion == null ||
        osVersion.startsWith("10.4.") ||
        osVersion.startsWith("10.3.") ||
        osVersion.startsWith("10.2."))
      {
        // present user with one-time dialog box
        IJ.showMessage("Bio-Formats",
          "One-time warning: There is a bug in Java on Mac OS X with the " +
          "native file chooser\nthat crashes ImageJ if you click on a file " +
          "in CXD, IPW, OIB or ZVI format while in\ncolumn view mode. You " +
          "can work around the problem in one of two ways:\n \n" +
          "    1. Switch to list view (press Command+2)\n" +
          "    2. Check \"Use JFileChooser to Open/Save\" under " +
          "Edit>Options>Input/Output...");
      }
    }

    String id = options.getId();
    if (id == null) {
      // construct and display dialog (or grab from macro options)
      String idLabel = options.getLabel(ImporterOptions.KEY_ID);
      OpenDialog od = new OpenDialog(idLabel, id);

      // harvest results
      String dir = od.getDirectory();
      String name = od.getFileName();
      if (dir == null || name == null) return STATUS_CANCELED;
      id = dir + name;
    }

    // verify validity
    Location idLoc = new Location(id);
    if (!idLoc.exists() && !id.toLowerCase().endsWith(".fake")) {
      if (!options.isQuiet()) {
        IJ.error("Bio-Formats",
          "The specified file (" + id + ") does not exist.");
      }
      return STATUS_FINISHED;
    }
    options.setId(id);

    return STATUS_OK;
  }

  /**
   * Gets the URL (id) to open from macro options,
   * or user prompt if necessary.
   *
   * @return status of operation
   */
  public int showDialogHTTP() {
    String id = options.getId();
    if (id == null) {
      // construct dialog
      GenericDialog gd = new GenericDialog("Bio-Formats URL");
      gd.addStringField("URL: ", "http://", 30);

      // display dialog (or grab from macro options)
      gd.showDialog();
      if (gd.wasCanceled()) return STATUS_CANCELED;

      // harvest results
      id = gd.getNextString();
    }

    // verify validity
    if (id == null) {
      if (!options.isQuiet()) IJ.error("Bio-Formats", "No URL was specified.");
      return STATUS_FINISHED;
    }
    options.setId(id);

    return STATUS_OK;
  }

  // -- OptionsDialog methods --

  /**
   * Gets the filename (id) from macro options, or user prompt if necessary.
   *
   * @return status of operation
   */
  public int showDialog() {
    // verify whether prompt is necessary
    if (options.isWindowless()) {
      BF.debug("IdDialog: skip");
      return STATUS_OK;
    }
    BF.debug("IdDialog: prompt");

    if (options.isLocal()) return showDialogLocal();
    return showDialogHTTP(); // options.isHTTP()
  }

}
