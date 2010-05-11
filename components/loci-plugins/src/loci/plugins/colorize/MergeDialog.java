//
// MergeDialog.java
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

package loci.plugins.colorize;

import ij.gui.GenericDialog;
import loci.plugins.in.ImportProcess;
import loci.plugins.in.ImporterDialog;
import loci.plugins.in.ImporterOptions;

/**
 * Bio-Formats Importer merge options dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/importer/MergeDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/importer/MergeDialog.java">SVN</a></dd></dl>
 */
public class MergeDialog extends ImporterDialog {

  // -- Fields --

  protected String[] mergeOptions;

  // -- Constructor --

  /** Creates a merge options dialog for the Bio-Formats Importer. */
  public MergeDialog(ImportProcess process, int[] nums) {
    super(process);
    
    mergeOptions = new String[7];
    mergeOptions[6] = options.getDefaultValue(ImporterOptions.KEY_MERGE_OPTION);
    for (int i=0; i<6; i++) {
      mergeOptions[i] = nums[i] + " planes, " + (i + 2) + " channels per plane";
    }
  }

  // -- ImporterDialog methods --

  protected boolean needPrompt() {
    return !process.isWindowless();
  }

  protected GenericDialog constructDialog() {
    GenericDialog gd = new GenericDialog("Merging Options...");

    String mergeOptionLabel =
      options.getLabel(ImporterOptions.KEY_MERGE_OPTION);

    gd.addMessage("How would you like to merge this data?");
    String mergeOptionDefault = mergeOptions[mergeOptions.length - 1];
    gd.addChoice(mergeOptionLabel, mergeOptions, mergeOptionDefault);

    return gd;
  }
  
  protected boolean harvestResults(GenericDialog gd) {
    options.setMergeOption("" + (gd.getNextChoiceIndex() + 2));
    return true;
  }

}
