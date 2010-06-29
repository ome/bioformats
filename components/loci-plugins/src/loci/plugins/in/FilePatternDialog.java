//
// FilePatternDialog.java
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

import loci.common.Location;
import loci.formats.FilePattern;

/**
 * Bio-Formats Importer file pattern dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/FilePatternDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/FilePatternDialog.java">SVN</a></dd></dl>
 */
public class FilePatternDialog extends ImporterDialog {

  // -- Constructor --

  /** Creates a file pattern dialog for the Bio-Formats Importer. */
  public FilePatternDialog(ImportProcess process) {
    super(process);
  }
  
  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    return !process.isWindowless() && options.isGroupFiles();
  }
  
  @Override
  protected GenericDialog constructDialog() {
    // CTR - CHECK
    Location idLoc = new Location(options.getId());
    String id = FilePattern.findPattern(idLoc);
    if (id == null) {
      if (!options.isQuiet()) {
        IJ.showMessage("Bio-Formats",
          "Warning: Bio-Formats was unable to determine a grouping that\n" +
          "includes the file you chose. The most common reason for this\n" +
          "situation is that the folder contains extraneous files with\n" +
          "similar names and numbers that confuse the detection algorithm.\n" +
          " \n" +
          "For example, if you have multiple datasets in the same folder\n" +
          "named series1_z*_c*.tif, series2_z*_c*.tif, etc., Bio-Formats\n" +
          "may try to group all such files into a single series.\n" +
          " \n" +
          "For best results, put each image series's files in their own\n" +
          "folder, or type in a file pattern manually.\n");
      }
      id = idLoc.getAbsolutePath();
    }

    // construct dialog
    GenericDialog gd = new GenericDialog("Bio-Formats File Stitching");
    int len = id.length() + 1;
    if (len > 80) len = 80;
    gd.addStringField("Pattern: ", id, len);

    return gd;
  }
  
  @Override
  protected boolean harvestResults(GenericDialog gd) {
    String id = gd.getNextString();
    options.setId(id);
    return true;
  }

}
