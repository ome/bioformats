//
// IdDialog.java
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

import ij.IJ;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import loci.common.Location;

/**
 * Bio-Formats Importer id chooser dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/IdDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/IdDialog.java">SVN</a></dd></dl>
 */
public class IdDialog extends ImporterDialog {

  // -- Fields --

  private OpenDialog od;

  // -- Constructor --

  /** Creates an id chooser dialog for the Bio-Formats Importer. */
  public IdDialog(ImportProcess process) {
    super(process);
  }

  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    return options.getId() == null;
  }

  @Override
  protected GenericDialog constructDialog() {
    GenericDialog gd = null;
    if (options.isHTTP()) {
      gd = new GenericDialog("Bio-Formats URL");
      gd.addStringField("URL: ", "http://", 30);
    }
    return gd;
  }

  /**
   * Asks user whether Bio-Formats should automatically check for upgrades,
   * and if so, checks for an upgrade and prompts user to install it.
   *
   * @return status of operation
   */
  @Override
  protected boolean displayDialog(GenericDialog gd) {
    if (options.isLocal()) {
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

      String idLabel = options.getLabel(ImporterOptions.KEY_ID);
      od = new OpenDialog(idLabel, options.getId());
      if (od.getFileName() == null) return false;
    }
    else if (options.isHTTP()) {
      gd.showDialog();
      if (gd.wasCanceled()) return false;
    }
    return true;
  }

  @Override
  protected boolean harvestResults(GenericDialog gd) {
    String id = null;
    if (options.isLocal()) {
      String dir = od.getDirectory();
      String name = od.getFileName();
      if (dir != null || name == null)
      id = dir + name;

      // verify validity
      Location idLoc = new Location(id);
      if (!idLoc.exists() && !id.toLowerCase().endsWith(".fake")) {
        if (!options.isQuiet()) {
          IJ.error("Bio-Formats",
            "The specified file (" + id + ") does not exist.");
        }
        return false;
      }
    }
    else if (options.isHTTP()) {
      id = gd.getNextString();
      if (id == null) {
        if (!options.isQuiet()) {
          IJ.error("Bio-Formats", "No URL was specified.");
        }
        return false;
      }
    }
    options.setId(id);
    return true;
  }

}
